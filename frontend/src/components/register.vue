<template>
  <div>
    <h2>Register</h2>
    <el-form :model="user" :rules="rules" ref="registerForm" label-width="80px">
      <el-form-item label="Username" prop="username">
        <el-input v-model="user.username" required></el-input>
      </el-form-item>
      <el-form-item label="Email" prop="email">
        <el-input v-model="user.email" required></el-input>
      </el-form-item>
      <el-form-item label="Password" prop="password">
        <el-input type="password" v-model="user.password" required></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="register">Register</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'userRegister',
  data() {
    return {
      user: {
        username: '',
        email: '',
        password: '',
      },
      rules: {
        // Add validation rules if needed
        username: [{ required: true, message: 'Please enter your username', trigger: 'blur' }],
        email: [
          { required: true, message: 'Please enter your email', trigger: 'blur' },
        ],
        password: [{ required: true, message: 'Please enter your password', trigger: 'blur' }],
      },
    };
  },
  methods: {
    register() {
      // 表单校验
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          // 发送注册请求
          axios.post('http://localhost:8080/account/register', this.user)
              .then(response => {
                console.log('Registration successful:', response.data);
                localStorage.setItem("token", response.data);
                // 注册成功后可以进行相关的处理，例如跳转到登录页面
                this.$router.push('/login');
              })
              .catch(error => {
                console.error('Registration error:', error);
              });
        } else {
          this.$message.error('Please fill in all required fields.');
        }
      });
    },
  }
};
</script>

<style scoped>
/* 根据需要添加样式 */
</style>
