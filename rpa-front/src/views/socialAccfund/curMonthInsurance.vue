<template>
  <div class="content spl-container">
    <div class="loading-mask-none" v-loading="mainLoading.isLoading" :element-loading-text="mainLoading.mainLoadingText"
      :element-loading-spinner="$global.elementLoadingSpinner">
      <!-- 导航 -->
      <breadcrumb :data="pathData"></breadcrumb>
      <div style="padding: 20px 20px 0 20px">
        <palTab :tabs="tabs" v-model="tabActive" :type="2" @change="changeTab">
          <template slot="btns">
            <el-button type="text" icon="el-icon-setting" style="margin-top: -5px;" @click="callBackSettingShow = true">回调作业信息配置</el-button>
          </template>
        </palTab>

        <!--社保-->
        <div v-show="tabActive == 0">
          <div class="search-row clearfix">
            <el-row>
              <el-col :span="8" class="display-flex">
                <span class="label required">参保城市：</span>
                <addrSelector v-model="socialSearch.addrName" :addrArr="addrArr" @changeAddrSelect="getSocialAddrId"
                  width="100%" class="search-row-item"></addrSelector>
              </el-col>
              <el-col :span="8" class="display-flex">
                <span class="label" style="flex-shrink: 0;">客户名称：</span>
                <el-select v-model="socialSearch.custId" @change="changeCust" class="search-row-item" clearable filterable
                  style="width:300px">
                  <el-option v-for="it in socialSearch.customArr" :key="it.itemId" :label="it.customerName"
                    :value="it.id"></el-option>
                </el-select>
              </el-col>
              <el-col :span="8" class="display-flex">
                <span class="label" style="flex-shrink: 0;">申报账户：</span>
                <el-select v-model="socialSearch.itemId" @change="changeCompany(1)" class="search-row-item" clearable
                  filterable style="width:300px">
                  <el-option v-for="it in socialSearch.companyArr" :key="it.itemId" :label="it.name"
                    :value="it.itemId"></el-option>
                </el-select>
              </el-col>
            </el-row>
            <el-row class="mt-15">
              <el-col :span="8" class="display-flex">
                <span class="label" style="flex-shrink: 0;">创建时间：</span>
                <div style="display:flex;max-width:300px">
                  <el-date-picker v-model="socialSearch.createStartTime" value-format="yyyy-MM-dd" style="width:50%;"
                    type="date" placeholder="选择时间" :picker-options="getPickerOption('start', 'socialSearch')"
                    clearable></el-date-picker>
                  <span class="lh-com" style="padding: 0 5px;">~</span>
                  <el-date-picker v-model="socialSearch.createEndTime" value-format="yyyy-MM-dd" style="width:50%;"
                    placeholder="选择时间" :picker-options="getPickerOption('end', 'socialSearch')"
                    clearable></el-date-picker>
                </div>
              </el-col>
              <el-col :span="8" style="padding-left: 20px">
                <el-input style="width: 100%;max-width: 370px;" v-model="socialSearch.keyword" placeholder="输入姓名/证件号码关键字"
                  clearable></el-input>
              </el-col>
              <el-col :span="4" style="display: flex">
                <span class="label" style="flex-shrink: 0;">附件状态：</span>
                <div class="flex1 pt-5">
                  <el-checkbox-group v-model="socialSearch.fileStatuses">
                    <el-checkbox label="1">已上传</el-checkbox>
                    <el-checkbox label="0">无上传</el-checkbox>
                  </el-checkbox-group>
                </div>
              </el-col>
              <el-col :span="4" class="text-right" v-if="$global.hasPermission('curInsuranceQuery')">
                <el-button type="primary" class="search-btn w-60 mr-10" @click="getSocialTable">查询</el-button>
                <el-button icon="ic-filter-blue" class="btn--border-blue" @click="socialFilter.show = true">筛选</el-button>
              </el-col>
            </el-row>
            <el-row class="mt-15">
              <el-col :span="socialSearch.statusList.indexOf('5')>-1?11:8" class="display-flex">
                <span class="label">申报状态：</span>
                <div class="flex1 display-flex">
                  <el-checkbox-group v-model="socialSearch.statusList" size="medium">
                    <el-checkbox-button class="sbStatusCeckbox" v-for="item in statusArr" :key="item.id" :label="item.id">{{
                        item.name }}</el-checkbox-button>
                  </el-checkbox-group>
                  <el-checkbox v-show="socialSearch.statusList.indexOf('5')>-1" class="ml-20 mt-10" :true-label="1" :false-label="0" v-model="socialSearch.ignoreFlag">不看忽略数据</el-checkbox>
                </div>
              </el-col>

              <el-col :span="socialSearch.statusList.indexOf('5')>-1?7:10" class="display-flex">
                <span class="label">申报类型：</span>
                <el-checkbox-group v-model="socialSearch.typeList" size="medium" @change="getOperationTypes(1)">
                  <el-checkbox-button v-for="item in typeArr" :key="item.id" :label="item.id">{{ item.name
                  }}</el-checkbox-button>
                </el-checkbox-group>
              </el-col>
              <el-col :span="6" class="text-right">
                <el-link type="primary" v-if="$global.hasPermission('interactiveData')" :underline="false"
                  @click="goInteractiveDataPage">
                  交互数据
                  <i class="el-icon-arrow-right"></i>
                </el-link>
                <el-button class="ml-15" size="small" type="primary" @click="openExportDialog()"
                  v-if="$global.hasPermission('generateOfferFile')">生成报盘文件</el-button>
                <el-button class="ml-15 mt-5 btn--border-blue" style="margin-left: 15px;" size="small" icon="el-icon-download" @click="downloadVoucher()"
                  v-show="socialVoucher.show">本月参保凭证</el-button>
              </el-col>
            </el-row>
          </div>
          <div>
            <!-- 表格 -->
            <dTable @fetch="getSocialTable" ref="socialTable" :tableHeight="tableHeight" :paging="true"
              :showSelection="true" :showIndex="true" :filterTags="socialSearch.filterTags" @resetFilter="resetSocial"
              rowKey="itemId" :rowHeight="45">
              <u-table-column prop="employeeName" label="姓名" min-width="80"
                :show-overflow-tooltip="true"></u-table-column>
              <u-table-column prop="idCard" label="证件号码" min-width="160" :show-overflow-tooltip="true"></u-table-column>
              <u-table-column prop="addrName" label="参保城市" min-width="80" :show-overflow-tooltip="true"></u-table-column>
              <u-table-column prop="changeType" label="申报类型" min-width="80" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>{{ handleChangeType(scope.row.changeType) }}</span>
                </template>
              </u-table-column>
              <u-table-column prop="insuredDate" label="参保时间" min-width="90" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>
                    {{
                      $moment(scope.row.insuredDate).format('YYYY-MM')
                    }}
                  </span>
                </template>
              </u-table-column>
              <u-table-column prop="sameStatusName" label="参保险种" min-width="180" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>{{
                    getProductNameList(scope.row.sameStatusName)
                  }}</span>
                </template>
              </u-table-column>
              <u-table-column prop="roundEmpTbBase" label="缴纳基数" min-width="80" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>{{ formatNumber(scope.row.roundEmpTbBase) }}</span>
                </template>
              </u-table-column>
              <u-table-column prop="status" label="申报状态" min-width="170" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>{{ handleDeclareStatus(scope.row.status) }}</span>
                  <span v-if="scope.row.ignoreFlag===1" class="causeTip ml-5">忽</span>
                </template>
              </u-table-column>
              <u-table-column prop="failCase" label="原因备注" min-width="150" :show-overflow-tooltip="false">
                <template slot-scope="scope">
                  <div class="display-flex">
                    <el-popover placement="top" title="" trigger="hover" width="350" v-if="scope.row.annotate">
                      <div v-html="handleCauseTip(scope.row.annotate)"></div>
                      <span class="causeTip" slot="reference">注</span>
                    </el-popover>
                    <el-tooltip effect="dark" :content="scope.row.failCase == null ? '' : scope.row.failCase" placement="top">
                      <p class="over-ell">{{ scope.row.failCase == null ? '' : scope.row.failCase }}</p>
                    </el-tooltip>
                  </div>
                </template>
              </u-table-column>
              <u-table-column prop="createTime" label="创建时间" min-width="160" :show-overflow-tooltip="true">
                <template scope="scope">
                  <span>{{ $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
                </template>
              </u-table-column>
              <u-table-column
                prop="status"
                label="附件资料"
                min-width="250"
                :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span v-for="(item, index) in scope.row.empFiles ? scope.row.empFiles : []" :key="index">
                    <span class="a-aim a-link f-cursor text-blue" v-for="(it, ind) in item.files" :key="ind" @click="handleDownLoadFile(it.id)">{{it.clientFileName}}；</span>
                  </span>
                </template>
              </u-table-column>
              <u-table-column fixed="right" label="操作" width="170">
                <template slot-scope="scope">
                  <el-button v-if="[1, 6].indexOf(scope.row.status) > -1" type="text" size="small"
                    @click="withdraw('social', 'single', scope.row)">撤回</el-button>
                  <el-button type="text" size="small" @click="viewRequest('1', scope.row)">查看</el-button>
                  <el-button v-if="scope.row.robotTrackVO" type="text" size="small" @click="viewRunTrack('1', scope.row)">
                    <i :class="'ic-run ' + getRunInfo(scope.row.robotTrackVO.trackStatus).icon"></i>执行轨迹</el-button>
                  <!-- <el-button
                    type="text"
                    class="ml-25"
                    size="small"
                    @click="getRecord(scope.row, 'social')"
                    v-if="$global.hasPermission('curInsuranceOperationRecord')"
                    >操作记录</el-button
                  >-->
                </template>
              </u-table-column>
              <template slot="pagination-btns">
                <div class="display-flex">
                  <!--<el-button
                    size="small"
                    class="btn&#45;&#45;border-red"
                    @click="handleVoidSocial()"
                    v-if="$global.hasPermission('curInsuranceVoid')"
                    >作废</el-button
                  >-->
                  <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                    v-if="$global.hasPermission('curInsuranceExport')" @click="exportSocialTableData">导出</el-button>
                  <el-button size="mini" class="btn--border-blue s-btn" @click="changeAdjust">调整状态</el-button>
                </div>
              </template>
            </dTable>
          </div>
        </div>
        <!--社保-->

        <!--公积金-->
        <div v-show="tabActive == 1">
          <div class="search-row clearfix">
            <el-row>
              <el-col :span="8" class="display-flex">
                <span class="label required">参保城市：</span>
                <addrSelector v-model="accfundSearch.addrName" :addrArr="addrArr" @changeAddrSelect="getAccfundAddrId"
                  width="100%" class="search-row-item"></addrSelector>
              </el-col>
              <el-col :span="8" class="display-flex">
                <span class="label" style="flex-shrink: 0;">客户名称：</span>
                <el-select v-model="accfundSearch.custId" @change="changeCust" class="search-row-item" clearable filterable
                  style="width:300px">
                  <el-option v-for="it in socialSearch.customArr" :key="it.itemId" :label="it.customerName"
                    :value="it.id"></el-option>
                </el-select>
              </el-col>
              <el-col :span="8" class="display-flex">
                <span class="label" style="flex-shrink: 0;">参保主体：</span>
                <el-select v-model="accfundSearch.itemId" @change="changeCompany(2)" clearable filterable
                  style="width:300px;">
                  <el-option v-for="it in accfundSearch.companyArr" :key="it.itemId" :label="it.name"
                    :value="it.itemId"></el-option>
                </el-select>
              </el-col>
            </el-row>
            <el-row class="mt-15">
              <el-col :span="8" class="display-flex">
                <span class="label" style="flex-shrink: 0">创建时间：</span>
                <div style="display:flex;max-width:300px;">
                  <el-date-picker v-model="accfundSearch.createStartTime" value-format="yyyy-MM-dd" style="width:50%"
                    type="date" placeholder="选择时间" :picker-options="getPickerOption('start', 'accfundSearch')"
                    clearable></el-date-picker>
                  <span class="lh-com" style="padding: 0 5px;">~</span>
                  <el-date-picker v-model="accfundSearch.createEndTime" value-format="yyyy-MM-dd" style="width:50%"
                    placeholder="选择时间" :picker-options="getPickerOption('end', 'accfundSearch')"
                    clearable></el-date-picker>
                </div>
              </el-col>
              <el-col :span="8" style="padding-left: 20px">
                <el-input style="width: 100%;max-width: 370px;" v-model="accfundSearch.keyword" placeholder="输入姓名/证件号码关键字"
                  clearable></el-input>
              </el-col>
              <el-col :span="4" style="display: flex">
                <span class="label" style="flex-shrink: 0;">附件状态：</span>
                <div class="flex1 pt-5">
                  <el-checkbox-group v-model="accfundSearch.fileStatuses">
                    <el-checkbox label="1">已上传</el-checkbox>
                    <el-checkbox label="0">无上传</el-checkbox>
                  </el-checkbox-group>
                </div>
              </el-col>
              <el-col :span="4" class="text-right" v-if="$global.hasPermission('curInsuranceQuery')">
                <el-button type="primary" class="search-btn w-60 mr-10" @click="getAccfundTable">查询</el-button>
                <el-button icon="ic-filter-blue" class="btn--border-blue"
                  @click="accfundFilter.show = true">筛选</el-button>
              </el-col>
            </el-row>
            <el-row class="mt-15">
              <el-col :span="accfundSearch.statusList.indexOf('5')>-1?11:8" class="display-flex">
                <span class="label">申报状态：</span>
                <div class="flex1 display-flex">
                  <el-checkbox-group v-model="accfundSearch.statusList" size="medium">
                    <el-checkbox-button class="sbStatusCeckbox" v-for="item in statusArr" :key="item.id" :label="item.id">{{
                        item.name }}</el-checkbox-button>
                  </el-checkbox-group>
                  <el-checkbox v-show="accfundSearch.statusList.indexOf('5')>-1" class="ml-20 mt-10" :true-label="1" :false-label="0" v-model="accfundSearch.ignoreFlag">不看忽略数据</el-checkbox>
                </div>
              </el-col>

              <el-col :span="accfundSearch.statusList.indexOf('5')>-1?7:10" class="display-flex">
                <span class="label">申报类型：</span>
                <el-checkbox-group v-model="accfundSearch.typeList" size="medium" @change="getOperationTypes(2)">
                  <el-checkbox-button v-for="item in typeArr" :key="item.id" :label="item.id">{{ item.name
                  }}</el-checkbox-button>
                </el-checkbox-group>
              </el-col>
              <el-col :span="6" class="text-right">
                <el-link type="primary" :underline="false" v-if="$global.hasPermission('interactiveData')"
                  @click="goInteractiveDataPage">
                  交互数据
                  <i class="el-icon-arrow-right"></i>
                </el-link>
                <el-button class="ml-15" size="small" type="primary" v-if="$global.hasPermission('generateOfferFile')"
                  @click="openExportDialog()">生成报盘文件</el-button>
                <el-button class="mt-5 btn--border-blue" style="margin-left: 15px;" size="small" icon="el-icon-download" @click="downloadVoucher()"
                           v-show="accfundVoucher.show">本月参保凭证</el-button>
              </el-col>
            </el-row>
          </div>
          <div>
            <!-- 表格 -->
            <dTable @fetch="getAccfundTable" ref="accfundTable" :tableHeight="tableHeight" :paging="true"
              :showSelection="true" :showIndex="true" :filterTags="accfundSearch.filterTags" @resetFilter="resetAccfund"
              rowKey="id" :rowHeight="45">
              <u-table-column prop="employeeName" label="姓名" min-width="80"
                :show-overflow-tooltip="true"></u-table-column>
              <u-table-column prop="idCard" label="证件号码" min-width="160" :show-overflow-tooltip="true"></u-table-column>
              <u-table-column prop="addrName" label="参保城市" min-width="80" :show-overflow-tooltip="true"></u-table-column>
              <u-table-column prop="changeType" label="申报类型" min-width="80" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>{{ handleChangeType(scope.row.changeType) }}</span>
                </template>
              </u-table-column>
              <u-table-column prop="insuredDate" label="参保时间" min-width="90" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>
                    {{
                      $moment(scope.row.insuredDate).format('YYYY-MM')
                    }}
                  </span>
                </template>
              </u-table-column>
              <u-table-column prop="roundEmpTbBase" label="缴纳基数" min-width="80" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>{{ formatNumber(scope.row.roundEmpTbBase) }}</span>
                </template>
              </u-table-column>
              <u-table-column prop="ratios" label="参保比例" min-width="160" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span v-html="scope.row.ratios ? scope.row.ratios : ''"></span>
                </template>
              </u-table-column>
              <u-table-column prop="status" label="申报状态" min-width="170" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>{{handleDeclareStatus(scope.row.status) }}
                  </span>
                  <span v-if="scope.row.ignoreFlag===1" class="causeTip ml-5">忽</span>
                </template>
              </u-table-column>
              <u-table-column prop="failCause" label="原因备注" min-width="150" :show-overflow-tooltip="false">
                <template slot-scope="scope">
                  <div class="display-flex">
                    <el-popover placement="top" title="" trigger="hover" width="350" v-if="scope.row.annotate">
                      <div v-html="handleCauseTip(scope.row.annotate)"></div>
                      <span class="causeTip" slot="reference">注</span>
                    </el-popover>
                    <el-tooltip effect="dark"
                      :content="handleCauseContent(scope.row)"
                      v-if="scope.row.failCause != scope.row.suppFailCause" placement="top">
                      <p class="over-ell">
                        {{ handleCauseContent(scope.row) }}
                      </p>
                    </el-tooltip>
                    <el-tooltip effect="dark" :content="scope.row.failCause" v-else placement="top">
                      <p class="over-ell">{{ scope.row.failCause }}</p>
                    </el-tooltip>
                  </div>
                </template>
              </u-table-column>
              <u-table-column prop="createTime" label="创建时间" min-width="170" :show-overflow-tooltip="true">
                <template scope="scope">
                  <span>{{ $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
                </template>
              </u-table-column>
              <u-table-column
                prop="status"
                label="附件资料"
                min-width="250"
                :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span v-for="(item, index) in scope.row.empFiles ? scope.row.empFiles : []" :key="index">
                    <span class="a-aim a-link f-cursor text-blue" v-for="(it, ind) in item.files" :key="ind" @click="handleDownLoadFile(it.id)">{{it.clientFileName}}；</span>
                  </span>
                </template>
              </u-table-column>
              <u-table-column fixed="right" label="操作" width="170">
                <template slot-scope="scope">
                  <el-button v-if="[1, 6].indexOf(scope.row.status) > -1" type="text" size="small"
                    @click="withdraw('accfund', 'single', scope.row)">撤回</el-button>
                  <el-button type="text" size="small" @click="viewRequest('2', scope.row)">查看</el-button>
                  <el-button v-if="scope.row.robotTrackVO" type="text" size="small" @click="viewRunTrack('2', scope.row)">
                    <i :class="'ic-run ' + getRunInfo(scope.row.robotTrackVO.trackStatus).icon"></i>执行轨迹</el-button>
                  <!-- <el-button
                    type="text"
                    class="ml-25"
                    size="small"
                    @click="getRecord(scope.row, 'accfund')"
                    v-if="$global.hasPermission('curInsuranceOperationRecord')"
                    >操作记录</el-button
                  >-->
                </template>
              </u-table-column>
              <template slot="pagination-btns">
                <div class="display-flex">
                  <!-- <el-button
                    size="small"
                    class="btn&#45;&#45;border-red"
                    @click="handleVoidAccfund()"
                    v-if="$global.hasPermission('curInsuranceVoid')"
                    >作废</el-button
                  >-->
                  <el-button size="mini" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                    @click="exportAccfundTableData">导出</el-button>
                  <el-button size="mini" class="btn--border-blue s-btn" @click="changeAdjust">调整状态</el-button>
                </div>
              </template>
            </dTable>
          </div>
        </div>
        <!--公积金-->
      </div>
    </div>

    <!-- 社保-筛选条件 -->
    <el-drawer title="筛选" :visible.sync="socialFilter.show" :wrapperClosable="false" custom-class="spl-filter-drawer"
      :show-close="true">
      <div class="pd-20 pt-10">
        <div class="type-title mt-0">
          <span class="text">参保时间</span>
        </div>
        <div class="pl-20 pr-20 pt-20 pb-20" style="display: flex; align-items: center; padding-right: 0px">
          <el-date-picker v-model="socialFilter.insuredStartDate" type="month" value-format="yyyy-MM" placeholder="开始月份"
            style="width: 100%" :picker-options="getPickerOption2('start', 'socialFilter')"></el-date-picker>
          <span style="margin: 0 5px">至</span>
          <el-date-picker v-model="socialFilter.insuredEndDate" type="month" value-format="yyyy-MM"
            :picker-options="getPickerOption2('end', 'socialFilter')" placeholder="结束月份"
            style="width: 100%"></el-date-picker>
        </div>

        <div class="type-title mt-0">
          <span class="text">子节点</span>
        </div>
        <div class="pl-20 pr-20 pt-20 pb-20">
          <el-select v-model="socialFilter.operationTypes" clearable multiple collapse-tags filterable
            style="width:100%;">
            <el-option v-for="it in socialOperationTypeArr" :key="it.id" :label="it.name" :value="it.code"></el-option>
          </el-select>
        </div>
        <div class="type-title mt-0">
          <span class="text">证件号码</span>
        </div>
        <div class="pl-20 pr-20 pt-20">
          <el-input v-model="socialFilter.empIdCards" type="textarea" rows="5" resize="none" placeholder="一行只可输入一个证件号码"
            @blur="replaceBlank('socialFilter', 'empIdCards')"></el-input>
          <p class="text-right mr-5 mt-5">已输入 {{ socialFilter.searchIdCardsLength }} 个</p>
        </div>

        <div class="type-title">
          <span class="text">姓名</span>
        </div>
        <div class="pl-20 pr-20 pt-20">
          <el-input v-model="socialFilter.empNames" type="textarea" rows="5" resize="none" placeholder="一行只能输入一个姓名"
            @blur="replaceBlank('socialFilter', 'empNames')"></el-input>
          <p class="text-right mr-5 mt-5">已输入 {{ socialFilter.searchNamesLength }} 个</p>
        </div>
      </div>
      <div class="drawer-footer-buts">
        <el-button class="btn--border-blue s-btn" @click="resetFilterSocial">重置</el-button>
        <el-button type="primary" class="s-btn ml-12" @click="confirmFilterSocial">确认筛选</el-button>
      </div>
    </el-drawer>

    <!-- 公积金-筛选条件 -->
    <el-drawer title="筛选" :visible.sync="accfundFilter.show" :wrapperClosable="false" custom-class="spl-filter-drawer"
      :show-close="true">
      <div class="pd-20 pt-10">
        <div class="type-title mt-0">
          <span class="text">参保时间</span>
        </div>
        <div class="pl-20 pr-20 pt-20 pb-20" style="display: flex; align-items: center; padding-right: 0px">
          <el-date-picker v-model="accfundFilter.insuredStartDate" type="month" value-format="yyyy-MM" placeholder="开始月份"
            style="width: 100%" :picker-options="getPickerOption2('start', 'accfundFilter')"></el-date-picker>
          <span style="margin: 0 5px">至</span>
          <el-date-picker v-model="accfundFilter.insuredEndDate" type="month" value-format="yyyy-MM"
            :picker-options="getPickerOption2('end', 'accfundFilter')" placeholder="结束月份"
            style="width: 100%"></el-date-picker>
        </div>

        <div class="type-title mt-0">
          <span class="text">子节点</span>
        </div>
        <div class="pl-20 pr-20 pt-20 pb-20">
          <el-select v-model="accfundFilter.operationTypes" clearable multiple collapse-tags filterable
            style="width:100%;">
            <el-option v-for="it in accfundOperationTypeArr" :key="it.id" :label="it.name" :value="it.code"></el-option>
          </el-select>
        </div>

        <div class="type-title mt-0">
          <span class="text">证件号码</span>
        </div>
        <div class="pl-20 pr-20 pt-20">
          <el-input v-model="accfundFilter.empIdCards" type="textarea" rows="5" resize="none" placeholder="一行只可输入一个证件号码"
            @blur="replaceBlank('accfundFilter', 'empIdCards')"></el-input>
          <p class="text-right mr-5 mt-5">已输入 {{ accfundFilter.searchIdCardsLength }} 个</p>
        </div>

        <div class="type-title">
          <span class="text">姓名</span>
        </div>
        <div class="pl-20 pr-20 pt-20">
          <el-input v-model="accfundFilter.empNames" type="textarea" rows="5" resize="none" placeholder="一行只能输入一个姓名"
            @blur="replaceBlank('accfundFilter', 'empNames')"></el-input>
          <p class="text-right mr-5 mt-5">已输入 {{ accfundFilter.searchNamesLength }} 个</p>
        </div>
      </div>
      <div class="drawer-footer-buts">
        <el-button class="btn--border-blue s-btn" @click="resetFilterAccfund">重置</el-button>
        <el-button type="primary" class="s-btn ml-12" @click="confirmFilterAccfund">确认筛选</el-button>
      </div>
    </el-drawer>

    <!-- 报盘导出 -->
    <el-drawer :visible.sync="exportObj.show" title="生成报盘文件" width="560px" :wrapperClosable="false"
      custom-class="spl-filter-drawer" :show-close="true" :close-on-click-modal="false" :before-close="cancelExport">
      <div class="pt-10">
        <el-form :model="exportObj" ref="exportObj" class="pd-20">

          <label class="required item-label">参保城市：</label>
          <el-form-item label="" prop="addrName" :rules="[{ required: true, message: '参保城市', trigger: 'blur' }]">
            <addrSelector v-model="exportObj.addrName" :addrArr="addrArr" @changeAddrSelect="getExportAddrId"
              width="300px"></addrSelector>
          </el-form-item>

          <label class="item-label">客户名称：</label>
          <el-form-item label="" prop="custId">
            <el-select v-model="exportObj.custId" clearable filterable @change="getCompanyList(bussinessType, true)"
              style="width:300px;">
              <el-option v-for="it in customerArr" :key="it.id" :label="it.name" :value="it.id"></el-option>
            </el-select>
          </el-form-item>

          <label class="required item-label">参保主体：</label>
          <el-form-item label="" prop="accountNumbers" :rules="[{ required: true, message: '请选择参保主体', trigger: 'change' }]">
            <el-select v-model="exportObj.accountNumbers" multiple collapse-tags clearable filterable
              style="width:300px;">
              <el-option v-for="it in exportObj.companyArr" :key="it.itemId" :label="it.name"
                :value="it.accountNumber"></el-option>
            </el-select>
          </el-form-item>

          <label class="required item-label">申报系统：</label>
          <el-form-item label="" prop="tplTypeCode" :rules="[{ required: true, message: '请选择申报系统', trigger: 'change' }]">
            <el-select v-model="exportObj.tplTypeCode" clearable filterable style="width:300px;" @change="mergeAddText()">
              <el-option v-for="it in exportObj.tplTypeArr" :key="it.tplType" :label="it.tplTypeName"
                :value="it.tplType"></el-option>
            </el-select>
            <span style="padding-left: 20px" v-text="exportObj.showMergeAddText"></span>
          </el-form-item>

          <label class="required item-label">申报类型：</label>
          <el-form-item label="" prop="declareType" :rules="[{ required: true, message: '请选择申报类型', trigger: 'change' }]">
            <el-checkbox-group size="medium" v-model="exportObj.declareType" @change="changeDeclareType()">
              <el-checkbox-button class="sbStatusCeckbox" v-for="item in typeArr" :key="item.id" :label="item.id">{{
                item.name }}</el-checkbox-button>
            </el-checkbox-group>
          </el-form-item>

          <label class="required item-label">子节点：</label>
          <el-form-item label="" prop="operationTypes" :rules="[{ required: true, message: '请选择子节点', trigger: 'change' }]">
            <el-select v-model="exportObj.operationTypes" clearable multiple collapse-tags filterable
              style="width:300px;">
              <el-option v-for="it in exportObj.operationTypeArr" :key="it.id" :label="it.name"
                :value="it.code"></el-option>
            </el-select>
          </el-form-item>

          <label class="required item-label">申报状态：</label>
          <el-form-item label="" prop="statusList" :rules="[{ required: true, message: '请选择申报状态', trigger: 'change' }]">
            <el-checkbox-group v-model="exportObj.statusList" size="medium">
              <el-checkbox-button class="sbStatusCeckbox" v-for="item in exportObj.statusArr" :key="item.id"
                :label="item.id">{{ item.name }}</el-checkbox-button>
            </el-checkbox-group>
          </el-form-item>

          <label class="required item-label">申报日期：</label>
          <el-form-item label="" prop="dateStr" :rules="[{ required: true, message: '请选择申报日期', trigger: 'change' }]">
            <el-date-picker v-model="exportObj.dateStr" type="date" value-format="yyyy-MM-dd" placeholder="申报日期"
              style="width:300px;"></el-date-picker>
            <p style="padding-left: 62px; margin-top: -10px;color: #42A2FF">请选择申报期内任一日期才能查出数据</p>
          </el-form-item>

          <label class="item-label" v-show="exportObj.addrName != ''">申报期：</label>
          <div v-show="exportObj.addrName != '' && exportObj.declareRange.length > 0">
            <p style="line-height: 40px" v-for="(item, index) in exportObj.declareRange" v-text="item"></p>
          </div>
          <div v-show="exportObj.addrName != '' && exportObj.declareRange.length == 0">
            <p style="line-height: 40px; color: #df5000">未设置申报期</p>
          </div>
        </el-form>

        <div class="drawer-footer-buts">
          <el-button size="small" class="mr-15 w-80" @click="cancelExport()">取消</el-button>
          <el-button size="small" class="w-80" type="primary" @click="exportSumbit()"
            :loading="exportObj.exportLoading">确定</el-button>
        </div>
        <!--<div class="text-right mt-30">
          <el-button size="small" class="mr-15 w-80" @click="cancelExport()">取消</el-button>
          <el-button
            size="small"
            class="w-80"
            type="primary"
            @click="exportSumbit()"
            :loading="exportObj.exportLoading"
          >确定</el-button>
        </div>-->
      </div>
    </el-drawer>

    <!--作废-->
    <el-dialog :visible.sync="voidForm.show" title="作废原因" width="600px" class="cust-dialog" :show-close="true"
      :close-on-click-modal="false" :before-close="cancelVoid">
      <div class="template-dialog-box pt-20">
        <el-form :model="voidForm" ref="voidForm">
          <div class="display-flex">
            <label class="required-pre w-70" style="vertical-align: top">作废原因：</label>
            <el-form-item prop="reason" class="flex1" :rules="[
              { required: true, message: '请输入作废原因', trigger: 'blur' },
            ]">
              <el-input v-model.trim="voidForm.reason" type="textarea" rows="5" resize="none"></el-input>
            </el-form-item>
          </div>
        </el-form>
        <div class="text-right mt-30">
          <el-button size="small" class="mr-15 w-80" @click="cancelVoid()">取消</el-button>
          <el-button size="small" class="w-80" type="primary" @click="voidSumbit()"
            :loading="voidForm.btnLoading">确定</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 回调配置信息 -->
    <el-drawer title="回调作业信息配置" :visible.sync="callBackSettingShow" :wrapperClosable="false"
      custom-class="spl-filter-drawer" :show-close="true" :before-close="clearCallBackSettingForm">
      <div class="drawer-body pt-20 pb-20">
        <el-form ref="callBackSettingCopy" :model="callBackSettingCopy" class="filter-form">

          <label class="required item-label">执行频率</label>
          <el-form-item label="" prop="cron" :rules="[{ required: true, message: '执行频率', trigger: 'blur' }]">
            <el-input v-model="callBackSettingCopy.cron" class="w-p100" maxlength="40" clearable
              placeholder="请输入"></el-input>
          </el-form-item>

          <label class="required item-label">状态</label>
          <el-form-item label="" prop="status">
            <el-switch v-model="callBackSettingCopy.status" active-color="#13ce66" :activeValue="1"
              :inactiveValue="0"></el-switch>
          </el-form-item>

          <label class="item-label">描述</label>
          <el-form-item label="" prop="comment">
            <el-input v-model.trim="callBackSettingCopy.comment" type="textarea" :rows="2" class="w-p100" maxlength="150"
              clearable placeholder="请输入"></el-input>
          </el-form-item>

        </el-form>
      </div>
      <div class="drawer-footer-buts">
        <el-button class="mr-12" size="small" @click="clearCallBackSettingForm()">取 消</el-button>
        <el-button size="small" type="primary" @click="vaildCallBackSettingForm()"
          :loading="callBackSettingSaveLoading">确定</el-button>
      </div>
    </el-drawer>
    <myDrawer :tabs="getType.tabs" :show.sync="getType.show" :type="this.bussinessType" :row="row" ref="myDrawer" isOpera
      requestType="employeeInsurance" :edit="false" @do-query="onDoQuery"></myDrawer>
    <!--作废-->
    <el-dialog :visible.sync="withdrawData.show" title="撤回" width="600px" class="cust-dialog" :show-close="true"
      :close-on-click-modal="false" :before-close="cancelWithdraw">
      <div class="template-dialog-box">
        <el-form :model="withdrawData" ref="withdrawForm">
          <p v-if="withdrawData.type2 == 'batch'">已筛选<span style="color:#FD6154;"> {{ withdrawData.ids.length }}
            </span>条待提交/待申报的记录</p>
          <p class="mb-20">一旦撤回，记录无法查出!</p>
          <div class="display-flex">
            <label class="required-pre w-70" style="vertical-align: top">原因：</label>
            <el-form-item prop="reason" class="flex1" :rules="[
              { required: true, message: '请输入撤回原因', trigger: 'blur' },
              { required: true, min: 0, max: 100, message: '撤回原因在100个字符以内', trigger: 'change' },
            ]">
              <el-input v-model.trim="withdrawData.reason" type="textarea" rows="3" resize="none"></el-input>
              <p style="position: absolute;right:0;font-size:12px;" v-if="withdrawData.reason.length > 80">
                {{ withdrawData.reason.length }}/<span style="color:#FD6154">100</span></p>
            </el-form-item>
          </div>
        </el-form>
        <div class="text-right mt-30">
          <el-button size="small" class="mr-15 w-80" @click="cancelWithdraw()">取消</el-button>
          <el-button size="small" class="w-80" type="primary" @click="withdrawSumbit()"
            :loading="withdrawData.btnLoading">确定</el-button>
        </div>
      </div>
    </el-dialog>
    <el-drawer title="调整状态" :visible.sync="adjustData.show" :wrapperClosable="false" custom-class="spl-filter-drawer"
      :show-close="true" class="adjustData" :before-close="cancelAdjustData">
      <div class="drawer-body pt-20 pb-20">
        <el-form ref="adjustData" :model="adjustData" class="filter-form" :rules="rules">
          <el-form-item label="参保城市：" prop="addrName">
            <span>{{ this.adjustData.addrName }}</span>
          </el-form-item>
          <!-- <label class="required item-label">申报账户：</label> -->
          <el-form-item label="申报账户：" prop="selects">
            <div style="max-height:200px;overflow:auto;margin-left:20px;">
              <p v-for="(item, index) in adjustData.selects" :key="index" style="word-break: break-all;">
                {{ item.orgName + (item.compAccount ? '-' + item.compAccount : '') }}
              </p>
            </div>
          </el-form-item>
          <el-form-item label="状态数据：" prop="dataType">
            <span>{{ adjustData.dataType.join(',') }}</span>
          </el-form-item>
          <el-form-item label="申报系统：" prop="tplTypeCode">
            <el-select v-model="adjustData.tplTypeCode" filterable style="width:100%;">
              <el-option v-for="it in exportObj.tplTypeArr" :key="it.tplType" :label="it.tplTypeName"
                :value="it.tplType"></el-option>
            </el-select>
            <p>{{ adjustDataTplTypeCode(adjustData.tplTypeCode) ? '(' + adjustDataTplTypeCode(adjustData.tplTypeCode) + ')' :
              '' }}</p>
          </el-form-item>
          <el-form-item label="调整为：" prop="declareStatus">
            <el-select v-model="adjustData.declareStatus" filterable style="width:100%;">
              <el-option v-for="it in statusArr" :key="it.id" :label="it.name" :value="it.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="" prop="adjustChecked" v-if="adjustData.status">
            <el-checkbox v-model="adjustData.adjustChecked" true-label="1" false-label="">
              <span style="display: inline-flex;white-space: pre-wrap;">我已确认，此申报中数据①未被申报；②未处在执行中；③已及时清理机器人报盘文件</span>
            </el-checkbox>
          </el-form-item>
          <el-form-item label="原因备注：" prop="failReason"
            :class="[adjustData.declareStatus == '4' || adjustData.declareStatus == '2' ? 'resonlabel' : '']">
            <el-input v-model.trim="adjustData.failReason" type="textarea" :rows="2" class="w-p100" maxlength="150"
              clearable placeholder="请输入"></el-input>
          </el-form-item>
          <el-form-item label="">
            <div style="color:#FF743E;background:#fff6f6;font-size:12px;line-height:1.4;padding:10px;margin-top:10px;">
              <p>①失败原因以【校验失败：】开头，失败类型为0，第三方客户驳回到待提交，客服处理</p>
              <p>②失败原因以【缺资料：】开头，失败类型为2，第三方客户驳回到待提交，客服处理</p>
              <p>③失败原因包含【柜台处理】，失败类型为2，第三方客户驳回到待提交，客服处理</p>
              <p>④失败原因包含【前台门报】，失败类型为3，第三方客户驳保持申报中，申报处理</p>
              <p>⑤正常申报失败，则原因无需体现①②③④所述</p>
            </div>
          </el-form-item>
          <el-form-item label="调整理由：" prop="adjustReason">
            <el-input v-model.trim="adjustData.adjustReason" type="textarea" :rows="2" class="w-p100" maxlength="150"
              clearable placeholder="请输入"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div class="drawer-footer-buts">
        <el-button class="mr-12" size="small" @click="cancelAdjustData">取 消</el-button>
        <el-button size="small" type="primary" @click="confirmAdjustData"
          :loading="callBackSettingSaveLoading">确定</el-button>
      </div>
    </el-drawer>

<!--  数据轨迹  -->
    <runTrack ref="runTrack"></runTrack>
  </div>
</template>

<script>
import myDrawer from '../../components/common/socialDetailDrawer'
import dTable from '../../components/common/table'
import palTab from '../../components/common/pal-tab'
import addrSelector from '../../components/common/addrSelector'
import runTrack from './component/runTrack.vue'
export default {
  components: { dTable, palTab, addrSelector, myDrawer, runTrack },
  inject: ['getInsuranceList'],
  data () {
    return {
      myHeaders: { Authorization: this.$store.state.token },
      tabs: ['社保', '公积金'],
      tabActive: 0,
      addrArr: [],
      socialOperationTypeArr: [],
      accfundOperationTypeArr: [],
      statusArr: [
        // <!--申报状态（0作废，1待申报，2申报中，3部分成功，4申报成功，5申报失败）
        { id: '0', name: '作废' },
        { id: '6', name: '待提交' },
        { id: '1', name: '待申报' },
        { id: '2', name: '申报中' },
        { id: '4', name: '申报成功' },
        { id: '5', name: '申报失败' }
      ],
      typeArr: [
        // 变更类型（1增，2减，3调基，5补缴）
        { id: '1', name: '增员' },
        { id: '2', name: '减员' },
        { id: '3', name: '调基' },
        { id: '5', name: '补缴' }
      ],
      exportObj: {
        exportLoading: false,
        show: false,
        statusArr: [
          { id: '1', name: '待申报' },
          { id: '2', name: '申报中' },
          { id: '4', name: '申报成功' },
          { id: '5', name: '申报失败' }
        ],
        custId: null,
        companyArr: [],
        tplTypeArr: [],
        operationTypes: [],
        operationTypeArr: [],
        itemId: '',
        declareType: ['1'],
        addrId: null,
        addrName: '',
        accountNumber: '',
        accountNumbers: [],
        tplTypeCode: '',
        statusList: ['1', '2'],
        dateStr: '',
        showMergeAddText: '',
        declareRange: []
      },
      customerArr: [],
      socialSearch: {
        addrName: '',
        custId: null,
        addrId: '',
        company: '',
        itemId: '',
        startMonth: '',
        createStartTime: this.$moment().startOf('months').format('YYYY-MM-DD'),
        createEndTime: this.$moment().format('YYYY-MM-DD'),
        keyword: '',
        fileStatuses: [],
        statusList: [],
        typeList: [],
        insuredStartDate: '',
        insuredEndDate: '',
        setEmpIdCards: [],
        setEmpNames: [],
        accountNumber: '',
        filterTags: [],
        companyArr: [],
        operationTypes: [],
        customArr: [],
        ignoreFlag: 1
      },
      accfundSearch: {
        addrName: '',
        addrId: '',
        company: '',
        itemId: '',
        startMonth: '',
        createStartTime: this.$moment().startOf('months').format('YYYY-MM-DD'),
        createEndTime: this.$moment().format('YYYY-MM-DD'),
        keyword: '',
        fileStatuses: [],
        statusList: [],
        typeList: [],
        insuredStartDate: '',
        insuredEndDate: '',
        setEmpIdCards: [],
        setEmpNames: [],
        accountNumber: '',
        filterTags: [],
        companyArr: [],
        custId: null,
        operationTypes: [],
        ignoreFlag: 1
      },

      socialFilter: {
        show: false,
        insuredStartDate: '',
        insuredEndDate: '',
        empIdCards: '',
        setEmpIdCards: [],
        searchIdCardsLength: 0,
        empNames: '',
        setEmpNames: [],
        searchNamesLength: 0,
        operationTypes: []
      },
      accfundFilter: {
        show: false,
        insuredStartDate: '',
        insuredEndDate: '',
        empIdCards: '',
        setEmpIdCards: [],
        searchIdCardsLength: 0,
        empNames: '',
        setEmpNames: [],
        searchNamesLength: 0,
        operationTypes: []
      },
      mainLoading: {
        isLoading: false,
        mainLoadingText: '正在导出...'
      },
      isLoadingAccfund: false,
      voidForm: {
        show: false,
        type: null,
        ids: [],
        reason: '',
        btnLoading: false
      },
      socialDetail: {
        show: false,
        row: {},
        tabActive: 0,
        recordData: [],
        tabs: ['社保参保详情', '操作记录']
      },
      accfundDetail: {
        show: false,
        row: {},
        tabActive: 0,
        recordData: [],
        tabs: ['公积金参保详情', '操作记录']
      },
      socialTableData: [],
      accfundTableData: [],
      loading: null,
      row: {},
      bussinessType: '1',
      insuranceList: [],
      callBackSettingShow: false,
      callBackSettingSaveLoading: false,
      callBackSetting: {
        id: null,
        createId: null,
        createTime: null,
        updateId: null,
        updateTime: null,
        cron: '',
        comment: '',
        status: 0
      },
      callBackSettingCopy: {
        id: null,
        createId: null,
        createTime: null,
        updateId: null,
        updateTime: null,
        cron: '',
        comment: '',
        status: 0
      },
      withdrawData: {
        show: false,
        reason: '',
        ids: [],
        type: '',
        type2: ''
      },
      adjustData: {
        show: false,
        addrId: '',
        addrName: '',
        changeUuids: [],
        tplTypeCode: '',
        failReason: '',
        adjustReason: '',
        adjustChecked: '',
        selects: [],
        selects2: [],
        dataType: [],
        status: false
      },
      rules: {
        addrName: [{ required: true, message: '请选择城市', trigger: 'blur' }],
        adjustReason: [{ required: true, message: '请输入调整理由', trigger: 'blur' }],
        failReason: [{ required: true, validator: this.checkFailReason, trigger: 'blur' }],
        tplTypeCode: [{ required: true, message: '请选择申报系统', trigger: 'change' }],
        declareStatus: [{ required: true, message: '请选择状态', trigger: 'change' }],
        adjustChecked: [{ required: true, message: '请确认', trigger: 'change' }],
        dataType: [{ required: true, message: '状态数据', trigger: 'change' }],
        selects: [{ type: 'array', required: true, message: '申报账户', trigger: 'change' }]
      },
      socialVoucher: { // 显示本月参保凭证
        show: false,
        fileIds: ''
      },
      accfundVoucher: { // 显示本月参保凭证
        show: false,
        fileIds: ''
      }
    }
  },
  computed: {
    adjustDataTplTypeCode () {
      return function (code) {
        var arr = []
        this.exportObj.tplTypeArr.forEach(item => {
          if (item.tplType == code) {
            arr = item.items
          }
        })
        arr = arr.map(item => item.itemName)
        return arr.join('、')
      }
    },
    adjustDataType () {
      return function (arr) {
        var index = false
        arr.forEach(item => {
          if (item.indexOf('申报中') > -1 || item.indexOf('部分') > -1) {
            index = true
          }
        })
        return index
      }
    },
    tableHeight: function () {
      return this.$global.bodyHeight - 450 + 'px'
    },
    tableHeight2: function () {
      return this.$global.bodyHeight - 190 + 'px'
    },
    getBusinessStrTime () {
      return function (code) {
        switch (code) {
          case 1:
            return '参保时间'
          case 2:
            return '减员时间'
          case 3:
            return '调基时间'
          case 5:
            return '补缴时间'
        }
        return ''
      }
    },
    getType () {
      return this.bussinessType == '1' ? this.socialDetail : this.accfundDetail
    },
    // 排序表格中的 参保险种字段的 顺序
    getProductNameList () {
      return function (nameList) {
        if (!nameList) {
          return ''
        }
        var arr = []
        var productNameList = nameList.split('|')
        this.getInsuranceList().forEach((item) => {
          productNameList.forEach((item2) => {
            if (item.dictName == item2) {
              arr.push(item2)
            }
          })
        })
        return arr.join('|').trim() || nameList
      }
    },
    // 千分号
    formatNumber (num) {
      return function (num) {
        if (!num) {
          return ''
        }
        num = Number(num).toFixed(2)
        return num
        // return ('' + num).replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,')
      }
    },
    getPickerOption () {
      return function (time, key) {
        var obj = {}
        var self = this
        if (time == 'start') {
          return {
            disabledDate (time) {
              if (!self[key].createEndTime) {
                return false
              }
              let curDate = new Date(self[key].createEndTime).getTime()
              let three = new Date(
                self
                  .$moment(self[key].createEndTime)
                  .subtract(6, 'months')
                  .format('YYYY-MM-DD')
              ).getTime()
              // let threeMonths = three - curDate
              return time.getTime() > curDate || time.getTime() < three
            }
          }
        } else {
          return {
            disabledDate (time) {
              if (!self[key].createStartTime) {
                return false
              }
              let curDate = new Date(self[key].createStartTime).getTime()
              let three = new Date(
                self
                  .$moment(self[key].createStartTime)
                  .add(6, 'months')
                  .format('YYYY-MM-DD')
              ).getTime()
              // let threeMonths = curDate + three
              return (
                time.getTime() <= curDate - 86400000 ||
                time.getTime() > three - 86400000
              )
            }
          }
        }
      }
    },
    getPickerOption2 () {
      return function (time, key) {
        var obj = {}
        var self = this
        if (time == 'start') {
          return {
            disabledDate (time) {
              if (!self[key].insuredEndDate) {
                return false
              }
              let curDate = new Date(self[key].insuredEndDate + '-01').getTime()
              let three = (365 / 2) * 24 * 3600 * 1000
              let threeMonths = curDate - three
              return time.getTime() > curDate
            }
          }
        } else {
          return {
            disabledDate (time) {
              if (!self[key].insuredStartDate) {
                return false
              }
              let curDate = new Date(
                self[key].insuredStartDate + '-01'
              ).getTime()
              let three = (365 / 2) * 24 * 3600 * 1000
              let threeMonths = curDate + three
              return time.getTime() <= curDate - 86400000
            }
          }
        }
      }
    }
  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    if (this.$route.query.businessType === '1') {
      if (this.$route.query.addrId) {
        this.socialSearch.addrId = this.$route.query.addrId
        this.socialSearch.addrName = this.$route.query.addrName
      }
      if (this.$route.query.clearCreateTime === 'true') {
        this.socialSearch.createStartTime = ''
        this.socialSearch.createEndTime = ''
      }
      if (this.$route.query.statusList) {
        this.socialSearch.statusList = this.$route.query.statusList.split(',')
      }
    } else if (this.$route.query.businessType === '2') {
      this.tabActive = 1
      if (this.$route.query.addrId) {
        this.accfundSearch.addrId = this.$route.query.addrId
        this.accfundSearch.addrName = this.$route.query.addrName
      }
      if (this.$route.query.clearCreateTime === 'true') {
        this.accfundSearch.createStartTime = ''
        this.accfundSearch.createEndTime = ''
      }
      if (this.$route.query.statusList) {
        this.accfundSearch.statusList = this.$route.query.statusList.split(',')
      }
    }
    this.getAddrArr()
    this.getCompanyList(1)
    this.getCompanyList(2)
    this.getCompanyList(1, true)
    this.getlistCustomer()
    let that = this
    this.$nextTick(() => {
      that.getCallBackSetting()
      that.getCustomerArr()
    })
    this.getOperationTypes(1)
    this.getOperationTypes(2)
    // this.getDictByKey('1003')
    // this.listSocialProcess()
  },
  watch: {
    'socialSearch.keyword' (newValue, oldValue) {
      newValue = newValue.replace(/[ ]/g, '')
      this.socialSearch.keyword = newValue
    },
    'accfundSearch.keyword' (newValue, oldValue) {
      newValue = newValue.replace(/[ ]/g, '')
      this.accfundSearch.keyword = newValue
    },
    'socialFilter.empIdCards' (newValue, oldValue) {
      newValue = newValue.replace(/[ ]/g, '')
      var obj = this.$global.getInputDataRow(newValue)
      this.socialFilter.searchIdCardsLength = obj.num
      this.socialFilter.setEmpIdCards = obj.value
    },
    'socialFilter.empNames' (newValue, oldValue) {
      newValue = newValue.replace(/[ ]/g, '')
      var obj = this.$global.getInputDataRow(newValue)
      this.socialFilter.searchNamesLength = obj.num
      this.socialFilter.setEmpNames = obj.value
    },
    'accfundFilter.empIdCards' (newValue, oldValue) {
      newValue = newValue.replace(/[ ]/g, '')
      var obj = this.$global.getInputDataRow(newValue)
      this.accfundFilter.searchIdCardsLength = obj.num
      this.accfundFilter.setEmpIdCards = obj.value
    },
    'accfundFilter.empNames' (newValue, oldValue) {
      newValue = newValue.replace(/[ ]/g, '')
      var obj = this.$global.getInputDataRow(newValue)
      this.accfundFilter.searchNamesLength = obj.num
      this.accfundFilter.setEmpNames = obj.value
    }
  },
  methods: {
    changeCust (e) {
      console.log(e)
      if (this.tabActive == 0) {
        this.getCompanyList(1)
      } else {
        this.getCompanyList(2)
      }
    },
    getlistCustomer () {
      this.$http({
        url: '/robot/app/client/listCustomer',
        method: 'post'
      })
        .then((data) => {
          this.socialSearch.customArr = data.data.data
        })
        .catch((err) => { })
    },
    checkFailReason (rule, value, callback) {
      if (this.adjustData.declareStatus == '4' || this.adjustData.declareStatus == '2') {
        return callback()
      }
      if (!value) {
        return callback(new Error('请输入原因备注'))
      }
      return callback()
    },
    getOperationTypes (businessType) {
      let that = this
      let changeType = []
      if (businessType === 1) {
        changeType = this.socialSearch.typeList
      } else {
        changeType = this.accfundSearch.typeList
      }
      this.$http({
        url: '/policy/offerSettings/getOperationTypes',
        method: 'post',
        params: {
          businessType: businessType,
          changeType: changeType.join(',')
        }
      }).then(({ data }) => {
        if (data.code == '200') {
          if (businessType === 1) {
            that.socialOperationTypeArr = data.data
            that.socialSearch.operationTypes = []
            that.socialFilter.operationTypes = []
          } else {
            that.accfundOperationTypeArr = data.data
            that.accfundSearch.operationTypes = []
            that.accfundFilter.operationTypes = []
          }
        }
      }).catch((data) => { })
    },
    getCustomerArr () {
      let that = this
      this.$http({
        url: '/policy/sys/customer/listAllCustomer?filter=&status=',
        method: 'post'
      }).then(({ data }) => {
        if (data.code == '200') {
          that.customerArr = data.data
        }
      }).catch((data) => { })
    },
    openExportDialog () {
      this.changeDeclareType()
      this.exportObj.show = true
      this.$nextTick(() => {
        if (this.bussinessType == 1) {
          this.getExportAddrId({ id: this.socialSearch.addrId })
          this.exportObj.addrName = this.socialSearch.addrName
          this.exportObj.addrId = this.socialSearch.addrId
        } else {
          this.getExportAddrId({ id: this.accfundSearch.addrId })
          this.exportObj.addrName = this.accfundSearch.addrName
          this.exportObj.addrId = this.accfundSearch.addrId
        }
        this.exportObj.dateStr = this.$moment().format('YYYY-MM-DD')
        this.$refs.exportObj.validateField('addrName')
        this.getCompanyList(this.bussinessType, true)
      })
    },
    downloadVoucher () {
      let fileIds = ''
      if (this.bussinessType == 1) {
        fileIds = this.socialVoucher.fileIds
      } else {
        fileIds = this.accfundVoucher.fileIds
      }
      this.$downloadFile(
        '/policy/sys/file/batchDownload/' + fileIds,
        'get',
        {},
        this.$global.ZIP
      )
    },
    changeDeclareType () {
      if (this.exportObj.declareType == null || this.exportObj.declareType.length == 0) {
        this.exportObj.operationTypes = []
        this.exportObj.operationTypeArr = []
      } else {
        let that = this
        this.$http({
          url: '/policy/offerSettings/getOperationTypes',
          method: 'post',
          params: {
            businessType: that.bussinessType,
            changeType: this.exportObj.declareType.join(',')
          }
        }).then(({ data }) => {
          if (data.code == '200') {
            that.exportObj.operationTypeArr = data.data
            let operationTypes = []
            for (var i = 0; i < that.exportObj.declareType.length; i++) {
              let changeType = that.exportObj.declareType[i]
              let arr = data.data.filter(item => {
                return item.changeType == changeType
              })
              if (arr && arr.length > 0) {
                operationTypes.push(arr[0].code)
              }
            }
            that.exportObj.operationTypes = operationTypes
          }
        }).catch((data) => { })
      }
    },
    mergeAddText () {
      let that = this
      if (!that.exportObj.tplTypeCode) {
        that.exportObj.showMergeAddText = ''
        return
      }
      let tplTypes = this.exportObj.tplTypeArr.filter((item) => {
        return item.tplType == that.exportObj.tplTypeCode
      })
      if (tplTypes && tplTypes.length > 0 && tplTypes[0].merge) {
        that.exportObj.showMergeAddText = '增补合并申报【是】'
      } else {
        that.exportObj.showMergeAddText = '增补合并申报【否】'
      }
    },
    getCallBackSetting () {
      let that = this
      this.$http({
        url: '/policy/declareChangeCallBack/callBackSetting',
        method: 'post'
      }).then(({ data }) => {
        if (data.code == '200') {
          that.callBackSetting = data.data
          that.callBackSettingCopy = this.$global.deepcopyArray([that.callBackSetting])[0]
        }
      }).catch((data) => { })
    },
    clearCallBackSettingForm () {
      this.callBackSettingShow = false
      this.callBackSettingCopy = this.$global.deepcopyArray([this.callBackSetting])[0]
      this.$refs.callBackSettingCopy.resetFields()
    },
    vaildCallBackSettingForm () {
      var that = this
      this.$refs.callBackSettingCopy.validate((valid) => {
        if (valid) {
          that.callBackSettingSaveLoading = true
          var formData = this.callBackSettingCopy
          that.$http({
            url: '/policy/declareChangeCallBack/addOrUpdate',
            method: 'POST',
            data: formData
          }).then(({ data }) => {
            that.callBackSettingSaveLoading = false
            that.$message.success('操作成功')
            that.clearCallBackSettingForm()
            that.getCallBackSetting()
          }).catch(() => {
            that.callBackSettingSaveLoading = false
          })
        }
      })
    },
    getTplType (addrId) {
      let that = this
      if (!addrId) {
        that.exportObj.tplTypeArr = []
        return
      }
      this.$http({
        url: '/policy/offerSettings/findAddrTplItems/' + addrId + '/' + that.bussinessType,
        method: 'post'
      }).then(({ data }) => {
        var resData = data.data
        if (resData && resData.tplItems && resData.tplItems.length > 0) {
          that.exportObj.tplTypeArr = resData.tplItems
          if (resData.tplItems.length == 1) {
            that.exportObj.tplTypeCode = resData.tplItems[0].tplType
            that.adjustData.tplTypeCode = resData.tplItems[0].tplType
            that.mergeAddText()
          }
        } else {
          that.exportObj.tplTypeArr = []
          that.exportObj.tplTypeCode = ''
        }
      })
        .catch((data) => { })
    },
    handlePreviewOk (item, index, inIndex) {
      this.$http({
        url: `api/policy/sys/file/download/${item.id}`,
        responseType: 'blob',
        baseURL: ''
      }).then((res) => {
        console.log('es.status', res)
        if (res.status === 200) {
          const blob = new Blob([res.data])
          const a = document.createElement('a')
          const URL = window.URL || window.webkitURL
          const herf = URL.createObjectURL(blob)
          a.href = herf
          a.download = item.clientFileName
          document.body.appendChild(a)
          a.click()
          document.body.removeChild(a)
          window.URL.revokeObjectURL(herf)
        }
      })
    },
    // showUploadFiles(index, itemVal) {
    //   // this.listOfIndex = index
    //   // this.rowFileData = itemVal
    // },
    // UploadUrl() {
    //   const baseUrl = window.location.origin
    //   console.log('baseUrl', baseUrl)
    //   return baseUrl
    // },
    // 获取参保地
    getAddrArr () {
      this.$http({
        url: '/policy/declareAddr/addrList',
        method: 'post'
      })
        .then(({ data }) => {
          this.addrArr = data.data
        })
        .catch((data) => { })
    },
    // 获取参保主体
    getCompanyList (type, isExport) {
      let that = this
      let addressId = ''; let custId = null
      if (!isExport) {
        if (type == 1) {
          addressId = that.socialSearch.addrId
        } else {
          addressId = that.accfundSearch.addrId
        }
      } else {
        addressId = that.exportObj.addrId
        custId = that.exportObj.custId
        if (!addressId) {
          that.exportObj.companyArr = []
          that.exportObj.accountNumber = []
          return false
        }
      }

      this.$http({
        url: '/policy/declareAccount/seeMainBody',
        method: 'post',
        params: {
          bussinessType: type,
          addressId: addressId,
          custLimit: 2,
          custId: custId || this.tabActive == 0 ? this.socialSearch.custId : this.accfundSearch.custId
        }
      })
        .then(({ data }) => {
          if (!isExport) {
            if (type == 1) {
              that.socialSearch.companyArr = data.data
              that.socialSearch.company = ''
              that.socialSearch.accountNumber = ''
              if (that.$route.query.businessType === '1' && that.$route.query.accountNumber) {
                for (var i = 0; i < data.data.length; i++) {
                  if (that.$route.query.accountNumber === data.data[i].accountNumber) {
                    that.socialSearch.itemId = data.data[i].itemId
                    that.changeCompany(1)
                    that.getSocialTable()
                    break
                  }
                }
              }
            } else {
              that.accfundSearch.companyArr = data.data
              that.accfundSearch.company = ''
              that.accfundSearch.accountNumber = ''
              if (that.$route.query.businessType === '2' && that.$route.query.accountNumber) {
                for (var i = 0; i < data.data.length; i++) {
                  if (that.$route.query.accountNumber === data.data[i].accountNumber) {
                    that.accfundSearch.itemId = data.data[i].itemId
                    that.changeCompany(2)
                    that.getAccfundTable()
                    that.changeTab(1)
                    break
                  }
                }
              }
            }
          } else {
            that.exportObj.companyArr = data.data
            that.$nextTick(() => {
              if (data.data && data.data.length == 1) {
                that.exportObj.accountNumbers = [data.data[0].accountNumber]
              } else {
                that.exportObj.accountNumbers = []
              }
              if (that.exportObj.show) {
                var accountNumber = that.bussinessType == 1 ? that.socialSearch.accountNumber : that.accfundSearch.accountNumber
                let arr = data.data.filter(item => {
                  if (item.accountNumber === null || item.accountNumber === '') {
                    return false
                  }
                  return item.accountNumber == accountNumber
                })
                if (arr.length <= 0) {
                  that.exportObj.accountNumbers = []
                  return
                }
                if (that.bussinessType == 1) {
                  that.exportObj.accountNumbers = [that.socialSearch.accountNumber]
                } else {
                  that.exportObj.accountNumbers = [that.accfundSearch.accountNumber]
                }
              }

              console.log(that.exportObj)
            })
          }
        })
        .catch((data) => { })
    },
    // 改变社保-参保地
    getSocialAddrId (item) {
      if (this.socialSearch.addrId == item.id) {
        return false
      }
      this.socialSearch.addrId = item.id
      this.socialSearch.itemId = ''
      this.getCompanyList(1)
    },
    changeCompany (type) {
      if (type == 1) {
        if (!this.socialSearch.itemId) {
          this.socialSearch.accountNumber = ''
          this.socialSearch.company = ''
        } else {
          var items = this.socialSearch.companyArr
            .filter((item) => {
              return item.itemId == this.socialSearch.itemId
            })
            .map((item) => {
              return item
            })
          if (items && items.length > 0) {
            this.socialSearch.accountNumber = items[0].accountNumber
            this.socialSearch.company = items[0].id
          }
        }
      } else {
        if (!this.accfundSearch.itemId) {
          this.accfundSearch.accountNumber = ''
          this.accfundSearch.company = ''
        } else {
          var items = this.accfundSearch.companyArr
            .filter((item) => {
              return item.itemId == this.accfundSearch.itemId
            })
            .map((item) => {
              return item
            })
          if (items && items.length > 0) {
            this.accfundSearch.accountNumber = items[0].accountNumber
            this.accfundSearch.company = items[0].id
          }
        }
      }
    },
    // 改变公积金-参保地
    getAccfundAddrId (item) {
      if (this.accfundSearch.addrId == item.id) {
        return false
      }
      this.accfundSearch.addrId = item.id
      this.accfundSearch.itemId = ''
      this.getCompanyList(2)
    },

    // 改变公积金-参保地
    getExportAddrId (item) {
      this.$refs.exportObj.validateField('addrName')
      if (this.exportObj.addrId == item.id) {
        return false
      }
      this.exportObj.addrId = item.id
      this.exportObj.itemId = ''
      this.getCompanyList(this.bussinessType, true)
      this.getTplType(item.id)
      this.getDeclareRange(this.bussinessType, item.id)
    },
    getDeclareRange (businessType, addrId) {
      let that = this
      if (!addrId) {
        that.exportObj.declareRange = []
        return
      }
      this.$http({
        url: 'policy/declareAddr/declarePolicyQuery',
        method: 'post',
        data: {
          addrId: Number(addrId),
          businessType: Number(businessType)
        }
      }).then(({ data }) => {
        var result = data.data
        if (result.policyAddrDeadlineSettingList.length > 0) {
          let arr = []
          result.policyAddrDeadlineSettingList.forEach((item) => {
            if (item.id) {
              let declareType = item.declareType == 1 ? '增员' : (item.declareType == 2 ? '减员' : (item.declareType == 3 ? '调基' : '补缴'))
              let monthBegin = item.monthBegin == 0 ? '当月' : (item.monthBegin == 1 ? '下月' : '上月')
              let dayBegin = item.dayBegin + '日'
              let monthEnd = item.monthEnd == 0 ? '当月' : (item.monthEnd == 1 ? '下月' : '上月')
              let dayEnd = item.dayEnd + '日'
              arr.push(declareType + ' ：' + monthBegin + dayBegin + '~' + monthEnd + dayEnd)
            }
          })
          that.exportObj.declareRange = arr
        } else {
          that.exportObj.declareRange = []
        }
      })
        .catch((data) => { })
    },

    // 改变tab
    changeTab (index) {
      if (!this.isLoadingAccfund && index == 1) {
        this.isLoadingAccfund = true
      }
      this.bussinessType = index + 1
    },

    // 处理显示【本月参保凭证】
    handleShowVoucher (search) {
      let that = this
      let voucher = null
      if (that.bussinessType == 1) {
        voucher = that.socialVoucher
      } else {
        voucher = that.accfundVoucher
      }
      voucher.show = false
      voucher.fileIds = []
      // 必填：参保城市，业务类型（社保、公积金）、申报账户、管理端加客户。（申报类型、创建时间 非必填）
      if (!search.addrName || !search.accountNumber || !search.custId) {
        return
      }
      this.$http({
        url: '/robot/voucher/hasVoucher',
        method: 'post',
        data: {
          clientId: (search.custId == '' ? null : search.custId),
          addrName: search.addrName,
          businessType: Number(that.bussinessType),
          accountNumber: search.accountNumber,
          declareTypes: search.typeList,
          startMonth: search.createStartTime,
          endMonth: search.createEndTime
        }
      }).then(({ data }) => {
        let res = data.data
        voucher.show = res.length > 0
        let fileIds = []
        res.map(item => {
          fileIds.push(item.fileId)
        })
        voucher.fileIds = fileIds.join(',')
      }).catch((data) => {
      })
    },

    // S=社保
    getSocialTable () {
      let that = this
      this.socialFilter.show = false
      this.fetchCallBackSocial()
      let search = this.socialSearch
      if (!search.addrName) {
        this.$message.warning('请先选择参保城市')
        return
      }
      this.handleShowVoucher(search) // 处理显示【本月参保凭证】
      var params = [
        { property: 'addrId', value: search.addrId },
        { property: 'id', value: search.company },
        { property: 'accountNumber', value: search.accountNumber },
        { property: 'createStartTime', value: search.createStartTime },
        { property: 'createEndTime', value: search.createEndTime },
        { property: 'keyword', value: search.keyword },
        { property: 'fileStatuses', value: search.fileStatuses },
        { property: 'insuredStartDate', value: search.insuredStartDate },
        { property: 'insuredEndDate', value: search.insuredEndDate },
        { property: 'operationTypes', value: search.operationTypes },
        { property: 'customerId', value: (search.custId == '' ? null : search.custId) }
      ]
      if (search.statusList.length > 0) {
        var index = search.statusList.indexOf('3')
        if (search.statusList.indexOf('5') > -1 && index <= -1) {
          search.statusList.push('3')
          params.push({ property: 'ignoreFlag', value: search.ignoreFlag })
        } else if (search.statusList.indexOf('5') <= -1 && index > -1) {
          search.statusList.splice(index, 1)
        }
        params.push({ property: 'status', value: search.statusList })
      }
      if (search.typeList.length > 0) {
        params.push({ property: 'changeType', value: search.typeList })
      }
      if (search.setEmpIdCards.length > 0) {
        params.push({ property: 'idCard', value: search.setEmpIdCards })
      }
      if (search.setEmpNames.length > 0) {
        params.push({ property: 'employeeName', value: search.setEmpNames })
      }
      this.$refs.socialTable.fetch({
        query: params,
        method: 'post',
        url: '/agent/declareChange/ListEmployeeSocialInsured'
      })
    },
    fetchCallBackSocial (res) {
      let filterTags = []
      let search = this.socialSearch
      var insuredDateText = ''
      if (search.insuredStartDate) {
        insuredDateText = search.insuredStartDate + '~'
      }
      if (search.insuredEndDate) {
        insuredDateText = (search.insuredStartDate ? insuredDateText : '~') + search.insuredEndDate
      }
      if (insuredDateText !== '') {
        filterTags.push({
          label: '参保时间：',
          text: insuredDateText,
          formCode: 'insuredDate'
        })
      }

      for (let i = 0; i < search.setEmpIdCards.length; i++) {
        filterTags.push({
          label: '',
          text: search.setEmpIdCards[i],
          formCode: 'idCard',
          index: i
        })
      }

      for (let i = 0; i < search.setEmpNames.length; i++) {
        filterTags.push({
          label: '',
          text: search.setEmpNames[i],
          formCode: 'name',
          index: i
        })
      }

      search.filterTags = filterTags
    },

    // 搜索框-确定
    confirmFilterSocial () {
      this.socialSearch.insuredStartDate = this.socialFilter.insuredStartDate
      this.socialSearch.insuredEndDate = this.socialFilter.insuredEndDate
      this.socialSearch.setEmpIdCards = [...this.socialFilter.setEmpIdCards]
      this.socialSearch.setEmpNames = [...this.socialFilter.setEmpNames]
      this.socialSearch.operationTypes = [...this.socialFilter.operationTypes]
      this.getSocialTable()
    },

    // 搜索框-重置
    resetFilterSocial () {
      this.socialFilter.insuredStartDate = ''
      this.socialFilter.insuredEndDate = ''
      this.socialFilter.empIdCards = ''
      this.socialFilter.setEmpIdCards = []
      this.socialFilter.searchIdCardsLength = 0
      this.socialFilter.empNames = ''
      this.socialFilter.setEmpNames = []
      this.socialFilter.searchNamesLength = 0
      this.socialFilter.operationTypes = []
    },

    // 表格头部标签查询条件-移除、重置
    resetSocial (index, filterTags) {
      if (this.$global.isNotBlank(index)) {
        // 单个移除
        this.resetItemSocial(this.socialSearch.filterTags[index])
      } else {
        //  重置
        this.socialSearch.insuredStartDate = ''
        this.socialSearch.insuredEndDate = ''
        this.socialSearch.setEmpIdCards = []
        this.socialSearch.setEmpNames = []
      }
      this.socialSearch.filterTags = filterTags
      this.getSocialTable()
    },

    resetItemSocial (tag) {
      let code = tag.formCode
      if (code == 'insuredDate') {
        this.socialSearch.insuredStartDate = ''
        this.socialSearch.insuredEndDate = ''
      } else if (code == 'idCard') {
        this.socialSearch.setEmpIdCards.splice(tag.index, 1)
      } else if (code == 'name') {
        this.socialSearch.setEmpNames.splice(tag.index, 1)
      }
    },
    // E=社保

    // S=公积金
    getAccfundTable () {
      let that = this
      this.accfundFilter.show = false
      this.fetchCallBackAccfund()
      let search = this.accfundSearch
      if (!search.addrName) {
        this.$message.warning('请先选择参保城市')
        return
      }
      this.handleShowVoucher(search) // 处理显示【本月参保凭证】
      var params = [
        { property: 'addrId', value: search.addrId },
        { property: 'id', value: search.company },
        { property: 'accountNumber', value: search.accountNumber },
        // { property: 'insuredDate', value: search.startMonth },
        { property: 'createStartTime', value: search.createStartTime },
        { property: 'createEndTime', value: search.createEndTime },
        { property: 'keyword', value: search.keyword },
        { property: 'fileStatuses', value: search.fileStatuses },
        { property: 'insuredStartDate', value: search.insuredStartDate },
        { property: 'insuredEndDate', value: search.insuredEndDate },
        { property: 'operationTypes', value: search.operationTypes },
        { property: 'customerId', value: (search.custId == '' ? null : search.custId) }
      ]
      if (search.statusList.length > 0) {
        var index = search.statusList.indexOf('3')
        params.push({ property: 'ignoreFlag', value: search.ignoreFlag })
        if (search.statusList.indexOf('5') > -1 && index <= -1) {
          search.statusList.push('3')
        } else if (search.statusList.indexOf('5') <= -1 && index > -1) {
          search.statusList.splice(index, 1)
        }
        params.push({ property: 'status', value: search.statusList })
      }
      if (search.typeList.length > 0) {
        params.push({ property: 'changeType', value: search.typeList })
      }
      if (search.setEmpIdCards.length > 0) {
        params.push({ property: 'idCard', value: search.setEmpIdCards })
      }
      if (search.setEmpNames.length > 0) {
        params.push({ property: 'employeeName', value: search.setEmpNames })
      }
      this.$refs.accfundTable.fetch({
        query: params,
        method: 'post',
        url: '/agent/declareChange/listEmpNewAccfund'
      })
    },
    fetchCallBackAccfund (res) {
      let filterTags = []
      let search = this.accfundSearch
      var insuredDateText = ''
      if (search.insuredStartDate) {
        insuredDateText = search.insuredStartDate + '~'
      }
      if (search.insuredEndDate) {
        insuredDateText = (search.insuredStartDate ? insuredDateText : '~') + search.insuredEndDate
      }
      if (insuredDateText !== '') {
        filterTags.push({
          label: '参保时间：',
          text: insuredDateText,
          formCode: 'insuredDate'
        })
      }

      for (let i = 0; i < search.setEmpIdCards.length; i++) {
        filterTags.push({
          label: '',
          text: search.setEmpIdCards[i],
          formCode: 'idCard',
          index: i
        })
      }

      for (let i = 0; i < search.setEmpNames.length; i++) {
        filterTags.push({
          label: '',
          text: search.setEmpNames[i],
          formCode: 'name',
          index: i
        })
      }

      search.filterTags = filterTags
    },

    // 搜索框-确定
    confirmFilterAccfund () {
      this.accfundSearch.insuredStartDate = this.accfundFilter.insuredStartDate
      this.accfundSearch.insuredEndDate = this.accfundFilter.insuredEndDate
      this.accfundSearch.setEmpIdCards = [...this.accfundFilter.setEmpIdCards]
      this.accfundSearch.setEmpNames = [...this.accfundFilter.setEmpNames]
      this.accfundSearch.operationTypes = [...this.accfundFilter.operationTypes]
      this.getAccfundTable()
    },

    // 搜索框-重置
    resetFilterAccfund () {
      this.accfundFilter.insuredStartDate = ''
      this.accfundFilter.insuredEndDate = ''
      this.accfundFilter.empIdCards = ''
      this.accfundFilter.setEmpIdCards = []
      this.accfundFilter.searchIdCardsLength = 0
      this.accfundFilter.empNames = ''
      this.accfundFilter.setEmpNames = []
      this.accfundFilter.searchNamesLength = 0
      this.accfundFilter.operationTypes = []
    },

    // 表格头部标签查询条件-移除、重置
    resetAccfund (index, filterTags) {
      if (this.$global.isNotBlank(index)) {
        // 单个移除
        this.resetItemAccfund(this.accfundSearch.filterTags[index])
      } else {
        //  重置
        this.accfundSearch.insuredStartDate = ''
        this.accfundSearch.insuredEndDate = ''
        this.accfundSearch.setEmpIdCards = []
        this.accfundSearch.setEmpNames = []
      }
      this.accfundSearch.filterTags = filterTags
      this.getAccfundTable()
    },

    resetItemAccfund (tag) {
      let code = tag.formCode
      if (code == 'insuredDate') {
        this.accfundSearch.insuredStartDate = ''
        this.accfundSearch.insuredEndDate = ''
      } else if (code == 'idCard') {
        this.accfundSearch.setEmpIdCards.splice(tag.index, 1)
      } else if (code == 'name') {
        this.accfundSearch.setEmpNames.splice(tag.index, 1)
      }
    },
    // E=公积金

    // 导出社保选据
    exportSocialTableData () {
      var params = this.$refs.socialTable.getParamsQuery()
      var uuids = this.$refs.socialTable.getSelectionsArr('uuid')
      if (!params) {
        this.$message.warning('请先查询数据')
        return
      }
      var params2 = this.$global.deepcopyArray(params)
      params2.push({ property: 'uuid', value: uuids })
      this.$downloadFile(
        '/agent/declareChange/downloadSocial',
        'post',
        {
          query: params2
        },
        this.$global.EXCEL
      )
    },
    // 导出公积金选据
    exportAccfundTableData () {
      let params = this.$refs.accfundTable.getParamsQuery()
      let ids = this.$refs.accfundTable.getSelectionsArr('id')
      if (!params) {
        this.$message.warning('请先查询数据')
        return
      }
      var params2 = this.$global.deepcopyArray(params)
      params2.push({ property: 'accfundId', value: ids })
      this.$downloadFile(
        '/agent/declareChange/downloadAccount',
        'post',
        {
          query: params2
        },
        this.$global.EXCEL
      )
    },
    // 查看详情
    viewRequest (type, row) {
      this.row = row
      let that = this
      this.$nextTick(() => {
        this.$refs.myDrawer.request(type, row)
      })
    },
    // 获取详情 参保状态
    getDetailItemBaseList (row, type) {
      this.$http({
        url: '/agent/declareChange/getDetailItemBaseList',
        method: 'post',
        data: {
          itemId: row.itemId
        }
      }).then(({ data }) => {
        // console.log(data)
        if (type == 'social') {
          this.socialTableData = data.data
        } else {
          this.accfundTable = data.data
        }
      })
    },
    // 作废
    handleVoidSocial () {
      this.voidForm.type = 'social'
      let selects = this.$refs.socialTable.selections
      if (selects.length == 0) {
        this.$message.warning('请先选择需要操作的数据')
        return
      }
      let ids = []
      //  1、	只有申报状态为：待申报(1)，可以作废，其它状态不允许作废
      for (let i = 0; i < selects.length; i++) {
        if (selects[i].status == 1) {
          ids.push({
            itemId: selects[i].itemId,
            sameStatusName: selects[i].sameStatusName
          })
        } else {
          this.$message.warning('只有待申报的数据可以作废')
          return
        }
      }
      this.voidForm.ids = ids
      this.voidForm.show = true
    },
    // 作废
    handleVoidAccfund () {
      this.voidForm.type = 'accfund'
      let selects = this.$refs.accfundTable.selections
      if (selects.length == 0) {
        this.$message.warning('请先选择需要操作的数据')
        return
      }
      let ids = []
      //  1、	只有申报状态为：待申报(1)，可以作废，其它状态不允许作废
      for (let i = 0; i < selects.length; i++) {
        if (selects[i].declareStatus == 1) {
          ids.push(selects[i].id)
        } else {
          this.$message.warning('只有待申报的数据可以作废')
          return
        }
      }
      this.voidForm.ids = ids
      this.voidForm.show = true
    },
    cancelExport () {
      this.$refs.exportObj.resetFields()
      this.exportObj.show = false
      this.exportObj.exportLoading = false
      this.exportObj.addrName = ''
      this.exportObj.addrId = null
      this.exportObj.custId = null
      this.exportObj.accountNumber = ''
      this.exportObj.accountNumbers = []
      this.exportObj.companyArr = []
      this.exportObj.tplTypeArr = []
      this.exportObj.tplTypeCode = ''
      this.exportObj.statusList = ['1', '2']
      this.exportObj.tbDate = ''
      this.exportObj.declareType = ['1']
      this.exportObj.showMergeAddText = ''
      this.exportObj.declareRange = []
      this.$refs.exportObj.resetFields()
    },
    exportSumbit () {
      var that = this
      this.$refs.exportObj.validate((valid) => {
        if (valid) {
          that.exportObj.exportLoading = true
          var formData = this.exportObj
          that.$downloadFile(
            '/agent/employeeDeclare/declareOfferExportPage',
            'post',
            {
              addrName: formData.addrName,
              businessType: that.bussinessType,
              declareTypes: formData.declareType.join(','),
              accountNumbers: formData.accountNumbers.join(','),
              tplTypeCode: formData.tplTypeCode,
              declareStatus: formData.statusList.join(','),
              operationTypes: formData.operationTypes.join(','),
              dateStr: formData.dateStr
            },
            this.$global.EXCEL,
            null,
            function () {
              that.cancelExport()
            }
          )
        }
      })
    },
    cancelVoid () {
      this.$refs.voidForm.resetFields()
      this.voidForm.type = null
      this.voidForm.ids = []
      this.voidForm.reason = ''
      this.voidForm.show = false
      this.voidForm.btnLoading = false
    },
    voidSumbit () {
      this.voidForm.btnLoading = true
      this.$refs.voidForm.validate((valid) => {
        if (valid) {
          let that = this
          let url = ''
          let setObj
          if (this.voidForm.type == 'social') {
            url =
              '/agent/declareChange/editSocialSataus?comment=' +
              that.voidForm.reason
            setObj = that.voidForm.ids
          } else if (this.voidForm.type == 'accfund') {
            url = '/agent/declareChange/editAccountStatus'
            setObj = {
              comment: that.voidForm.reason,
              id: that.voidForm.ids
            }
          }
          this.$http({
            url: url,
            method: 'post',
            data: setObj
          })
            .then(({ data }) => {
              if (that.voidForm.type == 'social') {
                that.getSocialTable()
              } else if (that.voidForm.type == 'accfund') {
                that.getAccfundTable()
              }
              that.cancelVoid()
            })
            .catch((data) => {
              that.voidForm.btnLoading = false
            })
        } else {
          this.voidForm.btnLoading = false
        }
      })
    },
    handleDeclareStatus (code) {
      //  <!--申报状态（0作废，1待申报，2申报中，4申报成功，5申报失败,3部分失败）-->
      switch (code) {
        case 0:
          return '作废'
        case 1:
          return '待申报'
        case 2:
          return '申报中'
        case 4:
          return '申报成功'
        case 5:
          return '申报失败'
        case 6:
          return '待提交'
        case 3:
          return '申报失败（部分）'
        case 7:
          return '申报成功（部分）'
      }
      return ''
    },
    handleChangeType (code) {
      //  <!--变更类型（1增，2减，3调基，5补缴）-->
      // {id: '1', name: '投保'}, {id: '3', name: '调整'}, {id: '2', name: '停保'}, {id: '5', name: '补缴'}
      switch (code) {
        case 1:
          return '增员'
        case 2:
          return '减员'
        case 3:
          return '调基'
        case 5:
          return '补缴'
      }
      return ''
    },
    handleAccfundRatios (val) {
      if (val === '' || val == null || val == undefined) {
        return ''
      }
      return val.replace(/<br\/>/g, '，')
    },
    // 跳转交互数据页面
    goInteractiveDataPage () {
      this.$router.push('/socialAccfund/interactiveData')
    },
    listSocialProcess (item) {
      this.$http({
        url:
          '/agent/declareChange/listSocialProcess?uuid=' +
          '83e5b683-77b7-47f0-82c6-4efc827d97ac',
        method: 'post'
      }).then((data) => {
        console.log(data)
      })
    },
    getRecord (row, type) {
      var url = ''
      if (type == 'social') {
        url = '/agent/declareChange/listSocialProcess?uuid=' + row.uuid
      } else {
        url = '/agent/declareChange/listAccfundProcess?uuid=' + row.uuid
      }
      this.$http({
        url: url,
        method: 'post'
      })
        .then(({ data }) => {
          if (type == 'social') {
            this.socialDetail.recordData = data.data
          } else {
            this.accfundDetail.recordData = data.data
          }
        })
        .catch((item) => { })
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
    // 获取险种字典，用与参保险种，按返回数据排序
    async getDictByKey () {
      return this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: '10003'
        }
      })
        .then((res) => {
          this.insuranceList = res.data.data
        })
        .catch((err) => {
          this.$message.error('字典接口出错，请稍后再试')
        })
    },
    // 撤回
    withdraw (type, type2, row) {
      this.withdrawData.type = type
      this.withdrawData.type2 = type2
      this.withdrawData.row = row
      if (type2 == 'single') {
        this.withdrawData.show = true
        if (this.bussinessType == '1') {
          this.withdrawData.ids = [{
            id: row.id,
            itemIds: this.withdrawData.type == 'social' ? row.itemId : '',
            uuid: row.uuid
          }]
        } else {
          this.withdrawData.ids = [{
            id: row.id,
            uuid: row.uuid
          }]
        }

        return
      }
      let selects = this.$refs[type + 'Table'].selections
      if (selects.length == 0) {
        this.$message.warning('请先选择需要操作的数据')
        return
      }
      let ids = []
      //  1、	只有申报状态为：待申报(1)，待提交(6)，可以撤回，其它状态不允许撤回
      for (let i = 0; i < selects.length; i++) {
        if (selects[i].status == 1 || selects[i].status == 6) {
          if (this.bussinessType == '1') {
            ids.push({
              id: selects[i].id,
              itemIds: this.withdrawData.type == 'social' ? selects[i].itemId : '',
              uuid: selects[i].uuid
            })
          } else {
            ids.push({
              id: selects[i].id,
              uuid: selects[i].uuid
            })
          }
        } else {
          this.$message.warning('只有待提交和待申报的数据可以作废')
          return
        }
      }
      this.withdrawData.ids = ids
      this.withdrawData.show = true
    },
    // 取消撤回
    cancelWithdraw () {
      this.withdrawData = {
        show: false,
        reason: '',
        ids: [],
        type: '',
        type2: ''
      }
      this.withdrawData.show = false
      this.$nextTick(() => {
        this.$refs.withdrawForm.clearValidate()
      })
    },
    withdrawSumbit () {
      this.$refs.withdrawForm.validate((valid) => {
        if (valid) {
          var obj = {
            businessType: this.bussinessType,
            remark: this.withdrawData.reason,
            items: this.withdrawData.ids
          }
          this.showLoading()
          this.$http({
            url: '/agent/declareChange/employeeInsuredChange',
            method: 'post',
            data: obj
          })
            .then((res) => {
              this.closeLoading()
              if (res.data.code == 200) {
                this.$message.success(
                  res.data.message ? res.data.message : '操作成功'
                )
                if (this.bussinessType == '1') {
                  this.getSocialTable()
                } else {
                  this.getAccfundTable()
                }
                this.cancelWithdraw()
              }
            })
            .catch((err) => {
              this.closeLoading()
            })
        }
      })
    },
    handleCauseTip (text) {
      return text.replace(/\n/g, '<br/>')
    },
    // 移除非换行符以外的空格
    replaceBlank (formKey, fieldKey) {
      this[formKey][fieldKey] = this[formKey][fieldKey].replace(/[^\S\r\n]+/g, '')
    },
    // 查看详情中的 操作记录 - 执行查询 点击回调 需要跳转到执行查询页面对应的记录
    onDoQuery (row) {
      console.log('onDoQuery-------', row)
      this.$router.push({
        path: '/customerAppList/customerIndex',
        query: {
          appCode: row.appCode,
          taskCode: row.taskCode,
          clientId: row.clientId,
          machineCode: row.machineCode,
          startTime: row.startTime,
          endTime: row.endTime
        }
      })
    },
    changeAdjust () {
      let selects = []
      if (this.bussinessType == 1) {
        selects = this.$refs.socialTable.selections
        this.adjustData.addrName = this.socialSearch.addrName
        this.adjustData.addrId = this.socialSearch.addrId
      } else {
        selects = this.$refs.accfundTable.selections
        this.adjustData.addrName = this.accfundSearch.addrName
        this.adjustData.addrId = this.accfundSearch.addrId
      }
      if (!this.adjustData.addrName) {
        this.$message.warning('请选择城市')
        return
      }
      if (selects.length <= 0) {
        this.$message.warning('请先勾选数据')
        return
      }
      var obj = {}
      this.adjustData.selects = []
      this.adjustData.selects2 = []
      selects.forEach(item => {
        this.adjustData.selects2.push(item)
        if (obj[item.orgName]) {
          return
        }
        obj[item.orgName] = '1'
        this.adjustData.selects.push(item)
      })
      var obj2 = {}
      selects.forEach(item => {
        if (obj2[this.handleDeclareStatus(item.status)]) {
          obj2[this.handleDeclareStatus(item.status)] += 1
        } else {
          obj2[this.handleDeclareStatus(item.status)] = 1
        }
      })
      this.adjustData.dataType = []
      this.adjustData.status = false
      for (const key in obj2) {
        this.adjustData.dataType.push(`${obj2[key]}条${key}`)
      }
      if (this.bussinessType == 1) {
        var statusList = selects.filter(item => {
          var list = item.base.map(item1 => item1.status)
          return list.indexOf(2) > -1
        })
        if (statusList.length > 0) {
          this.adjustData.status = true
        }
      } else {
        var statusList = selects.filter(item => {
          return item.status == 2 || item.suppDeclareStatus == 2
        })
        if (statusList.length > 0) {
          this.adjustData.status = true
        }
      }

      this.getTplType(this.adjustData.addrId)
      this.adjustData.show = true
    },
    cancelAdjustData () {
      this.adjustData = {
        show: false,
        addrId: '',
        addrName: '',
        changeUuids: [],
        tplTypeCode: '',
        failReason: '',
        adjustReason: '',
        adjustChecked: '',
        selects: [],
        selects2: [],
        dataType: [],
        status: false
      }
      this.$nextTick(() => {
        this.$refs.adjustData.clearValidate()
      })
    },
    confirmAdjustData () {
      this.$refs.adjustData.validate(valid => {
        if (valid) {
          var formData = this.adjustData
          formData.changeUuids = this.adjustData.selects2.map(item => {
            return item.uuid
          })
          formData.businessType = this.bussinessType
          this.showLoading()
          this.$http({
            url: '/agent/declareChange/adjustDeclareStatus',
            method: 'post',
            data: formData
          })
            .then((res) => {
              this.closeLoading()
              if (res.data.code == 200) {
                this.$message.success(
                  res.data.message ? res.data.message : '操作成功'
                )
                if (this.bussinessType == '1') {
                  this.getSocialTable()
                } else {
                  this.getAccfundTable()
                }
                this.cancelAdjustData()
              }
            })
            .catch((err) => {
              this.closeLoading()
            })
        }
      })
    },
    handleDownLoadFile (fileID) {
      this.$downloadFile(`/policy/sys/file/download/${fileID}`, 'get')
    },
    // 执行轨迹
    viewRunTrack (type, row) {
      let failCause = row.failCause !== row.suppFailCause ? this.handleCauseContent(row) : row.failCause
      let failReason = type === '1' ? row.failCase : failCause
      this.$refs.runTrack.init(type, row, { failReason: failReason })
    },
    handleCauseContent (row) {
      return (row.failCause == null ? '' : row.failCause) + (row.suppFailCause == null ? '' : (row.failCause == null || row.failCause === '' ? '' : ';') + row.suppFailCause)
    },
    getRunInfo (status) {
      // 1排队中 el-icon-time text-blue
      // 2离线 el-icon-circle-close text-red
      // 3暂停 el-icon-video-play text-orange
      // 4执行中 el-icon-video-pause text-green
      // 5审核 el-icon-s-check text-yellow
      // 6已完成 el-icon-circle-check text-green
      // 7执行中断 el-icon-remove-outline text-red
      // 8未到申报期 el-icon-date text-blue
      let obj = {
        '1': { text: '排队中', icon: 'el-icon-time text-blue' },
        '2': { text: '离线', icon: 'el-icon-circle-close text-red' },
        '3': { text: '暂停', icon: 'el-icon-video-play text-orange' },
        '4': { text: '执行中', icon: 'el-icon-video-pause text-green' },
        '5': { text: '审核', icon: 'el-icon-s-check text-yellow' },
        '6': { text: '已完成', icon: 'el-icon-circle-check text-green' },
        '7': { text: '执行中断', icon: 'el-icon-remove-outline text-red' },
        '8': { text: '未到申报期', icon: 'el-icon-date text-blue' }
      }
      return obj[status]
    }
  }
}
</script>

