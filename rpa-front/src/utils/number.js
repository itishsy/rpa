/* 加法： accAdd(0.1, 0.2)  // 得到结果：0.3
减法： accSub(1, 0.9)    // 得到结果：0.1
除法： accDiv(2.2, 100)  // 得到结果：0.022
乘法： accMul(7, 0.8)    // 得到结果：5.6

countDecimals()方法：计算小数位的长度
convertToInt()方法：将小数转成整数
getCorrectResult()方法：确认我们的计算结果无误，以防万一
———————————————— */
var accAdd = function (num1, num2) {
  num1 = Number(num1)
  num2 = Number(num2)
  var dec1, dec2, times
  try { dec1 = countDecimals(num1) + 1 } catch (e) { dec1 = 0 }
  try { dec2 = countDecimals(num2) + 1 } catch (e) { dec2 = 0 }
  times = Math.pow(10, Math.max(dec1, dec2))
  // var result = (num1 * times + num2 * times) / times;
  var result = (accMul(num1, times) + accMul(num2, times)) / times
  return getCorrectResult('add', num1, num2, result)
  // return result;
}

var accSub = function (num1, num2) {
  num1 = Number(num1)
  num2 = Number(num2)
  var dec1, dec2, times
  try { dec1 = countDecimals(num1) + 1 } catch (e) { dec1 = 0 }
  try { dec2 = countDecimals(num2) + 1 } catch (e) { dec2 = 0 }
  times = Math.pow(10, Math.max(dec1, dec2))
  // var result = Number(((num1 * times - num2 * times) / times);
  var result = Number((accMul(num1, times) - accMul(num2, times)) / times)
  return getCorrectResult('sub', num1, num2, result)
  // return result;
}

var accDiv = function (num1, num2) {
  num1 = Number(num1)
  num2 = Number(num2)
  var t1 = 0; var t2 = 0; var dec1; var dec2
  try { t1 = countDecimals(num1) } catch (e) { }
  try { t2 = countDecimals(num2) } catch (e) { }
  dec1 = convertToInt(num1)
  dec2 = convertToInt(num2)
  var result = accMul((dec1 / dec2), Math.pow(10, t2 - t1))
  return getCorrectResult('div', num1, num2, result)
  // return result;
}

var accMul = function (num1, num2) {
  num1 = Number(num1)
  num2 = Number(num2)
  var times = 0; var s1 = num1.toString(); var s2 = num2.toString()
  try { times += countDecimals(s1) } catch (e) { }
  try { times += countDecimals(s2) } catch (e) { }
  var result = convertToInt(s1) * convertToInt(s2) / Math.pow(10, times)
  return getCorrectResult('mul', num1, num2, result)
  // return result;
}

var countDecimals = function (num) {
  var len = 0
  try {
    num = Number(num)
    var str = num.toString().toUpperCase()
    if (str.split('E').length === 2) { // scientific notation
      var isDecimal = false
      if (str.split('.').length === 2) {
        str = str.split('.')[1]
        if (parseInt(str.split('E')[0]) !== 0) {
          isDecimal = true
        }
      }
      let x = str.split('E')
      if (isDecimal) {
        len = x[0].length
      }
      len -= parseInt(x[1])
    } else if (str.split('.').length === 2) { // decimal
      if (parseInt(str.split('.')[1]) !== 0) {
        len = str.split('.')[1].length
      }
    }
  } catch (e) {
    throw e
  } finally {
    if (isNaN(len) || len < 0) {
      len = 0
    }
    return len
  }
}

var convertToInt = function (num) {
  num = Number(num)
  var newNum = num
  var times = countDecimals(num)
  var temp_num = num.toString().toUpperCase()
  if (temp_num.split('E').length === 2) {
    newNum = Math.round(num * Math.pow(10, times))
  } else {
    newNum = Number(temp_num.replace('.', ''))
  }
  return newNum
}

var getCorrectResult = function (type, num1, num2, result) {
  var temp_result = 0
  switch (type) {
    case 'add':
      temp_result = num1 + num2
      break
    case 'sub':
      temp_result = num1 - num2
      break
    case 'div':
      temp_result = num1 / num2
      break
    case 'mul':
      temp_result = num1 * num2
      break
  }
  if (Math.abs(result - temp_result) > 1) {
    return temp_result
  }
  return result
}

var $toFixed = function (numVal, length) {
  var s = numVal + ''
  var len = length || 0
  var result = s
  var arr = s.split('.')

  // 整数
  if (arr.length < 2) {
    if (len > 0) {
      result += '.'
      for (var i = 1; i <= len; i++) {
        result += '0'
      }
    }
    return result
  }

  // 小数
  var first = arr[0]
  var second = arr[1]

  // 小数点位数等于length
  if (second.length == len) {
    return result
  }

  // 小数点位数小于length
  if (second.length < len) {
    for (var k = 1; k <= len - second.length; k++) {
      result += '0'
    }
    return result
  }

  // 小数点位数大于length
  result = first + second.substr(0, len + 1)
  var last = Math.floor((result * 1 + 5) / 10)
  result = (last / Math.pow(10, len)).toFixed(len)
  return result
}

export {
  accAdd,
  accSub,
  accDiv,
  accMul,
  countDecimals,
  convertToInt,
  getCorrectResult,
  $toFixed
}
