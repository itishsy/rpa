package com.seebon.rpa.actions.impl.declare;

import cn.hutool.core.map.MapUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.seebon.common.utils.FileZipUtil;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.ExcleFormat;
import com.seebon.rpa.service.RobotCommonService;
import com.seebon.rpa.utils.ELParser;
import com.seebon.rpa.utils.python.PythonUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RobotAction(name = "客户端费用明细", order = 50, targetType = NoneTarget.class, comment = "客户端费用明细(只能按一个月一个月下载处理)")
public class FeeDetailFromClient extends AbstractAction {
    @ActionArgs(value = "缴费记录表头操作横坐标", comment = "缴费记录表头操作横坐标", required = true, style = DynamicFieldType.text)
    private String operationPointX;

    @ActionArgs(value = "缴费记录表头操作纵坐标", comment = "缴费记录表头操作纵坐标", required = true, style = DynamicFieldType.text)
    private String operationPointY;

    @ActionArgs(value = "文件保存目录", comment = "文件保存目录", required = true, style = DynamicFieldType.text)
    private String filePath;

    @ActionArgs(value = "缴费记录总数", comment = "缴费记录总数", required = true, style = DynamicFieldType.text)
    private String totalCount;

    @ActionArgs(value = "申报明细-缴费人数横坐标", style = DynamicFieldType.text, required = true)
    private String jfrsPointX;
    @ActionArgs(value = "申报明细-缴费人数纵坐标", style = DynamicFieldType.text, required = true)
    private String jfrsPointY;
    @ActionArgs(value = "流程号", style = DynamicFieldType.text, required = true)
    private String instId;
    @ActionArgs(value = "费用明细格式", required = true, dict = ExcleFormat.class)
    private String fileFormat;
    @ActionArgs(value = "人员费用表头序号", required = true)
    private String tableHeader;
    @ActionArgs(value = "所用所属期", comment = "格式yyyyy-MM", required = true)
    private String feeDate;
    @ActionArgs(value = "获不到明细的项（列表）", comment = "格式:征收项目*征收品目,多个用英文逗号隔开")
    private String excludeInsurance;
    @ActionArgs(value = "人数限制", comment = "费用附件下载社保费用客户端限制1000人以内，超过就不给下载，处理方案是用其他数据代替，此配置若有配置以配置为准，反之用默认")
    private String detailMaxCount;
    @ActionArgs(value = "缴费明细导出文件", comment = "缴费明细导出文件地址")
    private String jiaofeimingxiPath;

    @Value("${tax.client.city.mapping}")
    private String cityMapping;
    @Value("${tax.client.excludeInsurance.mapping}")
    private String excludeInsuranceMapping;

    @Autowired
    private RobotCommonService robotCommonService;

