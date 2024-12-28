import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate'
import routeQuery from './modules/routeQuery'
Vue.use(Vuex)
const config = {
  plugins: [createPersistedState()],
  modules:{
    routeQuery
  },
  state: {
    rememberPsw: '',
    username: '',
    token: '',
    activePath: '',
    userData: {},
    allUrls: [],
    menuList: [],
    buttons: [],
    tabs: {},
    personAuthStatus: false,
    isShowTabs: true,
    companyInfo: {},
    insuranceRow: {},
    monitorDotCount:0,
    businessDotCount:0,       //商业缴费【未核定】 的记录条数
    msgNumber:0,
    declareSystemSelects:[],     //流程控制第一次进入 获取的下拉值
  },
  getters: {
    rememberPsw: state => state.rememberPsw,
    username: state => state.username,
    token: state => state.token,
    activePath: state => state.activePath,
    userData: state => state.userData,
    allUrls: state => state.allUrls,
    menuList: state => state.menuList,
    buttons: state => state.buttons,
    tabs: state => state.tabs,
    personAuthStatus: state => state.personAuthStatus,
    companyInfo: state => state.companyInfo,
    insuranceRow: state => state.insuranceRow,
    monitorDotCount:state => state.monitorDotCount,
    businessDotCount:state => state.businessDotCount,
    msgNumber:state => state.msgNumber,
    declareSystemSelects:state =>state.declareSystemSelects
  },
  mutations: {
    updateMsgNumber(state,payload){
      state.msgNumber = payload
    },
    updateRememberPsw(state, payload) {
      state.rememberPsw = payload
    },
    updateUsername(state, payload) {
      state.username = payload
    },
    updateToken(state, payload) {
      state.token = payload
    },
    updateActivePath(state, payload) {
      state.activePath = payload
    },
    updateUserData(state, payload) {
      state.userData = payload
    },
    isLogin(state, payload) {
      state.isLogin = payload
    },
    allUrls(state, payload) {
      state.allUrls = payload
    },
    menuList(state, payload) {
      state.menuList = payload
    },
    buttons(state, payload) {
      state.buttons = payload
    },
    tabs(state, payload) {
      state.tabs = payload
    },
    updatePersonAuthStatus(state, payload) {
      state.personAuthStatus = payload
    },
    UpdateIsShowTabs(state, payload) {
      state.isShowTabs = payload
    },
    updateCompanyInfo(state, payload) {
      state.companyInfo = payload
    },
    updateInsuranceRow(state, payload) {
      state.insuranceRow = payload
    },
    updateMonitorDotCount(state,payload){
      state.monitorDotCount = payload
    },
    updateBusinessDotCount(state,payload){
      state.businessDotCount = payload
    },
    updateDeclareSystemSelects(state,payload){
      state.declareSystemSelects = payload
    },
  },
  actions: {
    clearLoginInfo(context) {
      context.commit('updateUsername', '')
      context.commit('updateToken', '')
      context.commit('updateUserData', {})
      context.commit('updatePersonAuthStatus', false)
      context.commit('UpdateIsShowTabs', true)
      context.commit('updateCompanyInfo', {})
      context.commit('updateInsuranceRow', {})

    }
  }
}
const store = new Vuex.Store(config)
export default store
