<!-- src/components/Login.vue -->

<template>
  <div class="background-container">
    <el-container class="login-container">
      <el-main>
        <el-form :model="form" :rules="rules" ref="loginForm" label-width="80px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input type="password" v-model="form.password" placeholder="请输入密码"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="login">登录</el-button>
          </el-form-item>
        </el-form>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { ref } from 'vue';
import {ElForm, ElFormItem, ElInput, ElButton, ElMessage} from 'element-plus';
import axios from 'axios';
import router from "@/router";
export default {
  components: {
    ElForm,
    ElFormItem,
    ElInput,
    ElButton
  },
  setup() {
    const form = ref({
      username: '',
      password: ''
    });

    const rules = ref({
      username: [
        { required: true, message: 'Please enter your username', trigger: 'blur' }
      ],
      password: [
        { required: true, message: 'Please enter your password', trigger: 'blur' }
      ]
    });

    const login = () => {
      // 处理登录逻辑
      console.log('Username:', form.value.username);
      console.log('Password:', form.value.password);
      // 这里可以发起登录请求，例如使用axios或fetch
      axios.post( 'localhost:8080/api/login', {
        username: form.value.username,
        password: form.value.password
      }).then((response) => {
        console.log(response);
        if (response.data.code === 200) {
          // 登录成功
          // 跳转到首页
          router.push('/');
        } else {
          // 登录失败
          // 提示错误信息
          ElMessage.error(response.data.message);
        }
      }).catch((error) => {
        console.log(error);
      }
      );

    };

    return {
      form,
      rules,
      login
    };
  }
};
</script>

<style scoped>
.background-container {
  background: url('/pexels-felix-mittermeier-956999.jpg') center center/cover;
  min-height: 100vh;
  min-width: 100vw;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  position: relative;
}

.login-container {
  padding: 20px;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  max-width: 400px; /* 控制登录框的最大宽度 */
  width: 100%; /* 使登录框充满容器 */
  text-align: center; /* 文本居中 */
}

h1 {
  text-align: center;
  color: #333;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-button {
  width: 100%;
  border-radius: 5px;
}
</style>
