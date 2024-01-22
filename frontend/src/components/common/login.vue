<template>
  <el-container class="container">
    <el-main class="tMain">
      <el-row>
        <el-col :span="12">
          <el-card class="box-card">
            <el-image
                class="image"
                fit="contain"
                src="https://avatars.githubusercontent.com/u/121878866?s=400&u=44ed833ce20456153341fb5fa7620190c9aaabdd&v=4"
                style="width: 200px; height: 200px"
            ></el-image>
            <el-form ref="loginForm" :model="user" :rules="rules" class="login-form" label-width="80px">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="user.username" prefix-icon="el-icon-user"></el-input>
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="user.password" prefix-icon="el-icon-lock" type="password"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button class="button2" round type="primary" @click="login">登录</el-button>
              </el-form-item>
              <el-button class="button" type="text" @click="toRegister">还没有账号？</el-button>
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
      this.$router.push('/common/register');
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
                this.$router.push('/common/mainPage');
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

.button2 {
  margin-left: 70px;
}

.image {
  margin-left: 30px;
  border-radius: 50%; /* 设置边框半径为 50%，使图片变成圆形 */
}

.container {
  position: fixed; /* 设置定位类型为固定定位 */
  top: 0; /* 设置距离顶部为 0 */
  left: 0; /* 设置距离左侧为 0 */
  right: 0; /* 设置距离右侧为 0 */
  bottom: 0; /* 设置距离底部为 0 */
  background-color: #f5f5f5; /* 设置背景颜色为灰色 */
}

.tMain {
  width: 100%; /* 设置宽度为100% */
  height: 100%; /* 设置高度为100% */
}

.box-card {
  position: absolute; /* 设置定位类型为绝对定位 */
  top: 50%; /* 设置距离顶部为50% */
  left: 50%; /* 设置距离左侧为50% */
  transform: translate(-50%, 25%); /* 向左和向上移动50% */
  width: 400px;
}

.login-form {
  margin-top: auto; /* 设置上边距为自动 */
  margin-bottom: auto; /* 设置下边距为自动 */
}

</style>
