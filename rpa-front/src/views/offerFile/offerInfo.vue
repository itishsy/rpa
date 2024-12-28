<template>
  <div class="spl-container">
    <breadcrumb :data="pathData">
      <el-button type="primary" slot="breadcrumb-btn" size="mini" @click="openDialog">确定保存</el-button>
    </breadcrumb>
    <div class="pl-20 pr-20">
      <el-row style="padding-top: 10px">
        <el-col :span="6">
          <div class="search-row display-flex">
            <span class="ml-20 label text-right" style="white-space:nowrap;">参保城市：</span>
            <span class="label">{{ formData.addrName }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="search-row display-flex" style="align-items: center">
            <span class="ml-20 label text-right">业务类型：</span>
            <span class="label">{{bussinssType(formData.bussinssType)}}</span>
          </div>
        </el-col>
        <el-col :span="5">
          <div class="search-row display-flex" style="align-items: center">
            <span class="ml-20 label">申报类型：</span>
            <span class="label">
              {{
              changeType(formData.changeType)
              }}
            </span>
          </div>
        </el-col>
        <el-col :span="6">
          <div
            class="search-row display-flex"
            style="align-items: center"
            v-if="formData.changeType == 5"
          >
            <span class="ml-20 label text-right" style="white-space:nowrap;">补缴月份显示：</span>
            <el-select placeholder="请选择" v-model="formData.fyRuleType">
              <el-option label="合并连续月显示" :value="1"></el-option>
              <el-option label="月份单条显示" :value="2"></el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
      <el-row style="padding-top: 10px">
        <el-col :span="6" v-if="formData.bussinssType=='2'">
          <div class="search-row display-flex" style="align-items: center">
            <span class="ml-20 label text-right" style="white-space:nowrap;">数据显示：</span>
            <el-checkbox v-model="formData.showType" :true-label="1" :false-label="0">按险种分行显示</el-checkbox>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="search-row display-flex">
            <span class="ml-20 label text-right" style="white-space:nowrap;">申报注意事项：</span>
            <el-input
              type="textarea"
              :rows="3"
              :placeholder="`阐述申报过程注意事项，供用户录入参保时参考\n如：时间如何填值？`"
              v-model="formData.declareItem"
              style="width:100%;"
            ></el-input>
          </div>
        </el-col>
        <el-col :span="5" v-if="checkAddress">
          <div class="search-row display-flex ml-20">
            <el-checkbox v-model="formData.autoParse" :true-label="1" :false-label="0">自动解析户籍省市区</el-checkbox>
          </div>
        </el-col>
        <el-col :span="6">
          <div
            class="search-row display-flex"
            style="align-items: center"
            v-if="checkAddress && formData.autoParse == 1"
          >
            <span class="ml-20 label text-right" style="white-space:nowrap;">行政区划返回格式：</span>
            <el-select placeholder="请选择" v-model="formData.divisionsFormat" clearable>
              <el-option
                :label="item.label"
                :value="item.value"
                v-for="(item,index) in divisionsFormatArr"
                :key="index"
              ></el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
    </div>
    <div class="table-box">
      <el-button
        type="primary"
        slot="breadcrumb-btn"
        size="mini"
        @click="add({}, 0)"
        style="margin-bottom: 10px; float: right"
        v-if="table.tableData.length <= 0"
      >添加字段</el-button>
      <el-button
        type="primary"
        slot="breadcrumb-btn"
        size="mini"
        @click="autoSort"
        style="margin-bottom: 10px; float: right"
        v-if="table.tableData.length > 0"
      >自动排序</el-button>
      <el-form ref="tableForm" :model="table" label-width="0px" :rules="rules">
        <el-table
          :data="table.tableData"
          border
          header-cell-class-name="table-header"
          style="width: 100%;"
          :max-height="tableHeight"
          ref="table"
        >
          <el-table-column fixed="left" label="序号" width="55" align="center" prop="date">
            <template scope="scope">
              <span>{{ scope.$index + 1 }}</span>
            </template>
          </el-table-column>
          <el-table-column fixed="left" prop="date" label="字段名称" width="150">
            <template scope="scope">
              <span v-if="scope.row.rules && scope.row.rules.length>0" class="check-sign-blue">校</span>
              <span v-else-if="scope.row.conditionField" class="check-sign-blue check-sign-gray">校</span>
              <div @dblclick="editOutputName(scope.row)" style="position: relative;padding-left: 10px;">
                <span v-if="!scope.row.newly" style="cursor:pointer;">
                  <span v-if="!scope.row.comment">{{scope.row.declareColumnName}}</span>
                  <el-popover
                    placement="top-start"
                    title
                    width="200"
                    trigger="hover"
                    :content="scope.row.comment"
                    v-else
                  >
                    <span slot="reference">{{scope.row.declareColumnName}}</span>
                  </el-popover>
                </span>
                <el-form-item
                  label
                  v-else
                  :prop="'tableData.' + scope.$index + '.declareColumnName'"
                  :rules="rules.declareColumnName"
                >
                  <span>
                    <el-input
                      v-model.trim="scope.row.declareColumnName"
                      placeholder="请输入字段名称"
                      @blur="editOutputNameConfirm(scope.row)"
                      size="small"
                    ></el-input>
                  </span>
                </el-form-item>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="数据源" width="280">
            <template scope="scope">
              <div style="display: flex; align-items: center">
                <el-form-item
                  label
                  :prop="'tableData.' + scope.$index + '.dataSourceTableCode'"
                  :rules="rules.dataSourceTableCode"
                  style=" width: 50%"
                >
                  <div>
                    <el-select
                      v-model="scope.row.dataSourceTableCode"
                      placeholder="请选择"
                      clearable
                      filterable
                      size="mini"
                      @change="dataSourceTableCodeChange($event, scope.$index)"
                    >
                      <el-option
                        v-for="item in dataSourceOption"
                        :key="item.id"
                        :label="item.name"
                        :value="item.code"
                      ></el-option>
                    </el-select>
                  </div>
                </el-form-item>
                <el-form-item
                  label
                  :prop="'tableData.' + scope.$index + '.dataSourceItemCode'"
                  :rules="rules.dataSourceItemCode"
                  style=" width: 50%"
                  v-if="
                    scope.row.dataSourceTableCode != '' &&
                    scope.row.dataSourceTableCode != null &&
                    scope.row.dataSourceTableCode != '60000'
                  "
                >
                  <div style="margin-left: 10px">
                    <el-select
                      v-model="scope.row.dataSourceItemCode"
                      clearable
                      filterable
                      placeholder="请选择"
                      size="mini"
                      @change="dataSourceItemCodeChange($event, scope.row, scope.$index)"
                    >
                      <el-option
                        v-for="item in scope.row.dataSourceOption2"
                        :key="item.id"
                        :label="item.name"
                        :value="item.code"
                      ></el-option>
                    </el-select>
                  </div>
                </el-form-item>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="address" label="字段属性" width="280">
            <template scope="scope">
              <div style="display: flex; align-items: center">
                <el-form-item
                  label
                  :prop="'tableData.' + scope.$index + '.inputType'"
                  :rules="rules.inputType"
                  style="width: 50%"
                >
                  <div>
                    <el-select
                      v-model="scope.row.inputType"
                      placeholder="请选择"
                      size="mini"
                      @change="changeInputType($event, scope.$index)"
                      :disabled="scope.row.disabledSelect"
                    >
                      <el-option
                        v-for="item in filterValueTypeOption1(scope.row.dataSourceItemCode)"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      ></el-option>
                    </el-select>
                  </div>
                </el-form-item>
                <el-form-item
                  label
                  :prop="'tableData.' + scope.$index + '.exportFormat'"
                  :rules="rules.inputType"
                  style="width: 50%"
                  v-if="
                    scope.row.inputType &&
                    scope.row.inputType != 1 &&
                    scope.row.inputType != 2 || (['40000007','40000008','40000009','40000010'].indexOf(scope.row.dataSourceItemCode) > -1 && scope.row.inputType == 2)
                  "
                >
                  <div style="margin-left: 10px">
                    <el-select v-model="scope.row.exportFormat" placeholder="请选择" size="mini">
                      <el-option
                        v-for="item in scope.row.valueTypeOption2"
                        :key="item.dictName"
                        :label="item.dictName"
                        :value="item.dictCode"
                      ></el-option>
                    </el-select>
                  </div>
                </el-form-item>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="address" label="其他属性" width="180">
            <template scope="scope">
              <div style="display: flex; align-items: center">
                <el-form-item
                  label
                  :prop="'tableData.' + scope.$index + '.  otherBelongTo2'"
                  style="width: 100%"
                >
                  <div>
                    <el-select
                      v-model="scope.row.otherBelongTo2"
                      placeholder="请选择"
                      size="mini"
                      multiple
                    >
                      <el-option
                        v-for="item in valueTypeOption2Item['other']"
                        :key="item.dictCode"
                        :label="item.dictName"
                        :value="item.dictCode"
                      ></el-option>
                    </el-select>
                  </div>
                </el-form-item>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="输出值" min-width="150">
            <template scope="scope">
              <el-tooltip
                class="item"
                effect="light"
                placement="top"
                :disabled="getColumnScopes(scope.row.columnScopes).length <= 0"
              >
                <div slot="content">
                  <div
                    v-for="(
                      item, index
                    ) in getColumnScopes(scope.row.columnScopes).slice(0,6)"
                    :key="index"
                  >
                    <div class="output-content" style="text-align: left;">{{ item.scopeValue }}</div>
                  </div>
                  <div
                    v-if="scope.row.columnScopes != null && getColumnScopes(scope.row.columnScopes).length > 6"
                  >
                    <div class="output-content" style="text-align: left;">...</div>
                  </div>
                </div>
                <div>
                  <div v-if="scope.row.columnScopes != null && scope.row.columnScopes.length >= 1">
                    <div
                      class="output-content"
                      v-for="(item, index) in getColumnScopes(scope.row.columnScopes).slice(0,2)"
                      :key="index"
                    >{{ item.scopeValue }}</div>
                  </div>
                  <div
                    v-else
                    style="text-align: center"
                  >{{ scope.row.defaultValue ? scope.row.defaultValue : '' }}</div>
                  <div
                    style="display: flex"
                    v-if=" scope.row.columnScopes != null && getColumnScopes(scope.row.columnScopes).length > 2"
                  >
                    <div class="output-content">...</div>
                  </div>
                </div>
              </el-tooltip>
              <i
                class="el-icon-edit edit-icon"
                style="color:#3E82FF"
                @click="showDrawer(scope.$index)"
              ></i>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="校验" min-width="150">
            <template scope="scope">
              <el-tooltip
                class="item"
                effect="light"
                placement="top"
                :disabled="!scope.row.rules || scope.row.rules.length <= 0"
              >
                <div slot="content">
                  <div v-for="(item, index) in scope.row.rules" :key="index">
                    <div class="output-content" style="text-align: left;">校验{{index+1}}：{{ item.comment }}</div>
                  </div>
                </div>
                <div>
                  <div v-if="scope.row.rules != null && scope.row.rules.length >= 1">
                    <div class="output-content" v-for="(item, index) in scope.row.rules.slice(0,2)" :key="index">校验{{index+1}}：{{ item.comment }}</div>
                  </div>
                  <div style="display: flex" v-if=" scope.row.rules != null && scope.row.rules.length > 2" >
                    <div class="output-content">...</div>
                  </div>
                </div>
              </el-tooltip>
