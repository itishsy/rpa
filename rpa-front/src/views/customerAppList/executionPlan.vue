<template>
  <div :class="{'spl-container': !isComponent }">
    <breadcrumb v-if="!isComponent" :data="pathData" :cust-back="true" @back="toBack" :cust-link="true" @toPath="toBack">
    </breadcrumb>
    <div :style="{padding: isComponent?'0px':'20px'}">
      <div v-if="!isComponent">
        <el-row v-if="detailData">
          <el-col :span="8" class="row-item">
            <p class="lab">客户名称：</p>
            <p class="sel">{{ detailData.customerName }}</p>
          </el-col>
          <el-col :span="8" class="row-item">
            <p class="lab">应用名称：</p>
            <p class="sel">{{ detailData.appName }}</p>
          </el-col>
          <el-col :span="8" class="row-item">
            <p class="lab">所在设备：</p>
            <p class="sel">{{ detailData.machineCode }}（{{detailData.appAccountNumber}}）</p>
          </el-col>
        </el-row>

        <div class="text-right">
          <el-input placeholder="请输入申报单位/申报账户关键字搜索" v-model.trim="searchText" clearable
                    @keyup.enter.native="getTableData" @clear="getTableData"
                    style="display: inline-block;width: 350px;">
            <i @click="getTableData" slot="suffix" class="el-icon-search f-cursor font-16 fw-blod text-gray mr-5"
               style="color: #dbdbdb;vertical-align: middle"></i></el-input>
        </div>
      </div>

      <div v-loading="tableLoading" class="mt-15">
        <div class="type-item" v-for="(item, index) in tableData" :key="index">
          <div class="top">
            <span class="num" v-if="!isComponent">{{ index + 1 }}</span>
            <p class="name">{{ item.orgName }}-{{ item.accountNumber }}</p>
            <el-tooltip style="margin-top: 2px;margin-left: 20px;" effect="dark" :disabled="item.taskStatus!=='已停用'"
                        :content="item.comment" placement="top">
                  <span v-if="item.taskStatus" class="tag" :class="{'tag-red': item.taskStatus==='已停用',
                 'tag-blue': item.taskStatus==='已启用'}">{{ item.taskStatus }}</span>
            </el-tooltip>
          </div>
          <dTable :data="item.scheduleList" :showIndex="false" :showSelection="false" :paging="false"
                  :cancelMinHeight="true">
            <u-table-column prop="serviceItemName" label="服务项目" width="100"
                            :show-overflow-tooltip="true"></u-table-column>
            <u-table-column prop="flowType" label="流程类型" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.flowType === '1' ? '主' : '子' }}</span>
              </template>
            </u-table-column>
            <u-table-column prop="execOrder" label="执行顺序" width="80" :show-overflow-tooltip="true"></u-table-column>
            <u-table-column prop="taskName" label="流程名称" min-width="240">
              <template slot-scope="scope">
                <el-tooltip class="item" effect="dark" :disabled="scope.row.status!==0"
                            :content="'原因：'+scope.row.comment" placement="top">
                  <span v-if="scope.row.status!=null && scope.row.status!==''" class="tag" :class="{'tag-red': scope.row.status===0,
                 'tag-blue': scope.row.status===1}">{{ scope.row.status === 1 ? '启用' : '停用' }}</span>
                </el-tooltip>
                {{ scope.row.flowName }}
              </template>
            </u-table-column>
            <u-table-column prop="execPlant" label="执行计划" min-width="240" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span v-if="scope.row.taskType===2" class="ic-cust" title="自定义计划">
                  <i class="el-icon-star-on font-12" style="color: #ffffff"></i></span>
                <!--            <span v-else class="ic-cust ic-cust-no"></span>-->
                {{ scope.row.execPlant }}
              </template>
            </u-table-column>
            <u-table-column prop="editName" label="编辑人" width="100" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                {{ scope.row.editName?scope.row.editName: '上级计划'}}
              </template>
            </u-table-column>
            <u-table-column prop="editTime" label="编辑时间" width="150" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span v-if="scope.row.editTime">{{ $moment(scope.row.editTime).format('YYYY-MM-DD HH:mm') }}</span>
              </template>
            </u-table-column>
            <u-table-column label="操作" align="left" fixed="right" width="100">
              <template slot-scope="scope">
                <div class="jkh-flex">
                  <el-button type="text" size="small" class="text-blue"
                             @click="handleEdit(scope.row)">编辑
                  </el-button>
                  <span v-if="scope.row.status===1" class="text-gray font-12 ml-10 f-cursor"
                        @click="handleTaskFlowStatus(scope.row)">停用</span>
                  <el-button v-if="scope.row.status===0" type="text" size="small" class="text-blue"
                             @click="handleTaskFlowStatus(scope.row)">启用
                  </el-button>
                </div>
              </template>
            </u-table-column>
          </dTable>
        </div>

        <div v-if="!tableLoading && tableData.length===0">
          <el-empty description="暂无数据"></el-empty>
        </div>
      </div>
    </div>

    <!--    停用流程-->
    <el-dialog :visible.sync="stopForm.show" width="500px" :close-on-click-modal="false" title="停用账户"
               class="cust-dialog" :modal-append-to-body="false" :append-to-body="true">
      <el-form style="margin-top: 10px;padding: 0px 10px 10px 10px;" ref="stopForm" label-width="55px"
               :model="stopForm">
        <p class="mb-15">停用此服务项目下的所有流程自动任务</p>
        <el-form-item prop="comment" label="原因:" :rules="[{ required: true, message: '请输入原因',trigger:'blur' }]">
          <el-input type="textarea" :rows="4" v-model="stopForm.comment" maxlength="500" placeholder="请输入原因"
                    style="margin-top: 5px;"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button class="s-btn" @click="stopForm.show = false">取消</el-button>
        <el-button type="primary" class="s-btn" @click="confirmStop()">确定</el-button>
      </span>
    </el-dialog>

    <!--    编辑-->
    <EditPlanFormModel ref="editPlanFormModel" @refesh="doFresh"/>
  </div>
