<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <div class="search-row display-flex">
        <span class=label>帐号主体名称：</span>
        <el-input v-model="search.component" class="w-250" placeholder="请输入" clearable></el-input>

        <span class="label ml-60">机器人组件：</span>
        <el-input v-model="search.component" class="w-250" placeholder="请输入" clearable></el-input>
        <el-button type="primary" @click="search" class="ml-20">查询</el-button>
      </div>
      <div>
        <dTable :data="tableData" ref="table" :tableHeight="tableHeight" :showIndex='true' :showSelection='false' rowKey="component"
          :paging="false" >
          <u-table-column prop="component" label="帐号主体名称" min-width="180" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="version" label="帐号" min-width="120" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="version" label="机器人地区" min-width="120" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="common" label="RPA流程版本" min-width="120" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span class="text-btn" @click="handleView(scope.row, 1)">查看</span>
            </template>
          </u-table-column>
          <u-table-column prop="version" label="状态" min-width="80" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="releaseTime" label="服务开始日期" min-width="110" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{$moment(scope.row.releaseTime).format('YYYY-MM-DD')}}</span>
            </template>
          </u-table-column>
          <u-table-column prop="releaseTime" label="服务结束日期" min-width="110" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span>{{$moment(scope.row.releaseTime).format('YYYY-MM-DD')}}</span>
            </template>
          </u-table-column>
          <u-table-column prop="common" label="组件版本" min-width="80" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span class="text-btn" @click="handleView(scope.row, 2)">查看</span>
            </template>
          </u-table-column>

          <u-table-column prop="fileName" label="异常" min-width="80" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span class="text-btn" @click="handleView(scope.row, 3)"><i class="el-icon-warning font-18 mr-5" style="color: red;vertical-align: middle; margin-top: -2px;"></i>是</span>
              <!--<span class="text-btn">否</span>-->
            </template>
          </u-table-column>
          <u-table-column label="操作" fixed="right" align="left" width="80">
            <template slot-scope="scope">
            <el-button type="text" size="medium" class="text-blue text-blue2" @click="goHistoryPage(scope.row.component)">日志</el-button>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>
    <!-- 升级说明 -->
    <el-dialog title="" :visible.sync="viewInfo.show"
      width="650px" class="cust-dialog" :show-close="false" :close-on-click-modal="false">
      <div style="margin-top: -20px;">
        <p class="mb-20">帐号主体名称：{{viewInfo.companyName}}</p>

        <!--RPA流程版本-查看-->
        <div v-show="viewInfo.type===1">
          <dTable :data="viewInfo.data1" tableHeight="400px" :showIndex='false' :row-height="30" :showSelection='false' rowKey="component" :paging="false" >
            <u-table-column prop="component" label="RPA流程名称" min-width="120" :show-overflow-tooltip="true"></u-table-column>
            <u-table-column prop="v1" label="客户端版本" min-width="90" :show-overflow-tooltip="true"></u-table-column>
            <u-table-column prop="v2" label="最新版本" min-width="90" :show-overflow-tooltip="true"></u-table-column>
            <u-table-column label="操作" fixed="right" align="left" width="100">
              <template slot-scope="scope">
                <el-button type="text" size="medium" class="text-blue text-blue2" @click="handlePushUpdate(scope.row)">推送更新</el-button>
              </template>
            </u-table-column>
          </dTable>
        </div>
        <!--组件版本-查看-->
        <div v-show="viewInfo.type===2">
          <dTable :data="viewInfo.data2" tableHeight="400px" :showIndex='false' :row-height="30" :showSelection='false' rowKey="component" :paging="false" >
            <u-table-column prop="component" label="组件名称" min-width="120" :show-overflow-tooltip="true"></u-table-column>
            <u-table-column prop="v1" label="客户端版本" min-width="90" :show-overflow-tooltip="true"></u-table-column>
            <u-table-column prop="v2" label="最新版本" min-width="90" :show-overflow-tooltip="true"></u-table-column>
            <u-table-column label="操作" fixed="right" align="left" width="100">
              <template slot-scope="scope">
                <el-button type="text" size="medium" class="text-blue text-blue2" @click="handlePushUpdate(scope.row)">推送更新</el-button>
              </template>
            </u-table-column>
          </dTable>
        </div>
        <!--异常-查看-->
        <div class="mt-30" v-show="viewInfo.type===3">
          <div class="abnormal-li" v-for="(item, index) in viewInfo.data3" :key="index">
            <span class="num">{{index+1}}</span>
            <p class="text">{{item.text}}</p>
          </div>
        </div>
      </div>
      <div class="text-right mt-30">
        <el-button size="small" type="primary" @click="viewInfo.show=false">我知晓!</el-button>
      </div>
    </el-dialog>
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
          component: ''
        },
        viewInfo: {
          show: false,
          type: 0, // 1、RPA流程版本-查看；2、组件版本-查看 3、异常-查看
          companyName: '',
          data1: [{component: 1, v1: 2, v2: 3},{component: 22, v1: 2, v2: 3},{component: 1, v1: 2, v2: 3},{component: 22, v1: 2, v2: 3},{component: 1, v1: 2, v2: 3},{component: 22, v1: 2, v2: 3},{component: 1, v1: 2, v2: 3},{component: 22, v1: 2, v2: 3}], // 1、RPA流程版本-查看；2、组件版本-查看 3、异常-查看
          data2: [{component: 1, v1: 2, v2: 3},{component: 22, v1: 2, v2: 3},{component: 1, v1: 2, v2: 3},{component: 22, v1: 2, v2: 3},{component: 1, v1: 2, v2: 3},{component: 22, v1: 2, v2: 3},{component: 1, v1: 2, v2: 3},{component: 22, v1: 2, v2: 3}], // 1、RPA流程版本-查看；2、组件版本-查看 3、异常-查看
          // data2: [], // 1、RPA流程版本-查看；2、组件版本-查看 3、异常-查看
          data3: [{text: '近二次RPA流程执行失败'},{text: 'wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww'},{text: '近二次RPA流程执行失败近二次RPA流程执行失败近二次RPA流程执行失败近二次RPA流程执行失败近二次RPA流程执行失败'}], // 1、RPA流程版本-查看；2、组件版本-查看 3、异常-查看
        }
      }
    },
    computed: {
      tableHeight: function () {
        return this.$global.bodyHeight - 280 + 'px'
      }
    },
    created() {
      let that = this
      let tabs = this.$store.state.tabs
      if (tabs) {
        this.pathData = tabs[this.$route.path]
      }
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
        }).catch(() => {})
      },
      handleView(row, type){
        // type: 1、RPA流程版本-查看；2、组件版本-查看 3、异常-查看
        this.viewInfo.type = type
        this.viewInfo.show = true
      },
      //推送更新
      handlePushUpdate (row){
        let that = this
        this.$confirm('是否确定推送更新？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          showClose: false,
          customClass: 'pal-confirm',
          type: 'warning'
        }).then(() => {
          that.$http({
            url: '/robot/version/enableStop?component='+row.component,
            method: 'post'
          }).then(({data}) => {
            that.$message.success("操作成功")
          }).catch(() => {
          })
        }).catch(() => {

        });
      },

      goHistoryPage(component){
        this.$router.push('/robotManage/executeHistory?component=' + component)
      },

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
  .abnormal-li{
    margin-top: 30px;
    display: flex;
    .num{
      width: 30px;
      height: 30px;
      line-height: 30px;
      border-radius: 50%;
      color: #FFFFFF;
      background-color: red;
      text-align: center;
    }
    .text{
      flex: 1;
      margin-left: 20px;
    }
  }
</style>
