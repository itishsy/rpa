<script>
import store from '../../../store/index'
import Vue from 'vue'
import ElementUI from 'element-ui'
// 定义一些公共的属性和方法
const bodyHeight = window.innerHeight - 70
const menuBarWidth = 250 // 侧边栏宽度+间隔
const errorMsg = '系统异常'
const phoneRegExp = /^\d{11}$/ // 密码正则
const pswRegExp = '^[A-Za-z0-9]{6,12}$' // 密码正则
const pswRegExpMsg = '密码由6-12位以上的数字、字母组成' // 密码正则-提示信息
// var currentUser = null
var indexDefaultMenu = [{ resourceUrl: 'organization', resourceName: '组织架构' }, { resourceUrl: 'employeeList', resourceName: '员工信息' }, { resourceUrl: 'substituteSalary', resourceName: '薪酬核算' }]
var switchColor = ['#13ce66', '#dbdbdb'] // switch开关的颜色，第一个为开的颜色，第二个为关的颜色
var EXCEL = { fileFix: '.xlsx', blobType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8' }
var WORD = { fileFix: '.docx', blobType: 'application/msword;charset=utf-8' }
var PDF = { fileFix: '.pdf', blobType: 'application/pdf;charset=utf-8' }
var TXT = { fileFix: '.txt', blobType: 'text/plain' }
var JAR = { fileFix: '.jar', blobType: 'application/java-archive' }
var ZIP = { fileFix: '.zip', blobType: 'application/zip' }
var PNG = { fileFix: '.png', blobType: 'image/png' }
var JPG = { fileFix: '.jpg', blobType: 'image/jpg' }
var JPEG = { fileFix: '.jpeg', blobType: 'image/jpeg' }
// var setUploadHeaders = { 'Authorization': store.state.token } // token对象
var uploadImgUrl = '/api/api/sys/upload' // 上传图片接口
var uploadUrl = '/api/api/file/upload' // 上传文件接口
var uploadFileUrl = '/api/policy/sys/file/fileUpload' // 上传文件接口
var uploadReturnSheetInfo = '/api/api/file/uploadReturnSheetInfo' // 上传文件接口
var downloadUrl = '/api/file/download/' // 下载文件接口
var elementLoadingText = '拼命加载中' // loading层文本
var elementLoadingSpinner = 'el-icon-loading' // loading层样式
var AES_KEY = '1a2C9b8gh5ltvt0m'
var noTokenUrl = ['/oauth/token', '/api/sys/mobile/send/code', '/api/sys/mobile/validate/code', '/api/declareUser/loginByCaptcha']
// 日期转String yyyy-MM-dd
// function dateToStr (date) {
//   if (date == null) {
//     return ''
//   }
//   var d = new Date(date)
//   var ar_date = [d.getFullYear(), d.getMonth() + 1, d.getDate()]
//   for (var i = 0; i < ar_date.length; i++) ar_date[i] = dFormat(ar_date[i])
//   return ar_date.join('-')
//   function dFormat (i) {
//     return i < 10 ? '0' + i.toString() : i
//   }
// }

function dateFormat (dateString) { //  202007  转成 2020-07
  if (dateString) {
    var dateStr = dateString.toString()
    var date1 = dateStr.slice(0, 4).split(',')
    var date2 = dateStr.slice(4, 6).split(',')
    var date3 = date1.concat(date2)
    var date4 = date3.join('-')
    return date4
  }
}

function getNowDate (formart) {
  var d = new Date()
  var ar_date1 = []
  var ar_date = [d.getFullYear(), d.getMonth() + 1]
  if (formart.indexOf('dd') > -1) {
    ar_date.push(d.getDate())
  }
  if (formart.indexOf('HH') > -1) {
    ar_date1.push(d.getHours())
  }
  if (formart.indexOf('mm') > -1) {
    ar_date1.push(d.getMinutes())
  }
  if (formart.indexOf('ss') > -1) {
    ar_date1.push(d.getSeconds())
  }
  for (var i = 0; i < ar_date.length; i++) ar_date[i] = dFormat(ar_date[i])
  for (var i = 0; i < ar_date1.length; i++)ar_date1[i] = dFormat(ar_date1[i])
  return ar_date.join('-') + (ar_date1.length > 0 ? ' ' + ar_date1.join(':') : '')
  function dFormat (i) { return i < 10 ? '0' + i.toString() : i }
}

/**
 * 深拷贝数组（支持多维数组）
 * @param obj 被复制的数组
 * @param resetProperty 被置空的属性字段
 * @param deleteProperty 不被复制的属性字段
 **/
function deepcopyArray (obj, resetProperty, deleteProperty) {
  var out = []; var i = 0; var len = obj.length
  for (; i < len; i++) {
    if (obj[i] instanceof Array) {
      out[i] = deepcopyArray(obj[i], resetProperty, deleteProperty)
    } else if (obj[i] instanceof Object) {
      for (var item in obj[i]) {
        if (obj[i][item] instanceof Array) {
          if (out[i] == undefined) {
            out[i] = {}
          }
          out[i][item] = deepcopyArray(obj[i][item], resetProperty, deleteProperty)
        } else {
          if (out[i] == undefined) {
            out[i] = {}
          }
          if (!(deleteProperty != undefined && deleteProperty === item)) {
            if (resetProperty != undefined && resetProperty === item) {
              out[i][item] = ''
            } else {
              out[i][item] = obj[i][item]
            }
          }
        }
      }
    } else {
      out[i] = obj[i]
    }
  }
  return out
}

/**
 * 深拷贝数组（支持多维数组）
 * @param obj 被复制的数组
 * @param copyProperty 指定需要复制的字段属性
 * @param notOnlyFlag 非仅复制指定的字段，默认false
 * @param addProperty 除复制外额外添加的字段 {id:'zhangsan',name:'张三'}
 **/
function deepcopyArrayProperty (obj, copyProperty, notOnlyFlag, addProperty) {
  var out = []; var i = 0; var len = obj.length
  for (; i < len; i++) {
    if (obj[i] instanceof Array) {
      out[i] = deepcopyArrayProperty(obj[i], copyProperty, notOnlyFlag, addProperty)
    } else if (obj[i] instanceof Object) {
      for (var item in obj[i]) {
        if (obj[i][item] instanceof Array) {
          if (out[i] == undefined) {
            out[i] = {}
          }
          if (copyProperty != null && item in copyProperty) {
            out[i][copyProperty[item]] = deepcopyArrayProperty(obj[i][item], copyProperty, notOnlyFlag, addProperty)
          } else {
            out[i][item] = deepcopyArrayProperty(obj[i][item], copyProperty, notOnlyFlag, addProperty)
          }
        } else {
          if (out[i] == undefined) {
            out[i] = {}
          }
          if (copyProperty != null && item in copyProperty) {
            out[i][copyProperty[item]] = obj[i][item]
          } else {
            if (notOnlyFlag) {
              out[i][item] = obj[i][item]
            }
          }
          if (addProperty) {
            for (var key in addProperty) {
              out[i][key] = addProperty[key]
            }
          }
        }
      }
    } else {
      out[i] = obj[i]
    }
  }
  return out
}

/**
 * 深拷贝数组（支持多维数组）
 * copyResource、copyProperty、copyAllProperty,choiceProperty、merges、disabled
 * {
 *  copyResource:[] 需要拷贝的数组
 *  copyProperty:{id":"userId","name":"userName"} 指定copy后属性名变更,id变为userId，name变为userName
 *  copyAllProperty: true/false 是否复制全部属性字段 默认 true
 *  choiceProperty:{abort:['userIds','userNames'],get:"orgs"} 取舍字段，标识orgs与userIds,userNames同时存在值时，取orgs，舍弃掉userIds,userNames
 *  merges:合并属性 {name1:'newName1',name2:'newName2',mergeArrayName:'mergeArrayName'}name1和name2 必须是元素是字符串的数组,合并成字段名为mergeArrayName指点名称的对象数组
 *  disabled:true/false 标识disabled掉非叶子节点
 * }
 * */

function deepcopyArrayPropertyTwo (copyParams) {
  try {
    if (copyParams.choiceProperty && !copyParams.choiceProperty.get && !copyParams.choiceProperty.abort) {
      throw new Error('copyParams.choiceProperty get和abort参数错误')
    } else {
      var mergesKeys = []
      if (copyParams.merges) {
        for (var item in copyParams.merges) {
          if (item != 'mergeArrayName') {
            mergesKeys.push(item)
          }
        }
      }
      var copyParamsItem = {
        copyProperty: copyParams.copyProperty,
        copyAllProperty: copyParams.copyAllProperty,
        choiceProperty: copyParams.choiceProperty,
        merges: copyParams.merges,
        disabled: copyParams.disabled
      }
      var out = []; var i = 0; var len = copyParams.copyResource.length
      for (; i < len; i++) {
        if (copyParams.copyResource[i] instanceof Array) {
          copyParamsItem.copyResource = copyParams.copyResource[i]
          out[i] = deepcopyArrayPropertyTwo(copyParamsItem)
        } else if (copyParams.copyResource[i] instanceof Object) {
          for (var item in copyParams.copyResource[i]) {
            if (copyParams.copyResource[i][item] instanceof Array) {
              if (copyParams.choiceProperty && copyParams.choiceProperty.abort.indexOf(item) >= 0 && copyParams.copyResource[i][copyParams.choiceProperty.get] != undefined &&
                  copyParams.copyResource[i][copyParams.choiceProperty.get] != null && copyParams.copyResource[i][copyParams.choiceProperty.get] !== '' && copyParams.copyResource[i][copyParams.choiceProperty.get].length > 0) {
                // console.log('====舍弃=====' + JSON.stringify(copyParams.copyResource[i][item]))
              } else {
                if (out[i] == undefined) {
                  out[i] = {}
                }
                if (copyParams.merges && mergesKeys.indexOf(item) != -1) {
                  if (mergesKeys.indexOf(item) == 0) {
                    var mergeArray = []
                    for (var n = 0; n < copyParams.copyResource[i][item].length; n++) {
                      var mergeObj = {}
                      for (var key in mergesKeys) {
                        mergeObj[copyParams.merges[mergesKeys[key]]] = ''
                      }
                      mergeArray.push(mergeObj)
                    }
                    for (var str in mergesKeys) {
                      for (var m = 0; m < copyParams.copyResource[i][mergesKeys[str]].length; m++) {
                        mergeArray[m][copyParams.merges[mergesKeys[str]]] = copyParams.copyResource[i][mergesKeys[str]][m]
                      }
                    }
                    out[i][copyParams.merges.mergeArrayName] = mergeArray
                  }
                } else {
                  if (copyParams.copyProperty != null && item in copyParams.copyProperty) {
                    copyParamsItem.copyResource = copyParams.copyResource[i][item]
                    out[i][copyParams.copyProperty[item]] = deepcopyArrayPropertyTwo(copyParamsItem)
                  } else {
                    copyParamsItem.copyResource = copyParams.copyResource[i][item]
                    out[i][item] = deepcopyArrayPropertyTwo(copyParamsItem)
                  }
                }
              }
            } else {
              if (out[i] == undefined) {
                out[i] = {}
              }
              if (copyParams.copyProperty != null && item in copyParams.copyProperty) {
                out[i][copyParams.copyProperty[item]] = copyParams.copyResource[i][item]
              } else {
                if (copyParams.copyAllProperty == undefined || copyParams.copyAllProperty == true) {
                  out[i][item] = copyParams.copyResource[i][item]
                }
              }
              if (copyParams.disabled) {
                out[i].disabled = true
              }
            }
          }
        } else {
          out[i] = copyParams.copyResource[i]
        }
      }
      return out
    }
  } catch (e) {
    // console.error(e)
    // console.error(e.message)
  }
}

// 拷贝对象（深度）
function deepCopyObj (obj) {
  var result = Array.isArray(obj) ? [] : {}
  for (var key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      if (typeof obj[key] === 'object' && obj[key] !== null) {
        result[key] = deepCopyObj(obj[key]) // 递归复制
      } else {
        result[key] = obj[key]
      }
    }
  }
  return result
}

