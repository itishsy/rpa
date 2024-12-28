/**
 *
 * @param calFee 费用金额
 * @param feeRatio 费用比例
 * @param calAccuCode 精度
 * @returns {*}
 */
// 按照精度计算合计
function calFee (calFee, feeRatio, calAccuCode) {
  var rtn = accMul(calFee, feeRatio)
  if (calAccuCode != null && calAccuCode != undefined && calAccuCode != '') {
    if (calAccuCode == '10009001') { // 四舍五入到分
      return toFixedValue(rtn, 2)
    } else if (calAccuCode == '10009002') { // 四舍五入到角
      return toFixedValue(rtn, 1)
    } else if (calAccuCode == '10009003') { // 四舍五入到元
      return toFixedValue(rtn, 0)
    } else if (calAccuCode == '10009004') { // 四舍五入保留三位小数
      return toFixedValue(rtn, 3)
    } else if (calAccuCode == '10009005') { // 遇分进角
      return (Math.ceil(Math.ceil(Math.floor(rtn * 100)) / 10) / 10).toFixed(1)
    } else if (calAccuCode == '10009006') { // 遇厘进分
      return (Math.ceil(Math.ceil(Math.floor(rtn * 1000) / 10)) / 100).toFixed(2)
    } else if (calAccuCode == '10009007') { // 舍分取角
      return (Math.floor(rtn * 10) / 10).toFixed(1)
    } else if (calAccuCode == '10009008') { // 舍厘取分
      return (Math.floor(rtn * 100) / 100).toFixed(2)
    } else if (calAccuCode == '10009009') { // 进阶取整
      return Math.ceil(rtn)
    } else if (calAccuCode == '10009010') { // 遇角进元
      rtn = (Math.floor(rtn * 10) / 10).toFixed(1)
      return Math.ceil(rtn)
    } else if (calAccuCode == '10009011') { // 舍角取元
      return Math.floor(rtn).toFixed(0)
    } else if (calAccuCode == '10009012') { // 向上舍入到角
      return (Math.ceil(rtn * 10) / 10).toFixed(1)
    } else if (calAccuCode == '10009013') { // 向上舍入到分
      return (Math.ceil(rtn * 100) / 100).toFixed(2)
    } else if (calAccuCode == '10009014') { // 向上舍入到元
      return (Math.ceil(rtn * 1) / 1).toFixed(0)
    } else if (calAccuCode == '10009015') { // 向上舍入到十
      return (Math.ceil(rtn * 0.1) / 0.1).toFixed(0)
    }
  } else {
    return rtn.toFixed(2)
  }
}

/* 精确乘法 */
function accMul (arg1, arg2) {
  if (arg1 == null || arg1 == undefined || arg2 == null || arg2 == undefined) {
    return 0
  }
  var m = 0; var s1 = arg1.toString(); var s2 = arg2.toString()
  try {
    m += s1.split('.')[1].length
  } catch (e) {
  }
  try {
    m += s2.split('.')[1].length
  } catch (e) {
  }
  return Number(s1.replace('.', '')) * Number(s2.replace('.', '')) / Math.pow(10, m)
}

/* 精确相加 */
function accAdd2 (arg1, arg2) {
  arg1 = arg1 || 0
  arg2 = arg2 || 0

  var length = 0
  var arg1Length = 0
  var arg2Length = 0

  var s1 = arg1.toString()
  var s2 = arg2.toString()

  try {
    arg1Length = s1.split('.')[1].length
  } catch (e) {
  }
  try {
    arg2Length = s2.split('.')[1].length
  } catch (e) {
  }
  length = arg1Length >= arg2Length ? arg1Length : arg2Length
  // length = 2
  return (Number(s1) + Number(s2)).toFixed(length)
}

function toFixedValue (val1, len) {
  var s = val1 + ''
  if (!len) len = 0
  if (s.indexOf('.') == -1) s += '.'
  s += new Array(len + 1).join('0')
  if (new RegExp('^(-|\\+)?(\\d+(\\.\\d{0,' + (len + 1) + '})?)\\d*$').test(s)) {
    var s = '0' + RegExp.$2; var pm = RegExp.$1; var a = RegExp.$3.length; var b = true
    if (a == len + 2) {
      a = s.match(/\d/g)
      if (parseInt(a[a.length - 1]) > 4) {
        for (var i = a.length - 2; i >= 0; i--) {
          a[i] = parseInt(a[i]) + 1
          if (a[i] == 10) {
            a[i] = 0
            b = i != 1
          } else break
        }
      }
      s = a.join('').replace(new RegExp('(\\d+)(\\d{' + len + '})\\d$'), '$1.$2')
    }
    if (b) s = s.substr(1)
    return (pm + s).replace(/\.$/, '')
  }
  return this + ''
};

