<template>
  <!-- 报盘文件设置 -->
  <div class="spl-container">
    <breadcrumb :data="pathData">
      <div slot="breadcrumb-btn">
        <el-button size="small" class="btn--border-blue" @click="goCustomizedList">定制报盘</el-button>
        <el-button type="primary" size="small" @click="goAdd">新增</el-button>
      </div>
    </breadcrumb>
    <div class="pl-20 pr-20">
      <div class="search-row">
        <el-row>
  <!--        <el-col :span="10">
            <div class="search-row display-flex">
              <span class="ml-20 label">参保地：</span>
              <addrSelector v-model="formData.addrName" :addrArr="bussinssType.allAddr" @changeAddrSelect="changeSearch"></addrSelector>
              &lt;!&ndash; <el-button size="small" type="primary" class="ml-20 w-60" @click="getSocialTableData">查询</el-button> &ndash;&gt;
            </div>
          </el-col>-->
          <el-col :span="8">
            <el-input v-model.trim="formData.addrName" placeholder="请输入省/市关键字" style="width: 95%;max-width: 250px" @keyup.enter.native="getTableData" clearable></el-input>
          </el-col>
          <el-col :span="8">
            <div class="display-flex" style="align-items: center;">
              <span class="ml-20 label">业务类型：</span>
              <el-checkbox-group v-model="formData.bussinssType" @change="getTableData">
                <el-checkbox :label="1">社保</el-checkbox>
                <el-checkbox :label="2">公积金</el-checkbox>
              </el-checkbox-group>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="display-flex" style="align-items: center;">
              <span class="ml-20 label">申报类型：</span>
              <el-checkbox-group v-model="formData.changeTypes">
                <el-checkbox :label="1">增员</el-checkbox>
                <el-checkbox :label="2">减员</el-checkbox>
                <el-checkbox :label="5">补缴</el-checkbox>
                <el-checkbox :label="3">调基</el-checkbox>
              </el-checkbox-group>
            </div>
          </el-col>
        </el-row>
        <el-row class="mt-20">
        <el-col :span="8">
          <div class="display-flex" style="align-items: center;">
            <span class="">状态：</span>
            <el-checkbox-group v-model="formData.statuses" @change="getTableData">
              <el-checkbox :label="1">有效</el-checkbox>
              <el-checkbox :label="0">无效</el-checkbox>
            </el-checkbox-group>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="display-flex" style="align-items: center;">
            <span class="ml-20 label">校验版块：</span>
            <el-checkbox-group v-model="formData.columnRules">
              <el-checkbox :label="1">入数据</el-checkbox>
              <el-checkbox :label="2">导报盘</el-checkbox>
            </el-checkbox-group>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="display-flex" style="align-items: center;">
            <span class="ml-20 label">定制报盘：</span>
            <el-checkbox-group v-model="formData.customMades">
              <el-checkbox :label="1">有</el-checkbox>
              <el-checkbox :label="0">无</el-checkbox>
            </el-checkbox-group>
            <div class="flex1 text-right">
              <el-button type="primary" size="small" @click="getTableData">查询</el-button>
            </div>
          </div>
        </el-col>
      </el-row>
      </div>
      <div>
        <dTable @fetch="getTableData" ref="table" style="margin-top: 0;" :tableHeight="tableHeight"  :showIndex='false' :showNotFixedIndex="true" :showSelection='false' row-key="id" row-id="id">
          <u-table-column prop="addrName" label="参保城市" header-align="center" align="center" :show-overflow-tooltip="true"></u-table-column>
          <u-table-column prop="bussinssType" label="业务类型" header-align="center" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{scope.row.bussinssType == 1 ? '社保':'公积金'}}</p>
            </template>
          </u-table-column>
          <u-table-column prop="changeTypeName" label="申报类型"  header-align="center" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <p>{{formatType(scope.row.changeType)}}</p>
            </template>
          </u-table-column>
          <u-table-column prop="tpls" label="关联申报文件" min-width="300" header-align="center" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <div v-if="scope.row.tpls.length == 1 && scope.row.tpls[0].tplName == null">
                <p class="table-fileName-list">暂无</p>
              </div>
              <div v-else>
                <p v-for="(item,index) in scope.row.tpls" :key="index" class="table-fileName-list" @click="downLoadFile(item)">{{`【${item.tplTypeName}】【${item.operationTypeName}】${item.comAccountNumber ?`【${item.comAccountNumber}】`:''}` + item.tplName}}</p>
              </div>
            </template>
          </u-table-column>
          <u-table-column prop="customMadeNumber" label="定制报盘数"  header-align="center" align="center" :show-overflow-tooltip="true" width="120">
            <template slot-scope="scope">
              <p v-if="scope.row.customMadeNumber > 0" style="padding-left:20px;" class="table-fileName-list text-blue" @click="goCustomizedList(scope.row,'list')">{{scope.row.customMadeNumber}}</p>
            </template>
          </u-table-column>
          <u-table-column prop="ruleNumber" label="校验规则数"  header-align="center" align="center" :show-overflow-tooltip="true" width="120">
            <template slot-scope="scope">
              <p v-if="scope.row.ruleNumber > 0" style="padding-left:20px;" class="table-fileName-list text-blue" @click="goValidateRules(scope.row.uuid)">{{scope.row.ruleNumber}}</p>
            </template>
          </u-table-column>
          <u-table-column prop="status" label="状态"  header-align="center" align="center" :show-overflow-tooltip="true" width="100">
            <template slot-scope="scope">
              <p>{{scope.row.status == 1 ? '有效':'无效'}}</p>
            </template>
          </u-table-column>
          <u-table-column label="操作" align="left" width="300">
            <template slot-scope="scope">
              <div v-if="scope.row.status == '1'">
                <el-button type="text" size="small" class="text-blue" @click="handleSocialView(scope.row)">报盘信息维护</el-button>
                <div class="opt-btn-split"></div>
                <el-button type="text" size="small" class="text-blue" @click="associatedFileDialog(scope.row,1)">关联文件</el-button>
                <div class="opt-btn-split"></div>
                <el-button type="text" size="small" class="text-blue" @click="goValidateRules(scope.row.uuid)">校验规则</el-button>
                <div class="opt-btn-split"></div>
                <el-button type="text" size="small" class="text-gray" @click="disableFile(scope.row)">禁用</el-button>
              </div>
            </template>
          </u-table-column>
        </dTable>
      </div>
    </div>
    <!-- 关联文件 -->
    <el-dialog
      title="关联文件"
      :visible.sync="dialogVisible"
      width="900px"
      :before-close="handleClose"
      :close-on-click-modal="false">
      <div id="upload-dialog">
        <div style="padding:0 20px;font-size: 16px;font-weight: bold;">{{rowData.addrName}}-{{rowData.bussinssType == '1' ? '社保':'公积金'}}-{{formatType(rowData.changeType)}}</div>
        <div class="file-content-box" style="padding-top:0px;">
          <div class="upload-tips">
            <p>1、请导入政府网站原始模板，单元格以#字段#标识，如：#身份证号#，不要改模板名字</p>
            <p>2、#字段#中的字段，请采用报盘文件中的字段名</p>
            <p>3、同名文件请不要重复关联</p>
            <p>4、若指定单位社保号，说明该单位将采用此文件进行申报</p>
            <p>5、缺失关联标记，<span style="color:#3e82ff;cursor:pointer;" @click="openRelation">请点我添加</span></p>
          </div>
        </div>
        <div class="file-content-box">
          <el-form :model="uploadFileData" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
            <div class="upload-file-box">
              <div style="display:flex;">
                <el-form-item prop="operationType" label-width="0" style="margin-bottom:22px;margin-right:10px;">
                  <el-select v-model="uploadFileData.operationType" placeholder="请选择子节点" style="width:230px;" clearable>
                    <el-option
                      v-for="(item,index) in operationTypeList"
                      :key="index"
                      :label="item.name"
                      :value="item.code">
                    </el-option>
                  </el-select>
                </el-form-item>

                <el-form-item prop="tplType" label-width="0" style="margin-bottom:22px;margin-right:10px;">
                  <el-select v-model="uploadFileData.tplType" placeholder="请选择关联标记" style="width:225px;" clearable>
                    <el-option
                      v-for="(item,index) in options[rowData.bussinssType]"
                      :key="index"
                      :label="item.tplTypeName"
                      :value="item.tplType">
                    </el-option>
                  </el-select>
                </el-form-item>
                <span class="font-12 ml-10 mb-20" style="color:#666;padding-top: 8px;">{{getKeyName(uploadFileData.tplType,this.relationData.tplItems)}}</span>
              </div>
              <div style="display:flex;">
                <el-form-item prop="" label-width="0" style="margin-bottom:22px;margin-right:10px;">
                  <el-select v-model="uploadFileData.comAccountNumber" :placeholder="'请指定' + (rowData.bussinssType == '1'?'单位社保号':'单位公积金号')+'，选填'" style="width:230px;" clearable filterable>
                    <el-option
                      v-for="(item,index) in accountNumberList"
                      :key="index"
                      :label="item.name"
                      :value="item.accountNumber">
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item prop="uploadFileName" label-width="0" style="margin-bottom:0;">
                  <input type="text" v-model="uploadFileData.uploadFileName" readonly style="width:220px;">
                </el-form-item>
                <div class="upload-file">
                  <input type="file" @change="changeFile" ref="uploadFile"  accept=".xls,.xlsx,.XLS,.XLSX">选择文件
                </div>
                <div class="upload-file-btn" @click="confirmAssociated">确定关联</div>
              </div>
              <div class="error" v-if="associatedFileErrMsg.length > 0">
                <div><i class="el-icon-error" style="color:red;"></i> 出现错误：</div>
                <div v-for="(item,index) in associatedFileErrMsg" :key='index'>{{item}}</div>
              </div>
            </div>
          </el-form>

          <div class="mt-20 mb-10">已关联</div>
          <!-- <div class="file-list" v-if="associatedFileList.length > 0">
            <div class="file" v-for="(item,index) in associatedFileList" :key="index" @click="downLoadFile(item)">
              {{`【${getTplTypeStr(item.tplType)}】` + item.tplName}}
              <span class="file-remove" @click.stop="removeFile(item,index)"></span>
            </div>
          </div> -->
          <div class="cust-dialog">
            <el-table
              :data="fileTable"
              border
              header-cell-class-name="table-header"
              style="width: 100%"
              :max-height="300"
              >
              <el-table-column
                fixed
                type="index"
                width="50"
                label="序号">
                <template slot-scope="scope">
                  <div style="text-align:center;">{{scope.$index + 1 }}</div>
                </template>
              </el-table-column>
              <el-table-column
                prop="operationTypeStr"
                label="子节点"
                width="100">
                <template slot-scope="scope">
                  <div>{{scope.row.operationTypeStr}}</div>
                </template>
              </el-table-column>
              <el-table-column
                prop="processTypeStr"
                label="关联标记"
                width="100">
                <template slot-scope="scope">
                  <div>{{scope.row.tplTypeName}}</div>
                </template>
              </el-table-column>
              <el-table-column
                prop="comAccountNumber"
                :label="rowData.bussinssType == '1' ? '单位社保号' :'单位公积金号'"
                max-width="200">
                <template slot-scope="scope">
                  <div>{{getDeclareAccountStr(scope.row.comAccountNumber)}}</div>
                </template>
              </el-table-column>
              <el-table-column
                prop="operatorName"
                label="已关联申报文件"
                >
                <template slot-scope="scope">
                  <div class="file-list">
                    <div class="file " @click="downLoadFile(scope.row)">
                      {{scope.row.tplName}}
                      <span class="file-remove" @click.stop="removeFile(scope.row)"></span>
                    </div>
                  </div>
                </template>
              </el-table-column>
            </el-table>
        </div>
        </div>
      </div>
    </el-dialog>
    <el-dialog
        title="关联标记维护"
        :visible.sync="showRelation"
        width="1200px"
        :before-close="cancelRelationData"
        :close-on-click-modal="false">
        <div id="upload-dialog">
          <div class="file-content-box">
            <div style="display:flex;font-size:14px;padding-bottom:20px;">
              <p style="width:50%;"><span style="color:red;">*</span>参保城市：{{this.rowData.addrName}}</p>
              <p style="width:50%;"><span style="color:red;">*</span>业务类型：{{this.rowData.bussinssType == 1 ? '社保' : "公积金"}}</p>
            </div>
            <el-form :model="relationData" :rules="rules" ref="relationForm" label-width="100px" class="demo-ruleForm">
              <el-table
                :data="relationData.tplItems"
                border
                header-cell-class-name="table-header"
                style="width: 100%"
                ref="table2"
              >
                <el-table-column  label="关联标记" width="140" align="center" prop="date" >
                  <template slot="header">
                    <div>
                      <p>
                        <span style="color:red;">*</span>关联标记
                      </p>
                    </div>
                  </template>
                  <template scope="scope">
                    <el-form-item
                      label
                      label-width="0"
                      :prop="'tplItems.'+scope.$index+'.tplType'"
                      :rules="rules.relationData.tplType"
                    >
                      <el-select v-model="scope.row.tplType" placeholder="请选择" style="width:100%;" @change="changeTplType($event,scope.$index)">
                        <el-option
                          v-for="(item,index) in relationData.options[rowData.bussinssType]"
                          :key="index"
                          :label="item.dictName"
                          :value="item.dictCode"
                          >
                        </el-option>
                      </el-select>
                    </el-form-item>
                  </template>
                </el-table-column>
                <el-table-column label="申报险种" align="center" prop="date" width="280">
                  <template slot="header">
                    <div>
                      <p>
                        <span style="color:red;">*</span>申报险种
                      </p>
                    </div>
                  </template>
                  <template scope="scope">
                    <el-form-item
                      label
                      label-width="0"
                      :prop="'tplItems.' + scope.$index +'.items'"
                      :rules="rules.relationData.items"
                    >
                      <el-select v-model="scope.row.items" placeholder="请选择" style="width:100%;" multiple  value-key="itemCode" :disabled="scope.row.disabled" ref="elSelect">
                        <el-option
                          v-for="(item,index) in relationData[rowData.bussinssType]"
                          :key="index"
                          :label="item.itemName"
                          :value="item"
                         >
                        </el-option>
                      </el-select>
                    </el-form-item>
                  </template>
                </el-table-column>
                <el-table-column label="增补合并申报" align="center" prop="date" width="320">
                  <template slot="header">
                    <div>
                      <p>
                        <span style="color:red;">*</span>增补合并申报
                      </p>
                    </div>
                  </template>
                  <template scope="scope">
                    <div style="text-align: left !important;padding-top: 10px !important;">
                      <el-form-item
                        label
                        label-width="0"
                        :prop="'tplItems.' + scope.$index +'.merge'"
                      >
                        <el-radio-group v-model="scope.row.merge" @input="mergeChange(scope.$index)">
                          <el-radio :label="true">是</el-radio>
                          <el-radio :label="false">否</el-radio>
                        </el-radio-group>
                      </el-form-item>

                      <el-row v-if="scope.row.merge">
                        <el-col :span="7" style="text-align: right">
                          <label class="required">基数：</label>
                        </el-col>
                        <el-col :span="15">
                          <el-form-item
                            label
                            label-width="0"
                            :prop="'tplItems.'+scope.$index+'.mergeAddRule.baseType'"
                            :rules="rules.relationData.baseType"
                          >
                            <el-select v-model="scope.row.mergeAddRule.baseType" placeholder="请选择" style="width:100%;">
                              <el-option label="取增员基数" :value="1"></el-option>
                              <el-option label="取补缴基数" :value="5"></el-option>
                            </el-select>
                          </el-form-item>
                        </el-col>
                      </el-row>

                      <el-row v-if="scope.row.merge">
                        <el-col :span="7" style="text-align: right">
                          <label class="required">形式：</label>
                        </el-col>
                        <el-col :span="15">
                          <el-form-item
                            label
                            label-width="0"
                            :prop="'tplItems.'+scope.$index+'.mergeAddRule.mergeType'"
                            :rules="rules.relationData.mergeType"
                          >
                            <el-select v-model="scope.row.mergeAddRule.mergeType" placeholder="请选择" style="width:100%;">
                              <el-option label="参保日期合并" :value="1"></el-option>
                              <el-option label="附加补缴日期" :value="2"></el-option>
                            </el-select>
                          </el-form-item>
                        </el-col>
                      </el-row>

                      <el-row v-if="scope.row.merge && scope.row.mergeAddRule.mergeType==2">
                        <el-col :span="7" style="text-align: right">
                          <label class="required">附加补缴信息：</label>
                        </el-col>
                        <el-col :span="15">
                          <el-form-item
                            label
                            label-width="0"
                            :prop="'tplItems.'+scope.$index+'.mergeAddRule.bjFieldRule'"
                            :rules="rules.relationData.bjFieldRule"
                          >
                            <el-input v-model="scope.row.mergeAddRule.bjFieldRule"></el-input>
                          </el-form-item>
                        </el-col>
                      </el-row>

                      <el-row v-if="scope.row.merge">
                        <el-col :span="7" style="text-align: right">
                          <label class="required" >其他规则：</label>
                        </el-col>
                        <el-col :span="15">
                          <el-form-item
                            label
                            label-width="0"
                            :prop="'tplItems.'+scope.$index+'.mergeAddRule.mergeRule'"
                            :rules="rules.relationData.mergeRule"
                          >
                            <el-select v-model="scope.row.mergeAddRule.mergeRule" placeholder="请选择" style="width:100%;">
                              <el-option label="增补险种不同可合并" :value="0"></el-option>
                              <el-option label="增补险种完全一致才合并" :value="1"></el-option>
                            </el-select>
                          </el-form-item>
                        </el-col>
                      </el-row>

                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="申报前开启校验" align="center" prop="date" width="150">
                  <template slot="header">
                    <div>
                      <p>
                        <span style="color:red;">*</span>申报前开启校验
                      </p>
                    </div>
                  </template>
                  <template scope="scope">
                    <div style="text-align: left !important;padding-top: 10px !important;">
                      <el-form-item
                        label
                        label-width="0"
                        :prop="'tplItems.' + scope.$index +'.mergeAddRule.validateRule'"
                      >
                        <el-switch
                          v-model="scope.row.mergeAddRule.validateRule"
                          :active-value="1"
                          :inactive-value="0"
                          active-color="#13ce66">
                        </el-switch>
                      </el-form-item>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="回盘数据差异核对" align="center" prop="date" width="150">
                  <template slot="header">
                    <div>
                      <p>
                        <span style="color:red;">*</span>回盘数据差异核对
                      </p>
                    </div>
                  </template>
                  <template scope="scope">
                    <div style="text-align: left !important;padding-top: 10px !important;">
                      <el-form-item
                        label
                        label-width="0"
                        :prop="'tplItems.' + scope.$index +'.mergeAddRule.differenceRule'"
                      >
                        <el-switch
                          v-model="scope.row.mergeAddRule.differenceRule"
                          :active-value="1"
                          :inactive-value="0"
                          active-color="#13ce66">
                        </el-switch>
                      </el-form-item>
                    </div>
                  </template>
                </el-table-column>
                 <el-table-column label="操作" align="center">
                  <template scope="scope">
                    <div style="display:flex;justify-content: space-around;">
                      <span style="cursor:pointer;color:red;" v-if="relationData.tplItems.length > 1" @click="removeTplItems(scope.row,scope.$index)">移除</span>
                      <span style="cursor:pointer;color:#3e82ff" @click="addTplItems">添加</span>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </el-form>
            <div class="text-red mt-10">{{ relationError }}</div>
          </div>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button  size="small" class="w-80" @click="cancelRelationData" >取 消</el-button>
          <el-button type="primary" size="small" class="w-80" @click="confirmRelationData">确定保存</el-button>
        </span>
    </el-dialog>
  </div>
