<template>
<el-container class="container">
<el-header class="header">
  <h1 class="title">这姑且是一个聊天室主页吧😘</h1>
</el-header>
<el-main class="main">
  <el-row type="flex" class="row-bg" justify="center">
    <el-col :span="12" v-if="userInfo">
      <img :src="'http://127.0.0.1:8083/ipfs/'+userInfo.profile" alt="User Avatar" class="avatar">
      <h2 class="username">{{ userInfo.username }}</h2>
      <p class="signature">{{ userInfo.signature }}</p>
      <router-link to="/profile">更改个人信息</router-link>
    </el-col>
    <el-col :span="12">
      <router-link to="/openANewRoom" >
        <template v-slot:default="{ navigate }">
          <button class="button" @click="navigate">建立个新的聊天室</button>
        </template>
      </router-link>
      <el-input placeholder="请输入搜索内容"></el-input>
    </el-col>
  </el-row>
</el-main>
</el-container>
</template>

<script>

export default {
  name: 'MainPage',
  data() {
    return {
      userInfo: null,
    };
  },
   created() {
        let item = localStorage.getItem("userData");
        if (item) {
          this.userInfo = JSON.parse(item);
        }
      }
};
</script>

<style scoped>

.button {
  display: inline-block;
  padding: 10px 20px;
  font-size: 16px;
  color: #fff;
  background-color: #409EFF;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.button:hover {
  background-color: #66B3FF;
}
.title {
  font-size: 24px;
  color: #409EFF;
  font-weight: bold;
  text-align: center;
  margin-bottom: 20px;
  animation: fadeIn 2s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
/* ...existing styles... */

.avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
}

.username {
  font-size: 20px;
  margin-top: 10px;
}

.signature {
  font-size: 16px;
  margin-top: 5px;
  color: #888;
}

.container{
  width: 1200px;
  height: 100vh;
  padding: 10px;
}

.header {
  background-color: #feffb3 ;
  color: #FFF;
  text-align: center;
  line-height: 60px;
  width: 100%;
}

.main {
  background-color: #F5F5F5;
  color: #333;
  text-align: center;
  width: 100%;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.box-card {
  width: 50%;
  margin: 20px auto;
}

.row-bg {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.button {
  width: 200px;
  margin: 20px;
  background-color: #FF4081;
  color: #FFF;
}
</style>