// 生成特定开头的随机字符串
function generate (headStr, length) {
  var randStr = ''
  for (var i = 0; i < length; i++) {
    var randItem = Math.floor(Math.random() * 10)
    randStr += randItem
  }
  return headStr + randStr
}

/**
 *
 * @param formulaText 要校验的公式
 * @param texts 所有的固定项 以数组的形式传递
 * @returns {boolean} 返回的校验结果 true or false
 */
function checkFormula (formulaText, texts) {
  if (formulaText == null || formulaText == '') {
    return false
  } else {
    if (formulaText.indexOf('%%') >= 0 || formulaText.indexOf('+%') >= 0 ||
      formulaText.indexOf('-%') >= 0 || formulaText.indexOf('*%') >= 0 ||
      formulaText.indexOf('/%') >= 0 || formulaText.indexOf('+') == 0 ||
      formulaText.indexOf('-') == 0 || formulaText.indexOf('*') == 0 ||
      formulaText.indexOf('/') == 0 || formulaText.indexOf('%') == 0) {
      return false
    }
    formulaText = formulaText.replace('%', '/100')
  }
  var hantag = false// 是否包含公式项，默认不包含
  var formulaText1 = formulaText
  texts.forEach(item => {
    if (formulaText.indexOf(item) >= 0) {
      hantag = true
    }
  })

  texts = texts.sort(function (a, b) {
    return b.length - a.length
  })
  for (var i = 0; i < texts.length; i++) {
    formulaText1 = formulaText1.replace(new RegExp(texts[i], 'gm'), ',')
    formulaText = formulaText.replace(new RegExp(texts[i], 'gm'), generate('12', 3))
  }
  try {
    eval(formulaText)
    if (hantag) {
      var array = formulaText1.split(',')
      var reg = /^(-?\d+)(\.\d+)?$/
      var reg1 = /^(\d+)(\.\d+)?$/
      for (var i = 0; i < array.length; i++) {
        var tt = array[i]
        if (array[i] == '') { // 等于"",表示是公式项
          if (i < array.length - 1 && array[i + 1] == '' && array.length > 2) { // 说明两个公式项之间没有运算符
            return false
          }
          if (i == 0 && i < array.length - 1) {
            // 公式项后面不能跟正数
            var tt1 = array[i + 1]
            if (reg1.test(tt1.split('+')[0]) || reg1.test(tt1.split('-')[0]) ||
              reg1.test(tt1.split('*')[0]) || reg1.test(tt1.split('/')[0])) {
              return false
            }
          }

          if (i == array.length - 1 && array.length != 1) { // 公式项前面不能是数字（正数、负数）
            // 前面的不能跟数字
            var tt1 = array[i - 1]
            if (reg1.test(tt1.split('+')[tt1.split('+').length - 1]) ||
              reg1.test(tt1.split('-')[tt1.split('-').length - 1]) ||
              reg1.test(tt1.split('*')[tt1.split('*').length - 1]) ||
              reg1.test(tt1.split('/')[tt1.split('/').length - 1])) {
              return false
            }
          }
        } else {
          var ja = tt.split('+')
          var jian = tt.split('-')
          var chen = tt.split('*')
          var chu = tt.split('/')
          if (i == 0 || i == (array.length - 1)) { // 非公式项，并且处于公式的首位、或末尾
            if (i == 0) { // 首位
              // 首位的最后一个不能是数字
              if (reg.test(ja[ja.length - 1]) || reg.test(jian[jian.length - 1]) || reg.test(chen[chen.length - 1]) || reg.test(chu[chu.length - 1])) {
                return false
              }
            } else {
              // 末尾第一个不能是数字
              if (reg1.test(ja[0]) || reg1.test(jian[0]) || reg1.test(chen[0]) || reg1.test(chu[0])) {
                return false
              }
            }
          } else {
            /* array中间元素不为空的
             * 那么该元素第一位不能为正数
             * 最后一位不能为数字
             * */
            if (reg1.test(ja[0]) || reg.test(ja[ja.length - 1]) ||
              reg1.test(jian[0]) || reg.test(jian[jian.length - 1]) ||
              reg1.test(chen[0]) || reg.test(chen[chen.length - 1]) ||
              reg1.test(chu[0]) || reg.test(chu[chu.length - 1])
            ) {
              return false
            }
          }
        }
      }
    }
  } catch (e) {
    return false
  }
  return true
}

