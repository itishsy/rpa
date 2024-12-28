<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div style="padding: 20px 20px 0px 20px">
      <palTab :tabs="tabs" v-model="tabActive" :type="2"></palTab>

      <div>
        <!--        今日执行-->
        <div v-show="tabActive===0">
          <ExecuteToday></ExecuteToday>
        </div>
        <!--        客户统计-->
        <div v-show="tabActive===1">
          <CustomerStatistics></CustomerStatistics>
        </div>
        <!--        负责城市-->
        <div v-show="tabActive===2">
          <ResponsibleCity></ResponsibleCity>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import palTab from '../../components/common/pal-tab'
import ExecuteToday from './component/ExecuteToday.vue'
import CustomerStatistics from './component/CustomerStatistics.vue'
import ResponsibleCity from './component/ResponsibleCity.vue'

export default {
  name: '',
  components: { palTab, ExecuteToday, CustomerStatistics, ResponsibleCity },
  props: [''],
  data () {
    return {
      pathData: [],
      tabs: ['今日执行', '客户统计', '负责城市'],
      tabActive: 0
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 335 + 'px'
    }
  },
  watch: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    if (this.$route.query.tab === 'ResponsibleCity') {
      this.tabActive = 2
    }
  },
  beforeMount () {
  },
  mounted () {
  },
  methods: {}
}
</script>
<style lang="less" scoped>
</style>
