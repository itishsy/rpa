-- 机器人应用
CREATE TABLE If Not Exists `robot_app` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `robot_code` varchar(50) DEFAULT NULL COMMENT '机器人编码',
  `app_code` varchar(50) DEFAULT NULL COMMENT '应用编码',
  `app_name` varchar(50) DEFAULT NULL COMMENT '应用名称',
  `app_args` varchar(2000) DEFAULT NULL COMMENT '应用参数值',
  `version` varchar(50) DEFAULT NULL COMMENT '版本号',
  `comment` varchar(1000) DEFAULT NULL COMMENT '版本说明',
  `status` int(11) DEFAULT NULL COMMENT '状态（1 已发布，0 未发布 , 2 有更新未发布）',
  `release_time` datetime DEFAULT NULL COMMENT '发布时间',
  `run_total_times` bigint(20) DEFAULT '0' COMMENT '运行总时长 毫秒',
  `run_status` tinyint(1) DEFAULT '0' COMMENT '运行状态     0 下架  1 上架 ',
  `online_status` tinyint(1) DEFAULT '0' COMMENT '上线阶段： 0 调研 1 配置 2 上线 3运维',
  `website_links` varchar(4096) DEFAULT NULL COMMENT '网站链接',
  `remark` varchar(255) DEFAULT NULL COMMENT '下架备注',
  `batch_run` int(11) DEFAULT NULL COMMENT '并行执行（0-不可以，1-可以）',
  `session_keep` int(11) DEFAULT NULL COMMENT 'session维持（0：否，1-是）',
  `create_id` int(11) DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` int(11) DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
);

-- 机器人参数定义
CREATE TABLE If Not Exists `robot_args_define` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `scope` varchar(50) DEFAULT NULL COMMENT '参数域',
  `args_code` varchar(50) NOT NULL COMMENT '参数编码',
  `field_key` varchar(50) NOT NULL COMMENT '字段Key',
  `field_name` varchar(50) DEFAULT NULL COMMENT '字段名称',
  `field_type` varchar(50) DEFAULT NULL COMMENT '字段类型',
  `dict_key` varchar(200) DEFAULT NULL COMMENT '字段备选项，登录方式代码dictKey:1005',
  `display_order` int(11) DEFAULT NULL COMMENT '显示顺序',
  `form_group_id` int(11) DEFAULT NULL COMMENT 'robot_app_form_group主键',
  `group_name` varchar(50) DEFAULT NULL COMMENT '显示分组',
  `required` int(11) DEFAULT NULL COMMENT '是否必填 0:必填；1:非必填',
  `comment` varchar(200) DEFAULT NULL COMMENT '说明',
  `quantity` int(11) DEFAULT NULL COMMENT '图片上传数量',
  `cond` varchar(500) DEFAULT NULL COMMENT '联动条件拼接',
  `drop_down_option` varchar(255) DEFAULT NULL COMMENT '信息配置下拉选项逗号拼接',
  `show_or_hidden` int(11) DEFAULT NULL COMMENT '显隐性 0--显示；1--隐藏',
  `is_delete` int(11) DEFAULT '0' COMMENT '是否删除（0--否；1--是）',
  `create_id` int(11) DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` int(11) DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
);

-- 机器人任务
CREATE TABLE If Not Exists  `robot_task` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `client_id` int(20) DEFAULT NULL COMMENT '客户ID',
  `machine_code` varchar(50) DEFAULT NULL comment '机器标识码',
  `app_code` varchar(50) DEFAULT NULL COMMENT '应用编码',
  `task_code` varchar(50) DEFAULT NULL COMMENT '任务编码',
  `task_name` varchar(50) DEFAULT NULL COMMENT '任务名称',
  `schedule` varchar(500) DEFAULT NULL COMMENT '执行计划',
  `status` int(11) DEFAULT NULL COMMENT '状态  0--暂停  1--启动',
  `run` int(11) DEFAULT '0' COMMENT '运行标识  0-未执行  1-执行中',
  `sync` int(11) DEFAULT '0' COMMENT '同步标识  0-未同步  1-已同步',
  `edit_name` varchar(255) DEFAULT NULL COMMENT '编辑人',
  `exec_cycle` varchar(50) DEFAULT NULL COMMENT '执行周期',
  `exec_area_time` varchar(500) DEFAULT NULL COMMENT '执行区时',
  `exec_style` int(10) DEFAULT '2' COMMENT '执行方式：（1--连续时间区间执行；2--固定时间执行；3--实时）',
  `task_type` int(10) DEFAULT NULL COMMENT '计划类型：（1--通用计划；2--自定义计划）',
  `exec_order` int(10) DEFAULT NULL COMMENT '执行顺序',
  `last_execution_time` datetime DEFAULT NULL COMMENT '最近执行时间',
  `sync_time` datetime DEFAULT NULL COMMENT '同步时间',
  `comment` varchar(1000) DEFAULT NULL COMMENT '原因备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `enable_time` datetime DEFAULT NULL COMMENT '启用时间',
  PRIMARY KEY (`id`)
);

