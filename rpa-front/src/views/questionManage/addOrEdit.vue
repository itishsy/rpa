<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20 pt-20">
      <el-form :inline="true" :model="setForm" ref="setForm" label-width="0">
        <div style="padding: 10px;">
          <p class="item-label required-pre mb-5">问题</p>
          <el-form-item label="" prop="title" :rules="[{ required: true, message: '请输入问题', trigger: 'blur' }]">
            <el-input style="width: 600px;" v-model.trim="setForm.title" placeholder="请输入" clearable></el-input>
          </el-form-item>
          <p class="item-label required-pre mb-5">答案</p>
          <el-form-item label="" prop="content" :rules="[{ required: true, message: '请输入答案', trigger: 'blur' }]">
            <div class="pb-5">
              <custEditor ref="custEditor" height="450px" width="100%" v-model="setForm.content"
                          :showPlaceholder="true" @onEditorBlur="onEditorBlur"></custEditor>
            </div>
          </el-form-item>
        </div>
      </el-form>

      <div class="text-center pt-30 pb-30">
        <el-button class="btn--border-blue s-btn" @click="goBack">取消</el-button>
        <el-button type="primary" class="s-btn" style="margin-left: 30px;" :loading="setForm.btnLoading" @click="handleValid">保存</el-button>
      </div>
    </div>

  </div>
</template>
<script>
  import custEditor from '../../components/common/cust-editor'
  export default {
    components: { custEditor },
    data () {
      return {
        pathData: [],
        id: '',
        isEdit: false,
        setForm: {
          btnLoading: false,
          showRequiredMsg: false,
          title: '',
          content: ''
        }
      }
    },
    computed: {},
    watch: {},
    created () {
      var id = this.$route.query.id;
      this.id = id
      this.isEdit = id?true:false
      if(id){
        this.getData()
      }

      let pathData = []
      let tabs = this.$store.state.tabs
      if (tabs) {
        pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
      }
      pathData.push({ name: '常见问题管理', path: '/questionManage/index' })
      pathData.push({name: this.isEdit?'编辑':'添加'})
      this.pathData = pathData
    },
    mounted () {
    },
    methods: {
      getData(){
        let that = this
        let setForm = that.setForm
        this.$http({
          url: '/policy/viewOneProblem?id=' + this.id,
          method: 'post',
          data: {}
        }).then(({data}) => {
          let res = data.data
          setForm.title = res.question
          setForm.content = res.answer
        }).catch((data) => {
        })
      },

      handleValid: function () {
        let that = this
        that.setForm.btnLoading = true
        that.$refs.setForm.validate((valid) => {
          if (valid) {
            that.doSave()
          } else {
            that.setForm.btnLoading = false
          }
        })
      },
      doSave: function () {
        let that = this
        let setForm = that.setForm
        let url = ''
        var setObj = {
          question: setForm.title,
          answer: setForm.content
        }
        if(this.isEdit){
          url = '/policy/editAnswer'
          setObj.id = this.id
        }else{
          url = '/policy/addListProblem'
        }
        this.$http({
          url: url,
          method: 'post',
          data: this.isEdit?setObj:[setObj]
        }).then(({data}) => {
          that.$message.success("操作成功")
          that.goBack()
        }).catch((data) => {
          that.setForm.btnLoading = false
        })
      },
      onEditorBlur () {
        console.log(1)
        this.$refs.setForm.validateField('content')
      },
      // 返回
      goBack(id){
        this.$router.push('/questionManage/index')
      }
    }
  }
</script>
<style lang="less" scoped>

</style>
