<template>
  <!-- 报盘文件设置 -->
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <div class="search-row">
        <el-row>
          <el-col :span="8">
            <div class="display-flex" style="align-items: center;">
              <span class="ml-20 label">客户：</span>
              <el-select
                placeholder="请选择"
                filterable
                v-model="formData.clientId"
                style="width:260px;"
                clearable
                @clear="clearClientId"
              >
                <el-option
                  v-for="(item, index) in listCustomerOption"
                  :key="index"
                  :label="item.customerName"
                  :value="item.id"
                ></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="display-flex" style="align-items: center;">
              <span class="ml-20 label">业务类型：</span>
              <el-checkbox-group v-model="formData.statusList">
                <el-checkbox :label="1">启用</el-checkbox>
                <el-checkbox :label="2">禁用</el-checkbox>
              </el-checkbox-group>
            </div>
          </el-col>
          <el-col :span="8" style="text-align: right;">
            <el-button size="small" type="primary" class="ml-20 w-60" @click="getTableData">查询</el-button>
            <el-button size="small" type="primary" class="ml-20" @click="addRule">添加规则</el-button>
          </el-col>
        </el-row>
      </div>
      <div>
        <dTable
          ref="table"
          style="margin-top: 0;"
          :tableHeight="tableHeight"
          :showIndex="false"
          :showNotFixedIndex="true"
          :showSelection="false"
          row-key="id"
          row-id="id"
        >
          <u-table-column
            prop="customerName"
            label="客户"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          ></u-table-column>
          <u-table-column
            prop="sort"
            label="优先级"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
            width="100"
          ></u-table-column>
          <u-table-column
            prop="status"
            label="状态"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
            width="100"
          >
            <template slot-scope="scope">
              <p>{{getStatus(scope.row.status)}}</p>
            </template>
          </u-table-column>
          <u-table-column
            prop="comment"
            label="规则描述"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          ></u-table-column>
          <u-table-column
            prop="relation"
            label="逻辑"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
            width="120"
          >
            <template slot-scope="scope">{{ scope.row.relation == 1 ? '所有' : '单一' }}</template>
          </u-table-column>
          <u-table-column
            prop="conclusion"
            label="条件"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          >
          </u-table-column>
          <u-table-column
            prop="machineCode"
            label="设备或环境"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
            width="150"
          ></u-table-column>
          <u-table-column label="操作" align="left" width="250">
            <template slot-scope="scope">
              <div>
                <el-button
                  type="text"
                  size="small"
                  class="text-blue"
                  @click="showgGetById(scope.row,'view')"
                >查看</el-button>
                <div class="opt-btn-split"></div>
                <el-button
                  type="text"
                  size="small"
                  class="text-blue"
                  @click="showgGetById(scope.row,'edit')"
                >编辑</el-button>
                <div class="opt-btn-split"></div>
                <el-button
                  type="text"
                  size="small"
                  class="text-blue"
                  @click="stopOrOPen(scope.row)"
                >{{scope.row.status == 1 ?'禁用':'启用'}}</el-button>
                <div class="opt-btn-split"></div>
								<el-button
                  type="text"
                  size="small"
                  class="text-blue"
                  @click="showSort(scope.row)"
                >优先级</el-button>
              </div>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>
    <!--设置规则-->
    <el-drawer
      title="设置规则"
      :visible.sync="show"
      :wrapperClosable="false"
      custom-class="spl-filter-drawer detail-drawer"
      :show-close="true"
      size="100%"
      :before-close="cancelSave"
    >
      <div class="drawer-body" ref="drawerBody" style="padding: 20px;margin: 0;">
        <el-form ref="setForm" :model="setForm" label-width="100px">
          <div class="item-row">
            <el-form-item
              prop="clientId"
              :rules="[{ required: true, message: '请选择客户', trigger: 'change' }]"
              label="客户:"
            >
              <el-select
                placeholder="请选择"
                filterable
                v-model="setForm.clientId"
                style="width:260px;"
                clearable
                @change="changeClient"
								:disabled="type == 'view'"
              >
                <el-option
                  v-for="(item, index) in listCustomerOption"
                  :key="index"
                  :label="item.customerName"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              prop="comment"
              :rules="[{ required: true, message: '请输入规则描述', trigger: 'change' }]"
              label="规则描述:"
            >
              <el-input
                v-model.trim="setForm.comment"
                placeholder="请输入规则描述"
                style="width: 300px"
								:disabled="type == 'view'"
                clearable
              ></el-input>
            </el-form-item>
            <el-form-item
              prop="relation"
              :rules="[{ required: true, message: '请选择', trigger: 'change' }]"
              label-width="160px"
              label="逻辑规则：符合以下"
              style="margin-left: 8px;"
            >
              <!-- <span
                class="required label mr-10"
                style="width: 146px;display: inline-block;text-align: right;"
              >逻辑规则：符合以下</span> -->
              <el-select
                placeholder="请选择"
                filterable
                v-model="setForm.relation"
                style="width:260px;"
								:disabled="type == 'view'"
                clearable
              >
                <el-option label="所有" :value="1"></el-option>
                <el-option label="单一" :value="2"></el-option>
              </el-select>
              <span class="ml-10">条件</span>
            </el-form-item>
            <div
              style="display: flex;align-items: center;"
              v-for="(item,index) in setForm.ruleConditionList"
              :key="index"
            >
              <div style="margin-left: 60px;">
                <i
                  class="el-icon-delete f-cursor text-red label mr-5 font-18"
                  @click="delCondition(index)"
                  v-if="setForm.ruleConditionList.length > 1"
                ></i>
                <span
                  class="required label mr-10"
                  style="display: inline-block;text-align: right;margin-bottom: 22px;"
                >条件{{ index + 1 }}:</span>
              </div>
              <div style="display: flex;">
                <el-form-item
                  :prop="'ruleConditionList.' + index+ '.columnType'"
                  :rules="[{ required: true, message: '请选择', trigger: 'change' },{ required: true, validator: validateColumnType(index), trigger: 'change'  }]"
                  class="ml-10"
                  label-width="0"
                >
                  <el-select
                    placeholder="请选择"
                    filterable
                    v-model="item.columnType"
                    style="width:260px;"
                    clearable
                    @change="changeColumnType($event,item,index)"
										:disabled="type == 'view'"
                    :ref="item.columnType + index + '-1'"
                  >
                    <el-option label="应用名称" :value="1"></el-option>
                    <el-option label="申报账户" :value="2"></el-option>
                    <el-option label="申报系统" :value="3"></el-option>
                    <el-option label="事项" :value="4"></el-option>
                    <el-option label="登录方式" :value="5"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :prop="'ruleConditionList.' + index + '.contrastMode'"
                  :rules="[{ required: true, message: '请选择', trigger: 'change' }]"
                  class="ml-10"
                  label-width="0"
                >
                  <el-select
                    placeholder="请选择"
                    filterable
                    v-model="item.contrastMode"
                    style="width:260px;"
										:disabled="type == 'view'"
                    clearable
                    :ref="item.contrastMode + index"
                  >
                    <!-- <el-option label="等于" :value="1"></el-option> -->
                    <!-- <el-option label="不等于" :value="2"></el-option> -->
                    <el-option label="包含" :value="3"></el-option>
                    <el-option label="不包含" :value="4"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :prop="'ruleConditionList.' + index+ '.columnValueList'"
                  :rules="[{ required: true, message: '输入值', trigger: 'change' }]"
                  class="ml-10"
									v-if="item.columnType"
                  label-width="0"
                >
                  <el-select
                    placeholder="请选择"
                    filterable
                    v-model="item.columnValueList"
                    style="width:260px;"
										:disabled="type == 'view'"
                    clearable
                    v-if="item.columnType == 1"
                    :ref="item.columnType + index + '-02'"
                    multiple
                    collapse-tags
                  >
                    <el-option
                      v-for="(it,index) in options.appList"
                      :key="index"
                      :label="it.appName"
                      :value="it.appCode"
                    ></el-option>
                  </el-select>
                  <el-select
                    placeholder="请选择"
                    filterable
                    v-model="item.columnValueList"
                    style="width:260px;"
										:disabled="type == 'view'"
                    clearable
                    v-if="item.columnType == 2"
                    :ref="item.columnType + index + '-02'"
                    multiple
                    collapse-tags
                  >
                    <el-option
                      v-for="(it,index) in options.accountNumberList"
                      :key="index"
                      :label="it.accountAndOrgName"
                      :value="it.taskCode"
                    ></el-option>
                  </el-select>
                  <el-select
                    placeholder="请选择"
                    filterable
                    v-model="item.columnValueList"
                    style="width:260px;"
										:disabled="type == 'view'"
                    clearable
                    v-if="item.columnType == 3"
                    :ref="item.columnType + index + '-02'"
                    collapse-tags
                    multiple
                  >
                    <el-option
                      v-for="(it,index) in options.declareSystemSelects"
                      :key="index"
                      :label="it.dictName"
                      :value="it.dictCode"
                    ></el-option>
                  </el-select>
                  <el-select
                    placeholder="请选择"
                    filterable
                    v-model="item.columnValueList"
                    style="width:260px;"
                    clearable
                    v-if="item.columnType == 4"
										:disabled="type == 'view'"
                    :ref="item.columnType + index + '-02'"
                    multiple
                    collapse-tags
                  >
                    <el-option label="增减员（增员、减员、调基、补缴）" :value="1"></el-option>
                    <el-option label="缴费" :value="6"></el-option>
                    <el-option label="在册名单" :value="7"></el-option>
                    <el-option label="费用明细" :value="8"></el-option>
                    <el-option label="政策通知" :value="9"></el-option>
                    <el-option label="基数申报" :value="10"></el-option>
                    <el-option label="登录" :value="11"></el-option>
                    <el-option label="查审核结果" :value="12"></el-option>
                  </el-select>
                  <el-select
                    placeholder="请选择"
                    filterable
                    v-model="item.columnValueList"
                    style="width:260px;"
                    clearable
                    v-if="item.columnType == 5"
										:disabled="type == 'view'"
                    :ref="item.columnType + index + '-02'"
                    multiple
                    collapse-tags
                  >
                    <el-option label="H5认证" :value="1"></el-option>
                    <el-option label="配置优先城市" :value="2"></el-option>
                    <el-option label="ukey认证" :value="3"></el-option>
                    <el-option label="账号密码认证" :value="4"></el-option>
                  </el-select>
                </el-form-item>
              </div>
            </div>
            <div style="margin-left: 60px;">
              <span class="a-aim f-cursor text-blue" @click="addCondition" v-if="type == 'edit' || type == 'add'">添加条件</span>
            </div>
          </div>
          <div style="margin-top:20px;">
            <el-form-item
              prop="machineCodeList"
              :rules="[{ required: true, message: '请选择', trigger: 'change' }]"
              class="ml-10"
              label="那么任务下发到执行"
              label-width="150px"
            >
              <el-select
                placeholder="请选择"
                filterable
                v-model="setForm.machineCodeList"
                style="width:260px;"
								:disabled="type == 'view'"
                clearable
                multiple
                collapse-tags
              >
                <el-option
                  v-for="(item, index) in options.machineCodeList"
                  :key="index"
                  :label="item.machineCode"
                  :value="item.machineCode"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-form>
      </div>
      <div class="drawer-footer-buts">
        <!-- <el-button class="w-80 s-btn" v-show="setForm.uuid && isExistRule && setForm.rules.length>0" @click="handleClearRule">清空校验</el-button> -->
        <!-- <el-tooltip v-show="setForm.uuid && !isExistRule && !isImportRule" effect="dark" content="未设置时，可引入同申报网站同字段规则" placement="top">
    	      <el-button class="w-80 btn--border-blue s-btn" @click="handleImportRule">引入规则</el-button>
        </el-tooltip>-->
        <el-button class="w-80 btn--border-blue s-btn" @click="cancelSave">取消</el-button>
        <el-button type="primary" class="w-80 s-btn" @click="handleConfirmSave()" v-if="type == 'edit' || type == 'add'">确定保存</el-button>
      </div>
    </el-drawer>
		<el-dialog title="调整优先级" :visible.sync="dialogData.show" width="500px" :close-on-click-modal="false" class="test">
      <el-form ref="dialogData" :model="dialogData" label-width="100px">
				<el-form-item
          prop="sort"
          :rules="[{ required: true, message: '请选择优先级', trigger: 'change' }]"
					label="优先级"
        >
					<el-input-number v-model="dialogData.sort"  :min="1" :max="999" label="优先级"></el-input-number>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancel" size="small" class="w-60">取 消</el-button>
        <el-button type="primary" @click="confirm" size="small" class="w-60">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
  <script>
