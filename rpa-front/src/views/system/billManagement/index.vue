<template>
    <div class="content spl-container">
        <!-- 导航 -->
        <breadcrumb :data="pathData">
        </breadcrumb>
        <div style="padding: 20px 20px 0 20px">
            <!-- 顶部查询 -->
            <div class="search-row clearfix">
                <el-row>
                    <el-col :span="10" class="display-flex">
                        <span class="label">客户名称：</span>
                        <el-select v-model="searchForms.customerId" class="w-300" placeholder="请选择" clearable filterable>
                            <el-option v-for="(item, index) in listCustomerOption" :key="index" :label="item.customerName"
                                :value="item.id"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="8" class="display-flex">
                        <span class="label">收款单号：</span>
                        <el-input v-model.trim="searchForms.receiptNumber" class="w-300" placeholder="请输入"
                            clearable></el-input>
                    </el-col>
                    <el-col :span="6" class="display-flex">
                        <span class="label">收款年月：</span>
                        <div style="display:flex;max-width:300px">
                            <el-date-picker v-model="searchForms.receiptYearMonth" value-format="yyyy-MM"
                                style="width:100%;" type="month" placeholder="选择时间" clearable></el-date-picker>

                        </div>
                    </el-col>
                </el-row>
                <el-row class="mt-15">
                    <el-col :span="10" class="display-flex">
                        <span class="label">处理状态：</span>
                        <el-checkbox-group v-model="searchForms.handleStatus" size="medium"
                            style="display: flex;align-items: center;flex-wrap:wrap">
                            <el-checkbox v-for="item in searchOptions.declareTypes" :key="item.id" :label="item.id">
                                {{ item.name }} <div class="round" :style="'background:' + item.bgColor"></div>
                            </el-checkbox>
                        </el-checkbox-group>
                    </el-col>
                    <el-col :span="8" class="display-flex">
                        <span class="label">开票状态：</span>
                        <el-checkbox-group v-model="searchForms.invoiceStatus" size="medium"
                            style="display: flex;align-items: center;flex-wrap:wrap">
                            <el-checkbox v-for="item in searchOptions.status" :key="item.id" :label="item.id">
                                {{ item.name }}
                            </el-checkbox>
                        </el-checkbox-group>
                    </el-col>
                    <el-col :span="6" class="text-right">
                        <!-- <el-button type="primary" plain class="search-btn w-60 mr-10">重置</el-button> -->
                        <el-button type="primary" class="search-btn w-60 mr-10" @click="clickQuery">查询</el-button>
                        <el-button class="search-btn w-60 mr-80" @click="resetSearch">重置</el-button>
                    </el-col>
                </el-row>
            </div>
            <!-- 表格 -->
            <div class="top-divider">
                <el-button v-if="$global.hasPermission('billManagementCreate')" type="primary" class="search-btn  mr-10"
                    size="small" @click="createBill">创建收款账单</el-button>
                <el-button class="search-btn mr-10" size="small" @click="exportPayBill">导出收款账单</el-button>
            </div>
            <dTable @fetch="getTableData" ref="table" :tableHeight="tableHeight" :paging="true" :showSelection="true"
                :showIndex="true" rowKey="id" :rowHeight="45" @selection-change="handleSelectionChange">
                <u-table-column prop="receiptNumber" label="收款单号" min-width="220" :show-overflow-tooltip="true" fixed>
                    <template slot-scope="scope">
                        <p class="round margin-bottom-5"
                            :style="'background:' + findBgColorByStatus(scope.row.handleStatus)"></p>
                        <span class="pl-10">{{ scope.row.receiptNumber }}</span>
                    </template>
                </u-table-column>
                <u-table-column prop="customerName" label="客户名称" min-width="200" :show-overflow-tooltip="true"
                    fixed></u-table-column>
                <u-table-column prop="receiptYearMonth" label="收款年月" min-width="100"
                    :show-overflow-tooltip="true"></u-table-column>
                <u-table-column prop="receiptProject" label="费用项目" min-width="160" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span>{{ feeProjectStr(scope.row.receiptProject) }}</span>
                    </template>
                </u-table-column>
                <u-table-column prop="payCount" label="收款单金额" min-width="100" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span>{{ scope.row.payCount ? scope.row.payCount.toFixed(2) : '0.00' }}</span>
                    </template>
                </u-table-column>
                <u-table-column prop="agreeReceiptDay" label="约定到款日" min-width="100"
                    :show-overflow-tooltip="true"></u-table-column>
                <u-table-column prop="payTime" label="到账时间" min-width="100" :show-overflow-tooltip="true">
                </u-table-column>
                <u-table-column prop="actualAmount" label="实收金额" min-width="100" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span>{{ scope.row.actualAmount ? scope.row.actualAmount.toFixed(2) : '0.00' }}</span>
                    </template>
                </u-table-column>
                <u-table-column prop="accfundRegisterNumber" label="在户服务" min-width="100" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span>{{ getFeePirce(scope.row, 0) }}</span>
                    </template>
                </u-table-column>
                <u-table-column prop="accfundRegisterNumber" label="驻场服务" min-width="100" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span>{{ getFeePirce(scope.row, 1) }}</span>
                    </template>
                </u-table-column>
                <u-table-column prop="accfundRegisterNumber" label="外勤服务" min-width="100" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span>{{ getFeePirce(scope.row, 2) }}</span>
                    </template>
                </u-table-column>
                <u-table-column prop="accfundRegisterNumber" label="其他服务" min-width="100" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span>{{ getFeePirce(scope.row, 3) }}</span>
                    </template>
                </u-table-column>
                <u-table-column prop="checkName" label="审核人" min-width="100" :show-overflow-tooltip="true"></u-table-column>
                <u-table-column prop="checkTime" label="审核时间" min-width="160"
                    :show-overflow-tooltip="true"></u-table-column>
                <u-table-column prop="createName" label="创建人" min-width="100"
                    :show-overflow-tooltip="true"></u-table-column>
                <u-table-column prop="createTime" label="创建时间" min-width="160"
                    :show-overflow-tooltip="true"></u-table-column>
                <u-table-column prop="handleStatus" label="处理状态" min-width="100" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <el-tooltip class="item" effect="dark" :disabled="scope.row.handleStatus != 3"
                            :content="'驳回原因：' + scope.row.rejectRemark" placement="top">
                            <span>{{ handleStatusDetail(scope.row.handleStatus) }}</span>
                        </el-tooltip>
                    </template>
                </u-table-column>
                <u-table-column prop="invoiceStatus" label="开票状态" min-width="100" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span>{{ invoiceStatusDetail(scope.row.invoiceStatus) }}</span>
                    </template>
                </u-table-column>
                <u-table-column fixed="right" label="操作" width="180">
                    <template slot-scope="scope">
                        <!-- 制单员(删除、处理、上传发票)  审核员（审核）-->
                        <el-button type="text" size="small" @click="viewRequest(scope.row)">查看</el-button>
                        <el-button v-if="hasPermissionEdit(scope)" type="text" size="small"
                            @click="editRequest(scope.row)">编辑</el-button>
                        <el-button v-if="hasPermissionDel(scope)" type="text" size="small" class="text-red"
                            @click="delRequest(scope.row)">删除</el-button>
                        <el-button v-if="hasPermissionVerify(scope)" type="text" size="small"
                            @click="verifyBill(scope.row)">审核</el-button>
                        <el-button v-if="hasPermissionHandle(scope)" type="text" size="small"
                            @click="confirmReceipt(scope.row)">收款确认</el-button>
                        <el-button v-if="hasPermissionInvoice(scope)" type="text" size="small"
                            @click="handleInvoice(scope.row)">上传发票</el-button>
                    </template>
                </u-table-column>
                <template slot="pagination-btns">
                    <div class="display-flex">
                        <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                            @click="exportTableData">导出列表</el-button>
                    </div>
                </template>
            </dTable>
        </div>
        <!-- 上传发票 -->
        <uploadInvoice ref="uploadInvoice" @onSuccess="confirmInvoiceSuccess"></uploadInvoice>
        <!-- 确认收款 -->
        <confirmReceipt ref="confirmReceipt" @onSuccess="confirmReceiptSuccess"></confirmReceipt>
    </div>
