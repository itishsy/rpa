<template>
  <div class="content spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="main-box">
      <!-- 步骤条 -->
      <el-steps align-center direction="vertical" class="steps-box" v-if="historyData.length > 0">
        <i class="el-icon-top icon-top" v-show="historyData.length > 0"></i>
        <el-step
          :class="'step-item redNo'"
          v-show="
            historyData !== [] &&
            historyData[0] &&
            historyData[0].runStatus == 0
          "
        >
          <template slot="icon">
            <span>未发布</span>
          </template>
          <template slot="title">
            <span class="click-pointer" @click="handleVersion(1, 'publish')"
              >发布</span
            >
          </template>
          <template slot="description">
            <el-divider class="step-line"></el-divider>
          </template>
        </el-step>
        <el-step
          :class="
            item.status == 1
              ? 'step-item blue'
              : item.status == 0
              ? 'step-item redNo'
              : 'step-item opopp'
          "
          v-for="(item, i) in historyData"
          :key="i"
        >
          <template slot="icon">
            <span v-if="item.status == 0">未发布</span>
            <span v-if="item.status == 1">运行中</span>
            <span v-if="item.status == 2">失效</span>
            <span v-if="item.status == 3">作废</span>
          </template>
          <template slot="title">
            <span
              style="margin-left: 10px"
              v-if="item.status == 1 || item.status == 2"
              class="click-pointer"
              @click="handleVersion(item, 'use')"
              >使用此版本</span
            >
            <span v-else style="height:19px;display:inline-block"></span>
          </template>
          <template slot="description">
            <p
              style="white-space: nowrap"
              class="version-line"
              v-if="item.status == 1 || item.status == 2 || item.status == 3"
            >
              {{item.rule ? item.rule + '：':'版本号：'}}{{ item.version }}&nbsp;&nbsp;&nbsp;{{item.changeReason}}
            </p>
            <el-divider class="step-line"></el-divider>
            <div class="content-border" v-if="item.openComment">{{item.comment}}</div>
            <div
              class="step-left-time"
              v-if="item.status == 1 || item.status == 2 || item.status == 3"
            >
              {{ moment(item.releaseTime).format('YYYY-MM-DD HH:mm:ss') }} 发布
            </div>
            <div class="step-left-time" style="top:33px;">{{item.releaseName ? '发布人：' + item.releaseName : ''}}</div>
            <div class="right-icon">
              <!-- <i
                v-if="item.status == 1 || item.status == 2"
                @click="seeContent(item)"
                class="el-icon-s-order click-pointer"
                style="font-size: 20px"
              ></i> -->
              <i class="el-icon-arrow-down" style="font-size:14px;cursor:pointer;" @click="openComment(true,historyData,i)" v-if="!item.openComment"></i>
              <i class="el-icon-arrow-up" style="font-size:14px;cursor:pointer;" @click="openComment(false,historyData,i)" v-else></i>
            </div>
          </template>
        </el-step>
      </el-steps>
      <el-empty description="暂无历史版本" v-else style="position: absolute;top: 40%;transform: translateY(-50%);"></el-empty>

      <!-- 弹窗 -->
      <!--<el-dialog
        :close-on-click-modal="false"
        title="本次发布更新内容："
        :visible.sync="dialogVisible"
        width="30%"
      >
        <el-input
          type="textarea"
          :rows="5"
          placeholder="请输入内容"
          v-model="versionObject.comment"
        >
        </el-input>
        <span slot="footer" class="dialog-footer">
          <el-button  @click="dialogVisible = false" size="small" class="w-60"
            >取消</el-button
          >
          <el-button type="primary" @click="checkPublish" size="small" class="w-60">确定</el-button>
        </span>
      </el-dialog>-->

      <el-dialog
        :close-on-click-modal="false"
        :visible.sync="resultVisible"
        width="30%"
      >
        <el-result icon="warning" title="确定使用此版本？"> </el-result>
        <span slot="footer" class="dialog-footer">
          <el-button @click="resultVisible = false" size="small" class="w-60"> 取消</el-button>
          <el-button type="primary" @click="handleCheck()" size="small" class="w-60"> 确定</el-button>
        </span>
      </el-dialog>


      <el-dialog
        :close-on-click-modal="false"
        :visible.sync="resultVisibletwo"
        width="30%"
      >
        <el-result icon="success" title="发布成功"> </el-result>
        <span slot="footer" class="dialog-footer">
          <!-- <el-button @click="resultVisibletwo = false" size="small" class="w-60"> 取消</el-button> -->
          <el-button type="primary" @click="resultVisibletwo = false" size="small" class="w-60">
            确定</el-button
          >
        </span>
      </el-dialog>

      <!-- <el-dialog
        title="本次发布更新内容："
        :close-on-click-modal="false"
        :visible.sync="seeVisible"
        width="30%"
      >
        <el-input
          type="textarea"
          :rows="5"
          v-model="seeComtent"
          :disabled="true"
        >
        </el-input>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="nowContent()">我了解！</el-button>
        </span>
      </el-dialog> -->
      <!-- 发布 -->
    <el-dialog
      title="发布"
      :visible.sync="dialogVisible"
      width="600px"
      :before-close="cancelVersion"
      :close-on-click-modal="false">
      <div id="upload-dialog">
        <div class="file-content-box">
          <el-form :model="versionObject" :rules="rules" ref="newVersionRuleForm" label-width="100px" class="demo-ruleForm">
            <div class="upload-file-box">
              <div>
                <el-form-item prop="rule" label-width="150" style="display:flex;align-items: center;" label="版本规则：">
                      <el-radio-group v-model="versionObject.rule">
                        <el-radio label="主版本号">主版本</el-radio>
                        <el-radio label="子版本号">子版本</el-radio>
                        <el-radio label="补丁">补丁</el-radio>
                      </el-radio-group>
                </el-form-item>
              </div>
              <div style="margin-bottom:22px;">
                <span style="font-size:14px;">新版本号：</span><span style="font-size:14px;">{{setVersionStr}}</span>
              </div>
              <div>
                <el-form-item prop="changeReason" label-width="150" style="margin-bottom:0;" label="变更原因：">
                  <el-select placeholder="请选择变更原因" style="width:100%"
                             v-model="versionObject.changeReason" clearable>
                    <el-option v-for="(item, index) in changeReasonArr" :label="item" :value="item"></el-option>
                  </el-select>
                </el-form-item>
              </div>
              <div>
                <el-form-item prop="comment" label-width="150" style="margin-bottom:0;" label="更新内容：">
                      <el-input
                          type="textarea"
                          :rows="8"
                          placeholder="请输入本次发布更新的内容"
                          style="width:100%"
                          v-model="versionObject.comment">
                      </el-input>
                </el-form-item>
                <div style="text-align:right;">{{versionObject.comment.length}} / 500</div>
              </div>
            </div>
          </el-form>
          <div class="footer-btn-box">
                <el-button @click="cancelVersion" class="footer-btn">取  消</el-button>
                <el-button type="primary"  @click="checkPublish" class="ml-20 footer-btn">确定发布</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
      <!-- 发布成功弹窗 -->
      <el-dialog
        :close-on-click-modal="false"
        :visible.sync="successVisible"
        width="30%"
      >
        <el-result icon="success" title="发布成功"> </el-result>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="handleSuccess"> 确定</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import moment from 'moment'
