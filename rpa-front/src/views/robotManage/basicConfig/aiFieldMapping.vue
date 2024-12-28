<template>
  <!-- 报盘文件设置 -->
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <el-row>
        <el-col :span="10">
          <div class="search-row display-flex">
            <el-input
              v-model.trim="searchText"
              placeholder="请输入关键字搜索"
              style="width: 300px"
              @keyup.enter.native="getTableData"
              clearable
              v-filter-symbols
            ></el-input>
           <span class="lh-com ml-60"><el-checkbox :true-label="0" :false-label="1" v-model="status">只看禁用（{{disableCount}}）</el-checkbox></span>
          </div>
        </el-col>
        <el-col :span="14">
          <div class="search-row display-flex" style="justify-content: flex-end;">
            <el-button type="primary" class="w-60" size="mini" @click="getTableData">查询</el-button>
            <el-button class="btn--border-blue w-60" size="mini" @click="goUpate()">新增</el-button>
          </div>
        </el-col>
      </el-row>
      <div>
        <dTable
          @fetch="getTableData"
          ref="table"
          style="margin-top: 0"
          :tableHeight="tableHeight"
          :showIndex="false"
          :showSelection="false"
          :useVirtual="false"
          :showBorder="true"
          :span-method="arraySpanMethod"
        >
          <u-table-column
            prop="name"
            label="字段名称"
            min-width="150"
          >
            <template slot="header" slot-scope="scope">
              <span class="required-pre">字段名称</span>
            </template>
            <template slot-scope="scope">
              <el-tooltip effect="dark" :content="scope.row.name" placement="top">
                <span class="text-blue text-underline-hover f-cursor" @click="goUpate(scope.row.uuid)">{{ scope.row.name }}</span>
              </el-tooltip>
            </template>
          </u-table-column>
          <u-table-column
            prop="insuredName"
            label="映射组"
            width="80"
          >
            <template slot-scope="scope">
              <span>{{scope.row.itemBases[0].index}}</span>
            </template>
          </u-table-column>
          <u-table-column
            prop="customerName"
            label="字段填值"
            min-width="200"
          >
            <template slot="header" slot-scope="scope">
              <span class="required-pre">字段填值</span>
            </template>
            <template slot-scope="scope">
              <el-tooltip effect="dark" :content="handleInputItems(scope.row.itemBases[0].inputItems)" placement="top">
                <span>{{handleInputItems(scope.row.itemBases[0].inputItems, 'limit')}}</span>
              </el-tooltip>
            </template>
          </u-table-column>
          <u-table-column
            prop="customerName"
            label="申报值（黑色即优先匹配）"
            min-width="200"
          >
            <template slot="header" slot-scope="scope">
              申报值（黑色即优先匹配）
            </template>
            <template slot-scope="scope">
              <el-tooltip effect="dark" :content="handleDeclareItems(scope.row.itemBases[0].declareItems)" placement="top">
                <div v-html="handleDeclareItems(scope.row.itemBases[0].declareItems, 'limit')"></div>
              </el-tooltip>
            </template>
          </u-table-column>
          <u-table-column
            prop="declareAccount"
            label="状态"
            width="100"
          >
            <template slot-scope="scope">
              <div>{{scope.row.itemBases[0].status===1?'启用':'禁用'}}</div>
            </template>
          </u-table-column>
          <u-table-column label="操作" fixed="right" width="120">
            <template slot-scope="scope">
              <el-button type="text" size="small" class="text-blue" v-if="scope.row.itemBases[0].status===1" @click="goUpate(scope.row.uuid, scope.row.itemBases[0].uuid)">编辑</el-button>
              <el-button type="text" size="small" class="text-blue text-underline-hover" v-if="scope.row.itemBases[0].status===0" @click="doUpdateStatus(scope.row.itemBases[0].status, scope.row.itemBases[0].uuid)">启用</el-button>
              <el-button type="text" size="small" class="text-gray" v-else @click="doUpdateStatus(scope.row.itemBases[0].status, scope.row.itemBases[0].uuid)">禁用</el-button>
            </template>
          </u-table-column>

          <template slot="pagination-btns">
              <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn" @click="exportTableData">导出</el-button>
          </template>
        </dTable>
      </div>
    </div>
  </div>
