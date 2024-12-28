/**
 * @author  ZhenShijun
 * @dateTime 2021-01-20 15:25:50
 */
export default {
  positiveNumberAndZero: {
    inserted (el, binding, vnode) {
      el.oninput = function (e) {
        setTimeout(function () {
          let value = e.target.value
          var reg = /^([0]|[1-9][0-9]*)$/
          if (reg.test(value)) {
            e.target.value = value
            binding.value.set[binding.value.name] = value
          } else {
            if (value.indexOf('.') !== -1) {
              e.target.value = value.substring(0, value.indexOf('.'))
              binding.value.set[binding.value.name] = value.substring(0, value.indexOf('.'))
            } else {
              if (/^([0]{2})$/.test(value)) {
                e.target.value = '0'
                binding.value.set[binding.value.name] = '0'
              } else if (/^([0][1-9]+)$/.test(value)) {
                e.target.value = value.substring(1, value.length)
                binding.value.set[binding.value.name] = value.substring(1, value.length)
              } else {
                e.target.value = ''
                binding.value.set[binding.value.name] = ''
              }
            }
          }
        }, 200)
      }
    }
  },
  positiveNumber: {
    inserted (el, binding, vnode) {
      el.oninput = function (e) {
        let value = e.target.value
        var reg = /^([1-9][0-9]*)$/
        if (reg.test(value)) {
          e.target.value = value
          binding.value.set[binding.value.name] = value
        } else {
          if (value.indexOf('.') !== -1) {
            e.target.value = value.substring(0, value.indexOf('.'))
            binding.value.set[binding.value.name] = value.substring(0, value.indexOf('.'))
          } else {
            if (/^([0]+)$/.test(value)) {
              e.target.value = ''
              binding.value.set[binding.value.name] = ''
            } else if (/^([0][1-9]+)$/.test(value)) {
              e.target.value = value.substring(1, value.length)
              binding.value.set[binding.value.name] = value.substring(1, value.length)
            } else {
              e.target.value = ''
              binding.value.set[binding.value.name] = ''
            }
          }
        }
      }
    }
  },
  filterSpecialChar:{
    update: function (el, { value, modifiers }, vnode) {
            try {
               let a_el=el.children[0]
                    if(!a_el.value){
                       return false; 
                    }
                    if(/[^a-zA-Z0-9\u4E00-\u9FA5$><=#@%]/g.test(a_el.value)){
                      a_el.value = a_el.value.replace(/[^a-zA-Z0-9\u4E00-\u9FA5$><=#@%]/g, "");
                      a_el.dispatchEvent(new Event(modifiers.lazy ? 'change' : 'input'))
                    }
            } catch (e) {
              conosle.log(e)
            }
    }
  },
  //去掉特殊符号，！@#￥%……&*（）
  filterSymbols:{
    update: function (el, { value, modifiers }, vnode) {
      try {
         let a_el=el.children[0]
              if(!a_el.value){
                 return false; 
              }
              if(/[!$#%^&*()]/g.test(a_el.value)){
                a_el.value = a_el.value.replace(/[!#$%^&*()]/g, "");
                a_el.dispatchEvent(new Event(modifiers.lazy ? 'change' : 'input'))
              }
      } catch (e) {
        conosle.log(e)
      }
}
  }
}