-- 机器人任务参数
CREATE TABLE If Not Exists  `robot_task_args` (
    `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `task_code` varchar(50) DEFAULT NULL COMMENT '任务编码',
    `args_key` varchar(50) DEFAULT NULL COMMENT '参数Key',
    `args_value` text COMMENT '参数值',
    `args_type` varchar(50) DEFAULT NULL COMMENT '参数类型',
    `display_order` int(11) NOT NULL COMMENT '表单配置的顺序',
    `form_name` varchar(255) NOT NULL COMMENT '表单名',
    `group_name` varchar(255) NOT NULL COMMENT '分组名',
    `sync_time` datetime DEFAULT NULL COMMENT '同步时间',
    PRIMARY KEY (`id`)
);

-- 机器人流程
CREATE TABLE If Not Exists `robot_flow` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `app_code` varchar(50) DEFAULT NULL COMMENT '应用编码',
  `flow_code` varchar(50) DEFAULT NULL COMMENT '流程编码',
  `relation_flow_code` varchar(255) DEFAULT NULL COMMENT '关联的flow_code',
  `flow_name` varchar(50) DEFAULT NULL COMMENT '流程名称',
  `flow_type` int(11) DEFAULT NULL COMMENT '类型（1主流程，2子流程）',
  `exec_order` int(11) DEFAULT NULL COMMENT '执行顺序',
  `status` int(11) DEFAULT NULL COMMENT '状态',
  `comment` varchar(255) DEFAULT NULL COMMENT '说明',
  `add_type` varchar(255) DEFAULT NULL COMMENT '以哪种方式(add自定义新增  ,copyAdd复制新增, relateAdd关联新增 )',
  `open_type` int(11) DEFAULT NULL COMMENT '开放类型  0-开放  1-私有',
  `template_type` int(11) DEFAULT NULL COMMENT '是否模板(0：否，1：是)',
  `tag_code` varchar(255) DEFAULT NULL COMMENT '标签编码',
  `service_item` int(1) DEFAULT NULL COMMENT '服务项目，1：增员，2：减员，3：调基，5：补缴，6：缴费，7：名册',
  `declare_system` varchar(100) DEFAULT NULL COMMENT '申报系统 字典 10007,10008',
  `run_support` varchar(100) DEFAULT NULL COMMENT '运行载体 字典 10019',
  `create_id` int(11) DEFAULT NULL COMMENT '创建人Id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` int(11) DEFAULT NULL COMMENT '更新人Id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='机器人流程';

--应用执行计划表
CREATE TABLE If Not Exists  `robot_app_schedule` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `general_plan_id` int(10) DEFAULT NULL COMMENT '关联robot_general_plan表id',
  `flow_code` varchar(255) NOT NULL COMMENT '流程编码',
  `edit_id` int(11) DEFAULT NULL COMMENT '编辑人id',
  `exec_cycle` varchar(255) DEFAULT NULL COMMENT '执行周期',
  `exec_area_time` varchar(255) DEFAULT NULL COMMENT '执行区时',
  `exec_style` int(10) DEFAULT NULL COMMENT '执行方式：（1--连续时间区间执行；2--固定时间执行；3--实时）',
  `exec_order` int(11) DEFAULT NULL COMMENT '执行顺序',
  `task_type` int(10) DEFAULT NULL COMMENT '计划类型（1--通用计划；2--自定义计划）',
  `edit_time` datetime DEFAULT NULL COMMENT '编辑时间',
  PRIMARY KEY (`id`)
);

