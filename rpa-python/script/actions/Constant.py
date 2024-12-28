base_dir = "D:/seebon/rpa/python/"

shenbaomingxi_monthStart_col: str = "费款所属日期起"
shenbaomingxi_monthEnd_col: str = "费款所属日期止"
shenbaomingxi_insurance_col: str = "参保费种"
shenbaomingxi_feeType_col: str = "征收品目"
shenbaomingxi_amt_col: str = "本期应缴费额"
shenbaomingxi_act_amt_col: str = "本期实际应缴纳费额"
shenbaomingxi_jiaofe_people_count = "缴费人数"

# 命令开始
cmd_start = "start"
cmd_retry = "失败重试"
#每个流程可以重试的最大次数
cmd_retry_max_count = 10
cmd_shebaomingxi_win_close_step_name = "申报明细-关闭窗口"
cmd_renyuanmingxi_win_close_step_name = "申报明细-人员费用明细窗口关闭"
cmd_end = "end"
cmd_sub_step_retry = "step_retry"
cmd_sub_type = "type"
cmd_sub_succ_next = "succ_next"
cmd_sub_fail_next = "fail_next"
cmd_sub_time_interval = "time_interval"
cmd_sub_config_key = "config_key"
cmd_sub_status = "status"
cmd_sub_event = "event"
cmd_sub_command = "command"
cmd_sub_input_clear = 'input_clear'
cmd_sub_step_name = 'step_name'
cmd_sub_input_val = 'input_val'
cmd_sub_input_val_start_date = "start_date"
cmd_sub_input_val_end_date = "end_date"
cmd_sub_operate = "operate"
cmd_sub_scroll_count = "scroll_count"
cmd_sub_type_scrolling_times = "scrolling_times"

cmd_sub_status_valid = "valid"  # 状态有效
cmd_sub_event_click = "click"  # 点击
cmd_sub_operate_win = "win"
cmd_sub_event_win_txt = "win_txt"

cmd_sub_operate_mouse = "mouse"
cmd_sub_operate_judge = "judge"
cmd_sub_operate_reset = "reset"
cmd_sub_operate_uuid = "uuid"
cmd_sub_operate_data = "data"
cmd_sub_operate_calc = "calc"
cmd_sub_operate_io = "io"
cmd_sub_operate_cache = "cache"
cmd_sub_event_input = "input"
cmd_sub_event_scroll = "scroll"
cmd_sub_event_reduce = "reduce"
cmd_sub_event_mouse_click = "mouse_click"
cmd_sub_type_caozuo_point = "caozuo_point"
cmd_sub_type_jiaofeirenshu_point = "jiaofeirenshu_point"
cmd_sub_type_renyuanmingxin_win_close = "renyuanmingxin_win_close"
cmd_sub_type_shenbaomingxi_win_close = "shenbaomingxi_win_close"
cmd_sub_type_jiaofeimingxi_show_count = "jiaofeimingxi_show_count"
cmd_sub_type_shebaomingxi_show_count = "shebaomingxi_show_count"
cmd_sub_type_shebaomingxi_row_peopel_conut = "shebaomingxi_row_peopel_conut"

cmd_sub_type_shenbaomingxi_count = "shenbaominxi_count"
cmd_sub_type_jiaofeimingxi_count_judge = "jiaofeimingxi_count_judge"
cmd_sub_type_jiaofeimingxi_need_click_judge = "jiaofeimingxi_need_click_judge"
cmd_sub_type_shenbaomingxi_count_judge = "shebaomingxi_show_count_judge"
cmd_sub_type_caozuo_count = "caozuo_count"
cmd_sub_type_shenbaomingxi_count_get = "shenbaomingxi_count_get"
cmd_sub_error_ignore = "error_ignore"

"""
cmd_sub_event_condition_ele_exist = "condition_ele_exist"  # 判断元素是否存在

cmd_sub_event_conditional_judgment = 'conditional_judgment'
cmd_default_time_interval = 10  # 默认执行间隔
cmd_sub_manual_false = 'false'
cmd_sub_command_split = "_SPLIT_"
"""
