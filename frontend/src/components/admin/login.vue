<template>
  <el-container class="container">
    <el-aside>
      <el-calendar class="calendar" v-model="value"/>
    </el-aside>
    <el-main class="tMain">
      <el-row>
        <el-col :span="12">
          <el-card class="box-card">
            <el-image
                fit="contain"
                src="https://avatars.githubusercontent.com/u/121878866?s=400&u=44ed833ce20456153341fb5fa7620190c9aaabdd&v=4"
                class="image"
                style="width: 200px; height: 200px"
            ></el-image>
            <el-form ref="loginForm" :model="user" :rules="rules" class="login-form" label-width="80px">
              <el-form-item label="管理员名" prop="username">
                <el-input v-model="user.username" prefix-icon="el-icon-user"></el-input>
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="user.password" prefix-icon="el-icon-lock" type="password"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" round @click="login" class="button2">登录</el-button>
              </el-form-item>
              <el-button type="text" @click="toRegister" class="button">申请管理员账号🌳</el-button>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script>
import {Host} from "@/main";
import {ref} from "vue";

export default {
  name: 'adminLogin',
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data() {
    return {
      value: ref(new Date()),
      user: {
        username: '',
        password: '',
      },
      rules: {
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
          this.realAxios.post('http://' + Host + ':7000/auth/login', this.user)
              .then(response => {
                localStorage.setItem("token", response.data.data);
                this.$router.push('/admin/mainPage');
              })
        } else {
          this.$message.error('Please fill in all required fields.');
        }
      });
    },
    toRegister() {
      this.$router.push('/admin/register');
    },
  }
}
</script>

<style scoped>
.calendar {
  width: auto;
}

.button2 {
  margin-left: 50px;
}

.image {
  margin-left: 50px;
  border-radius: 50%;
}

.container {
  top: 65px;
  left: 0;
  position: relative;
  margin: 10px;
  background-color: #f5f5f5;
}

.tMain {
  width: 500px;
}

.box-card {
  margin-left: 100px;
  width: 400px;
}

.login-form {
  margin-top: 20px;
}
</style>