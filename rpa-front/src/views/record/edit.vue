<template>
  <div class="spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData">
      <template slot="breadcrumb-btn">
        <el-button size="small" type="primary" @click="doShowVideo()">播放录制视频</el-button>
      </template>
    </breadcrumb>
    <div style="padding: 10px;" v-if="detailData">
      <div style="padding: 0 20px">
        <el-row :gutter="20">
          <el-col :span="8" class="display-flex">
            <span class="lab required-pre">名称：</span>
            <div class="flex1">
              <el-input size="medium" class="input-item" placeholder="请输入" v-model.trim="detailData.name"
                        clearable></el-input>
            </div>
          </el-col>
          <el-col :span="8" class="display-flex">
            <span class="lab required-pre">地区：</span>
            <div class="flex1">
              <addrSelector
                v-model="detailData.addressName"
                :addrArr="options.allAddr"
                width="100%"
                class="input-item"
                @changeAddrSelect="changeAddrSelect"
              ></addrSelector>
            </div>
          </el-col>
          <el-col :span="8" class="display-flex">
            <span class="lab required-pre">业务类型：</span>
            <div class="flex1">
              <el-select v-model="detailData.businessType" placeholder="请选择" class="input-item">
                <el-option label="社保" value="1001001"></el-option>
                <el-option label="公积金" value="1001002"></el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20" class="pt-10">
          <el-col :span="8" class="display-flex">
            <span class="lab required-pre">操作类型：</span>
            <div class="flex1">
              <el-select v-model="detailData.type" placeholder="请选择" class="input-item" filterable>
                <el-option v-for="item in options.type" :key="item.code" :label="item.name"
                           :value="item.code"></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="8" class="display-flex">
            <span class="lab required-pre">网址url：</span>
            <div class="flex1">
              <el-input size="medium" class="input-item" placeholder="请输入" v-model.trim="detailData.url" clearable></el-input>
            </div>
          </el-col>
          <el-col :span="8" class="display-flex">
            <span class="lab">创建人/时间：</span>
            <div class="pt-5">{{ detailData.creatorName }}&nbsp;&nbsp;{{ $moment(detailData.createTime).format('YYYY-MM-DD HH:mm:ss')}}</div>
            <div class="flex1 text-right ml-10" style="margin-right: -20px;">
              <el-button size="small" type="primary" @click="doSaveOpt()">保存操作数据</el-button>
            </div>
          </el-col>
        </el-row>
      </div>

      <div class="content">
        <div class="content-l">
          <div class="box-title">操作过程</div>
          <div class="step-list" :style="{ height: optHeight }">
<!--            <draggable v-model="stepList" animation="300">
              <transition-group class="stepArea">-->
                <div v-for="(item, index) in stepList" :key="index" v-show="item.type!=='xmlhttprequest'">
                  <div class="step-item">
                    <span class="step-name">{{ handleType(item.type) }}{{ item.text ? item.text : '' }}</span>
<!--                    <div class="del-btn">
                      <i class="el-icon-delete text-red font-18 f-cursor" @click.stop="delOptStep(index)"></i>
                    </div>-->
                  </div>
                </div>
