<template>
  <div class="ml-10" style="cursor:pointer">
    <el-tag type="info" :closable="closable" @click="handlePreviewOk" :size="size" @close="remove">
      <i class="el-icon-document"></i>
      <span class="ellipsis" :title="fileName">{{fileName}}</span>
    </el-tag>
  </div>
</template>

<script>
export default {
  props: {
    file: {
      type: Object,
      default: function () {
        return {}
      },
    },
    size: {
      type: String,
      default: 'small',
    },
    fileName: {
      type: [String],
      default: '',
    },
    download: {
      type: Function,
    },
    closable: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {}
  },
  methods: {
    handlePreviewOk() {
      if (!this.file.id) {
        return
      }
      if (this.downLoad) {
        this.download()
        return
      }
      this.$http({
        url: `api/policy/sys/file/download/${this.file.id}`,
        responseType: 'blob',
        baseURL: '',
      }).then((res) => {
        // console.log('es.status', res)
        if (res.status === 200) {
          const blob = new Blob([res.data])
          const a = document.createElement('a')
          const URL = window.URL || window.webkitURL
          const herf = URL.createObjectURL(blob)
          a.href = herf
          a.download = this.file.clientFileName
          document.body.appendChild(a)
          a.click()
          document.body.removeChild(a)
          window.URL.revokeObjectURL(herf)
        }
      })
    },
    remove(event) {
      event.stopPropagation()
      this.$emit('remove', event)
    },
  },
}
</script>

<style lang="less" scoped>
.el-upload-list__item {
  margin-top: 0;
  background: #f3f3f3;
  cursor: pointer;
}
.ellipsis {
  max-width: 200px;
  text-overflow: ellipsis;
  overflow: hidden;
  display: inline-block;
  vertical-align: bottom;
  padding-left:5px;
}
</style>