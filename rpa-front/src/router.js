// 路由文件
import Vue from 'vue'
import Router from 'vue-router'
import store from './store/index'
// 导入 vue 和 vue-router  相当于
// <script src="vue.js">  <script src="vue-router.js">

// 添加路由规则
const originalPush = Router.prototype.push
Router.prototype.push = function push (location) {
  return originalPush.call(this, location).catch((err) => err)
}

Vue.use(Router)

// 暴露
const router = new Router({
  routes: [
    {
      path: '/',
      redirect: '/index'
    }, // 重定向
    {
      path: '/login',
      component: () => import('./components/Login.vue')
    },
    {
      path: '/home',
      component: () => import('./components/Home'),
      // redirect: '/welcome',
      children: [
        // 这个链接跳到对应的组件
        // 首页
        {
          path: '/index',

          // component: () => import('./views/index/index')     //旧的首页
          component: () => import('./views/monitorStation/index.vue')
        },
        {
          path: '/questionDetail',
          component: () => import('./views/index/questionDetail'),
          meta: {
            parentPath: '/index'
          }
        },
        /* 报盘文件设置 */
        {
          path: '/offerFile/offerFileSettings',
          component: () => import('./views/offerFile/offerFileSettings')
        },
        // 报盘文件设置-新增
        {
          path: '/offerFile/addOfferFile',
          component: () => import('./views/offerFile/addOfferFile'),
          meta: {
            parentPath: '/offerFile/offerFileSettings'
          }
        },
        // 报盘文件设置-维护报盘信息
        {
          path: '/offerFile/offerInfo',
          component: () => import('./views/offerFile/offerInfo'),
          meta: {
            parentPath: '/offerFile/offerFileSettings'
          }
        },
        // 报盘文件设置-校验规则
        {
          path: '/offerFile/validateRules',
          component: () => import('./views/offerFile/validateRules'),
          meta: {
            parentPath: '/offerFile/offerFileSettings'
          }
        },
        /* 参保管理-社保公积金-交互数据 */
        {
          path: '/socialAccfund/interactiveData',
          component: () => import('./views/socialAccfund/interactiveData'),
          meta: {
            parentPath: '/socialAccfund/curMonthInsurance'
          }
        },
        /* 社保公积金-当月参保情况 */
        {
          path: '/socialAccfund/curMonthInsurance',
          component: () => import('./views/socialAccfund/curMonthInsurance'),
          meta: {
            parentPath: '/socialAccfund/curMonthInsurance'
          }
        },
        /* 申报账户管理 */
        {
          path: '/declareAccount/management',
          component: () => import('./views/declareAccount/index')
        },
        // 参保险种
        {
          path: '/insuranceScheme/index',
          component: () => import('./views/insuranceScheme/index')
        },
        // 参保险种 - 新增
        {
          path: '/insuranceScheme/addSocial',
          component: () => import('./views/insuranceScheme/addSocial'),
          meta: {
            parentPath: '/insuranceScheme/index'
          }
        },
        // 参保城市
        {
          path: '/insuredCity/index',
          component: () => import('./views/insuredCity/index')
        },
        // 参保城市 - 申报政策
        {
          path: '/insuredCity/policy',
          component: () => import('./views/insuredCity/policy'),
          meta: {
            parentPath: '/insuredCity/index'
          }
        },
        // rpa流程设置-列表
        {
          path: '/processSetting/index',
          component: () => import('./views/processSetting/index')
        },
        // rpa流程设置-设置
        {
          path: '/processSetting/setting',
          component: () => import('./views/processSetting/setting'),
          meta: {
            parentPath: '/processSetting/index'
          }
        },
        // rpa流程设置-流程模板
        {
          path: '/processSetting/templateList',
          component: () => import('./views/processSetting/templateList'),
          meta: {
            parentPath: '/processSetting/index'
          }
        },
        // rpa流程设置-步骤设置
        {
          path: '/processSetting/stepSetting',
          component: () => import('./views/processSetting/stepSetting.vue'),
          meta: {
            parentPath: '/processSetting/index'
          }
        },
        // rpa流程设置 - 信息维护
        {
          path: '/processSetting/infoMaintain',
          component: () => import('./views/processSetting/infoMaintain'),
          meta: {
            parentPath: '/processSetting/index'
          }
        },
        // rpa流程设置 - 历史版本
        {
          path: '/processSetting/historyVersion',
          component: () => import('./views/processSetting/historyVersion'),
          meta: {
            parentPath: '/processSetting/index'
          }
        },
        // 地区业务变更管理
        {
          path: '/addr/businessChange',
          component: () => import('./views/addrBusinessChange/index.vue'),
          meta: {
            parentPath: '/processSetting/index'
          }
        },
        // 机器人管理 - 机器人版本管理
        {
          path: '/robotManage/versionManage',
          component: () => import('./views/robotManage/versionManage/index')
        },
        // 机器人管理 - 机器人版本管理 - 历史版本
        {
          path: '/robotManage/historyVersion',
          component: () =>
            import('./views/robotManage/versionManage/historyVersion'),
          meta: {
            parentPath: '/robotManage/versionManage'
          }
        },
        // 机器人管理 - 机器人客户端监控
        {
          path: '/robotManage/clientManage',
          component: () => import('./views/robotManage/clientManage/index')
        },
        // 机器人管理 - 机器人客户端监控 - 执行查询
        {
          path: '/robotManage/executeHistory',
          component: () =>
            import('./views/robotManage/clientManage/executeHistory'),
          meta: {
            parentPath: '/robotManage/clientManage'
          }
        },
        // 机器人管理 - session维持配置
        {
          path: '/robotManage/sessionManage',
          component: () =>
            import('./views/robotManage/sessionManage/index.vue')
        },
        // 机器人管理 - 配置Ca证书
        {
          path: '/robotManage/caManage',
          component: () =>
            import('./views/robotManage/caManage/index.vue')
        },
        {
          path: '/customerAppList/customerIndex',
          component: () => import('./views/customerAppList/customerIndex'),
          meta: {
            parentPath: '/customerAppList/customerIndex'
          }
        },
        {
          path: '/customerAppList/executionPlanEdit',
          component: () => import('./views/customerAppList/executionPlanEdit'),
          meta: {
            parentPath: '/customerAppList/customerIndex'
          }
        },
        // 机器人管理--基础配置
        {
          path: '/robotManage/basicConfig',
          component: () => import('./views/robotManage/basicConfig/index')
        },
        // 机器人管理--业务场景提示
        {
          path: '/basicConfig/businessSceneTip',
          component: () => import('./views/robotManage/businessSceneTip/index'),
          meta: {
            parentPath: '/robotManage/basicConfig'
          }
        },
        // 机器人管理--业务场景提示-添加
        {
          path: '/businessSceneTip/add',
          component: () => import('./views/robotManage/businessSceneTip/add'),
          meta: {
            parentPath: '/robotManage/basicConfig'
          }
        },
        // 机器人管理--定制服务
        {
          path: '/customServices/index',
          component: () => import('./views/customServices/index'),
          meta: {
            parentPath: '/robotManage/basicConfig'
          }
        },
        // 机器人管理--AI人员字段配置
        {
          path: '/basicConfig/aiFieldConfig',
          component: () => import('./views/robotManage/basicConfig/aiFieldConfig'),
          meta: {
            parentPath: '/robotManage/basicConfig'
          }
        },
        // 机器人管理--AI值映射
        {
          path: '/basicConfig/aiFieldMapping',
          component: () => import('./views/robotManage/basicConfig/aiFieldMapping'),
          meta: {
            parentPath: '/robotManage/basicConfig'
          }
        },
        // 机器人管理--AI值映射--新增编辑
        {
          path: '/basicConfig/aiFieldMappingUpdate',
          component: () => import('./views/robotManage/basicConfig/aiFieldMappingUpdate'),
          meta: {
            parentPath: '/robotManage/basicConfig'
          }
        },
        // 机器人管理--初始化申报账号
        {
          path: '/basicConfig/initDeclareAccount',
          component: () => import('./views/robotManage/basicConfig/initDeclareAccount'),
          meta: {
            parentPath: '/robotManage/basicConfig'
          }
        },
        // 机器人管理--初始化申报账号--导入
        {
          path: '/basicConfig/initDeclareAccountImport',
          component: () => import('./views/robotManage/basicConfig/initDeclareAccountImport'),
          meta: {
            parentPath: '/robotManage/basicConfig'
          }
        },
        // 机器人管理-基础配置-预警配置
        {
          path: '/system/warnConfig',
          component: () => import('./views/system/warnConfig'),
          meta: {
            parentPath: '/robotManage/basicConfig'
          }
        },
        // 问题管理-列表
        {
          path: '/questionManage/index',
          component: () => import('./views/questionManage/index'),
          meta: {
            parentPath: '/robotManage/basicConfig'
          }
        },
        // 问题管理-添加编辑
        {
          path: '/questionManage/addOrEdit',
          component: () => import('./views/questionManage/addOrEdit'),
          meta: {
            parentPath: '/robotManage/basicConfig'
          }
        },
        // 机器人管理--回盘规则配置
        {
          path: '/returnRuleConfig/index',
          component: () => import('./views/robotManage/returnRuleConfig/index')
        },
        // 机器人管理--回盘规则配置
        {
          path: '/returnRuleConfig/addOrEdit',
          component: () => import('./views/robotManage/returnRuleConfig/addOrEdit'),
          meta: {
            parentPath: '/returnRuleConfig/index'
          }
        },
        // {
        // {
        //   path: '/customerAppList/executionPlan',
        //   component: () => import('./views/customerAppList/executionPlan'),
        //   meta: {
        //     parentPath: '/customerAppList/executionPlan'
        //   }
        // },
        // rpa流程设置 - 执行计划
        {
          path: '/processSetting/executionPlan',
          component: () => import('./views/processSetting/executionPlan'),
          meta: {
            parentPath: '/processSetting/index'
          }
        },

        // 系统管理-角色权限
        {
          path: '/system/roleAssign',
          component: () => import('./views/system/roleAssign')
        },
        // 系统管理-用户管理
        {
          path: '/system/userAssign',
          component: () => import('./views/system/userAssign')
        },
        // 系统管理-用户管理-用户权限
        {
          path: '/system/userPermissions',
          component: () =>
            import('./views/system/userPermissions'),
          meta: {
            parentPath: '/system/userAssign'
          }
        },
        // // 客户权限管理
        // {
        //   path: '/system/customerPermissions',
        //   component: () => import('./views/system/customerPermissions')
        // },
        // 系统管理-运营系统菜单
        {
          path: '/system/menuAssign',
          component: () => import('./views/system/menuAssign')
        },
        // 系统管理-客户系统菜单
        {
          path: '/system/menuAssignClient',
          component: () => import('./views/system/menuAssignClient')
        },
        {
          path: '/pathError',
          component: () => import('./views/error/pathError')
        },
        {
          path: '/paidInSetting/index',
          component: () => import('./views/paidInSetting/index')
        },
        {
          path: '/paidInSetting/mapSetting',
          component: () => import('./views/paidInSetting/mapSetting'),
          meta: {
            parentPath: '/paidInSetting/index'
          }
        },
        {
          path: '/paidIn/detail',
          component: () => import('./views/paidIn/detail'),
          meta: {
            parentPath: '/paidIn/index'
          }
        },
        // 客户设备列表
        {
          path: '/deviceManagement/index',
          component: () => import('./views/deviceManagement/index'),
          meta: {
            parentPath: '/customerAppList/customerIndex'
          }
        },
        //  地区申报模板 /offerFile/offerFileSettings
        {
          path: '/offerFile/regionalDeclaration',
          component: () => import('./views/offerFile/regionalDeclaration'),
          meta: {
            parentPath: '/offerFile/regionalDeclaration'
          }
        },
        //  地区标准缴纳方案查询 /areaSchemeQuery/index
        {
          path: '/areaSchemeQuery/index',
          component: () => import('./views/areaSchemeQuery/index'),
          meta: {
            parentPath: '/areaSchemeQuery/index'
          }
        },
        // rpa 流程图
        {
          path: '/processSetting/flow',
          component: () => import('./views/flow/index'),
          meta: {
            parentPath: '/processSetting/index'
          }
        },
        // 监控与报表 - 运维监控
        {
          path: '/controlAndReport/devOps',
          component: () =>
            import('./views/controlAndReport/MonitoringPanel.vue')
        },
        // 监控与报表 - 运维监控 - 预警明细
        {
          path: '/controlAndReport/detail',
          component: () => import('./views/controlAndReport/WarningDetail.vue'),
          meta: {
            parentPath: '/controlAndReport/devOps'
          }
        },
        // 基础配置 - 应用列表
        {
          path: '/customerAppList/certificate',
          component: () => import('./views/customerAppList/certificate.vue'),
          meta: {
            parentPath: '/robotManage/basicConfig'
          }
        },
        // 基础配置 - 消息中心
        {
          path: '/controlAndReport/messageCenter',
          component: () =>
            import('./views/controlAndReport/MessageCenter.vue'),
          meta: {
            parentPath: '/robotManage/basicConfig'
          }
        },
        // 基础配置 - 消息中心-添加消息
        {
          path: '/controlAndReport/addMessage',
          component: () =>
            import('./views/controlAndReport/AddMessage.vue'),
          meta: {
            parentPath: '/robotManage/basicConfig'
          }
        },
        // 监控与报表 - 数据监控
        {
          path: '/controlAndReport/dataControl',
          component: () =>
            import('./views/controlAndReport/dataControl.vue')
        },
        // 机器人管理 - 登录推送模板
        {
          path: '/pushTemplate/index',
          component: () =>
            import('./views/pushTemplate/index.vue'),
          meta: {
            parentPath: '/robotManage/basicConfig'
          }
        },
        // 机器人管理 - 登录推送消息列表
        {
          path: '/loginPush/PushMsgList',
          component: () =>
            import('./views/loginPush/PushMsgList.vue')
        },
        /// 机器人管理 - 登录推送消息列表 - 短信模板配置
        {
          path: '/loginPush/textMessageTemplate',
          component: () =>
            import('./views/loginPush/TextMessageTemplate.vue'),
          meta: {
            parentPath: '/loginPush/PushMsgList'
          }
        },
        /// 监控与报表 - 用户地区配置列表
        {
          path: '/userRegionConfiguration/index',
          component: () =>
            import('./views/userRegionConfiguration/index.vue'),
          meta: {
            parentPath: '/userRegionConfiguration/index'
          }
        },
        /// 监控与报表 - 日报表
        {
          path: '/dailyReport/index',
          component: () =>
            import('./views/dailyReport/index.vue'),
          meta: {
            parentPath: '/dailyReport/index'
          }
        },
        // // 客户权限管理 - 账号权限
        // {
        //   path: '/system/accountPermissions',
        //   component: () =>
        //     import('./views/system/accountPermissions.vue'),
        //   meta: {
        //     parentPath: '/system/customerPermissions'
        //   }
        // },
        // 监控台-首页
        {
          path: '/monitorStation/index',
          component: () =>
            import('./views/monitorStation/index.vue')
        },
        // 监控台-城市上线情况
        {
          path: '/cityState/index',
          component: () =>
            import('./views/monitorStation/cityState/index.vue')
        },
        {
          path: '/cityState/detail',
          component: () =>
            import('./views/monitorStation/cityState/detail.vue'),
          meta: {
            parentPath: '/cityState/index'
          }
        },
        // 监控台-运维数据汇总
        {
          path: '/ywDataSummary/index',
          component: () =>
            import('./views/monitorStation/ywDataSummary/index.vue')
        },
        {
          path: '/ywDataSummary/ruleConfig',
          component: () =>
            import('./views/monitorStation/ywDataSummary/ruleConfig.vue'),
          meta: {
            parentPath: '/ywDataSummary/index'
          }
        },
        // 参保管理 - 商业缴费
        {
          path: '/commercialPayment/index',
          component: () =>
            import('./views/commercialPayment/index.vue')
        },
        // 政策库 - 报盘定制
        {
          path: '/offerFile/customizedList',
          component: () =>
            import('./views/offerFile/customizedList.vue'),
          meta: {
            parentPath: '/offerFile/offerFileSettings'
          }
        },
        // 政策库 - 报盘定制详情-编辑或新增
        {
          path: '/offerFile/customizedDetail',
          component: () =>
            import('./views/offerFile/customizedDetail.vue'),
          meta: {
            parentPath: '/offerFile/offerFileSettings'
          }
        },
        // 系统管理 - 在户人数管理
        {
          path: '/system/numberOfHouseHolds',
          component: () => import('./views/system/numberOfHouseHolds.vue')
        },
        // 系统管理 - 在户人数管理 - 在户名单
        {
          path: '/system/houseHoldsList',
          component: () => import('./views/system/houseHoldsList.vue'),
          meta: {
            parentPath: '/system/numberOfHouseHolds'
          }
        },
        // 系统管理 - 在户人数管理 - 在户名单 - 费用明细
        {
          path: '/system/houseHoldsDetail',
          component: () => import('./views/system/houseHoldsDetail.vue'),
          meta: {
            parentPath: '/system/numberOfHouseHolds'
          }
        },
        // 系统管理 - 收款账单管理
        {
          path: '/system/billManagement',
          component: () => import('./views/system/billManagement/index.vue')
        },
        // 系统管理 - 收款账单管理 - 创建收款单
        {
          path: '/system/billEdit',
          component: () => import('./views/system/billManagement/billEdit.vue'),
          meta: {
            parentPath: '/system/billManagement'
          }
        },
        // 录制记录 - 查询列表
        {
          path: '/record/index',
          component: () => import('./views/record/index.vue')
        },
        // 录制记录 - 查询列表
        {
          path: '/record/edit',
          component: () => import('./views/record/edit.vue'),
          meta: {
            parentPath: '/record/index'
          }
        },
        // 系统管理 - 社保费用导入
        {
          path: '/system/socialFeeImport',
          component: () => import('./views/system/socialFeeImport.vue')
        },
        // 系统管理 - 客户管理
        {
          path: '/system/customerMange',
          component: () => import('./views/system/customerMange.vue')
        },
        // 系统管理 - 客户管理 - 新增或编辑客户
        {
          path: '/system/customerSetting',
          component: () => import('./views/system/customerSetting.vue'),
          meta: {
            parentPath: '/system/customerMange'
          }
        },
        // 消息管理
        {
          path: '/messageManage',
          component: () => import('./views/messageManage/index.vue')
        },
        // 政策申领-客户管理
        {
          path: '/policyApply/customerMange',
          component: () => import('./views/policyApply/customerMange.vue')
        },
        // 政策申领-客户管理-详情
        {
          path: '/policyApply/detail',
          component: () => import('./views/policyApply/detail.vue'),
          meta: {
            parentPath: '/policyApply/customerMange'
          }
        },
        // 任务调度中心
        {
          path: '/taskManage',
          component: () => import('./views/taskManage/index.vue')
        },
        // 任务调度-指定环境规则
        {
          path: '/customerRules',
          component: () => import('./views/customerRules/index.vue'),
          meta: {
            parentPath: '/taskManage'
          }
        },
        // 政策申领-客户管理-详情-历史表格详情
        {
          path: '/policyApply/historyTableDetail',
          component: () => import('./views/policyApply/historyTable.vue'),
          meta: {
            parentPath: '/policyApply/customerMange'
          }
        },
        // 登录验证
        {
          path: '/loginValid',
          component: () => import('./views/loginValid/index.vue')
        },
        {
          path: '/robotManage/deviceManage',
          component: () => import('./views/robotManage/deviceManage/index.vue')
        },
        {
          path: '/robotManage/usbKeyManage',
          component: () => import('./views/robotManage/usbKeyManage/index.vue')
        },
        {
          path: '/robotManage/usbKeyAccount',
          component: () => import('./views/robotManage/usbKeyManage/indexAccount.vue')
        },
        {
          path: '/robotManage/usbCertManage',
          component: () => import('./views/robotManage/usbCertManage/index.vue')
        }
      ]
    }
  ]
})

