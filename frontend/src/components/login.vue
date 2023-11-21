<template>
    <el-container class="container">
      <el-header >
        <HeaderH></HeaderH>
      </el-header>
      <el-main class="center">
        <el-image
            style="width: 200px; height: 200px"
            src="https://avatars.githubusercontent.com/u/121878866?s=400&u=44ed833ce20456153341fb5fa7620190c9aaabdd&v=4"
            fit="contain"
        ></el-image>
        <el-form class="loginK" :model="user" :rules="rules" ref="loginForm" label-width="80px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="user.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input type="password" v-model="user.password" placeholder="请输入密码"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="login">登录</el-button>
          </el-form-item>
        </el-form>
      </el-main>
    </el-container>
</template>

<script>
import axios from 'axios';
import HeaderH from "@/components/Header.vue";

export default {
  name: 'accountName',
  components: {HeaderH},
  data() {
    return {
      user: {
        username: '',
        password: '',
      },
      rules: {
        // Add validation rules if needed
        username: [{required: true, message: 'Please enter your username', trigger: 'blur'}],
        password: [{required: true, message: 'Please enter your password', trigger: 'blur'}],
      },
    };
  },
  methods: {
    login() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          // 发送登录请求
          axios.post('http://localhost:8080/account/login', this.user)
              .then(response => {
                console.log('Registration successful:', response.data);
                localStorage.setItem("token", response.data);
              })
              .catch(error => {
                console.error('Registration error:', error);
              });
        } else {
          this.$message.error('Please fill in all required fields.');
        }
      });
    }
  }
}
</script>

<style scoped>


.container {
  width: 100%;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh; /* 让 .center 占满整个视口高度 */
}
.center {
  margin-left: 500px;
  margin-bottom: 50px;
  width: 100%;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh; /* 让 .center 占满整个视口高度 */
}


.loginK{
 border-radius: initial;
  margin-top: 50px; /* 调整上方的间距 */
  margin-bottom: 50px; /* 调整下方的间距 */
  /* 其他样式保持不变 */
}






</style>
