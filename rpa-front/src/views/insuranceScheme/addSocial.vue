<template>
  <div class="spl-container">
    <breadcrumb :data="pathData">
      <el-button type="primary" slot="breadcrumb-btn" size="mini" @click="confirmSave">确定保存</el-button>
    </breadcrumb>
    <div class="pl-20 pr-20">
      <el-form :model="listData" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-row>
          <el-col :span="10">
            <div class="search-row display-flex">
              <el-form-item prop="addressName" style="margin-bottom: 0" label="参保地">
                <addrSelector v-model="listData.addressName" :addrArr="allAddr" @changeAddrSelect="changeAddr">
                </addrSelector>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
        <div class="pl-20 pr-20" :style="{ height: tableHeight, overflow: 'auto' }">
          <el-row>
            <el-col :span="24" v-for="(item, index) in listData.formData" :key="index">
              <div class="search-row display-flex">
                <div class="select-add-box">
                  <span class="select-icon" @click="addSocial" v-if="index == 0">+</span>
                  <span class="select-icon2" @click="removeSocial(index)" v-else>-</span>
                  <span class="select-title">社保申报组合{{ index + 1 }}</span>
                </div>
              </div>
              <div class="select-content-box">
                <div>
                  <el-form-item :prop="'formData.' + index + '.items'" label-width="0" style="margin-bottom: 0"
                    :rules="rules.items">
                    <el-select v-model="item.items" placeholder="请选择" value-key="dictCode" multiple collapse-tags
                      clearable @change="socialChange($event, index)" :disabled="!listData.addressId">
                      <el-option v-for="item in options" :key="item.id" :label="item.dictName" :value="item">
                      </el-option>
                    </el-select>
                    <span class="select-nameList">已选：{{
                      productNameList(item.items)
                      ? productNameList(item.items)
                      : '暂无'
                    }}
                    </span>
                    <el-checkbox style="margin-left: 50px" v-model="item.outDisplay" :true-label="2"
                      :false-label="1">对外不展示</el-checkbox>
                  </el-form-item>
                </div>
              </div>
            </el-col>
          </el-row>
          <el-row class="mt-15" v-if="listData.addressId">
            <el-col :span="22" style="padding: 10px 20px;">
              <RelationMarkLayout :relationData.sync="relationData" :formData="formData" :rowData="rowData"
                :relationError="relationError" :showComplex="false">
              </RelationMarkLayout>
            </el-col>
          </el-row>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import addrSelector from '../../components/common/addrSelector'
import RelationMarkLayout from '../offerFile/component/relationMarkLayout.vue'
import { MessageBox } from 'element-ui'

