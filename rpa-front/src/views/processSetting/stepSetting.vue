<template>
  <div class="spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData">
      <template slot="breadcrumb-btn" v-if="isStepErrorMsg == ''">
        <el-select v-if="$route.query.appCode != 'null'" class="w-300" v-model="selectStartTask" value-key="taskCode"
          placeholder="请先选择申报账户再运行" clearable filterable>
          <el-option v-for="(item, index) in allTask" :key="index"
            :label="item.companyName + '（' + item.accountNumber + '）'" :value="item"></el-option>
        </el-select>
        <el-button v-if="$route.query.appCode != 'null'" type="primary" size="small"
          style="margin-right: 10px;margin-left: 10px;" @click="handleStartTask()">运行</el-button>
        <!--<el-button class="btn&#45;&#45;border-blue mr-20" size="small" @click="startFlow()">运行</el-button>-->
        <el-button type="primary" size="small" v-if="!appDetail.relationFlowCode" @click="handleSave()">保存</el-button>
      </template>
    </breadcrumb>

    <div class="pd-20" v-loading="isloadingPage" style="min-height: 200px">
      <div class="pb-20">
        <el-row>
          <el-col :span="7" v-if="appDetail.appName">应用名称： {{ appDetail.appName }}</el-col>
          <el-col :span="5" v-if="appDetail.businessType">应用类型： {{ appDetail.businessType }}</el-col>
          <el-col :span="7">流程名称： {{ appDetail.flowName }}</el-col>
          <el-col :span="5">流程类型： {{ appDetail.flowType }}</el-col>
        </el-row>
        <el-row class="mt-10">
          <el-col :span="7">流程状态：{{ $route.query.statusName }}</el-col>
          <el-col :span="5">原因备注：{{ $route.query.comment == 'null' ? '' : $route.query.comment }}</el-col>
        </el-row>
      </div>
      <div style="display: flex;" v-if="isStepErrorMsg == ''">
        <div class="area area-l">
          <p class="title">命令中心</p>
          <div class="area-border">
            <div class="mt-15" v-for="(item, index) in commandList" :key="index">
              <div class="f-cursor" @click="item.isOpen = !item.isOpen">
                <i class="ic-toggle"
                  :class="{ 'el-icon-caret-bottom': item.isOpen, 'el-icon-caret-right': !item.isOpen }"></i>
                {{ item.groupName }}
              </div>
              <div class="command-list" :class="{ 'open': item.isOpen }">
                <div class="mt-10 mb-5">
                  <draggable v-model="item.list"
                    :options="{ group: { name: 'itxst', pull: 'clone', put: false }, sort: false }" animation="300"
                    :move="(e, originalEvent) => onMoveCommand(e, originalEvent, index)" :disabled="isTest">
                    <transition-group>
                      <div class="item" v-for="(it, ind) in item.list" :key="ind">{{ it.actionName }}</div>
                    </transition-group>
                  </draggable>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="area area-c">
          <p class="title">步骤
            <span>({{ getAllStepCount() }})</span>
            <i v-if="stepListForm.stepList.length === 0 && !isTest"
              class="el-icon-circle-plus-outline text-blue font-18 f-cursor ml-5" style="vertical-align: middle;"
              title="插入步骤" @click.stop="handleAddOptStep(0, '')"></i>
          </p>
          <!-- :key="refeshKey"-->
          <div class="area-border">
            <!-- v-model="stepListForm.stepList"-->
            <draggable v-model="stepListForm.stepList" group="itxst" animation="300" :disabled="isTest" @add="addStep"
              :move="onMoveStep" @sort="resetSelectStep" style="height: 100%;">
              <transition-group class="stepArea">
                <div v-for="(item, index) in stepListForm.stepList" :key="index">
                  <div class="step-list">
                    <div class="step-item"
                      :class="{ 'active': (curSelectEditInd === '' || curSelectEditInd == undefined || curSelectEditInd == 'undefined') && curSelectEditIndex === index }"
                      @click="showStepArgs(index)">
                      <!--                      <span class="step-name">{{item.groupName}} </span>-->
                      <span class="comment">{{ item.stepName }}</span>
                      <span class="step-name">
                        <i class="el-icon-info text-orange font-16" style="vertical-align: middle;margin-top: -2px;"
                          v-if="item.skipTo == '' && (item.actionName == '条件判断' || item.actionName == '遍历器')"></i>
                        {{ item.actionName }}</span>
                      <div class="del-btn">
                        <i v-show="selectStartTask" class="el-icon-video-play text-green font-18 f-cursor mr-10"
                          title="运行" @click.stop="handleStartTaskStep(item)"></i>
                        <i v-if="!isTest" class="el-icon-circle-plus-outline text-blue font-18 f-cursor mr-10"
                          title="插入步骤" @click.stop="handleAddOptStep(index, '')"></i>
                        <i v-if="!isTest" class="el-icon-delete text-red font-18 f-cursor" title="删除"
                          @click.stop="delStep(index)"></i>
                      </div>
                    </div>
                    <!--子步骤-->
                    <div class="sub-list" :style="{ 'padding-top': item.list && item.list.length > 0 ? '10px' : '0' }">
                      <!-- v-model="item.list"-->
                      <draggable v-model="item.list" group="itxst" animation="300" :disabled="isTest"
                        @add="(e) => addStepSub(e, index)"
                        :move="(e, originalEvent) => onMoveStepSub(e, originalEvent, index)" @sort="resetSelectStep"
                        style="display: inline-block;width: 100%;">
                        <transition-group
                          style="display: block; min-height: 5px;width: 100%;padding-left: 30px;box-sizing: border-box">
                          <div v-for="(it, ind) in item.list" :key="ind">
                            <div class="step-item"
                              :class="{ 'active': curSelectEditIndex === index && curSelectEditInd === ind }"
                              @click.stop="showStepArgs(index, ind)">
                              <span class="comment">{{ it.stepName }}</span>
                              <span class="step-name"><i class="el-icon-info text-orange font-16"
                                  style="vertical-align: middle;margin-top: -2px;"
                                  v-if="it.skipTo == '' && (it.actionName == '条件判断' || it.actionName == '遍历器')"></i>
                                {{ it.actionName }} </span>
                              <div class="del-btn">
                                <i v-show="selectStartTask" class="el-icon-video-play text-blue font-18 f-cursor mr-10"
                                  title="运行" @click.stop="handleStartTaskStep(it)"></i>
                                <i class="el-icon-circle-plus-outline text-blue font-18 f-cursor mr-10" title="插入步骤"
                                  @click.stop="handleAddOptStep(index, ind)"></i>
                                <i class="el-icon-delete text-red font-18 f-cursor" title="删除"
                                  @click.stop="delStep(index, ind)"></i>
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
        <div class="area area1-r">
          <p class="title">属性</p>
          <div class="area-border" ref="argsArea" :key="refeshKey">
            <el-form :inline="true" :model="stepListForm" ref="stepListForm" label-width="0" :disabled="isTest">
              <div v-if="curSelectEditIndex !== ''">
                <div v-for="(item, index) in stepListForm.stepList" :key="index">
                  <!--主步骤-->
                  <div
                    v-if="(curSelectEditInd === '' || curSelectEditInd == undefined || curSelectEditInd == 'undefined') && curSelectEditIndex === index">
                    <div class="mb-15">
                      <el-form-item :prop="'stepList.' + index + '.stepName'"
                        :rules="[{ required: true, message: '请输入步骤名称', trigger: 'blur' }]">
                        <el-input v-model="item.stepName" maxlength="50" placeholder="请输入步骤名称，必填"
                          @blur="handleStepOptions"></el-input>
                      </el-form-item>
                    </div>
                    <div class="property-div" v-if="item.targetArgsVOS && item.targetArgsVOS.length > 0">
                      <p class="title">操作目标</p>
                      <div>
                        <table cellpadding="0" cellspacing="0">
                          <tbody>
                            <!--displayType: text、date、number、select-->
                            <!--fieldType: text,number,date,singleDropList,single-->
                            <tr v-for="(it, ind) in item.targetArgsVOS" :key="ind" v-show="!it.isHideCust">
                              <td><span :class="{ 'required-pre': it.required === 1 }">{{ it.fieldName }}</span>
                                <i class="el-icon-warning-outline ml-5 text-orange" v-if="it.comment"
                                  :title="it.comment"></i>
                              </td>
                              <td>
                                <div class="text-div">
                                  <el-form-item :prop="'stepList.' + index + '.targetArgsVOS.' + ind + '.fieldValue'"
                                    class="flex1"
                                    :rules="[{ validator: (rule, value, callback) => checkDynamicField(rule, value, callback, it), trigger: 'blur' },
                                    { validator: (rule, value, callback) => checkDynamicField(rule, value, callback, it), trigger: 'change' }]">
                                    <el-input v-if="it.fieldType == 'text' || it.fieldType == 'number'"
                                      v-model="it.fieldValue" :placeholder="it.comment ? '' : '请输入'"></el-input>

                                    <el-date-picker v-else-if="it.fieldType == 'date'" v-model="it.fieldValue" type="date"
                                      clearable value-format="yyyy-MM-dd"
                                      :placeholder="it.comment ? '' : '请选择'"></el-date-picker>

                                    <el-select v-else-if="it.fieldType == 'singleDropList'" v-model="it.fieldValue"
                                      @change="changeSingleDropList(index, ind, '', 'targetArgsVOS')"
                                      :placeholder="it.comment ? '' : '请选择'" value-key="dictCode" clearable filterable>
                                      <el-option v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)"
                                        :key="optIndex" :label="optItem.dictName" :value="optItem.dictCode"></el-option>
                                    </el-select>

                                    <div v-else-if="it.fieldType == 'single'">
                                      <el-radio v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)"
                                        :key="optIndex" v-model="it.fieldValue" :label="optItem.dictCode">{{
                                          optItem.dictName }}</el-radio>
                                    </div>
                                  </el-form-item>

                                  <i v-if="it.fieldType == 'text' && !isTest" class="el-icon-s-promotion ic-edit-input"
                                    :class="{ 'text-blue': handleCollectIcon(index, ind, '', 'targetArgsVOS') }"
                                    @click="doCollect(index, ind, '', 'targetArgsVOS')"></i>
                                  <i v-if="it.fieldType == 'text' && !isTest" class="el-icon-edit-outline ic-edit-input"
                                    @click="showEditDialog(index, ind, '', 'targetArgsVOS')"></i>
                                </div>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                    </div>
                    <div class="property-div" v-if="item.actionArgsVOS && item.actionArgsVOS.length > 0">
                      <p class="title">行为参数</p>
                      <div>
                        <table cellpadding="0" cellspacing="0">
                          <tbody>
                            <!--displayType: text、date、number、select-->
                            <!--fieldType: text,number,date,singleDropList,single-->
                            <tr v-for="(it, ind) in item.actionArgsVOS" :key="ind" v-show="!it.isHideCust">
                              <td><span :class="{ 'required-pre': it.required === 1 }">{{ it.fieldName }}</span>
                                <i class="el-icon-warning-outline ml-5 text-orange" v-if="it.comment"
                                  :title="it.comment"></i>
                              </td>
                              <td>
                                <div class="text-div">
                                  <el-form-item :prop="'stepList.' + index + '.actionArgsVOS.' + ind + '.fieldValue'"
                                    :rules="[{ validator: (rule, value, callback) => checkDynamicField(rule, value, callback, it), trigger: 'blur' },
                                    { validator: (rule, value, callback) => checkDynamicField(rule, value, callback, it), trigger: 'change' }]">
                                    <el-input v-if="it.fieldType == 'text' || it.fieldType == 'number'"
                                      v-model="it.fieldValue" :placeholder="it.comment ? '' : '请输入'"></el-input>

                                    <el-date-picker v-else-if="it.fieldType == 'date'" v-model="it.fieldValue" type="date"
                                      clearable value-format="yyyy-MM-dd"
                                      :placeholder="it.comment ? '' : '请选择'"></el-date-picker>

                                    <el-select v-else-if="it.fieldType == 'singleDropList'" v-model="it.fieldValue"
                                      @change="changeSingleDropList(index, ind, '', 'actionArgsVOS')"
                                      :placeholder="it.comment ? '' : '请选择'" value-key="dictCode" clearable filterable>
                                      <el-option v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)"
                                        :key="optIndex" :label="optItem.dictName" :value="optItem.dictCode"></el-option>
                                    </el-select>

                                    <div v-else-if="it.fieldType == 'single'">
                                      <el-radio v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)"
                                        :key="optIndex" v-model="it.fieldValue" :label="optItem.dictCode">{{
                                          optItem.dictName }}</el-radio>
                                    </div>
                                  </el-form-item>
                                  <i v-if="it.fieldType == 'text'" class="el-icon-s-promotion ic-edit-input"
                                    :class="{ 'text-blue': handleCollectIcon(index, ind, '', 'actionArgsVOS') }"
                                    @click="doCollect(index, ind, '', 'actionArgsVOS')"></i>
                                  <i v-if="it.fieldType == 'text'" class="el-icon-edit-outline ic-edit-input"
                                    @click="showEditDialog(index, ind, '', 'actionArgsVOS')"></i>
                                </div>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                    </div>
                    <div class="property-div">
                      <p class="title">流转控制</p>
                      <div>
                        <table cellpadding="0" cellspacing="0">
                          <tbody>
                            <tr>
                              <td><span>使用状态</span></td>
                              <td style="height: 30px;">
                                <!--使用状态：1启用，0禁用。-->
                                <el-radio v-model="item.status" :label="1">启用</el-radio>
                                <el-radio v-model="item.status" :label="0">禁用</el-radio>
                              </td>
                            </tr>
                            <tr>
                              <td><span>成功后跳转</span></td>
                              <td>
                                <el-select v-model="item.skipTo" placeholder="请选择" clearable filterable>
                                  <el-option v-for="(optItem, optIndex) in stepOptions" :key="optIndex" :label="optItem"
                                    :value="optItem"></el-option>
                                </el-select>
                              </td>
                            </tr>
                            <tr>
                              <td><span>失败后跳转</span></td>
                              <td>
                                <el-select v-model="item.failedSkipTo" placeholder="请选择" clearable filterable>
                                  <el-option v-for="(optItem, optIndex) in stepOptions" :key="optIndex" :label="optItem"
                                    :value="optItem"></el-option>
                                </el-select>
                              </td>
                            </tr>
                            <tr>
                              <td><span> 跳过此步骤条件</span></td>
                              <td>
                                <el-input v-model="item.skipCondition" placeholder="请输入"></el-input>
                              </td>
                            </tr>
                            <tr>
                              <td><span>执行超时(s)</span></td>
                              <td>
                                <el-input v-model="item.timeout" placeholder="10秒"></el-input>
                              </td>
                            </tr>
                            <tr>
                              <td><span>如果执行失败</span></td>
                              <td style="height: 30px;">
                                <el-radio v-model="item.failedStrategy" :label="0"
                                  style="margin-right: 25px;">终止</el-radio>
                                <el-radio v-model="item.failedStrategy" :label="1"
                                  style="margin-right: 25px;">忽略</el-radio>
                                <el-radio v-model="item.failedStrategy" :label="2"
                                  style="margin-right: 0px;">跳转</el-radio>
                              </td>
                            </tr>
                            <tr>
                              <td><span>失败重试次数</span></td>
                              <td>
                                <el-input v-model="item.failedRetry" placeholder="不重试"></el-input>
                              </td>
                            </tr>
                            <tr>
                              <td><span>执行前等待(s)</span></td>
                              <td>
                                <el-input v-model="item.waitBefore" placeholder="0"></el-input>
                              </td>
                            </tr>
                            <tr>
                              <td><span>执行后等待(s)</span></td>
                              <td>
                                <el-input v-model="item.waitAfter" placeholder="0"></el-input>
                              </td>
                            </tr>
                            <tr>
                              <td><span>最大执行次数</span></td>
                              <td>
                                <el-input v-model.number="item.maxExecuteNum" placeholder="1"></el-input>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                  <!--子步骤-->
                  <div v-for="(subItem, subIndex) in item.list" :key="subIndex">
                    <div v-if="curSelectEditIndex === index && curSelectEditInd === subIndex">
                      <div class="mb-15">
                        <el-form-item :prop="'stepList.' + index + '.list.' + subIndex + '.stepName'"
                          :rules="[{ required: true, message: '请输入步骤名称', trigger: 'blur' }]">
                          <el-input v-model="subItem.stepName" maxlength="50" placeholder="请输入步骤名称，必填"
                            @blur="handleStepOptions"></el-input>
                        </el-form-item>
                      </div>
                      <div class="property-div" v-if="subItem.targetArgsVOS && subItem.targetArgsVOS.length > 0">
                        <p class="title">操作目标</p>
                        <div>
                          <table cellpadding="0" cellspacing="0">
                            <tbody>
                              <!--displayType: text、date、number、select-->
                              <!--fieldType: text,number,date,singleDropList,single-->
                              <tr v-for="(it, ind) in subItem.targetArgsVOS" :key="ind" v-show="!it.isHideCust">
                                <td><span :class="{ 'required-pre': it.required === 1 }">{{ it.fieldName }}</span>
                                  <i class="el-icon-warning-outline ml-5 text-orange" v-if="it.comment"
                                    :title="it.comment"></i>
                                </td>
                                <td>
                                  <div class="text-div">
                                    <el-form-item
                                      :prop="'stepList.' + index + '.list.' + subIndex + '.targetArgsVOS.' + ind + '.fieldValue'"
                                      :rules="[{ validator: (rule, value, callback) => checkDynamicField(rule, value, callback, it), trigger: 'blur' },
                                      { validator: (rule, value, callback) => checkDynamicField(rule, value, callback, it), trigger: 'change' }]">
                                      <el-input v-if="it.fieldType == 'text' || it.fieldType == 'number'"
                                        v-model="it.fieldValue" :placeholder="it.comment ? '' : '请输入'"></el-input>

                                      <el-date-picker v-else-if="it.fieldType == 'date'" v-model="it.fieldValue"
                                        type="date" value-format="yyyy-MM-dd" :placeholder="it.comment ? '' : '请选择'"
                                        clearable></el-date-picker>

                                      <el-select v-else-if="it.fieldType == 'singleDropList'" v-model="it.fieldValue"
                                        @change="changeSingleDropList(index, subIndex, ind, 'targetArgsVOS')"
                                        :placeholder="it.comment ? '' : '请选择'" value-key="dictCode" clearable filterable>
                                        <el-option v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)"
                                          :key="optIndex" :label="optItem.dictName" :value="optItem.dictCode"></el-option>
                                      </el-select>

                                      <div v-else-if="it.fieldType == 'single'">
                                        <el-radio v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)"
                                          :key="optIndex" v-model="it.fieldValue" :label="optItem.dictCode">{{
                                            optItem.dictName }}</el-radio>
                                      </div>
                                    </el-form-item>
                                    <i v-if="it.fieldType == 'text'" class="el-icon-s-promotion ic-edit-input"
                                      :class="{ 'text-blue': handleCollectIcon(index, subIndex, ind, 'targetArgsVOS') }"
                                      @click="doCollect(index, subIndex, ind, 'targetArgsVOS')"></i>
                                    <i v-if="it.fieldType == 'text'" class="el-icon-edit-outline ic-edit-input"
                                      @click="showEditDialog(index, subIndex, ind, 'targetArgsVOS')"></i>
                                  </div>
                                </td>
                              </tr>
                            </tbody>
                          </table>
                        </div>
                      </div>
                      <div class="property-div" v-if="subItem.actionArgsVOS && subItem.actionArgsVOS.length > 0">
                        <p class="title">行为参数</p>
                        <div>
                          <table cellpadding="0" cellspacing="0">
                            <tbody>
                              <!--displayType: text、date、number、select-->
                              <!--fieldType: text,number,date,singleDropList,single-->
                              <tr v-for="(it, ind) in subItem.actionArgsVOS" :key="ind" v-show="!it.isHideCust">
                                <td><span :class="{ 'required-pre': it.required === 1 }">{{ it.fieldName }}</span>
                                  <i class="el-icon-warning-outline ml-5 text-orange" v-if="it.comment"
                                    :title="it.comment"></i>
                                </td>
                                <td>
                                  <div class="text-div">
                                    <el-form-item
                                      :prop="'stepList.' + index + '.list.' + subIndex + '.actionArgsVOS.' + ind + '.fieldValue'"
                                      :rules="[{ validator: (rule, value, callback) => checkDynamicField(rule, value, callback, it), trigger: 'blur' },
                                      { validator: (rule, value, callback) => checkDynamicField(rule, value, callback, it), trigger: 'change' }]">
                                      <el-input v-if="it.fieldType == 'text' || it.fieldType == 'number'"
                                        v-model="it.fieldValue" :placeholder="it.comment ? '' : '请输入'"></el-input>

                                      <el-date-picker v-else-if="it.fieldType == 'date'" v-model="it.fieldValue"
                                        type="date" clearable value-format="yyyy-MM-dd"
                                        :placeholder="it.comment ? '' : '请选择'"></el-date-picker>

                                      <el-select v-else-if="it.fieldType == 'singleDropList'"
                                        @change="changeSingleDropList(index, subIndex, ind, 'actionArgsVOS')"
                                        v-model="it.fieldValue" :placeholder="it.comment ? '' : '请选择'"
                                        value-key="dictCode" clearable filterable>
                                        <el-option v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)"
                                          :key="optIndex" :label="optItem.dictName" :value="optItem.dictCode"></el-option>
                                      </el-select>

                                      <div v-else-if="it.fieldType == 'single'">
                                        <el-radio v-for="(optItem, optIndex) in handleOptions(it.robotDataDicts)"
                                          :key="optIndex" v-model="it.fieldValue" :label="optItem.dictCode">{{
                                            optItem.dictName }}</el-radio>
                                      </div>
                                    </el-form-item>
                                    <i v-if="it.fieldType == 'text'" class="el-icon-s-promotion ic-edit-input"
                                      :class="{ 'text-blue': handleCollectIcon(index, subIndex, ind, 'actionArgsVOS') }"
                                      @click="doCollect(index, subIndex, ind, 'actionArgsVOS')"></i>
                                    <i v-if="it.fieldType == 'text'" class="el-icon-edit-outline ic-edit-input"
                                      @click="showEditDialog(index, subIndex, ind, 'actionArgsVOS')"></i>
                                  </div>
                                </td>
                              </tr>
                            </tbody>
                          </table>
                        </div>
                      </div>
                      <div class="property-div">
                        <p class="title">流转控制</p>
                        <div>
                          <table cellpadding="0" cellspacing="0">
                            <tbody>
                              <tr>
                                <td><span>使用状态</span></td>
                                <td style="height: 30px;">
                                  <!--使用状态：1启用，0禁用。-->
                                  <el-radio v-model="subItem.status" :label="1">启用</el-radio>
                                  <el-radio v-model="subItem.status" :label="0">禁用</el-radio>
                                </td>
                              </tr>
                              <tr>
                                <td><span>成功后跳转</span></td>
                                <td>
                                  <el-select v-model="subItem.skipTo" placeholder="请选择" clearable filterable>
                                    <el-option v-for="(optItem, optIndex) in stepOptions" :key="optIndex" :label="optItem"
                                      :value="optItem"></el-option>
                                  </el-select>
                                </td>
                              </tr>
                              <tr>
                                <td><span>失败后跳转</span></td>
                                <td>
                                  <el-select v-model="subItem.failedSkipTo" placeholder="请选择" clearable filterable>
                                    <el-option v-for="(optItem, optIndex) in stepOptions" :key="optIndex" :label="optItem"
                                      :value="optItem"></el-option>
                                  </el-select>
                                </td>
                              </tr>
                              <tr>
                                <td><span> 跳过此步骤条件</span></td>
                                <td>
                                  <el-input v-model="subItem.skipCondition" placeholder="请输入"></el-input>
                                </td>
                              </tr>
                              <tr>
                                <td><span>执行超时(s)</span></td>
                                <td>
                                  <el-input v-model="subItem.timeout" placeholder="10秒"></el-input>
                                </td>
                              </tr>
                              <tr>
                                <td><span>如果执行失败</span></td>
                                <td style="height: 30px;">
                                  <el-radio v-model="subItem.failedStrategy" :label="0"
                                    style="margin-right: 25px;">终止</el-radio>
                                  <el-radio v-model="subItem.failedStrategy" :label="1"
                                    style="margin-right: 25px;">忽略</el-radio>
                                  <el-radio v-model="subItem.failedStrategy" :label="2"
                                    style="margin-right: 0px;">跳转</el-radio>
                                </td>
                              </tr>
                              <tr>
                                <td><span>失败重试次数</span></td>
                                <td>
                                  <el-input v-model="subItem.failedRetry" placeholder="不重试"></el-input>
                                </td>
                              </tr>
                              <tr>
                                <td><span>执行前等待(s)</span></td>
                                <td>
                                  <el-input v-model="subItem.waitBefore" placeholder="0"></el-input>
                                </td>
                              </tr>
                              <tr>
                                <td><span>执行后等待(s)</span></td>
                                <td>
                                  <el-input v-model="subItem.waitAfter" placeholder="0"></el-input>
                                </td>
                              </tr>
                              <tr>
                                <td><span>最大执行次数</span></td>
                                <td>
                                  <el-input v-model.number="subItem.maxExecuteNum" placeholder="1"></el-input>
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

      <div class="text-center pt-100 text-red" v-else>{{ isStepErrorMsg }}</div>
    </div>

    <!--编辑输入框内容-->
    <el-dialog :visible.sync="editDialog.show" title="编辑" width="1000px" class="cust-dialog" :show-close="true"
      :close-on-click-modal="false">
      <div class="pt-10">
        <el-input v-model="editDialog.fieldValue" type="textarea" rows="20" resize="none" placeholder="请输入"></el-input>
      </div>
      <div class="text-right mt-15">
        <span v-if="editDialog.checkRes == 'success'" class="text-green mr-20">校验通过！</span>
        <span v-if="editDialog.checkRes == 'error'" class="text-red mr-20">校验失败，请检查！</span>
        <el-button size="small" class="btn--border-blue mr-20" @click="addElementValueJson">插入Element对象</el-button>
        <el-button size="small" class="btn--border-blue mr-20" @click="checkEditValueJson">校验json格式</el-button>
        <el-button size="small" class="w-60" type="primary" @click="confirmEditValue">确定</el-button>
      </div>
    </el-dialog>

    <!--  查看执行明细  -->
    <executeQuery ref="executeQuery"></executeQuery>

    <!--  插入步骤  -->
    <el-dialog :visible.sync="recordDialog.show" title="插入录制步骤" width="500px" class="cust-dialog" :show-close="true"
      :close-on-click-modal="false">
      <div class="pt-10">
        <el-form :model="recordDialog" ref="recordDialog" label-width="0">
          <p>名称：</p>
          <el-form-item prop="id" :rules="[{ required: true, message: '请选择名称', trigger: 'change' }]">
            <el-select class="w-p100" v-model="recordDialog.id" placeholder="请输入关键词" clearable filterable remote
              reserve-keyword :remote-method="getRecordOptions" :loading="recordDialog.loading">
              <el-option v-for="(item, index) in recordDialog.options" :key="index" :label="item.name"
                :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <div class="text-right mt-30">
        <el-button size="small" class="btn--border-blue mr-20" @click="recordDialog.show = false">取消</el-button>
        <el-button size="small" class="w-60" type="primary" @click="confirmRecord">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import draggable from 'vuedraggable'