import dTable from '../../components/common/table'
export default {
  components: { dTable },
  data() {
    return {
      formData: {
        clientId: null,
        statusList: [],
      },
      setForm: {
        clientId: '',
        comment: '',
        machineCode: '',
        machineCodeList:[],
        relation: '',
        ruleConditionList: [{ columnType: '', contrastMode: '', columnValueList: [] }],
      },
      sortForm: {
        sortNumber: '',
        sortError: '',
      },
      dialogVisible: false,
      pathData: [],
      associatedFileList: [],
      rules: {
        columnType:[{ validator: this.validateColumnType, trigger: 'change' }]
      },
      associatedFileErrMsg: [],
      rowData: {
        bussinssType: '1',
      },
      operationTypeList: [],
      loading: null,
      allOptions: [],
      timer: false,
      showRelation: false,
      fileTable: [],
      listCustomerOption: [],
      show: false,
      options: {
        custList: [],
        machineCodeList: [],
        appList: [],
        accountNumberList: [],
        declareSystemSelects: [],
      },
			rows:[],
			sortIndex:0,
			type:'add',
			dialogData:{
				id:"",
				show:false,
				sort:1
			},
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 310 + 'px'
    },
    formatType() {
      return function (index) {
        var list = ['增员', '减员', '调基', '变更', '补缴']
        return list[index - 1]
      }
    },
    getTplTypeStr() {
      return function (key) {
        var str = ''
        this.allOptions.forEach((item) => {
          if (item.dictCode == key) {
            str = item.dictName
          }
        })
        return str
      }
    },
    getOperationTypeStr() {
      return function (changeType, operationType) {
        if (operationType == 1) {
          if (changeType == 1) {
            return '开户'
          } else if (changeType == 2) {
            return '封存'
          } else if (changeType == 3) {
            return '调基'
          } else if (changeType == 5) {
            return '补缴'
          }
        } else if (operationType == 2) {
          return '启封'
        } else if (operationType == 3) {
          return '转入'
        } else if (operationType == 4) {
          return '转出'
        }
        return ''
      }
    },
    getOptionName() {
      return function (key, option) {
        console.log(key, option)
        var str = ''
        option.forEach((item) => {
          if (item.dictCode == key) {
            str = item.dictName
          }
        })
        return str
      }
    },
    getKeyName() {
      return function (key, option) {
        var str = ''
        var mergeStr = ''
        option.forEach((item, index) => {
          if (item.tplType == key) {
            item.items.forEach((item1, idx) => {
              str += item1.itemName + (idx == item.items.length - 1 ? '' : '、')
            })
            if (item.merge) {
              mergeStr = ' 增补合并申报【是】'
            } else {
              mergeStr = ' 增补合并申报【否】'
            }
          }
        })
        return str ? '(' + str + ')' + mergeStr : ''
      }
    },
    getDeclareAccountStr() {
      return function (code) {
        var str = ''
        this.accountNumberList.forEach((item) => {
          if (item.accountNumber == code) {
            str = item.name
          }
        })
        return str
      }
    },
    getStatus() {
      return function (status) {
        if (status == 1) {
          return '启用'
        }
        if (status == 2) {
          return '禁用'
        }
        return ''
      }
    },
  },
  created() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({ name: '指定环境规则' })
    this.getlistCustomer()
  },
  mounted() {
    this.getTableData()
  },
  methods: {
    validateColumnType(ids){
      var that = this
      return function(rule, value, callback){
        var check = false
        that.setForm.ruleConditionList.forEach((item,index)=>{
          if(item.columnType == value && ids != index && item.columnType){
            check = true
          }
        })
        if(check){
          callback(new Error('不允许重复，请修正'))
          return
        }
        callback();
      }
      
    },  
    cancelSort(row) {
      this.sortForm.sortNumber = ''
      this.sortForm.error = ''
      this.$nextTick(() => {
        row.sortShow = false
      })
    },
    showSort(row, index) {
      // this.$set(row, 'sortShow', true)
			// console.log(index)
      // this.sortIndex = index
      // this.sortForm.show = true
      // this.sortForm.error = ''
      console.log('showSort', row, index)
			this.dialogData.sort = row.sort
			this.dialogData.show = true
			this.dialogData.id = row.id
    },
    save() {
			this.showLoading()
      this.$http({
        url: 'robot/clientRule/save',
        method: 'post',
        data: this.setForm,
      })
        .then((res) => {
					this.closeLoading()
					if(res.data.code == 200 && res.data.data == 1){
						this.$message.success('操作成功')
						this.getTableData()
						this.cancelSave()
					}else{
						this.$message.error(
        		  res.data.message ? res.data.message : '服务出错，请稍后再试'
        		)
					}
				})
        .catch((err) => {
					this.closeLoading()
					this.$message.error('操作失败，请联系管理员')
				})
    },
    handleConfirmSave() {
      this.$refs.setForm.validate((valid) => {
        if (valid) {
          this.setForm.ruleConditionList = this.setForm.ruleConditionList.map((item,index)=>{
            var columnType = this.$refs[item.columnType + index + '-1'][0].selectedLabel
            var contrastMode = this.$refs[item.contrastMode + index][0].selectedLabel
            var columnValueList = []
            this.$refs[item.columnType + index +'-02'][0].selected.forEach(item=>{
              columnValueList.push(item.currentLabel)
            })
            columnValueList = columnValueList.join(',')
            item.conclusion = index + 1 + '.' + columnType + contrastMode + columnValueList
            item.columnValue = item.columnValueList.join(',')
            return item
          })
          this.setForm.machineCode = this.setForm.machineCodeList.join(',')
          this.save()
        }
      })
    },
		cancelSave(){
			this.setForm = this.$options.data().setForm
			this.show = false
      this.$refs.setForm.clearValidate()
		},
    clearClientId() {
      this.formData.clientId = null
    },
    getlistCustomer() {
      this.$http({
        url: '/robot/app/client/listCustomer',
        method: 'post',
      })
        .then((data) => {
          this.listCustomerOption = data.data.data
        })
        .catch((err) => {})
    },
    getTableData(pageChange, callback) {
      var params = [
        { property: 'clientId', value: this.formData.clientId },
        { property: 'statusList', value: this.formData.statusList },
      ]
      var self = this
      this.$refs.table.fetch({
        pageChange: pageChange ? pageChange : '',
        query: params,
        method: 'post',
        url: 'robot/clientRule/page',
        callback: function (res) {
        },
      })
    },
    //去报盘信息维护
    handleSocialView(row) {
      this.$router.push('/offerFile/offerInfo?uuid=' + row.uuid)
    },
    showLoading(target) {
      this.loading = this.$loading({
        target: target ? target : document.body,
        lock: true,
        text: '加载中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)',
      })
    },
    closeLoading() {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    },
    downLoadFile(item) {
      this.$downloadFile('policy/offerSettings/downloadTpl?tplId=' + item.id, 'post', {}, this.$global.EXCEL)
    },
    //获取字典值
    getDictByKey(dataKey, key) {
      this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: dataKey,
        },
      }).then((res) => {
        if (dataKey == '10004') {
          if (res && res.data.data) {
            res.data.data = res.data.data.map((item) => {
              return {
                itemCode: item.dictCode,
                itemName: item.dictName,
              }
            })
            this.$set(this.relationData, '2', res.data.data)
          }
        } else {
          this.allOptions.push(...res.data.data)
          this.$set(this.relationData.options, key, res.data.data)
          // this.relationData.options[key] = res.data.data
        }
        // console.log(this.relationData)
      })
    },
    //深克隆
    deepCopy(obj) {
      var result = Array.isArray(obj) ? [] : {}
      for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
          if (typeof obj[key] === 'object' && obj[key] !== null) {
            result[key] = this.deepCopy(obj[key]) //递归复制
          } else {
            result[key] = obj[key]
          }
        }
      }
      return result
    },
    addRule() {
      this.show = true
			this.type = 'add'
      this.$nextTick(()=>{
        this.$refs.setForm.clearValidate()
      })
    },
    addCondition() {
      this.setForm.ruleConditionList.push({ columnType: '', contrastMode: '', columnValueList: [] })
    },
    delCondition(index) {
      this.$confirm('是否确定移除此条件？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
          this.setForm.ruleConditionList.splice(index, 1)
          this.conditionKey = new Date().getTime()
        })
        .catch(() => {})
    },
    // 获取字典值
    async getDictByKey2(key) {
      return this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: key,
        },
      })
        .then((res) => {
          if (key == 10007) {
            // 申报系统下拉
            this.options.declareSystemSelects = res.data.data
          }
        })
        .catch((err) => {
          if (err.response.status == 401) {
            return
          }
          this.$message.error('字典接口出错，请稍后再试')
        })
    },
    changeClient(val){
      this.getMachineByClientId()
    },
    getMachineByClientId(type) {
      let clientId = this.setForm.clientId ? this.setForm.clientId : ''
      this.$http({
        url: '/robot/app/client/getMachineByClientId?clientId=' + clientId,
        method: 'get',
      })
        .then((data) => {
          if (data.data.code == '200') {
            this.options.machineCodeList = data.data.data
          }
        })
        .catch(() => {})
				if(type){
					return
				}
      	this.setForm.ruleConditionList = this.setForm.ruleConditionList.map((item) => {
      	  item.columnValueList = []
      	  item.columnType = ''
      	  return item
      	})
    },
    getDeclareSystem() {
      let appCode = this.setForm.appCode ? this.setForm.appCode : ''
      this.$http({
        url: '/robot/sessionKeep/listDeclareSystem?appCode=' + appCode,
        method: 'get',
      }).then((data) => {
        if (data.data.code == '200') {
          this.options.declareSystemSelects = data.data.data
        }
      })
    },
    changeCust() {
      let clientId = this.setForm.clientId ? this.setForm.clientId : ''
      this.$http({
        url: '/robot/sessionKeep/listApp?clientId=' + clientId + '&runSupport=',
        method: 'get',
      }).then((data) => {
        this.options.appList = data.data.data
      })
    },
    changeApp() {
      let clientId = this.setForm.clientId ? this.setForm.clientId : ''
      let appCode = ''
      this.$http({
        url: '/robot/sessionKeep/listTask?clientId=' + clientId + '&appCode=' + appCode,
        method: 'post',
      }).then((data) => {
        this.options.accountNumberList = data.data.data
      })
    },
    changeColumnType(val,item,index,type) {
        if(type != 'view'){
          this.setForm.ruleConditionList[index].columnValueList = []
        }
        if (val == 1) {
          this.changeCust()
        }
        if (val == 2) {
          this.changeApp()
        }
        if (val == 3) {
          this.getDictByKey2(10007)
        }
        this.$nextTick(()=>{
          this.setForm.ruleConditionList.forEach((item,index)=>{
            this.$refs.setForm.validateField(`ruleConditionList.${index}.columnType`)
          })
        })
    },
    showgGetById(row,type) {
      this.showLoading()
			this.type = type
			this.setForm.clientId = row.clientId
			this.setForm.comment = row.comment
			this.setForm.id = row.id
			this.setForm.machineCodeList = row.machineCode.toString().split(',')
      this.$http({
        url: 'robot/clientRule/getById',
        method: 'get',
        params: {
          id: row.id,
        },
      })
        .then(({data}) => {
          this.closeLoading()
          if(data.code == 200){
            this.show = true
					  this.setForm.relation = data.data.relation
					  this.setForm.ruleConditionList = data.data.ruleConditionList.map(item=>{
              if(item.columnType == 4 || item.columnType == 5){
                item.columnValueList = item.columnValue.toString().split(',').map(item=>{
                  item = Number(item)
                  return item
                })
              }else {
                item.columnValueList = item.columnValue.toString().split(',')

              }
              return item
            })
					  this.setForm.ruleConditionList.map((item,index)=>{
					  	this.changeColumnType(item.columnType,item,index,'view')
					  })
					  this.$nextTick(()=>{
					  	this.getMachineByClientId(1)
					  })
          }else{
            this.$message.error('请求失败，请稍后再试')
          }
        })
        .catch((err) => {
          console.log(err)
          this.$message.error('请求失败，请稍后再试')
          this.closeLoading()
        })
    },
		stopOrOPen(row){
			var status = row.status == 1 ? 2 : 1
			var str = row.status == 1 ? '禁用' : '启用'
			this.$confirm(`是否要${str}规则`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
			this.$http({
        url: 'robot/clientRule/enableOrStop',
        method: 'post',
        params: {
          id: row.id,
					status:status
        },
      })
        .then(({data}) => {
          if(data.code == 200){
						this.$message.success('操作成功')
						this.getTableData()
					}else{
						this.$message.error(
        		  res.data.message ? res.data.message : '服务出错，请稍后再试'
        		)
					}
          this.closeLoading()
        })
        .catch(() => {
					this.$message.error('服务出错，请稍后再试')
          this.closeLoading()
        })
			})
		},
		confirm(row){
			this.$http({
        url: 'robot/clientRule/updateSort',
        method: 'post',
        params: {
          id: this.dialogData.id,
					sort:this.dialogData.sort
        },
      })
        .then(({data}) => {
          if(data.code == 200){
						this.$message.success('操作成功')
						this.getTableData()
						this.cancel()
					}else{
						this.$message.error(
        		  res.data.message ? res.data.message : '服务出错，请稍后再试'
        		)
					}
          this.closeLoading()
        })
        .catch(() => {
					this.$message.error('服务出错，请稍后再试')
          this.closeLoading()
        })
		},
		cancel(){
			this.dialogData.show = false
			this.dialogData.sort = 0
			this.dialogData.id = ''
		}
  },
}
</script>
  <style lang="less" scoped>