--流程计划通用表
CREATE TABLE If Not Exists  `robot_app_general_plan` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `app_code` varchar(255) NOT NULL COMMENT '应用编码',
  `exec_cycle` varchar(255) DEFAULT NULL COMMENT '执行周期',
  `exec_area_time` varchar(255) DEFAULT NULL COMMENT '执行区时',
  `exec_style` int(10) DEFAULT NULL COMMENT '执行方式：（1--连续时间区间执行；2--固定时间执行）',
  `edit_time` datetime DEFAULT NULL COMMENT '编辑时间',
  PRIMARY KEY (`id`)
);

-- 机器人流程步骤
CREATE TABLE If Not Exists `robot_flow_step` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `flow_code` varchar(50) NOT NULL COMMENT '流程编码',
  `group_code` varchar(255) DEFAULT NULL COMMENT '代码块编码',
  `step_code` varchar(50) NOT NULL COMMENT '步骤编码',
  `step_name` varchar(100) NOT NULL COMMENT '步骤名称',
  `action_code` varchar(100) NOT NULL COMMENT '指令编码',
  `target_args` varchar(2000) DEFAULT NULL COMMENT '目标参数值',
  `action_args` varchar(3000) DEFAULT NULL COMMENT '行为参数值',
  `number` int(11) NOT NULL COMMENT '执行序号',
  `level` int(11) DEFAULT NULL COMMENT '显示层级',
  `status` int(11) DEFAULT '1' COMMENT '状态：1启用，0禁用。',
  `failed_retry` int(11) DEFAULT NULL COMMENT '失败重试次数',
  `failed_strategy` int(11) DEFAULT '0' COMMENT '失败处理策略. 1.忽略，0.中止流程。2.跳转',
  `failed_skip_to` varchar(100) DEFAULT NULL COMMENT '失败跳转步骤',
  `skip_to` varchar(100) DEFAULT NULL COMMENT '跳转步骤',
  `false_skip_to` varchar(255) DEFAULT NULL COMMENT '为false跳转步(判断指令才有效)',
  `skip_condition` varchar(500) DEFAULT NULL COMMENT '跳过步骤条件',
  `wait_before` int(11) DEFAULT NULL COMMENT '执行前等待(s), 默认随机时间',
  `wait_after` int(11) DEFAULT NULL COMMENT '执行后等待(s), 默认不等待',
  `timeout` int(11) DEFAULT NULL COMMENT '执行超时时间',
  `type` int(11) DEFAULT NULL COMMENT '步骤类型：0-自定义,1-模板',
  `open_edit` int(11) DEFAULT NULL COMMENT '是否放开编辑（0：否，1：是）',
  `max_execute_num` int(11) DEFAULT NULL COMMENT '最大执行次数',
  `create_id` int(11) DEFAULT NULL COMMENT '创建人Id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` int(11) DEFAULT NULL COMMENT '更新人Id',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='机器人流程步骤';

-- 机器人执行记录
CREATE TABLE If Not Exists  robot_execution (
    `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `execution_code` varchar(50) DEFAULT NULL COMMENT '执行编码',
    `client_id` int(11) DEFAULT NULL COMMENT '客户id',
    `machine_code` varchar(50) DEFAULT NULL COMMENT '机器标识码',
    `task_code` varchar(50) DEFAULT NULL COMMENT '任务编码',
    `flow_code` varchar(50) DEFAULT NULL COMMENT '流程编码',
    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
    `status` int(11) DEFAULT NULL COMMENT '状态（1--成功；0--失败）',
    `error` varchar(2000) DEFAULT NULL COMMENT '异常信息',
    `sync_time` datetime DEFAULT NULL COMMENT '同步时间',
    PRIMARY KEY (`id`)
);


-- 机器人执行明细
CREATE TABLE If Not Exists  robot_execution_detail (
    `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `execution_code` varchar(50) DEFAULT NULL COMMENT '执行编码',
    `flow_code` varchar(50) DEFAULT NULL COMMENT '流程编码',
    `step_code` varchar(50) DEFAULT NULL COMMENT '步骤编码',
    `step_name` varchar(50) DEFAULT NULL COMMENT '步骤名称',
    `step_data` longtext COMMENT '步骤数据',
    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
    `status` int(11) DEFAULT NULL COMMENT '执行状态(0失败、1成功）',
    `error` varchar(2000) DEFAULT NULL COMMENT '执行信息',
    `error_stack` longtext COMMENT '错误信息堆栈',
    `sync_time` datetime DEFAULT NULL COMMENT '同步时间',
    PRIMARY KEY (`id`)
);