// 按照精度格式化
function toFixedValueByCode (rtn, calAccuCode) {
  rtn = Number(rtn)
  if (calAccuCode != null && calAccuCode != undefined && calAccuCode != '') {
    if (calAccuCode == '10009001') { // 四舍五入到分
      return toFixedValue(rtn, 2)
    } else if (calAccuCode == '10009002') { // 四舍五入到角
      return toFixedValue(rtn, 1)
    } else if (calAccuCode == '10009003') { // 四舍五入到元
      return toFixedValue(rtn, 0)
    } else if (calAccuCode == '10009004') { // 四舍五入保留三位小数
      return toFixedValue(rtn, 3)
    } else if (calAccuCode == '10009005') { // 遇分进角
      return (Math.ceil(Math.ceil(Math.floor(rtn * 100)) / 10) / 10).toFixed(1)
    } else if (calAccuCode == '10009006') { // 遇厘进分
      return (Math.ceil(Math.ceil(Math.floor(rtn * 1000) / 10)) / 100).toFixed(2)
    } else if (calAccuCode == '10009007') { // 舍分取角
      return (Math.floor(rtn * 10) / 10).toFixed(1)
    } else if (calAccuCode == '10009008') { // 舍厘取分
      return (Math.floor(rtn * 100) / 100).toFixed(2)
    } else if (calAccuCode == '10009009') { // 进阶取整
      return Math.ceil(rtn)
    } else if (calAccuCode == '10009010') { // 遇角进元
      rtn = (Math.floor(rtn * 10) / 10).toFixed(1)
      return Math.ceil(rtn)
    } else if (calAccuCode == '10009011') { // 舍角取元
      return Math.floor(rtn).toFixed(0)
    } else if (calAccuCode == '10009012') { // 向上舍入到角
      return (Math.ceil(rtn * 10) / 10).toFixed(1)
    } else if (calAccuCode == '10009013') { // 向上舍入到分
      return (Math.ceil(rtn * 100) / 100).toFixed(2)
    } else if (calAccuCode == '10009014') { // 向上舍入到元
      return (Math.ceil(rtn * 1) / 1).toFixed(0)
    } else if (calAccuCode == '10009015') { // 向上舍入到十
      return (Math.ceil(rtn * 0.1) / 0.1).toFixed(0)
    }
  } else {
    return rtn.toFixed(2)
  }
}

/* 转换带有百分号的比例为相对精确的数值 例如：0.7% */
function getActualValue (val) {
  var s = val
  var len = 0
  if (s.indexOf('%') > 0) { len = len + 2 }
  s = s.replace('%', '')
  if (s.indexOf('.') > 0) { len = len + s.split('.')[1].length }

  return Number(s.replace('.', '')) / Math.pow(10, len)
}

function numberToPercent (val) {
  if (val != null && val != '') {
    return toFixedValue(val * 100, 4) + '%'
  } else {
    return ''
  }
}

function calOneFee (amount, feeRatio, calAccuCode) {
  return calFee(amount, feeRatio / 100, calAccuCode)
}

function calOneFee2 (amount, feeRatio, calAccuCode) {
  return calFee(amount, feeRatio, calAccuCode)
}

// 金额格式化
function fmoney (s, n) {
  if (undefined != s && s != null && s !== '' && s != 'null') {
    if (isNaN(s)) { // 如果s不是数字，直接返回原字符串
      return s
    }

    n = n > 0 && n <= 20 ? n : 2
    s = parseFloat((s + '').replace(/[^\d\.-]/g, '')).toFixed(n) + ''
    var l = s.split('.')[0].split('').reverse()
    var r = s.split('.')[1]
    var t = ''
    for (var i = 0; i < l.length; i++) {
      if ((l[i] == '-') && (t.charAt(t.length - 1) == ',')) {
        t = t.substring(0, t.length - 1)
      }

      t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? ',' : '')
    }
    return t.split('').reverse().join('') + '.' + r
  } else { // 如果传入空的s，则直接返回空
    return ''
  }
}

