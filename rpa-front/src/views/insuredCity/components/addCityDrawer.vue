<template>
  <div>
    <el-drawer
      title="新增参保城市"
      :visible.sync="localVisible"
      :wrapperClosable="false"
      custom-class="spl-filter-drawer add-drawer"
      :show-close="true"
      :before-close="doClose"
      size="20%"
    >
      <div>
        <div class="drawerForm" style="padding: 15px 20px 20px 20px">
          <el-form
            :inline="true"
            :model="addCityForm"
            ref="addCityForm"
            label-width="0"
          >
            <div style="padding: 10px">
              <p class="item-label required-pre">参保城市</p>
              <el-form-item label="" prop="addrName">
                <addrSelector
                  v-model="addCityForm.addrName"
                  :addrArr="allAddr"
                  width="100%"
                  placeholder="请输入"
                  @changeAddrSelect="changeAddrSelect"
                  :disabled="addCityForm.isDisabled"
                  :rules="[
                    {
                      required: true,
                      message: '请输入参保城市',
                      trigger: 'blur',
                    },
                  ]"
                ></addrSelector>
              </el-form-item>
            </div>
          </el-form>
        </div>
        <div class="drawer-footer-buts" v-if="!addCityForm.isDisabled">
          <el-button class="btn--border-blue s-btn mr-0" @click="doClose"
            >取消</el-button
          >
          <el-button
            type="primary"
            class="s-btn ml-12"
            :loading="addCityForm.btnLoading"
            @click="handleValid"
            >确认</el-button
          >
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import addrSelector from '../../../components/common/addrSelector.vue'
export default {
  name: 'addCityDrawer',
  components: {
    addrSelector,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
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
  },
  data() {
    return {
      addCityForm: {
        addrName: undefined,
        addrId: undefined,
      },
      allAddr: [],
    }
  },
  created() {},
  mounted() {
    this.getAddr()
  },
  watch: {},
  methods: {
    // 获取参保地
    getAddr(type) {
      let that = this
      this.$http({
        url: 'policy/policyAddr/addrList',
        method: 'post',
      }).then(({ data }) => {
        this.allAddr = data.data
      })
    },
    changeAddrSelect(val) {
      this.addCityForm = {
        addrName: val.name,
        addrId: val.id,
      }
    },
    doClose() {
      this.localVisible = false
    },
    handleNullFrom() {
      this.addCityForm = {
        addrName: undefined,
        addrId: undefined,
      }
      this.$refs.addCityForm.resetFields()
    },
    //验证，确定提交保存
    handleValid() {
      this.$refs.addCityForm.validate((valid) => {
        if (valid) {
          this.$http({
            url: 'policy/declareAddr/add',
            method: 'post',
            data: this.addCityForm,
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
          return false
        }
      })
    },
  },
}
</script>
<style lang="less" scoped>
</style>