export default {
  components: {
    addrSelector,
    RelationMarkLayout
  },
  data() {
    return {
      pathData: [],
      listData: {
        addressName: '',
        addressId: '',
        formData: [
          {
            addressId: '',
            addressName: '',
            bussinssType: 1,
            id: '',
            bussinssTypeName: '社保',
            items: [],
            outDisplay: 1,
            status: 1,
          },
        ],
      },
      rules: {
        addressName: [
          { required: true, message: '请选择参保地', trigger: 'change' },
        ],
        items: [
          {
            type: 'array',
            required: true,
            message: '请勾选添加参保险种',
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
      allAddr: [],
      options: [],
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
      formData: {
        bussinssType: ['1', '2'],
        status: '1',
        addrId: '',
        addrName: ""
      },
      sourceRelationData1: [],   //地区险种原始下拉
      tempSelectArray: [],    //每次下拉选择的
      rowData: {
        addrId: '',
        addrName: '',
        bussinssType: '1'
      },
      relationError: ''    //保存后错误信息
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 220 + 'px'
    },
    productNameList: function () {
      return function (item) {
        var str = []
        item.forEach((item1) => {
          str.push(item1.dictName)
        })
        return str.join(' | ')
      }
    },
  },
  watch: {
    'listData.addressId'(newV, oldV) {
      // console.log(newV, oldV)
      if (newV) {
        // 获取对应参保地的关联标记
        this.getPolicyItems(newV).then(item => {
          //1代表 社保 参保险种新增只有社保
          this.findAddrTplItems(newV, 1).then(item => {
          })
        })
      }
    },
    tempSelectArray(newV, oldV) {
      // console.log(newV, oldV)
      if (!this.listData.addressId) return
      if (newV.length > oldV.length) {
        //当前是新增勾选
        newV.forEach((element) => {
          if (this.sourceRelationData1.findIndex(item => item.itemCode == element.itemCode) == -1) {
            this.relationData['1'].push(element);
            console.log(element)
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
    this.formData.addrId = 440100
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
    }
    this.pathData.push({ name: '新增' })
    this.getAddr()
    this.getDictByKey()
    this.getDictByKey2('10007', '1')

  },
  methods: {
    //添加社保
    addSocial() {
      this.listData.formData.push({
        addressId: '',
        addressName: '',
        bussinssType: 1,
        id: '',
        bussinssTypeName: '社保',
        outDisplay: 1,
        items: [],
        status: 1,
      })
    },
    removeSocial(index) {
      this.listData.formData.splice(index, 1)
    },
    //下拉改变时
    socialChange(val, index) {
      // this.$refs.ruleForm.validate(item=>{})
      var arr = []
      this.options.forEach(item => {
        this.listData.formData[index].items.forEach((item2, index2) => {
          if (item.dictName == item2.dictName) {
            arr.push(item2)
          }
        })
      })

      this.listData.formData[index].items = arr
      //需要将this.listData.formData 里面勾选的项目归纳出来 然后和this.relationData['1']做并集再去重
      this.tempSelectArray = this.listData.formData.map(item => {
        return item.items.map(sitem => {
          return {
            itemCode: sitem.dictCode,
            itemName: sitem.dictName
          }
        })
      }).flat()


    },
    changeAddr(item) {
      if (!item.id) return
      this.listData.addressName = item.name
      this.listData.addressId = item.id
      this.rowData.addrId = item.id
      this.rowData.addrName = item.name
      //获取对应参保地的关联标记
      // this.getPolicyItems(item.id).then(item => {
      //   this.findAddrTplItems(item.id, 1).then(item => {
      //   })
      // })

      this.$refs.ruleForm.validate((item) => { })
    },
    confirmSave() {
      this.$refs.ruleForm.validate((vaild) => {
        if (vaild) {
          this.listData.formData.forEach((item) => {
            item.addressId = this.listData.addressId
            item.addressName = this.listData.addressName
            item.items.forEach((item1) => {
              item1.itemCode = item1.dictCode
              item1.itemName = item1.dictName
              item1.productId = ''
            })
          })
          this.showLoading()
          this.$http({
            url: 'policy/product/saveAddNew',
            method: 'post',
            data: {
              products: this.listData.formData,
              tplItemDTO: {
                addrId: this.rowData.addrId,
                businessType: this.rowData.bussinssType,
                tplItems: this.relationData.tplItems,
                addrName: this.rowData.addrName
              }
            }
          })
            .then((res) => {
              if (res.data.code == 200 && res.data.data == '') {
                this.$message.success('保存成功')
                setTimeout(() => {
                  this.$router.push('/insuranceScheme/index')
                  MessageBox.close()
                }, 1500)
              } else {
                this.$message.error('保存失败')
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
    //获取字典值
    getDictByKey() {
      this.$http({
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
      var check = true
      var count = 0
      value.forEach((item) => {
        itemNameList += 'itemName=' + item.dictName + '&'
        productNameList.push(item.dictName)
      })

      this.listData.formData.forEach((item, index) => {
        var nameList = []
        item.items.forEach((item1) => {
          nameList.push(item1.dictName)
        })
        if (
          productNameList.length == nameList.length &&
          productNameList.every((item2) => {
            return nameList.includes(item2)
          })
        ) {
          count++
        }
        if (count >= 2) {
          check = false
        }
      })
      if (!check) {
        return callback(new Error('与当前设置的参保组合重复，请修正'))
      }

      if (!this.listData.addressId) {
        return
      }

      await this.$http({
        url:
          'policy/product/validate/' + this.listData.addressId + itemNameList,
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
      console.log(addressId)
      if (!addressId) return
      return this.$http({
        url: 'policy/product/getPolicyItems/' + addressId,
        method: 'post',
      }).then(res => {
        if (res && res.data.data) {
          this.relationData['1'] = res.data.data
          this.sourceRelationData1 = res.data.data
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
.select-add-box {
  display: flex;
  align-items: center;

  .select-icon {
    display: block;
    width: 20px;
    height: 20px;
    border: 1px solid #3e82ff;
    color: #3e82ff;
    text-align: center;
    cursor: pointer;
  }

  .select-icon2 {
    display: block;
    width: 20px;
    height: 20px;
    border: 1px solid red;
    color: red;
    text-align: center;
    cursor: pointer;
  }

  .select-title {
    margin-left: 10px;
  }
}

.select-content-box {
  padding: 0px 20px;

  .select-nameList {
    margin-left: 10px;
    font-size: 12px;
  }
}
</style>
