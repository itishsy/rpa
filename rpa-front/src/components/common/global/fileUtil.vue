<script>
// 配置axios请求

import httpRequest from '../../../utils/httpRequest'
import { MessageBox } from 'element-ui'
httpRequest.defaults.baseURL = '/api'
let Base64 = require('js-base64').Base64

var httpTimeId
function initMessageBox(msg, title, type, callback) {
  let initType = type || 'error'
  MessageBox.confirm('<span style="white-space: pre-line">' + msg + '</span>', title || '提示', {
    customClass: 'spl-confirm',
    showConfirmButton: false,
    showCancelButton: false,
    dangerouslyUseHTMLString: true,
    type: initType,
    closeOnClickModal: false,
    duration: 2000
  }).catch(() => {
    window.clearTimeout(httpTimeId)
    if (callback) {
      callback()
    }
  })
  httpTimeId = setTimeout(function () {
    MessageBox.close()
    if (callback) {
      callback()
    }
  }, 5000)
}
function showErrorDialog2(obj) {
  MessageBox.prompt('请输入您的用户密码，验证通过后方可导出数据。', '安全验证', {
    confirmButtonText: '确认',
    showCancelButton: false,
    inputValidator: function (val) {
      if (val == null || val == '') {
        return '请输入用户密码'
      } else {
        return true
      }
    },
    inputErrorMessage: '请输入用户密码',
    inputPlaceholder: '请输入用户密码',
    closeOnClickModal: false,
    closeOnHashChange: false,
    closeOnPressEscape: false,
    inputType: 'password'
  }).then(({ value }) => {
    if (obj.callback) {
      obj.callback(value)
    }
  }).catch(() => {
  })
}

// 这儿的 getPdf() 是获取文件流的 axios 封装方法
function print(reqObj, callback) {
  if (reqObj == null) {
    try {
      throw new Error('参数错误')
    } catch (error) {
      // console.error(error)
    }
    // window.open('http://192.168.0.81:8888/group1/M00/01/EF/wKgAUV34Ps2AJrbgACMXVF1lULI859.pdf')
  } else {
    httpRequest(reqObj).then((res) => {
      if (callback) {
        callback()
      }
      var pdfUrl = getObjectURL(res.data)
      // console.log("=====pdfUrl==========="+pdfUrl)
      if (pdfUrl == null) {
        try {
          throw new Error('文件路径解析出错，请检查对应接口返回数据是否正确')
        } catch (error) {
          // console.error(error)
        }
      } else {
        if (isIE()) { window.navigator.msSaveOrOpenBlob(new Blob([res.data], { type: 'application/pdf;charset=utf-8' })) } else {
          var gotolink = document.createElement('a')
          gotolink.href = pdfUrl
          gotolink.setAttribute('target', '_blank')
          document.body.appendChild(gotolink)
          gotolink.click()
          document.body.removeChild(gotolink) // 下载完成移除元素
        }
      }
    })
  }
}
function downloadFile(url, method, params, fileType, fileName, callback, pswVal, requiredFileName) {
  var httpJson = {
    url: url,
    method: method,
    responseType: 'blob'
  }

  if (method === 'get' || method === 'GET') {
    if (pswVal) {
      params.password = Base64.encode(pswVal)
    }
    httpJson.params = params
  } else if (method === 'post' || method === 'POST') {
    if (pswVal) {
      httpJson.url = url + '?password=' + Base64.encode(pswVal)
    }
    httpJson.data = params
  }
  httpRequest(httpJson).then((response) => {
    if (callback) {
      callback()
    }
    var data = response.data
    var headers = response.headers
    if (headers.code == '403') {
      showErrorDialog2({
        callback: function (val) {
          downloadFile(url, method, params, fileType, fileName, callback, val)
        }
      })
      return
    } else if (headers.code == '402') {
      initMessageBox('密码不正确', '', '', function () {
        showErrorDialog2({
          callback: function (val) {
            downloadFile(url, method, params, fileType, fileName, callback, val)
          }
        })
      })
      return
    }
    if (!data) {
      return
    }
    var type = data.type
    if (type && type == 'application/json') {
      let reader = new FileReader(); // 创建读取文件对象
      reader.readAsText(data, 'utf-8'); // 设置读取的数据以及返回的数据类型为utf-8
      reader.addEventListener("loadend", function () { //
        let res = JSON.parse(reader.result); // 返回的数据
        initMessageBox(res.message ? res.message : '系统异常');
      })
      return;
    }
    var fName = decodeURI(headers.filename)
    var fType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8'
    var fileFix = '.xlsx'
    var fileTypeRes = headers.filetype == undefined ? headers['content-type'] : headers.filetype
    if (fileTypeRes) {
      fType = fileTypeRes

      if (fName == undefined || fName == 'undefined' || fName == null && fName === '') {
        if (fileName) {
          fName = fileName
        } else {
          fName = '导出文件.xlsx'
        }
      }
      if (requiredFileName) {
        fName = fileName
      }
    } else {
      if (fileType) {
        fType = fileType.blobType
        fileFix = fileType.fileFix
      }
      if (fName == undefined || fName == 'undefined' || fName == null && fName === '') {
        if (fileName) {
          fName = fileName + fileFix
        } else {
          fName = '导出文件.xlsx'
        }
      } else {
        if (fName.lastIndexOf(fileFix) < 0) {
          fName = fName + fileFix
        }
      }
    }

    const blob = new Blob([data], { type: fType })
    if (isIE()) {
      window.navigator.msSaveBlob(blob, fName)
    } else {
      const downloadElement = document.createElement('a')
      const href = window.URL.createObjectURL(blob)
      downloadElement.href = href
      downloadElement.download = fName
      document.body.appendChild(downloadElement)
      downloadElement.click()
      document.body.removeChild(downloadElement) // 下载完成移除元素
      window.URL.revokeObjectURL(href) // 释放掉blob对象
    }
  })
}

function isIE() {
  var userAgent = navigator.userAgent // 取得浏览器的userAgent字符串
  var isIE = userAgent.indexOf('compatible') > -1 && userAgent.indexOf('MSIE') > -1 // 判断是否IE<11浏览器
  var isEdge = userAgent.indexOf('Edge') > -1 && !isIE // 判断是否IE的Edge浏览器
  var isIE11 = userAgent.indexOf('Trident') > -1 && userAgent.indexOf('rv:11.0') > -1
  return isIE || isEdge || isIE11
}

// 将返回的流数据转换为url
function getObjectURL(data) {
  let url = null
  const file = new Blob([data], { type: 'application/pdf;charset=utf-8' })
  try {
    url = window.URL.createObjectURL(file)
  } catch (error) {
  }
  return url
}

// 暴露出这些属性和方法
export default {
  print, downloadFile
}
</script>
