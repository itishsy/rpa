<template>
  <!-- 报盘文件设置 -->
  <div class="spl-container">
    <breadcrumb :data="pathData">
      <!-- <el-button type="primary" slot="breadcrumb-btn" size="mini" @click="goAdd">新增</el-button> -->
    </breadcrumb>
    <div class="pl-20 pr-20">
      <el-row>
        <el-col :span="12">
          <div class="search-row display-flex">
            <p style="margin-right:20px;">
              成功：
              <span
                style="color:#67c23a;"
              >{{uploadData.successCount !== '' ? uploadData.successCount : 0}}条</span>
            </p>
            <p>
              失败：
              <span
                style="color:#FD6154;"
              >{{uploadData.failCount !== '' ? uploadData.failCount : 0}}条</span>
            </p>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="search-row display-flex" style="justify-content: flex-end;padding:10px;">
            <el-button
              type="primary"
              size="mini"
              @click="downloadTpl"
              plain
              style="margin:0 5px;"
            >
							<a href="static/初始化申报账号模板.xlsx" download="初始化申报账号模板.xlsx" style="color:#3E82FF">下载模板</a>
						</el-button>
            <el-upload
              class="upload-demo"
              :before-upload="beforeUpload"
              :http-request="upload"
              action
              :show-file-list="false"
            >
              <el-button size="small" type="primary" style="margin:0 5px;">导入</el-button>
            </el-upload>
            <el-button type="primary" size="mini" @click="save" style="margin:0 5px;">保存</el-button>
          </div>
        </el-col>
      </el-row>
      <div>
        <dTable
          @fetch="getTableData"
          ref="table"
          style="margin-top: 0"
          :tableHeight="tableHeight"
          :showNotFixedIndex="true"
          :showSelection="false"
          row-key="id"
          row-id="id"
					:dataKey="{rows:'content',total:'totalElements'}"
        >
          <u-table-column
            prop="bussinssType"
            label="结果"
            width="300"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          >
						<template slot-scope="scope">
							<p v-if="scope.row.aiPersonMessageDTO.status == 1" style="color:#67c23a;">成功</p>
              <p v-else style="white-space: pre-wrap;color:#fd6154">{{ getError(scope.row.aiPersonMessageDTO) }}</p>
            </template>
					</u-table-column>
          <u-table-column
            prop="customerName"
            label="客户名称"
            width="200"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          ></u-table-column>
          <u-table-column
            prop="insuredName"
            label="参保主体"
            width="100"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          >
          </u-table-column>
          <u-table-column
            prop="businessName"
            label="业务类型"
            header-align="center"
            align="center"
            width="100"
            :show-overflow-tooltip="true"
          ></u-table-column>
          <u-table-column
            prop="declareUnit"
            label="申报单位"
            width="200"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          >
          </u-table-column>
          <u-table-column
            prop="declareAccount"
            label="申报账号"
            header-align="center"
            align="center"
            :show-overflow-tooltip="true"
          >
          </u-table-column>
        </dTable>
      </div>
    </div>
  </div>
</template>
    <script>
