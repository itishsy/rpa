<template>
  <div>
    <!--element-loading-background="rgba(255, 255, 255, 0.5)"-->
    <div class="pl-20 mb-5 filter-text" ref="filterTags" v-show="filterTags.length>0">
      <span class="text">共筛选出</span><span class="text blue-text">{{total}}</span><span class="text">条数据</span>
      <el-tag class="ml-20 mb-5"
              v-for="(tag,index) in filterTags"
              :key="tag.text+index" size="small"
              closable
              :disable-transitions="true"
              type="info" @close="resetFilter(index)">
        <span v-text="tag.label"></span>
        <span class="tag-value-text" v-text="tag.text"></span>
      </el-tag>
      <el-button type="text" size="small" class="ml-20 mt-0" style="padding: 4px ;" @click="resetFilter(null)">重置</el-button>
    </div>
    <div class="table-div" :style="{height: this.height}" :class="{'cancelMinHeight': this.cancelMinHeight, 'showEmptyAll': this.showEmptyAll&&tableData.length<1}">
      <u-table size="mini" :key="tableKey" class="pal-table" :class="{'pal-table-border': showBorder}" ref="palTable" border style="width: 100%;"
               highlight-current-row
               :height="palTableHeight"
               :row-key="rowKey"
               v-loading="loading"
               element-loading-text="拼命加载中"
               element-loading-spinner="el-icon-loading"
               :data="tableData"
               :default-sort="defaultSort"
               :row-height="rowHeight"
               :use-virtual="useVirtual"
               :show-header="showHeader"
               :show-summary="showSummary"
               :summary-method="getSummaries"
               :row-class-name="tableRowClassNameHandle"
               :row-style="rowStyle"
               @sort-change="sortChange"
               @selection-change="selectionChange"
               @current-change="currentChange"
               @row-click="rowClick"
               @cell-mouse-enter="cellMouseEnter"
               @cell-mouse-leave="cellMouseLeave"
               :span-method="arraySpanMethod"
               >
        <u-table-column type="selection" fixed="left" align="center" width="43" v-if="showSelection"
                        :reserve-selection="reserveSelection" :selectable="selectableFun"></u-table-column>
        <u-table-column fixed="left" label="序号" width="50" align="center" v-if="showIndex">
          <template scope="scope"><span>{{scope.$index+(params.page - 1) * params.limit + 1}} </span></template>
        </u-table-column>
        <u-table-column label="序号" width="75" align="center" v-if="showNotFixedIndex">
          <template scope="scope"><span>{{scope.$index+(params.page - 1) * params.limit + 1}} </span></template>
        </u-table-column>
        <slot></slot>
        <template slot="empty">
          <p></p>
        </template>
      </u-table>
      <div class="empty-box" v-show="tableData.length<1">
        <div class="box">
          <img class="img" src="../../assets/images/empty.png">
          <p class="info">暂无数据信息</p>
        </div>
      </div>
    </div>
    <div class="pal-pagination page-box" v-if="showBottom || paging">
      <div class="btn-group text-left">
        <slot name="pagination-btns"></slot>
      </div>
      <el-pagination style="padding-right:0px;"
        v-if="paging"
        popper-class="pal-pagination"
        @size-change="limitChange"
        @current-change="pageChange"
        :current-page.sync="params.page"
        :page-size="params.limit"
        :page-sizes="pageSizes"
                     align="center"
        layout="slot, pager"
        :total="total">
        <span style="padding-right: 33px;">
          每页
          <el-select v-model="params.limit" @change="limitChange">
            <el-option v-for="(item,index) in pageSizes" :key="index" :label="item" :value="item">
            </el-option>
          </el-select>
          条
        </span>
        <button type="button" :disabled="params.page==1" class="btn-prev" @click="pageChange(params.page - 1)">
          <i class="el-icon el-icon-caret-left" style="font-size:16px;"></i>
        </button>
      </el-pagination>
      <el-pagination style="padding-left: 0px; padding-right:0px;"
        v-if="paging"
        popper-class="pal-pagination"
        @size-change="limitChange"
        @current-change="pageChange"
        :current-page.sync="params.page"
        :page-size="params.limit"
        :page-sizes="pageSizes"
        align="center"
        layout="slot"
        :total="total">
        <button type="button" :disabled="params.page==(Math.ceil(total/params.limit)==0?1:Math.ceil(total/params.limit))" class="btn-next" @click="pageChange(params.page + 1)">
          <i class="el-icon el-icon-caret-right" style="font-size:16px;"></i>
        </button>
      </el-pagination>
      <el-pagination style="padding-left: 0px;"
                     v-if="paging"
                     popper-class="pal-pagination"
                     @size-change="limitChange"
                     @current-change="pageChange"
                     :current-page.sync="params.page"
                     :page-size="params.limit"
                     :page-sizes="pageSizes"
                     align="center"
                     layout="jumper, slot"
                     :total="total">
        <span style="padding-left: 23px;">总数：{{$global.milliFormat(total,0)}}</span>
      </el-pagination>
    </div>
