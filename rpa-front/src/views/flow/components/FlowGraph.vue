<template>
  <div>
    <!-- 头部栏 应用名称、应用类型、流程名称、流程类型  操作 -  运行、保存 -->
    <div class="pb-10 pl-10 pt-10" v-if="mFlowId > 0 && mFlowId && appDetail">
      <el-row>
        <el-col :span="6">应用名称： {{ appDetail.appName }}</el-col>
        <el-col :span="4">应用类型： {{ appDetail.businessType }}</el-col>
        <el-col :span="6">流程名称： {{ appDetail.flowName }}</el-col>
        <el-col :span="4">流程类型： {{ appDetail.flowType }}</el-col>
        <el-col :span="4">
          <!-- <div v-if="isStepErrorMsg == ''">
            <el-dropdown class="test123" trigger="click" @command="handleStartTask">
              <el-button class="btn--border-blue mr-20" size="small">运行<i
                  class="el-icon-arrow-down el-icon--right text-blue"></i></el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-for="(item, index) in allTask" :key="index"
                  :command="item">{{ item.taskName }}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <el-button type="primary" size="small" @click="handleSave()">保存</el-button>
          </div> -->
        </el-col>
      </el-row>
    </div>
    <div class="shortcut-bar">
      <div class="rate">{{ parseInt(zoomSize.current * 100) }}%</div>
      <div class="rx-row" @click="toolbarFUN('zoomAdd')">
        <i class="el-icon-zoom-in"></i>
        <span class="item" title="放缩比例+10%">放大</span>
      </div>
      <div class="rx-row" @click="toolbarFUN('zoomReduce')">
        <i class="el-icon-zoom-out"></i>
        <span class="item" title="放缩比例-10%">缩小</span>
      </div>
      <div class="rx-row" @click="toolbarFUN('center')">
        <i class="el-icon-rank"></i>
        <span class="item">画布居中</span>
      </div>
      <div class="rx-row" @click="toolbarFUN('scale')">
        <i class="el-icon-full-screen"></i>
        <span class="item">适应画布</span>
      </div>
      <div class="rx-row" @click="toolbarFUN('adapt')">
        <i class="el-icon-c-scale-to-original"></i>
        <span class="item">实际尺寸</span>
      </div>
      <div class="rx-row" @click="toolbarFUN('clear')">
        <i class="el-icon-delete"></i>
        <span class="item" title="清空整个画布">清空</span>
      </div>
      <div class="rx-row" @click="toolbarFUN('undo')">
        <i class="el-icon-back"></i>
        <span class="item">撤销</span>
      </div>
      <div class="rx-row" @click="toolbarFUN('redo')">
        <i class="el-icon-right"></i>
        <span class="item">重做</span>
      </div>
      <div class="rx-row" @click="toolbarFUN('save')">
        <i class="el-icon-document-add"></i>
        <span class="item">保存</span>
      </div>
      <div class="rx-row" @click="toolbarFUN('import')">
        <i class="el-icon-download"></i>
        <span class="item">导入</span>
      </div>
      <div class="rx-row" @click="toolbarFUN('export')">
        <i class="el-icon-upload2"></i>
        <span class="item">导出</span>
      </div>
      <div class="rx-row" @click="toolbarFUN('png')">
        <i class="el-icon-picture-outline"></i>
        <span class="item">导出图片</span>
      </div>
      <div class="rx-row" @click="toolbarFUN('terminal')">
        <i class="el-icon-view"></i>
        <span class="item">terminal</span>
      </div>
    </div>
    <el-row class="row-bg">
      <el-col :span="4">
        <div class="grid-content bg-purple" :id="stencilId"></div>
      </el-col>
      <el-col :span="19">
        <div :id="containerId" class="grid-content bg-purple-light"></div>
      </el-col>
    </el-row>
    <div :id="minimapId" class="minimap"></div>
    <el-drawer :before-close="handleClose" :visible.sync="drawVisible" direction="rtl" custom-class="demo-drawer"
      ref="drawer">
      <div class="demo-drawer__content" v-if="!curSelectEditIndex && curSelectEditIndex != 0">
        <el-form :model="form">
          <!-- 节点名称 -->
          <el-form-item :label="drawerFormConfig.label" :label-width="formLabelWidth">
            <el-input v-model="form.flowName" autocomplete="off"></el-input>
          </el-form-item>
          <!-- 节点描述 -->
          <el-form-item :label="drawerFormConfig.desc" :label-width="formLabelWidth">
            <el-input v-model="form.desc" autocomplete="off"></el-input>
          </el-form-item>
          <!-- 节点状态 -->
          <el-form-item label="节点状态" :label-width="formLabelWidth">
            <el-select v-model="form.nodeState" placeholder="请选择节点状态">
              <el-option label="默认" value="0"></el-option>
              <el-option label="成功" value="1"></el-option>
              <el-option label="失败" value="2"></el-option>
            </el-select>
          </el-form-item>
          <!-- 节点逻辑 -->
          <el-form-item v-if="curCell.shape === 'edge'" label="节点逻辑" :label-width="formLabelWidth">
            <el-select v-model="form.nodeLogic" placeholder="请选择节点逻辑">
              <el-option v-for="item in edgeLogicConfig" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
          <!-- 节点表达式 (判断节点才有)-->
          <el-form-item v-if="curCell.shape === 'custom-polygon'" :label="drawerFormConfig.expression"
            :label-width="formLabelWidth">
            <el-input v-model="form.expression" autocomplete="off"></el-input>
          </el-form-item>
          <!-- 节点引用 (vue节点才有) -->
          <el-form-item v-if="curCell.shape === 'vue-shape'" label="节点引用" :label-width="formLabelWidth">
            <el-select v-model="form.quoteCode" @change="onQuoteNodeChange">
              <el-option v-for="node in nodeList" :index="node.id" :label="node.flowName"
                :value="node.flowCode"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>

      <!-- 指令步骤设置 -->
      <div class="area-border" v-loading="isloadingPage" :key="refeshKey">
