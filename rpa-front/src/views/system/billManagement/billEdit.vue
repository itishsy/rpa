<template>
    <div class="content spl-container">
        <!-- 导航 -->
        <breadcrumb :data="pathData">
        </breadcrumb>
        <div style="padding: 5px 20px 0 20px">
            <el-row type="flex" align="middle" class="mt-20">
                <span style="width: 10px;background: #3E82FF;height: 20px;display: inline-block;"></span><span
                    style="margin-left:10px;">基础信息</span>
            </el-row>
            <el-row type="flex" align="middle" class="mt-15">
                <el-form :model="form" ref="form" label-position="right" style="width: 100%;" label-width="110px"
                    :rules="rules" :disabled="type == 2">
                    <el-col :span="8">
                        <el-form-item label="客户：" class="display-center" prop="customerId">
                            <el-select v-model="form.customerId" class="w-300" @change="onChangeCustomerId"
                                placeholder="请选择" clearable filterable :disabled="type != 0">
                                <el-option v-for="(item, index) in listCustomerOption" :key="index"
                                    :label="item.customerName" :value="item.id"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="收款年月：" class="display-center" prop="receiptYearMonth">
                            <el-date-picker type="month" placeholder="选择日期" v-model="form.receiptYearMonth" class="w-300"
                                value-format="yyyy-MM" :disabled="type != 0"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="费用项目：" class="display-center" prop="project">
                            <el-select v-model="form.project" placeholder="请选择" style="width:80%" multiple
                                @change="onProjectChange">
                                <el-option label="在户人数" :value="0" disabled></el-option>
                                <el-option label="驻场服务" :value="1"></el-option>
                                <el-option label="外勤服务" :value="2"></el-option>
                                <el-option label="其他服务" :value="3"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8" v-if="type == 2">
                        <el-form-item label="收款单号:">
                            <span>{{ rowDetail.receiptNumber }}</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="16" v-if="type == 2">
                        <el-form-item label="收款单金额:">
                            <span>￥{{ rowDetail.payCount ? parseFloat(rowDetail.payCount).toFixed(2) : 0.00 }}</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="type == 2 ? 8 : 24">
                        <el-form-item label="约定到款日期:" class="display-center" prop="agreeReceiptDay">
                            <el-date-picker placeholder="选择日期" v-model="form.agreeReceiptDay" class="w-300"
                                value-format="yyyy-MM-dd"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="16" v-if="type == 2">
                        <el-form-item label="实际到款日期:" class="display-center">
                            <span>{{ rowDetail.payTime }}</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="入账公司：" class="display-center" prop="receiptCompany">
                            <el-select v-model="form.receiptCompany" :disabled="type != 0" placeholder="请选择" class="w-300"
                                clearable>
                                <el-option v-for="(item, index) in companyArr" :label="item.dictName"
                                    :value="item.dictName"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="入账银行：" class="display-center" clearable prop="complexResult">
                            <el-select v-model="form.complexResult" :disabled="type != 0" placeholder="请选择" class="w-300"
                                @change="onChangeComplexResult">
                                <el-option v-for="(item, index) in bankArr" :label="item.dictName"
                                    :value="item.dictName"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="type == 2">
                        <el-form-item label="到款回单：" class="display-center">
                            <div class="file-list">
                                <div class="file" @click="downLoadFile(item)" v-for="(item, index) in billList"
                                    :key="item.fileId">
                                    {{ item.fileName }}
                                    <!-- <span class="file-remove" @click.stop="removeBill(index)"></span> -->
                                </div>
                            </div>

                        </el-form-item>

                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注:" class="display-center">

                            <el-input v-if="type != 2" v-model.trim="form.remark" type="textarea" placeholder="请输入"
                                class="w-300" rows="4">
                            </el-input>
                            <p v-else class="remark-p">{{ form.remark }}</p>
                        </el-form-item>
                    </el-col>
                </el-form>
            </el-row>
            <el-row type="flex" align="middle" class="mt-20">
                <span style="width: 10px;background: #3E82FF;height: 20px;display: inline-block;"></span><span
                    style="margin-left:10px;">收费明细</span>
                <img class="iconImg"
                    :src="!showBox1 ? require('@/assets/images/icons/icon_d_down.png') : require('@/assets/images/icons/icon_d_up.png')"
                    @click="showBox1 = !showBox1" />
            </el-row>
            <collapse-transition>
                <div class="mt-15" v-if="showBox1">
                    <table class="cust-table-border w-p100">
                        <thead>
                            <tr>
                                <th style="width: 50px;">费用年月</th>
                                <th style="width: 100px;">费用项目</th>
                                <th class="required" style="width: 30px;">数量</th>
                                <th class="required" style="width: 30px;">单价</th>
                                <th class="required" style="width: 50px;">单位</th>
                                <th style="width: 50px;">总额</th>
                                <th style="width: 250px;">附件</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-if="form.project.indexOf(0) != -1">
                                <td>{{ feeDetailTable[0].payYearMonth }}</td>
                                <td>{{ feeDetailTable[0].payProject }}</td>
                                <td class="max-width-60"> <el-input-number v-model="feeDetailTable[0].personNumber"
                                        :controls="false" :min="0" placeholder="请输入" disabled
                                        @blur="onBlurPersonNumber($event, 0)" :disabled="type == 2"></el-input-number></td>
                                <td class="max-width-60">
                                    <el-input-number v-model="feeDetailTable[0].price" :precision="2" :controls="false"
                                        :min="0" placeholder="请输入" @blur="onBlurPersonNumber($event, 0)"
                                        :disabled="type == 2"></el-input-number>
                                </td>
                                <td>{{ feeDetailTable[0].unit }}</td>
                                <td>{{ feeDetailTable[0].countPrice }}</td>
                                <td>
                                </td>
                            </tr>

                            <tr v-if="form.project.indexOf(1) != -1">
                                <td>{{ feeDetailTable[1].payYearMonth }}</td>
                                <td>{{ feeDetailTable[1].payProject }}</td>
                                <td class="max-width-60"> <el-input-number v-model="feeDetailTable[1].personNumber"
                                        :controls="false" :min="0" placeholder="请输入" @blur="onBlurPersonNumber($event, 1)"
                                        :disabled="type == 2"></el-input-number></td>
                                <td class="max-width-60">
                                    <el-input-number v-model="feeDetailTable[1].price" :precision="2" :controls="false"
                                        :min="0" placeholder="请输入" @blur="onBlurPersonNumber($event, 1)"
                                        :disabled="type == 2"></el-input-number>
                                </td>
                                <td><el-input v-model="feeDetailTable[1].unit" placeholder="请输入" :disabled="type == 2" />
                                </td>
                                <td>{{ feeDetailTable[1].countPrice }}</td>
                                <td>
                                    <div style="display:flex">
                                        <span class="lab">
                                            <el-upload ref="upload" :headers="$global.setUploadHeaders()"
                                                :action="$global.uploadFileUrl" :auto-upload="true" :show-file-list="false"
                                                :multiple="false" :before-upload="onBeforeUpload" :limit="1"
                                                :on-success="(response) => onSuccessFile(response, 1)">
                                                <el-button plain class="s-btn" style="color: #3E82FF;"
                                                    :disabled="feeDetailTable[1].fileList.length == 1">上传附件+</el-button>
                                            </el-upload>
                                        </span>
                                        <div class="file-list" style="width:0px !important">
                                            <div class="file " @click="downLoadFile(item)"
                                                v-for="(item, index) in feeDetailTable[1].fileList" :key="item.fileId">
                                                <el-tooltip :content="item.fileName" placement="top">
                                                    <span class="over-file">{{ item.fileName }}</span>
                                                </el-tooltip>
                                                <span class="file-remove" @click.stop="removeFile(index, 1)"
                                                    v-if="type != 2"></span>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>

                            <tr v-if="form.project.indexOf(2) != -1">
                                <td>{{ feeDetailTable[2].payYearMonth }}</td>
                                <td>{{ feeDetailTable[2].payProject }}</td>
                                <td class="max-width-60"> <el-input-number v-model="feeDetailTable[2].personNumber"
                                        :controls="false" :min="0" placeholder="请输入" @blur="onBlurPersonNumber($event, 2)"
                                        :disabled="type == 2"></el-input-number></td>
                                <td class="max-width-60">
                                    <el-input-number v-model="feeDetailTable[2].price" :precision="2" :controls="false"
                                        :min="0" placeholder="请输入" @blur="onBlurPersonNumber($event, 2)"
                                        :disabled="type == 2"></el-input-number>
                                </td>
                                <td><el-input v-model="feeDetailTable[2].unit" placeholder="请输入" :disabled="type == 2" />
                                </td>
                                <td>{{ feeDetailTable[2].countPrice }}</td>
                                <td>
                                    <div style="display:flex">
                                        <span class="lab">
                                            <el-upload ref="upload" :headers="$global.setUploadHeaders()"
                                                :action="$global.uploadFileUrl" :auto-upload="true" :show-file-list="false"
                                                :multiple="false" :before-upload="onBeforeUpload" :limit="1"
                                                :on-success="(response) => onSuccessFile(response, 2)">
                                                <el-button plain class="s-btn" style="color: #3E82FF;"
                                                    :disabled="feeDetailTable[2].fileList.length == 1">上传附件+</el-button>
                                            </el-upload>
                                        </span>
                                        <div class="file-list" style="width:0px !important">
                                            <div class="file" @click="downLoadFile(item)"
                                                v-for="(item, index) in feeDetailTable[2].fileList" :key="item.fileId">
                                                <el-tooltip :content="item.fileName" placement="top">
                                                    <span class="over-file">{{ item.fileName }}</span>
                                                </el-tooltip>
                                                <span class="file-remove" @click.stop="removeFile(index, 2)"
                                                    v-if="type != 2"></span>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <tr v-if="form.project.indexOf(3) != -1">
                                <td>{{ feeDetailTable[3].payYearMonth }}</td>
                                <td>{{ feeDetailTable[3].payProject }}</td>
                                <td class="max-width-60"> <el-input-number v-model="feeDetailTable[3].personNumber"
                                        :controls="false" :min="0" placeholder="请输入" :disabled="type == 2"
                                        @blur="onBlurPersonNumber($event, 3)"></el-input-number></td>
                                <td class="max-width-60">
                                    <el-input-number v-model="feeDetailTable[3].price" :precision="2" :controls="false"
                                        :min="0" placeholder="请输入" @blur="onBlurPersonNumber($event, 3)"
                                        :disabled="type == 2"></el-input-number>
                                </td>
                                <td><el-input v-model="feeDetailTable[3].unit" placeholder="请输入" :disabled="type == 2" />
                                </td>
                                <td>{{ feeDetailTable[3].countPrice }}</td>
                                <td>
                                    <div style="display:flex">
                                        <span class="lab">
                                            <el-upload ref="upload" :headers="$global.setUploadHeaders()"
                                                :action="$global.uploadFileUrl" :auto-upload="true" :show-file-list="false"
                                                :multiple="false" :before-upload="onBeforeUpload" :limit="1"
                                                :on-success="(response) => onSuccessFile(response, 3)">
                                                <el-button plain class="s-btn" style="color: #3E82FF;"
                                                    :disabled="feeDetailTable[3].fileList.length == 1">上传附件+</el-button>
                                            </el-upload>
                                        </span>
                                        <div class="file-list" style="width:0px !important">
                                            <div class="file " @click="downLoadFile(item)"
                                                v-for="(item, index) in feeDetailTable[3].fileList" :key="item.fileId">
                                                <el-tooltip :content="item.fileName" placement="top">
                                                    <span class="over-file">{{ item.fileName }}</span>
                                                </el-tooltip>
                                                <span class="file-remove" @click.stop="removeFile(index, 3)"
                                                    v-if="type != 2"></span>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="5" class="text-center">合计</td>
                                <td>￥{{ totaleTablePrice }}</td>
                                <td></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </collapse-transition>

            <el-row type="flex" align="middle" class="mt-20">
                <span style="width: 10px;background: #3E82FF;height: 20px;display: inline-block;"></span><span
                    style="margin-left:10px;">项目明细</span>
                <img class="iconImg"
                    :src="!showBox2 ? require('@/assets/images/icons/icon_d_down.png') : require('@/assets/images/icons/icon_d_up.png')"
                    @click="showBox2 = !showBox2" />
            </el-row>
            <collapse-transition>
                <div class="mt-15" style="display: flex;" v-show="showBox2">
                    <div class="left-container">在户人数</div>
                    <table class="cust-table-border w-p100">
                        <thead>
                            <tr>
                                <th style="width: 300px;">申报单位</th>
                                <th style="width: 50px;">参保城市</th>
                                <th style="width: 50px;">在户人数</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(item, index) in tableData" :key="index">
                                <td>{{ item.orgName }}</td>
                                <td>{{ item.addrName }}</td>
                                <td>{{ item.registerNumber }}</td>
                            </tr>
                            <tr>
                                <td colspan="2" class="text-center">合计</td>
                                <td>{{ totalPeopleCount }}</td>
                            </tr>

                        </tbody>
                    </table>

                </div>
            </collapse-transition>

            <el-row type="flex" align="middle" class="mt-20" v-if="type == 2 && rowDetail.handleStatus == 0">
                <span style="width: 10px;background: #3E82FF;height: 20px;display: inline-block;"></span><span
                    style="margin-left:10px;">开票信息</span>
                <img class="iconImg"
                    :src="!showBox3 ? require('@/assets/images/icons/icon_d_down.png') : require('@/assets/images/icons/icon_d_up.png')"
                    @click="showBox3 = !showBox3" />
            </el-row>
            <collapse-transition v-if="type == 2 && rowDetail.handleStatus == 0">
                <div class="mt-15" v-show="showBox3">
                    <span>开票金额：{{ rowDetail.kpAmount }}</span>
                    <div class="row mt-15">
                        <!-- <span class="lab">
                            <el-upload ref="upload" :headers="$global.setUploadHeaders()" :action="$global.uploadFileUrl"
                                :auto-upload="true" :show-file-list="false" :multiple="false"
                                :before-upload="onBeforeUpload" :on-success="onSuccessInvoiceFile">
                                <el-button plain class="s-btn" style="color: #3E82FF;">上传附件+</el-button>
                            </el-upload>
                        </span> -->
                        <div class="sel">
                            <div class="file-list">
                                <div class="file" @click="downLoadFile(item)" v-for="(item, index) in invoiceList"
                                    :key="item.fileId">
                                    {{ item.fileName }}
                                    <!-- <span class="file-remove" @click.stop="removeInvoiceFile(index)"></span> -->
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </collapse-transition>

        </div>

        <div class="bottom-layout pd-20">
            <el-row type="flex" justify="center">
                <el-button plain @click="$router.back()">关闭</el-button>
                <el-button type="primary" class="ml-20" v-if="type <= 1" @click="createBillOrder()"
                    :disabled="btnCreate">确定</el-button>
                <el-button type="success" class="ml-20" v-if="type == 3 && $global.hasPermission('billManagementVerify')"
                    plain @click="showConfirmVerify()">通过</el-button>
                <el-button type="danger" class="ml-20" v-if="type == 3 && $global.hasPermission('billManagementVerify')"
                    plain @click="dialogVisible2 = true">驳回</el-button>
            </el-row>
        </div>
        <!-- <el-dialog title="审核" :visible.sync="dialogVisible" width="30%"  class="cust-dialog">
            <span>是否通过当前审核？</span>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="updateCheckStatus(1)">确 定</el-button>
            </span>
        </el-dialog> -->
        <el-dialog title="驳回原因" :visible.sync="dialogVisible2" width="30%" class="cust-dialog">
            <el-form :model="form" :rules="rules" ref="ruleForm" label-width="100px">
                <el-form-item label="驳回原因：" prop="rejectRemark">
                    <el-input type="textarea" v-model.trim="form.rejectRemark"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible2 = false">取 消</el-button>
                <el-button type="primary" @click="preReject()">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>