/deep/.el-dialog__header {
  padding: 10px 20px;
}
/deep/.el-dialog__body {
  padding: 10px 10px;
}
/deep/.el-form-item__content {
  line-height: 0px;
}

/deep/ .detail-drawer {
  width: 1100px !important;
}

.file-content-box {
  padding: 10px 20px;
  font-size: 12px;
  .file-list {
    display: flex;
    padding: 10px 0;
    border-bottom: 1px dashed #ddd;
    .file {
      background: #f1f8ff;
      border-radius: 10px;
      padding: 3px 10px;
      position: relative;
      cursor: pointer;
      margin-right: 15px;
      font-size: 12px;
      &:hover {
        color: #3e82ff;
        text-decoration: underline;
      }
      .file-remove {
        position: absolute;
        right: -10px;
        top: -10px;
        width: 20px;
        height: 20px;
        background: red;
        border-radius: 50%;
        display: none;
        &::before {
          content: '';
          display: inline-block;
          width: 10px;
          height: 2px;
          background: white;
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
        }
      }
      &:hover .file-remove {
        display: block;
      }
    }
  }
  .upload-tips {
    margin-top: 20px;
    font-size: 12px;
    color: #797979;
  }
}
.upload-file-box {
  margin-top: 10px;
  border-bottom: 1px dashed rgb(221, 221, 221);
  padding-bottom: 10px;
  input[type='text'] {
    border: 1px solid #ddd;
    height: 28px;
    width: 250px;
    outline: none;
  }
  .upload-file-btn {
    height: 32px;
    background: #3e82ff;
    color: white;
    padding: 0 10px;
    box-sizing: border-box;
    font-size: 12px;
    cursor: pointer;
    line-height: 32px;
    margin-left: 10px;
  }
  .upload-file {
    padding: 4px 10px;
    height: 32px;
    line-height: 20px;
    position: relative;
    cursor: pointer;
    color: #888;
    background: #fafafa;
    border: 1px solid #ddd;
    display: inline-block;
    box-sizing: border-box;
    input[type='file'] {
      position: absolute;
      font-size: 17px;
      right: 0;
      top: 0;
      opacity: 0;
      width: 295px;
      filter: alpha(opacity=0);
      cursor: pointer;
      &:hover {
        color: #444;
        background: #eee;
        border-color: #ccc;
        text-decoration: none;
      }
    }
  }
}
.tag {
  display: inline-block;
  padding: 0 15px;
  height: 30px;
  line-height: 30px;
  background: rgba(255, 255, 255, 1);
  border: 1px solid rgba(206, 206, 206, 1);
  border-radius: 0px 2px 2px 0px;
  margin-right: 10px;
  cursor: pointer;
}
.tag:hover,
.tag.active {
  background-color: #dddddd;
}
.table-fileName-list {
  cursor: pointer;
  &:hover {
    color: #3e82ff;
    text-decoration: underline;
  }
}
.text-blue {
  &:hover {
    text-decoration: underline;
  }
}
</style>
  <style lang="less" scoped>
