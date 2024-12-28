<template>
  <div class="spl-container pb-30">
    <breadcrumb :data="pathData">
      <el-button
        type="primary"
        slot="breadcrumb-btn"
        plain
        size="small"
        @click="handleSave(0)"
        v-if="type == 2 && !formData.status"
      >存草稿</el-button>
      <el-button
        type="primary"
        slot="breadcrumb-btn"
        size="small"
        @click="handleSave('')"
        v-if="type == 1 || (type == 2 && !formData.status)"
      >{{type == 1 ? '确定保存' : '确认发布'}}</el-button>
    </breadcrumb>
    <el-form ref="formData" :model="formData" class="mt-25" label-width="150px" :rules="rules">
      <p class="fw-blod font-16 pl-50 pt-20 pr-20" style="display:flex;justify-content: space-between;align-items: center;"><span>消息内容</span>
        <span v-if="rowId" style="color: #999999;font-size: 12px;font-weight: normal;">
          上次编辑：{{(this.formData.updateName ? this.formData.updateName : this.formData.createName) + ' ' + (this.formData.updateTime ? this.formData.updateTime : this.formData.createTime)}}
        </span>
      </p>
      <el-row style="padding: 20px 20px 0px 20px;">
        <el-col :span="8" class="input-item">
          <div style="display: flex">
            <el-form-item
              label="消息类型："
              prop="messageType"
            >
              <el-select
                v-model="formData.messageType"
                placeholder="请选择"
                filterable
                :disabled="isDisabled"
                @change="changeMessageType"
              >
                <el-option :label="item.dictName" :value="item.dictCode" v-for="(item,index) in this.option.messageType" :key="index"></el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="8" class="input-item">
          <div style="display: flex">
            <el-form-item
              label="消息项目："
              prop="warnType"
            >
              <el-select
                v-model="formData.warnType"
                ref="warnType"
                placeholder="请选择"
                filterable
                :disabled="isDisabled"
              >
                <el-option
                  v-for="item in filterWarnType"
                  :key="item.dictCode"
                  :label="item.dictName"
                  :value="item.dictCode"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="8" class="input-item" v-if="type == 1">
          <div style="display: flex">
            <el-form-item
              label="响应等级："
              prop="responseGrade"
            >
              <el-select
                v-model="formData.responseGrade"
                placeholder="请选择"
                filterable
              >
                <el-option
                  v-for="item in option.responseGrade"
                  :key="item.dictCode"
                  :label="item.dictName"
                  :value="item.dictCode"
                ></el-option>
              </el-select>
              <span
                style="width:212px;color: #999999;font-size: 12px;position: absolute;left:0px;bottom: -20px;line-height: 20px;text-align: right;"
              >{{handleResponseGrade}}</span>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="8" class="input-item" v-if="type == 2">
            <el-form-item
              label="消息维度："
              prop="addrName"
              style="margin-bottom: 0;"
            >
              <div style="display:flex;">
                <el-form-item
                  label
                  prop="addrName"
                  label-width="0"
                >
                  <addrSelector
                    v-model="formData.addrName"
                    :addrArr="addrArr"
                    @changeAddrSelect="getSocialAddrId"
                    class="search-row-item"
                    width="100%"
                  ></addrSelector>
                </el-form-item>
                <el-form-item
                  label
                  prop="businessType"
                  label-width="0"
                  style="width:100%;"
                >
                  <el-select
                    v-model="formData.businessType"
                    placeholder="请选择"
                    filterable
                    style="width:100%;"
                  >
                    <el-option
                      v-for="item in option.businessType"
                      :key="item.value"
                      :label="item.name"
                      :value="item.value"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </div>
            </el-form-item>
        </el-col>
        <el-col :span="16" class="input-item">
          <div style="display: flex">
            <el-form-item
              label="消息主题："
              prop="messageTopic"
            >
              <el-input
                v-model="formData.messageTopic"
                maxlength="500"
                placeholder="请输入"
                clearable
                @blur="value => inputParamBlur(value, 'titleValCursor')"
                ref="messageTopic"
              ></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="8" class="input-item" v-if="this.type == 1">
          <p class="mt-5"><span class="guide-text" @click="handleParamShow('title')">引</span></p>
        </el-col>
        <el-col :span="22" class="input-item">
          <div style="display: flex">
            <el-form-item
              label="邮件正文："
              prop="emailContent"
            >
              <!-- <div
                v-if="isDisabled"
                style="line-height: normal;border: 1px solid #dbdbdb;padding: 10px;border-radius: 4px"
                v-html="formData.emailContent"
              ></div> -->
              <custEditor
                ref="custEditor"
                height="300px"
                width="100%"
                v-model="formData.emailContent"
                :showPlaceholder="true"
                @onEditorBlur="onEditorBlur"
              ></custEditor>
              <!-- <div class="font-12 ml-10 text-gray" style="text-align: right;">字数统计：{{formData.emailContent.length}}</div> -->
            </el-form-item>
          </div>
        </el-col>
        <el-col v-if="type == 2" :span="22" class="input-item">
          <div style="display: flex">
            <el-form-item
              label="上传附件："
              prop="files"
            >
              <el-upload
                ref="upload"
                class="inlineBlock mr-15"
                :headers="$global.setUploadHeaders()"
                :action="$global.uploadFileUrl"
                :before-upload="this.$global.checkFileSize(50)"
                :auto-upload="true"
                :show-file-list="false"
                multiple
                :on-success="onSuccessFile">
                <el-button type="primary" class="s-btn">上传</el-button>
              </el-upload>
              <div class="inlineBlock mr-20" v-for="(item, index) in formData.files" :key="item.fileId">
                <span class="text-blue f-cursor" @click="downloadFile(item.fileId)">{{item.clientFileName}}</span>
                <i class="el-icon-delete ml-5 text-red f-cursor" @click="delFile(index)"></i>
              </div>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="2" v-if="this.type == 1">
          <span class="guide-text" style="margin-top: 270px;" @click="handleParamShow('email')">引</span>
        </el-col>
        <el-col :span="16" class="input-item" v-if="type == 1">
          <div style="display: flex">
            <el-form-item
              label="短信正文："
              prop="smsContent"
            >
              <el-input
                type="textarea"
                :rows="4"
                v-model="formData.smsContent"
                maxlength="500"
                placeholder="请输入"
                @blur="value => inputParamBlur(value, 'messageValCursor')"
                ref="smsContent"
              ></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="8" class="input-item" style="padding-top: 70px;" v-if="type == 1">
          <span class="font-12 ml-10 text-gray">字数统计：{{formData.smsContent.length}}</span>
          <span class="guide-text" @click="handleParamShow('message')">引</span>
        </el-col>
      </el-row>
      <p class="fw-blod font-16 pl-50 pt-20">通知设置</p>
      <el-row style="padding: 20px 20px 0px 20px;">
        <el-col :span="24" class="input-item">
          <div style="display: flex">
            <el-form-item
              label="通知方式："
              prop="sendWay"
              :disabled="isDisabled"
            >
              <el-checkbox-group v-model="formData.sendWay" @change="changeSenWay">
                <el-checkbox label="邮件">邮件</el-checkbox>
                <el-checkbox label="短信" v-if="type == 1">短信</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="24" class="input-item">
          <table cellspacing="0" cellpadding="0" border="0" style class="notice-table">
            <colgroup>
              <col width="20%" />
              <col width="50%" />
              <col width="30%" />
            </colgroup>
            <thead>
              <tr style="background: #f2f2f2;text-align:center;height: 40px;">
                <th>
                  <span style="color: red;">*</span>通知对象
                </th>
                <th>
                  <span style="color: red;">*</span>对象角色
                </th>
                <th>备注</th>
              </tr>
            </thead>
            <tr>
              <td>
                <div class="table-cell">
                  <el-checkbox v-model="formData.sendCust" :true-label="1" :false-label="0">客户</el-checkbox>
                </div>
              </td>
              <td>
                <div style="display: flex;" class="operatorList">
                  <el-form-item
                    label=""
                    prop="custList"
                    label-width="0"
                    style="flex-shrink: 0;"
                  >
                    <el-select
                      v-model="formData.custList"
                      placeholder="如需指定客户，请勾选添加"
                      filterable
                      multiple
                      collapse-tags
                      style="flex-shrink: 0;width: 250px;"
                      :disabled="!formData.sendCust && rowId != ''"
                    >
                      <el-option
                        v-for="item in customerArr"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                  <div style="display:flex;position: relative;width: 100%;flex-wrap: wrap;" id="custList">
                    <el-tag size="small" type="info" style="margin:5px 5px;" :closable="formData.sendCust == 1"
                            v-for="(item,index) in formData.custList" :key="index"
                            @close="removeCustTag(index)" class="elTag">
                      {{getCustName(item)}}
                    </el-tag>
                    <!-- <span class="el-tag el-tag--info el-tag--small el-tag--light" v-if="formData.operatorList.length > 1">
                      <span class="el-select__tags-text">+{{ formData.operatorList.length  - 1 }}
                      </span>
                    </span> -->
                  </div>
                </div>
              </td>
              <td>
                <el-input
                  v-model="formData.custComment"
                  maxlength="500"
                  placeholder="请输入"
                  clearable
                ></el-input>
              </td>
            </tr>
            <tr>
              <td>
                <div class="table-cell">
                  <el-checkbox v-model="formData.sendOperator" :true-label="1" :false-label="0">运营</el-checkbox>
                </div>
              </td>
              <td>
                <div style="display: flex;" class="operatorList">
                  <el-form-item
                    label=""
                    prop="operatorList"
                    label-width="0"
                    style="flex-shrink: 0;"
                  >
                    <el-select
                      v-model="formData.operatorList"
                      placeholder="请勾选添加"
                      filterable
                      multiple
                      collapse-tags
                      style="flex-shrink: 0;width: 250px;"
                      :disabled="!formData.sendOperator && rowId != ''"
                    >
                      <el-option
                        v-for="item in role"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                  <div style="display:flex;position: relative;width: 100%;flex-wrap: wrap;" id="tagList">
                    <el-tag size="small" type="info" style="margin:5px 5px;" :closable="formData.sendOperator == 1"
                      v-for="(item,index) in formData.operatorList" :key="index"
                      @close="removeTag(index)" class="elTag">
                      {{getOperatorListName(item)}}
                    </el-tag>
                    <!-- <span class="el-tag el-tag--info el-tag--small el-tag--light" v-if="formData.operatorList.length > 1">
                      <span class="el-select__tags-text">+{{ formData.operatorList.length  - 1 }}
                      </span>
                    </span> -->
                  </div>
                </div>
              </td>
              <td>
                <el-input
                  v-model="formData.operatorComment"
                  maxlength="500"
                  placeholder="请输入"
                  clearable
                ></el-input>
              </td>
            </tr>
            <tr>
              <td>
                <div class="table-cell">
                  <el-checkbox v-model="formData.sendPersonnel" :true-label="1" :false-label="0">指定人员</el-checkbox>
                </div>
              </td>
              <td>
                <div
                  style="display: flex;margin-top:5px;"
                  v-for="(item,index) in formData.personnelList"
                  :key="index"
                  class="personnelList"
                >
                  <el-form-item :prop="`personnelList.${index}.name`" label-width="0" style="width: 27%;" :rules="rules.name">
                    <el-input
                      v-model="item.name"
                      maxlength="500"
                      placeholder="人员姓名"
                      clearable
                      style="margin-left:0px;margin-right:5px;width: 100%;"
                      :disabled="!formData.sendPersonnel && rowId != ''"
                    ></el-input>
                  </el-form-item>
                  <el-form-item :prop="`personnelList.${index}.phoneNumber`" label-width="0" :rules="rules.phoneNumber" style="width: 27%;">
                    <el-input
                      v-model="item.phoneNumber"
                      maxlength="500"
                      placeholder="人员手机号"
                      clearable
                      style="margin-right:5px;width: 100%;"
                      :disabled="!formData.sendPersonnel && rowId != ''"
                    ></el-input>
                  </el-form-item>
                  <el-form-item :prop="`personnelList.${index}.email`" label-width="0" :rules="rules.email" style="width: 27%;">
                    <el-input
                      v-model="item.email"
                      maxlength="500"
                      placeholder="人员邮箱"
                      clearable
                      style="margin-right:5px;width: 100%;"
                      :disabled="!formData.sendPersonnel && rowId != ''"
                    ></el-input>
                  </el-form-item>
                  <div style="display: flex;align-items: center;width: 60px;" v-if="formData.sendPersonnel == 1">
                    <i
                      class="el-icon-remove-outline"
                      style="font-size: 24px;margin:0 5px;cursor: pointer;color:#FD6154;"
                      @click="remove(index)"
                      v-if="formData.personnelList.length > 1"
                    ></i>
                    <i
                      class="el-icon-circle-plus-outline"
                      style="font-size: 24px;margin:0 5px;cursor: pointer;color: #3E82FF;"
                      @click="add"
                      v-if="index == formData.personnelList.length - 1"
                    ></i>
                  </div>
                </div>
              </td>
              <td>
                <el-input
                  v-model="formData.personnelComment"
                  maxlength="500"
                  placeholder="请输入"
                  clearable
                ></el-input>
              </td>
            </tr>
          </table>
        </el-col>
      </el-row>
      <p class="fw-blod font-16 pl-50 pt-20">发送策略</p>
      <el-row style="padding: 20px 20px 0px 20px;">
        <el-col :span="12" class="input-item">
          <div style="display: flex">
            <el-form-item
              label="消息时效："
              prop="messageStrategy"
              :rules="[{ required: true, message: '请选择', trigger: 'change' }]"
            >
              <el-radio-group v-model="formData.messageStrategy">
                <el-radio :label="1">
                  <span>定时消息</span>
                </el-radio>
                <el-radio :label="2">
                  <span>实时消息</span>
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
        </el-col>
      </el-row>
      <el-row style="padding: 20px 20px 0px 20px;" v-if="formData.messageStrategy == 1">
        <el-col :span="12" class="input-item">
          <div style="display: flex">
            <el-form-item
              label="发送时间："
              prop="sendStrategy"
              :rules="[{ required: true, message: '请选择', trigger: 'change' }]"
            >
              <el-radio-group v-model="formData.sendStrategy" >
                <el-radio :label="1">
                  <span>连续时间区间</span>
                </el-radio>
                <el-radio :label="2">
                  <span>固定时间点</span>
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12" class="input-item" v-if="formData.sendStrategy == 1">
          <div style="display: flex;">
            <el-form-item label="选择区间：" prop="time1" style="flex: none;">
              <el-select
                v-model="formData.time1"
                placeholder="请选择"
                style="width: 100px"
                clearable
                filterable
              >
                <el-option
                  :label="item + '时'"
                  :value="item"
                  v-for="(item, index) in getTime1"
                  :key="index"
                ></el-option>
              </el-select>
            </el-form-item>
            <span style="padding: 0px 20px; margin-top: 10px">至</span>
            <el-form-item label-width="0px" prop="time2" style="flex: none;">
              <el-select
                v-model="formData.time2"
                placeholder="请选择"
                style="width: 100px"
                clearable
                filterable
              >
                <el-option
                  :label="item + '时'"
                  :value="item"
                  v-for="(item, index) in getTime2"
                  :key="index"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12" class="input-item" v-if="formData.sendStrategy == 2">
          <div>
            <el-form-item label="添加时间点：" prop="timeArr">
              <div
                style="
                  display: flex;
                  align-items: center;
                  flex-wrap: wrap;
                "
              >
                <div v-for="(item, index) in formData.timeArr" :key="item" class="tag-box">
                  <div
                    style="
                      padding: 5px;
                      border: 1px solid #ddd;
                      line-height: 1;
                      margin: 3px 5px;
                    "
                  >{{ (item < 10 ? '0' + item : item) + ':00' }}</div>
                  <i class="el-icon-remove remove-tag" @click="removeTime(index)"></i>
                </div>
                <el-popover placement="bottom" width="250" v-model="visible">
                  <div style="display:flex;align-items:center">
                    <el-select
                      v-model="formData.selectTimeArr"
                      placeholder="请选择"
                      style="width: 120px"
                      multiple
                      collapse-tags
                    >
                      <el-option
                        :label="item + '时'"
                        :value="item"
                        v-for="(item, index) in timeArr"
                        :key="index"
                      ></el-option>
                    </el-select>
                    <div
                      style="
                        text-align: right;
                        display:flex;
                        margin-left:10px;
                      "
                    >
                      <el-button size="mini" @click="cancalSelectTimeArr">取消</el-button>
                      <el-button type="primary" size="mini" @click="confirmSelectTimeArr">确定</el-button>
                    </div>
                  </div>
                  <span
                    slot="reference"
                    style="
                      color: #3e82ff;
                      font-size: 12px;
                      cursor: pointer;
                      display: inline-block;
                    "
                    @click.stop="openAddTag"
                  >添加</span>
                </el-popover>
              </div>
            </el-form-item>
          </div>
        </el-col>
      </el-row>
      <el-row style="padding: 20px 20px 0px 20px;" v-if="this.type == 1">
        <el-col :span="16" class="input-item">
          <div style="display: flex">
            <el-form-item
              label="概述："
              prop="comment"
            >
            <el-input
                type="textarea"
                :rows="4"
                v-model="formData.comment"
                maxlength="500"
                placeholder="请输入"
              ></el-input>
            </el-form-item>
          </div>
        </el-col>
      </el-row>
    </el-form>
    <!-- <div class="text-center mt-10" v-if="!isDisabled">
      <el-button type="primary" size="medium" class="w-110" @click="handleSave('')">确定保存</el-button>
    </div> -->

    <el-dialog :visible.sync="param.show" title="引入参数" width="660px" class="cust-dialog" :show-close="false"
               :close-on-click-modal="false">
      <div>
        <el-input v-model.trim="param.keyWordInput" @keyup.enter.native="doSearchParam" placeholder="输入关键字回车搜索" class="w-300" clearable>
          <i @click="doSearchParam" slot="suffix" class="el-input__icon el-icon-search f-cursor font-16 fw-blod" style="color: #dbdbdb;"></i>
        </el-input>
        <p class="mt-15"><span class="fw-blod font-16">常用参数</span> <span class="ml-10 text-gray">选中参数，点确定</span></p>
        <div>
          <el-tag class="f-cursor mt-15 mr-15" v-show="item.indexOf(param.keyWord)>-1" :type="param.selectTag===item?'':'info'" v-for="(item, index) in param.list" :key="index"
                  @click="param.selectTag=item"><span v-html="handleHighlightText(item)"></span></el-tag>
        </div>
      </div>
      <p class="text-gray mt-15" v-show="param.showEmpty">暂无数据</p>
      <p class="text-red mt-15" v-show="param.showErrorTip && param.selectTag===''">请先选择一个参数</p>
      <div class="text-right mt-50">
        <el-button size="small" class="mr-10" @click="param.show = false">取消</el-button>
        <el-button size="small" type="primary" @click="comfirmSelectParam()">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import custEditor from '../../components/common/cust-editor'