    @Override
    public void run() {

        if (StringUtils.isBlank(excludeInsurance)) {
            String city = JSON.parseObject(cityMapping).getString(ctx.get("addrName"));
            excludeInsurance = JSON.parseObject(excludeInsuranceMapping).getString(city);
        }

        //todo
        //feeDate = "2024-05";
        log.info("当前流程号:{}", ELParser.parse(instId, ctx.getVariables(), String.class));
        List<String> commands = Lists.newArrayList();
        String cachePath = ELParser.parse(filePath, ctx.getVariables(), String.class);
        commands.add(RobotContext.pythonPath);  //0
        commands.add(RobotContext.workPath + "\\python\\SocialFeeLoadFromClient.py"); //1
        commands.add(cachePath);//1

        commands.add(ELParser.parse(operationPointX, ctx.getVariables(), String.class)); //2
        commands.add(ELParser.parse(operationPointY, ctx.getVariables(), String.class));//3
        commands.add(ELParser.parse(totalCount, ctx.getVariables(), String.class));//4
        commands.add(ELParser.parse(jfrsPointX, ctx.getVariables(), String.class));//5
        commands.add(ELParser.parse(jfrsPointY, ctx.getVariables(), String.class));//6
      /*
        //
        commands.add("1323");  //2
        commands.add("449");//3
        commands.add("8");//4
        commands.add("776");//5
        commands.add("379"); //6
  */
        commands.add(ELParser.parse(instId, ctx.getVariables(), String.class)); //7
        commands.add(fileFormat.toLowerCase()); //8
        //commands.add("xls");  //8
        commands.add(feeDate);  //9
        //commands.add("2024-05"); //9
        commands.add(ctx.get("addrName"));//10
        //字段不能传空  否则部分环境python会将list的值移除   尚未查到原因  竖线也不能传  空字符串的值也被过滤
        commands.add(StringUtils.isBlank(excludeInsurance)?"不能传空*否则报错":ELParser.parse(excludeInsurance, ctx.getVariables(), String.class)); //11
        commands.add(StringUtils.isBlank(detailMaxCount)?"1000":detailMaxCount); //12
        commands.add(obtainedFeeDetail().toString());//已经获取过的数据    13
        commands.add(JSON.toJSONString(getJiaofeimingxiIndexMap().keySet()));//已经获取过的数据    14
        //commands.add("[]"); //13

        //获取本月户获取过数据  如果已经获取 python会自动终止
        log.info("客户端实缴参数:{}", JSON.toJSONString(commands));
        PythonUtil.runCommand("客户端取实缴费用", "全部获取成功", this.getTimeout(), commands);
        List<String> feeList = readFeeFile();
        if (CollectionUtils.isNotEmpty(feeList)) {
            //String monthStart = null;
            BigDecimal amount = BigDecimal.ZERO;
            //上传文件
            List<Map<String, Object>> _dataList = Lists.newLinkedList();
            Map<String, Map<String, Object>> _dataMap = Maps.newLinkedHashMap();
            Integer listSize = feeList.size();
            File[] listFile = new File[listSize];
            for (int i = 0; i < listSize; i++) {
                String file = feeList.get(i);
                try {
                    String[] tmp = file.split("\\|");
                    //monthStart = tmp[3];
                    // String monthEnd = tmp[4];
                    String insurance = tmp[5];
                    String feeType = tmp[6];
                    String _amount = tmp[7];
                    amount = amount.add(new BigDecimal(_amount));
                    String pdfFile = tmp[8];
                    listFile[i] = new File(cachePath + pdfFile);
                    String xlsxFile = tmp[9];
                    List<Map<String, String>> list = new LinkedList<>();
                    FileInputStream fileInputStream = new FileInputStream(cachePath + xlsxFile);
                    EasyExcel.read(fileInputStream)
                            .sheet()
                            .registerReadListener(new AnalysisEventListener<Map<String, String>>() {

                                @Override
                                public void invoke(Map<String, String> o, AnalysisContext analysisContext) {
                                    list.add(o);
                                }

                                @Override
                                public void invokeHeadMap(Map<Integer,String> headMap, AnalysisContext context) {
                                    System.out.println("解析到的表头数据: " + headMap.toString());
//                                    if (StringUtils.isEmpty(tableHeader)) {
                                    HashMap<String, String> headIndexMap = Maps.newHashMap();
                                    headMap.forEach((k,v)->headIndexMap.put(v,String.valueOf(k)));
                                    tableHeader = JSONObject.toJSONString(headIndexMap);
//                                    }
                                }

                                @Override
                                public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                                }
                            }).doRead();


                    //不同的地区excel的表格不一样
                    //上海 -- 序号	姓名	证件类型	证件号码	缴费工资	缴费基数	费率	应缴费额	减免费额	应补(退)费额	人员编号
                    //天津 -- 序号	姓名	证件类型	证件号码	缴费工资	缴费基数	费率	应缴费额	人员编号
                    //{"序号":0,"姓名":1,"证件类型":2,"证件号码":3,"缴费工资":4,"缴费基数":5,"费率":6,"应缴费额":7,"减免费额":8,"应补(退)费额":9,"人员编号":10}
                    JSONObject header = JSON.parseObject(tableHeader);
                    for (Map<String, String> a : list) {
//                 /*       a.put(FeeItem.参保费种.colName, insurance);
//                        a.put(FeeItem.征收品目.getColName(), feeType);*/
                        a.put(FeeItem.费款所属日期起.colName, feeDate);
                        a.put(FeeItem.费款所属日期止.colName, feeDate);
                        a.put("insurance", insurance);
                        a.put("feeType", feeType);
                        //amount.add(new BigDecimal(a.get(header.getInteger(FeeItem.应缴费额.colName)).replace(",", "")));
                        convert(a, header, _dataMap);
                    }


                } catch (Exception e) {
                    log.error("上传实缴数据失败", e);
                }
            }
            String zipFile = FileZipUtil.zipFiles(cachePath, instId + ".zip", listFile);
            String fileId = robotCommonService.fileUpload(new File(zipFile));
            log.info("上传文件结果：{}", fileId);
            HashMap<String, Object> params = Maps.newHashMap();
            params.put("orgName", ctx.get("companyName"));
            params.put("accountNumber", ctx.getAccountNumber());
            params.put("addressName", ctx.get("addrName"));
            params.put("businessType", ctx.get("businessTypeInt"));
            params.put("prefix", ctx.get("tplTypeCode"));
            params.put("periodOfExpense", feeDate);
            params.put("totalAmount", amount.toString());
            params.put("fileId", fileId);
            params.put("coverFlag",Boolean.TRUE);
            _dataList.addAll(_dataMap.values());
            params.put("mapList", _dataList);
            //log.info("保存费用明细完成,费用明细人数：{},请求参数:{}", _dataList.size(), JSON.toJSONString(params));
            String resp = robotCommonService.savePaidInPersonDataUrl(params);
            log.info("保存费用明细完成,费用明细人数：{},保存结果：{},请求参数:{}", _dataList.size(), resp, JSON.toJSONString(params));
        }
        log.info("客户端费用明细保存完成");
    }

    /**
     * 读取并去重【应征凭证序号】的列
     * @return
     */
    private Map<String ,Map<String, String>> getJiaofeimingxiIndexMap(){

        List<Map<String,String>> list = new LinkedList<>();
        HashMap<String, String> headIndexMap = Maps.newHashMap();
        EasyExcel.read(parse("${jiaofeijilu_export_path}",String.class))
                .sheet()
                .registerReadListener(new AnalysisEventListener<Map<Integer,String>>() {

                    @Override
                    public void invokeHeadMap(Map<Integer,String> headMap, AnalysisContext context) {
                        System.out.println("解析到的表头数据: " + headMap.toString());
                        headMap.forEach((k,v)->headIndexMap.put(String.valueOf(k),v));
                    }

                    @Override
                    public void invoke(Map<Integer,String> o, AnalysisContext analysisContext) {
                        HashMap<String, String> hashMap = Maps.newHashMap();
                        o.forEach((x,y)-> hashMap.put(headIndexMap.get(x.toString()),y));
                        list.add(hashMap);
                    }
                    @Override
                    public void doAfterAllAnalysed(AnalysisContext analysisContext) {}
                }).doRead();

        // 使用Stream API进行去重处理
        Map<String,Map<String, String>> jiaofeimingxiIndexMap = list.stream()
                .collect(Collectors.toMap(
                        record -> record.get("应征凭证序号").toString(),  // 使用应征凭证序号作为键
                        record -> record,                          // 使用记录本身作为值
                        (existing, replacement) -> {
                            // 如果现有的记录的序号小于替换记录的序号，则保留现有的
                            int existingSeq = Integer.parseInt(existing.get("序号").toString());
                            int replacementSeq = Integer.parseInt(replacement.get("序号").toString());
                            return existingSeq < replacementSeq ? existing : replacement;
                        }
                )) // 将结果转换回列表
                .values()
                .stream()
                .collect(Collectors.toMap(
                        record -> record.get("序号").toString(),  // 使用应征凭证序号作为键
                        record -> MapUtil.builder("应征凭证序号",record.get("应征凭证序号").toString()).map()
                ));
        log.info("读取并去重【应征凭证序号】的列:"+jiaofeimingxiIndexMap.toString());
        return jiaofeimingxiIndexMap;
    }

    private Set<BigDecimal> obtainedFeeDetail(){
        /**
         * orgName  --公司名
         * accountNumber  --单位社保号/公积金号
         * addressName    --参保地
         * businessType --业务类型
         * prefix  --系统类型
         * periodOfExpense -- 费款所属期
         * totalAmount --总金额
         * **/

        Map<String, Object> params = Maps.newLinkedHashMapWithExpectedSize(6);
        Set<BigDecimal> totalAmts = Sets.newHashSet();
        try {
            params.put("orgName", ctx.get("companyName"));
            params.put("accountNumber", ctx.getAccountNumber());
            params.put("addressName", ctx.get("addrName"));
            params.put("businessType", ctx.get("businessTypeInt"));
            params.put("prefix", ctx.get("tplTypeCode"));
            params.put("periodOfExpense", feeDate);
            String res = robotCommonService.obtainedFeeDetail(params);
            log.info("一会去的记录查询结果:{}  查询参数",res,params);
            JSONArray datas = JSON.parseObject(res).getJSONArray("data");
            for (int i = 0; i < datas.size(); i++) {
                JSONObject d =  datas.getJSONObject(i);
                totalAmts.add(d.getBigDecimal("totalAmount").setScale(2, BigDecimal.ROUND_HALF_UP));

            }
        }catch (Exception e){
            log.info("获取历史爬取的数据异常，异常则机器人继续爬",e);
        }
        return totalAmts;
    }

    @Getter
    private static enum FeeItem {
        序号("序号", "序号", false),
        姓名("姓名", "姓名", false),
        证件类型("证件类型", "证件类型", false),
        证件号码("证件号码", "证件号码", false),
        缴费工资("缴费工资", "缴费工资", true),
        缴费基数("缴费基数", "${insurance}-${feeType}-缴费基数", true),
        费率("费率", "${insurance}-${feeType}-费率", false),
        应缴费额("应缴费额", "${insurance}-${feeType}-应缴费额", true),
        减免费额("减免费额", "${insurance}-${feeType}-减免费额", true),
        应补_退_费额("应补(退)费额", "${insurance}-${feeType}-应补(退)费额", true),
        人员编号("人员编号", "人员编号", false),
        /*        参保费种("参保费种", "参保费种", false),
                征收品目("征收品目", "征收品目", false),*/
        费款所属日期起("费款所属日期起", "费款所属日期起", false),
        费款所属日期止("费款所属日期止", "费款所属日期止", false);

        //以上海为基准 序号	姓名	证件类型	证件号码	缴费工资	缴费基数	费率	应缴费额	减免费额	应补(退)费额	人员编号  + 几个自定义字段 参保费种 征收品目 费款所属日期起 费款所属日期止
        private String colName;
        private String format;
        private Boolean is_amt;

        FeeItem(String colName, String format, Boolean is_amt) {
            this.colName = colName;
            this.format = format;
            this.is_amt = is_amt;
        }
    }

    private void convert(Map<String, String> data, JSONObject tableHeader, Map<String, Map<String, Object>> _dataMap) throws IOException, TemplateException {
        Integer idcardIndex = tableHeader.getInteger(FeeItem.证件号码.colName);
        Map<String, Object> item = _dataMap.get(data.get(idcardIndex));
        if (MapUtils.isEmpty(item)) {
            item = Maps.newLinkedHashMap();
            _dataMap.put(data.get(idcardIndex), item);
        }
        for (FeeItem _item : FeeItem.values()) {
            if (/*_item.equals(FeeItem.征收品目) || _item.equals(FeeItem.参保费种) ||*/ _item.equals(FeeItem.费款所属日期起) || _item.equals(FeeItem.费款所属日期止)) {
                item.put(_item.getColName(), data.get(_item.getColName()));
                continue;
            }
            Integer index = tableHeader.getInteger(_item.getColName());
            //用格式化值作为key
            StringWriter result = new StringWriter();
            Template t = new Template("template", new StringReader(_item.getFormat()), new Configuration(Configuration.VERSION_2_3_20));
            t.process(data, result);
            String key = result.toString();
            if (null == index) {
                //舍弃
                continue;
            }
            String val = StringUtils.isBlank(data.get(index)) ? "" : data.get(index);
            if (_item.is_amt) {
                //金额带逗号
                val = val.replace(",", "");
            }
            item.put(key, val);
        }
    }

    private List<String> readFeeFile() {
        List<String> lines = null;
        try {
            String file = ELParser.parse(filePath, ctx.getVariables(), String.class) + ELParser.parse(instId, ctx.getVariables(), String.class) + "_" + feeDate + ".txt";
            //String file = ELParser.parse(filePath, ctx.getVariables(), String.class) + "69a49a18410a4e01a4e84b5afe45815e_2024-05.txt";
            // 创建InputStreamReader对象，并指定编码方式
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), Charset.forName("GBK"));
            // 创建BufferedReader对象
            BufferedReader reader = new BufferedReader(inputStreamReader);
            lines = Lists.newLinkedList();
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                lines.add(tempString);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            log.error("==抓取数据异常，没获取到数据(找不到文件的可能是超过最大次数没有爬取成功或者已经爬过，忽略)");
        } catch (IOException e) {
            log.error("===抓取数据异常，没获取到数据");
            throw new RuntimeException(e);
        }
        return lines;
    }
}
