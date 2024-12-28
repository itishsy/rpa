<template>
  <!-- 报盘文件设置 -->
  <div class="spl-container">
    <breadcrumb :data="pathData">
      <el-button type="primary" slot="breadcrumb-btn" size="mini" @click="goAdd">新增</el-button>
    </breadcrumb>
    <div class="pl-20 pr-20">
      <el-row>
        <el-col :span="10">
          <div class="search-row display-flex">
            <!-- <span class="ml-20 label">参保地：</span> -->
            <el-input v-model.trim="formData.keyWord" placeholder="搜索关键字后回车查询" style="width: 300px" @change="changeSearch"
              clearable></el-input>
            <!-- <addrSelector v-model="formData.addrName" :addrArr="bussinssType.allAddr" @changeAddrSelect="changeSearch"></addrSelector> -->
            <!-- <el-button size="small" type="primary" class="ml-20 w-60" @click="getSocialTableData">查询</el-button> -->
          </div>
        </el-col>
        <!-- <el-col :span="6">
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
            <span class="ml-20 label">&nbsp;</span>
            <el-checkbox v-model="formData.status" true-label="1" false-label="" @change="changeBussinssType">只看有效</el-checkbox>
          </div>
        </el-col>-->
      </el-row>
      <div>
        <dTable @fetch="getTableData" ref="table" style="margin-top: 0" :tableHeight="tableHeight" :showIndex="true"
          :showSelection="false" row-key="id" row-id="id">
          <u-table-column prop="bussinssType" label="类型" width="200" header-align="center" align="center"
            :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{ scope.row.bussinssType == 1 ? '社保' : '公积金' }}</p>
            </template>
          </u-table-column>
          <u-table-column prop="addressName" label="参保城市" header-align="center" align="center"
            :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="productName" label="参保险种" width="300" header-align="center" align="center"
            :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{ (getProductNameList(scope.row)) }}</p>
            </template>
          </u-table-column>
          <u-table-column prop="status" label="状态" header-align="center" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>
                {{ scope.row.status == 1 ? '启用' : '禁用' }}
                <span>
                  {{
                    scope.row.outDisplay == 2 ? '（对外不展示）' : ''
                  }}
                </span>
              </p>
            </template>
          </u-table-column>
          <u-table-column label="操作" align="left" width="300">
            <template slot-scope="scope">
              <div v-if="scope.row.status == '1'">
                <el-button type="text" size="small" class="text-blue" @click="openEditSocial(scope.row, 1)">调整</el-button>
                <div class="opt-btn-split"></div>
                <el-button type="text" size="small" class="text-red" @click="disableSocial(scope.row)">禁用</el-button>
              </div>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>
    <!-- 关联文件 -->
    <el-dialog title="调整险种申报组合" :visible.sync="dialogVisible" width="1200px" class="cust-dialog"
      :before-close="cancalEditSocial" :close-on-click-modal="false">
      <!-- <span slot="title" class="dialog-footer">
        <div>调整险种申报组合</div>
      </span>-->
      <div id="upload-dialog">
        <div class="file-content-box">
          <div class="display-flex">
            <span>已选：</span>
            <span class="flex1">
              {{
                socialListData.productNameList
                ? socialListData.productNameList
                : '暂无'
              }}
            </span>
          </div>
          <el-form :model="socialListData" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
            <div class="display-flex mt-20">
              <el-form-item prop="socialList" label-width="0" style="width: 300px;">
                <el-select class="w-p100" v-model="socialListData.socialList" placeholder="请选择调整险种" value-key="dictCode"
                  multiple collapse-tags clearable @change="socialChange">
                  <el-option v-for="item in options" :key="item.id" :label="item.dictName" :value="item"></el-option>
                </el-select>
              </el-form-item>
              <el-checkbox class="ml-20 mt-5" v-model="socialListData.outDisplay" :true-label="2"
                :false-label="1">对外不展示</el-checkbox>
            </div>
            <RelationMarkLayout :relationData.sync="relationData" :showTopRow="true" :formData="formData"
              :rowData="componentRowData" :relationError="relationError" :showComplex="false"/>

              <div class="text-center mt-50">
                <el-button size="small" class="mr-15 w-80" @click="cancalEditSocial()">取消</el-button>
                <el-button size="small" class="w-80" type="primary" @click="confirmEditSocial()">确定</el-button>
              </div>
          </el-form>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import dTable from '../../components/common/table'
