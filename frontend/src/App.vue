<template>
  <!-- 你的业务内容放在这里 -->
  <el-container>
    <el-header>
      <HeaderH></HeaderH>
    </el-header>
    <el-main class="thisContainer">
      <div>
        <keep-alive>
          <router-view/>
        </keep-alive>
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import HeaderH from "@/components/Header.vue";
import axios from 'axios';
import {provide} from 'vue'
import {ElMessage} from 'element-plus'
// 创建一个axios实例
const axiosFilter = axios.create();
// 添加请求拦截器
axiosFilter.interceptors.request.use(function (config) {
  // 在发送请求之前做些什么
  console.log("开始调用,URL" + config.url + ",method:" + config.method + ",data:" + JSON.stringify(config.data))
  return config;
}, function (error) {
  // 对请求错误做些什么
  return Promise.reject(error);
});
// 添加响应拦截器
axiosFilter.interceptors.response.use(function (response) {
  // 对响应数据做点什么
  if (response.data.code !== 200) {
    ElMessage.error(response.data.message);
    return Promise.reject(new Error(response.data.message));
  }
  if (response.headers.get("new-Token") != null) {
    console.log("令牌过期，自动刷新");
    let new1 = response.headers.get("new-Token");
    localStorage.setItem("token", String(new1));
  }
  console.log("调用成功,URL" + response.config.url + ",data:" + JSON.stringify(response.data))
  return response;
}, function (error) {
  // 对响应错误做点什么
  return Promise.reject(error);
});
// 提供axiosFilter
provide('axiosFilter', axiosFilter);

</script>

<style scoped>
.thisContainer {
  height: 100%;
  width: 100%;
  background-color: #f2f2f2;
  overflow: auto;
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  margin: 10px;
}

</style>
