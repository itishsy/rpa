<template>
  <div>
    <div class="search-row">
      <el-row>
        <el-col :span="7">
          <div class="display-flex" style="align-items: center;">
            <span class="ml-20 label">业务类型：</span>
            <el-checkbox-group v-model="formData1.businessType" @change="getTableList">
              <el-checkbox :label="1">社保</el-checkbox>
              <el-checkbox :label="2">公积金</el-checkbox>
            </el-checkbox-group>
          </div>
        </el-col>
        <el-col :span="10">
          <div class="display-flex" style="align-items: center;">
            <span class="ml-20 label">申报类型：</span>
            <el-checkbox-group v-model="formData1.declareTypes" @change="getTableList">
              <el-checkbox :label="1">增员</el-checkbox>
              <el-checkbox :label="2">减员</el-checkbox>
              <el-checkbox :label="5">补缴</el-checkbox>
              <el-checkbox :label="3">调基</el-checkbox>
            </el-checkbox-group>
          </div>
        </el-col>
        <el-col :span="7">
          <div class="display-flex" style="align-items: center;">
            <span class="">状态：</span>
            <el-checkbox-group v-model="formData1.statuses" @change="getTableList">
              <el-checkbox :label="1">启用</el-checkbox>
              <el-checkbox :label="0">禁用</el-checkbox>
            </el-checkbox-group>
            <div class="flex1 text-right">
              <el-button type="primary" size="small" @click="getTableList">查询</el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    <div class="box-si">
      <dTable
        :data="filestoreRuleData"
        style="margin-top: 0"
        :tableHeight="tableHeight"
        :paging="false"
        rowKey="id"
      >
        <u-table-column label="序号" width="80">
          <template slot-scope="scope">
            <span>{{ scope.$index + 1 }}</span>
          </template>
        </u-table-column>
        <u-table-column
          prop="fileTypeName"
          label="字段名称"
          :show-overflow-tooltip="true"
        ></u-table-column>
        <!--<u-table-column
          prop="fieldType"
          label="字段属性"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <span>文件上传</span>
          </template></u-table-column
        >-->
        <u-table-column
          label="字段要求"
          :show-overflow-tooltip="true"
          min-width="150"
          rowKey="id"
        >
          <template slot-scope="scope">
            <div>
              <p v-show="scope.row.format">
                格式要求：
                <span v-show="scope.row.format == 1">文件类型不限</span>
                <span v-show="scope.row.format == 2">图片类型不限</span>
                <span v-show="scope.row.format == 3">JPG图片</span>
              </p>
              <p v-show="scope.row.backgroundColor">
                底色：
                <span v-show="scope.row.backgroundColor == 1">白色</span>
                <span v-show="scope.row.backgroundColor == 2">蓝色</span>
                <span v-show="scope.row.backgroundColor == 3">红色</span>
              </p>
              <p v-show="scope.row.minSize">
                大小：
                <span v-show="scope.row.minSize !== scope.row.maxSize">
                {{ scope.row.minSize }}KB - {{ scope.row.maxSize }} KB
              </span>
                <span v-show="scope.row.minSize == scope.row.maxSize">
                {{ scope.row.minSize }}KB
              </span>
              </p>
              <p v-show="scope.row.minWidth">
                宽：
                <span v-show="scope.row.minWidth !== scope.row.maxWidth">
                {{ scope.row.minWidth }}PX - {{ scope.row.maxWidth }} PX
              </span>
                <span v-show="scope.row.minWidth == scope.row.maxWidth">
                {{ scope.row.minWidth }}PX
              </span>
              </p>
              <p v-show="scope.row.minHeight">
                高：
                <span v-show="scope.row.minHeight !== scope.row.maxHeight">
                {{ scope.row.minHeight }}PX - {{ scope.row.maxHeight }} PX
              </span>
                <span v-show="scope.row.minHeight == scope.row.maxHeight">
                {{ scope.row.minHeight }}PX
              </span>
              </p>
              <p v-show="scope.row.minCount">
                份：
                <span v-show="scope.row.minCount !== scope.row.maxCount">
                {{ scope.row.minCount }}份 - {{ scope.row.maxCount }}份
              </span>
                <span v-show="scope.row.minCount == scope.row.maxCount">
                {{ scope.row.minCount }}份
              </span>
              </p>
            </div>
          </template>
        </u-table-column>
        <u-table-column
          prop="comment"
          label="注解"
          :show-overflow-tooltip="true"
        ></u-table-column>
        <u-table-column
          rowKey="id"
          label="适用"
          min-width="120"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <div v-html="parseShiyong(scope.row)">
            </div>
          </template>
        </u-table-column>
        <u-table-column prop="status" label="状态" width="100" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <div>
            <span type="text" v-show="scope.row.status == 1" size="small"
            >启用</span
            >
              <span type="text" v-show="scope.row.status == 0" size="small"
              >禁用</span
              >
            </div>
          </template>
        </u-table-column>
        <u-table-column label="操作" width="150">
          <template slot-scope="scope">
            <div>
              <el-button
                v-show="scope.row.status == 1"
                type="text"
                size="small"
                class="text-blue"
                @click="setFormat(scope.row)"
              >设置</el-button
              >
              <el-button
                type="text"
                v-show="scope.row.status == '1'"
                size="small"
                class="text-blue"
                @click="openOroption(scope.row)"
              >禁用</el-button
              >
              <el-button
                type="text"
                v-show="scope.row.status == '0'"
                size="small"
                class="text-blue ml-0"
                @click="openOroption(scope.row)"
              >启用</el-button
              >
            </div>
          </template>
        </u-table-column>
      </dTable>
      <div class="flex-ce">
        <el-button type="text" size="small" @click="addSet()"
        ><span class="font-14">+添加字段</span></el-button
        >
      </div>
      <SetFormDrawer
        :isOption="isOption"
        :newAddCityFileForm="JSON.stringify(newAddCityFileForm)"
        :singleListRow="singleListRow"
        :visible.sync="setLocalVisible"
        @refresh-list="refreshlist"
        @makeNull-form="makeNullForm"
        @toUpdateFileNameDrawer="toUpdateFileNameDrawer"
      />
    </div>

    <UpdateFileNameDrawer :visible.sync="updateFileNameVisible" @doClose="setLocalVisible = true"></UpdateFileNameDrawer>
  </div>