body .el-table th.gutter {
  display: table-cell !important;
}
.error {
  margin-top: 10px;
  /* position: absolute; */
  left: 0;
  /* top: 120%; */
  color: #f56c6c;
  font-size: 12px;
}
/deep/.el-drawer__body {
  padding-bottom: 0;
}
/deep/.el-dialog__header {
  border-bottom: none !important;
}
.file-content-box /deep/.table-header {
  background: #f5f5f5 !important;
  color: #444;
}
.file-content-box /deep/.el-table__cell {
  border-bottom: 1px solid #ddd;
  border-right: 1px solid #ddd;
}
.file-content-box /deep/.el-table__row {
  border-bottom: 1px solid #ddd;
  border-right: 1px solid #ddd;
}
.file-content-box /deep/.el-table--border {
  border: 1px solid #ddd;
}
/deep/.el-table .el-table__cell {
  padding: 0px 0;
}
.dialog-footer {
  display: flex;
  justify-content: center;
}
.placeholderStyle {
  padding-left: 10px;
  padding-right: 10px;
}
.placeholderStyle::-webkit-input-placeholder {
  font-size: 12px;
  color: #c0c4cc;
}
.cust-dialog /deep/.el-table__body tr.hover-row > td.el-table__cell {
  background-color: rgba(0, 0, 0, 0) !important;
}

/deep/.table-header {
  background-color: #f2f2f2 !important;
  padding: 6px 0;
  border-bottom: 1px solid #e2e2e2 !important;
  border-top: 1px solid #e2e2e2 !important;
  border-right: 1px solid #e2e2e2 !important;
  border-right: transparent;
  text-align: left;
}
/deep/.table-header .cell {
  font-weight: bold;
  color: #303133;
}
.cust-dialog /deep/.el-table .el-table__cell {
  padding: 6px 0;
}
/deep/.test .el-input__inner{
	height: 40px !important;
}
/deep/.el-select__tags-text{
  max-width: 100px;
}
</style>
