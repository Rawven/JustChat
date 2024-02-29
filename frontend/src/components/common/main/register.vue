<template>
  <el-container class="JcContainer">
    <el-card class="box-card">
      <el-header>
        <el-text class="title">来注册一个账号吧👆👨</el-text>
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
        <el-text>上传图片</el-text>
        <el-upload
            :http-request="uploadFile"
            action=""
            class="upload-demo">
          <el-button plain size="small" type="primary">点击上传</el-button>
          <template #tip>
            <div class="el-upload__tip">
              jpg/png files with a size less than 500KB.
            </div>
          </template>
        </el-upload>
        <el-form-item>
          <el-button plain type="primary" @click="register">Register</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </el-container>
</template>

<script>
import {Host} from "@/main";

export default {
  name: 'userRegister',
  // eslint-disable-next-line vue/no-unused-components
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
        profile: '',
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
    uploadFile(param) {
      const formData = new FormData()
      formData.append('file', param.file)
      const url = 'http://' + Host + ':7000/file/upload'
      this.realAxios.post(url, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        }
      }).then(response => {
        this.user.profile = response.data.data;
        this.$message.success('文件上传成功');
      })
    },
    register() {
      // 表单校验
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          // 发送注册请求
          this.realAxios.post(`http://` + Host + `:7000/auth/register`, this.user)
              .then(response => {
                localStorage.setItem("token", response.data.data.token);
                // 注册成功后可以进行相关的处理，例如跳转到登录页面
                this.$message.success('注册成功');
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

/* 根据需要添加样式 */
</style>
