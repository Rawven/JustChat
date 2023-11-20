<template>
  <div id="app">
    <header>

    </header>

    <keep-alive>
      <router-view />
    </keep-alive>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();


</script>

<style scoped>
#app {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
}

header {
  text-align: center;
}

/* 使用 Element Plus 的样式进行按钮美化 */
.el-button {
  margin-top: 20px;
}
</style>
