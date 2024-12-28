<template>
  <div>
    <!--操作记录-->
    <el-drawer title="操作记录" :visible.sync="show" :wrapperClosable="false"
               custom-class="spl-filter-drawer detail-drawer"
               :show-close="true" size="100%">
      <div class="drawer-body" style="padding-top: 20px;margin: 0 20px;" v-if="rowData && show">
        <el-row>
          <el-col :span="12" class="row-item">
            <p class="lab">单位名称：</p>
            <p class="sel">{{rowData.orgName}}</p>
          </el-col>
          <el-col :span="12" class="row-item">
            <p class="lab">单位{{rowData.businessType==1?'社保':'公积金'}}号：</p>
            <p class="sel">{{rowData.accNumber}}</p>
          </el-col>
        </el-row>

        <dTable :data="tableData" v-loading="isLoading" style="margin-top: 0" :tableHeight="tableHeight"
                :showIndex="false" :showSelection="false" :paging="false" :showBottom="false">
          <u-table-column prop="status" label="操作" width="80">
            <template slot-scope="scope">
              <p>{{scope.row.status == 1 ? '启用' : '停用'}}</p>
            </template>
          </u-table-column>
          <u-table-column prop="remark" label="原因备注" min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="createName" label="操作人" width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="createTime" label="操作时间" width="180">
            <template slot-scope="scope">
              <p>{{ $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') }}</p>
            </template>
          </u-table-column>
        </dTable>
      </div>
      <div class="drawer-footer-buts">
        <el-button size="small" class="w-80 btn--border-blue" @click="show = false">关闭</el-button>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import dTable from '../../../components/common/table'
export default {
  name: 'operateRecordsDrawer',
  props: [],
  components: { dTable },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 150 + 'px'
    }
  },
  watch: {},
  data () {
    return {
      show: false,
      tableData: [],
      rowData: null,
      isLoading: false
    }
  },
  created () {
  },
  mounted () {
  },
  methods: {
    init (row) {
      this.rowData = row
      this.isLoading = true
      this.$http({
        url: `/robot/app/client/getAccountStatusHistory`,
        method: 'post',
        params: {
          taskCode: row.taskCode
        }
      }).then((data) => {
        this.isLoading = false
        if (data.data.code == 200) {
          this.tableData = data.data.data
          console.log(this.tableData)
          this.show = true
        }
      }).catch(() => {
        this.isLoading = false
      })
    }
  }
}
</script>
<style lang="less" scoped>
/deep/ .detail-drawer {
  width: 800px !important;
}
.row-item {
  display: flex;
  margin-bottom: 15px;

  .lab {
    width: 100px;
    text-align: right;
  }

  .sel {
    flex: 1;
    margin-left: 5px;
    word-break: break-all;
  }
}
</style>
