<template>
     <el-backtop  :visibility-height="visibilityHeight" :right="right" :bottom="bottom"></el-backtop>
</template>
<script>
export default {
    name:'backtop',
    props:{
        //滚动高度达到此参数值才出现
        visibilityHeight:{
            type:Number,
            default:50
        },
        //控制其显示位置, 距离页面右边距
        right:{
            type:Number,
            default:40,
        },
        //控制其显示位置, 距离页面底部距离
        bottom:{
            type:Number,
            default:40,
        }
    }
}
</script>
<style lang="less" scoped>

</style>