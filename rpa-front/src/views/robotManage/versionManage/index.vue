<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <!--<div class="text-left pt-20 pb-20" id="d1">-->
        <!--<el-input v-model="comments" type="text" style="width: 250px" placeholder="发布说明"/>-->
        <!--<el-button size="small" type="primary" class="ml-10" @click="aaa()">搜索</el-button>-->
      <!--</div>-->
      <div class="text-right pt-20 pb-20">
          <el-button type="primary" class="s-btn" @click="handleAddUpdate('add')">添加组件</el-button>
      </div>

      <!--<div class="text-left pt-20 pb-20">
        <el-input type="text" placeholder="发布说明" />
      </div>-->
      <div>
        <dTable :data="tableData" ref="table" :tableHeight="tableHeight" :showIndex='false' :showSelection='false' rowKey="id"
          :paging="false" >
          <u-table-column prop="component" label="组件名称" min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="version" label="版本号" min-width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="fileName" label="升级包" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span class="text-btn" @click="downloadPackage(scope.row.filePath, scope.row.fileName)">下载</span>
            </template>
          </u-table-column>
          <u-table-column prop="common" label="升级说明" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span class="text-btn" @click="handleView(scope.row.comment)">{{scope.row.comment}}</span>
            </template>
          </u-table-column>
          <u-table-column prop="releaseTime" label="升级时间" min-width="120" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{$moment(scope.row.releaseTime).format('YYYY-MM-DD HH:mm:ss')}}</span>
            </template>
          </u-table-column>
          <u-table-column prop="status" label="状态" min-width="100" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <!--状态（1 运行中，0 停止， 2 历史版本）-->
              <span class="text-green" v-if="scope.row.status===1">运行中</span>
              <span class="text-red" v-else-if="scope.row.status===0">停止</span>
              <span class="text-gray" v-else-if="scope.row.status===2">历史版本</span>
            </template>
          </u-table-column>
          <u-table-column label="操作" align="left" width="160">
            <template slot-scope="scope">
              <el-button type="text" size="medium" class="text-blue text-blue2" @click="handleAddUpdate('update', scope.row)">升级</el-button>
              <div class="opt-btn-split" v-if="scope.row.status===1||scope.row.status===0"></div>
              <!--状态（1 运行中，0 停止， 2 历史版本）-->
              <el-button type="text" size="medium" class="text-blue text-blue2" v-if="scope.row.status===1||scope.row.status===0"
                         @click="changeStatus(scope.row)">{{scope.row.status===1?'停止':'启用'}}</el-button>
              <div class="opt-btn-split"></div>
              <el-button type="text" size="medium" class="text-blue text-blue2" @click="goHistoryPage(scope.row.component)">历史</el-button>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>
    <!-- 升级说明 -->
    <el-dialog title="升级说明" :visible.sync="updateComment.show"
      width="600px" class="cust-dialog" :show-close="false" :close-on-click-modal="false">
      <div style="min-height: 150px">
        <p v-html="updateComment.comment"></p>
      </div>
      <div class="text-right mt-30">
        <el-button size="small" type="primary" @click="updateComment.show=false">我知道啦！</el-button>
      </div>
    </el-dialog>

    <!-- 添加组件\升级 -->
    <el-dialog :title="addUpdateComponent.isAdd?'添加组件':'升级组件'" :visible.sync="addUpdateComponent.show"
               width="500px" class="cust-dialog" :show-close="true" :close-on-click-modal="false" :before-close="resetAddUpdate">
      <div>
        <el-form label-width="0" :model="addUpdateComponent" ref="addUpdateComponent">
          <div class="display-flex">
            <span class="required-pre mr-15 pt-10">组件名称</span>
            <el-form-item prop="component" class="flex1"
                          :rules="[{ required: true, message: '请输入组件名称', trigger: 'blur' }, { validator: (rule, value, callback)=>checkComponent(rule, value, callback), trigger:'blur'}]">
              <el-input v-model="addUpdateComponent.component" :disabled="!addUpdateComponent.isAdd" maxlength="100" placeholder="请输入组件名称" clearable></el-input>
            </el-form-item>
          </div>
          <div class="display-flex flex1">
            <span class="required-pre mr-20" style="padding-left: 13px;">升级包</span>
            <el-form-item label="" prop="fileName" class="file-form-item" :rules="[{ required: true, message: '请上传升级包', trigger: 'change' }]">

              <div class="flex1 display-flex">
                <div>
                  <!--
                  :action="'/api/api/file/excelParse'"
                    :headers="this.$global.setUploadHeaders()"
                     name="installJar"
                    :data="{component: '1',comment: '2'}"
                    -->
                  <!---->
                  <el-upload
                    :http-request="customUpload"
                    action=""
                    ref="upload"
                    :show-file-list="false"
                    :auto-upload="false"
                    :on-change="uploadChange">
                    <el-button type="text" icon="ic-file-upload-blue">上传</el-button>
                  </el-upload>
                </div>
                <span class="flex1 pl-30">{{addUpdateComponent.fileName}}</span>
              </div>
            </el-form-item>
          </div>
          <div class="display-flex mt-10">
            <span class="required-pre mr-15 pt-10">升级说明</span>
            <el-form-item prop="comment" class="flex1"
                          :rules="[{ required: true, message: '请输入升级说明', trigger: 'blur' }]">
              <el-input v-model="addUpdateComponent.comment" placeholder="请输入更新的内容" maxlength="100" type="textarea" :rows="5" clearable></el-input>
            </el-form-item>
          </div>
        </el-form>
      </div>
      <div class="text-right mt-30">
        <el-button size="small" class="mr-15 w-80" @click="resetAddUpdate()">取消</el-button>
        <el-button size="small" class="w-80" type="primary" @click="doAddUpdate()" :loading="addUpdateComponent.btnLoading">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>