<style scoped lang="less">
.search-row {
  .label {
    width: 90px;
    text-align: right;
  }
}

/deep/.sbStatusCeckbox {
  .el-checkbox-button__inner {
    padding-left: 5px;
    padding-right: 5px;
    width: 75px;
  }
}

.search-row-item {
  width: 275px;
}

.content {
  .handle-btn {
    height: 50px;
    background: #f2f2f2;
    line-height: 50px;
  }

  .search-l {
    width: 25%;
    max-width: 750px;
    min-width: 600px;
  }

  .search-l-input {
    min-width: 370px;
  }

  /deep/ .el-table {
    margin-top: 0;
  }
}

.social-drawer-content {
  /deep/ .pal-pagination {
    border-top: none;
  }
}

/deep/ .filter-checkbox {
  margin-top: 20px;
  width: 120px;
}

.emp-item {
  flex: 1;

  .title {
    font-size: 14px;
    color: #303133;
    padding-left: 10px;
    margin-bottom: 10px;
  }

  .idCardInput-div {
    width: 100%;
    height: 70px;
    border: 1px dashed #dddddd;
    border-radius: 6px;
    position: relative;
    padding-bottom: 20px;

    /deep/ .idCardInput {
      height: 100%;

      textarea {
        width: 100%;
        height: 100% !important;
        padding-bottom: 30px;
        border: none;
      }
    }

    .row-count {
      position: absolute;
      right: 14px;
      bottom: 5px;
    }
  }

  .org-div {
    width: 100%;
    height: 492px;
    border: 1px dashed #dddddd;
    border-radius: 6px;
    margin-top: 20px;
    padding: 12px 15px;
    overflow: auto;
    box-sizing: border-box;

    .org-title {
      font-size: 16px;
      color: #909399;
    }

    /deep/ .el-icon-caret-right {
      margin: 0 13px;
      vertical-align: middle;
      margin-top: -2px;
    }

    .cur-org {
      display: inline-block;
      font-weight: bold;
      color: #303133;
      width: 200px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      vertical-align: top;
      /*cursor: pointer;*/
    }
  }

  .select-div {
    width: 100%;
    height: 602px;
    border: 1px dashed #dddddd;
    border-radius: 6px;
    padding: 0px 14px 14px 14px;
    box-sizing: border-box;
    overflow: auto;
  }

  .ic-org-gray {
    display: inline-block;
    width: 13px;
    height: 14px;
    vertical-align: middle;
    /*background: url("../../assets/images/icons2/ic-org-gray.png");*/
    margin-right: 6px;
    vertical-align: middle;
    margin-top: -2px;
  }

  .ic-emp-gray {
    /*background: url("../../assets/images/icons2/ic-emp-gray.png");*/
  }

  .ic-subOrg-blue {
    display: inline-block;
    width: 14px;
    height: 14px;
    vertical-align: middle;
    /*background: url("../../assets/images/icons2/ic-subOrg-blue.png");*/
    margin-right: 6px;
    vertical-align: middle;
    margin-top: -2px;
  }

  .org-text {
    /deep/ .el-checkbox__label {
      width: 230px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      vertical-align: top;
    }
  }
}

