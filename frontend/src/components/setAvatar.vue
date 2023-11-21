<template>
  <div>
    <h2>设置头像</h2>
    <input type="file" @change="uploadAvatar" accept="image/*" />
    <el-button type="primary" @click="uploadAvatar">上传头像</el-button>
  </div>
</template>

<script>
import axios from 'axios';
import header from "@/components/Header.vue";

export default {
  name: 'setAvatar',
  methods: {
    uploadAvatar(event) {
      const selectedFile = event.target.files[0];
      if (selectedFile) {
        // 文件存在，执行上传逻辑
        const formData = new FormData();
        formData.append('avatar', selectedFile);
        let item = localStorage.getItem("token");
        axios.post('http://localhost:8080/ipfs/upload', formData,
        {
          headers: {
            'Authorization': item
          }
        })
            .then(response => {
              console.log('Avatar upload successful:', response.data);
              // Additional logic after successful avatar upload
            })
            .catch(error => {
              console.error('Avatar upload error:', error);
            });
      } else {
        this.$message.error('请选择要上传的头像文件。');
      }
    },
  },
};
</script>

<style scoped>
/* 根据需要添加样式 */
</style>