<!--              </transition-group>
            </draggable>-->
          </div>
        </div>
        <div class="content-r">
          <div class="display-flex">
            <div class="step-box">
              <div class="box-title">步骤</div>
              <div class="step-list area-c" :style="{ height: contentHeight }">
                <draggable v-model="stepListForm.stepList" group="itxst" animation="300"
                           @add="addStep" @sort="resetSelectStep">
                  <transition-group class="stepArea">
                    <div v-for="(item, index) in stepListForm.stepList" :key="index">
                      <div class="step-list">
                        <div class="step-item" :class="{'active': (curSelectEditInd===''||curSelectEditInd==undefined||curSelectEditInd=='undefined')&&curSelectEditIndex===index}" @click="showStepArgs(index)">
                          <span class="comment">{{item.stepName}}</span>
                          <span class="step-name">
                        {{item.actionName}}</span>
                          <div class="del-btn">
                            <i class="el-icon-delete text-red font-18 f-cursor" title="删除" @click.stop="delStep(index)"></i>
                          </div>
                        </div>
                        <!--子步骤-->
                        <div class="sub-list" :style="{'padding-top': item.list&&item.list.length>0?'10px':'0'}">
                          <draggable v-model="item.list" group="itxst" animation="300"
                                     @add="(e)=>addStepSub(e, index)"
                                     @sort="resetSelectStep"
                                     style="display: inline-block;width: 100%;">
                            <transition-group style="display: block; min-height: 5px;width: 100%;padding-left: 30px;box-sizing: border-box">
                              <div v-for="(it, ind) in item.list" :key="ind">
                                <div class="step-item" :class="{'active': curSelectEditIndex===index&&curSelectEditInd===ind}"
                                     @click.stop="showStepArgs(index, ind)">
                                  <span class="comment">{{it.stepName}}</span>
                                  <span class="step-name"><i class="el-icon-info text-orange font-16" style="vertical-align: middle;margin-top: -2px;" v-if="it.skipTo=='' && (it.actionName=='条件判断'||it.actionName=='遍历器')"></i>
                                {{it.actionName}} </span>
                                  <div class="del-btn">
                                    <i class="el-icon-delete text-red font-18 f-cursor" title="删除" @click.stop="delStep(index, ind)"></i>
                                  </div>
                                </div>
                              </div>
                            </transition-group>
                          </draggable>
                        </div>
                      </div>
                    </div>
                  </transition-group>
                </draggable>
              </div>
            </div>
            <div class="arg-box">
              <div class="box-title">属性</div>
              <div class="step-list area1-r" ref="argsArea" :key="refeshKey" :style="{ height: contentHeight }">
                <el-form :inline="true" :model="stepListForm" ref="stepListForm" label-width="0">
                  <div v-if="curSelectEditIndex!==''">
                    <div v-for="(item, index) in stepListForm.stepList" :key="index">
                      <!--主步骤-->
                      <div v-if="(curSelectEditInd===''||curSelectEditInd==undefined||curSelectEditInd=='undefined')&&curSelectEditIndex===index">
                        <div class="mb-15">
                          <el-form-item :prop="'stepList.'+index+'.stepName'" :rules="[{ required: true, message: '请输入步骤名称',trigger:'blur' }]">
                            <el-input v-model="item.stepName" maxlength="50" placeholder="请输入步骤名称，必填"></el-input>
                          </el-form-item>
                        </div>
                        <div class="property-div" v-if="item.targetArgsVOS&&item.targetArgsVOS.length>0">
                          <p class="title">操作目标</p>
                          <div>
                            <table cellpadding="0" cellspacing="0">
                              <tbody>
                              <!--displayType: text、date、number、select-->
                              <!--fieldType: text,number,date,singleDropList,single-->
                              <tr v-for="(it, ind) in item.targetArgsVOS" :key="ind" v-show="!it.isHideCust">
                                <td><span :class="{'required-pre':it.required===1}">{{it.fieldName}}</span>
                                  <i class="el-icon-warning-outline ml-5 text-orange" v-if="it.comment" :title="it.comment"></i>
                                </td>
                                <td>
                                  <div class="text-div">
                                    <el-form-item :prop="'stepList.'+index+'.targetArgsVOS.'+ind+'.fieldValue'" class="flex1"
                                                  :rules="[{ validator: (rule, value, callback)=>checkDynamicField(rule, value, callback, it), trigger:'blur'},
                                              { validator: (rule, value, callback)=>checkDynamicField(rule, value, callback, it), trigger:'change'}]">
                                      <el-input v-if="it.fieldType=='text'||it.fieldType=='number'" v-model="it.fieldValue" :placeholder="it.comment?'':'请输入'"></el-input>

                                      <el-date-picker v-else-if="it.fieldType=='date'" v-model="it.fieldValue" type="date" clearable
                                                      value-format="yyyy-MM-dd" :placeholder="it.comment?'':'请选择'"></el-date-picker>

                                      <el-select v-else-if="it.fieldType=='singleDropList'" v-model="it.fieldValue" @change="changeSingleDropList(index, ind, '', 'targetArgsVOS')" :placeholder="it.comment?'':'请选择'" value-key="dictCode" clearable filterable>
                                        <el-option v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)" :key="optIndex" :label="optItem.dictName" :value="optItem.dictCode"></el-option>
                                      </el-select>

                                      <div v-else-if="it.fieldType=='single'">
                                        <el-radio v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)" :key="optIndex" v-model="it.fieldValue" :label="optItem.dictCode">{{optItem.dictName}}</el-radio>
                                      </div>
                                    </el-form-item>

                                    <i v-if="it.fieldType=='text'" class="el-icon-edit-outline ic-edit-input" @click="showEditDialog(index, ind, '','targetArgsVOS')"></i>
                                  </div>
                                </td>
                              </tr>
                              </tbody>
                            </table>
                          </div>
                        </div>
                        <div class="property-div" v-if="item.actionArgsVOS&&item.actionArgsVOS.length>0">
                          <p class="title">行为参数</p>
                          <div>
                            <table cellpadding="0" cellspacing="0">
                              <tbody>
                              <!--displayType: text、date、number、select-->
                              <!--fieldType: text,number,date,singleDropList,single-->
                              <tr v-for="(it, ind) in item.actionArgsVOS" :key="ind" v-show="!it.isHideCust">
                                <td><span :class="{'required-pre':it.required===1}">{{it.fieldName}}</span>
                                  <i class="el-icon-warning-outline ml-5 text-orange" v-if="it.comment" :title="it.comment"></i>
                                </td>
                                <td>
                                  <div class="text-div">
                                    <el-form-item :prop="'stepList.'+index+'.actionArgsVOS.'+ind+'.fieldValue'"
                                                  :rules="[{ validator: (rule, value, callback)=>checkDynamicField(rule, value, callback, it), trigger:'blur'},
                                            { validator: (rule, value, callback)=>checkDynamicField(rule, value, callback, it), trigger:'change'}]">
                                      <el-input v-if="it.fieldType=='text'||it.fieldType=='number'" v-model="it.fieldValue" :placeholder="it.comment?'':'请输入'"></el-input>

                                      <el-date-picker v-else-if="it.fieldType=='date'" v-model="it.fieldValue" type="date" clearable value-format="yyyy-MM-dd" :placeholder="it.comment?'':'请选择'"></el-date-picker>

                                      <el-select v-else-if="it.fieldType=='singleDropList'" v-model="it.fieldValue" @change="changeSingleDropList(index, ind, '', 'actionArgsVOS')" :placeholder="it.comment?'':'请选择'" value-key="dictCode" clearable filterable>
                                        <el-option v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)" :key="optIndex" :label="optItem.dictName" :value="optItem.dictCode"></el-option>
                                      </el-select>

                                      <div v-else-if="it.fieldType=='single'">
                                        <el-radio v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)" :key="optIndex" v-model="it.fieldValue" :label="optItem.dictCode">{{optItem.dictName}}</el-radio>
                                      </div>
                                    </el-form-item>
                                    <i v-if="it.fieldType=='text'" class="el-icon-edit-outline ic-edit-input" @click="showEditDialog(index, ind, '','actionArgsVOS')"></i>
                                  </div>
                                </td>
                              </tr>
                              </tbody>
                            </table>
                          </div>
                        </div>
                      </div>
                      <!--子步骤-->
                      <div v-for="(subItem, subIndex) in item.list" :key="subIndex">
                        <div v-if="curSelectEditIndex===index&&curSelectEditInd===subIndex">
                          <div class="mb-15">
                            <el-form-item :prop="'stepList.'+index+'.list.'+subIndex+'.stepName'" :rules="[{ required: true, message: '请输入步骤名称',trigger:'blur' }]">
                              <el-input v-model="subItem.stepName" placeholder="请输入"></el-input>
                            </el-form-item>
                          </div>
                          <div class="property-div" v-if="subItem.targetArgsVOS&&subItem.targetArgsVOS.length>0">
                            <p class="title">操作目标</p>
                            <div>
                              <table cellpadding="0" cellspacing="0">
                                <tbody>
                                <!--displayType: text、date、number、select-->
                                <!--fieldType: text,number,date,singleDropList,single-->
                                <tr v-for="(it, ind) in subItem.targetArgsVOS" :key="ind" v-show="!it.isHideCust">
                                  <td><span :class="{'required-pre':it.required===1}">{{it.fieldName}}</span>
                                    <i class="el-icon-warning-outline ml-5 text-orange" v-if="it.comment" :title="it.comment"></i>
                                  </td>
                                  <td>
                                    <div class="text-div">
                                      <el-form-item :prop="'stepList.'+index+'.list.'+subIndex+'.targetArgsVOS.'+ind+'.fieldValue'"
                                                    :rules="[{ validator: (rule, value, callback)=>checkDynamicField(rule, value, callback, it), trigger:'blur'},
                                              { validator: (rule, value, callback)=>checkDynamicField(rule, value, callback, it), trigger:'change'}]">
                                        <el-input v-if="it.fieldType=='text'||it.fieldType=='number'" v-model="it.fieldValue" :placeholder="it.comment?'':'请输入'"></el-input>

                                        <el-date-picker v-else-if="it.fieldType=='date'" v-model="it.fieldValue" type="date"
                                                        value-format="yyyy-MM-dd" :placeholder="it.comment?'':'请选择'" clearable></el-date-picker>

                                        <el-select v-else-if="it.fieldType=='singleDropList'" v-model="it.fieldValue" @change="changeSingleDropList(index, subIndex, ind, 'targetArgsVOS')" :placeholder="it.comment?'':'请选择'" value-key="dictCode" clearable filterable>
                                          <el-option v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)" :key="optIndex" :label="optItem.dictName" :value="optItem.dictCode"></el-option>
                                        </el-select>

                                        <div v-else-if="it.fieldType=='single'">
                                          <el-radio v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)" :key="optIndex" v-model="it.fieldValue" :label="optItem.dictCode">{{optItem.dictName}}</el-radio>
                                        </div>
                                      </el-form-item>
                                      <i v-if="it.fieldType=='text'" class="el-icon-edit-outline ic-edit-input" @click="showEditDialog(index, subIndex, ind,'targetArgsVOS')"></i>
                                    </div>
                                  </td>
                                </tr>
                                </tbody>
                              </table>
                            </div>
                          </div>
                          <div class="property-div" v-if="subItem.actionArgsVOS&&subItem.actionArgsVOS.length>0">
                            <p class="title">行为参数</p>
                            <div>
                              <table cellpadding="0" cellspacing="0">
                                <tbody>
                                <!--displayType: text、date、number、select-->
                                <!--fieldType: text,number,date,singleDropList,single-->
                                <tr v-for="(it, ind) in subItem.actionArgsVOS" :key="ind" v-show="!it.isHideCust">
                                  <td><span :class="{'required-pre':it.required===1}">{{it.fieldName}}</span>
                                    <i class="el-icon-warning-outline ml-5 text-orange" v-if="it.comment" :title="it.comment"></i>
                                  </td>
                                  <td>
                                    <div class="text-div">
                                      <el-form-item :prop="'stepList.'+index+'.list.'+subIndex+'.actionArgsVOS.'+ind+'.fieldValue'"
                                                    :rules="[{ validator: (rule, value, callback)=>checkDynamicField(rule, value, callback, it), trigger:'blur'},
                                              { validator: (rule, value, callback)=>checkDynamicField(rule, value, callback, it), trigger:'change'}]">
                                        <el-input v-if="it.fieldType=='text'||it.fieldType=='number'" v-model="it.fieldValue" :placeholder="it.comment?'':'请输入'"></el-input>

                                        <el-date-picker v-else-if="it.fieldType=='date'" v-model="it.fieldValue" type="date" clearable value-format="yyyy-MM-dd" :placeholder="it.comment?'':'请选择'"></el-date-picker>

                                        <el-select v-else-if="it.fieldType=='singleDropList'" @change="changeSingleDropList(index, subIndex, ind, 'actionArgsVOS')" v-model="it.fieldValue" :placeholder="it.comment?'':'请选择'" value-key="dictCode" clearable filterable>
                                          <el-option v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)" :key="optIndex" :label="optItem.dictName" :value="optItem.dictCode"></el-option>
                                        </el-select>

                                        <div v-else-if="it.fieldType=='single'">
                                          <el-radio v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)" :key="optIndex" v-model="it.fieldValue" :label="optItem.dictCode">{{optItem.dictName}}</el-radio>
                                        </div>
                                      </el-form-item>
                                      <i v-if="it.fieldType=='text'" class="el-icon-edit-outline ic-edit-input" @click="showEditDialog(index, subIndex, ind,'actionArgsVOS')"></i>
                                    </div>
                                  </td>
                                </tr>
                                </tbody>
                              </table>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </el-form>
              </div>
            </div>
          </div>
          <div class="text-center mt-15">
            <el-button size="small" class="btn--border-blue mr-30" @click="doChange()">转换</el-button>
            <el-button size="small" type="primary" @click="handleSave()">保存步骤</el-button>
          </div>
        </div>
      </div>

    </div>

    <!--编辑输入框内容-->
    <el-dialog :visible.sync="editDialog.show" title="编辑" width="1000px" class="cust-dialog" :show-close="true"
               :close-on-click-modal="false">
      <div class="pt-10">
        <el-input v-model="editDialog.fieldValue" type="textarea" rows="20" resize="none"
                  placeholder="请输入"></el-input>
      </div>
      <div class="text-right mt-15">
        <span v-if="editDialog.checkRes=='success'" class="text-green mr-20">校验通过！</span>
        <span v-if="editDialog.checkRes=='error'" class="text-red mr-20">校验失败，请检查！</span>
        <el-button size="small" class="btn--border-blue mr-20" @click="addElementValueJson">插入Element对象</el-button>
        <el-button size="small" class="btn--border-blue mr-20" @click="checkEditValueJson">校验json格式</el-button>
        <el-button size="small" class="w-60" type="primary" @click="confirmEditValue">确定</el-button>
      </div>
    </el-dialog>