-- 机器人执行数据
CREATE TABLE If Not Exists  robot_execution_data (
       `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
       `client_id` int(11) DEFAULT NULL COMMENT '客户id',
       `machine_code` varchar(50) DEFAULT NULL COMMENT '机器标识码',
       `uuid` varchar(50) DEFAULT NULL COMMENT '唯一编码',
       `execution_code` varchar(50) DEFAULT NULL COMMENT '执行编码',
       `batch_code` varchar(50) DEFAULT NULL COMMENT '批次码',
       `addrName` varchar(50) DEFAULT NULL COMMENT '申报地',
       `businessType` int(11) DEFAULT NULL COMMENT '业务类型 1：社保，2：公积金',
       `declareType` int(11) DEFAULT NULL COMMENT '申报类型 1：增员，2：减员，3：调基，5：补缴',
       `dataType` int(11) DEFAULT NULL COMMENT '数据类型 1：基本，2：补充',
       `flagType` int(11) DEFAULT NULL COMMENT '标记类型 1：新流程，2：旧流程',
       `operationType` int(11) DEFAULT NULL COMMENT '申报数据操作类型 （参考表 policy_declare_operation_type_dict）',
       `nodeComment` varchar(500) DEFAULT NULL COMMENT '节点结果备注',
       `accountNumber` varchar(50) DEFAULT NULL COMMENT '申报账户',
       `name` varchar(50) DEFAULT NULL COMMENT '人员姓名',
       `idCard` varchar(50) DEFAULT NULL COMMENT '身份证号',
       `declareMonth` varchar(200) DEFAULT NULL COMMENT '申报年月，增员、减员、调基传申报月份，补缴传实际补缴的月份区间，用逗号隔开，例：2022-07,2022-09',
       `extend` varchar(200) DEFAULT NULL COMMENT '扩展字段',
       `tplTypeCode` varchar(50) DEFAULT NULL COMMENT '系统类型，10007001：全险系统，10007002：养老系统，10007003：医疗系统，10007004：单工伤，10008001：公积金系统',
       `declareStatus` int(11) DEFAULT NULL COMMENT '申报状态，1：待申报，2：申报中，4：申报成功，5：申报失败',
       `robotExecStatus` int(11) DEFAULT NULL COMMENT '机器人执行状态，0：未执行，1：已执行，2：执行成功',
       `failType` int(11) DEFAULT NULL COMMENT '失败类型：1-全部失败,2-部分失败',
       `failReason` varchar(3000) DEFAULT NULL COMMENT '申报失败原因；如果部分失败，则需要列出具体的失败险种和成功的险种，没有列出的险种不做回盘([{\"itemName\":\"养老\",\"status\":5,\"failReason\":\"失败原因\"}])',
       `sync` int(11) DEFAULT NULL COMMENT '同步状态（0--未同步；1--已同步）',
       `syncFailReason` varchar(500) DEFAULT NULL COMMENT '同步失败原因',
       `syncTime` datetime DEFAULT NULL COMMENT '同步时间',
       `createTime` datetime DEFAULT NULL COMMENT '创建时间',
       PRIMARY KEY (`id`)
);

CREATE TABLE  If not EXISTS `robot_app_config_condition`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `logical_operator` varchar(20) DEFAULT NULL COMMENT '逻辑运算符',
  `condition_left` varchar(50) DEFAULT NULL COMMENT '条件左',
  `condition_right` varchar(50) DEFAULT NULL COMMENT '条件右',
  `relation` varchar(2) DEFAULT NULL COMMENT '关系',
  `args_define_id` int(11) DEFAULT NULL COMMENT '分组明细id(robot_args_define主键)',
  `relate_condition_args_id` int(11) DEFAULT NULL COMMENT '关联条件的args_define表的id',
  PRIMARY KEY (`id`)
);

CREATE TABLE If Not Exists  `robot_app_config_form`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `robot_type` varchar(100) DEFAULT NULL COMMENT '机器人类型(robot.robot_name)',
  `form_code` varchar(50) NOT NULL COMMENT '表单id',
  `form_name` varchar(50) DEFAULT NULL COMMENT '表单名',
  PRIMARY KEY (`id`)
);

CREATE TABLE If Not Exists  `robot_app_config_group`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `group_name` varchar(50) DEFAULT NULL COMMENT '分组名称',
  `status` char(1) DEFAULT NULL COMMENT '状态 0:启用 1:禁用',
  `robot_type` varchar(100) DEFAULT NULL COMMENT '机器人应用类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE If Not Exists  `robot_app_form_group`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `app_code` varchar(255) NOT NULL COMMENT '应用编码',
  `form_id` int(11) NULL DEFAULT NULL COMMENT 'config_form id',
  `group_id` int(11) NULL DEFAULT NULL COMMENT 'config_group id',
  `is_delete` int(11) NULL DEFAULT 0 COMMENT '是否被逻辑删除（0--否；1--是）',
  PRIMARY KEY (`id`)
);

CREATE TABLE If Not Exists `robot_client_usb` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `machine_code` varchar(50) DEFAULT NULL COMMENT '机器标识码',
  `sort` varchar(50) DEFAULT NULL COMMENT '序号',
  `orginal` varchar(500) DEFAULT NULL COMMENT '编码',
  `sync` int(11) DEFAULT '0' COMMENT '同步标识  0-未同步  1-已同步',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `sync_time` datetime DEFAULT NULL COMMENT '同步时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='机器人usb';


CREATE TABLE If Not Exists `robot_execution_file` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `execution_code` varchar(50) DEFAULT NULL COMMENT '执行编码',
  `flow_code` varchar(50) DEFAULT NULL COMMENT '流程编码',
  `task_code` varchar(50) DEFAULT NULL COMMENT '任务编码',
  `file_name` varchar(100) DEFAULT NULL COMMENT '文件名',
  `file_path` varchar(200) DEFAULT NULL COMMENT '文件路径',
  `status` int(11) DEFAULT NULL COMMENT '是否上传(0-未上传、1-上传成功，2-上传失败）',
  `error` varchar(2000) DEFAULT NULL COMMENT '错误信息',
  `upload_time` datetime DEFAULT NULL COMMENT '上传时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客户机器人执行文件';


CREATE TABLE If Not Exists `robot_app_env` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `app_code` varchar(50) DEFAULT NULL COMMENT '应用编码',
  `app_name` varchar(255) DEFAULT NULL COMMENT '应用名称',
  `flow_code` varchar(50) DEFAULT NULL COMMENT '流程编码',
  `download_url` varchar(2000) DEFAULT NULL COMMENT '下载链接',
  `path` varchar(200) DEFAULT NULL COMMENT '证书安装路径',
  `type` int(11) DEFAULT NULL COMMENT '类型（0-需启动(证书)，1-无需启动（浏览器插件））',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客户机器人流程环境';

CREATE TABLE If Not Exists `robot_flow_step_args` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `flow_code` varchar(50) NOT NULL COMMENT '流程编码',
  `group_code` varchar(255) DEFAULT NULL COMMENT '代码块编码',
  `step_code` varchar(50) NOT NULL COMMENT '步骤编码',
  `target_args` varchar(2000) DEFAULT NULL COMMENT '目标参数值',
  `action_args` varchar(3000) DEFAULT NULL COMMENT '行为参数值',
  `create_id` int(11) DEFAULT NULL COMMENT '创建人Id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` int(11) DEFAULT NULL COMMENT '更新人Id',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='机器人流程步骤参数';

CREATE TABLE If Not Exists `robot_flow_step_flow` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `flow_code` varchar(50) NOT NULL COMMENT '模板-流程编码',
  `step_code` varchar(50) NOT NULL COMMENT '步骤编码',
  `child_flow_code` varchar(50) NOT NULL COMMENT '子流程-流程编码',
  `create_id` int(11) DEFAULT NULL COMMENT '创建人Id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` int(11) DEFAULT NULL COMMENT '更新人Id',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='机器人流程步骤流程';

CREATE TABLE If Not Exists `robot_flow_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `template_flow_code` varchar(50) DEFAULT NULL COMMENT '模板-流程编码',
  `flow_code` varchar(50) DEFAULT NULL COMMENT '地区流程-流程编码',
  `create_id` int(11) DEFAULT NULL COMMENT '创建人Id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` int(11) DEFAULT NULL COMMENT '更新人Id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='机器人流程模板关系';

CREATE TABLE If Not Exists `robot_flow_template_step` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `template_flow_code` varchar(50) DEFAULT NULL COMMENT '模板-流程编码',
  `template_step_code` varchar(255) DEFAULT NULL COMMENT '模板-步骤编码',
  `main_flow_code` varchar(50) DEFAULT NULL COMMENT '主流程-流程编码',
  `flow_code` varchar(50) DEFAULT NULL COMMENT '地区流程-流程编码',
  `create_id` int(11) DEFAULT NULL COMMENT '创建人Id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` int(11) DEFAULT NULL COMMENT '更新人Id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='机器人流程模板步骤关系';

--任务执行计划表
CREATE TABLE If Not Exists `robot_task_schedule` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `general_plan_id` int(10) DEFAULT NULL COMMENT '关联robot_general_plan表id',
  `task_code` varchar(50) DEFAULT NULL COMMENT '任务编码',
  `task_type` int(10) DEFAULT NULL COMMENT '计划类型（1--通用计划；2--自定义计划）',
  `flow_code` varchar(255) NOT NULL COMMENT '流程编码',
  `exec_cycle` varchar(255) DEFAULT NULL COMMENT '执行周期',
  `exec_area_time` varchar(255) DEFAULT NULL COMMENT '执行区时',
  `exec_style` int(10) DEFAULT NULL COMMENT '执行方式：（1--连续时间区间执行；2--固定时间执行；3--实时）',
  `exec_order` int(11) DEFAULT NULL COMMENT '执行顺序',
  `status` int(1) DEFAULT '1' COMMENT '状态，1：启用，0：停用',
  `comment` varchar(1000) DEFAULT NULL COMMENT '原因备注',
  `edit_id` int(11) DEFAULT NULL COMMENT '编辑人id',
  `edit_time` datetime DEFAULT NULL COMMENT '编辑时间',
  PRIMARY KEY (`id`)
);

