<template>
  <el-container class="container">

    <el-card class="card">
      <h1 class="title">注册聊天室</h1>
    <el-form ref="form" :model="room" label-width="120px" @submit.prevent="submitForm">
      <el-form-item label="聊天室名字:" prop="name">
        <el-input v-model="room.name" auto-complete="off"></el-input>
      </el-form-item>
      <el-form-item label="聊天室简介:" prop="description">
        <el-input v-model="room.description" type="textarea"></el-input>
      </el-form-item>
      <el-form-item label="聊天室最大人数:" prop="maxPeople">
        <el-input-number v-model="room.maxPeople" :min="1"></el-input-number>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </el-form-item>
    </el-form>
    </el-card>
  </el-container>
</template>

<script>
import {Host} from "@/main";
import {ElMessage} from "element-plus";

export default {
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data() {
    return {
      room: {
        name: '',
        description: '',
        maxPeople: 1,
      },
    };
  },
  methods: {
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.realAxios.post('http://' + Host + ':7000/chat/createRoom', this.room, {
            headers: {
              'token': localStorage.getItem("token")
            }
          })
              .then(() => {
                ElMessage.success('创建成功')
                // 注册成功后可以进行相关的处理，例如跳转到登录页面
                this.$router.push('/mainPage');
              })
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
  },
};
</script>

<style scoped>
.card{
  width: 500px;
  height: 350px;
  margin-top:  95px;
  margin-left: 455px;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0,0,0,0.5);
  animation: fadeIn 2s;
}

.title{
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
.container {
  width: auto;
  height: auto;
  margin:  auto;
  padding: 20px;
}
</style>