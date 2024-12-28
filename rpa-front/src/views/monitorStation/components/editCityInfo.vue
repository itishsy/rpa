<template>
  <div>
    <el-dialog
      title="城市信息"
      :visible.sync="editForm.show"
      width="600px"
      class="cust-dialog"
      :before-close="cancelEditForm"
      :close-on-click-modal="false">
      <div>
        <el-form :model="editForm" ref="editForm" label-width="130px">
          <el-form-item prop="robot" label="城市：">
            <p style="line-height: 40px;">{{editForm.addrName}}</p>
          </el-form-item>
          <el-form-item prop="robot" label="应用：" style="margin-top: -10px;">
            <p style="line-height: 40px;">{{editForm.appName}}</p>
          </el-form-item>
          <div style="margin-top: -10px;">
            <el-form-item prop="onlineStatus" label="上线阶段：" :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
              <el-select size="medium" v-model="editForm.onlineStatus" placeholder="请选择" style="width:300px;">
                <el-option v-for="(item, index) in options.onlineStatus" :key="index" :label="item.dictName" :value="item.dictCode"></el-option>
              </el-select>
            </el-form-item>
          </div>
          <div>
            <div style="display: flex" v-for="(item, index) in editForm.websiteLinks" :key="index">
              <el-form-item :prop="'websiteLinks.'+index+'.val'" :label="index===0?'网站链接：':''" :rules="[{ required: true, message: '请输入', trigger: 'blur' }]">
                <el-input class="" size="medium" placeholder="请输入" style="width:300px;" v-model.trim="item.val"></el-input>
              </el-form-item>
              <i class="el-icon-remove text-red ml-10 font-24 mt-10 f-cursor" v-show="editForm.websiteLinks.length>1" @click="delWebsiteLinks(index)"></i>
              <i class="el-icon-circle-plus text-green ml-10 font-24 mt-10 f-cursor" v-show="index===(editForm.websiteLinks.length-1)" @click="addWebsiteLinks()"></i>
            </div>
          </div>
          <div>
            <el-form-item prop="latestDeclarationTime" label="最迟申报时间：" :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
              <el-select size="medium" v-model="editForm.latestDeclarationTime" placeholder="请选择" style="width:300px;" filterable>
                <el-option v-for="(item, index) in options.latestDeclarationTime" :key="index" :label="item.dictName"
                           :value="item.dictCode"></el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-form>
      </div>
      <div class="text-right pt-20 pb-10">
        <el-button @click="cancelEditForm" size="small" class="w-60">取 消</el-button>
        <el-button type="primary" @click="confirmEditForm" style="margin-left: 20px;" size="small">确定修改</el-button>
      </div>
    </el-dialog>

  </div>
</template>
<script>
export default {
  inject: ['reload'],
  components: {},
  props: ['editData'],
  data () {
    return {
      editForm: {
        show: false,
        id: '',
        addrName: '',
        appName: '',
        appCode: '',
        onlineStatus: '',
        websiteLinks: [],
        latestDeclarationTime: ''
      },
      options: {
        onlineStatus: [
          { dictCode: 0, dictName: '调研' },
          { dictCode: 1, dictName: '基础配置' },
          { dictCode: 2, dictName: '上线' },
          { dictCode: 3, dictName: '运维' }
        ],
        latestDeclarationTime: []
      },
      loading: null
    }
  },
  watch: {
    'editForm.show' (newValue, oldValue) {
      if (newValue) {
        //  打开
        var row = this.editData

        var websiteLinksArr = row.websiteLinks ? row.websiteLinks.split(',') : ['']
        var websiteLinks = []
        websiteLinksArr.map(item => {
          websiteLinks.push({ val: item })
        })
        row.websiteLinks = websiteLinks
        this.editForm = row
        if (this.options.latestDeclarationTime.length === 0) {
          this.initLatestDeclarationTime()
        }
      }
    }

  },
  filters: {

  },
  computed: {},
  created () {
  },
  mounted () {

  },
  methods: {
    initLatestDeclarationTime () {
      let arr = []
      for (let i = 1; i < 32; i++) {
        arr.push({ dictCode: String(i), dictName: '每月' + i + '号' })
      }
      this.options.latestDeclarationTime = arr
    },
    delWebsiteLinks (index) {
      this.editForm.websiteLinks.splice(index, 1)
      this.editForm.websiteLinks.map((item, index) => {
        this.$refs.editForm.validateField('websiteLinks.' + index + '.val')
      })
    },
    addWebsiteLinks () {
      this.editForm.websiteLinks.push({ val: '' })
    },
    // 编辑-取消
    cancelEditForm () {
      this.editForm = {
        show: false,
        id: '',
        addrName: '',
        appName: '',
        appCode: '',
        onlineStatus: '',
        websiteLinks: [],
        latestDeclarationTime: ''
      }
      this.$nextTick(() => {
        this.$refs.editForm.resetFields()
      })
    },
    // 编辑-确定
    confirmEditForm () {
      let that = this
      this.$refs.editForm.validate((valid) => {
        if (valid) {
          var editForm = that.editForm
          var websiteLinksArr = []
          var repeatIndex
          for (var i = 0; i < editForm.websiteLinks.length; i++) {
            repeatIndex = websiteLinksArr.indexOf(editForm.websiteLinks[i].val)
            if (repeatIndex > -1) {
              that.$message.warning('网站链接第' + (repeatIndex + 1) + '条和第' + (i + 1) + '条重复')
              return
            }
            websiteLinksArr.push(editForm.websiteLinks[i].val)
          }
          that.showLoading()
          that.$http({
            url: '/robot/monitor/city/edit',
            method: 'put',
            data: {
              'appId': editForm.id,
              'lastExecDay': editForm.latestDeclarationTime,
              'onlineStatus': editForm.onlineStatus,
              'websiteLinks': websiteLinksArr.join(',')
            }
          }).then(({ data }) => {
            that.cancelEditForm()
            that.closeLoading()
            that.$emit('success')
          }).catch(() => {
            that.closeLoading()
          })
        }
      })
    },
    showLoading (target) {
      this.loading = this.$loading({
        target: target || document.body,
        lock: true,
        text: '请稍等...',
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
/deep/.el-form-item{
  margin-bottom: 15px;
}
</style>