import dTable from '../../../components/common/table'
export default {
  components: { dTable },
  data() {
    return {
      formData: {},
      dialogVisible: false,
      pathData: [],
      options: [],
      rowData: {},
      loading: null,
      uuid: '',
      uploadData: {
        successCount: '',
        failCount: '',
				imported:false
      },
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 270 + 'px'
    },
    getProductNameList() {
      return function (row) {
        var arr = []
        var productNameList = row.productName.split('|')
        this.options.forEach((item) => {
          productNameList.forEach((item2) => {
            if (item.dictName == item2) {
              arr.push(item2)
            }
          })
        })
        return arr.join('|')
      }
    },
		getError(){
			return function (data){
				var str = ''
				if(data.status == 1){
					str = '成功'
				}else{
					str = '失败：\n'
					data.message.forEach((item,index)=>{
						str += `${index + 1}、${item}\n` 
					})
				}
				return str
			}
		}
  },
  created() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.deepCopy(tabs[this.$route.meta.parentPath])
      this.pathData.push({ name: '初始化申报账号', path: '/basicConfig/initDeclareAccount' })
      this.pathData.push({ name: '导入' })
    }
    this.$nextTick(() => {
      // this.getTableData()
    })
  },
  mounted() {},
  methods: {
    //列表查询
    async getTableData(obj, pageChange, callback) {
      var params = [{ property: 'uuid', value: this.uuid }]
      var self = this
      this.$refs.table.fetch({
        pageChange: pageChange ? pageChange : '',
        query: params,
        method: 'post',
        url: '/policy/declareAccount/queryAiPersonCache',
        callback: callback
          ? callback
          : function (res) {
              setTimeout(() => {
                self.$refs.table.doLayout()
              }, 2000)
            },
      })
    },
    changeBussinssType() {
      this.getTableData()
    },
    showLoading(target) {
      this.loading = this.$loading({
        target: target ? target : document.body,
        lock: true,
        text: '加载中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)',
      })
    },
    closeLoading() {
      if (this.loading.close) {
        this.loading.close()
      }
    },
    //获取字典值
    async getDictByKey() {
      return this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: '10003',
        },
      })
        .then((res) => {
          this.options = res.data.data
        })
        .catch((err) => {
          this.$message.error('字典接口出错，请稍后再试')
        })
    },
    //深克隆
    deepCopy(obj) {
      var result = Array.isArray(obj) ? [] : {}
      for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
          if (typeof obj[key] === 'object' && obj[key] !== null) {
            result[key] = this.deepCopy(obj[key]) //递归复制
          } else {
            result[key] = obj[key]
          }
        }
      }
      return result
    },
    save() {
			if(this.uploadData.imported && this.uploadData.successCount <= 0){
				this.$message.warning('未有成功条数，请重新导入')
        return
			}
      if (!this.uploadData.imported) {
        this.$message.warning('请先导入表格')
        return
      }
      this.showLoading()
      this.$http({
        url: 'policy/declareAccount/addAiPerson',
        method: 'post',
        data: {
          uuid: this.uuid,
        },
      })
        .then((res) => {
          this.$confirm(`<p style="text-align:center">已保存 ${this.uploadData.successCount} 条成功数据</p>`, '', {
            confirmButtonText: '继续设置',
            cancelButtonText: '返回',
            center: true,
            dangerouslyUseHTMLString: true,
            type: 'success',
          })
            .then(() => {
              // this.requestInfo()
							this.uuid = ''
							this.getTableData()
							this.uploadData.successCount = ''
							this.uploadData.failCount = ''
							this.uploadData.imported = false
            })
            .catch(() => {
              this.$router.back()
            })
          this.dialogVisible = false
          this.closeLoading()
        })
        .catch((err) => {
          this.closeLoading()
        })
    },
    importFile() {},
    downloadTpl() {},
    beforeUpload() {},
    upload(file) {
      let formData = new FormData()
      formData.set('file', file.file)
      this.showLoading()
      this.$http({
        url: 'policy/declareAccount/aiAccountImport',
        method: 'post',
        data: formData,
      })
        .then((res) => {
          this.closeLoading()
          this.uuid = res.data.data.uuid
          this.getTableData()
          this.queryAiPersonCacheCount()
        })
        .catch((err) => {
          this.closeLoading()
        })
    },
    queryAiPersonCacheCount() {
      this.showLoading()
      this.$http({
        url: 'policy/declareAccount/queryAiPersonCacheCount?uuid=' + this.uuid,
        method: 'post',
      })
        .then((res) => {
          this.closeLoading()
          this.uploadData = {
            successCount: res.data.data.successCount,
            failCount: res.data.data.failCount,
						imported:true
          }
        })
        .catch((err) => {
          this.closeLoading()
        })
    },
  },
}
</script>
    <style lang="less" scoped>
/deep/.el-dialog__header {
  padding: 10px 20px;
}
/deep/.el-dialog__body {
  padding: 10px 10px;
}
/deep/.el-form-item__content {
  line-height: 0px;
}
/deep/.el-drawer__body {
  padding-bottom: 0;
}
/deep/.el-dialog__header {
  border-bottom: none !important;
}

.file-content-box {
  padding: 20px 20px;
  .file-list {
    display: flex;
    padding: 10px 0;
    .file {
      background: #f1f8ff;
      border-radius: 10px;
      padding: 10px;
      position: relative;
      cursor: pointer;
      margin-right: 15px;
      &:hover {
        color: #3e82ff;
        text-decoration: underline;
      }
      .file-remove {
        position: absolute;
        right: -10px;
        top: -10px;
        width: 20px;
        height: 20px;
        background: red;
        border-radius: 50%;
        display: none;
        &::before {
          content: '';
          display: inline-block;
          width: 10px;
          height: 2px;
          background: white;
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
        }
      }
      &:hover .file-remove {
        display: block;
      }
    }
  }
  .upload-tips {
    margin-top: 20px;
    font-size: 12px;
    color: #797979;
  }
}
.tag {
  display: inline-block;
  padding: 0 15px;
  height: 30px;
  line-height: 30px;
  background: rgba(255, 255, 255, 1);
  border: 1px solid rgba(206, 206, 206, 1);
  border-radius: 0px 2px 2px 0px;
  margin-right: 10px;
  cursor: pointer;
}
.tag:hover,
.tag.active {
  background-color: #dddddd;
}
.table-fileName-list {
  cursor: pointer;
  &:hover {
    color: #3e82ff;
    text-decoration: underline;
  }
}
.text-blue {
  &:hover {
    text-decoration: underline;
  }
}
</style>
    <style scoped>
body .el-table th.gutter {
  display: table-cell !important;
}
.error {
  margin-top: 10px;
  /* position: absolute; */
  left: 0;
  /* top: 120%; */
  color: #f56c6c;
  font-size: 12px;
}
</style>
    