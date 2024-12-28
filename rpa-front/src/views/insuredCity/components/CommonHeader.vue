<template>
    <div class="list-title">
        <div style="display: flex; justify-content: start; align-items: center">
            <div class="blue-line"></div>
            <div class="area-text">{{header}}</div>
        </div>
    </div>
</template>
<script>
export default {
    name:'CommonHeader',
    props:{
        header:{
            type:String,
            require:true,
            default:'标题'
        }
    }
}
</script>
<style lang="less" scoped>
.list-title {
    background: #f8f8f8;
    border-radius: 1px;
    display: flex;
    align-items: center;
    padding: 10px;
    justify-content: space-between;

    .blue-line {
        width: 6px;
        height: 30px;
        background: #3e82ff;
        border-radius: 3px;
    }

    .area-text {
        margin-left: 10px;
        font-size: 16px;
        font-weight: bold;
    }
}
</style>