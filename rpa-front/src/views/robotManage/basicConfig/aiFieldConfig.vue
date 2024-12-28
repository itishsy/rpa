<template>
  <div class="spl-container">
    <breadcrumb :data="pathData">
      <el-button type="primary" slot="breadcrumb-btn" size="small" v-if="table.tableData.length===0" @click="handleAdd" class="mr-10">添加</el-button>
      <el-button type="primary" slot="breadcrumb-btn" size="small" @click="handleSave">保存</el-button>
    </breadcrumb>
    <el-form ref="tableForm" :model="table" label-width="0px" :rules="rules">
      <div class="table-box">
        <table class="cust-table-border w-p100">
          <thead>
          <tr>
            <th style="width: 60px">序号</th>
            <th><span class="required-pre">字段名</span></th>
            <th style="width: 300px"><span class="required-pre">字段性质</span></th>
            <th><span class="required-pre">客户名称</span></th>
            <th style="width: 120px"><span class="required-pre">操作</span></th>
          </tr>
          </thead>
          <tbody>
          <!-- isDelete: 0 未删除 1已删除-->
          <tr v-for="(item, index) in table.tableData" :key="index">
            <td>{{ index + 1 }}</td>
            <td>
              <div v-if="item.isNew">
                <el-form-item :prop="'tableData.' + index + '.name'" :rules="rules.name">
                  <el-input type="text" placeholder="请输入" v-model="item.name" clearable></el-input>
                </el-form-item>
              </div>
              <div v-else>
                {{ item.name }}
              </div>
            </td>
            <td>
              <el-radio-group v-model="item.columnProperty">
                <el-radio :label="1" :disabled="item.columnProperty===0">固定字段</el-radio>
                <el-radio :label="0" :disabled="item.columnProperty===1">动态字段</el-radio>
              </el-radio-group>
            </td>
            <td>
              <div v-if="item.isNew">
                <el-form-item :prop="'tableData.' + index + '.customerId'" :rules="rules.customerId">
                  <el-select v-model="item.customerId" class="w-p100" placeholder="请选择" size="small" filterable clearable @change="changeCustomer(index)">
                    <el-option
                      v-for="it in customerOption"
                      :key="it.id"
                      :label="it.customerName"
                      :value="it.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </div>
              <div v-else>
                {{ item.customerName }}
              </div>
            </td>
            <td>
              <span class="text-gray f-cursor" @click="handleDel(item, index)">删除</span>
              <span class="text-blue f-cursor ml-20" v-if="index===table.tableData.length-1" @click="handleAdd()">添加</span>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <div v-if="table.tableData.length===0" class="text-center text-gray pt-60">
        暂无数据
      </div>
    </el-form>
  </div>
</template>
<script>
export default {
  components: { },
  data () {
    return {
      pathData: [],
      customerOption: [],
      table: {
        tableData: [],
        delData: []
      },
      rules: {
        name: [
          { required: true, message: '请输入字段名', trigger: 'blur' },
          { required: false, trigger: 'blur', validator: this.checkRepeat }
        ],
        customerId: [
          { required: true, message: '请选择客户名称', trigger: 'change' }
        ]
      },
      loading: null
    }
  },
  computed: {
    tableHeight () {
      return this.$global.bodyHeight - 200 + 'px'
    }
  },
  watch: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
      this.pathData.push({ name: 'AI人员字段配置' })
    }
    this.getData()
    this.getCustomer()
  },
  methods: {
    // 获取数据
    getData () {
      this.showLoading()
      this.$http({
        url: '/policy/ai/queryAiFiled',
        method: 'post',
        data: {}
      }).then(({ data }) => {
        this.closeLoading()
        if (data && data.code == 200) {
          let tableData = []
          let delData = []
          data.data.map(item => {
            if (item.isDelete === 0) {
              tableData.push(item)
            } else {
              delData.push(item)
            }
          })
          this.table.tableData = tableData
        }
      }).catch(() => {
        this.closeLoading()
      })
    },

    getCustomer () {
      this.$http({
        url: '/robot/app/client/listCustomer',
        method: 'post'
      }).then((data) => {
        this.customerOption = data.data.data
      }).catch((err) => {
      })
    },

    changeCustomer (index) {
      let customerId = this.table.tableData[index].customerId
      let customerName = ''
      for (let i = 0; i < this.customerOption.length; i++) {
        if (this.customerOption[i].id === customerId) {
          customerName = this.customerOption[i].customerName
          break
        }
      }
      this.table.tableData[index].customerName = customerName
    },

    // 添加
    handleAdd () {
      this.table.tableData.push({
        customerId: '',
        customerName: '',
        name: '',
        columnProperty: 0, // 0动态字段 1固定字段
        isDelete: 0,
        isNew: true // 用于判断是不是本次新增，接口不需要该字段
      })
    },

    // 删除
    handleDel (item, index) {
      if (item.isNew) {
        this.table.tableData.splice(index, 1)
        this.$refs.tableForm.clearValidate()
      } else {
        this.$confirm('一旦删除，相关功能将无法引用此字段，是否继续？', '提示', {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          showClose: false,
          customClass: 'pal-confirm',
          type: 'warning'
        }).then(() => {
          item.isDelete = 1
          this.table.delData.push(item)
          this.table.tableData.splice(index, 1)
          this.$refs.tableForm.clearValidate()
        }).catch(() => {
        })
      }
    },

    // 保存
    handleSave () {
      this.$refs.tableForm.validate((vaild) => {
        if (vaild) {
          this.showLoading('正在保存...')
          const setData = this.table.tableData.concat(this.table.delData)
          this.$http({
            url: '/policy/ai/addAiFiled',
            method: 'post',
            data: setData
            // data: []
          }).then((data) => {
            this.closeLoading()
            this.$message.success('保存成功')
            this.getData()
          }).catch((err) => {
            this.closeLoading()
          })
        } else {
          let tableData = this.table.tableData
          let nameArr = []
          let customerArr = []
          let repeatIndex = ''
          let repeatArr = []
          let name = ''
          for (let i = 0; i < tableData.length; i++) {
            name = tableData[i].name
            if (name) {
              repeatIndex = nameArr.indexOf(name)
              if (repeatIndex > -1 && customerArr[repeatIndex] === tableData[i].customerId) {
                if (repeatArr.indexOf(name) === -1) {
                  repeatArr.push(name)
                }
              } else {
                nameArr.push(name)
                customerArr.push(tableData[i].customerId)
              }
            }
          }
          if (repeatArr.length > 0) {
            this.$message.warning('字段' + repeatArr.join('、') + '重复，请检查')
          }
        }
      })
    },
    checkRepeat (rule, value, callback) {
      let vaildRes = true
      let tableData = this.table.tableData
      let index = rule.field.split('.')[1]
      index = Number(index)
      let item = tableData[index]
      for (let i = 0; i < tableData.length; i++) {
        if (index !== i && item.customerId && tableData[i].customerId === item.customerId && tableData[i].name === value) {
          vaildRes = false
          break
        }
      }
      if (vaildRes) {
        return callback()
      } else {
        return callback(new Error('字段名重复，请检查'))
      }
    },
    showLoading (text) {
      this.loading = this.$loading({
        target: document.body,
        lock: true,
        text: text ? '加载中' : text,
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
.search-row {
  padding: 5px 10px;
}

.table-box {
  padding: 20px;
}
/deep/.el-form-item{
  margin-bottom: 0;
}
</style>
