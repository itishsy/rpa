<template>
  <div class="spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData"></breadcrumb>
    <el-row class="pt-15">
      <el-col :span="7" class="display-flex">
        <span class="lab">类型：</span>
        <span class="sel">{{getStatusStr2(type)}}</span>
      </el-col>
      <el-col :span="6" class="display-flex">
        <span class="lab">批次号：</span>
        <span class="sel">{{ batchNum }}</span>
      </el-col>
    </el-row>
    <div class="pt-15">
      <!-- 搜索栏 -->
      <el-row style="padding-bottom: 10px;">
        <el-col :span="6" class="display-flex">
          <span class="lab">企业名称：</span>
          <div class="sel">
            <el-input
              style="width: 100%;"
              v-model="searchData.comName"
              clearable
              placeholder="请输入企业名称"
            ></el-input>
          </div>
        </el-col>
        <el-col :span="6" class="display-flex">
          <span class="lab">接口执行状态：</span>
          <div class="sel">
            <el-select
              style="width:100%;"
              v-model="searchData.isRequest"
              class="search-row-item"
              clearable
              filterable
              placeholder="请选择类型"
            >
              <el-option label="成功" :value="0"></el-option>
              <el-option label="失败" :value="1"></el-option>
            </el-select>
          </div>
        </el-col>
        <el-col :span="6" class="display-flex">
          <span class="lab">是否符合：</span>
          <div class="sel">
            <el-select
              style="width:100%;"
              v-model="searchData.status"
              class="search-row-item"
              clearable
              filterable
              placeholder="请选择类型"
            >
              <el-option label="是" :value="0"></el-option>
              <el-option label="否" :value="1"></el-option>
            </el-select>
          </div>
        </el-col>
        <el-col :span="6" class="display-flex">
          <span class="lab">请求结果：</span>
          <div class="sel">
            <el-input
              v-model="searchData.result"
              clearable
              placeholder="请输入请求结果"
              style="width:100%;"
            ></el-input>
          </div>
        </el-col>
        <el-col :span="18" class="display-flex">
          <span class="lab">身份证：</span>
          <div class="sel">
            <el-input
              type="textarea"
              :rows="2"
              placeholder="请输入身份证一行一个"
              v-model="searchData.idCards"
              style="width: 210px;"
            ></el-input>
            <span style="margin-left: 10px;">已输入{{ idCardSize }}条</span>
          </div>
        </el-col>
        <el-col :span="6" style="text-align: right;">
          <el-button
            type="primary"
            class="s-btn"
            @click="getTableData"
            @keyup.enter.native="getTableData"
          >查询</el-button>
        </el-col>
      </el-row>
      <!-- 表格 -->
      <dTable
        @fetch="getTableData"
        ref="dTable"
        :tableHeight="tableHeight"
        :paging="true"
        :showSelection="true"
        :showIndex="true"
        rowKey="id"
      >
        <u-table-column prop="comName" label="企业名称" min-width="60" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="name" label="姓名" min-width="30" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="idCard" label="身份证" min-width="50" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="status" label="是否符合" min-width="50" :show-overflow-tooltip="true">
          <template scope="scope">
            {{ getStatusStr11(scope.row.status)}}
          </template>
        </u-table-column>
        <u-table-column
          prop="isRequest"
          label="接口验证状态"
          min-width="30"
          :show-overflow-tooltip="true">
          <template scope="scope">
            {{ getStatusStr(scope.row.isRequest)}}
          </template>
        </u-table-column>
        <u-table-column prop="result" label="执行结果" min-width="90" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="createTime" label="执行时间" width="180" :show-overflow-tooltip="true">
          <template
            scope="scope"
          >{{ scope.row.createTime ? $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') : '' }}</template>
        </u-table-column>
        <u-table-column fixed="right" label="操作" width="150">
          <template slot-scope="scope">
            <el-button type="text" class="s-btn" @click="checking(scope.row)">单个筛查</el-button>
          </template>
        </u-table-column>
      </dTable>
      <div style="position: absolute;left:20px;bottom: 20px;z-index: 999;">
        <el-button type="primary" class="s-btn" @click="doExport(2)">导出清单</el-button>
        <el-button type="primary" class="s-btn" @click="doExport(1)">导出原表格</el-button>
      </div>
    </div>
  </div>
</template>
  <script>
import dTable from '@/components/common/table'
export default {
  name: '',
  components: { dTable },
  props: [''],
  data() {
    return {
      pathData: [],
      searchData: {
        batchNum: '',
        comName: '',
        idCards: '',
        status: '',
        isRequest: '',
        result: '',
      },
      rules: {
        customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
        saleName: [{ required: true, message: '请输入销售人员', trigger: 'blur' }],
        status: [{ required: true, message: '请选择申报状态', trigger: 'change' }],
      },
      batchNum: '',
      custId: '',
      ossPath: '',
      type: '',
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 380 + 'px'
    },
    getStatusListName() {
      return function (dictCode) {
        var str = ''
        this.statusList.forEach((item) => {
          if (item.dictCode == dictCode) {
            str = item.dictName
          }
        })
        return str
      }
    },
    getStatusStr2() {
      return function (type) {
        if (type == 1) {
          return '贫困'
        } else if (type == 2) {
          return '失业'
        } else if (type == 3) {
          return '基础数据'
        }
        return ''
      }
    },
    getStatusStr(){
      return function(type){
        if(type === 0){
          return '成功'
        }else if(type === 1){
          return '失败'
        }
        return ''
      }
    },
    getStatusStr11(){
      return function(type){
        if(type === 0){
          return '是'
        }else if(type === 1){
          return '否'
        }
        return ''
      }
    },
    idCardSize(){
      var num = 0;
      this.searchData.idCards.split('\n').forEach(item=>{
        if (item.length > 0) num++;
      })
      return num
    },
  },
  watch: {},
  created() {
    let tabs = this.$store.state.tabs
    if (this.$route.query.batchNum) {
      this.batchNum = this.$route.query.batchNum
    }
    if (this.$route.query.custId) {
      this.custId = this.$route.query.custId
    }
    if (this.$route.query.ossPath) {
      this.ossPath = this.$route.query.ossPath
    }
    if (this.$route.query.type) {
      this.type = this.$route.query.type
    }
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({ name: '客户详情' })
  },
  beforeMount() {},
  mounted() {
    document.addEventListener('keydown', (e) => {
      let key = e.keyCode
      if (key == 13) {
        this.getTableData()
      }
    })
    this.getTableData()
  },
  methods: {
    getTableData() {
      let params = [
        { property: 'batchNum', value: this.batchNum }, //this.batchNum },
        { property: 'comName', value: this.searchData.comName },
        { property: 'idCards', value: this.searchData.idCards.split('\n').filter(item=>item.trim() != ''), },
        { property: 'isRequest', value: this.searchData.isRequest },
        { property: 'status', value: this.searchData.status },
        { property: 'result', value: this.searchData.result },
      ]
      this.$refs.dTable.fetch({
        query: params,
        method: 'post',
        url: '/subsidy/social/excelReadHistory/pageExcelTask',
      })
    },
    doExport(type) {
      if (this.ossPath && type == 1) {
        this.$global.showLoading('处理中')
        this.$downloadFile(`/policy/sys/file/download/${this.ossPath}`, 'get', {}, '', '', () => {
          this.$global.closeLoading()
        })
        return
      }
      let url = '/subsidy/social/socialCost/excelHistory'
      this.$global.showLoading('处理中')
      this.$downloadFile(url, 'post', { batchNumOne: this.batchNum, custId: this.custId }, this.$global.EXCEL, '', () => {
        this.$global.closeLoading()
      })
    },
    checking(row) {
        this.$global.showLoading()
      this.$http({
        url: `/subsidy/social/socialCost/retryOne`,
        method: 'get',
        params: {
          batchNum: row.batchNum,
          name: row.name,
          idCard: row.idCard,
        },
      })
        .then(({ data }) => {
          this.$global.closeLoading()
          var json = JSON.stringify(data.data)
          this.$alert(`${json}`,'操作结果', {
            confirmButtonText: '确定',
            callback: (action) => {},
          })
        })
        .catch(() => {
          this.$global.closeLoading()
        })
    },
  },
}
</script>
<style lang='less' scoped>
.lab {
  width: 110px;
  text-align: right;
  line-height: @inputHeight;
  // flex:1;
  flex-shrink: 0;
}
.sel {
  flex: 1;
  padding-right: 20px;
  line-height: @inputHeight;
}
.display-flex {
  padding-bottom: 10px;
}
</style>
