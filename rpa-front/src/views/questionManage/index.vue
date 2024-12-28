<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <el-row>
        <el-col :span="24">
          <div class="search-row display-flex">
            <div style="display: flex;width: 100%;">
              <el-input @keyup.enter.native="search" v-model.trim="keyword" clearable placeholder="请输入关键字搜索" style="width:250px;" ></el-input>

              <el-button type="primary" size="mini" @click="search" class="ml-20">查询</el-button>

              <div class="flex1 text-right">
                <el-button type="primary" size="mini" class="ml-20 mr-10" @click="goAddOrEditQuestion()">添加问题</el-button>
                <el-button class="btn--border-blue s-btn" @click="viewCollectUserProblem()">查看问题搜索记录</el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      <div>
        <dTable @fetch="getTableData" ref="table" style="margin-top: 0;" :tableHeight="tableHeight"  :showIndex='true' :showSelection='false' row-key="id" row-id="id">
          <u-table-column prop="question" label="问题" min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column label="操作" align="left" width="250" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="small" class="text-blue"  @click="goQuestionPage(scope.row.id)">查看</el-button>
              <el-button type="text" size="small" class="text-blue" style="margin-left: 20px;"  @click="goAddOrEditQuestion(scope.row.id)">编辑</el-button>
              <el-button type="text" size="small" class="text-red" style="margin-left: 20px;" @click="handleDel(scope.row.id)">删除</el-button>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>

    <!-- 查看问题搜索记录 -->
    <el-dialog title="搜索记录" :visible.sync="searchRecord.show"
               width="1000px" class="cust-dialog" :show-close="true" :close-on-click-modal="false">
      <div>
        <dTable @fetch="getSearchRecordData" ref="searchRecordTable" style="margin-top: 0;" :tableHeight="tableHeight2"  :showIndex='true' :showSelection='false' row-key="id" row-id="id">
          <u-table-column prop="question" label="问题" min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="searchTimes" label="搜索次数" width="200" :show-overflow-tooltip="true"></u-table-column>
        </dTable>
      </div>
    </el-dialog>

  </div>
</template>
<script>

  import dTable from '../../components/common/table'
  export default {
    components: { dTable },
    data () {
      return {
        pathData: [],
        keyword: '',
        searchRecord: {
          show: false,
          isLoad: false
        }
      }
    },
    computed: {
      tableHeight: function () {
        return this.$global.bodyHeight - 270 + 'px'
      },
      tableHeight2: function () {
        var h = this.$global.bodyHeight - 300
        h = h>300?h:300
        return  h + 'px'
      }
    },
    watch: {

    },
    created () {
      let that = this
      let tabs = this.$store.state.tabs
      if (tabs) {
        this.pathData = this.$global.deepcopyArray(
          tabs[this.$route.meta.parentPath]
        )
      }
      this.pathData.push({ name: '常见问题管理' })
      this.$nextTick(() => {
        that.getTableData()
      })
    },
    mounted () {
    },
    methods: {
      getTableData () {
        var params = [
          { property: 'keyword', value: this.keyword }
        ]
        this.$refs.table.fetch({
          query: params,
          method: 'post',
          url: '/policy/listCommonProblem'
        })
      },
      search () {
        this.getTableData()
      },

      // 添加问题
      goAddOrEditQuestion(id){
        let query = id?('?id='+id):''
        this.$router.push('/questionManage/addOrEdit'+query)
      },

    //  删除问题
      handleDel(id){
        let that = this
        this.$confirm('是否确定删除该问题？', '提示', {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          showClose: false,
          customClass: 'pal-confirm',
          type: 'warning'
        }).then(() => {
          that.$http({
            url: '/policy/removeAnswer?id=' + id,
            method: 'post',
            data: {}
          }).then(({data}) => {
            that.$message.success("操作成功")
            that.getTableData()
          }).catch((data) => {
          })
        }).catch(() => {})
      },
      goQuestionPage(id){
        this.$router.push('/questionDetail?id=' + id)
      },

    //  查看问题搜索记录
      viewCollectUserProblem(){
        this.searchRecord.show = true
        if(!this.searchRecord.isLoad){
          this.$nextTick(() => {
            this.getSearchRecordData()
          })
        }
      },
      getSearchRecordData(){
        let that = this
        this.$refs.searchRecordTable.fetch({
          query: [],
          method: 'post',
          url: '/policy/listCustomerProblem',
          callback: function () {
            that.searchRecord.isLoad = true
          }
        })
      },
    }
  }
</script>
<style lang="less" scoped>

</style>
