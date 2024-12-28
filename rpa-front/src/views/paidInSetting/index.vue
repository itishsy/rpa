<template>
  <!-- 报盘文件设置 -->
  <div class="spl-container">
    <breadcrumb :data="pathData">
        <el-button type="primary" slot="breadcrumb-btn" class="s-btn" @click="goAdd">新增</el-button>
    </breadcrumb>
    <div class="pl-20 pr-20">
      <el-row>
        <el-col :span="8">
          <div class="search-row display-flex">
            <span class="ml-20 label">参保城市：</span>
            <addrSelector v-model="formData.addrName" :addrArr="bussinssType.allAddr" @changeAddrSelect="changeSearch"></addrSelector>
            <!-- <el-button size="small" type="primary" class="ml-20 w-60" @click="getSocialTableData">查询</el-button> -->
          </div>
        </el-col>
        <el-col :span="8">
          <div class="search-row display-flex" style="align-items: center;">
            <span class="ml-20 label">业务类型：</span>
            <el-checkbox-group v-model="formData.bussinssType" @change="changeBussinssType">
              <el-checkbox label="1">社保</el-checkbox>
              <el-checkbox label="2">公积金</el-checkbox>
            </el-checkbox-group>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="search-row display-flex" style="align-items: center;">
            <span class="ml-20 label" style="visibility: hidden;">1</span>
            <el-checkbox v-model="formData.tempStatus" :true-label="1"
              :false-label="0">已关联模板</el-checkbox>
          </div>
        </el-col>
        <el-col :span="2">
          <div class="search-row display-flex" style="justify-content: flex-end;padding: 20px 0;">
            <el-button type="primary" slot="breadcrumb-btn" class="s-btn" @click="getTableData">查询</el-button>
            </div>
        </el-col>
      </el-row>
      <div>
        <dTable @fetch="getTableData" ref="table" style="margin-top: 0;" :tableHeight="tableHeight"  :showIndex='false' :showSelection='false' row-key="id" row-id="id">
          <u-table-column label="序号" width="75" align="center">
            <template scope="scope"><span>{{scope.$index + 1}} </span></template>
          </u-table-column>
          <u-table-column prop="addressName" label="参保城市" header-align="center" align="center" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="businessType" label="业务类型" header-align="center" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{scope.row.businessType == 1 ? '社保':'公积金'}}</p>
            </template>
          </u-table-column>
          <u-table-column prop="tempCount" label="关联模版数" header-align="center" align="center" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="businessType" label="状态 " header-align="center" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{scope.row.status == 1 ? '启用':'停用'}}</p>
            </template>
          </u-table-column>
          <u-table-column label="操作" align="left">
            <template slot-scope="scope">
              <div>
                <el-button type="text" size="small" class="text-blue" @click="handleSetting(scope.row)">编辑</el-button>
                <div class="opt-btn-split"></div>
                <el-button type="text" size="small" class="text-blue" @click="handleLinkCustTemp(scope.row)">关联客户模板</el-button>
                <div class="opt-btn-split"></div>
                <el-button type="text" size="small" :style="{color: scope.row.status == 2 || !scope.row.status ? '#3e82ff':'#F56C6C'}"  @click="handleUse(scope.row)">{{scope.row.status == 1 ? '停用':'启用'}}</el-button>
              </div>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>
    <!--关联客户模板-->
    <linkCustTemp ref="linkCustTemp" @close="getTableData"></linkCustTemp>
  </div>
</template>
<script>

