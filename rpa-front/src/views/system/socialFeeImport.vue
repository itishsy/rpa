<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <el-row>
        <el-col :span="24">
          <div class="search-row text-center">
            <el-upload
              ref="upload"
              class="inlineBlock"
              :headers="$global.setUploadHeaders()"
              :action="$global.uploadFileUrl"
              :auto-upload="true"
              accept=".xls,.xlsx,.XLS,.XLSX"
              :show-file-list="false"
              multiple
              :on-progress="onProgressFile"
              :on-success="onSuccessFile"
            >
              <el-button type="success" class="w-100 s-btn">导入</el-button>
            </el-upload>
            <el-button type="primary" class="w-100 s-btn ml-30" @click="getTableData">刷新</el-button>
<!--            <el-button type="success" class="w-100 s-btn ml-30" @click="doExport">导出</el-button>
            <div class="mt-20">
              <span class="text-blue ml-30" v-if="successFileCount>-1">成功导入了 {{successFileCount}} 个文件</span>
            </div>-->
          </div>
        </el-col>
      </el-row>
      <div>
        <dTable @fetch="getTableData" ref="table" style="margin-top: 0;" :tableHeight="tableHeight" :showIndex='true'
                :showSelection='false'>
          <u-table-column prop="batchNum" label="批次号" min-width="180" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="fileName" label="文件名称" min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="status" label="状态" min-width="80" :show-overflow-tooltip="true">
            <template scope="scope">
              <span v-if="scope.row.status===0" class="text-gray">处理中</span>
              <span v-else-if="scope.row.status===1">导入成功</span>
            </template>
          </u-table-column>
          <u-table-column prop="costCount" label="符合贫困人员条数" min-width="140" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="costNum" label="符合人员数" min-width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="amount" label="金额" min-width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="createName" label="创建人名称" min-width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="createTime" label="创建时间" min-width="120" :show-overflow-tooltip="true">
            <template scope="scope">
              <span>{{ $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
            </template>
          </u-table-column>
          <u-table-column fixed="right" label="操作" width="120">
            <template slot-scope="scope">
              <el-button v-if="scope.row.status===1" type="text" size="small" @click="doExport(scope.row.batchNum, 1)">导出</el-button>
              <el-button v-if="scope.row.status===1" type="text" size="small" style="margin-left: 20px;" @click="doExport(scope.row.batchNum, 2)">导出清单</el-button>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>

  </div>
</template>
<script>
import dTable from '../../components/common/table'

export default {
  components: { dTable },
  data () {
    return {
      pathData: [],
      batchNum: '',
      fileList: [],
      selectFileCount: 0,
      timer: null
      // successFileCount: -1
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    }
  },
  watch: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.$nextTick(() => {
      this.getTableData()
    })
  },
  mounted () {
  },
  methods: {
    getTableData () {
      var params = []
      this.$refs.table.fetch({
        query: params,
        method: 'post',
        url: '/policy/social/socialCost/pageSocialCost'
      })
    },
    onProgressFile (event, file, fileList) {
      this.$global.showLoading('导入中...')
      this.selectFileCount = fileList.length
      // this.successFileCount = -1
    },
    onSuccessFile (response) {
      if (response.code === 200) {
        let item = response.data[0]
        this.fileList.push({
          fileId: item.fileID
        })
      }
      if (this.selectFileCount === this.fileList.length) {
        this.doSave()
      }
    },
    doSave () {
      let that = this
      let ids = []
      this.fileList.map(item => {
        ids.push(item.fileId)
      })
      this.$http({
        url: `/policy/social/socialCost/importSocialCost`,
        method: 'post',
        data: {
          fileIds: ids
        }
      }).then((data) => {
        if (data.data.code == 200) {
          this.batchNum = data.data.data
          // this.successFileCount = ids.length
          this.fileList = []
          this.$refs.upload.clearFiles()

          this.$global.closeLoading()
          this.$message.success('操作成功')
          this.getTableData()
          // 异步去查询是否保存成功
          /* this.timer = setInterval(() => {
            that.checkSave()
          }, 1000) */
        }
      }).catch(() => {
        this.fileList = []
        this.$refs.upload.clearFiles()
        this.$global.closeLoading()
      })
    },
    checkSave () {
      this.$http({
        url: '/policy/social/socialCost/pageSocialCost',
        method: 'post',
        data: { 'page': 1, 'start': 0, 'size': 20, 'query': [{ property: 'batchNum', value: this.batchNum }], 'sidx': '', 'sort': '' }
      }).then((data) => {
        if (data.data.code == 200 && data.data.data.records > 0) {
          clearInterval(this.timer)
          this.$global.closeLoading()
          this.$message.success('操作成功')
          this.getTableData()
        }
      }).catch(() => {
        this.$global.closeLoading()
        clearInterval(this.timer)
      })
    },
    doExport (batchNum, type) {
      let url = type === 1 ? '/policy/social/socialCost/exportSocialCost' : '/policy/social/socialCost/exportSocialCostAll'
      this.$downloadFile(
        url,
        'post',
        { batchNum: batchNum },
        this.$global.EXCEL
      )
    }
  }
}
</script>
<style lang="less" scoped>

</style>
