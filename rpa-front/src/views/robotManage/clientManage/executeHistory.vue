<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <div class="search-row" style="padding-left: 0;padding-right: 0;">
        <el-row>
          <el-col :span="6"><p class="display-flex pt-10">帐号主体名称：<span class="flex1">帐号主体名称</span></p></el-col>
          <el-col :span="6"><p class="display-flex pt-10 ml-10">帐号：<span class="flex1">13580540519</span></p></el-col>
          <el-col :span="5"><p class="display-flex pt-10 ml-10">机器人地区：<span class="flex1">广东广州</span></p></el-col>
          <el-col :span="7">
            <div class="display-flex">
              <span class="label">执行时间：</span>
              <div class="flex1">
                <el-date-picker
                  class="w-p100"
                  style="max-width: 250px"
                  v-model="search.timeArr"
                  type="daterange"
                  size="small"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期">
                </el-date-picker>
              </div>
              <el-button type="primary" @click="search" class="ml-20">查询</el-button>
            </div>
          </el-col>
        </el-row>

      </div>

      <div>
        <el-row>
          <el-col :span="14">
            <div>
              <dTable :data="tableData" ref="table" :tableHeight="tableHeight" :showIndex='true' :showSelection='false'
                      rowKey="component"
                      :paging="false">
                <u-table-column prop="component" label="机器人地区" min-width="120"
                                :show-overflow-tooltip="true"></u-table-column>
                <u-table-column prop="version" label="业务" min-width="80" :show-overflow-tooltip="true"></u-table-column>
                <u-table-column prop="version" label="RPA流程名称" min-width="120"
                                :show-overflow-tooltip="true"></u-table-column>
                <u-table-column prop="version" label="执行时间" min-width="100" :show-overflow-tooltip="true">
                  <template slot-scope="scope">
                    <span>{{$moment(scope.row.releaseTime).format('YYYY-MM-DD')}}</span>
                  </template>
                </u-table-column>
                <u-table-column prop="common" label="状态" min-width="80" :show-overflow-tooltip="true">
                  <template slot-scope="scope">
                    <!--<span>成功</span>-->
                    <span style="color: red">失败</span>
                  </template>
                </u-table-column>
                <u-table-column prop="releaseTime" label="附件" min-width="80" :show-overflow-tooltip="true">
                  <template slot-scope="scope">
                    <el-button type="text" size="medium" class="text-blue text-blue2">下载
                    </el-button>
                  </template>
                </u-table-column>
                <u-table-column label="执行明细" fixed="right" align="left" width="80">
                  <template slot-scope="scope">
                    <el-button type="text" size="medium" class="text-blue text-blue2">查看
                    </el-button>
                  </template>
                </u-table-column>
              </dTable>
            </div>
          </el-col>
          <el-col :span="10">
            <div class="detail-box">
              <p class="title">查看执行明细</p>
              <div class="view-content" :style="{height: viewHeight}">
                <div class="step-item" v-for="(item, index) in viewInfo.steps" :key="item.id">
                  <p class="step-title"><span class="step-name">{{item.stepName}}</span>{{$moment(item.startTime).format('YYYY-MM-DD HH:mm:ss')}}</p>
                  <p class="reason" v-if="item.result=='failed'">原因：{{item.resultInfo}}</p>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>

  import dTable from '../../../components/common/table'

  export default {
    components: {dTable},
    data() {
      return {
        pathData: [],
        tableData: [],
        search: {
          timeArr: []
        },
        viewInfo: {
          steps: [{
            "result": "success",
            "instId": "afa042e3326b4e4ab8dae82fc2b44576",
            "flowCode": "3888d68f5d7342cc816e3f3f49763c3e",
            "stepName": "进入登录页面",
            "startTime": "2022-06-16T06:51:58.000+0000",
            "id": 469036,
            "stepNumber": 4,
            "endTime": "2022-06-16T06:52:09.000+0000",
            "flowName": null,
            "resultInfo": null
          }, {
            "result": "success",
            "instId": "afa042e3326b4e4ab8dae82fc2b44576",
            "flowCode": "3888d68f5d7342cc816e3f3f49763c3e",
            "stepName": "确认证书1",
            "startTime": "2022-06-16T06:52:10.000+0000",
            "id": 469037,
            "stepNumber": 5,
            "endTime": "2022-06-16T06:52:11.000+0000",
            "flowName": null,
            "resultInfo": null
          }, {
            "result": "success",
            "instId": "afa042e3326b4e4ab8dae82fc2b44576",
            "flowCode": "3888d68f5d7342cc816e3f3f49763c3e",
            "stepName": "确认证书2",
            "startTime": "2022-06-16T06:52:12.000+0000",
            "id": 469038,
            "stepNumber": 6,
            "endTime": "2022-06-16T06:52:23.000+0000",
            "flowName": null,
            "resultInfo": null
          }, {
            "result": "success",
            "instId": "afa042e3326b4e4ab8dae82fc2b44576",
            "flowCode": "3888d68f5d7342cc816e3f3f49763c3e",
            "stepName": "敲入密码",
            "startTime": "2022-06-16T06:52:24.000+0000",
            "id": 469039,
            "stepNumber": 7,
            "endTime": "2022-06-16T06:52:33.000+0000",
            "flowName": null,
            "resultInfo": null
          }, {
            "result": "success",
            "instId": "afa042e3326b4e4ab8dae82fc2b44576",
            "flowCode": "3888d68f5d7342cc816e3f3f49763c3e",
            "stepName": "密码回车",
            "startTime": "2022-06-16T06:52:33.000+0000",
            "id": 469040,
            "stepNumber": 8,
            "endTime": "2022-06-16T06:52:35.000+0000",
            "flowName": null,
            "resultInfo": null
          }, {
            "result": "success",
            "instId": "afa042e3326b4e4ab8dae82fc2b44576",
            "flowCode": "3888d68f5d7342cc816e3f3f49763c3e",
            "stepName": "输入密码",
            "startTime": "2022-06-16T06:52:35.000+0000",
            "id": 469041,
            "stepNumber": 9,
            "endTime": "2022-06-16T06:52:37.000+0000",
            "flowName": null,
            "resultInfo": null
          }, {
            "result": "success",
            "instId": "afa042e3326b4e4ab8dae82fc2b44576",
            "flowCode": "3888d68f5d7342cc816e3f3f49763c3e",
            "stepName": "确认登陆",
            "startTime": "2022-06-16T06:52:38.000+0000",
            "id": 469042,
            "stepNumber": 10,
            "endTime": "2022-06-16T06:52:50.000+0000",
            "flowName": null,
            "resultInfo": null
          }, {
            "result": "success",
            "instId": "afa042e3326b4e4ab8dae82fc2b44576",
            "flowCode": "3888d68f5d7342cc816e3f3f49763c3e",
            "stepName": "点击进入操作页面",
            "startTime": "2022-06-16T06:52:50.000+0000",
            "id": 469043,
            "stepNumber": 11,
            "endTime": "2022-06-16T06:53:03.000+0000",
            "flowName": null,
            "resultInfo": null
          }, {
            "result": "success",
            "instId": "afa042e3326b4e4ab8dae82fc2b44576",
            "flowCode": "3888d68f5d7342cc816e3f3f49763c3e",
            "stepName": "点击财政社保",
            "startTime": "2022-06-16T06:53:03.000+0000",
            "id": 469044,
            "stepNumber": 12,
            "endTime": "2022-06-16T06:53:16.000+0000",
            "flowName": null,
            "resultInfo": null
          }, {
            "result": "failed",
            "instId": "afa042e3326b4e4ab8dae82fc2b44576",
            "flowCode": "3888d68f5d7342cc816e3f3f49763c3e",
            "stepName": "移动到公积金(新模式)",
            "startTime": "2022-06-16T06:53:16.000+0000",
            "id": 469045,
            "stepNumber": 13,
            "endTime": "2022-06-16T06:55:04.000+0000",
            "flowName": null,
            "resultInfo": "失败。info:Must provide a location for a move action."
          }, {
            "result": "failed",
            "instId": "afa042e3326b4e4ab8dae82fc2b44576",
            "flowCode": "3888d68f5d7342cc816e3f3f49763c3e",
            "stepName": "移动到公积金(新模式)",
            "startTime": "2022-06-16T06:55:04.000+0000",
            "id": 469046,
            "stepNumber": 13,
            "endTime": "2022-06-16T06:56:52.000+0000",
            "flowName": null,
            "resultInfo": "失败。info:Must provide a location for a move action."
          }, {
            "result": "failed",
            "instId": "afa042e3326b4e4ab8dae82fc2b44576",
            "flowCode": "3888d68f5d7342cc816e3f3f49763c3e",
            "stepName": "移动到公积金(新模式)",
            "startTime": "2022-06-16T06:56:52.000+0000",
            "id": 469047,
            "stepNumber": 13,
            "endTime": "2022-06-16T06:58:41.000+0000",
            "flowName": null,
            "resultInfo": "失败。info:Must provide a location for a move action."
          }]
        }
      }
    },
    computed: {
      tableHeight: function () {
        return this.$global.bodyHeight - 195 + 'px'
      },
      viewHeight: function () {
        return this.$global.bodyHeight - 270 + 'px'
      }
    },
    created() {
      let that = this
      let tabs = this.$store.state.tabs
      if (tabs) {
        this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
      }
      this.pathData.push({name: '执行查询'})
      this.$nextTick(() => {
        that.getTableData()
      })
    },
    mounted() {
    },
    methods: {
      //列表查询
      getTableData() {
        let that = this;
        this.$http({
          url: '/robot/version/list',
          method: 'post'
        }).then(({data}) => {
          that.tableData = data.data
        }).catch(() => {
        })
      },
      handleView(row, type) {
        // type: 1、RPA流程版本-查看；2、组件版本-查看 3、异常-查看
        this.viewInfo.type = type
        this.viewInfo.show = true
      },

    }
  }
