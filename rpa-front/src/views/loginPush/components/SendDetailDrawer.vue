<template>
  <div>
    <el-drawer title="发送详情" :visible.sync="localVisible" :wrapperClosable="false"
      custom-class="spl-filter-drawer detail-drawer" :show-close="true" :before-close="doClose" size="30%">
      <div class="pt-20 pl-20 pr-20">
        <p class="fw-blod pb-10">详情</p>
        <el-row type="flex" style="flex-wrap:wrap;" v-if="currentRow">
          <el-col :span="8">
            <div class="mb-20">推送客户名称：{{ currentRow.custName || '-' }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">发送手机号码：{{ currentRow.recipientPhoneNumber }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">发送状态：{{ currentRow.sendStatus === 0 ? '待发送' : currentRow.sendStatus === 1 ? '已发送' :
              currentRow.sendStatus === 2 ? '发送失败' : '-' }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">验证结果：{{ currentRow.status === 0 ? '待验证' : currentRow.status === 1 ? '已验证' :
              currentRow.status === 2 ? '已过期' : '-' }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">是否语音电话通知：{{ currentRow.voiceReminder ? '是' : '否' }}</div>
          </el-col>
          <el-col :span="8">
            <div class="mb-20">验证方式：{{ currentRow.validateTypeName }}</div>
          </el-col>
        </el-row>

        <div v-if="currentRow.validateType === '10021001'">
          <p class="fw-blod pb-10 pt-10">发送过程</p>
          <el-row type="flex" style="flex-wrap:wrap;" v-for="item, index in currentRow.items" :key="item.id">
            <el-col :span="8">
              <div class="mb-20">{{ index === 0 ? '首次' : `第${index + 1}次` }}发送时间： {{ item.createTime }}</div>
            </el-col>
            <el-col :span="8">
              <div class="mb-20">接收验证码：{{ item.smsCode || '-' }} {{ item.dataType ? `(${item.dataType})` : '' }}</div>
            </el-col>
            <el-col :span="8">
              <div class="mb-20">验证状态： {{ item.status === 0 ? '待验证' : item.status === 1 ? '已验证' :
                item.status === 2 ? '已过期' : '-' }}</div>
            </el-col>
          </el-row>
        </div>

        <div v-else>
          <p class="fw-blod pb-10 pt-10">二维码信息</p>
          <el-row type="flex" style="flex-wrap:wrap;">
            <el-col :span="8" v-for="item,index in currentRow.items" :key="item.id">
              <div class="mb-20">
                <p class="mb-10">{{ index === 0 ? '首次' : `第${index + 1}次` }}发送时间： {{ item.createTime }}</p>
                <img :src="item.imgHttpUrl" alt="" width="150" height="150" />
                <p class="mt-10">验证状态：{{ item.status === 0 ? '待验证' : item.status === 1 ? '已验证' :
                  item.status === 2 ? '已过期' : '-' }}</p>
              </div>
            </el-col>
          </el-row>
        </div>

      </div>
    </el-drawer>
  </div>
</template>
<script>
export default {
  name: 'SendDetailDrawer',
  components: {
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    currentRow: {
      type: Object,
      default: () => {

      }
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
  },
  data() {
    return {
      loading: false,
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
  },
}
</script>
<style lang="less" scoped>
/deep/.detail-drawer {
  width: 60% !important;
  max-width: 1000px !important;

  .pal-tabs {
    padding-left: 0;
    height: 75px;
    line-height: 62px;
    position: relative;
    top: -10px;
  }

  .span-tabs {
    height: 71px;
    line-height: 80px;
  }
}
</style>
