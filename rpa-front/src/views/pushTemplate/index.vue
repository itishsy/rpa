<template>
  <div class="content spl-container">
    <!-- 导航 -->
    <!-- <breadcrumb :data="pathData"> -->
      <!--<el-button
        type="plain"
        slot="breadcrumb-btn"
        style="color:#3E82FF;border-color:#3E82FF;"
        size="mini"
        @click="goDetail"
      >费用明细</el-button>-->
    <!-- </breadcrumb> -->
    <div style="padding: 0px 0px;">
      <div>
        <div class="search-row clearfix">
          <el-row>
            <el-col :span="6" class="display-flex">
              <span class="label" style="white-space:nowrap">客户：</span>
                <el-select
                v-model="formData.custId"
                class="search-row-item"
                clearable
                filterable
                placeholder="请选择客户"
              >
              <el-option
                v-for="item in customList"
                :key="item.id"
                :label="item.customerName"
                :value="item.id"
              ></el-option>
            </el-select>
            </el-col>
            <el-col :span="12" class="display-flex" style="height: 30px;">
              <span class="label" style="white-space:nowrap;margin-left: 30px;">网站：</span>
              <el-input
                style="width: 215px;"
                v-model="formData.netAddress"
                clearable
                placeholder="请输入网站"
              ></el-input>
            </el-col>
            <!-- <el-col :span="7" class="display-flex" style="height: 30px;">
              <span class="label" style="white-space:nowrap;margin-left:30px;">手机号码：</span>
              <el-input
                style="width: 215px;"
                v-model="formData.recipientPhoneNumber"
                clearable
                placeholder="请输入手机号码"
              ></el-input>
            </el-col> -->
            <el-col :span="6" class="text-right">
              <el-button type="primary" class="search-btn w-60" @click="querySearch">搜索</el-button>
              <el-button type="primary" @click="addPushTemplate">添加推送</el-button>
              <el-button class="ml-15" size="small" type="primary" @click="configMsgTemplate">短信模板配置</el-button>
            </el-col>
          </el-row>
        </div>
        <div>
          <dTable
            ref="tableList"
            @fetch="getTableList"
            :tableHeight="tableHeight"
            :paging="true"
            :showIndex="true"
            rowKey="itemId"
          >
            <u-table-column
              prop="appName"
              label="任务名称"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="netAddress"
              label="应用-网址"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <a class="netAddress" :href="getNetAddress(scope.row.netAddress)" target="_blank">{{scope.row.netAddress}}</a>
              </template>
            </u-table-column>
            <u-table-column
              prop="validateType"
              label="验证方式"
              min-width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <span>{{getValidateType(scope.row.validateType)}}</span>
              </template>
            </u-table-column>
            <u-table-column
              prop="fileName"
              label="语音电话是否通知"
              :show-overflow-tooltip="true"
              min-width="150"
            >
              <template slot-scope="scope">
                <span>{{scope.row.voiceReminder ? '是' : '否'}}</span>
              </template>
            </u-table-column>
            <u-table-column fixed="right" label="操作" width="250">
              <template slot-scope="scope">
                <el-button
                  type="text"
                  size="small"
                  @click="toDetail(scope.row)"
                  class="emp-opt-btn"
                >详情</el-button>
                <el-button
                  type="text"
                  size="small"
                  @click="eidtTemplate(scope.row)"
                  class="emp-opt-btn"
                >编辑</el-button>
              </template>
            </u-table-column>
          </dTable>
        </div>
      </div>
    </div>

    <el-drawer
      :title="review ? '查看推送模板' : detailData.id ? '编辑推送模板' : '添加推送模板'"
      :visible.sync="show"
      :wrapperClosable="false"
      custom-class="spl-filter-drawer add-drawer"
      :show-close="true"
      :before-close="doClose"
      size="20%"
    >
      <div>
        <div class="drawerForm" style="padding: 15px 20px 20px 20px">
          <el-form :inline="true" :model="detailData" ref="ruleForm" label-width="150px" :rules="rules" v-if="!review">
            <div style="padding: 10px">
              <el-form-item :prop="item.prop" :label="item.label + '：'" v-for="item in formList" :key="item.label" v-if="getShow(item.show)">
                <el-select style="width: 250px;" v-model="detailData[item.prop]" placeholder="请选择" v-if="item.type == 'select'" filterable clearable :ref="item.ref">
                  <el-option
                    v-for="item1 in item.options"
                    :key="item1[getSelectProp(item.prop).key]"
                    :label="item1[getSelectProp(item.prop).label]"
                    :value="item1[getSelectProp(item.prop).value]"
                  ></el-option>
                </el-select>
								<el-input style="width: 250px;" v-model="detailData[item.prop]" placeholder="请输入" v-if="item.type == 'input'" clearable></el-input>
                <el-input style="width: 250px;" v-model="detailData[item.prop]" placeholder="请输入" type="textarea" :rows="3" v-if="item.type == 'textarea'" clearable></el-input>
              </el-form-item>
            </div>
          </el-form>
          <el-form :model="detailData" ref="ruleForm2" label-width="150px" v-if="review">
            <div style="padding: 10px">
              <el-form-item :prop="item.prop" :label="item.label + '：'" v-for="item in formList" :key="item.label" v-if="getShow(item.show)">
                <div style="white-space: pre-line;"><span>{{getKeyValue(item)}}</span></div>
              </el-form-item>
            </div>
          </el-form>
        </div>
        <div class="drawer-footer-buts">
          <el-button class="btn--border-blue s-btn mr-0" @click="doClose">取消</el-button>
          <el-button type="primary" class="s-btn ml-12" @click="handleValid" v-if="!review">确认添加</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import dTable from '../../components/common/table'