CREATE TABLE If Not Exists `robot_execution_monitor` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `execution_code` varchar(50) DEFAULT NULL COMMENT '执行编码',
  `client_id` int(11) DEFAULT NULL COMMENT '客户id',
  `machine_code` varchar(50) DEFAULT NULL COMMENT '机器标识码',
  `task_code` varchar(50) DEFAULT NULL COMMENT '任务编码',
  `declare_num` int(50) DEFAULT NULL COMMENT '申报人数',
  `success_num` int(11) DEFAULT NULL COMMENT '成功人数',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` int(11) DEFAULT NULL COMMENT '状态（1--成功；0--失败）',
  `sync_time` datetime DEFAULT NULL COMMENT '同步时间',
  PRIMARY KEY (`id`)
);

CREATE TABLE If Not Exists `robot_emp_account` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `businessType` int(11) DEFAULT NULL COMMENT '业务类型',
  `addrId` int(11) DEFAULT NULL COMMENT 'addrId',
  `accountList` json  DEFAULT NULL COMMENT '个人编号身份证json',
  `sync` int(11) DEFAULT 0 COMMENT '状态（1--已同步；0--未同步）',
  `sync_time` datetime DEFAULT NULL COMMENT '同步时间',
  PRIMARY KEY (`id`)
);

CREATE TABLE If Not Exists `robot_task_session_keep` (
    `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `client_id` int(20) DEFAULT NULL COMMENT '客户ID',
    `machine_code` varchar(50) DEFAULT NULL COMMENT '机器标识码',
    `app_code` varchar(50) DEFAULT NULL COMMENT '应用编码',
    `task_code` varchar(50) DEFAULT NULL COMMENT '任务编码',
    `declare_system` varchar(100) DEFAULT NULL COMMENT '申报系统 字典 10007,10008',
    `comment` varchar(100) DEFAULT NULL COMMENT '原因备注',
    `port` int(11) DEFAULT NULL COMMENT '远程端口号',
    `share_port` int(11) DEFAULT NULL COMMENT '共享端口号',
    `status` int(11) DEFAULT NULL COMMENT '维持状态：1-浏览器关闭 2-会话丢失 3-会话维持',
    `url` varchar(255) DEFAULT NULL COMMENT '维持访问链接',
    `xpath` varchar(255) DEFAULT NULL COMMENT '校验是否当前单位会话维持',
    `lose_xpath` varchar(255) DEFAULT NULL COMMENT '会话丢失xpath',
    `retry_num` int(11) DEFAULT NULL COMMENT '刷新重试次数',
    `match_express` varchar(255) DEFAULT NULL COMMENT '匹配表达式',
    `login_status` int(11) DEFAULT '0' COMMENT '登录状态  0-未登录  1-已登录',
    `file_id` int(11) DEFAULT NULL COMMENT '会话失效截图文件Id',
    `sync` int(11) DEFAULT '0' COMMENT '同步标识  0-未同步  1-已同步',
    `sync_machine_code` text COMMENT '同步机器标识码，逗号隔开',
    `max_keep_time` int(11) DEFAULT NULL COMMENT '最长维持时间(单位小时)',
    `sync_time` datetime DEFAULT NULL COMMENT '同步时间',
    `start_keep_time` datetime DEFAULT NULL COMMENT '会话维持开始时间',
    `end_keep_time` datetime DEFAULT NULL COMMENT '会话失效时间',
    `disabled` int(11) DEFAULT '1' COMMENT '启用状态：0-禁用，1-启用',
    `create_id` int(11) DEFAULT NULL COMMENT '创建者Id',
    `create_name` varchar(255) DEFAULT NULL COMMENT '创建者名称',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
);

