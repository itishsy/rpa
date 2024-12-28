"""
1.从社保费用客户端获取费用
  主要是操作两个功能  -- 社保费用申报 和  费用缴纳
  先操作社保费申报查询才能在费用缴纳中看到明细

"""
import sys
from pywinauto import mouse as auto_mouse
import GetWin as GW
import Log as log
import Config as cfg
import Constant as ct
import time
import datetime
import uuid
import pdfplumber
import FitzUtils
import json
import traceback
from decimal import Decimal
import ctypes



def get_windows_screen_scale():
    """ 调用 Windows API 函数获取缩放比例 """
    try:
        user32 = ctypes.windll.user32
        user32.SetProcessDPIAware()
        dpi = user32.GetDpiForSystem()
        # 计算缩放比例
        scale = round(dpi / 96.0, 2)
        log.logger.info("Windows 桌面的缩放比例:%.2f", scale )
        return scale
    except Exception as e:
        print("获取缩放比例时出错:", e)
        return 1

class SocialFeeClient(object):
    def __init__(self):
        self.current_cmd: str = ct.cmd_start  #起始步骤
        #   操作明细列表 表头 操作的坐标  先写死
        self.jiaofeimingxi_count: int = 24  #缴费明细总记录数
        self.jiaofeimingxi_count_counter: int = 1  #缴费明细操作数据的计数器
        self.jiaofeimingxi_show_count: int = 0  #缴费明细窗口可展示完整行数
        # 需要在pdf解析获取
        self.shenbaominxi_count: int = 1  #申报明细总记录
        self.shenbaominxi_count_counter: int = 1  #当前处理申报记录行数计数器
        self.shebaomingxi_show_count: int = 0  #申报记录创窗口可展示的完整行数
        # self.shebaomingxi_row_scroll: dict = {}
        self.caozuo_x: int = 1320  #缴费明细 表头操作位置-横坐标
        self.caozuo_y: int = 450  #缴费明细 表头操作位置-纵坐标
        self.shenbaomingxi_jiaofeirenshu_x: int = 777  # 申报明细-缴费人数坐标
        self.shenbaomingxi_jiaofeirenshu_y: int = 376  # 申报明细-缴费人数坐标  每点击一下 会增加值，直到最下面就不增加了
        self.org_shenbaomingxi_jiaofeirenshu_x: int = 777  # 申报明细-缴费人数坐标
        self.org_shenbaomingxi_jiaofeirenshu_y: int = 376  # 申报明细-缴费人数坐标
        self.shenbaomingxi_win_close_x: int = 0  #申报明细 关闭按钮坐标
        self.shenbaomingxi_win_close_y: int = 0  #申报明细 关闭按钮坐标
        self.renyuanmingxin_win_close_x: int = 0  #人员明细 关闭按钮坐标
        self.renyuanmingxin_win_close_y: int = 0  #人员明细 关闭按钮坐标
        self.table_hight: int = int(40 * get_windows_screen_scale())  # 表格单元格高度    每行高度
        self.cache_path: str = "D:\\seebon\\rpa\cache\\上海\\社保\\TEST\\"  #下载文件缓存目录
        self.batch_no: str = None  #批次号
        self.shenbaominxi_file: str = None  #申报明细的文件
        self.inst_id: str = uuid.uuid4().__str__()  #流程实例号
        self.jiaofeimingxi_index_map = {}  #缴费列表导出数据Map，以序号为key，序号为mapValue
        self.shenbaominxi_data = []  # shenbaominxi_file 解析的数据
        self.jiaofeimingxi_list = []  #人员明细文件记录  最终写入txt
        self.row: str = None  #申报明细 每行处理的结果  最终会缓存到  self.jiaofeimingxi_list
        self.scrolling_times = 0  # 垂直滚动次数
        self.renyuanmingxi_fileFormat = "xlsx"  # 人员明细默认导出格式 （注意不用地区的人员明细格式不一样）
        self.fee_date = None  #费用所属期
        self.retry_count = 0  #重试次数
        #目前沈阳
        self.current_city = None  #当前抓取的城市
        #记录每条记录
        self._step_retry = {}  #每步的重试次数  最大重试一次
        # 排除获取不到明细的项目和品目记录
        self.excludeInsurance = None  #排除抓不到人员明细的征收项目和品目
        self.detailMaxCount = 1000  #当人数达到一定数量  人员明细的pdf下载寿险  默认是1000  可以在流程进行设计
        self.obtainedFeeDetail = []  #已经取过的明细月份  金额匹配则终止本次循环
        self.mingxi_file_name_list_file_name = './mingxi_file_name_list_file_name.text'

    #记录人员明细行步骤的重试次数  返回是否可以重试  目前仅限重试一次
    def step_retry(self):
        #步骤设置了可以重试
        if cfg.get_cmd(self.current_cmd, ct.cmd_sub_step_retry):
            if self.shenbaominxi_count_counter not in self._step_retry:  #无记录
                log.logger.info("1地区：%s.月份%s ,流程号 %s, 行号%d ，步骤：%s 进行重试", self.current_city,
                                self.fee_date, self.inst_id, self.shenbaominxi_count_counter, self.current_cmd)
                self._step_retry[self.shenbaominxi_count_counter] = {self.current_cmd: 1}
                time.sleep(1)
                return True
            else:
                if self.current_cmd not in self._step_retry[self.shenbaominxi_count_counter]:
                    log.logger.info("2地区：%s.月份%s ,流程号 %s, 行号%d ，步骤：%s 进行重试", self.current_city,
                                    self.fee_date, self.inst_id, self.shenbaominxi_count_counter, self.current_cmd)
                    self._step_retry[self.shenbaominxi_count_counter][self.current_cmd] = 1  #有记录 在原有记录上添加重试次数
                    time.sleep(1)
                    return True
        return False

    def get_shenbaomingxin_detail(self, path: str):
        pdf = pdfplumber.open(path)
        record_total = 0
        self.shenbaominxi_data.clear()
        pages = len(pdf.pages)
        if pages > 1:
            # 合并表格在读
            log.logger.info("申报明细多页合并为一页在读取表格，文件为:%s", path)
            pdf.close()
            output_pdf = path[0:len(path) - 4] + "_merge.pdf"
            FitzUtils.merge_pdf_pages(path, output_pdf, pages, 521)
            pdf = pdfplumber.open(output_pdf)
        i: int = 1
        # 获取字段序号
        monthStart_col_index: int = 0
        monthEnd_col_index: int = 0
        insurance_col_index: int = 0
        feeType_col_index: int = 0
        amt_col_index: int = 0
        act_amt_col_index: int = 0
        jiaofei_people_count_col_index: int = 0
        total_amt = Decimal("0.00")
        for page in pdf.pages:
            for pdf_table in page.extract_table():
                row = []
                for item in pdf_table:
                    if item is not None:
                        row.append(item)
                text = row[0].replace("\n", "")
                if "序号" == text.replace("*", ""):
                    # 获取列索引
                    col_index = 0
                    for col in row:
                        col_val: str = col.replace("\n", "").replace("*", "")
                        if ct.shenbaomingxi_monthStart_col == col_val:
                            monthStart_col_index = col_index
                        elif ct.shenbaomingxi_monthEnd_col == col_val:
                            monthEnd_col_index = col_index
                        elif ct.shenbaomingxi_insurance_col == col_val:
                            insurance_col_index = col_index
                        elif ct.shenbaomingxi_feeType_col == col_val:
                            feeType_col_index = col_index
                        elif ct.shenbaomingxi_amt_col == col_val:
                            amt_col_index = col_index
                        elif ct.shenbaomingxi_jiaofe_people_count == col_val:
                            jiaofei_people_count_col_index = col_index
                        elif ct.shenbaomingxi_act_amt_col == col_val:
                            act_amt_col_index = col_index
                        col_index += 1
                    continue
                if '合计' in text:
                    break
                record_total = int(text)
                _i = str(i)
                # 加编号是为了防止后续取值精准
                ignore: bool = False
                _insurance: str = row[insurance_col_index].replace("\n", "")
                _feeType: str = row[feeType_col_index].replace("\n", "")
                _amt: str = row[amt_col_index].replace("\n", "").replace(",", "")
                total_amt += Decimal(_amt)
                if self.excludeInsurance is not None:
                    for exclude in self.excludeInsurance:
                        _exclude = exclude.split("*")
                        if _insurance == _exclude[0] and _feeType == _exclude[1]:
                            ignore = True
                            break
                metadata = {'No': i,
                            'monthStart_' + _i: row[monthStart_col_index].replace("\n", ""),
                            'monthEnd_' + _i: row[monthEnd_col_index].replace("\n", ""),
                            'insurance_' + _i: _insurance,
                            'feeType_' + _i: _feeType,
                            'amt_' + _i: _amt,
                            'ignore_' + _i: ignore,
                            'peopleCount_' + _i: int(
                                row[jiaofei_people_count_col_index].replace("\n", "").replace(",", "")),
                            }
                self.shenbaominxi_data.append(metadata)
                i = i + 1
        self.shenbaominxi_count = record_total
        log.logger.info(self.shenbaominxi_data)
        log.logger.info("申报明细%d,的记录数为：%d,解析pdf数据总数为:%d", self.shenbaominxi_count_counter, record_total,
                        len(self.shenbaominxi_data))
        if act_amt_col_index == 0:
            # 没有【本期实际应缴纳费额】的数据抓取不到的，所以跳过
            self.shenbaominxi_data.clear()
            self.current_cmd = ct.cmd_shebaomingxi_win_close_step_name
            log.logger.info(" instId:%s 地区:%s 月份 %s  行数 %s 本次抓取的没有【本期实际应缴纳费额】，不进行抓取 " ,self.inst_id,self.current_city,self.fee_date,self.jiaofeimingxi_count_counter)
            return False
        if total_amt.quantize(Decimal('0.00')) in self.obtainedFeeDetail and len(self.jiaofeimingxi_index_map) <= 1 :
            self.current_cmd = ct.cmd_end
            log.logger.info(self.obtainedFeeDetail)
            log.logger.info(" instId:%s 地区:%s 月份 %s 本次抓取的总金额为：%s ,识别已经抓取过，终止爬取 " ,self.inst_id,self.current_city,self.fee_date,total_amt)
            #关闭申报明细窗口
            self.close_shebaomingxi_win()
            return True
        return True
        # todo 只跑一条
        #self.shenbaominxi_count = 1

    def close_shebaomingxi_win(self):
        if self.shenbaomingxi_win_close_x <= 0:
            sub_cmd = cfg.get_cmd(ct.cmd_shebaomingxi_win_close_step_name, ct.cmd_sub_command)
            try:
                win = GW.get_win(sub_cmd)
                self.shenbaomingxi_win_close_x = win.BoundingRectangle.right - 20
                self.shenbaomingxi_win_close_y = win.BoundingRectangle.top + 18
            except Exception as e:
                pass
        auto_mouse.click(button='left',
                         coords=(self.shenbaomingxi_win_close_x, self.shenbaomingxi_win_close_y))
        time.sleep(1)

    # 如果发现异常  关闭所有窗口重新来  这个关闭的顺序不能改  应从最外层的窗口穷举关闭 在管内层的
    def close_all_win_retry(self):
        log.logger.error("流程：%s失重试次数为:%d", self.inst_id, self.retry_count)
        if ct.cmd_retry_max_count < self.retry_count:
            log.logger.error("异常警告=====流程：%s失败次数过多，终止抓取数据", self.inst_id)
            return
        self.retry_count += 1
        self.jiaofeimingxi_list = []
        self.shebaomingxi_show_count = 0
        self.caozuo_y = self.caozuo_y - self.table_hight
        # 关闭另存为
        win_args = []
        win_args.append("window[Name='另存为']/button[Name='关闭']")
        win_args.append("pane[ClassName='Tfrm_MsgDlgRich']/pane[Name='pnl_body']/child[index=0]/child[index=0]")
        win_args.append("pane[Name='确认信息']/pane[Name='pnl_body']/child[index=0]/child[index=1]")
        win_args.append("pane[ClassName='TfrmSyspzxx']/pane[ClassName='TITSSpeedButton']")  #网络异常
        # win_args.append("pane[ClassName='Tfrm_MsgDlgRich']/pane[Name='pnl_body']/child[index=0]/child[index=0]")
        log.logger.info("因操作异常关闭所有窗口，继续操作")
        for arg in win_args:
            try:
                win = GW.get_win(arg)
                win.Click()
                time.sleep(1)
            except Exception as e:
                pass
        # 通过坐标关闭窗口 关闭人员信息窗口  和 申报明细窗口
        try:
            if self.renyuanmingxin_win_close_x <= 0:
                sub_cmd = cfg.get_cmd(ct.cmd_renyuanmingxi_win_close_step_name, ct.cmd_sub_command)
                try:
                    win = GW.get_win(sub_cmd)
                    self.renyuanmingxin_win_close_x = win.BoundingRectangle.right - 20
                    self.renyuanmingxin_win_close_y = win.BoundingRectangle.top + 18
                except Exception as e:
                    pass
            auto_mouse.click(button='left', coords=(self.renyuanmingxin_win_close_x, self.renyuanmingxin_win_close_y))
            time.sleep(1)
        except Exception as e:
            log.logger.error("关闭人员明细窗口。。。error")
            log.logger.error(e)
        try:
            self.close_shebaomingxi_win()
        except Exception as e:
            log.logger.error("关闭申报明细窗口。。。error")
            log.logger.error(e)
        if self.scrolling_times > 0:
            self.scrolling_times = self.scrolling_times - 1
            # 滚回上一行
            try:
                GW.get_win("pane[Name='缴费记录查询']/scrollBar[Name='垂直滚动条']/button[Name='上一行']").Click()
                log.logger.error("返回上一条记录成功")
                time.sleep(1)
            except Exception as e:
                log.logger.error(e)
                log.logger.error("返回上一条记录失败")

    def writeMinxiFileListToFile(self):
        l: int = len(self.jiaofeimingxi_list)
        tmp: int = 1
        if l > 0:
            log.logger.info(f"存储已下载明细文件：{len(self.jiaofeimingxi_list)}")
            file = open(self.mingxi_file_name_list_file_name, "a")
            for item in self.jiaofeimingxi_list:
                file.write(item + '\n')
                # if l != tmp:
                # else:
                #    file.write(item)
                tmp += 1
            file.close()
        # 对于已保存的内容进行清空
        self.jiaofeimingxi_list = []
    def execute(self):
        log.logger.info("地区：%s ,月份:%s 缴费明细:%d===申报明细:%d==当前执行步骤：%s", self.current_city, self.fee_date,
                        self.jiaofeimingxi_count_counter,
                        self.shenbaominxi_count_counter, self.current_cmd)
        operate = cfg.get_cmd(self.current_cmd, ct.cmd_sub_operate)
        # win窗口操作
        try:
            if ct.cmd_sub_operate_win == operate:
                sub_cmd = cfg.get_cmd(self.current_cmd, ct.cmd_sub_command)
                error_ignore = cfg.get_cmd(self.current_cmd, ct.cmd_sub_error_ignore)
                try:
                    win = GW.get_win(sub_cmd)
                except Exception as e:
                    log.logger.info('是否忽略异常%s', error_ignore)
                    log.logger.error(e)
                    if error_ignore:
                        self.current_cmd = cfg.get_cmd(self.current_cmd, ct.cmd_sub_succ_next)
                        return
                    win = None
                if win is not None:
                    if ct.cmd_sub_event_input == cfg.get_cmd(self.current_cmd, ct.cmd_sub_event):
                        # if (cfg.get_cmd(self.current_cmd, ct.cmd_sub_input_clear)):
                        #    pass
                        val: str = cfg.get_cmd(self.current_cmd, ct.cmd_sub_input_val)
                        if ct.cmd_sub_input_val_start_date == val:
                            val = get_month(-2)  # 暂时废弃
                        elif ct.cmd_sub_input_val_end_date == val:
                            val = get_month(-1)  # 暂时废弃
                        else:
                            if '申报明细.pdf' in val:
                                val = self.cache_path + self.inst_id + '_' + self.batch_no + "_" + str(
                                    self.jiaofeimingxi_count_counter) + '_' + str(
                                    self.shenbaominxi_count_counter) +'_' + val.format(
                                    renyuanmingxi_fileFormat=self.renyuanmingxi_fileFormat)
                                self.shenbaominxi_file = val
                            else:
                                metadata: {} = self.shenbaominxi_data[self.shenbaominxi_count_counter - 1]
                                i: str = str(self.shenbaominxi_count_counter)
                                insurance: str = metadata['insurance_' + i]
                                feeType: str = metadata['feeType_' + i]
                                val = self.cache_path + self.inst_id + '_' + self.batch_no + "_" + str(
                                    self.jiaofeimingxi_count_counter) + '_' + str(
                                    self.shenbaominxi_count_counter) + '_' + insurance + '_' + feeType + '_' + val.format(
                                    renyuanmingxi_fileFormat=self.renyuanmingxi_fileFormat)
                        log.logger.info("输入的值为====%s", val)
                        win.SendKeys(val)
                    elif ct.cmd_sub_event_click == cfg.get_cmd(self.current_cmd, ct.cmd_sub_event):
                        log.logger.info('步骤--%s执行了点击', self.current_cmd)
                        win.Click()
                        if ct.cmd_sub_type_scrolling_times == cfg.get_cmd(self.current_cmd, ct.cmd_sub_type):
                            self.scrolling_times = self.scrolling_times + 1
                    elif ct.cmd_sub_event_scroll == cfg.get_cmd(self.current_cmd, ct.cmd_sub_event):
                        scroll_count = cfg.get_cmd(self.current_cmd, ct.cmd_sub_scroll_count)
                        while scroll_count > 0:
                            log.logger.info('步骤==%s执行了点击', self.current_cmd)
                            win.Click()
                            scroll_count = scroll_count - 1
                            time.sleep(1)
                    elif ct.cmd_sub_event_win_txt == cfg.get_cmd(self.current_cmd, ct.cmd_sub_event):
                        if ct.cmd_sub_type_jiaofeimingxi_show_count == cfg.get_cmd(self.current_cmd, ct.cmd_sub_type):
                            if self.jiaofeimingxi_show_count <= 0:
                                win = GW.get_win(sub_cmd)
                                # 减去滚动条和表头的行
                                self.jiaofeimingxi_show_count = (win.BoundingRectangle.height() - 17) // self.table_hight - 1
                                log.logger.info("缴费明细窗口完整展示的记录数为%d", self.jiaofeimingxi_show_count)
                        elif ct.cmd_sub_type_shebaomingxi_show_count == cfg.get_cmd(self.current_cmd, ct.cmd_sub_type):
                            win = GW.get_win(sub_cmd)
                            # 减去滚动条和表头的行
                            self.shebaomingxi_show_count = (win.BoundingRectangle.height() - 17) // self.table_hight - 1
                            log.logger.info("申报明细窗口完整展示的记录数为%d", self.shebaomingxi_show_count)
                    else:
                        pass
            elif ct.cmd_sub_operate_mouse == operate:
                if ct.cmd_sub_event_mouse_click == cfg.get_cmd(self.current_cmd, ct.cmd_sub_event):  # 坐标点击
                    if ct.cmd_sub_type_caozuo_point == cfg.get_cmd(self.current_cmd, ct.cmd_sub_type):
                        if self.jiaofeimingxi_show_count > 0:
                            self.caozuo_y = self.caozuo_y + self.table_hight
                        log.logger.info("缴费明细操作：鼠标点击的坐标-x：%d,-y:%d", self.caozuo_x, self.caozuo_y)
                        auto_mouse.click(button='left', coords=(self.caozuo_x, self.caozuo_y))
                    elif ct.cmd_sub_type_jiaofeirenshu_point == cfg.get_cmd(self.current_cmd, ct.cmd_sub_type):
                        if self.shebaomingxi_show_count > 0:
                            self.shenbaomingxi_jiaofeirenshu_y = self.shenbaomingxi_jiaofeirenshu_y + self.table_hight
                        log.logger.info("申报明细操作：鼠标点击的坐标-x：%d,-y:%d", self.caozuo_x, self.caozuo_y)
                        metadata: {} = self.shenbaominxi_data[self.shenbaominxi_count_counter - 1]
                        i: str = str(self.shenbaominxi_count_counter)
                        if metadata['ignore_' + i]:
                            #沈阳的 部分险种抓取不了 如果遇到需要忽略爬取的数据 则忽略直接跳转到指定步骤
                            log.logger.info("*****地区：%s ，忽略行数为: %d", self.current_city,
                                            self.shenbaominxi_count_counter)
                            self.current_cmd = cfg.get_cmd(self.current_cmd, ct.cmd_sub_fail_next)
                            return
                        else:
                            auto_mouse.click(button='left',
                                             coords=(
                                                 self.shenbaomingxi_jiaofeirenshu_x,
                                                 self.shenbaomingxi_jiaofeirenshu_y))
                    elif ct.cmd_sub_type_renyuanmingxin_win_close == cfg.get_cmd(self.current_cmd,
                                                                                 ct.cmd_sub_type):
                        if self.renyuanmingxin_win_close_x <= 0:
                            sub_cmd = cfg.get_cmd(self.current_cmd, ct.cmd_sub_command)
                            win = GW.get_win(sub_cmd)
                            self.renyuanmingxin_win_close_x = win.BoundingRectangle.right - 20
                            self.renyuanmingxin_win_close_y = win.BoundingRectangle.top + 18
                        log.logger.info("人员明细窗口关闭操作：鼠标点击的坐标-x：%d,-y:%d", self.caozuo_x, self.caozuo_y)
                        auto_mouse.click(button='left',
                                         coords=(self.renyuanmingxin_win_close_x, self.renyuanmingxin_win_close_y))
                    elif ct.cmd_sub_type_shenbaomingxi_win_close == cfg.get_cmd(self.current_cmd, ct.cmd_sub_type):
                        if self.shenbaomingxi_win_close_x <= 0:
                            sub_cmd = cfg.get_cmd(self.current_cmd, ct.cmd_sub_command)
                            win = GW.get_win(sub_cmd)
                            self.shenbaomingxi_win_close_x = win.BoundingRectangle.right - 20
                            self.shenbaomingxi_win_close_y = win.BoundingRectangle.top + 18
                        log.logger.info("申报明细窗口关闭操作：鼠标点击的坐标-x：%d,-y:%d", self.caozuo_x,
                                        self.caozuo_y)
                        auto_mouse.click(button='left',
                                         coords=(self.shenbaomingxi_win_close_x, self.shenbaomingxi_win_close_y))
            elif ct.cmd_sub_operate_judge == operate:
                if ct.cmd_sub_type_jiaofeimingxi_count_judge == cfg.get_cmd(self.current_cmd, ct.cmd_sub_type):
                    if self.jiaofeimingxi_count <= 0:
                        self.current_cmd = cfg.get_cmd(self.current_cmd, ct.cmd_sub_fail_next)
                        return
                elif ct.cmd_sub_type_jiaofeimingxi_need_click_judge == cfg.get_cmd(self.current_cmd, ct.cmd_sub_type):
                    if (self.jiaofeimingxi_index_map is None
                            or len(self.jiaofeimingxi_index_map) == 0
                            or not self.jiaofeimingxi_index_map.get(self.jiaofeimingxi_count_counter, None)):
                        # 对于忽略行数据补充
                        if self.jiaofeimingxi_show_count > 0:
                            self.caozuo_y = self.caozuo_y + self.table_hight
                        self.current_cmd = cfg.get_cmd(self.current_cmd, ct.cmd_sub_fail_next)
                        return
                elif ct.cmd_sub_type_jiaofeimingxi_show_count == cfg.get_cmd(self.current_cmd, ct.cmd_sub_type):
                    if self.jiaofeimingxi_show_count <= 0:
                        self.current_cmd = cfg.get_cmd(self.current_cmd, ct.cmd_sub_fail_next)
                        return
                elif ct.cmd_sub_type_shenbaomingxi_count == cfg.get_cmd(self.current_cmd, ct.cmd_sub_type):
                    if self.shenbaominxi_count > 0:
                        self.current_cmd = cfg.get_cmd(self.current_cmd, ct.cmd_sub_fail_next)
                        return

                elif ct.cmd_sub_type_shenbaomingxi_count_judge == cfg.get_cmd(self.current_cmd, ct.cmd_sub_type):
                    if self.shebaomingxi_show_count <= 0:
                        self.current_cmd = cfg.get_cmd(self.current_cmd, ct.cmd_sub_fail_next)
                        return
                elif ct.cmd_sub_type_shebaomingxi_row_peopel_conut == cfg.get_cmd(self.current_cmd, ct.cmd_sub_type):
                    metadata: {} = self.shenbaominxi_data[self.shenbaominxi_count_counter - 1]
                    i: str = str(self.shenbaominxi_count_counter)
                    peopleCount: int = metadata['peopleCount_' + i]
                    if peopleCount > self.detailMaxCount:
                        self.current_cmd = cfg.get_cmd(self.current_cmd, ct.cmd_sub_fail_next)
                        return

            elif ct.cmd_sub_operate_reset == operate:
                pass
                """
                self.shenbaomingxi_jiaofeirenshu_x = self.org_shenbaomingxi_jiaofeirenshu_x
                self.shenbaomingxi_jiaofeirenshu_y = self.org_shenbaomingxi_jiaofeirenshu_y
                self.shenbaominxi_count_counter = 1
                """
            elif ct.cmd_sub_operate_uuid == operate:
                self.batch_no = uuid.uuid4().__str__()
                self.shenbaomingxi_jiaofeirenshu_x = self.org_shenbaomingxi_jiaofeirenshu_x
                self.shenbaomingxi_jiaofeirenshu_y = self.org_shenbaomingxi_jiaofeirenshu_y
                self.shenbaominxi_count_counter = 1
            elif ct.cmd_sub_operate_data == operate:
                if ct.cmd_sub_type_shenbaomingxi_count_get == cfg.get_cmd(self.current_cmd, ct.cmd_sub_type):
                    # 获取申报明细的记录数
                    if not self.get_shenbaomingxin_detail(self.shenbaominxi_file):
                        return
                    # self.shenbaominxi_count = 1
            elif ct.cmd_sub_operate_calc == operate:
                if ct.cmd_sub_event_reduce == cfg.get_cmd(self.current_cmd, ct.cmd_sub_event):
                    if ct.cmd_sub_type_caozuo_count == cfg.get_cmd(self.current_cmd, ct.cmd_sub_type):
                        self.jiaofeimingxi_count = self.jiaofeimingxi_count - 1
                        self.jiaofeimingxi_show_count = self.jiaofeimingxi_show_count - 1
                        self.jiaofeimingxi_count_counter = self.jiaofeimingxi_count_counter + 1
                        #清空费用明细
                        self.writeMinxiFileListToFile()
                    if ct.cmd_sub_type_shenbaomingxi_count == cfg.get_cmd(self.current_cmd, ct.cmd_sub_type):
                        self.shenbaominxi_count = self.shenbaominxi_count - 1
                        self.shebaomingxi_show_count = self.shebaomingxi_show_count - 1
                        metadata: {} = self.shenbaominxi_data[self.shenbaominxi_count_counter - 1]
                        i: str = str(self.shenbaominxi_count_counter)
                        ignore: bool = metadata['ignore_' + i]
                        monthStart: str = metadata['monthStart_' + i]
                        monthEnd: str = metadata['monthEnd_' + i]
                        insurance: str = metadata['insurance_' + i]
                        feeType: str = metadata['feeType_' + i]
                        amt: str = metadata['amt_' + i]
                        peopleCount: int = metadata['peopleCount_' + i]
                        #如果超过人数上限制 则pdf下载不了，用excel上传到系统
                        pdf_suffix: str = "pdf"  #默认
                        if peopleCount > self.detailMaxCount:
                            pdf_suffix = self.renyuanmingxi_fileFormat
                        if ignore:
                            log.logger.info("==>地区：%s ，忽略行数为: %d  费用项目：%s  征收项目:%s", self.current_city,
                                            self.shenbaominxi_count_counter, insurance, feeType)
                            self.row = None
                        else:
                            fileName = self.inst_id + '_' + self.batch_no + "_" + str(
                                self.jiaofeimingxi_count_counter) + '_' + str(
                                self.shenbaominxi_count_counter) + '_' + insurance + '_' + feeType + '_' + '人员明细' + '.'

                            self.row = self.inst_id + '|' + self.batch_no + '|' + i + '|' + monthStart + '|' + monthEnd + '|' + insurance + '|' + feeType + '|' + amt + "|" + fileName + pdf_suffix + "|" + fileName + self.renyuanmingxi_fileFormat
                            log.logger.info("==row====" + self.row)
                        # 从元数据list获取元数据信息，这个+1不能放在前面
                        self.shenbaominxi_count_counter = self.shenbaominxi_count_counter + 1

            elif ct.cmd_sub_operate_cache == operate:
                if self.row is not None:
                    self.jiaofeimingxi_list.append(self.row)
            # 休眠
            if cfg.get_cmd(self.current_cmd, ct.cmd_sub_time_interval) > 0:
                time.sleep(cfg.get_cmd(self.current_cmd, ct.cmd_sub_time_interval))
            self.current_cmd = cfg.get_cmd(self.current_cmd, ct.cmd_sub_succ_next)
        except Exception as e:
            log.logger.error("执行错误%s，终止执行，指令置为空", self.current_cmd)
            log.logger.error(e)
            log.logger.error(traceback.format_exc())
            if self.step_retry():
                #直接不改变步骤 重试
                return
            self.close_all_win_retry()
            if ct.cmd_retry_max_count < self.retry_count:
                log.logger.error("警告：重试次数过多，终止重试，流程号:%s ,当前重试次数：%d", self.inst_id,
                                 self.retry_count)
                self.current_cmd = ct.cmd_end
                return
            self.current_cmd = ct.cmd_retry