import executeQuery from './component/executeQuery.vue'
const INTERVAL = 10000
export default {
  components: { draggable, executeQuery },
  data () {
    return {
      isTest: false, // 当为测试时 不可以拖动页面
      pathData: [
        // {name: 'RPA应用设计'},
        // {name: 'RPA流程配置'},
        // {name: 'RPA步骤设置 '}
      ],
      originalCommandList: [],
      commandList: [],
      stepListForm: {
        stepList: []
      },
      refeshKey: 0,
      curSelectEditIndex: '', // 当前选中的index
      curSelectEditInd: '', // 当前选中的ind
      // flowCode: '9669448a18fa49c9a6154a3ed2a20aaa',
      flowCode: '',
      appCode: '',
      id: '',
      collectData: {
        timer: null,
        timerCount: 3000,
        index: '',
        ind: '',
        typeIndex: '',
        type: ''
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
      stepOptions: [],
      appDetail: {},
      isStepErrorMsg: '',
      isloadingPage: true,
      allTask: [],
      selectStartTask: '',
      recordDialog: {
        show: false,
        id: '',
        index: '',
        ind: '',
        options: [],
        loading: false
      }
    }
  },
  computed: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({ name: 'RPA步骤设置' })
    this.flowCode = this.$route.query.flowCode
    this.appCode = this.$route.query.appCode
    this.id = this.$route.query.id
    this.getAppDetail()
    this.getCommandList()
    this.getListAllTask()

    if (this.$route.query.isTest) {
      this.isTest = true
    } else {
      this.isTest = false
    }
  },
  watch: {},
  methods: {
    // 获取应用详情
    getAppDetail () {
      let that = this
      this.$http({
        url: '/robot/flow/step/queryDescribe?id=' + this.id,
        method: 'post'
      }).then(({ data }) => {
        that.appDetail = data.data
        if (that.appDetail.relationAppNameAndFlowName) {
          that.$message.warning('此流程引用了 【' + that.appDetail.relationAppNameAndFlowName + '】不允许保存，请到该流程下保存.')
        }
      }).catch((data) => {
      })
    },

    // 获取命令中心
    getCommandList () {
      let that = this
      this.$http({
        url: '/robot/action/all',
        method: 'post'
      }).then(({ data }) => {
        that.originalCommandList = data.data
        var originalCommandList = data.data
        var commandList = []
        var result = []
        var resultIndex, obj, subItem
        for (var i = 0; i < originalCommandList.length; i++) {
          subItem = originalCommandList[i]
          resultIndex = result.indexOf(subItem.groupName)
          if (resultIndex > -1) {
            commandList[resultIndex].list.push(subItem)
          } else {
            result.push(subItem.groupName)
            obj = {
              groupName: subItem.groupName,
              isOpen: true,
              list: []
            }
            obj.list.push(subItem)
            commandList.push(obj)
          }
        }
        console.log(commandList)
        that.commandList = commandList
        that.getStepList()
      }).catch((data) => {
        that.isloadingPage = false
      })
    },

    // 获取步骤
    getStepList () {
      let that = this
      this.$http({
        url: '/robot/flow/step/list/' + that.flowCode,
        method: 'post'
      }).then(({ data }) => {
        var originalStepList = data.data
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
          subItem.targetArgsVOS = that.handleArgs(subItem.targetArgsVOS) // 处理动态字段根据条件显示或隐藏
          subItem.actionArgsVOS = that.handleArgs(subItem.actionArgsVOS) // 处理动态字段根据条件显示或隐藏
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
        that.handleStepOptions() // 处理成功和失败的跳转步骤，为已建步骤的下拉选择框

        that.isloadingPage = false
      }).catch((data) => {
        that.isloadingPage = false
      })
    },

    getActionObj (actionCode) {
      var obj = this.originalCommandList.find(item => {
        return item.actionCode === actionCode
      })
      return obj || ''
    },

    // move回调方法
    onMoveCommand (e, originalEvent, index) {
      // console.log('onMoveCommand')
      // console.log(index)
      // console.log(e.draggedContext.index)
      // return true;
    },
    onMoveStep (e, originalEvent) {
      /* console.log(e.draggedContext.index) */
    },
    onMoveStepSub (e, originalEvent, index) {
      /* console.log('onMoveStepSub')
        console.log(index)
        console.log(e.draggedContext.index)
        */
    },

    getStepItem (newObj) {
      return {
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
        maxExecuteNum: this.isNotEmptyString(newObj.maxExecuteNum) ? newObj.maxExecuteNum : '',
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
      this.getAllStepCount()
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
      this.getAllStepCount()
    },
    // 每次添加步骤（不管父步骤还是子步骤都需要统计 全部的步骤数）
    getAllStepCount () {
      let subStepCount = this.stepListForm.stepList.map(item => item.list).flat().length
      return subStepCount + this.stepListForm.stepList.length
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
      }).catch(() => { })
    },
    showStepArgs (index, ind) {
      console.log('showStepArgs')
      this.curSelectEditIndex = Number(index)
      this.curSelectEditInd = ind != undefined ? Number(ind) : ''
      this.$refs.argsArea.scrollTop = 0
      this.handleStepOptions()
      console.log(index)
      console.log(ind)
    },
    resetSelectStep () {
      this.curSelectEditIndex = null
      this.curSelectEditInd = null
    },
    // 行为参数-动态字段
    getActionArgs (index, ind) {
      console.log('getActionArgs')
      console.log(index)
      console.log(ind)
      let that = this
      let actionCode = ''
      if (ind != undefined) {
        //  子步骤
        actionCode = this.stepListForm.stepList[index].list[ind].actionCode
      } else {
        //  步骤
        actionCode = this.stepListForm.stepList[index].actionCode
      }
      this.$http({
        url: '/robot/action/args',
        method: 'post',
        params: {
          dataCode: actionCode
        }
      }).then(({ data }) => {
        var res = that.handleArgs(data.data)
        if (ind != undefined) {
          //  子步骤
          that.$set(that.stepListForm.stepList[index].list[ind], 'actionArgsVOS', res)
        } else {
          //  步骤
          that.$set(that.stepListForm.stepList[index], 'actionArgsVOS', res)
        }
      }).catch((data) => {
      })
    },
    // 操作目标-动态字段
    getTargetArgs (index, ind) {
      let that = this
      let actionCode = ''
      if (ind != undefined) {
        //  子步骤
        actionCode = this.stepListForm.stepList[index].list[ind].actionCode
      } else {
        //  步骤
        actionCode = this.stepListForm.stepList[index].actionCode
      }
      this.$http({
        url: '/robot/action/target/args',
        method: 'post',
        params: {
          dataCode: actionCode
        }
      }).then(({ data }) => {
        var res = that.handleArgs(data.data)
        if (ind != undefined) {
          //  子步骤
          that.$set(that.stepListForm.stepList[index].list[ind], 'targetArgsVOS', res)
        } else {
          //  步骤
          that.$set(that.stepListForm.stepList[index], 'targetArgsVOS', res)
        }
      }).catch((data) => {
      })
    },

    // 处理动态字段返回的数据
    handleArgs (arr) {
      // 动态字段同一组中最多只有一个cond不为空，cond控制同组中其他字段的显示或隐藏
      var condRes = false; var showFieldKeyArr = []
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
      if (condRes) {
        arr.map(item2 => {
          if (!(item2.cond != '' && item2.cond != null) && showFieldKeyArr.indexOf(item2.fieldKey) == -1) {
            item2.isHideCust = true
          }
        })
      }
      return arr
    },
    // 改变下拉选择
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
    // 获取当前应用的所有任务（用于运行）
    getListAllTask () {
      let that = this
      this.$http({
        url: '/robot/app/client/listAllTask?appCode=' + that.appCode,
        method: 'post'
        // data: {appCode: that.appCode}
      }).then(({ data }) => {
        that.allTask = data.data
      }).catch((data) => {
      })
    },
    // 运行某个任务
    handleStartTask () {
      let that = this
      let item = this.selectStartTask
      if (!item) {
        that.$message.warning('请选择需要操作的流程')
        return
      }
      this.showLoading()
      if (this.isTest) {
        this.$http({
          method: 'post',
          url: '/robot/task/runTestCommand',
          params: {
            taskCode: this.selectStartTask.taskCode,
            flowCode: this.flowCode,
            stepCode: ''
          }
        }).then(({ data }) => {
          that.closeLoading()
          console.log('data------', data)
          if (data.code == 200) {
            if (data.data.uuid) {
              // that.getTestTaskStatus(this.selectStartTask.taskCode,data.data.uuid, this.flowCode).then(result => {
              //   console.log('最终---------', result)
              // })
            } else {
              that.$message.warning(data.data.errorMsg)
            }
          }
        }).catch((data) => {
          console.log('error----------', data)
          that.closeLoading()
          that.$message.warning('没找到机器人, 请先运行机器人')
        })
      } else {
        this.$http({
          url: 'http://localhost:9090/api/robot/start/taskFlow/' + item.taskCode + '/' + this.flowCode,
          method: 'get'
        }).then(({ data }) => {
          that.closeLoading()
          if (data.code == 200) {
            that.$refs.executeQuery.init(data.data)
          }
        }).catch((data) => {
          that.closeLoading()
          that.$message.warning('没找到机器人, 请先运行机器人')
        })
      }
    },
    // 调用执行列表接口
    getExecutionList (executionCode, flowCode) {
      let that = this
      return new Promise((resolve, reject) => {
        this.$http(
          {
            url: `/robot/app/client/executionList`,
            method: 'post',
            data: {
              executionCode: executionCode
            }
          })
          .then(response => {
            const status = response.data.status
            that.getExecutionDetail(executionCode, flowCode)
            if (status === 2 || response.data == null) {
              // 如果状态为2，继续递归调用该函数
              setTimeout(() => {
                resolve(getExecutionList())
              }, INTERVAL)
            } else if (status === 0 || status === 1) {
              // 如果状态为0或1，返回执行详情接口的调用结果
              // resolve(getExecutionDetail(executionCode, flowCode));
            } else {
              // 如果状态不是0、1或2，抛出错误
              reject(new Error('未知的执行状态'))
            }
          })
          .catch(error => {
            reject(new Error('调用执行列表接口出错:' + error))
          })
      })
    },
    getTestTaskStatus (taskCode, executionCode, flowCode) {
      let that = this
      return new Promise((resolve, reject) => {
        this.$http(
          {
            url: `/robot/task/getTestTaskStatus`,
            method: 'post',
            params: {
              taskCode: taskCode
            }
          })
          .then(response => {
            const status = response.data.data
            // that.getExecutionDetail(executionCode, flowCode)
            if (status === 0) {
              // 如果状态为0，继续递归调用该函数
              setTimeout(() => {
                resolve(that.getTestTaskStatus(taskCode, executionCode, flowCode))
              }, INTERVAL)
            } else if (status === 1) {
              // 如果状态为0或1，返回执行详情接口的调用结果
              resolve(that.getExecutionDetail(executionCode, flowCode))
            } else {
              // 如果状态不是0、1，抛出错误
              reject(new Error('未知的执行状态'))
            }
          })
          .catch(error => {
            reject(new Error('调用getTestTaskStatus出错:' + error))
          })
      })
    },
    // 调用执行详情接口
    getExecutionDetail (executionCode, flowCode) {
      return new Promise((resolve, reject) => {
        const url = `/robot/app/client/client/execution/detail?executionCode=${executionCode}&flowCode=${flowCode}`
        this.$http(
          {
            url: url,
            method: 'post'
          })
          .then(response => {
            // const status = response.data.status;
            console.log('调用执行详情接口', response)
            resolve(response.data)
            // if (status === 2) {
            //   // 如果状态为2，继续递归调用该函数
            //   setTimeout(() => {
            //     resolve(getExecutionDetail(executionCode));
            //   }, INTERVAL);
            // } else {
            //   // 如果状态不是2，返回执行结果
            //   resolve(response.data);
            // }
          })
          .catch(error => {
            reject(new Error('调用执行详情接口出错:' + error))
          })
      })
    },
    // 运行某个步骤
    handleStartTaskStep (item) {
      let that = this
      this.showLoading()
      if (this.isTest) {
        this.$http({
          method: 'post',
          url: '/robot/task/runTestCommand',
          params: {
            taskCode: this.selectStartTask.taskCode,
            flowCode: item.flowCode,
            stepCode: item.stepCode
          }
        }).then(({ data }) => {
          that.closeLoading()
          console.log('data------', data)
          if (data.code == 200) {
            if (data.data.uuid) {
              // that.getTestTaskStatus(this.selectStartTask.taskCode,data.data.uuid, this.flowCode).then(result => {
              //   console.log('最终---------', result)
              // })
            } else {
              that.$message.warning(data.data.errorMsg)
            }
          }
        }).catch((data) => {
          console.log('error----------', data)
          that.closeLoading()
          that.$message.warning('没找到机器人, 请先运行机器人')
        })
      } else {
        this.$http({
          url: `http://localhost:9090/api/robot/start/startFlowStep/${this.selectStartTask.taskCode}/${item.flowCode}/${item.stepCode}`,
          method: 'get'
        }).then(({ data }) => {
          that.closeLoading()
          if (data.code == 200) {
            that.$refs.executeQuery.init(data.data)
          }
        }).catch((data) => {
          that.closeLoading()
          that.$message.warning('没找到机器人, 请先运行机器人')
        })
      }
    },
    // 选择插入步骤
    handleAddOptStep (index, ind) {
      this.recordDialog.id = ''
      this.recordDialog.index = index
      this.recordDialog.ind = ind
      this.recordDialog.show = true
      this.$nextTick(() => {
        this.$refs.recordDialog.clearValidate()
      })
    },
    getRecordOptions (query) {
      if (query === '') {
        return
      }
      this.recordDialog.loading = true
      this.$http({
        url: `/policy/sys/desktopApplications/getAllPage`,
        method: 'post',
        data: {
          page: 1,
          start: 0,
          size: 10,
          query: [{ property: 'name', value: query }]
        }
      }).then(({ data }) => {
        if (data.code == 200) {
          this.recordDialog.options = data.data.rows
          this.recordDialog.loading = false
        }
      }).catch((data) => {
      })
    },
    confirmRecord () {
      let that = this
      this.$refs.recordDialog.validate((valid, errorMessage) => {
        if (valid) {
          this.showLoading('请稍候...')
          this.$http({
            url: '/policy/saasRobotFlowStep/findStepListByDeskId?id=' + this.recordDialog.id,
            method: 'get'
          }).then((data) => {
            if (data.data.code === 200) {
              var originalStepList = data.data.data
              let index = this.recordDialog.index
              let ind = this.recordDialog.ind
              var stepList = []
              var subItem; var actionObj
              var resultIndex = -1
              for (var i = 0; i < originalStepList.length; i++) {
                subItem = originalStepList[i]
                actionObj = that.getActionObj(subItem.actionCode)
                if (ind !== '' && subItem.level === 1) {
                  that.closeLoading()
                  this.$message.warning('要插入的流程最大有两层, 只允许插入到第一层')
                  return
                }
                if (actionObj == '') {
                  that.closeLoading()
                  that.isStepErrorMsg = '步骤中第' + (i + 1) + '条数据 actionCode=' + subItem.actionCode + ' 已经不存在命令中心，请检查'
                  return
                }
                subItem.groupName = actionObj.groupName
                subItem.actionName = actionObj.actionName
                subItem.targetArgsVOS = that.handleArgs(subItem.targetArgsVOS) // 处理动态字段根据条件显示或隐藏
                subItem.actionArgsVOS = that.handleArgs(subItem.actionArgsVOS) // 处理动态字段根据条件显示或隐藏
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

              if (stepList.length > 0) {
                let arr = []
                let arr1 = []
                let arr2 = []
                if (ind === '') {
                  arr1 = this.stepListForm.stepList.slice(0, index + 1)
                  arr2 = this.stepListForm.stepList.slice(index + 1)
                } else {
                  arr1 = this.stepListForm.stepList[index].list.slice(0, ind + 1)
                  arr2 = this.stepListForm.stepList[index].list.slice(ind + 1)
                }
                arr1.map(item => {
                  arr.push(item)
                })
                stepList.map(item => {
                  arr.push(item)
                })
                arr2.map(item => {
                  arr.push(item)
                })
                if (ind === '') {
                  this.stepListForm.stepList = arr
                } else {
                  this.stepListForm.stepList[index].list = arr
                }
                this.recordDialog.show = false
                this.resetSelectStep()
                this.closeLoading()
                this.handleStepOptions() // 处理成功和失败的跳转步骤，为已建步骤的下拉选择框
                this.$message.success('操作成功')
              } else {
                this.recordDialog.show = false
                this.closeLoading()
                this.$message.warning('无步骤可插入')
              }
            }
          }).catch(() => {
          })
        }
      })
    },

    showLoading (msg) {
      this.loading = this.$loading({
        target: document.body,
        lock: true,
        text: msg || '运行中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      })
    },
    closeLoading () {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
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
              console.log(field)
              var fieldArr = field.split('.')
              var index = fieldArr[1]
              console.log(index)
              if (field.indexOf('.list') > -1) {
                //  子步骤
                var ind = fieldArr[3]
                console.log(ind)
                that.showStepArgs(index, ind)
              } else {
                that.showStepArgs(index)
              }
              return
            } */
          }
        })
      })
    },
    doSave () {
      let that = this
      var setData = []; var item; var subItem; var number = 0; var actionArgs; var targetArgs; var stepNameArr = []
      for (let i = 0; i < that.stepListForm.stepList.length; i++) {
        item = { ...that.stepListForm.stepList[i] }
        if (stepNameArr.indexOf(item.stepName) == -1) {
          stepNameArr.push(item.stepName)
        } else {
          that.$message.warning('存在重复的步骤名称"' + item.stepName + '"，请检查')
          return
        }
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
            if (stepNameArr.indexOf(subItem.stepName) == -1) {
              stepNameArr.push(subItem.stepName)
            } else {
              that.$message.warning('存在重复的步骤名称"' + subItem.stepName + '"，请检查')
              return
            }
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
      console.log(that.stepListForm.stepList)
      console.log(setData)
      if (setData.length == 0) {
        that.$message.warning('请先设置步骤')
        return
      }
      // return
      this.$http({
        url: '/robot/flow/step/save?flowCode=' + that.flowCode,
        method: 'post',
        data: setData
      }).then(({ data }) => {
        that.$message.success('保存成功')
        /* setTimeout(() => {
            that.$router.push('/processSetting/setting?appCode='+that.appCode)
          }, 1500) */
      }).catch((data) => {
      })
    },

    // 输入框-采集
    doCollect (index, ind, typeIndex, type) {
      let that = this
      let collectData = this.collectData
      collectData.index = index
      collectData.ind = ind
      collectData.typeIndex = typeIndex
      collectData.type = type
      localStorage.setItem('openTranscribe', 'true')
      /* 清空粘贴板 */
      navigator.clipboard.writeText('')
      // 定时任务
      collectData.timer = setInterval(function () {
        console.log('获取剪贴板开始')
        navigator.clipboard.readText().then((fieldValue) => {
          if (fieldValue) {
            localStorage.setItem('openTranscribe', 'false')
            clearInterval(collectData.timer)
            collectData.timer = null
            console.log('获取剪贴板成功：', fieldValue)
            let index = collectData.index
            let ind = collectData.ind
            let typeIndex = collectData.typeIndex
            let type = collectData.type
            if (collectData.typeIndex !== '') {
              //  子步骤
              that.stepListForm.stepList[index].list[ind][type][typeIndex].fieldValue = fieldValue
            } else {
              //  步骤
              that.stepListForm.stepList[index][type][ind].fieldValue = fieldValue
            }
          }
        }).catch((e) => {
          console.log('获取剪贴板失败：', e)
        })
      }, collectData.timerCount)
    },
    handleCollectIcon (index, ind, typeIndex, type) {
      let collectData = this.collectData
      if (collectData.timer && collectData.index === index && collectData.ind === ind && collectData.typeIndex === typeIndex && collectData.type === type) {
        return true
      }
      return false
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

    handleOptions (list) {
      if (list) {
        return list
      } else {
        return []
      }
    },

    // 处理成功和失败的跳转步骤，为已建步骤的下拉选择框
    handleStepOptions () {
      let stepList = this.stepListForm.stepList
      let activeStepName = this.curSelectEditInd != undefined && this.curSelectEditInd != null && this.curSelectEditInd !== '' ? stepList[this.curSelectEditIndex].list[this.curSelectEditInd].stepName : stepList[this.curSelectEditIndex].stepName

      console.log('activeStepName', activeStepName)
      console.log('this.curSelectEditIndex', this.curSelectEditIndex)
      let stepOptions = []
      stepList.map(item => {
        if (item.stepName && item.stepName != activeStepName) {
          stepOptions.push(item.stepName)
        }
        item.list.map(it => {
          if (it.stepName && it.stepName != activeStepName) {
            stepOptions.push(it.stepName)
          }
        })
      })
      //  如果之前选中的值已经不在下拉值中，那么需要清空
      stepList.map(item => {
        if (stepOptions.indexOf(item.skipTo) === -1 && item.skipTo != activeStepName) {
          item.skipTo = ''
        }
        if (stepOptions.indexOf(item.failedSkipTo) === -1 && item.failedSkipTo != activeStepName) {
          item.failedSkipTo = ''
        }
        item.list.map(it => {
          if (stepOptions.indexOf(it.skipTo) === -1 && it.skipTo != activeStepName) {
            it.skipTo = ''
          }
          if (stepOptions.indexOf(it.failedSkipTo) === -1 && it.failedSkipTo != activeStepName) {
            it.failedSkipTo = ''
          }
        })
      })
      this.stepOptions = stepOptions
    }
  }
}
</script>

