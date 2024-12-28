<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <el-row>
        <el-col :span="24">
          <div class="search-row display-flex">
            <div style="display: flex;width: 100%;">
              <el-input @keyup.enter.native="search" v-model.trim="keyword" clearable placeholder="请输入关键字搜索" style="width:250px;" ></el-input>

              <el-select v-model="type" class="w-200 ml-10" clearable filterable placeholder="请选择预警类型">
                <el-option v-for="it in option.warnTypes" :key="it.dictCode" :label="it.dictName" :value="it.dictCode"></el-option>
              </el-select>

              <el-select v-model="status" class="w-150 ml-10" clearable filterable placeholder="请选择状态">
                <el-option v-for="it in statuses" :key="it.code" :label="it.text" :value="it.code"></el-option>
              </el-select>

              <el-button type="primary" size="mini" @click="search" class="ml-20">查询</el-button>

              <div class="flex1 text-right">
                <el-button type="primary" size="mini" @click="addWarnConfigForm.show=true" class="ml-20">添加预警</el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      <div>
        <dTable @fetch="getTableData" ref="table" style="margin-top: 0;" :tableHeight="tableHeight"  :showIndex='true' :showSelection='false' row-key="id" row-id="id">
          <u-table-column prop="typeName" label="预警类别" min-width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="cron" label="预警频率" min-width="80" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="cronComment" label="预警频率描述" min-width="120" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="status" label="预警状态" min-width="80" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p v-if="scope.row.status==1" style="color: #13ce66">启动</p>
              <p v-else>停止</p>
            </template>
          </u-table-column>
          <u-table-column prop="recipients" label="提醒人员" min-width="160" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{handleRecipients(scope.row)}}</p>
            </template>
          </u-table-column>
          <u-table-column prop="sendSms" label="开启短信提醒" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p v-if="scope.row.sendSms==1">是</p>
              <p v-else>否</p>
            </template>
          </u-table-column>
          <u-table-column prop="sendEmail" label="开启邮件提醒" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p v-if="scope.row.sendEmail==1">是</p>
              <p v-else>否</p>
            </template>
          </u-table-column>
          <u-table-column label="操作" align="left" width="180" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="small" class="text-blue" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="text" size="small" class="text-blue ml-20" style="margin-left: 20px;" v-if="scope.row.status==0" @click="updateStatus(scope.row.id,1)">启动</el-button>
              <el-button type="text" size="small" class="text-blue ml-20" style="margin-left: 20px;" v-if="scope.row.status==1" @click="updateStatus(scope.row.id,1)">重启</el-button>
              <el-button type="text" size="small" class="text-blue ml-20" style="margin-left: 20px;" v-if="scope.row.status==1" @click="updateStatus(scope.row.id,0)">停止</el-button>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>

    <!-- 添加\编辑预警 -->
    <el-drawer :title="addWarnConfigForm.id==null?'添加预警':'编辑预警'" :visible.sync="addWarnConfigForm.show"
               :wrapperClosable="false" custom-class="spl-filter-drawer" :show-close="true" :before-close="clearAddWarnConfigForm">
      <div class="drawer-body pt-20 pb-20">
        <el-form ref="addWarnConfigForm" :model="addWarnConfigForm" class="filter-form">
          <label class="required item-label">预警类型</label>
          <el-form-item  v-show="addWarnConfigForm.id==null || addWarnConfigForm.id==''" label="" prop="type" :rules="[{ required: true, message: '请选择预警类型',trigger:'change'}]">
            <el-select v-model="addWarnConfigForm.type" class="w-p100" @change="changeWarnType" placeholder="请选择" clearable filterable>
              <el-option v-for="it in option.warnTypes" :key="it.dictCode" :label="it.dictName" :value="it.dictCode"></el-option>
            </el-select>
          </el-form-item>
          <el-input  class="w-p100" disabled  v-show="addWarnConfigForm.id!=null && addWarnConfigForm.id!=''" :value="addWarnConfigForm.typeName"/>

          <label class="required item-label">预警频率</label>
          <el-form-item label="" prop="cron" :rules="[{ required: true, message: '请输入预警频率',trigger:'blur'}]">
            <el-input v-model="addWarnConfigForm.cron" class="w-p100" maxlength="40" clearable placeholder="请输入"></el-input>
          </el-form-item>

          <label class="item-label">预警频率描述</label>
          <el-form-item label="" prop="cronComment">
            <el-input v-model.trim="addWarnConfigForm.cronComment" type="textarea"
                      :rows="2" class="w-p100" maxlength="150" clearable placeholder="请输入"></el-input>
          </el-form-item>

          <el-row>
            <el-col :span="6">
              <label class="required item-label">预警状态</label>
              <el-form-item label="" prop="status">
                <el-switch v-model="addWarnConfigForm.status" active-color="#13ce66"
                            :activeValue="1" :inactiveValue="0"></el-switch>
              </el-form-item>
            </el-col>
            <el-col :span="9">
              <label class="required item-label">短信提醒</label>
              <el-form-item label="" prop="sendSms">
                <el-radio-group v-model="addWarnConfigForm.sendSms">
                  <el-radio :label="1">开启</el-radio>
                  <el-radio :label="0">关闭</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="9">
              <label class="required item-label">邮件提醒</label>
              <el-form-item label="" prop="sendEmail">
                <el-radio-group v-model="addWarnConfigForm.sendEmail">
                  <el-radio :label="1">开启</el-radio>
                  <el-radio :label="0">关闭</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <label class="item-label">预警数据时间范围</label>
          <el-form-item label="" prop="rangeTime">
            <el-input v-model.trim="addWarnConfigForm.rangeTime" v-positiveNumberAndZero class="w-p100" maxlength="11" clearable placeholder="请输入"></el-input>
          </el-form-item>

          <label class="item-label" :class="{'required': addWarnConfigForm.sendSms==1}">短信内容</label>
          <el-form-item label="" prop="smsContent" :rules="[{ required: addWarnConfigForm.sendSms==1, message: '请输入短信内容',trigger:'blur'}]">
            <el-input v-model.trim="addWarnConfigForm.smsContent" type="textarea"
                      :rows="2" class="w-p100" maxlength="150" clearable placeholder="请输入"></el-input>
          </el-form-item>

          <label class="item-label" :class="{'required': addWarnConfigForm.sendEmail==1}">邮件主题</label>
          <el-form-item label="" prop="emailTheme" :rules="[{ required: addWarnConfigForm.sendEmail==1, message: '请输入邮件主题',trigger:'blur'}]">
            <el-input v-model.trim="addWarnConfigForm.emailTheme" type="textarea"
                      :rows="2" class="w-p100" maxlength="150" clearable placeholder="请输入"></el-input>
          </el-form-item>

          <label class="item-label" :class="{'required': addWarnConfigForm.sendEmail==1}">邮件内容</label>
          <el-form-item label="" prop="emailContent" :rules="[{ required: addWarnConfigForm.sendEmail==1, message: '请输入邮件内容',trigger:'change'}]">
            <quill-editor v-model="addWarnConfigForm.emailContent" ref="myQuillEditor" :options="editorOption">
              <div id="toolbar" slot="toolbar">
                <!-- Add a bold button -->
                <button class="ql-bold" title="加粗">Bold</button>
                <button class="ql-italic" title="斜体">Italic</button>
                <button class="ql-underline" title="下划线">underline</button>
                <button class="ql-strike" title="删除线">strike</button>
                <button class="ql-blockquote" title="引用"></button>
                <button class="ql-code-block" title="代码"></button>
                <button class="ql-header" value="1" title="标题1"></button>
                <button class="ql-header" value="2" title="标题2"></button>
                <!--Add list -->
                <button class="ql-list" value="ordered" title="有序列表"></button>
                <button class="ql-list" value="bullet" title="无序列表"></button>
                <!-- Add font size dropdown -->
                <select class="ql-header" title="段落格式">
                  <option selected>段落</option>
                  <option value="1">标题1</option>
                  <option value="2">标题2</option>
                  <option value="3">标题3</option>
                  <option value="4">标题4</option>
                  <option value="5">标题5</option>
                  <option value="6">标题6</option>
                </select>
                <select class="ql-size" title="字体大小">
                  <option value="10px">10px</option>
                  <option value="12px">12px</option>
                  <option value="14px">14px</option>
                  <option value="16px" selected>16px</option>
                  <option value="18px">18px</option>
                  <option value="20px">20px</option>
                </select>
                <select class="ql-font" title="字体">
                  <option value="SimSun">宋体</option>
                  <option value="SimHei">黑体</option>
                  <option value="Microsoft-YaHei">微软雅黑</option>
                  <option value="KaiTi">楷体</option>
                  <option value="FangSong">仿宋</option>
                  <option value="Arial">Arial</option>
                </select>
                <!-- Add subscript and superscript buttons -->
                <select class="ql-color" value="color" title="字体颜色"></select>
                <select class="ql-background" value="background" title="背景颜色"></select>
                <select class="ql-align" value="align" title="对齐"></select>
                <button class="ql-clean" title="还原"></button>
                <button class="ql-link" title="链接"></button>
                <!-- You can also add your own -->
              </div>
            </quill-editor>
          </el-form-item>

          <p class="fw-blod mb-10 mt-20" style="margin-left: -20px;">提醒人员 <el-button class="pl-20" type="text" icon="el-icon-plus" @click="addEmp">添加人员</el-button></p>

          <div>
            <table class="emp-table">
              <thead>
                <tr><th width="25%" class="required">姓名</th><th width="25%" :class="{'required': addWarnConfigForm.sendSms==1}">手机号码</th><th width="40%" :class="{'required': addWarnConfigForm.sendEmail==1}">电子邮箱</th><th width="10%"></th></tr>
              </thead>
              <tbody>
                <tr v-for="(item,index) in addWarnConfigForm.recipients" :key="index">
                  <td>
                    <el-form-item label="" :prop="'recipients.' + index + '.empName'" :rules="[{ required: true, message: '请输入姓名',trigger:'blur'}]">
                      <el-input v-model.trim="item.empName" class="w-p100" maxlength="150" clearable placeholder="请输入"></el-input>
                    </el-form-item>
                  </td>
                  <td>
                    <el-form-item label="" :prop="'recipients.' + index + '.phoneNumber'" :rules="[{ required: addWarnConfigForm.sendSms==1, message: '请输入手机号码',trigger:'blur'},{validator: $global.isPhoneValidator,trigger:['blur', 'change']}]">
                      <el-input v-model.trim="item.phoneNumber" class="w-p100" maxlength="20" clearable placeholder="请输入"></el-input>
                    </el-form-item>
                  </td>
                  <td>
                    <el-form-item label="" :prop="'recipients.' + index + '.email'" :rules="[{ required: addWarnConfigForm.sendEmail==1, message: '请输入电子邮箱',trigger:'blur'},{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change']}]">
                      <el-input v-model.trim="item.email" class="w-p100" maxlength="150" clearable placeholder="请输入"></el-input>
                    </el-form-item>
                  </td>
                  <td>
                    <el-button type="text" v-if="addWarnConfigForm.recipients.length>1" icon="el-icon-delete" class="del-btn" @click="delEmp(index)"></el-button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

        </el-form>
      </div>
      <div class="drawer-footer-buts">
        <el-button class="mr-12" size="small" @click="clearAddWarnConfigForm()">取 消</el-button>
        <el-button size="small" type="primary" @click="vaildAddWarnConfigForm()" :loading="addWarnConfigForm.saveLoading">确定</el-button>
      </div>
    </el-drawer>

  </div>
