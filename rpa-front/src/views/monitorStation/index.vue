<template>
    <div class="content pt-10" id="monitorT" style="height: calc(100vh - 100px);min-height: 800px;">
        <!-- 导航 -->
        <!-- <breadcrumb :data="pathData">
        </breadcrumb> -->
        <div class="container">
            <el-row type="flex" style="height: 100%;">
                <el-col :span="16" style="background-color: #f9fafd;">
                    <div class="left-layout">
                        <div id="main" style="width: 100%; min-height: 700px;flex:1" v-loading="loadingChart"></div>
                        <div class="bottom">
                            <el-row type="flex">
                                <el-col :span="8">
                                    <div class="bottom-item">
                                        <div>
                                            <span class="title">上线城市</span>
                                            <p class="desc"><span class="number">{{ headStatisticsInfo.onlineCount
                                            }}</span><span class="ml-5">个</span>
                                                <img v-if="headStatisticsInfo.onlineIndex > 0" class="ml-10"
                                                    :src="require('@/assets/images/icons/ic_rise.png')" width="12"
                                                    height="16" />
                                                <img v-else-if="headStatisticsInfo.onlineIndex < 0" class="ml-10"
                                                    :src="require('@/assets/images/icons/ic_decline.png')" width="12"
                                                    height="16" />
                                            </p>

                                        </div>
                                        <img src="@/assets/images/icons/ic_city_onlined.png" width="48" height="48" />
                                    </div>

                                </el-col>
                                <el-divider direction="vertical"></el-divider>
                                <el-col :span="8">
                                    <div class="bottom-item">
                                        <div>
                                            <span class="title">下线城市</span>
                                            <p class="desc"><span class="number">{{ headStatisticsInfo.offlineCount
                                            }}</span><span class="ml-5">个</span>
                                                <img v-if="headStatisticsInfo.offlineIndex > 0" class="ml-10"
                                                    :src="require('@/assets/images/icons/ic_rise.png')" width="12"
                                                    height="16" />
                                                <img v-else-if="headStatisticsInfo.offlineIndex < 0" class="ml-10"
                                                    :src="require('@/assets/images/icons/ic_decline.png')" width="12"
                                                    height="16" />
                                            </p>
                                        </div>
                                        <img src="@/assets/images/icons/ic_city_offlined.png" width="48" height="48" />
                                    </div>
                                </el-col>
                                <el-divider direction="vertical"></el-divider>
                                <el-col :span="8">
                                    <div class="bottom-item">
                                        <div>
                                            <span class="title">待上线城市</span>
                                            <p class="desc"><span class="number">{{ headStatisticsInfo.launchedCount
                                            }}</span><span class="ml-5">个</span>
                                                <img v-if="headStatisticsInfo.launchedIndex > 0" class="ml-10"
                                                    :src="require('@/assets/images/icons/ic_rise.png')" width="12"
                                                    height="16" />
                                                <img v-else-if="headStatisticsInfo.launchedIndex < 0" class="ml-10"
                                                    :src="require('@/assets/images/icons/ic_decline.png')" width="12"
                                                    height="16" />
                                            </p>
                                        </div>
                                        <img src="@/assets/images/icons/ic_city_ready.png" width="48" height="48" />
                                    </div>
                                </el-col>
                            </el-row>
                        </div>
                    </div>
                </el-col>
                <el-col :span="8">
                    <div class="right">
                      <el-row type="flex">
                        <el-col :span="12">
                          <StatisticsInfo title="盒子" class="f-cursor" :count="robotStatistics.machineTotal"
                                          :icon="require('@/assets/images/icons/ic_box.png')" :showStatus="false"
                                          :status="robotStatistics.boxIndex" showBottom lbTitle="执行中" rbTitle="设备异常"
                                          :lbCount="robotStatistics.execMachines" :rbCount="robotStatistics.abnormalMachines"
                                          @onClick="onClickMachineTotalBox" />
                        </el-col>
                        <el-col :span="12">
                          <StatisticsInfo title="申报账号" class="f-cursor" :count="robotStatistics.accountTotal"
                                          :showStatus="false" :status="info.accountIndex"
                                          :icon="require('@/assets/images/icons/ic_account.png')" showBottom lbTitle="运行中"
                                          rbTitle="已停用" :lbCount="robotStatistics.runningAccounts"
                                          :rbCount="robotStatistics.stopAccounts"
                                          @onClick="onClickAccountTotalBox" />
                        </el-col>
                      </el-row>
                      <el-row type="flex">
                        <el-col :span="12">
                          <StatisticsInfo title="服务员工" :count="declareStatistics.serviceEmpCount" unit="万" custom
                                          :icon="require('@/assets/images/icons/ic_staff.png')" showIconStatus showBottom
                                          lbTitle="当前在册" rbTitle="今日增加" :lbCount="declareStatistics.registerCount"
                                          :rbCount="declareStatistics.todayRegisterAdd">
                                    <span class="span-number">{{ (declareStatistics.serviceEmpCount / 10000).toFixed(2)
                                      }}</span>万
                          </StatisticsInfo>
                        </el-col>
                        <el-col :span="12">
                          <StatisticsInfo title="服务数据" :count="declareStatistics.serviceCount" unit="万"
                                          :showStatus="false" custom :status="declareStatistics.serviceDataIndex"
                                          :icon="require('@/assets/images/icons/ic_service_data.png')" showIconStatus showBottom
                                          lbTitle="申报成功" rbTitle="申报失败" :lbCount="declareStatistics.declareSuccess"
                                          :rbCount="declareStatistics.declareFail"
                                          :iconStatusText="`今日+${declareStatistics.todayDeclare}`">
                                    <span class="span-number">{{ (declareStatistics.serviceCount / 10000).toFixed(2)
                                      }}</span>万
                            <!-- <div v-if="info.serviceCount > 20000">
                                <countTo class="count-number" :endVal=" info.serviceCount/1000" :duration='1000'
                                    :easingFn="easingFn" /><span>k</span>
                            </div>
                            <div v-else>
                                <span class="span-number">{{ info.serviceCount/1000 }}</span>k
                            </div>
                           -->
                          </StatisticsInfo>
                        </el-col>
                      </el-row>
                      <el-row type="flex">
                        <el-col :span="12">
                          <StatisticsInfo title="总运行时间" :count="info.totalRunTime" unit="h"
                                          :icon="require('@/assets/images/icons/ic_run_hour.png')" />
                        </el-col>
                        <el-col :span="12">
                          <StatisticsInfo title="节省工时" :count="info.saveHour" unit="h"
                                          :icon="require('@/assets/images/icons/ic_workhour.png')" />
                        </el-col>
                      </el-row>
                        <el-row type="flex">
                            <!-- <el-col :span="12">
                                <StatisticsInfo title="开通城市" :count="info.rightPassCityCount" showStatus
                                    :status="info.cityIndex" :icon="require('@/assets/images/icons/ic_city_ready.png')"
                                    showBottom lbTitle="下线城市" rbTitle="上线城市" :lbCount="info.rightOfflineCityCount"
                                    :rbCount="info.rightLiveCityCount" />
                            </el-col> -->
                            <!-- <el-col :span="12">
                                <StatisticsInfo title="应用" :count="info.appCount" showStatus :status="info.appIndex"
                                    :icon="require('@/assets/images/icons/ic_application.png')" showBottom lbTitle="执行中"
                                    :lbCount="info.appExecutionCount" />
                            </el-col> -->
                            <el-col :span="12">
                                <StatisticsInfo title="今日任务" :count="taskStatistics.todayTaskCount"
                                    :icon="require('@/assets/images/icons/ic_mission.png')" :showStatus="false"
                                    :status="taskStatistics.boxIndex" showBottom lbTitle="执行中" rbTitle="执行次数"
                                    :lbCount="taskStatistics.todayTaskExecuteCount"
                                    :rbCount="taskStatistics.todayTaskExecuteNumber" />
                            </el-col>
                            <el-col :span="12">
                                <StatisticsInfo title="今日执行异常" class="f-cursor" :count="taskStatistics.todayExecuteError"
                                    :icon="require('@/assets/images/icons/ic_error.png')" :showStatus="false"
                                    :status="taskStatistics.boxIndex" showBottom lbTitle="预警次数"
                                    :lbCount="taskStatistics.warnCount"
                                    @onClick="() => $router.push('/controlAndReport/devOps')" />
                            </el-col>
                        </el-row>
                    </div>
                </el-col>
            </el-row>
        </div>
    </div>