<script>
import 'element-ui/lib/theme-chalk/base.css';
import CollapseTransition from 'element-ui/lib/transitions/collapse-transition';
export default {
    name: '',
    components: { CollapseTransition },
    props: [''],
    data() {
        return {
            btnCreate: false,
            dialogVisible: false,
            dialogVisible2: false,
            showBox1: true,
            showBox2: true,
            showBox3: true,
            pathData: [],
            form: {
                complexResult: '',           //入账银行 - 入账账号
                receiptCompany: '',          //入账公司
                receiptBlank: '',            //入账银行
                receiptAccount: '',          //入账账号
                customerId: null,
                customerName: "",
                agreeReceiptDay: null,
                receiptYearMonth: this.$moment().format('YYYY-MM'),
                project: [0],
                remark: '',
                rejectRemark: ''     //审核驳回时备注
            },
            rules: {
                customerId: [
                    { required: true, message: '请选择客户', trigger: 'change' }
                ],
                receiptYearMonth: [
                    { required: true, message: '请选择收款年月', trigger: 'change' }
                ],
                project: [
                    { required: true, message: '请选择费用项目', trigger: 'change' }
                ],
                agreeReceiptDay: [
                    { required: true, message: '请选择约定到款日期', trigger: 'change' }
                ],
                receiptCompany: [
                    { required: true, message: '请选择入账公司', trigger: 'change' }
                ],
                complexResult: [
                    { required: true, message: '请选择入账银行', trigger: 'change' }
                ],
                rejectRemark: [
                    { required: true, message: '请输入驳回备注', trigger: 'change' }
                ],
            },
            loading: false,
            feeDetailTable: [
                { payYearMonth: '2023-08', payProject: '在户人数', personNumber: null, price: 1, unit: '元/人/月', countPrice: 0, serviceType: 0, fileList: [] },
                { payYearMonth: '2023-08', payProject: '驻场服务', personNumber: null, price: null, unit: '', countPrice: 0, serviceType: 1, fileList: [] },
                { payYearMonth: '2023-08', payProject: '外勤服务', personNumber: null, price: null, unit: '', countPrice: 0, serviceType: 2, fileList: [] },
                { payYearMonth: '2023-08', payProject: '其他服务', personNumber: null, price: null, unit: '', countPrice: 0, serviceType: 3, fileList: [] },
            ],
            listCustomerOption: [],      //客户下拉
            invoiceList: [],      //发票信息中发票列表
            billList: [],         //到款回单
            tableData: [],            //在户人数表格
            totalPeopleCount: 0,         //合计 - 在户人数
            companyArr: [],          //入账公司下拉
            bankArr: [],          //入账银行
            totaleTablePrice: 0,      //收费明细的总额
            rowDetail: {}                //收款单详情
        };
    },
    computed: {
        type() {
            //0 创建 1 编辑 2详情 3审核
            return parseInt(this.$route.query.type)
        },
        orderId() {
            //账单Id 用于编辑账单 没有时则为创建账单
            return this.$route.query.orderId || null
        }
    },
    watch: {
        tableData(newV, oldV) {
            console.log('newew', newV, oldV)
            if (newV.length == 0 && oldV.length == 0) return
            this.totalPeopleCount = newV.reduce((acc, curr) => acc + curr.registerNumber, 0);
            this.feeDetailTable[0].personNumber = this.totalPeopleCount
            this.feeDetailTable[0].countPrice = (this.feeDetailTable[0].personNumber * this.feeDetailTable[0].price).toFixed(2)
        },
        'form.receiptYearMonth': {
            handler(newV, oldV) {
                this.feeDetailTable = this.feeDetailTable.map(item => {
                    return {
                        ...item,
                        payYearMonth: newV
                    }
                })
            },
            immediate: true, // 立即触发观测函数
        },
        feeDetailTable: {
            handler(newV, oldV) {
                let tempV = newV.reduce((acc, curr) => acc + parseFloat(curr.countPrice), 0);
                this.totaleTablePrice = tempV.toFixed(2)
            },
            immediate: true,
            deep: true
        }

    },
    created() {
        let tabs = this.$store.state.tabs
        if (tabs) {
            this.pathData = this.$global.deepcopyArray(
                tabs[this.$route.meta.parentPath]
            )
        }
        if (this.type == 0) {
            this.pathData.push({ name: '创建收款单' })
        } else if (this.type == 1) {
            this.pathData.push({ name: '编辑收款单' })
        } else if (this.type == 2) {
            this.pathData.push({ name: '收款单详情' })
        }
        this.getlistCustomer()
        // this.getCurrentMonthNumberHolds()
        this.getDictList(10033)
        this.getDictList(10034)
    },
    beforeMount() { },
    mounted() {
        if (this.$route.query.orderId) {
            this.getOrderDetail()
        }
    },
    methods: {
        //审核通过
        showConfirmVerify() {
            let that = this
            this.$confirm('是否通过当前审核？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                showClose: false,
                customClass: 'pal-confirm',
                type: 'warning'
            })
                .then(() => {
                    that.updateCheckStatus(1)
                })
                .catch((err) => {
                    console.log(err)
                })
        },
        //获取收款单详情
        getOrderDetail() {
            let that = this
            var params = [
                { property: '_id', value: this.$route.query.orderId },
            ]
            this.$http({
                url: 'policy/bill/queryBillData',
                method: 'post',
                data: {
                    query: params
                }
            }).then(result => {
                console.log('详情--------', result.data.data.rows[0])
                let rowDetail = result.data.data.rows[0]
                this.form.receiptCompany = rowDetail.receiptCompany
                this.form.receiptBlank = rowDetail.receiptBlank
                this.form.receiptAccount = rowDetail.receiptAccount
                this.form.customerId = rowDetail.customerId
                this.form.customerName = rowDetail.customerName
                this.form.agreeReceiptDay = rowDetail.agreeReceiptDay
                this.form.receiptYearMonth = rowDetail.receiptYearMonth
                let tempStr = ''
                if (rowDetail.payRemark) {
                    tempStr += `${rowDetail.payRemark};`
                }
                if (rowDetail.fpRemark) {
                    tempStr += `${rowDetail.fpRemark};`
                }
                if (tempStr.length > 0) {
                    if (rowDetail.remark) {

                        this.form.remark = rowDetail.remark + ';' + tempStr
                    } else {
                        this.form.remark = tempStr
                    }
                } else {
                    this.form.remark = rowDetail.remark
                }
                if (this.form.remark && this.form.remark .endsWith(';')) {
                    this.form.remark  = this.form.remark .substring(0, this.form.remark .length - 1);
                }
                this.form.project = rowDetail.receiptProject.split(',').map(item => parseInt(item))
                this.form.complexResult = rowDetail.receiptBlank + '-' + rowDetail.receiptAccount
                this.rowDetail = rowDetail
                //回显收费明细
                // this.feeDetailTable = rowDetail.receiptDetailList.map(item => {
                // let tempFeeDetailTable = rowDetail.receiptDetailList.map(item => {
                //     return {
                //         ...item,
                //         fileList: item.payFiledList || []
                //     }
                // })
                rowDetail.receiptDetailList = rowDetail.receiptDetailList?rowDetail.receiptDetailList:[]
                const nameArray = this.feeDetailTable.map(el => {
                    const item = rowDetail.receiptDetailList.find(obj => obj.serviceType === el.serviceType);
                    if (item) {
                        return {
                            ...item,
                            fileList: item.payFiledList || []
                        }
                    } else {
                        return el
                    }

                });
                this.feeDetailTable = nameArray
                console.log('name----', nameArray)

                // 过滤一下 防止报错
                // if (rowDetail.receiptDetailList) {
                //     const nameArray = this.feeDetailTable.map(el => {
                //         const item = rowDetail.receiptDetailList.find(obj => obj.serviceType === el.serviceType);
                //         if (item) {
                //             return {
                //                 ...item,
                //                 fileList: item.payFiledList || []
                //             }
                //         } else {
                //             return el
                //         }

                //     });
                //     this.feeDetailTable = nameArray
                //     console.log('name----', nameArray)
                // }

                //回显到款文件
                this.billList = rowDetail.dkFileList
                //回显发票文件
                this.invoiceList = rowDetail.fpFileList
                this.kpAmount = rowDetail.kpAmount
                this.getCurrentMonthNumberHolds()
            })
        },
        //校验收费明细必填项是否都已经填写
        validateTable() {
            let tempSelectProject = this.feeDetailTable.filter(item => this.form.project.includes(item.serviceType))
            let allCount = tempSelectProject.map(item => item.personNumber)
            let allPrice = tempSelectProject.map(item => item.price)
            let allUnit = tempSelectProject.map(item => item.unit)
            console.log(allCount, allPrice, allUnit)
            if (allUnit.includes('')) {
                this.$message.warning('请完善收费明细所有必填项')
                return false
            } else {
                return true
            }
        },
        //焦点失去
        onBlurPersonNumber(event, index) {
            this.feeDetailTable[index].countPrice = (this.feeDetailTable[index].personNumber * this.feeDetailTable[index].price).toFixed(2)
        },
        //入账银行账号符合下拉选择
        onChangeComplexResult(e) {
            this.form.receiptBlank = e.split('-')[0]
            this.form.receiptAccount = e.split('-')[1]
        },
        //客户下拉选择
        onChangeCustomerId(e) {
            console.log(e)
            this.form.customerName = this.listCustomerOption.find(item => item.id == e).customerName
            this.getCurrentMonthNumberHolds()
            this.getAgreeReceiptDayByCustomer()
        },
        //获取客户对应的约定到款日期 需要将当月/下月 转换为YYYY-MM-dd 同时只有制单员才有权限将其修改
        async getAgreeReceiptDayByCustomer() {
            const { data: { code, data: result } } = await this.$http({
                url: `policy/sys/customer/getCustomerById/${this.form.customerId}`,
                method: 'post',
            })
            if (code == 200) {
                //拼装约定到款日期
                if (result.accountMonth && result.accountDay) {
                    this.form.agreeReceiptDay = this.getMonth(result.accountMonth) + '-' + result.accountDay.toString().padStart(2, '0')
                } else {
                    this.form.agreeReceiptDay = ''
                }
            }
        },
        getMonth(type) {
            let format = 'YYYY-MM';
            let date = this.$moment();
            if (type !== 1) {
                date.add(1, 'month');
            }
            return date.format(format);
        },
        //获取在户人数 默认取全客户下 当前年月的在户人数
        async getCurrentMonthNumberHolds() {
            const { data: { code, data: list } } = await this.$http({
                url: 'policy/cust/insured/getRegisterList',
                method: 'post',
                data: {
                    query: [
                        {
                            "property": "custId",
                            "value": this.form.customerId
                        },
                        {
                            "property": "dataMonth",
                            "value": this.form.receiptYearMonth
                        }
                    ]
                }
            })
            if (code == 200) {
                this.tableData = list
            }
        },
        //获取客户下拉
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
        onProjectChange(e) {
            // console.log(e)
        },
        // 上传文件之前
        onBeforeUpload(file) {
            this.showLoading('正在上传...')
        },
        // 上传文件成功之后
        onSuccessFile(response, index) {
            this.closeLoading()
            if (response.code === 200) {
                let item = response.data[0]
                this.feeDetailTable[index].fileList.push({
                    fileId: item.fileID,
                    fileName: item.fileName,
                    fileType: 2
                })
            } else {
                this.$message.error(response.message ? response.message : '上传失败请重试')
            }
            console.log(this.feeDetailTable)
        },
        // 确认上传
        comfirmUpload() {
            let that = this
            if (this.fileList.length === 0) {
                this.$message.warning('请先上传文件')
                return
            }
            that.showLoading()
            that.$http({
                url: `policy/customer/payFee/savePayFeeFiles/${this.rowData.uuid}`,
                method: 'post',
                data: this.fileList
            }).then(data => {
                that.closeLoading()
                if (data.data.code === 200) {
                    that.$message.success('操作成功')
                    that.show = false
                }
            }).catch(() => {
                this.closeLoading()
            })
        },
        downLoadFile(item) {
            this.$downloadFile(`/policy/sys/file/download/${item.fileId}`, 'get')
        },
        removeFile(index, mainIndex) {
            this.$confirm('是否确定删除？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                showClose: false,
                customClass: 'pal-confirm',
                type: 'warning'
            }).then(() => {
                this.feeDetailTable[mainIndex].fileList.splice(index, 1)
            }).catch(() => {
            })
        },
        onSuccessInvoiceFile(response) {
            this.closeLoading()
            if (response.code === 200) {
                let item = response.data[0]
                this.invoiceList.push({
                    fileId: item.fileID,
                    fileName: item.fileName,
                    fileType: 2
                })
            } else {
                this.$message.error(response.message ? response.message : '上传失败请重试')
            }
        },
        removeInvoiceFile(index) {
            this.$confirm('是否确定删除？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                showClose: false,
                customClass: 'pal-confirm',
                type: 'warning'
            }).then(() => {
                this.invoiceList.splice(index, 1)
            }).catch(() => {
            })
        },
        removeBill(index) {
            this.$confirm('是否确定删除？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                showClose: false,
                customClass: 'pal-confirm',
                type: 'warning'
            }).then(() => {
                this.billList.splice(index, 1)
            }).catch(() => {
            })
        },
        showLoading(msg) {
            this.loading = this.$loading({
                target: document.body,
                lock: true,
                text: msg || '请稍候...',
                spinner: 'el-icon-loading',
                background: 'rgba(255, 255, 255, 0.7)'
            })
        },
        closeLoading() {
            if (this.loading && this.loading.close) {
                this.loading.close()
            }
        },
        //获取字典值  10033 - 入账公司 10034 - 入账银行及其账号
        async getDictList(datakey) {
            const { data: { code, data: result } } = await this.$http({
                url: `/policy/sys/dict/getDict/${datakey}`,
                method: "get"
            })
            if (code == 200) {
                if (datakey == 10033) {
                    this.companyArr = result
                } else if (datakey == 10034) {
                    this.bankArr = [
                        {
                            dictCode: result[0].dictCode + '-' + result[1].dictCode,
                            dictName: result[0].dictName + '-' + result[1].dictName
                        }
                    ]
                }
            }
        },
        //创建收款单
        createBillOrder() {
            this.$refs.form.validate(async (valid, errorMessage) => {
                if (valid) {
                    //校验收费表格明细的必填项是否已经都填写了
                    if (!this.validateTable()) return
                    let tempSelectProject = this.feeDetailTable.filter(item => this.form.project.includes(item.serviceType))
                    let dataObj = {
                        ...this.rowDetail,
                        ...this.form,
                        _id: this.orderId,
                        receiptProject: this.form.project.toString(),
                        receiptDetailList: tempSelectProject.map(item => {
                            return {
                                ...item,
                                price: parseFloat(item.price).toFixed(2),
                                countPrice: parseFloat(item.countPrice).toFixed(2),
                                payFiledList: item.fileList.length > 0 ? item.fileList : []
                            }
                        }),

                        payCount: this.totaleTablePrice
                    }
                    console.log('看看传参', dataObj)
                    this.btnCreate = true
                    // return
                    const { data: { code, data: result } } = await this.$http({
                        url: 'policy/bill/addOrUpdateBillData',
                        method: 'post',
                        data: dataObj
                    })
                    if (code == 200) {
                        this.$message.success('操作成功')
                        setTimeout(() => {
                            this.$router.go(-1)
                        }, 500);
                    }
                    this.btnCreate = false
                }
            })

        },
        preReject() {
            let that = this
            this.$refs.ruleForm.validate((validate) => {
                if (validate) {
                    that.updateCheckStatus(3)
                }
            })
        },
        //审核状态修改
        async updateCheckStatus(status) {
            //1 通过 3驳回
            const { data: { code, data: result } } = await this.$http({
                url: 'policy/bill/updateCheckStatus',
                method: 'post',
                data: {
                    _id: this.rowDetail._id,
                    handleStatus: status,
                    rejectRemark: this.form.rejectRemark || ''
                }
            })
            if (code == 200) {
                this.$message.success('操作成功')
                if (status == 1) {
                    this.dialogVisible = false
                } else {
                    this.dialogVisible2 = false
                    this.rejectRemark = ''
                }
                setTimeout(() => {
                    this.$router.go(-1)
                }, 500)
            }
        }
    },
}
</script>
<style lang='less' scoped>
.display-center {
    // display: flex;
    // justify-content: center;
}

