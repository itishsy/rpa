
/*
 * @用来存储各页面的查询参数，达到用户跳转详情页返回列表查询参数不变
 */
const state = {
    processSettingQuery: {} //流程配置页面查询参数
  }
  
  const mutations = {
    AUDIT_PROCESS_SETTING_QUERY: (state, data) => {
      state.processSettingQuery = data
    },
  }
  
  const actions = {
    addprocessSettingQuery({ commit }, data) {
      commit('AUDIT_PROCESS_SETTING_QUERY', data)
    },
  }
  
  export default {
    namespaced: true,
    state,
    mutations,
    actions
  }
  