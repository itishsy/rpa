<template>
  <div class="spl-container">
    <breadcrumb :data="pathData">
      <template slot="breadcrumb-btn">
        <!-- <el-button type="primary" slot="breadcrumb-btn" size="mini" @click="addProcess()">添加流程</el-button> -->
      </template>
    </breadcrumb>
    <div class="search-row display-flex">
      <div class="flex1">
        <el-row>
          <el-col :span="5" class="display-flex">
            <span class="label-text">流程类型：</span>
            <el-checkbox-group v-model="searchForms.type" size="medium" style="display: flex;align-items: center;">
              <el-checkbox v-for="item in searchOptions.types" :key="item.id" :label="item.id">
                {{ item.name }}
              </el-checkbox>
            </el-checkbox-group>
          </el-col>
          <el-col :span="12" class="display-flex">
            <span class="label-text">服务项目：</span>
            <div class="flex1 pt-5">
              <el-checkbox-group v-model="searchForms.service" size="medium" style="display: flex;align-items: center;flex-wrap: wrap;">
                <el-checkbox v-for="item in searchOptions.service" :key="item.id" :label="item.id">
                  {{ item.name }}
                </el-checkbox>
              </el-checkbox-group>
            </div>
          </el-col>
          <el-col :span="6" class="display-flex">
            <span class="label-text">申报系统：</span>
            <el-select v-model="searchForms.declareSystemList" multiple collapse-tags placeholder="请选择"
                       class="declareSystemList flex1">
              <el-option v-for="item in declareSystemSelects" :key="item.declareSystem" :label="item.declareSystemName"
                         :value="item.declareSystem">
              </el-option>
            </el-select>
          </el-col>
        </el-row>
        <el-row class="mt-10">
          <el-col :span="5">
            <div class="label-text">配置：</div>
            <div class="label-sel">{{ statistics.info1 }}</div>
          </el-col>
          <el-col :span="4">
            <div class="label-text">调试通过：</div>
            <div class="label-sel">{{ statistics.info2 }}</div>
          </el-col>
          <el-col :span="4">
            <div class="label-text">等待数据：</div>
            <div class="label-sel">{{ statistics.info3 }}</div>
          </el-col>
          <el-col :span="4">
            <div class="label-text">验证有效：</div>
            <div class="label-sel">{{ statistics.info4 }}</div>
          </el-col>
          <el-col :span="4">
            <div class="label-text">修复问题：</div>
            <div class="label-sel"><span class="text-red">{{ statistics.info5 }}</span></div>
          </el-col>
        </el-row>
      </div>
      <div class="ml-20">
        <el-button type="primary" class="s-btn" @click="getProcessInfo()">查询</el-button>
        <el-button type="primary" class="s-btn" @click="addProcess()"
                   :disabled="buttonGroupList.indexOf('addFlow') == -1">添加流程</el-button>
      </div>
    </div>
    <div style="padding: 0 20px;">
      <el-form ref="tableForm" :model="table" label-width="200px" :rules="rules">
        <el-table :data="table.tableData" border header-cell-class-name="table-header" style="width: 100%"
          :max-height="tableHeight" ref="table">
          <el-table-column fixed="left" label="执行顺序" width="80" align="center" prop="execOrder">
            <template scope="scope"><span>{{ scope.row.execOrder }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="serviceItemName" label="服务项目" width="100"></el-table-column>
          <el-table-column prop="flowType" label="流程类型" width="100">
            <template scope="scope">
              <span>{{ getFlowType(scope.row.flowType) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="declareSystemName" label="申报系统" width="150"></el-table-column>
          <el-table-column prop="runSupportName" label="运行载体" width="150"></el-table-column>
          <el-table-column prop="flowName" label="流程名称" width="250"></el-table-column>
          <el-table-column prop="statusName" label="流程状态" width="100">
            <template scope="scope">
              <span style="cursor:pointer" @click="showFlowTable(scope.row)"
                :class="scope.row.statusName == '修复问题' ? 'text-red' : 'text-blue'"> {{ scope.row.statusName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="comment" label="备注" width="180" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="tagName" label="标签" width="180"></el-table-column>
          <el-table-column prop="stepNum" label="步骤数" width="100" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="updateTime" label="更新时间" min-width="180">
            <template scope="scope">
              <span>{{ formatDateTime(scope.row.updateTime, 'y-M-d-h-m-s', '-') }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="control" label="操作" width="480" fixed="right">
            <template scope="scope">
              <div style="display: flex">

                <el-button size="medium" @click="goUrl(scope.row, scope.$index)"
                  :disabled="buttonGroupList.indexOf('setStep') == -1">设置步骤</el-button>
                <el-button size="medium" @click="goUrl2(scope.row, scope.$index)"
                  :disabled="buttonGroupList.indexOf('testFlow') == -1">测试流程</el-button>
                <el-dropdown class="ml-10" split-button size="medium" @click="handleClick1($event, scope)"
                  @command="handleCommand1($event, scope)">
                  调整状态
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="config" :disabled="buttonGroupList.indexOf('flowStatusConfig') == -1">
                      配置</el-dropdown-item>
                    <el-dropdown-item command="test"
                      :disabled="buttonGroupList.indexOf('flowStatusPassed') == -1">调试通过</el-dropdown-item>
                    <el-dropdown-item command="wait"
                      :disabled="buttonGroupList.indexOf('flowStatusWaitData') == -1">等待数据</el-dropdown-item>
                    <el-dropdown-item command="verify"
                      :disabled="buttonGroupList.indexOf('flowStatusCheckEffective') == -1">验证有效</el-dropdown-item>
                    <el-dropdown-item command="fix"
                      :disabled="buttonGroupList.indexOf('flowStatusRepairQuestion') == -1">修复问题</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
                <el-dropdown class="ml-10" split-button size="medium" @click="handleClick2($event, scope, scope.$index)"
                  @command="handleCommand2($event, scope)">
                  <el-popover placement="top" :ref="`popover-${scope.$index}`" trigger="manual">
                    <el-row type="flex">
                      <el-input v-model="newIndex" placeholder="输入新序号" type="number"></el-input>
                      <el-button size="mini" plain class="ml-10"
                        @click.stop="cancelPopover(scope.row, scope.$index)">取消</el-button>
                      <el-button type="primary" size="mini"
                        @click.stop="confirmPopover(scope.row, scope.$index)">确定</el-button>
                    </el-row>
                    <span slot="reference">排序</span>
                  </el-popover>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="edit"> 编辑</el-dropdown-item>
                    <el-dropdown-item command="delete"
                      :disabled="buttonGroupList.indexOf('deleteFlow') == -1">删除</el-dropdown-item>
                    <el-dropdown-item command="graph">流程图</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
                <!-- <div class="control-btn" style="color: #6b6b6b" @click="moveDown(scope.row, scope.$index)"
                  v-if="scope.$index != table.tableData.length - 1">
                  下移
                </div>
                <div class="control-btn" style="color: #6b6b6b" @click="moveUp(scope.row, scope.$index)"
                  v-if="scope.$index != 0">
                  上移
                </div>
                <div class="control-btn" style="color: #3e82ff" @click="edit(scope.row, scope.$index)">
                  编辑
                </div>
                <div class="control-btn" style="color: #3e82ff" @click="goUrl(scope.row, scope.$index)">
                  步骤设置
                </div>
                <div class="control-btn" style="color: #3e82ff" @click="goFlowUrl(scope.row, scope.$index)">
                  流程设置
                </div>
                <div class="control-btn" style="color: red" @click="remove(scope.row, scope.$index)">
                  删除
                </div> -->
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
    </div>
    <!-- 添加流程 -->
    <el-dialog :title="tableEdit == 'edit' ? '编辑流程' : '添加流程'" :visible.sync="dialogVisible" width="600px"
      :close-on-click-modal="false" :before-close="cancel" class="cust-dialog">
      <el-form :model="formData" :rules="rules" ref="addProcess" label-width="100px">
        <div class="adjust-content-box">
          <el-form-item label="添加方式：" prop="bussinssType" style="width:80%;">
            <el-radio-group v-model="formData.bussinssType" @change="changeBussinssType" :disabled="disabledBussinssType">
              <el-radio label="add">自定义</el-radio>
              <el-radio label="copyAdd">复制现有的</el-radio>
              <el-radio label="relateAdd">关联已有</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="申报系统：" prop="declareSystem" style="width:80%;">
            <el-select v-model="formData.declareSystem" placeholder="请选择" clearable filterable :style="{ width: '100%' }">
              <el-option v-for="item in dictList.key10008" :key="item.id" :label="item.dictName"
                :value="item.dictCode"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="运行载体：" prop="runSupport" style="width:80%;">
            <el-select v-model="formData.runSupport" placeholder="请选择" clearable filterable :style="{ width: '100%' }">
              <el-option v-for="item in dictList.key10019" :key="item.id" :label="item.dictName"
                :value="item.dictCode"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="流程类型：" prop="flowType" style="width:80%;"
            v-if="formData.bussinssType == 'add' || tableEdit == 'edit'">
            <el-radio-group v-model="formData.flowType" @change="changeFlowType">
              <el-radio label="1">主流程</el-radio>
              <el-radio label="2">子流程</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="processTypeName" prop="processTypeName" style="width:80%;"
            v-if="formData.bussinssType != 'add' && tableEdit != 'edit'">
            <el-select v-model="formData.processObj" placeholder="请选择" style="width:100%;" value-key="id"
              @change="changeProcess" filterable>
              <el-option :label="item.appNameAndFlowName" :value="item" v-for="(item, index) in processOption"
                :key="item.id"></el-option>
            </el-select>
            <span style="display: block;font-size: 12px;color: #999;line-height: 20px;">{{ processTips }}</span>
          </el-form-item>
          <el-form-item label="流程名称：" prop="processName" style="width:80%;">
            <el-input v-model="formData.processName" placeholder="请输入内容" maxlength="100" v-filter-special-char></el-input>
          </el-form-item>
          <el-form-item label="服务项目：" prop="serviceItem" style="width:80%;">
            <el-select v-model="formData.serviceItem" ref="serviceItem"
              :disabled="formData.flowType == 2 && formData.bussinssType == 'add'" placeholder="请选择" clearable filterable
              :style="{ width: '100%' }" @change="e => formData.tagCode = ''">
              <el-option v-for="item in serviceItemArr" :key="item.dictCode" :label="item.dictName"
                :value="item.dictCode">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="模板引用：" prop="temlateQuote" style="width:80%;" v-if="formData.bussinssType!='relateAdd'">
            <el-select v-model="formData.templateQuote" ref="templateQuote" placeholder="请选择" filterable
              :style="{ width: '100%' }">
              <el-option v-for="item in templateList" :key="item.flowCode" :label="item.flowName" :value="item.flowCode">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="标签：" prop="tagCode" style="width:80%;" :rules="formData.serviceItem == 6 ? [{ required: true, message: '请选择标签', trigger: 'change' }
          ] : []">
            <el-select v-model="formData.tagCode" ref="tagCode" placeholder="请选择" clearable filterable
              :style="{ width: '100%' }">
              <el-option v-for="item in tagList" v-if="businessType == item.comment || !item.comment" :key="item.dictCode"
                :label="item.dictName" :value="item.dictCode"
                :disabled="formData.serviceItem == 6 && item.dictCode == 10018009">
              </el-option>
            </el-select>
          </el-form-item>
        </div>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancel" size="small" class="w-80">取 消</el-button>
        <el-button type="primary" @click="confirm" size="small" class="w-80" style="margin-left: 20px;">确定</el-button>
      </span>
    </el-dialog>

    <!-- 调整状态 -->
    <el-dialog title="调整状态" :visible.sync="dialogVisible4" width="600px" :before-close="cancelChangeStatus"
      :close-on-click-modal="false" class="cust-dialog">
      <div id="upload-dialog">
        <div class="adjust-content-box">
          <el-form :model="formData" :rules="rules" ref="statusForm" label-width="100px" class="demo-ruleForm">
            <el-form-item prop="flowName" label="流程名称：">
              <span>{{ formData.flowName }}</span>
            </el-form-item>
            <el-form-item prop="flowName" label="当前状态：">
              <span>{{ formData.statusName }}</span>
              <span>（{{ stageArray[formData.status] }}阶段）</span>
            </el-form-item>

            <el-form-item prop="flowName" label="调整为：">
              <span v-if="changeStatus == 0">配置</span>
              <span v-if="changeStatus == 1">调试通过</span>
              <span v-if="changeStatus == 2">等待数据</span>
              <span v-if="changeStatus == 3">验证有效</span>
              <span v-if="changeStatus == 4">修复问题</span>
              <span>（{{ stageArray[changeStatus] }}阶段）</span>
            </el-form-item>
            <el-form-item :prop="changeStatus == 4 ? 'reason' : ''" label="原因备注：">
              <el-input v-model.trim="formData.reason" placeholder="比如：网站升级，重新调研" style="width:400px"></el-input>
            </el-form-item>
          </el-form>
          <span v-if="changeStatus == 4" class="text-red"
            style="width:100%;text-align:center;">请评估问题重大到需要下架应用，请及时下架应用。</span>
          <div class="text-right mt-30">
            <el-button class="s-btn w-80" @click="cancelChangeStatus">取 消</el-button>
            <el-button class="s-btn w-80" style="margin-left: 20px;" type="primary" @click="confirmChangeStatus">确定</el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 状态历史 -->
    <el-dialog title="状态历史" :visible.sync="flowTableVisiable" width="1100px" :before-close="cancelFlowTable"
      :close-on-click-modal="false">
      <el-table :data="tableData" border header-cell-class-name="table-header" style="width: 100%">
        <el-table-column prop="actionName" label="操作" width="150">
          <template scope="scope">
            <span :class="scope.row.flowStatus == 4 ? 'text-red' : ''">{{ scope.row.actionName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="flowStatusName" label="流程状态变为" width="150">
          <template scope="scope">
            <span :class="scope.row.flowStatus == 4 ? 'text-red' : ''">{{ scope.row.flowStatusName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="原因备注" width="250" :show-overflow-tooltip="true">
          <template scope="scope">
            <span :class="scope.row.flowStatus == 4 ? 'text-red' : ''">{{ scope.row.remark }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="appStatusName" label="应用状态变为" width="150">
          <template scope="scope">
            <span :class="scope.row.flowStatus == 4 ? 'text-red' : ''">{{ scope.row.appStatusName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createName" label="操作人" width="150">
          <template scope="scope">
            <span :class="scope.row.flowStatus == 4 ? 'text-red' : ''">{{ scope.row.createName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间">
          <template scope="scope">
            <span :class="scope.row.flowStatus == 4 ? 'text-red' : ''">{{ formatDateTime(scope.row.createTime,
              'y-M-d-h-m-s',
              '-') }}</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="bottom-group">
        <el-button type="primary" size="medium" @click="cancelFlowTable">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  data () {
    return {
      stageArray: ['配置', '配置', '测试', '测试', '运维'], // 0:配置 1:调试通过 2:等待数据 3:验证有效 4:修复问题
      tableData: [], // 状态历史table
      flowTableVisiable: false, // 流程状态table
      newIndex: '', // 用于排序的新序号 每次确定/取消后 需要清空
      popoverVisable: false, // 排序框显示
      changeStatus: 0,
      dialogVisible4: false,
      pathData: [],
      searchForms: {
        type: [1],
        service: [],
        declareSystemList: []
      },
      searchOptions: {
        types: [
          {
            name: '主',
            id: 1
          },
          {
            name: '子',
            id: 2
          }
        ],
        service: [
          {
            name: '增员',
            id: 1
          },
          {
            name: '减员',
            id: 2
          },
          {
            name: '调基',
            id: 3
          },
          {
            name: '补缴',
            id: 5
          },
          {
            name: '缴费',
            id: 6
          },
          {
            name: '在册名单',
            id: 7
          },
          {
            name: '费用明细',
            id: 8
          },
          {
            name: '政策通知',
            id: 9
          },
          {
            name: '基数申报',
            id: 10
          }
        ]
      },
      table: {
        tableData: []
      },
      rules: {
        processName: [
          { required: true, message: '请输入流程名称', trigger: 'change' },
          { required: true, trigger: 'change', validator: this.checkProcessName }
        ],
        bussinssType: [
          { required: true, message: '请选择添加方式', trigger: 'change' }
        ],
        processTypeName: [
          { required: true, trigger: 'change', validator: this.checkProcessTypeName }
        ],
        flowType: [
          { required: true, message: '请选择流程类型', trigger: 'change' }
        ],
        declareSystem: [
          { required: true, message: '请选择申报系统', trigger: 'change' }
        ],
        runSupport: [
          { required: true, message: '请选择运行载体', trigger: 'change' }
        ],
        uploadFileName: [
          { required: true, message: '请先上传文件', trigger: 'change' }
        ],
        tplType: [
          { required: true, message: '请选择模板类型', trigger: 'change' }
        ],
        newVersionText: [
          { required: true, message: '请输入本次发布更新的内容', trigger: 'change' },
          { min: 0, max: 500, message: '更新内容不能大于500字', trigger: 'change' }
        ],
        addrNameTemp: [
          { required: true, message: '请选择城市', trigger: 'change' }
        ],
        businessType: [
          { required: true, message: '请选择业务类型', trigger: 'change' }
        ],
        rule: [
          { required: true, message: '请选择版本规则', trigger: 'change' }
        ],
        robot: [
          { required: true, message: '请选择类型', trigger: 'change' }
        ],
        appName: [
          { required: true, message: '请输入应用名称', trigger: 'change' }
        ],
        offlineStatus: [
          { required: true, message: '请选择下架后状态', trigger: 'change' }
        ],
        changeStatus: [
          { required: true, message: '请选择调整状态', trigger: 'change' }
        ],
        reason: [
          { required: true, message: '请输入原因备注', trigger: 'change' }
        ],
        flowName: [
          { required: true, message: '请输入流程名称', trigger: 'change' }
        ],
        statusName: [
          { required: true, message: '请选择流程状态' }
        ]
      },
      formData: {
        bussinssType: 'add',
        processName: '',
        processTypeName: '',
        flowType: '1',
        processObj: {},
        flowCode: '',
        templateQuote: '', // 模板引用
        tagCode: '', // 标签编码
        serviceItem: '', // 服务项目
        runSupport: '', // 运行载体
        declareSystem: '' // 申报系统
      },
      tableIndex: 0,
      loading: null,
      uploadErrMsg: '',
      dialogVisible: false,
      processTypeName: '现有流程：',
      processTips: '说明：复制已有是通过现有的地区快速创建事项和操作流程',
      appCode: '',
      appInfo: {},
      businessType: null,
      processOption: [],
      disabledBussinssType: false,
      tableEdit: '',
      templateList: [], // 可以用来引用模板列表
      tagList: [], // 标签字典列表
      serviceItemArr: [
        { dictCode: 1, dictName: '增员' },
        { dictCode: 2, dictName: '减员' },
        { dictCode: 3, dictName: '调基' },
        { dictCode: 5, dictName: '补缴' },
        { dictCode: 6, dictName: '缴费' },
        { dictCode: 7, dictName: '在册名单' },
        { dictCode: 8, dictName: '费用明细' },
        { dictCode: 9, dictName: '政策通知' },
        { dictCode: 10, dictName: '基数申报' }
      ],
      statistics: {
        info1: 0, // 配置
        info2: 0, // 调试通过
        info3: 0, // 等待数据
        info4: 0, // 验证有效
        info5: 0 // 修复问题
      },
      dictList: {
        key10008: [], // 申报系统下拉
        key10019: [] // 运行载体下拉
      },
      buttonGroupList: [] // 按钮权限组
      // declareSystemSelects: [],   //列表存在的申报系统
    }
  },
  computed: {
    tableHeight () {
      return this.$global.bodyHeight - 220 + 'px'
    },
    // 业务类型
    bussinssType () {
      return function (val) {
        var arr = ['社保', '公积金']
        if (val != '' || val != null) {
          return arr[val - 1]
        } else {
          return '暂无'
        }
      }
    },
    getFlowType () {
      return function (val) {
        if (val == 1) {
          return '主流程'
        } else if (val == 2) {
          return '子流程'
        } else {
          return ''
        }
      }
    },
    // 业务类型
    changeType () {
      return function (val) {
        var arr = ['投保', '停保', '调整', '变更', '补缴']
        if (val != '' || val != null) {
          return arr[val - 1]
        } else {
          return '暂无'
        }
      }
    },
    declareSystemSelects () {
      return this.$store.state.declareSystemSelects
    }
  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({ name: 'RPA流程管理' })
    if (this.$route.query.appCode) {
      this.appCode = this.$route.query.appCode
    }
    // 获取store中的查询条件 有则回显否则按默认
    if (
      Object.keys(this.$store.state.routeQuery.processSettingQuery).length === 0
    ) {
      this.searchForms = {
        type: [1],
        service: [],
        declareSystemList: []
      }
    } else {
      this.searchForms = JSON.parse(
        JSON.stringify(this.$store.state.routeQuery.processSettingQuery)
      )
    }
    this.getProcessInfo()
    this.getAllFlow()
    this.getAppInfo()
    this.getTemplateList()
    this.getTagList()
    this.getDictByKey2(10019)
    console.log('declareSystemSelects', this.declareSystemSelects)
  },
  beforeRouteLeave (to, from, next) {
    if (to.path != '/processSetting/stepSetting') {
      // 将页面缓存改成默认的
      this.$store.dispatch('routeQuery/addprocessSettingQuery', {
        type: [1],
        service: [],
        declareSystemList: []
      })
    }

    next(true)
  },
  methods: {
    getServiceName (serviceItem) {
      if (serviceItem == 1) {
        return '增员'
      } else if (serviceItem == 2) {
        return '减员'
      } else if (serviceItem == 3) {
        return '调基'
      } else if (serviceItem == 5) {
        return '补缴'
      } else if (serviceItem == 6) {
        return '缴费'
      } else if (serviceItem == 7) {
        return '在册名单'
      } else if (serviceItem == 8) {
        return '费用明细'
      }
    },
    // 获取字典值
    async getDictByKey (key) {
      return this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: key
        }
      })
        .then((res) => {
          if (key == 10007) {
            // 申报系统下拉 社保时
            this.dictList.key10008 = res.data.data
          } else if (key == 10008) {
            // 申报系统 公积金下拉
            this.dictList.key10008 = res.data.data
          } else if (key == 10019) {
            // 运行载体下拉
            this.dictList.key10019 = res.data.data
          }
        })
        .catch((err) => {
          if (err.response.status == 401) return
          this.$message.error('字典接口出错，请稍后再试')
        })
    },
    // 获取运行载体
    async getDictByKey2 (key) {
      return this.$http({
        url: 'robot/data/dict/10019',
        method: 'post'
      })
        .then((res) => {
          this.dictList.key10019 = res.data.data
        })
        .catch((err) => {
          if (err.response.status == 401) return
          this.$message.error('字典接口出错，请稍后再试')
        })
    },
    changeFlowType (val) {
      if (this.formData.flowType == 2) {
        this.formData.serviceItem = ''
      }
    },
    // 获取应用详情
    async getAppInfo () {
      let that = this
      this.showLoading()
      await this.$http({
        url: '/robot/app/page',
        method: 'post',
        data: {
          query: [
            {
              property: 'appCode',
              value: this.appCode
            }
          ],
          page: 1,
          size: 1,
          start: 0
        }
      }).then(({ data }) => {
        if (data.data.rows.length > 0) {
          that.appInfo = data.data.rows[0]
          that.businessType = JSON.parse(data.data.rows[0].appArgs).businessType == '1001001' ? '社保' : '公积金'
          that.pathData[that.pathData.length - 1].name = 'RPA流程管理 （' + that.appInfo.appName + '）'
          // 获取应用详情后 需要获取当前按钮权限
          that.getButtonPermission(JSON.parse(data.data.rows[0].appArgs).addrName, JSON.parse(data.data.rows[0].appArgs).businessType)
          // 获取应用详情后 需要获取运行载体下拉 社保 10008 公积金 10019
          if (that.businessType == '社保') {
            this.getDictByKey(10007)
          } else {
            this.getDictByKey(10008)
          }
        }
      }).catch(() => {
      })
    },
    // 获取当前按钮权限
    async getButtonPermission (addrName, businessType) {
      let that = this
      this.showLoading()
      await this.$http({
        url: 'policy/dev/userAddr/getUserAddrButton',
        method: 'get',
        params: {
          addrName: addrName,
          businessType: businessType == 1001001 ? '社保' : '公积金'
        }
      }).then(({ data }) => {
        console.log('当前按钮权限-------------', data)
        this.buttonGroupList = data.data.map(item => item.buttonCode)
      }).catch(() => {
      })
    },
    // 获取列表查询
    getProcessInfo () {
      this.showLoading()
      this.$store.dispatch('routeQuery/addprocessSettingQuery', this.searchForms)
      this.$http({
        url: 'robot/flow/list',
        method: 'post',
        data: {
          appCode: this.appCode,
          flowTypeList: this.searchForms.type,
          serviceItemList: this.searchForms.service,
          declareSystemList: this.searchForms.declareSystemList
        }
      }).then(res => {
        this.closeLoading()
        res.data.data.sort((a, b) => {
          return a.execOrder - b.execOrder
        })
        this.table.tableData = res.data.data
        // 更新统计
        this.totalStatus()
        // 合并去重列表存在的申报系统
        let tempData = res.data.data.map(item => {
          return {
            declareSystem: item.declareSystem,
            declareSystemName: item.declareSystemName
          }
        })
        if (this.declareSystemSelects.length == 0) {
          let declareSystemSelects = Object.values(tempData.reduce((unique, item) => {
            if (!unique[item.declareSystem] && item.declareSystem) {
              unique[item.declareSystem] = item
            }
            return unique
          }, {}))
          if (declareSystemSelects.length == 1) {
            this.searchForms.declareSystemList = [declareSystemSelects[0].declareSystem]
          }
          this.$store.commit('updateDeclareSystemSelects', declareSystemSelects)
        }
      }).catch(err => {
        this.closeLoading()
      })
    },
    // 统计
    totalStatus () {
      this.showLoading()
      this.$http({
        url: 'robot/flow/totalStatus',
        method: 'post',
        data: {
          appCode: this.appCode,
          flowTypeList: this.searchForms.type,
          serviceItemList: this.searchForms.service
        }
      }).then(res => {
        this.closeLoading()
        let statisticsInfo = res.data.data
        this.statistics.info1 = statisticsInfo.find(item => item.status == 0) ? statisticsInfo.find(item => item.status == 0).statusNum : 0
        this.statistics.info2 = statisticsInfo.find(item => item.status == 1) ? statisticsInfo.find(item => item.status == 1).statusNum : 0
        this.statistics.info3 = statisticsInfo.find(item => item.status == 2) ? statisticsInfo.find(item => item.status == 2).statusNum : 0
        this.statistics.info4 = statisticsInfo.find(item => item.status == 3) ? statisticsInfo.find(item => item.status == 3).statusNum : 0
        this.statistics.info5 = statisticsInfo.find(item => item.status == 4) ? statisticsInfo.find(item => item.status == 4).statusNum : 0
      }).catch(err => {
        this.closeLoading()
      })
    },
    checkProcessTypeName (rules, value, callback) {
      if (!value) {
        return callback(new Error('请选择' + this.processTypeName.replace('：', '')))
      }
      return callback()
    },
    checkProcessName (rules, value, callback) {
      var check = false
      this.table.tableData.forEach(item => {
        if (value == item.flowName && !item.self) {
          check = true
        }
      })
      if (check) {
        return callback('流程名称不能重复')
      }
      return callback()
    },
    changeProcess (item) {
      this.formData.processTypeName = this.formData.processObj.appNameAndFlowName
    },
    getAllFlow (appCode) {
      var url = appCode ? 'robot/flow/showOneAppFlow?appCode=' + appCode : 'robot/flow/showOneAppFlow'
      this.$http({
        url: url,
        method: 'post'
      }).then(res => {
        this.closeLoading()
        this.processOption = res.data.data
      }).catch(err => {
        this.closeLoading()
      })
    },
    // 流程添加方式改变
    changeBussinssType (val) {
      if (val == 'copyAdd') {
        this.processTypeName = '现有流程：'
        this.processTips = '说明：复制已有是通过现有的地区快速创建事项和操作流程'
        this.getAllFlow()
      } else if (val == 'relateAdd') {
        this.processTypeName = '关联流程：'
        this.processTips = '说明：关联已有多申报帐户使用相同事项和操作流程'
        this.getAllFlow(this.appCode)
      }
      this.$refs.addProcess.clearValidate(['processTypeName'])
    },
    // 添加流程
    addProcess () {
      this.dialogVisible = true
    },
    // 取消添加流程
    cancel () {
      this.dialogVisible = false
      this.tableEdit = ''
      this.disabledBussinssType = false
      this.table.tableData.forEach(item => {
        item.self = false
      })
      this.formData = {
        bussinssType: 'add',
        processName: '',
        processTypeName: '',
        flowType: '1',
        processObj: {},
        flowCode: '',
        runSupport: '',
        declareSystem: '',
        serviceItem: '',
        templateQuote: '', // 模板引用
        tagCode: '' // 标签编码
      }
      this.$refs.addProcess.resetFields()
    },
    // 确定添加流程
    confirm (type) {
      // 打印模板引用的值
      console.log('打印模板引用的值', this.formData)
      this.$refs.addProcess.validate((valid) => {
        console.log(valid)
        if (valid) {
          console.log('添加流程')
          var obj = {}
          if (this.tableEdit == 'edit') {
            obj = {
              appCode: this.appCode,
              flowName: this.formData.processName,
              flowType: this.formData.flowType,
              flowCode: this.formData.flowCode,
              tagCode: this.formData.tagCode,
              serviceItem: this.formData.serviceItem,
              id: this.formData.id,
              declareSystem: this.formData.declareSystem,
              runSupport: this.formData.runSupport

            }
            this.showLoading()
            this.$http({
              url: 'robot/flow/editFlow',
              method: 'post',
              data: obj
            }).then(res => {
              this.closeLoading()
              if (res.data.code == 200) {
                this.getProcessInfo()
                this.cancel()
                this.getAllFlow()
                this.$message.success('操作成功')
              }
            }).catch(err => {
              this.closeLoading()
              // this.$message.error('接口错误，请稍后再试')
            })
            return
          } else if (this.formData.bussinssType == 'add') {
            obj = {
              appCode: this.appCode,
              flowName: this.formData.processName,
              flowType: this.formData.flowType
            }
          } else if (this.formData.bussinssType == 'copyAdd') {
            obj = {
              appCode: this.appCode,
              flowName: this.formData.processName,
              flowCode: this.formData.processObj.flowCode,
              flowType: ''
            }
          } else if (this.formData.bussinssType == 'relateAdd') {
            obj = {
              appCode: this.appCode,
              flowCode: this.formData.processObj.flowCode,
              flowName: this.formData.processName,
              flowType: ''
            }
          }

          this.showLoading()
          this.$http({
            url: 'robot/flow/' + this.formData.bussinssType,
            method: 'post',
            params: {
              ...obj,
              templateFlowCode: this.formData.templateQuote,
              tagCode: this.formData.tagCode,
              serviceItem: this.formData.serviceItem,
              declareSystem: this.formData.declareSystem,
              runSupport: this.formData.runSupport
            }
          }).then(res => {
            this.closeLoading()
            this.getProcessInfo()
            this.cancel()
            this.getAllFlow()
            this.tableEdit = ''
          }).catch(err => {
            this.closeLoading()
          })
        } else {
          console.log(valid)
        }
      })
    },
    // 操作栏，删除
    remove (row, index) {
      this.$confirm('是否确认删除该流程？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        // this.table.tableData.splice(index, 1)
        this.showLoading()
        this.$http({
          url: 'robot/flow/delete',
          method: 'post',
          params: {
            flowCode: row.flowCode
          }
        }).then(res => {
          this.closeLoading()
          this.getProcessInfo()
          this.$message.success(res.message ? res.message : '操作成功')
        }).catch(err => {
          this.closeLoading()
        })
      }).catch(() => { })
    },
    // 操作栏，上挪
    moveUp (row, index) {
      this.$nextTick(() => {
        this.$refs.table.doLayout()
        this.showLoading()
        this.$http({
          url: 'robot/flow/update',
          method: 'post',
          params: {
            appCode: row.appCode,
            flowCode: row.flowCode,
            flag: 1,
            flowName: row.flowName
          }
        }).then(res => {
          this.closeLoading()
          if (res.data.code != 200) {
            return
          }
          if (index != 0) {
            this.table.tableData[index].execOrder -= 1
            this.table.tableData[index - 1].execOrder += 1
            this.table.tableData[index] = this.table.tableData.splice(
              index - 1,
              1,
              this.table.tableData[index]
            )[0]
          } else {
            this.table.tableData.push(this.table.tableData.shift())
          }
        }).catch(err => {
          this.closeLoading()
        })
      })
    },
    // 操作栏，下移
    moveDown (row, index) {
      this.$nextTick(() => {
        this.showLoading()
        this.$refs.table.doLayout()
        this.$http({
          url: 'robot/flow/update',
          method: 'post',
          params: {
            appCode: row.appCode,
            flowCode: row.flowCode,
            flag: 2,
            flowName: row.flowName
          }
        }).then(res => {
          this.closeLoading()
          if (res.data.code != 200) {
            return
          }
          // this.getProcessInfo()
          if (index != this.table.tableData.length - 1) {
            this.table.tableData[index].execOrder += 1
            this.table.tableData[index + 1].execOrder -= 1
            this.table.tableData[index] = this.table.tableData.splice(
              index + 1,
              1,
              this.table.tableData[index]
            )[0]
          } else {
            this.table.tableData.unshift(this.table.tableData.splice(index, 1)[0])
          }
        }).catch(err => {
          this.closeLoading()
        })
      })
    },
    // 编辑asd
    edit (row, index) {
      this.formData.processName = row.flowName
      this.formData.flowType = row.flowType + ''
      this.formData.bussinssType = row.addType ? row.addType : 'add'
      this.formData.flowCode = row.flowCode
      this.formData.id = row.id
      this.formData.templateQuote = row.templateFlowCode
      this.formData.tagCode = row.tagCode
      this.formData.serviceItem = row.serviceItem ? row.serviceItem : ''
      this.formData.declareSystem = row.declareSystem
      this.formData.runSupport = row.runSupport
      this.disabledBussinssType = true
      this.dialogVisible = true
      this.tableEdit = 'edit'
      row.self = true
    },
    // 操作栏步骤设置
    goUrl (row, index) {
      this.$router.push('/processSetting/stepSetting?flowCode=' + row.flowCode + '&appCode=' + row.appCode + '&id=' + row.id + '&comment=' + row.comment + '&statusName=' + row.statusName)
    },
    // 测试流程 按钮、拖拽操作都不可用
    goUrl2 (row, index) {
      // 测试流程 检测状态非【调试通过、等待数据、验证有效】，无法进行测试 弹出提示
      console.log(row)
      if (row.status > 0 && row.status < 4) {
        this.$router.push('/processSetting/stepSetting?flowCode=' + row.flowCode + '&appCode=' + row.appCode + '&id=' + row.id + '&isTest=1' +
          '&comment=' + row.comment + '&statusName=' + row.statusName)
      } else {
        this.$message.warning(`当前状态为${row.statusName}，无法测试进行。`)
      }
    },
    // 流程设置
    goFlowUrl (row, index) {
      console.log('row------->', row)
      let url = `/processSetting/flow?flowCode=${row.flowCode}&appCode=${row.appCode}&id=${row.id}&appName=${this.appInfo.appName}&flowName=${row.flowName}`
      if (row.templateFlowCode) url += `&templateFlowCode=${row.templateFlowCode}`
      this.$router.push(url)
    },
    // 深克隆
    deepCopy (obj) {
      var result = Array.isArray(obj) ? [] : {}
      for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
          if (typeof obj[key] === 'object' && obj[key] !== null) {
            result[key] = this.deepCopy(obj[key]) // 递归复制
          } else {
            result[key] = obj[key]
          }
        }
      }
      return result
    },
    showLoading (target) {
      this.loading = this.$loading({
        target: target || document.body,
        lock: true,
        text: '加载中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      })
    },
    closeLoading () {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    },
    // 设置时间,时间转换
    formatDateTime (date, params, separator) {
      if (date == undefined || date == null || date == '') {
        return ''
      }
      var date = new Date(date.substring(0, 19))
      var Year = date.getFullYear()
      var Month = date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1
      var d = date.getDate() < 10 ? '0' + date.getDate() : date.getDate()
      var Hours = date.getHours() < 10 ? '0' + date.getHours() : date.getHours()
      var Minutes = date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()
      var Seconds = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds()
      var over_time = Year + '/' + Month + '/' + d + ' ' + Hours + ':' + Minutes + ':' + Seconds
      //* **至此以上是将时间2020-03-18T01:57:23.000+0000转为正常时间格式，以下为将时间进行增加8小时解决时区差异的操作***
      var time = new Date(over_time)
      time.setTime(time.setHours(time.getHours()))
      var y = time.getFullYear()
      var m = this.addZero(time.getMonth() + 1)
      var d = this.addZero(time.getDate())
      var h = this.addZero(time.getHours() + 8)
      var minute = this.addZero(time.getMinutes())
      var second = this.addZero(time.getSeconds())
      var symbol = separator || '-'

      if (!params) {
        return y + '_' + m + '_' + d + '_' + h + ':' + minute + ':' + second
      }

      var arr = params.split('-')

      var result = ''
      for (var i = 0; i < arr.length; i++) {
        switch (arr[i]) {
          case 'y':
            result += y + (i != arr.length - 1 ? symbol : '')
            break
          case 'M':
            result += m + (i != arr.length - 1 ? symbol : '')
            break
          case 'd':
            result += d + (i != arr.length - 1 ? ' ' : '')
            break
          case 'h':
            result += h + (i != arr.length - 1 ? ':' : '')
            break
          case 'm':
            result += minute + (i != arr.length - 1 ? ':' : '')
            break
          case 's':
            result += second
            break
        }
      }
      return result
    },
    addZero (num) {
      return num < 10 ? '0' + num : num
    },
    // 获取流程模板列表
    async getTemplateList () {
      const { data } = await this.$http({
        url: 'robot/flow/listTemplate',
        params: {
          templateType: 1,
          flowType: 1
        },
        method: 'post'
      })
      console.log('流程模板引用列表 -------------', data)
      this.templateList = data.data
    },
    // 获取标签列表
    async getTagList () {
      const { data } = await this.$http({
        url: 'robot/data/dict/10018',
        method: 'post'
      })
      console.log('获取标签列表 -------------', data)
      this.tagList = data.data
    },
    // 调整状态
    handleClick1 (e, scope) {

    },
    // 排序
    handleClick2 (e, scope, index) {
      console.log(this.$refs[`popover-${index}`])
      this.$refs[`popover-${index}`].doShow()
    },
    // 调整状态下拉
    handleCommand1 (command, row) {
      console.log(command, row)
      switch (command) {
        case 'config':
          this.hanleChangeStatusModal(row, 0)
          break
        case 'test':
          this.hanleChangeStatusModal(row, 1)
          break
        case 'wait':
          this.hanleChangeStatusModal(row, 2)
          break
        case 'verify':
          this.hanleChangeStatusModal(row, 3)
          break
        case 'fix':
          this.hanleChangeStatusModal(row, 4)
          break
        default:
          break
      }
    },
    // 排序下拉
    handleCommand2 (command, scope) {
      console.log(command, scope.row)
      switch (command) {
        case 'edit':
          this.edit(scope.row, scope.$index)
          break
        case 'delete':
          this.remove(scope.row, scope.$index)
          break
        case 'graph':
          this.goFlowUrl(scope.row, scope.$index)
          break
        default:
          break
      }
    },
    // 取消排序框
    cancelPopover (row, index) {
      this.newIndex = ''
      this.$refs[`popover-${index}`].doClose()
    },
    // 确定排序框
    async confirmPopover (row, index) {
      if (!this.newIndex) {
        this.$message.warning('请输入排序')
        return
      }
      // 输入的序号不能小于最小execOrder 或 不能大于最大execOrder
      let max = Math.max(...this.table.tableData.map(item => item.execOrder))
      let min = Math.min(...this.table.tableData.map(item => item.execOrder))
      if (this.newIndex < min || this.newIndex > max) {
        this.$message.warning(`输入排序不能少于${min}，不能大于${max}`)
        return
      }
      // 请求接口进行排序
      const { data } = await this.$http({
        url: 'robot/flow/editFlowOrder',
        data: {
          id: row.id,
          appCode: this.appCode,
          execOrder: this.newIndex
        },
        method: 'post'
      })
      if (data.code == 200) {
        this.$message.success('操作成功')
        this.newIndex = ''
        this.$refs[`popover-${index}`].doClose()
        this.getProcessInfo()
      }
    },
    // 弹窗 调整状态
    hanleChangeStatusModal (scope, type) {
      this.dialogVisible4 = true
      this.changeStatus = type
      console.log(scope, type)
      this.formData.flowName = scope.row.flowName
      this.formData.statusName = scope.row.statusName
      this.formData.flowCode = scope.row.flowCode
      this.formData.status = scope.row.status
      this.formData.id = scope.row.id
    },
    // 取消 调整状态
    cancelChangeStatus () {
      this.dialogVisible4 = false
      this.formData.rule = ''
      this.formData.reason = ''
      this.$nextTick(() => {
        this.$refs.statusForm.clearValidate()
      })
    },
    // 确定 调整状态
    async confirmChangeStatus () {
      let that = this
      this.$refs.statusForm.validate(async (valid) => {
        if (valid) {
          const { data } = await this.$http({
            url: 'robot/flow/editFlowStatus',
            data: {
              status: this.changeStatus,
              comment: this.formData.reason,
              flowCode: this.formData.flowCode,
              appCode: this.appCode,
              id: this.formData.id
            },
            method: 'post'
          })
          if (data.code == 200) {
            this.$message.success('操作成功')
            this.cancelChangeStatus()
            // 重新请求页面
            await this.getProcessInfo()
            setTimeout(() => {
              console.log('tsasd', this.table.tableData)
              let tempStatusList = this.table.tableData.map(item => item.status)
              console.log(tempStatusList)
              console.log('过滤列表中不为【验证有效】的条目数：', tempStatusList.filter(item => item != 3).length)
              // 验证所有流程的状态都为【验证有效】后弹出上线跳转弹框
              if (tempStatusList.filter(item => item != 3).length > 0) return
              this.$confirm('所有流程均【验证有效】，该应用已达到上线标准', '提示', {
                confirmButtonText: '立即上线',
                cancelButtonText: '暂不上线',
                showClose: false,
                customClass: 'pal-confirm',
                type: 'warning'
              }).then(() => {
                that.$router.push('/processSetting/index?appName=' + that.appInfo.appName)
              }).catch(() => { })
            }, 500)
          }
        }
      })
    },
    // 显示流程状态
    showFlowTable (row) {
      this.flowTableVisiable = true
      console.log(row)
      this.getStatusHistory(this.appCode, row.flowCode)
    },
    // 取消显示 流程状态
    cancelFlowTable () {
      this.flowTableVisiable = false
    },
    // 获取状态历史
    async getStatusHistory (appCode, flowCode) {
      const { data } = await this.$http({
        url: 'robot/flow/statusHistory',
        data: {
          appCode: appCode,
          flowCode: flowCode,
          historyType: 2
        },
        method: 'post'
      })
      this.tableData = data.data
      console.log('状态历史---------', data)
    }
  }
}
</script>

<style lang="less" scoped>
.search-row {
  padding: 15px 20px;
  .label-text{
    width: 90px;
    line-height: @inputHeight;
  }
}

/deep/.table-header {
  background: #f5f5f5 !important;
  color: #444;
}

/deep/.el-table__cell {
  border-bottom: 1px solid #ddd;
  border-right: 1px solid #ddd;
}

/deep/.el-table__row {
  border-bottom: 1px solid #ddd;
  border-right: 1px solid #ddd;
}

/deep/.el-table--border {
  border: 1px solid #ddd;
}

/deep/.el-table .el-table__cell {
  padding: 9px 0;
}

.control-btn {
  margin-left: 10px;
  cursor: pointer;
}

.gray-btn {
  padding: 5px 10px;
  background: #fff;
  color: #949494;
  border: 1px solid #949494;
  font-size: 12px;
  border-radius: 5px;
  cursor: pointer;
}

.footer-btn {
  position: absolute;
  // bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
}

/deep/.el-drawer__body {
  padding-bottom: 0;
}

.footer-btn-box {
  text-align: center;
  margin-top: 40px;

  .footer-btn {
    width: 100px;
    height: 35px;
    line-height: 35px;
    padding: 0 10px;
  }
}

.footer-btn {
  width: 100px;
  height: 35px;
  line-height: 35px;
  padding: 0 10px;
}

.flex-no-wrap {
  display: flex;
  flex-wrap: nowrap;
}

.bottom-group {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.aligin-center {
  align-items: center;
}
.adjust-content-box{
  /deep/.el-form-item{
    margin-bottom: 10px;
  }
}

@media screen and (min-width: 1440px) and (max-width:1920px) {
  .declareSystemList {
    width: 140px;
  }
}

@media screen and (min-width: 1920px) {
  .declareSystemList {
    width: 240px;
  }
}
</style>