<!--    播放视频-->
    <VueDragResize
      :isActive="true"
      :w="500"
      :h="300"
      :x="20"
      :y="150"
      class="video_fixed" v-show="video.show">
      <div class="video_box">
        <i class="el-icon-circle-close ic-close-video" @click="doCloseVideo"></i>
        <video
          v-if="detailData && detailData.recVideoUrl"
          ref="videoPlayer"
          controls
          autoplay
          muted
          :src="detailData.recVideoUrl"
        ></video>
      </div>
    </VueDragResize>

  </div>
</template>

<script>
import draggable from 'vuedraggable'
import addrSelector from 'components/common/addrSelector.vue'
import VueDragResize from 'vue-drag-resize'

export default {
  components: { addrSelector, draggable, VueDragResize },
  data () {
    return {
      pathData: [],
      options: {
        allAddr: [],
        type: [
          { name: '登录', code: 1 },
          { name: '核验', code: 2 },
          { name: '开户', code: 3 },
          { name: '启封', code: 4 },
          { name: '转入', code: 5 },
          { name: '封存', code: 6 },
          { name: '补缴', code: 7 },
          { name: '基数调整', code: 8 },
          { name: '其他', code: 9 }
        ]
      },
      detailData: null,
      stepList: [],
      stepListForm: {
        stepList: []
      },
      editDialog: {
        show: false,
        fieldValue: '',
        index: '',
        ind: '',
        typeIndex: '',
        type: '',
        checkRes: ''
      },
      refeshKey: 0,
      curSelectEditIndex: '', // 当前选中的index
      curSelectEditInd: '', // 当前选中的ind
      originalCommandList: [], // 命令中心数据
      collectData: {
        timer: null,
        timerCount: 3000,
        index: '',
        ind: '',
        typeIndex: '',
        type: ''
      },
      isEdit: false,
      video: {
        show: false,
        top: 20,
        left: 20,
        offsetX: null,
        offsetY: null
      }
    }
  },
  computed: {
    optHeight: function () {
      return this.$global.bodyHeight - 255 + 'px'
    },
    contentHeight: function () {
      return this.$global.bodyHeight - 315 + 'px'
    }
  },
  watch: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
    }
    this.pathData.push({ name: '编辑' })

    if (this.$route.query.id) {
      this.id = this.$route.query.id
      this.getData()
    }

    this.getAddr() // 获取参保地
    this.getCommandList() // 获取参保地
  },
  mounted () {
  },
  methods: {
    // 获取详情
    getData () {
      this.$http({
        url: '/policy/sys/desktopApplications/getDetailById?id=' + this.id,
        method: 'get'
      }).then((data) => {
        if (data.data.code === 200) {
          this.detailData = data.data.data
          try {
            this.stepList = JSON.parse(data.data.data.browserEvent)
            console.log(this.stepList)
          } catch (e) {

          }
        }
      }).catch(() => {
      })
    },
    handleType (type) {
      switch (type) {
        case 'click':
          return '点击'
        case 'focus':
          return '聚焦'
        case 'blur':
          return '失焦'
        case 'upload':
          return '上传'
        case 'downLoad':
          return '下载'
        case 'xmlhttprequest':
          return '请求'
      }
      return ''
    },
    delOptStep (index) {
      let that = this
      this.$confirm('是否确认删除该操作过程？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        that.stepList.splice(index, 1)
      }).catch(() => {
      })
    },
    doBack () {
      this.$router.replace(`/record/index`)
    },
    // 保存
    doSaveOpt () {
      let that = this
      let setObj = this.detailData
      let msg = ''
      if (!setObj.name) {
        msg = '请输入名称'
      } else if (!setObj.addressName) {
        msg = '请选择地区'
      } else if (!setObj.businessType) {
        msg = '请选择业务类型'
      } else if (!setObj.type) {
        msg = '请选择操作类型'
      } else if (!setObj.url) {
        msg = '请输入网址url'
      }
      if (msg !== '') {
        this.$message.warning(msg)
        this.$message.close()
        return
      }
      this.showLoading()
      setObj.browserEvent = JSON.stringify(this.stepList)
      this.$http({
        url: '/policy/sys/desktopApplications/desktopApplicationsSaveOrUpdate',
        method: 'post',
        data: setObj
      }).then((data) => {
        this.closeLoading()
        if (data.data.code === 200) {
          this.$message.success('操作成功')
          // setTimeout(function () {
          //   that.doBack()
          // }, 1500)
        }
      }).catch(() => {
        this.closeLoading()
      })
    },

    // 获取参保地
    getAddr () {
      this.$http({
        url: 'policy/declareAddr/addrList',
        method: 'post'
      }).then(({ data }) => {
        this.options.allAddr = data.data
      })
    },
    changeAddrSelect (val) {
      this.detailData.addressId = val.id
      this.detailData.addressName = val.name
    },

    // 获取命令中心
    getCommandList () {
      let that = this
      this.$http({
        url: '/robot/action/all',
        method: 'post'
      }).then(({ data }) => {
        that.originalCommandList = data.data
        if (this.$route.query.id) {
          this.getStepList()
        }
      }).catch((data) => {
      })
    },

    getActionObj (actionCode) {
      var obj = this.originalCommandList.find(item => {
        return item.actionCode === actionCode
      })
      return obj || ''
    },

    // 处理动态字段返回的数据
    handleArgs (arr, actionCode) {
      // 动态字段同一组中最多只有一个cond不为空，cond控制同组中其他字段的显示或隐藏
      var condRes = false; var showFieldKeyArr = []
      if (actionCode === 'pullOffer') {
      //   对【拉取报盘文件：pullOffer】进行单独的逻辑处理：如果为必填且值为空，则不显示
        arr.map(item => {
          item.fieldValue = item.fieldValue ? item.fieldValue : ''
          if (item.fieldValue === '' && item.required === 1) {
            item.isHideCust = true
          }
        })
      } else {
        arr.map(item => {
          item.fieldValue = item.fieldValue ? item.fieldValue : ''
          item.isHideCust = false
          if (item.cond != '' && item.cond != null) {
            condRes = true

            // "{post:\"text,password\",get:\"number,date,single\"}"
            var itemObj = item
            if (itemObj.fieldValue) {
              var condObj = JSON.parse(itemObj.cond)
              showFieldKeyArr = []
              for (var key in condObj) {
                if (itemObj.fieldValue == key) {
                  showFieldKeyArr = condObj[key]
                  break
                }
              }
            }
          }
        })
      }

      if (condRes) {
        arr.map(item2 => {
          if (!(item2.cond != '' && item2.cond != null) && showFieldKeyArr.indexOf(item2.fieldKey) == -1) {
            item2.isHideCust = true
          }
        })
      }
      return arr
    },

    // 获取步骤
    getStepList () {
      this.$http({
        url: '/policy/saasRobotFlowStep/findStepListByDeskId?id=' + this.id,
        method: 'get'
      }).then((data) => {
        if (data.data.code === 200) {
          if (data.data.data.length > 0) {
            this.isEdit = true
            this.handleStepList(data.data.data)
          }
        }
      }).catch(() => {
      })
    },

    // 转换
    doChange () {
      this.showLoading('请稍等...')
      this.$http({
        url: '/policy/saasRobotFlowStep/parseData?id=' + this.detailData.id,
        method: 'get'
      }).then((data) => {
        this.closeLoading()
        if (data.data.code === 200) {
          this.$message.success('转换成功')
          this.resetSelectStep()
          this.handleStepList(data.data.data)
        }
      }).catch(() => {
        this.resetSelectStep()
        this.stepListForm.stepList = []
        this.closeLoading()
      })
    },

    handleStepList (originalStepList) {
      console.log(originalStepList)
      let that = this
      var stepList = []
      var resultIndex = -1; var subItem; var actionObj
      for (var i = 0; i < originalStepList.length; i++) {
        subItem = originalStepList[i]
        actionObj = that.getActionObj(subItem.actionCode)
        if (actionObj == '') {
          that.isloadingPage = false
          that.isStepErrorMsg = '步骤中第' + (i + 1) + '条数据 actionCode=' + subItem.actionCode + ' 已经不存在命令中心，请检查'
          return
        }
        subItem.groupName = actionObj.groupName
        subItem.actionName = actionObj.actionName
        subItem.targetArgsVOS = that.handleArgs(subItem.targetArgsVOS, subItem.actionCode) // 处理动态字段根据条件显示或隐藏
        subItem.actionArgsVOS = that.handleArgs(subItem.actionArgsVOS, subItem.actionCode) // 处理动态字段根据条件显示或隐藏
        if (subItem.level === 0) {
          //  父级
          resultIndex++
          subItem.list = []

          stepList.push(subItem)
        } else if (subItem.level === 1) {
          //  子级
          stepList[resultIndex].list.push(subItem)
        }
      }
      console.log(stepList)
      that.stepListForm.stepList = stepList
    },

    getStepItem (newObj) {
      return {
        id: newObj.id,
        sysDesktopApplicationsId: newObj.sysDesktopApplicationsId,
        groupName: newObj.groupName,
        actionName: newObj.actionName,
        actionCode: newObj.actionCode,
        stepName: this.isNotEmptyString(newObj.stepName) ? newObj.stepName : '',
        failedRetry: this.isNotEmptyString(newObj.failedRetry) ? newObj.failedRetry : '',
        skipTo: this.isNotEmptyString(newObj.skipTo) ? newObj.skipTo : '',
        failedSkipTo: this.isNotEmptyString(newObj.failedSkipTo) ? newObj.failedSkipTo : '',
        skipCondition: this.isNotEmptyString(newObj.skipCondition) ? newObj.skipCondition : '',
        waitBefore: this.isNotEmptyString(newObj.waitBefore) ? newObj.waitBefore : '',
        waitAfter: this.isNotEmptyString(newObj.waitAfter) ? newObj.waitAfter : '',
        timeout: this.isNotEmptyString(newObj.timeout) ? newObj.timeout : '',
        status: this.isNotEmptyString(newObj.status) ? newObj.status : 1,
        failedStrategy: this.isNotEmptyString(newObj.failedStrategy) ? newObj.failedStrategy : 0,
        actionArgsVOS: newObj.actionArgsVOS ? newObj.actionArgsVOS : '',
        targetArgsVOS: newObj.targetArgsVOS ? newObj.targetArgsVOS : ''
      }
    },
    addStep (e) {
      console.log('addStep-----------', e)
      console.log(this.stepListForm.stepList)

      this.resetSelectStep()
      var newObj = this.stepListForm.stepList[e.newIndex]
      console.log('newObj -----------', newObj)
      let obj = this.getStepItem(newObj)
      console.log('obj===')
      console.log(obj)
      obj.list = []
      this.$set(this.stepListForm.stepList, e.newIndex, obj)
      this.refeshKey = new Date().getTime()

      if (!obj.actionArgsVOS) {
        this.getActionArgs(e.newIndex)
      }
      if (!obj.targetArgsVOS) {
        this.getTargetArgs(e.newIndex)
      }
    },
    addStepSub (e, index) {
      console.log('addStepSub')
      console.log(index)
      console.log(e.newIndex)
      this.resetSelectStep()
      var newObj = this.stepListForm.stepList[index].list[e.newIndex]
      let obj = this.getStepItem(newObj)
      this.$set(this.stepListForm.stepList[index].list, e.newIndex, obj)
      console.log('obj===')
      console.log(obj)
      this.refeshKey = new Date().getTime()
      if (!obj.actionArgsVOS) {
        this.getActionArgs(index, e.newIndex)
      }
      if (!obj.targetArgsVOS) {
        this.getTargetArgs(index, e.newIndex)
      }
    },
    isNotEmptyString (str) {
      return str !== '' && str !== undefined
    },
    delStep (index, ind) {
      let that = this
      this.$confirm('是否确认删除该步骤？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        that.resetSelectStep()
        if (ind != undefined) {
          //  子步骤
          that.stepListForm.stepList[index].list.splice(ind, 1)
        } else {
          //  步骤
          that.stepListForm.stepList.splice(index, 1)
        }
      }).catch(() => {})
    },
    showStepArgs (index, ind) {
      console.log('showStepArgs')
      this.curSelectEditIndex = Number(index)
      this.curSelectEditInd = ind != undefined ? Number(ind) : ''
      this.$refs.argsArea.scrollTop = 0
      console.log(index)
      console.log(ind)
    },
    resetSelectStep () {
      this.curSelectEditIndex = null
      this.curSelectEditInd = null
    },

    // 输入框-编辑
    showEditDialog (index, ind, typeIndex, type) {
      let editDialog = this.editDialog
      if (typeIndex !== '') {
        //  子步骤
        editDialog.fieldValue = this.stepListForm.stepList[index].list[ind][type][typeIndex].fieldValue
      } else {
        //  步骤
        editDialog.fieldValue = this.stepListForm.stepList[index][type][ind].fieldValue
      }
      editDialog.index = index
      editDialog.ind = ind
      editDialog.typeIndex = typeIndex
      editDialog.type = type
      editDialog.checkRes = ''
      editDialog.show = true
    },
    confirmEditValue () {
      let editDialog = this.editDialog
      let index = editDialog.index
      let ind = editDialog.ind
      let typeIndex = editDialog.typeIndex
      let type = editDialog.type
      if (editDialog.typeIndex !== '') {
        //  子步骤
        this.stepListForm.stepList[index].list[ind][type][typeIndex].fieldValue = editDialog.fieldValue
      } else {
        //  步骤
        this.stepListForm.stepList[index][type][ind].fieldValue = editDialog.fieldValue
      }
      editDialog.show = false
    },
    addElementValueJson () {
      this.editDialog.fieldValue = this.editDialog.fieldValue + '{"element":{"attrType":"xpath","attrValue":"","timeout":10}}'
    },
    checkEditValueJson () {
      let str = this.editDialog.fieldValue
      // this.editDialog.checkRes = val!=''?'success':'error'
      if (typeof str === 'string') {
        try {
          var obj = JSON.parse(str)
          if (typeof obj === 'object' && obj) {
            this.editDialog.checkRes = 'success'
          } else {
            this.editDialog.checkRes = 'error'
          }
        } catch (e) {
          this.editDialog.checkRes = 'error'
        }
      } else {
        this.editDialog.checkRes = 'error'
        console.log('It is not a string!')
      }
    },

    handleOptions (list) {
      if (list) {
        return list
      } else {
        return []
      }
    },
    checkDynamicField (rule, value, callback, item) {
      if (this.$lodash.isEmpty(value)) {
        if (item.required === 1 && !item.isHideCust) {
          if (item.fieldType == 'text' || item.fieldType == 'number') {
            callback(new Error('请输入'))
          } else {
            callback(new Error('请选择'))
          }
        } else {
          callback()
        }
      } else {
        if (item.fieldType == 'number' && !item.isHideCust) {
          if (!(/(^(-?\d+)\d*$)/.test(value))) { // 必须为整数
            callback(new Error('请输入正确数值'))
          } else {
            if (value >= 0) {
              callback()
            } else {
              callback(new Error('请输入正确数值'))
            }
          }
        } else {
          callback()
        }
      }
    },
    changeSingleDropList (index, ind, typeIndex, type) {
      var itemObj, list
      if (typeIndex !== '') {
        //  子步骤
        list = this.stepListForm.stepList[index].list[ind][type]
        itemObj = list[typeIndex]
      } else {
        //  步骤
        list = this.stepListForm.stepList[index][type]
        itemObj = list[ind]
      }
      if (!itemObj.cond) {
        // 没有条件控制
        return
      }
      // "{post:\"text,password\",get:\"number,date,single\"}"
      var condObj = JSON.parse(itemObj.cond); var showFieldKeyArr = []
      for (var key in condObj) {
        if (itemObj.fieldValue == key) {
          showFieldKeyArr = condObj[key]
          break
        }
      }
      list.map(item => {
        if ((itemObj.fieldKey == item.fieldKey) || showFieldKeyArr.indexOf(item.fieldKey) > -1) {
          item.isHideCust = false
        } else {
          item.isHideCust = true
        }
      })
    },
    checkArgsVOS (list, index) {
      let that = this
      let item
      let j = 0
      for (let i = 0; i < list.length; i++) {
        if (!list[i].stepName) {
          if (index != undefined) {
            that.showStepArgs(index, i)
          } else {
            that.showStepArgs(i)
          }
          return false
        }
        for (j = 0; j < list[i].targetArgsVOS.length; j++) {
          item = list[i].targetArgsVOS[j]
          if (!item.isHideCust && item.required === 1 && !item.fieldValue) {
            if (index != undefined) {
              that.showStepArgs(index, i)
            } else {
              that.showStepArgs(i)
            }
            return false
          }
        }
        for (j = 0; j < list[i].actionArgsVOS.length; j++) {
          item = list[i].actionArgsVOS[j]
          if (!item.isHideCust && item.required === 1 && !item.fieldValue) {
            if (index != undefined) {
              that.showStepArgs(index, i)
            } else {
              that.showStepArgs(i)
            }
            return false
          }
        }
        if (list[i].list) {
          if (!this.checkArgsVOS(list[i].list, i)) {
            break
          }
        }
      }
      return true
    },
    handleSave () {
      let that = this
      this.checkArgsVOS(that.stepListForm.stepList)
      this.$nextTick(() => {
        this.$refs.stepListForm.validate((valid, errorMessage) => {
          if (valid) {
            console.log('校验通过')
            that.doSave()
          } else {
            console.log(errorMessage)
            /* for (var i in errorMessage) {
              var field = errorMessage[i][0].field
              var fieldArr = field.split('.')
              var index = fieldArr[1]
              that.showStepArgs(index)
              return
            } */
          }
        })
      })
    },
    doSave () {
      let that = this
      var setData = []; var item; var subItem; var number = 0; var actionArgs; var targetArgs
      for (let i = 0; i < that.stepListForm.stepList.length; i++) {
        item = { ...that.stepListForm.stepList[i] }
        item.flowCode = that.flowCode
        item.level = 0
        item.number = number
        number++
        actionArgs = {}
        targetArgs = {}
        if (item.actionArgsVOS) {
          item.actionArgsVOS.map(it => {
            actionArgs[it.fieldKey] = it.isHideCust ? '' : it.fieldValue
          })
        }
        if (item.targetArgsVOS) {
          item.targetArgsVOS.map(it => {
            targetArgs[it.fieldKey] = it.isHideCust ? '' : it.fieldValue
          })
        }

        item.actionArgs = JSON.stringify(actionArgs)
        item.targetArgs = JSON.stringify(targetArgs)
        item.actionArgsVOS = []
        item.targetArgsVOS = []
        setData.push(item)
        if (item.list) {
          for (let j = 0; j < item.list.length; j++) {
            subItem = { ...item.list[j] }
            subItem.flowCode = that.flowCode
            subItem.level = 1
            subItem.number = number
            number++
            actionArgs = {}
            targetArgs = {}
            if (subItem.actionArgsVOS) {
              subItem.actionArgsVOS.map(it => {
                actionArgs[it.fieldKey] = it.isHideCust ? '' : it.fieldValue
              })
            }
            if (subItem.targetArgsVOS) {
              subItem.targetArgsVOS.map(it => {
                targetArgs[it.fieldKey] = it.isHideCust ? '' : it.fieldValue
              })
            }

            subItem.actionArgs = JSON.stringify(actionArgs)
            subItem.targetArgs = JSON.stringify(targetArgs)
            subItem.actionArgsVOS = []
            subItem.targetArgsVOS = []
            setData.push(subItem)
          }
          item.list = []
        }
      }
      if (setData.length == 0) {
        that.$message.warning('请先转换操作步骤')
        return
      }
      // return
      this.showLoading('正在保存...')
      setData.map((item, index) => {
        item.number = index
      })
      let url = '/policy/saasRobotFlowStep/saveList'
      if (this.isEdit) {
        url = '/policy/saasRobotFlowStep/update'
      }
      this.$http({
        url: url,
        method: 'post',
        data: setData
      }).then(({ data }) => {
        that.closeLoading()
        that.$message.success('保存成功')
        // setTimeout(function () {
        //   that.doBack()
        // }, 1500)
      }).catch((data) => {
        that.closeLoading()
      })
    },

    doShowVideo () {
      if (!this.detailData.recVideoUrl) {
        this.$message.warning('没有找到录屏文件')
        return
      }
      this.video.show = true
      this.$refs.videoPlayer.player.play()
    },
    doCloseVideo () {
      this.video.show = false
      this.$refs.videoPlayer.pause()
    },

    showLoading (msg) {
      this.loading = this.$loading({
        target: document.body,
        lock: true,
        text: msg || '正在保存...',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      })
    },
    closeLoading () {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    }
  }
}
</script>

