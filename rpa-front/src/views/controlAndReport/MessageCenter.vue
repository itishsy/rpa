<template>
  <div class="content spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData"></breadcrumb>
    <div style="padding: 20px 20px 0 20px">
      <palTab :tabs="tabs" v-model="tabActive" :type="2" @change="tabChange"></palTab>

      <!--运营消息-->
      <div v-show="tabActive == 0">
        <div class="search-row display-flex">
          <el-input class="w-300" v-model="search1.searchText" placeholder="请输入关键字搜索" clearable></el-input>
          <div class="ml-50" style="padding-top: 7px;">
            <el-checkbox-group v-model="search1.statuses" size="medium" @change="getTable1">
              <el-checkbox class="sbStatusCeckbox" :label="1">启用</el-checkbox>
              <el-checkbox class="sbStatusCeckbox" :label="2">禁用</el-checkbox>
            </el-checkbox-group>
          </div>
          <div class="flex1 text-right">
            <el-button type="primary" class="search-btn w-60 mr-10" @click="getTable1">查询</el-button>
            <el-button class="btn--border-blue" plain @click="goAddAndView1('','1')">新建消息</el-button>
          </div>
        </div>
        <div>
          <!-- 表格 -->
          <dTable
            @fetch="getTable1"
            ref="dTable1"
            :tableHeight="tableHeight"
            :paging="true"
            :showSelection="false"
            :showIndex="true"
            rowKey="id"
          >
            <u-table-column
              prop="messageTypeName"
              label="消息类型"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="warnTypeName"
              label="消息项目"
              min-width="100"
              :show-overflow-tooltip="true"
            >
            </u-table-column>
            <u-table-column
              prop="messageStrategyName"
              label="消息时效"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="responseGradeName"
              label="响应等级"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="notificationObject"
              label="通知对象"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="notificationWay"
              label="通知方式"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="statusName"
              label="状态"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column fixed="right" label="操作" width="130">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="goAddAndView1(scope.row.id,'1')">详情</el-button>
                <el-button
                  type="text"
                  size="small"
                  @click="handleUpdateStatus(scope.row)"
                >{{scope.row.status===1?'停用':'启用'}}</el-button>
              </template>
            </u-table-column>
          </dTable>
        </div>
      </div>
      <!--运营消息-->
      <!--系统公告-->
      <div v-show="tabActive == 1">
        <div class="search-row display-flex">
          <el-input class="w-300" v-model="search2.searchText" placeholder="请输入关键字搜索" clearable></el-input>
          <div class="ml-50" style="padding-top: 7px;">
            <el-checkbox-group v-model="search2.statuses" size="medium" @change="getTable2">
              <el-checkbox class="sbStatusCeckbox" :label="0">草稿</el-checkbox>
              <el-checkbox class="sbStatusCeckbox" :label="1">已发布</el-checkbox>
            </el-checkbox-group>
          </div>
          <div class="flex1 text-right">
            <el-button type="primary" class="search-btn w-60 mr-10" @click="getTable2">查询</el-button>
            <el-button class="btn--border-blue" @click="goAddAndView2('','2')">新增公告</el-button>
          </div>
        </div>
        <div>
          <!-- 表格 -->
          <dTable
            @fetch="getTable2"
            ref="dTable2"
            :tableHeight="tableHeight"
            :paging="true"
            :showSelection="false"
            :showIndex="true"
            rowKey="id"
          >
            <u-table-column
              prop="warnTypeName"
              label="消息项目"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="messageTopic"
              label="消息主题"
              min-width="100"
              :show-overflow-tooltip="true"
            >
            </u-table-column>
            <u-table-column
              prop="warnName"
              label="消息维度"
              min-width="100"
              :show-overflow-tooltip="true"
            >
            <template slot-scope="scope">
              {{ scope.row.addrName +'-'+scope.row.businessTypeName }}
            </template>
            </u-table-column>
            <u-table-column
              prop="messageStrategyName"
              label="消息时效"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="notificationObject"
              label="通知对象"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="notificationWay"
              label="通知方式"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="statusName"
              label="状态"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column fixed="right" label="操作" width="130">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="goAddAndView2(scope.row.id,'2')">详情</el-button>
              </template>
            </u-table-column>
          </dTable>
        </div>
      </div>
      <!--系统公告-->
      <!-- 登录推送模板 -->
      <div v-show="tabActive == 2">
        <pushTemplate ref="pushTemplate"></pushTemplate>
      </div>
      <!-- 登录推送模板 -->
    </div>
  </div>
