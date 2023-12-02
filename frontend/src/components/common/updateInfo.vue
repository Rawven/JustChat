<template>
  <el-container class="container">
    <el-card class="box-card">
      <el-form ref="form" :model="form" label-width="120px">
        <el-form-item label="用户名">
          <el-input v-model="form.username"></el-input>
          <el-button type="primary" @click="updateUsername">更新用户名</el-button>
        </el-form-item>
        <el-form-item label="个性签名">
          <el-input v-model="form.signature"></el-input>
          <el-button type="primary" @click="updateSignature">更新个性签名</el-button>
        </el-form-item>
        <el-form-item label="头像">
          <input accept="image/*" type="file" @change="selectFile"/>
          <el-button type="primary" @click="updateAvatar">更新头像</el-button>
        </el-form-item>
        <el-form-item>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </el-container>
</template>

<script>
import {Host} from "@/main";
import {ElMessage} from "element-plus";

export default {
  name: 'UpdateInfo',
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data() {
    return {
      form: {
        username: '',
        signature: '',
      },
      selectedFile: null,
    };
  },
  methods: {
    selectFile(event) {
      this.selectedFile = event.target.files[0];
    },
    updateUsername() {
      let formData = new FormData();
      formData.append('username', this.form.username);
      let item = localStorage.getItem("token");
      this.realAxios.post('http://' + Host + ':7000/user/common/setUsername', formData,
          {
            headers: {
              'token': item
            }
          })
          .then(() => {
            ElMessage.success('更新成功')
            this.getLatestInfo();
          })
    },
    updateSignature() {
      let formData = new FormData();
      formData.append('signature', this.form.signature);
      let item = localStorage.getItem("token");
      this.realAxios.post('http://' + Host + ':7000/user/common/setSignature', formData,
          {
            headers: {
              'token': item
            }
          })
          .then(() => {
            ElMessage.success('更新成功')
            this.getLatestInfo();
          })
    },
    updateAvatar() {
      let formData = new FormData();
      if (this.selectedFile) {
        formData.append('file', this.selectedFile);
      }
      let item = localStorage.getItem("token");
      this.realAxios.post('http://' + Host + ':7000/user/common/setProfile', formData,
          {
            headers: {
              'token': item
            }
          })
          .then(() => {
            ElMessage.success('更新成功')
            this.getLatestInfo();
          })
    },
    getLatestInfo() {
      this.realAxios.post('http://' + Host + ':7000/user/common/defaultInfo', {}, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response1 => {
        localStorage.setItem("userData", JSON.stringify(response1.data.data));
      })
    },
    goBack() {
      this.$router.push('/common/mainPage');
    },
  },
};
</script>

<style scoped>
.container {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 400px;
}

.box-card {
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>