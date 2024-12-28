<template>
  <!-- 报盘文件设置 -->
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <el-row>
        <el-col :span="5">
          <div class="search-row display-flex">
            <span class="ml-20 label" style="white-space:nowrap">参保城市：</span>
            <addrSelector width="100%" v-model="formData.addrName" :addrArr="bussinssType.allAddr" @changeAddrSelect="changeSearch"></addrSelector>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="search-row display-flex" style="align-items: center;">
            <span class="ml-20 label" style="white-space:nowrap">业务类型：</span>
            <el-checkbox-group v-model="formData.bussinssTypes" @change="changeBussinssType">
              <el-checkbox :label="1">社保</el-checkbox>
              <el-checkbox :label="2">公积金</el-checkbox>
            </el-checkbox-group>
          </div>
        </el-col>
        <el-col :span="5">
          <div class="search-row display-flex" style="align-items: center;">
            <span class="ml-20 label" style="white-space:nowrap">变更类型：</span>
            <el-select v-model="formData.changeTypes" clearable multiple collapse-tags>
              <el-option v-for="(item, index) in changeTypeArr" :label="item" :value="item"></el-option>
            </el-select>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="search-row display-flex" style="align-items: center;">
            <span class="ml-20 label" style="white-space:nowrap">修改时间：</span>
            <div style="display:flex;max-width:380px">
              <el-date-picker v-model="formData.startDate" value-format="yyyy-MM-dd" style="width:50%;"
                              type="date" placeholder="选择时间" :picker-options="getPickerOption('start', 'formData')"
                              clearable></el-date-picker>
              <span class="lh-com" style="padding: 0 5px;">~</span>
              <el-date-picker v-model="formData.endDate" value-format="yyyy-MM-dd" style="width:50%;"
                              placeholder="选择时间" :picker-options="getPickerOption('end', 'formData')"
                              clearable></el-date-picker>
            </div>
            <el-button type="primary" slot="breadcrumb-btn" class="s-btn ml-50" @click="getTableData">查询</el-button>
          </div>
        </el-col>
      </el-row>
      <div>
        <dTable @fetch="getTableData" ref="table" style="margin-top: 0;" :tableHeight="tableHeight"
                :showIndex='true' :showSelection='false' row-key="id" row-id="id">
          <u-table-column prop="addrName" min-width="120" label="参保城市" header-align="center" align="center" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="businessTypeName" min-width="120" label="业务类型" header-align="center" align="center" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="changeType" min-width="180" label="变更类型" header-align="center" align="center" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="changeReason" min-width="180" label="变更原因" header-align="center" align="center" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="newValue" min-width="280" label="变更内容" header-align="center" align="center" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="originalValue" min-width="280" label="变更前内容" header-align="center" align="center" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="comment" min-width="180" label="原因备注" header-align="center" align="center" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="createName" min-width="120" label="修改人" header-align="center" align="center" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="createTime" min-width="180" label="修改时间" header-align="center" align="center" :show-overflow-tooltip="true"></u-table-column>
          <template slot="pagination-btns">
            <div class="display-flex">
              <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                         @click="exportTableData">导出</el-button>
            </div>
          </template>
        </dTable>
      </div>
    </div>
  </div>