</template>
<script>

  import dTable from '../../components/common/table'
  import addrSelector from '../../components/common/addrSelector'
  export default {
    components: {addrSelector, dTable},
    data() {
      return {
        /*bussinssType:{
          allAddr:[]
        },*/
        formData: {
          // addrId:'',
          addrName: '',
          bussinssType: [],
          changeTypes: [],
          statuses: [1],
          columnRules: [],
          customMades: []
        },
        dialogVisible:false,
        pathData: [],
        associatedFileList:[],
        uploadFileData:{
          uploadFileName:'',
          fileID:'',
          fileData:null,
          tplType:"",
          operationType: null,
          comAccountNumber:""
        },
        rules: {
          uploadFileName: [
            { required: true, message: '请先上传文件', trigger: 'change' },
          ],
          tplType:[
            { required: true, message: '请选择关联标记', trigger: 'change' },
          ],
          operationType:[
            { required: true, message: '请选择子节点', trigger: 'change' },
          ],
          relationData:{
            tplType:[
              { required: true, message: '请选择关联标记', trigger: 'change' },
              {
                required: true,
                trigger: 'change',
                validator: this.checkTplType,
              },
            ],
            items:[
              { type: 'array', required: true, message: '请选择申报险种', trigger: 'change' }
            ],
            baseType: [
              { required: true, message: '请选择基数规则', trigger: 'change' },
            ],
            mergeType: [
              { required: true, message: '请选择合并形式', trigger: 'change' },
            ],
            bjFieldRule: [
              { required: true, message: '请输入附加补缴信息', trigger: 'change' },
            ],
            mergeRule: [
              { required: true, message: '请选择其他规则', trigger: 'change' },
            ],
            validateRule: [
              { required: true, message: '请选择其他规则', trigger: 'change' },
            ],
            differenceRule: [
              { required: true, message: '请选择其他规则', trigger: 'change' },
            ]
          }
        },
        associatedFileErrMsg:[],
        rowData:{
          bussinssType:'1'
        },
        operationTypeList:[],
        loading:null,
        options:{
          '1':[],
          '2':[]
        },
        allOptions:[],
        timer:false,
        showRelation:false,
        relationData:{
          tplItems: [
            {
              items: [],
              tplType: "",
              tplTypeName: "",
              disabled:false,
              merge: false,
              mergeAddRule: {
                id: null,
                addrId: null,
                addrName: null,
                businessType: null,
                tplTypeCode: null,
                baseType: null,
                mergeType: null,
                bjFieldRule: null,
                mergeRule: null,
                validateRule: 0,
                differenceRule: 0
              }
            }
          ],
          '1':[],
          '2':[],
          options:{}
        },
        defaultRelationData:[{
          items: [],
          tplType: "",
          tplTypeName: "",
          merge: false,
          mergeAddRule: {
            id: null,
            addrId: null,
            addrName: null,
            businessType: null,
            tplTypeCode: null,
            baseType: null,
            mergeType: null,
            bjFieldRule: null,
            mergeRule: null
          }
        }],
        fileTable:[],
        accountNumberList:[], //社保号或公积金号
        relationError:''    //标记错误信息
      }
    },
    computed: {
      tableHeight: function () {
        return this.$global.bodyHeight - 310 + 'px'
      },
      formatType(){
        return function(index){
          var list = ['增员','减员','调基','变更','补缴']
          return list[index - 1]
        }
      },
      getTplTypeStr(){
        return function (key){
          var str = ''
          this.allOptions.forEach(item=>{
            if(item.dictCode == key){
              str = item.dictName
            }
          })
          return str
        }
      },
      getOperationTypeStr() {
        return function (changeType, operationType) {
          if (operationType == 1) {
            if (changeType == 1) {
              return "开户";
            } else if (changeType == 2) {
              return "封存";
            } else if (changeType == 3) {
              return "调基";
            } else if (changeType == 5) {
              return "补缴";
            }
          } else if (operationType == 2){
            return "启封";
          } else if (operationType == 3){
            return "转入";
          } else if (operationType == 4){
            return "转出";
          }
          return "";
        }
      },
      getOptionName(){
        return function(key,option){
          console.log(key,option)
          var str = ''
          option.forEach(item=>{
            if(item.dictCode == key){
              str = item.dictName
            }
          })
          return str
        }
      },
      getKeyName(){
         return function(key,option){
          var str = ''
          var mergeStr = ''
          option.forEach((item,index)=>{
            if(item.tplType == key){
              item.items.forEach((item1,idx)=>{
                str += item1.itemName + ((idx == item.items.length - 1) ? '' : '、')
              })
              if (item.merge) {
                mergeStr = ' 增补合并申报【是】'
              } else {
                mergeStr = ' 增补合并申报【否】'
              }
            }
          })
          return str ? '(' + str + ')' + mergeStr : ''
        }
      },
      getDeclareAccountStr(){
        return function(code){
          var str = ''
          this.accountNumberList.forEach(item=>{
            if(item.accountNumber == code){
              str = item.name
            }
          })
          return str
        }
      }
    },
    created() {
      let tabs = this.$store.state.tabs
      if (tabs) {
        this.pathData = tabs[this.$route.path]
      }
      let that = this
      this.$nextTick(() => {
        // that.getTableData()
        // that.getAddr()
        this.getDictByKey('10004','1')
        this.getDictByKey('10008','2')
        this.getDictByKey('10007','1')
        if(sessionStorage.getItem('offerFileSetting-params')){
          var obj = JSON.parse(sessionStorage.getItem('offerFileSetting-params'))
          if(obj.changeTypes){
            this.formData = obj
            this.getTableData()
          }
        }
      })
    },
    mounted() {
    },
    methods: {
      //改变设置关联标记时
      changeTplType(val,index){
        if(val == '10007004'){
          this.relationData.tplItems[index].items = []
          this.relationData.tplItems[index].disabled = true
          var check = false
          //检测是否有工伤类型
          this.relationData[this.rowData.bussinssType].forEach(item=>{
            if(item.itemCode =='10003002'){
              check = true
            }
          })
          if(check){
            this.relationData.tplItems[index].items.push({itemCode: "10003002", itemName: "工伤"})
          }else{
            this.$message.error('此地区未设置工伤险种，不可设置单工伤关联标记')
          }
        }else{
          this.relationData.tplItems[index].disabled = false
        }
        this.relationData.tplItems[index].mergeAddRule.tplTypeCode = val
        // console.log(this.$refs.elSelect)
      },
      checkTplType(rule,value,callback){
        var arr = []
        this.relationData.tplItems.forEach(item=>{
          if(item.tplType == value){
            console.log('item',item,value)
            arr.push(this.getOptionName(value,this.relationData.options[this.rowData.bussinssType]))
          }
        })
        if(arr.length >= 2){
          return callback(new Error(`关联标记${arr[0]}重复添加，请更正`))
        }
        return callback()
      },
      //取消关联标记
      cancelRelationData(){
        this.$refs.relationForm.clearValidate()
        this.showRelation = false
        this.relationError = ""
        this.$set(this.relationData,'tplItems',this.deepCopy(this.defaultRelationData))
        // console.log(this.defaultRelationData)
      },
      //关联标记确定
      confirmRelationData(){
        this.$refs.relationForm.validate(valid=>{
          if(valid){
            this.showLoading()
            this.$http({
              url:'policy/offerSettings/saveAddrTplItems',
              method:'post',
              data:{
                addrId:this.rowData.addrId,
                businessType:this.rowData.bussinssType,
                tplItems:this.relationData.tplItems,
                addrName:this.rowData.addrName
              }
            }).then(({data})=>{
              if(data.code == 200 && !data.data){
                this.$message.success(data.message ? data.message : '操作成功')
                this.showRelation = false
                this.relationError = ""
                this.findAddrTplItems(this.rowData.addrId,this.rowData.bussinssType)
              }else {
                //存在报错信息 需要提示
                this.relationError = data.data
              }
              this.closeLoading()

            }).catch(err=>{
              console.log(err)
              this.closeLoading()
            })
          }
        })
      },
      changeItem(val){
        // console.log(val)
      },
      mergeChange(index) {
        if (this.relationData.tplItems[index].merge) {
          if (this.relationData.tplItems[index].mergeAddRule.mergeType == null) {
            this.relationData.tplItems[index].mergeAddRule.mergeType = 1
          }
          if (this.relationData.tplItems[index].mergeAddRule.baseType == null) {
            this.relationData.tplItems[index].mergeAddRule.baseType = 1
          }
        }
      },
      //添加关联标记
      addTplItems(){
       this.relationData.tplItems.push(
        {
          items: [],
          tplType: "",
          tplTypeName: "",
          merge: false,
          mergeAddRule: {
            id: null,
            addrId: this.rowData.addrId,
            addrName: this.rowData.addrName,
            businessType: this.rowData.bussinssType,
            tplTypeCode: null,
            baseType: null,
            mergeType: null,
            bjFieldRule: null,
            mergeRule: null,
            validateRule: 0,
            differenceRule: 0
          }
        })
      },
      //移除关联标记
      removeTplItems(row,index){
        if (!row.tplType) {
          this.relationData.tplItems.splice(index,1)
          return;
        }
        this.showLoading()
        this.checkSelectTplCount(this.rowData.addrId,row.tplType).then(res=>{
          this.closeLoading()
          if(res.data && res.data.code ==200){
            if(res.data.data > 0){
              this.$message.error('此标记已被文件关联，不得删除')
            }else if(res.data.data <= 0){
              this.relationData.tplItems.splice(index,1)
            }
          }
        }).catch(err=>{
          this.closeLoading()
        })
      },
      //报盘文件设置-根据参保地id，模板类型获取关联的文件个数
      checkSelectTplCount(addrId,tplTypeCode){
        return this.$http({
          url: `policy/offerSettings/selectTplCount/${addrId}/${tplTypeCode}`,
          method: 'post',
          headers:{
            customSuccess:true
          }
        })
      },
      //关闭关联标记
      handleCloseRelation(){
        this.showRelation = false
      },
      //打开关联标记
      openRelation(){
        if(this.relationData[this.rowData.bussinssType].length <= 0){
          this.$message.error('此地区下没有设置参保险种，请先设置')
          return
        }
        this.showRelation = true
        this.$nextTick(item=>{
          this.$refs.relationForm.clearValidate()
        })
      },
      /*changeSearch(obj){
        clearTimeout(this.timer)
        this.timer = setTimeout(()=>{
          this.formData.addrId = obj.id
          this.getTableData()
        },100)
      },*/
      //去新增
      goAdd(){
        this.$router.push('/offerFile/addOfferFile')
      },
      //关联文件，确定关联
      confirmAssociated(){
        var self = this
        this.$refs.ruleForm.validate(vaild=>{
          if(vaild){
            // console.log('有文件')
            // if(this.associatedFileErrMsg.length > 0){
            //   return
            // }
            this.showLoading(document.getElementById('upload-dialog'))
            this.$http({
              url:'policy/offerSettings/addTpl/' + this.rowData.uuid + '/' + this.uploadFileData.tplType + '/' + this.uploadFileData.operationType,
              method:'post',
              data:this.fileData,
              params:{
                comAccountNumber:this.uploadFileData.comAccountNumber
              }
            }).then(res=>{
              if(res.data.data.length == 0){
                this.$refs.ruleForm.validateField('uploadFileName')
                this.$message.success('关联成功')
                this.associatedFileErrMsg = []
                this.uploadFileData.comAccountNumber = ''
                this.getTableData('change',function(data){
                  this.$nextTick(()=>{
                    data.rows.forEach(item=>{
                      if(item.uuid == self.rowData.uuid){
                        self.rowData = item
                        self.associatedFileList = self.rowData.tpls
                        self.uploadFileData.uploadFileName = ''
                        self.fileTable = self.rowData.tpls.map(item=>{
                          return {
                            id:item.id,
                            comAccountNumber:item.comAccountNumber,
                            tplName:item.tplName,
                            tplType:item.tplType,
                            tplTypeName:item.tplTypeName,
                            operationType: item.operationType,
                            operationTypeStr: item.operationTypeName
                          }
                        })
                      }
                    })
                  })
                })
              }else{
                this.associatedFileErrMsg = res.data.data
              }
              this.closeLoading()
            }).catch(err=>{
              this.closeLoading()
            })
          }
        })
      },
      //关联文件，上传文件
      changeFile(e){
        // console.log(e.target.files)
        var name = e.target.files[0].name.split('.')
        var list = ['xlsx','xls','XLSX','XLS']
        if(list.indexOf(name[1]) <= -1){
          this.uploadFileData.uploadFileName = e.target.files[0].name
          this.associatedFileErrMsg.push('请选择excel格式文件')
          return
        }else{
           this.associatedFileErrMsg = []
        }
        if(e.target.files.length > 0){
          this.uploadFileData.uploadFileName = e.target.files[0].name
          this.fileData = new FormData()
          this.fileData.append('file',e.target.files[0])
          this.$refs.uploadFile.value = ''
          this.$nextTick(()=>{
            this.$refs.ruleForm.validateField('uploadFileName')
          })
        }
      },
      removeFile(item){
        var self = this
        this.$confirm('一旦取消关联，不可逆，请谨慎操作', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          showClose: false,
          customClass: 'pal-confirm',
          type: 'warning'
        }).then(() => {
          console.log('确定删除文件')
          this.$http({
            url:'policy/offerSettings/deleteTpl?tplId=' + item.id,
            method:'DELETE'
          }).then(res=>{
            // this.associatedFileList.splice(index,1)
            this.getTableData('change',function(data){
              this.$nextTick(()=>{
                data.rows.forEach(item=>{
                  if(item.uuid == self.rowData.uuid){
                    self.rowData = item
                    self.associatedFileList = self.rowData.tpls
                    self.uploadFileData.uploadFileName = ''
                    self.fileTable = self.rowData.tpls.map(item=>{
                      return {
                        id:item.id,
                        comAccountNumber:item.comAccountNumber,
                        tplName:item.tplName,
                        tplType:item.tplType,
                        tplTypeName:item.tplTypeName,
                        operationType: item.operationType,
                        operationTypeStr: item.operationTypeName
                      }
                    })
                  }
                })
              })
            })
            this.$message.success('操作成功')
          })
        }).catch(() => {
          console.log('取消删除文件')
        });
      },
      //禁用文件
      disableFile(row){
        this.$confirm('一旦禁用，模板无法重新生效，请谨慎操作。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          showClose: false,
          customClass: 'pal-confirm',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: '/policy/offerSettings/updateStatus',
            method: 'post',
            data: this.$qs.stringify({uuid:row.uuid,status:0}),
          }).then(({data}) => {
            this.getTableData()
          }).catch(() => {

          })
        }).catch(() => {

        });
      },
      //关闭关联文件弹窗
      handleClose(){
        this.dialogVisible = false
        this.$refs.uploadFile.value = ''
        this.uploadFileData.uploadFileName = ''
        this.uploadFileData.tplType = ''
        this.associatedFileErrMsg = []
        this.$refs.ruleForm.clearValidate()
      },
      //开启关联文件弹窗
      associatedFileDialog(row){
        let that = this
        this.dialogVisible = true
        this.rowData = row
        this.associatedFileList = this.rowData.tpls

        this.$nextTick(()=>{
          this.$refs.ruleForm.clearValidate()
        })
        this.fileTable = this.rowData.tpls.map(item=>{
          return {
            id:item.id,
            comAccountNumber:item.comAccountNumber,
            tplName:item.tplName,
            tplType:item.tplType,
            tplTypeName: item.tplTypeName,
            operationType: item.operationType,
            operationTypeStr: item.operationTypeName
          }
        })
        this.getOperationTypeList(row)
        this.uploadFileData.comAccountNumber = ''
        this.getDeclareAccount(row.bussinssType,this.formData.addrId)
        this.getPolicyItems(this.rowData.addrId).then(item=>{
          this.findAddrTplItems(this.rowData.addrId,this.rowData.bussinssType).then(item=>{
            if(this.options[this.rowData.bussinssType].length == 1){
              this.uploadFileData.tplType = this.options[this.rowData.bussinssType][0].tplType
            }
          })
        })
      },
      getOperationTypeList(row) {
        let self = this
        this.$http({
          url: 'policy/offerSettings/getOperationTypes',
          method: 'post',
          params: {
            businessType: row.bussinssType,
            changeType: row.changeType
          }
        }).then(({data}) => {
          self.uploadFileData.operationType = ''
          self.operationTypeList = data.data
          if (self.operationTypeList.length > 0) {
            self.uploadFileData.operationType = self.operationTypeList[0].code
          }
          self.$nextTick(()=>{
            self.$refs.ruleForm.clearValidate()
          })
        })
      },
      getTableData(pageChange,callback) {
        var params = [
          {property: "addrName", value: this.formData.addrName},
          {property: "bussinssType", value: this.formData.bussinssType},
          {property: "changeTypes", value: this.formData.changeTypes},
          {property: "statuses", value: this.formData.statuses},
          {property: "columnRules", value: this.formData.columnRules},
          {property: "customMades", value: this.formData.customMades}
        ]
        sessionStorage.setItem('offerFileSetting-params',JSON.stringify(this.formData))
        var self = this
        this.$refs.table.fetch({
          pageChange: pageChange ? pageChange : '',
          query: params,
          method: 'post',
          url: '/policy/offerSettings/page',
          callback:callback ? callback : function(res){
            setTimeout(() => {
              self.$refs.table.doLayout()
            }, 2000);
          }
        })
      },
      // 获取参保地
      /*getAddr(type) {
        let that = this
        this.$http({
          url: 'policy/declareAddr/addrList',
          method: 'post',
        }).then(({data}) => {
            this.bussinssType.allAddr = data.data
        })
      },*/
      //去报盘信息维护
      handleSocialView(row){
        this.$router.push('/offerFile/offerInfo?uuid=' + row.uuid)
      },
      showLoading(target){
        this.loading = this.$loading({
          target:target? target : document.body,
          lock: true,
          text: '加载中',
          spinner: 'el-icon-loading',
          background: 'rgba(255, 255, 255, 0.7)'
        });
      },
      closeLoading(){
        if(this.loading && this.loading.close){
          this.loading.close()
        }
      },
      downLoadFile(item){
        this.$downloadFile('policy/offerSettings/downloadTpl?tplId=' + item.id, 'post', {}, this.$global.EXCEL)
        // this.$http({
        //   url:'policy/offerSettings/downloadTpl?tplId=' + item.id,
        //   method:'post',
        //   responseType: 'blob',
        //   headers:{
        //     customSuccess:true
        //   }
        // }).then(res=>{
        //   console.log(res)
        //   // if(res.data.code == 200){
        //     // let name = res.headers["content-disposition"].split(';')[1].split('filename=')[1];
        //     var blob = new Blob([res.data]);
        //     var url = window.URL.createObjectURL(blob);
        //     var a = document.createElement("a");
        //     a.href = url;
        //     a.download = decodeURI(name);
        //     a.click();
        //     window.URL.revokeObjectURL(url);
        //   // }else{
        //   //   this.$message.error(res.data.message ? res.data.message :'接口异常，请稍后再试')
        //   // }

        // })
      },
      //获取字典值
      getDictByKey(dataKey,key){
        this.$http({
          url:'policy/sys/dict/getDictByKey',
          method:'get',
          params:{
            dataKey:dataKey,
          },
        }).then(res=>{
          if(dataKey == '10004'){
            if(res && res.data.data){
              res.data.data = res.data.data.map(item=>{
                return {
                  itemCode:item.dictCode,
                  itemName:item.dictName
                }
              })
              this.$set(this.relationData,'2',res.data.data)
            }
          }else{
            this.allOptions.push(...res.data.data)
            this.$set(this.relationData.options,key,res.data.data)
            // this.relationData.options[key] = res.data.data
          }
        // console.log(this.relationData)
        })
      },
      //查询该地区方案所涵盖的险种（去重）
      getPolicyItems(addressId){
        return this.$http({
          url:'policy/product/getPolicyItems/' + addressId,
          method:'post',
        }).then(res=>{
          if(res && res.data.data){
           this.$set(this.relationData,'1',res.data.data)
          }
          // this.relationData[1] = res.data.data
        })
      },
      //报盘文件设置-获取关联标记信息
      findAddrTplItems(addrId,bussinssType){
        this.showLoading()
        return this.$http({
          url:`policy/offerSettings/findAddrTplItems/${addrId}/${bussinssType}`,
          method:'post',
        }).then(({data})=>{
          if(data.data){
            data.data.tplItems.forEach(item=>{
              item.tplType == '10007004' ? item.disabled = true : ''
            })
            this.options[bussinssType] = data.data.tplItems ? data.data.tplItems : []
            this.$set(this.relationData,'tplItems',data.data.tplItems ? data.data.tplItems : this.$options.data().relationData.tplItems)
            this.defaultRelationData = data.data.tplItems ? this.deepCopy(data.data.tplItems) : this.$options.data().relationData.tplItems
          }else{
            this.options[bussinssType] = []
            this.$set(this.relationData,'tplItems',this.$options.data().relationData.tplItems)
            this.defaultRelationData = this.$options.data().relationData.tplItems
          }
          this.closeLoading()
        }).catch(()=>{
          this.$set(this.relationData,'tplItems',this.$options.data().relationData.tplItems)
          this.defaultRelationData = this.$options.data().relationData.tplItems
          this.closeLoading()
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
      //根据类型 获取社保或公积金号，关联文件用
      getDeclareAccount(type,addressId){
        this.$http({
          url: '/policy/declareAccount/seeMainBody',
          method: 'post',
          params: {
            bussinessType: type,
            addressId: addressId,
            custLimit:0
          }
        }).then(({data}) => {
          this.accountNumberList = data.data
        }).catch((data) => {
        })
      },
      goCustomizedList(row,type){
        if(type == 'list'){
          this.$router.push(`/offerFile/customizedList?addrName=${row.addrName}&addrId=${row.addrId}&bussinssType=${row.bussinssType}&changeType=${row.changeType}`)
          return
        }
        this.$router.push('/offerFile/customizedList')
      },
      goValidateRules(uuid){
        this.$router.push('/offerFile/validateRules?uuid=' + uuid)
      },
   },
  }
</script>
<style lang="less" scoped>
/deep/.el-dialog__header{
  padding:10px 20px;
}
/deep/.el-dialog__body{
  padding:10px 10px;
}
/deep/.el-form-item__content{
  line-height: 0px;
}

.file-content-box{
  padding:10px 20px;
  font-size: 12px;
  .file-list{
    display: flex;
    padding: 10px 0;
    border-bottom:1px dashed #ddd;
    .file{
      background: #f1f8ff;
      border-radius: 10px;
      padding:3px 10px;
      position: relative;
      cursor: pointer;
      margin-right:15px;
      font-size: 12px;
      &:hover{
        color: #3e82ff;
        text-decoration: underline;
      }
      .file-remove{
        position: absolute;
        right: -10px;
        top:-10px;
        width: 20px;
        height: 20px;
        background: red;
        border-radius: 50%;
        display: none;
        &::before{
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
      &:hover .file-remove{
        display: block;
      }
    }
  }
  .upload-tips{
    margin-top:20px;
    font-size: 12px;
    color:#797979;
  }
}
.upload-file-box{
  margin-top:10px;
  border-bottom:1px dashed rgb(221, 221, 221);
  padding-bottom: 10px;
  input[type=text]{
    border: 1px solid #ddd;
    height: 28px;
    width: 250px;
    outline: none;
  }
  .upload-file-btn{
    height: 32px;
    background: #3e82ff;
    color: white;
    padding:0 10px;
    box-sizing: border-box;
    font-size: 12px;
    cursor: pointer;
    line-height: 32px;
    margin-left:10px;
  }
  .upload-file{
    padding: 4px 10px;
    height: 32px;
    line-height: 20px;
    position: relative;
    cursor: pointer;
    color: #888;
    background: #fafafa;
    border: 1px solid #ddd;
    display: inline-block;
    box-sizing: border-box;
    input[type=file] {
      position: absolute;
      font-size: 17px;
      right: 0;
      top: 0;
      opacity: 0;
      width: 295px;
      filter: alpha(opacity=0);
      cursor: pointer;
      &:hover {
        color: #444;
        background: #eee;
        border-color: #ccc;
        text-decoration: none
      }
    }
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
.tag:hover, .tag.active {
  background-color: #dddddd;
}
.table-fileName-list{
  cursor: pointer;
  &:hover{
    color:#3e82ff;
    text-decoration:underline;
  }
}
.text-blue{
  &:hover{
    text-decoration: underline;
  }
}
</style>
<style lang="less" scoped>
body .el-table th.gutter {
  display: table-cell !important;
}
.error{
  margin-top:10px;
  /* position: absolute; */
  left: 0;
  /* top: 120%; */
  color:#F56C6C;
  font-size: 12px;
}
/deep/.el-drawer__body{
  padding-bottom: 0;
}
/deep/.el-dialog__header{
  border-bottom: none!important;
}
.file-content-box /deep/.table-header {
  background: #f5f5f5 !important;
  color: #444;
}
.file-content-box /deep/.el-table__cell {
  border-bottom: 1px solid #ddd;
  border-right: 1px solid #ddd;
}
.file-content-box /deep/.el-table__row {
  border-bottom: 1px solid #ddd;
  border-right: 1px solid #ddd;
}
.file-content-box /deep/.el-table--border {
  border: 1px solid #ddd;
}
/deep/.el-table .el-table__cell {
  padding: 0px 0;
}
.dialog-footer{
  display: flex;
  justify-content: center;
}
.placeholderStyle{
  padding-left:10px;
  padding-right:10px;
}
.placeholderStyle::-webkit-input-placeholder {
  font-size: 12px;
	color: #C0C4CC;
}
.cust-dialog /deep/.el-table__body tr.hover-row>td.el-table__cell{
    background-color: rgba(0, 0, 0, 0) !important;
}

/deep/.table-header{
  background-color: #f2f2f2 !important;
  padding: 6px 0;
  border-bottom: 1px solid #e2e2e2 !important;
  border-top: 1px solid #e2e2e2 !important;
   border-right: 1px solid #e2e2e2 !important;
  border-right: transparent;
  text-align: left;
}
/deep/.table-header .cell{
  font-weight: bold;
  color: #303133;
}
.cust-dialog /deep/.el-table .el-table__cell{
  padding: 6px 0;
}

</style>
