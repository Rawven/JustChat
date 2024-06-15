<template>
  <el-container class="JcContainer">
    <el-aside>
      <JcAside></JcAside>
    </el-aside>
    <el-main>
      <el-card class="box-card">
        <el-form ref="form" :model="form" label-width="120px">
          <el-form-item label="用户名">
            <el-input v-model="form.username"></el-input>
            <el-button plain type="primary" @click="updateUsername">更新用户名
            </el-button>
          </el-form-item>
          <el-form-item label="个性签名">
            <el-input v-model="form.signature"></el-input>
            <el-button plain type="primary" @click="updateSignature">
              更新个性签名
            </el-button>
          </el-form-item>
          <el-form-item label="头像">
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
            <el-button plain type="primary" @click="updateAvatar">更新头像
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>
import {Host} from "@/main";
import {ElMessage} from "element-plus";
import JcAside from "@/components/common/aside.vue";

export default {
  name: 'UpdateInfo',
  components: {JcAside},
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
    uploadFile(param) {
      const formData = new FormData()
      formData.append('file', param.file)
      const url = 'http://' + Host + ':7000/file/upload'
      this.realAxios.post(url, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        this.user.profile = response.data.data;
        this.$message.success('文件上传成功');
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
  },
};
</script>

<style scoped>


.box-card {
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>