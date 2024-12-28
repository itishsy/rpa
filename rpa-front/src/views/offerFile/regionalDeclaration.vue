<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <div class="search-row">
        <addrSelector
          v-model="formData.addrName"
          :addrArr="allAddr"
          style="line-height: 0px"
          @changeAddrSelect="changeAddrSelect"
        ></addrSelector>
        <el-button
          size="small"
          type="primary"
          class="ml-20 w-60"
          @click="handleSearch()">查询</el-button>
      </div>
      <div class="file-box">
        <palTab
          :tabs="tabs"
          v-model="tabActive"
          :type="2"
          @change="changeTab"
        ></palTab>
        <!-- <p class="p-padd" v-show="!formData.addrName">全部</p> -->
        <div class="empty-box" v-show="fileData.length == 0">
          <div>
            <img
              src="../../assets/images/empty-state-img.png"
              class="emptyState"
              alt=""
            />
            <p class="empty-p">
              暂时查不到该地区的申报模板，请稍后重试或联系客服
            </p>
          </div>
        </div>
        <div
          class="box-padd file-flex"
          v-loading="loading"
          element-loading-text="拼命加载中"
          element-loading-spinner="el-icon-loading"
          v-show="fileData.length > 0"
        >
          <div v-for="(item, index) in fileData" :key="index" class="all-box">
            <div
              :class="
                fileIndex == index
                  ? ['box-single', 'isHover-dow']
                  : ['box-single', '']
              "
              @mouseover="onHover(item, index)"
              @mouseout="onOut(item, index)"
            >
              <div style="position: relative">
                <img
                  src="../../assets/images/file-img.png"
                  class="img-file"
                  alt=""
                />
                <el-button
                  v-show="fileIndex == index"
                  size="small"
                  type="primary"
                  class="down-button w-dow"
                  @click="handleDownFile(item)"
                  >下载文件</el-button
                >
              </div>

              <p class="down-p">
                {{ item.addrName
                }}{{ item.bussinssType == 1 ? '社保' : '公积金'
                }}{{
                  item.changeType == '1'
                    ? '投保'
                    : item.changeType == '2'
                    ? '停保'
                    : item.changeType == '3'
                    ? '调整'
                    : item.changeType == '5'
                    ? '补缴'
                    : ''
                }}
              </p>
            </div>
          </div>
        </div>
        <!-- table -->
        <dTable
          v-show="fileData.length > 0"
          @fetch="getTableData"
          ref="table"
          style="margin-top: 0"
          :tableHeight="tableHeight"
          :showIndex="false"
          :showSelection="false"
          row-key="id"
          row-id="id"
        >
        </dTable>
        <!-- table -->
        <!-- 分页 -->
        <!-- 分页 -->
      </div>
    </div>
    <!-- 引入地区模板 -->
  </div>
</template>
<script>
import dTable from '../../components/common/table'
import palTab from '../../components/common/pal-tab'
import addrSelector from '../../components/common/addrSelector'
import { MessageBox } from 'element-ui'
export default {
  components: { addrSelector, palTab, dTable },
  data() {
    return {
      loading: false,
      pageInfo: {
        pageSize: 20,
        pageNo: 1,
      },
      fileIndex: undefined,
      fileData: [],
      tabs: ['社保', '公积金'],
      tabActive: 0,
      formData: {
        bussinssType: [], //1-社保或2-公积金
        addrId: '', //城市id
        quoteAddrId: '', //源地区
        quoteAddrName: '',
        addrName: '', //城市名字
        changeType: '', //申报类型
        changeTypeName: '', //申报类型名字
        columnSettings: [], //解析 excel 的字段集合
        fyRuleType: '', //费用年月显示规则
        status: '1', //有效无效
      },
      allAddr: [],
      pathData: [],
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 320 + 'px'
    },
  },
  created() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
    }
    this.getAddr(1)
    this.$nextTick(() => {
      this.formData.addrName = '全部'
      this.getTableData()
    })
  },
  mounted() {},
  watch: {},
  methods: {
    handleDownFile(item) {
      this.$downloadFile(
        `/agent/declareChange/excelChangeTemplate?businessType=${item.bussinssType}&address=${item.addrId}&change=${item.changeType}`,
        'get'
      )
    },
    handleSearch() {
      // if (!this.formData.addrName) {
      //   this.formData.addrName = '全部'
      // }
      this.getTableData()
    },
    onHover(item, index) {
      this.fileIndex = index
    },
    onOut(item, index) {
      this.fileIndex = -1
    },
    changeTab(index) {
      console.log('index', index)
      if (index == 0) {
        this.formData.bussinssType = ['1']
      } else {
        this.formData.bussinssType = ['2']
      }

      this.getTableData()
    },
    getTableData(pageChange, callback) {
      this.loading = true
      let start = this.pageInfo.pageSize * (this.pageInfo.pageNo - 1)
      var params = [
        { property: 'addrId', value: this.formData.addrId },
        { property: 'bussinssType', value: this.formData.bussinssType },
        { property: 'status', value: this.formData.status },
      ]
      sessionStorage.setItem(
        'offerFileSetting-params',
        JSON.stringify(this.formData)
      )
      var self = this
      this.$refs.table.fetch({
        pageChange: pageChange ? pageChange : '',
        query: params,
        method: 'post',
        url: '/policy/offerSettings/page',
        callback: callback
          ? callback
          : function (res) {
              self.fileData = res.rows
              this.loading = false
            },
      })
    },
    //改变城市
    changeAddrSelect(item) {
      this.formData.addrName = item.name
      this.formData.addrId = item.id
      this.getTableData()
    },
    // 获取参保地
    getAddr(type) {
      let that = this
      this.$http({
        url: 'policy/declareAddr/addrList',
        method: 'post',
      }).then(({ data }) => {
        this.allAddr = data.data
      })
    },
  },
}
</script>
<style lang="less" scoped>
.p-padd {
  padding: 15px;
}
.box-padd {
  padding: 15px;
}
.file-flex {
  display: flex;
  justify-content: start;
  flex-wrap: wrap;
  margin-left: 4%;
  .all-box {
    width: 12%;
    padding: 21px;
    padding-bottom: 0;
  }
  .isHover-dow {
    background: gainsboro;
  }
  .box-single {
    display: flex;
    flex-flow: column;
    align-items: center;
    margin-bottom: 40px;
    position: relative;
    padding: 10px 0px;
    .img-file {
      width: 86px;
      height: 67px;
    }
    .down-button {
      position: absolute;
      top: 56%;
      left: 0%;
    }
    .down-p {
      margin-top: 15px;
    }
  }
}
.ml-20 {
  margin-left: 20px;
}
.w-dow {
  width: 80px;
}
.file-box {
  /deep/ .page-box {
    margin-top: -330px;
  }
  /deep/ .el-table--border {
    border: none;
  }
}
.empty-box {
  width: 100%;
  height: 100%;
  padding: 60px 0px;
  display: flex;
  justify-content: space-around;
  align-items: center;
  align-content: center;
}
.emptyState {
  width: 250px;
  height: 250px;
}
.empty-p {
  margin-left: -27px;
  color: darkgray;
}
</style>
