<template>
  <div :style="{minHeight: containerHeight}">
    <div class="noPower" :style="{height: noPowerHeight}">
      <div class="noPower-img">
        <img class="img" src="../../assets/images/index-noPower-img.png">
        <p class="title">无此页面或暂无权限，如需查看数据，请联系系统管理员</p>
        <!-- <p class="goback" @click="goIndex">点我返回首页</p> -->
      </div>
    </div>
  </div>
</template>
<script>
  export default {
    data () {
      return {
      }
    },
    computed: {
      containerHeight: function () {
        return this.$global.bodyHeight - 100 + 'px'
      },
      noPowerHeight: function () {
        return this.$global.bodyHeight - 40 + 'px'
      }
    },
    created () {
      
    },
    watch: {
    },
    methods: {
      goIndex(){
        this.$router.push('/index')
      }
    }
  }
</script>
<style lang="less" scoped>
  .noPower{
    background-color: #ffffff;
    width: 100%;
    position: relative;
    text-align: center;
    // padding-top: 20px;
    border-top: 20px solid @bodyBaseBgc;
    .noPower-img{
      position: absolute;
      left: 50%;
      top: 50%;
      margin-left: -141px;
      margin-top: -105px;
    }
    .title{
      font-size: 14px;
      color: #909399;
    }
    .goback{
      color:#3E82FF;
      cursor: pointer;
      margin-top:20px;
    }
  }
</style>