</template>
<script>

  import dTable from '../../components/common/table'
  import addrSelector from '../../components/common/addrSelector'
  export default {
    components: { addrSelector, dTable },
    data () {
      return {
        bussinssType: {
          allAddr: []
        },
        formData: {
          bussinssTypes: [],
          status: '1',
          addrId: null,
          addrName: '',
          changeTypes: [],
          startDate: null,
          endDate: null
        },
        changeTypeArr: [
          '调整RPA流程',
          '调整RPA执行计划',
          '修改信息配置',
          '调整应用状态',
          '申报期调整',
          '险种调整',
          '修改报盘文件',
          '调整报盘字段',
          '调整报盘校验规则',
          '调整回盘规则',
          '申报附件维护'
        ],
        pathData: [],
        timer: false
      }
    },
    computed: {
      tableHeight: function () {
        return this.$global.bodyHeight - 265 + 'px'
      },
      getPickerOption () {
        return function (time, key) {
          var self = this
          if (time == 'start') {
            return {
              disabledDate (time) {
                if (!self[key].endDate) {
                  return time.getTime() > new Date().getTime()
                } else {
                  let curDate = new Date(self[key].endDate).getTime()
                  let three = new Date(
                    self
                      .$moment(self[key].endDate)
                      .subtract(6, 'months')
                      .format('YYYY-MM-DD')
                  ).getTime()
                  return time.getTime() > curDate || time.getTime() < three
                }
              }
            }
          } else {
            return {
              disabledDate (time) {
                if (!self[key].startDate) {
                  return time.getTime() > new Date().getTime()
                } else {
                  let curDate = new Date(self[key].startDate).getTime()
                  let three = new Date(
                    self
                      .$moment(self[key].startDate)
                      .add(6, 'months')
                      .format('YYYY-MM-DD')
                  ).getTime()
                  return (
                    time.getTime() <= curDate - 86400000 ||
                    time.getTime() > three - 86400000 || time.getTime() > new Date().getTime()
                  )
                }
              }
            }
          }
        }
      }
    },
    created () {
      let tabs = this.$store.state.tabs
      if (tabs) {
        this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
      }
      this.pathData.push({name: '变更查询'})
      let that = this
      this.$nextTick(() => {
        that.getAddr()
        that.getTableData()
      })
    },
    mounted () {
      this.getTableData()
    },
    methods: {
      changeSearch (obj) {
        clearTimeout(this.timer)
        this.timer = setTimeout(() => {
          this.formData.addrId = obj.id
          this.getTableData()
        }, 100)
      },
      getTableData (pageChange, callback) {
        var params = [{ property: 'addrId', value: this.formData.addrId?Number(this.formData.addrId):null },
          { property: 'businessTypes', value: this.formData.bussinssTypes },
          { property: 'changeTypes', value: this.formData.changeTypes },
          { property: 'startDate', value: this.formData.startDate },
          { property: 'endDate', value: this.formData.endDate }
        ]
        var self = this
        this.$refs.table.fetch({
          pageChange: pageChange || '',
          query: params,
          method: 'post',
          url: '/policy/addrBusinessChange/page',
          callback: callback
        })
      },
      exportTableData() {
        let params = this.$refs.table.getParamsQuery()
        if (!params) {
          this.$message.warning('请先查询数据')
          return
        }

        var params2 = this.$global.deepcopyArray(params)
        this.$downloadFile(
          '/policy/addrBusinessChange/export',
          'post',
          {
            query: params2,
          },
          this.$global.EXCEL
        )
      },
      changeBussinssType () {
        this.getTableData()
      },
      // 获取参保地
      getAddr (type) {
        let that = this
        this.$http({
          url: 'policy/declareAddr/addrList',
          method: 'post'
        }).then(({ data }) => {
          this.bussinssType.allAddr = data.data
        })
      }
    }
  }