</template>

<script>
import dTable from '../../components/common/table'
import palTab from '../../components/common/pal-tab'
import pushTemplate from '../pushTemplate/index.vue'

export default {
  components: { dTable, palTab, pushTemplate },
  data() {
    return {
      tabs: ['运营消息', '系统公告', '登录推送模板'],
      tabActive: 0,
      search1: {
        statuses: [1],
        searchText: '',
      },
      search2: {
        statuses: [0],
        searchText: '',
      },
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 340 + 'px'
    },
  },
  created() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({ name: '消息配置' })
    this.$nextTick(() => {
      this.getTable1()
    })
  },
  mounted(){
    if(sessionStorage.getItem('messageCenterActive')){
      this.tabActive = Number(sessionStorage.getItem('messageCenterActive')) - 1
      if(this.tabActive == 0){
        this.getTable1()
      }else{
        this.getTable2()
      }
    }
  },
  watch: {},
  methods: {
    //S=运营消息
    getTable1() {
      let that = this
      let search = this.search1
      var params = [
        // { property: 'messageStatus', value: search.messageStatus },
        { property: 'searchText', value: search.searchText },
        { property: 'ruleType', value: 1 },
        { property: 'statuses', value: search.statuses },
      ]
      this.$refs.dTable1.fetch({
        query: params,
        method: 'post',
        url: '/policy/message/rule/page',
      })
    },
    //S=运营消息
    getTable2() {
      let that = this
      let search = this.search2
      var params = [
        // { property: 'messageStatus', value: search.messageStatus },
        { property: 'searchText', value: search.searchText },
        { property: 'ruleType', value: 2 },
        { property: 'statuses', value: search.statuses },
      ]
      this.$refs.dTable2.fetch({
        query: params,
        method: 'post',
        url: '/policy/message/rule/page',
      })
    },
    // 切换状态
    handleUpdateStatus(row) {
      console.log(row)
      let that = this
      var str = ''
      if(row.status == 1){
        str = '停用消息，则系统将不会再通知此类消息。'
      }else if(row.status == 2){
        str = '启用消息，则系统将根据规则生成消息并发出通知。'
      }else{
        str = '启用消息，则系统将根据规则生成消息并发出通知。'
      }
      this.$confirm(str,'提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
          that.udpateStatus(row.id,row.status)
        })
        .catch((err) => {
          console.log(err)
        })
    },
    udpateStatus(id,status){
      if(status == 1){
        status = 2
      }else if(status == 2){
        status = 1
      }else {
        status = 1
      }
      this.$http({
        url: `/policy/message/rule/udpateStatus/${id}/${status}`,
        method: 'post',
        data: {
          id: id,
          status: status
        },
      }).then((data) => {
        if (data && data.data.code == '200') {
          this.$message.success('操作成功')
          this.getTable1()
        }
      })
      .catch((err) => {
        
      })
    },
    goAddAndView1(id,type) {
      let query = id ? '&id=' + id : ''
      this.$router.push('/controlAndReport/addMessage?type='+ type  + query )
    },
    goAddAndView2(id,type) {
      let query = id ? '&id=' + id : ''
      this.$router.push('/controlAndReport/addMessage?type=' + type + query )
    },
    handleResponseGrade(val) {
      var responseGrade = [
        { id: 1, name: '一级响应' },
        { id: 2, name: '二级响应' },
        { id: 3, name: '三级响应' },
        { id: 4, name: '四级响应' },
      ]
      for (var i = 0; i < responseGrade.length; i++) {
        if (responseGrade[i].id === val) {
          return responseGrade[i].name
        }
      }
    },
    tabChange(index){
      if(index == 0){
        this.getTable1()
      }else if(index == 1){
        this.getTable2()
      }else if(index == 2){
        this.$refs.pushTemplate.getTableList()
      }
    }
  },
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
.byselfList {
  display: flex;
  align-items: center;
}
/deep/.spl-container{
  margin-top:0;
}
.btn--border-blue {
    border: 1px solid #3E82FF;
    color: #3E82FF;
}
</style>
