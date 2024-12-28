<template>
  <!-- 报盘文件设置 -->
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20">
      <div class="search-row">
        <el-row>
          <el-col :span="8">
            <div class="display-flex">
              <span class="ml-20 label" style="width: 70px;flex-shrink: 0;text-align: right;">参保城市：</span>
              <addrSelector
                v-model="formData.addrName"
                :addrArr="allAddr"
                @changeAddrSelect="changeSearch"
              ></addrSelector>
              <!-- <el-button size="small" type="primary" class="ml-20 w-60" @click="getSocialTableData">查询</el-button> -->
            </div>
          </el-col>
          <el-col :span="6">
            <div class="display-flex" style="align-items: center;">
              <span class="ml-20 label">业务类型：</span>
              <el-checkbox-group v-model="formData.businessType" @change="getOrgList">
                <el-checkbox label="1">社保</el-checkbox>
                <el-checkbox label="2">公积金</el-checkbox>
              </el-checkbox-group>
            </div>
          </el-col>
          <el-col :span="9">
            <div class="display-flex" style="align-items: center;">
              <span class="ml-20 label" style="flex-shrink: 0;">申报类型：</span>
              <el-checkbox-group v-model="formData.changeTypes">
                <el-checkbox
                  :label="item.id"
                  v-for="(item,index) in typeArr"
                  :key="index"
                >{{item.name}}</el-checkbox>
              </el-checkbox-group>
            </div>
          </el-col>
        </el-row>
        <el-row style="padding-top:20px;">
          <el-col :span="8">
            <div class="display-flex" style="align-items: center;">
              <span class="ml-20 label" style="flex-shrink: 0;width: 70px;">申报单位：</span>
              <el-select v-model="formData.declareBaseId" clearable filterable style="width:260px;">
                <el-option
                  v-for="it in customMadeList"
                  :key="it.id"
                  :label="it.orgName"
                  :value="it.id"
                ></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="display-flex" style="align-items: center;">
              <span class="ml-20 label">&nbsp;</span>
              <el-checkbox
                v-model="formData.status"
                true-label="1"
                false-label
                @change="changeBussinssType"
              >只看有效</el-checkbox>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="display-flex" style="align-items: center;justify-content: flex-end;">
              <el-button type="primary" @click="getTableData">查询</el-button>
              <el-button @click="goUrl">添加定制</el-button>
            </div>
          </el-col>
        </el-row>
      </div>
      <div>
        <dTable
          @fetch="getTableData"
          ref="table"
          style="margin-top: 0;"
          :tableHeight="tableHeight"
          :showIndex="false"
          :showSelection="false"
          row-key="id"
          row-id="id"
        >
          <u-table-column label="序号" width="75" align="center">
            <template scope="scope">
              <span>{{scope.$index + 1}}</span>
            </template>
          </u-table-column>
          <u-table-column
            prop="addrName"
            label="参保城市"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          ></u-table-column>
          <u-table-column
            prop="businessTypeName"
            label="业务类型"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          ></u-table-column>
          <u-table-column
            prop="changeTypeName"
            label="申报类型"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <p>{{formatType(scope.row.changeType)}}</p>
            </template>
          </u-table-column>
          <u-table-column
            prop="orgName"
            label="申报单位"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          ></u-table-column>
          <u-table-column
            prop="fieldNumber"
            label="定制字段数"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          ></u-table-column>
          <u-table-column
            prop="status"
            label="状态"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <p>{{scope.row.status == 1 ? '有效':'无效'}}</p>
            </template>
          </u-table-column>
          <u-table-column label="操作" align="left" min-width="200">
            <template slot-scope="scope">
              <div>
                <el-button
                  type="text"
                  size="small"
                  class="text-blue"
                  @click="handleSocialView(scope.row)"
                >编辑定制</el-button>
                <div class="opt-btn-split"></div>
                <el-button
                  type="text"
                  size="small"
                  class="text-blue"
                  @click="disableFile(scope.row)"
                >{{scope.row.status == '1' ? '禁用' : '启用'}}</el-button>
              </div>
            </template>
          </u-table-column>
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
  data() {
    return {
      allAddr: [],
      formData: {
        businessType: ['1', '2'],
        changeTypes: ['1'],
        status: '1',
        addrId: '',
        addrName: '',
        declareBaseId: '',
      },
      dialogVisible: false,
      pathData: [],
      associatedFileList: [],
      uploadFileData: {
        uploadFileName: '',
        fileID: '',
        fileData: null,
        tplType: '',
        comAccountNumber: '',
      },
      rules: {
        uploadFileName: [{ required: true, message: '请先上传文件', trigger: 'change' }],
      },
      typeArr: [
        //变更类型（1增，2减，3调基，5补缴）
        { id: '1', name: '增员' },
        { id: '2', name: '减员' },
        { id: '3', name: '调基' },
        { id: '5', name: '补缴' },
      ],
      associatedFileErrMsg: [],
      rowData: {
        businessType: '1',
      },
      loading: null,
      timer: false,
      showRelation: false,
      fileTable: [],
      accountNumberList: [], //社保号或公积金号
      customMadeList: [], //申报单位
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 320 + 'px'
    },
    formatType() {
      return function (index) {
        var list = ['增员', '减员', '调基', '变更', '补缴']
        return list[index - 1]
      }
    },
    getTplTypeStr() {
      return function (key) {
        var str = ''
        this.allOptions.forEach((item) => {
          if (item.dictCode == key) {
            str = item.dictName
          }
        })
        return str
      }
    },
    getOptionName() {
      return function (key, option) {
        var str = ''
        option.forEach((item) => {
          if (item.dictCode == key) {
            str = item.dictName
          }
        })
        return str
      }
    },
    getKeyName() {
      return function (key, option) {
        var str = ''
        var mergeStr = ''
        option.forEach((item, index) => {
          if (item.tplType == key) {
            item.items.forEach((item1, idx) => {
              str += item1.itemName + (idx == item.items.length - 1 ? '' : '、')
            })
            if (item.merge) {
              mergeStr = ' 增补合并申报【是】'
            } else {
              mergeStr = ' 增补合并申报【否】'
            }
          }
        })
        return str ? '(' + str + ')' + mergeStr : ''
      }
    },
    getDeclareAccountStr() {
      return function (code) {
        var str = ''
        this.accountNumberList.forEach((item) => {
          if (item.accountNumber == code) {
            str = item.name
          }
        })
        return str
      }
    },
  },
  created() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({ name: '定制报盘' })
    if(sessionStorage.getItem('customizedList-params')){
      this.formData = JSON.parse(sessionStorage.getItem('customizedList-params'))
    }
    if(this.$route.query.addrId){
      this.formData.businessType = [this.$route.query.bussinssType]
      for (const key in this.$route.query) {
          const val = this.$route.query[key];
          this.formData[key] = val
      }
    }
    let that = this
    this.$nextTick(() => {
      that.getAddr()
      this.getOrgList()
      this.getTableData()
    })
  },
  mounted() {},
  methods: {
    changeSearch(obj) {
      clearTimeout(this.timer)
      this.timer = setTimeout(() => {
        this.formData.addrId = obj.id
        this.getOrgList()
        this.getTableData()
      }, 100)
    },
    //去新增
    goAdd() {
      this.$router.push('/offerFile/addOfferFile')
    },
    //禁用文件
    disableFile(row) {
      var status = row.status == '1' ? '0' : '1'
      if (row.status == '0') {
        this.confirm('是否启用定制报盘', () => {
          return this.updateFileStatus(row.uuid, status)
        })
        return
      }
      this.confirm('一旦禁用，定制的字段逻辑将失效，请谨慎操作', () => {
        return this.updateFileStatus(row.uuid, status)
      })
    },
    updateFileStatus(uuid, status) {
      this.$http({
        url: '/policy/customMade/offerSettings/updateStatus',
        method: 'post',
        data: this.$qs.stringify({ uuid: uuid, status: status }),
      })
        .then(({ data }) => {
          this.getTableData()
        })
        .catch(() => {})
    },
    getTableData(pageChange, callback) {
      var params = [
        { property: 'addrId', value: this.formData.addrId },
        { property: 'businessTypes', value: this.formData.businessType },
        { property: 'changeTypes', value: this.formData.changeTypes },
        { property: 'declareBaseId', value: this.formData.declareBaseId },
        { property: 'status', value: this.formData.status },
      ]
      sessionStorage.setItem('customizedList-params',JSON.stringify(this.formData))
      var self = this
      this.$refs.table.fetch({
        pageChange: pageChange ? pageChange : '',
        query: params,
        method: 'post',
        url: '/policy/customMade/offerSettings/page',
        callback: callback
          ? callback
          : function (res) {
              setTimeout(() => {
                self.$refs.table.doLayout()
              }, 1000)
            },
      })
    },
    changeBussinssType() {
      this.getTableData()
    },
    // 获取参保地
    getAddr(type) {
      let that = this
      this.$http({
        url: 'policy/declareAddr/addrList',
        method: 'post',
      }).then(({ data }) => {
        this.allAddr = data.data
      })
    },
    //去报盘信息维护
    handleSocialView(row) {
      this.$router.push(
        `/offerFile/customizedDetail?uuid=${row.uuid}&type=edit&addrId=${row.addrId}&addrName=${row.addrName}&businessType=${row.businessType}&changeType=${row.changeType}&orgName=${row.orgName}`
      )
    },
    showLoading(target) {
      this.loading = this.$loading({
        target: target ? target : document.body,
        lock: true,
        text: '加载中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)',
      })
    },
    closeLoading() {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    },
    //获取字典值
    getDictByKey(dataKey, key) {
      this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: dataKey,
        },
      }).then((res) => {
        if (dataKey == '10004') {
          if (res && res.data.data) {
            res.data.data = res.data.data.map((item) => {
              return {
                itemCode: item.dictCode,
                itemName: item.dictName,
              }
            })
            this.$set(this.relationData, '2', res.data.data)
          }
        } else {
          this.allOptions.push(...res.data.data)
          this.$set(this.relationData.options, key, res.data.data)
          // this.relationData.options[key] = res.data.data
        }
        // console.log(this.relationData)
      })
    },
    //深克隆
    deepCopy(obj) {
      var result = Array.isArray(obj) ? [] : {}
      for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
          if (typeof obj[key] === 'object' && obj[key] !== null) {
            result[key] = this.deepCopy(obj[key]) //递归复制
          } else {
            result[key] = obj[key]
          }
        }
      }
      return result
    },
    //获取申报单位
    getOrgList() {
      this.$http({
        url: 'policy/customMade/offerSettings/getOrgList',
        method: 'post',
        params: {
          addrId: this.formData.addrId,
          custFilter: 0,
          businessType: this.formData.businessType.length == 2 || this.formData.businessType.length == 0 ? '' : this.formData.businessType[0],
          custId: ''
        },
      })
        .then((res) => {
          res.data.data = res.data.data[0] == null ? res.data.data.slice(1) : res.data.data
          this.customMadeList = res.data.data
        })
        .catch((item) => {})
    },
    confirm(tips, callback) {
      this.$confirm(`${tips}`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
          if (callback) {
            console.log(callback)
            callback()
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    goUrl() {
      this.$router.push(`/offerFile/customizedDetail`)
    },
  },
}
</script>
  <style lang="less" scoped>
/deep/.el-dialog__header {
  padding: 10px 20px;
}
/deep/.el-dialog__body {
  padding: 10px 10px;
}
/deep/.el-form-item__content {
  line-height: 0px;
}

.file-content-box {
  padding: 10px 20px;
  font-size: 12px;
  .file-list {
    display: flex;
    padding: 10px 0;
    border-bottom: 1px dashed #ddd;
    .file {
      background: #f1f8ff;
      border-radius: 10px;
      padding: 3px 10px;
      position: relative;
      cursor: pointer;
      margin-right: 15px;
      font-size: 12px;
      &:hover {
        color: #3e82ff;
        text-decoration: underline;
      }
      .file-remove {
        position: absolute;
        right: -10px;
        top: -10px;
        width: 20px;
        height: 20px;
        background: red;
        border-radius: 50%;
        display: none;
        &::before {
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
      &:hover .file-remove {
        display: block;
      }
    }
  }
  .upload-tips {
    margin-top: 20px;
    font-size: 12px;
    color: #797979;
  }
}
.upload-file-box {
  margin-top: 10px;
  border-bottom: 1px dashed rgb(221, 221, 221);
  padding-bottom: 10px;
  input[type='text'] {
    border: 1px solid #ddd;
    height: 28px;
    width: 250px;
    outline: none;
  }
  .upload-file-btn {
    height: 32px;
    background: #3e82ff;
    color: white;
    padding: 0 10px;
    box-sizing: border-box;
    font-size: 12px;
    cursor: pointer;
    line-height: 32px;
    margin-left: 10px;
  }
  .upload-file {
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
    input[type='file'] {
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
        text-decoration: none;
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
.tag:hover,
.tag.active {
  background-color: #dddddd;
}
.table-fileName-list {
  cursor: pointer;
  &:hover {
    color: #3e82ff;
    text-decoration: underline;
  }
}
.text-blue {
  &:hover {
    text-decoration: underline;
  }
}
body .el-table th.gutter {
  display: table-cell !important;
}
.error {
  margin-top: 10px;
  /* position: absolute; */
  left: 0;
  /* top: 120%; */
  color: #f56c6c;
  font-size: 12px;
}
/deep/.el-drawer__body {
  padding-bottom: 0;
}
/deep/.el-dialog__header {
  border-bottom: none !important;
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
.dialog-footer {
  display: flex;
  justify-content: center;
}
.placeholderStyle {
  padding-left: 10px;
  padding-right: 10px;
}
.placeholderStyle::-webkit-input-placeholder {
  font-size: 12px;
  color: #c0c4cc;
}
.cust-dialog /deep/.el-table__body tr.hover-row > td.el-table__cell {
  background-color: rgba(0, 0, 0, 0) !important;
}

/deep/.table-header {
  background-color: #f2f2f2 !important;
  padding: 6px 0;
  border-bottom: 1px solid #e2e2e2 !important;
  border-top: 1px solid #e2e2e2 !important;
  border-right: 1px solid #e2e2e2 !important;
  border-right: transparent;
  text-align: left;
}
/deep/.table-header .cell {
  font-weight: bold;
  color: #303133;
}
.cust-dialog /deep/.el-table .el-table__cell {
  padding: 6px 0;
}
</style>
