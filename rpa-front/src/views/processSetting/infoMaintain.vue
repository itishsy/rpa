<template>
  <div class="content spl-container">
    <breadcrumb :data="pathData">
      <template slot="breadcrumb-btn">
        <el-button type="plain" style="color: #3e82ff; border-color: #3e82ff" size="mini" @click="copyArguments">复制参数</el-button>
        <el-button type="plain" style="color: #3e82ff; border-color: #3e82ff" size="mini" @click="generalArguments">配置通用参数</el-button>
        <el-button type="primary" size="mini" @click="groupDrawer.show=true;groupDrawer.isEdit = false">添加分组</el-button>
      </template>
    </breadcrumb>
    <div class="pd-20" v-if="formData&&formData.length>0">
      <div class="form-box" v-for="(item, index) in formData" :key="item.formCode" v-if="item.formCode && item.robotAppGroupVOS.length > 0">
        <p class="form-title">{{item.formName}}（ID：{{item.formCode}}）</p>
        <div class="group-item" v-for="(it, ind) in item.robotAppGroupVOS?item.robotAppGroupVOS:[]" :key="ind">
          <div class="type-title">
            <span class="text">{{it.groupName}}</span>
            <el-button type="text" class="del-btn" @click="delGroup(it,item)">删除</el-button>
            <el-button type="text" @click="changeGroup(item, it)">换表单</el-button>
          </div>
          <div>
            <table class="cust-table w-p100">
              <thead>
              <tr>
                <th style="width: 15%">字段名称</th>
                <th>字段属性</th>
                <th style="width: 20%">字段key</th>
                <th style="width: 25%">联动条件</th>
                <th style="width: 220px">操作</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="(row, rowInd) in it.argsDefineVOS?it.argsDefineVOS:[]" :key="row.fieldKey">
                <td>
                  <el-tooltip class="item" effect="dark" :content="row.fieldName" placement="top-start">
                    <span>{{row.fieldName}}</span>
                  </el-tooltip>
                </td>
                <td>
                  <el-tooltip class="item" effect="dark" :content="checkDropList(row)" placement="top-start">
                    <span>{{ checkDropList(row) }}</span>
                  </el-tooltip>
                </td>
                <td>
                  <el-tooltip class="item" effect="dark" :content="row.fieldKey" placement="top-start">
                    <span>{{row.fieldKey}}</span>
                  </el-tooltip>
                </td>
                <td>
                  <el-tooltip class="item" effect="dark" :content="row.cond" placement="top-start">
                    <span>{{row.cond}}</span>
                  </el-tooltip>
                </td>
                <td>
                  <el-button type="text" class="del-btn" @click="delGroupRow(row,item,ind)">删除</el-button>
                  <el-button type="text" @click="showAddCondition(it,it.argsDefineVOS,rowInd)">添加条件</el-button>
                  <el-button type="text" @click="doAddEditField('edit',item,it,rowInd)">编辑</el-button>
                  <el-button type="text" v-show="rowInd !== it.argsDefineVOS.length-1" @click="doMove(row, 'down')">下移</el-button>
                  <el-button type="text" v-show="rowInd!==0" @click="doMove(row, 'up')">上挪</el-button>
                </td>
              </tr>
              </tbody>
            </table>
            <div class="text-center mt-20">
              <el-button type="text" icon="el-icon-plus" @click="doAddEditField('add',item,it)">自定义信息</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="pd-20" v-else>
      <el-empty description="暂无配置信息"></el-empty>
    </div>

    <!-- 维护表单,增加表单 -->
    <el-drawer title="维护表单" :visible.sync="formDrawer.show" :wrapperClosable="false" custom-class="spl-filter-drawer" :before-close="cancelFormDrawer" :close-on-press-escape="false">
      <div class="formDrawer-content">
        <el-form :model="formDrawer" ref="formDrawer" label-width="0" :rules="rules">
          <el-form-item label="" prop="">
            <p class="item-lab required-pre clearfix">应用名称<span class="text-gray font-12 ml-15">维护此应用类型的公共表单</span></p>
            <el-input v-model="appType" disabled></el-input>
          </el-form-item>
          <el-form-item label="" prop="inputName">
            <p class="item-lab required-pre">表单名称</p>
            <el-autocomplete
              class="w-p100"
              v-model="formDrawer.inputName"
              :fetch-suggestions="querySearchFormList"
              placeholder="请输入"
              @select="handleSelectFormName" @blur="handleBlurFormName">
              <template slot-scope="{ item }">
                <div class="name" v-if="item.isNew">{{ item.formName }}<span class="text-gray">（新）</span></div>
                <div class="name" v-else>{{ item.formName }}</div>
              </template>
            </el-autocomplete>
          </el-form-item>
          <el-form-item label="" prop="inputId">
            <p class="item-lab required-pre">表单ID<span class="text-gray font-12 ml-15">后台唯一标识，便于前端根据ID取分组内容</span></p>
            <el-input v-model.trim="formDrawer.inputId" :disabled="!formDrawer.isAdd" placeholder="请输入" clearable></el-input>
          </el-form-item>
        </el-form>
      </div>

      <div class="drawer-footer-buts">
        <el-button class="btn--border-blue s-btn" @click="cancelFormDrawer">取消</el-button>
        <el-button type="primary" class="s-btn ml-12" :loading="formDrawer.btnLoading" @click="confirmFormDrawer">确定</el-button>
      </div>
    </el-drawer>

    <!-- 维护分组，改分组名 -->
    <el-drawer title="维护分组" :visible.sync="formDrawer.show2" :wrapperClosable="false" custom-class="spl-filter-drawer" :before-close="cancelFormDrawer" :close-on-press-escape="false">
      <div class="formDrawer-content">
        <el-form :model="formDrawer" ref="formDrawer2" label-width="0">
          <el-form-item label="" prop="">
            <p class="item-lab required-pre clearfix">应用名称<span class="text-gray font-12 ml-15">维护此应用类型的公共类型</span></p>
            <el-input v-model="appType" disabled></el-input>
          </el-form-item>
          <p class="item-lab required-pre clearfix">已有分组<span class="text-gray font-12 ml-15">点击分组改名</span></p>
          <div class="formName-list-box">
            <div class="formName-list" v-for="(item,index) in allGroupList" :key="item.groupName" @click="editFormName(item,index)" v-if="!item.edit">
              {{item.groupName}}
            </div>
            <el-form-item label="" prop="formGroupName" v-else :rules="rules.formGroupName" style="width:140px;margin-left:10px;">
              <el-input v-model.trim="formDrawer.formGroupName"  @blur="changeFormName(item,index)" ref="formGroupName"></el-input>
            </el-form-item>
          </div>
        </el-form>
      </div>

      <div class="drawer-footer-buts">
        <el-button class="btn--border-blue s-btn" @click="cancelFormDrawer">取消</el-button>
        <!-- <el-button type="primary" class="s-btn ml-12" :loading="formDrawer.btnLoading" @click="confirmFormDrawer">确定</el-button> -->
      </div>
    </el-drawer>

    <!-- 添加分组\编辑分组 -->
    <el-drawer :title="'添加分组'" :visible.sync="groupDrawer.show" :wrapperClosable="false" custom-class="spl-filter-drawer" :before-close="cancelGroupDrawer" :close-on-press-escape="false">
      <div class="formDrawer-content">
        <el-form :model="groupDrawer" ref="groupDrawer" label-width="0">
          <el-form-item label="" prop="">
            <p class="item-lab required-pre clearfix">应用名称</p>
            <el-input v-model="appType" disabled></el-input>
          </el-form-item>
          <el-form-item label="" prop="inputId" :rules="[{ required: true, message: '请输入所在表单',trigger: ['blur']}]">
            <p class="item-lab required-pre clearfix">所在表单 <span class="text-blue fr f-cursor mr-5" @click="formDrawer.show=true">查无？点我</span></p>
            <el-select placeholder="请选择" v-model="groupDrawer.inputId" clearable filterable class="w-p100">
              <el-option v-for="item in allFormList" :key="item.id" :label="item.formName" :value="item.id">
                <span style="display: inline-block;min-width: 120px;margin-right: 10px;">{{ item.formName }}</span>
                <span>ID：{{ item.formCode }}</span>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="" prop="groupName" :rules="rules.groupName">
            <p class="item-lab required-pre">分组名称 <span class="text-blue fr f-cursor mr-5" @click="formDrawer.show2=true">想改名？点我</span></p>
            <el-autocomplete
              class="w-p100"
              v-model.trim="groupDrawer.groupName"
              :fetch-suggestions="querySearchGroupName"
              placeholder="请输入"
              @select="handleSelectGroupName" @blur="handleBlurGroupName">
              <template slot-scope="{ item }">
                <div class="name" v-if="item.isNew">{{ item.groupName }}<span class="text-gray">（新）</span></div>
                <div class="name" v-else>{{ item.groupName }}</div>
              </template>
            </el-autocomplete>
          </el-form-item>
        </el-form>
      </div>

      <div class="drawer-footer-buts">
        <el-button class="btn--border-blue s-btn" @click="cancelGroupDrawer">取消</el-button>
        <el-button type="primary" class="s-btn ml-12" :loading="groupDrawer.btnLoading" @click="confirmGroupDrawer">确定</el-button>
      </div>
    </el-drawer>

    <!-- 换表单 -->
    <el-drawer :title="'换表单'" :visible.sync="groupDrawer.show2" :wrapperClosable="false" custom-class="spl-filter-drawer" :before-close="cancelChangeGroupDrawer" :close-on-press-escape="false">
      <div class="formDrawer-content">
        <el-form :model="groupDrawer" ref="groupDrawer2" label-width="0">
          <!-- <el-form-item label="" prop="">
            <p class="item-lab required-pre clearfix">应用名称</p>
            <el-input v-model="appType" disabled></el-input>
          </el-form-item> -->
          <el-form-item label="" prop="inputId" :rules="[{ required: true, message: '请输入所在表单',trigger: ['blur']}]">
            <p class="item-lab required-pre clearfix">所在表单 <span class="text-gray font-12 ml-15">可将分组更换到其他表单</span></p>
            <el-select placeholder="请选择" v-model="groupDrawer.inputId" clearable filterable class="w-p100">
              <el-option v-for="item in allFormList" :key="item.id" :label="item.formName" :value="item.id">
                <span style="display: inline-block;min-width: 120px;margin-right: 10px;">{{ item.formName }}</span>
                <span>ID：{{ item.formCode }}</span>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="" prop="groupName" :rules="[{ required: true, message: '请输入分组名称',trigger: ['blur', 'change']}]">
            <p class="item-lab required-pre">分组名称 </p>
            <el-input disabled v-model="groupDrawer.groupName"></el-input>
          </el-form-item>
        </el-form>
      </div>

      <div class="drawer-footer-buts">
        <el-button class="btn--border-blue s-btn" @click="cancelChangeGroupDrawer">取消</el-button>
        <el-button type="primary" class="s-btn ml-12" :loading="groupDrawer.btnLoading" @click="changeGroupDrawer">确定</el-button>
      </div>
    </el-drawer>

    <!-- 添加\编辑字段信息 -->
    <el-drawer title="添加字段信息" :visible.sync="fieldDrawer.show" :wrapperClosable="false" custom-class="spl-filter-drawer" :before-close="cancelFieldDrawer" :close-on-press-escape="false">
      <div class="formDrawer-content">
        <el-form :model="fieldDrawer" ref="fieldDrawer" label-width="0">
          <el-form-item label="" prop="groupName">
            <p class="item-lab required-pre">字段分组</p>
            <el-input v-model.trim="fieldDrawer.groupName" placeholder="请输入" readonly></el-input>
            <p class="text-606 text-right" style="line-height: normal">字段按该分组归类显示</p>
          </el-form-item>
          <el-form-item label="" prop="fieldName" :rules="rules.fieldName">
            <p class="item-lab required-pre clearfix">字段名称</p>
            <el-autocomplete
              class="inline-input"
              v-model.trim="fieldDrawer.fieldName"
              :fetch-suggestions="querySearch"
              placeholder="请输入"
              style="width:100%"
              value-key="fieldName"
              @select="changeFieldDrawerFieldName"
            ></el-autocomplete>
            <!-- <el-input v-model.trim="fieldDrawer.fieldName" placeholder="请输入" clearable></el-input> -->
          </el-form-item>
          <el-form-item label="" prop="fieldType" :rules="[{ required: true, message: '请选择字段属性',trigger: ['change']}]">
            <p class="item-lab required-pre clearfix">字段属性</p>
            <el-select placeholder="请选择" v-model="fieldDrawer.fieldType" clearable filterable class="w-p100" @change="changefieldType">
              <el-option v-for="item in selectData" :key="item.dictCode" :label="item.dictName" :value="item.dictCode"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="" prop="dropDownOption" :rules="rules.dropDownOption" v-if="columnAttributeType('dropDownOption')">
            <p class="item-lab required-pre clearfix">下拉选项（值,号分隔）</p>
            <el-input v-model.trim="fieldDrawer.dropDownOption" placeholder="请输入" clearable></el-input>
          </el-form-item>
          <el-form-item label="" prop="quantity" :rules="rules.quantity" v-if="columnAttributeType('quantity')">
            <p class="item-lab required-pre clearfix">份数</p>
            <el-input v-model="fieldDrawer.quantity" placeholder="请输入" clearable></el-input>
          </el-form-item>
          <el-form-item label="" prop="comment" :rules="rules.comment">
            <p class="item-lab required-pre clearfix">字段注释</p>
            <el-input v-model.trim="fieldDrawer.comment" placeholder="请输入" clearable></el-input>
          </el-form-item>
          <el-form-item label="" prop="fieldKey" :rules="rules.fieldKey">
            <p class="item-lab required-pre clearfix">字段key</p>
            <el-input v-model.trim="fieldDrawer.fieldKey" placeholder="请输入" clearable></el-input>
          </el-form-item>
          <el-form-item label="" prop="required">
            <p class="item-lab required-pre clearfix">是否必填</p>
            <!--是否必填0:必填；1:非必填-->
            <el-checkbox v-model="fieldDrawer.required" :true-label="0" :false-label="1">是</el-checkbox>
          </el-form-item>
        </el-form>
      </div>

      <div class="drawer-footer-buts">
        <el-button class="btn--border-blue s-btn" @click="cancelFieldDrawer">取消</el-button>
        <el-button type="primary" class="s-btn ml-12" @click="confirmFieldDrawer">确定</el-button>
      </div>
    </el-drawer>

    <!-- 添加条件 -->
    <el-drawer title="添加条件" :visible.sync="conditionDrawer.show" :wrapperClosable="false" custom-class="spl-filter-drawer condition-drawer" :before-close="cancelConditionDrawer" :close-on-press-escape="false">
      <div class="formDrawer-content">
        <el-form :model="conditionDrawer" ref="conditionDrawer" label-width="0">
          <span class="item-lab required-pre">关系：</span>
          <el-form-item label="" class="inlineBlock w-150" prop="relation" :rules="[{ required: true, message: '请选择关系',trigger: ['change']}]">
            <el-select placeholder="请选择" v-model="conditionDrawer.relation">
              <el-option label="或者" value="或者"></el-option>
              <el-option label="并且" value="并且"></el-option>
            </el-select>
          </el-form-item>
          <div class="display-flex" v-for="(item, index) in conditionDrawer.list" :key="index">
            <div class="w-200 display-flex">
              <span class="required-pre" style="line-height: 40px;">条件：</span>
              <el-form-item class="flex1" label="" :prop="'list.'+index+'.conditionLeft'" :rules="rules.conditionLeft">
                <el-select placeholder="请选择" v-model="item.conditionLeft" clearable filterable @change="changeFieldName($event,index)" >
                  <el-option :label="item.fieldName" :value="item.fieldName" v-for="(item,index) in conditionList" :key="index" ></el-option>
                </el-select>
              </el-form-item>
            </div>
            <div class="w-100 ml-10">
              <el-form-item label="" :prop="'list.'+index+'.logicalOperator'" :rules="[{ required: true, message: '请选择',trigger: ['change']}]">
                <el-select placeholder="请选择" v-model="item.logicalOperator" clearable filterable>
                  <el-option label="等于" value="等于"></el-option>
                  <el-option label="不等于" value="不等于"></el-option>
                  <el-option label="包含" value="包含"></el-option>
                  <el-option label="不包含" value="不包含"></el-option>
                </el-select>
              </el-form-item>
            </div>

            <div class="flex1 ml-10">
              <el-form-item label="" :prop="'list.'+index+'.conditionRight'" :rules="rules.conditionRight" >
                <el-input v-model.trim="item.conditionRight" placeholder="请输入" clearable @change="chagneConditionRight"></el-input>
              </el-form-item>
            </div>

            <span style="padding-top: 12px;width: 65px">
              <i class="el-icon-close f-cursor font-20 text-red ml-10 fw-blod" v-if="conditionDrawer.list.length>1" @click="delCondition(index)"></i>
              <i class="el-icon-plus f-cursor font-20 text-green ml-10 fw-blod" v-if="index === conditionDrawer.list.length-1" @click="addCondition()"></i>
            </span>
          </div>

          <div class="type-title">
            <span class="text">结果</span>
          </div>
          <div style="padding: 20px 10px;">
            <table class="cust-table w-p100">
              <thead>
              <tr>
                <th style="width: 50%">字段名称</th>
                <th>显隐性</th>
              </tr>
              </thead>
              <tbody>
              <tr>
                <td>{{conditionDrawer.fieldName}}</td>
                <td>
                  <el-form-item label="" prop="showOrHidden" :rules="rules.showOrHidden">
                    <el-radio-group v-model="conditionDrawer.showOrHidden">
                      <el-radio :label="0" style="margin-right: 25px;">显示</el-radio>
                      <el-radio :label="1" style="margin-right: 25px;">隐藏</el-radio>
                    </el-radio-group>
                  </el-form-item>
                </td>
              </tr>
              </tbody>
            </table>
          </div>
        </el-form>
        <div class="msg-tip" v-if="fieldError.length > 0">
          <p>出现错误：</p>
          <p v-for="(item,index) in fieldError" :key="index">{{item.label}}</p>
        </div>
      </div>
      <div class="drawer-footer-buts">
        <el-button class="btn--border-blue s-btn" @click="cancelConditionDrawer">取消</el-button>
        <el-button type="primary" class="s-btn ml-12" @click="confirmConditionDrawer">确定</el-button>
      </div>
    </el-drawer>

    <!-- 复制参数 -->
    <el-drawer title="复制参数" :visible.sync="copyArgumentsData.show" custom-class="spl-filter-drawer"
      :wrapperClosable="false" :show-close="true" :before-close="cancelCopy">
      <p style="background: #fff6f6;color:#ff7844;height: 40px;line-height: 40px;padding: 0 10px;">一旦确定，将清空并替换当前已存在字段</p>
      <div style="padding:0  20px;">
        <el-form :model="copyArgumentsData" ref="copyArguments">
          <el-form-item label="当前应用：" class="column" :rules="[
            { required: true, message: '选择', trigger: 'change' }]">
            <p style="padding:0 20px">{{ appInfo.appName }}</p>
          </el-form-item>
          <el-form-item label="选择同配置的地区应用：" prop="addrName" class="column"  :rules="[
            { required: true, message: '请选择同配置的地区应用', trigger: 'change' }]">
            <!-- <addrSelector v-model="copyArgumentsData.addrName" :addrArr="allAddr"  @changeAddrSelect="changeAddrSelect"></addrSelector> -->
            <el-select placeholder="请选择" v-model="copyArgumentsData.addrName" clearable filterable @change="changeAddrSelect">
              <el-option :label="item.appName" :value="item.addrName" v-for="(item,index) in appList" :key="index"></el-option>
            </el-select>
          </el-form-item>
        </el-form>

        <div class="drawer-footer-buts">
          <el-button class="btn--border-blue s-btn" @click="cancelCopy">取消</el-button>
          <el-button type="primary" class="s-btn ml-12" @click="confirmCopy">确定</el-button>
        </div>
      </div>
    </el-drawer>
    <!-- 配置通用参数 -->
    <el-drawer title="配置通用参数" :visible.sync="generalArgumentsData.show" custom-class="spl-filter-drawer"
      :close-on-click-modal="false" :show-close="true" :before-close="cancelGeneralArguments">
      <div style="padding:0  20px;">
        <div class="checkList-column" v-for="(item,index) in generalArgumentsData.list" :key="index">
          <p style="color: #383838;font-size: 16px;padding: 15px 0;font-weight: bold;">{{item.formName + `（ID:${item.formCode}）`}}</p>
          <div v-for="(item1,index) in item.robotAppGroupVOS" :key="index">
            <el-checkbox :label="item1.groupName" v-model="item1.groupNameSet" @change="changeArgumentsGrounpName($event,item1.groupName)">{{ item1.groupName }}</el-checkbox>
            <div style="display:flex;flex-direction:column;padding:0 20px;">
              <el-checkbox :label="item2.fieldName" v-for="(item2,index) in item1.argsDefineVOS" :key="index" v-model="item2.check" @change="changeArgumentsCheck($event,item1.argsDefineVOS,item1.groupName)">
                {{ item2.fieldName + `（${item2.fieldKey}）` }}
              </el-checkbox>
            </div>
          </div>
        </div>

        <div class="drawer-footer-buts">
          <div class="error">{{generalArgumentsData.error ? '请至少勾选一个参数' : ''}}</div>
          <el-button class="btn--border-blue s-btn" @click="cancelGeneralArguments">取消</el-button>
          <el-button type="primary" class="s-btn ml-12" @click="confirmGeneralArguments">确定</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import addrSelector from '../../components/common/addrSelector'