<!--    <div  class="pal-pagination display-flex" v-if="showBottom">
      <div class="flex1 text-left">
        <slot name="bottom-btns"></slot>
      </div>
    </div>-->
  </div>
</template>
<style lang="less">
  .pal-table{
   .el-loading-spinner i{
     font-size: 40px;
     margin-bottom: 10px;
   }
    .el-table__fixed{
      height: calc(100% - 10px)!important;
    }
    .is-scrolling-none + .el-table__fixed{
      height: 100%!important;
    }
 }
 .palTableFixed {
   .pal-table {
     .el-table__fixed {
       height: auto !important; // 此处的important表示优先于element.style
       bottom: 11px; // 改为自动高度后，设置与父容器的底部距离，则高度会动态改变
     }
   }
 }
</style>
<style lang="less" scoped>
  .table-div{
    position: relative;
    .empty-box{
      position: absolute;
      width: 100%;
      height: calc(100% - 70px);
      bottom: 20px;
      text-align: center;
      .box{
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        color: #909399;
      }
    }
  }
  /deep/.pal-table {
    position: relative;
    .el-table-column--selection .cell{
      margin-right: 0;
    }
    thead{
      th{
        border-top: none!important;
        border-bottom: none!important;
        //height: 40px;
        padding: 8px 0;
        box-sizing: border-box;
        vertical-align: top;
      }
    }
  tbody{
    td{
      border-bottom: none!important;
    }
  }
  .el-table__fixed::before, .el-table__fixed-right::before{
    display: none;
  }
  }
  /deep/.pal-table-border {
    .el-table__body-wrapper .el-table__body{
      border-left: 1px solid #dbdbdb!important;
      border-bottom: 1px solid #dbdbdb!important;
    }
    .el-table__fixed-body-wrapper{
      td{
        border-left: 1px solid #dbdbdb!important;
      }
    }
    thead{
      th{
        border-top: 1px solid #dbdbdb!important;
        border-bottom: 1px solid #dbdbdb!important;
        border-right: 1px solid #dbdbdb!important;
      }
      th:first-of-type{
        border-left: 1px solid #dbdbdb!important;
      }
    }
    tbody{
      td{
        border-bottom: 1px solid #dbdbdb!important;
        border-right: 1px solid #dbdbdb!important;
      }
    }
  }
  /deep/.pal-table.el-table::before{
    display: none;
  }
  .pal-pagination{
    text-align: center;
    padding: 30px 15px;
    border: none;
    /*border:1px solid rgba(221, 221, 221, 1);*/
    .el-pagination{
      margin-top: 0;
      font-weight: normal;
      display: inline-block;

    }
  }
  /deep/.el-pagination{
    .el-select{
      .el-input {
        width: 80px;
      }
    }
  }
  .page-box {
    position: relative;
    .btn-group {
      position: absolute;
      top: 29px;
    }
  }
  /deep/ .el-table__fixed-right:before, /deep/ .el-table__fixed:before {
    /*background: none;*/
  }

  /deep/.el-table__fixed-right .el-table__fixed-body-wrapper .el-table--fixed__virtual-wrapper .el-table__body {
    position: absolute;
    right: 0;
  }
  /deep/.el-table--border{
    border-left: none;
  }
  /deep/.el-table--border:after{
    background-color: unset;
  }
  /deep/.el-table:before {
    background-color: unset;
  }

  /deep/.el-pagination__total {
    margin-right: 0px;
    margin-left: 10px;
  }

  /deep/.el-pagination__jump {
    margin-left: 25px;
  }

  /deep/.el-pager {
    margin-top: -2px;
  }

  .empty-box2{
    text-align: center;
    .info{
      font-size: 14px;
      color: #C3C3C3;
      line-height: normal;
    }
  }

  /deep/ .el-table .checked-row {
    background: #ecf5ff !important;
  }

  /deep/ .el-table .unchecked-row {
    td {
      /*background: none !important;*/
    }
  }
  /deep/.filter-text{
    max-height: 150px;
    overflow: auto;
    .text {
      font-size: 14px;
      font-family: Microsoft YaHei;
      font-weight: 400;
      color: #303133
    }
    .blue-text {
      padding: 0 5px;
      color: #3E82FF;
    }

    /deep/.el-tag {
      font-size: 14px;
      font-family: Microsoft YaHei;
      font-weight: 400;
      .tag-value-text {
        color: #303133;
      }
    }
  }
  /deep/.el-table--enable-row-hover .el-table__body tr:hover>td {
    background-color: #f5f7fa !important;
  }
  /deep/.el-table__row.hover-row{
    background-color: #f5f7fa !important;
  }/deep/.el-table__body-wrapper {
     &::-webkit-scrollbar { // 滚动条高 解决行未对齐
       height: 10px;
     }
   }
  /deep/.el-table__fixed-body-wrapper .el-table__body {
    padding-bottom: 10px; // 滚动条高度
  }

  /deep/.el-table__header {
    table-layout: inherit;
  }
  /deep/.cancelMinHeight{
    .pal-table{
      height: auto!important;
      .el-table__body-wrapper, .el-table{
        height: auto!important;
      }
      .cell{
        width: 100%!important;
      }
    }
  }
  /deep/.showEmptyAll{
    .pal-table{
      .el-table__body-wrapper, .el-table{
        min-height: 250px!important;
      }
    }
  }