</script>
<style lang="less" scoped>
  /deep/.el-dialog__header{
    padding:10px 20px;
  }
  /deep/.el-dialog__body{
    padding:10px 10px;
  }
  /deep/.el-form-item__content{
    line-height: 0px;
  }

  .file-content-box{
    padding:10px 20px;
    font-size: 12px;
    .file-list{
      display: flex;
      padding: 10px 0;
      border-bottom:1px dashed #ddd;
      .file{
        background: #f1f8ff;
        border-radius: 10px;
        padding:3px 10px;
        position: relative;
        cursor: pointer;
        margin-right:15px;
        font-size: 12px;
        &:hover{
          color: #3e82ff;
          text-decoration: underline;
        }
        .file-remove{
          position: absolute;
          right: -10px;
          top:-10px;
          width: 20px;
          height: 20px;
          background: red;
          border-radius: 50%;
          display: none;
          &::before{
            content: '';
            display: inline-block;
            width: 10px;
            height: 2px;
            background: white;
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
          }
        }
        &:hover .file-remove{
          display: block;
        }
      }
    }
    .upload-tips{
      margin-top:20px;
      font-size: 12px;
      color:#797979;
    }
  }
  .upload-file-box{
    margin-top:10px;
    border-bottom:1px dashed rgb(221, 221, 221);
    padding-bottom: 10px;
    input[type=text]{
      border: 1px solid #ddd;
      height: 28px;
      width: 250px;
      outline: none;
    }
    .upload-file-btn{
      height: 32px;
      background: #3e82ff;
      color: white;
      padding:0 10px;
      box-sizing: border-box;
      font-size: 12px;
      cursor: pointer;
      line-height: 32px;
      margin-left:10px;
    }
    .upload-file{
      padding: 4px 10px;
      height: 32px;
      line-height: 20px;
      position: relative;
      cursor: pointer;
      color: #888;
      background: #fafafa;
      border: 1px solid #ddd;
      display: inline-block;
      box-sizing: border-box;
      input[type=file] {
        position: absolute;
        font-size: 17px;
        right: 0;
        top: 0;
        opacity: 0;
        width: 295px;
        filter: alpha(opacity=0);
        cursor: pointer;
        &:hover {
          color: #444;
          background: #eee;
          border-color: #ccc;
          text-decoration: none
        }
      }
    }

  }
  .tag {
    display: inline-block;
    padding: 0 15px;
    height: 30px;
    line-height: 30px;
    background: rgba(255, 255, 255, 1);
    border: 1px solid rgba(206, 206, 206, 1);
    border-radius: 0px 2px 2px 0px;
    margin-right: 10px;
    cursor: pointer;
  }
  .tag:hover, .tag.active {
    background-color: #dddddd;
  }
  .table-fileName-list{
    cursor: pointer;
    &:hover{
      color:#3e82ff;
      text-decoration:underline;
    }
  }
  .text-blue{
    &:hover{
      text-decoration: underline;
    }
  }
</style>
<style lang="less" scoped>
  body .el-table th.gutter {
    display: table-cell !important;
  }
  .error{
    margin-top:10px;
    /* position: absolute; */
    left: 0;
    /* top: 120%; */
    color:#F56C6C;
    font-size: 12px;
  }
  /deep/.el-drawer__body{
    padding-bottom: 0;
  }
  /deep/.el-dialog__header{
    border-bottom: none!important;
  }
  .file-content-box /deep/.table-header {
    background: #f5f5f5 !important;
    color: #444;
  }
  .file-content-box /deep/.el-table__cell {
    border-bottom: 1px solid #ddd;
    border-right: 1px solid #ddd;
  }
  .file-content-box /deep/.el-table__row {
    border-bottom: 1px solid #ddd;
    border-right: 1px solid #ddd;
  }
  .file-content-box /deep/.el-table--border {
    border: 1px solid #ddd;
  }
  /deep/.el-table .el-table__cell {
    padding: 0px 0;
  }
  .dialog-footer{
    display: flex;
    justify-content: center;
  }
  .placeholderStyle{
    padding-left:10px;
    padding-right:10px;
  }
  .placeholderStyle::-webkit-input-placeholder {
    font-size: 12px;
    color: #C0C4CC;
  }
  .cust-dialog /deep/.el-table__body tr.hover-row>td.el-table__cell{
    background-color: rgba(0, 0, 0, 0) !important;
  }

  /deep/.table-header{
    background-color: #f2f2f2 !important;
    padding: 6px 0;
    border-bottom: 1px solid #e2e2e2 !important;
    border-top: 1px solid #e2e2e2 !important;
    border-right: 1px solid #e2e2e2 !important;
    border-right: transparent;
    text-align: left;
  }
  /deep/.table-header .cell{
    font-weight: bold;
    color: #303133;
  }
  .cust-dialog /deep/.el-table .el-table__cell{
    padding: 6px 0;
  }

</style>