export default {
  components: {
    addrSelector
  },
  data () {
    return {
      pathData: [],
      appCode: '', // 应用code
      appType: '', // 应用类型，申报机器人，财务机器人
      appInfo: {}, // 应用详情
      formData: [], // 主页渲染数据
      // 表单详情
      formDrawer: {
        show: false,
        show2: false,
        inputName: '',
        inputNameTemp: '',
        inputId: '',
        isAdd: true,
        btnLoading: false,
        formGroupName: ''
      },
      // 分组详情
      groupDrawer: {
        show: false,
        show2: false,
        groupNameTemp: '', // 记录上一次输入的分组名称，用于对比是否修改
        groupName: '',
        groupId: '',
        inputId: '',
        isEdit: false,
        btnLoading: false,
        oldInputId: ''
      },
      // 字段详情
      fieldDrawer: {
        show: false,
        isEdit: false,
        btnLoading: false,
        fieldType: '', // 字段属性
        fieldKey: '', // 字段key
        cond: '', // 联动条件
        groupId: '', // 分组id
        groupName: '', // 分组名称
        fieldName: '', // 字段名称
        dropDownOption: '', // 下拉选项
        quantity: '', // 图片上传，附件上传总数
        comment: '', // 字段注释
        required: 1, // 是否必填0:必填；1:非必填
        item: [],
        formData: {}
      },
      fieldError: [],
      // 条件详情
      conditionDrawer: {
        show: false,
        list: [{ conditionLeft: '', conditionRight: '', logicalOperator: '' }],
        showOrHidden: 0,
	      argsDefineId: '',
	      relation: '',
        condition: ''
      },
      copyArgumentsData: {
        show: false,
        addrId: '',
        addrName: '',
        error: ''
      },
      generalArgumentsData: {
        list: [],
        show: false,
        error: false
      },
      allAddr: [],
      // 字段添加，字段属性
      selectData: [],
      allFormList: [], // 系统该应用类型所有表单
      allGroupList: [], // 该应用类型所有分组数据
      conditionList: [],
      rules: {
        inputName: [
          { required: true, message: '请输入表单名称', trigger: ['change', 'change'] },
          { validator: this.checkInputName, trigger: 'change' }
        ],
        inputId: [
          { required: true, message: '请输入表单ID', trigger: ['change'] },
          { validator: this.checkRepeatKey, trigger: 'change' }
        ],
        conditionLeft: [
          { required: true, message: '请选择', trigger: ['change'] },
          { validator: this.checkRepeatConditionLeft, trigger: 'change' }
        ],
        conditionRight: [
          { validator: this.checkRepeatCondition, trigger: 'change' },
          { required: true, message: '请输入条件值', trigger: 'change' }
        ],
        fieldName: [
          { required: true, message: '请输入字段名称', trigger: 'change' },
          { validator: this.checkRepeatFieldName, trigger: 'change' }
        ],
        fieldKey: [
          { required: true, message: '请输入字段key', trigger: 'change' },
          { validator: this.checkRepeatFieldkey, trigger: 'change' }
        ],
        showOrHidden: [
          { required: true, message: '请选择显隐性', trigger: 'change' }
        ],
        quantity: [
          { validator: this.checkNumber, trigger: 'change' }
        ],
        groupName: [{ required: true, message: '请输入分组名称', trigger: 'change' }, { validator: this.checkGroupName, trigger: 'change' }],
        formGroupName: [
          { required: true, message: '请输入分组名称', trigger: 'change' },
          { validator: this.checkFormGroupName, trigger: 'change' }
        ],
        comment: [
          { required: true, message: '请输入字段注释', trigger: ['blur'] },
          { validator: this.checkComment, trigger: 'change' }
        ],
        dropDownOption: [
          { required: true, message: '请输入下拉选项', trigger: ['blur'] },
          { validator: this.checkDropDownOption, trigger: 'change' }
        ]
      },
      loading: null,
      businessType: '',
      timer: null,
      appList: []
    }
  },
  computed: {
    columnAttributeType () {
      return function (type) {
        var quantityType = ['photoUpload', 'fileUpload']
        var dropDownOptionType = ['singleDropList', 'multipleDropList', 'single', 'multiple']
        if (type == 'quantity' && quantityType.indexOf(this.fieldDrawer.fieldType) > -1) {
          return true
        }
        if (type == 'dropDownOption' && dropDownOptionType.indexOf(this.fieldDrawer.fieldType) > -1) {
          return true
        }
        return false
      }
    },
    checkDropList () {
      return function (row) {
        if (['singleDropList', 'multipleDropList'].indexOf(row.fieldType) > -1) {
          return `${this.getfieldKeyName(row.fieldType)}：${row.dropDownOption}`
        }
        return this.getfieldKeyName(row.fieldType)
      }
    },
    getfieldKeyName () {
      return function (type) {
        var str = ''
        this.selectData.forEach(item => {
          if (item.dictCode == type) {
            str = item.dictName
          }
        })
        return str
      }
    }
  },
  async created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({ name: '信息配置' })
    this.appCode = this.$route.query.appCode
    await this.getAppInfo() // 获取应用详情
    this.getInfoData() // 获取配置信息
    this.getAllGroupList() // 获取系统该应用类型所有分组数据
    this.getDictByKey('1016')
    this.getAddr()
    if (this.$route.query.businessType) {
      this.businessType = this.$route.query.businessType
    }
    this.getGetGeneralArgs()
    this.getAppData()
  },
  watch: {},
  mounted () {},
  methods: {
    checkDropDownOption (rule, value, callback) {
      if (value && value.length > 1000) {
        return callback(new Error('下拉选项不得超过1000字'))
      }
      return callback()
    },
    checkComment (rule, value, callback) {
      if (value && value.length > 100) {
        return callback(new Error('字符注释不得超过100字'))
      }
      return callback()
    },
    checkFormGroupName (rule, value, callback) {
      if (value.trim() && value.trim().length > 100) {
        return callback(new Error('分组名称不能超过100字'))
      }
      return callback()
    },
    // 表单名称不得超过100字符
    checkInputName (rule, value, callback) {
      if (value && value.length > 100) {
        return callback(new Error('表单名称不得超过100字'))
      }
      return callback()
    },
    // 分组名称不得超过100字符
    checkGroupName (rule, value, callback) {
      if (value.trim() && value.trim().length > 100) {
        return callback(new Error('分组名称不得超过100字'))
      }
      return callback()
    },
    checkNumber (rule, value, callback) {
      var reg = /^[1-9]d*$/g
      if (!value) {
        return callback(new Error('请输入份数'))
      }
      if (!reg.test(value)) {
        return callback(new Error('请输入>0的整数'))
      }
      return callback()
    },
    // 检测字段是否交叉引用
    checkRepeatConditionLeft (rule, value, callback, key) {
      var check = false
      var str = ''
      var index = Object.keys(key)[0]
      var num = index.split('.')[1]
      for (let index = 0; index < this.conditionList.length; index++) {
        var item = this.conditionList[index]
        if (item.item.conditionList <= 0) {
          continue
        }
        if (item.fieldName == value) {
          item.item.conditionList.forEach(item1 => {
            if (item1.conditionLeft == this.conditionDrawer.fieldName) {
              str = item.fieldName
              check = true
            }
          })
        }
      }
      if (check) {
        // this.$set(this.fieldError,index,`字段${str}与${this.conditionDrawer.fieldName}互为联动条件，请更正`)
        this.fieldError.push({
          key: index,
          label: `第${num}行，字段${str}与${this.conditionDrawer.fieldName}互为联动条件，请更正`
        })
        var temp = {}
        this.fieldError = this.fieldError.reduce((pre, cur) => {
          console.log(pre, cur)
          if (!temp[cur.key]) {
            pre.push(cur)
            temp[cur.key] = true
          }
          return pre
        }, [])
        // this.fieldError[str] = `字段${str}与${this.conditionDrawer.fieldName}互为联动条件，请更正`
        return callback(new Error(` `))
      } else {
        this.fieldError = this.fieldError.filter(item => {
          return item.key != index
        })
        // this.$set(this.fieldError,index,'')
        console.log(this.fieldError)
      }
      return callback()
    },
    changeFieldName (val, index) {
      this.$refs.conditionDrawer.validate()
    },
    // 改变条件值，校验表单
    chagneConditionRight () {
      this.conditionDrawer.list.forEach((item, index) => {
        this.$refs.conditionDrawer.validateField('list.' + index + '.conditionRight')
      })
    },
    // 检测字段key是否重复
    checkRepeatFieldkey (rule, value, callback) {
      if (value.length > 100) {
        return callback(new Error('字段Key不得超过100个字符'))
      }
      var check = false
      // var check1 = false
      // var reg = /^[a-zA-Z_][a-zA-Z0-9_]*/m
      // console.log(res.test(value),value)
      // if(!reg.test(value)){
      //   check1 = true
      // }
      // if(check1){
      //   return callback(new Error('不得使用中文,特殊符号,开头为数字命名。如：input123可取，123input不可取'))
      // }
      this.fieldDrawer.formData.robotAppGroupVOS.forEach(item => {
        item.argsDefineVOS.forEach(item1 => {
          if (item1.fieldKey == value && !item1.self) {
            check = true
          }
        })
      })
      console.log(this.fieldDrawer)
      if (check) {
        return callback(new Error('此表单存在此字段key，请更正'))
      }
      return callback()
    },
    // 检测字段名称是否重复，和是否超过100字符
    checkRepeatFieldName (rule, value, callback) {
      if (value && value.length > 100) {
        return callback(new Error('字段名称不能超过100字'))
      }
      var check = false
      this.fieldDrawer.item.forEach(item => {
        if (item.fieldName == value && !item.self) {
          check = true
        }
      })
      if (check) {
        return callback(new Error('字段重复，请更正'))
      }
      return callback()
    },
    // 检测条件值是否重复
    checkRepeatCondition (rule, value, callback, key) {
      if (!value) {
        return callback()
      }
      if (value.trim().length > 100) {
        return callback(new Error('条件值不得大于100字符'))
      }
      var arr = []
      var index = Object.keys(key)[0].split('.')[1]
      var selfArr = this.conditionDrawer.list.filter((item) => {
        return item.conditionLeft == this.conditionDrawer.list[index].conditionLeft
      })
      selfArr.forEach(item => {
        if (item.conditionRight == value && selfArr.length > 1 && item.conditionLeft) {
          arr.push(1)
        }
      })
      if (arr.length >= 2) {
        return callback(new Error('字段条件值出现重复，请先更正'))
      }
      return callback()
    },
    // 分组名字 失焦时
    changeFormName (item, index) {
      console.log(item)
      this.$refs.formDrawer2.validateField('formGroupName', (valid) => {
        if (!valid) {
          if (item.groupName != this.formDrawer.formGroupName) {
            this.$confirm('确定更换分组名字吗？', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              showClose: false,
              customClass: 'pal-confirm',
              type: 'warning'
            }).then(() => {
              console.log('确定更换')
              this.showLoading()
              this.$http({
                url: '/robot/appConfig/editGroupInfo',
                method: 'post',
                data: {
                  robotType: this.appType,
                  groupName: this.formDrawer.formGroupName,
                  appCode: this.appCode,
                  groupId: item.id
                }
              }).then(({ data }) => {
                this.closeLoading()
                if (data && data.code == 200) {
                  this.formDrawer.formGroupName = ''
                  this.allGroupList.forEach(item => {
                    item.edit = false
                  })
                  this.$message.success(data.message ? data.message : '操作成功')
                }
                this.getAllGroupList()
                this.getAppInfo()
                this.getInfoData()
              }).catch((err) => {
                this.allGroupList.forEach(item => {
                  item.edit = false
                })
                this.formDrawer.formGroupName = ''
                this.closeLoading()
              })
            }).catch(() => {
              console.log('取消更换')
              this.formDrawer.formGroupName = ''
              this.allGroupList.forEach(item => {
                item.edit = false
              })
            })
          } else {
            this.$set(this.allGroupList, index, { ...item, edit: false })
          }
        }
      })
    },
    // 编辑分组名字
    editFormName (item, index) {
      this.allGroupList.forEach(item => {
        item.edit = false
      })
      this.$set(this.allGroupList, index, { ...item, edit: true })
      this.formDrawer.formGroupName = item.groupName
      this.$nextTick(item => {
        this.$refs.formGroupName.focus()
      })
    },
    // 获取应用详情
    async getAppInfo () {
      let that = this
      this.showLoading()
      await this.$http({
        url: '/robot/app/page',
        method: 'post',
        data: {
          query: [
            {
              property: 'appCode',
              value: this.appCode
            }
          ],
          page: 1,
          size: 1,
          start: 0
        }
      }).then(({ data }) => {
        if (data.data.rows.length > 0) {
          that.appInfo = data.data.rows[0]
          that.appType = that.appInfo.robotName
          that.pathData[that.pathData.length - 1].name = '信息配置 （' + that.appInfo.appName + '）'
          that.getAllFormList() // 获取系统该应用类型所有表单
        } else {
          that.$message.error('未查询到该应用')
        }
        this.closeLoading()
      }).catch(() => {
        this.closeLoading()
      })
    },
    // 获取配置信息
    getInfoData () {
      let that = this
      this.showLoading()
      this.$http({
        url: '/robot/appConfig/getRobotConfigInfoById/' + this.appCode,
        method: 'post',
        data: { robotType: this.appType }
      }).then(({ data }) => {
        that.formData = data.data ? data.data : []
        this.closeLoading()
      }).catch(() => {
        this.closeLoading()
      })
    },
    // 获取系统该应用类型所有表单
    getAllFormList () {
      let that = this
      this.$http({
        url: '/robot/appConfig/getInputInfo',
        method: 'post',
        data: { robotType: this.appType }
      }).then(({ data }) => {
        that.allFormList = data.data ? data.data : []
      }).catch(() => {
      })
    },
    // 获取系统该应用类型所有分组数据
    getAllGroupList () {
      let that = this
      this.$http({
        url: '/robot/appConfig/getGroupInfo',
        method: 'post',
        params: { robotType: this.appType }
      }).then(({ data }) => {
        that.allGroupList = data.data ? data.data : []
        if (that.allGroupList.length > 0) {
          that.allGroupList.forEach(item => {
            item.edit = false
          })
        }
      }).catch(() => {
      })
    },
    // 换表单，弹窗
    changeGroup (item, it) {
      console.log(item, it)
      this.groupDrawer.formCode = item.formCode
      this.groupDrawer.groupId = it.id
      this.groupDrawer.groupName = it.groupName
      this.groupDrawer.groupNameTemp = it.groupName
      this.groupDrawer.oldInputId = item.id
      this.groupDrawer.isEdit = true
      this.groupDrawer.show2 = true
    },
    // 换表单确定
    changeGroupDrawer () {
      this.$refs.groupDrawer2.validate(valid => {
        if (valid) {
          this.showLoading()
          var that = this
          this.$http({
            url: 'robot/appConfig/changeGroupToOtherForm?formId=' + this.groupDrawer.inputId,
            method: 'post',
            data: {
              appCode: this.appCode,
              formId: this.groupDrawer.oldInputId,
              groupId: this.groupDrawer.groupId
            }
          }).then(({ data }) => {
            that.$message.success('操作成功')
            that.getInfoData()
            that.getAllGroupList()
            this.closeLoading()
            this.cancelChangeGroupDrawer()
          }).catch(() => {
            this.closeLoading()
          })
        }
      })
    },
    // 换表单取消
    cancelChangeGroupDrawer () {
      this.groupDrawer = this.$options.data().groupDrawer
      this.$refs.groupDrawer2.resetFields()
    },
    // 删除分组
    delGroup (item, item1) {
      let that = this
      console.log(item, item1)
      if (item.argsDefineVOS.length > 0) {
        this.$message.error('此分组下存在字段，不能删除')
        return
      }
      this.$confirm('是否确定删除该分组{' + item.groupName + '}？', '提示', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        that.$http({
          url: '/robot/appConfig/deleteGroupInfo',
          method: 'post',
          data: {
            appCode: this.appCode,
            formId: item1.id,
            groupId: item.id
          }
        }).then(({ data }) => {
          that.$message.success('操作成功')
          that.getInfoData()
        }).catch(() => {
        })
      }).catch(() => {})
    },
    // 添加表单时，检测时否重复的key
    checkRepeatKey (rule, value, callback) {
      if (value.trim().length > 100) {
        return callback(new Error('表单ID不得大于100个字符'))
      }
      if (this.formDrawer.isAdd == false) {
        return callback()
      }
      var check = false
      var name = ''
      this.allFormList.forEach(item => {
        if (item.formCode == value) {
          check = true
          name = item.inputName
        }
      })
      if (check) {
        return callback(new Error('表单ID为唯一，不可和其他表单重复'))
      }
      return callback()
    },
    // 删除分组字段
    delGroupRow (data, group, index) {
      let that = this
      console.log(group, index, data.id)
      // 删除的是否，需要校验是否有其他引用此字段的条件
      var check = false
      var str = ''
      group.robotAppGroupVOS[index].argsDefineVOS.forEach(item => {
        item.conditionList.forEach(item1 => {
          if (item1.conditionLeft == data.fieldName) {
            check = true
            str = item.fieldName
          }
        })
      })
      if (check) {
        this.$message.error(`此字段被[${str}]字段引用条件，不可删除`)
        return
      }
      this.$confirm('是否确定删除该字段？', '提示', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        that.$http({
          url: '/robot/appConfig/deleteGroupItemById/' + data.id,
          method: 'post'
        }).then(({ data }) => {
          that.$message.success('操作成功')
          that.getInfoData()
        })
          .that.getInfoData()
      }).catch(() => {})
    },
    // 维护表单-搜索表单名称
    querySearchFormList (queryString, cb) {
      var results = queryString ? this.createFilterFormList(queryString) : this.allFormList
      // 调用 callback 返回建议列表的数据
      cb(results)
    },
    createFilterFormList (queryString) {
      let res = []
      let isSame = false
      this.allFormList.map(item => {
        console.log(item, item.formName.indexOf(queryString) > -1)
        if (item.formName && item.formName.indexOf(queryString) > -1) {
          if (item.formName === queryString) {
            isSame = true
            res.unshift(item)
          } else {
            res.push(item)
          }
        }
      })
      // console.log(res)
      if (!isSame) {
        res.unshift({ formName: queryString, isNew: true })
      }
      return res
    },
    // 维护表单-选择表单名称
    handleSelectFormName (res) {
      console.log(res)
      this.formDrawer.inputName = res.formName
      this.formDrawer.inputNameTemp = res.formName
      if (res.isNew) {
        this.formDrawer.inputId = ''
        this.formDrawer.isAdd = true
      } else {
        this.formDrawer.inputId = res.formCode
        this.formDrawer.isAdd = false
      }
      this.$refs.formDrawer.validateField('inputId')
    },
    // 维护表单-表单名称失去焦点
    handleBlurFormName () {
      let that = this
      setTimeout(function () {
        if (that.formDrawer.inputId == '' && that.formDrawer.inputNameTemp != that.formDrawer.formName) {
          that.formDrawer.inputNameTemp = ''
          that.formDrawer.formName = ''
        } else {
          that.formDrawer.inputNameTemp = that.formDrawer.formName
        }
      }, 500)
    },
    // 维护表单-确定
    confirmFormDrawer () {
      let that = this
      let data = this.formDrawer
      data.btnLoading = true
      this.$refs.formDrawer.validate(valid => {
        if (valid) {
          let url = ''
          if (data.isAdd) {
            url = '/robot/appConfig/addInput'
          } else {
            url = '/robot/appConfig/addInput'
          }
          that.$http({
            url: url,
            method: 'post',
            data: {
              robotType: that.appType,
              formCode: data.inputId,
              formName: data.inputName,
              createRobotAppId: this.appCode
            }
          }).then(({ data }) => {
            that.$message.success('操作成功')
            that.cancelFormDrawer()
            that.getAllFormList() // 重新获取所有表单list
            if (!data.isAdd) {
              // 如果是修改的话，需要刷新信息配置的数据，因为有可能是已配置的表单名称被修改了
              that.getInfoData()
            }
          }).catch(() => {
            data.btnLoading = false
          })
        } else {
          data.btnLoading = false
        }
      })
    },
    // 维护表单取消
    cancelFormDrawer () {
      this.formDrawer = this.$options.data().formDrawer
      this.allGroupList.forEach(item => {
        item.edit = false
      })
      this.$refs.formDrawer.resetFields()
    },
    // 添加分组-搜索分组名称
    querySearchGroupName (queryString, cb) {
      var results = queryString ? this.createFilterGroupName(queryString) : this.allGroupList
      // 调用 callback 返回建议列表的数据
      cb(results)
    },
    createFilterGroupName (queryString) {
      let res = []
      let isSame = false
      this.allGroupList.map(item => {
        if (item.groupName.indexOf(queryString) > -1) {
          if (item.groupName === queryString) {
            isSame = true
            res.unshift(item)
          } else {
            res.push(item)
          }
        }
      })
      if (!isSame) {
        res.unshift({ groupName: queryString, isNew: true })
      }
      return res
    },
    // 添加分组-选择分组名称
    handleSelectGroupName (res) {
      this.groupDrawer.groupName = res.groupName
      this.groupDrawer.groupNameTemp = res.groupName
      if (res.isNew) {
        this.groupDrawer.groupId = ''
        this.groupDrawer.isAdd = true
      } else {
        this.groupDrawer.groupId = res.id
        this.groupDrawer.isAdd = false
      }
    },
    // 添加分组-分组名称失去焦点
    handleBlurGroupName () {
      let that = this
      setTimeout(function () {
        if (that.groupDrawer.groupId == '' && that.groupDrawer.groupNameTemp != that.groupDrawer.groupName) {
          that.groupDrawer.groupNameTemp = ''
          that.groupDrawer.groupName = ''
        } else {
          that.groupDrawer.groupNameTemp = that.groupDrawer.groupName
        }
      }, 500)
    },
    // 添加分组,修改分组名称，换分组--确定
    confirmGroupDrawer () {
      let that = this
      let data = this.groupDrawer
      data.btnLoading = true
      this.$refs.groupDrawer.validate(valid => {
        if (valid) {
          let url = ''
          let obj = {}
          if (data.isEdit) {
            url = '/robot/appConfig/editGroupInfo'
            obj = {
              robotType: this.appType,
              groupName: data.groupName,
              groupId: data.inputId,
              appCode: this.appCode
            }
          } else {
            url = '/robot/appConfig/addGroup'
            obj = {
              groupName: data.groupName,
              groupId: data.groupId,
              appCode: this.appCode,
              formId: data.inputId,
              robotType: this.appType
            }
          }
          that.$http({
            url: url,
            method: 'post',
            data: obj
          }).then(({ data }) => {
            that.$message.success('操作成功')
            that.cancelGroupDrawer()
            that.getInfoData()
            that.getAllGroupList()
          }).catch(() => {
            data.btnLoading = false
          })
        } else {
          data.btnLoading = false
        }
      })
    },
    // 添加分组，修改分组名称，取消
    cancelGroupDrawer () {
      this.groupDrawer = this.$options.data().groupDrawer
      this.$refs.groupDrawer.resetFields()
    },
    // 添加\编辑字段信息
    doAddEditField (type, item, it, index) {
      item.robotAppGroupVOS.forEach(item => {
        item.argsDefineVOS.forEach(item1 => {
          item1.self = false
        })
      })
      if (type == 'edit') {
        it.argsDefineVOS[index].self = true
        this.fieldDrawer = this.deepCopy(it.argsDefineVOS[index])
      }
      this.fieldDrawer.formData = item
      this.fieldDrawer.item = it.argsDefineVOS // 分组里其他信息，用于校验其他字段重复
      this.fieldDrawer.isEdit = type == 'edit' // 是否编辑状态
      this.fieldDrawer.groupId = it.id // 分组id
      this.fieldDrawer.groupName = it.groupName // 分组名称
      this.fieldDrawer.show = true
      this.fieldDrawer.formId = item.id // 表单id
    },
    // 字段添加\编辑确定
    confirmFieldDrawer () {
      let that = this
      var data = this.fieldDrawer
      data.btnLoading = true
      this.$refs.fieldDrawer.validate(valid => {
        if (valid) {
          this.showLoading()
          let url = ''
          let setObj = {
            appCode: that.appCode,
            groupId: data.groupId, // 分组名称
            groupName: data.groupName, // 分组名称
            fieldName: data.fieldName, // 字段名称
            fieldType: data.fieldType, // 字段属性
            dropDownOption: data.dropDownOption, // 下拉选项
            quantity: data.quantity, // 图片上传，附件上传总数
            comment: data.comment, // 字段注释
            fieldKey: data.fieldKey, // 字段key
            required: data.required, // 是否必填0:必填；1:非必填
            formId: data.formId,
            id: data.id
          }
          if (data.isEdit) {
            url = '/robot/appConfig/editGroupItem'
          } else {
            url = '/robot/appConfig/addGroupItem'
            setObj.displayOrder = data.item.length + 1
          }
          that.$http({
            url: url,
            method: 'post',
            data: setObj
          }).then(({ data }) => {
            that.$message.success('操作成功')
            that.cancelFieldDrawer()
            that.getInfoData()
            that.getAllGroupList()
            this.closeLoading()
          }).catch(() => {
            this.closeLoading()
          })
        } else {
          this.closeLoading()
        }
      })
    },
    // 字段添加/编辑取消
    cancelFieldDrawer () {
      this.fieldDrawer = this.$options.data().fieldDrawer
      this.$refs.fieldDrawer.resetFields()
    },
    // 添加条件确定
    confirmConditionDrawer () {
      this.$refs.conditionDrawer.validate(valid => {
        if (valid) {
          this.showLoading()
          this.$http({
            url: '/robot/appConfig/addCondition',
            method: 'post',
            data: {
              ...this.conditionDrawer,
              appCode: this.appCode,
	            configConditions: this.conditionDrawer.list
            }
          }).then(({ data }) => {
            this.$message.success('操作成功')
            this.getInfoData()
            this.closeLoading()
            this.cancelConditionDrawer()
          }).catch(() => {
            this.closeLoading()
          })
        }
      })
    },
    // 取消条件
    cancelConditionDrawer () {
      this.conditionDrawer = this.$options.data().conditionDrawer
      this.fieldError = []
      this.$refs.conditionDrawer.resetFields()
    },
    // 删除条件
    delCondition (index) {
      this.conditionDrawer.list.splice(index, 1)
    },
    // 添加条件
    addCondition () {
      this.conditionDrawer.list.push({ conditionLeft: '', conditionRight: '', logicalOperator: '' })
    },
    // 添加条件弹窗
    showAddCondition (it, item, index) {
      console.log(it, item, index)
      this.conditionDrawer.argsDefineId = item[index].id
      this.conditionDrawer.show = true
      this.initConditionList(item, it.id)
      // 获取条件信息
      it.argsDefineVOS[index].conditionList.forEach(item => {
        item.conditionLeft = item.conditionLeft
      })
      this.conditionDrawer.list = it.argsDefineVOS[index].conditionList ? this.deepCopy(it.argsDefineVOS[index].conditionList) : []
      if (this.conditionDrawer.list.length <= 0) {
        this.conditionDrawer.list.push({ conditionLeft: '', conditionRight: '', logicalOperator: '' })
        this.conditionDrawer.relation = ''
      } else {
        this.conditionDrawer.relation = it.argsDefineVOS[index].conditionList[0].relation
      }
      this.conditionDrawer.fieldName = it.argsDefineVOS[index].fieldName
      this.conditionDrawer.showOrHidden = it.argsDefineVOS[index].showOrHidden ? Number(it.argsDefineVOS[index].showOrHidden) : 0
    },
    // 初始化条件字段
    initConditionList (data, targetId) {
      this.conditionList = []
      console.log(data, this.conditionDrawer.argsDefineId)
      data.forEach(item => {
        if (item.id != this.conditionDrawer.argsDefineId) {
          this.conditionList.push({
            fieldName: item.fieldName,
            relateConditionArgsId: item.id,
            argsDefineId: targetId,
            item: item
          })
        }
      })
      console.log(this.conditionList)
    },
    // 操作栏，上挪\下移
    doMove (row, type) {
      this.showLoading()
      let displayOrder = type === 'up' ? row.displayOrder - 1 : row.displayOrder + 1
      this.$http({
        url: '/robot/appConfig/editGroupItemOrder',
        method: 'post',
        data: {
          id: row.id,
          displayOrder: displayOrder
        }
      }).then(({ data }) => {
        this.closeLoading()
        this.$message.success('操作成功')
        this.getInfoData()
      }).catch(() => {
        this.closeLoading()
      })
    },
    showLoading (target) {
      this.loading = this.$loading({
        target: target || document.body,
        lock: true,
        text: '加载中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      })
    },
    closeLoading () {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    },
    // 获取字典值
    getDictByKey (key) {
      this.$http({
        url: `/robot/data/dict/${key}`,
        method: 'post'
      }).then(res => {
        this.selectData = res.data.data
      })
    },
    // 深克隆
    deepCopy (obj) {
      var result = Array.isArray(obj) ? [] : {}
      for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
          if (typeof obj[key] === 'object' && obj[key] !== null) {
            result[key] = this.deepCopy(obj[key]) // 递归复制
          } else {
            result[key] = obj[key]
          }
        }
      }
      return result
    },
    // 复制参数
    copyArguments () {
      this.copyArgumentsData.show = true
    },
    // 配置通用参数
    generalArguments () {
      this.generalArgumentsData.show = true
    },
    // 改变城市
    changeAddrSelect (val) {
      this.appList.forEach(item => {
        if (item.addrName == val) {
          this.copyArgumentsData.addrName = item.addrName
          this.copyArgumentsData.addrId = item.addrId
        }
      })
    },
    // 获取参保地
    getAddr (type) {
      let that = this
      this.$http({
        url: 'policy/declareAddr/addrList',
        method: 'post'
      }).then(({ data }) => {
        this.allAddr = data.data
      })
    },
    // 取消
    cancelCopy () {
      this.copyArgumentsData.show = false
      this.copyArgumentsData.addrName = ''
      this.$refs.copyArguments.clearValidate()
    },
    // 复制通用参数
    confirmCopy () {
      this.$refs.copyArguments.validate(valid => {
        if (valid) {
          this.copyArgs()
        }
      })
    },
    copyArgs () {
      this.showLoading()
      this.$http({
        url: '/robot/appConfig/copyArgs',
        method: 'post',
        data: {
          appCode: this.appCode,
          addrId: this.copyArgumentsData.addrId,
          addrName: this.copyArgumentsData.addrName,
          businessType: this.businessType == 1 ? '1001001' : '1001002',
          businessName: this.businessType == 1 ? '社保' : '公积金'
        }
      }).then(({ data }) => {
        console.log(data)
        this.$message.success('操作成功')
        this.getInfoData()
        this.cancelCopy()
        this.closeLoading()
      }).catch(() => {
        this.closeLoading()
      })
    },
    cancelGeneralArguments () {
      this.generalArgumentsData.show = false
      this.generalArgumentsData.list.forEach(item => {
        item.robotAppGroupVOS.forEach(item1 => {
          item1.groupNameSet = false
          item1.argsDefineVOS.forEach(item2 => {
            this.$set(item2, 'check', false)
          })
        })
      })
    },
    // 配置通用参数 确定时
    confirmGeneralArguments () {
      var checkList = []
      this.generalArgumentsData.list.forEach(item => {
        item.robotAppGroupVOS.forEach(item1 => {
          if (item1.groupNameSet) {
            checkList.push(item1.groupNameSet)
          }
          item1.argsDefineVOS.forEach(item2 => {
            if (item2.check) {
              checkList.push(item2.check)
            }
          })
        })
      })
      if (checkList.length <= 0) {
        this.generalArgumentsData.error = true
      } else {
        this.generalArgumentsData.error = false
      }
      if (!this.generalArgumentsData.error) {
        // 处理
        var data = this.deepCopy(this.generalArgumentsData.list)
        data.forEach(item => {
          item.robotAppGroupVOS.forEach(item1 => {
            item1.argsDefineVOS = item1.argsDefineVOS.filter(item2 => item2.check)
          })
          item.robotAppGroupVOS = item.robotAppGroupVOS.filter(item1 => item1.groupNameSet || item1.argsDefineVOS.length > 0)
        })
        data = data.filter(item => item.robotAppGroupVOS.length > 0)
        this.addGeneralArgs(data)
      }
    },
    // 添加通用参数
    addGeneralArgs (data) {
      this.showLoading()
      this.$http({
        url: '/robot/appConfig/addGeneralArgs',
        method: 'post',
        params: {
          appCode: this.appCode
        },
        data: data
      }).then(({ data }) => {
        this.$message.success('操作成功')
        this.cancelGeneralArguments()
        this.getInfoData()
        this.closeLoading()
      }).catch((err) => {
        console.log(err)
        this.closeLoading()
      })
    },
    async querySearch (queryString, cb) {
      clearTimeout(this.timer)
      this.timer = setTimeout(async () => {
        if (!queryString) {
          cb([])
          return
        }
        var results = await this.getFiledNameList(queryString)
        console.log(results)
        var list = []
        results.reduce((prev, cur, index, orgin) => {
          // console.log(prev,cur,index,orgin)
          if (!prev[cur.fieldName + cur.fieldType]) {
            prev[cur.fieldName + cur.fieldType] = 1
            list.push(cur)
          }
          return prev
        }, {})
        // 调用 callback 返回建议列表的数据
        cb(list)
      }, 300)
    },
    createFilter (queryString) {
      return (restaurant) => {
        return (restaurant.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0)
      }
    },
    changefieldType (val) {
      if (this.columnAttributeType('dropDownOption') || this.columnAttributeType('quantity')) {
        this.$refs.fieldDrawer.clearValidate(['dropDownOption', 'quantity'])
      }
    },
    // 获取通用参数
    getGetGeneralArgs () {
      this.showLoading()
      var formData = new FormData()
      formData.append('businessType', this.businessType)
      this.$http({
        url: '/robot/appConfig/getGeneralArgs',
        method: 'post',
        data: formData
      }).then(({ data }) => {
        this.generalArgumentsData.list = data.data
        this.closeLoading()
      }).catch(() => {
        this.closeLoading()
      })
    },
    // 配置通用参数，当勾选分组名称时
    changeArgumentsGrounpName (val, groupName) {
      this.generalArgumentsData.list.forEach(item => {
        item.robotAppGroupVOS.forEach(item1 => {
          if (item1.groupName == groupName) {
            item1.argsDefineVOS.forEach(item2 => {
              if (val) {
                this.$set(item2, 'check', true)
              } else {
                this.$set(item2, 'check', false)
              }
            })
          }
        })
      })
    },
    // 分组下的 选项 勾选
    changeArgumentsCheck (val, argsDefineVOS, groupName) {
      var checkList = []
      argsDefineVOS.forEach(item => {
        if (item.check) {
          checkList.push(item.check)
        }
      })
      this.generalArgumentsData.list.forEach(item => {
        item.robotAppGroupVOS.forEach(item1 => {
          if (item1.groupName == groupName) {
            if (checkList.length == argsDefineVOS.length) {
              item1.groupNameSet = true
            } else {
              item1.groupNameSet = false
            }
          }
        })
      })
    },
    // 搜索系统下已存在的字段名字
    async getFiledNameList (keyWord) {
      return this.$http({
        url: '/robot/appConfig/getFiledNameList',
        method: 'post',
        params: { keyWord: keyWord }
      }).then(({ data }) => {
        this.closeLoading()
        return data.data
      }).catch(() => {
        this.closeLoading()
        return []
      })
    },
    changeFieldDrawerFieldName (val) {
      this.fieldDrawer.fieldName = val.fieldName
      this.fieldDrawer.fieldType = val.fieldType
      this.fieldDrawer.fieldKey = val.fieldKey
      this.fieldDrawer.dropDownOption = val.dropDownOption
      this.fieldDrawer.comment = val.comment
      this.fieldDrawer.quantity = val.quantity
    },
    getAppData () {
      var params = [
        { property: 'keyWord', value: '' },
        { property: 'serviceItemList', value: [] },
        { property: 'businessList', value: [Number(this.businessType)] },
        { property: 'onlineList', value: [] },
        { property: 'appStatusList', value: [] }
      ]
      this.$http({
        url: '/robot/app/getAppData',
        method: 'post',
        data: { 'page': 1,
          'start': 0,
          'size': 999,
          'query': params,
          'sidx': '',
          'sort': '' }
      }).then(({ data }) => {
        this.appList = data.data.rows.filter(item => item.appName != this.appInfo.appName)
      }).catch(() => {
      })
    }
  }
}
</script>