router.onError((error) => {
  console.log('error===' + error)
  const pattern = /Loading chunk (\d)+ failed/g
  const isChunkLoadFailed = error.message.match(pattern)
  const pattern2 = /Loading chunk chunk-(.*)+ failed/g
  const isChunkLoadFailed2 = error.message.match(pattern2)
  console.log('isChunkLoadFailed==' + isChunkLoadFailed)
  console.log('isChunkLoadFailed2==' + isChunkLoadFailed2)
  if (isChunkLoadFailed || isChunkLoadFailed2) {
    // 用路由的replace方法，并没有相当于F5刷新页面，失败的js文件并没有从新请求，会导致一直尝试replace页面导致死循环，而用 location.reload 方法，相当于触发F5刷新页面，虽然用户体验上来说会有刷新加载察觉，但不会导致页面卡死及死循环，从而曲线救国解决该问题
    // router.replace(targetPath)
    const targetPath = router.history.pending.fullPath
    console.log('targetPath==' + targetPath)
    if (targetPath) {
      // router.replace(targetPath)
      window.location.href = window.location.origin + '/#' + targetPath
      location.reload()
    } else {
      location.reload()
    }
  }
})

router.onReady((vm) => {
  // try {
  //   let LoadingChunkPath = window.sessionStorage.getItem('LoadingChunkPath')
  //   const path = vm.path
  //   if (LoadingChunkPath) {
  //     if (path !== LoadingChunkPath && path !== '/') {
  //       window.sessionStorage.setItem('LoadingChunkPath', vm.path)
  //       router.replace(vm.path)
  //     } else {
  //       router.replace(LoadingChunkPath)
  //     }
  //   }
  // } catch (e) {
  //   console.log(e)
  // }
})

