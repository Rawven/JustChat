<template>
  <el-container class="container">

    <el-card class="box-card">
      <el-row class="title">选择自己的头像吧！😄</el-row>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-avatar size="large" src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png"></el-avatar>
        </el-col>
        <el-col :span="18">
          <div class="grid-content bg-purple">
            <input accept="image/*" type="file" @change="selectFile"/>
            <el-button type="primary" @click="uploadAvatar">上传头像</el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </el-container>
</template>

<script>
import {Host} from "@/main";

export default {
  name: 'setAvatar',
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  methods: {
    selectFile(event) {
      this.selectedFile = event.target.files[0];
      if (!this.selectedFile) {
        this.$message.error('请选择要上传的头像文件。');
      }
    },
    uploadAvatar() {
      if (this.selectedFile) {
        // 文件存在，执行上传逻辑
        let formData = new FormData();
        formData.append('file', this.selectedFile);
        let item = localStorage.getItem("token");
        this.realAxios.post('http://' + Host + ':7000/info/setProfile', formData,
            {
              headers: {
                'token': item
              }
            })
            .then(() => {
              this.realAxios.post('http://' + Host + ':7000/info/user/defaultInfo', {}, {
                headers: {
                  'token': localStorage.getItem("token")
                }
              }).then(response => {
                 localStorage.setItem("userData", JSON.stringify(response.data.data));
              })
              this.$router.push('/common/mainPage');
              // Additional logic after successful avatar upload
            })
      } else {
        this.$message.error('请选择要上传的头像文件。');
      }
    },
  },
};
</script>

<style scoped>
.title {
  font-size: 24px; /* 设置字体大小 */
  color: #409EFF; /* 设置字体颜色 */
  font-weight: bold; /* 设置字体为粗体 */
  text-align: center; /* 设置文字居中 */
  margin-bottom: 20px; /* 设置下边距 */
  animation: fadeIn 2s; /* 添加淡入动画 */
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
  top: 300px; /* 设置距离顶部的距离 */
  left: 600px; /* 设置距离左侧的距离 */
  margin-right: 100px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 50%; /* 设置卡片宽度为70% */
  height: 50%; /* 设置卡片高度为70% */

}

.bg-purple {
  background: #d3dce6;
  padding: 20px;
  border-radius: 4px;
}

/* 根据需要添加样式 */
</style>