</template>
<script>
import SetFormDrawer from './components/setFormDrawer.vue'
import dTable from '../../components/common/table'
import UpdateFileNameDrawer from '@/views/insuredCity/components/updateFileNameDrawer.vue'
export default {
  components: {
    UpdateFileNameDrawer,
    dTable,
    SetFormDrawer
  },
  props: {
    singleListRow: {
      type: Object,
      default: () => {}
    },
    filestoreRuleData: {
      type: Array,
      default: () => []
    }
  },

  data () {
    return {
      isOption: '',
      newAddCityFileForm: {},
      setLocalVisible: false,
      formData1: {
        businessType: [],
        declareTypes: [],
        statuses: []
      },
      updateFileNameVisible: false
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 320 + 'px'
    }
  },
  created () {
    // this.$nextTick(() => {
    //   this.getTableData3() //表格数据
    // })
  },
  mounted () {},
  watch: {},
  methods: {
    openOroption (val) {
      let statusTitle = ''
      if (val.status == 1) {
        statusTitle = '禁用'
      } else if (val.status == 0) {
        statusTitle = '启用'
      }
      let status
      if (val.status == 1) {
        status = 0
      } else if (val.status == 0) {
        status = 1
      }
      this.$confirm(`是否确定${statusTitle}该字段？`, '提示', {
        confirmButtonText: `确定${statusTitle}`,
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: `/policy/declareAddr/updateFilestoreRuleStatus/${val.id}/${status}`,
            method: 'post'
          }).then(({ data }) => {
            this.$message.success('操作成功')
            this.$emit('refresh-list', this.singleListRow) // 刷新列表
          })
        })
        .catch(() => {})
    },
    parseShiyong (row) {
      var htmlArr = []
      var socialArr = []
      var accfundArr = []
      if (row.socialAdd == 1) {
        socialArr.push('增员')
      }
      if (row.socialStop == 1) {
        socialArr.push('减员')
      }
      if (row.socialAdjust == 1) {
        socialArr.push('调基')
      }
      if (row.socialSuppment == 1) {
        socialArr.push('补缴')
      }
      if (row.accfundAdd == 1) {
        accfundArr.push('增员')
      }
      if (row.accfundStop == 1) {
        accfundArr.push('减员')
      }
      if (row.accfundAdjust == 1) {
        accfundArr.push('调基')
      }
      if (row.accfundSuppment == 1) {
        accfundArr.push('补缴')
      }
      if (socialArr.length > 0) {
        htmlArr.push('<p>&nbsp;&nbsp;&nbsp;社保：' + socialArr.join('、') + '</p>')
      }
      if (accfundArr.length > 0) {
        htmlArr.push('<p>公积金：' + accfundArr.join('、') + '</p>')
      }
      return htmlArr.join('')
    },
    makeNullForm () {
      this.newAddCityFileForm = {}
    },
    getTableList () {
      var obj = {
        addrId: this.singleListRow.addrId,
        addrName: this.singleListRow.addrName,
        businessType: this.formData1.businessType && this.formData1.businessType.length > 0 ? this.formData1.businessType.join(',') : null,
        declareTypes: this.formData1.declareTypes && this.formData1.declareTypes.length > 0 ? this.formData1.declareTypes.join(',') : null,
        statuses: this.formData1.statuses && this.formData1.statuses.length > 0 ? this.formData1.statuses.join(',') : null,
        cityName: this.singleListRow.cityName,
        provinceId: this.singleListRow.provinceId,
        provinceName: this.singleListRow.provinceName
      }
      this.$emit('refresh-list', obj) // 刷新列表
    },
    refreshlist (val) {
      this.$emit('refresh-list', val) // 刷新列表
    },
    addSet () {
      this.setLocalVisible = true
      this.isOption = 'add'
    },
    setFormat (row) {
      this.isOption = 'edit'
      this.newAddCityFileForm = row
      console.log('newAddCityFileForm', this.newAddCityFileForm)
      this.setLocalVisible = true
    },
    toUpdateFileNameDrawer () {
      this.updateFileNameVisible = true
    }
    // getTableData3(pageChange, callback) {
    //   var params = [{ property: 'addrId', value: this.formData.value }]
    //   var self = this
    //   this.$refs.table3.fetch({
    //     pageChange: pageChange ? pageChange : '',
    //     query: params,
    //     method: 'post',
    //     url: '/policy/declareAddr/page',
    //     callback: callback
    //       ? callback
    //       : function (res) {
    //           setTimeout(() => {
    //             self.$refs.table3.doLayout()
    //           }, 2000)
    //         },
    //   })
    // },
  }
}
</script>
<style lang="less" scoped>
.box-si {
  padding: 0 20px;
}
.flex-ce {
  display: flex;
  justify-content: center;
}
</style>