// 挂载路由导航守卫
router.beforeEach((to, from, next) => {
  // console.log(to.meta.parentPath,to.path,store.state.menuList)
  // console.log(checkMenuList(store.state.menuList,to.path))
  store.commit('updateActivePath', to.path)
  if (to.path === '/login' || to.path == '/pathError' || to.path == '/messageManage') return next()
  const tokenStr = store.state.token
  if (!tokenStr) return next('/login')
  if (to.meta.parentPath) {
    if (checkMenuList(store.state.menuList, to.meta.parentPath).length <= 0) {
      return next('/pathError')
    } else {
      return next()
    }
  } else if (checkMenuList(store.state.menuList, to.path).length <= 0) {
    return next('/pathError')
  }
  return next()
  // // to: Route: 即将要进入的目标 路由对象
  // // from: Route: 当前导航正要离开的路由  /^\-[1-9][0-9]*$/
  // // next: Function: 一定要调用该方法来 resolve 这个钩子。执行效果依赖 next 方法的调用参数。
  // // var patt = /Verification[0-9]{1}/
  // var reg1 = new RegExp('/verification[0-9]{1}')
  // if (to.path === '/login') return next()
  // if (to.path === '/about' || to.path === '/retrievePsd' || to.path === '/accountSettings' || to.path === '/pathError' || reg1.test(to.path)) return next()
  // const tokenStr = store.state.token
  // if (!tokenStr) return next('/login')
  // let allUrls = store.state.allUrls
  // // if ((to.meta && to.meta.parentPath && allUrls.indexOf(to.meta.parentPath) > -1) || allUrls.indexOf(to.path) > -1) {
  // if (allUrls.indexOf(to.path) > -1) {
  //   next()
  // } else {
  //   return next('/pathError')
  // }
})
function checkMenuList (data, target) {
  if (!target) {
    return []
  }
  return data.filter((item) => {
    // console.log(item.url.replace(/^\//,''),target.replace(/^\//,''))
    item.url = item.url ? item.url : ''
    return item.url.replace(/^\//, '') == target.replace(/^\//, '')
      ? true
      : item.childMenu
        ? checkMenuList(item.childMenu, target).length > 0
        : false
  })
}

export default router
