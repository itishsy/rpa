<template>
    <div class="content spl-container">
        <!-- 导航 -->
        <breadcrumb :data="pathData">
        </breadcrumb>
        <div style="padding: 0px 20px 0 20px">
            <div class="search-row clearfix">
                <el-row>
                    <el-col :span="7" class="display-flex">
                        <div class="label">模板名称
                            <el-tooltip style="diaplay:inline" effect="dark" content="输入模板名称进行查询" placement="top">
                                <i class="el-icon-warning-outline" />
                            </el-tooltip>：
                        </div>
                        <el-input v-model="searchData.name" placeholder="请输入" class="input-w-300"></el-input>
                    </el-col>
                    <el-col :span="3" class="text-right" :offset="14">
                        <el-button class="ml-15" size="small" type="primary" @click="getTableData">搜索</el-button>
                        <el-button class="ml-15" size="small" type="primary" @click="addTemplate">新增</el-button>
                    </el-col>
                </el-row>
            </div>
            <!-- 表格 -->
            <dTable @fetch="getTableData" ref="dTable" :tableHeight="tableHeight" :paging="true" :showSelection="false"
                :showIndex="true" rowKey="id">
                <u-table-column prop="name" label="模板名称" min-width="150" :show-overflow-tooltip="true"></u-table-column>
                <u-table-column prop="tempType" label="模板类型" min-width="100" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span>{{ scope.row.tempType === 1 ? '短信消息模板' : (scope.row.tempType === 2?'语音消息模板':(scope.row.tempType === 3?'缴费核定通知模板':'缴费成功通知模板')) }}</span>
                    </template>
                </u-table-column>
                <u-table-column prop="content" label="内容" min-width="250" :show-overflow-tooltip="true"></u-table-column>
                <u-table-column prop="comment" label="描述" min-width="250" :show-overflow-tooltip="true"></u-table-column>
                <u-table-column fixed="right" label="操作" width="150">
                    <template slot-scope="scope">
                        <el-button type="text" size="small" @click="toDetail(scope.row)" class="emp-opt-btn">详情</el-button>
                        <el-button type="text" size="small" @click="toEdit(scope.row)" class="emp-opt-btn">编辑</el-button>
                    </template>
                </u-table-column>
            </dTable>
        </div>
        <TemplateDrawer ref="drawer" :visible.sync="templateVisiable" :templateId="drawerTemplateId"
            :isDetail="drawerIsDetail" @getTable-data="getTableData()" />
    </div>
</template>

<script>
import dTable from '../../components/common/table'
import TemplateDrawer from './components/TemplateDrawer.vue'
import _ from 'lodash'
export default {
    components: {
        dTable, TemplateDrawer
    },
    data() {
        return {
            pathData: [],
            searchData: {
                name: '',
                netAddress: '',
                recipientPhoneNumber: '',
            },
            templateVisiable: false,
            drawerTemplateId: '',
            drawerIsDetail: false
        }
    },
    computed: {
        tableHeight: function () {
            return this.$global.bodyHeight - 270 + 'px'
        }
    },
    created() {
        let tabs = this.$store.state.tabs
        if (tabs) {
            this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
        }
        this.pathData.push({ name: '短信模板配置' })
        // this.getlistCustomer()
        // this.getValidateTypeDict()
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
                { property: 'name', value: searchData.name },
            ]
            this.$refs.dTable.fetch({
                query: params,
                method: 'post',
                url: '/policy/sys/robot/noticeTemplate/page'
            })
        },
        //获取验证方式的字典值 10021
        async getValidateTypeDict() {
            const { data } = await this.$http({
                url: 'policy/sys/dict/getDictByKey?dataKey=10015',
                method: 'get',
                data: {}
            })
            console.log('获取验证方式的字典值-----------', data)
        },
        //新增短信模板
        addTemplate() {
            //新增时 清除掉模板内容
            this.$refs.drawer.handleNullFrom()
            this.drawerTemplateId = ''
            this.drawerIsDetail = false
            this.templateVisiable = true

        },
        //查看模板详情
        toDetail(row) {
            this.drawerTemplateId = row.id
            this.$refs.drawer.form = _.cloneDeep(row)
            this.drawerIsDetail = true
            this.templateVisiable = true

        },
        //编辑模板
        toEdit(row) {
            this.drawerTemplateId = row.id
            this.$refs.drawer.form = _.cloneDeep(row)
            this.drawerIsDetail = false
            this.templateVisiable = true

        }
    }
}
</script>

<style lang="less" scoped>
.search-row-item {
    width: 80%;
    min-width: 150px;
    max-width: 300px;
}

.view-text {
    color: @blueColor;
    text-decoration: underline;
    cursor: pointer;
}

.input-w-300 {
    width: 300px;
}
.label{
    white-space: nowrap;
}
</style>