<!--              <span
                :title="scope.row.columnCondition ? scope.row.columnCondition.conclusion : ''"
                class="ellipsis"
              >{{scope.row.columnCondition ? scope.row.columnCondition.conclusion : ''}}</span>
              <i
                class="el-icon-edit edit-icon"
                style="color:#3E82FF"
                @click="showConditionDrawer(scope.$index)"
              ></i>-->
            </template>
          </el-table-column>
          <!-- <el-table-column prop="comment" label="注解"> </el-table-column> -->
          <el-table-column prop="comment" label="是否必填">
            <template scope="scope">
              <div style="display: flex">
                <el-checkbox
                  v-model="scope.row.required"
                  :disabled="scope.row.disabled"
                  :true-label="2"
                  :false-label="1"
                >{{ scope.row.required == 2 ? '必填' : '选填' }}</el-checkbox>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="comment" label="非首次收集" width="120">
            <template scope="scope">
              <div style="display: flex">
                <el-checkbox
                  v-model="scope.row.notFirstTime"
                  :true-label="1"
                  :false-label="0"
                  @change="changeFirstKey($event,scope)"
                >{{ scope.row.notFirstTime == 1 ? '非首次' : '首次' }}</el-checkbox>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="comment" label="是否显示">
            <template scope="scope">
              <div style="display: flex">
                <el-checkbox
                  v-model="scope.row.isShow"
                  :disabled="scope.row.disabled || handleIsShowDisabled(scope.row)"
                  :true-label="1"
                  :false-label="0"
                >{{ scope.row.isShow === 1 ? '显示' : '隐藏' }}</el-checkbox>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="comment" label="注解" width="150" :show-overflow-tooltip="true">
          </el-table-column>
          <el-table-column prop="address" label="操作" width="200">
            <template scope="scope">
              <div style="display: flex">
                <div
                  class="control-btn"
                  style="color: red"
                  @click="remove(scope.row, scope.$index)"
                >移除</div>
                <el-popover placement="top" :ref="`sort-${scope.$index}`" trigger="manual">
                  <el-row type="flex">
                    <el-input v-model="newIndex" class="w-120" placeholder="输入新序号" v-positiveNumber clearable></el-input>
                    <el-button size="mini" plain class="ml-10"
                               @click.stop="cancelPopover(scope.$index)">取消</el-button>
                    <el-button type="primary" size="mini"
                               @click.stop="confirmPopover(scope.$index)">确定</el-button>
                  </el-row>
                  <div slot="reference" class="control-btn" style="color: #6b6b6b" @click="handleShowOrder(scope.$index)">排序</div>
                </el-popover>
<!--                <div
                  class="control-btn"
                  style="color: #6b6b6b"
                  @click="moveDown(scope.row, scope.$index)"
                  v-if="scope.$index != table.tableData.length - 1"
                >下移</div>
                <div
                  class="control-btn"
                  style="color: #6b6b6b"
                  @click="moveUp(scope.row, scope.$index)"
                  v-if="scope.$index != 0"
                >上挪</div>-->
                <div
                  class="control-btn"
                  style="color: #3e82ff"
                  @click="add(scope.row, scope.$index)"
                >添加</div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
    </div>
    <!-- 设置输出值 -->
    <el-drawer
      title="设置输出值"
      :visible.sync="openDrawer"
      size="50%"
      :with-header="true"
      :wrapperClosable="false"
      custom-class="spl-filter-drawer my-drawer"
    >
      <div style="padding: 20px;padding-bottom:100px;">
        <el-form ref="outputForm" :model="outputForm" label-width="80px" :rules="rules">
          <div ref="outPutFormTop">
            <div style="display: flex; justify-content: space-between">
              <el-form-item prop="declareColumnName" label="字段名称">
                <el-input v-model.trim="outputForm.declareColumnName" placeholder="请输入字段名称" :disabled="isDeclareColumnNameDisabled"></el-input>
              </el-form-item>
              <el-form-item prop="comment" label="注解" style="width: 50%">
                <el-input v-model="outputForm.comment" placeholder="请输入注解"></el-input>
              </el-form-item>
            </div>
            <div style="display:flex;">
              <p class="table-title">
                <span>输出方式</span>
                <span
                  style="color: #949494"
                  v-if="outputForm.inputType == '1' || outputForm.inputType == '2'"
                >（①和②二选一，非必需）</span>
              </p>
            </div>
            <!-- 默认值 -->
            <div style="margin-top: 20px">
              ① 默认值
              <el-form-item prop="defaultValue" label label-width="0">
                <el-input v-model="outputForm.defaultValue" placeholder="请输入值"></el-input>
              </el-form-item>
            </div>
          </div>
          <!-- 下拉值 -->
          <div style="margin-top: 20px">
            <div style="margin-top: 20px" v-if="outputForm.inputType == '2'">
              <div style="display: flex; justify-content: space-between">
                <span>
                  ② 下拉值
                  <span style="color:#666;font-size:12px;">（参保时的下拉值）</span>
                </span>
                <div>
                  <span class="gray-btn" style="margin-left: 10px" @click="batchAdd">批量</span>
                  <span class="gray-btn" style="margin-left: 10px" @click="addSelectTable">添加</span>
                  <span
                    class="gray-btn"
                    style="margin-left: 10px"
                    @click="upTable('selectTable')"
                    v-if="this.selectTableH == '100%'"
                  >
                    <i class="el-icon-arrow-down"></i>
                  </span>
                  <span
                    class="gray-btn"
                    style="margin-left: 10px"
                    @click="dowmTable('selectTable')"
                    v-else
                  >
                    <i class="el-icon-arrow-up"></i>
                  </span>
                </div>
              </div>
              <div :style="{'margin-top': '20px','overflow': 'hidden',height:selectTableH}">
                <el-table
                  :data="outputForm.selectTable"
                  border
                  header-cell-class-name="table-header"
                  style="width: 100%"
                  :max-height="tableHeight1"
                  ref="outputFormTable1"
                >
                  <el-table-column fixed="left" label="序号" align="center" width="50" prop="num">
                    <template scope="scope">
                      <span>{{scope.$index + 1}}</span>
                    </template>
                  </el-table-column>
                  <el-table-column fixed="left" label="显示值" align="center" prop="date">
                    <template slot="header" slot-scope="scope">
                      <div>
                        <p>
                          <span style="color:red;">*</span> 选项
                        </p>
                        <div class="icon-query">
                          <i class="el-icon-zoom-in" @click="openQueryImport(scope.row)"></i>
                        </div>
                      </div>
                    </template>
                    <template scope="scope">
                      <el-form-item
                        class="mb-0"
                        :prop="'selectTable.' + scope.$index + '.scopeValue'"
                        label
                        label-width="0"
                        :rules="rules.scopeValue"
                      >
                        <el-input
                          v-model="scope.row.scopeValue"
                          placeholder="请输入显示值"
                          :readonly="scope.row.readonly"
                          :title="scope.row.scopeValue"
                          :disabled="scope.row.readonly"
                          size="mini"
                          @blur="changeOriginalText"
                        ></el-input>
                      </el-form-item>
                    </template>
                  </el-table-column>
                  <el-table-column fixed="left" label="是否显示" width="105" align="center" prop="date">
                    <template slot="header" slot-scope="scope">
                      <div>
                        <span>是否显示</span>
                        <el-checkbox
                          v-model="outputForm.checkedAll"
                          :true-label="1"
                          :false-label="0"
                          @change="changeCheckAll"
                          style="margin-left:10px;"
                        ></el-checkbox>
                      </div>
                    </template>
                    <template scope="scope">
                      <div>
                        <el-checkbox v-model="scope.row.isShow" :true-label="1" :false-label="0"></el-checkbox>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </div>
          <!-- 值映射 -->
          <div style="margin-top: 20px">
            <div style="display: flex; justify-content: space-between">
              <p class="table-title">
                <span>值映射</span>
                <span style="font-size:12px;color:#666">（影响报盘导出）</span>
              </p>
              <div>
                <span class="gray-btn" style="margin-right:10px;" @click="outputList">导出</span>
                <span class="gray-btn" @click="improtMapping">导入映射</span>
                <span class="gray-btn" style="margin-left: 10px" @click="addOutput">添加</span>
                <span
                  class="gray-btn"
                  style="margin-left: 10px"
                  @click="upTable('outputTable')"
                  v-if="this.outputTableH == '100%'"
                  title="收起"
                >
                  <i class="el-icon-arrow-down"></i>
                </span>
                <span
                  class="gray-btn"
                  style="margin-left: 10px"
                  @click="dowmTable('outputTable')"
                  v-else
                >
                  <i class="el-icon-arrow-up" title="展开"></i>
                </span>
              </div>
            </div>
            <div :style="{'margin-top': '20px','overflow': 'hidden',height:outputTableH}">
              <el-table
                :data="outputForm.outputTable"
                border
                header-cell-class-name="table-header"
                style="width: 100%"
                :max-height="tableHeight2"
                ref="outputFormTable2"
              >
                <el-table-column fixed="left" label="序号" align="center" width="50" prop="num">
                  <template scope="scope">
                    <span>{{scope.$index + 1}}</span>
                  </template>
                </el-table-column>
                <el-table-column fixed="left" label="获取值" width="200" align="center" prop="date">
                  <template slot="header">
                    <div>
                      <p>
                        <span style="color:red;">*</span>获取值
                      </p>
                    </div>
                  </template>
                  <template scope="scope">
                    <el-form-item
                      class="mb-0"
                      style="margin-bottom:12px;"
                      :prop="'outputTable.' + scope.$index + '.originalText'"
                      label
                      label-width="0"
                      :rules="rules.originalText"
                    >
                      <el-input
                        v-model="scope.row.originalText"
                        placeholder="请输入获取值"
                        :readonly="scope.row.readonly"
                        :title="scope.row.originalText"
                        :disabled="scope.row.readonly"
                        size="mini"
                        @blur="changeOriginalText"
                      ></el-input>
                    </el-form-item>
                  </template>
                </el-table-column>
                <el-table-column fixed="left" label="导出值" align="center" prop="date">
                  <template scope="scope">
                    <el-form-item
                      class="mb-0"
                      :prop="'outputTable.' + scope.$index + '.showText'"
                      label
                      label-width="0"
                    >
                      <el-input v-model="scope.row.showText" placeholder="请输入导出值"></el-input>
                    </el-form-item>
                  </template>
                </el-table-column>
                <!-- <el-table-column
                  fixed="left"
                  label="是否显示"
                  width="55"
                  align="center"
                  prop="date"
                >
                  <template scope="scope">
                    <div>
                      <el-checkbox
                        v-model="scope.row.isShow"
                        :true-label="1"
                        :false-label="0"
                      ></el-checkbox>
                    </div>
                  </template>
                </el-table-column>-->
              </el-table>
            </div>
          </div>
        </el-form>
      </div>
      <div class="footer-btn drawer-footer-buts">
        <el-button type="primary" style="padding: 10px 30px" @click="outputDataConfig">确定</el-button>
      </div>
    </el-drawer>
    <!-- 导入映射 -->
    <el-dialog title="导入映射" :visible.sync="openImportMap" width="400px" :before-close="handleClose">
      <!-- <div slot="title">
          <span style="display: flex;align-items: center;"><span class="block"></span><span>导入映射</span></span>
      </div>-->
      <div id="upload">
        <div style="text-align: center">
          <p style="text-align: left; margin-left: 37px; margin-bottom: 5px">表格参考：</p>
          <img src="../../assets/images/u2132.png" alt style="width: 280px; height: 100px" />
        </div>
        <div style="display: block; margin: 0 auto" class="upload-file-box">
          <el-form
            :model="importData"
            :rules="rules"
            ref="importFieldUpload"
            label-width="0px"
            class="demo-ruleForm"
          >
            <div style="text-align: center; margin-top: 15px">
              <el-form-item label prop="uploadFileName" style="width: 285px; margin: 0 auto">
                <input
                  type="text"
                  style="height: 36px"
                  placeholder="请选择文件"
                  @click="importMapUpload($event)"
                  ref="upload"
                  readonly
                  v-model="importData.uploadFileName"
                />
              </el-form-item>
              <div class="upload-file">
                <input type="file" @change="importChangeFile" ref="importFile" />
              </div>
              <div
                style="
                  color: #f56c6c;
                  font-size: 12px;
                  text-align: left;
                  margin-left: 40px;
                "
              >{{ uploadErrMsg }}</div>
            </div>
          </el-form>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="importOnClose" size="small" class="w-60">取 消</el-button>
        <el-button type="primary" @click="importFile" size="small" class="w-60">导入</el-button>
      </span>
    </el-dialog>
    <!-- 快速显示值，系统常量 -->
    <el-dialog
      title="快速获取"
      :visible.sync="openQueryMap"
      width="400px"
      :before-close="importQueryOnClose"
    >
      <!-- <div slot="title">
        <span style="display: flex;align-items: center;"><span class="block"></span><span>快速获取</span></span>
      </div>-->
      <div style="text-align: center">
        <p style="text-align: left">从系统字段引入：</p>
      </div>
      <el-form
        :model="importData"
        :rules="rules"
        ref="importQueryField"
        label-width="0px"
        class="demo-ruleForm"
      >
        <div style="display: flex">
          <el-form-item label prop="importQueryField" style="width: 100%">
            <el-select
              placeholder="请选择"
              style="width: 100%"
              v-model="importData.importQueryField"
              @change="importQueryFieldChange"
              value-key="code"
            >
              <el-option
                v-for="(item, index) in importData.systemList"
                :key="index"
                :label="item.name"
                :value="item"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="importQueryOnClose" size="small" class="w-60">取 消</el-button>
        <el-button type="primary" @click="importQuery" size="small" class="w-60">引入</el-button>
      </span>
    </el-dialog>
    <!-- 添加条件 -->
    <el-drawer
      title="添加条件"
      :visible.sync="conditionDrawer.show"
      :wrapperClosable="false"
      custom-class="spl-filter-drawer condition-drawer"
      :before-close="cancelConditionDrawer"
    >
      <div class="formDrawer-content">
        <el-form :model="conditionDrawer" ref="conditionDrawer" label-width="0">
          <span class="item-lab required-pre">关系：</span>
          <el-form-item
            label
            class="inlineBlock w-150"
            prop="relation"
            :rules="[{ required: true, message: '请选择关系',trigger: ['change']}]"
          >
            <el-select placeholder="请选择" v-model="conditionDrawer.relation">
              <el-option label="或者" :value="2"></el-option>
              <el-option label="并且" :value="1"></el-option>
            </el-select>
          </el-form-item>
          <div
            class="display-flex"
            v-for="(item, index) in conditionDrawer.conditionItemList"
            :key="index"
          >
            <div class="w-200 display-flex">
              <span class="required-pre" style="line-height: 40px;">条件：</span>
              <el-form-item
                class="flex1"
                label
                :prop="'conditionItemList.'+index+'.declareColumnName'"
                :rules="[{ required: true, message: '请选择',trigger: 'change'},{ required: true, validator: checkRepeatCondition, trigger: 'change' }]"
              >
                <el-select
                  placeholder="请选择"
                  v-model="item.declareColumnName"
                  clearable
                  filterable
                  @change="changeDeclareColumnName"
                >
                  <el-option
                    :label="item.declareColumnName"
                    :value="item.declareColumnName"
                    v-for="item in conditionDrawer.conditionColumnNameOption"
                    :key="item.declareColumnName"
                  ></el-option>
                </el-select>
              </el-form-item>
            </div>
            <div class="w-100 ml-10">
              <el-form-item
                label
                :prop="'conditionItemList.'+index+'.contrastMode'"
                :rules="[{ required: true, message: '请选择',trigger: ['change']}]"
              >
                <el-select placeholder="请选择" v-model="item.contrastMode" clearable filterable>
                  <el-option label="等于" :value="1"></el-option>
                  <el-option label="不等于" :value="2"></el-option>
                  <el-option label="包含" :value="3"></el-option>
                  <el-option label="不包含" :value="4"></el-option>
                </el-select>
              </el-form-item>
            </div>

            <div class="flex1 ml-10">
              <el-form-item
                label
                :prop="'conditionItemList.'+index+'.contrastValue'"
                :rules="[{ required: true, validator: checkRepeatContrastValue, trigger: 'change' },{ required: true, message: '请输入',trigger: 'change'}]"
              >
                <el-input
                  v-model.trim="item.contrastValue"
                  placeholder="请输入"
                  clearable
                  @change="changeContrastValue"
                ></el-input>
              </el-form-item>
            </div>

            <span style="padding-top: 12px;width: 65px">
              <i
                class="el-icon-close f-cursor font-20 text-red ml-10 fw-blod"
                v-if="conditionDrawer.conditionItemList.length>1"
                @click="delCondition(index)"
              ></i>
              <i
                class="el-icon-plus f-cursor font-20 text-green ml-10 fw-blod"
                v-if="index === conditionDrawer.conditionItemList.length-1"
                @click="addCondition()"
              ></i>
            </span>
          </div>

          <div class="type-title">
            <span class="text">结果</span>
          </div>
          <div style="padding:10px 10px;">字段名称：{{conditionDrawer.declareColumnName}}</div>
          <div style="padding: 20px 10px;">
            <table class="cust-table w-p100">
              <thead>
                <tr>
                  <th style="width: 30%">
                    <span style="color:red">*</span> 显隐性
                  </th>
                  <th>
                    <span style="color:red;">*</span> 必填性
                  </th>
                  <th>
                    赋值
                    <el-tooltip
                      class="item"
                      effect="dark"
                      content="输入赋值，说明满足条件后，此字段默认此值，不可改"
                      placement="top"
                    >
                      <i class="el-icon-question" style="color:#3E82FF"></i>
                    </el-tooltip>
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>
                    <div style="display:flex;">
                      <el-radio
                        v-model="conditionDrawer.isShow"
                        :label="1"
                        style="margin-right: 25px;"
                      >显示</el-radio>
                      <!-- <el-radio
                        v-model="conditionDrawer.isShow"
                        :label="0"
                        style="margin-right: 25px;"
                      >隐藏</el-radio>-->
                    </div>
                  </td>
                  <td>
                    <div style="display:flex;">
                      <el-radio
                        v-model="conditionDrawer.required"
                        :label="2"
                        style="margin-right: 25px;"
                      >必填</el-radio>
                      <el-radio
                        v-model="conditionDrawer.required"
                        :label="1"
                        style="margin-right: 25px;"
                      >选填</el-radio>
                    </div>
                  </td>
                  <td>
                    <el-input
                      v-model.trim="conditionDrawer.conditionValue"
                      placeholder="请输入"
                      clearable
                    ></el-input>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="type-title">
            <span class="text">结论</span>
          </div>
          <div style="padding:10px;">{{this.conditionDrawer.conclusion}}</div>
        </el-form>

        <div class="msg-tip">
          <p>出现错误：</p>
          <p>1、条件[登录方式]的条件值出现重复，请先更正</p>
        </div>
      </div>

      <div class="drawer-footer-buts">
        <el-button class="btn--border-blue s-btn" @click="removeCondition">去除条件</el-button>
        <el-button class="btn--border-blue s-btn" @click="cancelConditionDrawer">取消</el-button>
        <el-button type="primary" class="s-btn ml-12" @click="confirmConditionDrawer">确定</el-button>
      </div>
    </el-drawer>
    <!-- 发布 -->
    <el-dialog
      title="提示"
      :visible.sync="dialogVisible"
      width="600px"
      :before-close="cancelNewVersion"
      :close-on-click-modal="false"
      class="my-dialog"
    >
      <div id="upload-dialog">
        <div class="file-content-box">
          <el-form
            :model="newVersion"
            :rules="rules"
            ref="newVersionRuleForm"
            label-width="100px"
            class="demo-ruleForm"
          >
            <div class="upload-file-box">
              <div style="margin-bottom:22px;">
                <span style="font-size:14px;">新版本号：</span>
                <span
                  style="font-size:14px;"
                >{{newVersion.versions ? newVersion.versions : newVersion.oldVersions}}</span>
              </div>
              <div style="margin-bottom:22px;">
                <el-form-item
                  prop="changeReason"
                  label-width="150"
                  style="margin-bottom:0;"
                  label="变更原因："
                >
                  <el-select v-model="newVersion.changeReason" clearable style="width:100%">
                    <el-option v-for="(item, index) in changeReasonArr" :label="item" :value="item"></el-option>
                  </el-select>
                </el-form-item>
              </div>
              <div>
                <el-form-item
                  prop="explain"
                  label-width="150"
                  style="margin-bottom:0;"
                  label="改动内容描述："
                >
                  <el-input
                    type="textarea"
                    :rows="8"
                    placeholder="请仿照下方格式，简要描述改动内容。例子如下：