CREATE TABLE If Not Exists `robot_policy_notice_msg` (
   `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
   `addr_name` varchar(50) DEFAULT NULL COMMENT '地区名',
   `tpl_type_code` varchar(50) DEFAULT NULL COMMENT '险种类型',
   `business_type` int(20) DEFAULT NULL COMMENT '业务类型',
   `push_date` varchar(50) DEFAULT NULL COMMENT '推送日期',
   `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`)
);

CREATE TABLE If Not Exists `robot_client_priority` (
   `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
   `client_id` int(11) DEFAULT NULL COMMENT '客户ID',
   `app_code` varchar(50) DEFAULT NULL COMMENT '应用编码',
   `app_name` varchar(50) DEFAULT NULL COMMENT '应用名称',
   `sync_machine_code` text COMMENT '同步机器标识码，逗号隔开',
   `sync_time` datetime DEFAULT NULL COMMENT '同步时间',
   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE If Not Exists `robot_task_queue` (
    `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `client_id` int(20) DEFAULT NULL COMMENT '客户ID',
    `execution_code` varchar(255) DEFAULT NULL COMMENT '执行编码',
    `machine_code` varchar(50) DEFAULT NULL COMMENT '执行盒子编码',
    `fix_machine_code` varchar(2000) DEFAULT NULL COMMENT '分配盒子编码',
    `app_code` varchar(255) DEFAULT NULL COMMENT '应用编码',
    `task_code` varchar(50) DEFAULT NULL COMMENT '任务编码',
    `declare_system` varchar(255) DEFAULT NULL COMMENT '申报系统 字典 10007,10008',
    `declare_account` varchar(255) DEFAULT NULL COMMENT '申报账户',
    `company_name` varchar(255) DEFAULT NULL COMMENT '申报单位',
    `business_type` int(11) DEFAULT NULL COMMENT '业务类型 1：社保，2：公积金',
    `queue_item` int(11) DEFAULT NULL COMMENT '执行事项，1：增减员（增员、减员、调基、补缴），6：缴费，7：在册名单，8：费用明细，9：政策通知 10：基数申报 11：登录 12：查审核结果',
    `rule_id` int(11) DEFAULT NULL COMMENT '指定盒子规则Id(表：robot_client_rule的id)',
    `sort` int(11) DEFAULT '999' COMMENT '执行优先级',
    `sort_rule` int(11) DEFAULT NULL COMMENT '排序规则：1-H5认证 2-配置优先城市 3-ukey认证 4-账号密码认证',
    `phone_number` varchar(50) DEFAULT NULL COMMENT '通知手机号码',
    `login_status` int(11) DEFAULT '-1' COMMENT '登录状态 -1-待登录 0-开始登录 1-登录成功 2-登录失败',
    `login_type` varchar(255) DEFAULT NULL COMMENT '登录方式',
    `login_time` datetime DEFAULT NULL COMMENT '登录时间',
    `status` int(11) DEFAULT '0' COMMENT '运行标识  1-执行中  2-待执行 3-执行中断 4-执行成功',
    `type` int(11) DEFAULT NULL COMMENT '类型：1-固定盒子，2-可调度盒子',
    `source` int(11) DEFAULT NULL COMMENT '任务来源：1-自动任务 2-手动任务',
    `sync` int(11) DEFAULT NULL COMMENT '同步标识  0-未同步  1-已同步',
    `comment` text COMMENT '原因备注',
    `pre_time` bigint(11) DEFAULT NULL COMMENT '预计时长(单位：秒)',
    `pre_start_time` datetime DEFAULT NULL COMMENT '预计开始时间',
    `pre_end_time` datetime DEFAULT NULL COMMENT '预计结束时间',
    `pra_time` bigint(11) DEFAULT NULL COMMENT '实际时长(单位：秒)',
    `pra_start_time` datetime DEFAULT NULL COMMENT '实际开始时间',
    `pra_end_time` datetime DEFAULT NULL COMMENT '实际结束时间',
    `create_id` int(11) DEFAULT NULL COMMENT '创建人名称',
    `create_name` varchar(255) DEFAULT NULL COMMENT '创建人id',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
);

CREATE TABLE If Not Exists `robot_app_ca` (
    `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `app_code` varchar(50) DEFAULT NULL COMMENT '应用编码',
    `declare_system` varchar(100) DEFAULT NULL COMMENT '申报系统 字典 10007,10008',
    `file_name` varchar(100) DEFAULT NULL COMMENT '执行文件名称',
    `declare_account` text COMMENT '申报账户，逗号隔开',
    `sync_machine_code` text COMMENT '同步机器标识码，逗号隔开',
    `sync_time` datetime DEFAULT NULL COMMENT '同步时间',
    `disabled` int(11) DEFAULT '1' COMMENT '启用状态：0-禁用，1-启用',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `create_id` int(11) DEFAULT NULL COMMENT '创建者Id',
    `create_name` varchar(255) DEFAULT NULL COMMENT '创建者名称',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
);

CREATE TABLE If Not Exists `robot_client` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `client_id` int(11) DEFAULT NULL COMMENT '客户Id',
    `machine_code` varchar(50) DEFAULT NULL COMMENT '机器标识码',
    `machine_name` varchar(100) DEFAULT NULL COMMENT '设备名称',
    `machine_factory` varchar(200) DEFAULT NULL COMMENT '设备厂商',
    `machine_amount` decimal(11,2) DEFAULT NULL COMMENT '设备费用',
    `token` varchar(500) DEFAULT NULL COMMENT '机器登录凭证',
    `ip` varchar(50) DEFAULT NULL COMMENT '内网IP地址',
    `port` int(11) DEFAULT NULL COMMENT '端口号',
    `work_path` varchar(200) DEFAULT NULL COMMENT '工作目录',
    `status` int(11) DEFAULT NULL COMMENT '0 New 初始化状态、用户未登录;1 Runnable 机器人已启动，准备就绪 ;2 Running 机器人正在执行任务中;3 Closed 关闭;4 RobotError 机器人程序内部异常;5 ClientError 客户端内部异常;6 Upgrading 机器人正在升级;7 UpgradeFailed 机器人升级失败;8 Offline 离线状态，超过10分钟没有心跳',
    `cmd` varchar(50) DEFAULT NULL COMMENT 'rk',
    `msg` int(11) DEFAULT '1' COMMENT '提醒开关',
    `user_name` varchar(50) DEFAULT NULL COMMENT '维护负责人',
    `type` int(11) DEFAULT NULL COMMENT '设备类型：1-数据机器，2-费用机器',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `login_time` datetime DEFAULT NULL COMMENT '登录时间',
    `heartbeat_time` datetime DEFAULT NULL COMMENT '心跳时间',
    `last_maintenance_date` datetime DEFAULT NULL COMMENT '最新维护日期',
    `max_keep_num` int(11) DEFAULT NULL COMMENT '维持的最大数量',
    `sync` int(11) DEFAULT '0' COMMENT '同步标识  0-未同步  1-已同步',
    `count_time` bigint(20) DEFAULT '0' COMMENT '盒子总运行时间',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
);

CREATE TABLE If Not Exists `robot_config` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `key` varchar(50) DEFAULT NULL COMMENT '配置项',
    `context` varchar(5000) DEFAULT NULL COMMENT '配置内容',
    `disabled` int(11) DEFAULT '1' COMMENT '启用状态：0-禁用，1-启用',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `sync_machine_code` text COMMENT '同步机器标识码，逗号隔开',
    `sync_time` datetime DEFAULT NULL COMMENT '同步时间',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
);