<!--        RPA-5009 当一个流程引用了模板。即进入流程图页面，templateFlowCode不为空时
        开放编辑=启用
        “目标参数”、“行为参数”栏控件为可编辑
        名称、"流转控制"栏控件不可编辑
        开放编辑=禁用
        名称、目标参数、行为参数、流转控制均不可编辑-->
        <el-form :inline="true" :model="stepListForm" ref="stepListForm" label-width="0" :disabled="isDisabledStepSet()">
          <div v-if="curSelectEditIndex || curSelectEditIndex == 0">
            <div v-for="(item, index) in stepListForm.stepList" :kef="index">
              <!--主步骤-->
              <div v-if="(curSelectEditInd === '' ||
                curSelectEditInd == undefined ||
                curSelectEditInd == 'undefined') &&
                curSelectEditIndex === index
                ">
                <div class="mb-15">
                    <el-row type="flex">
                      <div class="mr-5 required lh-com mt-5">名称：</div>
                      <el-form-item :prop="'stepList.' + index + '.stepName'" :rules="[
                    {
                      required: true,
                      message: '请输入步骤名称',
                      trigger: 'blur',
                    },
                  ]" style="flex: 1">
                      <el-input v-model="item.stepName" placeholder="请输入步骤名称，必填" @blur="handleStepOptions" :disabled="isDisabledStepSetArgs()"></el-input>
                      </el-form-item>
                    </el-row>
                </div>

                <div class="property-div" v-if="item.targetArgsVOS && item.targetArgsVOS.length > 0">
                  <p class="title">操作目标</p>
                  <div>
                    <table cellpadding="0" cellspacing="0">
                      <tbody>
                        <!--displayType: text、date、number、select-->
                        <!--fieldType: text,number,date,singleDropList,single-->
                        <tr v-for="(it, ind) in item.targetArgsVOS" :key="ind" v-show="!it.isHideCust">
                          <td>
                            <span :class="{ 'required-pre': it.required === 1 }">{{ it.fieldName }}</span>
                            <i class="el-icon-warning-outline ml-5 text-orange" v-if="it.comment" :title="it.comment"></i>
                          </td>
                          <td>
                            <div style="position: relative" :class="{ 'pr-20': it.fieldType == 'text' }" class="text-div">
                              <el-form-item class="flex1" :prop="'stepList.' +
                                index +
                                '.targetArgsVOS.' +
                                ind +
                                '.fieldValue'
                                " :rules="[
    {
      validator: (rule, value, callback) =>
        checkDynamicField(
          rule,
          value,
          callback,
          it
        ),
      trigger: 'blur',
    },
    {
      validator: (rule, value, callback) =>
        checkDynamicField(
          rule,
          value,
          callback,
          it
        ),
      trigger: 'change',
    },
  ]">
                                <el-input v-if="it.fieldType == 'text' ||
                                  it.fieldType == 'number'
                                  " v-model="it.fieldValue" :placeholder="it.comment ? '' : '请输入'"></el-input>
                                <el-date-picker v-else-if="it.fieldType == 'date'" v-model="it.fieldValue" type="date"
                                  clearable value-format="yyyy-MM-dd"
                                  :placeholder="it.comment ? '' : '请选择'"></el-date-picker>

                                <el-select v-else-if="it.fieldType == 'singleDropList'" v-model="it.fieldValue" @change="
                                  changeSingleDropList(
                                    index,
                                    ind,
                                    '',
                                    'targetArgsVOS'
                                  )
                                  " :placeholder="it.comment ? '' : '请选择'" value-key="dictCode" clearable filterable>
                                  <el-option v-for="(optItem, optIndex) in handleOptions(
                                    it.robotDataDicts
                                  )" :key="optIndex" :label="optItem.dictName" :value="optItem.dictCode"></el-option>
                                </el-select>

                                <div v-else-if="it.fieldType == 'single'">
                                  <el-radio v-for="(optItem, optIndex) in handleOptions(
                                    it.robotDataDicts
                                  )" :key="optIndex" v-model="it.fieldValue" :label="optItem.dictCode">{{
  optItem.dictName }}</el-radio>
                                </div>

                              </el-form-item>
                              <!-- 增加插件支持的小飞机显示 -->
                              <i v-if="it.fieldType == 'text' && !isDisabledStepSet()" class="el-icon-s-promotion ic-edit-input"
                                :class="{ 'text-blue': handleCollectIcon(index, ind, '', 'targetArgsVOS') }"
                                style="position:relative ;top:0" @click="doCollect(index, ind, '', 'targetArgsVOS')"></i>
                              <i v-if="it.fieldType == 'text' && !isDisabledStepSet()" class="el-icon-edit-outline ic-edit-input" @click="
                                showEditDialog(
                                  index,
                                  ind,
                                  '',
                                  'targetArgsVOS'
                                )
                                "></i>
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
                          <td>
                            <span :class="{ 'required-pre': it.required === 1 }">{{ it.fieldName }}</span>
                            <i class="el-icon-warning-outline ml-5 text-orange" v-if="it.comment" :title="it.comment"></i>
                          </td>
                          <td>
                            <div style="position: relative" :class="{ 'pr-20': it.fieldType == 'text' }">
                              <el-form-item :prop="'stepList.' +
                                index +
                                '.actionArgsVOS.' +
                                ind +
                                '.fieldValue'
                                " :rules="[
    {
      validator: (rule, value, callback) =>
        checkDynamicField(
          rule,
          value,
          callback,
          it
        ),
      trigger: 'blur',
    },
    {
      validator: (rule, value, callback) =>
        checkDynamicField(
          rule,
          value,
          callback,
          it
        ),
      trigger: 'change',
    },
  ]">
                                <el-input v-if="it.fieldType == 'text' ||
                                  it.fieldType == 'number'
                                  " v-model="it.fieldValue" :placeholder="it.comment ? '' : '请输入'"></el-input>

                                <el-date-picker v-else-if="it.fieldType == 'date'" v-model="it.fieldValue" type="date"
                                  clearable value-format="yyyy-MM-dd"
                                  :placeholder="it.comment ? '' : '请选择'"></el-date-picker>

                                <el-select v-else-if="it.fieldType == 'singleDropList'" v-model="it.fieldValue" @change="
                                  changeSingleDropList(
                                    index,
                                    ind,
                                    '',
                                    'actionArgsVOS'
                                  )
                                  " :placeholder="it.comment ? '' : '请选择'" value-key="dictCode" clearable filterable>
                                  <el-option v-for="(optItem, optIndex) in handleOptions(
                                    it.robotDataDicts
                                  )" :key="optIndex" :label="optItem.dictName" :value="optItem.dictCode"></el-option>
                                </el-select>

                                <div v-else-if="it.fieldType == 'single'">
                                  <el-radio v-for="(optItem, optIndex) in handleOptions(
                                    it.robotDataDicts
                                  )" :key="optIndex" v-model="it.fieldValue" :label="optItem.dictCode">{{
  optItem.dictName }}</el-radio>
                                </div>
                                <!-- 引用模板下 子流程不是选择 是下拉框，选项为“子流程”类型的模板 -->
                                <el-select v-else-if="it.fieldType == 'slot'" v-model="it.fieldValue" placeholder="请选择"
                                  @change="onTemplateSelectChange" clearable>
                                  <el-option v-for="(subItem, subIndex) in templateSubSelects" :key="subIndex"
                                    :label="subItem.flowName" :value="subItem.flowName"></el-option>
                                </el-select>
                              </el-form-item>
                              <i v-if="it.fieldType == 'text' && !isDisabledStepSet()" class="el-icon-edit-outline ic-edit-input" @click="
                                showEditDialog(
                                  index,
                                  ind,
                                  '',
                                  'actionArgsVOS'
                                )
                                "></i>

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
                        <!--<tr v-if="$route.query.template">-->
                        <tr>
                          <td><span>开放编辑</span></td>
                          <td style="height: 30px">
                            <!--模板下是否开放编辑：1启用，0禁用。-->
                            <el-radio v-model="item.openEdit" :label="1" :disabled="isDisabledStepSetArgs()">启用</el-radio>
                            <el-radio v-model="item.openEdit" :label="0" :disabled="isDisabledStepSetArgs()">禁用</el-radio>
                          </td>
                        </tr>
                        <tr>
                          <td><span>使用状态</span></td>
                          <td style="height: 30px">
                            <!--使用状态：1启用，0禁用。-->
                            <el-radio v-model="item.status" :label="1" :disabled="isDisabledStepSetArgs()">启用</el-radio>
                            <el-radio v-model="item.status" :label="0" :disabled="isDisabledStepSetArgs()">禁用</el-radio>
                          </td>
                        </tr>
                        <!-- <tr>
                          <td><span>成功后跳转</span></td>
                          <td>
                            <el-select v-model="item.skipTo" placeholder="请选择" clearable filterable>
                              <el-option v-for="(optItem, optIndex) in stepOptions" :key="optIndex" :label="optItem"
                                :value="optItem"></el-option>
                            </el-select>
                          </td>
                        </tr> -->
                        <!-- <tr>
                          <td><span>失败后跳转</span></td>
                          <td>
                            <el-select v-model="item.failedSkipTo" placeholder="请选择" clearable filterable>
                              <el-option v-for="(optItem, optIndex) in stepOptions" :key="optIndex" :label="optItem"
                                :value="optItem"></el-option>
                            </el-select>
                          </td>
                        </tr> -->
                        <tr>
                          <td><span> 跳过此步骤条件</span></td>
                          <td>
                            <el-input v-model="item.skipCondition" placeholder="请输入" :disabled="isDisabledStepSetArgs()"></el-input>
                          </td>
                        </tr>
                        <tr>
                          <td><span>执行超时(s)</span></td>
                          <td>
                            <el-input v-model="item.timeout" placeholder="10秒" :disabled="isDisabledStepSetArgs()"></el-input>
                          </td>
                        </tr>
                        <tr>
                          <td><span>如果执行失败</span></td>
                          <td style="height: 30px">
                            <el-radio v-model="item.failedStrategy" :label="0" style="margin-right: 25px" :disabled="isDisabledStepSetArgs()">终止</el-radio>
                            <el-radio v-model="item.failedStrategy" :label="1" style="margin-right: 25px" :disabled="isDisabledStepSetArgs()">忽略</el-radio>
                            <!-- <el-radio v-model="item.failedStrategy" :label="2" style="margin-right: 0px">跳转</el-radio> -->
                          </td>
                        </tr>
                        <tr>
                          <td><span>失败重试次数</span></td>
                          <td>
                            <el-input v-model="item.failedRetry" placeholder="不重试" :disabled="isDisabledStepSetArgs()"></el-input>
                          </td>
                        </tr>
                        <tr>
                          <td><span>执行前等待(s)</span></td>
                          <td>
                            <el-input v-model="item.waitBefore" placeholder="0" :disabled="isDisabledStepSetArgs()"></el-input>
                          </td>
                        </tr>
                        <tr>
                          <td><span>执行后等待(s)</span></td>
                          <td>
                            <el-input v-model="item.waitAfter" placeholder="0" :disabled="isDisabledStepSetArgs()"></el-input>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
              <!--子步骤-->
              <div v-for="(subItem, subIndex) in item.list" :key="subIndex">
                <div v-if="curSelectEditIndex === index &&
                  curSelectEditInd === subIndex
                  ">
                  <div class="mb-15">
                    <el-form-item :prop="'stepList.' + index + '.list.' + subIndex + '.stepName'
                      " :rules="[
    {
      required: true,
      message: '请输入步骤名称',
      trigger: 'blur',
    },
  ]">
                      <el-input v-model="subItem.stepName" placeholder="请输入步骤名称，必填" @blur="handleStepOptions" :disabled="isDisabledStepSetArgs()"></el-input>
                    </el-form-item>
                  </div>
                  <div class="property-div" v-if="subItem.targetArgsVOS && subItem.targetArgsVOS.length > 0
                    ">
                    <p class="title">操作目标</p>
                    <div>
                      <table cellpadding="0" cellspacing="0">
                        <tbody>
                          <!--displayType: text、date、number、select-->
                          <!--fieldType: text,number,date,singleDropList,single-->
                          <tr v-for="(it, ind) in subItem.targetArgsVOS" :key="ind" v-show="!it.isHideCust">
                            <td>
                              <span :class="{ 'required-pre': it.required === 1 }">{{ it.fieldName }}</span>
                              <i class="el-icon-warning-outline ml-5 text-orange" v-if="it.comment"
                                :title="it.comment"></i>
                            </td>
                            <td>
                              <div style="position: relative" :class="{ 'pr-20': it.fieldType == 'text' }">
                                <el-form-item :prop="'stepList.' +
                                  index +
                                  '.list.' +
                                  subIndex +
                                  '.targetArgsVOS.' +
                                  ind +
                                  '.fieldValue'
                                  " :rules="[
    {
      validator: (rule, value, callback) =>
        checkDynamicField(
          rule,
          value,
          callback,
          it
        ),
      trigger: 'blur',
    },
    {
      validator: (rule, value, callback) =>
        checkDynamicField(
          rule,
          value,
          callback,
          it
        ),
      trigger: 'change',
    },
  ]">
                                  <el-input v-if="it.fieldType == 'text' ||
                                    it.fieldType == 'number'
                                    " v-model="it.fieldValue" :placeholder="it.comment ? '' : '请输入'"></el-input>

                                  <el-date-picker v-else-if="it.fieldType == 'date'" v-model="it.fieldValue" type="date"
                                    value-format="yyyy-MM-dd" :placeholder="it.comment ? '' : '请选择'"
                                    clearable></el-date-picker>

                                  <el-select v-else-if="it.fieldType == 'singleDropList'" v-model="it.fieldValue" @change="
                                    changeSingleDropList(
                                      index,
                                      subIndex,
                                      ind,
                                      'targetArgsVOS'
                                    )
                                    " :placeholder="it.comment ? '' : '请选择'" value-key="dictCode" clearable filterable>
                                    <el-option v-for="(
                                                                optItem, optIndex
                                                              ) in handleOptions(it.robotDataDicts)" :key="optIndex"
                                      :label="optItem.dictName" :value="optItem.dictCode"></el-option>
                                  </el-select>

                                  <div v-else-if="it.fieldType == 'single'">
                                    <el-radio v-for="(
                                                                optItem, optIndex
                                                              ) in handleOptions(it.robotDataDicts)" :key="optIndex"
                                      v-model="it.fieldValue" :label="optItem.dictCode">{{ optItem.dictName }}</el-radio>
                                  </div>
                                </el-form-item>
                                <i v-if="it.fieldType == 'text' && !isDisabledStepSet()" class="el-icon-edit-outline ic-edit-input" @click="
                                  showEditDialog(
                                    index,
                                    subIndex,
                                    ind,
                                    'targetArgsVOS'
                                  )
                                  "></i>
                              </div>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </div>
                  <div class="property-div" v-if="subItem.actionArgsVOS && subItem.actionArgsVOS.length > 0
                    ">
                    <p class="title">行为参数</p>
                    <div>
                      <table cellpadding="0" cellspacing="0">
                        <tbody>
                          <!--displayType: text、date、number、select-->
                          <!--fieldType: text,number,date,singleDropList,single-->
                          <tr v-for="(it, ind) in subItem.actionArgsVOS" :key="ind" v-show="!it.isHideCust">
                            <td>
                              <span :class="{ 'required-pre': it.required === 1 }">{{ it.fieldName }}</span>
                              <i class="el-icon-warning-outline ml-5 text-orange" v-if="it.comment"
                                :title="it.comment"></i>
                            </td>
                            <td>
                              <div style="position: relative" :class="{ 'pr-20': it.fieldType == 'text' }">
                                <el-form-item :prop="'stepList.' +
                                  index +
                                  '.list.' +
                                  subIndex +
                                  '.actionArgsVOS.' +
                                  ind +
                                  '.fieldValue'
                                  " :rules="[
    {
      validator: (rule, value, callback) =>
        checkDynamicField(
          rule,
          value,
          callback,
          it
        ),
      trigger: 'blur',
    },
    {
      validator: (rule, value, callback) =>
        checkDynamicField(
          rule,
          value,
          callback,
          it
        ),
      trigger: 'change',
    },
  ]">
                                  <el-input v-if="it.fieldType == 'text' ||
                                    it.fieldType == 'number'
                                    " v-model="it.fieldValue" :placeholder="it.comment ? '' : '请输入'"></el-input>

                                  <el-date-picker v-else-if="it.fieldType == 'date'" v-model="it.fieldValue" type="date"
                                    clearable value-format="yyyy-MM-dd"
                                    :placeholder="it.comment ? '' : '请选择'"></el-date-picker>

                                  <el-select v-else-if="it.fieldType == 'singleDropList'" @change="
                                    changeSingleDropList(
                                      index,
                                      subIndex,
                                      ind,
                                      'actionArgsVOS'
                                    )
                                    " v-model="it.fieldValue" :placeholder="it.comment ? '' : '请选择'"
                                    value-key="dictCode" clearable filterable>
                                    <el-option v-for="(
                                                                optItem, optIndex
                                                              ) in handleOptions(it.robotDataDicts)" :key="optIndex"
                                      :label="optItem.dictName" :value="optItem.dictCode"></el-option>
                                  </el-select>

                                  <div v-else-if="it.fieldType == 'single'">
                                    <el-radio v-for="(
                                                                optItem, optIndex
                                                              ) in handleOptions(it.robotDataDicts)" :key="optIndex"
                                      v-model="it.fieldValue" :label="optItem.dictCode">{{ optItem.dictName }}</el-radio>
                                  </div>
                                </el-form-item>
                                <i v-if="it.fieldType == 'text' && !isDisabledStepSet()" class="el-icon-edit-outline ic-edit-input" @click="
                                  showEditDialog(
                                    index,
                                    subIndex,
                                    ind,
                                    'actionArgsVOS'
                                  )
                                  "></i>
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
                            <td style="height: 30px">
                              <!--使用状态：1启用，0禁用。-->
                              <el-radio v-model="subItem.status" :label="1" :disabled="isDisabledStepSetArgs()">启用</el-radio>
                              <el-radio v-model="subItem.status" :label="0" :disabled="isDisabledStepSetArgs()">禁用</el-radio>
                            </td>
                          </tr>
                          <tr>
                            <td><span>成功后跳转</span></td>
                            <td>
                              <el-select v-model="subItem.skipTo" placeholder="请选择" clearable filterable :disabled="isDisabledStepSetArgs()">
                                <el-option v-for="(optItem, optIndex) in stepOptions" :key="optIndex" :label="optItem"
                                  :value="optItem"></el-option>
                              </el-select>
                            </td>
                          </tr>
                          <tr>
                            <td><span>失败后跳转</span></td>
                            <td>
                              <el-select v-model="subItem.failedSkipTo" placeholder="请选择" clearable filterable :disabled="isDisabledStepSetArgs()">
                                <el-option v-for="(optItem, optIndex) in stepOptions" :key="optIndex" :label="optItem"
                                  :value="optItem"></el-option>
                              </el-select>
                            </td>
                          </tr>
                          <tr>
                            <td><span> 跳过此步骤条件</span></td>
                            <td>
                              <el-input v-model="subItem.skipCondition" placeholder="请输入" :disabled="isDisabledStepSetArgs()"></el-input>
                            </td>
                          </tr>
                          <tr>
                            <td><span>执行超时(s)</span></td>
                            <td>
                              <el-input v-model="subItem.timeout" placeholder="10秒" :disabled="isDisabledStepSetArgs()"></el-input>
                            </td>
                          </tr>
                          <tr>
                            <td><span>如果执行失败</span></td>
                            <td style="height: 30px">
                              <el-radio v-model="subItem.failedStrategy" :label="0"
                                style="margin-right: 25px" :disabled="isDisabledStepSetArgs()">终止</el-radio>
                              <el-radio v-model="subItem.failedStrategy" :label="1"
                                style="margin-right: 25px" :disabled="isDisabledStepSetArgs()">忽略</el-radio>
                              <el-radio v-model="subItem.failedStrategy" :label="2"
                                style="margin-right: 0px" :disabled="isDisabledStepSetArgs()">跳转</el-radio>
                            </td>
                          </tr>
                          <tr>
                            <td><span>失败重试次数</span></td>
                            <td>
                              <el-input v-model="subItem.failedRetry" placeholder="不重试" :disabled="isDisabledStepSetArgs()"></el-input>
                            </td>
                          </tr>
                          <tr>
                            <td><span>执行前等待(s)</span></td>
                            <td>
                              <el-input v-model="subItem.waitBefore" placeholder="0" :disabled="isDisabledStepSetArgs()"></el-input>
                            </td>
                          </tr>
                          <tr>
                            <td><span>执行后等待(s)</span></td>
                            <td>
                              <el-input v-model="subItem.waitAfter" placeholder="0" :disabled="isDisabledStepSetArgs()"></el-input>
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
      <div class="demo-drawer__footer">
        <el-button @click="cancelForm">取 消</el-button>
        <el-button v-if="!isDisabledStepSet()" type="primary" @click="onSaveForm" :loading="loading">{{
          loading ? '提交中 ...' : '确 定'
        }}</el-button>
      </div>
    </el-drawer>

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
  </div>
