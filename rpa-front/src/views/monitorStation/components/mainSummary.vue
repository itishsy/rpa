<template>
  <div>
    <div style="padding: 0px 20px 0 20px">
      <div class="search-row clearfix">
        <el-row>
          <el-col :span="7" class="display-flex">
            <span class="label">组织名称：</span>
            <el-select
              v-model="search.orgName"
              class="search-row-item"
              clearable
              filterable
              placeholder="请选择组织名称">
              <el-option v-for="(item, index) in options.declareAccount" :key="index" :label="item" :value="item"></el-option>
            </el-select>
          </el-col>
          <el-col :span="7" class="display-flex">
            <span class="label">纳税人识别号：</span>
            <div
              class="date-editor-div search-row-item"
              style="display: flex; align-items: center; padding-right: 30px"
            >
              <el-input
                v-model.trim="search.taxNumber"
                placeholder="请输入纳税人识别号"
                clearable
                size
                class="width-260"
              ></el-input>
            </div>
          </el-col>
          <el-col :span="7" class="display-flex">
            <div
              class="date-editor-div search-row-item"
              style="display: flex; align-items: center; padding-right: 0px"
            >
              <el-input
                v-model.trim="search.keyWord"
                placeholder="请输入社保号或公积金号"
                clearable
                size
                class="width-260"
              ></el-input>
            </div>
          </el-col>
          <el-col :span="3" class="text-right">
            <el-button class="ml-15 w-60" type="primary" @click="getTableData"
            >查询</el-button
            >
          </el-col>
        </el-row>
      </div>
      <!-- 表格 -->
      <dTable @fetch="getTableData" ref="dTable" :paging="true" :showSelection="true" :showIndex="true" rowKey="id" :cancelMinHeight="true">
        <u-table-column prop="orgName" label="组织名称" min-width="150" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="taxNumber" label="纳税人识别号" min-width="150" :show-overflow-tooltip="true"></u-table-column>
        <u-table-column prop="socialAccounts" label="单位社保号" min-width="150" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <p>{{handleAccount(scope.row.socialAccounts)}}</p>
          </template>
        </u-table-column>
        <u-table-column prop="accfundAccounts" label="单位公积金号" min-width="150" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <p>{{handleAccount(scope.row.accfundAccounts)}}</p>
          </template>
        </u-table-column>
        <u-table-column fixed="right" label="操作" :width="100">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleAddOrUpdateOrView('view', scope.row)">查看</el-button>
          </template>
        </u-table-column>
      </dTable>
    </div>

    <!--查看-->
    <viewEditDeclareAccount ref="viewEditDeclareAccount" :detailData="setForm"></viewEditDeclareAccount>

  </div>
</template>
<script>
import dTable from '../../../components/common/table'
import viewEditDeclareAccount from '@/views/declareAccount/components/viewEditDeclareAccount'
export default {
  inject: ['reload'],
  components: { dTable, viewEditDeclareAccount },
  props: ['configData'],
  data () {
    return {
      search: {
        taxNumber: '',
        orgName: '',
        keyWord: ''
      },
      options: {
        declareAccount: []
      },
      setForm: null
    }
  },
  watch: {},
  filters: {

  },
  computed: {},
  created () {
  },
  mounted () {
    this.getOrganizeList()
    this.$nextTick(() => {
      this.getTableData()
    })
  },
  methods: {
    // 获取组织
    getOrganizeList () {
      let that = this
      this.options.version = []
      this.$http({
        url: '/robot/monitor/city/getOrganizeList?addressName=' + that.configData.addrName,
        method: 'post',
        data: {}
      }).then(({ data }) => {
        that.options.declareAccount = data.data.organizeList
        that.$emit('tabCountCallback', { totalCount: data.data.organizeList.length })
      }).catch(() => {
      })
    },
    getTableData () {
      let that = this
      let search = this.search
      var params = [
        { property: 'addressName', value: that.configData.addrName },
        { property: 'orgName', value: search.orgName },
        { property: 'taxNumber', value: search.taxNumber },
        { property: 'keyWord', value: search.keyWord },
        { property: 'id', value: that.configData.id }
      ]
      this.$refs.dTable.fetch({
        query: params,
        method: 'post',
        url: '/policy/declareAccount/principalPage'
      })
    },
    handleAccount (arr) {
      var arr2 = arr || []
      var strArr = []
      arr2.map(item => {
        strArr.push(item.accountNumber)
      })
      console.log(strArr)
      return strArr.join('；')
    },
    // 查看
    handleAddOrUpdateOrView (type, item) {
      this.setForm = {
        show: true,
        type: '',
        orgName: item.orgName,
        taxNum: item.taxNumber,
        addrName: item.addressName,
        addrId: item.addressId,
        socialChecked: !(item.socialAccounts && item.socialAccounts.length > 0),
        accfundChecked: !(item.accfundAccounts && item.accfundAccounts.length > 0),
        socialList: item.socialAccounts,
        accfundList: item.accfundAccounts,
        custId: '',
        id: '',
        isDisabled: true
      }
      this.$refs.viewEditDeclareAccount.setForm.show = true
    }
  }
}
</script>
<style lang="less" scoped>
</style>
