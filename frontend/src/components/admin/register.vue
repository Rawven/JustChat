<template>
  <el-container>
    <el-card class="box-card">
      <el-header>
        <el-text class="title">来注册一个管理员账号吧👆👨</el-text>
      </el-header>
      <el-form ref="registerForm" :model="user" :rules="rules" label-width="80px">
        <el-form-item label="Username" prop="username">
          <el-input v-model="user.username" required></el-input>
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="user.email" required></el-input>
        </el-form-item>
        <el-form-item label="Password" prop="password">
          <el-input v-model="user.password" required type="password"></el-input>
        </el-form-item>
        <el-form-item label="Secret Key" prop="secretKey">
          <el-input v-model="user.privateKey" required></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="register">Register</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </el-container>
</template>

<script>
import {Host} from "@/main";

export default {
  name: 'adminRegister',
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data() {
    return {
      user: {
        username: '',
        email: '',
        password: '',
        privateKey: '',
      },
      rules: {
        username: [{required: true, message: 'Please enter your username', trigger: 'blur'}],
        email: [
          {required: true, message: 'Please enter your email', trigger: 'blur'},
        ],
        password: [{required: true, message: 'Please enter your password', trigger: 'blur'}],
        privateKey: [{required: true, message: 'Please enter your secret key', trigger: 'blur'}],
      },
    };
  },
  methods: {
    register() {
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          this.realAxios.post('http://' + Host + ':7000/auth/registerAdmin', this.user)
              .then(response => {
                localStorage.setItem("token", response.data.data);
                this.$router.push('/admin/mainPage');
              })
        } else {
          this.$message.error('Please fill in all required fields.');
        }
      });
    },
  }
};
</script>

<style scoped>
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
.box-card {
  position: absolute; /* 设置定位类型为绝对定位 */
  top: 175px; /* 设置距离顶部的距离 */
  left: 405px; /* 设置距离左侧的距离 */
  margin-right: 100px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 50%; /* 设置卡片宽度为70% */
  height: 50%; /* 设置卡片高度为70% */

}
</style>