</style>
<script>
  export default {
    props: {
      // 参数  默认返回分页和条数
      params: {
        type: [Object, String, Number],
        default: function () {
          return {page: 1, limit: 20}
        }
      },

      defaultSort: {
        type: Object,
        default: function () {
          return {
            prop: '',
            order: 'ascending'
          }
        }
      },

      useVirtual: {
        type: Boolean,
        default: true
      },

      rowHeight: {
        type: Number,
        default: 38
      },

      data: {
        type: [Array],
        default: function () {
          return []
        }
      },

      cancelMinHeight: {
        default: false
      },
      showEmptyAll: {
        default: true
      },

      // 分页
      paging: {
        default: true
      },
      // 分页
      showBottom: {
        default: false
      },
      // 多选框
      showSelection: {
        default: false
      },
      clickAndSelect: {
        default: false
      },
      // 序号
      showIndex: {
        default: false
      },
      //是否显示 非固定左边的序号
      showNotFixedIndex:{
        default: false
      },
      tableHeight: {
        default: '600px'
      },

      noMinHeight: {
        default: false
      },
      rowKey: {
        default: ''
      },
      reserveSelection: {
        default: false
      },
      spanMethod: {
        type: Function,
        default: null
      },
      rowStyle: {
        type: Function,
        default: null
      },
      pageSizes: {
        type: [Array],
        default: function () {
          return [10, 20, 50, 100, 500, 1000]
        }
      },

      showSelectionOpt: {
        default: false
      },
      showHeader: {
        default: true
      },
      selectableFun: {
        type: Function,
        default: function () {
          return true
        }
      },
      filterTags: {
        type: [Array],
        default: function () {
          return []
        }
      },
      showSummary: {
        default: false
      },
      getSummaries:{
        type: Function,
        default: function () {
          return []
        }
      },
      customTableRowClassName: {
        type: Function,
       },
      dataKey:{
        type:Object,
        default:function(){
          return {
            rows:'rows',
            total:'records'
          }
        }
      },
      showBorder: {
        type: Boolean,
        default: false
      },
      frontSortArray:{      //需要前端自行排序的字段数组，当使用排序时使用表格自带的拍下。这样不通过后端排序后，页面不会刷新。因此得以保留排序图标的激活状态
        type:Array,
        default:()=>([])
      }
    },
    data() {
      /*
       *表格查询格式
       *{
       "filters": [   表头过滤
       {
       "operator": "string",   模糊or精确
       "property": "string",  字段
       "value": {}   //值
       }
       ],
       "page": 0,   //当前页
       "query": [   //表格查询
       {
       "property": "string",  字段
       "value": {}   值
       }
       ],
       "sidx": "string",   排序字段名
       "size": 0,          每页展示条数
       "sort": "string"    排序方式（升序或降序）
       }
       * */
      return {
        api: '', // 请求接口
        requestMethod: 'post', // 默认post请求
        query: [], // 表格查询条件
        fetchPaging: '', // fetch传入的paging参数
        tableData: [], // 表格数据
        tableDataRecord: [], // 保存表格数据，用于前端分页时
        total: 0, // 总条数
        loading: false, // loading动画
        requestParams: {},
        callback: null,
        successback: null,
        errorback: null,
        selections: [],
        height: '',
        palTableHeight: '',
        palTableFixed: false,
        sortCol: null,
        palSortFieldObj: {},
        tableKey: this.$global.UUID()
      }
    },
    watch: {
      data: function () {
        if (this.data) {
          this.tableData = this.data
        }
      },
      tableHeight: function () {
        this.watchTableHeight()
      },
      // filterTags: function (nvl, ovl) {
      /*  this.$nextTick(() => {
          let filterTagsHeight = this.$refs.filterTags.offsetHeight
          this.palTableHeight = (parseInt(this.tableHeight.replace('px', '')) - filterTagsHeight) + 'px'
        })*/
      // }
    },
    created() {
      this.watchTableHeight()

      //    console.log(JSON.stringify(this.data))
      if (this.data) {
        this.tableData = this.data
      } else {
        this.tableData = []
      }
    },
    updated() {
      if (this.$refs.palTable.$el.classList.value.indexOf('el-table--scrollable-x') >= 0) {
        this.palTableFixed = true
      } else {
        this.palTableFixed = false
      }
    },
    computed: {
      // 实时更新server
      // server: function () {
      //   return this.api.split('.')[0]
      // },
      // 实时更新url
      // url: function () {
      //   return this.api.split('.')[1]
      // },
    },
    methods: {
      // query, method, url,paging, callback, successback, errorback, pageChange
      // query--查询参数
      // method--请求方式,
      // url--接口,
      // noSort--不传排序字段sidx和sort,
      // custSort--自定义排序字段跟排序方式（即：不根据表单列点击排序）,
      // paging--判断是否在前端分页, 不传默认后台分页，传'front'则在前端分页
      // callback--处理完表格数据后的回调，successback--直接回调（需要自行处理表格数据） errorback--错误回调
      // pageChange  // 忽略
      fetch(obj) {
        //  this.loading = true
        //   {
        //     "filters": [
        //     {
        //       "operator": "string",
        //       "property": "string",
        //       "value": {}
        //     }
        //   ],
        //     "page": 0,
        //     "query": [
        //     {
        //       "property": "string",
        //       "value": {}
        //     }
        //   ],
        //     "sidx": "string",
        //     "size": 0,
        //     "sort": "string"
        //   }
        this.loading = true
        // 如果有分页, 执行此方法重置分页为1，如果是分页触发才不用改变
        if (obj.pageChange !== 'change') {
          if (this.paging) {
            this.params.page = 1
          }
          if (obj.pageChange == 'limitChange' && this.fetchPaging == 'front') {
            //  在前端分页
            this.frontPagingHandle()
            return
          }
        } else {
          if (this.fetchPaging == 'front') {
            //  在前端分页
            this.frontPagingHandle()
            return
          }

        }

        if (obj.url) {
          this.api = obj.url
        }
        if (obj.query) {
          this.query = obj.query
        }
        if (obj.paging) {
          this.fetchPaging = obj.paging
        }
        if (obj.method) {
          this.requestMethod = obj.method
        }
        if (obj.callback) {
          this.callback = obj.callback
        }
        if (obj.successback) {
          this.successback = obj.successback
        }
        if (obj.errorback) {
          this.errorback = obj.errorback
        }
        if(obj.custSort){
          this.palSortFieldObj = obj.sortFieldObj
        }

        var httpJson = {
          url: this.api,
          method: this.requestMethod
        }

        var httpParams = {
          page: this.params.page,
          start: (this.params.page - 1) * this.params.limit,
          size: this.params.limit,
          query: this.query
        }

        if (!obj.noSort) {
          if (this.sortCol != null && this.sortCol.prop != null && this.sortCol.order != null) {
            httpParams.sidx = this.sortCol.column.sortable == true ? this.sortCol.prop : this.sortCol.column.sortable
            httpParams.sort = this.sortCol.order === 'ascending' ? 'asc' : 'desc'
          } else {
            httpParams.sidx = ''
            httpParams.sort = ''
          }
        }

        if(this.palSortFieldObj.sidx){
          httpParams.sidx = this.palSortFieldObj.sidx
          httpParams.sort = this.palSortFieldObj.sort
        }

        this.requestParams = httpParams
        if (this.requestMethod === 'get' || this.requestMethod === 'GET') {
          httpJson.params = httpParams
        } else if (this.requestMethod === 'post' || this.requestMethod === 'POST') {
          httpJson.data = httpParams
        }
        this.$http(httpJson).then(({data}) => {
          var res = data.data
          this.loading = false
          this.cancelSelection()
          if (this.successback) {
            this.successback(res)
          } else {
            this.tableDataRecord = this.$lodash.get(res,this.dataKey.rows) || []
            if (obj.paging == 'front') {
              //  在前端分页
              this.frontPagingHandle()
            } else {
              this.tableData =this.$lodash.get(res,this.dataKey.rows) || []
              // 如果有分页
            }
            if (this.paging) {
              this.total = this.$lodash.get(res,this.dataKey.total) || 0
              // this.params.page = data.curr || 0
            }
            this.tableKey = this.$global.UUID()
            if (this.callback) {
              this.callback(res)
            }
          }
        }).catch((data) => {
          this.loading = false
          if (this.errorback) {
            this.errorback(data)
          } else {
            // this.$message({
            //   message: '系统异常',
            //   type: 'error'
            // })
          }
        })
      },

      frontPagingHandle() {
        let page = this.params.page
        let limit = this.params.limit
        let start = (page - 1) * limit
        let end = start + limit
        let data = this.tableDataRecord.slice(start, end)
        this.tableData = data
        this.loading = false
      },

      init(params) {
        this.loading = true
        // 如果采用微服务的方式需要传微服务和url
        this.$http({
          url: 'http://192.168.0.68:3003/api/cust/countCustByType/detail/2019?page=1&results=10&',
          method: this.method ? this.method : 'get',
          params: params
        }).then(({data}) => {
          this.tableData = data.data[0] || []
          // 如果有分页
          if (this.paging) {
            this.total = data.count || 0
            this.params.page = data.curr || 0
          }
          this.loading = false
        })
      },
      // 重新请求 //如果需要重新请求使用$refs 调用这个方法
      reload() {
        // 如果有分页
        if (this.paging) {
          this.params.page = 1
        }
        // api动态加载完 开始重新请求数据
        this.$nextTick(() => {
          this.fetch({})
        })
      },
      // 判断表格数据是否为空
      isEmptyTable() {
        if (this.tableData.length > 0) {
          return false
        } else {
          return true
        }
      },
      // 重新请求 //如果需要重新请求使用$refs 调用这个方法
      emptyTable() {
        this.tableData = []
        this.tableDataRecord = []
      },
      // 以下是对el-table原来的方法再次封装emit出去
      // 多选
      selectionChange(val) {
        this.selections = val
        this.$emit('selection-change', val)
      },
      // 单选
      currentChange(currentRow, oldCurrentRow) {
        this.$emit('current-change', currentRow, oldCurrentRow)
      },
      rowClick(row, event, column) {
        this.$emit('row-click', row, event, column)
        if (this.clickAndSelect) {
          this.$refs.palTable.toggleRowSelection(row)
        }
      },
      cellMouseEnter(row, column, cell, event) {
        this.$emit('cell-mouse-enter', row, column, cell, event)
      },
      cellMouseLeave(row, column, cell, event) {
        this.$emit('cell-mouse-leave', row, column, cell, event)
      },
      // 排序
      sortChange(column, prop, order) {
        if (column.prop === this.defaultSort.prop && column.order === this.defaultSort.order) {
          return
        }
        this.sortCol = column
        if(this.frontSortArray.includes(column.prop)){
          //当为需要前端自行排序的字段时，不执行以下后端请求
          this.$emit('sort-change', column, prop, order)
          return
        }
        let that = this
        this.fetch({pageChange: 'change', callback: function(res) {
            that.defaultSort = {
              prop:column.prop,
              order:column.order
            }
        }})
      },
      // 表格翻页
      pageChange(page) {
        this.params.page = page
        this.fetch({pageChange: 'change'})
      },
      limitChange(limit) {
        this.params.limit = limit
        this.fetch({pageChange: 'limitChange'})
      },
      arraySpanMethod({row, column, rowIndex, columnIndex}) {
        if (this.spanMethod) {
          return this.spanMethod({row, column, rowIndex, columnIndex})
        }
      },
      getParamsObj() {
        var obj = {}
        this.requestParams.query.forEach(item => {
          obj[item.property] = item.value
        })
        return obj
      },
      getParamsQuery() {
        return this.requestParams.query
      },
      clearSort() {
        this.$refs.palTable.clearSort()
        this.sortCol = null
        this.fetch({pageChange: 'change'})
      },
      clearSelection() {
        this.$refs.palTable.clearSelection()
      },
      getSelectionsArr(field) {
        //      console.log(field)
        if (!field) {
          return false
        }
        var arr = []

        for (var i = 0; i < this.selections.length; i++) {
          arr.push(this.selections[i][field])
        }
        //      console.log(arr)
        return arr
      },
      selectRow(row, selected) {
        if (this.showSelection) {
          this.$refs.palTable.toggleRowSelection([{row: row, selected: selected}])
        } else {
          this.$refs.palTable.setCurrentRow(row)
        }
      },
      selectRows(selectArray) {
        if (this.showSelection) {
          this.$refs.palTable.toggleRowSelection(selectArray)
        }
      },
      watchTableHeight() {
        if (!this.cancelMinHeight) {
          if (this.tableHeight.replace('px', '') < 260) {
            if (this.noMinHeight) {
              this.height = this.tableHeight
              this.palTableHeight = this.tableHeight
            } else {
              this.height = '260px'
              this.palTableHeight = '260px'
            }
          } else {
            this.height = this.tableHeight
            this.palTableHeight = this.tableHeight
          }
        } else {
          //  不设置最小高度，为了某些静态表
          this.height = 'auto'
          this.palTableHeight = '0'
        }
      },
      // tableRowClassName({row, rowIndex}) {
      //   if (this.showSelection) {
      //     if (this.selections.indexOf(row) > -1) {
      //       return 'checked-row'
      //     }
      //     return 'unchecked-row'
      //   }
      //   return ''
      // },
      tableRowClassNameHandle({ row, rowIndex }) {
      if (typeof this.customTableRowClassName === 'function') {
        return this.customTableRowClassName({ row, rowIndex })
      }
      if (this.showSelection) {
        if (this.selections.indexOf(row) > -1) {
          return 'checked-row'
        }
        return 'unchecked-row'
      }
      return ''
    },
      // 取消选择
      cancelSelection (row) {
        this.$refs.palTable.clearSelection();
      },

      doLayout(){
        this.$refs.palTable.doLayout();
      },
      resetFilter(index) {
        let filterTags = this.$global.deepcopyArray(this.filterTags)
        if (this.$global.isNotBlank(index)) {
          filterTags.splice(index, 1)
        } else {
          filterTags = []
        }
        this.$emit('resetFilter', index, filterTags)
      }
    }
  }
</script>
