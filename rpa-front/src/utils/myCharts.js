/**
 * @author  ZhenShijun
 * @dateTime 2021-05-18 09:30:10
 */

/**
 * 各种画echarts图表的方法都封装在这里
 * 注意：这里echarts没有采用按需引入的方式，只是为了方便学习
 */
import * as echarts from 'echarts'
const install = function (Vue) {
  Object.defineProperties(Vue.prototype, {
    $chart: {
      get () {
        return {
          // 根据配置参数绘制chart图
          draw: function (id, optionData) {
            let chart = echarts.init(document.getElementById(id))
            chart.clear()
            chart.setOption(optionData)
            return chart
          }
        }
      }
    }
  })
}
export default {
  install
}
