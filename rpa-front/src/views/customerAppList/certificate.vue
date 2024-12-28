<template>
  <!-- 客户应用列表  -->
  <div class="spl-container">
    <div>
      <breadcrumb :data="pathData">
      </breadcrumb>
      <div class="pl-20 pr-20">
        <el-row class="row-fa">
          <el-col :span="10">
            <div class="search-row display-flex">
              <el-select v-model="formData.appCode" placeholder="请选择应用" class="w-300" clearable @change="changeBussinssType" value-key="appCode" filterable>
                <el-option v-for="item in appList" :key="item.appCode" :label="item.appName" :value="item.appCode"></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="button-add pt-10">
              <el-button
                size="small"
                type="primary"
                style="margin: 10px"
                @click="addCertificate()"
              >添加证书
              </el-button>
            </div>
          </el-col>
        </el-row>
        <div>
          <dTable
            @fetch="getTableData"
            ref="table"
            style="margin-top: 0"
            :tableHeight="tableHeight"
            :showIndex="true"
            :showSelection="false"
            :paging="true"
            row-key="id"
            row-id="id"
          >
            <u-table-column prop="appName" label="应用名称" width="120" :show-overflow-tooltip="true"/>
            <u-table-column prop="flowName" label="流程名称" width="120" :show-overflow-tooltip="true"/>
            <u-table-column prop="path" label="证书安装路径" min-width="120" :show-overflow-tooltip="true"/>
            <u-table-column prop="downloadUrl" label="下载地址" width="120" :show-overflow-tooltip="true"/>
            <u-table-column prop="type " label="类型" width="160" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <p>{{getTypeStr(scope.row.type)}}</p>
              </template>
            </u-table-column>
            <u-table-column prop="createTime" label="创建时间" width="160" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                {{ scope.row.createTime ? moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') : '' }}
              </template>
            </u-table-column>
            <u-table-column prop="updateTime" label="更新时间" width="160" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                {{ scope.row.updateTime ? moment(scope.row.updateTime).format('YYYY-MM-DD HH:mm:ss') : '' }}
              </template>
            </u-table-column>
            <u-table-column label="操作" align="left" fixed="right" width="150">
              <template slot-scope="scope">
                <div class="jkh-flex">
                  <el-button type="text" size="small" class="text-blue" @click="editCertificate(scope.row)">编辑证书</el-button>
                  <el-button type="text" size="small" class="text-red" @click="removeCertificate(scope.row)">删除证书</el-button>
                </div>
              </template>
            </u-table-column>
          </dTable>
        </div>
      </div>
    </div>
    <el-dialog :title="dialogTile" :visible.sync="dialogVisible" width="600px" :before-close="cancel" :close-on-click-modal="false">
      <div style="padding:0 20px;margin-top:20px;">
        <el-form ref="certificate" :model="certificateData" label-width="130px">
          <el-form-item label="应用名称：" prop="appName" :rules="rules.appName">
            <el-select v-model="certificateData.appObj" placeholder="请选择" class="content-len" @change="handleApp" value-key="appCode" filterable>
              <el-option v-for="item in appList" :key="item.appCode" :label="item.appName" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="流程名称：" prop="flowName">
            <el-select v-model="certificateData.flowObj" placeholder="请选择" class="content-len" @change="handleFlow" value-key="flowCode" filterable>
              <el-option v-for="item in flowList" :key="item.flowCode" :label="item.flowName" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="证书安装路径：" prop="path" :rules="rules.path" style="width: 100%">
            <el-input v-model="certificateData.path" type="textarea" :autosize="{ minRows: 4, maxRows: 46 }" placeholder="请输入证书安装路径" style="width: 300px" @change="changeSearch"></el-input>
          </el-form-item>
          <el-form-item label="下载地址" prop="downloadUrl" style="width: 100%">
            <el-input v-model="certificateData.downloadUrl" type="textarea" :autosize="{ minRows: 4, maxRows: 46 }" placeholder="请输入证书安装路径" style="width: 300px" @change="changeSearch"></el-input>
          </el-form-item>
          <el-form-item label="类型：" style="width: 100%">
            <el-radio-group v-model="certificateData.type">
              <el-radio :label="0">需启动(证书)</el-radio>
              <el-radio :label="1">无需启动（浏览器插件）</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancel" size="small" class="w-60">取 消</el-button>
        <el-button type="primary" @click="confirm" size="small" class="w-60">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
    import dTable from '../../components/common/table'
    import moment from 'moment'

    export default {
        components: {
            dTable,
        },
        data() {
            return {
                moment,
                dialogVisible: false,
                dialogTile: '添加证书',
                isloadingQuey: false,
                formData: {
                    keyWord: '',
                },
                pathData: [],
                appList: [],
                flowList: [],
                loading: null,
                certificateData: {
                    appCode: '',
                    appName: '',
                    appObj: {},
                    path: '',
                    status: 0,
                    type: 0,
                    flowObj: {}
                },
                rules: {
                    path: [
                        {required: true, message: '请输入证书安装路径', trigger: 'change'},
                    ],
                    appName: [
                        {required: true, message: '请选择应用', trigger: 'change'},
                    ],
                },
                isEdit: false,
            }
        },
        computed: {
            tableHeight: function () {
                return this.$global.bodyHeight - 270 + 'px'
            },
            getTypeStr() {
                return function (type) {
                    if (type == '0') {
                        return '需启动(证书)'
                    } else if (type == '1') {
                        return '无需启动（浏览器插件）'
                    }
                    return ''
                }
            },
        },
        created() {
          let tabs = this.$store.state.tabs
          if (tabs) {
            this.pathData = this.$global.deepcopyArray(
              tabs[this.$route.meta.parentPath]
            )
          }
          this.pathData.push({ name: '应用环境' })
            if (this.$route.query.id) {
                this.formData.id = this.$route.query.id
            }
            this.$nextTick(() => {
              this.getTableData()
              this.appAll()
            })
        },
        mounted() {
        },
        methods: {
            getTableData() {
                var params1 = [
                    {property: 'appCode', value: this.formData.appCode},
                ]
                this.$refs.table.fetch({
                    query: params1,
                    method: 'post',
                    url: '/robot/app/env/page',
                })
            },
            changeSearch() {
                this.getTableData(params)
            },
            changeBussinssType() {
                let params = {
                    appCode: this.formData.appCode,
                }
                this.getTableData(params)
            },
            appAll() {
                this.$http({
                    url: '/robot/app/list',
                    method: 'post',
                }).then((data) => {
                    this.appList = data.data.data
                })
            },
            handleApp(val) {
                if (val) {
                    this.certificateData.appName = val.appName
                    this.certificateData.appCode = val.appCode
                    this.$http({
                        url: '/robot/flow/list',
                        method: 'post',
                        data: {
                            appCode: val.appCode
                        }
                    }).then((data) => {
                        this.flowList = data.data.data
                    })
                }
            },
            handleFlow(val) {
                if (val) {
                    this.certificateData.flowName = val.flowName
                    this.certificateData.flowCode = val.flowCode
                }
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
                if (this.loading && this.loading.close) {
                    this.loading.close()
                }
            },
            //添加证书
            addCertificate(row) {
                this.dialogTile = '添加证书'
                this.dialogVisible = true
                this.certificateData = this.$options.data().certificateData
                this.isEdit = false
            },
            //编辑证书
            editCertificate(row) {
                this.dialogTile = '编辑证书'
                this.certificateData = {}
                this.dialogVisible = true
                this.isEdit = true
                let that = this;
                this.$http({
                    url: '/robot/app/env/getById',
                    method: 'get',
                    params: {
                        id: row.id,
                    },
                }).then((resp) => {
                    if (resp.data.code == '200') {
                        that.certificateData = resp.data.data
                        let value = {
                            appName: that.certificateData.appName,
                            appCode: that.certificateData.appCode,
                        }
                        this.certificateData.flowObj = {
                            flowCode: this.certificateData.flowCode,
                            flowName: this.certificateData.flowName
                        }
                        this.certificateData.appObj = value
                        that.handleApp(value);
                    }
                }).catch((err) => {
                    this.isloading = false
                    this.$message.error('接口出错，请稍后再试')
                })
            },
            //删除证书
            remove(row) {
                this.$confirm('是否删除此证书', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    showClose: false,
                    customClass: 'pal-confirm',
                    type: 'warning',
                }).then(() => {
                    this.removeCertificate(row)
                })
            },
            removeCertificate(row) {
                this.showLoading()
                this.$http({
                    url: '/robot/app/env/deleteById',
                    method: 'post',
                    params: {
                        id: row.id
                    },
                }).then((res) => {
                    this.closeLoading()
                    if (!res) {
                        return
                    }
                    if (res.data.code == 200) {
                        this.getTableData()
                    }
                }).catch((err) => {
                    this.closeLoading()
                })
            },
            confirm() {
                this.$refs.certificate.validate((vaild) => {
                    if (vaild) {
                        var url = 'robot/app/env/add'
                        if (this.isEdit) {
                            url = '/robot/app/env/add'
                        } else {
                            url = 'robot/app/env/add'
                        }
                        this.showLoading()
                        this.$http({
                            url: url,
                            method: 'post',
                            data: this.certificateData,
                        }).then((res) => {
                            this.closeLoading()
                            if (res.data.code == 200) {
                                this.getTableData()
                            }
                            this.dialogVisible = false
                        }).catch((err) => {
                            this.closeLoading()
                        })
                    }
                })
            },
            cancel() {
                this.certificateData = this.$options.data().certificateData
                this.dialogVisible = false
            },
        },
    }
</script>
<style lang="less" scoped>
  /deep/ .el-dialog__header {
    padding: 10px 20px;
  }

  /deep/ .el-dialog__body {
    padding: 10px 10px;
  }

  /deep/ .el-drawer__body {
    padding-bottom: 0;
  }

  /deep/ .el-dialog__header {
    border-bottom: none !important;
  }

  .text-blue {
    &:hover {
      text-decoration: underline;
    }
  }

  /deep/ .el-radio__input.is-checked .el-radio__inner::after {
    content: '';
    width: 3px;
    height: 7px;
    border: 1px solid white;
    border-top: transparent;
    border-left: transparent;
    text-align: center;
    display: block;
    position: absolute;
    top: 20%;
    left: 50%;
    vertical-align: middle;
    transform: rotate(45deg) translateX(-50%);
    border-radius: 0px;
    background: none;
  }

  /deep/ .el-radio__inner {
    border-radius: 2px;
    width: 14px;
    height: 14px;
  }

  /deep/ .drawerForm {
    .item-label {
      margin: 5px 0;
    }

    .el-form-item {
      width: 100%;
      margin-right: 0;
      margin-bottom: 0;

      .el-form-item__content,
      .addr-main,
      .el-select {
        width: 100%;
      }
    }

    .el-form-item__error {
      position: relative;
    }

    .opt-icon {
      margin-left: 15px;
      margin-top: 8px;
      cursor: pointer;
      font-size: 28px;
    }

    .ic-addr {
      margin-top: 13px !important;
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

  .jkh-flex {
    /* display: flex;
    justify-content: center; */
  }

  .button-topst {
    position: absolute;
    top: -60px;
    left: 0%;
  }

  .row-fa {
    position: relative;
  }

  .button-add {
    position: absolute;
    right: 0;
  }
</style>
