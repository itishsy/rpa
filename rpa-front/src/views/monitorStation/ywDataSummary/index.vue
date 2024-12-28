<template>
    <div class="content spl-container">
        <!-- 导航 -->
        <breadcrumb :data="pathData">
            <template slot="breadcrumb-btn">
                <!-- <el-button type="primary" class="search-btn mr-20" size="small"
                    @click="viewRequest('1', null)">测试查看</el-button>
                <el-button type="primary" class="search-btn mr-20" size="small"
                    @click="withdraw('accfund', 'single', testRow)">测试撤回</el-button> -->
                <el-button type="primary" class="search-btn mr-20" size="small" @click="routeToRuleSetting">规则设置</el-button>
            </template>
        </breadcrumb>
        <!-- Tabs -->
        <div style="padding: 20px 20px 0 20px">
            <palTabEnd :tabs="tabs" v-model="tabActive" :type="2" custom @change="onTabChange">
                <template v-slot:default="slotProps">
                    <div v-if="!slotProps.row.includes('校验失败')">{{ slotProps.row }}</div>
                    <div v-else class="circle-text">{{ slotProps.row.replace(/\([^)]*\)/g, "") }}<div class="circle">
                            {{ getErrorCount(slotProps.row) }}</div>
                    </div>

                </template>
            </palTabEnd>

            <!--待处理-->
            <div v-show="tabActive == 0">
                <!-- 搜索栏 -->
                <SearchLayout @query="queryPendingTableData" />
                <div>
                    <!-- 表格 -->
                    <dTable @fetch="getPendingTableData" ref="0Table" :cancelMinHeight="true" :paging="true"
                        :showSelection="true" :showIndex="true" rowKey="id" :rowHeight="45">
                        <u-table-column prop="employeeName" label="姓名" min-width="80" :show-overflow-tooltip="true"
                            fixed="left"></u-table-column>
                        <u-table-column prop="idCard" label="证件号码" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="createTime" label="创建时间" min-width="160" :show-overflow-tooltip="true">
                            <template scope="scope">
                                <span>{{ $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="createTime" label="申报时间" min-width="160" :show-overflow-tooltip="true">
                            <template scope="scope">
                                <span>{{ $moment(scope.row.createTime).format('YYYY-MM') }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="addrName" label="参保城市" min-width="80"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="changeType" label="申报类型" min-width="80" :show-overflow-tooltip="true">
                            <template slot-scope="scope">
                                <span>{{ handleChangeType(scope.row.changeType) }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="status" label="申报状态" min-width="100" :show-overflow-tooltip="true">
                            <template slot-scope="scope">
                                <span>{{ handleDeclareStatus(scope.row.status) }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="machineCode" label="归属盒子" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="companyNameAndAccountNumber" label="归属公司" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="clientName" label="归属主体" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column v-if="pendingQueryForm.businessType == 1" prop="failCase" label="原因备注"
                            min-width="150" :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column v-else prop="comment" label="原因备注"
                            min-width="150" :show-overflow-tooltip="true"></u-table-column>

                        <u-table-column fixed="right" label="操作" width="160">
                            <template slot-scope="scope">
                                <el-button type="text" size="small"
                                    @click="viewRequest(pendingQueryForm.businessType, scope.row)">查看</el-button>
                                <el-button type="text" size="small"
                                    @click="showEditDraw(pendingQueryForm.businessType, scope.row)">编辑</el-button>
                                <el-button type="text" size="small"
                                    @click="withdraw(pendingQueryForm.businessType == 1 ? 'social' : 'accfund', 'single', scope.row)">撤回</el-button>
                            </template>
                        </u-table-column>
                        <template slot="pagination-btns">
                            <div class="display-flex">
                                <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                                    @click="exportData(pendingQueryForm.businessType == 1 ? 'social' : 'accfund')">导出</el-button>
                                <el-button size="small" class="btn--border-blue s-btn"
                                    @click="withdraw(pendingQueryForm.businessType == 1 ? 'social' : 'accfund', 'batch')">批量撤回</el-button>
                            </div>
                        </template>
                    </dTable>
                </div>
            </div>
            <!-- 待提交 -->
            <div v-show="tabActive == 1">
                <!-- 搜索栏 -->
                <SearchLayout @query="queryOtherTableData" />
                <div>
                    <!-- 表格 -->
                    <dTable @fetch="getOtherTableData" ref="1Table" :cancelMinHeight="true" :paging="true"
                        :showSelection="true" :showIndex="true" rowKey="id" :rowHeight="45">
                        <u-table-column prop="employeeName" label="姓名" min-width="80" :show-overflow-tooltip="true"
                            fixed="left"></u-table-column>
                        <u-table-column prop="idCard" label="证件号码" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="createTime" label="创建时间" min-width="160" :show-overflow-tooltip="true">
                            <template scope="scope">
                                <span>{{ $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="createTime" label="申报时间" min-width="160" :show-overflow-tooltip="true">
                            <template scope="scope">
                                <span>{{ $moment(scope.row.createTime).format('YYYY-MM') }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="addrName" label="参保城市" min-width="80"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="changeType" label="申报类型" min-width="80" :show-overflow-tooltip="true">
                            <template slot-scope="scope">
                                <span>{{ handleChangeType(scope.row.changeType) }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="machineCode" label="归属盒子" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="companyNameAndAccountNumber" label="归属公司" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="clientName" label="归属主体" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="failCase" label="原因备注" min-width="150"
                            :show-overflow-tooltip="true"></u-table-column>

                        <u-table-column fixed="right" label="操作" width="160">
                            <template slot-scope="scope">
                                <el-button type="text" size="small"
                                    @click="viewRequest(QueryForm1.businessType, scope.row)">查看</el-button>
                                <el-button type="text" size="small"
                                    @click="withdraw(QueryForm1.businessType == 1 ? 'social' : 'accfund', 'single', scope.row)">撤回</el-button>
                            </template>
                        </u-table-column>
                        <template slot="pagination-btns">
                            <div class="display-flex">
                                <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                                    @click="exportData(QueryForm1.businessType == 1 ? 'social' : 'accfund')">导出</el-button>
                                <el-button size="small" class="btn--border-blue s-btn"
                                    @click="withdraw(QueryForm1.businessType == 1 ? 'social' : 'accfund', 'batch')">批量撤回</el-button>
                            </div>
                        </template>
                    </dTable>
                </div>
            </div>
            <!-- 待申报 -->
            <div v-show="tabActive == 2">
                <!-- 搜索栏 -->
                <SearchLayout @query="queryOtherTableData" />
                <div>
                    <!-- 表格 -->
                    <dTable @fetch="getOtherTableData" ref="2Table" :cancelMinHeight="true" :paging="true"
                        :showSelection="true" :showIndex="true" rowKey="id" :rowHeight="45">
                        <u-table-column prop="employeeName" label="姓名" min-width="80" :show-overflow-tooltip="true"
                            fixed="left"></u-table-column>
                        <u-table-column prop="idCard" label="证件号码" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="createTime" label="创建时间" min-width="160" :show-overflow-tooltip="true">
                            <template scope="scope">
                                <span>{{ $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="createTime" label="申报时间" min-width="160" :show-overflow-tooltip="true">
                            <template scope="scope">
                                <span>{{ $moment(scope.row.createTime).format('YYYY-MM') }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="declareTime" label="申报时长" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="addrName" label="参保城市" min-width="80"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="changeType" label="申报类型" min-width="80" :show-overflow-tooltip="true">
                            <template slot-scope="scope">
                                <span>{{ handleChangeType(scope.row.changeType) }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="machineCode" label="归属盒子" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="companyNameAndAccountNumber" label="归属公司" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="clientName" label="归属主体" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="failCase" label="原因备注" min-width="150"
                            :show-overflow-tooltip="true"></u-table-column>

                        <u-table-column fixed="right" label="操作" width="160">
                            <template slot-scope="scope">
                                <el-button type="text" size="small"
                                    @click="viewRequest(QueryForm2.businessType, scope.row)">查看</el-button>
                                <el-button type="text" size="small"
                                    @click="withdraw(QueryForm2.businessType == 1 ? 'social' : 'accfund', 'single', scope.row)">撤回</el-button>
                            </template>
                        </u-table-column>
                        <template slot="pagination-btns">
                            <div class="display-flex">
                                <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                                    @click="exportData(QueryForm2.businessType == 1 ? 'social' : 'accfund')">导出</el-button>
                                <el-button size="small" class="btn--border-blue s-btn"
                                    @click="withdraw(QueryForm2.businessType == 1 ? 'social' : 'accfund', 'batch')">批量撤回</el-button>
                            </div>
                        </template>
                    </dTable>
                </div>
            </div>
            <!-- 申报中 -->
            <div v-show="tabActive == 3">
                <!-- 搜索栏 -->
                <SearchLayout @query="queryOtherTableData" />
                <div>
                    <!-- 表格 -->
                    <dTable @fetch="getOtherTableData" ref="3Table" :cancelMinHeight="true" :paging="true"
                        :showSelection="true" :showIndex="true" rowKey="id" :rowHeight="45">
                        <u-table-column prop="employeeName" label="姓名" min-width="80" :show-overflow-tooltip="true"
                            fixed="left"></u-table-column>
                        <u-table-column prop="idCard" label="证件号码" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="createTime" label="创建时间" min-width="160" :show-overflow-tooltip="true">
                            <template scope="scope">
                                <span>{{ $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="createTime" label="申报时间" min-width="160" :show-overflow-tooltip="true">
                            <template scope="scope">
                                <span>{{ $moment(scope.row.createTime).format('YYYY-MM') }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="declareTime" label="申报时长" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="addrName" label="参保城市" min-width="80"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="changeType" label="申报类型" min-width="80" :show-overflow-tooltip="true">
                            <template slot-scope="scope">
                                <span>{{ handleChangeType(scope.row.changeType) }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="machineCode" label="归属盒子" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="companyNameAndAccountNumber" label="归属公司" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="clientName" label="归属主体" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="failCase" label="原因备注" min-width="150"
                            :show-overflow-tooltip="true"></u-table-column>

                        <u-table-column fixed="right" label="操作" width="160">
                            <template slot-scope="scope">
                                <el-button type="text" size="small"
                                    @click="viewRequest(QueryForm3.businessType, scope.row)">查看</el-button>
                                <el-button type="text" size="small"
                                    @click="withdraw(QueryForm3.businessType == 1 ? 'social' : 'accfund', 'single', scope.row)">撤回</el-button>
                            </template>
                        </u-table-column>
                        <template slot="pagination-btns">
                            <div class="display-flex">
                                <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                                    @click="exportData(QueryForm3.businessType == 1 ? 'social' : 'accfund')">导出</el-button>
                                <el-button size="small" class="btn--border-blue s-btn"
                                    @click="withdraw(QueryForm3.businessType == 1 ? 'social' : 'accfund', 'batch')">批量撤回</el-button>
                            </div>
                        </template>
                    </dTable>
                </div>
            </div>
            <!-- 申报失败 -->
            <div v-show="tabActive == 4">
                <!-- 搜索栏 -->
                <SearchLayout @query="queryOtherTableData" />
                <div>
                    <!-- 表格 -->
                    <dTable @fetch="getOtherTableData" ref="4Table" :cancelMinHeight="true" :paging="true"
                        :showSelection="true" :showIndex="true" rowKey="id" :rowHeight="45">
                        <u-table-column prop="employeeName" label="姓名" min-width="80" :show-overflow-tooltip="true"
                            fixed="left"></u-table-column>
                        <u-table-column prop="idCard" label="证件号码" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="createTime" label="创建时间" min-width="160" :show-overflow-tooltip="true">
                            <template scope="scope">
                                <span>{{ $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="createTime" label="申报时间" min-width="160" :show-overflow-tooltip="true">
                            <template scope="scope">
                                <span>{{ $moment(scope.row.createTime).format('YYYY-MM') }}</span>
                            </template>
                        </u-table-column>
                        <!-- <u-table-column prop="declareTime" label="申报时长" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column> -->
                        <u-table-column prop="addrName" label="参保城市" min-width="80"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="changeType" label="申报类型" min-width="80" :show-overflow-tooltip="true">
                            <template slot-scope="scope">
                                <span>{{ handleChangeType(scope.row.changeType) }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="machineCode" label="归属盒子" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="companyNameAndAccountNumber" label="归属公司" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="clientName" label="归属主体" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="failCase" label="原因备注" min-width="150"
                            :show-overflow-tooltip="true"></u-table-column>

                        <u-table-column fixed="right" label="操作" width="160">
                            <template slot-scope="scope">
                                <el-button type="text" size="small"
                                    @click="viewRequest(QueryForm4.businessType, scope.row)">查看</el-button>
                                <el-button v-if="[1, 6].indexOf(scope.row.status) > -1" type="text" size="small"
                                    @click="withdraw(QueryForm4.businessType == 1 ? 'social' : 'accfund', 'single', scope.row)">撤回</el-button>
                            </template>
                        </u-table-column>
                        <template slot="pagination-btns">
                            <div class="display-flex">
                                <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                                    @click="exportData(QueryForm4.businessType == 1 ? 'social' : 'accfund')">导出</el-button>
                                <el-button size="small" class="btn--border-blue s-btn"
                                    @click="withdraw(QueryForm4.businessType == 1 ? 'social' : 'accfund', 'batch')">批量撤回</el-button>
                            </div>
                        </template>
                    </dTable>
                </div>
            </div>
            <!-- 已处理 -->
            <div v-show="tabActive == 5">
                <!-- 搜索栏 -->
                <SearchLayout @query="queryProcessedableData" />
                <div>
                    <!-- 表格 -->
                    <dTable @fetch="getProcessedTableData" ref="5Table" :cancelMinHeight="true" :paging="true"
                        :showSelection="true" :showIndex="true" rowKey="id" :rowHeight="45">
                        <u-table-column prop="employeeName" label="姓名" min-width="80" :show-overflow-tooltip="true"
                            fixed="left"></u-table-column>
                        <u-table-column prop="idCard" label="证件号码" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="createTime" label="创建时间" min-width="160" :show-overflow-tooltip="true">
                            <template scope="scope">
                                <span>{{ $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="createTime" label="申报时间" min-width="160" :show-overflow-tooltip="true">
                            <template scope="scope">
                                <span>{{ $moment(scope.row.createTime).format('YYYY-MM') }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="addrName" label="参保城市" min-width="80"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="changeType" label="申报类型" min-width="80" :show-overflow-tooltip="true">
                            <template slot-scope="scope">
                                <span>{{ handleChangeType(scope.row.changeType) }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="machineCode" label="归属盒子" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="companyNameAndAccountNumber" label="归属公司" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="clientName" label="归属主体" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                            <u-table-column v-if="processedQueryForm.businessType == 1" prop="processComment" label="处理结果"
                            min-width="150" :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column v-else prop="comment" label="处理结果"
                            min-width="150" :show-overflow-tooltip="true"></u-table-column>

                        <u-table-column fixed="right" label="操作" width="160">
                            <template slot-scope="scope">
                                <el-button type="text" size="small"
                                    @click="viewRequest(processedQueryForm.businessType, scope.row)">查看</el-button>
                            </template>
                        </u-table-column>
                        <template slot="pagination-btns">
                            <div class="display-flex">
                                <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                                    @click="exportData(processedQueryForm.businessType == 1 ? 'social' : 'accfund')">导出</el-button>
                            </div>
                        </template>
                    </dTable>
                </div>
            </div>
            <!-- 校验失败 -->
            <div v-show="tabActive == 6">
                <!-- 搜索栏 -->
                <SearchLayout :type="2" @query="queryCheckFailureTableData" />
                <div>
                    <!-- 表格 -->
                    <dTable @fetch="getCheckFailureTableData" ref="6Table" :cancelMinHeight="true" :paging="true"
                        :showSelection="true" :showIndex="true" rowKey="id" :rowHeight="45">
                        <u-table-column prop="batchNumber" label="批次号" min-width="160" :show-overflow-tooltip="true"
                            fixed="left"></u-table-column>
                        <u-table-column prop="name" label="姓名" min-width="80" :show-overflow-tooltip="true"
                            fixed="left"></u-table-column>
                        <u-table-column prop="idCard" label="证件号码" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="addrName" label="参保城市" min-width="80"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="changeType" label="申报类型" min-width="80" :show-overflow-tooltip="true">
                            <template slot-scope="scope">
                                <span>{{ handleChangeType(scope.row.declareType) }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="insuredDate" label="缴纳起始月" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="tbBase" label="缴纳基数" min-width="160"
                            :show-overflow-tooltip="true"></u-table-column>
                        <u-table-column prop="createTime" label="交互时间" min-width="160" :show-overflow-tooltip="true">
                            <template scope="scope">
                                <span>{{ $moment(scope.row.createTime).format('YYYY-MM') }}</span>
                            </template>
                        </u-table-column>
                        <u-table-column prop="errorMsg" label="原因备注" min-width="150"
                            :show-overflow-tooltip="true"></u-table-column>

                        <u-table-column fixed="right" label="操作" width="80">
                            <template slot-scope="scope">
                                <el-button type="text" size="small"
                                    @click="viewInfo(errorQueryForm.businessType, scope.row)">查看</el-button>
                            </template>
                        </u-table-column>
                        <template slot="pagination-btns">
                            <div class="display-flex">
                                <el-button size="small" icon="icon ic-export-blue" class="btn--border-blue s-btn"
                                    @click="exportData(errorQueryForm.businessType == 1 ? 'social' : 'accfund')">导出</el-button>
                                <!-- <el-button size="small" class="btn--border-blue s-btn"
                                    @click="withdraw(errorQueryForm.businessType == 1 ? 'social' : 'accfund', 'batch')">撤回</el-button> -->
                            </div>
                        </template>
                    </dTable>
                </div>
            </div>
        </div>
        <!-- 查看dialog -->
        <myDrawer :tabs="getType.tabs" :show.sync="getType.show" :type="this.bussinessType" :row="row" ref="myDrawer"
            requestType="employeeInsurance" :edit="false" @do-query="onDoQuery"></myDrawer>
        <!--撤回-->
        <el-dialog :visible.sync="withdrawData.show" title="撤回" width="600px" class="cust-dialog" :show-close="true"
            :close-on-click-modal="false" :before-close="cancelWithdraw">
            <div class="template-dialog-box">
                <el-form :model="withdrawData" ref="withdrawForm">
                    <p v-if="withdrawData.type2 == 'batch'">已筛选<span style="color:#FD6154;"> {{ withdrawData.ids.length }}
                        </span>条待提交/待申报的记录</p>
                    <p class="mb-20">一旦撤回，记录无法查出!</p>
                    <div class="display-flex">
                        <label class="required-pre w-70" style="vertical-align: top">原因：</label>
                        <el-form-item prop="reason" class="flex1" :rules="[
                            { required: true, message: '请输入撤回原因', trigger: 'blur' },
                            { required: true, min: 0, max: 100, message: '撤回原因在100个字符以内', trigger: 'change' },
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
        <!-- 状态更改 -->
        <el-dialog :visible.sync="editdrawData.show" title="状态更改" width="600px" class="cust-dialog" :show-close="true"
            :close-on-click-modal="false" :before-close="cancelEditdraw">
            <div class="template-dialog-box">
                <el-form :model="editdrawData" ref="editdrawForm">

                    <el-form-item label="变更状态：" prop="declareStatus" class="flex1" :rules="[
                        { required: true, message: '请选择变更状态', trigger: 'change' },
                    ]">
                        <el-select v-model="editdrawData.declareStatus" placeholder="请选择" class="w-400">
                            <el-option label="待申报" :value="1"></el-option>
                            <el-option label="申报成功" :value="4"></el-option>
                        </el-select>
                    </el-form-item>
                    <div class="display-flex">
                        <label class="required-pre w-80" style="vertical-align: top;text-align:center">原因：</label>
                        <el-form-item prop="comment" class="flex1" :rules="[
                            { required: true, message: '请输入状态更改原因', trigger: 'blur' },
                            { required: true, min: 0, max: 100, message: '状态更改原因在100个字符以内', trigger: 'change' },
                        ]">
                            <el-input v-model.trim="editdrawData.comment" type="textarea" rows="3" resize="none"></el-input>
                            <p style="position: absolute;right:0;font-size:12px;" v-if="editdrawData.comment.length > 80">
                                {{ editdrawData.comment.length }}/<span style="color:#FD6154">100</span></p>
                        </el-form-item>
                    </div>
                </el-form>
                <div class="text-right mt-30">
                    <el-button size="small" class="mr-15 w-80" @click="cancelEditdraw()">取消</el-button>
                    <el-button size="small" class="w-80" type="primary" @click="editdrawSumbit()"
                        :loading="editdrawData.btnLoading">确定</el-button>
                </div>
            </div>
        </el-dialog>


        <!--校验失败 - 查看社保详情-->
        <el-dialog :visible.sync="socialDetailError.show" title="社保参保详情" width="1200px" class="cust-dialog detail-dialog"
            :show-close="false" :close-on-click-modal="false">
            <div class="pt-10">
                <el-row>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">姓名：</span>
                        <span class="val">{{ socialDetailError.row.name }}</span>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">证件号码：</span>
                        <span class="val">{{ socialDetailError.row.idCard }}</span>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">参保城市：</span>
                        <span class="val">{{ socialDetailError.row.addrName }}</span>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">申报类型：</span>
                        <span class="val">{{ socialDetailError.row.declareTypeName }}</span>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">参保险种：</span>
                        <span class="val">{{ getProductNameList(socialDetailError.row.items) }}</span>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">缴纳起始月：</span>
                        <span class="val" v-if="socialDetailError.row.declareType == 5"><span
                                v-if="socialDetailError.row.insuredBeginDate || socialDetailError.row.insuredEndDate">{{
                                    socialDetailError.row.insuredBeginDate }}
                                ~ {{ socialDetail.row.insuredEndDate }}</span></span>
                        <span class="val" v-else>{{ socialDetailError.row.insuredDate }}</span>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">缴纳基数：</span>
                        <span class="val">{{ socialDetailError.row.tbBase ? getTwoPoint(socialDetailError.row.tbBase) :
                            '' }}</span>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">批次号：</span>
                        <span class="val">{{ socialDetailError.row.batchNumber }}</span>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">交互时间：</span>
                        <span class="val">{{ socialDetailError.row.createTime ?
                            $moment(socialDetailError.row.createTime).format('YYYY-MM-DD HH: mm: ss') : '' }}</span>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">创建人：</span>
                        <span class="val">{{ socialDetailError.row.createName }}</span>
                    </el-col>
                </el-row>
                <el-row>
                    <!--其它报盘文件配置的数据字段-->
                    <el-col :span="8" class="detail-item" v-for="(item, key, index) in socialDetailError.row.declareInfo"
                        :key="index">
                        <span class="lab">{{ key }}：</span>
                        <span class="val">{{ item }}</span>
                    </el-col>
                </el-row>
            </div>
            <div class="text-right mt-10">
                <el-button size="small" class="w-60" type="primary" @click="socialDetailError.show = false">关闭</el-button>
            </div>
        </el-dialog>

        <!--校验失败 - 查看公积金详情-->
        <el-dialog :visible.sync="accfundDetailError.show" title="公积金参保详情" width="1200px" class="cust-dialog detail-dialog"
            :show-close="false" :close-on-click-modal="false">
            <div class="pt-10">
                <el-row>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">姓名：</span>
                        <span class="val">{{ accfundDetailError.row.name }}</span>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">证件号码：</span>
                        <span class="val">{{ accfundDetailError.row.idCard }}</span>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">参保城市：</span>
                        <span class="val">{{ accfundDetailError.row.addrName }}</span>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">申报类型：</span>
                        <span class="val">{{ accfundDetailError.row.declareTypeName }}</span>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">参保比例：</span>
                        <div class="val" v-html="handleRatioText(accfundDetailError.row, 'detail')"></div>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">缴纳起始月：</span>
                        <span class="val" v-if="accfundDetailError.row.declareType == 5"><span
                                v-if="accfundDetailError.row.insuredBeginDate || accfundDetailError.row.insuredEndDate">{{
                                    accfundDetailError.row.insuredBeginDate }}
                                ~ {{ accfundDetailError.row.insuredEndDate }}</span></span>
                        <span class="val" v-else>{{ accfundDetailError.row.insuredDate }}</span>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">缴纳基数：</span>
                        <span class="val">{{ accfundDetailError.row.tbBase ? getTwoPoint(accfundDetailError.row.tbBase) :
                            '' }}</span>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">批次号：</span>
                        <span class="val">{{ accfundDetailError.row.batchNumber }}</span>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">交互时间：</span>
                        <span class="val">{{ accfundDetailError.row.createTime ?
                            $moment(accfundDetailError.row.createTime).format('YYYY-MM-DD HH: mm: ss') : '' }}</span>
                    </el-col>
                    <el-col :span="8" class="detail-item">
                        <span class="lab">创建人：</span>
                        <span class="val">{{ accfundDetailError.row.createName }}</span>
                    </el-col>
                </el-row>
                <el-row>
                    <!--其它报盘文件配置的数据字段-->
                    <el-col :span="8" class="detail-item" v-for="(item, key, index) in accfundDetailError.row.declareInfo"
                        :key="index">
                        <span class="lab">{{ key }}：</span>
                        <span class="val">{{ item }}</span>
                    </el-col>
                    <!--其它报盘文件配置的数据字段-->
                </el-row>
            </div>
            <div class="text-right mt-10">
                <el-button size="small" class="w-60" type="primary" @click="accfundDetailError.show = false">关闭</el-button>
            </div>
        </el-dialog>

        <!-- 返回顶部 -->
        <backtop />
    </div>
</template>
<script>
import dTable from '@/components/common/table'
import palTabEnd from '@/components/common/pal-tab-end'
import addrSelector from '@/components/common/addrSelector'
import SearchLayout from './component/SearchLayout.vue'
import myDrawer from '@/components/common/socialDetailDrawer.vue'
import { accMul } from '@/utils/socialAccfundProduct'
export default {
    inject: ['getInsuranceList'],
    name: '',
    components: { dTable, palTabEnd, addrSelector, SearchLayout, myDrawer },
    props: [''],
    data() {
        return {
            pathData: [],
            tabs: [],
            tabActive: 0,
            row: null,
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
            //编辑待处理 - 状态变更
            editdrawData: {
                show: false,
                comment: '',
                declareStatus: null,     //更改的状态
                uuid: null,                  //需要更改的uuid
            },
            //待处理表格查询参数
            pendingQueryForm: {
                businessType: 1,           //业务类型 默认为社保
                company: null,               //申报账户的公司
                changeType: [],            //申报类型
                addrName: null,             //参保城市
                addrId: null,                //城市id
                accountNumber: null,              //申报账户
                insuredStartDate: null,      //开始创建时间
                insuredEndDate: null,        //结束创建时间
                keyword: null,              //关键字
                beginDate: null,       //开始交互时间
                endDate: null,          //开始交互时间
                insuredDate: null,       //缴纳起始月
                declareTypes: [],             //校验失败时所需申报类型
                validateStauts: [0]          //校验失败的 校验结果
            },
            QueryForm1: {
                businessType: 1,           //业务类型 默认为社保
                company: null,               //申报账户的公司
                changeType: [],            //申报类型
                addrName: null,             //参保城市
                addrId: null,                //城市id
                accountNumber: null,              //申报账户
                insuredStartDate: null,      //开始创建时间
                insuredEndDate: null,        //结束创建时间
                keyword: null,              //关键字
                beginDate: null,       //开始交互时间
                endDate: null,          //开始交互时间
                insuredDate: null,       //缴纳起始月
                declareTypes: [],             //校验失败时所需申报类型
                validateStauts: [0]          //校验失败的 校验结果
            },
            QueryForm2: {
                businessType: 1,           //业务类型 默认为社保
                company: null,               //申报账户的公司
                changeType: [],            //申报类型
                addrName: null,             //参保城市
                addrId: null,                //城市id
                accountNumber: null,              //申报账户
                insuredStartDate: null,      //开始创建时间
                insuredEndDate: null,        //结束创建时间
                keyword: null,              //关键字
                beginDate: null,       //开始交互时间
                endDate: null,          //开始交互时间
                insuredDate: null,       //缴纳起始月
                declareTypes: [],             //校验失败时所需申报类型
                validateStauts: [0]          //校验失败的 校验结果
            },
            QueryForm3: {
                businessType: 1,           //业务类型 默认为社保
                company: null,               //申报账户的公司
                changeType: [],            //申报类型
                addrName: null,             //参保城市
                addrId: null,                //城市id
                accountNumber: null,              //申报账户
                insuredStartDate: null,      //开始创建时间
                insuredEndDate: null,        //结束创建时间
                keyword: null,              //关键字
                beginDate: null,       //开始交互时间
                endDate: null,          //开始交互时间
                insuredDate: null,       //缴纳起始月
                declareTypes: [],             //校验失败时所需申报类型
                validateStauts: [0]          //校验失败的 校验结果
            },
            QueryForm4: {
                businessType: 1,           //业务类型 默认为社保
                company: null,               //申报账户的公司
                changeType: [],            //申报类型
                addrName: null,             //参保城市
                addrId: null,                //城市id
                accountNumber: null,              //申报账户
                insuredStartDate: null,      //开始创建时间
                insuredEndDate: null,        //结束创建时间
                keyword: null,              //关键字
                beginDate: null,       //开始交互时间
                endDate: null,          //开始交互时间
                insuredDate: null,       //缴纳起始月
                declareTypes: [],             //校验失败时所需申报类型
                validateStauts: [0]          //校验失败的 校验结果
            },
            //校验错误表格查询参数
            errorQueryForm: {
                businessType: 1,           //业务类型 默认为社保
                company: null,               //申报账户的公司
                changeType: [],            //申报类型
                addrName: null,             //参保城市
                addrId: null,                //城市id
                accountNumber: null,              //申报账户
                insuredStartDate: null,      //开始创建时间
                insuredEndDate: null,        //结束创建时间
                keyword: null,              //关键字
                beginDate: null,       //开始交互时间
                endDate: null,          //开始交互时间
                insuredDate: null,       //缴纳起始月
                declareTypes: [],             //校验失败时所需申报类型
                validateStauts: [0],          //校验失败的 校验结果
                batchNumber: ''
            },
            //已处理表格查询参数
            processedQueryForm: {
                businessType: 1,           //业务类型 默认为社保
                company: null,               //申报账户的公司
                changeType: [],            //申报类型
                addrName: null,             //参保城市
                addrId: null,                //城市id
                accountNumber: null,              //申报账户
                insuredStartDate: null,      //开始创建时间
                insuredEndDate: null,        //结束创建时间
                keyword: null,              //关键字
                beginDate: null,       //开始交互时间
                endDate: null,          //开始交互时间
                insuredDate: null,       //缴纳起始月
                declareTypes: [],             //校验失败时所需申报类型
                validateStauts: [0]          //校验失败的 校验结果
            },
            socialDetailError: {            //校验失败社保详情
                show: false,
                row: {}
            },
            accfundDetailError: {           //校验失败公积金详情
                show: false,
                row: {}
            },
        }

    },
    computed: {
        getType() {
            return this.bussinessType == '1' ? this.socialDetail : this.accfundDetail
        },
        //排序表格中的 参保险种字段的 顺序
        getProductNameList() {
            return function (nameList) {
                if (!nameList) {
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
        //保留两位小数
        getTwoPoint() {
            return function (str) {
                if (Number(str) != Number(str)) {
                    return str
                } else if (Number(str)) {
                    return Number(str).toFixed(2)
                } else {
                    return str
                }
            }
        },
    },
    watch: {

    },
    created() {
        let tabs = this.$store.state.tabs
        if (tabs) {
            this.pathData = tabs[this.$route.path]
        }
        // this.pathData.push({ name: '申报数据统计' })
        this.getTableTitleStatistics()
    },
    beforeMount() { },
    mounted() {
        this.getPendingTableData()
    },
    methods: {
        //导出数据
        exportData(type) {
            if (type == 'social') {
                this.exportSocialTableData()
            } else {
                this.exportAccfundTableData()
            }
        },
        // 导出社保选据
        exportSocialTableData() {
            var params = this.$refs[`${this.tabActive}Table`].getParamsQuery()
            var uuids = this.$refs[`${this.tabActive}Table`].getSelectionsArr('uuid')
            var id = this.$refs[`${this.tabActive}Table`].getSelectionsArr('id')
            if (!params) {
                this.$message.warning('请先查询数据')
                return
            }
            var params2 = this.$global.deepcopyArray(params)

            let url = ''
            if (this.tabActive != 6) {
                url = '/agent/declareData/import'
                params2.push({ property: 'uuid', value: uuids })
                params2.push({ property: 'exportStatus', value: this.tabActive == 0 ? 1 : this.tabActive == 5 ? 2 : this.tabActive == 6 ? 4 : 3 })
            } else {
                url = '/agent/declareData/exportFailData'
                params2.push({ property: 'id', value: id })
            }
            this.$downloadFile(
                url,
                'post',
                {
                    query: params2
                },
                this.$global.EXCEL
            )
        },
        // 导出公积金选据
        exportAccfundTableData() {
            let params = this.$refs[`${this.tabActive}Table`].getParamsQuery()
            let ids = this.$refs[`${this.tabActive}Table`].getSelectionsArr('id')
            if (!params) {
                this.$message.warning('请先查询数据')
                return
            }
            var params2 = this.$global.deepcopyArray(params)
            params2.push({ property: 'accfundId', value: ids })
            params2.push({ property: 'exportStatus', value: this.tabActive == 0 ? 1 : this.tabActive == 5 ? 2 : this.tabActive == 6 ? 4 : 3 })
            this.$downloadFile(
                '/agent/declareData/exportNewAccfund',
                'post',
                {
                    query: params2,
                },
                this.$global.EXCEL
            )
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
        //查看详情
        viewRequest(type, row) {
            this.row = row
            console.log(type, row)
            let that = this
            this.$nextTick(() => {
                this.$refs.myDrawer.request(type, this.row)
                // this.$refs.myDrawer.request(type, row)
            })
        },
        //显示撤回
        withdraw(type, type2, row) {
            this.withdrawData.type = type
            this.withdrawData.type2 = type2
            this.withdrawData.row = row
            if (type2 == 'single') {
                this.withdrawData.show = true
                if (this.bussinessType == '1') {
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

            let selects = this.$refs[this.tabActive + 'Table'].selections
            if (selects.length == 0) {
                this.$message.warning('请先选择需要操作的数据')
                return
            }
            let ids = []
            //  1、	只有申报状态为：待申报(1)，待提交(6)，可以撤回，其它状态不允许撤回
            for (let i = 0; i < selects.length; i++) {
                if (selects[i].status == 1 || selects[i].status == 6) {
                    if (this.bussinessType == '1') {
                        ids.push({
                            id: selects[i].id,
                            itemIds: this.withdrawData.type == 'social' ? selects[i].itemId : '',
                            uuid: selects[i].uuid
                        })
                    } else {
                        ids.push({
                            id: selects[i].id,
                            uuid: selects[i].uuid
                        })
                    }
                } else {
                    this.$message.warning('只有待提交和待申报的数据可以作废')
                    return
                }
            }
            this.withdrawData.ids = ids
            this.withdrawData.show = true
        },
        //取消撤回
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
        //确定撤回
        withdrawSumbit() {
            this.$refs.withdrawForm.validate((valid) => {
                if (valid) {
                    var obj = {
                        businessType: this.bussinessType,
                        remark: this.withdrawData.reason,
                        items: this.withdrawData.ids,
                    }
                    this.showLoading()
                    this.$http({
                        url: '/agent/declareData/employeeInsuredWithDraw',
                        method: 'post',
                        data: obj,
                    })
                        .then((res) => {
                            this.closeLoading()
                            if (res.data.code == 200) {
                                this.$message.success(
                                    res.data.message ? res.data.message : '操作成功'
                                )

                                if (this.tabActive === 0) {
                                    //请求待处理的表格数据
                                    this.getPendingTableData()
                                } else if (this.tabActive === 6) {
                                    //请求校验失败的表格数据 校验失败 没有撤回 不用管
                                } else if (this.tabActive === 5) {
                                    //请求已处理的表格数据 校验失败 没有撤回 不用管

                                } else {
                                    //请求 待提交、待申报、申报中、申报失败、已处理的表格数据
                                    this.getOtherTableData()
                                }
                                // if (this.bussinessType == '1') {
                                //     this.getSocialTable()
                                // } else {
                                //     this.getAccfundTable()
                                // }
                                this.cancelWithdraw()
                            }
                        })
                        .catch((err) => {
                            this.closeLoading()
                        })
                }
            })
        },
        //显示编辑弹框
        showEditDraw(type, row) {
            console.log(type, row)
            this.editdrawData = {
                show: true,
                comment: '',
                declareStatus: null,     //更改的状态
                uuid: row.uuid,                  //需要更改的uuid
            }
            this.editdrawData.show = true
        },
        //取消编辑弹框
        cancelEditdraw() {
            this.editdrawData = {
                show: false,
                comment: '',
                declareStatus: null,     //更改的状态
                uuid: null,                  //需要更改的uuid
            }
            this.editdrawData.show = false
            this.$nextTick(() => {
                this.$refs.editdrawForm.clearValidate()
            })
        },
        //确定编辑
        editdrawSumbit() {
            this.$refs.editdrawForm.validate((valid) => {
                if (valid) {
                    var obj = {
                        businessType: this.pendingQueryForm.businessType,
                        comment: this.editdrawData.comment,
                        declareStatus: this.editdrawData.declareStatus,
                        uuid: this.editdrawData.uuid
                    }
                    this.showLoading()
                    this.$http({
                        url: '/agent/declareData/editProcess',
                        method: 'post',
                        params: obj,
                    })
                        .then((res) => {
                            this.closeLoading()
                            if (res.data.code == 200) {
                                this.$message.success(
                                    res.data.message ? res.data.message : '操作成功'
                                )

                                this.getPendingTableData()
                                this.cancelEditdraw()
                            }
                        })
                        .catch((err) => {
                            this.closeLoading()
                        })
                }
            })
        },
        //跳转到规则设置
        routeToRuleSetting() {
            this.$router.push('/ywDataSummary/ruleConfig')
        },
        //获取个表头数据统计 

        async getTableTitleStatistics() {
            const { data: { code, data: result } } = await this.$http({
                url: '/agent/declareData/statisticalDataCount',
                method: 'post'
            })
            if (code === 200) {
                for (const key in result) {
                    if (result.hasOwnProperty(key)) {
                        const value = result[key];
                        this.tabs.push(`${key}(${value})`)
                    }
                }
                // console.log(this.tabs)
            }
        },
        //tab 切换回调
        onTabChange(e) {
            console.log('onTabChange', e)
            if (e === 0) {
                //请求待处理的表格数据
                this.getPendingTableData()
            } else if (e === 6) {
                //请求校验失败的表格数据
                this.getCheckFailureTableData()
            } else if (e === 5) {
                //请求已处理的数据
                this.getProcessedTableData()
            } else {
                //请求 待提交、待申报、申报中、申报失败、已处理的表格数据
                this.getOtherTableData()
            }
        },
        queryPendingTableData(form) {
            console.log('点击了查询', form)
            this.pendingQueryForm = form
            this.bussinessType = form.businessType.toString()
            this.getPendingTableData()
        },
        //获取“待处理”的表格数据
        getPendingTableData() {
            var params = [
                { property: 'businessType', value: this.pendingQueryForm.businessType },
                { property: 'company', value: this.pendingQueryForm.company },
                { property: 'accountNumber', value: this.pendingQueryForm.accountNumber },
                { property: 'addrId', value: this.pendingQueryForm.addrId },
                { property: 'changeType', value: this.pendingQueryForm.declareTypes },
                { property: 'keyword', value: this.pendingQueryForm.keyword },
                { property: 'createStartTime', value: this.pendingQueryForm.insuredStartDate },
                { property: 'createEndTime', value: this.pendingQueryForm.insuredEndDate }
            ]

            this.$refs['0Table'].fetch({
                query: params,
                method: 'post',
                url: '/agent/declareData/employeeSocialInsuredList'
            })
        },
        queryCheckFailureTableData(form) {
            console.log('点击了查询', form)
            this.errorQueryForm = form
            this.bussinessType = form.businessType.toString()
            this.getCheckFailureTableData()
        },
        //获取“校验失败”的表格数据
        getCheckFailureTableData() {
            let searchForms = this.errorQueryForm
            var params = [
                { property: 'businessType', value: searchForms.businessType },
                { property: 'addrName', value: searchForms.addrName },
                { property: 'accountNumbers', value: searchForms.accountNumber ? [searchForms.accountNumber] : [] },
                { property: 'declareTypes', value: searchForms.declareTypes },
                { property: 'insuredDate', value: searchForms.insuredDate },
                { property: 'beginDate', value: searchForms.beginDate },
                { property: 'endDate', value: searchForms.endDate },
                { property: 'validateStauts', value: searchForms.validateStauts },
                { property: 'batchNumber', value: searchForms.batchNumber }
            ]

            this.$refs['6Table'].fetch({
                query: params,
                method: 'post',
                url: '/agent/declareData/failPage'
            })
        },
        queryProcessedableData(form) {
            console.log('点击了查询', form)
            this.processedQueryForm = form
            this.bussinessType = form.businessType.toString()
            this.getProcessedTableData()
        },
        //获取“已处理”的表格数据
        getProcessedTableData() {
            let searchForms = this.processedQueryForm
            var params = [
                { property: 'businessType', value: searchForms.businessType },
                { property: 'company', value: searchForms.company },
                { property: 'accountNumber', value: searchForms.accountNumber },
                { property: 'addrId', value: searchForms.addrId },
                { property: 'changeType', value: searchForms.declareTypes },
                { property: 'keyword', value: searchForms.keyword },
                { property: 'createStartTime', value: searchForms.insuredStartDate },
                { property: 'createEndTime', value: searchForms.insuredEndDate }
            ]

            this.$refs['5Table'].fetch({
                query: params,
                method: 'post',
                url: '/agent/declareData/socialProcessedData'
            })
        },
        queryOtherTableData(form) {
            console.log('点击了查询', form)
            this[`QueryForm${this.tabActive}`] = form
            this.bussinessType = form.businessType.toString()
            this.getOtherTableData()
        },
        //获取“待提交、待申报、申报中、申报失败”的表格数据
        getOtherTableData() {
            let searchForms = this[`QueryForm${this.tabActive}`]
            var params = [
                { property: 'businessType', value: searchForms.businessType },
                { property: 'company', value: searchForms.company },
                { property: 'accountNumber', value: searchForms.accountNumber },
                { property: 'addrId', value: searchForms.addrId },
                { property: 'changeType', value: searchForms.declareTypes },
                { property: 'keyword', value: searchForms.keyword },
                { property: 'createStartTime', value: searchForms.insuredStartDate },
                { property: 'createEndTime', value: searchForms.insuredEndDate },
                { property: 'status', value: [this.tabActive == 1 ? 6 : this.tabActive == 2 ? 1 : this.tabActive == 3 ? 2 : 5] },
            ]

            this.$refs[`${this.tabActive}Table`].fetch({
                query: params,
                method: 'post',
                url: '/agent/declareData/employInsuredDataList'
            })
        },
        handleChangeType(code) {
            //  <!--变更类型（1增，2减，3调基，5补缴）-->
            // {id: '1', name: '投保'}, {id: '3', name: '调整'}, {id: '2', name: '停保'}, {id: '5', name: '补缴'}
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
        //显示校验失败的详情
        viewInfo(type, row) {
            if (type == 1) {
                this.viewSocialInfo(row)
            } else {
                this.viewAccfundInfo(row)
            }
        },
        //校验失败 - 查看社保详情
        viewSocialInfo(row) {
            this.socialDetailError.row = row
            console.log('viewSocialInfo', row)
            this.socialDetailError.show = true
        },
        //校验失败 - 查看公积金详情
        viewAccfundInfo(row) {
            this.accfundDetailError.row = row
            this.accfundDetailError.show = true
        },
        handleRatioText(row, type) {
            var compRatio = row.compRatio;
            var empRatio = row.empRatio;
            var new_compRatio = '';
            var new_empRatio = '';
            if (compRatio == null || compRatio.trim() == '') {
                new_compRatio = '';
            } else if (!isNaN(new_compRatio)) {
                new_compRatio = accMul(compRatio, 100) + '%';
            }

            if (empRatio == null || empRatio.trim() == '') {
                new_empRatio = '';
            } else if (!isNaN(new_empRatio)) {
                new_empRatio = accMul(empRatio, 100) + '%';
            }

            var suppCompRatio = row.suppCompRatio;
            var suppEmpRatio = row.suppEmpRatio;
            var new_suppCompRatio = '';
            var new_suppEmpRatio = '';
            if (suppCompRatio == null || suppCompRatio.trim() == '') {
                new_suppCompRatio = '';
            } else if (!isNaN(new_suppCompRatio)) {
                new_suppCompRatio = accMul(suppCompRatio, 100) + '%';
            }

            if (suppEmpRatio == null || suppEmpRatio.trim() == '') {
                new_suppEmpRatio = '';
            } else if (!isNaN(new_suppEmpRatio)) {
                new_suppEmpRatio = accMul(suppEmpRatio, 100) + '%';
            }

            var text = ''
            if (new_compRatio != '') {
                text += '单位' + new_compRatio
            }
            if (new_empRatio != '') {
                text += (new_compRatio != '' ? '|' : '') + '个人' + new_empRatio
            }
            var text2 = ''
            if (new_suppCompRatio != '' || new_suppEmpRatio != '') {
                if (new_suppCompRatio != '') {
                    text2 += '补充单位' + new_suppCompRatio
                }
                if (new_suppEmpRatio != '') {
                    text2 += (new_suppCompRatio != '' ? '|' : '补充') + '个人' + new_suppEmpRatio
                }
            }
            var res = ''
            if (text == '') {
                res = text2 == '' ? '' : text2
            } else {
                if (text2 == '') {
                    res = text;
                } else {
                    res = text + (type == 'list' ? '<br/>' : '，') + text2
                }
            }
            return res;
        },
        handleDeclareStatus(code) {
            //  <!--申报状态（0作废，1待申报，2申报中，4申报成功，5申报失败,3部分失败）-->
            switch (code) {
                case 0:
                    return '作废'
                case 1:
                    return '待申报'
                case 2:
                    return '申报中'
                case 4:
                    return '申报成功'
                case 5:
                    return '申报失败'
                case 6:
                    return '待提交'
                case 3:
                    return '申报失败（部分）'
            }
            return ''
        },
        //查看详情中的 操作记录 - 执行查询 点击回调 需要跳转到执行查询页面对应的记录
        onDoQuery(row){
            console.log('onDoQuery-------',row)
            this.$router.push({

                path: '/customerAppList/customerIndex',
                query: {
                    appCode: row.appCode,
                    clientId: row.clientId,
                    machineCode: row.machineCode,
                    startTime: row.startTime,
                    endTime:row.endTime
                }
            })
        }
    },  

}
</script>
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
    width: 16px;
    height: 16px;
    background: #FD6154;
    border-radius: 50%;
    font-size: 10px;
    text-align: center;
    line-height: 14px;
    color: #ffffff;
    padding: 3px;
}

.detail-item {
    display: flex;
    margin-bottom: 20px;

    .lab {
        width: 160px;
        text-align: right;
    }

    .val {
        flex: 1;
        text-align: left;
    }
}

/deep/.detail-dialog {
    .el-dialog {
        width: 95% !important;
        max-width: 1300px;
    }
}
</style>