# 根据interval 获取月份
def get_month(interval: int):
    now = datetime.datetime.now().date()
    return now.replace(month=now.month + interval).strftime("%Y-%m")


def execute():
    log.logger.info("获取费用开始，参数处理 start")
    log.logger.info(sys.argv)
    social = SocialFeeClient()
    #log.logger.info(sys.argv)
    social.cache_path = sys.argv[1]
    social.caozuo_x = int(sys.argv[2])
    social.caozuo_y = int(sys.argv[3])
    social.jiaofeimingxi_count = int(sys.argv[4])
    # # 只跑一条  只需要跑第一条就可以了
    # 由于应收凭证序号存在不同，需要查询不同的应收凭证
    # if social.jiaofeimingxi_count > 0:
    #     social.jiaofeimingxi_count = 1
    social.org_shenbaomingxi_jiaofeirenshu_x = int(sys.argv[5])
    social.org_shenbaomingxi_jiaofeirenshu_y = int(sys.argv[6])
    social.shenbaomingxi_jiaofeirenshu_x = int(sys.argv[5])
    social.shenbaomingxi_jiaofeirenshu_y = int(sys.argv[6])
    social.inst_id = sys.argv[7]
    social.renyuanmingxi_fileFormat = sys.argv[8]
    social.fee_date = sys.argv[9]
    social.current_city = sys.argv[10]
    _excludeInsurance: str = sys.argv[11]
    social.detailMaxCount: int = int(sys.argv[12])
    social.jiaofeimingxi_index_map = {val: val for val in json.loads(sys.argv[14])}
    social.mingxi_file_name_list_file_name = social.cache_path + social.inst_id + '_' + social.fee_date + ".txt"
    amt = json.loads(sys.argv[13])
    for a in amt:
        social.obtainedFeeDetail.append(Decimal(a).quantize(Decimal('0.00')))
    if len(_excludeInsurance) > 0:
        social.excludeInsurance = _excludeInsurance.split(",")
    else:
        social.excludeInsurance = None
    log.logger.info("获取费用开始，参数处理 end")

    #先创建并清空内容
    with open(social.mingxi_file_name_list_file_name, 'w') as file:
        file.truncate()

    while True:
        social.execute()
        if social.current_cmd == ct.cmd_end:
            log.logging.info("****执行步骤成功%s", '666')
            log.logger.info(len(social.jiaofeimingxi_list))
            l: int = len(social.jiaofeimingxi_list)
            tmp: int = 1
            if l > 0:
                file = open(social.mingxi_file_name_list_file_name, "a")
                for item in social.jiaofeimingxi_list:
                    if l != tmp:
                        file.write(item + '\n')
                    else:
                        file.write(item)
                    tmp += 1
                file.close()
            log.logging.info("****执行步骤成功%s", '666')
            if social.step_retry is not None:
                log.logger.info("****流程编号：%s 地区 %s,月份：%s 步骤重试情况，", social.inst_id, social.current_city,
                                social.fee_date)
                log.logger.info(social.step_retry)
            print("全部获取成功")
            break
        elif social.current_cmd is None:
            #现在没有使用这个逻辑了
            l: int = len(social.jiaofeimingxi_list)
            tmp: int = 1
            if l > 0:
                file = open(social.mingxi_file_name_list_file_name, "a")
                for item in social.jiaofeimingxi_list:
                    if l != tmp:
                        file.write(item + '\n')
                    else:
                        file.write(item)
                    tmp += 1
                file.close()
            log.logging.error("****执行步骤失败%s", 'www')
            if social.step_retry is not None:
                log.logger.info("==>流程编号：%s 地区 %s,月份：%s 步骤重试情况，", social.inst_id, social.current_city,
                                social.fee_date)
                log.logger.info(social.step_retry)
            print("全部获取成功")
            break