</template>
<script>
import EditPlanFormModel from './components/editPlanFormModel.vue'
import dTable from '../../components/common/table'

export default {
  components: {
    dTable,
    EditPlanFormModel
  },
  data () {
    return {
      lineId: 0,
      popupForm: {},
      showFormModelVisisble: false,
      isloading: false,
      pathData: [],
      searchText: '',
      detailData: null,
      tableData: [],
      tableLoading: false,
      stopForm: {
        show: false,
        comment: '',
        row: null
      }
    }
  },
  props: {
    infoData: {
      type: Object,
      default: null
    },
    isComponent: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 320 + 'px'
    }
  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
    }
    this.pathData.push({ name: '执行计划' })
    if (this.infoData.isSearchAccNumber) {
      this.searchText = this.infoData.accNumber
    }
    this.getRobotClientInfo()
    this.getTableData()
    console.log(this.infoData)
  },
  mounted () {
  },
  watch: {},
  methods: {
    getRobotClientInfo () {
      let obj = this.infoData
      this.$http({
        url: `/robot/app/client/getRobotClientInfo/${obj.clientId}/${obj.machineCode}/${obj.appCode}`,
        method: 'post'
      }).then((data) => {
        if (data.data.code == 200) {
          this.detailData = data.data.data
        }
      }).catch(() => {
      })
    },
    getTableData () {
      this.tableLoading = true
      let obj = this.infoData
      this.$http({
        url: `/robot/app/client/getRobotPlant/${obj.clientId}/${obj.machineCode}/${obj.appCode}?searchText=${this.searchText}`,
        method: 'post'
      }).then((data) => {
        this.tableLoading = false
        if (data.data.code == 200) {
          this.tableData = data.data.data
        }
      }).catch(() => {
        this.tableLoading = false
      })
    },
    // 编辑
    handleEdit (row) {
      this.$refs.editPlanFormModel.init(row)
    },
    //   停用、启用
    handleTaskFlowStatus (row) {
      let that = this
      if (row.status === 1) {
        this.stopForm.comment = ''
        this.stopForm.row = row
        this.$nextTick(() => {
          if (this.$refs.stopForm) {
            this.$refs.stopForm.clearValidate()
          }
        })
        this.stopForm.show = true
      } else {
        this.$confirm('是否确定启用此流程？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          showClose: false,
          customClass: 'pal-confirm',
          type: 'warning'
        }).then(() => {
          that.updateTaskFlowStatus(row)
        }).catch(() => {
        })
      }
    },
    updateTaskFlowStatus (row) {
      let status = row.status === 1 ? 0 : 1
      let comment = row.status === 1 ? this.stopForm.comment : ''
      let flowCodes = row.flowCode
      this.$http({
        url: `/robot/app/client/updateTaskFlowStatus/${row.appCode}/${row.taskCode}/${status}?flowCodes=${flowCodes}&comment=${comment}`,
        method: 'post'
      }).then((data) => {
        if (data.data.code == 200) {
          this.stopForm.show = false
          this.$message.success('操作成功')
          this.doFresh()
        }
      }).catch(() => {
      })
    },
    confirmStop () {
      this.$refs.stopForm.validate((valid) => {
        if (valid) {
          this.updateTaskFlowStatus(this.stopForm.row)
        }
      })
    },
    getEditDialog (val) {
      this.lineId = val.id
      this.$http({
        url: '/robot/app/client/editEcho',
        method: 'post',
        params: {
          id: val.id
          // appCode: '679f161f0f8c4670b40a3fb1a1eca721',
        }
      }).then((data) => {
        if (data.data.code == '200') {
          Object.assign(this.popupForm, data.data.data)
          this.showFormModelVisisble = true
        }
      })
    },

    editDialog (val) {
      // Object.assign(this.popupForm, val)
      this.getEditDialog(val)
    },
    toBack () {
      this.$emit('to-index')
    },
    doFresh () {
      this.getTableData()
      this.$emit('refreshTable')
    }
  }
}
</script>
<style lang="less" scoped>
.row-item {
  display: flex;
  margin-bottom: 15px;

  .sel {
    flex: 1;
    margin-left: 5px;
    word-break: break-all;
  }
}

