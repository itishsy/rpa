<template>
    <div class="display-flex">
        <el-select v-for="(option, index) in options" :key="index" v-model="selectedValues[index]"
            :placeholder="'请选择' + option.label" class="my-select">
            <el-option v-for="(item, i) in option.items" :key="i" :label="item.label" :value="item.value"></el-option>
        </el-select>
    </div>
</template>
<script>
export default {
    props: {
        options: {
            type: Array,
            required: true
        },
        defaultValue: {
            type: Array,
            required: true
        }
    },
    data() {
        return {
            selectedValues: []
        }
    },
    mounted() {
        this.selectedValues = this.defaultValue
    },
    watch: {
        selectedValues: {
            handler(val) {
                // const filteredArr = val.filter(item => item.trim() !== "");
                this.$emit('input', val)
            },
            deep: true
        }
    }
}
</script>
<style lang='less' scoped>
.my-select {
    width: 100px;
}
</style>