</template>
<script>
import dTable from '../../../components/common/table'
export default {
  components: { dTable },
  data () {
    return {
      pathData: [],
      searchText: '',
      status: 1, // 状态  1：启用，0：禁用
      disableCount: 0,
      tableData: []
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    }
  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
    }
    this.pathData.push({ name: 'AI值映射' })
    this.getDisableCount()
    this.$nextTick(() => {
      this.getTableData()
    })
  },
  mounted () {},
  methods: {
    // 列表查询
    getTableData () {
      let that = this
      var params = [
        { property: 'searchText', value: this.searchText },
        { property: 'status', value: this.status === 1 ? null : this.status }
      ]
      this.$refs.table.fetch({
        query: params,
        method: 'post',
        url: '/policy/ai/fieldMapping/page',
        callback: function (res) {
          that.tableData = res.rows
        }
      })
    },
    // 获取统计禁用记录
    getDisableCount () {
      this.$http({
        url: '/policy/ai/fieldMapping/getDisableCount',
        method: 'post'
      }).then((data) => {
        if (data.data.code === 200) {
          this.disableCount = data.data.data
        }
      }).catch(() => {
      })
    },
    // 启用、禁用
    doUpdateStatus (status, itemBaseUuid) {
      let that = this
      // status: 状态  1：启用，0：禁用
      let changeStatus = null
      let msg = ''
      if (status === 1) {
        changeStatus = 0
        msg = '一旦禁用，相关功能将无法引用此映射组信息，是否继续？'
      } else {
        changeStatus = 1
        msg = '是否确定启用该字段映射组信息？'
      }
      this.$confirm(msg, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        that.$http({
          url: `/policy/ai/fieldMapping/updateStatus/${itemBaseUuid}/${changeStatus}`,
          method: 'post'
        }).then((data) => {
          if (data.data.code === 200) {
            that.$message.success('操作成功')
            that.getTableData()
            that.getDisableCount()
          }
        }).catch(() => {
        })
      }).catch(() => {})
    },
    handleInputItems (items, type) {
      let arr = []
      items.map(item => {
        arr.push(item.fieldValue)
      })
      let text = arr.join('、')
      if (type === 'limit' && text.length > 200) {
      //   超出200字显示省略号
        return text.slice(0, 200) + '...'
      }
      return arr.join('、')
    },
    handleDeclareItems (items, type) {
      let arr = []
      let textLen = 0
      let item = null
      for (let i = 0; i < items.length; i++) {
        item = items[i]
        if (type === 'limit') {
          //   超出200字显示省略号
          textLen += item.fieldValue.length
          if (textLen > 200) {
            return arr.join('、') + '...'
          }
          if (item.strategy === 1) {
            arr.push('<span>' + item.fieldValue + '</span>')
          } else {
            arr.push('<span class="text-gray">' + item.fieldValue + '</span>')
          }
        } else {
          arr.push(item.fieldValue)
        }
      }
      return arr.join('、')
    },
    // 合并行
    arraySpanMethod ({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 0) {
        if (row.itemBases[0].index === 1) {
          let rowspan = 0
          this.tableData.map(item => {
            if (item.uuid === row.uuid) {
              ++rowspan
            }
          })
          return {
            rowspan: rowspan,
            colspan: 1
          }
        } else {
          return {
            rowspan: 0,
            colspan: 0
          }
        }
      }
    },
    // 去新增 社保方案
    goUpate (uuid, itemUuid) {
      let query = (uuid ? ('?uuid=' + uuid) : '') + (itemUuid!=undefined?('&itemUuid=' + itemUuid):'')
      this.$router.push('/basicConfig/aiFieldMappingUpdate' + query)
    },
    // 导出表格
    exportTableData () {
      if (this.tableData.length === 0) {
        this.$message.warning('无数据可导出')
        return
      }
      let params = this.$refs.table.getParamsQuery()
      this.$downloadFile(
        '/policy/ai/fieldMapping/export',
        'post',
        {
          query: params
        },
        this.$global.EXCEL
      )
    }
  }
}
</script>
<style lang="less" scoped>
.text-underline-hover:hover{
  text-decoration: underline;
}
</style>