<style lang="less">
.el-dropdown-menu.el-popper {
  max-height: 350px;
  overflow: auto;
  margin-top: 5px;
}
</style>
<style scoped lang="less">
.area {
  height: 100%;

  .title {
    font-size: 16px;
    font-weight: bold;
    text-align: center;
    padding-bottom: 15px;
  }
}

.area-border {
  border: 1px solid #dbdbdb;
  border-radius: 4px;
  height: calc(100vh - 285px);
  box-sizing: border-box;
  overflow: auto;
}

.area-l {
  width: 250px;

  .area-border {
    padding: 0px 20px 20px 20px;
  }

  .ic-toggle {
    color: #909399;
    font-size: 16px;
    vertical-align: middle;
    margin-top: -3px;
  }

  .command-list {
    transition: height linear 2s;
    height: 0;
    overflow: hidden;
    padding-left: 40px;
  }

  .command-list.open {
    transition: height 2s;
    height: auto;
  }

  .item {
    margin-bottom: 5px;
    cursor: move;
  }
}

.area-c {
  flex: 1;
  margin: 0 25px;

  .area-border {
    /*padding: 10px 15px;*/
  }

  .step-item {
    padding: 7px 10px;
    border: 1px solid #dbdbdb;
    border-radius: 4px;
    display: flex;
    cursor: move;
  }

  .step-item.active {
    border-color: @blueColor;
    background-color: rgba(203, 218, 255, 0.3);
  }

  .step-item:hover {
    border-color: @blueColor;
  }

  .step-name {
    margin-left: 30px;
  }

  .comment {
    margin-right: 10px;
    flex: 1;
  }

  .del-btn {
    margin-left: 20px;
  }

  .step-list {
    border: 1px dashed #dbdbdb;
    border-radius: 4px;
    padding: 10px 10px 0px 10px;
    margin-bottom: 15px;
  }

  .sub-list {

    /*padding: 10px 0 15px 0px;*/
    /*padding-top: 10px;*/
    .step-item {
      margin-bottom: 10px;
    }
  }
}

.area1-r {
  width: 400px;

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

  .text-div {
    display: flex;
    align-items: center;
  }

  .ic-edit-input {
    margin-left: 8px;
    font-size: 16px;
    cursor: pointer;
  }
}

/deep/.el-form-item {
  margin-bottom: 0;
  margin-right: 0;
  width: 100%;

  .el-form-item__content {
    width: 100%;
  }

  .el-form-item__error {
    position: relative;
  }
}

.stepArea {
  display: block;
  width: 100%;
  min-height: 100%;
  overflow: auto;
  padding: 10px 10px 50px 10px;
  box-sizing: border-box;
}

/deep/.detail-drawer {
  width: 800px !important;
}
</style>
