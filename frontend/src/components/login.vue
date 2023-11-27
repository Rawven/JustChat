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
              <el-form-item label="用户名" prop="username">
                <el-input v-model="user.username" prefix-icon="el-icon-user"></el-input>
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="user.password" prefix-icon="el-icon-lock" type="password"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" round @click="login" class="button2">登录</el-button>
              </el-form-item>
              <el-button type="text" @click="toRegister" class="button">还没有账号？</el-button>
            </el-form>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-row>
            <el-switch
                v-model="value2"
                class="ml-2"
                style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
            />
          </el-row>
          <el-row>
            <el-text type="warning" v-if="value2">今日你若冷眼旁待</el-text>
            <el-text type="warning" v-else>他人祸临己身 无人为你摇旗呐喊</el-text>
          </el-row>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script>
import HeaderH from "@/components/Header.vue";
import {Host} from "@/main";
import {ref} from "vue";

export default {

  name: 'accountName',
  // eslint-disable-next-line vue/no-unused-components
  components: {HeaderH},
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
                localStorage.setItem("token", response.data.data);
                this.realAxios.post('http://' + Host + ':7000/info/defaultInfo', {}, {
                  headers: {
                    'token': localStorage.getItem("token")
                  }
                }).then(response1 => {
                  localStorage.setItem("userData", JSON.stringify(response1.data.data));
                })
                this.$router.push('/mainPage');
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
.calendar {
  width: auto;
}

.button {
  margin-left: 100px;
}

.button2 {
  margin-left: 50px;
}

.image {
  margin-left: 50px;
  border-radius: 50%; /* 设置边框半径为 50%，使图片变成圆形 */
}

.container {
  top: 65px; /* 设置距离顶部为 0 */
  left: 0; /* 设置距离左侧为 0 */
  position: relative; /* 设置定位类型为绝对定位 */
  margin: 10px;
  background-color: #f5f5f5; /* 设置背景颜色为灰色 */
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