import dTable from '../../components/common/table'
import addrSelector from '../../components/common/addrSelector'
import linkCustTemp from './component/linkCustTemp'
export default {
  components: { addrSelector, dTable, linkCustTemp },
  data () {
    return {
      bussinssType: {
        allAddr: []
      },
      formData: {
        bussinssType: ['1', '2'],
        status: '1',
        address: '',
        addrName: '',
        tempStatus: 0 // 已关联模板
      },
      dialogVisible: false,
      pathData: [],
      rowData: {
        bussinssType: '1'
      },
      loading: null,
      options: {
        '1': [],
        '2': []
      },
      allOptions: [],
      timer: false,
      showRelation: false

    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 265 + 'px'
    },
    formatType () {
      return function (index) {
        var list = ['投保', '停保', '调整', '变更', '补缴']
        return list[index - 1]
      }
    },
    getTplTypeStr () {
      return function (key) {
        var str = ''
        this.allOptions.forEach(item => {
          if (item.dictCode == key) {
            str = item.dictName
          }
        })
        return str
      }
    },
    getOptionName () {
      return function (key, option) {
        var str = ''
        option.forEach(item => {
          if (item.dictCode == key) {
            str = item.dictName
          }
        })
        return str
      }
    },
    getKeyName () {
      return function (key, option) {
        var str = ''
        option.forEach((item, index) => {
          if (item.tplType == key) {
            item.items.forEach((item1, idx) => {
              str += item1.itemName + ((idx == item.items.length - 1) ? '' : '、')
            })
          }
        })
        return str ? '(' + str + ')' : ''
      }
    }
  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    let that = this
    this.$nextTick(() => {
      // that.getTableData()
      that.getAddr()
      if (sessionStorage.getItem('paidInSetting-params')) {
        var obj = JSON.parse(sessionStorage.getItem('paidInSetting-params'))
        this.formData = obj
        this.getTableData()
      }
    })
  },
  mounted () {
    this.getTableData()
  },
  methods: {
    changeSearch (obj) {
      console.log(obj)
      clearTimeout(this.timer)
      this.timer = setTimeout(() => {
        this.formData.address = obj.id
        this.getTableData()
      }, 100)
    },
    // 去新增
    goAdd () {
      this.$router.push('/paidInSetting/mapSetting')
    },
    getTableData (pageChange, callback) {
      var params = [{ property: 'address', value: this.formData.address },
        { property: 'businessType', value: this.formData.bussinssType },
        { property: 'status', value: this.formData.status },
        { property: 'tempStatus', value: this.formData.tempStatus }
      ]
      sessionStorage.setItem('paidInSetting-params', JSON.stringify(this.formData))
      var self = this
      this.$refs.table.fetch({
        pageChange: pageChange || '',
        query: params,
        method: 'post',
        url: '/policy/paidIn/lsitPaidCityAndBusinessType',
        callback: callback || function (res) {
          setTimeout(() => {
            self.$refs.table.doLayout()
          }, 2000)
        }
      })
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
    },
    // 去设置映射
    handleSetting (row) {
      this.$router.push('/paidInSetting/mapSetting?uuid=' + row.uuid + '&address=' + row.addrId + '&businessType=' + row.businessType + '&addressName=' + row.addressName)
    },
    // 关联客户模板
    handleLinkCustTemp (row) {
      this.$refs.linkCustTemp.init({
        uuid: row.uuid,
        addrId: row.addrId,
        addressName: row.addressName,
        businessType: row.businessType
      })
    },
    showLoading (target) {
      this.loading = this.$loading({
        target: target || document.body,
        lock: true,
        text: '加载中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      })
    },
    closeLoading () {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    },
    // 获取字典值
    getDictByKey (dataKey, key) {
      this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: dataKey
        }
      }).then(res => {
        if (dataKey == '10004') {
          if (res && res.data.data) {
            res.data.data = res.data.data.map(item => {
              return {
                itemCode: item.dictCode,
                itemName: item.dictName
              }
            })
            this.$set(this.relationData, '2', res.data.data)
          }
        } else {
          this.allOptions.push(...res.data.data)
          this.$set(this.relationData.options, key, res.data.data)
          // this.relationData.options[key] = res.data.data
        }
        console.log(this.relationData)
      })
    },
    // 深克隆
    deepCopy (obj) {
      var result = Array.isArray(obj) ? [] : {}
      for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
          if (typeof obj[key] === 'object' && obj[key] !== null) {
            result[key] = this.deepCopy(obj[key]) // 递归复制
          } else {
            result[key] = obj[key]
          }
        }
      }
      return result
    },
    // 停用或启用
    handleUse (row) {
      var obj = {
        status: row.status == 1 ? '2' : '1',
        uuid: row.uuid
      }
      if (obj.status == 2) {
        this.$confirm('是否停用该地区设置', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          showClose: false,
          customClass: 'pal-confirm',
          type: 'warning'
        }).then(() => {
          this.stopOrOpen(obj)
        }).catch(() => {

        })
      } else {
        this.stopOrOpen(obj)
      }
    },
    stopOrOpen (obj) {
      this.showLoading()
      this.$http({
        url: 'policy/paidIn/stopPaidField',
        method: 'post',
        params: obj
      }).then(({ data }) => {
        this.closeLoading()
        if (data.code == 200) {
          this.$message.success('操作成功')
          this.getTableData()
        } else {
          this.$message.error('服务出错，请稍后再试')
        }
      }).catch(err => {
        this.closeLoading()
        this.$message.error('服务出错，请稍后再试')
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
