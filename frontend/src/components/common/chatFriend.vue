<template>
  <div class="flex flex-col flex-1 max-w-full mainDiv">
    <header class="flex items-center justify-between p-4 bg-white border-b border-gray-200 theH">
      <div class="flex items-center space-x-4">
        <a class="text-gray-500 hover:text-gray-900" href="#">
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
            <path d="m12 19-7-7 7-7"></path>
            <path d="M19 12H5"></path>
          </svg>
        </a>
        <div>
          <h2 class="text-lg font-semibold">{{ this.theFriend.friendName }}</h2>
        </div>
      </div>
      <a class="text-gray-500 hover:text-gray-900" href="#">
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
          <path d="M12 20a8 8 0 1 0 0-16 8 8 0 0 0 0 16Z"></path>
          <path d="M12 14a2 2 0 1 0 0-4 2 2 0 0 0 0 4Z"></path>
          <path d="M12 2v2"></path>
          <path d="M12 22v-2"></path>
          <path d="m17 20.66-1-1.73"></path>
          <path d="M11 10.27 7 3.34"></path>
          <path d="m20.66 17-1.73-1"></path>
          <path d="m3.34 7 1.73 1"></path>
          <path d="M14 12h8"></path>
          <path d="M2 12h2"></path>
          <path d="m20.66 7-1.73 1"></path>
          <path d="m3.34 17 1.73-1"></path>
          <path d="m17 3.34-1 1.73"></path>
          <path d="m11 13.73-4 6.93"></path>
        </svg>
      </a>
    </header>
    <main class="flex-1 p-4 space-y-4  main-content">
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
                width="220"
                confirm-button-text="OK"
                cancel-button-text="No, Thanks"
                :icon="InfoFilled"
                icon-color="#626AEF"
                title="是否拍一拍?"
                @confirm = "addApplyFriend(message.user)"
            >
              <template #reference>
                <img :src="'http://10.24.3.176:8083/ipfs/'+message.profile" alt="User Avatar" class="avatar">
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
                <p class="font-medium text-lg font-serif">{{ message.text }}</p>
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
        />
        <button
            class="inline-flex items-center justify-center rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 bg-primary text-primary-foreground hover:bg-primary/90 h-10 px-4 py-2"
            @click="sendMessage">
          Send
        </button>
      </div>
    </footer>
  </div>
</template>

<script>
import {Host} from "@/main";
import {InfoFilled} from "@element-plus/icons-vue";

export default {
  name: 'ChatFriend',
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
    let token = localStorage.getItem("token");
    this.theFriend = JSON.parse(this.friend);
    console.log('WebSocket created:', this.theFriend)
    this.socket = new WebSocket(`ws://` + Host + `:8081/ws/friend/${token}/${this.theFriend.friendId}`);

    this.socket.onopen = () => {
      this.realAxios.post(`http://` + Host + `:7000/chat/common/restoreFriendHistory/${this.theFriend.friendId}`, {}, {
        headers: {
          'token': token
        }
      })
          .then(response => {
            this.messages = response.data.data.map(messageVO => ({
              time: new Date(messageVO.time).getTime(), // 将Date对象转换为时间戳
              text: messageVO.text,
              user: messageVO.user,
              profile: messageVO.profile
            })); // 使用map方法将每个MessageVO对象转换为msg对象
          })
      console.log('WebSocket is open now.');
    };

    this.socket.onmessage = (event) => {
      console.log('WebSocket message received:', event.data);
      const data = JSON.parse(event.data);
      const msg = {
        time: Date.now(),
        text: data.message.text,
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
    isMe(message) {
      return message.user === this.user;
    },
    Host() {
      return Host
    },
    sendMessage() {
      if (this.message) {
        const msg = {time: Date.now(), text: this.message};
        this.socket.send(JSON.stringify(msg));
        this.message = '';
      }
    },

  },
};
</script>

<style scoped>
.mainDiv{
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  margin: 20px; /* 添加这一行 */
}


.this {
  overflow: hidden;
}


.main-content {
  height: 90%; /* 设置高度为视口的100% */
  overflow-y: auto; /* 当内容溢出时显示滚动条 */
  padding-top: 30px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.useFoot, .main-content, .theH {
  box-sizing: border-box;

  width: 77%;
  position: fixed;
}

.theH {
  top: 0;
}

.useFoot {
  animation: fadeIn 1s;
  bottom: 0;
}


</style>