.emp-list {
  .li {
    float: left;
    margin-right: 20px;
    padding: 7px 14px;
    border: 1px solid #dddddd;
    border-radius: 18px;
    color: #606266;
    box-sizing: border-box;
    margin-top: 14px;
  }

  /deep/ .el-icon-error {
    margin-left: 10px;
    font-size: 14px;
    color: #c0c4cc;
    cursor: pointer;
  }
}

.ic-select-blue {
  display: inline-block;
  width: 17px;
  height: 16px;
  vertical-align: middle;
  /*background: url("../../assets/images/icons2/ic-select-blue.png");*/
  margin-right: 6px;
  vertical-align: middle;
  margin-top: -2px;
}

.detail-item {
  display: flex;
  margin-bottom: 20px;

  .lab {
    width: 160px;
    text-align: right;
  }

  .val {
    flex: 1;
    text-align: left;
  }
}

/deep/.table-header {
  background-color: #f2f2f2 !important;
  padding: 6px 0;
  border-bottom: 1px solid #e2e2e2 !important;
  border-top: 1px solid #e2e2e2 !important;
  border-right: 1px solid #e2e2e2 !important;
  border-right: transparent;
  text-align: left;
}

/deep/.table-header .cell {
  font-weight: bold;
  color: #303133;
}

