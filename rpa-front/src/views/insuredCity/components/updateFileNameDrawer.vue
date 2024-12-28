<template>
  <div>
    <el-drawer
      title="维护附件名称"
      :visible.sync="setLocalVisible"
      :wrapperClosable="false"
      custom-class="spl-filter-drawer add-drawer"
      :show-close="true"
      :before-close="doClose"
    >
      <div class="drawer-body" style="margin: 0 20px;padding-top: 15px;">
        <el-form
          :rules="rules"
          label-width="80px"
          :model="setForm"
          ref="setForm"
          class="flex1"
        >
          <p><span class="fw-blod">新增附件</span><span
            class="text-red ml-10">同含义附件尽量不要重复添加，设置附件信息时在注解中说明即可</span></p>
          <div class="display-flex mt-20" style="align-items: end">
            <div class="flex1">
              <el-form-item label="附件名称" prop="dictName">
                <el-input placeholder="请输入附件名称" v-model.trim="setForm.dictName"/>
              </el-form-item>
              <el-form-item
                label="说明"
                prop="comment"
                style="margin-top: -10px;"
              >
                <el-input
                  placeholder="备注说明该附件通常的应用情况"
                  v-model.trim="setForm.comment"
                  class="w-p100"
                ></el-input>
              </el-form-item>
            </div>
            <el-button type="primary" class="s-btn ml-15" style="margin-bottom: 25px;" @click="handleValid">确定添加</el-button>
          </div>

          <div class="display-flex" style="align-items: center">
            <span class="fw-blod flex1">已有附件</span>
            <el-input v-model.trim="searchWord" @keyup.enter.native="doSearch" placeholder="输入关键字回车搜索" class="w-250">
              <i @click="doSearch" slot="suffix" class="el-input__icon el-icon-search f-cursor font-16 fw-blod" style="color: #dbdbdb;"></i>
            </el-input>
          </div>
          <table class="cust-table-border w-p100 mt-10 mb-20">
            <thead>
            <tr>
              <th>附件名称</th>
              <th>备注</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(item, index) in fileNameList" :key="index" v-show="!searchWord2 || item.dictName.indexOf(searchWord2)>-1">
              <td>{{item.dictName}}</td>
              <td>{{item.comment}}</td>
            </tr>
            </tbody>
          </table>
        </el-form>
      </div>
      <div class="drawer-footer-buts">
        <el-button class="btn--border-blue s-btn mr-0" @click="doClose">取消</el-button>
      </div>
    </el-drawer>
  </div>
</template>
<script>
export default {
  name: 'updateFileNameDrawer',
  components: {},
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    setLocalVisible: {
      get () {
        if (this.visible) {
          this.getFileNameList()
        }
        return this.visible
      },
      set (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  data () {
    return {
      setForm: {
        dictName: '', // 字段名称
        comment: ''
      },
      rules: {
        dictName: [{ required: true, message: '请输入附件名称', trigger: 'blur' },
          { validator: this.checkName, trigger: 'blur' }]
      },
      fileNameList: [],
      searchWord: '',
      searchWord2: ''
    }
  },
  created () {
  },
  mounted () {
  },
  watch: {},
  methods: {
    getFileNameList () {
      this.$http({
        url: '/policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: '10014'
        }
      }).then(({ data }) => {
        let res = data.data
        let flag1 = []
        let flag0 = []
        res.map(item => {
          if (item.flag === 1) {
            flag1.push(item)
          } else {
            flag0.push(item)
          }
        })
        flag1.sort((a, b) => {
          return a.dictDisplayOrder - b.dictDisplayOrder
        })
        flag0.sort((a, b) => {
          return a.dictDisplayOrder - b.dictDisplayOrder
        })
        this.fileNameList = [...flag1, ...flag0]
      })
    },
    // 验证，确定提交保存
    handleValid () {
      this.$refs.setForm.validate((valid) => {
        if (valid) {
          let setForm = this.setForm
          this.$http({
            url: '/policy/declareAddr/addFileTypeDict',
            method: 'post',
            data: {
              dictName: setForm.dictName,
              comment: setForm.comment
            }
          }).then(({ data }) => {
            this.$message.success('操作成功')
            this.doClose()
          })
        }
      })
    },
    doClose () {
      this.$emit('doClose')
      this.setLocalVisible = false
      this.setForm = {
        dictName: '', // 字段名称
        comment: ''
      }
      this.searchWord = ''
      this.searchWord2 = ''
      this.$nextTick(() => {
        if (this.$refs.setForm) {
          this.$refs.setForm.clearValidate()
        }
      })
    },
    checkName (rule, value, callback) {
      this.$http({
        url: '/policy/declareAddr/validateFileTypeDict',
        method: 'post',
        data: {
          dictName: value
        }
      }).then(({ data }) => {
        if (data.data) {
          callback(new Error(data.data))
        } else {
          callback()
        }
      }).catch(() => {
      })
    },
    doSearch () {
      this.searchWord2 = this.searchWord
    }
  }
}
</script>
<style lang="less" scoped>
/deep/.el-drawer__body {
  padding-bottom: 80px!important;
}
/deep/.add-drawer{
  width: 600px!important;
}
</style>
