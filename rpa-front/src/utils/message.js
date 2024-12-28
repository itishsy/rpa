import { MessageBox } from 'element-ui'
let count = 0
function common (type, msg, title, callback) {
  if (count == 0) {
    MessageBox.confirm(msg, title || '提示', {
      customClass: 'spl-confirm',
      showConfirmButton: false,
      showCancelButton: false,
      type: type,
      closeOnClickModal: false,
      dangerouslyUseHTMLString: true
    }).catch(() => {
      count = 0
      window.clearTimeout(timeId)
    })
    count = 5000
    var timeId = setTimeout(function () {
      count = 0
      MessageBox.close()
      if (callback) {
        callback()
      }
    }, 2000)
  } else {
    setTimeout(function () {
      count = 0
      common(msg, title, callback)
    }, 2000)
  }
}
function message(type,msg,title,callback){
  this.type = type ? type : ''
  this.msg = msg ? msg : ''
  this.title = title ? title : '提示'
  this.callback = callback ? callback : function(){}
  this.timer = null
  this.second = 2000
}
message.prototype.doClose = function(second){
  clearTimeout(this.timer)
  if(second){
    this.second = second
    this.timer = setTimeout(()=>{
      MessageBox.close()
      if(this.callback){
        this.callback()
      }
    },second)
  }else{
    this.timer = setTimeout(()=>{
      MessageBox.close()
      if(this.callback){
        this.callback()
      }
    },this.second)
  }
  return this
}
message.prototype.init= function(){
  MessageBox.confirm(this.msg, this.title, {
    customClass: 'spl-confirm',
    showConfirmButton: false,
    showCancelButton: false,
    type: this.type,
    closeOnClickModal: false,
    dangerouslyUseHTMLString: true,
    modal:true,
  }).catch(() => {
    if(this.callback){
      this.callback()
    }
    clearTimeout(this.timer)
  })
  return this
}
export default {
  install (Vue) {
    Vue.prototype.$message = {// 全局方法
      success: function (msg, title, callback) { // 获取url中的参数
        // common('success', msg, title, callback)
        return new message('success',msg, title, callback).init().doClose()
      },
      error: function (msg, title, callback) { // 获取url中的参数
        // common('error', msg, title, callback)
        return new message('error',msg, title, callback).init().doClose()
      },
      warning: function (msg, title, callback) { // 获取url中的参数
        // common('warning', msg, title, callback)
        return new message('warning',msg, title, callback).init().doClose()
      },
      close: function () {
        MessageBox.close()
      }
    }
  }
}