/**
 * 10008001  每月
 10008002  每季度
 10008003  每半年
 10008004  每年
 10008005  一次性
 * @param array
 */
function getSocialCyleAmount (array, type) {
  if (array == null || array.length == 0) {
    return 0
  }
  var feeCycleArray = [
    { dictCode: 10008001, dictName: '每月' },
    { dictCode: 10008004, dictName: '每年' },
    { dictCode: 10008005, dictName: '一次性' }
  ]
  var yearTotalAmt = 0
  var monthTotalAmt = 0
  var oneTimeTotalAmt = 0
  var totalAmt = 0
  for (var i = 0; i < array.length; i++) {
    if (array[i].payFrequencyCode == 10008004) {
      yearTotalAmt = accAdd2(yearTotalAmt, calOneFee2(array[i].compBaseMin, array[i].compRatioMax, array[i].compCalAccuCode))
      yearTotalAmt = accAdd2(yearTotalAmt, calOneFee2(array[i].empBaseMin, array[i].empRatioMax, array[i].empCalAccuCode))
    }
    if (array[i].payFrequencyCode == 10008001) {
      monthTotalAmt = accAdd2(monthTotalAmt, calOneFee2(array[i].compBaseMin, array[i].compRatioMax, array[i].compCalAccuCode))
      monthTotalAmt = accAdd2(monthTotalAmt, calOneFee2(array[i].empBaseMin, array[i].empRatioMax, array[i].empCalAccuCode))
    }
    if (array[i].payFrequencyCode == 10008005) {
      oneTimeTotalAmt = accAdd2(oneTimeTotalAmt, calOneFee2(array[i].compBaseMin, array[i].compRatioMax, array[i].compCalAccuCode))
      oneTimeTotalAmt = accAdd2(oneTimeTotalAmt, calOneFee2(array[i].empBaseMin, array[i].empRatioMax, array[i].empCalAccuCode))
    }
  }
  totalAmt = accAdd2(yearTotalAmt, monthTotalAmt)
  totalAmt = accAdd2(totalAmt, oneTimeTotalAmt)
  totalAmt = fmoney(totalAmt, 2)
  if (type == 10008001) {
    return monthTotalAmt
  } else if (type == 10008004) {
    return yearTotalAmt
  } else if (type == 10008005) {
    return oneTimeTotalAmt
  } else {
    return totalAmt
  }
}

function getAccfundAmount (array, type) {
  if (array == null || array.length == 0) {
    return 0
  }
  var comMinAmount = 0
  var comMaxAmount = 0
  var empMinAmount = 0
  var empMaxAmount = 0
  for (let i = 0; i < array.length; i++) {
    // -----单位
    // 单位最低金额
    var comMin = calOneFee2(array[i].compBaseMin, array[i].compRatioMax, array[i].compCalAccuCode)
    if (comMin != undefined) comMinAmount = accAdd2(comMinAmount, parseFloat(comMin))
    // 单位最高金额
    var comMax = calOneFee2(array[i].compBaseMax, array[i].compRatioMax, array[i].compCalAccuCode)
    if (comMax != undefined) comMaxAmount = accAdd2(comMaxAmount, parseFloat(comMax))
    // -----个人
    // 个人最低金额
    var empMin = calOneFee2(array[i].empBaseMin, array[i].empRatioMax, array[i].empCalAccuCode)
    if (empMin != undefined) empMinAmount = accAdd2(empMinAmount, parseFloat(empMin))
    // 个人最高金额
    var empMax = calOneFee2(array[i].empBaseMax, array[i].empRatioMax, array[i].empCalAccuCode)
    if (empMax != undefined) empMaxAmount = accAdd2(empMaxAmount, parseFloat(empMax))
  }
  if (type == 'comMinAmount') {
    return fmoney(comMinAmount, 2)
  } else if (type == 'comMaxAmount') {
    return fmoney(comMaxAmount, 2)
  } else if (type == 'empMinAmount') {
    return fmoney(empMinAmount, 2)
  } else if (type == 'empMaxAmount') {
    return fmoney(empMaxAmount, 2)
  } else if (type == 'sumMinAmount') {
    return fmoney(accAdd2(empMinAmount, comMinAmount), 2)
  } else if (type == 'sumMaxAmount') {
    return fmoney(accAdd2(empMaxAmount, comMaxAmount), 2)
  } else {
    return 0
  }
}

