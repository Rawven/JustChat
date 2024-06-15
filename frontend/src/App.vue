<template>
  <router-view v-slot="{ Component }">
    <keep-alive>
      <component :is="Component"/>
    </keep-alive>
  </router-view>
</template>
<script>
import {Host} from "@/main";

export default {
  name: 'App',
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data() {
    return {
      gap_time: 0,
      beforeUnload_time: 0,
    };
  },
  created() {

  },
  methods: {
// 关闭窗口之前执行
    beforeunloadHandler() {
      this.beforeUnload_time = new Date().getTime();
    },
    unloadHandler() {
      this.gap_time = new Date().getTime() - this.beforeUnload_time;
      //判断是窗口关闭还是刷新 毫秒数判断 网上大部分写的是5
      if (this.gap_time <= 10) {
        this.realAxios.post('http://' + Host + ':7000/auth/logout', {}, {
          headers: {
            'token': localStorage.getItem("token")
          }
        })
        localStorage.removeItem("token");
        localStorage.removeItem("userData");
        this.$router.push('/common/login');
      } else {
        console.log(document.domain);
        return confirm("确定要离开本系统么？");
      }
    },
  },
  unmounted() {//vue可换为destroyed()生命周期，不过这个也可以用
    // 移除监听
    window.removeEventListener("beforeunload", () => this.beforeunloadHandler());
    window.removeEventListener("unload", () => this.unloadHandler());
  },
  mounted() {
    // 监听浏览器关闭
    window.addEventListener("beforeunload", () => this.beforeunloadHandler());
    window.addEventListener("unload", () => this.unloadHandler());
  }
}
</script>

<style>
html, body {
  height: 100%;
  width: 100%;
  margin: 0;
  padding: 0;
}


</style>