<style lang="less" scoped>
.lab {
  display: inline-block;
  line-height: @inputHeight;
  width: 90px;
  text-align: right;
  color: #606266;
}

.input-item {
  width: 90%;
  max-width: 400px;
}

.content {
  margin-top: 10px;
  display: flex;

  .content-l {
    flex: 0.8;
    margin-right: 15px;
    .step-list {
      min-height: 300px;
      border: 1px solid #EEF0F7;
      padding: 5px 10px 15px 10px;
      overflow: auto;
      box-sizing: border-box;
    }
    .step-item:hover {
      border-color: @blueColor;
    }

    .step-name {
      flex: 1;
      word-break: break-all;
    }
  }

  .content-r {
    flex: 2;
    border-left: 1px dashed #dbdbdb;
    padding-left: 15px;

    .step-list {
      padding: 10px 10px 0px 10px;
      overflow: auto;
    }

    .step-box {
      border: 1px solid #EEF0F7;
      margin-right: 15px;
      flex: 2;
    }

    .arg-box {
      border: 1px solid #EEF0F7;
      flex: 1;
    }
    .step-name {
      margin-left: 30px;
    }
    .comment {
      margin-right: 10px;
      flex: 1;
    }
    .del-btn {
      margin-left: 10px;
    }
  }

  .box-title {
    height: 40px;
    line-height: 40px;
    font-size: 16px;
    font-weight: bold;
    color: #303133;
    background: #EEF0F7;
    padding-left: 10px;
  }
  .step-item {
    padding: 7px 10px;
    border: 1px solid #dbdbdb;
    border-radius: 4px;
    display: flex;
    margin-top: 10px;
  }

  .del-btn {
    margin-left: 20px;
  }

  .area-c {
    min-height: 300px;
    box-sizing: border-box;
    .step-item {
      padding: 7px 10px;
      border: 1px solid #dbdbdb;
      border-radius: 4px;
      display: flex;
      cursor: move;
      margin-top: 0;
    }

    .step-item.active {
      border-color: @blueColor;
      background-color: rgba(203, 218, 255, 0.3);
    }

    .step-item:hover {
      border-color: @blueColor;
    }

    .step-list {
      border: 1px dashed #dbdbdb;
      border-radius: 4px;
      padding: 10px 10px 0px 10px;
      margin-bottom: 15px;
    }
    .sub-list {
      .step-item {
        margin-bottom: 10px;
      }
    }
  }
}