.text-center {
    text-align: center;
}

.iconImg {
    width: 25px;
    height: 25px;
    cursor: pointer;
    padding-left: 6px;
}

.left-container {
    width: 100px;
    display: flex;
    text-align: center;
    align-items: center;
    background: #81d3f8;
    justify-content: center;
    border-radius: 5px 0 0 5px;
}

.bottom-layout {
    // position: fixed;
    // bottom: 0;
}

.row {
    display: flex;
    margin-bottom: 15px;

    .lab {
        width: 90px;
        text-align: right;
        line-height: 32px;
    }

    .sel {
        flex: 1;
        line-height: 32px;
    }
}

.file-list {
    margin-left: 10px;

    .file {
        display: inline-block;
        background: #f1f8ff;
        border-radius: 10px;
        padding: 5px 10px;
        position: relative;
        cursor: pointer;
        margin-right: 15px;
        margin-bottom: 10px;

        &:hover {
            color: #3e82ff;
            text-decoration: underline;
        }

        .file-remove {
            position: absolute;
            right: -5px;
            top: -10px;
            width: 18px;
            height: 18px;
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

.max-width-60 {
    max-width: 25px !important;
    width: 25px !important;
    // background-color:#ff00ff;

    /deep/ .el-input-number {
        max-width: 100% !important;
        text-align: center;
    }
}

.over-file {
    max-width: 280px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow-x: hidden;
    display: inline-block;
}

.td-30 {
    width: 30% important;
}

.ml-20 {
    margin-left: 20px !important;
}

.remark-p {
    max-width: 600px !important;
    word-wrap: break-word;
    font-size: 14px;
}
</style>
