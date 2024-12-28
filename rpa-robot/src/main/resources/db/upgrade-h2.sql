alter table robot_execution_file  add step_code varchar(50) DEFAULT NULL comment '步骤编码';

alter table robot_flow add column open_type int(11) NULL DEFAULT NULL COMMENT '开放类型  0-开放  1-私有';

alter table robot_flow add column template_type int(11) NULL DEFAULT NULL COMMENT '是否模板(0：否，1：是)';

alter table robot_flow add column tag_code varchar(255) NULL DEFAULT NULL COMMENT '标签编码';

alter table robot_flow_step add column group_code varchar(255)  DEFAULT NULL COMMENT '代码块编码';

alter table robot_flow_step add column false_skip_to varchar(255) DEFAULT NULL COMMENT '为false跳转步(判断指令才有效)';

alter table robot_flow_step add column type int(11) NULL DEFAULT NULL COMMENT '步骤类型：0-自定义,1-模板';

alter table robot_flow_step add column open_edit int(11) NULL DEFAULT NULL COMMENT '是否放开编辑（0：否，1：是）';

alter table robot_flow_step add column max_execute_num int(11) NULL DEFAULT NULL COMMENT '最大执行次数';

alter table robot_app add column run_total_times bigint(20) DEFAULT '0' COMMENT '运行总时长 毫秒';

alter table robot_app add column run_status tinyint(1) DEFAULT '0' COMMENT '运行状态：0 下架  1 上架 ';

alter table robot_app add column online_status tinyint(1) DEFAULT '0' COMMENT '上线阶段：0 调研 1 配置 2 上线 3运维';

alter table robot_app add column website_links varchar(4096) DEFAULT NULL COMMENT '网站链接';

alter table robot_app add column remark varchar(255) DEFAULT NULL COMMENT '下架备注';

alter table robot_app add column session_keep int(11) DEFAULT NULL COMMENT 'session维持（0：否，1-是）';

alter table robot_app add column batch_run int(11) DEFAULT NULL COMMENT '并行执行（0-不可以，1-可以）';

alter table robot_execution_data add column operationType int(11) DEFAULT NULL COMMENT '申报数据操作类型 （参考表 policy_declare_operation_type_dict）';

alter table robot_execution_data add column nodeComment varchar(500) DEFAULT NULL COMMENT '节点结果备注';

alter table robot_execution_data add column client_id int(11) DEFAULT NULL COMMENT '客户id';

alter table robot_execution_data add column machine_code varchar(50) DEFAULT NULL COMMENT '机器标识码';

alter table robot_execution_data add column uuid varchar(50) DEFAULT NULL COMMENT '唯一编码';

alter table robot_execution_data add column flagType int(11) DEFAULT NULL COMMENT '标记类型 1：新流程，2：旧流程';

alter table robot_execution_detail add column step_data longtext COMMENT '步骤数据';

alter table robot_flow add column service_item int(1) NULL DEFAULT NULL COMMENT '服务项目，1：增员，2：减员，3：调基，5：补缴，6：缴费，7：名册';

alter table robot_task_schedule add column status int(1) NULL DEFAULT '1' COMMENT '状态，1：启用，0：停用';

alter table robot_task_schedule add column comment varchar(1000) NULL DEFAULT NULL COMMENT '原因备注';

alter table robot_flow_step add column success_retry int(11)  DEFAULT NULL COMMENT '成功跳转重试次数';

alter table robot_flow_step modify column step_name varchar(4000) NOT NULL COMMENT '步骤名称';

alter table robot_flow_step modify column target_args varchar(8000) NULL DEFAULT NULL COMMENT '行为参数值';

alter table robot_flow_step modify column action_args varchar(8000) NULL DEFAULT NULL COMMENT '行为参数值';

alter table robot_flow add column declare_system varchar(100) NULL DEFAULT NULL COMMENT '申报系统 字典 10007,10008';

alter table robot_flow add column run_support varchar(100) NULL DEFAULT NULL COMMENT '运行载体 字典 10019';

alter table robot_app add column app_remark varchar(1000) NULL DEFAULT NULL COMMENT '应用备注';

alter table robot_app add column app_status int(11) NULL DEFAULT NULL COMMENT '应用状态 0:调研 1:配置 2:调试通过 3:等待数据 4:验证有效 5:正常运行 6:修复问题';