</template>
<script>
import dTable from '@/components/common/table'
import uploadInvoice from './component/uploadInvoice.vue'
import confirmReceipt from './component/confirmReceipt.vue'
export default {
    name: '',
    components: { dTable, uploadInvoice, confirmReceipt },
    props: [''],
    data() {
        return {
            pathData: [],
            listCustomerOption: [],
            searchOptions: {
                declareTypes: [
                    {
                        name: '待审核',
                        id: 2,
                        bgColor: '#d4dde9'
                    },
                    {
                        name: '审核不通过',
                        id: 3,
                        bgColor: '#fd6763'
                    },
                    {
                        name: '未到账',
                        id: 1,
                        bgColor: '#ffb82f'
                    },
                    {
                        name: '已到账',
                        id: 0,
                        bgColor: '#3dca90'
                    }
                ],
                status: [
                    {
                        name: '未开票',
                        id: 0
                    },
                    {
                        name: '已开票',
                        id: 1
                    },
                    {
                        name: '部分开票',
                        id: 2
                    }
                ],
                feeProject: [
                    '在户人数',
                    '驻场服务',
                    '外勤服务',
                    '其他服务'
                ]
            },
            searchForms: {
                customerId: "",
                receiptNumber: "",
                receiptYearMonth: "",
                handleStatus: [],
                invoiceStatus: []
            },
            loading: null
        };
    },
    computed: {
        tableHeight: function () {
            return this.$global.bodyHeight - 360 + 'px'
        },
        //处理状态
        handleStatusDetail() {
            return function (handleStatus) {
                return this.searchOptions.declareTypes.find(item => item.id == handleStatus).name
            }
        },
        //开票状态
        invoiceStatusDetail() {
            return function (invoiceStatus) {
                return this.searchOptions.status.find(item => item.id == invoiceStatus).name
            }
        },
        //费用项目string
        feeProjectStr() {
            return (receiptProject) => {
                let tempArray = receiptProject.split(',')
                let tempStr = ''
                tempArray.forEach((item, index) => {
                    tempStr += this.searchOptions.feeProject[index] + ','
                })
                return tempStr.slice(0, -1)
            }
        },
        //是否有删除权限【制单员】
        hasPermissionDel() {
            return (scope) => {
                return this.$global.hasPermission('billManagementDelete') && [2, 3].includes(scope.row.handleStatus)
            }
        },
        //是否有编辑权限【制单员】
        hasPermissionEdit() {
            return (scope) => {
                return this.$global.hasPermission('billManagementEdit') && [2, 3].includes(scope.row.handleStatus)
            }
        },
        //是否有处理权限【制单员】
        hasPermissionHandle() {
            return (scope) => {
                return this.$global.hasPermission('billManagementHandle') && [1].includes(scope.row.handleStatus)
            }
        },
        //是否有上传发票权限【制单员】
        hasPermissionInvoice() {
            return (scope) => {
                return this.$global.hasPermission('billManagementInvoice') && [0, 1].includes(scope.row.handleStatus) && scope.row.invoiceStatus != 1         //已经全额开票的不显示上传发票按钮
                // return this.$global.hasPermission('billManagementInvoice')        //已经全额开票的不显示上传发票按钮
            }
        },
        //是否有审核权限【审核员】
        hasPermissionVerify() {
            return (scope) => {
                return this.$global.hasPermission('billManagementVerify') && [2].includes(scope.row.handleStatus)
            }
        },
    },
    watch: {},
    created() {
        let tabs = this.$store.state.tabs
        if (tabs) {
            this.pathData = tabs[this.$route.path]
        }
        this.getlistCustomer()
    },
    beforeMount() { },
    mounted() {
        this.getTableData()
    },
    methods: {
        findBgColorByStatus(handleStatus) {
            return this.searchOptions.declareTypes.find(item => item.id == handleStatus).bgColor
        },
        //获取费用项目对应的金额
        getFeePirce(row, serviceType) {
            if (row.receiptDetailList) {
                const findItem = row.receiptDetailList.find(item => item.serviceType == serviceType)
                return findItem ? findItem.countPrice.toFixed(2) : '0.00'
            } else {
                return '0.00'
            }
        },
        //获取用户下拉
        getlistCustomer() {
            this.$http({
                url: '/robot/app/client/listCustomer',
                method: 'post'
            })
                .then((data) => {
                    this.listCustomerOption = data.data.data
                })
                .catch((err) => { })
        },
        // 上传发票
        handleInvoice(row) {
            console.log('上传发票', row)
            this.$refs.uploadInvoice.init(row)
        },
        //确认收款
        confirmReceipt(row) {
            console.log('确认收款', row);
            this.$refs.confirmReceipt.init(row)
        },
        clickQuery() {
            this.getTableData()
        },
        resetSearch() {
            this.searchForms = {
                customerId: "",
                receiptNumber: "",
                receiptYearMonth: "",
                handleStatus: [],
                invoiceStatus: []
            }
            this.getTableData()
        },
        getTableData() {
            let that = this
            let search = this.searchForms
            var params = [
                { property: 'customerId', value: search.customerId || null },
                { property: 'receiptNumber', value: search.receiptNumber },
                { property: 'receiptYearMonth', value: search.receiptYearMonth },
                { property: 'handleStatusList', value: search.handleStatus },
                { property: 'invoiceStatusList', value: search.invoiceStatus },
            ]
            this.$refs.table.fetch({
                query: params,
                method: 'post',
                url: 'policy/bill/queryBillData',
                callback: function (res) {
                    console.log('res----------', res)
                    if (res.records == 0) return
                },
            })
        },
        //表格勾选回调
        handleSelectionChange(val) {
        },
        exportTableData() {
            let params = this.$refs.table.getParamsQuery()
            let params2 = this.$global.deepcopyArray(params)
            let selectonIds = this.$refs.table.getSelectionsArr('_id')
            if (selectonIds.length == 0) {
                this.$message.warning('请先勾选需要导出的列表数据')
                return
            }
            params2.push({ property: 'ids', value: selectonIds })
            this.$downloadFile(
                '/policy/bill/downloadBill',
                'post',
                {
                    query: params2,
                },
                this.$global.EXCEL
            )
        },
        //查看
        viewRequest(row) {
            this.$router.push({
                path: '/system/billEdit',
                query: {
                    type: 2,
                    orderId: row._id,
                }
            })
        },
        //审核
        verifyBill(row) {
            this.$router.push({
                path: '/system/billEdit',
                query: {
                    type: 3,
                    orderId: row._id,
                }
            })
        },
        //创建收款单
        createBill() {
            this.$router.push('/system/billEdit?type=0')
        },
        //编辑收款单
        editRequest(row) {
            console.log('edit', row)
            this.$router.push('/system/billEdit?type=1&orderId=' + row._id)
        },
        //删除收款单
        delRequest(row) {
            this.$confirm('是否确认删除该收款账单？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                showClose: false,
                customClass: 'pal-confirm',
                type: 'warning'
            })
                .then(() => {
                    this.showLoading()
                    this.$http({
                        url: '/policy/bill/deleteBill',
                        method: 'post',
                        data: {
                            _id: row._id,
                        }
                    }).then((res) => {
                        if (res.data.code == 200) {
                            this.getTableData()
                            this.$message.success(res.message ? res.message : '操作成功')
                        }
                        this.closeLoading()
                    }).catch((err) => {
                        this.closeLoading()
                    })
                })
                .catch((err) => {
                    console.log(err)
                })
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
        //导出收款单
        exportPayBill() {
            let selectonIds = this.$refs.table.getSelectionsArr('_id')
            // let params = this.$refs.table.getParamsQuery()
            // var params2 = this.$global.deepcopyArray(params)
            console.log(selectonIds)
            if (selectonIds.length == 0) {
                this.$message.warning('请至少选择一条记录')
                return
            }
            let that = this
            this.$downloadFile(
                '/policy/bill/downloadPay',
                'post',
                {
                    ids: selectonIds
                },
                this.$global.ZIP
            )

        },
        //接口上传发票成功
        confirmInvoiceSuccess() {
            this.getTableData()
        },
        //到账处理成功
        confirmReceiptSuccess() {
            this.getTableData()
        }
    },
}
</script>
<style lang='less' scoped>
.search-row {
    padding: 0;

    .label {
        width: 90px;
        text-align: right;
    }

}

.round {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    display: inline-block;
    // box-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
}

.top-divider {
    padding-top: 10px;
    padding-bottom: 10px;
    margin-top: 10px;
    border-top: 1px solid #e5e5e5;
}

.margin-bottom-5 {
    margin-bottom: 5px;
}
</style>