// 数据中添加千位符number为需要转化的数字 decimalNumber为保留的小数位数
function milliFormat (number, decimal) {
  if (number == null || number === '') {
    return ''
  }
  if (!isRealNum(number)) {
    return number
  }
  var decimalNumber
  if (decimal == null || decimal === '') {
    decimalNumber = 2
  } else if (decimal == 'keep') {
    decimalNumber = String(number).split('.')[1] ? String(number).split('.')[1].length : 2
  } else {
    decimalNumber = decimal
  }
  return (Math.round(parseFloat(number) * Math.pow(10, decimalNumber)) / Math.pow(10, decimalNumber)).toFixed(decimalNumber)
    .replace(/\d+/, function (s) {
      return s.replace(/(\d)(?=(\d{3})+$)/g, '$1,')
    })
}

/**
 * 四舍五入保留小数位数
 * @param number 需要保留的数字
 * @param decimal 保留小数位数
 * @returns {string}
 */
function preserveDecimal (number, decimal) {
  if (number == null || number === '') {
    return ''
  }
  var decimalNumber = 2
  if (decimal != undefined && decimal != null || decimal !== '') {
    decimalNumber = decimal
  }
  return (Math.round(parseFloat(number) * Math.pow(10, decimalNumber)) / Math.pow(10, decimalNumber)).toFixed(decimalNumber)
}

