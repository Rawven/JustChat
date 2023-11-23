<template>
  <el-container class="container">
    <el-card class="box-card">
      <el-row class="title"> 欢迎来到聊天室！😄</el-row>
      <el-row :gutter="20">
        <el-col :span="18">
          <el-card class="grid-content bg-purple">
            <el-input v-model="message" placeholder="请输入消息"></el-input>
            <el-button type="primary" @click="sendMessage">发送消息</el-button>
            <el-card class="message-card">
              <div v-for="msg in messages" :key="msg.time" class="message">
                <el-aside class="meSide">
                  <el-row class="message-user">{{ msg.user }}</el-row>
                  <img :src="'http://127.0.0.1:8083/ipfs/'+msg.profile" alt="User profile" class="message-profile">
                </el-aside>
                <el-card class="message-content">
                  <p class="message-time">{{ new Date(msg.time).toLocaleString() }}</p>
                  <p class="message-text">{{ msg.text }}</p>
                </el-card>
              </div>
            </el-card>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </el-container>
</template>

<script>
import axios from "axios";
import {Host} from "@/main";

export default {
  name: 'ChatRoom',
  data() {
    return {
      message: '',
      messages: [],
      socket: null,
    };
  },
  created() {
    let token = localStorage.getItem("token");

    this.socket = new WebSocket(`ws://`+Host+`:8081/websocket/${token}`);
    console.log('WebSocket created:', token)
    this.socket.onopen = () => {
      axios.post("http://"+Host+":7000/chat/restoreHistory", {},{
        headers: {
          'token': token
        }
      })
          .then(response => {
            console.log('Restore history successful:', response.data.data);
            this.messages = response.data.data.map(messageVO => ({
              time: new Date(messageVO.time).getTime(), // 将Date对象转换为时间戳
              text: messageVO.text,
              user: messageVO.user,
              profile: messageVO.profile
            })); // 使用map方法将每个MessageVO对象转换为msg对象
          })

          .catch(error => {
            console.error('Restore history error:', error);
          });
      console.log('WebSocket is open now.');
    };

    this.socket.onmessage = (event) => {
      console.log('WebSocket message received:', event.data);
      const data = JSON.parse(event.data);
      const msg = {
        time: Date.now(),
        text: JSON.parse(data.message).text,
        user: data.userInfo.username,
        profile: data.userInfo.profile
      };
      this.messages.push(msg);
    };

    this.socket.onclose = () => {
      console.log('WebSocket is closed now.');
    };

    this.socket.onerror = (event) => {
      console.error('WebSocket error observed:', event);
    };
  },

  methods: {
    sendMessage() {
      if (this.message) {
        const msg = {time: Date.now(), text: this.message};
        this.socket.send(JSON.stringify(msg));
        this.message = '';
      }
    },
    beforeUnmount() {
      if (this.socket) {
        this.socket.close();
      }
    },

  },
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

.grid-content {
  margin-right: 200px;
  width: 43cm;
  height: 20cm;
}

.box-card {
  position: absolute;
  top: 75px;
  left: 100px;
  margin-right: 400px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 90%;
  height: 100%;
}

.meSide {
  width: 80px; /* 调整宽度 */
  height: 150px; /* 调整高度 */
}

.message {
  display: flex;
  border: 1px solid #d3dce6;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 10px;
  height: 120px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); /* 添加阴影 */
  background-color: #fff; /* 添加背景色 */
}

.message-card {
  width: 100%;
  height: 100%;
}

.message-profile {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-right: 10px;
}

.message-content {
  display: flex;
  left: 100px;
  flex-direction: column;
  padding: 10px; /* 添加内边距 */
  border: 1px solid #d3dce6; /* 添加边框 */
  border-radius: 20px; /* 添加圆角 */
  margin-right: auto; /* 添加这一行 */
}


.message-user {
  order: 2;
}

.message-time {
  order: 1;
  color: #409EFF;
  font-size: 12px;
  top: 20px;
}

.message-text {
  order: 3;
  margin-top: 5px;
  font-size: 16px;
}

.bg-purple {
  background: #d3dce6;
  padding: 20px;
  border-radius: 4px;
}

</style>