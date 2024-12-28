<template>
  <div>
    <el-row type="flex" style="flex-wrap:wrap">
      <slot></slot>
    </el-row>
  </div>
</template>

<script>
export default {
  components: {},
  props: {
    column: {
      type: [Number, String],
      default: 8,
    },
  },
  data() {
    return {}
  },
  computed: {},
  created() {},
  mounted() {},
  methods: {},
}
</script>
<style lang="less" scoped>
</style>