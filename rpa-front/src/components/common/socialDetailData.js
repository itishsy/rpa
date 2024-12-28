var data = {
  social: [
    { name: '姓名', code: 'employeeName' },
    { name: '证件类型', code: 'cardType' },
    { name: '证件号码', code: 'idCard' },
    { name: '申报类型', code: 'changeType', format: 'handleChangeType(this.getData.changeType)' },
    { name: '参保城市', code: 'addrName' },
    { name: '单位社保号', code: 'companyNameAndAccountNumber' },
    { name: '', nameCode: 'changeType', formatName: "getBusinessStrTime(this.getData.changeType)", code: 'strInsuredDate'},
    { name: '参保险种', code: 'sameStatusName',format: 'getProductNameList(this.getData.sameStatusName)'},
    { name: '缴纳基数', code: 'roundEmpTbBase',format: 'toFixed(this.getData.roundEmpTbBase)'},
    // { name: '创建日期', code: 'createTime', format: '$moment(this.getData.createTime).format("YYYY-MM-DD HH:mm")' },
    // { name: '创建人', code: 'createName' },
  ],
  accfund: [
    { name: '姓名', code: 'employeeName' },
    { name: '证件类型', code: 'cardType' },
    { name: '证件号码', code: 'idCard' },
    { name: '申报类型', code: 'changeType', format: 'handleChangeType(this.getData.changeType)' },
    { name: '参保城市', code: 'addrName' },
    { name: '单位公积金号', code: 'companyNameAndAccountNumber' },
    { name: '参保比例', code: 'ratios', format: 'handleAccfundRatios(this.getData.ratios)' },
    { name: '', nameCode: 'changeType', formatName: "getBusinessStrTime(this.getData.changeType)", code: 'strInsuredDate' },
    { name: '缴纳基数', code: 'roundEmpTbBase',format: 'toFixed(this.getData.roundEmpTbBase)'},
    // { name: '申报状态', code: 'status', format: 'handleDeclareStatus(this.getData.status)' },
    // { name: '原因', code: 'comment' },
    // { name: '创建日期', code: 'createTime', format: '$moment(this.getData.createTime).format("YYYY-MM-DD HH:mm")' },
    // { name: '创建人', code: 'createName' }
  ],
  errorTable: [
    { prop: "sameStatusName", label: '险种', width: '150', fixed: true,formatFn: 'getProductNameList', parameter: 'sameStatusName', format: 'fn'},
    { prop: "status", label: '申报状态', formatFn: 'handleDeclareStatus', parameter: 'status', format: 'fn'},
    /*{ prop: "robotExecStatus", label: '执行状态', formatFn: 'handleRobotExecStatus', parameter: 'robotExecStatus', format: 'fn'},*/
    { prop: "failCase", label: '原因', },
  ],
  logTable: [
    { prop: "processTypeStr", label: '操作', fixed: true },
    { prop: "tplTypeName", label: '申报系统', fixed: true },
    { prop: "processComment", label: '申报结果' },
    { prop: "remarks", label: '原因备注' },
    { prop: "nodeComment", label: '节点标签' },
    { prop: "operatorName", label: '操作人' },
    { prop: "createTime", label: '操作时间', parameter: 'createTime', formatDate: 'YYYY-MM-DD HH:mm:ss', format: 'date' },
  ],
  computed: {
    tableHeight2: function () {
      return this.$global.bodyHeight - 190 + 'px'
    },
    getBusinessStrTime() {
      return function (code) {
        switch (code) {
          case 1:
            return '参保时间'
          case 2:
            return '减员时间'
          case 3:
            return '调基时间'
          case 5:
            return '补缴时间'
        }
        return ''
      }
    },
    handleDeclareStatus() {
      //0作废，1待申报，2申报中，3部分成功，4申报成功，5申报失败，6待提交
      return function (code) {
        switch (code) {
          case 0:
            return '作废'
          case 1:
            return '待申报'
          case 2:
            return '申报中'
          case 3:
            return '申报失败(部分)'
          case 4:
            return '申报成功'
          case 5:
            return '申报失败'
          case 6:
            return '待提交'
        }
        return ''
      }

    },
    handleRobotExecStatus() {
      return function (code, row) {
        if (row.status == 2 && code ==1 && row.failCase) {
          return '执行中断'
        }

        if (row.status == 2 && code ==1 && !row.failCase) {
          return '待社保局审核'
        }

        if (row.status == 4 || row.status ==5) {
          return "执行完毕"
        }
        return ""
      }
    },
    handleChangeType() {
      //  <!--变更类型（1增，2减，3调基，5补缴）-->
      // {id: '1', name: '投保'}, {id: '3', name: '调整'}, {id: '2', name: '停保'}, {id: '5', name: '补缴'}
      return function (code) {
        switch (code) {
          case 1:
            return '增员'
          case 2:
            return '减员'
          case 3:
            return '调基'
          case 5:
            return '补缴'
        }
        return ''
      }

    },
    handleAccfundRatios() {
      return function (val) {
        if (val === '' || val == null || val == undefined) {
          return ''
        }
        return val.replace(/<br\/>/g, '，')
      }
    },
    //其他，直接书写代码，直接运行
    formatFN() {
      return function (code) {
        return eval(`this.${code}`)
      }
    },
    //表格 format
    formatFN2() {
      return function (fn, val, key) {
        return this[fn](val[key], val)
      }
    },
    //表格 format 类型为date
    formatDate() {
      return function (val, key, format) {
        return this.$moment(val[key]).format(format||'YYYY-MM-DD')
      }
    },
    //表格用
    format() {
      return function (item, obj) {
        if (item.format == 'date') {
          return this.formatDate(obj, item.parameter, item.formatDate)
        } else {
          return this.formatFN2(item.formatFn, obj, item.parameter)
        }
      }
    },
    //排序表格中的 参保险种字段的 顺序
    getProductNameList() {
      return function (nameList) {
        if(!nameList){
          return ''
        }
        var arr = []
        var productNameList = nameList.split('|')
        this.getInsuranceList().forEach((item) => {
          productNameList.forEach((item2) => {
            if (item.dictName == item2) {
              arr.push(item2)
            }
          })
        })
        return arr.join('|').trim() || nameList
      }
    },
    toFixed(){
      return function(num){
        if(num == undefined || num == null || num == ''){
          return num
        }
        if(Number(num) != Number(num)){
          return num
        }
        return Number(num).toFixed(2)
      }
    }
  },

}

export default data
