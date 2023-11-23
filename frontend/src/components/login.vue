<template>
  <el-container class="container">

    <el-card>
      <el-image
          fit="contain"
          src="https://avatars.githubusercontent.com/u/121878866?s=400&u=44ed833ce20456153341fb5fa7620190c9aaabdd&v=4"
          style="width: 200px; height: 200px"
      ></el-image>
      <el-form ref="loginForm" :model="user" :rules="rules" class="loginK" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="user.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="user.password" placeholder="请输入密码" type="password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login">登录</el-button>
        </el-form-item>
        <el-button type="primary" @click="toRegister">还没有账号？</el-button>
      </el-form>
    </el-card>
  </el-container>
</template>

<script>
import axios from 'axios';
import HeaderH from "@/components/Header.vue";
import {Host} from "@/main";

export default {
  name: 'accountName',
  // eslint-disable-next-line vue/no-unused-components
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
    toRegister() {
      this.$router.push('/register');
    },
    login() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          // 发送登录请求
          axios.post('http://'+Host+':7000/account/login', this.user)
              .then(response => {
                console.log('Registration successful:', response.data);
                localStorage.setItem("token", response.data.data);
                let token = localStorage.getItem("token");
                axios.post('http://'+Host+':7000/account/defaultInfo', {}, {
                  headers: {
                    'token': token
                  }
                }).then(response => {
                  console.log('GetUserInfo successful:', response.data);
                  localStorage.setItem("userData", response.data.data);
                })
                this.$router.push('/mainPage');
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


.loginK {
  border-radius: initial;
  margin-top: 50px; /* 调整上方的间距 */
  margin-bottom: 50px; /* 调整下方的间距 */
  /* 其他样式保持不变 */
}


</style>
