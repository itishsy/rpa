<template>
    <div class="content spl-container">
        <!-- 导航 -->
        <breadcrumb :data="pathData">
        </breadcrumb>
        <!-- Tabs -->
        <div style="padding: 20px 20px 0 20px">
            <palTab :tabs="tabs" v-model="tabActive" :type="2">
            </palTab>

            <!--社保-->
            <div v-show="tabActive == 0">
                <!-- 搜索栏 -->
                <SearchLayout :type="1" @add="addRow" @query="doQuery" :hasRolePermission="hasRolePermission" />
                <div>
                    <!-- 表格 -->
                    <dTable @fetch="getSocialTable" ref="socialTable" :tableHeight="tableHeight" :paging="true"
                        :showSelection="true" :showIndex="true" rowKey="id" :rowHeight="45"
                        @selection-change="handleSelectionChange" :row-style="handleRowStyle">
                        <u-table-column prop="addrName" label="参保城市" min-width="90"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="accountNumber" label="申报账户" min-width="180"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="systemTypeName" label="申报系统" min-width="100"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="feeTypeName" label="缴费类型" min-width="90"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="periodOfPayment" label="费款所属期" min-width="100"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="payNumber" label="缴费人数" min-width="80"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="amount" label="缴费金额(元)" min-width="110"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="statusName" label="缴费状态" min-width="80"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="billPlant" label="缴费制单计划" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="createTime" label="创建时间" min-width="160" :show-overflow-tooltip="true">
                            <template scope="scope">
                                <span>{{ $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="billTypeName" label="缴费方式" min-width="80"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="serialNumber" label="流水号" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="comment" label="原因备注" min-width="200"
                            :show-overflow-tooltip="true"></u-table-column>

                        <u-table-column fixed="right" label="操作" width="210">
                            <template slot-scope="scope">
                                <el-button type="text" size="small" @click="viewRequest('1', scope.row)">查看</el-button>
                                <!-- 缴费状态为未核定，才有立即核定 -->
                                <el-button v-if="scope.row.status == 0" type="text" size="small"
                                    @click="viewRequest('2', scope.row)">立即核定</el-button>
                                <!-- 缴费状态为已核定，且缴费方式为手动缴费 -->
                                <el-button v-if="scope.row.status == 1 && scope.row.billType == 1" type="text" size="small"
                                    @click="showRobotDialog('social', scope.row)">缴费</el-button>
                                <!-- 管理员才有,并且状态不是已缴费和缴费异常 -->

                              <!-- 缴费状态为已缴费 ，才有上传凭证 -->
                              <el-button v-if="scope.row.status == 2" type="text" size="small" @click="handleUploadPayFee(1, scope.row)">上传凭证</el-button>
                                <el-button
                                    v-if="$global.hasPermission('commercialPaymentAbolish') && ![2, 3].includes(scope.row.status)"
                                    type="text" size="small" @click="withdraw('social', 'single', scope.row)"
                                    style="color:red">作废</el-button>
                            </template>
                        </u-table-column>
                        <template slot="pagination-btns">
                            <div class="display-flex">
                                <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                                    @click="exportTableData(1)">批量下载凭证</el-button>
                            </div>
                        </template>
                    </dTable>
                </div>
            </div>
            <!-- 公积金 -->
            <div v-show="tabActive == 1">
                <!-- 搜索栏 -->
                <SearchLayout :type="2" @add="addRow" @query="doQuery" :hasRolePermission="hasRolePermission" />
                <div>
                    <!-- 表格 -->
                    <dTable @fetch="getAccfundTable" ref="accfundTable" :tableHeight="tableHeight" :paging="true"
                        :showSelection="true" :showIndex="true" rowKey="id" :rowHeight="45"
                        @selection-change="handleSelectionChange" :row-style="handleRowStyle">
                        <u-table-column prop="addrName" label="参保城市" min-width="90"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="accountNumber" label="申报账户" min-width="180"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="systemTypeName" label="公积金类型" min-width="100"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="feeTypeName" label="缴费类型" min-width="90"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="periodOfPayment" label="费款所属期" min-width="100"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="ratio" label="参保比例" min-width="80" :show-overflow-tooltip="true">
                            <template slot-scope="scope">
                                <span v-if="scope.row.ratio || scope.row.ratio == 0">{{ scope.row.ratio * 100 }}%</span>
                                <span v-else>-</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="payNumber" label="缴费人数" min-width="80"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="amount" label="缴费金额(元)" min-width="110"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="statusName" label="缴费状态" min-width="80"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="billPlant" label="缴费制单计划" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="createTime" label="创建时间" min-width="160" :show-overflow-tooltip="true">
                            <template scope="scope">
                                <span>{{ $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="billTypeName" label="缴费方式" min-width="80"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="serialNumber" label="流水号" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="comment" label="原因备注" min-width="200" :show-overflow-tooltip="true">
                        </u-table-column>
                        <u-table-column fixed="right" label="操作" width="210">
                            <template slot-scope="scope">
                                <el-button type="text" size="small" @click="viewRequest('1', scope.row)">查看</el-button>
                                <!-- 缴费状态为未核定 ，才有立即核定 -->
                                <el-button v-if="scope.row.status == 0" type="text" size="small"
                                    @click="viewRequest('2', scope.row)">立即核定</el-button>
                                <!-- 缴费状态为已核定，且缴费方式为手动缴费 -->
                                <el-button v-if="scope.row.status == 1 && scope.row.billType == 1" type="text" size="small"
                                    @click="showRobotDialog('accfund', scope.row)">缴费</el-button>
                                <!-- 管理员才有 -->

                              <!-- 缴费状态为已缴费 ，才有上传凭证 -->
                              <el-button type="text" size="small" v-if="scope.row.status == 2" @click="handleUploadPayFee(2, scope.row)">上传凭证</el-button>
                                <el-button
                                    v-if="$global.hasPermission('commercialPaymentAbolish') && ![2, 3].includes(scope.row.status)"
                                    type="text" size="small" @click="withdraw('accfund', 'single', scope.row)"
                                    style="color:red">作废</el-button>
                            </template>
                        </u-table-column>
                        <template slot="pagination-btns">
                            <div class="display-flex">
                                <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                                    @click="exportTableData(2)">批量下载凭证</el-button>
                            </div>

                        </template>
                    </dTable>
                </div>
            </div>
        </div>
        <!--作废-->
        <el-dialog :visible.sync="withdrawData.show" title="作废" width="600px" class="cust-dialog" :show-close="true"
            :close-on-click-modal="false" :before-close="cancelWithdraw">
            <div class="template-dialog-box">
                <el-form :model="withdrawData" ref="withdrawForm">
                    <p v-if="withdrawData.type2 == 'batch'">已筛选<span style="color:#FD6154;"> {{ withdrawData.ids.length }}
                        </span>条待提交/待申报的记录</p>
                    <p class="mb-20">一旦作废，缴费状态变更为异常!</p>
                    <div class="display-flex">
                        <label class="required-pre w-70" style="vertical-align: top">原因：</label>
                        <el-form-item prop="reason" class="flex1" :rules="[
                            { required: true, message: '请输入作废原因', trigger: 'blur' },
                            { required: true, min: 0, max: 100, message: '作废原因在100个字符以内', trigger: 'change' },
                        ]">
                            <el-input v-model.trim="withdrawData.reason" type="textarea" rows="3" resize="none"></el-input>
                            <p style="position: absolute;right:0;font-size:12px;" v-if="withdrawData.reason.length > 80">
                                {{ withdrawData.reason.length }}/<span style="color:#FD6154">100</span></p>
                        </el-form-item>
                    </div>
                </el-form>
                <div class="text-right mt-30">
                    <el-button size="small" class="mr-15 w-80" @click="cancelWithdraw()">取消</el-button>
                    <el-button size="small" class="w-80" type="primary" @click="withdrawSumbit()"
                        :loading="withdrawData.btnLoading">确定</el-button>
                </div>
            </div>
        </el-dialog>
        <!-- 核定有误dialog -->
        <el-dialog :visible.sync="errorDialog.show" title="错误反馈" width="600px" class="cust-dialog" :show-close="true"
            :close-on-click-modal="false" :before-close="cancelErrordraw">
            <div class="template-dialog-box">
                <el-form :model="errorDialog" ref="errorDrawForm">
                    <p class="mb-20">请填写错误原因，以便我们能够更好地了解问题并进行修正</p>
                    <div class="display-flex">
                        <label class="required-pre w-70" style="vertical-align: top">原因：</label>
                        <el-form-item prop="reason" class="flex1" :rules="[
                            { required: true, message: '请输入错误原因', trigger: 'blur' },
                            { required: true, min: 0, max: 100, message: '错误原因在100个字符以内', trigger: 'change' },
                        ]">
                            <el-input v-model.trim="errorDialog.reason" type="textarea" rows="3" resize="none"></el-input>
                            <p style="position: absolute;right:0;font-size:12px;" v-if="errorDialog.reason.length > 80">
                                {{ errorDialog.reason.length }}/<span style="color:#FD6154">100</span></p>
                        </el-form-item>
                    </div>
                </el-form>
                <div class="text-right mt-30">
                    <el-button size="small" class="mr-15 w-80" @click="cancelErrordraw()">取消</el-button>
                    <el-button size="small" class="w-80" type="primary" @click="withErrorSumbit()"
                        :loading="errorDialog.btnLoading">确定</el-button>
                </div>
            </div>
        </el-dialog>
        <!-- 查看drawer -->
        <el-drawer :title="detailDrawer.title" :visible.sync="detailDrawer.show" :wrapperClosable="false"
            custom-class="spl-filter-drawer detail-drawer">
            <div class="pt-20 pl-20 pr-20">
                <!-- <p class="fw-blod pb-10">账户信息</p> -->
                <CommonHeader header="账户信息"></CommonHeader>
                <el-row type="flex" style="flex-wrap:wrap;padding: 10px;">
                    <el-col :span="8">
                        <div class="mb-20">参保城市：{{ detailForm.addrName }}</div>
                    </el-col>
                    <el-col :span="8">
                        <div class="mb-20">申报账户：{{ detailForm.accountNumber }}</div>
                    </el-col>
                    <el-col :span="8">
                        <div class="mb-20">{{ detailForm.businessType == 1 ? '申报系统' : '公积金类型' }}：{{
                            detailForm.systemTypeName }}
                        </div>
                    </el-col>
                    <el-col :span="8">
                        <div class="mb-20">缴费制单计划：{{ detailForm.billPlant }}</div>
                    </el-col>
                </el-row>
                <CommonHeader header="缴费信息"></CommonHeader>
                <el-row type="flex" style="flex-wrap:wrap;padding: 10px;">
                    <el-col :span="8">
                        <div class="mb-20">费款所属期： {{ detailForm.periodOfPayment }}</div>
                    </el-col>
                    <el-col :span="8">
                        <div class="mb-20">缴费人数：{{ detailForm.payNumber }}</div>
                    </el-col>
                    <el-col :span="8">
                        <div class="mb-20">缴费金额（元）： {{ detailForm.amount }}</div>
                    </el-col>
                    <el-col :span="8">
                        <div class="mb-20">缴费类型： {{ detailForm.feeTypeName }}</div>
                    </el-col>
                    <el-col :span="8">
                        <div class="mb-20">缴费状态： {{ detailForm.statusName }}</div>
                    </el-col>
                    <el-col :span="8">
                        <div class="mb-20">流水号： {{ detailForm.serialNumber }}</div>
                    </el-col>
                    <el-col :span="8" v-if="detailForm.businessType == 2">
                        <div class="mb-20">参保比例： {{ detailForm.ratio || detailForm.ratio == 0 ? detailForm.ratio * 100 + '%'
                            : '-' }}</div>
                    </el-col>
                </el-row>
                <CommonHeader header="单据凭证"></CommonHeader>
                <div style="padding: 10px;">
                    <el-upload action="#" list-type="picture-card" :auto-upload="false" :file-list="detailForm.files"
                        :disabled="true">
                        <i slot="default" class="el-icon-plus"></i>
                        <div slot="file" slot-scope="{file}">
                            <div v-if="checkFileIsPic(file)" class="file-item">
                                <img class="el-upload-list__item-thumbnail" :src="file.fileUrl" alt="">
                                <span class="file-name">{{ file.fileName }}</span>
                            </div>

                            <div v-else class="file-item">
                                <img src="../../assets/images/upload_blank.jpg" alt="" width="120" height="120">
                                <span class="file-name">{{ file.fileName }}</span>
                            </div>


                            <span class="el-upload-list__item-actions">
                                <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
                                    <i class="el-icon-zoom-in"></i>
                                </span>
                                <span v-if="!disabled" class="el-upload-list__item-delete" @click="downLoadFile(file)">
                                    <i class="el-icon-download"></i>
                                </span>
                            </span>
                        </div>
                    </el-upload>
                </div>
                <CommonHeader header="过程"></CommonHeader>
                <div style="padding: 10px;">
                    <el-steps direction="vertical">
                        <el-step class="step-item blue" v-for="item in detailForm.processes" :key="item.id">
                            <template #icon>
                                <span>{{ item.processTypeName }}</span>
                            </template>
                            <template #title>
                                <span class="title">{{ item.createName }}</span>
                            </template>
                            <template #description>
                                <span class="desc">{{ item.createTime || '-' }}</span>
                            </template>
                        </el-step>
                        <!-- <el-step class="step-item opopp">
                            <template #icon>
                                <span>核定</span>
                            </template>
                            <template #title>
                                <span class="title">gzsb</span>
                            </template>
                            <template #description>
                                <span class="desc">2023-06-22 15:30</span>
                            </template>
                        </el-step> -->


                    </el-steps>
                </div>
            </div>
            <div class="drawer-footer-buts">
                <div v-if="detailDrawer.type == 2">
                    <el-checkbox class="check" v-model="checked">我已阅读，一旦确认核定，说明我已授权；机器人为我司申报并提交此申报账户该月缴费信息</el-checkbox>
                    <span v-if="showRaidoError" style="color:red;position: absolute;left:10px">请勾选我已阅读</span>
                    <el-row type="flex" style="position: absolute;top: 40px;left:50%;transform: translateX(-50%)">

                        <el-button size="small" class="w-80 check-btn" type="primary"
                            @click="showErrorDialog">核定有误</el-button>
                        <el-button size="small" class="w-80 check-btn" type="primary" @click="sumbtApprove">确认核定</el-button>
                    </el-row>
                </div>
                <div v-else>
                    <el-button size="small" class="w-80" type="primary" @click="cancelDetailDrawer">关闭</el-button>
                </div>

            </div>
        </el-drawer>
        <backtop />

        <!-- 添加dialog -->
        <el-dialog title="添加缴费记录" :visible.sync="addVisible" width="30%" :close-on-click-modal="false" custom-class="add-dialog" >
            <el-form :model="addForm" ref="addForm" label-width="110px" :rules="rules">
                <el-form-item label="缴费方式：" prop="billType">
                    <el-radio v-model="addForm.billType" :label="1">手动缴费</el-radio>
                    <el-radio v-model="addForm.billType" :label="2">自动缴费</el-radio>
                </el-form-item>
                <el-form-item label="" label-width="0px" prop="addrName">
                    <p style="display: inline-block;padding-right: 10px;width: 100px;text-align: right;" class="required">
                        参保城市：</p>
                    <addrSelector v-model="addForm.addrName" :addrArr="addrArr" @changeAddrSelect="getSocialAddrId"
                        width="300px" class="search-row-item"></addrSelector>
                    <template slot="error">
                        <span class="custom-error">请选择参保城市</span>
                    </template>
                </el-form-item>
                <el-form-item label="业务类型：" prop="businessType">
                    <el-select v-model="addForm.businessType" placeholder="请选择" class="w-300"
                        @change="onChangeBusinessType">
                        <el-option label="社保" :value="1"></el-option>
                        <el-option label="公积金" :value="2"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item :label="addForm.businessType == 2 ? '公积金类型：' : '申报系统：'" prop="systemType">
                    <el-select v-model="addForm.systemType" placeholder="请选择" class="w-300">
                        <el-option v-for="it in addFormSelects.systemType" :key="it.dictCode" :label="it.dictName"
                            :value="it.dictCode"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="申报账户：" prop="accountNumber">
                    <el-select v-model="addForm.accountNumber" class="w-300" placeholder="请选择申报账户"
                        @change="changeCompany(tabActive == 0 ? 1 : 2)" clearable filterable>
                        <el-option v-for="it in addFormSelects.companyArr" :key="it.itemId" :label="it.name"
                            :value="it.accountNumber"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="参保比例：" prop="ratio" v-if="addForm.businessType == 2">
                    <el-select v-model="addForm.ratio" class="w-300" placeholder="请选择参保比例" clearable>
                        <el-option v-for="it in addFormSelects.ratioArr" :key="it" :label="it" :value="it"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="缴费类型：" prop="feeType">
                    <el-select v-model="addForm.feeType" placeholder="请选择" class="w-300" multiple collapse-tags>
                        <el-option v-for="it in addFormSelects.feeType" :key="it.dictCode" :label="it.dictName"
                            :value="it.dictCode"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="缴费所属期：" prop="periodOfPayment">
                    <el-date-picker type="month" placeholder="选择日期" v-model="addForm.periodOfPayment" value-format="yyyy-MM"
                        class="w-300"></el-date-picker>
                </el-form-item>
                <el-form-item label="缴费人数：" prop="payNumber">
                    <el-input v-model.number="addForm.payNumber" autocomplete="off" class="w-300"
                        placeholder="请输入"></el-input>
                </el-form-item>
                <el-form-item label="缴费金额：" prop="amount">
                    <el-input v-model="addForm.amount" autocomplete="off" class="w-300" placeholder="请输入"></el-input>
                </el-form-item>
                <el-form-item label-width="0px" prop="files">
                    <span style="width: 100px;text-align: right;display: inline-block;padding-right:10px"
                        class="required">单据凭证：</span>
                    <template slot="error">
                        <span class="custom-error">请上传单据凭证</span>
                    </template>
                    <div class="w-300" style="display: inline-block;">
                        <el-upload ref="upload" :show-file-list="false" :auto-upload="true"
                            :headers="$global.setUploadHeaders()" :action="$global.uploadFileUrl"
                            :on-success="uploadChange">
                            <!--  -->
                            <span class="text-blue">+ 上传</span>
                        </el-upload>

                    </div>
                </el-form-item>

            </el-form>
            <div style="display: flex;flex-direction: column;    margin: 5px 0 0 100px;" class="w-300">
                <div class="inlineBlock custom-upload" style="vertical-align: top; margin-top: 8px"
                    v-for="(fileItem, fileIndex) in addForm.files" :key="fileItem.fileId">
                    <span class="el-upload-list__item is-ready">
                        <a class="el-upload-list__item-name f-cursor" @click="handlePreviewFile(fileItem.fileId)">
                            <i class="el-icon-document"></i>
                            {{ fileItem.fileName }}
                        </a>
                        <label class="el-upload-list__item-status-label">
                            <i class="el-icon-upload-success el-icon-circle-check"></i>
                        </label>
                        <i class="el-icon-close" @click="
                            handleDelFile(
                                fileItem.fileId,
                                fileIndex
                            )
                            "></i>
                        <i class="el-icon-close-tip"></i>
                    </span>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="addVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitAdd">确 定</el-button>
            </span>
        </el-dialog>
        <!-- 预览dialog -->
        <el-dialog :visible.sync="dialogVisible">
            <img width="100%" :src="dialogImageUrl" alt="">
        </el-dialog>
        <!-- 机器人立即执行dialog -->
        <el-dialog :visible.sync="robotVisible" title="确定缴费" width="30%">
            <span>确认后机器人将执行缴费流程！</span>
            <span slot="footer" class="dialog-footer">
                <el-button @click="robotVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitRobot">确 定</el-button>
            </span>
        </el-dialog>

      <!--上传凭证-->
      <uploadPayFeeFiles ref="uploadPayFeeFiles"></uploadPayFeeFiles>

      <!--批量下载凭证-->
      <batchDownloadPayFeeFiles ref="batchDownloadPayFeeFiles"></batchDownloadPayFeeFiles>
    </div>
</template>
<script>
import dTable from '@/components/common/table'
import palTab from '@/components/common/pal-tab'
import addrSelector from '@/components/common/addrSelector'
import SearchLayout from './component/SearchLayout'
import CommonHeader from '../insuredCity/components/CommonHeader.vue'
import uploadPayFeeFiles from './component/uploadPayFeeFiles.vue'
import batchDownloadPayFeeFiles from './component/batchDownloadPayFeeFiles.vue'
export default {
    name: '',
    components: { dTable, palTab, addrSelector, SearchLayout, CommonHeader, uploadPayFeeFiles, batchDownloadPayFeeFiles },
    props: [''],
    data() {
        return {
            loading: {},
            pathData: [],
            tabs: ['社保', '公积金'],
            tabActive: 0,
            currentRow: {},
            bussinessType: '1',
            socialDetail: {
                show: false,
                row: {},
                tabActive: 0,
                recordData: [],
                tabs: ['社保参保详情', '操作记录', '异常记录']
            },
            accfundDetail: {
                show: false,
                row: {},
                tabActive: 0,
                recordData: [],
                tabs: ['公积金参保详情', '操作记录', '异常记录']
            },
            withdrawData: {
                show: false,
                reason: '',
                ids: [],
                type: '',
                type2: ""
            },
            detailDrawer: {
                title: '查看',
                show: false,
                type: 1         //详情drawer的类型 1查看 2核定
            },
            showRaidoError: false,       //核定勾选错误信息
            checked: false,          //核定勾选
            addVisible: false,            //添加缴费记录弹框是否显示
            addForm: {
                files: [],                   //用于添加时 单据凭证上传
                billType: 1,                //默认选中手动缴费
                systemType: null,            //申报系统
                accountNumber: null,
                addrName: null,
                addrId: null,
                feeType: null,
                periodOfPayment: null,
                payNumber: null,
                amount: null,
                ratio: null,
            },                          //添加缴费表单
            addFormSelects: {            //添加缴费表单中所需的下拉项
                companyArr: [],               //参保账户
                systemType: [],               //申报系统
                feeType: [],                  //缴费类型
                ratioArr: [0.05, 0.06, 0.07, 0.08, 0.09, 0.10, 0.11, 0.12]  //公积金下参保比例
            },
            rules: {                    //缴费表单验证
                billType: [
                    { required: true, message: '请选择缴费方式', trigger: 'change' }
                ],
                feeType: [
                    { required: true, message: '请选择缴费类型', trigger: 'change' }
                ],
                addrName: [
                    { required: true, message: '请选择参保城市', trigger: 'change' }
                ],
                periodOfPayment: [
                    { required: true, message: '请选择缴费所属期', trigger: 'change' }
                ],
                accountNumber: [
                    { required: true, message: '请选择申报账户', trigger: 'change' }
                ],
                businessType: [
                    { required: true, message: '请选择业务类型', trigger: 'change' }
                ],
                addrId: [
                    { required: true, message: '请选择参保城市', trigger: 'change' }
                ],
                systemType: [
                    { required: true, message: '请选择申报系统', trigger: 'change' }
                ],
                amount: [
                    { required: true, message: '请填写缴费金额', trigger: 'blur' },
                    { validator: this.validateAmount, trigger: 'blur' }
                ],
                payNumber: [
                    { required: true, message: '请填写缴费人数', trigger: 'blur' },
                    { type: 'number', message: '缴费人数必须为数字值' }
                ],
                files: [
                    { required: true, message: '请上传单据凭证', trigger: ['change', 'blur'] }
                ]
            },
            addrArr: [],                //选择地址
            active: 1,
            dialogImageUrl: '',
            dialogVisible: false,
            disabled: false,
            fileViewList: [
                { name: 'food.jpeg', url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100' },
                { name: 'food2.jpeg', url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100' },
                { name: 'git指南.pdf', url: 'https://www.gjtool.cn/pdfh5/git.pdf' }
            ],
            socialSearch: {
                accountNumber: null,              //申报账户
                /*minCreateDate: this.$moment().startOf('month').format('YYYY-MM-DD'),      //最小创建时间
                maxCreateDate: this.$moment().format('YYYY-MM-DD'),        //最大创建时间*/
                minCreateDate: '',        //最大创建时间
                maxCreateDate: '',        //最大创建时间
                minPeriodOfPayment: this.$moment().format('YYYY-MM'),       //最小费款所属期
                maxPeriodOfPayment: this.$moment().format('YYYY-MM'),       //最大费款所属期
                statuses: []
            },
            accfundSearch: {
                accountNumber: null,              //申报账户
                /*minCreateDate: this.$moment().startOf('month').format('YYYY-MM-DD'),      //最小创建时间
                minCreateDate: this.$moment().startOf('month').format('YYYY-MM-DD'),      //最小创建时间*/
                minCreateDate: '',        //最大创建时间
                maxCreateDate: '',        //最大创建时间
                minPeriodOfPayment: this.$moment().format('YYYY-MM'),       //最小费款所属期
                maxPeriodOfPayment: this.$moment().format('YYYY-MM'),       //最大费款所属期
                statuses: []
            },
            socialSearch2: {
                addrName: '',
                addrId: '',
                company: '',
                itemId: '',
                startMonth: '',
                createStartTime: this.$moment().startOf('months').format('YYYY-MM-DD'),
                createEndTime: this.$moment().format('YYYY-MM-DD'),
                keyword: '',
                statusList: [],
                typeList: [],
                insuredStartDate: '',
                insuredEndDate: '',
                setEmpIdCards: [],
                setEmpNames: [],
                accountNumber: '',
                filterTags: [],
                companyArr: []
            },
            accfundSearch2: {
                addrName: '',
                addrId: '',
                company: '',
                itemId: '',
                startMonth: '',
                createStartTime: this.$moment().startOf('months').format('YYYY-MM-DD'),
                createEndTime: this.$moment().format('YYYY-MM-DD'),
                keyword: '',
                statusList: [],
                typeList: [],
                insuredStartDate: '',
                insuredEndDate: '',
                setEmpIdCards: [],
                setEmpNames: [],
                accountNumber: '',
                filterTags: [],
                companyArr: []
            },
            socialSections: [],                          //社保表格勾选
            accfundSections: [],                          //公积金表格勾选
            detailForm: {},                               //detailDrawer中的数据
            robotVisible: false,                         //机器人立即执行dialog
            errorDialog: {
                show: false,
                reason: '',
            },
          highlight: {
            id: '',
            businessType: '',
            status: ''
          }
        };
    },
    computed: {
        getType() {
            return this.bussinessType == '1' ? this.socialDetail : this.accfundDetail
        },
        tableHeight: function () {
            return this.$global.bodyHeight - 390 + 'px'
        },
        hasRolePermission() {
            return this.$store.state.userData.roles.includes("admin")
        }
    },
    watch: {

    },
    created() {
        let tabs = this.$store.state.tabs
        if (tabs) {
            this.pathData = tabs[this.$route.path]
        }
        this.getAddrArr()
        this.updateMenuDotCount()

    },
    beforeMount() { },
    mounted() {
      if (this.$route.query.status) {
        let statuses = []
        statuses.push(Number(this.$route.query.status))
        this.socialSearch.statuses = statuses
        this.accfundSearch.statuses = statuses
      }
      if (this.$route.query.createStartTime) {
        this.socialSearch.minCreateDate = this.$route.query.createStartTime
        this.accfundSearch.minCreateDate = this.$route.query.createStartTime
      }
      if (this.$route.query.createEndTime) {
        this.socialSearch.maxCreateDate = this.$route.query.createEndTime
        this.accfundSearch.maxCreateDate = this.$route.query.createEndTime
      }
      if(this.$route.query.status || this.$route.query.createStartTime || this.$route.query.createEndTime){
        this.socialSearch.minPeriodOfPayment = ''
        this.socialSearch.maxPeriodOfPayment = ''
        this.accfundSearch.minPeriodOfPayment = ''
        this.accfundSearch.maxPeriodOfPayment = ''
      }
      if (this.$route.query.businessType === '2') {
        this.tabActive = 1
      }
      // (从首页消息通知跳转进来需要高亮)
      if (this.$route.query.highlightId) {
        this.highlight.id = this.$route.query.highlightId
        this.highlight.businessType = this.$route.query.businessType
        this.highlight.status = this.$route.query.status
      }
        this.getSocialTable()
        this.getAccfundTable()
    },
    methods: {
        //金额验证
        validateAmount(rule, value, callback) {
            if (!value) {
                callback(new Error('请输入金额'));
            } else if (!/^\d+(\.\d{1,2})?$/.test(value)) {
                callback(new Error('请输入有效的金额'));
            } else {
                callback();
            }
        },
        //S=社保
        getSocialTable() {
            let that = this
            let search = this.socialSearch
            //   if(!search.addrName){
            //     this.$message.warning('请先选择参保城市')
            //     return
            //   }
            var params = [
                { property: 'businessType', value: 1 },
                { property: 'accountNumber', value: search.accountNumber },
                { property: 'minCreateDate', value: search.minCreateDate },
                { property: 'maxCreateDate', value: search.maxCreateDate },
                { property: 'minPeriodOfPayment', value: search.minPeriodOfPayment },
                { property: 'maxPeriodOfPayment', value: search.maxPeriodOfPayment },
                { property: 'statuses', value: search.statuses }
            ]

            this.$refs.socialTable.fetch({
                query: params,
                method: 'post',
                url: '/policy/customer/payFee/page'
            })
        },
        doQuery(searchParmas) {
            if (this.tabActive === 0) {
                //获取社保查询
                this.socialSearch = searchParmas
                this.getSocialTable()
            } else {
                //获取公积金查询
                this.accfundSearch = searchParmas
                this.getAccfundTable()
            }
        },

        //获取错误数
        getErrorCount(row) {
            let regex = /\((\d+)\)/;
            let match = row.match(regex);

            if (match) {
                let contentInsideParentheses = match[1];
                if (contentInsideParentheses > 99) {
                    return 99
                } else {
                    return contentInsideParentheses
                }
            }
        },
        //查看详情 type  1:查看 2：核定
        async viewRequest(type, row) {

            // this.row = row
            this.detailDrawer.type = type
            console.log('row', row)
            if (row.businessType == 1) {
                if (type == 1) {
                    this.detailDrawer.title = '社保缴费'
                } else {
                    this.detailDrawer.title = '社保核定缴费'
                }
            } else {
                if (type == 1) {
                    this.detailDrawer.title = '公积金缴费'
                } else {
                    this.detailDrawer.title = '公积金核定缴费'
                }
            }
            // this.detailDrawer.show = true
            const { data: { code, data: result } } = await this.$http({
                url: `/policy/customer/payFee/detail/${row.uuid}`,
                method: 'post'
            })
            console.log('viewRequest', code, result)
            if (code == 200) {
                this.detailForm = result
                this.checked = false
                this.detailDrawer.show = true
            }
        },
        //显示撤回
        withdraw(type, type2, row) {
            this.withdrawData.type = type
            this.withdrawData.type2 = type2
            this.withdrawData.row = row
            if (type2 == 'single') {
                this.withdrawData.show = true
                if (this.tabActive == 0) {
                    this.withdrawData.ids = [{
                        id: row.id,
                        itemIds: this.withdrawData.type == 'social' ? row.itemId : '',
                        uuid: row.uuid
                    }]
                } else {
                    this.withdrawData.ids = [{
                        id: row.id,
                        uuid: row.uuid
                    }]
                }

                return
            }
            let selects = this.$refs[type + 'Table'].selections
            if (selects.length == 0) {
                this.$message.warning('请先选择需要操作的数据')
                return
            }
            let ids = []
            //  1、	只有申报状态为：待申报(1)，待提交(6)，可以撤回，其它状态不允许撤回
            // for (let i = 0; i < selects.length; i++) {
            //     if (selects[i].status == 1 || selects[i].status == 6) {
            //         if (this.bussinessType == '1') {
            //             ids.push({
            //                 id: selects[i].id,
            //                 itemIds: this.withdrawData.type == 'social' ? selects[i].itemId : '',
            //                 uuid: selects[i].uuid
            //             })
            //         } else {
            //             ids.push({
            //                 id: selects[i].id,
            //                 uuid: selects[i].uuid
            //             })
            //         }
            //     } else {
            //         this.$message.warning('只有待提交和待申报的数据可以作废')
            //         return
            //     }
            // }
            this.withdrawData.ids = ids
            this.withdrawData.show = true
        },
        //取消作废
        cancelWithdraw() {
            this.withdrawData = {
                show: false,
                reason: '',
                ids: [],
                type: '',
                type2: ""
            }
            this.withdrawData.show = false
            this.$nextTick(() => {
                this.$refs.withdrawForm.clearValidate()
            })
        },
        //确定作废
        withdrawSumbit() {
            this.$refs.withdrawForm.validate((valid) => {
                if (valid) {
                    // var obj = {
                    //     businessType: this.bussinessType,
                    //     remark: this.withdrawData.reason,
                    //     items: this.withdrawData.ids,
                    // }
                    console.log('this.withdrawData', this.withdrawData)
                    // return
                    this.showLoading()
                    this.$http({
                        url: `/policy/customer/payFee/updateStatus/${this.withdrawData.row.uuid}/3?comment=${this.withdrawData.reason}`,
                        method: 'post',
                        // data: obj,
                    })
                        .then((res) => {
                            this.closeLoading()
                            if (res.data.code == 200) {
                                this.$message.success(
                                    res.data.message ? res.data.message : '操作成功'
                                )
                                if (this.tabActive == 0) {
                                    this.getSocialTable()
                                } else {
                                    this.getAccfundTable()
                                }
                                this.cancelWithdraw()
                            }
                        })
                        .catch((err) => {
                            this.closeLoading()
                        })
                }
            })
        },
        //S=公积金
        getAccfundTable() {
            let that = this
            const search = this.accfundSearch
            var params = [
                { property: 'businessType', value: 2 },
                { property: 'accountNumber', value: search.accountNumber },
                { property: 'minCreateDate', value: search.minCreateDate },
                { property: 'maxCreateDate', value: search.maxCreateDate },
                { property: 'minPeriodOfPayment', value: search.minPeriodOfPayment },
                { property: 'maxPeriodOfPayment', value: search.maxPeriodOfPayment },
                { property: 'statuses', value: search.statuses }
            ]

            this.$refs.accfundTable.fetch({
                query: params,
                method: 'post',
                url: '/policy/customer/payFee/page',
            })
        },
        cancelDetailDrawer() {
            this.detailDrawer.show = false
        },
        //点击添加缴费记录
        addRow() {
            this.addForm = {
                businessType: this.tabActive == 0 ? 1 : 2,
                periodOfPayment: this.$moment().format('YYYY-MM'),
                files: [],                   //用于添加时 单据凭证上传
                billType: 1,                //默认选中手动缴费
                systemType: null,            //申报系统
                accountNumber: null,
                addrName: null,
                addrId: null,
                feeType: null,
                payNumber: null,
                amount: null,
                ratio: null,
            }
            //请求所需申报账户
            // this.getCompanyList(this.tabActive == 0 ? 1 : 2)
            //根据tab设置 业务类型
            // this.addForm.businessType = this.tabActive == 0 ? 1 : 2
            //设置初始的缴费所属期
            // this.addForm.periodOfPayment = this.$moment().format('YYYY-MM')
            //获取申报系统字典值
            if (this.tabActive == 0) {
                // this.getDictByKey(10007, "systemType")
                //需要根据城市来进行联动获取
            } else {
                // this.getDictByKey(10004, "systemType")
            }
            this.getCitySocialSystem(null)
            this.getDictByKey(10019, "feeType")


            setTimeout(() => {
                this.addVisible = true
                //清除添加表单之前的错误验证
                this.$nextTick(() => {
                    this.$refs['addForm'].clearValidate()
                })
            }, 300)

        },
        // 获取参保地
        getAddrArr() {
            this.$http({
                url: '/policy/declareAddr/addrList',
                method: 'post',
            })
                .then(({ data }) => {
                    this.addrArr = data.data
                })
                .catch((data) => { })
        },
        // 改变参保地
        getSocialAddrId(item) {
            console.log('getSocialAddrId', item)
            if (!item.id || !item.name) {
                return
            }
            this.addForm.addrId = item.id
            this.getCompanyList(this.tabActive == 0 ? 1 : 2, item.id)

            this.addForm.systemType = null      //重新置空原来的社保申报系统
            this.getCitySocialSystem(item.id,this.addForm.businessType)
        },
        //业务类型改变
        onChangeBusinessType(e) {
            console.log(e, this.addForm)
            this.addForm.systemType = null
            if (e == 1) {
                // this.getDictByKey(10007, "systemType")
                //需要根据城市来进行联动获取
                this.getCitySocialSystem(this.addForm.addrId,this.addForm.businessType)
            } else {
                // this.getDictByKey(10004, "systemType")
                this.getCitySocialSystem(this.addForm.addrId,this.addForm.businessType)
            }
        },
        uploadChange(e) {
            this.addForm.files.push({ fileId: e.data[0].fileID, fileName: e.data[0].fileName })
        },
        handlePreviewFile(fileID) {
            this.$downloadFile(`/policy/sys/file/download/${fileID}`, 'get')
        },
        handleDelFile(fieldKey, fileIndex) {
            let that = this
            this.$confirm('是否确定删除该文件？', '提示', {
                confirmButtonText: '确定删除',
                cancelButtonText: '取消',
                showClose: false,
                customClass: 'pal-confirm',
                type: 'warning',
            })
                .then(() => {
                    that.addForm.files.splice(fileIndex, 1)
                })
                .catch(() => { })
        },

        handleRemove(file) {
            console.log(file);
        },
        handlePictureCardPreview(file) {
            console.log(file)
            if (this.checkFileIsPic(file)) {
                this.dialogImageUrl = file.fileUrl;
                this.dialogVisible = true;
            } else {
                window.open(file.fileUrl, '_blank')
            }

        },
        handleDownload(file) {
            console.log(file);
        },
        checkFileIsPic(file) {
            const fileExtension = file.fileName.split('.').pop().toLowerCase();
            // 支持的图片文件后缀列表
            const imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp'];
            return imageExtensions.includes(fileExtension)
        },
        //更新菜单上红点数
        async updateMenuDotCount() {
            const { data } = await this.$http({
                url: '/policy/customer/payFee/countWeiHeDing',
                method: 'post'
            })
            if (data.code == 200) {
                this.$store.commit('updateBusinessDotCount', data.data)
            }
        },
        //表格勾选回调
        handleSelectionChange(val) {
            if (this.tabActive == 0) {
                this.socialSections = val
            } else {
                this.accfundSections = val
            }
        },
        //详情drawer中文件下载
        downLoadFile(row) {
            //获取文件后缀 不同后缀获取不同的文件类型
            // console.log(row)
            const fileExtension = row.fileName.split('.').pop().toLowerCase();
            let fileType = ''
            if (fileExtension == 'jpg') {
                fileType = this.$global.JPG
            } else if (fileExtension == 'png') {
                fileType = this.$global.PNG
            } else if (fileExtension == 'jpeg') {
                fileType = this.$global.JPEG
            } else if (fileExtension == 'xlsx') {
                fileType = this.$global.EXCEL
            } else if (fileExtension == 'docx') {
                fileType = this.$global.WORD
            } else if (fileExtension == 'pdf') {
                fileType = this.$global.PDF
            }
            this.$downloadFile(
                'policy/sys/file/download/' + row.fileId,
                'get',
                {},
                fileType,
                `${this.detailForm.accountNumber}-${this.detailForm.addrName}-${this.detailForm.periodOfPayment}-${row.fileName}`,
                null,
                null,
                true
            )
        },
        //全局加载
        showLoading(target) {
            this.loading = this.$loading({
                target: target ? target : document.body,
                lock: true,
                text: '加载中',
                spinner: 'el-icon-loading',
                background: 'rgba(255, 255, 255, 0.7)',
            })
        },
        //关闭全局加载
        closeLoading() {
            if (this.loading && this.loading.close) {
                this.loading.close()
            }
        },
        //新增缴费记录
        submitAdd() {
            // console.log('this.addForm',this.addForm)
            this.$refs['addForm'].validate((valid) => {
                if (valid) {
                    // alert('submit!');
                    this.showLoading()
                    this.$http({
                        url: 'policy/customer/payFee/saveAdd',
                        method: 'post',
                        data: {
                            ...this.addForm,
                            feeType: this.addForm.feeType.join(",")
                        }
                    }).then(res => {

                        this.addVisible = false

                        if (res.data.code == 200) {
                            this.$message.success(
                                res.data.message ? res.data.message : '操作成功'
                            )
                            if (this.tabActive == 0) {
                                this.getSocialTable()
                            } else {
                                this.getAccfundTable()
                            }
                            this.closeLoading()
                        }
                    }).catch(err => {
                        this.addVisible = false
                        this.closeLoading()
                    })
                } else {
                    console.log(`this.$refs['addForm'] error submit!!`);
                    return false;
                }
            });
        },
        changeCompany(type) {
            if (type == 1) {
                if (!this.socialSearch2.itemId) {
                    this.socialSearch2.accountNumber = ''
                    this.socialSearch2.company = ''
                } else {
                    var items = this.socialSearch2.companyArr
                        .filter((item) => {
                            return item.itemId == this.socialSearch2.itemId
                        })
                        .map((item) => {
                            return item
                        })
                    if (items && items.length > 0) {
                        this.socialSearch2.accountNumber = items[0].accountNumber
                        this.socialSearch2.company = items[0].id
                    }
                }
            } else {
                if (!this.accfundSearch2.itemId) {
                    this.accfundSearch2.accountNumber = ''
                    this.accfundSearch2.company = ''
                } else {
                    var items = this.accfundSearch2.companyArr
                        .filter((item) => {
                            return item.itemId == this.accfundSearch2.itemId
                        })
                        .map((item) => {
                            return item
                        })
                    if (items && items.length > 0) {
                        this.accfundSearch2.accountNumber = items[0].accountNumber
                        this.accfundSearch2.company = items[0].id
                    }
                }
            }
        },
        // 获取参保主体
        async getCompanyList(type, id) {
            let that = this,
                addressId = ''
            // if (type == 1) {
            //     addressId = that.socialSearch2.addrId
            // } else {
            //     addressId = that.accfundSearch2.addrId
            // }
            addressId = id
            return this.$http({
                url: '/policy/declareAccount/seeMainBody',
                method: 'post',
                params: {
                    bussinessType: type,
                    addressId: addressId,
                    custLimit: 2         //传1是查当前用户的数据，不是1就查全部

                },
            })
                .then(({ data }) => {
                    that.addFormSelects.companyArr = data.data
                    if (type == 1) {
                        that.socialSearch2.companyArr = data.data
                        that.socialSearch2.company = ''
                        that.socialSearch2.accountNumber = ''
                        if (data.data.length == 1) {
                            that.socialSearch2.itemId = data.data[0].itemId
                            this.changeCompany(1)
                        }
                    } else {
                        that.accfundSearch2.companyArr = data.data
                        that.accfundSearch2.company = ''
                        that.accfundSearch2.accountNumber = ''
                        if (data.data.length == 1) {
                            that.accfundSearch2.itemId = data.data[0].itemId
                            this.changeCompany(2)
                        }
                    }
                })
                .catch((data) => { })
        },
        //获取字典值
        getDictByKey(key, field) {
            let that = this
            this.$http({
                url: 'policy/sys/dict/getDictByKey',
                method: 'get',
                params: {
                    dataKey: key
                }
            }).then(res => {
                that.addFormSelects[field] = res.data.data
            }).catch()
        },
        // 上传凭证
      exportTableData(type){
        var selectList = type === 1 ? this.socialSections : this.accfundSections
        if (!selectList || selectList.length === 0) {
          this.$message.warning('请先勾选需要下载的记录')
          return
        }
        this.$refs.batchDownloadPayFeeFiles.init(selectList)
      },
      // 上传凭证
      handleUploadPayFee(type, row){
          this.$refs.uploadPayFeeFiles.init(type, row)
      },
        //显示立即缴费dialog
        showRobotDialog(type, row) {
            this.currentRow = row
            this.robotVisible = true
        },
        //确定立即缴费
        async submitRobot() {
            this.showLoading()
            try {
                const { data: { code, data: result } } = await this.$http({
                    url: `policy/customer/payFee/execFlow/${this.currentRow.id}`,
                    method: 'post'
                })
                if (code == 200) {
                    this.$message.success('操作成功')
                    if (this.tabActive == 0) {
                        this.getSocialTable()
                    } else {
                        this.getAccfundTable()
                    }

                }
                this.robotVisible = false
                this.closeLoading()
            } catch (error) {
                this.robotVisible = false
                this.closeLoading()
            }

        },
        //取消核定有误dialog
        cancelErrordraw() {
            this.errorDialog.show = false
        },
        //核定有误确认
        withErrorSumbit() {
            this.errorDialog.show = true
            this.$refs.errorDrawForm.validate((valid) => {
                if (valid) {
                    // var obj = {
                    //     businessType: this.bussinessType,
                    //     remark: this.withdrawData.reason,
                    //     items: this.withdrawData.ids,
                    // }
                    console.log('this.errorDialog', this.errorDialog)
                    this.showLoading()
                    this.$http({
                        url: `/policy/customer/payFee/updateStatus/${this.detailForm.uuid}/3?comment=${this.errorDialog.reason}`,
                        method: 'post',
                        // data: obj,
                    })
                        .then((res) => {
                            this.closeLoading()
                            if (res.data.code == 200) {
                                this.$message.success(
                                    res.data.message ? res.data.message : '操作成功'
                                )
                                if (this.tabActive == 0) {
                                    this.getSocialTable()
                                } else {
                                    this.getAccfundTable()
                                }
                                this.cancelErrordraw()
                                this.detailDrawer.show = false
                            }
                        })
                        .catch((err) => {
                            this.closeLoading()
                        })
                }
            })
        },
        //显示核定有误dialog
        showErrorDialog() {
            this.errorDialog.show = true
        },
        //确认核定
        async sumbtApprove() {
            if (this.checked) {
                this.showLoading()
                this.$http({
                    url: `/policy/customer/payFee/updateStatus/${this.detailForm.uuid}/1?comment=`,
                    method: 'post',
                    // data: obj,
                })
                    .then((res) => {
                        this.closeLoading()
                        if (res.data.code == 200) {
                            this.$message.success(
                                res.data.message ? res.data.message : '操作成功'
                            )
                            if (this.tabActive == 0) {
                                this.getSocialTable()
                            } else {
                                this.getAccfundTable()
                            }
                            this.cancelErrordraw()
                            this.detailDrawer.show = false
                            this.showRaidoError = false
                        }
                    })
                    .catch((err) => {
                        this.closeLoading()
                    })
            } else {
                this.showRaidoError = true
            }
        },
        //获取城市所需的社保申报系统/公积金类型
        async getCitySocialSystem(addrId,type) {
            if (!addrId) {
                this.addFormSelects['systemType'] = []
                return
            }
            const { data: { code, data: result } } = await this.$http({
                url: `/policy/customer/payFee/getSocialSystem/${addrId}/${type}`,
                method: 'post'
            })
            if (code == 200) {
                this.addFormSelects['systemType'] = result
                this.addForm.systemType = null
            }
        },

      // 高亮(从首页消息通知跳转进来需要高亮)
      handleRowStyle ({ row, rowIndex }) {
        let tabActive = this.highlight.businessType == 1 ? 0 : 1
        if (tabActive == this.tabActive && row.id == this.highlight.id && row.status == this.highlight.status) {
          return { backgroundColor: '#d3e3fd!important' }
        } else {
          return {}
        }
      }
    },
}
</script>
<style lang="less">
.add-dialog {
    min-width: 500px;
}
</style>
<style lang='less' scoped>
.search-row {
    .label {
        width: 90px;
        text-align: right;
    }

    /deep/ .el-col {
        // display: flex;
        // justify-content: center;
    }
}

.circle-text {
    position: relative;
    display: inline-block;
    padding-right: 30px;
    /* 为圆点腾出位置 */
}

.circle {
    position: absolute;
    top: 15px;
    right: 0;
    width: 20px;
    height: 20px;
    background: #FD6154;
    border-radius: 50%;
    font-size: 10px;
    text-align: center;
    line-height: 14px;
    color: #ffffff;
    padding: 3px;
}

/deep/.detail-drawer {
    width: 60% !important;
    max-width: 1000px !important;

    .pal-tabs {
        padding-left: 0;
        height: 75px;
        line-height: 62px;
        position: relative;
        top: -10px;
    }

    .span-tabs {
        height: 71px;
        line-height: 80px;
    }

    .drawer-footer-buts {
        height: 100px;
    }

    .check {
        position: absolute;
        top: -20px;
        left: 10px;
    }

    .file-item {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;

        .file-name {
            padding: 0 10px;
            max-width: 120px;
            white-space: nowrap;
            /* 防止文本换行 */
            overflow: hidden;
            /* 隐藏超出容器宽度的部分 */
            text-overflow: ellipsis;
            /* 使用省略号表示被截断的文本 */
            color: #999;
        }
    }

    .check-btn {
        // position: absolute;
        top: 40px
    }
}

.custom-upload {
    /deep/ .el-upload-list__item :hover {
        color: #409eff !important;
        cursor: pointer;
        background-color: #f5f7fa !important;
    }
}

/deep/.blue .el-step__icon.is-text {
    border-radius: 50%;
    border: 2px solid;
    border-color: inherit;
    width: 50px;
    height: 50px;
    position: relative;
    left: -13px;
    font-size: 13px;
    color: #3E82FF;
    border-color: #3E82FF;
}

/deep/.opopp .el-step__icon.is-text {
    border-radius: 50%;
    border: 2px solid;
    border-color: inherit;
    width: 50px;
    height: 50px;
    position: relative;
    left: -13px;
    font-size: 13px;
}


/deep/ .el-step.is-vertical .el-step__main {
    padding-left: 30px;
    height: 100px;
}

/deep/ .el-step.is-vertical {
    margin-left: 20px;
}

.step-item {
    // .blue {
    //     color: #3E82FF;
    //     border-color: #3E82FF;
    // }

    // .opopp {
    //     color: #C0C4CC;
    //     border-color: #C0C4CC;
    // }

    .title {
        color: #000;
    }

    .desc {
        color: #999;
    }
}

/deep/ .el-upload--picture-card {
    display: none;
}

/deep/ .el-dialog__header {
    border-bottom: none !important;
}

.custom-error {
    color: #F56C6C;
    font-size: 12px;
    line-height: 1;
    padding-top: 4px;
    position: absolute;
    top: 100%;
    left: 0;
    padding-left: 110px;
}



</style>