// 将数字按小数位数解析成百分比格式
function parsePercent (s, n) {
  if (s != null && s != '') {
    s = s.replace(/^(\d*)$/, '$1.')
    if (n > 0) {
      if (n == 3) {
        s = (s + '000').replace(/(\d*\.\d\d\d)\d*/, '$1')
      } else if (n == 2) {
        s = (s + '00').replace(/(\d*\.\d\d)\d*/, '$1')
      } else if (n == 1) {
        s = (s + '0').replace(/(\d*\.\d)\d*/, '$1')
      }
    } else {
      s = (s + '0').replace(/(\d*\.\d)\d*/, '$1')
    }
    s = s.replace('.', ',')
    if (n > 0) {
      if (n == 3) {
        s = s.replace(/,(\d\d\d)$/, '.$1')
      } else if (n == 2) {
        s = s.replace(/,(\d\d)$/, '.$1')
      } else if (n == 1) {
        s = s.replace(/,(\d)$/, '.$1')
      }
    } else {
      s = s.replace(/,(\d)$/, '.$1')
      var dotIdx = (s + '').indexOf('.')
      s = s.substring(0, dotIdx)
    }
    return s + '%'
  } else {
    return ''
  }
}

/**
 * 根据报盘文件设置的货币、百分比的小数规则，获取保留的小数位数
 * */
function parseNumberFormat (code) {
  switch (code) {
    case '10093001':
      return 0
    case '10093002':
      return 1
    case '10093003':
      return 2
    case '10093004':
      return 3
    case '10094001':
      return 0
    case '10094002':
      return 1
    case '10094003':
      return 2
    case '10094004':
      return 3
  }
  return 2
}
/**
 * 根据报盘文件设置的日期，获取格式
 * */
function parseDateFormat (code) {
  switch (code) {
    case '10006005':
      return 'yyyy/MM'
    case '10006004':
      return 'yyyy-MM'
    case '10006006':
      return 'yyyyMM'
    case '10006001':
      return 'yyyy-MM-dd'
    case '10006003':
      return 'yyyy/MM/dd'
    case '10006002':
      return 'yyyyMMdd'
  }
  return 'yyyy-MM-dd'
}
/**
 * 根据报盘文件设置的日期，获取日期控件类型
 * */
function parsePickerType (code) {
  switch (code) {
    case '10092001':
      return 'year'
    case '10092002':
      return 'month'
    case '10092003':
      return 'date'
    case '10092004':
      return 'month'
    case '10092005':
      return 'month'
    case '10092006':
      return 'month'
    case '10092007':
      return 'date'
    case '10092008':
      return 'date'
    case '10092009':
      return 'date'
  }
  return 'date'
}

/**
 *将数字按小数位数解析成带千位符的货币格式
 * */
function parseCurrency (s, n) {
  if (s != null && s != '') {
    s = s.replace(/^(\d*)$/, '$1.')
    if (n > 0) {
      if (n == 3) {
        s = (s + '000').replace(/(\d*\.\d\d\d)\d*/, '$1')
      } else if (n == 2) {
        s = (s + '00').replace(/(\d*\.\d\d)\d*/, '$1')
      } else if (n == 1) {
        s = (s + '0').replace(/(\d*\.\d)\d*/, '$1')
      }
    } else {
      s = (s + '0').replace(/(\d*\.\d)\d*/, '$1')
    }
    s = s.replace('.', ',')
    var re = /(\d)(\d{3},)/
    while (re.test(s)) { s = s.replace(re, '$1,$2') }

    if (n > 0) {
      if (n == 3) {
        s = s.replace(/,(\d\d\d)$/, '.$1')
      } else if (n == 2) {
        s = s.replace(/,(\d\d)$/, '.$1')
      } else if (n == 1) {
        s = s.replace(/,(\d)$/, '.$1')
      }
    } else {
      s = s.replace(/,(\d)$/, '.$1')
      var dotIdx = (s + '').indexOf('.')
      s = s.substring(0, dotIdx)
    }

    // s=s.replace(/,(\d\d)$/,".$1");
    return '￥' + s.replace(/^\./, '0.')
  } else {
    return ''
  }
}