export default {
  components: { dTable },
  data() {
    return {
      formData: {
        accountNumber:'',
        netAddress:'',
        recipientPhoneNumber:'',
        custName:"",
        custId:""
      },
      listData: [],
      accountList:[],
      pathData: [{ name: '机器人管理' }, { name: '登录推送模板' }],
      show: false,
      detailData: {},
      options: [],
      customList:[],
      formList: [
				{label:'选择推送应用',type:'select',required:true,message:'请选择推送应用',prop:'appCode',show:"",options:[],ref:'appName'},
				{label:'输入推送网站',type:'input',required:true,message:'请输入推送网站',prop:'netAddress',show:""},
				{label:'选择推送方式',type:'select',required:true,message:'请选择推送方式',prop:'validateType',show:"",options:[],defaultValue:'10021001'},
				{label:'是否需要语音提醒',type:'select',required:true,message:'请选择是否需要语音提醒',prop:'voiceReminder',show:"",options:[{label:'是',value:1},{label:'否',value:0}],defaultValue:0},
				{label:'选择语音模板',type:'select',required:true,message:'请选择语音模板',prop:'voiceTempId',show:'this.detailData.voiceReminder == "1"',options:[]},
        {label:'是否需要短信提醒',type:'select',required:true,message:'请选择是否需要短信提醒',prop:'smsReminder',show:"",options:[{label:'是',value:1},{label:'否',value:0}],defaultValue:0},
				{label:'选择短信模板',type:'select',required:true,message:'请选择短信模板',prop:'smsTempId',show:'this.detailData.smsReminder == "1"',options:[]},
				{label:'系统类型',type:'select',required:true,message:'请选择系统类型',prop:'tplTypeCode',show:'',options:[]},
				{label:'等待时长(秒)',type:'input',required:true,message:'请输入等待时长',prop:'singleAgeing',show:'',defaultValue:60},
				// {label:'tips',type:'textarea',required:true,message:'请输入tips',prop:'tips',show:''},
				// {label:'选择扫码方式',type:'select',required:true,message:'请选择扫码方式',prop:'i',show:'this.detailData.validateType == "10021002"',options:[]},
				{label:'链接有效时长(秒)',type:'input',required:true,message:'请输入链接有效时长',prop:'linkAgeing',show:'',defaultValue:60},
				{label:'关联客户',type:'aaa',required:false,message:'无',prop:'customerList',show:'this.review'},
			],
      formSelect:{
        validateType:{
          label:"dictName",
          value:"dictCode",
          key:'dictCode'
        },
        appCode:{
          label:"appName",
          value:"appCode",
          key:'appCode'
        },
        tplTypeCode:{
          label:'dictName',
          value:'dictCode',
          key:'dictCode'
        },
        voiceTempId:{
          label:'name',
          value:'id',
          key:'id'
        },
        smsTempId:{
          label:'name',
          value:'id',
          key:'id'
        }
      },
			rules:{
			  appCode:[{ required: true, message: '请选择推送应用', trigger: 'change' }],
			  netAddress:[
          { required: true, message: '请输入推送网站', trigger: 'change' },
          { required: true, trigger: 'change',validator:this.checkLength(100)},
          { required: true, trigger: 'change',validator:this.checkNetAddress}
        ],
				validateType:[{ required: true, message: '请选择推送方式', trigger: 'change' }],
				voiceReminder:[{ required: true, message: '请选择是否需要语音提醒', trigger: 'change' }],
				smsReminder:[{ required: true, message: '请选择是否需要短信提醒', trigger: 'change' }],
				voiceTempId:[{ required: true, message: '请选择语音模板', trigger: 'change' }],
				smsTempId:[{ required: true, message: '请选择短信模板', trigger: 'change' }],
        tplTypeCode:[{ required: true, message: '请选择系统类型', trigger: 'change' }],
				singleAgeing:[
          { required: true, message: '请输入等待时长', trigger: 'change' },
          { required: true, trigger: 'change',validator:this.checkNumber},
          { required: true, trigger: 'change',validator:this.checkSecond}
        ],
				tips:[
          { required: true, message: '请输入tips', trigger: 'change' },
          { required: true, trigger: 'change',validator:this.checkLength(300)}
        ],
				i:[{ required: true, message: '请选择扫码方式', trigger: 'change' }],
				linkAgeing:[
          { required: true, message: '请输入链接有效时长', trigger: 'change' },
          { required: true, trigger: 'change',validator:this.checkNumber},
          { required: true, trigger: 'change',validator:this.checkSecond}
        ],
			},
      appList:[],
      loading:null,
      review:false
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 340 + 'px'
    },
    getSelectProp(){
      return function(key){
        if(!this.formSelect[key]){
          return {
            label:'label',
            value:'value',
            key:'value'
          }
        }
        return this.formSelect[key]
      }
    },
    getShow(){
      return function(str){
        if(!str){
          return true
        }
        return eval(str)
      }
    },
    getValidateType(){
      return function(key){
        var str = ''
        this.formList[2].options.forEach(item=>{
          if(key == item.dictCode){
            str = item.dictName
          }
        })
        return str
      }
    },
    getKeyValue(){
      return function(item){
        var str = this.detailData[item.prop]
        var prop = item.prop
        if(prop == 'appCode'){
          return this.detailData.appName
        }
        if(prop == 'validateType'){
          return this.getValidateType(str)
        }
        if(prop == 'voiceReminder' || prop == 'smsReminder'){
          return str == '1' ? '是' : '否'
        }
        if(prop == 'voiceTempId'){
          let val = ''
          item.options.forEach(item1=>{
            if(item1.id == str){
              val = item1.content
            }
          })
          return val
        }
        if(prop == 'smsTempId'){
          let val = ''
          item.options.forEach(item1=>{
            if(item1.id == str){
              val = item1.content
            }
          })
          return val
        }
        if(prop == 'tplTypeCode'){
          let val = ''
          item.options.forEach(item1=>{
            if(item1.dictCode == str){
              val = item1.dictName
            }
          })
          return val
        }
        if(prop == 'customerList'){
          let val = ''
          if(this.detailData[prop] && this.detailData[prop].length > 0){
            this.detailData[prop].forEach(item=>{
              val += item.name + '\n'
            })
          }
          return val
        }
        return str
      }
    },
    getNetAddress(){
      return function(val){
        var reg = /^(http|https)/
        return reg.test(val) ? val : `//${val}`
      }
    }
  },
	created(){
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({name: '登录推送模板'})
		this.resetDetail() // 重置detailData
    // this.getAccountList() //获取申报账户
    this.getDictByKey(10021,'validateType') //获取字典值
    this.getDictByKey(10007,'tplTypeCode') //获取字典值
    this.getDictByKey(10008,'tplTypeCode') //获取字典值
    this.getNoticeTemplate(1) //获取短信模板
    this.getNoticeTemplate(2) //获取短信模板
    this.getlistDeclareAccount() //获取应用
    this.getlistCustomer() //获取客户
	},
  mounted(){
    this.getTableList()
  },
  methods: {
    checkNetAddress(rule,value,callback){
      var reg = /^(?:http(s)?:\/\/)?[\w.-]+(?:\.[\w\.-]+)+[\w\-\._~:/?#[\]@!\$&'\*\+,;=.]+$/
      // var reg = /^(http(s)?:\/\/)?([0-9a-z-]{1,}.)?[0-9a-z-]{2,}.([0-9a-z-]{2,}.)?[a-z]{2,}$/g
      if(!value){
        return callback()
      }
      reg.lastIndex = 0
      if(!reg.test(value)){
        return callback(new Error('请输入正确的网站'))
      }
      return callback()
    },
    checkSecond(rule,value,callback){
      if(!value){
        return callback()
      }
      if(value > 86400){
        return callback(new Error('不得超过86400s'))
      }
      return callback()
    },
    checkLength(length){
      var length = length ? length : 300
      return function(rule,value,callback){
          if(!value){
          return callback()
        }
        if(value.length > length){
          return callback(new Error(`不得超过${length}字符`))
        }
        return callback()
      }
    },
    checkNumber(rule,value,callback){
      var reg = /^[1-9]*[1-9][0-9]*$/g
      if(!value){
        return callback(new Error('请输入等待时长'))
      }
      reg.lastIndex = 0
      if(!reg.test(value)){
        return callback(new Error('请输入>0的整数'))
      }
      return callback()
    },
    querySearch() {
      this.getTableList()
    },
    getTableList() {
      var params = [
        // { property: 'accountNumber', value: this.formData.accountNumber },
        { property: 'custId', value: this.formData.custId },
        { property: 'netAddress', value: this.formData.netAddress },
        // { property: 'recipientPhoneNumber', value: this.formData.recipientPhoneNumber },
      ]
      this.$refs.tableList.fetch({
        query: params,
        method: 'post',
        url: '/policy/sys/robot/loginNoticeTemplate/page',
      })
    },
    addPushTemplate() {
      this.resetDetail()
			this.show = true
      this.review = false
      console.log(this.detailData)
		},
    toDetail(row) {
      this.loginNoticeTemplateById(row.id,'review')
      // this.detailData = this.deepCopy(row)
      // this.show = true
      // this.review = true
    },
    eidtTemplate(row) {
      // this.detailData = this.deepCopy(row)
      this.loginNoticeTemplateById(row.id,'edit')
    },
    doClose() {
			this.resetDetail()
      this.$nextTick(()=>{
        if(this.review){
			  this.$refs.ruleForm2.clearValidate()
        }else{
			    this.$refs.ruleForm.clearValidate()
        }
				this.show = false
      })
		},
    handleValid() {
			this.$refs.ruleForm.validate(valid=>{
				if(valid){
          this.$nextTick(item=>{
            this.saveTemp()
          })
				}
			})
		},
		resetDetail(){
      this.detailData = {}
			this.formList.forEach(item=>{
        if(item.defaultValue !== undefined && item.defaultValue !== ''){
          this.$set(this.detailData,item.prop,item.defaultValue)
          return
        }
        this.$set(this.detailData,item.prop,'')
			})
      this.$nextTick(item=>{
        if(this.$refs.ruleForm){
          this.$refs.ruleForm.clearValidate()
        }
      })
		},
    // 获取申报账户
    getAccountList() {
      let that = this
      this.$http({
        url: '/policy/declareAccount/seeMainBody',
        method: 'post',
        params: {
        },
      })
        .then(({ data }) => {
          that.accountList = data.data
        })
        .catch((data) => {})
    },
    // 获取字典值
    getDictByKey (dictCode,key) {
      let that = this
      this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: dictCode
        }
      }).then(res => {
        this.formList.forEach(item=>{
          if(item.prop == key){
            item.options.push(...res.data.data)
          }
        })
      }).catch()
    },
    getNoticeTemplate(type){
      this.$http({
        url: `policy/sys/robot/noticeTemplate/getNoticeTemplate/${type}`,
        method: 'post',
      }).then(res => {
        if(res.data.code == 200){
          this.formList.forEach(item1=>{
            if(item1.label == '选择语音模板' && type == '2'){
              item1.options = res.data.data
            }
            if(item1.label == '选择短信模板' && type == '1'){
              item1.options = res.data.data
            }
          })
        }
      }).catch()
    },
    getlistDeclareAccount() {
      let that = this
      this.isLoading = true
      this.$http({
        url: '/robot/app/list',
        method: 'post',
      })
        .then(({ data }) => {
          let res = data.data;
          // var arr = []
          that.appList = res
          this.formList[0].options = res
        })
        .catch((data) => {})
    },
    saveTemp(){
      this.showLoading()
      this.detailData.appName = this.$refs.appName[0].selectedLabel
      this.$http({
        url: 'policy/sys/robot/loginNoticeTemplate/saveTemp',
        method: 'post',
        data:this.detailData
      })
        .then(({ data }) => {
          this.closeLoading()
          if(data.code == 200){
            this.$message.success(data.message ? data.message :"操作成功")
            this.show = false
            this.getTableList()
          }else{
            this.$message.error(data.message ? data.message :"操作失败")
          }
        })
        .catch((err) => {
          this.closeLoading()
        })
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
    loginNoticeTemplateById(id,type){
      this.showLoading()
      this.$http({
        url: 'policy/sys/robot/loginNoticeTemplate/findById/' + id,
        method: 'post',
      })
        .then(({ data }) => {
          this.closeLoading()
          if(data.code == 200){
            this.detailData = this.deepCopy(data.data)
            this.show = true
            if(type == 'review'){
              this.review = true
              this.$nextTick(()=>{
                this.$refs.ruleForm2.clearValidate()
              })
            }else{
              this.$nextTick(()=>{
                this.$refs.ruleForm.clearValidate()
              })
              this.review = false
            }
          }else{
            this.$message.error(data.message ? data.message :"获取失败")
          }
        })
        .catch((err) => {
          this.closeLoading()
        })
    },
    getlistCustomer() {
      this.$http({
        url: '/robot/app/client/listCustomer',
        method: 'post',
      })
        .then((data) => {
          // console.log('data', data)
          this.customList = data.data.data
        })
        .catch((err) => {})
    },
    //配置短信模板
    configMsgTemplate() {
        this.$router.push('/loginPush/textMessageTemplate')
    }
  },
}
</script>
<style lang="less" scoped>
.netAddress{
  text-decoration: underline;
  color:#3E82FF;
}
</style>