import addrSelector from '../../components/common/addrSelector'
import RelationMarkLayout from '../offerFile/component/relationMarkLayout.vue'
export default {
  components: { addrSelector, dTable, RelationMarkLayout },
  data() {
    return {
      bussinssType: {
        allAddr: [],
      },
      formData: {
        bussinssType: ['1', '2'],
        status: '1',
        addrId: '',
        keyWord: '',
      },
      dialogVisible: false,
      pathData: [],
      socialListData: {
        socialList: [],
        outDisplay: 1,
      },
      rules: {
        socialList: [
          {
            type: 'array',
            required: true,
            message: '请选择调整险种',
            trigger: 'change',
          },
          {
            type: 'array',
            required: true,
            trigger: 'change',
            validator: this.checkRepeat,
          },
        ],
      },
      options: [],
      rowData: {},
      loading: null,
      relationData: {
        tplItems: [
          {
            items: [],
            tplType: "",
            tplTypeName: "",
            disabled: false,
            merge: false,
            mergeAddRule: {
              id: null,
              addrId: null,
              addrName: null,
              businessType: null,
              tplTypeCode: null,
              baseType: null,
              mergeType: null,
              bjFieldRule: null,
              mergeRule: null
            }
          }
        ],
        '1': [],
        '2': [],
        options: {}
      },
      sourceRelationData1: [],   //地区险种原始下拉
      tempSelectArray: [],    //每次下拉选择的
      componentRowData: {
        addrId: '',
        addrName: '',
        bussinssType: '1'
      },
      relationError: ''    //保存后错误信息
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    },
    getProductNameList() {
      return function (row) {
        var arr = []
        var productNameList = row.productName.split('|')
        this.options.forEach((item) => {
          productNameList.forEach((item2) => {
            if (item.dictName == item2) {
              arr.push(item2)
            }
          })
        })
        return arr.join('|')
      }
    },
  },
  watch: {
    tempSelectArray(newV, oldV) {
      // console.log('tempSelectArray--------', newV, oldV, this.componentRowData)
      if (!this.componentRowData.addrId) return
      if (newV.length > oldV.length) {
        //当前是新增勾选
        newV.forEach((element) => {
          if (this.sourceRelationData1.findIndex(item => item.itemCode == element.itemCode) == -1) {
            this.relationData['1'].push(element);
          }

        });
      } else if (newV.length < oldV.length) {
        //当前是减去勾选
        let a = newV
        let b = oldV
        let diffArray = a.filter(item => !b.some(element => element.itemCode === item.itemCode && element.itemName === item.itemName))
          .concat(b.filter(item => !a.some(element => element.itemCode === item.itemCode && element.itemName === item.itemName)));
        //找出relationData['1']中是否存在 被减去的勾选 并且不属于原始勾选项的 将其去掉 
        if (this.sourceRelationData1.findIndex(item => item.itemCode == diffArray[0].itemCode) > -1) {
          let preSpliceIndex = this.relationData['1'].findIndex(item => item.itemCode == diffArray[0].itemCode)
          if (preSpliceIndex != -1) {
            //移除该项
            this.relationData['1'].splice(preSpliceIndex, 1)
            //同时判断申报险种中是否有勾选该值 如果有也有去掉该勾选
            this.relationData.tplItems.forEach(obj => {
              obj.items = obj.items.filter(item => item.itemCode !== diffArray[0].itemCode)
            })
          }
        }
      }
    }
  },
  created() {
    let that = this
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.getDictByKey2('10007', '1')
    this.$nextTick(() => {
      that.getTableData()
      // this.getDictByKey()
    })
  },
  mounted() { },
  methods: {
    changeSearch() {
      this.getTableData()
    },
    //去新增 社保方案
    goAdd() {
      this.$router.push('/insuranceScheme/addSocial')
    },
    //方案变动时
    socialChange(list) {
      var productNameList = []
      var arr = []
      this.options.forEach((item) => {
        this.socialListData.socialList.forEach((item2) => {
          if (item.dictName == item2.dictName) {
            arr.push(item2)
          }
        })
      })
      this.socialListData.socialList = arr
      this.socialListData.socialList.forEach((item) => {
        productNameList.push(item.dictName)
      })
      this.socialListData.productNameList = productNameList.join(' | ')

      this.tempSelectArray = this.socialListData.socialList.map(item => {
        return {
          itemCode: item.dictCode,
          itemName: item.dictName
        }
      })
    },
    //取消调整
    cancalEditSocial() {
      this.dialogVisible = false
      this.relationError = ''
      this.$refs.ruleForm.resetFields()
    },
    //确定调整
    confirmEditSocial() {
      var self = this
      this.$refs.ruleForm.validate((vaild) => {
        if (vaild) {
          // console.log(this.socialListData.socialList)
          this.socialListData.socialList.forEach((item) => {
            item.itemCode = item.dictCode
            item.itemName = item.dictName
            item.productId = this.rowData.id
            item.id = this.rowData.id
          })
          this.rowData.items = this.socialListData.socialList
          this.rowData.outDisplay = this.socialListData.outDisplay
          this.showLoading(document.getElementById('upload-dialog'))
          this.$http({
            url: 'policy/product/saveEditNew',
            method: 'post',
            data:
            {
              product: this.rowData,
              tplItemDTO: {
                addrId: this.componentRowData.addrId,
                businessType: this.componentRowData.bussinssType,
                tplItems: this.relationData.tplItems,
                addrName: this.componentRowData.addrName
              }
            }
          })
            .then((res) => {
              if (res.data.code == 200 && res.data.data == '') {
                this.$message.success('调整成功')
                this.getTableData()
                this.dialogVisible = false
                this.relationError = ""
              } else {
                this.$message.error('调整失败')
                this.relationError = res.data.data
              }
              this.closeLoading()
            })
            .catch((err) => {
              this.closeLoading()
            })
        }
      })
    },
    //禁用方案
    disableSocial(row) {
      this.$confirm('一旦禁用，无法重新启用，是否确定禁用？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
          this.showLoading()
          this.$http({
            url: 'policy/product/updateStatus/' + row.id + '/0',
            method: 'post',
          })
            .then(({ data }) => {
              this.getTableData()
              this.closeLoading()
            })
            .catch(() => {
              this.closeLoading()
            })
        })
        .catch(() => { })
    },
    //开启调整窗口
    openEditSocial(row) {
      // console.log('row', row)
      this.dialogVisible = true
      this.rowData = this.deepCopy(row)
      this.socialListData.outDisplay = row.outDisplay == '2' ? 2 : 1
      var productNameList = this.rowData.productName.split('|')
      this.socialListData.productNameList = []
      this.socialListData.socialList = []
      this.options.forEach((item) => {
        productNameList.forEach((item2) => {
          if (item.dictName == item2) {
            this.socialListData.socialList.push(item)
            this.socialListData.productNameList.push(item.dictName)
          }
        })
      })
      this.socialListData.productNameList =
        this.socialListData.productNameList.join(' | ')
      this.componentRowData.addrId = row.addressId
      this.componentRowData.addrName = row.addressName


      let that = this
      that.$nextTick(() => {
        this.getPolicyItems(row.addressId).then(item => {
          //1代表 社保 参保险种新增只有社保
          this.findAddrTplItems(row.addressId, 1).then(item => {
            // that.tempSelectArray = that.socialListData.socialList.map(item => {
            //   return {
            //     itemCode: item.dictCode,
            //     itemName: item.dictName
            //   }
            // })
          })
        })
        console.log(productNameList, this.socialListData)
      })

    },
    //列表查询
    async getTableData(obj, pageChange, callback) {
      if (this.options.length <= 0) {
        await this.getDictByKey()
      }
      var params = obj
        ? obj
        : [
          { property: 'keyWord', value: this.formData.keyWord },
          { property: 'status', value: 1 },
        ]
      var self = this
      this.$refs.table.fetch({
        pageChange: pageChange ? pageChange : '',
        query: params,
        method: 'post',
        url: '/policy/product/page',
        callback: callback
          ? callback
          : function (res) {
            setTimeout(() => {
              self.$refs.table.doLayout()
            }, 2000)
          },
      })
    },
    changeBussinssType() {
      this.getTableData()
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
      if (this.loading.close) {
        this.loading.close()
      }
    },
    //获取字典值
    async getDictByKey() {
      return this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: '10003',
        },
      })
        .then((res) => {
          this.options = res.data.data
        })
        .catch((err) => {
          this.$message.error('字典接口出错，请稍后再试')
        })
    },
    //检测参保险种组合设置是否重复
    async checkRepeat(rule, value, callback) {
      var itemNameList = '?'
      var productNameList = []
      var rowDataProductNameList = this.rowData.productName
        ? this.rowData.productName.split('|')
        : []
      value.forEach((item) => {
        itemNameList += 'itemName=' + item.dictName + '&'
        productNameList.push(item.dictName)
      })
      if (
        productNameList.length == rowDataProductNameList.length &&
        productNameList.every((item) => {
          return rowDataProductNameList.includes(item)
        })
      ) {
        return callback()
      }
      await this.$http({
        url: 'policy/product/validate/' + this.rowData.addressId + itemNameList,
        method: 'post',
        headers: {
          customSuccess: true,
        },
      })
        .then((res) => {
          if (res.data.code == 200 && res.data.data) {
            return callback(new Error(res.data.data))
          } else if (res.data.code == 200 && !res.data.data) {
            return callback()
          } else if (res.data.code == 500) {
            return callback(new Error('校验参保组合失败，请稍后再试'))
          }
        })
        .catch((err) => {
          this.$message.error('校验参保组合失败，请稍后再试')
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
    //---------------------- 关联标记 组件相关 start ----------------------------------
    //报盘文件设置-获取关联标记信息
    findAddrTplItems(addrId, bussinssType) {
      if (!addrId) return
      this.showLoading()
      return this.$http({
        url: `policy/offerSettings/findAddrTplItems/${addrId}/${bussinssType}`,
        method: 'post',
      }).then(({ data }) => {
        if (data.data) {
          data.data.tplItems.forEach(item => {
            item.tplType == '10007004' ? item.disabled = true : ''
          })
          this.relationData.tplItems = data.data.tplItems ? data.data.tplItems : this.$options.data().relationData.tplItems
        } else {
          this.relationData.tplItems = this.$options.data().relationData.tplItems
        }
        this.closeLoading()
      }).catch(() => {
        this.relationData.tplItems = this.$options.data().relationData.tplItems
        this.closeLoading()
      })
    },
    //查询该地区方案所涵盖的险种（去重）
    getPolicyItems(addressId) {
      if (!addressId) return
      return this.$http({
        url: 'policy/product/getPolicyItems/' + addressId,
        method: 'post',
      }).then(res => {
        if (res && res.data.data && res.data.data[0] != null) {
          this.relationData['1'] = res.data.data
          this.sourceRelationData1 = res.data.data
        } else {
          this.relationData['1'] = []
          this.sourceRelationData1 = []
          this.tempSelectArray = []
        }
      })
    },
    //获取字典值
    getDictByKey2(dataKey, key) {
      this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: dataKey,
        },
      }).then(res => {
        this.relationData.options['1'] = res.data.data    //只有社保
      })
    },
    //---------------------- 关联标记 组件相关 end ----------------------------------
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

