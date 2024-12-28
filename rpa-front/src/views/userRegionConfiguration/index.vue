<template>
  <div class="content spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div style="padding: 0px 20px;">
      <div>
        <div class="search-row clearfix">
          <el-row>
            <el-col :span="5" class="display-flex">
              <span class="label" style="white-space:nowrap;">业务类型：</span>
              <el-select v-model="formData.businessType" class="search-row-item" clearable filterable placeholder="请选择业务类型">
                <el-option key="社保" label="社保" value="社保"></el-option>
                <el-option key="公积金" label="公积金" value="公积金"></el-option>
              </el-select>
            </el-col>
            <el-col :span="5" class="display-flex">
              <span class="label" style="white-space:nowrap;">阶段：</span>
              <el-select v-model="formData.stage" class="search-row-item" clearable filterable placeholder="请选择阶段">
                <el-option key="开发" label="开发" value="开发"></el-option>
                <el-option key="测试" label="测试" value="测试"></el-option>
                <el-option key="运维" label="运维" value="运维"></el-option>
              </el-select>
            </el-col>
            <el-col :span="5" class="display-flex">
              <span class="label" style="white-space:nowrap;">地区名称：</span>
              <el-input v-model="formData.addrName" clearable placeholder="请输入地区名称"></el-input>
            </el-col>
            <el-col :span="5" class="display-flex" style="margin-left: 20px;">
              <span class="label" style="white-space:nowrap;">开发人员：</span>
              <el-input v-model="formData.devUserName" clearable placeholder="请输入开发人员"></el-input>
            </el-col>
            <el-col :span="2" class="text-right">
              <el-button type="primary" class="search-btn w-60" @click="querySearch">搜索</el-button>
            </el-col>
          </el-row>
        </div>
        <div>
          <dTable
            :tableHeight="tableHeight"
            :showIndex="true"
            rowKey="itemId"
            :data="dataList"
            :paging="false"
            ref="dTable"
          >
            <u-table-column
              prop="addrName"
              label="地区名称"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="businessType"
              label="业务类型"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="stage"
              label="阶段"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="devUserName"
              label="开发人员"
              min-width="100"
              :show-overflow-tooltip="true"
            ></u-table-column>
            <u-table-column
              prop="testUserName"
              label="测试人员"
              :show-overflow-tooltip="true"
              min-width="100"
            ></u-table-column>
            <u-table-column
              prop="ywUserName"
              label="运维人员"
              :show-overflow-tooltip="true"
              min-width="100"
            ></u-table-column>
          </dTable>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
    import dTable from '../../components/common/table'

    export default {
        components: {dTable},
        data() {
            return {
                formData: {
                    addrName: '',
                    businessType: '',
                    stage: '',
                    devUserName: '',
                },
                listData: [],
                accountList: [],
                pathData: [{name: '监控于报表'}, {name: '用户地区配置列表'}],
                show: false,
                options: [],
                customList: [],
                loading: null,
                review: false,
                dataList: [],
            }
        },
        computed: {
            tableHeight: function () {
                return this.$global.bodyHeight - 190 + 'px'
            },
        },
        created() {
            let tabs = this.$store.state.tabs
            if (tabs) {
                this.pathData = tabs[this.$route.path]
            }
        },
        async mounted() {
            this.getListUserAddr()
        },
        methods: {
            querySearch() {
                this.getListUserAddr()
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
            //获取开发消息管理-地区消息列表
            getListUserAddr() {
                let that = this
                this.$refs.dTable.loading = true
                this.$http({
                    url: 'policy/dev/userAddr/listUserAddr',
                    method: 'get',
                    params: that.formData
                }).then((res) => {
                    if (res.data.code == 200) {
                        this.dataList = res.data.data
                        this.$refs.dTable.loading = false
                    }
                }).catch((err) => {
                    this.$refs.dTable.loading = false
                    this.$message.error('获取消息列表出错，请稍后再试')
                })
            },
            // 获取当前登录用户的信息
            getCurrentInfo() {
                return this.$http({
                    url: '/api/user/current',
                    method: 'post',
                    data: {},
                }).then(({data}) => {
                    this.$store.commit('updateCompanyInfo', data.data)
                    this.$store.commit('updateUserData', data.data)
                })
            },
        },
    }
</script>
<style lang="less" scoped>
  .netAddress {
    text-decoration: underline;
    color: #3e82ff;
  }
</style>
