<template>
  <div class="content spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData">
      <template #breadcrumb-btn>
        <el-button  type="primary" size="small" class="s-btn" @click="interfaceValidate">接口校验</el-button>
      </template>
    </breadcrumb>
    <!-- Tabs -->
    <div style="padding: 10px 15px 0 20px">
      <el-row>
        <el-col :span="7" class="display-flex">
          <span class="lab">客户名称：</span>
          <span class="sel">{{info.custName}}</span>
        </el-col>
        <el-col :span="6" class="display-flex">
          <span class="lab">符合贫困人口：</span>
          <span class="sel">{{ info.poorCount }}</span>
        </el-col>
        <el-col :span="6" class="display-flex">
          <span class="lab">失业人员数：</span>
          <span class="sel">{{ info.lostWorkCount }}</span>
        </el-col>
        <!-- <el-col :span="5" class="display-flex">
          <span class="lab">符合月份：</span>
          <span class="sel">{{ info.monthCount }}</span>
        </el-col> -->
        <el-col :span="5" class="display-flex">
          <span class="lab">销售人员：</span>
          <span class="sel">{{ info.saleName }}</span>
        </el-col>
      </el-row>
      <el-row class="mb-5">
        <!-- <el-col :span="7" class="display-flex">
          <span class="lab">销售人员：</span>
          <span class="sel">{{ info.saleName }}</span>
        </el-col> -->
        <el-col :span="7" class="display-flex">
          <span class="lab">交付人员：</span>
          <span class="sel">
            <span v-if="showEditIndex!==0">{{info.serviceName}}<i @click="showEdit(0)" class="el-icon-edit-outline f-cursor font-18 text-blue ml-5" style="vertical-align: middle"></i></span>
            <span v-else>
              <el-input ref="editInput0" v-model.trim="editValue" placeholder="请输入" class="w-150" @blur="saveEditValue()"></el-input>
            </span>
          </span>
        </el-col>
        <el-col :span="6" class="display-flex">
          <span class="lab">失业申领金额：</span>
          <span class="sel">
            <span v-if="showEditIndex!==1">{{info.lostWorkAmt}}<i @click="showEdit(1)" class="el-icon-edit-outline f-cursor font-18 text-blue ml-5" style="vertical-align: middle"></i></span>
            <span v-else>
              <el-input ref="editInput1" v-model.trim="editValue" placeholder="请输入" class="w-150" @blur="saveEditValue()"></el-input>
            </span>
          </span>
        </el-col>
        <el-col :span="6" class="display-flex">
          <span class="lab">退税申领金额：</span>
          <span class="sel">
            <span v-if="showEditIndex!==2">{{info.refundTaxAmt}}<i @click="showEdit(2)" class="el-icon-edit-outline f-cursor font-18 text-blue ml-5" style="vertical-align: middle"></i></span>
            <span v-else>
              <el-input ref="editInput2" v-model.trim="editValue" placeholder="请输入" class="w-150" @blur="saveEditValue()"></el-input>
            </span>
          </span>
        </el-col>
      </el-row>

      <palTab :tabs="tabs" v-model="tabActive" :type="2" @change="changeTab">
        <template #btns>
          <el-button  type="primary" size="small" class="s-btn" @click="getTableData3" v-if="tabActive === 2" style="margin-right:20px !important;">查询</el-button>
          <el-upload
            ref="upload3"
            class="inlineBlock"
            :headers="$global.setUploadHeaders()"
            :action="$global.uploadFileUrl"
            :auto-upload="true"
            accept=".xls,.xlsx,.XLS,.XLSX"
            :show-file-list="false"
            multiple
            :before-upload="onbeforeUpload"
            :on-progress="onProgressFile"
            :on-success="onSuccessFile(3)"
            :on-error="onError"
          >
            <el-button type="primary" class="s-btn">导入基础数据</el-button>
          </el-upload>
          <el-upload
            ref="upload1"
            class="inlineBlock"
            :headers="$global.setUploadHeaders()"
            :action="$global.uploadFileUrl"
            :auto-upload="true"
            accept=".xls,.xlsx,.XLS,.XLSX"
            :show-file-list="false"
            multiple
            :before-upload="onbeforeUpload"
            :on-progress="onProgressFile"
            :on-success="onSuccessFile(1)"
            :on-error="onError"
          >
            <el-button type="primary" class="s-btn ml-10">导入贫困人口筛查表格</el-button>
          </el-upload>
          <el-upload
            ref="upload2"
            class="inlineBlock"
            :headers="$global.setUploadHeaders()"
            :action="$global.uploadFileUrl"
            :auto-upload="true"
            accept=".xls,.xlsx,.XLS,.XLSX"
            :show-file-list="false"
            multiple
            :before-upload="onbeforeUpload"
            :on-progress="onProgressFile"
            :on-success="onSuccessFile(2)"
            :on-error="onError"
          >
            <el-button type="primary" class="s-btn ml-10">导入失业人口筛查表格</el-button>
          </el-upload>
        </template>
      </palTab>

      <!--符合人员清单-->
      <div v-show="tabActive == 0" class="pt-15">
        <!-- 搜索栏 -->
         <el-row style="padding-bottom: 10px;">
            <el-col :span="6" class="display-flex">
              <span class="lab">企业名称：</span>
              <div class="sel">
                <!-- <el-input
                  style="width: 100%;"
                  v-model="search.comName"
                  clearable
                  placeholder="请输入企业名称"
                ></el-input> -->
                <el-select
                  style="width:100%;"
                  v-model="search.comNameList"
                  class="search-row-item"
                  clearable
                  filterable
                  placeholder="请选择类型"
                  multiple>
                  <el-option :label="item" :value="item" v-for="(item,index) in serachComName" :key="index"></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="6" class="display-flex">
              <span class="lab">类型：</span>
              <div class="sel">
                <el-select
                  style="width:100%;"
                  v-model="search.type"
                  class="search-row-item"
                  clearable
                  filterable
                  placeholder="请选择类型"
                  multiple>
                  <el-option label="贫困" :value="1"></el-option>
                  <el-option label="失业" :value="2"></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="6" class="display-flex">
              <span class="lab">姓名：</span>
              <div class="sel">
                <el-input v-model="search.name" clearable placeholder="请输入姓名" style="width:100%;"></el-input>
              </div>
            </el-col>
            <el-col :span="6" class="display-flex">
              <span class="lab">是否生成证明：</span>
              <div class="sel">
                <el-select
                  style="width: 100%;"
                  v-model="search.proveFile"
                  class="search-row-item"
                  clearable
                  filterable
                  placeholder="请选择">
                  <el-option label="是" :value="0"></el-option>
                  <el-option label="否" :value="1"></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="6" class="display-flex">
              <span class="lab">批次号：</span>
              <div class="sel">
                <!-- <el-input v-model="search.batchNo" clearable placeholder="请输入批次号" style="width:100%;"></el-input> -->
                <el-select
                  style="width:100%;"
                  v-model="search.batchNum"
                  class="search-row-item"
                  clearable
                  filterable
                  placeholder="请选择类型"
                  multiple>
                  <el-option :label="item" :value="item" v-for="(item,index) in searchBatchNumList" :key="index"></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="12" class="display-flex">
              <span class="lab">身份证：</span>
              <div class="sel">
                <!-- <el-input v-model="search.idCardStr" placeholder="请输入身份证" style="width:100%;"></el-input> -->
                <el-input type="textarea" :rows="2" placeholder="请输入身份证一行一个" v-model="search.idCardStr" style="width: 210px;"></el-input>
                <span style="margin-left: 10px;">已输入{{ idCardSize }}条</span>
              </div>
            </el-col>
            <el-col :span="6" style="text-align: right;">
              <el-button type="primary" class="s-btn" @click="getTableData1" @keyup.enter.native="getTableData1">查询</el-button>
              <el-button plain class="s-btn" @click="search.show=true" @keyup.enter.native="getTableData1">筛选</el-button>
            </el-col>
         </el-row>
        <!-- 表格 -->
        <dTable @fetch="getTableData1" ref="dTable1" :tableHeight="tableHeight" :paging="true"
                :showSelection="true" :showIndex="true" rowKey="id" @selection-change="selectionChange">
          <u-table-column prop="comName" label="企业名称" min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="type" label="类型" min-width="90" :show-overflow-tooltip="true">
            <template scope="scope">
              {{ getStatusStr2(scope.row.type)}}
            </template>
          </u-table-column>
          <u-table-column prop="name" label="姓名" min-width="90" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="idCard" label="身份证" min-width="150" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="entryTime" label="入职日期" min-width="120" :show-overflow-tooltip="true">
            <template scope="scope">
              {{ scope.row.entryTime ? $moment(scope.row.entryTime).format('YYYY-MM-DD') : '' }}
            </template>
          </u-table-column>
          <u-table-column prop="tbDate" label="参保时间" min-width="90" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="month" label="符合月份" min-width="90" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="proveFileId" label="是否生成证明" min-width="90" :show-overflow-tooltip="true">
            <template scope="scope">
              {{ scope.row.proveFileId ? '是': '否' }}
            </template>
          </u-table-column>
          <!-- <u-table-column prop="poorMonthCount" label="贫困符合月份" min-width="90" :show-overflow-tooltip="true"></u-table-column> -->
          <!-- <u-table-column prop="poorAmt" label="贫困金额" min-width="90" :show-overflow-tooltip="true"></u-table-column> -->
          <!-- <u-table-column prop="lostWorkCount" label="符合失业月份" min-width="90" :show-overflow-tooltip="true"></u-table-column> -->
          <!-- <u-table-column prop="lostWorkAmt" label="失业金额" min-width="90" :show-overflow-tooltip="true"></u-table-column> -->
          <u-table-column prop="batchNums" label="批次号" min-width="150" :show-overflow-tooltip="true">
            <template scope="scope">
              {{ split(scope.row.batchNums) }}
            </template>
          </u-table-column>
          <u-table-column prop="age" label="年龄" min-width="50" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="contractEnd" label="合同截止时间" min-width="100" :show-overflow-tooltip="true">
            <template scope="scope">
              {{ scope.row.contractEnd}}
            </template>
          </u-table-column>
          <u-table-column prop="jobStatus" label="现在就职状态" min-width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="isSubsidy" label="是否已申请过其他补助项目" min-width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="remark" label="备注" min-width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="poorCreateTime" label="创建时间" width="180" :show-overflow-tooltip="true">
            <template scope="scope">
              {{ scope.row.createTime ? $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') : '' }}
            </template>
          </u-table-column>
          <u-table-column prop="poorCreateTime" label="操作" width="100" :show-overflow-tooltip="true" fixed="right">
            <template scope="scope">
              <!-- <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button> -->
              <el-button type="text" size="small" @click="handleEdit(scope.row)">修改</el-button>
              <el-button type="text" size="small" @click="handleRemove(scope.row)">删除</el-button>
            </template>
          </u-table-column>
        </dTable>
        <div style="position: absolute;bottom: 0;z-index: 999;">
          <el-button type="primary" class="s-btn" @click="exportTable(1)">导出符合贫困人口</el-button>
          <el-button type="primary" class="s-btn ml-15" @click="exportTable(2)">导出符合失业人口</el-button>
          <el-button type="primary" class="s-btn ml-15" @click="exportTable(9)">导出基础数据</el-button>
          <el-button type="primary" class="s-btn ml-15" @click="exportTable(3)">生成采集表</el-button>
          <el-button type="primary" class="s-btn ml-15" @click="exportTable(4)">拉取贫困人口证明</el-button>
          <el-button type="primary" class="s-btn ml-15" @click="exportTable(8)">拉取失业人口证明</el-button>
          <el-button type="primary" class="s-btn ml-15" @click="exportTable(5)">生成采集表-最新</el-button>
          <el-button type="primary" class="s-btn ml-15" @click="exportTable(6)">数据对比</el-button>
          <el-button type="danger" class="s-btn ml-15" @click="exportTable(7)">删除</el-button>
        </div>
      </div>
      <!-- 历史导入表格 -->
      <div v-show="tabActive == 1" class="pt-15">
        <!-- 搜索栏 -->
        <el-row style="padding-bottom: 10px;">
            <el-col :span="6" class="display-flex">
              <span class="lab">批次号：</span>
              <div class="sel">
                <!-- <el-input
                  style="width: 100%;"
                  v-model="hisTorySearch.excelTaskId"
                  clearable
                  placeholder="请输入批次号"
                ></el-input> -->
                <el-select
                  style="width:100%;"
                  v-model="hisTorySearch.excelTaskIdList"
                  class="search-row-item"
                  clearable
                  filterable
                  placeholder="请选择类型"
                  multiple>
                  <el-option :label="item" :value="item" v-for="(item,index) in searchBatchNumList" :key="index"></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="6" class="display-flex">
              <span class="lab">类型：</span>
              <div class="sel">
                <el-select
                  style="width:100%;"
                  v-model="hisTorySearch.typeList"
                  class="search-row-item"
                  clearable
                  filterable
                  placeholder="请选择类型" multiple>
                  <el-option label="贫困" :value="1"></el-option>
                  <el-option label="失业" :value="2"></el-option>
                  <el-option label="基础数据" :value="3"></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="6" class="display-flex">
              <span class="lab">状态：</span>
              <div class="sel">
                <el-select
                  style="width:100%;"
                  v-model="hisTorySearch.excelTaskStatus"
                  class="search-row-item"
                  clearable
                  filterable
                  placeholder="请选择状态">
                  <el-option label="执行中" :value="1"></el-option>
                  <el-option label="执行完成" :value="2"></el-option>
                  <el-option label="执行异常" :value="3"></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="6" style="text-align: right;">
              <el-button type="primary" class="s-btn" @click="getTableData2" @keyup.enter.native="getTableData2">查询</el-button>
            </el-col>
         </el-row>
        <!-- 表格 -->
        <dTable @fetch="getTableData2" ref="dTable2" :tableHeight="tableHeight2" :paging="true"
                :showSelection="true" :showIndex="true" rowKey="id" @selection-change="selectionHistory">
          <u-table-column prop="type" label="类型" min-width="50" :show-overflow-tooltip="true">
            <template scope="scope">
              {{ getStatusStr2(scope.row.type)}}
            </template>
          </u-table-column>
          <u-table-column prop="excelTaskId" label="批次号" min-width="80" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="fileName" label="文件名称" min-width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="excelTaskStatus" label="状态" min-width="65" :show-overflow-tooltip="true">
            <template scope="scope">
              {{ getExcelTaskStatus2(scope.row.excelTaskStatus)}}
            </template>
          </u-table-column>
          <u-table-column prop="result" label="执行结果" min-width="90" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="row" label="导入总条数" min-width="60" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="succeedRow" label="符合人数" min-width="50" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="executeNum" label="已经处理条数" min-width="50" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="isRequestNum" label="第三方接口请求成功条数" min-width="50" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="executeTimeStr" label="执行时长" min-width="70" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="createName" label="创建人" min-width="50" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="createTime" label="创建时间" width="180" :show-overflow-tooltip="true">
            <template scope="scope">
              {{ scope.row.createTime ? $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') : '' }}
            </template>
          </u-table-column>
          <u-table-column fixed="right" label="操作" width="210">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="goDetail(scope.row)">查看</el-button>
              <el-button type="text" size="small" @click="doExport(scope.row,1)">导出</el-button>
              <el-button type="text" size="small" style="margin-left: 10px;" @click="doExport(scope.row,2)">导出清单</el-button>
              <el-button type="text" size="small" style="color: #F56C6C;" @click="removeHistory(scope.row)" v-if="scope.row.result != '成功'">删除</el-button>
            </template>
          </u-table-column>
        </dTable>
        <div style="position: absolute;bottom: 35px;z-index: 999;">
          <el-button type="danger" class="s-btn" @click="removebatchHistory">删除</el-button>
        </div>
      </div>
      <div v-show="tabActive == 2">
        <!-- 表格 -->
        <dTable @fetch="getTableData3" ref="dTable3" :tableHeight="tableHeight3" :paging="true"
                :showSelection="true" :showIndex="true" rowKey="id" @selection-change="selectionHistory">
          <u-table-column prop="type" label="类型" min-width="60" :show-overflow-tooltip="true">
            <template scope="scope">
              {{ getStatusStr2(scope.row.type)}}
            </template>
          </u-table-column>
          <u-table-column prop="excelTaskId" label="批次号" min-width="120" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="fileName" label="文件名称" min-width="100" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="result" label="状态" min-width="90" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="createName" label="创建人" min-width="90" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="createTime" label="创建时间" width="180" :show-overflow-tooltip="true">
            <template scope="scope">
              {{ scope.row.createTime ? $moment(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') : '' }}
            </template>
          </u-table-column>
          <u-table-column fixed="right" label="操作" width="180">
            <template slot-scope="scope">
              <el-button  type="text" size="small" @click="doExport(scope.row,1)" v-if="scope.row.ossPath">下载</el-button>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>
    <!-- 编辑 -->
    <el-dialog title="修改" :visible.sync="setForm.show" :before-close="handleCancel" width="30%" :close-on-click-modal="false" custom-class="cust-dialog" >
      <el-form :model="setForm" ref="setForm" label-width="100px" :rules="rules">
        <el-form-item prop="entryTime" label="入职日期">
          <el-date-picker
            v-model="setForm.entryTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="选择日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item prop="tbDate" label="参保时间">
          <el-date-picker
            v-model="setForm.tbDate"
            type="month"
            value-format="yyyy-MM"
            placeholder="选择日期">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button class="s-btn" @click="handleCancel">取 消</el-button>
        <el-button type="primary" class="s-btn" @click="handleConfirm">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 数据对比 -->
    <el-dialog title="数据对比" :visible.sync="dataForm.show" :before-close="handleDataCancel" width="500px" :close-on-click-modal="false" custom-class="cust-dialog" >
      <el-form :model="dataForm" ref="dataForm" label-width="80px" :rules="rules">
        <el-form-item prop="typeOne" label="类型">
          <el-select
            style="width:100%;"
            v-model="dataForm.typeOne"
            class="search-row-item"
            clearable
            filterable
            placeholder="请选择类型"
            @change="changeTypeOne">
            <el-option label="贫困" :value="1"></el-option>
            <el-option label="失业" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="batchNum" label="批次号">
          <el-select
            style="width:100%;"
            v-model="dataForm.batchNum"
            class="search-row-item"
            clearable
            filterable
            placeholder="请选择批次号"
            multiple
            no-data-text="请先选择类型">
            <el-option :label="item" :value="item" v-for="(item,index) in dataForm.batchNumList" :key="index"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="customerName" label="企业名称">
          <el-select
            style="width:100%;"
            v-model="dataForm.comName"
            class="search-row-item"
            clearable
            filterable
            placeholder="请选择企业名称"
            multiple
            no-data-text="请先选择类型">
            <el-option :label="item" :value="item" v-for="(item,index) in dataForm.comNameList" :key="index"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button class="s-btn" @click="handleDataCancel">取 消</el-button>
        <el-button type="primary" class="s-btn" @click="handleDataConfirm">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 接口校验 -->
    <el-dialog title="接口校验" ref="interface" :visible.sync="interfaceData.show" :before-close="interfaceConfirm" width="600px" :close-on-click-modal="false" custom-class="cust-dialog" >
      <el-table
        :data="interfaceData.tableData"
        border
        style="width: 100%">
        <el-table-column
          prop="name"
          label="接口名称"
          width="180">
        </el-table-column>
        <el-table-column
          prop="url"
          label="接口地址"
          width="180">
        </el-table-column>
        <el-table-column
          prop="type"
          label="接口结果">
          <template slot-scope="scope">
            <el-tooltip class="item" effect="dark" content="Top Center 提示文字" placement="top" v-if="scope.row.errorData">
              <div slot="content" >
                {{ scope.row.errorData }}
              </div>
              <div style="color:#3E82FF;">{{ interfaceType(scope.row.type) }}</div>
            </el-tooltip>
            <div v-else>{{ interfaceType(scope.row.type) }}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="操作"
          label="接口结果">
            <template slot-scope="scope">
              <el-button  type="text" size="small" @click="interFaceCheck(scope.row.url,scope.$index)">校验</el-button>
            </template>
        </el-table-column>
      </el-table>
      <div style="color: #F56C6C;white-space: pre-wrap;">说明：
1.检测用到参数为已确认贫困或失业的人员，接口返回参数res.errcode=0表示检测成功或证明可下载。
2.检测是调用第三方接口，故会产生费用。
3.因第三方接口不稳定，建议校验3次，如果3次还不行，那就是接口异常！
      </div>
      <div slot="footer" class="dialog-footer" style="text-align: center;">
        <el-button type="primary" class="s-btn" @click="interfaceConfirm">知道了</el-button>
      </div>
    </el-dialog>
    <!-- 筛选 -->
    <el-drawer title="筛选" :visible.sync="search.show" :wrapperClosable="false" custom-class="spl-filter-drawer"
      :show-close="true">
      <div class="pd-20 pt-10">
        <div class="type-title mt-0">
          <span class="text">年龄</span>
        </div>
        <div class="pl-20 pr-20 pt-20 pb-20 age" style="display: flex; align-items: center; padding-right: 0px">
          <el-input-number v-model="search.minAge" :min="1" :max="99" label="年龄" style="width: 100%;"></el-input-number>
          <span style="margin: 0 5px">至</span>
          <el-input-number v-model="search.maxAge" :min="search.minAge" :max="99" label="年龄" style="width: 100%;"></el-input-number>
        </div>
        <div class="type-title mt-0">
          <span class="text">入职日期</span>
        </div>
        <div class="pl-20 pr-20 pt-20 pb-20" style="display: flex; align-items: center; padding-right: 0px">
          <el-date-picker v-model="search.startEntryTime" type="date" value-format="yyyy-MM-dd" placeholder="开始月份"
            style="width: 100%" :picker-options="getPickerOption2('start', 'search','endEntryTime')"></el-date-picker>
          <span style="margin: 0 5px">至</span>
          <el-date-picker v-model="search.endEntryTime" type="date" value-format="yyyy-MM-dd"
            :picker-options="getPickerOption2('end', 'search','startEntryTime')" placeholder="结束月份"
            style="width: 100%"></el-date-picker>
        </div>
        <div class="type-title mt-0">
          <span class="text">参保时间</span>
        </div>
        <div class="pl-20 pr-20 pt-20 pb-20" style="display: flex; align-items: center; padding-right: 0px">
          <el-date-picker v-model="search.startTbDate" type="month" value-format="yyyy-MM" placeholder="开始月份"
            style="width: 100%" :picker-options="getPickerOption2('start', 'search','endTbDate')"></el-date-picker>
          <span style="margin: 0 5px">至</span>
          <el-date-picker v-model="search.endTbDate" type="month" value-format="yyyy-MM"
            :picker-options="getPickerOption2('end', 'search','startTbDate')" placeholder="结束月份"
            style="width: 100%"></el-date-picker>
        </div>
        <div class="type-title mt-0">
          <span class="text">是否一人多类型</span>
        </div>
        <div class="pl-20 pr-20 pt-20 pb-20" style="display: flex; align-items: center; padding-right: 0px">
          <el-select v-model="search.isMore" placeholder="请选择" style="width:100%;">
            <el-option label="是" :value="0"></el-option>
            <el-option label="否" :value="1"></el-option>
          </el-select>
        </div>
        <div class="type-title mt-0">
          <span class="text">是否失业满半年</span>
        </div>
        <div class="pl-20 pr-20 pt-20 pb-20" style="display: flex; align-items: center; padding-right: 0px">
          <el-select v-model="search.isPoorFull" placeholder="请选择" style="width:100%;">
            <el-option label="是" :value="0"></el-option>
            <el-option label="否" :value="1"></el-option>
          </el-select>
        </div>
        <div class="type-title mt-0">
          <span class="text">登记时间早于入职且间隔6个月以上</span>
        </div>
        <div class="pl-20 pr-20 pt-20 pb-20" style="display: flex; align-items: center; padding-right: 0px">
          <el-select v-model="search.abcdegf" placeholder="请选择" style="width:100%;">
            <el-option label="是" :value="0"></el-option>
            <el-option label="否" :value="1"></el-option>
          </el-select>
        </div>
      </div>
      <div class="drawer-footer-buts">
        <el-button class="btn--border-blue s-btn" @click="resetFilterSearch">重置</el-button>
        <el-button type="primary" class="s-btn ml-12" @click="confirmSearch">确认筛选</el-button>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import dTable from '@/components/common/table'
import palTab from '@/components/common/pal-tab'
export default {
  name: '',
  components: { dTable, palTab },
  props: [''],
  data () {
    return {
      pathData: [],
      loading: {},
      tabs: ['符合人员清单', '历史导入表格','下载列表'],
      tabActive: 0,
      id: '',
      search: {
        idCardStr: '',
        name: '',
        comName:'',
        type:[],
        batchNo:'',
        proveFile:null,
        comNameList:[],
        batchNum:[],
        show:false,
        minAge:0,
        maxAge:99,
        startEntryTime:'',
        endEntryTime:'',
        startTbDate:'',
        endTbDate:'',
        //是否一人多类型 传值 0或1 0=是 1=否
        isMore:'',
        isPoorFull:null,
        abcdegf:null,
      },
      info: {
        custId:'',
        custName:'',
        saleName:'',
        monthCount:'',
        lostWorkCount:'',
        poorCount:'',
        refundTaxAmt:'',
        serviceName:"",
        lostWorkAmt:"",
      },
      showEditIndex: -1,
      editValue: '',
      batchNum: '',
      fileList: [],
      selectFileCount: 0,
      tableCount:0,
      selectionList:[],
      setForm:{
        show:false,
        tbDate:'',
        entryTime:''
      },
      dataForm:{
        show:false,
        typeOne:null,
        batchNum:[],
        comName:[],
        batchNumList:[],
        comNameList:[]
      },
      hisTorySearch:{
        type:"",
        excelTaskId:"",
        hisTorySearch:[],
        typeList:[],
        excelTaskStatus:''
      },
      rules:{
        typeOne:[{ required: true, message: '请选择类型', trigger: 'change' }],
        batchNum:{ type: 'array', required: true, message: '请选择批次号', trigger: 'change' },
        entryTime:[{ required: true, message: '请选择入职日期', trigger: 'change' }],
        tbDate:[{ required: true, message: '请选择投保月', trigger: 'change' }]
      },
      serachComName:[],
      searchBatchNumList:[],
      selectionHistoryList:[],
      interfaceData:{
        show:false,
        tableData:[]
      },
    }
  },
  computed: {
    tableHeight: function () {
      return this.$global.bodyHeight - 500 + 'px'
    },
    tableHeight2: function () {
      return this.$global.bodyHeight - 420 + 'px'
    },
    tableHeight3: function () {
      return this.$global.bodyHeight - 350 + 'px'
    },
    getStatusStr(){
      return function(type){
        if(type === 0){
          return '处理中'
        }else if(type === 1){
          return '导入成功'
        }else if(type === 2){
          return '导入失败'
        }
      }
    },
    split(){
      return function(str){
        if(str){
          str = str.split(',')
          str = str.filter(item=>{
            return item
          })
          return str.join(',')
        }
        return str
      }
    },
    getStatusStr2(){
      return function(type){
        if(type === 1){
          return '贫困'
        }else if(type === 2){
          return '失业'
        }else if(type == 3){
          return '基础数据'
        }
        return ''
      }
    },
    getExcelTaskStatus2() {
      return function(type) {
        if (type === 0) {
          return '初始化'
        } else if (type === 1) {
          return '执行中'
        } else if (type === 2) {
          return '执行完成'
        } else if (type === 3) {
          return '执行异常'
        }
        return ''
      }
    },
    idCardSize(){
      var num = 0;
      this.search.idCardStr.split('\n').forEach(item=>{
        if (item.length > 0) num++;
      })
      return num
    },
    getPickerOption2 () {
      return function (time, key,key2) {
        var obj = {}
        var self = this
        if (time == 'start') {
          return {
            disabledDate (time) {
              if (!self[key][key2]) {
                return false
              }
              let curDate = new Date(self[key][key2]).getTime()
              return time.getTime() > curDate
            }
          }
        } else {
          return {
            disabledDate (time) {
              if (!self[key][key2]) {
                return false
              }
              let curDate = new Date(
                self[key][key2]
              ).getTime()
              return time.getTime() < curDate - 86400000
            }
          }
        }
      }
    },
    interfaceType(){
      return function(type){
        if(type == '待检测'){
          return '待检测'
        }
        if(type != 0){
          return '接口异常'
        }
        if(type == 0){
          return '接口正常'
        }
      }
    }
  },
  watch: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({ name: '客户详情'})
    document.addEventListener('keyup',(e)=>{
      // console.log(e)
      let key = e.keyCode
      if(key == 13 && this.tabActive == 0){
        this.getTableData1()
      }else if(key == 13 && this.tabActive == 1){
        this.getTableData2()
      }
    })
  },
  mounted () {
    this.id = this.$route.query.id
    this.getTableData1()
    this.custDetail()
    this.getSearchDict()


  },
  methods: {
    changeTypeOne(){
      this.$global.showLoading()
      this.$http({
        url:"/subsidy/social/socialCost/findBatchNumAndComName",
        method: 'post',
        data: {
          custId:this.id,
          typeOne:this.dataForm.typeOne,
        }
      }).then(res=>{
        this.dataForm.batchNumList = res.data.data.batchNum
        this.dataForm.comNameList = res.data.data.comName
        this.$global.closeLoading()
      }).catch(err=>{
        console.log(err)
        this.$global.closeLoading()
      })
    },
    getSearchDict(){
      this.$http({
        url:"/subsidy/social/socialCost/findBatchNumAndComName",
        method: 'post',
        data: {
          custId:this.id,
        }
      }).then(res=>{
        this.searchBatchNumList = res.data.data.batchNum
        this.serachComName = res.data.data.comName
        this.$global.closeLoading()
      }).catch(err=>{
        console.log(err)
        this.$global.closeLoading()
      })
    },
    handleDataConfirm(){
      this.$refs.dataForm.validate(valid=>{
        if(valid){
          this.$global.showLoading()
          this.$http({
            url:"/subsidy/social/socialCost/compareExcel",
            method: 'post',
            responseType: 'blob',
            data: {
              custId:this.id,
              typeOne:this.dataForm.typeOne,
              batchNum:this.dataForm.batchNum,
              comNameList:this.dataForm.comName
            }
          }).then(res=>{
            this.download(res)
            this.dataForm.show = false
            this.$global.closeLoading()
          }).catch(err=>{
            console.log(err)
            this.$global.closeLoading()
          })
        }
      })
    },
    handleDataCancel(){
      this.dataForm = this.$options.data().dataForm
    },
    exportTable(type){
      if(this.tableCount <= 0 && [3,5,6].indexOf(type) <= -1){
        this.$message.error('列表暂无数据')
        return
      }
      // if(this.selectionList.length <= 0){
      //   if([1,2,3].indexOf(type) > -1){
      //     this.$message.error('请勾选数据后再导出')
      //   }else if(type == 4){
      //     this.$message.error('请选择数据再去拉去证明')
      //   }
      //   return
      // }
      var shiyeIds = []
      var pinkunIds = []
      var idCards = []
      var ids = []
      var url = '/subsidy/social/socialCost/poorExcel'
      if(type === 1){
        url = '/subsidy/social/socialCost/poorExcel'
        // pinkunIds = this.selectionList.filter(item=>{
        //   return item.pinkunId
        // }).map(item=>item.pinkunId)
        // if(pinkunIds.length <=0){
        //   this.$message.error('请至少选择一个贫困人员')
        //   return
        // }
      }else if(type === 2){
        url = "/subsidy/social/socialCost/poorExcel"
        // shiyeIds = this.selectionList.filter(item=>{
        //   return item.shiyeId
        // }).map(item=>item.shiyeId)
        // if(shiyeIds.length <=0){
        //   this.$message.error('请至少选择一个失业人员')
        //   return
        // }
      }else if(type === 3){
        url = "/subsidy/social/socialCost/excelOldAll"
        // shiyeIds = this.selectionList.filter(item=>{
        //   return item.shiyeId
        // }).map(item=>item.shiyeId)
        // pinkunIds = this.selectionList.filter(item=>{
        //   return item.pinkunId
        // }).map(item=>item.pinkunId)
      }else if(type === 4){
        url = "/subsidy/social/socialCost/exportCertZip"
        if(this.selectionList.length <= 0){
          this.$message.error('请勾选需要拉取的数据')
          return
        }
        let list = this.selectionList.filter(item=>item.type == 1)
        if(list.length <= 0){
          this.$message.error('请选择贫困类型的人员拉取证明！')
          return
        }
      }else if(type === 5){
        url = "/subsidy/social/socialCost/excelAll"
      }else if(type === 6){
        this.dataForm = this.$options.data().dataForm
        this.dataForm.show = true
        this.$nextTick(()=>{
          this.$refs.dataForm.clearValidate()
        })
        return
      }else if(type === 7){
        url = "/subsidy/social/socialCost/deleteEmpByIds"
        if(this.selectionList.length <= 0){
          this.$message.error('请勾选要删除的数据')
          return
        }
      }else if(type == 8){
        url = '/subsidy/social/socialCost/exportUnemploymentZip'
        if(this.selectionList.length <=0){
          this.$message.error('请勾选要拉取失业证明的数据！')
          return
        }
        let list = this.selectionList.filter(item=>item.type == 2)
        if(list.length <= 0){
          this.$message.error('请选择失业类型的人员拉取证明！')
          return
        }
      }else if(type == 9){
        url = "/subsidy/social/socialCost/exportBaseDate"
      }
      ids = this.selectionList.map(item=>item.id)
      // idCards = this.selectionList.map(item=>item.idCard)
      var responseType = 'blob'
      if(type == 7){
        responseType = 'json'
        this.$confirm('是否删除选中数据', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          showClose: false,
          customClass: 'pal-confirm',
          type: 'warning',
        }).then(()=>{
          this.exprotExcel({type,idCards,ids,responseType,url})
        }).catch(()=>{})
        return
      }else if(type == 4 || type == 8){
        responseType = 'json'
      }
      this.exprotExcel({type,idCards,ids,responseType,url})
    },
    exprotExcel({type,idCards,ids,responseType,url}){
      this.$global.showLoading()
      this.$http({
        url:url,
        method: 'post',
        responseType: responseType,
        data: {
          custId:this.id,
          idCards:this.search.idCardStr.split('\n').filter(item=>item.trim() != ''),
          name:this.search.name,
          comNameList:this.search.comNameList,
          type:this.search.type,
          // batchNumOne:this.search.batchNo,
          batchNum:this.search.batchNum,
          proveFile:this.search.proveFile,
          typeOne:type,
          ids:ids,
          isPoorFull:this.search.isPoorFull,
          abcdegf:this.search.abcdegf
          // pinkunIds:pinkunIds,
          // shiyeIds:shiyeIds,
        }
      }).then(res=>{
        // if(!res.headers.filename){
        //   this.$message.error('暂无证明')
        //   this.$global.closeLoading()
        //   return
        // }
        if(type == 7 && res.data.code == 200){
          this.$message.success('操作成功')
          this.getTableData1()
        }else if([4,8].indexOf(type) > -1 && res.data.code == 200){
          this.$message.success('正在执行，请到下载列表查看')
        }else if([4,8].indexOf(type) > -1 && res.data.code != 200){
          this.$message.error(res.data.message)
        }
        if(responseType == 'json'){
          this.$global.closeLoading()
          return
        }
        res.data.text().then((text)=>{
          this.$global.closeLoading()
          try{
            var json = JSON.parse(text)
            if(json.code == 500){
              this.$message.error(json.message)
              return
            }
          }catch(err){
            if([4,5,6,8].indexOf(type) > -1){
              this.download(res,{ type: "application/zip" })
            }else if([1,2,3,9].indexOf(type) > -1){
              this.download(res)
            }
          }
        })
        this.custDetail()
      }).catch(err=>{
        console.log(err)
        this.$global.closeLoading()
      })
    },
    download(res,fileType){
      const blob = new Blob([res.data],fileType ? fileType : { type: 'application/vnd.ms-excel; charset=utf-8' })
      const a = document.createElement('a')
      const URL = window.URL || window.webkitURL
      const herf = URL.createObjectURL(blob)
      a.download = decodeURI(res.headers.filename);
      a.href = herf
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      window.URL.revokeObjectURL(herf)
    },
    getTableData1 () {
      let params = [
        { property: 'custId', value: this.id },
        { property: 'idCards', value: this.search.idCardStr.split('\n').filter(item=>item.trim() != '') },
        { property: 'name', value: this.search.name },
        // { property: 'comName', value: this.search.comName },
        { property: 'type', value: this.search.type },
        // { property: 'batchNo', value: this.search.batchNo },
        { property: 'proveFile', value: this.search.proveFile },
        { property: 'batchNum', value: this.search.batchNum },
        { property: 'comNameList', value: this.search.comNameList },
        { property: 'minAge', value: this.search.minAge },
        { property: 'maxAge', value: this.search.maxAge },
        { property: 'startEntryTime', value: this.search.startEntryTime },
        { property: 'endEntryTime', value: this.search.endEntryTime },
        { property: 'startTbDate', value: this.search.startTbDate },
        { property: 'endTbDate', value: this.search.endTbDate },
        { property: 'isMore', value: this.search.isMore },
        { property: 'isPoorFull', value: this.search.isPoorFull },
        { property: 'abcdegf', value: this.search.abcdegf },
      ]
      this.custDetail()
      var that = this
      this.$refs.dTable1.fetch({
        query: params,
        method: 'post',
        url: '/subsidy/social/socialCost/listmatchings',
        callback:function(res){
          that.tableCount = res.records
        }
      })
    },
    getTableData2 () {
      let params = [
        { property: 'custId', value: this.id },
        { property: 'typeList', value: this.hisTorySearch.typeList },
        { property: 'excelTaskIdList', value: this.hisTorySearch.excelTaskIdList },
        { property: 'excelTaskStatus', value: this.hisTorySearch.excelTaskStatus },
      ]
      this.$refs.dTable2.fetch({
        query: params,
        method: 'post',
        url: '/subsidy/social/socialCost/pageSocialCost'
      })
    },
    getTableData3 () {
      let params = [
        { property: 'custId', value: this.id },
      ]
      this.$refs.dTable3.fetch({
        query: params,
        method: 'post',
        url: '/subsidy/social/excelTask/pageExcelTask'
      })
    },
    changeTab (index) {
      if (index === 0) {
        this.getTableData1()
      } else if(index === 1) {
        this.getTableData2()
      } else if(index === 2){
        this.getTableData3()
      }
    },
    showEdit (index) {
      this.showEditIndex = index
      if (this.showEditIndex === 0) {
        // 交付人员
        this.editValue = this.info.serviceName
      } else if (this.showEditIndex === 1) {
        // 失业申领金额
        this.editValue = this.info.lostWorkAmt
      } else if (this.showEditIndex === 2) {
        // 退税申领金额
        this.editValue = this.info.refundTaxAmt
      }
      this.$nextTick(() => {
        this.$refs['editInput' + index].focus()
      })
    },
    saveEditValue () {
      if (this.showEditIndex === 0) {
      // 交付人员
        this.info.serviceName = this.editValue
      }

      if(Number(this.editValue) != Number(this.editValue)){
        this.$message.error("请输入数字")
        this.showEditIndex = -1
        return
      }
      if (this.showEditIndex === 1) {
        // 失业申领金额
        this.info.lostWorkAmt = Number(this.editValue).toFixed(2)
      } else if (this.showEditIndex === 2) {
        // 退税申领金额
        this.info.refundTaxAmt = Number(this.editValue).toFixed(2)
      }
      this.$global.showLoading()
      this.$http({
        url:'/subsidy/social/socialCost/updateCustDetail',
        method: 'post',
        data: {
          lostWorkAmt:this.info.lostWorkAmt,
          serviceName:this.info.serviceName,
          refundTaxAmt:this.info.refundTaxAmt,
          custId:this.id
        }
      }).then(res=>{
        this.$message.success('操作成功')
        this.$global.closeLoading()
      }).catch(err=>{
        this.$global.closeLoading()
      })
      this.showEditIndex = -1
    },
    onbeforeUpload(file){
      var split =  file.name.split('.')
      var arr = ['xls','XLS','xlsx','XLSX']
      if(arr.indexOf(split[split.length -1]) <= -1){
        this.$message.error('请上传excel表格格式为xls,xlsx')
        return false
      }
      return true
    },
    onProgressFile (event, file, fileList) {
      this.$global.showLoading('导入中...')
      this.selectFileCount = fileList.length
    },
    onSuccessFile (type) {
      return (response)=>{
        if (response.code === 200) {
          let item = response.data[0]
          this.fileList.push({
            fileId: item.fileID,
          })
        }
        if (this.selectFileCount === this.fileList.length) {
          this.doSave(type)
        }
      }
    },
    onError(err, file, fileList){
      this.$message.error(err)
      this.$refs.upload1.clearFiles()
      this.$refs.upload2.clearFiles()
      this.$refs.upload3.clearFiles()
    },
    doSave (type) {
      let ids = []
      this.fileList.map(item => {
        ids.push(item.fileId)
      })
      var url = '/subsidy/excel/import/importSocialCost'
      if(type === 1 || type === 2){
        url = '/subsidy/excel/import/importSocialCost'
      }else{
        url = '/subsidy/excel/import/importUpdateSocialCost'
      }
      this.$http({
        url:url,
        method: 'post',
        data: {
          fileIds: ids,
          custId:this.id,
          type:type
        }
      }).then((data) => {
        if (data.data.code == 200) {
          this.batchNum = data.data.data
          this.fileList = []
          this.$refs.upload1.clearFiles()
          this.$refs.upload2.clearFiles()
          this.$refs.upload3.clearFiles()

          this.$global.closeLoading()
          this.$message.success('操作成功')
          if(this.tabActive == 0){
            this.getTableData1()
          }else{
            this.getTableData2()
          }
        }
      }).catch(() => {
        this.fileList = []
        this.$refs.upload1.clearFiles()
        this.$refs.upload2.clearFiles()
        this.$refs.upload3.clearFiles()
        this.$global.closeLoading()
      })
    },
    custDetail(){
      this.$global.showLoading()
      this.$http({
        url:"/subsidy/social/socialCost/custDetail",
        method: 'post',
        data: {
          custId:this.id,
        }
      }).then(res=>{
        this.$global.closeLoading()
        if(res.data.code == 200){
          for (const key in res.data.data) {
            this.info[key] = res.data.data[key]
          }
        }
      }).catch(err=>{
        console.log(err)
        this.$global.closeLoading()
      })
    },
    doExport (row,type) {
      if(row.ossPath && type == 1){
        this.$global.showLoading('处理中')
        this.$downloadFile(`/policy/sys/file/download/${row.ossPath}`, 'get',{},'','',()=>{this.$global.closeLoading()})
        return
      }
      let url = '/subsidy/social/socialCost/excelHistory'
      this.$global.showLoading('处理中')
      this.$downloadFile(
        url,
        'post',
        {batchNumOne: row.excelTaskId,custId:row.custId,},
        this.$global.EXCEL,
        '',
        ()=>{this.$global.closeLoading()}
      )
    },
    selectionChange(selection){
      this.selectionList = selection
    },
    handleEdit(row){
      this.setForm.tbDate = row.tbDate
      this.setForm.id = row.id
      if(row.entryTime){
        this.setForm.entryTime = this.$moment(row.entryTime).format('YYYY-MM-DD')
      }
      this.setForm.show = true
    },
    handleCancel(){
      this.setForm.show = false
      this.setForm = this.$options.data().setForm
    },
    handleConfirm(){
      this.$refs.setForm.validate(valid=>{
        if(valid){
          this.$global.showLoading()
          this.$http({
            url: `/subsidy/social/socialCost/updateEmp`,
            method: 'post',
            data: {
              id:this.setForm.id,
              entryTime:this.setForm.entryTime,
              tbDate:this.setForm.tbDate
            }
          }).then((data) => {
              this.$global.closeLoading()
              this.getTableData1()
              this.$message.success('操作成功')
              this.handleCancel()
          }).catch(() => {
            this.$global.closeLoading()
          })
        }
      })
    },
    handleRemove(row){
      this.$confirm('是否删除此数据', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
          this.deleteEmp(row.id)
        })
        .catch(() => {})
    },
    deleteEmp(id){
      this.$global.showLoading()
      this.$http({
        url: `/subsidy/social/socialCost/deleteEmp`,
        method: 'post',
        data: {
          id:id
        }
      }).then((data) => {
          this.$global.closeLoading()
          this.getTableData1()
          this.$message.success('操作成功')
      }).catch(() => {
        this.$global.closeLoading()
      })
    },
    resetFilterSearch(){
      this.search.minAge = 0
      this.search.maxAge = 99
      this.search.startEntryTime = ''
      this.search.endEntryTime = ''
      this.search.startTbDate = ''
      this.search.endTbDate = ''
      this.search.isMore = ''
      this.search.isPoorFull = null
      this.search.abcdegf = null
    },
    confirmSearch(){
      this.search.show = false
      this.getTableData1()
    },
    removeHistory(row){
      var ids = [row.id]
      this.$confirm('是否删除数据', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      }).then(()=>{
        this.removeHistoryApi(ids)
      }).catch(()=>{})
    },
    selectionHistory(selection){
      this.selectionHistoryList = selection
    },
    removebatchHistory(){
      if(this.selectionHistoryList.length <= 0){
        this.$message.error('请选择数据')
        return
      }
      var arr = this.selectionHistoryList.filter(item=>item.result == '成功')
      if(arr.length > 0){
        this.$message.error('所选的数据中包含成功状态，不能批量删除')
        return
      }
      var ids = this.selectionHistoryList.map(item=>item.id)
      this.$confirm('是否删除选中的数据？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      }).then(()=>{
        this.removeHistoryApi(ids)
      }).catch(()=>{})
    },
    removeHistoryApi(ids){
      this.$global.showLoading()
      this.$http({
        url: `/subsidy/social/excelTask/detByIds`,
        method: 'post',
        data: {
          ids:ids,
          custId:this.id
        }
      }).then((data) => {
          this.$global.closeLoading()
          this.getTableData2()
          this.$message.success('操作成功')
      }).catch(() => {
        this.$global.closeLoading()
      })
    },
    interfaceValidate(){
      this.interfaceData.show = true
      this.monitorTest()
    },
    interfaceConfirm(){
      this.interfaceData.show = false
      this.interfaceData.tableData = []
    },
    monitorTest(){
      this.$global.showLoading({
        target:this.$refs.interface.$refs.dialog
      })
      this.$http({
        url: `/subsidy/monitor/urls`,
        method: 'get',
      }).then((data) => {
          this.$global.closeLoading()
          for (const key in data.data.data) {
            this.interfaceData.tableData.push({
              name:data.data.data[key],
              url:key,
              type:'待检测',
              errorData:""
            })
          }
      }).catch(() => {
        this.$global.closeLoading()
      })
    },
    interFaceCheck(urls,index){
      this.$global.showLoading({
        target:this.$refs.interface.$refs.dialog
      })
      this.$http({
        url: `/subsidy/monitor/test`,
        method: 'post',
        data:{
          url:urls
        }
      }).then(({data}) => {
          this.$global.closeLoading()
          var res = JSON.parse(data.data.res)
          this.interfaceData.tableData[index].type = res.errcode
          if(urls == 'https://apiv2.mgr.img.zgh135.com/bat/shiye' && (!res.info || !res.info['身份证'])){
            this.interfaceData.tableData[index].errorData = res
            this.interfaceData.tableData[index].type = -1
            return
          }else if(urls == 'https://apiv2.mgr.img.zgh135.com/bat/shiye' && res.info && res.info['身份证']){
            this.interfaceData.tableData[index].type = 0
            this.interfaceData.tableData[index].errorData = ''
            return
          }
          if(res.errcode != 0){
            this.interfaceData.tableData[index].errorData = res
          }else{
            this.interfaceData.tableData[index].errorData = ''
          }
      }).catch(() => {
        this.$global.closeLoading()
      })
    },
    goDetail(row){
      var batchNum = row.excelTaskId
      var ossPath = row.ossPath
      var custId = row.custId
      var type = row.type
      this.$router.push('/policyApply/historyTableDetail?batchNum='+batchNum + `&ossPath=${ossPath}&custId=${custId}&type=${type}`)
    }
  }
}
</script>
<style lang="less">
.lab{
  width: 110px;
  text-align: right;
  line-height: @inputHeight;
  // flex:1;
  flex-shrink: 0;
}
.sel{
  flex: 1;
  padding-right: 20px;
  line-height: @inputHeight;
}
.display-flex{
  padding-bottom: 10px;
}
.age{
  .el-input__inner{
    height: 40px !important;
  }
}
.type-title{
  font-size: 14px !important;
  height: 40px !important;
  .text{
    height: 20px !important;
    line-height: 40px !important;
    &::before{
      height: 20px !important;
    }
  }
}

.el-tooltip__popper{
  max-width: 1000px !important;
}
</style>