execute()
# """
# if __name__ == '__main__':
#     social = SocialFeeClient()
#     # todo 测试需要
#     social.jiaofeimingxi_count = 25
#     social.fee_date = '2024-10'
#     social.current_city = '重庆'
#     #social.excludeInsurance = ['基本医疗保险费|职工基本医疗保险(单位缴纳)']
#     social.excludeInsurance = None
#     social.detailMaxCount = 1000
#     social.renyuanmingxi_fileFormat = "xls"
#     social.jiaofeimingxi_index_map = {1:{"序号":1},18:{"序号":18}}
#     social.caozuo_x = 1780
#     social.caozuo_y = 280
#     amt = json.loads("[1818662.65]")
#     for a in amt:
#         #四舍五入
#         social.obtainedFeeDetail.append(Decimal(a).quantize(Decimal('0.00')))
#     while True:
#         social.execute()
#         # log.logger.info("===>" + social.current_cmd)
#         if social.current_cmd == ct.cmd_end:
#             log.logging.info("****执行步骤成功%s", '666')
#             log.logger.info(len(social.jiaofeimingxi_list))
#             l: int = len(social.jiaofeimingxi_list)
#             tmp: int = 1
#             if l > 0:
#                 file = open(social.cache_path + social.inst_id + '_' + social.fee_date + ".txt", "w")
#                 for item in social.jiaofeimingxi_list:
#                     if l != tmp:
#                         file.write(item + '\n')
#                     else:
#                         file.write(item)
#                     tmp += 1
#                 file.close()
#             log.logging.info("****执行步骤成功%s", '666')
#             if social.step_retry is not None:
#                 log.logger.info("==>流程编号：%s 地区 %s,月份：%s 步骤重试情况，", social.inst_id, social.current_city,
#                             social.fee_date)
#                 log.logger.info(social.step_retry)
#             print("全部获取成功")
#             break
#         elif social.current_cmd is None:
#             log.logging.error("****执行步骤失败%s", 'www')
#             print("执行失败")
#             break
# """