.area1-r {
  min-height: 300px;
  box-sizing: border-box;
  /deep/.el-form-item{
    margin-bottom: 0;
    margin-right: 0;
    width: 100%;
    .el-form-item__content{
      width: 100%;
    }
    .el-form-item__error{
      position: relative;
    }
  }
  /deep/.el-form--inline .el-form-item__content{
    width: 100%;
  }
  .area-border {
    padding: 15px;
  }
  .property-div {
    border-radius: 4px;
    margin-bottom: 20px;
    .title {
      padding: 8px 0;
      font-size: 14px;
      text-align: center;
      background: #5792ff;
      border-top-left-radius: 4px;
      border-top-right-radius: 4px;
      color: #FFFFFF;
    }
    table {
      width: 100%;
      border: 1px solid #dbdbdb;
      border-bottom-left-radius: 4px;
      border-bottom-right-radius: 4px;
    }
    td {
      padding: 5px;
      border-bottom: 1px solid #dbdbdb;
    }
    td:first-of-type {
      border-right: 1px solid #dbdbdb;
      width: 120px;
    }
    tr:last-of-type td {
      border-bottom: none;
    }
  }
  .property-div:last-of-type {
    margin-bottom: 0;
  }
  .text-div{
    display: flex;
    align-items: center;
  }
  .ic-edit-input{
    margin-left: 8px;
    font-size: 16px;
    cursor: pointer;
  }
}

.video_fixed {
  position: fixed;
  cursor: move;
  z-index: 10000!important;
  .video_box{
    width: 100%;
    height: 100%;
  }
  .ic-close-video{
    position: absolute;
    right: -14px;
    top: -14px;
    font-size: 24px;
    color: #606266;
    z-index: 2;
    cursor: pointer;
  }
  video {
    width: 100%;
    height: 100%;
    background-color: #000;
  }
  /deep/.vdr-stick{
    display: none;
  }
  /deep/.vdr-stick-tr{
    display: none!important;
  }
  &:hover{
    /deep/.vdr-stick{
      display: block;
    }
  }
}
</style>
