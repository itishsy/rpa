<template>
  <!-- 报盘文件设置 -->
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <el-row v-if="headStatisticsInfo">
        <el-col :span="6">
          <div class="card-layout bg-green">
            <div class="flex-col top">城市数<span class="top-number">{{ headStatisticsInfo.cityCount }}</span></div>
            <div class="display-flex" style="align-items:center">
              <div class="flex-col">{{ headStatisticsInfo.onlineCount }}<span>上线</span></div>
              <i v-if="headStatisticsInfo.onlineIndex > 0" class="el-icon-top ml-10"></i>
              <i v-if="headStatisticsInfo.onlineIndex < 0" class="el-icon-bottom ml-10"></i>
            </div>
            |
            <div class="display-flex" style="align-items:center">
              <div class="flex-col">{{ headStatisticsInfo.offlineCount }}<span>下线</span></div>
              <i v-if="headStatisticsInfo.offlineIndex > 0" class="el-icon-top ml-10"></i>
              <i v-if="headStatisticsInfo.offlineIndex < 0" class="el-icon-bottom ml-10"></i>
            </div>
            |
            <div class="display-flex" style="align-items:center">
              <div class="flex-col">{{ headStatisticsInfo.launchedCount }}<span>待上线</span></div>
              <i v-if="headStatisticsInfo.launchedIndex > 0" class="el-icon-top ml-10"></i>
              <i v-if="headStatisticsInfo.launchedIndex < 0" class="el-icon-bottom ml-10"></i>
            </div>

          </div>
        </el-col>

        <el-col :span="6">
          <div class="card-layout bg-blue">
            <div class="flex-col top pointer" @click="pointerQuery([0, 1, 2])">配置<span class="top-number">{{
              headStatisticsInfo.configurationStageCount }}</span></div>
            <div class="flex-col pointer" @click="pointerQuery([0])">{{ headStatisticsInfo.surveyCount }}<span>调研</span>
            </div>|
            <div class="flex-col pointer" @click="pointerQuery([1])">{{ headStatisticsInfo.configurationCount
            }}<span>配置</span></div>|
            <div class="flex-col pointer" @click="pointerQuery([2])">{{ headStatisticsInfo.shakedownCount
            }}<span>调试通过</span></div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="card-layout bg-blue2">
            <div class="flex-col top pointer" @click="pointerQuery([3, 4])">测试<span class="top-number">{{
              headStatisticsInfo.testStageCount }}</span></div>
            <div class="flex-col pointer" @click="pointerQuery([3])">{{ headStatisticsInfo.waitCount }}<span>等待数据</span>
            </div>|
            <div class="flex-col pointer" @click="pointerQuery([4])">{{ headStatisticsInfo.checkCount }}<span>验证有效</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="card-layout bg-blue-dark">
            <div class="flex-col top pointer" @click="pointerQuery([5, 6])">运维<span class="top-number">{{
              headStatisticsInfo.operationStageCount }}</span></div>
            <div class="flex-col pointer" @click="pointerQuery([5])">{{ headStatisticsInfo.normalCount }}<span>正常运行</span>
            </div>|
            <div class="flex-col pointer" @click="pointerQuery([6])">{{ headStatisticsInfo.repairCount }}<span>修复问题</span>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-row>
        <div class="display-flex search-row" style="flex-wrap: wrap;padding:5px 10px 10px 10px;">
          <el-col :span="7" class="display-flex pt-5">
            <span>业务类型：</span>
            <div class="flex1">
              <el-checkbox-group v-model="searchForms.type" size="medium" style="display: flex;align-items: center;">
                <el-checkbox v-for="item in searchOptions.types" :key="item.id" :label="item.id">
                  {{ item.name }}
                </el-checkbox>
              </el-checkbox-group>
            </div>
          </el-col>
          <el-col :span="6" class="display-flex pt-5">
            <span>上线标记：</span>
            <div class="flex1">
              <el-checkbox-group v-model="searchForms.mark" size="medium">
                <el-checkbox v-for="item in searchOptions.mark" :key="item.id" :label="item.id">
                  {{ item.name }}
                </el-checkbox>
              </el-checkbox-group>
            </div>
          </el-col>
          <el-col :span="9" class="display-flex">
            <span class="label">应用状态：</span>
            <div class="flex1 display-flex">
              <el-select v-model="searchForms.status1" placeholder="请选择" clearable @change="onChangeStatus1" class="flex1" style="max-width: 280px">
                <el-option label="配置" :value="1"></el-option>
                <el-option label="测试" :value="2"></el-option>
                <el-option label="运维" :value="3"></el-option>
              </el-select>
              <el-select v-model="searchForms.status2" placeholder="请选择" clearable multiple collapse-tags class="flex1" style="max-width: 280px">
                <el-option v-if="searchForms.status1 == 1" label="调研" :value="0"></el-option>
                <el-option v-if="searchForms.status1 == 1" label="配置" :value="1"></el-option>
                <el-option v-if="searchForms.status1 == 1" label="调试通过" :value="2"></el-option>
                <el-option v-if="searchForms.status1 == 2" label="等待数据" :value="3"></el-option>
                <el-option v-if="searchForms.status1 == 2" label="验证有效" :value="4"></el-option>
                <el-option v-if="searchForms.status1 == 3" label="正常运行" :value="5"></el-option>
                <el-option v-if="searchForms.status1 == 3" label="修复问题" :value="6"></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="14" class="display-flex mt-15 pt-5">
            <span>服务项目：</span>
            <div class="flex1">
              <el-checkbox-group v-model="searchForms.service" size="medium">
                <el-checkbox v-for="item in searchOptions.service" :key="item.id" :label="item.id">
                  {{ item.name }}
                </el-checkbox>
              </el-checkbox-group>
            </div>
          </el-col>

          <el-col :span="10" class="mt-15">
            <div class="display-flex text-right" style="flex-wrap: wrap;align-content: end">

              <!-- <el-select v-model="formData.robot" class="search-row-item" clearable filterable placeholder="申报机器人">
                              <el-option v-for="it in formData.robotSort" :key="it.id" :label="it" :value="it"></el-option>
                            </el-select> -->
              <!-- <span class="ml-20 label">参保地：</span>
            <addrSelector v-model="formData.addrName" :addrArr="bussinssType.allAddr" @changeAddrSelect="changeSearch"></addrSelector> -->
              <el-input v-model.trim="searchForms.keyword" placeholder="请输入省/市/申报账户/应用名称/负责人关键字"
                        style="width:280px;" @keyup.enter.native="search" clearable></el-input>
              <el-button type="primary" size="mini" @click="search" class="ml-20 mb-10">查询</el-button>
              <el-button class="btn--border-blue ml-20" size="mini" @click="reset">重置</el-button>
              <el-button type="primary" size="mini" @click="addCity"
                         class="ml-20">添加应用</el-button>
              <el-button type="primary" size="mini" @click="addTemplate"
                         class="ml-20">添加流程模板</el-button>
              <el-button type="primary" size="mini" @click="addrChange"
                         class="ml-20">变更查询</el-button>
            </div>
          </el-col>
        </div>

      </el-row>
      <div>
        <dTable :data="tableListData" ref="table" style="margin-top: 0;" :tableHeight="tableHeight" :showIndex='true'
          :showSelection='false' row-key="id" row-id="id" :rowHeight="52">
          <u-table-column prop="appName" width="180" label="应用名称" header-align="center" align="center"
            :show-overflow-tooltip="true" fixed>
            <template slot-scope="scope">
              <span
                :class="['tag', { 'tag-red': scope.row.runStatus == 0 }, { 'tag': scope.row.runStatus == 1 }, , { 'tag-grey': scope.row.runStatus == 2 }]">{{
                  scope.row.runStatus == 0 ? '下线' : scope.row.runStatus == 1 ? '上线' : '待上线' }}</span>
              <el-tooltip class="item" effect="dark" :content="scope.row.appName" placement="left">
                <span>{{ scope.row.appName }}</span>
              </el-tooltip>
            </template>
          </u-table-column>
          <u-table-column prop="appName" width="80" label="所处阶段" header-align="center" align="center"
            :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{ scope.row.onlineStatus == 0 ? '配置' : scope.row.onlineStatus == 1 ? '测试' : '运维' }}</span>
            </template>
          </u-table-column>
          <u-table-column prop="appStatus" width="80" label="应用状态" header-align="center" align="center"
            :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span style="cursor:pointer" :class="scope.row.appStatus == 6 ? 'text-red' : 'text-blue'"
                @click="showFlowTable(scope.row)">{{
                  scope.row.appStatus != null ? appStatusArray[parseInt(scope.row.appStatus)] : '' }}</span>
            </template>
          </u-table-column>
          <u-table-column prop="remark" width="200" label="原因备注" header-align="center" align="center"
            :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="serviceItem" width="650" label="服务项目" header-align="center" align="center"
            :show-overflow-tooltip="false">
            <template slot-scope="scope">
              <div class="tag-layout">
                <el-badge :value="scope.row.addCount" :max="99"
                  :class="computeBadgeItem(scope.row.serviceVOList, 1, scope.row.addCount)"
                  :hidden="scope.row.addCount == 0">

                  <el-tag :type="getServiceItemTypeByVOList(scope.row.serviceVOList, 1, scope.row.addCount)"
                    :class="computeBadgeItem(scope.row.serviceVOList, 1, scope.row.payCount)">
                    <el-tooltip class="item" effect="dark" :content="computedTagContent(scope.row.serviceVOList, 1)"
                      placement="top" :disabled="computedToolTipDisabled(scope.row.serviceVOList, 1)">
                      <span>增员</span>
                    </el-tooltip>
                  </el-tag>
                </el-badge>
                <el-badge :value="scope.row.reduceCount" :max="99"
                  :class="computeBadgeItem(scope.row.serviceVOList, 2, scope.row.reduceCount)"
                  :hidden="scope.row.reduceCount == 0">

                  <el-tag :type="getServiceItemTypeByVOList(scope.row.serviceVOList, 2, scope.row.reduceCount)"
                    class="ml-15" :class="computeBadgeItem(scope.row.serviceVOList, 2, scope.row.payCount)">
                    <el-tooltip class="item" effect="dark" :content="computedTagContent(scope.row.serviceVOList, 2)"
                      placement="top" :disabled="computedToolTipDisabled(scope.row.serviceVOList, 2)">
                      <span>减员</span>
                    </el-tooltip>
                  </el-tag>
                </el-badge>
                <el-badge :value="scope.row.tjCount" :max="99"
                  :class="computeBadgeItem(scope.row.serviceVOList, 3, scope.row.tjCount)"
                  :hidden="scope.row.tjCount == 0">
                  <el-tag :type="getServiceItemTypeByVOList(scope.row.serviceVOList, 3, scope.row.tjCount)" class="ml-15"
                    :class="computeBadgeItem(scope.row.serviceVOList, 3, scope.row.payCount)">
                    <el-tooltip class="item" effect="dark" :content="computedTagContent(scope.row.serviceVOList, 3)"
                      placement="top" :disabled="computedToolTipDisabled(scope.row.serviceVOList, 3)">
                      <span>调基</span>
                    </el-tooltip>
                  </el-tag>
                </el-badge>
                <el-badge :value="scope.row.bjCount" :max="99"
                  :class="computeBadgeItem(scope.row.serviceVOList, 5, scope.row.bjCount)"
                  :hidden="scope.row.bjCount == 0">
                  <el-tag :type="getServiceItemTypeByVOList(scope.row.serviceVOList, 5, scope.row.bjCount)" class="ml-15"
                    :class="computeBadgeItem(scope.row.serviceVOList, 5, scope.row.payCount)">
                    <el-tooltip class="item" effect="dark" :content="computedTagContent(scope.row.serviceVOList, 5)"
                      placement="top" :disabled="computedToolTipDisabled(scope.row.serviceVOList, 5)">
                      <span>补缴</span>
                    </el-tooltip>
                  </el-tag>
                </el-badge>
                <el-badge :value="scope.row.jfCount" :max="99"
                  :class="computeBadgeItem(scope.row.serviceVOList, 6, scope.row.jfCount)"
                  :hidden="scope.row.jfCount == 0">
                  <el-tag :type="getServiceItemTypeByVOList(scope.row.serviceVOList, 6, scope.row.jfCount)" class="ml-15"
                    :class="computeBadgeItem(scope.row.serviceVOList, 6, scope.row.payCount)">
                    <el-tooltip class="item" effect="dark" :content="computedTagContent(scope.row.serviceVOList, 6)"
                      placement="top" :disabled="computedToolTipDisabled(scope.row.serviceVOList, 6)">
                      <span>缴费</span>
                    </el-tooltip>
                  </el-tag>
                </el-badge>
                <el-badge :value="scope.row.zcCount" :max="99"
                  :class="computeBadgeItem(scope.row.serviceVOList, 7, scope.row.zcCount)"
                  :hidden="scope.row.zcCount == 0">
                  <el-tag :type="getServiceItemTypeByVOList(scope.row.serviceVOList, 7, scope.row.zcCount)" class="ml-15"
                    :class="computeBadgeItem(scope.row.serviceVOList, 7, scope.row.payCount)">
                    <el-tooltip class="item" effect="dark" :content="computedTagContent(scope.row.serviceVOList, 7)"
                      placement="top" :disabled="computedToolTipDisabled(scope.row.serviceVOList, 7)">
                      <span>在册名单</span>
                    </el-tooltip>
                  </el-tag>
                </el-badge>
                <el-badge :value="scope.row.payCount" :max="99"
                  :class="computeBadgeItem(scope.row.serviceVOList, 8, scope.row.payCount)"
                  :hidden="scope.row.payCount == 0">
                  <el-tag :type="getServiceItemTypeByVOList(scope.row.serviceVOList, 8, scope.row.payCount)" class="ml-15"
                    :class="computeBadgeItem(scope.row.serviceVOList, 8, scope.row.payCount)">
                    <el-tooltip class="item" effect="dark" :content="computedTagContent(scope.row.serviceVOList, 8)"
                      placement="top" :disabled="computedToolTipDisabled(scope.row.serviceVOList, 8)">
                      <span>费用明细</span>
                    </el-tooltip>
                  </el-tag>
                </el-badge>
                <el-badge :value="scope.row.zctzCount" :max="99"
                  :class="computeBadgeItem(scope.row.serviceVOList, 9, scope.row.zctzCount)"
                  :hidden="scope.row.zctzCount == 0">
                  <el-tag :type="getServiceItemTypeByVOList(scope.row.serviceVOList, 9, scope.row.zctzCount)" class="ml-15"
                    :class="computeBadgeItem(scope.row.serviceVOList, 9, scope.row.zctzCount)">
                    <el-tooltip class="item" effect="dark" :content="computedTagContent(scope.row.serviceVOList, 9)"
                      placement="top" :disabled="computedToolTipDisabled(scope.row.serviceVOList, 9)">
                      <span>政策通知</span>
                    </el-tooltip>
                  </el-tag>
                </el-badge>
                <el-badge :value="scope.row.zctzCount" :max="99"
                          :class="computeBadgeItem(scope.row.serviceVOList, 10, scope.row.zctzCount)"
                          :hidden="scope.row.zctzCount == 0">
                  <el-tag :type="getServiceItemTypeByVOList(scope.row.serviceVOList, 10, scope.row.zctzCount)" class="ml-15"
                          :class="computeBadgeItem(scope.row.serviceVOList, 10, scope.row.zctzCount)">
                    <el-tooltip class="item" effect="dark" :content="computedTagContent(scope.row.serviceVOList, 10)"
                                placement="top" :disabled="computedToolTipDisabled(scope.row.serviceVOList, 10)">
                      <span>基数申报</span>
                    </el-tooltip>
                  </el-tag>
                </el-badge>
              </div>

            </template>
          </u-table-column>
          <u-table-column prop="flowCount" width="140" label="流程数(问题/总)" header-align="center" align="left"
            :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <div><span :class="{ 'text-red': scope.row.problemCount > 0 }">{{ scope.row.problemCount }}</span> / {{
                scope.row.flowCount }}</div>
            </template>
          </u-table-column>
          <u-table-column prop="state" width="120" label="当前版本状态" header-align="center" align="center"
            :show-overflow-tooltip="true" min-width='100'>
            <template slot-scope="scope">
              <p style="position:relative;">
                <span
                  :style="{ 'margin-left': '10px', color: scope.row.status == 0 ? 'red' : scope.row.status == 1 ? '#5DBD00' : '#303133' }">{{
                    scope.row.status == 0
                    ? '有新版本未发布' : scope.row.status == 1 ? '已发布' : '未发布' }}</span>
              </p>
            </template>
          </u-table-column>
          <u-table-column prop="version" width="100" label="当前版本号" header-align="center" align="center"
            :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span type="text" size="small" class="text-blue" style="cursor: pointer;"
                @click="goHistory(scope.row, 1)">{{ scope.row.version }}</span>
            </template>
          </u-table-column>
          <u-table-column prop="releaseTime" width="150" label="最新发布时间" header-align="center" align="center"
            :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{ formatDateTime(scope.row.releaseTime, 'y-M-d-h-m', '-') }}</p>
            </template>
          </u-table-column>
          <u-table-column prop="ywUserName" width="100" label="运维人员" header-align="center" align="center"
            :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="xqUserName" width="100" label="调研人员" header-align="center" align="center"
            :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="devUserName" width="100" label="开发人员" header-align="center" align="center"
            :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="testUserName" width="100" label="测试人员" header-align="center" align="center"
            :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="appRemark" width="100" label="备注" header-align="center" align="center"
            :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="robotName" width="100" label="类型" header-align="center" align="center"
            :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="province" width="100" label="所在省" header-align="center" align="center"
            :show-overflow-tooltip="true"></u-table-column>
          <u-table-column label="操作" align="left" width="290" fixed="right">
            <template slot-scope="scope">
              <el-dropdown split-button size="medium" @click="handleClick1($event, scope.row)"
                @command="handleCommand1($event, scope.row)" class="mytest">
                流程管理
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="info"> 信息配置</el-dropdown-item>
                  <el-dropdown-item command="project">执行计划</el-dropdown-item>
                  <el-dropdown-item command="async">同步到生产</el-dropdown-item>
                  <el-dropdown-item command="edit">编辑应用</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <el-dropdown class="ml-10" size="medium" split-button @click="handleClick2($event, scope.row)"
                @command="handleCommand2($event, scope.row)">
                状态管理
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="status">调整状态</el-dropdown-item>
                  <el-dropdown-item command="online">上线</el-dropdown-item>
                  <el-dropdown-item command="offline">下架</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <!-- <el-button type="text" size="small" class="text-blue" @click="handleNewVersion(scope.row)">发布</el-button>
              <div class="opt-btn-split"></div>
              <el-button type="text" size="small" class="text-blue" @click="goSetting(scope.row)">信息配置</el-button>
              <div class="opt-btn-split"></div>
              <el-button type="text" size="small" class="text-blue" @click="goProcess(scope.row)">RPA流程设置</el-button>
              <div class="opt-btn-split"></div>
              <el-button v-if="$global.hasPermission('syncOutToProd')" type="text" size="small" class="text-blue"
                @click="syncOut(scope.row)">同步至生产</el-button>
              <div v-if="$global.hasPermission('syncOutToProd')" class="opt-btn-split"></div>
              <el-button type="text" size="small" class="text-blue" @click="goPlan(scope.row, 1)">执行计划</el-button> -->
            </template>

          </u-table-column>
          <template slot="pagination-btns">
            <div class="display-flex">
              <el-button size="small" class="btn--border-blue" icon="icon ic-export-blue" @click="exportSocialTableData">导出</el-button>
            </div>
          </template>
        </dTable>
      </div>
    </div>
    <!-- 上线 -->
    <el-dialog title="上线" :visible.sync="dialogVisible" width="600px" :before-close="cancelNewVersion"
      :close-on-click-modal="false">
      <div id="upload-dialog">
        <div class="file-content-box">
          <el-form :model="formData" :rules="rules" ref="newVersionRuleForm" label-width="100px" class="demo-ruleForm">
            <el-form-item prop="appName" label-width="150" label="应用名称：" style="display:flex;align-items: center;">
              <span>{{ formData.appName }}（{{ runStatusArray[formData.runStatus] }}）</span>
            </el-form-item>
            <el-form-item prop="appName" label-width="150" label="申报账户：" style="display:flex;align-items: center;">
              <p>启用{{ formData.startAccountCount }}个，停用<span class="text-red">{{ formData.stopAccountCount }}</span>个</p>
            </el-form-item>
            <el-form-item prop="appName" label-width="150" label="问题流程：" style="display:flex;align-items: center;">
              <p><span class="text-red">{{ formData.problemCount }}</span>个</p>
            </el-form-item>
            <el-form-item prop="appName" label-width="150" label="当前状态：" style="display:flex;align-items: center;">
              <span>{{ appStatusArray[parseInt(formData.appStatus)] }}（{{ formData.onlineStatus == 0 ? '配置' :
                formData.onlineStatus == 1 ? '测试' : '运维' }}阶段）</span>
            </el-form-item>
            <el-form-item prop="appName" label-width="150" label="发布后状态：" style="display:flex;align-items: center;">
              <span>正常运行（运维阶段）</span>
            </el-form-item>
            <div class="upload-file-box">
              <div>
                <el-form-item prop="rule" label-width="150" style="display:flex;align-items: center;" label="版本规则：">
                  <el-radio-group v-model="formData.rule">
                    <el-radio label="主版本号">主版本</el-radio>
                    <el-radio label="子版本号">子版本</el-radio>
                    <el-radio label="补丁">补丁</el-radio>
                  </el-radio-group>
                </el-form-item>
              </div>
              <div style="margin-bottom:22px;">

                <el-form-item prop="appName" label-width="150" label="新版本号：" style="display:flex;align-items: center;">
                  <span style="font-size:14px;">{{ setVersionStr }}</span>
                </el-form-item>
              </div>
              <div>
                <el-form-item prop="changeReason" label-width="150" style="margin-bottom:0;" label="变更原因：">
                  <el-select placeholder="请选择变更原因" style="width:100%"
                              v-model="formData.changeReason" clearable>
                    <el-option v-for="(item, index) in changeReasonArr" :label="item" :value="item"></el-option>
                  </el-select>
                </el-form-item>
              </div>
              <div>
                <el-form-item prop="newVersionText" label-width="150" style="margin-bottom:0;" label="更新内容：">
                  <el-input type="textarea" :rows="8" placeholder="请输入本次发布更新的内容" style="width:100%"
                    v-model="formData.newVersionText">
                  </el-input>
                </el-form-item>
                <div style="text-align:right;">{{ formData.newVersionText.length }} / 500</div>
              </div>
            </div>
          </el-form>
          <span class="text-red" style="width:100%;text-align:center;">上线应用，请及时启用被停用的账户，以便数据及时申报</span>
          <div class="footer-btn-box" style="margin-top:15px">
            <el-button @click="cancelNewVersion" class="footer-btn">取 消</el-button>
            <el-button type="primary" @click="preConfirmNewVersion" class="ml-20 footer-btn">确定</el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 下架 -->
    <el-dialog title="下架" :visible.sync="dialogVisible3" width="600px" :before-close="cancelOffline"
      :close-on-click-modal="false">
      <div id="upload-dialog">
        <div class="file-content-box">
          <el-form :model="formData" :rules="rules" ref="offlineForm" label-width="100px" class="demo-ruleForm">
            <el-form-item prop="appName" label-width="150" label="应用名称：" style="display:flex;align-items: center;">
              <span>{{ formData.appName }}（{{ runStatusArray[formData.runStatus] }}）</span>
            </el-form-item>
            <el-form-item prop="appName" label-width="150" label="申报账户：" style="display:flex;align-items: center;">
              <p>{{ formData.startAccountCount + formData.stopAccountCount }}个</p>
            </el-form-item>
            <el-form-item prop="appName" label-width="150" label="问题流程：" style="display:flex;align-items: center;">
              <p><span class="text-red">{{ formData.problemCount }}</span>个</p>
            </el-form-item>
            <el-form-item prop="appName" label-width="150" label="当前状态：" style="display:flex;align-items: center;">
              <span>{{ appStatusArray[parseInt(formData.appStatus)] }}（{{ formData.onlineStatus == 0 ? '配置' :
                formData.onlineStatus == 1 ? '测试' : '运维' }}阶段）</span>
            </el-form-item>
            <el-form-item prop="appName" label-width="150" label="自动停用申报账户：" style="display:flex;align-items: center;">
              <span>是</span>
            </el-form-item>
            <el-form-item prop="offlineStatus" label-width="150" label="下架后状态：" style="display:flex;align-items: center;">
              <el-select v-model="formData.offlineStatus" placeholder="请选择状态" clearable>
                <el-option label="调研" :value="0"></el-option>
                <el-option label="配置" :value="1"></el-option>
                <el-option label="调试通过" :value="2"></el-option>
                <el-option label="等待数据" :value="3"></el-option>
                <el-option label="验证有效" :value="4"></el-option>
                <el-option label="修复问题" :value="6"></el-option>
              </el-select>
              <span v-if="formData.offlineStatus !== ''">（{{ stageArray[formData.offlineStatus] }}阶段）</span>
            </el-form-item>
            <el-form-item prop="reason" label-width="150" label="下架原因：" style="display:flex;align-items: center;">
              <el-select placeholder="请选择下架原因" style="width:100%"
                         v-model="formData.reason" clearable>
                <el-option v-for="(item, index) in changeReasonArr1" :label="item" :value="item"></el-option>
              </el-select>
              <!--<el-input v-model="formData.reason" placeholder="比如：网站升级，重新调研" style="width:400px"></el-input>-->
            </el-form-item>

          </el-form>
          <span class="text-red" style="width:100%;text-align:center;">下架应用，请及时标注需【修复问题】的流程，以便进行跟踪修复</span>
          <div class="footer-btn-box" style="margin-top:15px">
            <el-button @click="cancelOffline" class="footer-btn">取 消</el-button>
            <el-button type="primary" @click="confirmOffline" class="ml-20 footer-btn">确定</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
    <!-- 调整状态 -->
    <el-dialog title="调整状态" :visible.sync="dialogVisible4" width="600px" :before-close="cancelChangeStatus"
      :close-on-click-modal="false">
      <div id="upload-dialog">
        <div class="file-content-box">
          <el-form :model="formData" :rules="rules" ref="statusForm" label-width="100px" class="demo-ruleForm">
            <el-form-item prop="appName" label-width="150" label="应用名称：" style="display:flex;align-items: center;">
              <span>{{ formData.appName }}（{{ runStatusArray[formData.runStatus] }}）</span>
            </el-form-item>
            <el-form-item prop="appName" label-width="150" label="申报账户：" style="display:flex;align-items: center;">
              <p>{{ formData.startAccountCount + formData.stopAccountCount }}个</p>
            </el-form-item>
            <el-form-item prop="appName" label-width="150" label="问题流程：" style="display:flex;align-items: center;">
              <p><span class="text-red">{{ formData.problemCount }}</span>个</p>
            </el-form-item>
            <el-form-item prop="appName" label-width="150" label="当前状态：" style="display:flex;align-items: center;">
              <span>{{ appStatusArray[parseInt(formData.appStatus)] }}（{{ formData.onlineStatus == 0 ? '配置' :
                formData.onlineStatus == 1 ? '测试' : '运维' }}阶段）</span>
            </el-form-item>

            <el-form-item prop="changeStatus" label-width="150" label="调整状态：" style="display:flex;align-items: center;">
              <el-select v-model="formData.changeStatus" placeholder="请选择状态" clearable>
                <el-option label="调研" :value="0" :disabled="!buttonGroupPermission.includes('survey')"></el-option>
                <el-option label="配置" :value="1" :disabled="!buttonGroupPermission.includes('config')"></el-option>
                <el-option label="调试通过" :value="2" :disabled="!buttonGroupPermission.includes('testPass')"></el-option>
                <el-option label="等待数据" :value="3" :disabled="!buttonGroupPermission.includes('waitData')"></el-option>
                <el-option label="验证有效" :value="4" :disabled="!buttonGroupPermission.includes('check')"></el-option>
                <el-option label="修复问题" :value="6" :disabled="!buttonGroupPermission.includes('repair')"></el-option>
              </el-select>
              <span v-if="formData.changeStatus !== ''">（{{ stageArray[formData.changeStatus] }}阶段）</span>
            </el-form-item>
            <el-form-item prop="reason2" label-width="150" label="原因备注：" style="display:flex;align-items: center;">
              <el-select placeholder="请选择原因备注" style="width:100%"
                         v-model="formData.reason2" clearable>
                <el-option v-for="(item, index) in changeReasonArr1" :label="item" :value="item"></el-option>
              </el-select>
              <!--<el-input v-model.trim="formData.reason2" placeholder="比如：网站升级，重新调研" style="width:400px"></el-input>-->
            </el-form-item>

          </el-form>
          <span class="text-red" style="width:100%;text-align:center;"
            v-if="formData.changeStatus == 6">请评估问题重大到需要下架应用，请及时下架应用</span>
          <div class="footer-btn-box" style="margin-top:15px">
            <el-button @click="cancelChangeStatus" class="footer-btn">取 消</el-button>
            <el-button type="primary" @click="confirmChangeStatus" class="ml-20 footer-btn">确定</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
    <!-- 同步至生产 -->
    <el-dialog title="同步至生产" :visible.sync="dialogVisibleSyncOut" width="600px" :before-close="cancelSyncOut"
      :close-on-click-modal="false">
      <div id="upload-dialog">
        <div class="file-content-box">
          <el-form :model="formData" :rules="rules" ref="newVersionRuleSyncOutForm" label-width="100px"
            class="demo-ruleForm">
            <div class="upload-file-box">
              <div>
                <el-form-item prop="newVersionText" label-width="150" style="margin-bottom:0;" label="同步内容：">
                  <el-input type="textarea" :rows="8" placeholder="请输入本次同步更新的内容" style="width:100%"
                    v-model="formData.newVersionText">
                  </el-input>
                </el-form-item>
                <div style="text-align:right;">{{ formData.newVersionText.length }} / 500</div>
              </div>
            </div>
          </el-form>
          <div class="footer-btn-box">
            <el-button @click="cancelSyncOut" class="footer-btn">取 消</el-button>
            <el-button type="primary" @click="confirmSyncOut" class="ml-20 footer-btn">确定同步</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
    <!-- 添加参保城市 -->
    <el-dialog :title="dialogVisibleEdit ? '编辑应用' : '添加应用'" :visible.sync="dialogVisible2" width="800px"
      :before-close="cancelAddCity" :close-on-click-modal="false">
      <div id="upload-dialog">
        <div class="file-content-box">
          <el-form :model="formData" ref="addCity" label-width="90px" class="demo-ruleForm" :rules="rules">
            <div class="upload-file-box">
              <div>
                <el-form-item prop="robot" label="类型：">
                  <el-select size="medium" v-model="formData.robot" class="search-row-item" clearable filterable
                    placeholder="申报机器人" @change="getDynamic" style="width:260px;">
                    <el-option v-for="it in options.robotSort" :key="it.id" :label="it.robotName"
                      :value="it.robotCode"></el-option>
                  </el-select>
                </el-form-item>
              </div>
              <div>
                <el-form-item prop="appName" label="应用名称：">
                  <el-input class="" size="medium" placeholder="地区、业务类型" style="width:260px;"
                    v-model="formData.appName"></el-input>
                  <span>（调研）</span>
                </el-form-item>
              </div>
              <div>
                <el-form-item prop="addrNameTemp" label="地区：" v-if="formData.dynamics.indexOf('addrName') > -1">
                  <addrSelector v-model="formData.addrNameTemp" :addrArr="bussinssType.allAddr"
                    :disabled="dialogVisibleEdit" style="line-height: 0px" @changeAddrSelect="changeAddrSelect">
                  </addrSelector>
                </el-form-item>
              </div>
              <div>
                <el-form-item prop="businessType" label="业务类型：" v-if="formData.dynamics.indexOf('businessType') > -1">
                  <el-select size="medium" v-model="formData.businessType" placeholder="请选择业务类型"
                    :disabled="dialogVisibleEdit" style="width:260px;">
                    <el-option v-for="it in options['1001']" :key="it.id" :label="it.dictName"
                      :value="it.dictCode"></el-option>
                  </el-select>
                </el-form-item>
              </div>
              <div>
                <el-form-item prop="comment" label="备注：">
                  <el-input v-model="formData.comment" type="textarea" style="width:260px;" placeholder="请输入"></el-input>
                </el-form-item>
              </div>
            </div>
          </el-form>

          <div class="mt-10">
            <table class="cust-table-border w-p100">
              <thead>
                <tr>
                  <th style="width: 50px;">服务项目</th>
                  <th style="width: 50px;">标记</th>
                  <th style="width: 200px;">情况说明</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, index) in serviceItemVOTable" :key="item.serviceItem">
                  <td>
                    <el-badge :value="returnType(item.serviceItem, formData)" :max="99"
                      :class="computeBadgeItem(serviceItemVOTable, item.serviceItem, returnType(item.serviceItem, formData))"
                      :hidden="returnType(item.serviceItem, formData) == 0">
                      <el-tag
                        :type="getServiceItemTypeByVOList(serviceItemVOTable, item.serviceItem, returnType(item.serviceItem, formData))"
                        :class="computeBadgeItem(serviceItemVOTable, item.serviceItem, returnType(item.serviceItem, formData))">{{
                          searchOptions.service.find(el => el.id == item.serviceItem).name }}</el-tag>
                    </el-badge>
                  </td>
                  <td><el-checkbox v-model="item.serviceStatus" :true-label="0" :false-label="2"
                      @change="val => handleTableCheckChange(val, index)" :disabled="dialogVisibleEdit && item.serviceStatus == 1">不实现</el-checkbox></td>
                  <td><el-input v-model="item.serviceRemark" :disabled="dialogVisibleEdit && item.serviceStatus == 1"
                      placeholder="请输入"></el-input></td>
                  <!-- <td><el-checkbox v-model="item.checked" :true-labe="true" :false-labe="false"
                    @change="val => handleCityCheckChange(val, index)"></el-checkbox></td> -->
                </tr>
              </tbody>
            </table>
          </div>
          <!-- <div class="footer-btn-box">
              <el-button @click="cancelAddCity" class="footer-btn">取  消</el-button>
              <el-button type="primary"  @click="confirmAddCity" class="ml-20 footer-btn">确定</el-button>
          </div> -->
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelAddCity" size="small" class="w-60">取 消</el-button>
        <el-button type="primary" @click="confirmAddCity" size="small" class="w-60">确定</el-button>
      </span>
    </el-dialog>

    <!-- 状态历史 -->
    <el-dialog title="状态历史" :visible.sync="flowTableVisiable" width="1000px" :before-close="cancelFlowTable"
      :close-on-click-modal="false">
      <div class="customer-table">
        <el-table :data="tableData" border header-cell-class-name="table-header" style="width: 100%">
          <el-table-column prop="actionName" label="操作" width="150">
            <template slot-scope="scope">
              <p :class="appStatusTextColor(scope.row.onLive)">{{ scope.row.actionName }}</p>
            </template>
          </el-table-column>
          <el-table-column prop="appStatusName" label="应用状态变为" width="150">
            <template slot-scope="scope">
              <p :class="appStatusTextColor(scope.row.onLive)">{{ scope.row.appStatusName }}</p>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="原因备注" width="200" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p :class="appStatusTextColor(scope.row.onLive)">{{ scope.row.remark }}</p>
            </template>
          </el-table-column>
          <el-table-column prop="onLiveName" label="上线标记" width="100">
            <template slot-scope="scope">
              <p :class="appStatusTextColor(scope.row.onLive)">{{ scope.row.onLiveName }}</p>
            </template>
          </el-table-column>
          <el-table-column prop="createName" label="操作人" width="150">
            <template slot-scope="scope">
              <p :class="appStatusTextColor(scope.row.onLive)">{{ scope.row.createName }}</p>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="操作时间">
            <template slot-scope="scope">
              <p :class="appStatusTextColor(scope.row.onLive)">{{ formatDateTime(scope.row.createTime, 'y-M-d-h-m-s', '-')
              }}</p>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="bottom-group">
        <el-button type="primary" size="medium" @click="cancelFlowTable">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>