function isRealNum (val) {
  if (!isNaN(val)) {
    return true
  } else {
    return false
  }
}

// 获取select选中的文本
function getSelectLabel (value, options) {
  if ((typeof value) === 'object') {
  //  为多选
    var currentLabel = []
    options.forEach(item => {
      value.forEach(it => {
        if (item.value === it) {
          currentLabel.push(item.currentLabel)
        }
      })
    })
  } else {
  //  单选
    var currentLabel = ''
    options.forEach(item => {
      if (item.value === value) {
        currentLabel = item.currentLabel
      }
    })
  }
  return currentLabel
}

function UUID () { // 获取唯一值
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    var r = Math.random() * 16 | 0; var v = c == 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

// 获取指定长度的数字和字母组合的字符串
function getRandomPassword (length) {
  var codes = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
    'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
  var numbers = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9']
  var letters = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
    'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
  codes.sort(randSort)
  var arr = []
  var total = codes.length - 1
  var haveNumber = false
  var haveLetter = false
  for (var i = 0; i < length; i++) {
    var index = Math.round(Math.random() * total)
    let code = codes[index]
    if (!haveNumber && numbers.indexOf(code) != -1) {
      haveNumber = true
    }
    if (!haveLetter && letters.indexOf(code) != -1) {
      haveLetter = true
    }
    arr.push(code)
  }
  if (!(haveNumber && haveLetter)) {
    if (haveNumber) {
      var index = Math.round(Math.random() * (letters.length - 1))
      var code = letters[index]
      arr[length - 1] = code
    }
    if (haveLetter) {
      var index = Math.round(Math.random() * (numbers.length - 1))
      var code = numbers[index]
      arr[length - 1] = code
    }
  }
  return arr.join('')
}
function randSort (a, b) {
  return Math.random() > 0.5 ? -1 : 1
}

function isBlank (str) {
  return str == undefined || str == null || str === '' || str == 'null'
}
function isNotBlank (str) {
  return str != undefined && str != null && str !== '' && str !== 'null'
}

function isPhone (phone) {
  if (phoneRegExp.test(phone)) {
    return true
  } else {
    return false
  }
}
function isPhoneValidator (rule, value, callback) {
  if (value == null || value == '' || phoneRegExp.test(value)) {
    callback()
  } else {
    callback(new Error('请输入正确手机号'))
  }
}
function hidePhoneHandle (phone) {
  return phone.replace(/^(\d{3})\d{4}(\d+)/, '$1****$2')
}

function getInputDataRow (val) {
  var conts = val.split('\n')
  var num = 0
  var valArr = []
  conts.forEach(it => {
    if (it.replace(/\s+/g, '').length > 0) {
      valArr.push(it)
      num++
    }
  })
  var obj = { num: num, value: valArr }
  return obj
}

function getAgeByIdCard (UUserCard) {
  // 获取年龄
  var myDate = new Date()
  var month = myDate.getMonth() + 1
  var day = myDate.getDate()
  var age = myDate.getFullYear() - UUserCard.substring(6, 10) - 1
  if (UUserCard.substring(10, 12) < month || UUserCard.substring(10, 12) == month && UUserCard.substring(12, 14) <= day) {
    age++
  }
  return age
}

function getSexByIdCard (UUserCard) {
  console.info(UUserCard)
  // 获取性别
  if (parseInt(UUserCard.substr(16, 1)) % 2 == 1) {
    // 男
    console.info('男')
    return '男'
  } else {
    // 女
    return '女'
  }
}

/**
 * 根据身份证获取出生日期（yyyy-MM-dd）
 * @param psidno
 * @returns {birthday:yyyy-MM-dd}
 * @constructor
 */
function getBirthday (idCard) {
  var birthdayno, birthdaytemp
  if (idCard.length == 18) {
    birthdayno = idCard.substring(6, 14)
  } else if (idCard.length == 15) {
    birthdaytemp = idCard.substring(6, 12)
    birthdayno = '19' + birthdaytemp
  } else {
    return ''
  }
  var birthday = birthdayno.substring(0, 4) + '-' + birthdayno.substring(4, 6) + '-' + birthdayno.substring(6, 8)
  return birthday
}

/**
 *根据输入的身份证获取性别代码
 * @param psidno
 * @returns {sex}
 * @constructor
 */
function getSex (idCard) {
  var sexno, sex
  if (idCard.length == 18) {
    sexno = idCard.substring(16, 17)
  } else if (idCard.length == 15) {
    sexno = idCard.substring(14, 15)
  } else {
    return ''
  }
  var tempid = sexno % 2
  if (tempid == 0) {
    sex = 2
  } else {
    sex = 1
  }
  return sex
}

function isAcrobatPluginInstall () {
  // 下面代码都是处理IE浏览器的情况
  if ((window.ActiveXObject) || (navigator.userAgent.indexOf('Trident') > -1)) {
    for (var x = 2; x < 10; x++) {
      try {
        var oAcro = eval("new ActiveXObject('PDF.PdfCtrl." + x + "');")
        if (oAcro) {
          return true
        }
      } catch (e) {}
    }
    try {
      var oAcro4 = new ActiveXObject('PDF.PdfCtrl.1')
      if (oAcro4) { return true }
    } catch (e) {}
    try {
      var oAcro7 = new ActiveXObject('AcroPDF.PDF.1')
      if (oAcro7) { return true }
    } catch (e) {}
  } else {
  // chrome和FF、safrai等其他浏览器
    return true
  }
}

function setUploadHeaders () {
  return { 'Authorization': 'Bearer ' + store.state.token }
}

function hasPermission (permissionCode) {
  var userPermissions = store.state.buttons
  if (userPermissions && userPermissions.length > 0) {
    return userPermissions.filter(item => { return item.code === permissionCode }).length > 0
  }
  return false
}

function hasAnyPermission (permissionCodes) {
  if (!permissionCodes || permissionCodes.length == 0) {
    return false
  }
  var userPermissions = store.state.buttons
  var result = false
  if (userPermissions && userPermissions.length > 0) {
    for (var i = 0; i < permissionCodes.length; i++) {
      result = userPermissions.filter(item => { return item.code === permissionCodes[i] }).length > 0
      if (result) {
        break
      }
    }
  }
  return result
}

/**
 * 对象数组去重
 * @param {any} array:数组
 * @param {any} field:去重字段
 */
function arrayToDistinct (arr, field) {
  const res = new Map()
  return arr.filter(item => !res.has(item[field]) && res.set(item[field], 1))
}

// size限制的文件大小，单位为MB
function checkFileSize (size) {
  var size2 = size || 10
  return function (file) {
    if (file.size / 1024 / 1024 > size2) {
      Vue.prototype.$message.error('上传附件文件不能超过' + size2 + 'M大小')
      return false
    }
    return true
  }
}
var loading = null
function showLoading (options) {
  var obj = {
    msg: '加载中...',
    target: document.body
  }
  if (typeof options === 'string') {
    obj.msg = options
  } else {
    Object.assign(obj, options)
  }
  loading = ElementUI.Loading.service({
    target: obj.target,
    lock: true,
    text: obj.msg,
    spinner: 'el-icon-loading',
    background: 'rgba(255, 255, 255, 0.7)'
  })
}
function closeLoading () {
  if (loading && loading.close) {
    loading.close()
  }
}

function decrypt (str) {
  var cryptoJS = require('crypto-js')
  var key = cryptoJS.enc.Utf8.parse(AES_KEY)
  var decrypt = cryptoJS.AES.decrypt(str, key, {
    mode: cryptoJS.mode.ECB,
    padding: cryptoJS.pad.Pkcs7
  })
  return decrypt.toString(cryptoJS.enc.Utf8)
}

// 暴露出这些属性和方法
export default {
  bodyHeight,
  menuBarWidth,
  switchColor,
  EXCEL,
  WORD,
  PDF,
  TXT,
  JAR,
  ZIP,
  dateFormat,
  deepcopyArray,
  getNowDate,
  milliFormat,
  getSelectLabel,
  checkFormula,
  generate,
  deepcopyArrayProperty,
  errorMsg,
  deepcopyArrayPropertyTwo,
  deepCopyObj,
  pswRegExp,
  pswRegExpMsg,
  // currentUser,
  indexDefaultMenu,
  UUID,
  setUploadHeaders,
  uploadImgUrl,
  uploadUrl,
  uploadFileUrl,
  uploadReturnSheetInfo,
  downloadUrl,
  elementLoadingText,
  elementLoadingSpinner,
  isBlank,
  isNotBlank,
  isPhone,
  isPhoneValidator,
  hidePhoneHandle,
  getInputDataRow,
  noTokenUrl,
  getAgeByIdCard,
  getSexByIdCard,
  isAcrobatPluginInstall,
  preserveDecimal,
  getBirthday,
  getSex,
  getRandomPassword,
  arrayToDistinct,
  hasPermission,
  hasAnyPermission,
  checkFileSize,
  AES_KEY,
  showLoading,
  closeLoading,
  decrypt
}
</script>

<style scoped>

</style>