/deep/.el-drawer__body {
  padding-bottom: 0;
}

/deep/.el-dialog__header {
  border-bottom: none !important;
}

.file-content-box {
  padding: 20px 20px;

  .file-list {
    display: flex;
    padding: 10px 0;

    .file {
      background: #f1f8ff;
      border-radius: 10px;
      padding: 10px;
      position: relative;
      cursor: pointer;
      margin-right: 15px;

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
</style>
<style scoped>
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
</style>
<template>
  <!-- 报盘文件设置 -->
  <div class="spl-container">
    <breadcrumb :data="pathData">
      <el-button type="primary" slot="breadcrumb-btn" size="mini" @click="goAdd">新增</el-button>
    </breadcrumb>
    <div class="pl-20 pr-20">
      <el-row>
        <el-col :span="10">
          <div class="search-row display-flex">
            <!-- <span class="ml-20 label">参保地：</span> -->
            <el-input v-model.trim="formData.keyWord" placeholder="搜索关键字后回车查询" style="width: 300px" @change="changeSearch"
              clearable></el-input>
            <!-- <addrSelector v-model="formData.addrName" :addrArr="bussinssType.allAddr" @changeAddrSelect="changeSearch"></addrSelector> -->
            <!-- <el-button size="small" type="primary" class="ml-20 w-60" @click="getSocialTableData">查询</el-button> -->
          </div>
        </el-col>
        <!-- <el-col :span="6">
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
            <span class="ml-20 label">&nbsp;</span>
            <el-checkbox v-model="formData.status" true-label="1" false-label="" @change="changeBussinssType">只看有效</el-checkbox>
          </div>
        </el-col>-->
      </el-row>
      <div>
        <dTable @fetch="getTableData" ref="table" style="margin-top: 0" :tableHeight="tableHeight" :showIndex="true"
          :showSelection="false" row-key="id" row-id="id">
          <u-table-column prop="bussinssType" label="类型" width="200" header-align="center" align="center"
            :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{ scope.row.bussinssType == 1 ? '社保' : '公积金' }}</p>
            </template>
          </u-table-column>
          <u-table-column prop="addressName" label="参保城市" header-align="center" align="center"
            :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="productName" label="参保险种" width="300" header-align="center" align="center"
            :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{ (getProductNameList(scope.row)) }}</p>
            </template>
          </u-table-column>
          <u-table-column prop="status" label="状态" header-align="center" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>
                {{ scope.row.status == 1 ? '启用' : '禁用' }}
                <span>
                  {{
                    scope.row.outDisplay == 2 ? '（对外不展示）' : ''
                  }}
                </span>
              </p>
            </template>
          </u-table-column>
          <u-table-column label="操作" align="left" width="300">
            <template slot-scope="scope">
              <div v-if="scope.row.status == '1'">
                <el-button type="text" size="small" class="text-blue" @click="openEditSocial(scope.row, 1)">调整</el-button>
                <div class="opt-btn-split"></div>
                <el-button type="text" size="small" class="text-red" @click="disableSocial(scope.row)">禁用</el-button>
              </div>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>
    <!-- 关联文件 -->
    <el-dialog title="调整险种申报组合" :visible.sync="dialogVisible" width="1200px" class="cust-dialog"
      :before-close="cancalEditSocial" :close-on-click-modal="false">
      <!-- <span slot="title" class="dialog-footer">
        <div>调整险种申报组合</div>
      </span>-->
      <div id="upload-dialog">
        <div class="file-content-box">
          <div class="display-flex">
            <span>已选：</span>
            <span class="flex1">
              {{
                socialListData.productNameList
                ? socialListData.productNameList
                : '暂无'
              }}
            </span>
          </div>
          <el-form :model="socialListData" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
            <div class="display-flex mt-20">
              <el-form-item prop="socialList" label-width="0" style="width: 300px;">
                <el-select class="w-p100" v-model="socialListData.socialList" placeholder="请选择调整险种" value-key="dictCode"
                  multiple collapse-tags clearable @change="socialChange">
                  <el-option v-for="item in options" :key="item.id" :label="item.dictName" :value="item"></el-option>
                </el-select>
              </el-form-item>
              <el-checkbox class="ml-20 mt-5" v-model="socialListData.outDisplay" :true-label="2"
                :false-label="1">对外不展示</el-checkbox>
            </div>
            <RelationMarkLayout :relationData.sync="relationData" :showTopRow="true" :formData="formData"
              :rowData="componentRowData" :relationError="relationError" :showComplex="false"/>

              <div class="text-center mt-50">
                <el-button size="small" class="mr-15 w-80" @click="cancalEditSocial()">取消</el-button>
                <el-button size="small" class="w-80" type="primary" @click="confirmEditSocial()">确定</el-button>
              </div>
          </el-form>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import dTable from '../../components/common/table'
import addrSelector from '../../components/common/addrSelector'
import RelationMarkLayout from '../offerFile/component/relationMarkLayout.vue'
export default {
  components: { addrSelector, dTable, RelationMarkLayout },
  data() {
    return {
      bussinssType: {
        allAddr: [],
      },
      formData: {
        bussinssType: ['1', '2'],
        status: '1',
        addrId: '',
        keyWord: '',
      },
      dialogVisible: false,
      pathData: [],
      socialListData: {
        socialList: [],
        outDisplay: 1,
      },
      rules: {
        socialList: [
          {
            type: 'array',
            required: true,
            message: '请选择调整险种',
            trigger: 'change',
          },
          {
            type: 'array',
            required: true,
            trigger: 'change',
            validator: this.checkRepeat,
          },
        ],
      },
      options: [],
      rowData: {},
      loading: null,
      relationData: {
        tplItems: [
          {
            items: [],
            tplType: "",
            tplTypeName: "",
            disabled: false,
            merge: false,
            mergeAddRule: {
              id: null,
              addrId: null,
              addrName: null,
              businessType: null,
              tplTypeCode: null,
              baseType: null,
              mergeType: null,
              bjFieldRule: null,
              mergeRule: null
            }
          }
        ],
        '1': [],
        '2': [],
        options: {}
      },
      sourceRelationData1: [],   //地区险种原始下拉
      tempSelectArray: [],    //每次下拉选择的
      componentRowData: {
        addrId: '',
        addrName: '',
        bussinssType: '1'
      },
      relationError: ''    //保存后错误信息
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    },
    getProductNameList() {
      return function (row) {
        var arr = []
        var productNameList = row.productName.split('|')
        this.options.forEach((item) => {
          productNameList.forEach((item2) => {
            if (item.dictName == item2) {
              arr.push(item2)
            }
          })
        })
        return arr.join('|')
      }
    },
  },
  watch: {
    tempSelectArray(newV, oldV) {
      // console.log('tempSelectArray--------', newV, oldV, this.componentRowData)
      if (!this.componentRowData.addrId) return
      if (newV.length > oldV.length) {
        //当前是新增勾选
        newV.forEach((element) => {
          if (this.sourceRelationData1.findIndex(item => item.itemCode == element.itemCode) == -1) {
            this.relationData['1'].push(element);
          }

        });
      } else if (newV.length < oldV.length) {
        //当前是减去勾选
        let a = newV
        let b = oldV
        let diffArray = a.filter(item => !b.some(element => element.itemCode === item.itemCode && element.itemName === item.itemName))
          .concat(b.filter(item => !a.some(element => element.itemCode === item.itemCode && element.itemName === item.itemName)));
        //找出relationData['1']中是否存在 被减去的勾选 并且不属于原始勾选项的 将其去掉 
        if (this.sourceRelationData1.findIndex(item => item.itemCode == diffArray[0].itemCode) > -1) {
          let preSpliceIndex = this.relationData['1'].findIndex(item => item.itemCode == diffArray[0].itemCode)
          if (preSpliceIndex != -1) {
            //移除该项
            this.relationData['1'].splice(preSpliceIndex, 1)
            //同时判断申报险种中是否有勾选该值 如果有也有去掉该勾选
            this.relationData.tplItems.forEach(obj => {
              obj.items = obj.items.filter(item => item.itemCode !== diffArray[0].itemCode)
            })
          }
        }
      }
    }
  },
  created() {
    let that = this
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.getDictByKey2('10007', '1')
    this.$nextTick(() => {
      that.getTableData()
      // this.getDictByKey()
    })
  },
  mounted() { },
  methods: {
    changeSearch() {
      this.getTableData()
    },
    //去新增 社保方案
    goAdd() {
      this.$router.push('/insuranceScheme/addSocial')
    },
    //方案变动时
    socialChange(list) {
      var productNameList = []
      var arr = []
      this.options.forEach((item) => {
        this.socialListData.socialList.forEach((item2) => {
          if (item.dictName == item2.dictName) {
            arr.push(item2)
          }
        })
      })
      this.socialListData.socialList = arr
      this.socialListData.socialList.forEach((item) => {
        productNameList.push(item.dictName)
      })
      this.socialListData.productNameList = productNameList.join(' | ')

      this.tempSelectArray = this.socialListData.socialList.map(item => {
        return {
          itemCode: item.dictCode,
          itemName: item.dictName
        }
      })
    },
    //取消调整
    cancalEditSocial() {
      this.dialogVisible = false
      this.relationError = ''
      this.$refs.ruleForm.resetFields()
    },
    //确定调整
    confirmEditSocial() {
      var self = this
      this.$refs.ruleForm.validate((vaild) => {
        if (vaild) {
          // console.log(this.socialListData.socialList)
          this.socialListData.socialList.forEach((item) => {
            item.itemCode = item.dictCode
            item.itemName = item.dictName
            item.productId = this.rowData.id
            item.id = this.rowData.id
          })
          this.rowData.items = this.socialListData.socialList
          this.rowData.outDisplay = this.socialListData.outDisplay
          this.showLoading(document.getElementById('upload-dialog'))
          this.$http({
            url: 'policy/product/saveEditNew',
            method: 'post',
            data:
            {
              product: this.rowData,
              tplItemDTO: {
                addrId: this.componentRowData.addrId,
                businessType: this.componentRowData.bussinssType,
                tplItems: this.relationData.tplItems,
                addrName: this.componentRowData.addrName
              }
            }
          })
            .then((res) => {
              if (res.data.code == 200 && res.data.data == '') {
                this.$message.success('调整成功')
                this.getTableData()
                this.dialogVisible = false
                this.relationError = ""
              } else {
                this.$message.error('调整失败')
                this.relationError = res.data.data
              }
              this.closeLoading()
            })
            .catch((err) => {
              this.closeLoading()
            })
        }
      })
    },
    //禁用方案
    disableSocial(row) {
      this.$confirm('一旦禁用，无法重新启用，是否确定禁用？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
          this.showLoading()
          this.$http({
            url: 'policy/product/updateStatus/' + row.id + '/0',
            method: 'post',
          })
            .then(({ data }) => {
              this.getTableData()
              this.closeLoading()
            })
            .catch(() => {
              this.closeLoading()
            })
        })
        .catch(() => { })
    },
    //开启调整窗口
    openEditSocial(row) {
      // console.log('row', row)
      this.dialogVisible = true
      this.rowData = this.deepCopy(row)
      this.socialListData.outDisplay = row.outDisplay == '2' ? 2 : 1
      var productNameList = this.rowData.productName.split('|')
      this.socialListData.productNameList = []
      this.socialListData.socialList = []
      this.options.forEach((item) => {
        productNameList.forEach((item2) => {
          if (item.dictName == item2) {
            this.socialListData.socialList.push(item)
            this.socialListData.productNameList.push(item.dictName)
          }
        })
      })
      this.socialListData.productNameList =
        this.socialListData.productNameList.join(' | ')
      this.componentRowData.addrId = row.addressId
      this.formData.addrId = row.addressId
      this.componentRowData.addrName = row.addressName


      let that = this
      that.$nextTick(() => {
        this.getPolicyItems(row.addressId).then(item => {
          //1代表 社保 参保险种新增只有社保
          this.findAddrTplItems(row.addressId, 1).then(item => {
            // that.tempSelectArray = that.socialListData.socialList.map(item => {
            //   return {
            //     itemCode: item.dictCode,
            //     itemName: item.dictName
            //   }
            // })
          })
        })
        console.log(productNameList, this.socialListData)
      })

    },
    //列表查询
    async getTableData(obj, pageChange, callback) {
      if (this.options.length <= 0) {
        await this.getDictByKey()
      }
      var params = obj
        ? obj
        : [
          { property: 'keyWord', value: this.formData.keyWord },
          { property: 'status', value: 1 },
        ]
      var self = this
      this.$refs.table.fetch({
        pageChange: pageChange ? pageChange : '',
        query: params,
        method: 'post',
        url: '/policy/product/page',
        callback: callback
          ? callback
          : function (res) {
            setTimeout(() => {
              self.$refs.table.doLayout()
            }, 2000)
          },
      })
    },
    changeBussinssType() {
      this.getTableData()
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
      if (this.loading.close) {
        this.loading.close()
      }
    },
    //获取字典值
    async getDictByKey() {
      return this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: '10003',
        },
      })
        .then((res) => {
          this.options = res.data.data
        })
        .catch((err) => {
          this.$message.error('字典接口出错，请稍后再试')
        })
    },
    //检测参保险种组合设置是否重复
    async checkRepeat(rule, value, callback) {
      var itemNameList = '?'
      var productNameList = []
      var rowDataProductNameList = this.rowData.productName
        ? this.rowData.productName.split('|')
        : []
      value.forEach((item) => {
        itemNameList += 'itemName=' + item.dictName + '&'
        productNameList.push(item.dictName)
      })
      if (
        productNameList.length == rowDataProductNameList.length &&
        productNameList.every((item) => {
          return rowDataProductNameList.includes(item)
        })
      ) {
        return callback()
      }
      await this.$http({
        url: 'policy/product/validate/' + this.rowData.addressId + itemNameList,
        method: 'post',
        headers: {
          customSuccess: true,
        },
      })
        .then((res) => {
          if (res.data.code == 200 && res.data.data) {
            return callback(new Error(res.data.data))
          } else if (res.data.code == 200 && !res.data.data) {
            return callback()
          } else if (res.data.code == 500) {
            return callback(new Error('校验参保组合失败，请稍后再试'))
          }
        })
        .catch((err) => {
          this.$message.error('校验参保组合失败，请稍后再试')
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
    //---------------------- 关联标记 组件相关 start ----------------------------------
    //报盘文件设置-获取关联标记信息
    findAddrTplItems(addrId, bussinssType) {
      if (!addrId) return
      this.showLoading()
      return this.$http({
        url: `policy/offerSettings/findAddrTplItems/${addrId}/${bussinssType}`,
        method: 'post',
      }).then(({ data }) => {
        if (data.data) {
          data.data.tplItems.forEach(item => {
            item.tplType == '10007004' ? item.disabled = true : ''
          })
          this.relationData.tplItems = data.data.tplItems ? data.data.tplItems : this.$options.data().relationData.tplItems
        } else {
          this.relationData.tplItems = this.$options.data().relationData.tplItems
        }
        this.closeLoading()
      }).catch(() => {
        this.relationData.tplItems = this.$options.data().relationData.tplItems
        this.closeLoading()
      })
    },
    //查询该地区方案所涵盖的险种（去重）
    getPolicyItems(addressId) {
      if (!addressId) return
      return this.$http({
        url: 'policy/product/getPolicyItems/' + addressId,
        method: 'post',
      }).then(res => {
        if (res && res.data.data && res.data.data[0] != null) {
          this.relationData['1'] = res.data.data
          this.sourceRelationData1 = res.data.data
        } else {
          this.relationData['1'] = []
          this.sourceRelationData1 = []
          this.tempSelectArray = []
        }
      })
    },
    //获取字典值
    getDictByKey2(dataKey, key) {
      this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: dataKey,
        },
      }).then(res => {
        this.relationData.options['1'] = res.data.data    //只有社保
      })
    },
    //---------------------- 关联标记 组件相关 end ----------------------------------
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

/deep/.el-drawer__body {
  padding-bottom: 0;
}

/deep/.el-dialog__header {
  border-bottom: none !important;
}

.file-content-box {
  padding: 20px 20px;

  .file-list {
    display: flex;
    padding: 10px 0;

    .file {
      background: #f1f8ff;
      border-radius: 10px;
      padding: 10px;
      position: relative;
      cursor: pointer;
      margin-right: 15px;

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
</style>
<style scoped>
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
</style>
