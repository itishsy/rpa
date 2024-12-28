<!--
  changeTab--切换tab触发的事件，参数obj={ active: index, item: item }
-->
<template>
  <div :class="'pal-tabs display-flex pal-tabs' + type">
    <span class="span-tabs"  v-for="(item, index) in tabs" :key="index" @click="handleClickTabs(index, item)">
      <div v-if="enableTips">
        <el-tooltip v-if="item.tips" class="item" effect="dark" :content="item.tips" placement="top">
          <div class="tabs-div">
            <div class="w-p100" :class="{ active: index === splActive }" v-html="item.html"></div>
            <i v-show="index != (tabs.length - 1)" class="el-icon-arrow-right right"></i>
          </div>
        </el-tooltip>
        <div v-else class="tabs-div">
          <div class="w-p100" :class="{ active: index === splActive }" v-html="item.html"></div>
          <i v-show="index != (tabs.length - 1)" class="el-icon-arrow-right right"></i>
        </div>
      </div>
      <div v-if="custom">
        <div class="w-p100" :class="{ active: index === splActive }">
          <slot :row="item"></slot>
        </div>
        <i v-show="index != (tabs.length - 1)" class="el-icon-arrow-right right"></i>
        </div>
      <div v-else class="tabs-div">
        <div class="w-p100" :class="{ active: index === splActive }" v-html="item">
        </div>
        <i v-show="index != (tabs.length - 1)" class="el-icon-arrow-right right"></i>
      </div>
    </span>
    <div class="flex1 pt-10 text-right pr-20" >
      <slot name="btns"></slot>
    </div>
  </div>
</template>
<script>
export default {
  name: 'pal-tab',
  data() {
    return {
      splActive: 0
    }
  },
  props: {
    tabs: {
      type: [Array],
      default: function () {
        return []
      },
      required: true
    },
    value: {
      type: Number,
      default: 0,
      required: false
    },
    type: {
      type: Number,
      default: 0,
      required: false
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    enableTips: {
      type: Boolean,
      default: false
    },
    //完全自定义
    custom:{
      type:Boolean,
      default:false
    }
  },
  model: {
    prop: 'value',
    event: 'change'
  },
  components: {},
  watch: {
    value(newValue, oldValue) {
      this.splActive = this.value
    }
  },
  created() {
    this.splActive = this.$lodash.isEmpty(this.value) ? this.value : 0
  },
  methods: {
    /* tab切换 */
    handleClickTabs(index, item) {
      if (this.disabled) {
        return
      }
      this.splActive = index
      this.$emit('change', index)
      this.$emit('changeTab', { active: index, item: item })
    }
  }
}
</script>

<style lang='less' scoped>
.pal-tabs0 {
  border-bottom: 1px solid #E5E5E5;
  /*width: 100%;*/
  height: 50px;
  text-align: center;
  background: #FFFFFF;
  padding-bottom: 2px;

  .span-tabs {
    font-size: 16px;
    font-family: Microsoft YaHei;
    font-weight: 400;
    color: #606266;
    position: relative;
    display: inline-block;
    height: 100%;
    padding: 0 20px;
    text-align: center;
    line-height: 50px;
    cursor: pointer;

    span {
      display: inline-block;
      height: 100%;
    }

    .right {
      position: absolute;
      right: -7px;
      top: 18px;
      font-size: 16px;
    }

    /*.ic-tab-active {*/
    /*position: absolute;*/
    /*left: 50%;*/
    /*bottom: 0;*/
    /*margin-left: -9px;*/
    /*width: 18px;*/
    /*height: 16px;*/
    /*background: url('../../assets/images/icons/ic-account-type16.png');*/
    /*}*/
  }

  .active {
    border-bottom: 3px solid #3E82FF;
    color: #3E82FF;
  }
}

.pal-tabs1 {
  /*width: 100%;*/
  height: 40px;
  border-bottom: 1px solid rgba(206, 206, 206, 1);

  .span-tabs {
    font-size: 16px;
    font-family: Microsoft YaHei;
    font-weight: 400;
    color: #606266;
    position: relative;
    display: inline-block;
    padding: 0 20px;
    height: 39px;
    line-height: 39px;
    text-align: center;
    cursor: pointer;
    background: rgba(248, 248, 248, 1);
    border: 1px solid rgba(206, 206, 206, 1);
    border-bottom: none;
    border-radius: 5px 5px 0px 0px;
    margin-right: 10px;

    span {
      display: inline-block;
      height: 100%;
    }

    .right {
      position: absolute;
      right: -7px;
      top: 18px;
      font-size: 16px;
    }

    .ic-tab-active {
      display: none;
    }
  }

  .active {
    background: #fff;
    color: @blueColor;
    border-color: @blueColor;
    border-bottom: 1px solid #ffffff;
  }
}

.pal-tabs2 {
  /*width: 100%;*/
  height: 50px;
  background: #f8f8f8;

  .span-tabs {
    display: inline-block;
    font-size: 16px;
    color: #606266;
    position: relative;
    min-width: 150px;
    height: 47px;
    line-height: 47px;
    text-align: center;
    cursor: pointer;
    border-bottom: 3px solid #F8F8F8;

    span {
      display: inline-block;
      height: 100%;
      width: 100%;
    }

    .right {
      display: none;
    }

    /deep/.ic-tab-active {
      display: none;
    }
  }

  .active {
    background: #ECF6FD;
    color: @blueColor;
    border-bottom: 3px solid @blueColor;
  }
}
.tabs-div{
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