import addrSelector from '../../components/common/addrSelector'
export default {
  components: { custEditor, addrSelector },
  data () {
    return {
      isDisabled: false,
      formData: {
        id: '',
        messageType: '10026001', // 消息类型
        warnType: '', // 消息项目
        responseGrade: '10030001', // 响应等级
        messageTopic: '', // 消息主题
        sendWay: ['邮件', '短信'],
        emailContent: '', // 邮件正文
        smsContent: '', // 短信正文
        sendCust: 1, // 是否选中客户
        sendOperator: 1, // 是否选中运营人员
        sendPersonnel: 0, // 是否选中指定人员
        messageStrategy: 1, // 消息时效
        sendStrategy: 1, // 发送策略
        sendTime: '', // 发送时间
        emailWay: 1, // 通知方式 邮件是否选中
        smsWay: 1, // 通知方式 短信是否选中
        personnelList: [{ name: '', phoneNumber: '', email: '' }], // 指定人员数组
        operatorComment: '', // 是否通知运营备注信息
        personnelComment: '', // 是否通知指定人员备注信息
        custComment: '', // 是否通知客户备注信息
        timeArr: [],
        selectTimeArr: [],
        time1: '',
        time2: '',
        operatorList: [],
        custList: [],
        updateTime: '',
        updateName: '',
        businessType: '',
        addrId: '',
        addrName: '',
        status: '',
        comment: '',
        createName: '',
        createTime: '',
        files: []
      },
      option: {
        warnType: [], // 消息项目
        personType: [],
        messageType: [], // 消息类型
        responseGrade: [], // 响应等级
        businessType: [
          { name: '社保', value: 1 },
          { name: '公积金', value: 2 }
        ]
      },
      saveBtnLoading: false,
      timeArr: [],
      checked: false,
      visible: false,
      type: '1',
      customerArr: [],
      role: [],
      rules: {
        messageType: [{ required: true, message: '请选择', trigger: 'change' }],
        warnType: [{ required: true, message: '请选择', trigger: 'change' }],
        responseGrade: [{ required: true, message: '请选择', trigger: 'change' }],
        messageTopic: [{ required: true, message: '请输入消息主题', trigger: 'change' }],
        emailContent: [{ required: true, message: '请输入邮件内容', trigger: 'change' }],
        smsContent: [{ required: true, message: '请输入短信正文', trigger: 'change' }],
        sendWay: [{ required: true, message: '请选择', trigger: 'change', type: 'array' }],
        timeArr: [{ required: true, message: '请至少选择一个时间', trigger: 'change', type: 'array' }],
        time1: [{ required: true, message: '请选择时间', trigger: 'change' }],
        time2: [{ required: true, message: '请选择时间', trigger: 'change' }],
        operatorList: [{ required: true, validator: this.checkPersonnelList, trigger: 'change' }],
        email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] },
          { required: true, validator: this.checkEmail, trigger: 'change' }],
        name: [{ required: true, validator: this.checkName, trigger: 'change' }],
        phoneNumber: [{ required: true, validator: this.checkPhone, trigger: 'change' }],
        sendStrategy: [{ required: true, message: '请选择发送时间', trigger: 'change' }],
        businessType: [{ required: true, message: '请选择类型', trigger: 'change' }],
        addrName: [{ required: true, message: '请选择城市', trigger: 'change' }]
      },
      rowId: '',
      loading: null,
      addrArr: [],

      param: {
        show: false,
        type: '', // title: 主题  email: 邮件  message: 短信
        titleValCursor: 0, // 主题最后的光标位置
        messageValCursor: 0, // 短信最后的光标位置
        list: ['表格', '参保城市', '申报单位', '申报账号', '客户名称', '业务类型', '盒子编号', '关联标记',
          '申报类型', '异常原因', '主流程名称', '未申报数据量', '临期天数', '异常数据量', '提交数',
          '申报成功数', '申报失败数', '申报中数', '申报中-网站待审核数', '未申报数'],
        keyWordInput: '',
        keyWord: '',
        selectTag: '',
        showEmpty: false,
        showErrorTip: false
      }
    }
  },
  computed: {
    handleResponseGrade () {
      var str = ''
      this.option.responseGrade.forEach((item) => {
        if (item.dictCode == this.formData.responseGrade) {
          str = item.comment
        }
      })
      return str
    },
    getTime1 () {
      if (this.formData.time2 !== '') {
        var index = this.timeArr.indexOf(this.formData.time2)
        // if (index == 0) {
        //   index = 1
        // }
        return this.timeArr.slice(0, index)
      }
      return this.timeArr
    },
    getTime2 () {
      if (this.formData.time1 !== '') {
        var index = this.timeArr.indexOf(this.formData.time1)
        return this.timeArr.slice(index + 1, this.timeArr.length)
      }
      return this.timeArr
    },
    filterWarnType () {
      if (this.type == 2) {
        return this.option.warnType
      }
      var arr = this.option.warnType.filter(item => {
        return item.comment == this.formData.messageType
      })
      return arr
    },
    getOperatorListName () {
      return function (id) {
        var str = ''
        this.role.forEach(item => {
          if (item.id == id) {
            str = item.name
          }
        })
        return str
      }
    },
    getCustName () {
      return function (id) {
        var str = ''
        this.customerArr.forEach(item => {
          if (item.id == id) {
            str = item.name
          }
        })
        return str
      }
    }
  },
  watch: {
    'formData.messageTopic' (newVal, oldVal) {
      try {
        this.removeStr(newVal, oldVal, this.$refs.messageTopic.$refs.input, 'messageTopic')
      } catch (err) {
        console.log(err)
      }
    },
    'formData.smsContent' (newVal, oldVal) {
      try {
        this.removeStr(newVal, oldVal, this.$refs.smsContent.$refs.textarea, 'smsContent')
      } catch (err) {
        console.log(err)
      }
    }
    // 'formData.emailContent'(newVal,oldVal){
    //   // console.log(newVal.length,oldVal.length,newVal,oldVal)
    //   // if(newVal.length > oldVal.length){
    //   //   return
    //   // }
    //   var reg = /<(.*?)>|<(.*?)\/>/g
    //   oldVal = oldVal.replace(reg,'')
    //   console.log(oldVal[oldVal.length -1])
    //   this.$nextTick(()=>{
    //     var ref = this.$refs.custEditor.$refs.myQuillEditor.quill
    //     var index = ref.selection.savedRange.index - 1
    //     console.log(index,oldVal.length)
    //     var val = oldVal.substring(index,index - 1)
    //     console.log(val)
    //     if(val == '$'){

    //       var str = oldVal.substring(0,index - 1)
    //       console.log(str)
    //       str = str.split('').reverse().join('')
    //       var index2 = str.search(/\$/)
    //       var key = str.substring(0,index2)
    //       console.log(key)
    //       if(!key){
    //         return
    //       }
    //       key = key.split('').reverse().join('')
    //       if(this.param.list.indexOf(key) <= -1){
    //         return
    //       }
    //       console.log(new RegExp('\\$'+key,'gi'))
    //       console.log(key,this.formData.emailContent.match(new RegExp(key,'gi')))
    //       this.formData.emailContent = this.formData.emailContent.replace(new RegExp('\\$'+key,''),'')
    //       this.$nextTick(()=>{
    //         console.log(ref);
    //         ref.setSelection(index-key.length)
    //       })
    //     }
    //   })
    // },
  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.type = this.$route.query.type

    this.pathData.push({ name: '消息配置', path: '/controlAndReport/messageCenter' })
    // this.getWarnType()
    this.getPersonType()
    if (this.$route.query.id) {
      if (this.type == 1) {
        this.pathData.push({ name: '查看消息' })
      } else {
        this.pathData.push({ name: '查看公告' })
      }
      this.isDisabled = true
      this.formData.id = this.$route.query.id
      this.rowId = this.$route.query.id
      this.getDetail()
    } else {
      if (this.type == 1) {
        this.pathData.push({ name: '新建消息' })
      } else if (this.type == 2) {
        this.pathData.push({ name: '新增公告' })
      }
    }

    if (this.type == 1) {
      this.formData.messageType = '10026001'
      this.formData.sendWay = ['邮件', '短信']
      this.formData.emailWay = 1
      this.formData.smsWay = 1
    } else if (this.type == 2) {
      this.formData.messageType = '10028001'
      this.formData.sendWay = ['邮件']
      this.formData.emailWay = 1
      this.formData.smsWay = 0
    }

    for (let i = 0; i <= 23; i++) {
      this.timeArr.push(i)
    }
    this.getlistCustomer()
    this.getRole()
    this.getAddrArr()
    if (this.type == 1) {
      this.getDictByKey('10030')
      this.getDictByKey('10026')
      this.getDictByKey('10027')
    } else {
      this.getDictByKey('10028')
      this.getDictByKey('10029')
    }
    sessionStorage.setItem('messageCenterActive', this.type)
  },
  methods: {
    // 改变社保-参保地
    getSocialAddrId (item) {
      if (this.formData.addrId == item.id) {
        return false
      }
      this.formData.addrId = item.id
    },
    checkPersonnelList (rule, value, callback) {
      if (this.formData.sendOperator == 1 && value && value.length <= 0) {
        return callback('请至少选择一个角色')
      }
      return callback()
    },
    checkName (rule, value, callback) {
      if (this.formData.sendPersonnel != 1 || this.formData.sendWay.indexOf('短信') === -1) {
        return callback()
      }
      if (!value) {
        return callback(new Error('请输入人员姓名'))
      }
      return callback()
    },
    checkEmail (rule, value, callback) {
      if (this.formData.sendPersonnel != 1 || this.formData.sendWay.indexOf('邮件') === -1) {
        return callback()
      }
      if (!value) {
        return callback(new Error('请输入人员邮箱'))
      }
      return callback()
    },
    checkPhone (rule, value, callback) {
      if (this.formData.sendPersonnel != 1 || this.formData.sendWay.indexOf('短信') === -1) {
        return callback()
      }
      if (value.length < 11 || value.length > 11) {
        return callback(new Error('请输入正确的手机号码'))
      }
      return callback()
    },
    removeTag (index) {
      this.formData.operatorList.splice(index, 1)
    },
    removeCustTag (index) {
      this.formData.custList.splice(index, 1)
    },
    changeSenWay (val) {
      if (val.indexOf('邮件') > -1) {
        this.rules.emailContent[0].required = true
        this.formData.emailWay = 1
      } else {
        this.rules.emailContent[0].required = false
        this.formData.emailWay = 0
        this.$refs.formData.validateField('emailContent')
      }
      if (val.indexOf('短信') > -1) {
        this.rules.smsContent[0].required = true
        this.formData.smsWay = 1
      } else {
        this.rules.smsContent[0].required = false
        this.formData.smsWay = 0
        this.$refs.formData.validateField('smsContent')
      }
    },
    // 改变消息类型时，需联动消息项目,置空消息类目
    changeMessageType () {
      this.formData.warnType = ''
    },
    // 获取消息项目
    getWarnType () {
      let that = this
      this.$http({
        url: '/robot/data/dict/10017',
        method: 'post',
        data: {}
      })
        .then((data) => {
          that.option.warnType = data.data.data
        })
        .catch((err) => {})
    },
    // 获取通知对象
    getPersonType () {
      let that = this
      this.$http({
        url: '/robot/warn/person/getPersonType',
        method: 'post',
        data: {}
      })
        .then((data) => {
          that.option.personType = data.data.data.personListType
        })
        .catch((err) => {})
    },
    // 获取通知对象
    getDetail () {
      let that = this
      this.showLoading()
      this.$http({
        url: `/policy/message/rule/detail/${this.rowId}`,
        method: 'post'
      })
        .then((res) => {
          this.closeLoading()
          if (res.data.code == '200') {
            for (const key in this.formData) {
              if (res.data.data[key] !== '' && res.data.data[key] != null && res.data.data[key] != undefined) {
                this.formData[key] = res.data.data[key]
              }
            }
            console.log(this.formData)
            if (this.formData.sendStrategy == 1) {
              this.formData.time1 = Number(this.formData.sendTime.split('-')[0])
              this.formData.time2 = Number(this.formData.sendTime.split('-')[1])
            } else if (this.formData.sendStrategy == 2) {
              this.formData.selectTimeArr = this.formData.sendTime.split(',').map(item => {
                return Number(item.split(':')[0])
              })
              this.formData.timeArr = this.formData.selectTimeArr
            }
            this.formData.operatorList = this.formData.operatorList.map(item => {
              return item.roleId
            })
            this.formData.custList = this.formData.custList.map(item => {
              return item.custId
            })
            if (this.formData.personnelList.length == 0) {
              this.formData.personnelList = [{ name: '', phoneNumber: '', email: '' }]
            }
            this.formData.sendWay = []
            if (this.formData.emailWay == 1) {
              this.formData.sendWay.push('邮件')
            }
            if (this.formData.smsWay == 1) {
              this.formData.sendWay.push('短信')
            }
            this.changeSenWay(this.formData.sendWay)
          }
          // that.option.personType = data.data.data.personListType
        })
        .catch((err) => {
          this.closeLoading()
        })
    },
    handleSave (status) {
      let that = this
      that.saveBtnLoading = true
      let setObj = this.deepCopy(that.formData)
      if (setObj.sendCust == 0 && setObj.sendOperator == 0 && setObj.sendPersonnel == 0) {
        this.$message.warning('请至少选择一个通知对象')
        return
      }
      var arr = []
      this.role.forEach(item => {
        if (setObj.operatorList.indexOf(item.id) > -1) {
          arr.push({
            roleName: item.name,
            roleId: item.id
          })
        }
      })
      setObj.operatorList = arr

      var custList = []
      this.customerArr.forEach(item => {
        if (setObj.custList.indexOf(item.id) > -1) {
          custList.push({
            custName: item.name,
            custId: item.id
          })
        }
      })
      setObj.custList = custList

      if (setObj.messageStrategy == 1 && setObj.sendStrategy == 1) {
        setObj.sendTime = (this.formData.time1 < 10 ? '0' + this.formData.time1 : this.formData.time1) + '-' + (this.formData.time2 < 10 ? '0' + this.formData.time2 : this.formData.time2)
      } else if (setObj.messageStrategy == 1 && setObj.sendStrategy == 2) {
        setObj.sendTime = this.formData.selectTimeArr.map(item => item < 10 ? `0${item}:00` : `${item}:00`).join(',')
      }
      setObj.ruleType = Number(this.type)
      setObj.personnelList = setObj.personnelList.filter(item => {
        return item.email || item.phoneNumber || item.name
      })
      if (this.type == 1) {
        setObj.status = setObj.status
      } else if (this.type == 2) {
        setObj.status = status !== '' ? status : 1
      }

      if (setObj.sendOperator == 0) {
        setObj.operatorList = []
      }
      if (setObj.sendCust == 0) {
        setObj.custList = []
      }
      console.log(setObj)
      // return
      this.$refs.formData.validate((valid, errorMessage) => {
        if (valid) {
          if (setObj.status == 1 && this.type == 2) {
            this.$confirm('是否确定内容准确无误，可以发布？', '提示', {
              confirmButtonText: '确定发布',
              cancelButtonText: '取消',
              showClose: false,
              customClass: 'pal-confirm',
              type: 'warning'
            })
              .then(() => {
                this.saveAll(setObj)
              })
              .catch(() => { })
          } else {
            this.saveAll(setObj)
          }
        }
      })
    },
    onEditorBlur () {
      this.$refs.formData.validateField('emailContent')
    },
    handleEdit () {
      this.isDisabled = false
      this.pathData[this.pathData.length - 1].name = '编辑消息'
      console.log(this.pathData)
    },
    remove (index) {
      this.formData.personnelList.splice(index, 1)
    },
    add () {
      this.formData.personnelList.push({ name: '', phoneNumber: '', email: '' })
    },
    cancalSelectTimeArr () {
      // this.formData.selectTimeArr = []
      this.visible = false
    },
    confirmSelectTimeArr () {
      this.formData.selectTimeArr.sort((a, b) => {
        return a - b
      })
      this.formData.timeArr = this.deepCopy(this.formData.selectTimeArr)
      // this.$refs.popoverForm.validateField('timeArr')
      this.visible = false
    },
    openAddTag () {
      setTimeout(() => {
        this.visible = true
      }, 200)
    },
    closeTag () {
      this.visible = false
    },
    removeTime (index) {
      this.formData.timeArr.splice(index, 1)
      this.formData.selectTimeArr = this.deepCopy(this.formData.timeArr)
      // this.$refs.popoverForm.validateField('timeArr')
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
    // 获取险种字典，用与参保险种，按返回数据排序
    getDictByKey (key) {
      return this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: key
        }
      })
        .then((res) => {
          if (key == '10026' || key == '10028') {
            this.option.messageType = res.data.data
          }
          if (key == '10027' || key == '10029') {
            this.option.warnType = res.data.data
            if (this.option.warnType.length == 1) {
              this.formData.warnType = this.option.warnType[0].dictCode
            }
          }
          if (key == '10030') {
            this.option.responseGrade = res.data.data
          }
        })
        .catch((err) => {
          this.$message.error('字典接口出错，请稍后再试')
        })
    },
    getDictByKeyComment (dataKey, comment) {
      return this.$http({
        url: `policy/sys/dict/getDict/${dataKey}`,
        method: 'get',
        params: {
          comment: comment
        }
      })
        .then((res) => {
          this.option[dataKey] = res.data.data
        })
        .catch((err) => {
          console.log(err)
          this.$message.error('字典接口出错，请稍后再试')
        })
    },
    // 获取客户
    getlistCustomer () {
      this.$http({
        url: '/policy/sys/customer/listAllCustomer?filter=true&status=1',
        method: 'post'
      }).then((data) => {
        this.customerArr = data.data.data
      }).catch((err) => {
      })
    },
    getRole () {
      this.$http({
        url: '/sys/role/page',
        method: 'post',
        data: {
          'page': 1,
          'start': 0,
          'size': 9999,
          'query': [
            {
              'property': 'searchText',
              'value': ''
            }
          ],
          'sidx': '',
          'sort': ''
        }
      })
        .then((res) => {
          this.role = res.data.data.rows
        })
        .catch((err) => {
          console.log(err)
          this.$message.error('字典接口出错，请稍后再试')
        })
    },
    // 获取参保地
    getAddrArr () {
      this.$http({
        url: '/policy/declareAddr/addrList',
        method: 'post'
      })
        .then(({ data }) => {
          this.addrArr = data.data
        })
        .catch((data) => {})
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

    handleParamShow (type) {
      this.param.show = true
      this.param.type = type
    },
    inputParamBlur (val, field) {
      this.param[field] = val.srcElement.selectionStart
    },
    doSearchParam () {
      this.param.keyWord = this.param.keyWordInput
      this.param.selectTag = ''
      if (this.param.keyWordInput === '') {
        this.param.showEmpty = false
      } else {
        for (let i = 0; i < this.param.list.length; i++) {
          if (this.param.list[i].indexOf(this.param.keyWordInput) > -1) {
            this.param.showEmpty = false
            return
          }
        }
        this.param.showEmpty = true
      }
    },
    handleHighlightText (text) {
      const content = text
      const regex = new RegExp(this.param.keyWord, 'gi')
      const highlightedContent = content.replace(regex, match => `<span class="text-red">${match}</span>`)
      return highlightedContent
    },
    comfirmSelectParam () {
      this.param.showErrorTip = true
      if (this.param.selectTag) {
        this.param.show = false
        let selectTag = '$' + this.param.selectTag + '$'
        console.log(selectTag)
        if (this.param.type === 'email') {
          this.$refs.custEditor.insertTemplate(selectTag)
        } else if (this.param.type === 'title') {
          this.formData.messageTopic = this.insertStr(this.formData.messageTopic, this.param.titleValCursor, selectTag)
          this.param.titleValCursor = this.param.titleValCursor + selectTag.length
        } else if (this.param.type === 'message') {
          this.formData.smsContent = this.insertStr(this.formData.smsContent, this.param.messageValCursor, selectTag)
          this.param.messageValCursor = this.param.titleValCursor + selectTag.length
        }
      }
    },
    insertStr (str1, n, str2) {
      var s1 = ''
      var s2 = ''
      if (str1.length < n) {
        return str1 + str2
      } else {
        s1 = str1.substring(0, n)
        s2 = str1.substring(n, str1.length)
        return s1 + str2 + s2
      }
    },
    saveAll (setObj) {
      this.showLoading()
      this.$http({
        url: '/policy/message/rule/save',
        method: 'post',
        data: setObj
      })
        .then((data) => {
          this.closeLoading()
          if (data && data.data.code == '200') {
            this.$message.success('操作成功')
            sessionStorage.setItem('messageCenterActive', this.type)
            this.$router.push('/controlAndReport/messageCenter')
          }
        })
        .catch((err) => {
          this.saveBtnLoading = false
          this.closeLoading()
        })
    },
    removeStr (newVal, oldVal, el, keyWord) {
      if (newVal.length > oldVal.length) {
        return
      }
      this.$nextTick(() => {
        var index = el.selectionStart
        var val = oldVal.substring(index, index + 1)
        if (val == '$') {
          // var reg = /\$(.*?)\$/g
          var str = oldVal.substring(0, index)
          str = str.split('').reverse().join('')
          var index2 = str.search(/\$/)
          var key = str.substring(0, index2)
          if (!key) {
            return
          }
          key = key.split('').reverse().join('')
          if (this.param.list.indexOf(key) <= -1) {
            return
          }
          str = str.substring(index2 + 1, str.length).split('').reverse().join('')
          var str3 = str + oldVal.substring(index + 1, oldVal.length)
          this.formData[keyWord] = str3
          this.$nextTick(() => {
            el.setSelectionRange(index - key.length - 1, index - key.length - 1)
          })
        }
      })
    },
    onSuccessFile (response) {
      if (response.code === 200) {
        let item = response.data[0]
        this.formData.files.push({
          fileId: item.fileID,
          clientFileName: item.fileName
        })
      }
    },
    delFile (index) {
      this.formData.files.splice(index, 1)
    },
    downloadFile (fileId) {
      this.$downloadFile(`/policy/sys/file/download/${fileId}`, 'get')
    }
  }
}
</script>

