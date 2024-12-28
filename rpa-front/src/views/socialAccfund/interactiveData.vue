<template>
  <div class="content spl-container">
    <div class="loading-mask-none">
      <!-- 导航 -->
      <breadcrumb :data="pathData"></breadcrumb>
      <div style="padding: 20px 20px 0 20px;">
        <palTab :tabs="tabs" v-model="tabActive" :type="2"></palTab>

        <!--社保-->
        <div v-show="tabActive==0">
          <div class="search-row clearfix" style="display: flex">
            <div class="flex1">
              <el-row>
                <el-col :span="8" class="display-flex">
                  <span class="label required-pre">参保城市：</span>
                  <addrSelector v-model="socialSearch.addrName" :addrArr="addrArr"
                                @changeAddrSelect="getSocialAddrId" width="100%"  class="search-row-item"></addrSelector>
                </el-col>
                <el-col :span="7" class="display-flex pr-30">
                  <span class="label">申报账户：</span>
                  <el-select v-model="socialSearch.accountNumbers" class="search-row-item" collapse-tags multiple clearable filterable>
                    <el-option v-for="it in socialSearch.accountNumberArr" :key="it.name" :label="it.name" :value="it.accountNumber"></el-option>
                  </el-select>
                </el-col>
                <el-col :span="9" class="display-flex">
                  <span class="label">缴纳起始月：</span>
                  <div class="date-editor-div search-row-item">
                    <el-date-picker v-model="socialSearch.insuredDate" value-format="yyyy-MM" class="date-editor2" type="month"
                                    placeholder="选择日期" clearable>
                    </el-date-picker>
                  </div>
                </el-col>
              </el-row>

              <el-row class="mt-20">
              <el-col :span="8" class="display-flex">
                <span class="label">交互时间：</span>
                <div class="search-row-item" style="display: flex">
                  <el-date-picker v-model="socialSearch.beginDate" value-format="yyyy-MM-dd" class="flex1 date-editor2" type="date"
                                  placeholder="开始时间" clearable>
                  </el-date-picker>
                  <span class="ml-5 mr-5 lh-com">~</span>
                  <el-date-picker v-model="socialSearch.endDate" value-format="yyyy-MM-dd" class="flex1 date-editor2" type="date"
                                  placeholder="结束时间" clearable>
                  </el-date-picker>
                </div>
              </el-col>

              <el-col :span="7" class="display-flex">
                <span class="label required-pre">校验结果：</span>
                <el-checkbox-group v-model="socialSearch.statusList" size="medium">
                  <el-checkbox-button class="checkRes" v-for="item in statusArr" :key="item.id" :label="item.id">{{item.name}}</el-checkbox-button>
                </el-checkbox-group>
              </el-col>

              <el-col :span="9" class="display-flex">
                <span class="label required-pre">申报类型：</span>
                <el-checkbox-group v-model="socialSearch.typeList" size="medium">
                  <el-checkbox-button v-for="item in typeArr" :key="item.id" :label="item.id">{{item.name}}</el-checkbox-button>
                </el-checkbox-group>
              </el-col>
            </el-row>
            </div>
            <div>
              <el-button type="primary" class="search-btn w-60 mr-10" @click="getSocialTable">查询</el-button>
              <el-button icon="ic-filter-blue" class="btn--border-blue mr-10" @click="socialFilter.show=true">筛选</el-button>
            </div>
          </div>
          <div>
            <!-- 表格 -->
            <dTable @fetch="getSocialTable" ref="socialTable"
                    :tableHeight="tableHeight" :paging="true" :showSelection="true" :showIndex="true"
                    :filterTags="socialSearch.filterTags" @resetFilter="resetSocial" rowKey="id" :rowHeight="45">
              <u-table-column prop="batchNumber" label="批次号" min-width="210" :show-overflow-tooltip="true" fixed="left"></u-table-column>
              <u-table-column prop="name" label="姓名" min-width="80" :show-overflow-tooltip="true" fixed="left"></u-table-column>
              <u-table-column prop="idCard" label="证件号码" min-width="170" :show-overflow-tooltip="true"></u-table-column>
              <u-table-column prop="addrName" label="参保城市" min-width="80" :show-overflow-tooltip="true"></u-table-column>
              <u-table-column prop="declareTypeName" label="申报类型" min-width="80" :show-overflow-tooltip="true"></u-table-column>
              <u-table-column prop="items" label="参保险种" min-width="180" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>{{
                    getProductNameList(scope.row.items)
                  }}</span>
                </template>
              </u-table-column>
              <u-table-column prop="insuredDate" label="缴纳起始月" min-width="150" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <p v-if="scope.row.declareType==5">
                    <span v-if="scope.row.insuredBeginDate || scope.row.insuredEndDate">{{scope.row.insuredBeginDate}} ~ {{scope.row.insuredEndDate}}</span>
                  </p>
                  <p v-else>{{scope.row.insuredDate}}</p>
                </template>
              </u-table-column>
              <u-table-column prop="tbBase" label="缴纳基数" min-width="80" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>{{scope.row.tbBase ? getTwoPoint(scope.row.tbBase) : ''}}</span>
                </template>
              </u-table-column>
              <u-table-column prop="validateStatus" label="校验结果" min-width="80" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>{{scope.row.validateStatus==1?'校验成功':'校验失败'}}</span>
                </template>
              </u-table-column>
              <u-table-column prop="errorMsg" label="原因" min-width="200" :show-overflow-tooltip="true"></u-table-column>
              <u-table-column prop="createTime" label="交互时间" min-width="160" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                <span>{{scope.row.createTime?$moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss'):''}}</span>
                </template>
              </u-table-column>
              <u-table-column fixed="right" label="操作" width="60">
                <template slot-scope="scope">
                  <el-button class="mr-25" type="text" size="small" @click="viewSocialInfo(scope.row)">查看</el-button>
                </template>
              </u-table-column>
            </dTable>
          </div>
        </div>
        <!--社保-->

        <!--公积金-->
        <div v-show="tabActive==1">
          <div class="search-row clearfix" style="display: flex">
            <div class="flex1">
              <el-row>
                <el-col :span="8" class="display-flex">
                  <span class="label required-pre">参保城市：</span>
                  <addrSelector v-model="accfundSearch.addrName" :addrArr="addrArr"
                                @changeAddrSelect="getAccfundAddrId" width="100%"  class="search-row-item"></addrSelector>
                </el-col>
                <el-col :span="7" class="display-flex pr-30">
                  <span class="label">申报账户：</span>
                  <el-select v-model="accfundSearch.accountNumbers" class="search-row-item" collapse-tags multiple clearable filterable>
                    <el-option v-for="it in accfundSearch.accountNumberArr" :key="it.name" :label="it.name" :value="it.accountNumber"></el-option>
                  </el-select>
                </el-col>
                <el-col :span="9" class="display-flex">
                  <span class="label">缴纳起始月：</span>
                  <div class="date-editor-div search-row-item">
                    <el-date-picker v-model="accfundSearch.insuredDate" value-format="yyyy-MM" class="date-editor2" type="month"
                                    placeholder="选择日期" clearable>
                    </el-date-picker>
                  </div>
                </el-col>
              </el-row>
              <el-row class="mt-20">
                <el-col :span="8" class="display-flex">
                  <span class="label">交互时间：</span>
                  <div class="search-row-item" style="display: flex">
                    <el-date-picker v-model="accfundSearch.beginDate" value-format="yyyy-MM-dd" class="flex1 date-editor2" type="date"
                                    placeholder="开始时间" clearable>
                    </el-date-picker>
                    <span class="ml-5 mr-5 lh-com">~</span>
                    <el-date-picker v-model="accfundSearch.endDate" value-format="yyyy-MM-dd" class="flex1 date-editor2" type="date"
                                    placeholder="结束时间" clearable>
                    </el-date-picker>
                  </div>
                </el-col>
                <el-col :span="7" class="display-flex">
                  <span class="label required-pre required-pre">校验结果：</span>
                  <el-checkbox-group v-model="accfundSearch.statusList" size="medium">
                    <el-checkbox-button v-for="item in statusArr" :key="item.id" :label="item.id">{{item.name}}</el-checkbox-button>
                  </el-checkbox-group>
                </el-col>
                <el-col :span="9" class="display-flex">
                  <span class="label required-pre">申报类型：</span>
                  <el-checkbox-group v-model="accfundSearch.typeList" size="medium">
                    <el-checkbox-button v-for="item in typeArr" :key="item.id" :label="item.id">{{item.name}}</el-checkbox-button>
                  </el-checkbox-group>
                </el-col>
              </el-row>
            </div>
            <div>
              <el-button type="primary" class="search-btn w-60 mr-10" @click="getAccfundTable">查询</el-button>
              <el-button icon="ic-filter-blue" class="btn--border-blue mr-10" @click="accfundFilter.show=true">筛选</el-button>
            </div>

          </div>
          <div>
            <!-- 表格 -->
            <dTable @fetch="getAccfundTable" ref="accfundTable"
                    :tableHeight="tableHeight" :paging="true" :showSelection="true" :showIndex="true"
                    :filterTags="accfundSearch.filterTags" @resetFilter="resetAccfund" rowKey="id" :rowHeight="45">
              <u-table-column prop="batchNumber" label="批次号" min-width="210" :show-overflow-tooltip="true" fixed="left"></u-table-column>
              <u-table-column prop="name" label="姓名" min-width="80" :show-overflow-tooltip="true" fixed="left"></u-table-column>
              <u-table-column prop="idCard" label="证件号码" min-width="170" :show-overflow-tooltip="true"></u-table-column>
              <u-table-column prop="addrName" label="参保城市" min-width="80" :show-overflow-tooltip="true"></u-table-column>
              <u-table-column prop="declareTypeName" label="申报类型" min-width="80" :show-overflow-tooltip="true"></u-table-column>
              <u-table-column prop="insuredDate" label="缴纳起始月" min-width="150" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <p v-if="scope.row.declareType==5">
                    <span v-if="scope.row.insuredBeginDate || scope.row.insuredEndDate">{{scope.row.insuredBeginDate}} ~ {{scope.row.insuredEndDate}}</span>
                  </p>
                  <p v-else>{{scope.row.insuredDate}}</p>
                </template>
              </u-table-column>
              <u-table-column prop="tbBase" label="缴纳基数" min-width="80" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>{{scope.row.tbBase ? getTwoPoint(scope.row.tbBase) : ''}}</span>
                </template>
              </u-table-column>
              <u-table-column prop="items" label="参保比例" min-width="140" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <div v-html="handleRatioText(scope.row, 'list')"></div>
                </template>
              </u-table-column>
              <u-table-column prop="validateStatus" label="校验结果" min-width="80" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>{{scope.row.validateStatus==1?'校验成功':'校验失败'}}</span>
                </template>
              </u-table-column>
              <u-table-column prop="errorMsg" label="原因" min-width="200" :show-overflow-tooltip="true"></u-table-column>
              <u-table-column prop="createTime" label="交互时间" min-width="160" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>{{scope.row.createTime?$moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss'):''}}</span>
                </template>
              </u-table-column>
              <u-table-column fixed="right" label="操作" width="80">
                <template slot-scope="scope">
                  <el-button class="mr-25" type="text" size="small" @click="viewAccfundInfo(scope.row)">查看</el-button>
                </template>
              </u-table-column>
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
        <div class="type-title mt-0"><span class="text">批次号</span></div>
        <div class="pl-20 pr-20 pt-20">
          <el-input v-model.trim="socialFilter.batchNumber" placeholder="请输入" @blur="replaceBlank(1,'socialFilter','batchNumber')"></el-input>
        </div>

        <div class="type-title"><span class="text">证件号码</span></div>
        <div class="pl-20 pr-20 pt-20">
          <el-input v-model="socialFilter.empIdCards" type="textarea" rows="5" resize="none" placeholder="一行只可输入一个证件号码" @blur="replaceBlank(2,'socialFilter','empIdCards')"></el-input>
          <p class="text-right mr-5 mt-5">已输入 {{socialFilter.searchIdCardsLength}} 个</p>
        </div>

        <div class="type-title"><span class="text">姓名</span></div>
        <div class="pl-20 pr-20 pt-20">
          <el-input v-model="socialFilter.empNames" type="textarea" rows="5" resize="none" placeholder="一行只能输入一个姓名" @blur="replaceBlank(2,'socialFilter','empNames')"></el-input>
          <p class="text-right mr-5 mt-5">已输入 {{socialFilter.searchNamesLength}} 个</p>
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

        <div class="type-title mt-0"><span class="text">批次号</span></div>
        <div class="pl-20 pr-20 pt-20">
          <el-input v-model.trim="accfundFilter.batchNumber" placeholder="请输入"  @blur="replaceBlank(1,'accfundFilter','batchNumber')"></el-input>
        </div>

        <div class="type-title"><span class="text">证件号码</span></div>
        <div class="pl-20 pr-20 pt-20">
          <el-input v-model="accfundFilter.empIdCards" type="textarea" rows="5" resize="none" placeholder="一行只可输入一个证件号码"  @blur="replaceBlank(2,'accfundFilter','empIdCards')"></el-input>
          <p class="text-right mr-5 mt-5">已输入 {{accfundFilter.searchIdCardsLength}} 个</p>
        </div>

        <div class="type-title"><span class="text">姓名</span></div>
        <div class="pl-20 pr-20 pt-20">
          <el-input v-model="accfundFilter.empNames" type="textarea" rows="5" resize="none" placeholder="一行只能输入一个姓名"  @blur="replaceBlank(2,'accfundFilter','empNames')"></el-input>
          <p class="text-right mr-5 mt-5">已输入 {{accfundFilter.searchNamesLength}} 个</p>
        </div>

      </div>
      <div class="drawer-footer-buts">
        <el-button class="btn--border-blue s-btn" @click="resetFilterAccfund">重置</el-button>
        <el-button type="primary" class="s-btn ml-12" @click="confirmFilterAccfund">确认筛选</el-button>
      </div>
    </el-drawer>

    <!--查看社保详情-->
    <el-dialog :visible.sync="socialDetail.show" title="社保参保详情" width="1200px" class="cust-dialog detail-dialog" :show-close="false" :close-on-click-modal="false">
      <div class="pt-10">
        <el-row>
          <el-col :span="8" class="detail-item">
            <span class="lab">姓名：</span>
            <span class="val">{{socialDetail.row.name}}</span>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">证件号码：</span>
            <span class="val">{{socialDetail.row.idCard}}</span>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">参保城市：</span>
            <span class="val">{{socialDetail.row.addrName}}</span>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">申报类型：</span>
            <span class="val">{{socialDetail.row.declareTypeName}}</span>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">参保险种：</span>
            <span class="val">{{getProductNameList(socialDetail.row.items)}}</span>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">缴纳起始月：</span>
            <span class="val" v-if="socialDetail.row.declareType==5"><span v-if="socialDetail.row.insuredBeginDate || socialDetail.row.insuredEndDate">{{socialDetail.row.insuredBeginDate}} ~ {{socialDetail.row.insuredEndDate}}</span></span>
            <span class="val" v-else>{{socialDetail.row.insuredDate}}</span>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">缴纳基数：</span>
            <span class="val">{{socialDetail.row.tbBase ? getTwoPoint(socialDetail.row.tbBase) : ''}}</span>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">批次号：</span>
            <span class="val">{{socialDetail.row.batchNumber}}</span>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8" class="detail-item">
            <span class="lab">交互时间：</span>
            <span class="val">{{socialDetail.row.createTime?$moment(socialDetail.row.createTime).format('YYYY-MM-DD HH:mm:ss'):''}}</span>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">创建人：</span>
            <span class="val">{{socialDetail.row.createName}}</span>
          </el-col>
        </el-row>
        <el-row>
          <!--其它报盘文件配置的数据字段-->
          <el-col :span="8" class="detail-item" v-for="(item, key, index) in socialDetail.row.declareInfo" :key="index">
            <span class="lab">{{key}}：</span>
            <span class="val">{{item}}</span>
          </el-col>
        </el-row>
      </div>
      <div class="text-right mt-10">
        <el-button size="small" class="w-60" type="primary" @click="socialDetail.show=false">关闭</el-button>
      </div>
    </el-dialog>

    <!--查看公积金详情-->
    <el-dialog :visible.sync="accfundDetail.show" title="公积金参保详情" width="1200px" class="cust-dialog detail-dialog" :show-close="false" :close-on-click-modal="false">
      <div class="pt-10">
        <el-row>
          <el-col :span="8" class="detail-item">
            <span class="lab">姓名：</span>
            <span class="val">{{accfundDetail.row.name}}</span>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">证件号码：</span>
            <span class="val">{{accfundDetail.row.idCard}}</span>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">参保城市：</span>
            <span class="val">{{accfundDetail.row.addrName}}</span>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">申报类型：</span>
            <span class="val">{{accfundDetail.row.declareTypeName}}</span>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">参保比例：</span>
            <div class="val" v-html="handleRatioText(accfundDetail.row, 'detail')"></div>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">缴纳起始月：</span>
            <span class="val" v-if="accfundDetail.row.declareType==5"><span v-if="accfundDetail.row.insuredBeginDate || accfundDetail.row.insuredEndDate">{{accfundDetail.row.insuredBeginDate}} ~ {{accfundDetail.row.insuredEndDate}}</span></span>
            <span class="val" v-else>{{accfundDetail.row.insuredDate}}</span>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">缴纳基数：</span>
            <span class="val">{{accfundDetail.row.tbBase ? getTwoPoint(accfundDetail.row.tbBase) : ''}}</span>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">批次号：</span>
            <span class="val">{{accfundDetail.row.batchNumber}}</span>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8" class="detail-item">
            <span class="lab">交互时间：</span>
            <span class="val">{{accfundDetail.row.createTime?$moment(accfundDetail.row.createTime).format('YYYY-MM-DD HH:mm:ss'):''}}</span>
          </el-col>
          <el-col :span="8" class="detail-item">
            <span class="lab">创建人：</span>
            <span class="val">{{accfundDetail.row.createName}}</span>
          </el-col>
        </el-row>
        <el-row>
          <!--其它报盘文件配置的数据字段-->
          <el-col :span="8" class="detail-item" v-for="(item, key, index) in accfundDetail.row.declareInfo" :key="index">
            <span class="lab">{{key}}：</span>
            <span class="val">{{item}}</span>
          </el-col>
          <!--其它报盘文件配置的数据字段-->
        </el-row>
      </div>
      <div class="text-right mt-10">
        <el-button size="small" class="w-60" type="primary" @click="accfundDetail.show=false">关闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
  import dTable from '../../components/common/table'
  import palTab from '../../components/common/pal-tab'
  import addrSelector from '../../components/common/addrSelector'
  import {accMul} from '../../utils/socialAccfundProduct'

  export default {
    components: {dTable, palTab, addrSelector},
    inject:['getInsuranceList'],
    data() {
      return {
        pathData: [],
        tabs: ['社保', '公积金'],
        tabActive: 0,
        addrArr: [],
        statusArr: [
          {id: 0, name: '校验失败'}, {id: 1, name: '校验成功'}
        ],
        typeArr: [
          //变更类型（1增，2减，3调基，5补缴）
          {id: 1, name: '增员'}, {id: 2, name: '减员'}, {id: 3, name: '调基'}, {id: 5, name: '补缴'}
        ],
        socialSearch: {
          addrName: '',
          addrId: '',
          accountNumbers: [],
          insuredDate: '',
          beginDate: '',
          endDate: '',
          statusList: [0, 1],
          typeList: [1, 2, 3, 5],
          batchNumber: '',
          setEmpIdCards: [],
          setEmpNames: [],

          filterTags: [],
          accountNumberArr: []
        },
        accfundSearch: {
          addrName: '',
          addrId: '',
          accountNumbers: [],
          insuredDate: '',
          beginDate: '',
          endDate: '',
          statusList: [0, 1],
          typeList: [1, 2, 3, 5],
          batchNumber: '',
          setEmpIdCards: [],
          setEmpNames: [],

          filterTags: [],
          accountNumberArr: []
        },

        socialFilter: {
          show: false,
          batchNumber: '',
          empIdCards: '',
          setEmpIdCards: [],
          searchIdCardsLength: 0,
          empNames: '',
          setEmpNames: [],
          searchNamesLength: 0
        },
        accfundFilter: {
          show: false,
          batchNumber: '',
          empIdCards: '',
          setEmpIdCards: [],
          searchIdCardsLength: 0,
          empNames: '',
          setEmpNames: [],
          searchNamesLength: 0
        },
        socialDetail: {
          show: false,
          row: {}
        },
        accfundDetail: {
          show: false,
          row: {}
        },
        insuranceList:[]
      }
    },
    computed: {
      tableHeight: function () {
        return this.$global.bodyHeight - 390 + 'px'
      },
      //保留两位小数
      getTwoPoint(){
        return function(str){
          if(Number(str) != Number(str)){
            return str
          }else if(Number(str)){
            return Number(str).toFixed(2)
          }else{
            return str
          }
        }
      },
      //排序表格中的 参保险种字段的 顺序
      getProductNameList() {
        return function (nameList) {
          if(!nameList){
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
    },
    created() {

      let tabs = this.$store.state.tabs
      if (tabs) {
        this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
      }
      this.pathData.push({name: '交互数据'})
      this.getAddrArr();
      this.getCompanyList(1);
      this.getCompanyList(2);
      this.$nextTick(() => {
        // this.getSocialTable()
      })
      // this.getDictByKey('1003')
    },
    watch: {
      'socialFilter.empIdCards'(newValue, oldValue) {
        newValue = newValue.replace(/[ ]/g,"");
        var obj = this.$global.getInputDataRow(newValue)
        this.socialFilter.searchIdCardsLength = obj.num
        this.socialFilter.setEmpIdCards = obj.value
      },
      'socialFilter.empNames'(newValue, oldValue) {
        newValue = newValue.replace(/[ ]/g,"");
        var obj = this.$global.getInputDataRow(newValue)
        this.socialFilter.searchNamesLength = obj.num
        this.socialFilter.setEmpNames = obj.value
      },
      'accfundFilter.empIdCards'(newValue, oldValue) {
        newValue = newValue.replace(/[ ]/g,"");
        var obj = this.$global.getInputDataRow(newValue)
        this.accfundFilter.searchIdCardsLength = obj.num
        this.accfundFilter.setEmpIdCards = obj.value
      },
      'accfundFilter.empNames'(newValue, oldValue) {
        newValue = newValue.replace(/[ ]/g,"");
        var obj = this.$global.getInputDataRow(newValue)
        this.accfundFilter.searchNamesLength = obj.num
        this.accfundFilter.setEmpNames = obj.value
      }
    },
    methods: {
      // 获取参保地
      getAddrArr() {
        this.$http({
          url: '/policy/declareAddr/addrList',
          method: 'post'
        }).then(({data}) => {
          this.addrArr = data.data
        }).catch((data) => {
        })
      },
      // 获取参保主体
      getCompanyList(type) {
        let that = this, addressId = ''
        if(type==1){
          addressId = that.socialSearch.addrId
        }else{
          addressId = that.accfundSearch.addrId
        }
        this.$http({
          url: '/policy/declareAccount/seeMainBody',
          method: 'post',
          params: {
            bussinessType: type,
            custLimit: 0,
            addressId: addressId
          }
        }).then(({data}) => {
          if(type==1){
            that.socialSearch.accountNumberArr = data.data
            that.socialSearch.accountNumbers = []
          }else{
            that.accfundSearch.accountNumberArr = data.data
            that.accfundSearch.accountNumbers = []
          }
        }).catch((data) => {
        })
      },
      // 改变社保-参保地
      getSocialAddrId(item) {
        if (this.socialSearch.addrId == item.id) {
          return false
        }
        this.socialSearch.addrId = item.id
        this.getCompanyList(1)
      },
      // 改变公积金-参保地
      getAccfundAddrId(item) {
        if (this.accfundSearch.addrId == item.id) {
          return false
        }
        this.accfundSearch.addrId = item.id
        this.getCompanyList(2)
      },

      //S=社保
      getSocialTable() {
        let that = this;
        this.socialFilter.show = false;
        let search = this.socialSearch
        if(!search.addrName){
          this.$message.warning("请选择参保城市")
          return
        }else if(search.statusList.length==0){
          this.$message.warning("请选择校验结果")
          return
        }else if(search.typeList.length==0){
          this.$message.warning("请选择申报类型")
          return
        }else if(search.beginDate && search.endDate && new Date(search.endDate) < new Date(search.beginDate)){
          this.$message.warning("交互开始时间不能大于结束时间")
          return
        }
        this.fetchCallBackSocial()

        var params = [
          {property: 'addrName', value: search.addrName},
          {property: 'accountNumbers', value: search.accountNumbers},
          {property: 'businessType', value: 1}, // 业务类型（1：社保，2：公积金）
          {property: 'insuredDate', value: search.insuredDate},
          {property: 'beginDate', value: search.beginDate},
          {property: 'endDate', value: search.endDate},
          {property: 'batchNumber', value: search.batchNumber}
        ]
        if(search.statusList.length>0){
          params.push({property: 'validateStatus', value: search.statusList})
        }
        if(search.typeList.length>0){
          params.push({property: 'declareTypes', value: search.typeList})
        }
        if(search.setEmpIdCards.length>0){
          params.push({property: 'idCards', value: search.setEmpIdCards})
        }
        if(search.setEmpNames.length>0){
          params.push({property: 'empNames', value: search.setEmpNames})
        }
        this.$refs.socialTable.fetch({
          query: params,
          method: 'post',
          url: '/agent/employeeDeclare/page'
        })
      },
      fetchCallBackSocial(res) {
        let filterTags = []
        let search = this.socialSearch
        if(search.batchNumber){
          filterTags.push({
            label: '',
            text: '批次号：' + search.batchNumber,
            formCode: 'batchNumber'
          })
        }
        for (let i = 0; i < search.setEmpIdCards.length; i++) {
          filterTags.push({
            label: '',
            text: '证件号码：' + search.setEmpIdCards[i],
            formCode: 'idCard',
            index: i
          })
        }

        for (let i = 0; i < search.setEmpNames.length; i++) {
          filterTags.push({
            label: '',
            text: '姓名：' + search.setEmpNames[i],
            formCode: 'name',
            index: i
          })
        }

        search.filterTags = filterTags
      },

      // 搜索框-确定
      confirmFilterSocial() {
        this.socialSearch.batchNumber = this.socialFilter.batchNumber
        this.socialSearch.setEmpIdCards = [...this.socialFilter.setEmpIdCards]
        this.socialSearch.setEmpNames = [...this.socialFilter.setEmpNames]
        this.getSocialTable()
      },

      // 搜索框-重置
      resetFilterSocial() {
        this.socialFilter.batchNumber = ''
        this.socialFilter.empIdCards = ''
        this.socialFilter.setEmpIdCards = []
        this.socialFilter.searchIdCardsLength = 0
        this.socialFilter.empNames = ''
        this.socialFilter.setEmpNames = []
        this.socialFilter.searchNamesLength = 0
      },

      // 表格头部标签查询条件-移除、重置
      resetSocial(index, filterTags) {
        if (this.$global.isNotBlank(index)) {
          // 单个移除
          this.resetItemSocial(this.socialSearch.filterTags[index])
        } else {
          //  重置
          this.socialSearch.batchNumber = ''
          this.socialSearch.setEmpIdCards = []
          this.socialSearch.setEmpNames = []
        }
        this.socialSearch.filterTags = filterTags
        this.getSocialTable()
      },

      resetItemSocial(tag) {
        let code = tag.formCode
        if (code == 'batchNumber') {
          this.socialSearch.batchNumber = ''
        } else if (code == 'idCard') {
          this.socialSearch.setEmpIdCards.splice(tag.index, 1)
        } else if (code == 'name') {
          this.socialSearch.setEmpNames.splice(tag.index, 1)
        }
      },
      //E=社保

      //S=公积金
      getAccfundTable() {
        let that = this;
        this.accfundFilter.show = false;
        let search = this.accfundSearch;

        if(!search.addrName){
          this.$message.warning("请选择参保城市")
          return
        }else if(search.statusList.length==0){
          this.$message.warning("请选择校验结果")
          return
        }else if(search.typeList.length==0){
          this.$message.warning("请选择申报类型")
          return
        }else if(search.beginDate && search.endDate && new Date(search.endDate) < new Date(search.beginDate)){
          this.$message.warning("交互开始时间不能大于结束时间")
          return
        }
        this.fetchCallBackAccfund()
        var params = [
          {property: 'addrName', value: search.addrName},
          {property: 'accountNumbers', value: search.accountNumbers},
          {property: 'businessType', value: 2}, // 业务类型（1：社保，2：公积金）
          {property: 'insuredDate', value: search.insuredDate},
          {property: 'beginDate', value: search.beginDate},
          {property: 'endDate', value: search.endDate},
          {property: 'batchNumber', value: search.batchNumber}
        ]
        if(search.statusList.length>0){
          params.push({property: 'validateStatus', value: search.statusList})
        }
        if(search.typeList.length>0){
          params.push({property: 'declareTypes', value: search.typeList})
        }
        if(search.setEmpIdCards.length>0){
          params.push({property: 'idCards', value: search.setEmpIdCards})
        }
        if(search.setEmpNames.length>0){
          params.push({property: 'empNames', value: search.setEmpNames})
        }
        this.$refs.accfundTable.fetch({
          query: params,
          method: 'post',
          url: '/agent/employeeDeclare/page'
        })
      },
      fetchCallBackAccfund(res) {
        let filterTags = []
        let search = this.accfundSearch;

        if(search.batchNumber){
          filterTags.push({
            label: '',
            text: '批次号：' + search.batchNumber,
            formCode: 'batchNumber'
          })
        }

        for (let i = 0; i < search.setEmpIdCards.length; i++) {
          filterTags.push({
            label: '',
            text: '证件号码：' + search.setEmpIdCards[i],
            formCode: 'idCard',
            index: i
          })
        }

        for (let i = 0; i < search.setEmpNames.length; i++) {
          filterTags.push({
            label: '',
            text: '姓名：' + search.setEmpNames[i],
            formCode: 'name',
            index: i
          })
        }

        search.filterTags = filterTags
      },

      // 搜索框-确定
      confirmFilterAccfund() {
        this.accfundSearch.batchNumber = this.accfundFilter.batchNumber
        this.accfundSearch.setEmpIdCards = [...this.accfundFilter.setEmpIdCards]
        this.accfundSearch.setEmpNames = [...this.accfundFilter.setEmpNames]
        this.getAccfundTable()
      },

      // 搜索框-重置
      resetFilterAccfund() {
        this.accfundFilter.batchNumber = ''
        this.accfundFilter.empIdCards = ''
        this.accfundFilter.setEmpIdCards = []
        this.accfundFilter.searchIdCardsLength = 0
        this.accfundFilter.empNames = ''
        this.accfundFilter.setEmpNames = []
        this.accfundFilter.searchNamesLength = 0
      },

      // 表格头部标签查询条件-移除、重置
      resetAccfund(index, filterTags) {
        if (this.$global.isNotBlank(index)) {
          // 单个移除
          this.resetItemAccfund(this.accfundSearch.filterTags[index])
        } else {
          //  重置
          this.accfundSearch.batchNumber = ''
          this.accfundSearch.setEmpIdCards = []
          this.accfundSearch.setEmpNames = []
        }
        this.accfundSearch.filterTags = filterTags
        this.getAccfundTable()
      },

      resetItemAccfund(tag) {
        let code = tag.formCode
        if (code == 'batchNumber') {
          this.accfundSearch.batchNumber = ''
        } else if (code == 'idCard') {
          this.accfundSearch.setEmpIdCards.splice(tag.index, 1)
        } else if (code == 'name') {
          this.accfundSearch.setEmpNames.splice(tag.index, 1)
        }
      },
      //E=公积金

      //查看社保详情
      viewSocialInfo(row){
        this.socialDetail.row = row
        console.log('viewSocialInfo',row)
        this.socialDetail.show = true
      },
      //查看公积金详情
      viewAccfundInfo(row){
        this.accfundDetail.row = row
        this.accfundDetail.show = true
      },

      handleRatioText(row, type) {
        var compRatio = row.compRatio;
        var empRatio = row.empRatio;
        var new_compRatio = '';
        var new_empRatio = '';
        if(compRatio==null || compRatio.trim()==''){
          new_compRatio = '';
        }else if(!isNaN(new_compRatio)){
          new_compRatio = accMul(compRatio, 100)  + '%';
        }

        if(empRatio==null || empRatio.trim()==''){
          new_empRatio = '';
        }else if(!isNaN(new_empRatio)){
          new_empRatio = accMul(empRatio, 100) + '%';
        }

        var suppCompRatio = row.suppCompRatio;
        var suppEmpRatio = row.suppEmpRatio;
        var new_suppCompRatio = '';
        var new_suppEmpRatio = '';
        if(suppCompRatio==null || suppCompRatio.trim()==''){
          new_suppCompRatio = '';
        }else if(!isNaN(new_suppCompRatio)){
          new_suppCompRatio = accMul(suppCompRatio, 100) + '%';
        }

        if(suppEmpRatio==null || suppEmpRatio.trim()==''){
          new_suppEmpRatio = '';
        }else if(!isNaN(new_suppEmpRatio)){
          new_suppEmpRatio = accMul(suppEmpRatio, 100) + '%';
        }

        var text = ''
        if(new_compRatio!=''){
          text += '单位' + new_compRatio
        }
        if(new_empRatio!=''){
          text += (new_compRatio!=''?'|':'')+'个人' + new_empRatio
        }
        var text2 = ''
        if(new_suppCompRatio!='' || new_suppEmpRatio!=''){
          if(new_suppCompRatio!=''){
            text2 += '补充单位' + new_suppCompRatio
          }
          if(new_suppEmpRatio!=''){
            text2 += (new_suppCompRatio!=''?'|':'补充')+'个人' + new_suppEmpRatio
          }
        }
        var res = ''
        if(text==''){
          res = text2==''?'':text2
        }else{
          if(text2==''){
            res = text;
          }else{
            res = text + (type=='list'?'<br/>':'，')+text2
          }
        }
        return res;
      },
      //获取险种字典，用与参保险种，按返回数据排序
      async getDictByKey() {
        return this.$http({
          url: 'policy/sys/dict/getDictByKey',
          method: 'get',
          params: {
            dataKey: '10003',
          },
        })
          .then((res) => {
            this.insuranceList = res.data.data
          })
          .catch((err) => {
            this.$message.error('字典接口出错，请稍后再试')
          })
      },
      replaceBlank(type,formKey,fieldKey){
        if(type == 1){
          //去除所有空格
          this[formKey][fieldKey] = this[formKey][fieldKey].replace(/\s+/g,"")
        }else {
          //去除换行符以外的空格
          this[formKey][fieldKey] = this[formKey][fieldKey].replace(/[^\S\r\n]+/g, "")
        }
      }
    },

  }
</script>

<style scoped lang="less">
  .search-row{
    padding-left: 0;
    padding-right: 0;
  }
  .search-row .label{
    width: 90px;
    text-align: right;
  }
  /deep/.el-date-editor .el-range-separator{
    line-height: 25px;
  }
  .search-row-item{
    /*padding-right: 30px;*/
    flex: 1;
    max-width: 280px;
  }
  /deep/.checkRes{
    .el-checkbox-button__inner{
      padding-left: 10px!important;
      padding-right: 10px!important;
    }
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

  .detail-item{
    display: flex;
    margin-bottom: 20px;
    .lab{
      width: 160px;
      text-align: right;
    }
    .val{
      flex: 1;
      text-align: left;
    }
  }
  /deep/.detail-dialog{
    .el-dialog{
      width: 95%!important;
      max-width: 1300px;
    }
  }
</style>