import dTable from '../../components/common/table'
import addrSelector from '../../components/common/addrSelector'
import _ from 'lodash'
export default {
  components: { addrSelector, dTable },
  data () {
    return {
      buttonGroupPermission: [], // 按钮组权限
      headStatisticsInfo: {}, // 头部统计
      appStatusArray: ['调研', '配置', '调试通过', '等待数据', '验证有效', '正常运行', '修复问题'], // 应用状态
      runStatusArray: ['下线', '上线', '待上线'], // 应用上线标记
      stageArray: ['配置', '配置', '配置', '测试', '测试', '运维', '运维'], // 应用状态对应的阶段
      bussinssType: {
        allAddr: []
      },
      tableListData: [],
      flowTableVisiable: false,
      tableData: [], // 状态历史table
      formData: {
        status: '1',
        addrId: '',
        addrName: '',
        addrNameTemp: '',
        changeReason: '',
        newVersionText: '',
        newCity: '',
        robot: '',
        comment: '',
        appName: '',
        businessType: '',
        dynamics: [],
        appArgs: {},
        rule: '补丁',
        offlineStatus: '', // 下架后状态
        reason: '', // 下架原因
        changeStatus: ''
      },
      changeReasonArr: [
        '新应用上线',
        '流程配置缺陷',
        '流程优化',
        '网站新场景',
        '网页元素变化',
        '网站业务变更'
      ],
      changeReasonArr1: [
        '流程配置缺陷',
        '流程优化',
        '网站新场景',
        '网页元素变化',
        '网站业务变更'
      ],
      dialogVisibleEdit: false, // 是否为编辑应用
      dialogVisible: false,
      dialogVisible2: false,
      dialogVisible3: false, // 下线
      dialogVisible4: false, // 调整状态
      dialogVisibleSyncOut: false,
      pathData: [],
      rules: {
        uploadFileName: [
          { required: true, message: '请先上传文件', trigger: 'change' }
        ],
        tplType: [
          { required: true, message: '请选择模板类型', trigger: 'change' }
        ],
        changeReason: [
          { required: true, message: '请选择变更原因', trigger: 'change' }
        ],
        newVersionText: [
          { required: true, message: '请输入本次发布更新的内容', trigger: 'change' },
          { min: 0, max: 500, message: '更新内容不能大于500字', trigger: 'change' }
        ],
        addrNameTemp: [
          { required: true, message: '请选择城市', trigger: 'change' }
        ],
        businessType: [
          { required: true, message: '请选择业务类型', trigger: 'change' }
        ],
        rule: [
          { required: true, message: '请选择版本规则', trigger: 'change' }
        ],
        robot: [
          { required: true, message: '请选择类型', trigger: 'change' }
        ],
        appName: [
          { required: true, message: '请输入应用名称', trigger: 'change' }
        ],
        offlineStatus: [
          { required: true, message: '请选择下架后状态', trigger: 'change' }
        ],
        changeStatus: [
          { required: true, message: '请选择调整状态', trigger: 'change' }
        ],
        reason: [
          { required: true, message: '请选择下架原因', trigger: 'change' }
        ],
        reason2: [
          { required: true, message: '请选择原因备注', trigger: 'change' }
        ]
      },
      associatedFileErrMsg: [],
      rowData: {},
      loading: null,
      options: {
        robotSort: []
      },
      allOptions: [],
      timer: false,
      version: '',
      searchForms: {
        type: [], // 业务类型
        mark: [], // 上线标记
        status1: '', // 应用状态1
        status2: [], // 应用状态2
        service: [], // 服务项目
        keyword: '' // 关键字查询
      },
      searchOptions: {
        types: [
          {
            name: '社保',
            id: 1
          },
          {
            name: '公积金',
            id: 2
          }
        ],
        mark: [
          {
            name: '上线',
            id: 1
          },
          {
            name: '下线',
            id: 0
          },
          {
            name: '待上线',
            id: 2
          }
        ],
        service: [
          {
            name: '增员',
            id: 1
          },
          {
            name: '减员',
            id: 2
          },
          {
            name: '调基',
            id: 3
          },
          {
            name: '补缴',
            id: 5
          },
          {
            name: '缴费',
            id: 6
          },
          {
            name: '在册名单',
            id: 7
          },
          {
            name: '费用明细',
            id: 8
          },
          {
            name: '政策通知',
            id: 9
          },
          {
            name: '基数申报',
            id: 10
          }
        ]
      },
      test1: false,
      test2: '',
      serviceItemVOTable: [
        {
          'id': null,
          'appCode': null,
          'serviceItem': 1,
          'serviceStatus': 2,
          'serviceRemark': null
        },
        {
          'id': null,
          'appCode': null,
          'serviceItem': 2,
          'serviceStatus': 2,
          'serviceRemark': null
        },
        {
          'id': null,
          'appCode': null,
          'serviceItem': 3,
          'serviceStatus': 2,
          'serviceRemark': null
        },
        {
          'id': null,
          'appCode': null,
          'serviceItem': 5,
          'serviceStatus': 2,
          'serviceRemark': null
        },
        {
          'id': null,
          'appCode': null,
          'serviceItem': 6,
          'serviceStatus': 2,
          'serviceRemark': null
        },
        {
          'id': null,
          'appCode': null,
          'serviceItem': 7,
          'serviceStatus': 2,
          'serviceRemark': null
        },
        {
          'id': null,
          'appCode': null,
          'serviceItem': 8,
          'serviceStatus': 2,
          'serviceRemark': null
        }
      ]
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 410 + 'px'
    },
    computeBadgeItem () {
      return function (list, type, count) {
        let badgeClass = ''
        let tagClass = ''
        if (count > 99) {
          badgeClass = 'badge-item-25'
        } else if (count >= 10) {
          badgeClass = 'badge-item-15'
        } else {
          badgeClass = 'badge-item-12'
        }
        if (Array.isArray(list)) {
          let choiceItem = list.find(item => item.serviceItem == type)
          if (!choiceItem || choiceItem.serviceStatus == 2) {
            tagClass = 'unrealized-tag'
          } else {
            tagClass = ''
          }

          return tagClass + ' ' + badgeClass
        } else {
          return ''
        }
      }
    },
    formatType () {
      return function (index) {
        var list = ['投保', '停保', '调整', '变更', '补缴']
        return list[index - 1]
      }
    },
    getTplTypeStr () {
      return function (key) {
        var str = ''
        this.allOptions.forEach(item => {
          if (item.dictCode == key) {
            str = item.dictName
          }
        })
        return str
      }
    },
    // 使用字段为 serviceItemList
    getServiceItemType () {
      return function (list, type, count) {
        if (list == null || list.length == 0) return 'info'
        if (count > 0) return 'danger'
        if (list.includes(type) || list.find(item => item == type)) return ''
        else return 'info'
      }
    },
    // 使用字段为 serviceVOList
    getServiceItemTypeByVOList () {
      return function (list, type, count) {
        let choiceItem = list.find(item => item.serviceItem == type)
        if (choiceItem == null) return ''
        else if (count > 0 && choiceItem.serviceStatus != 0) return 'danger'
        else if (choiceItem.serviceStatus == 1) return ''
        else if (choiceItem.serviceStatus == 0) return 'info'
        else return ''
      }
    },
    computedTagContent () {
      return function (list, type) {
        let choiceItem = list.find(item => item.serviceItem == type)
        return choiceItem ? choiceItem.serviceRemark : ''
      }
    },
    computedToolTipDisabled () {
      return function (list, type) {
        let choiceItem = list.find(item => item.serviceItem == type)
        return !(choiceItem != null && choiceItem.serviceRemark)
      }
    },
    // 状态历史 文本颜色 上线 绿色 下线红色 其他默认
    appStatusTextColor () {
      return (onLive) => {
        if (onLive == 1) return 'text-green'
        else if (onLive == 2) return 'text-red'
        else return ''
      }
    },
    setVersionStr () {
      if (!this.version) {
        return this.version
      }
      var str = this.version.replace(/V|v/, '').split('.')
      // console.log(str)
      if (this.formData.rule == '' || this.formData.rule == null) {
        return this.version
      }
      if (this.formData.rule == '主版本号') {
        str[0] = Number(str[0]) + 1
      } else if (this.formData.rule == '子版本号') {
        str[1] = Number(str[1]) + 1
      } else {
        str[2] = Number(str[2]) + 1
      }
      return 'V' + str.join('.')
    }
  },
  watch: {
    'k.value': function () {
      if (!this.formData.dynamics) return
      let addr = this.formData.dynamics.filter((it) => { return it.fieldName == '地区' })
      let type = this.formData.dynamics.filter((it) => { return it.fieldName == '业务类型' })
      this.formData.appName = addr[0].value + type[0].value
    }
  },
  created () {
    let that = this
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.$nextTick(() => {
      // that.getTableData()
      // that.getAddr()
      that.getRobotType()
      this.getAddr()
      this.getDictByKey('1002') // 获取地区
      this.getDictByKey('1001') // 获取业务类型
      this.getStatisticsInfo() // 获取头部统计
      // if(sessionStorage.getItem('offerFileSetting-params')){
      //   var obj = JSON.parse(sessionStorage.getItem('offerFileSetting-params'))
      //   this.formData = obj
      //   this.getTableData()
      // }
    })
    // 清空流程管理中第一次进入的下拉 让其可以重新获取
    this.$store.commit('updateDeclareSystemSelects', [])
  },
  mounted () {
    if (sessionStorage.getItem('processSetting-params')) {
      this.formData.addrNameTemp = JSON.parse(sessionStorage.getItem('processSetting-params')).addrNameTemp
      // if (this.formData.addrNameTemp) {
      //   this.getTableData()
      // }
    }
    // 获取store中的查询条件 有则回显否则按默认
    var processIndexQuery = sessionStorage.getItem('processIndexQuery')
    if (this.$route.query.appName) {
      this.searchForms.keyword = this.$route.query.appName
      this.getTableData()
    } else if (processIndexQuery) {
      processIndexQuery = JSON.parse(sessionStorage.getItem('processIndexQuery'))
      this.searchForms = processIndexQuery
      if (processIndexQuery.type.length > 0 || processIndexQuery.mark.length > 0 || processIndexQuery.status2.length ||
        processIndexQuery.service.length || processIndexQuery.keyword) {
        this.getTableData()
      }
    }
  },
  methods: {
    // 勾选 - 不实现
    handleTableCheckChange (val, index) {
      console.log(val, index, this.serviceItemVOTable)
    },
    // 查询 - 应用状态变更
    onChangeStatus1 (e) {
      console.log(e)
      if (e == 1) {
        this.searchForms.status2 = [0, 1, 2]
      } else if (e == 2) {
        this.searchForms.status2 = [3, 4]
      } else if (e == 3) {
        this.searchForms.status2 = [5, 6]
      } else {
        this.searchForms.status2 = []
      }
    },
    // 获取按钮组权限(现在用于获取调整状态里面的code 需要传入addrName 和 businessType)
    async getButtonGroupPermission (addrName, businessType) {
      const { data: { code, data: result } } = await this.$http({
        url: `policy/dev/userAddr/getUserAppButton?addrName=${addrName}&businessType=${businessType == 1001001 ? '社保' : '公积金'}`,
        method: 'get'
      })
      if (code == 200) {
        this.buttonGroupPermission = result.map(item => item.buttonCode)
      }
    },
    // 获取头部统计
    async getStatisticsInfo () {
      const { data: { code, data: result } } = await this.$http({
        url: '/robot/app/getAppCount',
        method: 'post'
      })
      if (code == 200) {
        this.headStatisticsInfo = result
      }
      // console.log('头部统计-------', code, result)
    },
    changeAddrSelect (val) {
      console.log('--------', this.formData)
      this.formData.addrId = val.id
      this.formData.addrName = val.name
    },
    // 去执行计划页面
    goPlan (row) {
      this.$router.push('/processSetting/executionPlan?appCode=' + row.appCode + '&name=' + row.appName)
    },
    // 去流程设置
    goProcess (row) {
      this.$router.push('/processSetting/setting?appCode=' + row.appCode)
    },
    // 去历史版本页面
    goHistory (row) {
      this.$router.push('/processSetting/historyVersion?appCode=' + row.appCode)
    },
    // 去信息配置页面
    goSetting (row) {
      this.$router.push('/processSetting/infoMaintain?appCode=' + row.appCode + '&businessType=' + row.businessType)
    },
    // 取消添加参保地
    cancelAddCity () {
      this.dialogVisible2 = false
      this.formData.dynamics = []
      this.formData.addrName = ''
      this.formData.addrNameTemp = ''
      this.formData.businessType = ''
      this.formData.appArgs = {}
      this.$nextTick(() => {
        this.$refs.addCity.resetFields()
      })
    },
    // xxxxx
    getAppName () {
      let addr = this.formData.dynamics.filter((it) => { return it.fieldName == '地区' })
      let type = this.formData.dynamics.filter((it) => { return it.fieldName == '业务类型' })
      console.log('addr=' + addr[0].value)
      this.formData.appName = addr[0].value + type[0].value
    },
    // 获取新增地区的动态字段
    async getDynamic () {
      var robotCode = this.formData.robot
      await this.$http({
        url: '/robot/app/dynamic/field?robotCode=' + robotCode,
        method: 'post'
      }).then(({ data }) => {
        data.data.forEach(item => {
          this.formData.dynamics.push(item.fieldKey)
        })
      })
    },
    // 新增地区确定
    confirmAddCity () {
      let that = this
      this.$refs.addCity.validate((valid) => {
        if (valid) {
          console.log('新增地区')
          console.log(that.formData.dynamics)
          for (var i = 0; i < that.formData.dynamics.length; i++) {
            var fieldKey = that.formData.dynamics[i]
            // this.formData.appArgs += fieldKey + ":" + this.formData[fieldKey] + ";"
            if (fieldKey == 'addrName') {
              that.formData.appArgs['addrName'] = that.formData.addrName
              that.formData.appArgs['addrId'] = that.formData.addrId
            } else {
              that.formData.appArgs[fieldKey] = that.formData[fieldKey]
            }
          }
          // 校验服务项目中 如果勾选了 不实现的 是否填入对应的备注
          let serviceItem0List = this.serviceItemVOTable.filter(item => item.serviceStatus == 0)
          let notNullList = serviceItem0List.filter(item => item.serviceserviceRemark != '' && item.serviceRemark != null)
          if (serviceItem0List.length != notNullList.length) {
            this.$message.warning('请先完善勾选了不实现的服务项目的情况说明 ')
            return
          }
          // console.log('准备提交',this.formData,this.serviceItemVOTable)
          // return
          this.showLoading()
          let url = this.dialogVisibleEdit ? '/robot/app/updateApp' : '/robot/app/add'
          this.$http({
            url: url,
            method: 'post',
            data: {
              appName: this.formData.appName,
              robotName: this.formData.robot,
              appArgs: JSON.stringify(this.formData.appArgs),
              id: this.formData.id || null,
              appRemark: this.formData.comment,
              robotServiceItemList: this.serviceItemVOTable,
              appCode: this.formData.appCode || null
            }
          }).then(({ data }) => {
            this.closeLoading()
            this.getTableData()
            this.cancelAddCity()
          }).catch(err => {
            this.closeLoading()
          })
        }
      })
    },
    // 添加参保地
    addCity () {
      this.dialogVisible2 = true
      this.dialogVisibleEdit = false
      this.formData.robot = ''
      this.formData.appName = ''
      this.formData.comment = ''
      this.formData.addrNameTemp = ''
      this.formData.addrId = ''
      this.formData.addrName = ''
      this.formData.businessType = ''
      this.formData.id = null
      this.formData.appCode = null
      this.formData.addCount = ''
      this.formData.reduceCount = 0
      this.formData.tjCount = 0
      this.formData.bjCount = 0
      this.formData.jfCount = 0
      this.formData.zcCount = 0
      this.formData.payCount = 0
      this.formData.zctzCount = 0

      this.serviceItemVOTable = [
        {
          'id': null,
          'appCode': null,
          'serviceItem': 1,
          'serviceStatus': 2,
          'serviceRemark': null
        },
        {
          'id': null,
          'appCode': null,
          'serviceItem': 2,
          'serviceStatus': 2,
          'serviceRemark': null
        },
        {
          'id': null,
          'appCode': null,
          'serviceItem': 3,
          'serviceStatus': 2,
          'serviceRemark': null
        },
        {
          'id': null,
          'appCode': null,
          'serviceItem': 5,
          'serviceStatus': 2,
          'serviceRemark': null
        },
        {
          'id': null,
          'appCode': null,
          'serviceItem': 6,
          'serviceStatus': 2,
          'serviceRemark': null
        },
        {
          'id': null,
          'appCode': null,
          'serviceItem': 7,
          'serviceStatus': 2,
          'serviceRemark': null
        },
        {
          'id': null,
          'appCode': null,
          'serviceItem': 8,
          'serviceStatus': 2,
          'serviceRemark': null
        },
        {
          'id': null,
          'appCode': null,
          'serviceItem': 9,
          'serviceStatus': 2,
          'serviceRemark': null
        }
      ]
      this.$nextTick(() => {
        this.$refs.addCity.clearValidate()
      })
    },
    // 根据类型返回count
    returnType (type, row) {
      if (type == 1) {
        return row.addCount
      } else if (type == 2) {
        return row.reduceCount
      } else if (type == 3) {
        return row.tjCount
      } else if (type == 5) {
        return row.bjCount
      } else if (type == 6) {
        return row.jfCount
      } else if (type == 7) {
        return row.zcCount
      } else if (type == 8) {
        return row.payCount
      } else if (type == 9) {
        return row.zctzCount
      }
    },
    // 编辑应用
    editApp (row) {
      console.log('editaPP', row)
      this.dialogVisibleEdit = true
      // 回显编辑应用相关信息
      // todo：缺少了robotCode
      this.formData.robot = this.options.robotSort.find(item => item.robotName == row.robotName).robotCode
      this.formData.appName = row.appName
      this.formData.comment = row.appRemark
      this.formData.addrNameTemp = JSON.parse(row.appArgs).addrName
      this.formData.addrId = JSON.parse(row.appArgs).addrId
      this.formData.addrName = JSON.parse(row.appArgs).addrName
      this.formData.businessType = JSON.parse(row.appArgs).businessType
      this.formData.id = row.id
      this.formData.appCode = row.appCode
      this.formData.addCount = row.addCount
      this.formData.reduceCount = row.reduceCount
      this.formData.reduceCount = row.reduceCount
      this.formData.tjCount = row.tjCount
      this.formData.bjCount = row.bjCount
      this.formData.jfCount = row.jfCount
      this.formData.zcCount = row.zcCount
      this.formData.payCount = row.payCount

      this.serviceItemVOTable = row.serviceVOList
      this.dialogVisible2 = true
      this.getDynamic()
    },
    // 发布新版本吗，弹窗
    handleNewVersion (row) {
      if (row.status == 1) {
        // this.$message.warning('未发现有新版本可以发布')
        // return
      }
      console.log('上架', row)
      this.appCode = row.appCode
      this.formData.rule = '补丁'
      this.formData.appName = row.appName
      this.formData.startAccountCount = row.startAccountCount
      this.formData.stopAccountCount = row.stopAccountCount
      this.formData.problemCount = row.problemCount
      this.formData.appStatus = row.appStatus
      this.formData.onlineStatus = row.onlineStatus
      this.formData.runStatus = row.runStatus
      this.version = row.version
      this.dialogVisible = true
    },
    // 同步至生产吗，弹窗
    syncOut (row) {
      this.appCode = row.appCode
      this.formData.rule = '补丁'
      this.version = row.version
      this.dialogVisibleSyncOut = true
    },
    // 取消同步至生产
    cancelSyncOut () {
      this.dialogVisibleSyncOut = false
      this.formData.newVersionText = ''
      this.formData.rule = ''
      this.$nextTick(() => {
        this.$refs.newVersionRuleSyncOutForm.clearValidate()
      })
    },
    // 取消发布版本
    cancelNewVersion () {
      this.dialogVisible = false
      this.formData.newVersionText = ''
      this.formData.rule = ''
      this.$nextTick(() => {
        this.$refs.newVersionRuleForm.clearValidate()
      })
    },
    // 弹窗 下架
    hanleOfflineModal (row) {
      this.dialogVisible3 = true
      console.log('下架', row)
      this.formData.appCode = row.appCode
      this.formData.appName = row.appName
      this.formData.startAccountCount = row.startAccountCount
      this.formData.stopAccountCount = row.stopAccountCount
      this.formData.problemCount = row.problemCount
      this.formData.appStatus = row.appStatus
      this.formData.onlineStatus = row.onlineStatus
      this.formData.runStatus = row.runStatus
    },
    // 取消下架
    cancelOffline () {
      this.dialogVisible3 = false
      this.formData.rule = ''
      this.formData.reason = ''
      this.formData.offlineStatus = ''
      this.$nextTick(() => {
        this.$refs.offlineForm.clearValidate()
      })
    },
    // 确定下架
    confirmOffline () {
      this.$refs.offlineForm.validate((valid) => {
        if (valid) {
          console.log('确定下架')
          this.showLoading()
          this.$http({
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            url: 'robot/app/offlineStatus',
            method: 'post',
            params: {
              appCode: this.formData.appCode,
              comment: this.formData.reason,
              appStatus: this.formData.offlineStatus
            }
          }).then(({ data }) => {
            this.closeLoading()
            this.$message.success('下架成功')
            this.cancelOffline()
            this.getTableData()
          }).catch(err => {
            this.closeLoading()
          })
        }
      })
    },
    // 弹窗 调整状态
    hanleChangeStatusModal (row) {
      // 打开前需要先获取权限
      console.log(row, '调整状态')
      this.getButtonGroupPermission(JSON.parse(row.appArgs).addrName, JSON.parse(row.appArgs).businessType)
      this.dialogVisible4 = true
      this.formData.appCode = row.appCode
      this.formData.appName = row.appName
      this.formData.startAccountCount = row.startAccountCount
      this.formData.stopAccountCount = row.stopAccountCount
      this.formData.problemCount = row.problemCount
      this.formData.appStatus = row.appStatus
      this.formData.onlineStatus = row.onlineStatus
      this.formData.runStatus = row.runStatus
    },
    // 取消 调整状态
    cancelChangeStatus () {
      this.dialogVisible4 = false
      this.formData.rule = ''
      this.formData.reason2 = ''
      this.formData.changeStatus = ''
      this.$nextTick(() => {
        this.$refs.statusForm.clearValidate()
      })
    },
    // 确定 调整状态
    confirmChangeStatus () {
      this.$refs.statusForm.validate((valid) => {
        if (valid) {
          console.log('确定调整状态')
          this.showLoading()
          this.$http({
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            url: 'robot/app/updateAppStatus',
            method: 'post',
            params: {
              appCode: this.formData.appCode,
              remark: this.formData.reason2,
              appStatus: this.formData.changeStatus
            }
          }).then(({ data }) => {
            this.closeLoading()
            this.$message.success('操作成功')
            this.cancelChangeStatus()
            this.getTableData()
          }).catch(err => {
            this.closeLoading()
          })
        }
      })
    },
    // 确定同步至生产
    confirmSyncOut () {
      this.$refs.newVersionRuleSyncOutForm.validate((valid) => {
        if (valid) {
          this.showLoading()
          this.$http({
            url: 'robot/app/syncOut?appCode=' + this.appCode + '&comment=' + this.formData.newVersionText,
            method: 'post'
          }).then(({ data }) => {
            this.closeLoading()
            this.$message.success('同步成功')
            this.cancelSyncOut()
            // this.getTableData()
          }).catch(err => {
            this.closeLoading()
          })
        }
      })
    },
    // 上线前校验
    preConfirmNewVersion () {
      let that = this
      this.$refs.newVersionRuleForm.validate((valid) => {
        if (valid) {
          if (this.formData.problemCount > 0) {
            this.$confirm(`存在${this.formData.problemCount}个问题流程，该应用是否继续上线？`, '提示', {
              confirmButtonText: '继续上线',
              cancelButtonText: '暂不上线',
              showClose: false,
              customClass: 'pal-confirm',
              type: 'warning'
            })
              .then(() => {
                that.confirmNewVersion()
              })
              .catch(() => {
                that.cancelNewVersion()
              })
          } else {
            that.confirmNewVersion()
          }
        }
      })
    },
    // 确定发布版本
    confirmNewVersion () {
      console.log(this.appCode)
      let that = this
      this.$refs.newVersionRuleForm.validate((valid) => {
        if (valid) {
          console.log('发布新版本')
          this.showLoading()
          this.$http({
            url: 'robot/app/release',
            method: 'post',
            data: {
              appCode: this.appCode,
              changeReason: this.formData.changeReason,
              comment: this.formData.newVersionText,
              rule: this.formData.rule
            }
          }).then(({ data }) => {
            this.closeLoading()
            this.$message.success('发布成功')
            this.cancelNewVersion()
            this.getTableData()
          }).catch(err => {
            this.closeLoading()
          })
        }
      })
    },
    changeSearch (obj) {
      clearTimeout(this.timer)
      this.timer = setTimeout(() => {
        this.formData.addrId = obj.id
      }, 100)
    },
    search () {
      this.getTableData()
    },
    reset () {
      this.searchForms = {
        type: [], // 业务类型
        mark: [], // 上线标记
        status1: '', // 应用状态1
        status2: [], // 应用状态2
        service: [], // 服务项目
        keyword: '' // 关键字查询
      }
      sessionStorage.setItem('processIndexQuery', '')
    },
    // 去新增
    goAdd () {
      this.$router.push('/offerFile/addOfferFile')
    },
    // 获取列表信息
    getTableData () {
      var params = [
        { property: 'keyWord', value: this.searchForms.keyword },
        { property: 'serviceItemList', value: this.searchForms.service },
        { property: 'businessList', value: this.searchForms.type },
        { property: 'onlineList', value: this.searchForms.mark },
        { property: 'appStatusList', value: this.searchForms.status2 }
      ]
      sessionStorage.setItem('processIndexQuery', JSON.stringify(this.searchForms))
      this.$refs.table.fetch({
        query: params,
        method: 'post',
        url: '/robot/app/getAppData'
      })
    },
    // 顶部点击查询
    pointerQuery (status2) {
      this.$refs.table.fetch({
        query: [{ property: 'appStatusList', value: status2 }],
        method: 'post',
        url: '/robot/app/getAppData'
      })
    },
    // 列出机器人种类
    getRobotType () {
      this.$http({
        url: 'robot/app/type',
        method: 'post'
      }).then(res => {
        // console.log(res)
        this.options.robotSort = res.data.data
      })
    },
    // 获取参保地
    getAddr (type) {
      let that = this
      this.$http({
        url: 'policy/declareAddr/addrList',
        method: 'post'
      }).then(({ data }) => {
        this.bussinssType.allAddr = data.data
      })
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
    // 获取字典值
    getDictByKey (key) {
      this.$http({
        url: `/robot/data/dict/${key}`,
        method: 'post'
      }).then(res => {
        this.options[key] = res.data.data
        this.allOptions.push(...res.data.data)
        // console.log(this.valueTypeOption2Item)
      })
    },
    // 设置时间,时间转换
    formatDateTime (date, params, separator) {
      if (date == undefined || date == null || date == '') {
        return ''
      }
      var date = new Date(date.substring(0, 19))
      var Year = date.getFullYear()
      var Month = date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1
      var d = date.getDate() < 10 ? '0' + date.getDate() : date.getDate()
      var Hours = date.getHours() < 10 ? '0' + date.getHours() : date.getHours()
      var Minutes = date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()
      var Seconds = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds()
      var over_time = Year + '/' + Month + '/' + d + ' ' + Hours + ':' + Minutes + ':' + Seconds
      //* **至此以上是将时间2020-03-18T01:57:23.000+0000转为正常时间格式，以下为将时间进行增加8小时解决时区差异的操作***
      var time = new Date(over_time)
      time.setTime(time.setHours(time.getHours()))
      var y = time.getFullYear()
      var m = this.addZero(time.getMonth() + 1)
      var d = this.addZero(time.getDate())
      var h = this.addZero(time.getHours() + 8)
      var minute = this.addZero(time.getMinutes())
      var second = this.addZero(time.getSeconds())
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
    addZero (num) {
      return num < 10 ? '0' + num : num
    },
    // 跳转到流程模板页面 进行添加
    addTemplate () {
      this.$router.push('/processSetting/templateList')
    },
    addrChange () {
      this.$router.push('/addr/businessChange')
    },
    // 流程管理点击
    handleClick1 (e, row) {
      console.log(e, row)
      this.goProcess(row)
    },
    // 状态管理点击
    handleClick2 (e, row) {
      console.log(e, row)
      // this.goPlan(row, 1)
    },
    // 流程管理 命令
    handleCommand1 (command, row) {
      switch (command) {
        case 'info':
          this.goSetting(row)
          break
        case 'project':
          this.goPlan(row, 1)
          break
        case 'async':
          this.syncOut(row)
          break
        case 'edit':
          this.editApp(row)
          break
        default:
          break
      }
      console.log(command, row)
    },
    // 状态管理 命令
    handleCommand2 (command, row) {
      console.log(command, row)
      switch (command) {
        case 'status':
          this.hanleChangeStatusModal(row, 1)
          break
        case 'online':
          if (row.flowCount == 0) {
            this.$message.warning('该应用无流程，无法上线')
            return
          }
          this.handleNewVersion(row)
          break
        case 'offline':
          this.hanleOfflineModal(row)
          break
        default:
          break
      }
    },
    // 显示流程状态
    async showFlowTable (row) {
      console.log('asdasd', row)
      this.flowTableVisiable = true
      // 获取应用状态
      const { data: { code, data: result } } = await this.$http({
        url: `robot/app/selectAppHistoryStatus`,
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        method: 'post',
        params: {
          appCode: row.appCode
        }
      })
      if (code == 200) {
        this.tableData = result
      }
    },
    // 取消显示 流程状态
    cancelFlowTable () {
      this.flowTableVisiable = false
    },
    // 导出
    exportSocialTableData () {
      let paramsObj = this.$refs.table.getParamsObj()
      /* if(paramsObj.accountNumber==''){
        this.$message.warning('请先选择申报账户查询出数据后再导出')
        return
      } */
      let params = this.$refs.table.getParamsQuery()
      // params.push({ property: 'itemId', value: ids })
      this.$downloadFile(
        '/robot/app/downExcel',
        'post',
        {
          query: params
        },
        this.$global.EXCEL
      )
    }
  }
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

.file-content-box {
  /deep/.el-form-item__label {
    line-height: 32px !important;
    padding-right: 0;
  }

  padding:10px 20px;
  font-size: 12px;

  .file-list {
    display: flex;
    padding: 10px 0;

    .file {
      background: #f1f8ff;
      border-radius: 10px;
      padding: 10px;
      position: relative;
      cursor: pointer;
      margin-right: 15px;

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
  input[type=text] {
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

// .tag:hover,
// .tag.active {
//   background-color: #dddddd;
// }

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

.footer-btn-box {
  text-align: center;
  margin-top: 40px;

  .footer-btn {
    width: 100px;
    height: 35px;
    line-height: 35px;
    padding: 0 10px;
  }
}

.customer-table {
  padding: 20px;

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
  color: #F56C6C;
  font-size: 12px;
}

/deep/.el-drawer__body {
  padding-bottom: 0;
}

/deep/.el-dialog__header {
  border-bottom: none !important;
}

.bg-green {
  background-color: #008000;
}

.bg-blue {
  background-color: #66b1ff;
}

.bg-blue2 {
  background-color: #409eff;
}

.bg-blue-dark {
  background-color: #0475ff;
}

.card-layout {
  display: flex;
  align-items: center;

  color: #ffffff;
  border-radius: 5px;
  padding: 20px 10px;
  margin: 15px 20px;
  justify-content: space-around;

  .top {
    font-size: 16px;
    font-weight: bold;
  }

  .top-number {
    font-size: 16px;
    font-weight: bold;
  }
}

.flex-col {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

}

.pointer {
  cursor: pointer;
}

.tag-layout {
  display: flex;
  padding: 10px 0;
}

/deep/ .el-badge__content {
  z-index: 99;
}

.tag {
  display: inline-block;
  margin-right: 5px;
  text-align: center;
  font-size: 12px;
  color: #ffffff;
  background-color: #008000;
  height: 24px;
  line-height: 24px;
  border-radius: 30px;
  margin-right: 5px;
}

.tag-red {
  background-color: #fde2e2;
  color: #CC0000;
}

.tag-blue {
  background-color: #d9ecff;
  color: @blueColor;
}

.tag-grey {
  background: #f4f4f5;
  color: #868686;
}

.search-row {
  .label {
    width: 70px;
  }
}

.bottom-group {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.badge-item-12 {
  /deep/.el-badge__content {

    right: 12px
  }
}

.badge-item-20 {
  /deep/.el-badge__content {

    right: 20px
  }
}

.badge-item-25 {
  /deep/.el-badge__content {

    right: 25px
  }
}

.mytest {
  // /deep/ .el-button--medium {
  //   padding-left: 10px;
  //   padding-right: 10px;
  // }

}

//未实现的服务项目 字体为黑
.unrealized-tag {
  color: #000;
  border-color: #e9e9eb;
  .el-tag{
    background-color: #f4f4f5;
  }
}
</style>
