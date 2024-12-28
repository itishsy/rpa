<template>
  <div class="spl-breadcrumb clearfix">
    <el-breadcrumb separator-class="el-icon-caret-right">
      <span @click="goBack" class="cur-router mt-10 back"><i class="ic-breadcrumb-back"></i>返回<span style="color: #C0C4CC;padding: 0 22px;">|</span>
      </span>
      <!--<span class="cur-router"><i class="ic-breadcrumb-home"></i>当前位置：</span>-->
      <div v-if="custLink">
        <el-breadcrumb-item class="f-cursor" v-for="(item, index) in data" :key="item.path">
          <a v-if="item.path" @click="toPath(item, index)">{{item.name}}</a>
          <a v-else style="cursor: initial">{{item.name}}</a>
        </el-breadcrumb-item>
      </div>
      <div v-else>
        <el-breadcrumb-item class="f-cursor" v-for="item in data" :key="item.path">
          <router-link v-if="item.path" :to="item.path" v-text="item.name"></router-link>
          <a v-else style="cursor: initial">
            {{item.name}}
          </a>
        </el-breadcrumb-item>
      </div>
    </el-breadcrumb>
    <div class="breadcrumb-btn">
      <slot name="breadcrumb-btn"></slot>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    // 参数  导航数据
    data: {
      type: Array,
      default: function () {
        return []
      }
    },
    custBack: {
      type: Boolean,
      default: false
    },
    custLink: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {}
  },
  created () {
  },
  computed: {},
  methods: {
    goBack () {
      if (this.custBack) {
        this.$emit('back')
      } else {
        history.go(-1)
      }
      /* let data = this.data
      if (data.length <= 2) {
        history.go(-1)
      } else {
        let backPath = data[data.length-2].path
        this.$router.push(backPath)
      } */
    },
    toPath (item, index) {
      this.$emit('toPath', item, index)
    }
  }
}
</script>
<style lang="less" scoped>

  .mt-10 {
    margin-top: 10px;
  }

  .cur-router {
    float: left;
    color: #888888;
  }

  .back {
    font-size: 14px;
    font-family: Microsoft YaHei;
    font-weight: 400;
    color: #333333;
    cursor: pointer;
  }

  .ic-breadcrumb-home {
    display: inline-block;
    width: 24px;
    height: 20px;
    margin-top: 19px;
    background: url('../../assets/images/icons/ic-breadcrumb-home.png');
    margin-right: 9px;
  }

  .ic-breadcrumb-back {
    display: inline-block;
    width: 16px;
    height: 10px;
    margin-top: 19px;
    background: url('../../assets/images/icons/ic-breadcrumb-back.png');
    margin-right: 9px;
  }

  /*//面包屑*/
  .spl-breadcrumb {
    position: fixed;
    top: 70px;
    /*width: calc(100% - 420px);*/
    width: 80%;
    max-width: 1920px;
    min-width: 1200px;
    // height: 120px;
    background-color: @bodyBaseBgc;
    /*margin-left: -20px;*/
    z-index: 9;
    padding-top: 20px;
    /deep/ .el-breadcrumb {
      padding: 0 30px;
      height: 60px;
      background-color: #F8F8F8;
      box-shadow: 0px 0px 10px 0px rgba(135, 175, 228, 0.1);
      border-radius: 6px 6px 0px 0px;
      margin-bottom: 0px;
      font-family: Microsoft YaHei;
      /*font-size: 15px;*/
      a{
        color: #888888;
        font-weight: normal;
        line-height: 66px;
      }
    }
    /deep/.el-breadcrumb__item {
      float: none;
    }
    /deep/.el-breadcrumb__item .el-breadcrumb__inner a {
      font-size: 16px;
      font-weight: bold;
    }
    /deep/.el-breadcrumb__item:last-child .el-breadcrumb__inner a{
      color: #333333;
    }
    /deep/.el-breadcrumb__separator[class*=icon]{
      padding: 0 10px;
    }
    .spl-index {
      background-color: #fff;
      padding: 20px 240px 20px 40px;
      margin-bottom: 20px;
    }
    .breadcrumb-btn{
      position: absolute;
      top: 35px;
      right: 20px;
    }
  }
</style>