<style lang="less" scoped>
.lab {
  line-height: 36px;
  min-width: 150px;
  text-align: right;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  cursor: pointer;
}
.el-form-item {
  flex: 1;
  margin-bottom: 20px;
}

.icon-check {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: #4caf50;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}
.my-required:before {
  content: '*';
  display: inline-block;
  color: red;
  margin-right: 5px;
}
.tips-content {
  display: flex;
  padding: 20px;
  flex-direction: column;
}
.ellipsis {
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  cursor: pointer;
  box-sizing: border-box;
}
.pl-20 {
  padding-left: 20px;
}
.font-15 {
  font-size: 15px;
}
.font-14 {
  font-size: 14px;
}
/deep/.width-260 {
  max-width: 210px !important;
  .el-popover__reference {
    max-width: 210px !important;
  }
}
/deep/.ic-addr {
  margin-top: 13px !important;
}
/deep/.addr-main input::input-placeholder {
  color: #f56c6c !important;
}
/deep/.addr-main input::-moz-placeholder {
  color: #f56c6c !important;
}
/deep/.addr-main input::-webkit-input-placeholder {
  color: #f56c6c !important;
}
.notice-table {
  margin-left: 65px;
  width: calc(100% - 95px);
  border: 1px solid #e4e4e4;
  border-right: none;
  border-bottom: none;
  th {
    border-bottom: 1px solid #e4e4e4;
    border-right: 1px solid #e4e4e4;
    font-weight: 400;
  }
  td {
    padding: 15px 10px;
    box-sizing: border-box;
    border-bottom: 1px solid #e4e4e4;
    border-right: 1px solid #e4e4e4;
    font-weight: 400;
  }
  .table-cell {
    margin-left: 35%;
  }
}
.tag-box {
  position: relative;
  cursor: pointer;
  .remove-tag {
    position: absolute;
    top: -6px;
    right: -3px;
    font-size: 18px;
    color: #f56c6c;
    cursor: pointer;
    display: none;
  }
  &:hover .remove-tag {
    display: block;
  }
}
.elTag{
  // transition: all .3s;
  // position: absolute;
  // transform: translateY(-50%);
  // &:hover{
  //   transform: translateY(-60%);
  //   z-index: 9999;
  //   cursor: pointer;
  // }
}
.operatorList{
  /deep/.el-form-item{
    margin-bottom: 0px;
  }
}
.personnelList{
  /deep/.el-form-item{
    margin-bottom: 0px;
  }
}
.guide-text{
  display: inline-block;
  border: 1px solid @blueColor;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  color: @blueColor;
  margin-top: 3px;
  cursor: pointer;
  margin-left: 10px;
}
</style>