.tag {
  display: inline-block;
  margin-right: 5px;
  text-align: center;
  font-size: 12px;
  color: #ffffff;
  background-color: #008000;
  width: 70px;
  height: 24px;
  line-height: 24px;
  border-radius: 30px;
}

.tag-red {
  background-color: #fde2e2;
  color: #CC0000;
}

.tag-blue {
  background-color: #d9ecff;
  color: @blueColor;
}

.ic-cust {
  display: inline-block;
  width: 16px;
  height: 16px;
  line-height: 14px;
  border-radius: 50%;
  text-align: center;
  background: @blueColor;
  vertical-align: middle;
  margin-top: -3px;
}

.ic-cust-no {
  background: none;
}

.type-item {
  border: 1px solid #dbdbdb;
  margin-bottom: 20px;
  padding: 0px 20px 20px 20px;
  border-radius: 4px;

  .top {
    display: flex;
    padding: 10px 0;
  }

  .num {
    display: inline-block;
    width: 26px;
    height: 26px;
    line-height: 26px;
    border-radius: 50%;
    text-align: center;
    border: 1px solid #dbdbdb;
  }

  .name {
    font-weight: bold;
    line-height: 30px;
    margin-left: 15px;
  }
}
/deep/.cust-dialog{
  z-index: 10000000!important;
}
</style>
