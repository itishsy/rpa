// import Vue from 'vue'
import axios from 'axios'
import router from '../router'
import store from '../store/index'
import merge from 'lodash/merge'
import { clearLoginInfo } from '../utils'
import { MessageBox } from 'element-ui'
import global from '../components/common/global/global'

var httpTimeId
var isShowErrorDialog = false
var isShowLoginLose = false
function initMessageBox (msg, title, type) {
  if (!isShowErrorDialog) {
    let initType = type || 'error'
    MessageBox.confirm('<span style="white-space: pre-line">' + msg + '</span>', title || '提示', {
      customClass: 'spl-confirm',
      showConfirmButton: false,
      showCancelButton: false,
      dangerouslyUseHTMLString: true,
      type: initType,
      closeOnClickModal: false
    }).catch(() => {
      window.clearTimeout(httpTimeId)
    })
    httpTimeId = setTimeout(function () {
      MessageBox.close()
    }, 5000)
  } else {
    setTimeout(function () {
      initMessageBox(msg, title, type)
    }, 3000)
  }
}

function showErrorDialog () {
  window.clearTimeout(httpTimeId)
  if (!isShowErrorDialog) {
    isShowErrorDialog = true
    MessageBox.confirm('<i class="sysError"></i>', '', {
      customClass: 'spl-confirm sysErrorDialog',
      center: true,
      showConfirmButton: true,
      confirmButtonText: '返回页面',
      confirmButtonClass: 'confirmBtn',
      showCancelButton: false,
      dangerouslyUseHTMLString: true,
      closeOnClickModal: false,
      modal: false
    }).then(() => {
      isShowErrorDialog = false
    }).catch(() => {})
  }
}

const http = axios.create({
  timeout: 1000 * 60 * 30,
  withCredentials: true
})

/**
 * 请求拦截
 */
http.interceptors.request.use(config => {
  if (global.noTokenUrl.indexOf(config.url) > -1 || config.url.indexOf('/api/sys/validate/image/code/') > -1 || config.url.indexOf('/api/sys/get/userMobile') > -1 || config.url.indexOf('/api/sys/forget/password') > -1) return config
  if (store.state.token !== null && store.state.token !== '') {
    config.headers['Authorization'] = 'Bearer ' + store.state.token // 请求头带上token
  }
  return config
}, error => {
  return Promise.reject(error)
})

/**
 * 响应拦截
 */
http.interceptors.response.use(response => {
  switch (response.data.code) {
    case 5000:
      initMessageBox(response.data.msg ? response.data.msg : '会话已过期，请重新登录')
      clearLoginInfo()
      // router.push('/login')
      break
  }
  if (response.data.code === 500 || (response.data.code && response.data.code != '200')) {
    let errorMsg = response.data.message ? response.data.message : response.data.data.message ? response.data.data.message : response.data.data ? response.data.data : global.errorMsg
    if (response.data.code === 500 && (errorMsg.indexOf('Exception') > -1 || errorMsg.indexOf('exception') > -1)) {
      showErrorDialog()
    } else if(response.data.code == '401'){
      if(!isShowLoginLose){
        isShowLoginLose = true
        initMessageBox('会话已过期，请重新登录')
      }
      clearLoginInfo()
    } else {
      isShowLoginLose = false
      if (errorMsg == '无法获取当前登录用户信息') {
        clearLoginInfo()
        // router.push('/login')
      } else {
        if (response.config.headers.customSuccess) {
          return response
        } else {
          initMessageBox(errorMsg)
        }
      }
    }
  } else {
    return response
  }
}, error => {
  switch (error.response.status) {
    case 400:
      initMessageBox(error.response.data)
      break
    case 401:
      if(!isShowLoginLose){
        isShowLoginLose = true
        initMessageBox('会话已过期，请重新登录')
      }
      clearLoginInfo()
      router.push('/login')
      break
    case 405:
      initMessageBox('http请求方式有误')
      break
    case 500:
      // initMessageBox('服务器出了点小差，请稍后再试')
      showErrorDialog()
      break
    case 501:
      initMessageBox('服务器不支持当前请求所需要的某个功能')
      break
  }
  return Promise.reject(error)
})

/**
 * 请求地址处理
 * @param {*} actionName action方法名称
 */
http.adornUrl = (actionName) => {
  // 非生产环境 && 开启代理, 接口前缀统一使用[/proxyApi/]前缀做代理拦截!
//  return (process.env.NODE_ENV !== 'production' && process.env.OPEN_PROXY ? '/proxyApi' : window.SITE_CONFIG.baseUrl) + actionName
  return (process.env.NODE_ENV !== 'production' && process.env.OPEN_PROXY ? '/proxyApi' : window.SITE_CONFIG.baseUrl) + actionName
}

/**
 * get请求参数处理
 * @param {*} params 参数对象
 * @param {*} openDefultParams 是否开启默认参数?
 */
http.adornParams = (params = {}, openDefultParams = true) => {
  var defaults = {
    't': new Date().getTime()
  }
  return openDefultParams ? merge(defaults, params) : params
}

/**
 * post请求数据处理
 * @param {*} data 数据对象
 * @param {*} openDefultdata 是否开启默认数据?
 * @param {*} contentType 数据格式
 *  json: 'application/json; charset=utf-8'
 *  form: 'application/x-www-form-urlencoded; charset=utf-8'
 */
// http.adornData = (data = {}, openDefultdata = true, contentType = 'json') => {
//   var defaults = {
//     't': new Date().getTime()
//   }
//   data = openDefultdata ? merge(defaults, data) : data
//   return contentType === 'json' ? JSON.stringify(data) : qs.stringify(data)
// }

export default http
