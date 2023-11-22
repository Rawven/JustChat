<script>
export default {
  // eslint-disable-next-line vue/multi-word-component-names
  name: "headerH"
}
</script>

<template>
  <el-header class="navbar">
    <div class="logo">Raven的聊天室项目</div>
    <el-link href="https://github.com/UWEPPPP" target="_blank" type="primary">访问 GitHub</el-link>
  </el-header>
</template>


<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000; /* 设置一个较大的 z-index，确保 HeaderH 在最上层 */
}
</style>