import dTable from '../../../components/common/table'
export default {
  components: { dTable },
  data () {
    return {
      pathData: [],
      tableData: [],
      comments: '',
      updateComment: {
        show: false,
        comment: ''
      },
      addUpdateComponent: {
        show: false,
        btnLoading: false,
        isAdd: true,
        component: '',
        comment: '',
        fileName: '',
        version: '',
        fileUid: '' // 上传组件可能是上传了多个文件，最终上传的需要用fileUi判断
      }
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 190 + 'px'
    }
  },
  created () {
    let that = this
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.$nextTick(() => {
      that.getTableData()
    })
  },
  mounted () {
  },
  methods: {
    // 列表查询
    getTableData () {
      let that = this
      this.$http({
        url: '/robot/version/list',
        method: 'post'
      }).then(({ data }) => {
        that.tableData = data.data
      }).catch(() => {})
    },
    handleView (comment) {
      this.updateComment.comment = comment
      this.updateComment.show = true
    },
    // 禁用\启用
    changeStatus (row) {
      let that = this
      let status = row.status
      // <!--状态（1 运行中，0 停止， 2 历史版本）-->
      this.$confirm('是否确定' + (status === 1 ? '停止' : '启用') + '？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        that.$http({
          url: '/robot/version/enableStop?id=' + row.id,
          method: 'post'
        }).then(({ data }) => {
          that.$message.success('操作成功')
          that.getTableData()
        }).catch(() => {
        })
      }).catch(() => {

      })
    },

    aaa () {
      let val = this.comments
      console.log(val)
      this.$http({
        url: '/robot/version/queryByComment?comment=' + val,
        method: 'post'
      }).then(({ data }) => {
        this.tableData = data.data
      })
    },

    handleAddUpdate (type, row) {
      if (type === 'add') {
        this.addUpdateComponent.isAdd = true
      } else {
        this.addUpdateComponent.isAdd = false
        this.addUpdateComponent.component = row.component
        this.addUpdateComponent.version = row.version
      }
      this.addUpdateComponent.show = true
    },
    resetAddUpdate () {
      this.addUpdateComponent = this.$options.data().addUpdateComponent
      this.$refs.upload.clearFiles()
      this.$refs.addUpdateComponent.resetFields()
    },
    uploadChange (file, fileList) {
      console.log(file)
      this.addUpdateComponent.fileName = file.name
      this.addUpdateComponent.fileUid = file.uid
      this.$refs.addUpdateComponent.validateField('fileName')
    },
    doAddUpdate () {
      let that = this
      this.$refs.addUpdateComponent.validate((valid) => {
        if (valid) {
          that.$refs.upload.submit()
        }
      })
    },
    customUpload (file) {
      let that = this
      console.log(file)
      this.showLoading()
      this.addUpdateComponent.btnLoading = true
      let data = this.addUpdateComponent
      if (data.fileUid != file.file.uid) {
        this.addUpdateComponent.btnLoading = false
        this.closeLoading()
        return
      }
      let FormDatas = new FormData()
      FormDatas.append('installJar', file.file)
      FormDatas.append('component', data.component)
      FormDatas.append('comment', data.comment)
      let url = ''
      if (data.isAdd) {
        url = '/robot/version/add'
      } else {
        url = '/robot/version/upgrade'
        FormDatas.append('version', data.version)
      }
      this.$http({
        url: url,
        method: 'post',
        data: FormDatas
      }).then(({ data }) => {
        that.closeLoading()
        that.$message.success('操作成功')
        that.getTableData()
        that.resetAddUpdate()
      }).catch(() => {
        that.addUpdateComponent.btnLoading = false
        that.closeLoading()
      })
    },
    showLoading(target){
      this.loading = this.$loading({
        target:target? target : document.body,
        lock: true,
        text: '提交中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      });
    },
    closeLoading(){
      if(this.loading && this.loading.close){
        this.loading.close()
      }
    },
    checkComponent (rule, value, callback) {
      if (this.addUpdateComponent.isAdd) {
        for (let i = 0; i < this.tableData.length; i++) {
          if (this.tableData[i].component === value) {
            callback(new Error('组件名称不能重复使用'))
            return
          }
        }
        callback()
      } else {
        // 升级时组件名称不可编辑
        callback()
      }
    },

    downloadPackage (filePath, fileName) {
      var nameArr = fileName.split('.')
      var fileType = nameArr[nameArr.length - 1]
      fileType = fileType.toLowerCase()
      if (fileType == 'jar') {
        this.$downloadFile(filePath, 'get', this.$global.JAR, '', fileName)
      } else {
        this.$downloadFile(filePath, 'get', {}, '', fileName)
      }
    },

    goHistoryPage (component) {
      this.$router.push('/robotManage/historyVersion?component=' + component)
    }

  }
}
</script>
<style lang="less" scoped>
.text-blue2{
  &:hover{
    text-decoration: underline;
  }
}
  .text-btn{
    text-decoration: underline;
    cursor: pointer;
    &:hover{
      color:#3e82ff;
    }
  }

  #d1{
    width: 50%;
    float: left;
  }

  .textColor{
    color: #ED523F;
  }

  /deep/.file-form-item {
    .el-upload .el-button{
      padding-top: 0;
      padding-bottom: 0;
    }
    .el-form-item__content{
      line-height: normal;
    }
  }
</style>