</script>
<style lang="less" scoped>
  .text-blue2 {
    &:hover {
      text-decoration: underline;
    }
  }

  .search-row {
    .flex1 {
      word-break: break-all;
    }
  }

  .detail-box {
    border: 1px solid #dbdbdb;
    margin-left: 20px;
    .title {
      height: 35px;
      line-height: 35px;
      background-color: #eef0f7;
      border-bottom: 1px solid #dbdbdb;
      padding-left: 15px;
      font-weight: bold;
    }
    .view-content {
      padding: 20px;
      overflow-y: auto;
    }
    .step-item {
      color: #151515;
      /*border-left: 1px solid #DDDDDD;*/
      padding-left: 15px;
      padding-bottom: 25px;
      position: relative;
    }

    .step-item:before {
      display: inline-block;
      content: '';
      width: 10px;
      height: 10px;
      background: #DDDDDD;
      border-radius: 50%;
      position: absolute;
      top: 3px;
      left: -5px;
    }

    .step-item:after {
      display: inline-block;
      content: '';
      width: 1px;
      height: 100%;
      background: #DDDDDD;
      position: absolute;
      top: 3px;
      left: 0px;
    }

    .step-item:last-of-type:after {
      display: none;
    }

    .step-title {
      /*margin-top: -10px;*/
    }

    .step-name {
      display: inline-block;
      color: #458AE6;
      margin-right: 10px;
    }

    .reason {
      color: #E15E63;
      margin-top: 10px;
    }
  }
</style>
