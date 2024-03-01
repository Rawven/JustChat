<template>
  <el-container class="cr-container">
    <header class="flex items-center justify-between p-4 bg-white border-b border-gray-200 ">
      <div class="flex items-center space-x-4">
        <div>
          <h2 class="text-lg font-semibold">{{ this.theRoom.roomName }}</h2>
        </div>

        <el-button class="button" plain type="primary" @click="addMsg">消息漫游-分页加载-</el-button>
      </div>
    </header>
    <main class="flex-1 p-4 space-y-4  main-content" >
      <div v-for="(message, index) in messages" :key="index">
        <div v-if="isMe(message)">
          <div class="flex flex-col items-end space-y-2">
            <div class="rounded-lg border shadow-sm bg-green-50 text-green-700" data-v0-t="card">
              <div class="p-6">
                <p class="font-medium text-lg font-serif">{{ message.text }}</p>

              </div>
            </div>
          </div>
        </div>
        <div v-else>
          <div class="flex items-end space-x-2">
            <el-popconfirm
                :icon="InfoFilled"
                cancel-button-text="No, Thanks"
                confirm-button-text="OK"
                icon-color="#626AEF"
                title="是否向该群员发送好友申请?"
                width="220"
                @confirm="addApplyFriend(message.user)"
            >
              <template #reference>
                <img :src="ipfsHost()+message.profile" alt="User Avatar" class="avatar">
              </template>
            </el-popconfirm>


            <span class="relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full mt-2">
          <span
              class="flex h-full w-full items-center justify-center rounded-full bg-muted font-medium text-lg font-serif">{{
              message.user
            }}</span>
        </span>
            <div class="rounded-lg border shadow-sm bg-blue-50 text-blue-700" data-v0-t="card">
              <div class="p-6">
                <p v-if="message.offline" class="text-xs text-gray-500"><el-text>{{ message.text }}</el-text> <el-icon  color="#409EFC">
                  <ChatRound/>
                </el-icon></p>
                <p v-else class="font-medium text-lg font-serif">{{ message.text }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
    <footer class="p-4 bg-white border-t border-gray-200 useFoot">
      <div class="flex items-center space-x-4 this">
        <button
            class="inline-flex items-center justify-center rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 border border-input bg-background hover:bg-accent hover:text-accent-foreground h-10 w-10">
          <svg
              class="w-6 h-6"
              fill="none"
              height="24"
              stroke="currentColor"
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="50"
              xmlns="http://www.w3.org/2000/svg"
          >
            <path d="M5 12h14"></path>
            <path d="M12 5v14"></path>
          </svg>
        </button>
        <input
            v-model="message"
            class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 flex-1"
            placeholder="Type a message"
            @keyup.enter="sendMessage"
        />
        <button
            class="inline-flex items-center justify-center rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 bg-primary text-primary-foreground hover:bg-primary/90 h-10 px-4 py-2"
            @click="sendMessage">
          Send
        </button>
      </div>
    </footer>
  </el-container>
</template>

<script>
import {Host, ipfsHost} from "@/main";
import {ChatRound, InfoFilled} from "@element-plus/icons-vue";

export default {
  name: 'ChatRoom',
  components: {ChatRound},
  computed: {
    InfoFilled() {
      return InfoFilled
    }
  },
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  props: {
    room: {
      type: String,
      required: true
    },
    user: {
      type: String,
      required: true
    },
  },
  data() {
    return {
      page: 0,
      theRoom: {
        roomId: '',
        lastMsg: '',
        roomProfile: '',
        roomName: '',
        founderName: '',
        maxPeople: 1,
      },
      message: '',
      messages: [],
      socket: null,
    };
  },
  created() {
    this.theRoom = JSON.parse(this.room);
    console.log('WebSocket created:', this.theRoom)
    this.getOffline();
    this.socket = this.$global.ws;
    this.socket.onopen = () => {

      console.log('WebSocket is open now.');
    };

    this.socket.onmessage = (event) => {
      console.log('WebSocket message received:', event.data);
      const data = JSON.parse(event.data);
      const msg = {
        time: Date.now(),
        text: data.message.text,
        user: data.message.userInfoDTO.username,
        profile: data.message.userInfoDTO.profile
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
    getOffline() {
      let token = localStorage.getItem("token");
      this.realAxios.post(`http://` + Host + `:7000/chat/message/getLatestRoomHistory`, {
        roomId: this.theRoom.roomId,
      }, {
        headers: {
          'token': token
        }
      })
          .then(response => {
            this.messages = response.data.data.map(messageVO => ({
              time: new Date(messageVO.time).getTime(), // 将Date对象转换为时间戳
              text: messageVO.text,
              user: messageVO.userInfoDTO.username,
              profile: messageVO.userInfoDTO.profile,
              offline: true
            })); // 使用map方法将每个MessageVO对象转换为msg对象
          })

      this.page++;
    },
    ipfsHost() {
      return ipfsHost
    },
    isMe(message) {
      return message.user === this.user;
    },
    Host() {
      return Host
    },
    sendMessage() {
      if (this.message) {
        let userInfo =JSON.parse(localStorage.getItem("userData"));
        const msg = {
          time: Date.now(),
          belongId: this.theRoom.roomId,
          text: this.message,
          type: "room",
          userInfoDTO:{
            userId: userInfo.userId,
            username: userInfo.username,
            profile: userInfo.profile
          }};
        this.$global.ws.send(JSON.stringify(msg));
        this.message = '';
      }
    }
    , addMsg() {
      let token = localStorage.getItem("token");
      this.realAxios.post(`http://` + Host + `:7000/chat/message/queryRoomMsgPages`, {
        roomId: this.theRoom.roomId,
        page: this.page,
        size: 15
      }, {
        headers: {
          'token': token
        }
      })
          .then(response => {
            const newMessages = response.data.data.map(messageVO => ({
              time: new Date(messageVO.time).getTime(), // 将Date对象转换为时间戳
              text: messageVO.text,
              user: messageVO.userInfoDTO.username,
              profile: messageVO.userInfoDTO.profile
            }));

            // 使用 push 方法将新的消息添加到数组中
            this.messages.push(...newMessages);
            this.messages.sort((a, b) => a.time - b.time);
            this.page++;
          });
    }
    ,
    addApplyFriend(username) {
      this.realAxios.get(`http://` + Host + `:7000/user/notice/addFriendApply/${username}`, {
        headers: {
          'token': localStorage.getItem("token")
        },
      }).then(() => {
        this.$message({
          message: '好友申请已发送',
          type: 'success'
        });
      })
    },
  }
};
</script>

<style scoped>
.cr-container {
  height: 100%;
  width: auto;
  display: flex;
  flex-direction: column;
}

body {
  overflow: hidden; /* 阻止整个页面滚动 */
}

.this {
  overflow: hidden;
}


.main-content {
  height: 90%; /* 设置高度为视口的100% */
  overflow-y: auto; /* 当内容溢出时显示滚动条 */
  padding-top: 50px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.useFoot, .main-content {
  box-sizing: border-box;
  width: 100%;
}

.useFoot {
  animation: fadeIn 1s;
  bottom: 0;
}


</style>