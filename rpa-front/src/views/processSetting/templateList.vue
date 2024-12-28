<template>
  <!-- 流程模板列表 -->
  <div class="spl-container">
    <breadcrumb :data="pathData">
      <template slot="breadcrumb-btn">
        <el-button v-if="$global.hasPermission('syncOutToProd')" type="primary" size="mini" slot="breadcrumb-btn" @click="syncOut()">同步至生产</el-button>
        <el-button type="primary" size="mini" slot="breadcrumb-btn" @click="addProcess()">添加流程模板</el-button>
      </template>
    </breadcrumb>
    <div class="pl-20 pr-20">
    </div>
    <div class="table-box">
      <el-form ref="tableForm" :model="table" label-width="200px" :rules="rules">
        <el-table
          :data="table.tableData"
          border
          header-cell-class-name="table-header"
          style="width: 100%"
          :max-height="tableHeight"
          ref="table"
        >
          <el-table-column
            fixed="left"
            label="执行顺序"
            width="100"
            align="center"
            prop="execOrder"
          >
            <template scope="scope"
              ><span>{{ scope.row.execOrder }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="flowType" label="流程类型" width="150">
            <template scope="scope">
              <span>{{ getFlowType(scope.row.flowType) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="flowName" label="流程名称" width="280"></el-table-column>
          <el-table-column prop="createTime" label="创建时间" min-width="150">
            <template scope="scope">
              <span>{{ formatDateTime(scope.row.createTime,'y-M-d-h-m-s','-')}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="control" label="操作" min-width="150">
            <template scope="scope">
              <div style="display: flex">
                <div
                  class="control-btn"
                  style="color: #6b6b6b"
                  @click="moveDown(scope.row, scope.$index)"
                  v-if="scope.$index != table.tableData.length - 1"
                >
                  下移
                </div>
                <div
                  class="control-btn"
                  style="color: #6b6b6b"
                  @click="moveUp(scope.row, scope.$index)"
                  v-if="scope.$index != 0"
                >
                  上移
                </div>
                <div
                  class="control-btn"
                  style="color: #3e82ff"
                  @click="edit(scope.row, scope.$index)"
                >
                  编辑
                </div>
                <div
                  class="control-btn"
                  style="color: #3e82ff"
                  @click="goUrl(scope.row, scope.$index)"
                >
                  步骤设置
                </div>
                <div
                  class="control-btn"
                  style="color: #3e82ff"
                  @click="goFlowUrl(scope.row, scope.$index)"
                >
                  流程设置
                </div>
                <!--<div
                  class="control-btn"
                  style="color: red"
                  @click="remove(scope.row, scope.$index)"
                >
                  删除
                </div>-->
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
    </div>
    <!-- 添加流程 -->
    <el-dialog
      :title="tableEdit == 'edit' ? '编辑流程': '添加流程'"
      :visible.sync="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
      :before-close="cancel">
      <el-form :model="formData" :rules="rules" ref="addProcess" label-width="100px" class="cust-dialog">
        <div>
          <el-form-item label="添加方式：" prop="bussinssType" style="width:80%;">
            <el-radio-group v-model="formData.bussinssType" @change="changeBussinssType" :disabled="disabledBussinssType">
              <el-radio label="add">自定义</el-radio>
              <el-radio label="copyAdd">复制现有的</el-radio>
              <!--<el-radio label="relateAdd">关联已有</el-radio>-->
            </el-radio-group>
          </el-form-item>
          <el-form-item label="流程类型：" prop="flowType" style="width:80%;" v-if="formData.bussinssType == 'add' || tableEdit == 'edit' ">
            <el-radio-group v-model="formData.flowType" @change="changeFlowType">
              <el-radio label="1">主流程</el-radio>
              <el-radio label="2">子流程</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="processTypeName" prop="processTypeName" style="width:80%;" v-if="formData.bussinssType != 'add' && tableEdit != 'edit'">
            <el-select v-model="formData.processObj" placeholder="请选择" style="width:100%;" value-key="id" @change="changeProcess" filterable>
              <el-option :label="item.appNameAndFlowName" :value="item" v-for="(item,index) in processOption" :key="item.id" ></el-option>
            </el-select>
            <span style="display: block;font-size: 12px;color: #999;line-height: 20px;">{{processTips}}</span>
          </el-form-item>
          <el-form-item label="流程名称：" prop="processName" style="width:80%;">
            <el-input v-model="formData.processName" placeholder="请输入内容" v-filter-special-char></el-input>
          </el-form-item>
        </div>
      </el-form>
      <span slot="footer" class="dialog-footer" >
        <el-button @click="cancel" size="small" class="w-60">取 消</el-button>
        <el-button type="primary" @click="confirm" size="small" class="w-60">确定</el-button>
      </span>
    </el-dialog>

    <!-- 同步至生产 -->
    <el-dialog title="同步至生产" :visible.sync="dialogVisibleSyncOut" width="600px" :before-close="cancelSyncOut" :close-on-click-modal="false">
      <div id="upload-dialog">
        <div class="file-content-box">
          <el-form :model="formData2" :rules="rules2" ref="newVersionRuleSyncOutForm" label-width="100px" class="demo-ruleForm">
            <div class="upload-file-box">
              <div>
                <el-form-item prop="newVersionText" label-width="150" style="margin-bottom:0;" label="同步内容：">
                  <el-input type="textarea" :rows="8" placeholder="请输入本次同步更新的内容" style="width:100%" v-model="formData2.newVersionText"></el-input>
                </el-form-item>
                <div style="text-align:right;">{{formData2.newVersionText.length}} / 500</div>
              </div>
            </div>
          </el-form>
          <div class="footer-btn-box">
            <el-button @click="cancelSyncOut" class="footer-btn">取  消</el-button>
            <el-button @click="confirmSyncOut" class="ml-20 footer-btn" type="primary"  >确定同步</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  data() {
    return {
      pathData: [],
      table: {
        tableData: [],
      },
      rules: {
        processName:[
          { required: true, message: '请输入流程名称', trigger: 'change'},
          { required: true, trigger: 'change',validator:this.checkProcessName},
        ],
        bussinssType:[
          { required: true, message: '请选择添加方式', trigger: 'change'},
        ],
        processTypeName:[
          { required: true,  trigger: 'change',validator:this.checkProcessTypeName},
        ],
        flowType:[
          { required: true, message: '请选择流程类型', trigger: 'change'},
        ]
      },
      rules2: {
          newVersionText:[
              { required: true, message: '请输入本次发布更新的内容', trigger: 'change' },
              { min: 0, max: 500, message: '更新内容不能大于500字', trigger: 'change' }
          ],
      },
      formData: {
        bussinssType:"add",
        processName:"",
        processTypeName:'',
        flowType:"1",
        processObj:{},
        flowCode:''
      },
      formData2:{
          newVersionText:'',
      },
      tableIndex:0,
      loading:null,
      uploadErrMsg:'',
      dialogVisible:false,
      dialogVisibleSyncOut: false,
      processTypeName:'现有流程：',
      processTips:"说明：复制已有是通过现有的地区快速创建事项和操作流程",
      appCode:'',
      appInfo: {},
      processOption:[],
      disabledBussinssType:false,
      tableEdit:''
    }
  },
  computed:{
    tableHeight(){
      return this.$global.bodyHeight - 200 + 'px'
    },
    //业务类型
    bussinssType(){
      return function(val){
        var arr = ['社保','公积金']
        if(val != '' || val != null){
          return arr[val - 1]
        }else{
          return '暂无'
        }
      }
    },
    getFlowType(){
      return function(val){
        if(val == 1){
          return '主流程'
        }else if(val == 2){
          return '子流程'
        }else {
          return ''
        }
      }
    },
    //业务类型
    changeType(){
      return function(val){
        var arr = ['投保','停保','调整','变更','补缴']
        if(val != '' || val != null){
          return arr[val - 1]
        }else{
          return '暂无'
        }
      }
    }
  },
  created() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({name: '流程模板列表'})
    if(this.$route.query.appCode){
      this.appCode = this.$route.query.appCode
    }
    // this.getProcessInfo()
    // this.getAllFlow()
    // this.getAppInfo()

    //获取流程模板列表
    this.getTemplateList()
  },
  methods: {
    changeFlowType(val){

    },
    //获取应用详情
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
          that.pathData[that.pathData.length - 1].name = 'RPA流程配置 （' + that.appInfo.appName + '）'
        }
      }).catch(() => {
      })
    },
    getProcessInfo(){
      this.showLoading()
      this.$http({
        url:'robot/flow/list',
        method:'post',
        params:{
          appCode:this.appCode,
        },
      }).then(res=>{
        this.closeLoading()
        res.data.data.sort((a,b)=>{
          return a.execOrder - b.execOrder
        })
        this.table.tableData = res.data.data
      }).catch(err=>{
        this.closeLoading()
      })
    },
    checkProcessTypeName(rules,value,callback){
      if(!value){
        return callback(new Error('请选择' + this.processTypeName.replace('：','')))
      }
      return callback()
    },
    checkProcessName(rules,value,callback){
      var check = false
      this.table.tableData.forEach(item=>{
        if(value == item.flowName && !item.self){
          check = true
        }
      })
      if(check){
        return callback('流程名称不能重复')
      }
      return callback()
    },
    changeProcess(item){
      this.formData.processTypeName = this.formData.processObj.appNameAndFlowName
    },
    getAllFlow(appCode){
      var url = appCode ? 'robot/flow/showOneAppFlow?appCode=' + appCode : 'robot/flow/showOneAppFlow'
      this.$http({
        url:url,
        method:'post',
      }).then(res=>{
        this.closeLoading()
        this.processOption = res.data.data
      }).catch(err=>{
        this.closeLoading()
      })
    },
    //流程添加方式改变
    changeBussinssType(val){
      if(val == 'copyAdd'){
        this.processTypeName = '现有流程：'
        this.processTips = '说明：复制已有是通过现有的地区快速创建事项和操作流程'
        // this.getAllFlow()
      }else if(val == 'relateAdd'){
        this.processTypeName = '关联流程：'
        this.processTips = '说明：关联已有多申报帐户使用相同事项和操作流程'
        // this.getAllFlow(this.appCode)
      }
      // this.formData.processTypeName = ''
      this.$refs.addProcess.clearValidate(['processTypeName'])
    },
    //同步至生产弹窗
    syncOut(){
        this.dialogVisibleSyncOut = true
    },
    //取消同步至生产
    cancelSyncOut(){
        this.dialogVisibleSyncOut = false;
        this.formData2.newVersionText = ""
        this.$nextTick(()=>{
            this.$refs.newVersionRuleSyncOutForm.clearValidate()
        })
    },
    //确定同步至生产
    confirmSyncOut() {
        this.$refs.newVersionRuleSyncOutForm.validate((valid)=>{
            if(valid){
                this.showLoading()
                this.$http({
                    url: 'robot/flowDesign/syncOut?comment='+this.formData2.newVersionText,
                    method: 'post',
                }).then(({data}) => {
                    this.closeLoading()
                    this.$message.success('同步成功')
                    this.cancelSyncOut()
                }).catch(err=>{
                    this.closeLoading()
                })
            }
        })
    },
    //添加流程
    addProcess(){
      this.dialogVisible = true
    },
    //取消添加流程
    cancel(){
      this.dialogVisible = false
      this.tableEdit = ''
      this.disabledBussinssType = false
      this.table.tableData.forEach(item=>{
          item.self = false
      })
      this.formData = {
        bussinssType:"add",
        processName:"",
        processTypeName:'',
        flowType:"1",
        processObj:{},
        flowCode:''
      }
      this.$refs.addProcess.resetFields()
    },
    //确定添加流程
    confirm(type){
      this.$refs.addProcess.validate((valid)=>{
        console.log(valid)
        if(valid){
          console.log('添加流程')
          var obj = {}
          if(this.tableEdit == 'edit'){
            obj = {
              appCode:this.appCode,
              flowName:this.formData.processName,
              flowType:this.formData.flowType,
              flowCode:this.formData.flowCode,
              id:this.formData.id
            }
            this.showLoading()
            this.$http({
              url:'robot/flow/editFlow',
              method:'post',
              data:obj
            }).then(res=>{
              this.closeLoading()
              if(res.data.code == 200){
                // this.getProcessInfo()
                this.getTemplateList()
                this.cancel()
                // this.getAllFlow()
                this.$message.success('操作成功')
              }
            }).catch(err=>{
              this.closeLoading()
              this.$message.error('接口错误，请稍后再试')
            })
            return
          }else if(this.formData.bussinssType =='add'){
            obj = {
              appCode:this.appCode,
              flowName:this.formData.processName,
              flowType:this.formData.flowType
            }
          }else if(this.formData.bussinssType == 'copyAdd'){
            obj = {
              appCode:this.appCode,
              flowName:this.formData.processName,
              flowCode:this.formData.processObj.flowCode,
              flowType:''
            }
          }else if(this.formData.bussinssType == 'relateAdd'){
            obj = {
              appCode:this.appCode,
              flowCode:this.formData.processObj.flowCode,
              flowName:this.formData.processName,
              flowType:''
            }
          }

          this.showLoading()
          //obj 增加templateType 默认为1
          obj.templateType = 1
          this.$http({
            url:'robot/flow/' + this.formData.bussinssType,
            method:'post',
            params:obj
          }).then(res=>{
            this.closeLoading()
            // this.getProcessInfo()
            this.getTemplateList()
            this.cancel()
            // this.getAllFlow()
            this.getTemplateList()
            this.tableEdit = ''
          }).catch(err=>{
            this.closeLoading()
          })
        }else{
          console.log(valid)
        }
      })
    },
    //操作栏，删除
    remove(row, index) {
      this.$confirm('是否确认删除该流程？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      }).then(() => {
          // this.table.tableData.splice(index, 1)
          this.showLoading()
          this.$http({
            url:'robot/flow/delete',
            method:'post',
            params:{
              flowCode:row.flowCode
            }
          }).then(res=>{
            this.closeLoading()
            // this.getProcessInfo()
            this.getTemplateList()
            this.$message.success(res.message ? res.message : '操作成功')
          }).catch(err=>{
            this.closeLoading()
          })
      }).catch(() => {})
    },
    //操作栏，上挪
    moveUp(row, index) {
      this.$nextTick(() => {
        this.$refs.table.doLayout()
        this.showLoading()
        this.$http({
          url:'robot/flow/update',
          method:'post',
          params:{
            appCode:row.appCode,
            flowCode:row.flowCode,
            flag:1,
            flowName:row.flowName
          }
        }).then(res=>{
          this.closeLoading()
          if(res.data.code != 200){
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
        }).catch(err=>{
          this.closeLoading()
        })
      })
    },
    //操作栏，下移
    moveDown(row, index) {
      this.$nextTick(() => {
        this.showLoading()
        this.$refs.table.doLayout()
        this.$http({
          url:'robot/flow/update',
          method:'post',
          params:{
            appCode:row.appCode,
            flowCode:row.flowCode,
            flag:2,
            flowName:row.flowName
          }
        }).then(res=>{
          this.closeLoading()
          if(res.data.code != 200){
            return
          }
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
        }).catch(err=>{
          this.closeLoading()
        })
      })
    },
    //编辑
    edit(row,index){
      this.formData.processName = row. flowName
      this.formData.flowType = row.flowType + ''
      this.formData.bussinssType = row.addType ? row.addType : 'add'
      this.formData.flowCode = row.flowCode
      this.formData.id = row.id
      this.disabledBussinssType = true
      this.dialogVisible = true
      this.tableEdit = 'edit'
      row.self = true
    },
    //操作栏步骤设置
    goUrl(row, index) {
      this.$router.push('/processSetting/stepSetting?flowCode=' + row.flowCode+'&appCode=' + row.appCode+'&id=' + row.id)
    },
    //流程设置
    goFlowUrl(row, index) {
      console.log('row------->',row)
      this.$router.push('/processSetting/flow?flowCode=' + row.flowCode+'&id='
      + row.id+ '&flowName='+row.flowName + '&template=1')
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
    showLoading(target){
      this.loading = this.$loading({
        target:target? target : document.body,
        lock: true,
        text: '加载中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      });
    },
    closeLoading(){
      if(this.loading && this.loading.close){
        this.loading.close()
      }
    },
    //设置时间,时间转换
    formatDateTime(date, params, separator) {
        if (date == undefined || date == null || date == '') {
            return ''
        }
        var date = new Date(date.substring(0, 19));
        var Year = date.getFullYear();
        var Month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
        var d = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
        var Hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
        var Minutes = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
        var Seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
        var over_time = Year + "/" + Month + "/" + d + " " + Hours + ":" + Minutes + ":" + Seconds
        //***至此以上是将时间2020-03-18T01:57:23.000+0000转为正常时间格式，以下为将时间进行增加8小时解决时区差异的操作***
        var time = new Date(over_time);
        time.setTime(time.setHours(time.getHours()));
        var y = time.getFullYear() ;
        var m =this.addZero(time.getMonth() + 1) ;
        var d =this.addZero(time.getDate());
        var h = this.addZero(time.getHours() + 8);
        var minute =this.addZero(time.getMinutes());
        var second =this.addZero(time.getSeconds());
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
    addZero(num) {
      return num < 10 ? '0' + num : num;
    },
    //获取流程模板列表
    async getTemplateList(){
      const {data} = await this.$http({
        url:'robot/flow/listTemplate',
        params: {
          templateType: 1
        },
        method:'post',
      })
      console.log('流程模板列表 -------------',data)
      this.table.tableData = data.data
    }
  },
}
</script>

<style lang="less" scoped>
.search-row {
  padding: 10px;
}
.table-box {
  padding: 20px;
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
.output-content {
  text-align: center;
  width: 100%;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  padding: 0 10px;
}
.edit-icon {
  position: absolute;
  top: 50%;
  right: 10px;
  transform: translateY(-50%);
  cursor: pointer;
}
.header-box {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
  background: #3e82ff;
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

.block{
  background: rgb(0, 102, 255);
  width: 5px;
  height: 15px;
  display: inline-block;
  margin-right: 3px;
}
// .dialog-footer{
//   // display: flex;
//   // justify-content: center;
//   // margin-top:20px;
//   // text-align: right;
// }
.upload-file-box{
  // margin-top:10px;
  input[type=text]{
    border: 1px solid #ddd;
    border-width: 1px;
    height: 26px;
    width: 285px;
    // position: absolute;
    // left: 50%;
    // transform: translateX(-50%);
    cursor: pointer;
    border-radius: 5px;
    box-sizing: border-box;
    padding:0 10px;
    &:focus{
      outline: none;
    }
  }
  .upload-file-btn{
    height: 30px;
    background: #3e82ff;
    color: white;
    padding:0 10px;
    box-sizing: border-box;
    font-size: 12px;
    cursor: pointer;
    line-height: 30px;
    margin-left:10px;
    display: inline-block;
  }
  .upload-file{
    padding: 4px 10px;
    height: 30px;
    line-height: 20px;
    cursor: pointer;
    color: #888;
    background: #fafafa;
    border: 1px solid #ddd;
    display: inline-block;
    box-sizing: border-box;
    position: absolute;
    opacity: 0;
    width: 280px;
    left: 50%;
    transform: translateX(-50%);
    display: none;
    input[type=file] {
      position: absolute;
      font-size: 18px;
      right: 0;
      top: 0;
      opacity: 0;
      cursor: pointer;
      width:280px;
      &:hover {
        color: #444;
        background: #eee;
        border-color: #ccc;
        text-decoration: none
      }
    }
  }
}
.icon-query{
  position: absolute;
  right: 15px;
  cursor: pointer;
  height: 100%;
  font-size: 16px;
  top: 50%;
  transform: translateY(-50%);
}
/deep/.el-drawer__body{
  padding-bottom: 0;
}
/deep/.el-dialog__header{
  border-bottom: none!important;
}

.file-content-box{
  /deep/.el-form-item__label{
    line-height: 32px!important;
    padding-right: 0;
  }

  padding:10px 20px;
  font-size: 12px;
  .file-list{
    display: flex;
    padding: 10px 0;
    .file{
      background: #f1f8ff;
      border-radius: 10px;
      padding:10px;
      position: relative;
      cursor: pointer;
      margin-right:15px;
      &:hover{
        color: #3e82ff;
        text-decoration: underline;
      }
      .file-remove{
        position: absolute;
        right: -10px;
        top:-10px;
        width: 20px;
        height: 20px;
        background: red;
        border-radius: 50%;
        display: none;
        &::before{
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
      &:hover .file-remove{
        display: block;
      }
    }
  }
  .upload-tips{
    margin-top:20px;
    font-size: 12px;
    color:#797979;
  }
}

.footer-btn-box{
  text-align:center;
  margin-top:40px;
  .footer-btn{
    width: 100px;
    height: 35px;
    line-height: 35px;
    padding:0 10px;
  }
}


.upload-file-box{
  input[type=text]{
    border: 1px solid #ddd;
    height: 28px;
    width: 250px;
    outline: none;
  }
  .upload-file-btn{
    height: 32px;
    background: #3e82ff;
    color: white;
    padding:0 10px;
    box-sizing: border-box;
    font-size: 12px;
    cursor: pointer;
    line-height: 32px;
    margin-left:10px;
  }
  .upload-file{
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
    input[type=file] {
      position: absolute;
      font-size: 17px;
      right: 0;
      top: 0;
      opacity: 0;
      width: 334px;
      filter: alpha(opacity=0);
      cursor: pointer;
      &:hover {
        color: #444;
        background: #eee;
        border-color: #ccc;
        text-decoration: none
      }
    }
  }
}
/deep/.el-dialog__body{
  padding:10px 10px;
}
/deep/.el-dialog__header {
  padding: 10px 20px;
}
</style>