export default {
  name: 'historyVersion',
  components: {},
  data() {
    return {
      moment,
      versionObject: {
        comment: '',
        changeReason: '',
        rule:"补丁"
      },
      changeReasonArr: [
        '新应用上线',
        '流程配置缺陷',
        '流程优化',
        '网站新场景',
        '网页元素变化',
        '网站业务变更'
      ],
      temporyAppcode: undefined,
      successVisible: false,
      seeComtent: undefined,
      seeVisible: false,
      checkItem: false,
      reUseVisible: false,
      historyData: [],
      resultVisible: false,
      resultVisibletwo: false,
      dialogVisible: false,
      pathData: [
        {name: 'RPA应用设计'},
        {name: '历史版本'}
      ],
      appCode:"",
      temporyAppcode:"",
      version:'',
      rules: {
          comment:[
            { required: true, message: '请输入本次发布更新的内容', trigger: 'change' },
            { min: 0, max: 500, message: '更新内容不能大于500字', trigger: 'change' }
          ],
          rule:[
            { required: true, message: '请选择版本规则', trigger: 'change' }
          ],
          changeReason: [
            { required: true, message: '请选择变更原因', trigger: 'change' },
          ]
        },
    }
  },
  computed: {
    setVersionStr(){
        var str = this.version.replace(/V|v/,'').split('.')
        console.log(str)
        if(this.versionObject.rule == '' || this.versionObject.rule == null){
          return this.version
        }
        if(this.versionObject.rule == '主版本号'){
          str[0] = Number(str[0]) + 1
        }else if(this.versionObject.rule  == '子版本号'){
          str[1] = Number(str[1]) + 1
        }else{
          str[2] = Number(str[2]) + 1
        }
        return 'V' + str.join('.')
      },
  },
  created() {},
  watch: {},
  mounted() {
    if(this.$route.query.appCode){
      this.appCode = this.$route.query.appCode
      this.temporyAppcode = this.$route.query.appCode
    }
    // //正确逻辑 从其他页面点进来带入 appCode appCode
    // var appCode = '60d9c10875ab49b7acb5ede313ae183d' //
    // this.temporyAppcode = '60d9c10875ab49b7acb5ede313ae183d'
    this.seeVesion(this.appCode)
  },
  methods: {
    cancelVersion(){
        this.dialogVisible = false;
        this.versionObject.comment = ""
        this.versionObject.rule = ''
        this.$nextTick(()=>{
          this.$refs.newVersionRuleForm.clearValidate()
        })
    },
    nowContent() {
      this.seeVisible = false
      this.seeComtent = undefined
    },
    seeContent(item) {
      this.seeComtent = item.comment
      this.seeVisible = true
    },
    handleSuccess() {
      this.successVisible = false
    },
    seeVesion(val) {
      this.showLoading()
      this.$http({
        url: 'robot/app/history',
        method: 'post',
        params: {
          appCode: val,
        },
      }).then(({ data }) => {
        this.historyData = data.data
        this.closeLoading()
      }).catch(err=>{
        this.closeLoading()
      })
    },

    openDialog() {
      this.dialogVisible = true
      this.versionObject.rule = '补丁'
    },
    // 确定使用此版本
    handleCheck() {
      console.log('versionObject', this.versionObject)
      this.showLoading()
      this.$http({
        url: 'robot/app/activate',
        method: 'post',
        params: {
          appCode: this.versionObject.appCode,
          version: this.versionObject.version,
        },
      }).then(({ data }) => {
          this.resultVisible = false
          this.versionObject = {comment: '',rule:""}
          this.seeVesion(this.temporyAppcode)
          this.resultVisibletwo = true
          this.closeLoading()
      }).catch(err=>{
        this.closeLoading()
      })
    },
    checkPublish() {
      this.$refs.newVersionRuleForm.validate((valid)=>{
        if(valid){
          console.log('发布新版本')
          this.showLoading()
          //确定发布3
          this.$http({
            url: 'robot/app/release',
            method: 'post',
            data:{
              appCode:this.appCode,
              changeReason: this.versionObject.changeReason,
              comment:this.versionObject.comment,
              rule:this.versionObject.rule
            },
            // params: {
            //   comment: this.versionObject.comment,
            //   appCode: this.temporyAppcode,
            // },
          }).then(({ data }) => {
              this.dialogVisible = false
              this.successVisible = true
              this.seeVesion(this.temporyAppcode)
              this.versionObject = {comment: '',changeReason:"", rule:""}
              this.closeLoading()
          }).catch(err=>{
            this.closeLoading()
          })
        }
      })

    },
    handleVersion(val, type) {
      console.log(val)
      Object.assign(this.versionObject, val)
      if (type == 'publish') {
        this.dialogVisible = true
        this.version = this.historyData[0].version
      } else if (type == 'use') {
        this.resultVisible = true
      }
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
    openComment(type,data,i){
      if(type){
        this.$set(data[i],'openComment',true)
      }else{
        this.$set(data[i],'openComment',false)
      }
    }
  },
}
</script>

<style scoped lang="less">
.icon-top {
  font-size: 30px;
  color: #c1c4cb;
  margin-left: -4px;
}
/deep/ .el-dialog__header {
  border-bottom: 0px solid #bfbfbf !important;
}
.click-pointer {
  color: #5a9cf8;
  cursor: pointer;
  font-size: 14px;
}
.main-box {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 15px;
}
/deep/ .el-step.is-vertical .el-step__head {
  width: 39px;
  height: 168px;
}
/deep/.opopp .el-step__icon.is-text {
  border-radius: 50%;
  border: 2px solid;
  border-color: inherit;
  width: 50px;
  height: 50px;
  position: relative;
  left: -13px;
  font-size: 13px;
}
/deep/.blue .el-step__icon.is-text {
  border-radius: 50%;
  border: 2px solid;
  border-color: #5a9cf8;
  width: 50px;
  height: 50px;
  position: relative;
  left: -13px;
  font-size: 13px;
  color: #5a9cf8;
}
/deep/.redNo .el-step__icon.is-text {
  border-radius: 50%;
  border: 2px solid;
  border-color: red;
  width: 50px;
  height: 50px;
  position: relative;
  left: -13px;
  font-size: 13px;
  color: red;
}
/deep/ .el-step.is-vertical .el-step__line {
  margin-top: -18px;
}
/deep/ .el-step__line {
  height: 291px;
}
.step-item {
  position: relative;
  .step-line {
    width: 141px;
    position: absolute;
    top: 0px;
    left: 40px;
  }
  .right-icon {
    position: absolute;
    top: 14px;
    left: 186px;
  }
  .step-left-time {
    position: absolute;
    top: 17px;
    left: -170px;
  }
  .version-line {
    white-space: nowrap;
    position: absolute;
    top: 32px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: center;
}
.content-border{
  position: absolute;
  margin-top:25px;
  border:1px solid #ddd;
  border-radius: 5px;
  width:200px;
  color:#666;
  white-space: pre-wrap;
  overflow-y:scroll;
  height: 100px;
  padding:5px;
  font-size: 12px;
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
</style>
