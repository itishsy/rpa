// 入口文件main.js

import Vue from 'vue'
// import UmyUi from 'umy-ui'
import { UTable, UTableColumn } from 'umy-ui'
import App from './App.vue'
import moment from 'moment'
import ElementUI, { Message } from 'element-ui'
import router from './router'
import store from './store/index'

// 引入EventBus
// import EventBus from './plugins/eventbus'
// import './plugins/element.js'
import lodash from 'lodash'
// 导入全局样式表
// import './assets/fonts/iconfont.css'
// 导入全局样式表
// import './assets/css/global.less'

import breadcrumb from './components/common/breadcrumb'
import backtop from './components/common/backtop'

//
import global from './components/common/global/global'
import fileUtil from './components/common/global/fileUtil'
import qs from 'qs'
import httpRequest from './utils/httpRequest'
import message from './utils/message.js'// 引入
// import myCharts from './utils/myCharts.js'
import directives from './utils/directives'

// 引quill-editor编緝器
/* import VueQuillEditor from 'vue-quill-editor'
import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css' */

import '../element-variables.scss'
import './assets/css/common.less'// 引入
let Base64 = require('js-base64').Base64 // api: https://github.com/axios/axios
Vue.component('breadcrumb', breadcrumb)
Vue.component('backtop', backtop)
Vue.directive('positiveNumberAndZero', directives.positiveNumberAndZero) // 添加只能输入正整数和零的限制指令
Vue.directive('positiveNumber', directives.positiveNumber) // 添加只能输入正整数的限制指令
// Vue.directive('filterSpecialChar', directives.filterSpecialChar) // 添加自动去掉空格，和不可输入特殊符号
// Vue.directive('filterSymbols', directives.filterSymbols)
// Vue.use(VueQuillEditor)

httpRequest.defaults.baseURL = '/api'
Vue.prototype.$http = httpRequest
// 配置全局属性
Vue.prototype.$global = global
Vue.prototype.$print = fileUtil.print
Vue.prototype.$moment = moment
Vue.prototype.$lodash = lodash
// Vue.prototype.$eventbus = EventBus
Vue.prototype.bus = new Vue()

Vue.prototype.$qs = qs

Vue.use(ElementUI)
// Vue.use(UmyUi)
Vue.use(UTable)
Vue.use(UTableColumn)

Vue.use(message) // 通过全局方法 Vue.use() 使用插件
// Vue.use(myCharts)
Vue.config.productionTip = false

Vue.prototype.message = Message
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

// 得到文件流后下载文件
// 拿到数据以后 通过 new Blob对象 创建excel
Vue.prototype.$downloadFile = fileUtil.downloadFile
// new Vue({
//   el: '#app',
//   router: router,
//   render: h => h(app)
//   render:function (h) {return function h(app)}
// }) 一致