</template>
<script>

  import dTable from '../../components/common/table'
  import { Quill,quillEditor } from 'vue-quill-editor'
  import 'quill/dist/quill.core.css'
  import 'quill/dist/quill.snow.css'
  import 'quill/dist/quill.bubble.css'

  //引入font.css
  import '../../assets/css/font.css'

  // 自定义字体大小
  let Size = Quill.import('attributors/style/size')
  Size.whitelist = ['10px', '12px', '14px', '16px', '18px', '20px']
  Quill.register(Size, true)

  // 自定义字体类型
  var fonts = ['SimSun', 'SimHei', 'Microsoft-YaHei', 'KaiTi', 'FangSong', 'Arial', 'Times-New-Roman', 'sans-serif',
    '宋体', '黑体'
  ]
  var Font = Quill.import('formats/font')
  Font.whitelist = fonts
  Quill.register(Font, true)
  export default {
    components: { dTable, quillEditor },
    data () {
      return {
        pathData: [],
        keyword: '',
        type: '',
        status: null,
        allRoleList: [],
        editorOption: {
          placeholder: "请输入",
          theme: "snow", // or 'bubble'
          modules: {
            toolbar: {
              container: '#toolbar'
            }
          }
        },
        statuses: [
          {code: 1, text: '启动'},
          {code: 0, text: '停止'}
        ],
        setForm: {
          show: false,
          saveLoading: false,
          id: '',
          roleList: []
        },
        addWarnConfigForm: {
          show: false,
          saveLoading: false,
          id: null,
          type: '',
          typeName: '',
          status: 0,
          sendSms: 0,
          smsContent: '',
          sendEmail: 1,
          emailTheme: '',
          emailContent: '',
          cron: '',
          cronComment: '',
          rangeTime: null,
          recipients:[{empName:'', phoneNumber:'', email:''}],
          createId:null,
          createTime: null
        },
        option: {
          warnTypes: []
        }
      }
    },
    computed: {
      tableHeight: function () {
        return this.$global.bodyHeight - 270 + 'px'
      }
    },
    watch: {
      'addWarnConfigForm.emailContent': {
          handler: function() {
              var that = this
              this.$nextTick(() => {
                that.$refs.addWarnConfigForm.validateField('emailContent')
              })
          },
          deep:true
      }
    },
    created () {
      let that = this
      let tabs = this.$store.state.tabs
      if (tabs) {
        this.pathData = this.$global.deepcopyArray(
          tabs[this.$route.meta.parentPath]
        )
      }
      this.pathData.push({ name: '预警配置' })
      this.$nextTick(() => {
        that.getTableData()
      })
      this.getDictByKey('1028', 'warnTypes') // 获取字典值-平台方
    },
    mounted () {
    },
    methods: {
      getTableData () {
        var params = [
          { property: 'searchText', value: this.keyword },
          { property: 'type', value: this.type },
          { property: 'status', value: this.status },
        ]
        this.$refs.table.fetch({
          query: params,
          method: 'post',
          url: '/robot/warn/page'
        })
      },
      search () {
        this.getTableData()
      },
      // 获取字典值
      getDictByKey (key, field) {
        let that = this
        this.$http({
          url: 'robot/data/dict/' + key,
          method: 'post',
          data:{}
        }).then(res => {
          that.option[field] = res.data.data
        }).catch()
      },

      // 启动、重启、停止预警
      updateStatus (id, status) {
        let that = this
        this.$http({
          url: '/robot/warn/updateStatus/' + id + '/' + status,
          method: 'POST',
          data: {}
        }).then(({ data }) => {
          that.$message.success('操作成功')
          that.getTableData()
        }).catch(() => {
        })
      },

      // 新增客户
      clearAddWarnConfigForm (tab) {
        this.addWarnConfigForm = {
          show: false,
          saveLoading: false,
          id: null,
          type: '',
          typeName: '',
          status: 0,
          sendSms: 0,
          smsContent: '',
          sendEmail: 1,
          emailTheme: '',
          emailContent: '',
          cron: '',
          cronComment: '',
          rangeTime: null,
          recipients:[{empName:'', phoneNumber:'', email:''}],
          createId:null,
          createTime: null
        }
        this.$refs.addWarnConfigForm.resetFields()
      },
      handleEdit (row) {
        var that = this
        this.addWarnConfigForm.id = row.id
        this.addWarnConfigForm.type = row.type
        this.addWarnConfigForm.typeName = row.typeName
        this.addWarnConfigForm.status = row.status
        this.addWarnConfigForm.sendSms = row.sendSms
        this.addWarnConfigForm.smsContent = row.smsContent
        this.addWarnConfigForm.sendEmail = row.sendEmail
        this.addWarnConfigForm.emailTheme = row.emailTheme
        this.addWarnConfigForm.emailContent = row.emailContent
        this.addWarnConfigForm.cron = row.cron
        this.addWarnConfigForm.cronComment = row.cronComment
        this.addWarnConfigForm.rangeTime = row.rangeTime
        this.addWarnConfigForm.recipients=[]
        this.addWarnConfigForm.recipients.push(...row.recipients)
        this.addWarnConfigForm.createId = row.createId
        this.addWarnConfigForm.createTime = row.createTime
        this.addWarnConfigForm.show = true
      },
      addEmp () {
        this.addWarnConfigForm.recipients.push({empName:'', phoneNumber:'', email:''});
      },
      delEmp (index) {
        this.addWarnConfigForm.recipients.splice(index,1);
      },
      changeWarnType () {
          if (this.addWarnConfigForm.type) {
            this.addWarnConfigForm.typeName = this.option.warnTypes.filter((item)=>{
              return item.dictCode == this.addWarnConfigForm.type
            })[0].dictName
          } else {
            this.addWarnConfigForm.typeName = ''
          }
      },
      vaildAddWarnConfigForm () {
        var that = this
        this.$refs.addWarnConfigForm.validate((valid) => {
          if (valid) {
            that.addWarnConfigForm.saveLoading = true
            var formData = this.addWarnConfigForm
            that.$http({
              url: '/robot/warn/addOrUpdate',
              method: 'POST',
              data: formData
            }).then(({ data }) => {
              that.addWarnConfigForm.saveLoading = false
              that.$message.success('操作成功')
              that.getTableData()
              that.clearAddWarnConfigForm()
            }).catch(() => {
              that.addWarnConfigForm.saveLoading = false
            })
          }
        })
      },
      handleRecipients (row) {
        var arr = row.recipients ? row.recipients : []
        var res = []
        arr.map(item => {
          var its = []
          its.push(item.empName)
          if (item.phoneNumber) {
            its.push(item.phoneNumber)
          }
          if (item.email) {
            its.push(item.email)
          }
          res.push(its.join('-'))
        })
        return res.join('，')
      }
    }
  }
</script>
<style lang="less" scoped>
  /deep/.el-dialog__header{
    padding:10px 20px;
  }
  /deep/.el-dialog__body{
    padding:10px 10px;
  }
  /deep/.el-form-item__content{
    line-height: 0px;
  }

  /deep/.el-form-item{
    margin-bottom: 10px;
  }

  /deep/.spl-filter-drawer{
    width: 600px !important;
  }

  .emp-table{
    width: 100%;
  }
  .del-btn {
    color: #F91317 !important;
    margin-top: -5px;
  }
  /deep/.ql-editor{
    height: 200px !important;
    overflow-y: auto;
  }
</style>
