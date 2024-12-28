<template>
  <div id="app">
    <!-- 添加路由占位符 -->
    <router-view v-if="isRouterAlive"></router-view>
  </div>
</template>

<script>

export default {
  name: 'app',
  provide () {
    return {
      reload: this.reload
    }
  },
  components: {

  },
  created () {
    // console.log('环境==' + process.env.VUE_APP_URL)
    String.prototype.endWith = function (endStr) {
      var d = this.length - endStr.length
      return (d >= 0 && this.lastIndexOf(endStr) == d)
    }
  },
  data () {
    return {
      isRouterAlive: true
    }
  },
  methods: {
    reload () {
      this.isRouterAlive = false
      this.$nextTick(function () {
        this.isRouterAlive = true
      })
    }
  }
}
</script>

<style lang="less">

  .pal-confirm {
    /deep/ .el-message-box__btns {
      .el-button--primary {
        background-color: #3E82FF !important;
      }
      .el-button--primary:hover{
        background-color: #84AFFF !important;
      }
    }
  }
</style>
