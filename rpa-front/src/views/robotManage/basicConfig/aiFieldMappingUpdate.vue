<template>
  <div class="content spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData">
      <template #breadcrumb-btn>
        <el-button type="primary" class="s-btn" @click="doSave">确定保存</el-button>
      </template>
    </breadcrumb>
    <div style="padding: 20px;">
      <div class="type-title"><span>字段集</span> <span class="text">可引用映射区逻辑的字段</span></div>
      <div class="type-content field-area">
        <div class="display-flex">
          <p>已选{{fieldForm.fields.length}}：</p>
          <p class="flex1"><span v-if="fieldForm.fields.length>0">{{ fieldForm.fields.join("、") }}</span>
            <span v-else class="text-gray">暂无，请先添加</span> <i
            class="el-icon-edit text-blue font-16 f-cursor ml-20" @click="fieldForm.showEdit=!fieldForm.showEdit"></i></p>
        </div>
        <div v-show="fieldForm.showEdit">
          <div class="mt-10">
            <el-input v-model.trim="fieldForm.keyWord" @keyup.enter.native="searchField" placeholder="输入关键字回车搜索"
                      style="width:300px;">
              <i @click="searchField" slot="suffix" class="el-input__icon el-icon-search f-cursor font-16 fw-blod"
                 style="color: #dbdbdb;"></i>
            </el-input>
          </div>
          <div class="display-flex mt-15" style="flex-flow: wrap">
            <span class="mr-30 text-gray lh-com" v-if="fieldForm.noFilterData">无匹配数据</span>
            <div class="tag" :class="{'selected': handleFieldSelected(item)}" v-for="item in fieldForm.filterFieldList" :key="item"
                 @click="toggleSelect(item)">{{item}} <i class="el-icon-success ic-select"></i></div>

            <!--自定义-->
            <div class="tag" :class="{'selected': handleFieldSelected(item)}" v-for="(item, index) in fieldForm.custFields" :key="index"
                 @click="toggleSelectCust(item)">{{item}} <i class="el-icon-success ic-select"></i></div>

            <el-input v-if="fieldForm.showAddValue" class="w-180" ref="addValue" v-model.trim="fieldForm.addValue"
                      @keyup.enter.native="doAddValue" placeholder="输入后回车，点击选中"></el-input>
            <el-button v-else size="small" class="btn--border-blue s-btn" @click="doShowAddValue"><i class="el-icon-plus"></i> 添加字段</el-button>
          </div>
        </div>
      </div>

      <div class="type-title" style="margin-top: 5px;"><span>映射区</span> <span
        class="text">确定与字段所填值同义的申报值</span></div>
      <div class="type-content field-area">
        <table class="cust-table-border w-p100">
          <thead>
          <tr>
            <th style="width: 50px">映射组</th>
            <th style="width: 400px"><span class="required-pre">字段填值</span></th>
            <th>申报值（黑色即优先匹配）</th>
            <th style="width: 80px">勾选</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(item, index) in mapForm.tableData" :key="index">
            <td>{{index+1}}</td>
            <td style="vertical-align: top">
              <div v-if="mapForm.curEditIndex===index">
                <div class="display-flex">
                  <p>已选{{item.inputItemList.length}}：</p>
                  <p class="flex1">
                    <span class="inputItemTag" v-for="(it, ind) in item.inputItemList" :key="ind">{{ind!==0?'、':''}}{{it}}
                      <i class="el-icon-error ic-del" @click="doDelInputItem(index, ind)"></i></span>
                  </p>
                </div>
                <div class="mt-15">
                  <el-input
                    v-model="mapForm.inputItemInput"
                    type="textarea"
                    rows="7"
                    resize="none"
                    placeholder="一行输入一个值"
                  ></el-input>
                  <div class="text-right mt-10">已输入{{mapForm.inputItemArr.length}}个
                    <el-button size="small" class="btn--border-blue ml-10" @click="doAddInputItem(index)">添加</el-button>
                  </div>
                </div>
              </div>
              <div v-else>
                <div class="display-flex">
                  <p class="flex1"><span>{{item.inputItemList.join("、")}}</span></p>
                </div>
              </div>
            </td>
            <td style="vertical-align: top">
              <div class="display-flex">
                <p v-if="mapForm.curEditIndex===index">已选{{handleDeclareItems(item.fieldOptions).length}}：</p>
                <p class="flex1" v-html="handleDeclareItems(item.fieldOptions).join('、')"></p>
              </div>
              <div v-if="mapForm.curEditIndex===index">
                <el-input class="mt-15" v-model.trim="mapForm.keyWord" @keyup.enter.native="searchDeclareItem(index)" placeholder="输入关键字回车搜索"
                          style="width:300px;">
                  <i @click="searchDeclareItem" slot="suffix" class="el-input__icon el-icon-search f-cursor font-16 fw-blod"
                     style="color: #dbdbdb;"></i>
                </el-input>
                <table class="cust-table-border w-p100 mt-10">
                  <thead>
                  <tr>
                    <th><span class="required-pre">申报值</span></th>
                    <th style="width: 180px"><span class="required-pre">匹配策略</span></th>
                    <th style="width: 50px">勾选 <el-checkbox :true-labe="true" :false-labe="false" v-model="item.checkAll" @change="val => handleCheckAllChange(val, index)"></el-checkbox></th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="it in item.fieldOptions" :key="it.fieldValue" v-show="it.isShow">
                    <td>{{it.fieldValue}}</td>
                    <td>
                      <el-radio-group v-model="it.strategy">
                        <!--匹配策略，1：优先，0：次之-->
                        <el-radio :label="1">优先</el-radio>
                        <el-radio :label="0">次之</el-radio>
                      </el-radio-group>
                    </td>
                    <td class="text-center">
                      <el-checkbox :true-labe="true" :false-labe="false" v-model="it.isSelected" @change="val => handleCheckChange(val, index)"></el-checkbox>
                    </td>
                  </tr>
                  </tbody>
                </table>
                <p class="text-center text-gray pt-10" v-show="handleDeclareItemsFilterRes(index)">无匹配数据</p>
              </div>
            </td>
            <td>
              <div v-if="mapForm.curEditIndex===index">
                <span class="text-gray f-cursor" @click="handleCancelDeclareItem(index)">取消</span>
                <span class="text-blue f-cursor ml-20" @click="handleConfirmDeclareItem(index)">确定</span>
              </div>
              <span v-if="mapForm.curEditIndex===''" class="text-blue f-cursor" @click="showEditDeclareItem(index)">编辑</span>
            </td>
          </tr>
          </tbody>
        </table>

        <div class="text-center" v-if="mapForm.curEditIndex===''">
          <el-button class="btn--border-blue mt-20 w-300" @click="handleAddDeclareItem"><i class="el-icon-plus"></i> 新增映射</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  components: {},
  data () {
    return {
      pathData: [],
      uuid: '',
      itemUuid: '',
      id: '',
      fieldForm: {
        fields: [],
        fieldResource: [],
        keyWord: '',
        addValue: '',
        showAddValue: false,
        custFields: [], // 自定义添加的字段
        filterFieldList: [],
        noFilterData: false, // 没匹配到数据时显示
        showEdit: false
      },
      mapForm: {
        tableData: [],
        radio: 1,
        inputItemInput: '',
        inputItemArr: '',
        keyWord: '',
        filterWord: '',
        editRowTemp: null, // 用来暂存编辑前的数据
        curEditIndex: ''
      }
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 310 + 'px'
    }
  },
  watch: {
    'mapForm.inputItemInput' (newVal, oldVal) {
      var obj = this.$global.getInputDataRow(newVal)
      let arr = []
      obj.value.map(item => {
        if (item.replace(/\s+/g, '')) {
          arr.push(item)
        }
      })
      this.mapForm.inputItemArr = arr
    }
  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
    }
    this.pathData.push({ name: 'AI值映射', path: '/basicConfig/aiFieldMapping' })
    this.pathData.push({ name: '维护映射' })

    if (this.$route.query.uuid) {
      this.uuid = this.$route.query.uuid
      if (this.$route.query.itemUuid != undefined && this.$route.query.itemUuid != null) {
        this.itemUuid = this.$route.query.itemUuid
      }
      this.getData()
    } else {
      this.fieldForm.showEdit = true
      this.handleAddDeclareItem()
    }
    this.getFieldResource()
  },
  mounted () {
  },
  methods: {
    // 获取详情
    getData () {
      this.$http({
        url: `/policy/ai/fieldMapping/getMapping/${this.uuid}`,
        method: 'post'
      }).then((data) => {
        if (data.data.code === 200) {
          let res = data.data.data
          this.id = res.id
          this.fieldForm.fields = res.fields

          let inputItemList = []
          res.itemBases.map(item => {
            inputItemList = []
            item.inputItems.map(it => {
              inputItemList.push(it.fieldValue)
            })
            item.inputItemList = inputItemList
          })
          this.mapForm.tableData = res.itemBases
          this.getFieldOptions('init')
        }
      }).catch(() => {
      })
    },
    // 获取字段集所有选项
    getFieldResource () {
      this.$http({
        url: `/policy/ai/fieldMapping/getFieldResource?uuid=` + this.uuid,
        method: 'post'
      }).then((data) => {
        if (data.data.code === 200) {
          this.fieldForm.fieldResource = data.data.data
        }
      }).catch(() => {
      })
    },
    // 字段集-筛选
    searchField () {
      let list = []
      if (this.fieldForm.keyWord) {
        this.fieldForm.fieldResource.map(item => {
          if (item.indexOf(this.fieldForm.keyWord) > -1) {
            list.push(item)
          }
        })
      }
      this.fieldForm.filterFieldList = list
      this.fieldForm.noFilterData = !!(this.fieldForm.keyWord && list.length === 0)
    },
    // 字段集-字段是否被选中
    handleFieldSelected (name) {
      return this.fieldForm.fields.indexOf(name) > -1
    },
    //   字段集-字段选择、去除选择
    toggleSelect (value) {
      let fields = this.fieldForm.fields
      let index = fields.indexOf(value)
      if (index > -1) {
        fields.splice(index, 1)
        this.getFieldOptions()
      } else {
        this.$http({
          url: `/policy/ai/fieldMapping/validate?uuid=` + this.uuid + '&value=' + value,
          method: 'post'
        }).then((data) => {
          if (data.data.code === 200) {
            if (data.data.data) {
              this.$message.error(data.data.data)
            } else {
              fields.push(value)
              this.getFieldOptions()
            }
          }
        }).catch(() => {
        })
      }
    },
    //   字段集-字段选择、去除选择（自定义）(添加字段的时候已经校验过了，所以这里选择时不用再校验)
    toggleSelectCust (value) {
      let fields = this.fieldForm.fields
      let index = fields.indexOf(value)
      if (index > -1) {
        fields.splice(index, 1)
        this.getFieldOptions()
      } else {
        fields.push(value)
      }
      this.getFieldOptions()
    },
    // 显示自定义输入框
    doShowAddValue () {
      this.fieldForm.showAddValue = true
      this.$nextTick(() => {
        this.$refs.addValue.focus()
      })
    },
    //   字段集-添加字段
    doAddValue () {
      let value = this.fieldForm.addValue
      let fields = this.fieldForm.fields
      let index = fields.indexOf(value)
      if (value) {
        let custFields = this.fieldForm.custFields
        let custIndex = custFields.indexOf(value)
        if (custIndex === -1) {
          this.$http({
            url: `/policy/ai/fieldMapping/validate?uuid=` + this.uuid + '&value=' + value,
            method: 'post'
          }).then((data) => {
            if (data.data.code === 200) {
              if (data.data.data) {
                this.$message.error(data.data.data)
              } else {
                if (index === -1) {
                  fields.push(value)
                  this.getFieldOptions()
                }
                custFields.push(value)
                this.fieldForm.addValue = ''
                this.fieldForm.showAddValue = false
              }
            }
          }).catch(() => {
          })
        } else {
          if (index === -1) {
            fields.push(value)
            this.getFieldOptions()
          }
          this.fieldForm.addValue = ''
          this.fieldForm.showAddValue = false
        }
      }
    },

    // 映射区-字段填值-添加
    doAddInputItem (index) {
      let arr = this.mapForm.inputItemArr
      let curInputItemList = this.mapForm.tableData[index].inputItemList
      if (arr === 0) {
        return
      }
      let item
      let fieldName = ''
      let addArr = []
      for (let i = 0; i < arr.length; i++) {
        fieldName = arr[i]
        for (let j = 0; j < this.mapForm.tableData.length; j++) {
          item = this.mapForm.tableData[j]
          if (index !== j && item.inputItemList.indexOf(fieldName) > -1) {
            // 状态  1：启用，0：禁用
            this.$message.error('字段填值{' + fieldName + '}已在' + (item.status === 1 ? '启用' : '禁用') + '的映射组下存在，请勿重复添加')
            return
          }
        }

        if (curInputItemList.indexOf(fieldName) === -1 && addArr.indexOf(fieldName) === -1) {
          addArr.push(fieldName)
        }
      }
      this.mapForm.tableData[index].inputItemList = curInputItemList.concat(addArr)
      this.resetInputItem()
    },
    // 映射区-字段填值-重置编辑
    resetInputItem () {
      this.mapForm.inputItemInput = ''
      this.mapForm.inputItemArr = []
    },
    // 映射区-字段填值-删除
    doDelInputItem (index, ind) {
      this.mapForm.tableData[index].inputItemList.splice(ind, 1)
    },

    // 映射区-申报值-已选
    handleDeclareItems (fieldOptions) {
      if (!fieldOptions) {
        return []
      }
      let list = []
      let list2 = []
      fieldOptions.map(item => {
        if (item.isSelected) {
          // 匹配策略，1：优先，0：次之
          if (item.strategy === 1) {
            list.push('<span>' + item.fieldValue + '</span>')
          } else {
            list2.push("<span class='text-gray'>" + item.fieldValue + '</span>')
          }
        }
      })
      return list.concat(list2)
    },
    // 映射区-申报值-搜索
    searchDeclareItem (index) {
      this.mapForm.filterWord = this.mapForm.keyWord
      let item = this.mapForm.tableData[index]
      item.fieldOptions.map(item => {
        item.isShow = this.mapForm.filterWord === '' || item.fieldValue.indexOf(this.mapForm.filterWord) > -1
      })
      this.$set(item, 'checkAll', this.checkIsAllSelect(item.fieldOptions))
    },
    // 映射区-申报值-获取下拉值
    getFieldOptions (type) {
      if (this.fieldForm.fields.length === 0) {
        this.mapForm.tableData.map((item, index) => {
          this.$set(item, 'fieldOptions', [])
          this.$set(item, 'checkAll', false)
        })
        return
      }
      let that = this
      this.$http({
        url: `/policy/ai/fieldMapping/getFieldOptions?fieldNames=` + this.fieldForm.fields.join(','),
        method: 'post'
      }).then((data) => {
        if (data.data.code === 200) {
          let res = data.data.data
          let fieldOptions = []
          let isSelected = false
          let declareItems = []
          var editIndex = null
          this.mapForm.tableData.map((item, index) => {
            if (item.uuid === that.itemUuid) {
              editIndex = index
            }
            fieldOptions = []
            declareItems = type === 'init' ? item.declareItems : item.fieldOptions
            res.map(it => {
              isSelected = false
              var strategy = 1
              for (var i = 0; i < declareItems.length; i++) {
                if (declareItems[i].fieldValue === it.fieldValue) {
                  strategy = declareItems[i].strategy
                  isSelected = type === 'init' ? true : declareItems[i].isSelected
                  break
                }
              }
              fieldOptions.push({
                fieldValue: it.fieldValue,
                strategy: isSelected?strategy:it.strategy,
                isSelected: isSelected,
                isShow: that.mapForm.filterWord === '' || it.fieldValue.indexOf(that.mapForm.filterWord) > -1
              })
            })
            console.log(this.mapForm.filterWord)
            console.log(fieldOptions)
            this.$set(item, 'fieldOptions', fieldOptions)
            this.$set(item, 'checkAll', this.checkIsAllSelect(fieldOptions))
          })
        }
        if (editIndex != null) {
          that.$nextTick(() => {
            that.showEditDeclareItem(editIndex)
          })
        }
      }).catch(() => {
      })
    },
    //   映射区-申报值-新增映射
    handleAddDeclareItem () {
      this.mapForm.editRowTemp = null
      let length = this.mapForm.tableData.length
      let fieldOptions = []
      if (length !== 0) {
        fieldOptions = JSON.parse(JSON.stringify(this.mapForm.tableData[length - 1].fieldOptions))
      }
      fieldOptions.map(item => {
        item.strategy = 1
        item.isSelected = false
      })
      this.mapForm.tableData.push({
        inputItemList: [],
        fieldOptions: fieldOptions
      })
      this.mapForm.curEditIndex = length

      if (length === 0) {
        this.getFieldOptions()
      }
    },
    //   映射区-申报值-显示编辑区
    showEditDeclareItem (index) {
      let item = this.mapForm.tableData[index]
      item.fieldOptions.map(it => {
        it.isShow = this.mapForm.filterWord === '' || it.fieldValue.indexOf(this.mapForm.filterWord) > -1
      })
      this.mapForm.editRowTemp = JSON.parse(JSON.stringify(item))
      this.mapForm.curEditIndex = index
    },
    //   映射区-申报值-取消
    handleCancelDeclareItem (index) {
      if (this.mapForm.editRowTemp) {
        this.$set(this.mapForm.tableData, index, JSON.parse(JSON.stringify(this.mapForm.editRowTemp)))
      } else {
        this.mapForm.tableData.splice(this.mapForm.curEditIndex, 1)
      }
      this.mapForm.curEditIndex = ''
      this.mapForm.keyWord = ''
      this.mapForm.filterWord = ''
      this.resetInputItem()
    },
    //   映射区-申报值-确定
    handleConfirmDeclareItem (index) {
      let item = this.mapForm.tableData[index]
      if (item.inputItemList.length === 0) {
        this.$message.warning('请先添加字段填值')
        return
      }
      this.mapForm.curEditIndex = ''
      this.mapForm.keyWord = ''
      this.mapForm.filterWord = ''
    },
    //   映射区-申报值-校验是否全选
    checkIsAllSelect (list) {
      let res = true
      let count = 0
      for (let i = 0; i < list.length; i++) {
        if (!list[i].isShow) {
          count++
        }
        if (list[i].isShow && !list[i].isSelected) {
          res = false
          break
        }
      }
      if (count === list.length) {
        return false
      }
      return res
    },
    //   映射区-申报值-全选/去掉全选
    handleCheckAllChange (val, index) {
      let item = this.mapForm.tableData[index]
      item.fieldOptions.map(it => {
        if (it.isShow) {
          it.isSelected = !!val
        }
      })
      item.checkAll = this.checkIsAllSelect(item.fieldOptions)
    },
    //   映射区-申报值-单个勾选
    handleCheckChange (val, index, ind) {
      let item = this.mapForm.tableData[index]
      item.checkAll = val ? this.checkIsAllSelect(item.fieldOptions) : false
    },
    // 映射区-申报值-搜索结果
    handleDeclareItemsFilterRes (index) {
      let res = true
      let list = this.mapForm.tableData[index].fieldOptions
      for (let i = 0; i < list.length; i++) {
        if (list[i].isShow) {
          res = false
          break
        }
      }
      return res
    },

    // 确定保存
    doSave () {
      this.showLoading()
      let that = this
      let msg = ''
      if (this.fieldForm.fields.length === 0) {
        msg = '请先添加字段集'
      } else if (this.mapForm.curEditIndex !== '') {
        msg = '请先保存映射区的操作'
      } else if (this.mapForm.tableData.length === 0) {
        msg = '请先添加映射区'
      }
      if (msg) {
        this.closeLoading()
        this.$message.warning(msg)
        return
      }
      let itemBases = []
      let declareItems = []
      let inputItems = []
      this.mapForm.tableData.map(item => {
        declareItems = []
        inputItems = []
        item.fieldOptions.map(it => {
          if (it.isSelected) {
            declareItems.push({
              fieldValue: it.fieldValue,
              strategy: it.strategy
            })
          }
        })
        item.inputItemList.map(it => {
          inputItems.push({
            fieldValue: it
          })
        })
        itemBases.push({
          declareItems: declareItems,
          fieldValue: item.inputItemList.join('\n'),
          inputItems: inputItems
        })
      })
      let setObj = {
        id: this.id,
        uuid: this.uuid,
        fields: this.fieldForm.fields,
        itemBases: itemBases
      }
      this.$http({
        url: `/policy/ai/fieldMapping/saveOrUpdate`,
        method: 'post',
        data: setObj
      }).then((data) => {
        that.closeLoading()
        if (data.data.code === 200) {
          that.$message.success('保存成功')
          setTimeout(function () {
            that.$router.replace('/basicConfig/aiFieldMapping')
          }, 1500)
        }
      }).catch(() => {
        that.closeLoading()
      })
    },
    showLoading (target) {
      this.loading = this.$loading({
        target: target || document.body,
        lock: true,
        text: '保存中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      })
    },
    closeLoading () {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    }

  }
}
</script>

<style lang="less" scoped>
.type-title {
  font-weight: bold;
  font-size: 16px;

  &:before {
    margin-right: 5px;
    content: '';
    display: inline-block;
    height: 22px;
    width: 6px;
    vertical-align: middle;
    margin-top: -2px;
    background: #3E82FF;
    border-radius: 2px;
  }

  .text {
    font-weight: normal;
    font-size: 14px;
    margin-left: 30px;
  }
}

.type-content {
  padding: 15px 10px;
}

.field-area {
  .tag {
    padding: 0 10px;
    height: 30px;
    line-height: 30px;
    border-radius: 40px;
    border: 1px solid #dbdbdb;
    position: relative;
    margin-right: 30px;
    cursor: pointer;
    margin-bottom: 15px;

    .ic-select {
      display: none;
    }
  }

  .tag.selected {
    border-color: @blueColor;
    background-color: #d9ecff;

    .ic-select {
      position: absolute;
      right: -6px;
      top: -5px;
      font-size: 18px;
      color: @blueColor;
      display: inline-block;
      z-index: 2;
    }
  }
}
.inputItemTag{
  position: relative;
  .ic-del{
    display: none;
    color: @redColor;
    font-size: 16px;
    position: absolute;
    top: -3px;
    right: -5px;
    cursor: pointer;
  }
  &:hover{
    .ic-del{
      display: block;
    }
  }
}

</style>