</template>
<script>
// 引入antv x6
import { Graph, Addon, Shape, Cell, DataUri } from '@antv/x6'
// 用于模拟的数据
import { mockStateGraph as mockStateData } from '../mock/data'
// 引入渲染vue节点
import '@antv/x6-vue-shape'
import Extra from './ExtraNode.vue'
import ActionNode from './ActionNode.vue'
import { graphConfig } from '../config/graph'
import { stencilConfig } from '../config/stencil'
import { portsConfig } from '../config/port'
import { nodeConfig } from '../config/registerNode'
import { createClass } from '@antv/x6/lib/util/object/inherit'
import { async } from '@antv/x6/lib/registry/marker/async'
const { Stencil } = Addon
export default {
  name: 'flow-graph',
  components: {},
  props: {
    nodeList: {
      type: [Array],
      default: function () {
        return []
      }
    },
    containerId: {
      type: String,
      require: true,
      default: 'container'
    },
    stencilId: {
      type: String,
      require: true,
      default: 'stencil'
    },
    minimapId: {
      type: String,
      require: true,
      default: 'minimap'
    },
    mFlowId: {
      // 用于整合命令中心时获取顶部应用节点详情的流程Id
      type: [Number, String],
      default: 0
    },
    curFlowCode: {
      // 当前流程getByCode时获取到的flowCode
      // 当前流程getFlowOpenType时获取到的flowCode（新）
      type: String,
      default: ''
    },
    templateFlowCode: {
      // 当前流程getFlowOpenType时获取到的templateFlowCode
      type: String,
      default: ''
    },
    mStepObj: {
      // 用于获取子流程的步骤 以及保存时的传参 里面有stepCode 和 stepName
      type: Object,
      default: () => { }
    },
    templateType: {
      type: [String, Number],
      default: ''
    }
  },
  data () {
    return {
      isGraphChange: false, // 画布是否发生了改变
      graph: null,
      stencil: null,
      drawVisible: false, // 右侧draw是否可见
      form: {
        id: '', // 当前对应节点的id
        flowName: '', // 对应节点的名称
        desc: '', // 加入到节点json中的描述
        nodeState: '0', // 节点状态
        flowCode: '', // 当前节点flowCode 如果为引用节点则将引用节点的flowCode置于此
        quoteCode: '', // 引用节点
        quote: false, // 当前节点是否为引用
        quoteId: false, // 引用节点id
        expression: '', // 节点表达式（判断节点才存在）
        nodeLogic: null // 节点逻辑（是/否 线段才有 用于真正判断线的逻辑 节点名称只是显示功能）
      },
      zoomSize: {
        // 当前画布缩放系数
        min: 0.1,
        max: 2.0,
        current: 1.0
      },
      formLabelWidth: '100px',
      loading: false,
      timer: null,
      formColumns: [
        {
          label: '节点名称',
          desc: '节点描述',
          expression: '节点表达式'
        }, // 流程、边的右侧表单配置
        {
          label: '节点表达式',
          desc: '节点描述'
        } // 判断节点的右侧表单配置
      ],
      curCell: Cell, // 当前双击的节点信息
      // nodeList: []   //当前所有节点
      edgeLogicConfig: [
        {
          label: '是',
          value: '1'
        },
        {
          label: '否',
          value: '0'
        }
      ],
      // ------------------------------------ 整合命令中心 start -----------------------------------------
      appDetail: {}, // 应用节点详情
      originalCommandList: [], // 接口返回的源命令中心
      commandList: [], // 分组解析后的命令中心
      stepListForm: {
        stepList: []
      },
      refeshKey: 0, // 添加步骤时刷新的key
      isloadingPage: true,
      isStepErrorMsg: '',
      stepOptions: [],
      curSelectEditIndex: '', // 当前选中的index  level 0
      curSelectEditInd: '', // 当前选中的ind  level 1
      editDialog: {
        show: false,
        fieldValue: '',
        index: '',
        ind: '',
        typeIndex: '',
        type: '',
        checkRes: ''
      },
      // ------------------------------------ 整合命令中心 end -------------------------------------------
      templateSubSelects: [], // 模板下子流程选择
      collectData: { // 剪切板数据
        timer: null,
        timerCount: 3000,
        index: '',
        ind: '',
        typeIndex: '',
        type: ''
      }
    }
  },
  watch: {},
  created () { },
  beforeMount () { },
  mounted () {
    this.initGraph() // 初始化画布
    this.getCommandList() // 获取命令中心的指令

    this.registerCustomNode() // 注册左侧栏的自定义节点
    this.initEvent() // 初始化画布的一些事件
    this.bindGraphKeyShortCut() // 绑定画布的快捷操作

    this.getAppDetail()
    if (this.$route.query.template == 1) this.getTemplateSubFlowSelects()
  },
  destroyed () {
    this.isGraphChange = false
  },
  methods: {
    // 判断当前节点是否是传入的类型？edge - 边/custom-rect - 流程节点 /custom-polygon - 判断节点
    judgeCurCellShape (shape) {
      return this.curCell.store && this.curCell.store.data.shape === shape
    },
    // 初始化画布
    initGraph () {
      const defaultOptions = {
        container: this.getUniqueId('container'),
        // width:500,
        // height:500,
        height: this.getUniqueId('container').offsetHeight,
        scroller: {
          enabled: true
        },
        minimap: {
          enabled: true,
          container: this.getUniqueId('minimap'),
          // container: document.getElementById(this.minimapId),
          width: 200,
          height: 200,
          padding: 10,
          graphOptions: {
            async: true,
            createCellView (cell) {
              // 在小地图中不渲染边
              if (cell.isEdge()) {
                return null
              }
            }
          }
        }
        // connecting: {
        //   router: {
        //     name: 'manhattan',
        //     args: {
        //       startDirections: ['top'],
        //       endDirections: ['bottom'],
        //     },
        //   },
        // }
        // onToolItemCreated({ tool }) {
        //   const handle = tool
        //   const options = handle.options
        //   if (options && options.index % 2 === 1) {
        //     tool.setAttrs({ fill: 'red' })
        //   }
        // },
      }
      const graphOptions = Object.assign(defaultOptions, graphConfig)
      console.log('graphOptions--->', graphOptions)
      const graph = new Graph(graphOptions)
      this.graph = graph
      // this.graph.fromJSON(mockData)    //加载mock数据 hello-world
    },
    // 注册自定义节点
    registerCustomNode () {
      Graph.registerNode('custom-rect', nodeConfig.rectConfig, true)

      Graph.registerNode('custom-polygon', nodeConfig.polygonConfig, true)

      // Graph.registerNode('custom-circle', nodeConfig.circleConfig, true)

      // Graph.registerNode('custom-image', nodeConfig.imageConfig, true)

      // 注册额外的渲染vue节点
      Graph.registerVueComponent(
        'extra-node',
        {
          template: `<extra/>`,
          components: { extra: Extra }
        },
        true
      )

      // 注册命令节点
      Graph.registerVueComponent(
        'action-node',
        {
          template: `<action-node/>`,
          components: { ActionNode }
        },
        true
      )
    },
    // 初始化侧边栏
    initLeftStencil () {
      const stencil = new Stencil(
        Object.assign({ target: this.graph }, stencilConfig)
      )
      // 创建一些装载到组里面的模板节点
      this.stencil = stencil
      this.getUniqueId('stencil').appendChild(stencil.container)
      // document.querySelector('#stencil').appendChild(stencil.container)
    },
    // 初始化一些组内模板节点
    /* initStencilTemplateNode () {
      const graph = this.graph
      const r1 = graph.createNode({
        shape: 'custom-rect',
        label: '开始',
        attrs: {
          body: {
            rx: 20,
            ry: 26
          }
        },
        data: {
          nodeType: 'root' // 开始的根节点
        }
      })
      const r5 = graph.createNode({
        shape: 'custom-rect',
        label: '结束',
        attrs: {
          body: {
            rx: 20,
            ry: 26
          }
        },
        data: {
          nodeType: 'end'
        }
      })
      const r2 = graph.createNode({
        shape: 'custom-rect',
        label: '过程'
      })
      const r4 = graph.createNode({
        shape: 'custom-polygon',
        width: 160,
        height: 50,
        attrs: {
          body: {
            refPoints: '0,10 10,0 20,10 10,20'
          }
        },
        label: '条件判断',
        data: {
          flowName: '条件判断',
          type: 'action'
        }
      })
      // 自定义流程块的一些基本属性
      const extra1 = graph.createNode({
        shape: 'vue-shape',
        width: 220,
        height: 40,
        component: 'extra-node',
        data: {
          flowName: '代码块',
          desc: '描述',
          nodeState: '0',
          quote: false,
          type: 'code' // 当前自定义vue节点服务的类型为 代码块 - 跳转至旧的步骤设置
        },
        ports: { ...portsConfig }
      })

      const extra2 = graph.createNode({
        shape: 'vue-shape',
        width: 220,
        height: 40,
        component: 'extra-node',
        data: {
          flowName: '子流程块',
          desc: '描述',
          nodeState: '0',
          quote: true, // true 样式上为子流程 黄色
          type: 'flow' // 当前自定义vue节点服务的类型为 子流程 - 跳转至新的子流程图中
        },
        ports: { ...portsConfig }
      })

      this.stencil.load([r1, r5, extra1, extra2, r4], 'basic')
      // this.stencil.load([extra1], 'advanced')
    }, */
    // 初始化画布的一些事件
    initEvent () {
      const container = this.getUniqueId('container')
      // const container = document.getElementById('container')
      const graph = this.graph
      graph.on('node:mouseenter', ({ node }) => {
        // 判断当前移入的节点是否存在输出边 如果存在 就不能显示链接桩了 因为普通节点只能存在一个成功指向 条件判断节点可以存在成功、失败指向。
        const edges = graph.getConnectedEdges(node, {
          incoming: false,
          outgoing: true
        }) // 返回输出边

        if (edges.length === 0 && node.getData().actionCode != 'match' && node.getData().actionCode != 'loginMatch') {
          // 鼠标移入时 显示链接桩
          this.togglePorts(container, true)
        } else if (edges.length < 2 && (node.getData().actionCode == 'match' || node.getData().actionCode == 'loginMatch')) {
          // 鼠标移入时 显示链接桩
          this.togglePorts(container, true)
        }
      })

      graph.on('node:mouseleave', () => {
        // 鼠标移出时 隐藏链接桩
        this.togglePorts(container, false)
      }),
      // 边单击触发
      graph.on('edge:click', ({ edge }) => {
        // 当前点击了连接线
        this.togglePorts(container, false) // 移除所有链接桩
        // 给当前连接线 进行高亮
        edge.addTools({
          name: 'boundary'
        })
      })
      // 当前双击了连接线
      graph.on('edge:dblclick', ({ edge }) => {
        console.log('edge:dblclick', edge)
      })
      graph.on('edge:mouseleave', ({ edge }) => {
        edge.removeTool('boundary')
      })
      // 基类(边/节点)cell 双击时触发
      graph.on('cell:dblclick', ({ cell, e }) => {
        // 获取当前节点的邻居节点、前序节点、后续节点
        if (cell.isNode() && (cell.store.data.shape === 'vue-shape' || cell.store.data.shape === 'custom-polygon')) {
          console.log('当前为节点，shape为：', cell.store.data.shape)
        } else if (cell.store.data.shape === 'edge') {
          console.log('cell:dblclick')
        } else {
          console.log('当前为节点不进行弹框，shape为', cell.store.data.shape)
          return
        }
        this.curCell = cell
        this.setDataRightForm()
        this.drawVisible = true
        // //双击后触发显示element的draw 然后可以改变对应的节点信息
      })
      // 空白处点击
      graph.on('blank:click', () => {
        this.togglePorts(container, false)
        let edges = graph.getEdges()
        if (edges.length > 0) {
          edges.forEach((e) => {
            e.removeTools()
          })
        }
      })
      graph.on('node:custom-edit', (nodeData) => {
        let mapData = {
          flowCode: this.curFlowCode,
          templateFlowCode: this.templateFlowCode
        }
        this.$emit('onNodeClick', nodeData, mapData)
      })

      // 节点被新增到画布时触发
      graph.on('node:added', ({ cell }) => {
        console.log('Node added:', cell)
        this.curCell = cell
        this.addStep({ newIndex: this.stepListForm.stepList.length })
      })

      // 线段被连接时触发
      graph.on('edge:connected', ({ isNew, edge }) => {
        if (isNew) {
          const source = edge.getSourceCell()
          const target = edge.getTargetCell()
          console.log(`当前创建了新边 线的源头为 ：`, source)
          console.log(`当前创建了新边 线的指向为 ：`, target)

          // 当前是条件判断的情况下 需要默认第一条输出边为是，另外一条则为否 删除边后两者是、否逻辑应该互斥
          if (source.getData().actionCode != 'match' && source.getData().actionCode != 'loginMatch') {
            // 非条件判断节点
            // 在stepList中查找当前源头节点 并设置源头节点的成功指向skipTo
            const findNode = this.stepListForm.stepList.find(
              (item) => item.stepCode == source.id
            )
            findNode.trueSkipTo = target.id
          } else {
            // 条件判断节点
            // 判断当前的新边是否为条件判断节点的第一条输出边 如果是 则将其指向设置到stepList中skipTo 并将边 标为是
            const edges = graph.getConnectedEdges(source, {
              incoming: false,
              outgoing: true
            }) // 返回输出边
            console.log('看看条件判断节点的输出边:', edges)
            if (edges.length === 1) {
              // 当前为第一条条件判断的输出边 将其设置为是 并在stepList中设置指向skipTo
              this.stepListForm.stepList.find(
                (item) => item.stepCode == source.id
              ).trueSkipTo = target.id
              edges[0].setLabels('是')
            } else {
              const preEdgeLabel = edges[0].store.data.labels[0]
              console.log('前一条输出边为:', preEdgeLabel)
              if (preEdgeLabel === '是') {
                this.stepListForm.stepList.find(
                  (item) => item.stepCode == source.id
                ).falseSkipTo = target.id
                edges[1].setLabels('否')
              } else {
                this.stepListForm.stepList.find(
                  (item) => item.stepCode == source.id
                ).trueSkipTo = target.id
                edges[1].setLabels('是')
              }
            }
          }

          console.log('新边创建后stepList:', this.stepListForm.stepList)
        }
      })
      graph.on('cell:changed', ({ node, options }) => {
        // console.log('画布发生了改变')
        this.isGraphChange = true
      })
      graph.on('edge:mouseenter', ({ cell }) => {
        cell.addTools(
          {
            name: 'segments',
            args: {
              snapRadius: 20,
              attrs: {
                fill: '#a0c0ff'
              }
            }
          }, 'onhover'
        )
      })

      graph.on('edge:mouseleave', ({ cell }) => {
        if (cell.hasTools('onhover')) {
          cell.removeTools()
        }
      })
    },
    // 绑定一些画布快捷键操作
    bindGraphKeyShortCut () {
      const graph = this.graph
      graph.bindKey(['meta+c', 'ctrl+c'], () => {
        const cells = graph.getSelectedCells()
        if (cells.length) {
          graph.copy(cells)
        }
        return false
      })
      graph.bindKey(['meta+x', 'ctrl+x'], () => {
        const cells = graph.getSelectedCells()

        if (cells.length) {
          graph.cut(cells)
        }
        return false
      })
      graph.bindKey(['meta+v', 'ctrl+v'], () => {
        if (!graph.isClipboardEmpty()) {
          const cells = graph.paste({ offset: 32 })
          graph.cleanSelection()
          graph.select(cells)
        }
        return false
      })

      // undo redo
      graph.bindKey(['meta+z', 'ctrl+z'], () => {
        if (graph.history.canUndo()) {
          graph.history.undo()
        }
        return false
      })
      graph.bindKey(['meta+shift+z', 'ctrl+shift+z'], () => {
        if (graph.history.canRedo()) {
          graph.history.redo()
        }
        return false
      })

      // select all
      graph.bindKey(['meta+a', 'ctrl+a'], () => {
        const nodes = graph.getNodes()
        if (nodes) {
          graph.select(nodes)
        }
      })

      // delete
      graph.bindKey('backspace', () => {
        this.isGraphChange = true
        const cells = graph.getSelectedCells()
        if (cells.length) {
          graph.removeCells(cells)
          console.log('即将删除的cells', cells[0])
          if (cells[0].store.data.shape !== 'edge') {
            // 同时stepList也要移除对应节点
            const delIndex = this.stepListForm.stepList.findIndex(item => item.stepCode === cells[0].id)
            this.stepListForm.stepList.splice(delIndex, 1)
            console.log('删除节点后stepList', this.stepListForm.stepList, cells[0].id, delIndex)
          }
        }
      })

      // zoom
      graph.bindKey(['ctrl+1', 'meta+1'], () => {
        const zoom = graph.zoom()
        if (zoom < 1.5) {
          graph.zoom(0.1)
        }
      })
      graph.bindKey(['ctrl+2', 'meta+2'], () => {
        const zoom = graph.zoom()
        if (zoom > 0.5) {
          graph.zoom(-0.1)
        }
      })
    },
    // 切换节点元素的链接桩 显示/隐藏
    togglePorts (container, visible) {
      const ports = container.querySelectorAll('.x6-port-body')
      for (let i = 0, len = ports.length; i < len; i = i + 1) {
        ports[i].style.visibility = visible ? 'visible' : 'hidden'
      }
    },
    // 双击后回显右侧表单
    setDataRightForm () {
      // console.log('graph to JSON --->', this.graph.toJSON())
      const curCellJSON = this.graph
        .toJSON()
        .cells.find((item) => item.id === this.curCell.id)
      this.form.id = this.curCell.id
      console.log('curCellJSON --->', curCellJSON)
      // console.log('curCellJSON --curCellJSON.data.actionCode->', curCellJSON.data.actionCode)
      if (!this.judgeCurCellShape('edge')) {
        // 当前双击为节点
        this.form = {
          ...curCellJSON.data,
          flowName: curCellJSON.data
            ? curCellJSON.data.flowName
            : curCellJSON.attrs.text.text,
          desc: curCellJSON.data ? curCellJSON.data.desc : '',
          nodeState: curCellJSON.data ? curCellJSON.data.nodeState : '0'
        }
        // this.form.quote = curCellJSON.data ? curCellJSON.data.quote : ''
        // console.log('看看现在的form------>', this.form)
      } else {
        // 当前点击为边
        // this.form.label = curCellJSON.data ? curCellJSON.data.label : ''
        // this.form.desc = curCellJSON.data ? curCellJSON.data.desc : ''
        // this.form.nodeState = curCellJSON.data ? curCellJSON.data.nodeState : ''
      }

      // 通过当前节点的stepCode进行右侧步骤设置,要命令节点才有右侧的操作 其他全部没有！
      if (
        curCellJSON.data
        // curCellJSON.data.actionCode !== 'subFlow' &&
        // curCellJSON.data.actionCode !== 'codeBlock'
      ) {
        // 寻找出正确的步骤命令下标进行展示
        if (curCellJSON.data.stepCode) {
          this.curSelectEditIndex = this.stepListForm.stepList.findIndex(
            (item) => item.stepCode === curCellJSON.data.stepCode
          ) // level 0
          console.log('当前双击的curSelectEditIndex -- 通过stepCode方式寻找', this.curSelectEditIndex)
          this.handleStepOptions()
        } else if (curCellJSON.data.newIndex || curCellJSON.data.newIndex === 0) {
          // 存在newIndex 代表当前指令节点新增时候的顺序
          this.curSelectEditIndex = curCellJSON.data.newIndex
          console.log('当前双击的curSelectEditIndex -- 通过newIndex方式寻找', this.curSelectEditIndex)
          this.handleStepOptions()
        }
      } else {
        this.resetSelectStep()
        console.log('当前双击的curSelectEditIndex', this.curSelectEditIndex)
      }
    },
    // 右侧表单drawer关闭前回调
    handleClose (done) {
      done()
    },
    // 右侧表单内取消按钮点击回调
    cancelForm () {
      this.loading = false
      this.drawVisible = false
      clearTimeout(this.timer)
    },
    // 右侧表单节点保存
    onSaveForm () {
      console.log(
        '看看命令的值？？？？？？？？',
        this.stepListForm.stepList[this.curSelectEditIndex]
      )

      this.curCell.setData({
        ...this.curCell.data,
        desc: this.form.desc || '',
        flowName: this.form.flowName || '',
        nodeState: this.form.nodeState || '0',
        quote: !!this.form.quoteCode,
        quoteCode: this.form.quoteCode || undefined,
        nodeLogic: this.form.nodeLogic || undefined,
        flowCode: this.form.quoteCode || this.form.flowCode || null,
        flowId: this.form.quoteCode
          ? this.nodeList.find((item) => item.flowCode === this.form.quoteCode)
            .id
          : this.form.flowId, // 如果是引用时 引用flowId 否则用自己flowId
        // penEdit: this.$route.query.template ? this.stepListForm.stepList[this.curSelectEditIndex].openEdit : undefined    //若当前为模板编辑，则将是否开放编辑设置到data里面
        openEdit: this.curSelectEditIndex ? this.stepListForm.stepList[this.curSelectEditIndex].openEdit : false // 全部开放
      })

      // 如果当前是模板并且是子流程的确定 需要拼上subFlowCode给后端  2023/7/3
      if (this.$route.query.template == 1 && this.stepListForm.stepList[this.curSelectEditIndex].actionCode === 'subFlow') {
        let tempItem = this.stepListForm.stepList[this.curSelectEditIndex].actionArgsVOS.find(item => item.fieldKey === 'flowName') // 找到actionArgsVOS里面的流程名称项
        console.log('当前找到', tempItem)
        if (tempItem.fieldValue) {
          // 模板下， 不校验子流程的流程名称是否选择了 选择了才赋值flowName和SubFlowCode 2023/07/18
          this.stepListForm.stepList[this.curSelectEditIndex].actionArgs = JSON.stringify({
            flowName: tempItem.fieldValue,
            subFlowCode: this.templateSubSelects.find(el => el.flowName == tempItem.fieldValue).flowCode
          })
        } else {
          // 没值就设置为空串即可 2023/07/18
          this.stepListForm.stepList[this.curSelectEditIndex].actionArgs = JSON.stringify({
            // flowName: '',
            // subFlowCode: ''
          })
        }
        console.log('当前是模板并且是子流程的确定 stepList ---》', this.stepListForm.stepList)
      }
      if (
        this.judgeCurCellShape('vue-shape') ||
        this.judgeCurCellShape('extra-node') ||
        this.judgeCurCellShape('custom-polygon')
      ) {
        // 通过setData已经改变了对象的值

        console.log(
          'extra-node 属性改变',
          this.form.quoteCode,
          this.curCell.store.data
        )
        if (this.judgeCurCellShape('custom-polygon')) {
          // 用于条件判断
          this.curCell.setData({
            step: JSON.stringify(
              this.stepListForm.stepList[this.curSelectEditIndex]
            ),
            flowName:
              this.stepListForm.stepList[this.curSelectEditIndex].stepName
          })

          this.curCell.attr(
            'label/text',
            this.stepListForm.stepList[this.curSelectEditIndex].stepName
          )
        } else if (!this.stepListForm.stepList[this.curSelectEditIndex]) {
          // 子流程和流程块
          this.curCell.setData({
            step: JSON.stringify(
              this.stepListForm.stepList[this.curSelectEditIndex]
            )
          })
        } else {
          this.curCell.setData({
            step: JSON.stringify(
              this.stepListForm.stepList[this.curSelectEditIndex]
            ),
            flowName:
              this.stepListForm.stepList[this.curSelectEditIndex].stepName
          })
        }
      } else {
        if (!this.judgeCurCellShape('edge')) {
          // 当前保存的类型不为边
          this.curCell.attr('label/text', this.form.flowName)
          // 同时设置它的业务状态
        } else {
          // 当前保存的是边 设置边的是/否
          this.curCell.setLabels(this.form.flowName)
        }
        this.renderCellAttr()
      }

      this.$refs.drawer.closeDrawer() // 关闭drawer
    },
    // 根据表单状态渲染基类cell
    renderCellAttr () {
      if (!this.judgeCurCellShape('edge')) {
        // console.log(' this.form.nodeState', this.form.nodeState)
        this.curCell.setAttrs({
          body: {
            fill:
              this.form.nodeState === '2'
                ? '#FDE2E2'
                : this.form.nodeState === '1'
                  ? '#E1F3D8'
                  : '#EFF4FF',
            stroke:
              this.form.nodeState === '2'
                ? '#F56C6C'
                : this.form.nodeState === '1'
                  ? '#67C23A'
                  : '#5F95FF'
          }
        })
      } else {
        this.curCell.setAttrs({
          line: {
            stroke:
              this.form.nodeState === '0'
                ? '#A2B1C3'
                : this.form.nodeState === '1'
                  ? '#67C23A'
                  : '#F56C6C'
          }
        })
      }
    },
    // 将图表转换为JSON
    graphToJSON () {
      if (this.graph) {
        console.log(this.graph.toJSON(), '<-----图表转换的json数据')
        return this.graph.toJSON().toString()
      }
    },
    // 回显默认json 并渲染对应状态
    testSwitchState () {
      this.graph.fromJSON(mockStateData)
    },
    // 从JSON渲染图
    graphFromJSON (json) {
      if (this.graph) {
        this.graph.fromJSON(json)
      }
    },
    // 从JSON渲染图
    graphFromJSON2 (json) {
      json.cells.map(item => {
        if (item.data) {
          item.data.isDisabledStepSet = this.templateFlowCode !== '' && item.data.openEdit === 0
        }
      })
      console.log('graphFromJSON2')
      console.log(json)
      if (this.graph) {
        this.graph.fromJSON(json)
        var nodes = this.graph.getNodes()
        var nodeCell
        nodes.map(node => {
          if (node.data && (node.data.actionCode === 'match' || node.data.actionCode === 'loginMatch') && this.templateFlowCode !== '' && node.data.openEdit === 0) {
            nodeCell = this.graph.getCellById(node.id)
            nodeCell.attr('body/fill', '#eeeeee')
            nodeCell.attr('body/stroke', '#dbdbdb')
          }
        })
      }
    },
    isBigFUN (num1, num2) {
      return num1 - num2 > 0.0001
    },
    // 快捷栏的操作
    toolbarFUN (type) {
      switch (type) {
        case 'zoomAdd':
          {
            // 放大
            if (this.isBigFUN(this.zoomSize.max, this.zoomSize.current)) {
              this.zoomSize.current += 0.1
              this.graph.zoomTo(this.zoomSize.current)
            }
          }
          break
        case 'zoomReduce':
          {
            // 缩小
            if (this.isBigFUN(this.zoomSize.current, this.zoomSize.min)) {
              this.zoomSize.current -= 0.1
              this.graph.zoomTo(this.zoomSize.current)
            }
          }
          break
        case 'clear':
          {
            // 清空
            this.graph.clearCells()
            // 清空stepList
            this.stepListForm.stepList = []
          }
          break
        case 'undo':
          {
            // 撤回
            this.graph.history.undo()
          }
          break
        case 'redo':
          {
            // 重做
            this.graph.history.redo()
          }
          break
        case 'save':
          {
            // if (this.graph.isFrozen()) {
            //   this.$message.warning('当前画布已冻结，无法保存')
            //   return
            // }

            // if (!this.checkTempalteSave()) {
            //   return
            // }
            // 保存
            const json = this.graph.toJSON()
            if (json.cells.length === 0) {
              // localStorage.removeItem("graphData");
              this.$message.warning('请先添加流程节点后再保存～')
            } else {
              // this.handleSave(json)
              // console.log('调用保存时stepList:', this.stepListForm.stepList)
              this.isGraphChange = false
              this.doSave(json)
            }
          }
          break
        case 'png':
          {
            // 导出图片
            this.graph.toPNG((dataUri) => {
              DataUri.downloadDataUri(dataUri, 'flow-chart.png')
            })
          }
          break
        case 'export':
        case 'export':
          {
            // 导出
            this.graphToJSON()
            this.exportGraphJSON()
          }
          break
        case 'import':
          {
            // 导入
            this.openFilePicker()
          }
          break
        case 'center':
          {
            // 画布居中
            this.graph.centerContent()
          }
          break
        case 'scale':
          {
            // 适应画布
            this.graph.zoomToFit()
          }
          break
        case 'adapt':
          {
            // 实际尺寸
            this.graph.zoomTo(1)
          }
          break
        case 'terminal': {
          // 控制台输出
          this.graphToJSON()
          // this.freezeGraph()
        }
      }
    },
    // 文件选择json读取
    openFilePicker () {
      const input = document.createElement('input')
      input.type = 'file'
      input.accept = '.json' // 只允许选择 txt 文件
      input.onchange = (event) => {
        const file = event.target.files[0]
        const reader = new FileReader()
        reader.readAsText(file)
        reader.onload = () => {
          const fileContent = reader.result
          // 在这里可以对文件内容进行处理
          console.log('fileContent --->', fileContent, typeof fileContent)
          this.graphFromJSON(JSON.parse(fileContent))
        }
      }
      input.click()
    },
    // 导出图表json文件
    exportGraphJSON () {
      // 将 JSON 对象转换为字符串
      let jsonString = JSON.stringify(this.graph.toJSON())
      // 使用浏览器内置 API 将字符串写入文件
      let blob = new Blob([jsonString], { type: 'application/json' })
      let url = URL.createObjectURL(blob)
      let link = document.createElement('a')
      link.setAttribute('href', url)
      link.setAttribute('download', 'chart.json')
      link.click()
    },
    onQuoteNodeChange (e) {
      console.log('onQuoteNodeChange------>', e)
    },
    // 重置右侧表单
    /* resetRightForm () {
      this.form = {
        id: '', // 当前对应节点的id
        flowName: '', // 对应节点的名称
        desc: '', // 加入到节点json中的描述
        nodeState: '0', // 节点状态
        flowCode: '', // 当前节点flowCode 如果为引用节点则将引用节点的flowCode置于此
        quoteCode: '', // 引用节点
        quote: false, // 当前节点是否为引用
        quoteId: false, // 引用节点id
        expression: '', // 节点表达式（判断节点才存在）
        nodeLogic: null // 节点逻辑（是/否 线段才有 用于真正判断线的逻辑 节点名称只是显示功能）
      }
    }, */
    // 获取各画布唯一containerId
    getUniqueId (type) {
      if (type === 'container') {
        return document.getElementById(this.containerId)
      } else if (type === 'stencil') {
        return document.getElementById(this.stencilId)
      } else if (type === 'minimap') {
        return document.getElementById(this.minimapId)
      }
    },
    // 冻结当前画布
    /* freezeGraph () {
      if (this.graph && !this.graph.isFrozen()) this.graph.freeze()
    }, */
    // ------------------------------------------------------  流程器 整合命令中心 start ------------------------------------------------------
    // 获取应用（节点）详情
    getAppDetail () {
      if (this.mFlowId === 0 || typeof this.mFlowId === 'string') return
      let that = this
      this.$http({
        url: '/robot/flow/step/queryDescribe?id=' + this.mFlowId, // 传入节点的流程id
        method: 'post'
      })
        .then(({ data }) => {
          that.appDetail = data.data // 当前应用节点的详情
        })
        .catch((data) => { })
    },
    // 获取命令中心
    getCommandList () {
      let that = this
      this.$http({
        url: '/robot/action/all',
        method: 'post'
      })
        .then(({ data }) => {
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
          // 流程控制 命令集 需要过滤掉 、最后子流程
          that.commandList = commandList.map((item) => {
            if (item.groupName === '流程控制') {
              item.list = item.list.filter(
                (sitem) => sitem.actionCode !== 'finalFlow'
              )
              return item
            } else {
              return item
            }
          })
          console.log('commandList-------->', commandList)
          // that.commandList = commandList

          that.addCommandNodePanel() // 准备左侧侧边栏节点组
          that.initLeftStencil() // 初始化侧边栏
          // that.initStencilTemplateNode() //初始化侧边栏节点
          that.preAddCommondNode() // 侧边栏新增指令
          that.getStepList()
        })
        .catch((data) => {
          // that.isloadingPage = false
        })
    },
    // 获取步骤
    getStepList () {
      // if (!this.mFlowCode) return
      let that = this
      let url = ''
      console.log('this.mStepObj=')
      console.log(this.templateFlowCode)
      if (this.mStepObj) {
        // 当前是子流程获取步骤
        if (this.templateFlowCode) {
          // 引用模板的情况-------------
          url = `/robot/flow/step/list/${this.curFlowCode}?flowType=2&templateFlowCode=${this.templateFlowCode}`
        } else {
          url = `/robot/flow/step/list/${this.curFlowCode}?flowType=2`
        }
        console.log('url===' + url)
      } else {
        // 普通主流程获取步骤
        if (this.templateFlowCode) {
          // 引用模板的情况-------------
          url = `/robot/flow/step/list/${this.curFlowCode}?templateFlowCode=${this.templateFlowCode || ''}`
        } else {
          url = `/robot/flow/step/list/${this.curFlowCode}`
        }
      }

      this.$http({
        url: url,
        method: 'post'
      })
        .then(({ data }) => {
          var originalStepList = data.data
          var stepList = []
          var resultIndex = -1
          var subItem
          var actionObj
          for (var i = 0; i < originalStepList.length; i++) {
            subItem = originalStepList[i]
            actionObj = that.getActionObj(subItem.actionCode)
            if (actionObj == '') {
              that.isloadingPage = false
              that.isStepErrorMsg =
                '步骤中第' +
                (i + 1) +
                '条数据 actionCode=' +
                subItem.actionCode +
                ' 已经不存在命令中心，请检查'
              return
            }
            subItem.openEdit = subItem.openEdit || 0
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
          console.log('stepList------->', stepList)
          that.stepListForm.stepList = stepList
          that.handleTemplateDropDownSelect() // 模板下的子流程步骤 不是输入框 需要改成下拉选择 选择项是''
          that.handleStepOptions() // 处理成功和失败的跳转步骤，为已建步骤的下拉选择框
          that.isloadingPage = false
        })
        .catch((data) => {
          that.isloadingPage = false
        })
    },
    // 模板下的子流程步骤 需要改变为下拉
    handleTemplateDropDownSelect () {
      console.log('this.$route.query.template == 1', this.$route.query.template == 1)
      // 如果当前为模板 则需要请求子流程的下拉值 并且将stepList中子流程输入类型从text改变为slot
      if (this.$route.query.template == 1) {
        try {
          let temp = []
          for (const item of this.stepListForm.stepList) {
            if (item.actionCode === 'subFlow') {
              let tempItem = item.actionArgsVOS.find(sitem => sitem.fieldKey === 'flowName')
              tempItem.fieldType = 'slot'
              tempItem.required = 0
            }
            temp.push(item)
          }
          this.stepListForm.stepList = temp
          console.log('看看改造后 模板下的子流程步骤 stepList', this.stepListForm.stepList)
          this.getTemplateSubFlowSelects()
        } catch (error) {
          console.log(error)
        }
      }
    },
    // 请求获取子流程下拉的选择
    async getTemplateSubFlowSelects () {
      const { data: { code, data: result } } = await this.$http({
        url: '/robot/flow/listTemplate',
        method: 'post',
        params: { templateType: 1, flowType: 2 }
      })
      if (code == 200) {
        console.log('请求获取子流程下拉的选择', result)
        this.templateSubSelects = result
      }
    },
    // 模板下子流程选择回调
    onTemplateSelectChange (e) {
      console.log('模板下子流程选择回调', e)
    },
    getActionObj (actionCode) {
      var obj = this.originalCommandList.find((item) => {
        return item.actionCode === actionCode
      })
      return obj || ''
    },
    // 处理动态字段返回的数据
    handleArgs (arr) {
      // 动态字段同一组中最多只有一个cond不为空，cond控制同组中其他字段的显示或隐藏
      var condRes = false
      var showFieldKeyArr = []
      arr.map((item) => {
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
        arr.map((item2) => {
          if (
            !(item2.cond != '' && item2.cond != null) &&
            showFieldKeyArr.indexOf(item2.fieldKey) == -1
          ) {
            item2.isHideCust = true
          }
        })
      }
      return arr
    },
    // 处理成功和失败的跳转步骤，为已建步骤的下拉选择框
    handleStepOptions () {
      let stepList = this.stepListForm.stepList
      let activeStepName =
        this.curSelectEditInd != undefined &&
          this.curSelectEditInd != null &&
          this.curSelectEditInd !== ''
          ? stepList[this.curSelectEditIndex].list[this.curSelectEditInd]
            .stepName
          : stepList[this.curSelectEditIndex].stepName

      let stepOptions = []
      stepList.map((item) => {
        if (item.stepName && item.stepName != activeStepName) {
          stepOptions.push(item.stepName)
        }
        item.list.map((it) => {
          if (it.stepName && it.stepName != activeStepName) {
            stepOptions.push(it.stepName)
          }
        })
      })
      //  如果之前选中的值已经不在下拉值中，那么需要清空
      stepList.map((item) => {
        if (
          stepOptions.indexOf(item.skipTo) === -1 &&
          item.skipTo != activeStepName
        ) {
          item.skipTo = ''
        }
        if (
          stepOptions.indexOf(item.failedSkipTo) === -1 &&
          item.failedSkipTo != activeStepName
        ) {
          item.failedSkipTo = ''
        }
        item.list.map((it) => {
          if (
            stepOptions.indexOf(it.skipTo) === -1 &&
            it.skipTo != activeStepName
          ) {
            it.skipTo = ''
          }
          if (
            stepOptions.indexOf(it.failedSkipTo) === -1 &&
            it.failedSkipTo != activeStepName
          ) {
            it.failedSkipTo = ''
          }
        })
      })
      this.stepOptions = stepOptions

      // console.log('stepOptions----------',stepOptions)
    },
    showEditDialog (index, ind, typeIndex, type) {
      let editDialog = this.editDialog
      if (typeIndex !== '') {
        //  子步骤
        editDialog.fieldValue =
          this.stepListForm.stepList[index].list[ind][type][
            typeIndex
          ].fieldValue
      } else {
        //  步骤
        editDialog.fieldValue =
          this.stepListForm.stepList[index][type][ind].fieldValue
      }
      editDialog.index = index
      editDialog.ind = ind
      editDialog.typeIndex = typeIndex
      editDialog.type = type
      editDialog.checkRes = ''
      editDialog.show = true
    },
    addElementValueJson () {
      this.editDialog.fieldValue =
        this.editDialog.fieldValue +
        '{"element":{"attrType":"xpath","attrValue":"","timeout":10}}'
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
    confirmEditValue () {
      let editDialog = this.editDialog
      let index = editDialog.index
      let ind = editDialog.ind
      let typeIndex = editDialog.typeIndex
      let type = editDialog.type
      if (editDialog.typeIndex !== '') {
        //  子步骤
        this.stepListForm.stepList[index].list[ind][type][
          typeIndex
        ].fieldValue = editDialog.fieldValue
      } else {
        //  步骤
        this.stepListForm.stepList[index][type][ind].fieldValue =
          editDialog.fieldValue
      }
      editDialog.show = false
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
          if (!/(^(-?\d+)\d*$)/.test(value)) {
            // 必须为整数
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
    // 通过命令中心新加各组左侧的节点组
    addCommandNodePanel () {
      const graph = this.graph
      // 获取当前侧边栏的分组
      // console.log('stencilConfig----addCommandNodePanel-------',stencilConfig)
      if (stencilConfig.groups.length > 1) return
      this.commandList.map((item) => {
        stencilConfig.groups.push({
          name: item.groupName,
          title: item.groupName,
          graphHeight: item.list.length * 80,
          collapsed: item.groupName !== '流程控制'
        })
      })
    },
    // 通过命令中心新加各组左侧的节点
    preAddCommondNode () {
      const graph = this.graph
      // 获取当前侧边栏的分组
      // console.log('stencilConfig-----preAddCommondNode------',stencilConfig)
      if (stencilConfig.groups.length > this.commandList.length + 1) return // 不能超过基础节点 + 指令中心的指令类型数 不然会重新渲染
      this.commandList.map((item) => {
        const actionNodeList = item.list.map((actionItem) => {
          // console.log('actionItem-------------', actionItem)
          // 根据指令的actionCode 将其对应渲染节点形状
          if (
            actionItem.actionCode === 'start' ||
            actionItem.actionCode === 'end'
          ) {
            return graph.createNode({
              shape: 'custom-rect',
              label: actionItem.actionName,
              attrs: {
                body: {
                  rx: 20,
                  ry: 26
                }
              },
              data: {
                ...actionItem
              }
            })
          } else if (actionItem.actionCode === 'match' || actionItem.actionCode === 'loginMatch') {
            return graph.createNode({
              shape: 'custom-polygon',
              width: 160,
              height: 50,
              attrs: {
                body: {
                  refPoints: '0,10 10,0 20,10 10,20'
                }
              },
              label: actionItem.actionName,
              data: {
                flowName: actionItem.actionName,
                ...actionItem
                // type: 'action'
              }
            })
          } else if (actionItem.actionCode === 'codeBlock') {
            return graph.createNode({
              shape: 'vue-shape',
              width: 220,
              height: 40,
              component: 'extra-node',
              data: {
                flowName: '',
                // flowName: actionItem.actionName,
                desc: '描述',
                nodeState: '0',
                quote: false,
                ...actionItem
                // type: 'code'   //当前自定义vue节点服务的类型为 代码块 - 跳转至旧的步骤设置
              },
              ports: { ...portsConfig }
            })
          } else if (actionItem.actionCode === 'subFlow') {
            return graph.createNode({
              shape: 'vue-shape',
              width: 220,
              height: 40,
              component: 'extra-node',
              data: {
                // flowName: actionItem.actionName,
                flowName: '',
                desc: '描述',
                nodeState: '0',
                quote: false,
                ...actionItem
                // type: 'flow'   //当前自定义vue节点服务的类型为 子流程 - 跳转至新的子流程图中
              },
              ports: { ...portsConfig }
            })
          } else {
            return graph.createNode({
              shape: 'vue-shape',
              width: 220,
              height: 40,
              component: 'action-node',
              data: {
                flowName: '',
                ...actionItem,
                desc: '描述',
                nodeState: '0',
                type: 'action' // 当前自定义vue节点服务的类型为 指令
              },
              ports: { ...portsConfig }
            })
          }
        })
        // console.log(`actionNodeList---${item.groupName}----`, actionNodeList)
        this.stencil.load(actionNodeList, item.groupName)
      })
    },
    // 左侧命令中心的命令拖入
    addStep (e) {
      // if (this.graph.isFrozen()) {
      //   console.log('当前画布已冻结，无法添加')
      //   return
      // }
      if (!this.checkTempalteSave()) {
        return
      }

      console.log('addStep----开始')
      // console.log(this.stepListForm.stepList)

      console.log(this.curCell.id, '当前节点画布ID------------')
      // if (this.curCell.getData().type !== 'action') return    //当前拉入非指令 直接无视
      const preActionObj = this.originalCommandList.find(
        (item) => item.actionCode === this.curCell.getData().actionCode
      )

      console.log('pre', preActionObj)

      this.stepListForm.stepList.push(preActionObj)
      this.curCell.setData({
        newIndex: e.newIndex,
        stepCode: this.curCell.id
      })

      this.resetSelectStep()
      var newObj = this.stepListForm.stepList[e.newIndex]
      let obj = this.getStepItem(newObj)
      // console.log('obj===')
      // console.log(obj)
      obj.list = []
      obj.stepCode = this.curCell.id
      if (obj.actionCode === 'start') {
        obj.stepName = '开始'
      } else if (obj.actionCode === 'end') {
        obj.stepName = '结束'
      }

      // 同时将当前节点的画布id塞到stepList里面的stepCode
      this.$set(this.stepListForm.stepList, e.newIndex, obj)
      this.refeshKey = new Date().getTime()

      if (!obj.actionArgsVOS) {
        this.getActionArgs(e.newIndex)
      }
      if (!obj.targetArgsVOS) {
        this.getTargetArgs(e.newIndex)
      }

      console.log(this.curCell.getData(), '增加后节点data------------')
      console.log('增加后----------stepList', this.stepListForm.stepList)
    },
    // 重新设置右侧的要显示的步骤index
    resetSelectStep () {
      this.curSelectEditIndex = null
      this.curSelectEditInd = null
    },
    // 判断字符串是否为空
    isNotEmptyString (str) {
      return str !== '' && str !== undefined
    },
    // 生成步骤Item
    getStepItem (newObj) {
      return {
        groupName: newObj.groupName,
        actionName: newObj.actionName,
        actionCode: newObj.actionCode,
        stepName: this.isNotEmptyString(newObj.stepName) ? newObj.stepName : '',
        failedRetry: this.isNotEmptyString(newObj.failedRetry)
          ? newObj.failedRetry
          : '',
        skipTo: this.isNotEmptyString(newObj.skipTo) ? newObj.skipTo : '',
        failedSkipTo: this.isNotEmptyString(newObj.failedSkipTo)
          ? newObj.failedSkipTo
          : '',
        skipCondition: this.isNotEmptyString(newObj.skipCondition)
          ? newObj.skipCondition
          : '',
        waitBefore: this.isNotEmptyString(newObj.waitBefore)
          ? newObj.waitBefore
          : '',
        waitAfter: this.isNotEmptyString(newObj.waitAfter)
          ? newObj.waitAfter
          : '',
        timeout: this.isNotEmptyString(newObj.timeout) ? newObj.timeout : '',
        status: this.isNotEmptyString(newObj.status) ? newObj.status : 1,
        failedStrategy: this.isNotEmptyString(newObj.failedStrategy)
          ? newObj.failedStrategy
          : 0,
        actionArgsVOS: newObj.actionArgsVOS ? newObj.actionArgsVOS : '',
        targetArgsVOS: newObj.targetArgsVOS ? newObj.targetArgsVOS : '',
        // 模板编辑时 会有是否开放编辑
        openEdit: this.$route.query.template ? 0 : null
      }
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
      })
        .then(({ data }) => {
          var res = that.handleArgs(data.data)
          if (ind != undefined) {
            //  子步骤
            that.$set(
              that.stepListForm.stepList[index].list[ind],
              'targetArgsVOS',
              res
            )
          } else {
            //  步骤
            that.$set(that.stepListForm.stepList[index], 'targetArgsVOS', res)
          }
        })
        .catch((data) => { })
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
      })
        .then(({ data }) => {
          var res = that.handleArgs(data.data)
          if (ind != undefined) {
            //  子步骤
            that.$set(
              that.stepListForm.stepList[index].list[ind],
              'actionArgsVOS',
              res
            )
          } else {
            //  步骤
            if (actionCode === 'subFlow' && this.$route.query.template == 1) {
              console.log('看看------------------', res)
              let tempItem = res.find(sitem => sitem.fieldKey === 'flowName')
              tempItem.fieldType = 'slot'
              tempItem.required = 0
              that.$set(that.stepListForm.stepList[index], 'actionArgsVOS', res)
            } else {
              that.$set(that.stepListForm.stepList[index], 'actionArgsVOS', res)
            }
          }
        })
        .catch((data) => { })
    },

    /* handleSave (json) {
      let that = this
      this.$refs.stepListForm.validate((valid, errorMessage) => {
        if (valid) {
          console.log('校验通过')
          that.doSave(json)
        } else {
          console.log(errorMessage)
          for (var i in errorMessage) {
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
          }
        }
      })
    }, */
    // 保存步骤List json为画布Json
    doSave (json) {
      let that = this
      var setData = []; var item; var subItem; var number = 0; var actionArgs; var targetArgs; var stepNameArr = []
      for (let i = 0; i < that.stepListForm.stepList.length; i++) {
        item = { ...that.stepListForm.stepList[i] }
        if (item.stepName !== '' && stepNameArr.indexOf(item.stepName) == -1) {
          stepNameArr.push(item.stepName)
        } else {
          if (item.stepName === '') {
            that.$message.warning('步骤名称不能为空，请检查')
            return
          }
          console.log('有重复步骤名称item', item, stepNameArr)
          that.$message.warning('存在重复的步骤名称"' + item.stepName + '"，请检查')
          return
        }
        var k = 0
        for (k = 0; k < item.targetArgsVOS.length; k++) {
          if (item.targetArgsVOS[k].required === 1 && !item.targetArgsVOS[k].isHideCust && !item.targetArgsVOS[k].fieldValue) {
            that.$message.warning('【' + item.stepName + '】存在需填写的操作目标')
            return
          }
        }
        for (k = 0; k < item.actionArgsVOS.length; k++) {
          if (item.actionArgsVOS[k].required === 1 && !item.actionArgsVOS[k].isHideCust && !item.actionArgsVOS[k].fieldValue) {
            that.$message.warning('【' + item.stepName + '】存在需填写的行为参数')
            return
          }
        }

        // item.flowCode = that.flowCode
        item.flowCode = that.curFlowCode
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

        item.actionArgs = item.actionArgs && JSON.parse(item.actionArgs).subFlowCode ? item.actionArgs : JSON.stringify(actionArgs)
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
            subItem.flowCode = that.curFlowCode
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
      console.log('看看转换后stepList---------------', that.stepListForm.stepList)
      console.log('看看转换后setData--------------', setData)

      if (setData.length == 0) {
        that.$message.warning('请先设置步骤')
        return
      }
      const data = JSON.stringify(json)
      this.$emit('save', data, setData, this.mStepObj, this.templateFlowCode, this.curFlowCode)
      // return
      // this.$http({
      //   url: '/robot/flow/step/save?flowCode='+that.flowCode,
      //   method: 'post',
      //   data: setData
      // }).then(({data}) => {
      //   that.$message.success("保存成功")
      //   /*setTimeout(() => {
      //     that.$router.push('/processSetting/setting?appCode='+that.appCode)
      //   }, 1500)*/
      // }).catch((data) => {
      // })
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
    // 检查当前流程编辑器是否可以进行保存
    checkTempalteSave () {
      console.log('checkTempalteSave', this.templateType)
      // 当 templateFlowCode不为空时，当前为引用模板。同时判断addTab getByCode返回的templateType 如果为1代表是模板，就不能保存编辑否则就是可以进行保存
      if (this.templateFlowCode && this.templateType == 1) {
        this.$message.warning('当前为引用模板，无法保存编辑')
        return false
      } else {
        return true
      }
    },
    // ------------------------------------------------------  流程器 整合命令中心 end --------------------------------------------------------
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
    isDisabledStepSet () {
      return this.templateFlowCode !== '' && this.stepListForm && this.stepListForm.stepList && this.curSelectEditIndex !== '' && this.curSelectEditIndex !== null && this.stepListForm.stepList[this.curSelectEditIndex].openEdit === 0
    },
    isDisabledStepSetArgs () {
      return this.templateFlowCode !== '' && this.stepListForm && this.stepListForm.stepList && this.curSelectEditIndex !== '' && this.curSelectEditIndex !== null && this.stepListForm.stepList[this.curSelectEditIndex].openEdit === 1
    }
  },
  computed: {
    drawerFormConfig () {
      // if (this.judgeCurCellShape('custom-polygon')) {
      //   return this.formColumns[1]
      // } else {
      //   return this.formColumns[0]
      // }
      return this.formColumns[0]
    }
  }
}
</script>
<style lang="less" scoped>
@import '../config/x6-widget.less';
@import '../config/step-right.less';

.bg-purple {
  background-color: #f5f5f5;
}

.grid-content {
  height: 75vh;
}

.dataV {
  background: #ffffff;
  padding: 10px;
}

.el-form-item .el-select {
  width: 100%;
}

// ---------- drawer -----------
.demo-drawer {
  display: flex;
  flex-direction: column;

  .demo-drawer__content {
    flex: 1;
    padding: 0 16px;
  }

  .demo-drawer__footer {
    display: flex;
    justify-content: center;
    padding-bottom: 10px;
  }
}

.minimap {
  position: fixed;
  right: 20px;
  bottom: 20px;
  width: 200px;
  height: 200px;
  border: 1px solid #ccc;
  border-radius: 4px;
  background-color: #fafbfc;
}

@media screen and (max-height: 350px) {
  .minimap {
    visibility: hidden;
  }
}

// 快捷栏
.shortcut-bar {
  display: flex;
  padding: 5px;

  .rate {
    // display: inline-block;
    width: 60px;
    text-align: center;
    color: grey;
    line-height: 48px;
  }

  .rx-row {
    width: 60px;
    height: 48px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    user-select: none;
    transition: all linear 0.1s;
    position: relative;
    border-radius: 4px;

    span {
      transform: scale(0.8);
    }

    &:hover {
      cursor: pointer;
      background-color: #f6f6f6;
      border-radius: 2px;
    }
  }
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

</style>
