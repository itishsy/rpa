<template>
  <div class="spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData"></breadcrumb>
    <div style="padding: 0px 20px 0 20px;">
      <div class="search-row clearfix">
        <el-input v-model.trim="keyword" clearable placeholder="输入关键字搜索" suffix-icon="el-icon-search text-gray"
                  style="width:300px;"></el-input>
      </div>

      <div style="padding: 0 20px;">
        <div v-for="(item, index) in list" :key="index">
          <p class="type-title">{{item.type}}</p>
          <div class="type-list">
            <a class="type" v-for="(it, ind) in item.list" :key="ind" :href="'/#/' + it.url" v-show="it.title.indexOf(keyword)>-1">{{it.title}}</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  data () {
    return {
      keyword: '',
      list: [
        {
          type: '业务类',
          list: [
            {
              title: '定制服务',
              url: 'customServices/index'
            },
            {
              title: '初始化申报账号',
              url: 'basicConfig/initDeclareAccount'
            },
            {
              title: '业务场景提示',
              url: 'basicConfig/businessSceneTip'
            }
          ]
        },
        {
          type: '消息类',
          list: [
            {
              title: '预警配置',
              url: 'system/warnConfig'
            },
            // {
            //   title: '登录推送模板',
            //   url: 'pushTemplate/index'
            // },
            {
              title: '消息配置',
              url: 'controlAndReport/messageCenter'
            },
            {
              title: '常见问题管理',
              url: 'questionManage/index'
            }
          ]
        },
        {
          type: 'AI相关',
          list: [
            {
              title: 'AI人员字段配置',
              url: 'basicConfig/aiFieldConfig'
            },
            {
              title: 'AI值映射',
              url: 'basicConfig/aiFieldMapping'
            }
          ]
        },
        {
          type: '机器人',
          list: [
            {
              title: '应用环境',
              url: 'customerAppList/certificate'
            }
          ]
        }
      ]
    }
  },
  computed: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
  },
  mounted () {

  },
  methods: {}
}
</script>
<style lang="less" scoped>
.type-title{
  font-weight: bold;
  font-size: 16px;
  margin-top: 10px;
}
.type-list{
  margin-top: 20px;
}
.type{
  display: inline-block;
  padding: 15px 20px;
  min-width: 150px;
  border: 1px solid #dbdbdb;
  border-radius: 4px;
  margin-right: 80px;
  text-align: center;
  cursor: pointer;
  margin-bottom: 20px;
  color: @comFontSizeColor;
}
.type:hover{
  color: #ffffff;
  background-color: @blueColor;
}
</style>