<style scoped lang="less">
  .form-box{
    border: 1px solid #dbdbdb;
    border-radius: 4px;
    padding: 10px;
    margin-bottom: 30px;
    .form-title{
      font-size: 16px;
      font-weight: bold;
      margin-bottom: 15px;
    }
    .group-item{
      padding: 10px;
      margin-bottom: 20px;
    }
    .del-btn{
      color: #909399;
    }
    .cust-table{
      table-layout: fixed;
      tbody{
        td{
          padding-top: 0;
          padding-bottom: 0;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }
  }
  .item-lab{
    line-height: 30px;
  }
  .el-form-item{
    margin-bottom: 10px;
  }
  .type-title {
    font-size: 16px;
    height: 50px;
    background: #F8F8F8;
    display: flex;
    color: #606266;
    padding-right: 20px;
    .text {
      height: 50px;
      display: inline-block;
      line-height: 50px;
      flex: 1;
    }
    .text:before {
      margin-right: 5px;
      content: '';
      display: inline-block;
      height: 30px;
      width: 6px;
      vertical-align: middle;
      background: #3E82FF;
      border-radius: 6px;
    }
  }
  .formDrawer-content{
    padding: 15px 30px 30px 30px;
    position: relative;
    min-height: 100%;
    .msg-tip{
      position: absolute;
      bottom: 10px;
      left: 30px;
      color: @redColor;
      background-color: #fff;
      z-index: 5;
    }
  }
  .formName-list-box{
    display: flex;
    flex-wrap: wrap;
  }
  .formName-list{
    padding:10px 20px;
    border: 1px solid #ddd;
    border-radius: 10px;
    line-height: 1;
    cursor: pointer;
    background: #eee;
    margin-left:10px;
    margin-bottom: 20px;
    width: 100px;
  }
  /deep/.condition-drawer{
    width: 700px!important;
  }
  /deep/ .column.el-form-item{
    display: flex;
    flex-direction: column;
  }
  /deep/ .column .el-form-item__label{
    text-align: left;
  }
  .error{
    position: absolute;
    top: -60px;
    color: #F56C6C;
  }
  /deep/ .checkList-column .el-checkbox-group{
    padding-left: 20px;
    display: flex;
    flex-direction: column;
  }
  /deep/ .checkList-column .el-checkbox{
    padding: 5px 0;
  }
</style>
