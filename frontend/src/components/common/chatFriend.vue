<template>
  <el-container class="chatFriend-container">
    <el-header class="flex items-center justify-between p-4 bg-white border-b border-gray-200 header ">
      <div class="flex items-center space-x-4">
        <div>
          <h2 class="text-lg font-semibold">{{ this.theFriend.friendName }}</h2>
        </div>

        <el-button class="button" plain type="primary" @click="addMsg">消息漫游-分页加载-</el-button>
      </div>
    </el-header>
    <el-main class="flex-1 p-4 space-y-4  main-content" style="overflow-y: auto;" >
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
                title="是否拍一拍?"
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
    </el-main>
    <el-footer class=" foot ">
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
    </el-footer>
  </el-container>
</template>

<script>
import {Host, ipfsHost} from "@/main";
import {ChatRound, InfoFilled} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";

export default {
  name: 'ChatFriend',
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
    friend: {
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
      theFriend: {
        friendId: '',
        lastMsg: '',
        friendProfile: '',
        friendName: '',
        maxPeople: 1,
      },
      message: '',
      messages: [],
      socket: null,
    };
  },
  created() {
    localStorage.getItem("token");
    this.theFriend = JSON.parse(this.friend);
    console.log('WebSocket created:', this.theFriend)
    this.getOffline();
    this.socket = this.$global.ws;
    this.socket.onopen = () => {
      console.log('WebSocket is open now.');
    };

    this.socket.onmessage = (event) => {
      console.log('WebSocket message received:', event.data);
      const data = JSON.parse(event.data);
      if (data.type === "FRIEND_APPLY") {
        this.applyNoticeIsNew = true;
        ElMessage.success('您有新的好友申请');
      } else if (data.type === "ROOM_APPLY") {
        this.applyNoticeIsNew = true;
        ElMessage.success('您有新的群聊申请');
      } else if (data.type === "RECORD_MOMENT_FRIEND" || data.type === "RECORD_MOMENT") {
        this.momentNoticeIsNew = true;
        ElMessage.success('您有新的朋友圈消息');
      } else if(data.type === "friend"){
        const msg = {
          time: Date.now(),
          text: data.text,
          user: data.userInfo.username,
          profile: data.userInfo.profile
        };
        this.messages.push(msg);
      }
    };

    this.socket.onclose = () => {
      console.log('WebSocket is closed now.');
    };

    this.socket.onerror = (event) => {
      console.error('WebSocket error observed:', event);
    };
  }
  ,

  methods: {
    ipfsHost() {
      return ipfsHost
    },
    isMe(message) {
      return message.user === this.user;
    },
    Host() {
      return Host
    },
    addMsg() {
      let token = localStorage.getItem("token");
      this.realAxios.post(`http://` + Host + `:7000/chat/message/queryFriendMsgPages`, {
        friendId: this.theFriend.friendId,
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
    , getOffline() {
      let token = localStorage.getItem("token");
      this.realAxios.post(`http://` + Host + `:7000/chat/message/getLatestFriendHistory`, {
        friendId: this.theFriend.friendId,
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

            this.messages.sort((a, b) => b.time - a.time);
            this.page++;
          })
    },
    sendMessage() {
      if (this.message) {
        let userInfo =JSON.parse(localStorage.getItem("userData"));
        const msg = {
          time: Date.now(),
          belongId: this.theFriend.friendId,
          text: this.message,
          type: "friend",
          userInfo:{
            userId: userInfo.userId,
            username: userInfo.username,
            profile: userInfo.profile
          }};
        this.$global.ws.send(JSON.stringify(msg));
        this.message = '';
      }
    },
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

  },
};
</script>

<style scoped>
.chatFriend-container {
  display: flex;
  height: 100%;
  width: 100%;
}

.header {
  position: fixed;
  top: 0;
  width: 100%;
  z-index: 1000;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.main-content, .header {
  box-sizing: border-box;
  width: 100%;
}

.main-content {
  height: 75%; /* 设置高度为视口的100% */
  overflow-y: auto; /* 当内容溢出时显示滚动条 */
  padding-top: 50px;
  padding-bottom: 50px;
  margin-bottom: 25px;
}

.foot {
  animation: fadeIn 1s;
  position: fixed;
  bottom: 0;
  width: 70%; /* 使用 100% 宽度，确保覆盖整个屏幕 */
  background-color: transparent; /* 如果需要，根据你的设计添加合适的背景颜色 */
  z-index: 1; /* 确保位于其他元素之上 */
  margin-top: 20px; /* 设置 el-footer 与上方元素的间距 */
}
</style>