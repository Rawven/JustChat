<template>
  <el-container class="container">
    <el-card class="box-card">
      <el-row class="title">  欢迎来到聊天室！😄</el-row>
      <el-row :gutter="20">
        <el-col :span="18">
          <el-card class="grid-content bg-purple">
            <el-input v-model="message" placeholder="请输入消息"></el-input>
            <el-button type="primary" @click="sendMessage">发送消息</el-button>
            <el-card class="message-card">
              <div v-for="msg in messages" :key="msg.id" class="message">
                <p class="message-time">{{ new Date(msg.id).toLocaleString() }}</p>
                <p class="message-text">{{ msg.text }}</p>
              </div>
            </el-card>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </el-container>
</template>

<script>
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
    this.socket = new WebSocket('ws://your-websocket-server.com');
    this.socket.onmessage = (event) => {
      const message = JSON.parse(event.data);
      this.messages.push(message);
    };
  },
  methods: {
    sendMessage() {
      if (this.message) {
        const msg = { id: Date.now(), text: this.message };
        this.socket.send(JSON.stringify(msg));
        this.message = '';
      }
    },
    beforeDestroy() {
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
.message {
  border: 1px solid #d3dce6;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 10px;
}

.message-time {
  color: #409EFF;
  font-size: 12px;
}

.message-text {
  margin-top: 5px;
  font-size: 16px;
}
.message-card {
  margin-top: 20px;
  padding: 10px;
  border: 1px solid #d3dce6;
  border-radius: 4px;

  height: 13cm;
}

.bg-purple {
  background: #d3dce6;
  padding: 20px;
  border-radius: 4px;
}
</style>