<template>
  <el-container>
    <el-card class="box-card">
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
        <el-form-item>
          <el-button type="primary" @click="register">Register</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </el-container>
</template>

<script>
import HeaderH from "@/components/Header.vue";
import {Host} from "@/main";

export default {
  name: 'userRegister',
  // eslint-disable-next-line vue/no-unused-components
  components: {HeaderH},
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
      },
      rules: {
        // Add validation rules if needed
        username: [{required: true, message: 'Please enter your username', trigger: 'blur'}],
        email: [
          {required: true, message: 'Please enter your email', trigger: 'blur'},
        ],
        password: [{required: true, message: 'Please enter your password', trigger: 'blur'}],
      },
    };
  },
  methods: {
    register() {
      // 表单校验
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          // 发送注册请求
          this.realAxios.post('http://' + Host + ':7000/account/register', this.user)
              .then(response => {
                localStorage.setItem("token", response.data.data);
                // 注册成功后可以进行相关的处理，例如跳转到登录页面
                this.$router.push('/setAvatar');
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
.box-card {
  position: absolute; /* 设置定位类型为绝对定位 */
  top: 300px; /* 设置距离顶部的距离 */
  left: 600px; /* 设置距离左侧的距离 */
  margin-right: 100px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 50%; /* 设置卡片宽度为70% */
  height: 50%; /* 设置卡片高度为70% */

}

/* 根据需要添加样式 */
</style>