.cust-dialog /deep/.el-table .el-table__cell {
  padding: 6px 0;
}

/deep/.detail-dialog {
  .el-dialog {
    width: 95% !important;
    max-width: 1300px;
  }
}

/deep/.detail-drawer {
  width: 95% !important;
  max-width: 1200px !important;

  .pal-tabs {
    padding-left: 0;
    height: 75px;
    line-height: 62px;
    position: relative;
    top: -10px;
  }

  .span-tabs {
    height: 71px;
    line-height: 80px;
  }
}

/deep/.el-upload-list__item {
  background: #f3f3f3;
}

/deep/.el-form-item__content {

  .el-radio-group {
    .sbStatusCeckbox {
      .el-radio-button__inner {
        border-radius: 0px !important;
        width: 75px !important;
      }
    }
  }

  .el-radio-group:first-child {
    .sbStatusCeckbox {
      .el-radio-button__inner {
        border-radius: 4px 0px 0px 4px !important;
      }
    }
  }

  .el-radio-group:last-child {
    .sbStatusCeckbox {
      .el-radio-button__inner {
        border-radius: 0px 4px 4px 0px !important;
      }
    }
  }

  /*.sbStatusCeckbox{
      .el-radio-button__inner {
        border-radius: 0px !important;
      }
    }*/
}

.byselfList {
  display: flex;
  align-items: center;
}

.causeTip {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  font-size: 12px;
  text-align: center;
  background-color: #FF6600;
  color: #fff;
  margin-right: 5px;
  border-radius: 50%;
  cursor: default;
}

.adjustData /deep/ .el-form-item {
  margin-bottom: 10px;
}

.resonlabel /deep/ .el-form-item__label::before {
  content: '' !important;
}
.ic-run{
  font-size: 16px;
  margin-right: 3px;
  vertical-align: middle;
}
</style>
