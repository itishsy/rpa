<template>
  <div>
    <el-drawer :title="title" :visible.sync="localVisible" :wrapperClosable="false"
      custom-class="spl-filter-drawer add-drawer" :show-close="true" :before-close="doClose" size="20%">
      <div class="drawerForm" style="padding: 15px 20px 20px 20px">
        <el-form :model="form" ref="addTemplateForm" label-width="0" :disabled="isDetail">
          <el-form-item label="" prop="name"
            :rules="[{ required: true, message: '请输入模板名称', trigger: ['change', 'blur'] }]">
            <p class="item-lab clearfix required-pre">模板名称</p>
            <el-input v-model.trim="form.name" placeholder="请输入模板名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="" prop="tempType" :rules="[{ required: true, message: '请选择模板类型', trigger: ['change'] }]">
            <p class="item-lab required-pre">模板类型</p>
            <el-select placeholder="请选择" v-model="form.tempType" clearable filterable class="w-p100">
              <el-option v-for="item in handleTypeList" :key="item.dictCode" :label="item.dictName"
                :value="item.dictCode"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="" prop="content"
            :rules="[{ required: true, message: '请输入模板内容', trigger: ['change', 'blur'] }]">
            <p class="item-lab  clearfix required-pre">模板内容
              <el-tooltip placement="top" effect="light">
                <div slot="content">
                  <p>可在模板内容，配置以下通配符，以##分割</p>
                  <p>#公司名称#</p>
                  <p>#业务类型#</p>
                  <p>#申报账户#</p>
                  <p>#参保城市#</p>
                </div>
                <i class="el-icon-question" style="color:#3E82FF;cursor: pointer;font-size: 16px;margin-left:10px;"></i>
              </el-tooltip>
            </p>
            <el-input v-model.trim="form.content" type="textarea" :autosize="{ minRows: 4, maxRows: 46 }"
              placeholder="请输入模板内容" clearable></el-input>
          </el-form-item>
          <el-form-item label="" prop="comment">
            <p class="item-lab  clearfix">模板描述</p>
            <el-input v-model.trim="form.comment" type="textarea" :autosize="{ minRows: 4, maxRows: 46 }"
              placeholder="请输入模板描述" clearable></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div class="drawer-footer-buts" v-if="!isDetail">
        <el-button class="btn--border-blue s-btn mr-0" @click="doClose">取消</el-button>
        <el-button type="primary" class="s-btn ml-12" :loading="loading" @click="handleValid">确定添加</el-button>
      </div>
    </el-drawer>
  </div>
</template>
<script>
export default {
  name: 'TemplateDrawer',
  components: {
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    templateId: {
      type: [String, Number],
      default: ''    //空代表当前是新增
    },
    isDetail: {
      type: Boolean,
      default: false //默认是可编辑状态
    }
  },
  computed: {
    localVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      },
    },
    title() {
      return this.isDetail ? '查看短信模板' : this.templateId ? '编辑短信模板' : '新增短信模板';
    }
  },
  data() {
    return {
      loading: false,
      form: {
        name: undefined,      //模板名称
        tempType: undefined,   //模板类型
        comment: undefined,    //模板描述
        content: undefined     //模板内容
      },
      handleTypeList: [
        { dictCode: 1, dictName: '短信消息模板' },
        { dictCode: 2, dictName: '语音消息模板' },
        { dictCode: 3, dictName: '缴费核定通知模板' },
        { dictCode: 4, dictName: '缴费成功通知模板' }
      ]
    }
  },
  created() { },
  mounted() {

  },
  watch: {},
  methods: {
    doClose() {
      this.localVisible = false
    },
    handleNullFrom() {
      this.form = {
        name: undefined,      //模板名称
        tempType: undefined,   //模板类型
        comment: undefined,    //模板描述
        content: undefined     //模板内容
      }
      this.$nextTick(item=>{
        this.$refs.addTemplateForm.resetFields()
      })
    },
    //验证，确定提交保存
    handleValid() {
      this.loading = true
      this.$refs.addTemplateForm.validate((valid) => {
        if (valid) {
          //验证通过后 先校验模板名称是否唯一
          this.$http({
            url: '/policy/sys/robot/noticeTemplate/validateTempName',
            method: 'post',
            data: this.$qs.stringify({
              id: this.templateId || undefined,
              name: this.form.name
            })
          }).then(({ data }) => {
            console.log('校验模板名称', data)

            if (data.code === 200 && data.data.length === 0) {
              this.$http({
                url: '/policy/sys/robot/noticeTemplate/saveTemp',
                method: 'post',
                data: {
                  ...this.form,
                  id: this.templateId || null
                },
              })
                .then(({ data }) => {
                  this.localVisible = false
                  this.handleNullFrom()
                  this.$emit('getTable-data') //刷新列表
                })
                .catch(() => {
                  this.$message.error(data.message)
                })

            } else {
              this.$message.error(data.data)
            }
            this.loading = false
          })

        } else {
          this.loading = false
          return false
        }
      })
    },
  },
}
</script>
<style lang="less" scoped></style>
