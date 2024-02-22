<template>
  <el-container class="login-container">
          <el-card class="login-card">
            <el-image
                class="image"
                fit="contain"
                src="https://avatars.githubusercontent.com/u/121878866?s=400&u=44ed833ce20456153341fb5fa7620190c9aaabdd&v=4"
                style="width: 200px; height: 200px"
            ></el-image>
            <br>
            <el-text class="text-center" style="font-size: 20px; font-weight: bold">黑夜为何嚎哭？</el-text>
            <el-form ref="loginForm" :model="user" :rules="rules" class="login-form" label-width="80px">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="user.username" prefix-icon="el-icon-user"></el-input>
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="user.password" prefix-icon="el-icon-lock" type="password"></el-input>
              </el-form-item>
              <el-form-item >
                <el-button type="success"  @click="login" plain>Login</el-button>
              </el-form-item>
            </el-form>
            <el-button class="button" type="text" @click="toRegister" plain>还没有账号？</el-button>
          </el-card>
  </el-container>
</template>
<script>
import {Host} from "@/main";
import {ref} from "vue";

export default {

  name: 'accountName',
  // eslint-disable-next-line vue/no-unused-components
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data() {
    return {
      value2: ref(true),
      value: ref(new Date()),
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
          this.realAxios.post('http://' + Host + ':7000/auth/login', this.user)
              .then(response => {
                localStorage.removeItem("token");
                localStorage.setItem("token", response.data.data);
                this.realAxios.post('http://' + Host + ':7000/user/common/defaultInfo', {}, {
                  headers: {
                    'token': localStorage.getItem("token")
                  }
                }).then(response1 => {
                  localStorage.setItem("userData", JSON.stringify(response1.data.data));
                })
                this.$router.push('/main');
              })
        } else {
          this.$message.error('Please fill in all required fields.');
        }
      });
    }
  }
}
</script>

<style scoped>

.button {
  margin-left: 100px;
}


.image {
  margin-left: 50px;
  border-radius: 50%; /* 设置边框半径为 50%，使图片变成圆形 */
}


.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
  background-image: url('/pexels-eberhard-grossgasteiger-691668.jpg'); /* 替换成你的背景图链接 */
  background-size: cover; /* 背景图铺满整个容器 */
  background-position: center; /* 背景图居中 */
}

.login-card {
  width: 400px;
  padding: 20px;
  border-radius: 10px;
}

</style>
