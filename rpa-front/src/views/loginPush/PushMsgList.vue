<template>
    <div class="content spl-container">
        <!-- 导航 -->
        <breadcrumb :data="pathData">
        </breadcrumb>
        <div style="padding: 0px 20px 0 20px" v-if="validateTypeList">
            <div class="search-row clearfix">
                <el-row>
                    <el-col :span="6" class="display-flex">
                        <div class="label">客户名称
                            <el-tooltip style="diaplay:inline" effect="dark" content="选择客户名称进行查找" placement="top">
                                <i class="el-icon-warning-outline" />
                            </el-tooltip>：
                        </div>

                        <el-select v-model="searchData.clientId" class="search-row-item" clearable filterable
                            placeholder="请选择">
                            <el-option v-for="item in listCustomerOption" :key="item.id" :label="item.customerName"
                                :value="item.id"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="6" class="display-flex">
                        <div class="label">网站
                            <el-tooltip style="diaplay:inline" effect="dark" content="输入相关的应用与社保网址网站进行查询" placement="top">
                                <i class="el-icon-warning-outline" />
                            </el-tooltip>：
                        </div>
                        <el-input v-model="searchData.netAddress" placeholder="请输入" class="input-w-215"></el-input>
                    </el-col>
                    <el-col :span="7" class="display-flex">
                        <div class="label">手机号码
                            <el-tooltip style="diaplay:inline" effect="dark" content="输入发送手机号码进行查找" placement="top">
                                <i class="el-icon-warning-outline" />
                            </el-tooltip>：
                        </div>
                        <el-input v-model="searchData.recipientPhoneNumber" placeholder="请输入"
                            class="input-w-215"></el-input>
                    </el-col>
                    <el-col :span="4" class="text-right" :offset="1">
                        <el-button class="ml-15" size="small" type="primary" @click="getTableData">搜索</el-button>
                        <el-button class="ml-15" size="small" type="primary" @click="configMsgTemplate">短信模板配置</el-button>
                    </el-col>
                </el-row>
            </div>
            <!-- 表格 -->
            <dTable @fetch="getTableData" ref="dTable" :tableHeight="tableHeight" :paging="true" :showSelection="false"
                :showIndex="true" rowKey="id">
                <u-table-column prop="custName" label="客户名称" min-width="150" :show-overflow-tooltip="true"></u-table-column>
                <u-table-column prop="status" label="状态" min-width="100" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span>{{ scope.row.status === 0 ? '待验证' : scope.row.status === 1 ? '已验证' : scope.row.status === 2 ? '已过期':'-' }}</span>
                    </template>
                </u-table-column>
                <u-table-column prop="netAddress" label="应用-网址" min-width="150" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <a class="netAddress" :href="getNetAddress(scope.row.netAddress)" target="_blank">{{ scope.row.appName }} - {{ scope.row.netAddress }}</a>
                    </template>
                </u-table-column>
                <u-table-column prop="recipientPhoneNumber" label="发送手机号码" min-width="100"
                    :show-overflow-tooltip="true"></u-table-column>
                <u-table-column prop="validateType" label="验证方式" min-width="100" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <!-- <span>{{ validateTypeList.find(item => item.dictCode === scope.row.validateType).dictName }}</span> -->
                        <span>{{ findDictName(scope.row.validateType) }}</span>
                    </template>
                </u-table-column>
                <u-table-column prop="voiceReminder" label="语音电话是否通知" :show-overflow-tooltip="true" min-width="100">
                    <template slot-scope="scope">
                        <span>{{ scope.row.voiceReminder ? '是' : '否' }}</span>
                    </template>
                </u-table-column>
                <u-table-column prop="createTime" label="发送时间" min-width="150"
                    :show-overflow-tooltip="true"></u-table-column>
                <u-table-column fixed="right" label="操作" width="100">
                    <template slot-scope="scope">
                        <el-button type="text" size="small" @click="toDetail(scope.row)"
                            class="emp-opt-btn">详情</el-button>
                    </template>
                </u-table-column>
            </dTable>
        </div>
        <SendDetailDrawer ref="drawer" :visible.sync="drawerVisiable" :currentRow="currentRow"/>
    </div>
</template>
  
<script>
import { setTimeout } from 'timers'
import dTable from '../../components/common/table'
import SendDetailDrawer from './components/SendDetailDrawer.vue'
export default {
    components: {
        dTable,SendDetailDrawer
    },
    data() {
        return {
            pathData: [],
            searchData: {
                clientId: null,
                netAddress: null,
                recipientPhoneNumber: null,
            },
            listCustomerOption: [],
            validateTypeList: [],     //验证方式的字典list
            drawerVisiable:false,
            currentRow:{}       //当前要展示的行
        }
    },
    computed: {
        tableHeight: function () {
            return this.$global.bodyHeight - 270 + 'px'
        },
        getNetAddress(){
          return function(val){
            var reg = /^(http|https)/
            return reg.test(val) ? val : `//${val}`
          }
        }
    },
    created() {
        let tabs = this.$store.state.tabs
        if (tabs) {
            this.pathData = tabs[this.$route.path]
        }
        this.getlistCustomer()
        this.getValidateTypeDict()
        this.$nextTick(() => {
            this.getTableData()
        })
    },
    mounted() {
    },
    methods: {
        //获取表格内容
        getTableData() {
            let searchData = this.searchData
            var params = [
                { property: 'custId', value: searchData.clientId || null },
                { property: 'netAddress', value: searchData.netAddress || null },
                { property: 'recipientPhoneNumber', value: searchData.recipientPhoneNumber || null },
            ]
            this.$refs.dTable.fetch({
                query: params,
                method: 'post',
                url: '/policy/sys/robot/noticeList/page'
            })
        },
        //获取客户
        getlistCustomer() {
            this.$http({
                url: '/robot/app/client/listCustomer',
                method: 'post'
            })
                .then((data) => {
                    this.listCustomerOption = data.data.data
                })
                .catch((err) => {
                })
        },
        //获取验证方式的字典值 10021
        async getValidateTypeDict() {
            const { data: { data: result } } = await this.$http({
                url: 'policy/sys/dict/getDictByKey?dataKey=10021',
                method: 'get',
                data: {}
            })
            console.log('获取验证方式的字典值-----------', result)
            if (result.length > 0) this.validateTypeList = result

        },
        //配置短信模板
        configMsgTemplate() {
            this.$router.push('/loginPush/textMessageTemplate')
        },
        findDictName(validateType){
            return this.validateTypeList.find(item => item.dictCode === validateType).dictName
        },
        //查看发送详情
        async toDetail(row){
            const {data:{code,data:result}} = await this.$http({
                url:`/policy/sys/robot/noticeList/detail/${row.id}`,
                method:'post',
                data:{}
            })
            if(code === 200){
                console.log('当前行发送详情',result)
                this.currentRow = {
                    ...result,
                    validateTypeName:this.findDictName(result.validateType)
                }
                setTimeout(()=>{
                    this.drawerVisiable = true
                },300)
            }
        }
    }
}
</script>
  
<style lang="less" scoped>
.search-row-item {
    width: 80%;
    min-width: 150px;
    max-width: 215px;
}

.view-text {
    color: @blueColor;
    text-decoration: underline;
    cursor: pointer;
}

.input-w-215 {
    width: 215px;
}
.netAddress{
  text-decoration: underline;
  color:#3E82FF;
}
.label{
   white-space: nowrap;
}
</style>
  