性别，增加收集性别字段
婚姻状况，变更字段下拉值"
                    style="width:100%"
                    v-model="newVersion.explain"
                  ></el-input>
                </el-form-item>
                <div style="text-align:right;">{{newVersion.explain.length}} / 500</div>
              </div>
            </div>
          </el-form>
          <div class="footer-btn-box">
            <el-button @click="cancelNewVersion" class="footer-btn1">取 消</el-button>
            <el-button type="primary" @click="saveAll" class="ml-20 footer-btn1">确定发布</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
    <!-- 批量添加 -->
    <el-dialog
      title="批量添加"
      :visible.sync="batch.show"
      width="600px"
      :before-close="cancelBatch"
      :close-on-click-modal="false"
      class="my-dialog"
    >
      <div id="upload-dialog">
        <div class="file-content-box">
          <el-form
            :model="batch"
            :rules="rules"
            ref="batch"
            label-width="100px"
            class="demo-ruleForm"
          >
            <div class="upload-file-box">
              <div>
                <el-form-item label-width="0" style="margin-bottom:0;" label>
                  <div>
                    <el-input
                      v-model="batch.text"
                      type="textarea"
                      rows="7"
                      resize="none"
                      placeholder="一行一个选项值"
                    ></el-input>
                    <p class="text-right mr-5 mt-10">已输入 {{ batch.num }} 个</p>
                  </div>
                </el-form-item>
              </div>
            </div>
          </el-form>
          <div class="footer-btn-box">
            <el-button @click="cancelBatch" class="footer-btn1">取 消</el-button>
            <el-button type="primary" @click="batchConfrim" class="ml-20 footer-btn1">添加</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  data() {
    return {
      input: '',
      openDrawer: false,
      pathData: [],
      table: {
        tableData: [
          // {
          //   comment:'', //注解
          //   dataSourceItemCode:'',
          //   dataSourceTableCode:'',
          //   declareColumnName:"测试",  //字段名称
          //   defaultValue:"",
          //   exportFormat:'',  //字段属性，
          //   inputType:'1', //1:文本，2：数值，3：日期,
          //   newly:false,
          //   columnConstantSettings:[],
          //   dataSourceOption2: [], //源字段属性
          //   valueTypeOption2:[]  //字段属性，
          // },
        ],
      },
      dataSourceOption: [],
      dataSourceOptionItem: {},
      valueTypeOption1: [
        //字段属性下拉
        {
          value: 1,
          label: '文本',
        },
        {
          value: 2,
          label: '下拉',
        },
        {
          value: 3,
          label: '数值',
        },
        {
          value: 4,
          label: '年月日',
        },
        {
          value: 5,
          label: '年月',
        },
      ],
      //字段属性具体下拉
      valueTypeOption2Item: {},
      rules: {
        declareColumnName: [
          { required: true, message: '请输入字段名称', trigger: 'change' },
          {
            required: true,
            trigger: 'change',
            validator: this.checkDeclareColumnNameRepeat,
          },
        ],
        importQueryField: [{ required: true, message: '请选择引入字段', trigger: 'change' }],
        uploadFileName: [{ required: true, message: '请选择映射文件', trigger: 'change' }],
        originalText: [
          {
            required: true,
            message: '请输入显示值',
            trigger: 'change',
            validator: this.checkoriginalText,
          },
          {
            required: true,
            message: '获取值重复，请修改',
            trigger: 'change',
            validator: this.checkValueRepeat,
          },
        ],
        scopeValue: [
          {
            required: true,
            trigger: 'change',
            validator: this.checkScopeValueText,
          },
          {
            required: true,
            trigger: 'change',
            validator: this.checkScopeValueSouce,
          },
        ],
        inputType: [{ required: true, message: '请设置字段属性', trigger: 'change' }],
        dataSourceItemCode: [
          { required: true, message: '请设置源字段', trigger: 'change' },
          { validator: this.checkDataSourceTableCode, required: true, trigger: 'change' },
        ],
        dataSourceTableCode: [{ required: true, message: '请设置源字段', trigger: 'change' }],
        changeReason: [{ required: true, message: '请选择变更原因', trigger: 'change' }],
        explain: [{ required: true, message: '请输入改动内容描述', trigger: 'change' }],
        defaultValue: [{ validator: this.defaultValueCheck, trigger: 'blur' }],
      },
      formData: {
        addrName: '广州',
        bussinssType: '1',
        changeType: '1',
        fyRuleType: '',
        showType: 0,
        declareItem: '',
        autoParse: 0,
      },
      outputForm: {
        declareColumnName: '',
        comment: '',
        defaultValue: '',
        outputTable: [],
        selectTable: [],
      },
      openImportMap: false,
      openQueryMap: false,
      //快速获取，系统常量
      importData: {
        importQueryField: '',
        systemList: [],
        importQueryFieldList: [],
        uploadFileName: '',
        fileData: {
          uploadFileName: '',
          fileID: '',
          fileData: null,
        },
        importMapList: [],
      },
      tableIndex: 0,
      loading: null,
      uploadErrMsg: '',
      conditionDrawer: {
        //条件
        show: false,
        relation: '',
        conditionItemList: [{ declareColumnName: '', contrastMode: '', contrastValue: '' }],
        conditionColumnNameOption: [], //列表字段下拉，用于条件
        conditionValue: '',
        isShow: 1,
        required: 1,
        declareColumnName: '',
        declareColumnUuid: '',
        conclusion: '',
      },
      selectTableH: '100%', //输出值，下拉值表格高度
      outputTableH: '100%', //输出值，值映射表格高度
      dialogVisible: false,
      changeReasonArr: [
        '报盘字段优化调整',
        '网站填报规则变化',
        '申报系统变更'
      ],
      newVersion: {
        versions: '',
        changeReason: '',
        oldVersions: '',
        explain: '',
        id: '',
        policyDeclareBaseSettingVO: {},
      },
      //下拉值批量添加
      batch: {
        show: false,
        text: '',
        num: 0,
        textArr: [],
      },
      outputTable1: 0,
      outputTable2: 0,
      //行政区划返回格式
      divisionsFormatArr: [
        {
          value: 1,
          label: '国标.名称',
        },
        {
          value: 2,
          label: '国标-名称',
        },
        {
          value: 3,
          label: '国标_名称',
        },
        {
          value: 4,
          label: '国标 名称',
        },
        {
          value: 5,
          label: '国标',
        },
        {
          value: 6,
          label: '名称.国标',
        },
        {
          value: 7,
          label: '名称-国标',
        },
        {
          value: 8,
          label: '名称_国标',
        },
        {
          value: 9,
          label: '名称 国标',
        },
        {
          value: 10,
          label: '国标名称',
        },
        {
          value: 11,
          label: '名称国标',
        },
        {
          value: 12,
          label: '国标--名称',
        },
        {
          value: 13,
          label: '名称--国标',
        },
      ],
      newIndex: '',
      curShowIndex: 0,
      isDeclareColumnNameDisabled: ''
    }
  },
  computed: {
    checkAddress() {
      var check = false
      this.table.tableData.forEach((item) => {
        if (item.dataSourceItemCode == '10000015') {
          check = true
        }
      })
      return check
    },
    tableHeight() {
      return this.$global.bodyHeight - 300 + 'px'
    },
    tableHeight1() {
      return this.$global.bodyHeight / 2 - 200 + this.outputTable1
    },
    tableHeight2() {
      return this.$global.bodyHeight / 2 - 200 + this.outputTable2
    },
    //业务类型
    bussinssType() {
      return function (val) {
        var arr = ['社保', '公积金']
        if (val != '' || val != null) {
          return arr[val - 1]
        } else {
          return '暂无'
        }
      }
    },
    //业务类型
    changeType() {
      return function (val) {
        var arr = ['增员', '减员', '调基', '变更', '补缴']
        if (val != '' || val != null) {
          return arr[val - 1]
        } else {
          return '暂无'
        }
      }
    },
    getContrastMode() {
      return function (type) {
        var arr = ['等于', '不等于', '包含', '不包含']
        return arr[type - 1]
      }
    },
    filterCondition(data) {
      return function (data) {
        var str = ''
        if (data && data.conditionItemList) {
          data.conditionItemList.forEach((item, index) => {
            str += index + 1 + '.' + item.declareColumnName + this.getContrastMode(item.contrastMode) + '值：' + item.contrastValue + '\n'
          })
          str += '字段' + (data.isShow ? '显示' : '隐藏')
        }

        return str ? str : ''
      }
    },
    getColumnScopes() {
      return function (data) {
        var arr = []
        if (!data) {
          return arr
        }
        data.forEach((item) => {
          if (item.isShow == 1) {
            arr.push(item)
          }
        })
        return arr
      }
    },
    filterValueTypeOption1() {
      return function (code) {
        if (code == '20000002') {
          return this.valueTypeOption1.filter((item) => item.value == 4 || item.value == 5)
        }
        return this.valueTypeOption1
      }
    },
  },
  watch: {
    conditionDrawer: {
      handler(val) {
        console.log(val)
        var strArr = []
        var str = ''
        if (val.conditionItemList && val.conditionItemList.length > 0) {
          val.conditionItemList.forEach((item, index) => {
            if (item.contrastMode && item.contrastValue && item.declareColumnName) {
              var Symbol = this.getContrastMode(item.contrastMode)
              strArr.push(`${item.declareColumnName}${Symbol}${item.contrastValue}`)
            }
          })
          if (strArr.length > 0) {
            var relation = this.conditionDrawer.relation == 1 ? ' 并且 ' : ' 或者 '
            var isShow = this.conditionDrawer.isShow == 1 ? '显示' : '隐藏'
            var required = this.conditionDrawer.required == 2 ? '必填' : '选填'
            str = `${this.conditionDrawer.declareColumnName}${isShow}且${required}${
              this.conditionDrawer.conditionValue ? '，填写默认值：' + this.conditionDrawer.conditionValue : ''
            }`
            this.conditionDrawer.conclusion = strArr.join(relation) + '，' + str
          }
        }
      },
      deep: true,
    },
    'batch.text'(newVal, oldVal) {
      console.log(newVal)
      this.batch.text = newVal.replace(/^(\,|，)*|(\,|\，)*$/g, '')
      var obj = this.getInputDataRow(newVal)
      this.batch.num = obj.num
      this.batch.textArr = obj.value
    },
  },
  created() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({ name: '报盘信息维护' })
    this.requestInfo().catch((err) => {
      console.log(err)
    })
  },
  methods: {
    //默认值和下拉值可同时存在，且如果是下拉属性，默认值应从下拉选项里选一个当默认值，不可随便输入
    defaultValueCheck(rule, value, callback) {
      var check = false
      if(!value){
        return callback()
      }
      this.outputForm.selectTable.forEach((item) => {
          if (item.scopeValue == value) {
            check = true
          }
        })
      if (this.outputForm.inputType == 2 && !check && this.outputForm.selectTable && this.outputForm.selectTable.length > 0) {
        return callback(new Error('因字段属性是下拉，默认值应当从下拉选择一个填写'))
      }
      return callback()
    },
    //源字段参保时间 dataSourceItemCode 20000002 和 缴纳基数 20000005 不可重复
    checkDataSourceTableCode(rule, value, callback) {
      var arr = []
      var check = true
      for (let index = 0; index < this.table.tableData.length; index++) {
        var item = this.table.tableData[index]
        if (item.dataSourceItemCode && value == item.dataSourceItemCode && value == '20000005') {
          arr.push(item)
        }
        if (arr.length >= 2) {
          check = false
        }
      }
      if (!check) {
        return callback(new Error('不可设置多个字段同时引用源字段[缴纳基数]'))
      }
      return callback()
    },
    //非首次收集字段勾选 改变
    changeFirstKey(val, scope) {
      var index = scope.$index
      if (this.table.tableData[index].columnCondition != null && val == '1') {
        this.$confirm('该操作会清除已设置的条件信息，是否继续？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          showClose: false,
          customClass: 'pal-confirm',
          type: 'warning',
        })
          .then(() => {
            this.table.tableData[index].required = 1
            this.table.tableData[index].disabled = true
            this.table.tableData[index].columnCondition = null
          })
          .catch(() => {
            this.table.tableData[index].notFirstTime = 0
            this.table.tableData[index].required = 0
            this.table.tableData[index].disabled = false
          })
      } else {
        if (val == '1') {
          this.table.tableData[index].required = 1
          this.table.tableData[index].disabled = true
        } else {
          this.table.tableData[index].disabled = false
          if (this.table.tableData[index].columnCondition != null) {
            this.table.tableData[index].disabled = true
          }
        }
      }
    },
    changeDeclareColumnName() {
      this.$refs.conditionDrawer.validate()
      // this.conditionDrawer.conditionItemList.forEach((item,index)=>{
      //   this.$refs.conditionDrawer.validateField('conditionItemList.'+index+'.contrastValue')
      // })
    },
    changeContrastValue() {
      this.conditionDrawer.conditionItemList.forEach((item, index) => {
        this.$refs.conditionDrawer.validateField('conditionItemList.' + index + '.contrastValue')
      })
    },
    //收起设置输出值的表格
    upTable(type) {
      if (type == 'selectTable') {
        this.selectTableH = '0'
        this.outputTable1 = 0
        this.outputTable2 = 200
      } else {
        this.outputTableH = '0'
        this.outputTable1 = 200
        this.outputTable2 = 0
      }
      if (this.$refs.outputFormTable1) {
        this.$refs.outputFormTable1.doLayout()
      }
      this.$refs.outputFormTable2.doLayout()
    },
    //展开设置输出值的表格
    dowmTable(type) {
      if (type == 'selectTable') {
        this.selectTableH = '100%'
        this.outputTable2 = 0
      } else {
        this.outputTableH = '100%'
        this.outputTable1 = 0
      }
      if (this.outputTableH == '0') {
        this.outputTable1 = 200
      } else if (this.selectTableH == '0') {
        this.outputTable2 = 200
      }
      if (this.$refs.outputFormTable1) {
        this.$refs.outputFormTable1.doLayout()
      }
      this.$refs.outputFormTable2.doLayout()
    },
    //去除条件
    removeCondition() {
      this.$confirm('是否确定去除此字段的条件？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
          this.$set(this.table.tableData[this.tableIndex], 'columnCondition', null)
          this.table.tableData[this.tableIndex].disabled = false
          if (this.table.tableData[this.tableIndex].notFirstTime == 1) {
            this.table.tableData[this.tableIndex].disabled = true
          }
          this.cancelConditionDrawer()
        })
        .catch(() => {})
    },
    //检测条件值是否重复
    checkRepeatContrastValue(rule, value, callback, key) {
      var arr = []
      var index = Object.keys(key)[0].split('.')[1]
      var selfArr = this.conditionDrawer.conditionItemList.filter((item) => {
        return item.declareColumnName == this.conditionDrawer.conditionItemList[index].declareColumnName
      })
      if (!value) {
        return callback()
      }
      selfArr.forEach((item) => {
        if (item.contrastValue == value && selfArr.length > 1) {
          arr.push(1)
        }
      })
      if (arr.length >= 2) {
        return callback(new Error('字段条件值出现重复，请先更正'))
      }
      return callback()
    },
    //检测 是否有重复条件和是否交叉使用条件
    checkRepeatCondition(rule, value, callback) {
      var check = false
      var str = ''
      for (let index = 0; index < this.conditionDrawer.conditionColumnNameOption.length; index++) {
        var item = this.conditionDrawer.conditionColumnNameOption[index]
        if (!item.columnCondition) {
          continue
        }

        if (!item.columnCondition.conditionItemList || item.columnCondition.conditionItemList.length <= 0) {
          continue
        }
        if (item.declareColumnName == value) {
          item.columnCondition.conditionItemList.forEach((item1) => {
            if (item1.declareColumnName == this.conditionDrawer.declareColumnName) {
              str = item.declareColumnName
              check = true
            }
          })
        }
      }
      if (check) {
        return callback(new Error(`字段${str}与${this.conditionDrawer.declareColumnName}互为联动条件，请更正`))
      }
      return callback()
    },
    // 添加条件
    confirmConditionDrawer() {
      this.$refs.conditionDrawer.validate((valid) => {
        if (valid) {
          this.$set(this.table.tableData[this.tableIndex], 'columnCondition', this.deepCopy(this.conditionDrawer))
          this.table.tableData[this.tableIndex].required = 1
          this.table.tableData[this.tableIndex].disabled = true
          // this.table.tableData[this.tableIndex].columnCondition = this.deepCopy(this.conditionDrawer)
          console.log(this.table.tableData[this.tableIndex].columnCondition)
          this.cancelConditionDrawer()
        }
      })
    },
    cancelConditionDrawer() {
      this.conditionDrawer = this.$options.data().conditionDrawer
      this.$refs.conditionDrawer.resetFields()
    },
    //打开条件
    showConditionDrawer(index) {
      this.tableIndex = index
      if (this.table.tableData[this.tableIndex].notFirstTime == 1) {
        this.$message.warning('非首次收集字段不可设置条件')
        return
      }
      if (this.table.tableData[this.tableIndex].columnCondition) {
        this.conditionDrawer = this.deepCopy(this.table.tableData[this.tableIndex].columnCondition)
      }
      this.conditionDrawer.declareColumnName = this.table.tableData[index].declareColumnName
      this.conditionDrawer.declareColumnUuid = this.table.tableData[index].uuid
      this.conditionDrawer.conditionColumnNameOption = []
      this.table.tableData.forEach((item, index) => {
        if (index != this.tableIndex && item.declareColumnName != '' && item.notFirstTime != 1) {
          this.conditionDrawer.conditionColumnNameOption.push(item)
        }
      })
      this.conditionDrawer.isShow = 1
      this.conditionDrawer.show = true
    },
    //删除条件一行
    delCondition(index) {
      this.conditionDrawer.conditionItemList.splice(index, 1)
    },
    //添加条件一行
    addCondition() {
      this.conditionDrawer.conditionItemList.push({
        declareColumnName: '',
        contrastMode: '',
        contrastValue: '',
      })
    },
    //字段属性改变时
    changeInputType(val, index) {
      if (val != 2) {
        this.table.tableData[index].columnScopes = []
      }
      if (val == 2 && this.table.tableData[index].dataSourceTableCode != '60000') {
        this.dataSourceItemCodeChange(this.table.tableData[index].dataSourceItemCode, this.table.tableData[index], index)
        if (this.table.tableData[index].columnScopes.length <= 0) {
          this.table.tableData[index].inputType = val
        }
      }
      if (
        ['40000007', '40000008', '40000009', '40000010'].indexOf(this.table.tableData[index].dataSourceItemCode) > -1 &&
        this.table.tableData[index].inputType == 1
      ) {
        this.table.tableData[index].exportFormat = ''
        return
      }
      if ((val == 1 || val == 2) && ['40000007', '40000008', '40000009', '40000010'].indexOf(this.table.tableData[index].dataSourceItemCode) <= -1) {
        this.table.tableData[index].exportFormat = ''
        return
      }
      this.table.tableData[index].valueTypeOption2 = this.valueTypeOption2Item[this.table.tableData[index].inputType]
      if (
        ['40000007', '40000008', '40000009', '40000010'].indexOf(this.table.tableData[index].dataSourceItemCode) > -1 &&
        this.table.tableData[index].inputType == 2
      ) {
        this.table.tableData[index].valueTypeOption2 = this.valueTypeOption2Item[3]
      }
      this.$nextTick(() => {
        this.table.tableData[index].exportFormat = this.table.tableData[index].valueTypeOption2[0].dictCode
      })
    },
    //数据源改变时
    dataSourceTableCodeChange(e, index) {
      //选中请选择, 或自定义时无需出现旁边的下拉
      if (e == '') {
        this.table.tableData[index].dataSourceItemCode = ''
        this.table.tableData[index].columnConstantSettings = []
        this.table.tableData[index].columnScopes = []
        return
      } else if (e == '60000') {
        this.table.tableData[index].dataSourceItemCode = ''
        this.table.tableData[index].inputType = 1
        this.table.tableData[index].disabledSelect = false
        this.table.tableData[index].columnConstantSettings = []
        this.table.tableData[index].columnScopes = []
        // this.table.tableData[index].defaultValue = ''
        return
      } else if (e == '50000' || e == '70000') {
        // 源【特殊类型】，是否显示 默认不勾选=隐藏，只读
        // 源【其他】，默认不勾选=隐藏 可编辑
        this.table.tableData[index].isShow = 0
      }

      this.table.tableData[index].dataSourceOption2 = this.dataSourceOptionItem[this.table.tableData[index].dataSourceTableCode]

      this.$nextTick(() => {
        if (this.table.tableData[index].dataSourceOption2.length > 0) {
          this.table.tableData[index].dataSourceItemCode = this.table.tableData[index].dataSourceOption2[0].code
          if (this.table.tableData[index].dataSourceOption2[0].type == 1) {
            this.autoMapping(this.table.tableData[index].dataSourceOption2[0].code, this.table.tableData[index])
            if (this.table.tableData[index].columnConstantSettings.length <= 0) {
              this.table.tableData[index].disabledSelect = false
            }
          } else if (this.table.tableData[index].dataSourceOption2[0].type == 0) {
            this.table.tableData[index].inputType = 1
            this.table.tableData[index].disabledSelect = false
          } else {
            this.table.tableData[index].disabledSelect = false
          }
          // console.log(this.table.tableData)
          this.dataSourceItemCodeChange(this.table.tableData[index].dataSourceOption2[0].code, this.table.tableData[index], index)
        }
      })
    },
    //数据源字段改变2 -  如果是系统下拉值，则字段属性 改变为下拉，且不可编辑
    dataSourceItemCodeChange(val, row, index) {
      // console.log(val,row)

      // 纯取值类，属性【固定隐藏】，默认不勾选=隐藏，只读
      let dataSourceOption2 = row.dataSourceOption2?row.dataSourceOption2:[]
      for(let i=0;i<dataSourceOption2.length;i++){
        if(dataSourceOption2[i].code===val){
          if(dataSourceOption2[i].input===0 && dataSourceOption2[i].output===1){
            this.table.tableData[index].isShow = 0
          } else {
            this.table.tableData[index].isShow = 1
          }
          break
        }
      }

      this.autoMapping(val, row)
      this.$refs.tableForm.validate()
    },
    //如果是系统下拉值,则映射值自动赋值
    autoMapping(val, row) {
      // console.log(val,row,this.importData.systemList,row.dataSourceOption2)
      var check = false
      for (let index = 0; index < row.dataSourceOption2.length; index++) {
        const item = row.dataSourceOption2[index]
        if (item.code == val) {
          console.log('当前字段属性' + item.type)
          row.inputType = item.type
          var columnConstantSettings = this.importData.systemList.filter((item1) => {
            return item1.code == item.code
          })
          // console.log(columnConstantSettings)
          // row.columnConstantSettings = []
          let column = []
          let column2 = []
          var obj = {}
          row.columnConstantSettings.forEach((item) => {
            obj[item.originalText] = item.originalText
          })
          if (columnConstantSettings.length > 0) {
            columnConstantSettings[0].constantSettingList.forEach((item) => {
              var str = ''
              if (item.originalText == '$0<=0') {
                str = '不参保'
              } else if (item.originalText == '$0>0') {
                str = '参保'
              }
              if (!obj[item.originalText]) {
                column.push({
                  originalText: item.originalText,
                  showText: str ? str : item.originalText,
                  readonly: false,
                  isShow: 1,
                })
              }
              column2.push({
                scopeValue: item.originalText,
                readonly: false,
                isShow: 1,
              })
            })
            row.columnConstantSettings = column
            row.columnScopes = column2
            row.defaultValue = ''
            check = true

            row.exportFormat = ''
            if (item.type > 2) {
              row.inputType = item.type
              row.valueTypeOption2 = this.valueTypeOption2Item[item.type]
              row.exportFormat = this.valueTypeOption2Item[item.type][0].dictCode
            }
          } else {
            // row.columnConstantSettings = []
            row.columnScopes = []
            row.exportFormat = ''
            row.defaultValue = ''
            if (item.type > 2) {
              row.inputType = item.type
              row.valueTypeOption2 = this.valueTypeOption2Item[item.type]
              row.exportFormat = this.valueTypeOption2Item[item.type][0].dictCode
              check = true
            }
          }
          break
        }
      }
      if (row.dataSourceItemCode == '20000002') {
        row.disabledSelect = false
      } else {
        row.disabledSelect = false
      }
      // if(!check){
      //   row.disabledSelect = false
      // }else{
      //   row.disabledSelect = true
      // }
      return check
    },
    async requestInfo() {
      this.showLoading()
      this.$http({
        url: 'policy/offerSettings/findByUuid/' + this.$route.query.uuid,
        method: 'post',
      })
        .then(async ({ data }) => {
          data.data.showType = data.data.showType == null ? 0 : data.data.showType
          this.formData = data.data
          this.formData.declareColumnUuid = data.data.uuid
          // this.table.tableData = data.data.columnSettings
          await this.getIntroduction(data.data.addrId)
          await this.getFindAllDataSource()
          await this.getDictByKey('10001', '3')
          await this.getDictByKey('10006', '4')
          await this.getDictByKey('10036', 'other')

          try {
            data.data.columnSettings.forEach((item, index) => {
              item.dataSourceOption2 = this.dataSourceOptionItem[item.dataSourceTableCode]
              item.valueTypeOption2 = this.valueTypeOption2Item[item.inputType]
              if (['40000007', '40000008', '40000009', '40000010'].indexOf(item.dataSourceItemCode) > -1) {
                item.valueTypeOption2 = this.valueTypeOption2Item[3]
              }
              var arr = this.importData.systemList.filter((item1) => {
                return item1.code == item.dataSourceItemCode
              })
              if (arr.length > 0) {
                item.dataSourceName = arr[0].constantSettingList.map((item2) => {
                  return item2.originalText
                })
              }
              if (item.inputType >= 1 && item.dataSourceTableCode != '60000') {
                if (item.dataSourceItemCode == '20000002') {
                  item.disabledSelect = false
                } else {
                  item.disabledSelect = false
                }
              }
              if (item.notFirstTime == 1) {
                item.disabled = true
              }
            })
            //原字段下拉
            data.data.columnSettings.forEach((item) => {
              if (item.inputType == 2 && item.dataSourceOption2 && this.checkDataSource(item.dataSourceOption2, item.dataSourceItemCode)) {
                item.disabledSelect = false
                // item.columnConstantSettings.forEach((item1) => {
                //   if (item.dataSourceName.indexOf(item1.originalText) > -1) {
                //     item1.readonly = true
                //   }
                // })
                item.columnScopes.forEach((item1) => {
                  if (item.dataSourceName.indexOf(item1.scopeValue) > -1) {
                    item1.readonly = false
                  }
                })
              }
              if (item.inputType > 2 && item.valueTypeOption2 && !item.exportFormat) {
                item.exportFormat = item.valueTypeOption2[0].dictCode
              }
              if (item.columnCondition && item.columnCondition.conclusion) {
                item.disabled = true
              }
              this.$set(item,'otherBelongTo2',item.otherBelongTo ? item.otherBelongTo.split(',') : [])
              item.otherBelongTo2 = item.otherBelongTo ? item.otherBelongTo.split(',') : []
            })
          } catch (err) {
            // throw new Error(err)
            console.log(err)
          }
          this.table.tableData = data.data.columnSettings
          this.closeLoading()
        })
        .catch(() => {
          this.closeLoading()
        })
    },
    //是否是源字段下拉
    checkDataSource(data, key) {
      // console.log(data,key)
      var check = false
      data.forEach((item) => {
        if (item.code == key) {
          if (item.type == 2) {
            check = true
          }
        }
      })
      return check
    },
    //快速获取常量值
    importQueryFieldChange(item) {
      // console.log(this.importData.importQueryField)
      // this.importData.importQueryField = item.tableCode
      this.importData.importQueryFieldList = item.constantSettingList
    },
    //添加输出值
    addOutput() {
      // if (
      //   this.outputForm.inputType == 2 &&
      //   this.outputForm.dataSourceTableCode != '60000'
      // ) {
      //   this.$message.warning('显示值取自系统下拉常量，不能自行添加')
      //   return
      // }
      this.outputForm.outputTable.push({
        originalText: '',
        showText: '',
        isShow: 0,
      })
    },
    addSelectTable() {
      // if (
      //   this.outputForm.inputType == 2 &&
      //   this.outputForm.dataSourceTableCode != '60000'
      // ) {
      //   this.$message.warning('选值取值系统常量，不能执行添加')
      //   return
      // }
      this.outputForm.selectTable.push({
        originalText: '',
        showText: '',
        isShow: 1,
      })
    },
    //引入快速获取
    importQuery() {
      this.$refs.importQueryField.validate((vaild) => {
        if (vaild) {
          this.openQueryMap = false
          this.importData.importQueryFieldList.forEach((item) => {
            this.outputForm.selectTable.push({
              scopeValue: item.originalText,
              isShow: 1,
            })
          })
        }
      })
    },
    //输出值确定是，校验
    outputDataConfig() {
      var str = ''
      var originalTextArr = []
      for (let index = 0; index < this.outputForm.outputTable.length; index++) {
        const item = this.outputForm.outputTable[index]
        if (item.showText) {
          if (!item.originalText) {
            str = '获取值未输入，请输入'
          }
        }
        if (item.originalText) {
          originalTextArr.push(item.originalText)
        }
      }
      var map = new Set(originalTextArr)
      if (str) {
        this.$message.error(str)
      } else if (originalTextArr.length != [...map].length) {
        this.$message.error('获取值重复，请更正')
      }

      var arr = this.outputForm.outputTable.filter((item) => {
        return item.originalText
      })
      var arr2 = this.outputForm.selectTable.filter((item) => {
        return item.scopeValue
      })
      var map2 = new Set(arr2.map((item) => item.scopeValue))
      if (arr2.length != [...map2].length) {
        this.$message.error('下拉值重复，请更正')
      }

      this.$refs.outputForm.validate((vaild) => {
        if (vaild) {
          // if (this.outputForm.defaultValue && arr2.length > 0) {
          //   this.$message.warning('输出方式只能二选一，请修正')
          //   return
          // } else
          if (this.outputForm.inputType == 2 && arr2.length <= 0 && !this.outputForm.defaultValue) {
            this.$message.warning('需设置下拉值才能下拉选择，请检查')
            return
          }

          this.table.tableData[this.tableIndex].declareColumnName = this.outputForm.declareColumnName
          this.table.tableData[this.tableIndex].comment = this.outputForm.comment
          this.table.tableData[this.tableIndex].defaultValue = this.outputForm.defaultValue
          //映射值
          this.table.tableData[this.tableIndex].columnConstantSettings = arr
          //下拉值
          this.table.tableData[this.tableIndex].columnScopes = arr2
          // console.log(this.table.tableData)
          this.openDrawer = false
          this.$nextTick(() => {
            this.outputForm = {
              declareColumnName: '',
              comment: '',
              defaultValue: '',
              outputTable: [],
              selectTable: [],
            }
          })
        }
      })
    },
    //检测显示值是否存在重复
    checkValueRepeat(rule, value, callback) {
      // console.log(value)
      var arr = []
      var check = true
      var ids = ''
      for (let index = 0; index < this.outputForm.outputTable.length; index++) {
        const item = this.outputForm.outputTable[index]
        if (item.originalText && value == item.originalText) {
          arr.push(item)
        }
        if (arr.length >= 2) {
          check = false
          break
        }
      }
      if (!check) {
        var str = '显示值重复，请修改'
        return callback(new Error(str))
      }
      return callback()
    },
    //检测 下拉值是否存在重复
    checkScopeValueText(rule, value, callback) {
      var arr = []
      var check = true
      for (let index = 0; index < this.outputForm.selectTable.length; index++) {
        const item = this.outputForm.selectTable[index]
        if (item.scopeValue && value == item.scopeValue) {
          arr.push(item)
        }
        if (arr.length >= 2) {
          check = false
          break
        }
      }
      if (!check) {
        var str = '下拉值重复，请修改'
        return callback(new Error(str))
      }
      return callback()
    },
    //检查下拉值 是否 是公积金单位比例、公积金个人比例、补充公积金单位比例、补充公积个人比例
    //是则输入限制格式
    checkScopeValueSouce(rule, value, callback) {
      var check = true
      var arr = ['40000007', '40000008', '40000009', '40000010']
      if (!value) {
        return callback()
      }
      if (arr.includes(this.outputForm.dataSourceItemCode)) {
        var reg = /^(0{1})(\.\d{1,2})$/
        check = reg.test(value)
      }
      if (!check) {
        var str = '比例选值请设置小数数值，如：0.05'
        return callback(new Error(str))
      }
      return callback()
    },
    //获取值失去焦点
    changeOriginalText() {
      this.$refs.outputForm.validate()
    },
    //获取值验证，只有输入值  有值时才校验
    checkoriginalText(rule, value, callback, key) {
      var index = Object.keys(key)[0].split('.')[1]
      if (this.outputForm.outputTable[index].showText) {
        if (!value) {
          return callback(new Error('请输入获取值'))
        } else {
          return callback()
        }
      } else {
        return callback()
      }
    },
    //检测映射值重复值
    checkDeclareColumnNameRepeat(rule, value, callback) {
      var arr = []
      var check = true
      for (let index = 0; index < this.table.tableData.length; index++) {
        var item = this.table.tableData[index]
        if (item.declareColumnName && value == item.declareColumnName) {
          arr.push(item)
        }
        if (arr.length >= 2) {
          check = false
        }
      }
      if (!check) {
        return callback(new Error('字段名称不能重复，请修正'))
      }
      if (value.length > 20) {
        return callback(new Error('名称不能超过20个字符'))
      }
      return callback()
    },
    //快速获取关闭
    importQueryOnClose() {
      this.$refs.importQueryField.clearValidate()
      ;(this.openQueryMap = false), (this.importData.importQueryField = '')
    },
    //删除输出值 某一列
    removeOutputVal(row, index) {
      this.outputForm.outputTable.splice(index, 1)
      this.$nextTick(() => {
        this.$refs.outputForm.validate((vaild) => {})
      })
    },
    //打开快速获取弹窗
    openQueryImport() {
      // console.log(this.outputForm)
      if (this.outputForm.inputType == 2 && this.outputForm.dataSourceTableCode != '60000') {
        this.$message.warning('选值取值系统常量，不能执行添加')
        return
      }
      this.openQueryMap = true
    },
    handleClose() {
      this.openImportMap = false
      this.closeLoading()
    },
    importMapUpload(e) {
      this.$refs.importFile.click()
    },
    importOnClose() {
      this.$refs.importFieldUpload.clearValidate()
      this.uploadErrMsg = ''
      this.importData.uploadFileName = ''
      this.$refs.importFile.value = null
      this.openImportMap = false
    },
    //导入映射弹窗
    improtMapping() {
      this.openImportMap = true
    },
    //导入映射文件
    importFile() {
      this.$refs.importFieldUpload.validate((vaild) => {
        if (vaild) {
          if (this.uploadErrMsg) {
            return
          }
          var obj = {}
          this.openImportMap = false
          // console.log(this.outputForm)
          //如果不是系统下拉值,
          let column = []
          if (this.outputForm.dataSourceTableCode == '60000' && this.outputForm.inputType == 2) {
            this.outputForm.outputTable = []
            this.importData.importMapList.forEach((item) => {
              column.push({
                originalText: item.originalText,
                showText: item.showText,
                isShow: 1,
              })
            })
            this.outputForm.outputTable = column
          } else if (this.outputForm.dataSourceTableCode != '60000' && this.outputForm.inputType == 2) {
            // let arr = []
            // this.outputForm.outputTable.forEach((item) => {
            //   arr.push(item.originalText)
            // })
            let arr2 = []
            this.importData.importMapList.forEach((item) => {
              // if (arr.indexOf(item.originalText) > -1) {
              arr2.push(item)
              // }
            })
            // console.log(arr2,arr)
            // if (arr2.length > 0) {
            //   this.outputForm.outputTable = []
            // }
            arr2.forEach((item) => {
              column.push({
                originalText: item.originalText,
                showText: item.showText,
                isShow: 1,
              })
            })
            this.outputForm.outputTable = []
            this.outputForm.outputTable = column
          } else {
            this.outputForm.outputTable = []
            this.importData.importMapList.forEach((item) => {
              column.push({
                originalText: item.originalText,
                showText: item.showText,
                isShow: 1,
              })
            })
            this.outputForm.outputTable = column
          }
          var obj = {}
          //去重
          this.outputForm.outputTable = this.outputForm.outputTable.reduce((item, next) => {
            obj[next.originalText] ? '' : (obj[next.originalText] = true && item.push(next))
            return item
          }, [])
        }
      })
    },
    //导出映射值
    outputList() {
      this.$downloadFile('policy/offerSettings/exportColumnConstant', 'post', this.outputForm.outputTable)
    },
    //导入映射选择文件确定时
    importChangeFile(e) {
      // console.log(e.target.files)
      var arr = ['xlsx', 'xls', 'XLSX', 'XLS']
      if (e.target.files.length > 0) {
        this.importData.uploadFileName = e.target.files[0].name
        var split = e.target.files[0].name.split('.')
        split = split[split.length - 1]
        this.uploadErrMsg = ''
        if (arr.indexOf(split) <= -1) {
          this.uploadErrMsg = '仅支持上传xlsx，xls格式文件'
          return
        }
        var fileData = new FormData()
        fileData.append('file', e.target.files[0])
        this.showLoading(document.getElementById('upload'))
        this.$http({
          url: 'policy/offerSettings/getColumnConstantByFile',
          method: 'post',
          data: fileData,
          headers: {
            customSuccess: true,
          },
        })
          .then((res) => {
            if (res.data.code == 200) {
              this.importData.importMapList = res.data.data
              this.uploadErrMsg = ''
            } else {
              this.uploadErrMsg = res.data.message
              this.$message.error(this.uploadErrMsg ? this.uploadErrMsg : '')
            }
            e.target.value = ''
            this.$refs.importFieldUpload.clearValidate()
            this.closeLoading()
            // this.formData.columnSettings = res.data.data
            // this.$refs.custonMode.validateField('uploadFileName')
          })
          .catch((err) => {
            console.log(err)
            this.$refs.importFieldUpload.clearValidate()
            this.closeLoading()
            e.target.value = ''
          })
      }
    },
    //确定保存
    save() {
      this.$refs.tableForm.validate((vaild) => {
        if (vaild) {
          if (this.formData.columnSettings.length <= 0) {
            this.$message.error('请至少设置一条数据')
            return
          }
          var check = true
          var checkAutoParse = false
          var err = []
          this.formData.columnSettings = this.table.tableData
          var dictCode = this.table.tableData.map((item) => {
            return item.dataSourceItemCode
          })
          //需校验这几个是否是下拉，是下拉，需要输入浮点数
          var checkArr = ['40000007','40000008','40000009','40000010']
          var _reg = /^(0{1})(\.\d{1,2})$/;
          this.formData.columnSettings.forEach((item, index) => {
            // if (item.uuid) {
            //   item.declareColumnUuid = this.formData.uuid
            // }
            // item.sort = index + 1
            // item.columnConstantSettings.forEach((item1, index) => {
            //   if (item1.uuid) {
            //     item1.declareColumnUuid = this.formData.uuid
            //   }
            // })
            if (item.inputType == 2 && (item.columnScopes == null || item.columnScopes.length <= 0) && !item.defaultValue) {
              check = false
              err.push(`${err.length + 1}、字段{ ${item.declareColumnName} }属性下拉，缺失下拉值，请修正\n`)
            }
            if (item.dataSourceItemCode == '10000015') {
              checkAutoParse = true
            }
            if(checkArr.indexOf(item.dataSourceItemCode) > -1){
              for (let index = 0; index < item.columnScopes.length; index++) {
                const item1 = item.columnScopes[index];
                _reg.lastIndex = 0
                if(!_reg.test(item1.scopeValue)){
                  check = false
                  err.push(`${err.length + 1}、字段{ ${item.declareColumnName} }属性下拉，比例选值请设置小数数值，如：0.05，请修正\n`)
                  break;
                }
              }
            }
            item.otherBelongTo = item.otherBelongTo2.join(',')
          })
          if (!checkAutoParse) {
            this.formData.autoParse = 0
            this.formData.divisionsFormat = ''
          }
          //检查源字段 属性下拉 值是否一致
          var obj1 = {}
          for (let i = 0; i < this.formData.columnSettings.length; i++) {
            const item = this.formData.columnSettings[i]
            if (!obj1[item.dataSourceItemCode]) {
              obj1[item.dataSourceItemCode] = []
              obj1[item.dataSourceItemCode].push(item)
            } else {
              continue
            }
            for (let j = i + 1; j < dictCode.length; j++) {
              const item1 = dictCode[j]
              if (
                item.dataSourceItemCode == item1 &&
                item.inputType == 2 &&
                this.formData.columnSettings[j].inputType == 2 &&
                item.dataSourceTableCode != '60000'
              ) {
                obj1[item1].push(this.formData.columnSettings[j])
              }
            }
          }
          var equalMessage = ''
          for (const prop in obj1) {
            if (obj1[prop].length == 1) {
              continue
            }
            let checkList = []
            obj1[prop].reduce((prev, cur, index, arr) => {
              let arr1 = prev.columnScopes ? prev.columnScopes.map((item) => item.scopeValue) : []
              let arr2 = cur.columnScopes ? cur.columnScopes.map((item) => item.scopeValue) : []
              checkList.push(this.$lodash.isEqual(arr1, arr2)) //比较下拉值是否一致
              return cur
            })
            if (checkList.includes(false)) {
              check = false
              obj1[prop].forEach((item) => {
                equalMessage += item.declareColumnName + '、'
              })
              err.push(`${err.length + 1}、字段{ ${equalMessage} }采用同一源字段，下拉选项需设置一致，请修正\n`)
            }
          }
          if (this.formData.fyRuleType == '1' && this.formData.changeType == 5) {
            var checkArr = []
            let check = false
            this.formData.columnSettings.forEach((item) => {
              // console.log(item)
              if (item.dataSourceItemCode == '20000003' || item.dataSourceItemCode == '20000004') {
                checkArr.push(String(item.dataSourceItemCode))
              }
            })
            if (checkArr.indexOf('20000003') > -1 && checkArr.indexOf('20000004') > -1) {
              check = true
            }
            if (!check) {
              this.$message.error('补缴合并连续月显示要求设置{补缴开始时间}和{补缴结束时间}，系统无法获取，请检查')
              return
            }
          }

          if(this.formData.bussinssType == '2'){
            let check = false
            let accfundList = ['40000007','40000008', '20000005']
            let checkList = []
            this.formData.columnSettings.forEach((item) => {
              checkList.push(String(item.dataSourceItemCode))
            })
            var arr1 = checkList.filter(item=>{
              return ['20000016','20000017','20000018','20000019'].indexOf(item) > -1
            })
            if(arr1.length > 0){
              checkList.forEach(item=>{
                var index = accfundList.indexOf(item)
                if(index > -1){
                  accfundList.splice(index,1)
                }
                return index > -1
              })
              if(accfundList.length == 0){
                check = true
              }
              if(!check){
                this.$message.error(`请同时设置参保比例和缴纳基数，便于参保时计算缴纳金额`).close(3000)
                return
              }
            }
          }
          if (this.checkAddress && this.formData.autoParse == 1 && !this.formData.divisionsFormat) {
            this.$message.error('请选择行政区划返回格式')
            return
          }

          if (!check) {
            var errMsg = ''
            err.forEach((item, index) => {
              errMsg += `<p>${item}</p>`
            })
            this.$confirm(errMsg, '', {
              dangerouslyUseHTMLString: true,
              showCancelButton: false,
              showConfirmButton: false,
              type: 'error',
            })
            setTimeout(() => {
              this.$msgbox.close()
            }, 3000)
            return
          }
          this.formData.versions = this.newVersion.versions
          this.formData.explain = this.newVersion.explain
          this.formData.oldVersions = this.newVersion.oldVersions
          this.confirmNewVersion()
        }
      })
    },
    //打开侧边栏
    showDrawer(index) {
      this.tableIndex = index
      this.openDrawer = true
      this.outputForm.declareColumnName = this.table.tableData[index].declareColumnName
      this.outputForm.comment = this.table.tableData[index].comment
      this.outputForm.defaultValue = this.table.tableData[index].defaultValue
      this.isDeclareColumnNameDisabled = this.table.tableData[index].conditionField
      this.$set(this.outputForm, 'checkedAll', 0)

      //值映射
      this.outputForm.outputTable = this.deepCopy(this.table.tableData[index].columnConstantSettings)
      //下拉选项值
      this.outputForm.selectTable = this.deepCopy(this.table.tableData[index].columnScopes)
      // console.log(this.outputForm)
      this.outputForm.inputType = this.table.tableData[index].inputType
      this.outputForm.dataSourceItemCode = this.table.tableData[index].dataSourceItemCode
      this.outputForm.dataSourceOption2 = this.table.tableData[index].dataSourceOption2
      this.outputForm.dataSourceTableCode = this.table.tableData[index].dataSourceTableCode
      var isShowList = this.outputForm.selectTable ? this.outputForm.selectTable.map((item) => item.isShow) : []
      if (isShowList.every((item) => item == 1)) {
        this.$set(this.outputForm, 'checkedAll', 1)
      }
    },
    //输出值，下拉选项 ，是否显示，加入勾选全部
    changeCheckAll(val) {
      if (val) {
        this.outputForm.selectTable.forEach((item) => {
          item.isShow = 1
        })
      } else {
        this.outputForm.selectTable.forEach((item) => {
          item.isShow = 0
        })
      }
    },
    //关闭侧边栏
    closeDrawer() {
      this.openDrawer = false
    },
    //操作栏，删除
    remove(row, index) {
      /*var check = true
      var arr = []
      this.table.tableData.forEach((item) => {
        if (item.columnCondition && item.columnCondition.conditionItemList) {
          item.columnCondition.conditionItemList.forEach((item1) => {
            if (item1.declareColumnName == row.declareColumnName) {
              check = false
              arr.push(item.declareColumnName)
            }
          })
        }
      })
      if (!check) {
        this.$message.error(`此字段被{${arr}}引用了条件，不可删除`)
        return
      }*/
      if(row.conditionField || (row.rules && row.rules.length>0)){
        this.$message.error(`字段{`+row.declareColumnName+`}存在校验规则，无法移除`)
        return
      }

      this.$confirm('是否确定移除该字段？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning',
      })
        .then(() => {
          this.table.tableData.splice(index, 1)
        })
        .catch(() => {})
    },
    //操作栏，上挪
    moveUp(row, index) {
      if (index != 0) {
        this.table.tableData[index] = this.table.tableData.splice(index - 1, 1, this.table.tableData[index])[0]
      } else {
        this.table.tableData.push(this.table.tableData.shift())
      }
      this.$nextTick(() => {
        this.$refs.table.doLayout()
      })
    },
    //操作栏，下移
    moveDown(row, index) {
      if (index != this.table.tableData.length - 1) {
        this.table.tableData[index] = this.table.tableData.splice(index + 1, 1, this.table.tableData[index])[0]
      } else {
        this.table.tableData.unshift(this.table.tableData.splice(index, 1)[0])
      }
      this.$nextTick(() => {
        this.$refs.table.doLayout()
      })
    },
    //操作栏，添加
    add(row, index) {
      var obj = {
        comment: '', //注解
        dataSourceItemCode: '',
        dataSourceTableCode: '',
        declareColumnName: '', //字段名称
        defaultValue: '',
        exportFormat: '', //字段属性，
        inputType: '', //1:文本，2：数值，3：日期,
        columnConstantSettings: [],
        dataSourceOption2: [],
        columnScopes: [],
        columnCondition: null,
        newly: true,
        required: 2,
        isShow: 1,
        otherBelongTo:"",
        otherBelongTo2:[],
      }
      if (index == this.table.tableData.length - 1) {
        this.table.tableData.push(obj)
      } else {
        this.table.tableData.splice(index + 1, 0, obj)
      }
    },
    //字段名称双击编辑
    editOutputName(row) {
      if(row.conditionField){
        this.$message.warning(`字段{${row.declareColumnName}}作为条件规则，不能改名`)
        return
      }
      this.$set(row, 'newly', true)
      row.newly = true
    },
    //字段名称失焦确定
    editOutputNameConfirm(row) {
      // console.log(row)
      if (row.declareColumnName != '') {
        this.$refs.tableForm.validate((vaild) => {
          if (vaild) {
            row.newly = false
          }
        })
      } else {
        row.newly = true
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
    //快速获取下拉值
    async getIntroduction(addrId) {
      this.$http({
        url: 'policy/offerSettings/getIntroduction/' + addrId + '/' + this.formData.bussinssType,
        method: 'post',
      }).then(({ data }) => {
        this.importData.systemList = data.data
      })
    },
    //获取源字段下拉
    async getFindAllDataSource() {
      await this.$http({
        url: 'policy/offerSettings/findAllDataSource',
        method: 'post',
      }).then(async (res) => {
        this.dataSourceOption = res.data.data
        this.dataSourceOption.unshift({
          code: '',
          name: '请选择',
        })
        for (let index = 0; index < this.dataSourceOption.length; index++) {
          const item = this.dataSourceOption[index]
          await this.getFindDataSourceItem(item.code)
        }
        // this.dataSourceOption.forEach(async (item)=>{
        //   await this.getFindDataSourceItem(item.code)
        // })
      })
    },
    async getFindDataSourceItem(tableCode) {
      if (!tableCode || tableCode == '60000') {
        return
      }
      await this.$http({
        url: 'policy/offerSettings/findDataSourceItem',
        method: 'post',
        params: {
          bussinssType: this.formData.bussinssType,
          addrName: this.formData.addrName,
          tableCode: tableCode,
        },
      }).then((res) => {
        // console.log(tableCode)
        this.dataSourceOptionItem[tableCode] = res.data.data
        this.table.tableData.forEach((item) => {
          if (item.dataSourceTableCode == tableCode) {
            item.dataSourceOption2 = this.dataSourceOptionItem[tableCode]
          }
        })
      })
    },
    //获取字典值
    async getDictByKey(dataKey, key) {
      await this.$http({
        url: 'policy/sys/dict/getDictByKey',
        method: 'get',
        params: {
          dataKey: dataKey,
        },
      }).then((res) => {
        if (key == '4') {
          this.valueTypeOption2Item['4'] = res.data.data.filter((item) => {
            // item.dictName.length >= 8 && item.dictName!="yyyy年MM月"
            return item
          })
          this.valueTypeOption2Item['5'] = res.data.data.filter((item) => {
            return item
          })
        } else {
          this.valueTypeOption2Item[key] = res.data.data
        }
        // console.log(this.valueTypeOption2Item)
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
    openDialog() {
      // this.dialogVisible = true
      this.save()
    },
    cancelNewVersion() {
      this.$refs.newVersionRuleForm.resetFields()
      this.$nextTick(() => {
        this.dialogVisible = false
      })
    },
    confirmNewVersion() {
      var isHasShow = false
      for(let i=0;i<this.formData.columnSettings.length;i++){
        if(!isHasShow && this.formData.columnSettings[i].isShow){
          isHasShow = true
          break
        }
      }
      if(!isHasShow){
        this.$message.warning('请勾选必须显示的字段')
        return
      }
      this.showLoading()
      this.$http({
        url: 'policy/offerSettings/selectByVersions',
        method: 'post',
        data: {
          versions: '',
          oldVersions: '',
          explain: '',
          id: 0,
          policyDeclareBaseSettingVO: this.formData,
        },
      })
        .then(async (res) => {
          this.closeLoading()
          if (res.data.code == '200') {
            this.newVersion.explain = res.data.data.explain
            ;(this.newVersion.oldVersions = res.data.data.oldVersions ? res.data.data.oldVersions.replace(/\*/g, '.') : ''),
              (this.newVersion.versions = res.data.data.versions ? res.data.data.versions.replace(/\*/g, '.') : '')
            this.newVersion.id = res.data.data.id
            this.newVersion.policyDeclareBaseSettingVO = this.formData
            this.dialogVisible = true
          } else {
          }
        })
        .catch((err) => {
          this.closeLoading()
          console.log(err)
        })
    },
    saveAll() {
      this.$refs.newVersionRuleForm.validate((valid) => {
        if (valid) {
          this.newVersion.policyDeclareBaseSettingVO.columnSettings.forEach((item, index) => {
            item.sort = index + 1
            if (item.notFirstTime == 1) {
              item.columnCondition = null
            }
          })
          this.showLoading()
          this.$http({
            url: 'policy/offerSettings/addOrUpdate',
            method: 'post',
            data: this.newVersion,
          })
            .then((res) => {
              this.closeLoading()
              if (res.data.code == 200) {
                this.dialogVisible = false
                var message = ''
                if (res.data.data) {
                  message = res.data.data.message
                }
                this.$confirm(message ? `<p style="text-align:center">保存成功<p>${message}</p><p>` : '保存成功', '', {
                  confirmButtonText: '继续设置',
                  cancelButtonText: '返回',
                  center: true,
                  dangerouslyUseHTMLString: true,
                  type: 'success',
                })
                  .then(() => {
                    // this.requestInfo()
                  })
                  .catch(() => {
                    this.$router.push('/offerFile/offerFileSettings')
                  })
              } else {
                this.$message.error(res.data.message ? res.data.message : '服务异常，请稍后再试')
              }
            })
            .catch(() => {
              this.closeLoading()
            })
        }
      })
    },
    batchAdd() {
      // if (
      //   this.outputForm.inputType == 2 &&
      //   this.outputForm.dataSourceTableCode != '60000'
      // ) {
      //   this.$message.warning('选值取值系统常量，不能执行添加')
      //   return
      // }
      console.log(this.outputForm.selectTable)
      this.batch.text = ''
      if (this.outputForm.selectTable.length > 0) {
        this.outputForm.selectTable.forEach((item) => {
          this.batch.text += item.scopeValue + '\n'
        })
      }
      this.batch.show = true
    },
    batchConfrim() {
      this.batch.show = false
      console.log(this.batch.textArr)
      var obj = {}
      this.outputForm.selectTable = this.batch.textArr.map((item) => {
        return {
          scopeValue: item,
          isShow: 1,
        }
      })
      //去重
      this.outputForm.selectTable = this.outputForm.selectTable.filter((item) => {
        if (obj[item.scopeValue]) {
          return false
        }
        obj[item.scopeValue] = '1'
        return true
      })
      this.outputForm.outputTable.push(
        ...this.batch.textArr.map((item) => {
          return {
            originalText: item,
            showText: item,
            isShow: 0,
          }
        })
      )
      obj = {}
      //去重
      this.outputForm.outputTable = this.outputForm.outputTable.filter((item) => {
        if (obj[item.originalText]) {
          return false
        }
        obj[item.originalText] = '1'
        return true
      })
    },
    cancelBatch() {
      this.batch.show = false
    },
    getInputDataRow(val) {
      var conts = val.split('\n')
      var num = 0
      var valArr = []
      conts.forEach((it) => {
        if (it.length > 0) {
          valArr.push(it)
          num++
        }
      })
      var obj = { num: num, value: valArr }
      return obj
    },
    autoSort() {
  // var arr = ['单位社保号','单位公积金号','姓名','国籍','证件类型','证件号码','出生日期','性别','参保时间','参保险种','公积金单位比例','公积金个人比例','补充公积金单位比例','补充公积个人比例','缴纳基数']
      var arr = ['30000003', '30000004', '10000001','10000025','10000002','10000003', '10000009','10000011','20000002', '40000011', '40000007', '40000008', '40000009', '40000010', '20000005']
      var tmpArr = []
      this.table.tableData.forEach((item, index) => {
        var sortIndex = arr.indexOf(item.dataSourceItemCode)
        if (sortIndex > -1) {
          tmpArr.push({ rowIndex: index, sortIndex: sortIndex })
        }
      })
      var tableData = []
      var arr = []
      tmpArr.forEach((item) => {
        tableData.push({ data: this.table.tableData[item.rowIndex], sortIndex: item.sortIndex })
        arr.push(item.rowIndex)
      })
      this.table.tableData = this.table.tableData.filter((item, index) => {
        return arr.indexOf(index) == -1
      })
      tableData.sort((a, b) => {
        return b.sortIndex - a.sortIndex
      })
      tableData.forEach((item) => {
        this.table.tableData.unshift(item.data)
      })
    },

  //  处理【是否显示】可否编辑
    handleIsShowDisabled(row){
      // 源【其他】，默认不勾选=隐藏，可编辑
    //   纯取值类，属性【固定隐藏】，默认不勾选=隐藏，只读
    //   源【特殊类型】，属性【固定隐藏】，默认不勾选=隐藏，只读
      if(row.dataSourceTableCode==50000){
        return false
      }
      if(row.dataSourceTableCode==70000){
        return true
      }
      let dataSourceOption2 = row.dataSourceOption2?row.dataSourceOption2:[]
      let val = row.dataSourceItemCode
      for(let i=0;i<dataSourceOption2.length;i++){
        if(dataSourceOption2[i].code===val){
          if(dataSourceOption2[i].input===0 && dataSourceOption2[i].output===1){
            return true
          }
          break
        }
      }
      return false
    },

    // 排序
    handleShowOrder (index) {
      this.$refs[`sort-${this.curShowIndex}`].doClose()
      this.curShowIndex = index
      this.newIndex = ''
      this.$refs[`sort-${index}`].doShow()
    },
    // 取消排序框
    cancelPopover (index) {
      this.$refs[`sort-${index}`].doClose()
    },
    // 确定排序框
    confirmPopover (index) {
      if (this.newIndex === '') {
        this.$message.warning('请输入新序号')
        return
      }
      if (this.newIndex > this.table.tableData.length) {
        this.$message.warning('序号不能大于列表序号最大值')
        return
      }
      let arr = this.table.tableData
      arr.splice(this.newIndex - 1, 0, ...arr.splice(index, 1))
      this.$refs[`sort-${index}`].doClose()
      this.newIndex = ''
      this.$message.success('操作成功')
    }
  },
}
</script>

<style lang="less" scoped>
.search-row {
  padding: 5px 10px;
}
.search-row /deep/.el-checkbox__label {
  line-height: 32px;
}
.table-box {
  padding: 15px 20px 20px 20px;
}
/deep/.table-header {
  background: #f5f5f5 !important;
  color: #444;
}
/deep/.el-table__cell {
  border-bottom: 1px solid #ddd;
  border-right: 1px solid #ddd;
}
/deep/.el-table__row {
  border-bottom: 1px solid #ddd;
  border-right: 1px solid #ddd;
}
/deep/.el-table--border {
  border: 1px solid #ddd;
}
/deep/.el-table .el-table__cell {
  padding: 0;
}
.control-btn {
  margin-left: 10px;
  cursor: pointer;
}
.output-content {
  text-align: center;
  width: 100%;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  padding: 0 10px;
}
.edit-icon {
  position: absolute;
  top: 50%;
  right: 10px;
  transform: translateY(-50%);
  cursor: pointer;
}
.header-box {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
  background: #3e82ff;
}
.gray-btn {
  padding: 5px 10px;
  background: #fff;
  color: #949494;
  border: 1px solid #949494;
  font-size: 12px;
  border-radius: 5px;
  cursor: pointer;
}
.footer-btn {
  position: absolute;
  // bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
}
.block {
  background: rgb(0, 102, 255);
  width: 5px;
  height: 15px;
  display: inline-block;
  margin-right: 3px;
}
// .dialog-footer{
//   // display: flex;
//   // justify-content: center;
//   // margin-top:20px;
//   // text-align: right;
// }
.upload-file-box {
  // margin-top:10px;
  input[type='text'] {
    border: 1px solid #ddd;
    border-width: 1px;
    height: 26px;
    width: 285px;
    // position: absolute;
    // left: 50%;
    // transform: translateX(-50%);
    cursor: pointer;
    border-radius: 5px;
    box-sizing: border-box;
    padding: 0 10px;
    &:focus {
      outline: none;
    }
  }
  .upload-file-btn {
    height: 30px;
    background: #3e82ff;
    color: white;
    padding: 0 10px;
    box-sizing: border-box;
    font-size: 12px;
    cursor: pointer;
    line-height: 30px;
    margin-left: 10px;
    display: inline-block;
  }
  .upload-file {
    padding: 4px 10px;
    height: 30px;
    line-height: 20px;
    cursor: pointer;
    color: #888;
    background: #fafafa;
    border: 1px solid #ddd;
    display: inline-block;
    box-sizing: border-box;
    position: absolute;
    opacity: 0;
    width: 280px;
    left: 50%;
    transform: translateX(-50%);
    display: none;
    input[type='file'] {
      position: absolute;
      font-size: 18px;
      right: 0;
      top: 0;
      opacity: 0;
      cursor: pointer;
      width: 280px;
      &:hover {
        color: #444;
        background: #eee;
        border-color: #ccc;
        text-decoration: none;
      }
    }
  }
}
.icon-query {
  position: absolute;
  right: 15px;
  cursor: pointer;
  height: 50%;
  font-size: 16px;
  top: 0;
  // transform: translateY(-50%);
}
/deep/.el-drawer__body {
  padding-bottom: 0;
}
/deep/.el-dialog__header {
  border-bottom: none !important;
}

.formDrawer-content {
  padding: 15px 30px 30px 30px;
  position: relative;
  min-height: 100%;
  .msg-tip {
    position: absolute;
    bottom: 10px;
    left: 30px;
    color: @redColor;
    background-color: #fff;
    z-index: 5;
  }
}
/deep/.condition-drawer {
  width: 700px !important;
}
/deep/.spl-filter-drawer {
  width: 620px !important;
}
.table-title {
  display: flex;
  align-items: center;
  &::before {
    content: '';
    display: inline-block;
    height: 100%;
    margin-right: 5px;
    width: 5px;
    background: #3e82ff;
  }
}

.mb-0 {
  margin-bottom: 0px !important;
}
.ellipsis {
  cursor: pointer;
  width: 95%;
  -webkit-line-clamp: 3;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 20px;
  -webkit-box-orient: vertical;
  display: -webkit-box;
}
.file-content-box {
  /deep/.el-form-item__label {
    line-height: 32px !important;
    padding-right: 0;
  }

  padding: 10px 20px;
  font-size: 12px;
  .file-list {
    display: flex;
    padding: 10px 0;
    .file {
      background: #f1f8ff;
      border-radius: 10px;
      padding: 10px;
      position: relative;
      cursor: pointer;
      margin-right: 15px;
      &:hover {
        color: #3e82ff;
        text-decoration: underline;
      }
      .file-remove {
        position: absolute;
        right: -10px;
        top: -10px;
        width: 20px;
        height: 20px;
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
  .upload-tips {
    margin-top: 20px;
    font-size: 12px;
    color: #797979;
  }
}
.upload-file-box {
  input[type='text'] {
    border: 1px solid #ddd;
    height: 28px;
    width: 250px;
    outline: none;
  }
  .upload-file-btn {
    height: 32px;
    background: #3e82ff;
    color: white;
    padding: 0 10px;
    box-sizing: border-box;
    font-size: 12px;
    cursor: pointer;
    line-height: 32px;
    margin-left: 10px;
  }
  .upload-file {
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
    input[type='file'] {
      position: absolute;
      font-size: 17px;
      right: 0;
      top: 0;
      opacity: 0;
      width: 334px;
      filter: alpha(opacity=0);
      cursor: pointer;
      &:hover {
        color: #444;
        background: #eee;
        border-color: #ccc;
        text-decoration: none;
      }
    }
  }
}
.footer-btn-box {
  text-align: center;
  margin-top: 40px;
  .footer-btn1 {
    width: 100px;
    height: 35px;
    line-height: 35px;
    padding: 0 10px;
  }
}
.my-dialog /deep/.el-dialog__header {
  padding: 10px 20px;
}
.my-dialog /deep/.el-dialog__body {
  padding: 10px 10px;
}
.my-dialog /deep/.el-form-item__content {
  line-height: 0px;
}
.table-box /deep/.el-form-item {
  margin-bottom: 0;
}
.table-box /deep/.el-form-item__error {
  position: relative;
}
.table-box /deep/.cell {
  padding: 3px 10px;
}
/deep/.el-popover--plain {
  padding: 10px !important;
}
/deep/.el-input--mini .el-input__inner {
  height: 28px !important;
  line-height: 28px !important;
}
.table-box /deep/.el-form-item__content {
  line-height: 32px;
}
/deep/.my-drawer .el-form-item__error {
  position: relative;
  text-align: left;
}
/deep/.my-drawer .el-form-item__content {
  line-height: 32px;
}
/deep/.my-drawer .el-form-item__label {
  line-height: 32px;
}
.check-sign-blue{
  display: inline-block;
  width: 18px;
  height: 18px;
  line-height: 18px;
  font-size: 10px;
  text-align: center;
  background-color: @blueColor;
  color: #ffffff;
  position: absolute;
  top: 0;
  left: 0;
}
.check-sign-gray{
  background-color: #dbdbdb;
  color: @comFontSizeColor;
}
</style>
<style>
.el-popover--plain {
  padding: 10px !important;
}
</style>