</template>
<script>
import * as echarts from 'echarts'
import china from './data/china.json'
import StatisticsInfo from './components/statisticsInfo.vue'
import countTo from 'vue-count-to'
export default {
  name: '',
  components: { StatisticsInfo, countTo },
  props: [''],
  data () {
    return {
      loadingChart: true,
      pathData: [],
      info: {
        leftPassCityCount: 0, // 左菜单-当前已开通城市
        leftOfflineCityCount: 0, // 左菜单-下线城市
        leftLiveCityCount: 0, // 左菜单-待上线城市
        rightPassCityCount: 0, // 右菜单-开通城市
        rightOfflineCityCount: 0, // 右菜单-下线城市
        rightLiveCityCount: 0, // 右菜单-上线城市
        boxCount: 0, // 盒子总数
        boxExecutionCount: 0, // 盒子执行中
        declareRunCount: 0, // 盒子休眠中
        declareAccountCoun: 0, // 申报账号总数
        declareRunCount: 0, // 申报运行中
        declareNoRunCount: 0, // 申报未运行
        servicePerson: 0, // 服务员工总数
        appCount: 0, // 应用总数
        appExecutionCount: 0, // 应用执行中
        serviceCount: 0, // 服务数据
        serviceSuccessCount: 0, // 服务成功
        serviceFailCount: 0, // 服务失败
        totalRunTime: 0, // 总运行时间
        saveHour: 0, // 节省工时
        cityIndex: 0, // 城市指标-返回正数上升，负数下降，0等于
        boxIndex: 0, // 盒子指标-返回正数上升，负数下降，0等于
        accountIndex: 0, // 申报账号指标-返回正数上升，负数下降，0等于
        serviceDataIndex: 0 // 应用指标-返回正数上升，负数下降，0等于
      },
      robotStatistics: {}, // 运营后台-盒子和申报账户统计信息
      declareStatistics: {
        todayRegisterAdd: 0,
        serviceCount: 0,
        todayDeclare: 0,
        serviceEmpCount: 0
      }, // 运营后台-服务员工和服务数据统计信息
      headStatisticsInfo: {}, // 城市相关统计
      taskStatistics: {}, // 任务统计
      chart: null // 地图
    }
  },
  computed: {

  },
  watch: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.requestData()
    this.getRobotStatistics()
    this.getDeclareStatistics()
    this.getStatisticsInfo()
    this.getTaskCount()
  },
  beforeMount () { },
  mounted () {
    document.getElementsByClassName('home-container')[0].style.paddingBottom = '0'
    // 获取已开通城市及其经纬度
    this.getCityData()

    let self = this
    const viewElem = document.body
    const resizeObserver = new ResizeObserver(() => {
      if (self.chart) {
        self.chart.resize()
      }
    })
    resizeObserver.observe(viewElem)
  },
  methods: {
    // 模拟测试数据
    dealWithData () {
      var geoCoordMap = {
        海门: [121.15, 31.89],
        鄂尔多斯: [109.781327, 39.608266],
        招远: [120.38, 37.35],
        舟山: [122.207216, 29.985295],
        齐齐哈尔: [123.97, 47.33],
        盐城: [120.13, 33.38],
        赤峰: [118.87, 42.28],
        青岛: [120.33, 36.07],
        乳山: [121.52, 36.89],
        金昌: [102.188043, 38.520089],
        泉州: [118.58, 24.93],
        莱西: [120.53, 36.86],
        日照: [119.46, 35.42],
        胶南: [119.97, 35.88],
        南通: [121.05, 32.08],
        拉萨: [91.11, 29.97],
        云浮: [112.02, 22.93],
        梅州: [116.1, 24.55],
        文登: [122.05, 37.2],
        上海: [121.48, 31.22],
        攀枝花: [101.718637, 26.582347],
        威海: [122.1, 37.5],
        承德: [117.93, 40.97],
        厦门: [118.1, 24.46],
        汕尾: [115.375279, 22.786211],
        潮州: [116.63, 23.68],
        丹东: [124.37, 40.13],
        太仓: [121.1, 31.45],
        曲靖: [103.79, 25.51],
        烟台: [121.39, 37.52],
        福州: [119.3, 26.08],
        瓦房店: [121.979603, 39.627114],
        即墨: [120.45, 36.38],
        抚顺: [123.97, 41.97],
        玉溪: [102.52, 24.35],
        张家口: [114.87, 40.82],
        阳泉: [113.57, 37.85],
        莱州: [119.942327, 37.177017],
        湖州: [120.1, 30.86],
        汕头: [116.69, 23.39],
        昆山: [120.95, 31.39],
        宁波: [121.56, 29.86],
        湛江: [110.359377, 21.270708],
        揭阳: [116.35, 23.55],
        荣成: [122.41, 37.16],
        连云港: [119.16, 34.59],
        葫芦岛: [120.836932, 40.711052],
        常熟: [120.74, 31.64],
        东莞: [113.75, 23.04],
        河源: [114.68, 23.73],
        淮安: [119.15, 33.5],
        泰州: [119.9, 32.49],
        南宁: [108.33, 22.84],
        营口: [122.18, 40.65],
        惠州: [114.4, 23.09],
        江阴: [120.26, 31.91],
        蓬莱: [120.75, 37.8],
        韶关: [113.62, 24.84],
        嘉峪关: [98.289152, 39.77313],
        广州: [113.23, 23.16],
        延安: [109.47, 36.6],
        太原: [112.53, 37.87],
        清远: [113.01, 23.7],
        中山: [113.38, 22.52],
        昆明: [102.73, 25.04],
        寿光: [118.73, 36.86],
        盘锦: [122.070714, 41.119997],
        长治: [113.08, 36.18],
        深圳: [114.07, 22.62],
        珠海: [113.52, 22.3],
        宿迁: [118.3, 33.96],
        咸阳: [108.72, 34.36],
        铜川: [109.11, 35.09],
        平度: [119.97, 36.77],
        佛山: [113.11, 23.05],
        海口: [110.35, 20.02],
        江门: [113.06, 22.61],
        章丘: [117.53, 36.72],
        肇庆: [112.44, 23.05],
        大连: [121.62, 38.92],
        临汾: [111.5, 36.08],
        吴江: [120.63, 31.16],
        石嘴山: [106.39, 39.04],
        沈阳: [123.38, 41.8],
        苏州: [120.62, 31.32],
        茂名: [110.88, 21.68],
        嘉兴: [120.76, 30.77],
        长春: [125.35, 43.88],
        胶州: [120.03336, 36.264622],
        银川: [106.27, 38.47],
        张家港: [120.555821, 31.875428],
        三门峡: [111.19, 34.76],
        锦州: [121.15, 41.13],
        南昌: [115.89, 28.68],
        柳州: [109.4, 24.33],
        三亚: [109.511909, 18.252847],
        自贡: [104.778442, 29.33903],
        吉林: [126.57, 43.87],
        阳江: [111.95, 21.85],
        泸州: [105.39, 28.91],
        西宁: [101.74, 36.56],
        宜宾: [104.56, 29.77],
        呼和浩特: [111.65, 40.82],
        成都: [104.06, 30.67],
        大同: [113.3, 40.12],
        镇江: [119.44, 32.2],
        桂林: [110.28, 25.29],
        张家界: [110.479191, 29.117096],
        宜兴: [119.82, 31.36],
        北海: [109.12, 21.49],
        西安: [108.95, 34.27],
        金坛: [119.56, 31.74],
        东营: [118.49, 37.46],
        牡丹江: [129.58, 44.6],
        遵义: [106.9, 27.7],
        绍兴: [120.58, 30.01],
        扬州: [119.42, 32.39],
        常州: [119.95, 31.79],
        潍坊: [119.1, 36.62],
        重庆: [106.54, 29.59],
        台州: [121.420757, 28.656386],
        南京: [118.78, 32.04],
        滨州: [118.03, 37.36],
        贵阳: [106.71, 26.57],
        无锡: [120.29, 31.59],
        本溪: [123.73, 41.3],
        克拉玛依: [84.77, 45.59],
        渭南: [109.5, 34.52],
        马鞍山: [118.48, 31.56],
        宝鸡: [107.15, 34.38],
        焦作: [113.21, 35.24],
        句容: [119.16, 31.95],
        北京: [116.46, 39.92],
        徐州: [117.2, 34.26],
        衡水: [115.72, 37.72],
        包头: [110, 40.58],
        绵阳: [104.73, 31.48],
        乌鲁木齐: [87.68, 43.77],
        枣庄: [117.57, 34.86],
        杭州: [120.19, 30.26],
        淄博: [118.05, 36.78],
        鞍山: [122.85, 41.12],
        溧阳: [119.48, 31.43],
        库尔勒: [86.06, 41.68],
        安阳: [114.35, 36.1],
        开封: [114.35, 34.79],
        济南: [117, 36.65],
        德阳: [104.37, 31.13],
        温州: [120.65, 28.01],
        九江: [115.97, 29.71],
        邯郸: [114.47, 36.6],
        临安: [119.72, 30.23],
        兰州: [103.73, 36.03],
        沧州: [116.83, 38.33],
        临沂: [118.35, 35.05],
        南充: [106.110698, 30.837793],
        天津: [117.2, 39.13],
        富阳: [119.95, 30.07],
        泰安: [117.13, 36.18],
        诸暨: [120.23, 29.71],
        郑州: [113.65, 34.76],
        哈尔滨: [126.63, 45.75],
        聊城: [115.97, 36.45],
        芜湖: [118.38, 31.33],
        唐山: [118.02, 39.63],
        平顶山: [113.29, 33.75],
        邢台: [114.48, 37.05],
        德州: [116.29, 37.45],
        济宁: [116.59, 35.38],
        荆州: [112.239741, 30.335165],
        宜昌: [111.3, 30.7],
        义乌: [120.06, 29.32],
        丽水: [119.92, 28.45],
        洛阳: [112.44, 34.7],
        秦皇岛: [119.57, 39.95],
        株洲: [113.16, 27.83],
        石家庄: [114.48, 38.03],
        莱芜: [117.67, 36.19],
        常德: [111.69, 29.05],
        保定: [115.48, 38.85],
        湘潭: [112.91, 27.87],
        金华: [119.64, 29.12],
        岳阳: [113.09, 29.37],
        长沙: [113, 28.21],
        衢州: [118.88, 28.97],
        廊坊: [116.7, 39.53],
        菏泽: [115.480656, 35.23375],
        合肥: [117.27, 31.86],
        武汉: [114.31, 30.52],
        大庆: [125.03, 46.58]
      }
      var data = []
      for (var key in geoCoordMap) {
        data.push({ name: key, value: geoCoordMap[key] })
      }
      return data
    },
    // 获取城市相关统计
    async getStatisticsInfo () {
      const { data: { code, data: result } } = await this.$http({
        url: '/robot/app/getAppCount',
        method: 'post'
      })
      if (code == 200) {
        this.headStatisticsInfo = result
      }
      console.log('头部统计-------', code, result)
    },
    // 运营后台-盒子和申报账户统计信息
    async getRobotStatistics () {
      const { data: { code, data: result } } = await this.$http({
        url: 'robot/client/robotStatistics',
        method: 'post'
      })

      if (code == 200) {
        this.robotStatistics = result
      }
    },
    // 运营后台-服务员工和服务数据统计信息
    async getDeclareStatistics () {
      const { data: { code, data: result } } = await this.$http({
        url: 'agent/declareChange/declareStatistics',
        method: 'post'
      })

      if (code == 200) {
        this.declareStatistics = result
      }
    },
    // 监控台-获取任务统计
    async getTaskCount () {
      const { data: { code, data: result } } = await this.$http({
        url: 'robot/monitor/station/getTaskCount',
        method: 'post'
      })

      if (code == 200) {
        this.taskStatistics = result
      }
    },
    // 请求数据面板
    async requestData () {
      const { data: { code, data: result } } = await this.$http({
        url: 'robot/monitor/station/count',
        method: 'post'
      })

      if (code == 200) {
        this.info = result
      }
    },
    // 请求获取已开通城市数据
    async getCityData () {
      const { data: { code, data: result } } = await this.$http({
        url: 'robot/monitor/station/getCity',
        method: 'post'
      })

      if (code == 200) {
        console.log('看看请求的数据', result)
        this.renderChart(result)
      }
    },
    // 格式化城市数据
    formatCityData (addrList) {
      let result = []
      result = addrList.map(item => {
        return {
          name: item.cityName,
          value: [item.lng, item.lat]
        }
      })
      console.log('看看规整后的数据', result)
      return result
    },
    // 渲染地图
    renderChart (result) {
      var myChart = echarts.init(document.getElementById('main'))
      echarts.registerMap('china', china) // 注册地图
      // var dataValue = this.dealWithData();
      var data1 = this.formatCityData(result.addrOnlineList)
      var data2 = this.formatCityData(result.addrLaunchedList)
      var data3 = this.formatCityData(result.addrOfflineList)
      const cityData = [
        { name: '北京', value: [116.46, 39.92] },
        { name: '天津', value: [117.2, 39.13] },
        { name: '上海', value: [121.48, 31.22] },
        { name: '重庆', value: [106.54, 29.59] },
        { name: '石家庄', value: [114.48, 38.03] },
        { name: '太原', value: [112.53, 37.87] },
        { name: '呼和浩特', value: [111.65, 40.82] },
        { name: '沈阳', value: [123.38, 41.8] },
        { name: '长春', value: [125.35, 43.88] },
        { name: '哈尔滨', value: [126.63, 45.75] },
        { name: '南京', value: [118.78, 32.04] },
        { name: '杭州', value: [120.19, 30.26] },
        { name: '合肥', value: [117.27, 31.86] },
        { name: '福州', value: [119.3, 26.08] },
        { name: '南昌', value: [115.89, 28.68] },
        { name: '济南', value: [117, 36.65] },
        { name: '郑州', value: [113.65, 34.76] },
        { name: '武汉', value: [114.31, 30.52] },
        { name: '长沙', value: [113, 28.21] },
        { name: '广州', value: [113.23, 23.16] },
        { name: '南宁', value: [108.33, 22.84] },
        { name: '海口', value: [110.35, 20.02] },
        { name: '成都', value: [104.06, 30.67] },
        { name: '贵阳', value: [106.71, 26.57] },
        { name: '昆明', value: [102.73, 25.04] },
        { name: '拉萨', value: [91.11, 29.97] },
        { name: '西安', value: [108.95, 34.27] },
        { name: '兰州', value: [103.73, 36.03] },
        { name: '西宁', value: [101.74, 36.56] },
        { name: '银川', value: [106.27, 38.47] },
        { name: '乌鲁木齐', value: [87.68, 43.77] },
        { name: '香港', value: [114.17, 22.28] },
        { name: '澳门', value: [113.54, 22.19] },
        { name: '台北', value: [121.5, 25.03] },
        { name: '澎湖', value: [119.58, 23.58] }
      ]
      let option = {
        tooltip: {
          show: false
        },
        geo: {
          map: 'china',
          zoom: 1.75,
          silent: true,
          roam: true,
          center: [105, 36], // 调整地图位置
          label: {
            normal: {
              show: false, // 关闭省份名展示
              fontSize: '10',
              color: 'rgba(0,0,0,0.7)'
            },
            emphasis: {
              show: false
            }
          },
          scaleLimit: {
            min: 1.5,
            max: 8
          },
          itemStyle: {
            normal: {
              areaColor: '#e8ecf7',
              borderColor: '#637c8c',
              borderWidth: 1, // 设置外层边框
              shadowBlur: 10,
              shadowOffsetY: 10,
              shadowOffsetX: 0,
              shadowColor: '#d2d5e2'
            },
            emphasis: {
              areaColor: '#3e82ff',
              shadowOffsetX: 0,
              shadowOffsetY: 0,
              shadowBlur: 3,
              borderWidth: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        },
        series: [
          {
            type: 'scatter', // 使用散点图系列
            coordinateSystem: 'geo', // 坐标系使用地理坐标系
            data: cityData, // 省会城市的坐标数据
            symbolSize: 5, // 标记点的大小
            label: {
              show: true,
              formatter: '{b}', // 标签显示省会城市名称
              fontSize: 12,
              offset: [0, -15] // 标签偏移量，使其显示在标记点上方
            },
            itemStyle: {
              color: '#595959' // 标记点的颜色
            }
          },
          {
            name: '已开通城市',
            type: 'effectScatter',
            coordinateSystem: 'geo',
            data: data1,
            symbolSize: 15,
            tooltip: {
              show: true,
              formatter: '{a} : {b}',
              position: 'right'
            },
            encode: {
              value: 2
            },
            showEffectOn: 'render',
            rippleEffect: {
              brushType: 'stroke',
              color: '#3e82ff',
              period: 9,
              scale: 5
            },
            hoverAnimation: true,
            label: {
              formatter: '{b}',
              position: 'right',
              show: true
            },
            itemStyle: {
              color: '#3e82ff',
              shadowBlur: 2,
              shadowColor: '#333'
            },
            zlevel: 1
          },
          {
            name: '待上线',
            type: 'effectScatter',
            coordinateSystem: 'geo',
            data: data2,
            symbolSize: 15,
            tooltip: {
              show: true,
              formatter: '{a} : {b}',
              position: 'right'
            },
            encode: {
              value: 2
            },
            showEffectOn: 'render',
            rippleEffect: {
              brushType: 'stroke',
              color: '#2dce67',
              period: 9,
              scale: 5
            },
            hoverAnimation: true,
            label: {
              formatter: '{b}',
              position: 'right',
              show: true
            },
            itemStyle: {
              color: '#2dce67',
              shadowBlur: 2,
              shadowColor: '#333'
            },
            zlevel: 1
          },
          {
            name: '已下线',
            type: 'effectScatter',
            coordinateSystem: 'geo',
            data: data3,
            symbolSize: 15,
            tooltip: {
              show: true,
              formatter: '{a} : {b}',
              position: 'right'
            },
            encode: {
              value: 2
            },
            showEffectOn: 'render',
            rippleEffect: {
              brushType: 'stroke',
              color: '#ff6238',
              period: 9,
              scale: 5
            },
            hoverAnimation: true,
            label: {
              formatter: '{b}',
              position: 'right',
              show: true
            },
            itemStyle: {
              color: '#ff6238',
              shadowBlur: 2,
              shadowColor: '#333'
            },
            zlevel: 1
          }
        ]
      }
      myChart.setOption(option)
      this.chart = myChart
      this.loadingChart = false
    },
    // 点击右侧盒子区域-盒子
    onClickMachineTotalBox (type) {
      if (type === 'lbTitle') {
        // 执行中：传参设备状态为【正在运行-2】跳转，自动查数据
        this.$router.push('/deviceManagement/index?status=2')
      } else if (type === 'rbTitle') {
        // 设备异常：传参设备状态为【已关闭-3、升级失败-7、程序异常-4、客户端异常-5、离线-8】跳转，自动查数据
        this.$router.push('/deviceManagement/index?status=3,7,4,5,8')
      } else {
        // 总数：不传参跳转，自动查数据
        this.$router.push('/deviceManagement/index')
      }
    },
    // 点击右侧盒子区域-申报账户
    onClickAccountTotalBox (type) {
      if (type === 'lbTitle') {
        // 运行中：传参账户状态为【正在执行】跳转，自动查数据
        this.$router.push('/customerAppList/customerIndex?status=正在执行')
      } else if (type === 'rbTitle') {
        // 已停用：传参账户状态为【已停用】跳转，自动查数据
        this.$router.push('/customerAppList/customerIndex?status=已停用')
      } else {
        // 总数：不传参跳转，自动查数据
        this.$router.push('/customerAppList/customerIndex')
      }
    }
  }
}
</script>
<style lang='less' scoped>
page {
    //overflow: hidden;
}

.container {
    height: 100%;

    .bottom {
        width: 99%;
        height: 120px;
        background-color: #f2f4fa;
        padding: 5px;
        border-radius: 6px;
        margin-right: 10px;

        &-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            // height: 130px;
            padding: 20px 30px;

            .title {
                width: 111px;
                height: 15px;
                font-size: 16px;
                font-family: Alibaba PuHuiTi;
                font-weight: 400;
                color: #303133;
                line-height: 32px;
            }

            .desc {
                font-size: 16px;

                .number {
                    font-size: 32px;
                    font-family: Alibaba PuHuiTi;
                    font-weight: bold;
                    color: #303133;
                    line-height: 32px;
                }
            }
        }

        /deep/ .el-divider--vertical {
            height: 70px;
            margin: 10px 0;
        }
    }

    .right {
        width: 100%;
        height: 100%;
      box-sizing: border-box;
      .el-row {
        height: 25%;
        padding: 10px 0;
        box-sizing: border-box;
        &:nth-of-type(1){
          padding-top: 0;
        }
      }
      .el-col-12 {
        overflow: hidden;
        margin: 0 10px;
        height: 100%;
      }
    }
}

.span-number {
    font-size: 32px;
    font-family: Alibaba PuHuiTi;
    font-weight: bold;
    color: #303133;
    line-height: 32px;
    padding-right: 10px;
}

.left-layout {
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    .bottom-item{
      padding-top: 10px;
    }
}
</style>
