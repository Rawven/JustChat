<template>
  <el-container class="container">
    <h1>注册聊天室</h1>
    <el-form ref="form" :model="room" label-width="120px" @submit.prevent="submitForm">
      <el-form-item label="聊天室名字:" prop="name">
        <el-input v-model="room.name" auto-complete="off"></el-input>
      </el-form-item>
      <el-form-item label="聊天室简介:" prop="description">
        <el-input type="textarea" v-model="room.description"></el-input>
      </el-form-item>
      <el-form-item label="聊天室最大人数:" prop="maxPeople">
        <el-input-number v-model="room.maxPeople" :min="1"></el-input-number>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </el-form-item>
    </el-form>
  </el-container>
</template>

<script>
import axios from "axios";
import {Host} from "@/main";

export default {
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
          axios.post('http://'+Host+':7000/chat/createRoom', this.room,{
            headers: {
              'token': localStorage.getItem("token")
            }
          })
              .then(response => {
                console.log('注册聊天室 successful:', response.data);
                // 注册成功后可以进行相关的处理，例如跳转到登录页面
                this.$router.push('/mainPage');
              }).catch(error => {
                console.error('Registration error:', error);
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
.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}
</style>