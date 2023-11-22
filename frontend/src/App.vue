<template>
  <!-- 你的业务内容放在这里 -->
  <el-container>
    <el-header>
      <HeaderH></HeaderH>
    </el-header>
    <el-main>
      <keep-alive>
        <router-view/>
      </keep-alive>
    </el-main>
  </el-container>
</template>

<script setup>
import HeaderH from "@/components/Header.vue";
</script>

<style scoped>

</style>