alter table robot_execution_monitor add column zy_num int(11)  DEFAULT NULL COMMENT '增员人数';

alter table robot_execution_monitor add column jy_num int(11)  DEFAULT NULL COMMENT '减员人数';

alter table robot_execution_monitor add column tj_num int(11)  DEFAULT NULL COMMENT '调基人数';

alter table robot_execution_monitor add column bj_num int(11)  DEFAULT NULL COMMENT '补缴人数';

alter table robot_execution_detail add column error_stack longtext COMMENT '错误信息堆栈';

alter table robot_args_define modify column `cond` varchar(1000) DEFAULT NULL COMMENT '联动条件拼接';

alter table robot_execution_file modify column file_name varchar(250) COMMENT '文件名';

alter table robot_execution_file modify column file_name varchar(1024) COMMENT '文件名';

alter table robot_task add column comment varchar(1000) NULL DEFAULT NULL COMMENT '原因备注';

alter table robot_task modify column `comment` text DEFAULT NULL COMMENT '原因备注';

alter table robot_task add column enable_time datetime DEFAULT NULL COMMENT '启用时间';

alter table robot_task add column sync_fee int(11) NULL DEFAULT NULL COMMENT '费用机器同步标识  0-未同步  1-已同步';

alter table robot_task add column sync_machine_code text DEFAULT NULL COMMENT '同步机器标识码，逗号隔开';

alter table robot_task_args modify column args_value text DEFAULT NULL COMMENT '参数值';

alter table robot_task_session_keep add column lose_xpath varchar(255) DEFAULT NULL COMMENT '会话丢失xpath';

alter table robot_task_session_keep add column retry_num int(11) NULL DEFAULT NULL COMMENT '刷新重试次数';

alter table robot_task_session_keep add column file_id  int(11) NULL DEFAULT NULL COMMENT '会话失效截图文件Id';

alter table robot_task_session_keep add column sync_machine_code text COMMENT '同步机器标识码，逗号隔开';

alter table robot_task_session_keep add column start_keep_time datetime(0) NULL DEFAULT NULL COMMENT '会话维持开始时间';

alter table robot_task_session_keep add column end_keep_time datetime(0) NULL DEFAULT NULL COMMENT '会话失效时间';

alter table robot_task_session_keep add column share_port int(11) DEFAULT NULL COMMENT '共享端口号';

alter table robot_task_session_keep modify column port int(11) NOT NULL COMMENT '远程端口号';

alter table robot_task_session_keep modify column status int(11) NULL DEFAULT 1 COMMENT '维持状态：1-浏览器关闭 2-会话丢失 3-会话维持';

alter table robot_task_session_keep add column create_id int(11) NULL DEFAULT NULL COMMENT '创建者Id';

alter table robot_task_session_keep add column create_name varchar(255) DEFAULT NULL COMMENT '创建者名称';

alter table robot_task_session_keep add column match_express varchar(255) DEFAULT NULL COMMENT '匹配表达式';

alter table robot_task_session_keep add column login_status int(11) DEFAULT '0' COMMENT '登录状态  0-未登录  1-已登录';

alter table robot_task_session_keep add column max_keep_time int(11) DEFAULT NULL COMMENT '最长维持时间(单位小时)';

alter table robot_task_session_keep add column disabled int(11) DEFAULT '1' COMMENT '启用状态：0-禁用，1-启用';

alter table robot_task_queue add column phone_number varchar(50) DEFAULT NULL COMMENT '通知手机号码';

alter table robot_task_queue add column login_status int(11) DEFAULT '-1' COMMENT '登录状态 -1-待登录 0-开始登录 1-登录成功 2-登录失败';

alter table robot_task_queue add column login_type varchar(255) DEFAULT NULL COMMENT '登录方式';

alter table robot_task_queue add column login_time datetime(0) DEFAULT NULL COMMENT '登录时间';

alter table robot_app_ca add column declare_account text DEFAULT NULL COMMENT '申报账户，逗号隔开';

alter table robot_app_ca add column disabled int(11) NULL DEFAULT 1 COMMENT '启用状态：0-禁用，1-启用';

alter table robot_app_ca add column remark varchar(255) DEFAULT NULL COMMENT '备注';
