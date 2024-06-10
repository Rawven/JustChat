<template>
  <el-container class="JcContainer">
    <el-card class="login-card">
      <el-image
          class="image"
          fit="contain"
          src="public/pexels-laura-meinhardt-3678799.jpg"
          style="width: 200px; height: 200px"
      ></el-image>
      <br>
      <el-text class="text-center" style="font-size: 20px; font-weight: bold">？</el-text>
      <el-form ref="loginForm" :model="user" :rules="rules" class="login-form" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="user.username" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="user.password" prefix-icon="el-icon-lock" type="password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button plain type="success" @click="login">Login</el-button>
        </el-form-item>
      </el-form>

      <el-button class="button" plain type="primary" @click="toRegister">还没有账号？</el-button>
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
                localStorage.setItem("token", response.data.data.token);
                localStorage.setItem("expireTime", response.data.data.expireTime);
                // 创建WebSocket连接
                const socket = new WebSocket(`ws://` + Host + `:7000/ws/${localStorage.getItem("token")}`);
                this.$global.setWs(socket);
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


.login-card {
  width: 400px;
  padding: 20px;
  border-radius: 10px;
}

</style>