function onlyNumber (val, n, type) {
  if (val == null || val == '') {
    return ''
  }
  val = val.replace(/[^\d\.]/ig, '')
  var dotIdx = val.indexOf('.')
  var dotLeft
  var dotRight
  if (dotIdx === 0) {
    val = '0.'
    return
  }
  if (dotIdx > 0 && val.length >= n) {
    dotLeft = val.substring(0, dotIdx)
    dotRight = val.substring(dotIdx + 1)
    dotLeft = parseInt(dotLeft)
    if (dotRight.length > n) {
      dotRight = dotRight.substring(0, n)
    }
    if (parseInt(dotRight) != 0) {
      dotRight = parseFloat('0.' + dotRight)
    } else {
      dotRight = '.' + dotRight
    }
    val = dotLeft + dotRight
  }
  let res = val
  if (type == 'currency') {
    res = parseCurrency(val + '', n)
  } else if (type == 'percent') {
    res = parsePercent(val + '', n)
  } else {
    res = val + ''
  }
  return res
}

// 社保方案详情-计算公司和个人的固定金额
function calcFixedAmount (type, item) {
  //  type：公司-0， 公司-1
  /* 非年缴的险种，直接空着，也不要显示0
   年缴的险种，按照基数*比例计算出来范围显示，eg：基数范围50~50，比例100%，金额显示50~50，基数范围50~100，比例50%，金额显示25~50
   10008001	每月
   10008002	每季度
   10008003	每半年
   10008004	每年
   10008005	一次性 */

  if (item.payFrequencyCode == '10008004') {
    //  年缴
    let min, max
    if (type == 0) {
      //  公司
      min = accMul(item.compBaseMin, item.compRatioMin)
      max = accMul(item.compBaseMax, item.compRatioMin)
    } else {
      //  个人
      min = accMul(item.empBaseMin, item.empRatioMin)
      max = accMul(item.empBaseMax, item.empRatioMin)
    }
    return min + '~' + max
  } else {
    return ''
  }
}

// 参保系统字典值转化
function parseSystemType (code) {
  switch (code) {
    case '10007001':
      return '社保系统'
    case '10007002':
      return '养老系统'
    case '10007003':
      return '医疗系统'
    case '10007004':
      return '单工伤'
    case '10007005':
      return '工伤系统'
    case '10007006':
      return '备案系统'
    case '10008001':
      return '公积金系统'
  }
  return ''
}
// 处理缴费计划显示
function parsePaymentPlan (item) {
  let systemTypeName = parseSystemType(item.systemType)
  let monthBegin = item.monthBegin === 0 ? '当月' : '下月'
  let monthEnd = item.monthEnd === 0 ? '当月' : '下月'
  let hourBegin = item.hourBegin < 10 ? '0' + item.hourBegin : item.hourBegin
  let hourEnd = item.hourEnd < 10 ? '0' + item.hourEnd : item.hourEnd
  return `${systemTypeName}&nbsp;&nbsp;${monthBegin}${item.dayBegin}日 ${hourBegin}时~${monthEnd}${item.dayEnd}日 ${hourEnd}时`
}

// 处理申报政策显示
function parseDeclarePolicy (list, declareType) {
  let item
  for (let i = 0; i < list.length; i++) {
    item = list[i]
    if (item.declareType === declareType) {
      let monthBegin = item.monthBegin === 0 ? '当月' : item.monthBegin === 1 ? '下月' : '上月'
      let monthEnd = item.monthEnd === 0 ? '当月' : item.monthEnd === 1 ? '下月' : '上月'
      let hourBegin = ''
      if (item.hourBegin == null) {
        hourBegin = '-'
      } else {
        hourBegin = item.hourBegin < 10 ? '0' + item.hourBegin : item.hourBegin
      }
      let hourEnd = ''
      if (item.hourEnd == null) {
        hourEnd = '-'
      } else {
        hourEnd = item.hourEnd < 10 ? '0' + item.hourEnd : item.hourEnd
      }
      let dayBegin = item.dayBegin == null ? '-' : item.dayBegin
      let dayEnd = item.dayEnd == null ? '-' : item.dayEnd
      return `${monthBegin}${dayBegin}日 ${hourBegin}时~${monthEnd}${dayEnd}日 ${hourEnd}时`
    }
  }
  return '-'
}

export {
  getSocialCyleAmount,
  getAccfundAmount,
  toFixedValue,
  accMul,
  calFee,
  accAdd2,
  parsePercent,
  parseNumberFormat,
  parseCurrency,
  onlyNumber,
  parseDateFormat,
  parsePickerType,
  toFixedValueByCode,
  calcFixedAmount,
  parseDeclarePolicy,
  parsePaymentPlan
}
