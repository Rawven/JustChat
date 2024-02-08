<template>
  <el-container class="containerM">
    <el-aside class="w-48 border-r border-gray-200 overflow-y-auto">
      <el-header class="p-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold">Navigation</h2>
      </el-header>
      <el-menu class="p-4 space-y-2">
        <div class="flex items-center space-x-4" @click="turnSearch">
          <svg
              class="w-6 h-6"
              fill="none"
              height="24"
              stroke="currentColor"
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="24"
              xmlns="http://www.w3.org/2000/svg"
          >
            <path d="M6 18h8"></path>
            <path d="M3 22h18"></path>
            <path d="M14 22a7 7 0 1 0 0-14h-1"></path>
            <path d="M9 14h2"></path>
            <path d="M9 12a2 2 0 0 1-2-2V6h6v4a2 2 0 0 1-2 2Z"></path>
            <path d="M12 6V3a1 1 0 0 0-1-1H9a1 1 0 0 0-1 1v3"></path>
          </svg>
          <el-text tag="b">Search</el-text>
        </div>
        <div class="flex items-center space-x-4" @click="turnRooms">
          <svg
              class="w-6 h-6"
              fill="none"
              height="24"
              stroke="currentColor"
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="24"
              xmlns="http://www.w3.org/2000/svg"
          >
            <path d="M3 7V5c0-1.1.9-2 2-2h2"></path>
            <path d="M17 3h2c1.1 0 2 .9 2 2v2"></path>
            <path d="M21 17v2c0 1.1-.9 2-2 2h-2"></path>
            <path d="M7 21H5c-1.1 0-2-.9-2-2v-2"></path>
            <rect height="5" rx="1" width="7" x="7" y="7"></rect>
            <rect height="5" rx="1" width="7" x="10" y="12"></rect>
          </svg>
          <el-text tag="b">Rooms</el-text>
        </div>
        <div class="flex items-center space-x-4" @click="turnNotifications">
          <svg
              class="w-6 h-6"
              fill="none"
              height="24"
              stroke="currentColor"
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="24"
              xmlns="http://www.w3.org/2000/svg"
          >
            <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"></path>
            <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"></path>
          </svg>
          <span>Notifications</span>
        </div>
        <div class="flex items-center space-x-4" @click="turnMoment">
          <el-icon>
            <PictureFilled/>
          </el-icon>
          <el-text tag="b">Moment</el-text>
        </div>
        <div class="flex items-center space-x-4" @click="logOut">
          <svg
              class="w-6 h-6"
              fill="none"
              height="24"
              stroke="currentColor"
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="24"
              xmlns="http://www.w3.org/2000/svg"
          >
            <path d="M13 4h3a2 2 0 0 1 2 2v14"></path>
            <path d="M2 20h3"></path>
            <path d="M13 20h9"></path>
            <path d="M10 12v.01"></path>
            <path
                d="M13 4.562v16.157a1 1 0 0 1-1.242.97L5 20V5.562a2 2 0 0 1 1.515-1.94l4-1A2 2 0 0 1 13 4.561Z"></path>
          </svg>
          <span>Logout</span>
        </div>
      </el-menu>
    </el-aside>
    <el-aside class="theAside w-64 border-r border-gray-200 overflow-y-auto assside">
      <el-header class="p-4 border-b border-gray-200">
        <el-row>
          <img :src="ipfsHost()+userInfo.profile" alt="User Avatar" class="avatar">
          <h2 class="logo"> Just Chat </h2>
        </el-row>
      </el-header>
      <el-main class="p-4 border-b border-gray-200">
        <div class="flex items-center space-x-4">
      <span class="relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full">
        <span class="flex h-full w-full items-center justify-center rounded-full bg-muted">CN</span>
      </span>
          <div>
            <h3 class="text-lg font-semibold">{{ userInfo.username }}</h3>
            <p class="text-sm text-gray-400">{{ userInfo.signature }}</p>
          </div>
        </div>
      </el-main>
      <el-header class="p-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold">我的好友</h2>
      </el-header>
      <el-menu class="p-4 space-y-2">
        <div
            v-for="(friend) in friends"
            :key="friend.isNew"
            class="flex items-center space-x-4 border rounded-lg p-2 cursor-pointer bg-white list-room"
            @click="checkOut(friend.friendId)"
        >
    <span class="relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full">
      <img :src="ipfsHost()+friend.friendProfile" alt="User Avatar" class="avatar">
    </span>
          <el-col>
            <el-col v-if="checkNull(friend.lastMsgSender)">{{
                formatDateOrTime(JSON.parse(friend.lastMsg).timestamp)
              }}
            </el-col>
            <el-col>
              <el-tag>{{ friend.friendName }}</el-tag>
              <el-row>
                <el-icon v-if="friend.isNew" color="#FF0000">
                  <ChatLineRound/>
                </el-icon>
                <el-icon v-else color="#409EFC">
                  <ChatRound/>
                </el-icon>
                <el-col v-if="checkNull(friend.lastMsgSender)" class="font-medium text-lg font-serif">
                  {{ friend.lastMsgSender + "：" + JSON.parse(friend.lastMsg).content }}
                </el-col>
                <el-col v-else class="font-medium text-lg font-serif">暂无消息</el-col>
              </el-row>
            </el-col>
          </el-col>
        </div>
      </el-menu>
    </el-aside>
    <chat-Friend v-if="nowFriendId >=1" :key="nowFriendId"
                 :friend="JSON.stringify(this.friends[this.friendIndex.get(nowFriendId)])"
                 :user="this.userInfo.username"></chat-Friend>
  </el-container>
</template>

<script>
import {Host, ipfsHost} from "@/main";
import {reactive, ref} from "vue";
import ChatFriend from "@/components/common/chatFriend.vue";
import {ChatLineRound, ChatRound, PictureFilled} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";

export default {
  watch: {
    nowRoomId(newVal, oldVal) {
      if (newVal !== oldVal) {
        // nowRoomId has changed. You can add your logic here to re-render the chat-room component
        this.nowRoomId = newVal;
      }
    },
  },
  name: 'MainPage',
  components: {PictureFilled, ChatFriend, ChatLineRound, ChatRound},
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data() {
    return {
      radio: ref(0),
      searchInput: '',
      websocket: null,
      messageCount: 0,
      friend: {
        friendId: 0,
        friendName: "",
        friendProfile: "",
        lastMsg: "",
        lastMsgSender: ""
      },
      userInfo: {
        username: '',
        profile: '',
        signature: '',
      },
      friends: reactive([]),
      friendIndex: new Map(),
      pageSize: 5,
      nowFriendId: 0,
    };
  },
  created() {
    this.getFriends();
    //this.updateMessageCount();
    let item = localStorage.getItem("userData");
    if (item) {
      this.userInfo = JSON.parse(item);
    }
    this.initWebSocket();

  },

  methods: {
    ipfsHost() {
      return ipfsHost
    },
    ref,
    Host() {
      return Host
    },
    logOut() {
      let token = localStorage.getItem("token");
      this.realAxios.get(`http://` + Host + `:7000/auth/logout/${token}`, {})
      localStorage.removeItem("token");
      localStorage.removeItem("userData");
      this.$router.push('/common/login');
      ElMessage.success('登出成功');
    },
    turnSearch() {
      this.$router.push('/common/roomPage');
    },
    turnRooms() {
      this.$router.push('/common/mainPage');
    },
    checkOut(friendId) {
      this.nowFriendId = friendId;
      this.friends[this.friendIndex.get(friendId)].isNew = false;
    },
    turnMoment() {
      this.$router.push('/common/moment');
    },
    initWebSocket() {
      let token = localStorage.getItem("token");
      this.websocket = new WebSocket(`ws://` + Host + `:8080/ws/${token}`);
      this.websocket.onopen = () => {
        console.log('WebSocket is open now.');
      };
      this.websocket.onmessage = (event) => {

        let data = JSON.parse(event.data)
        if (data.type === "FRIEND_APPLY") {
          this.noticeIsNew = true;
          ElMessage.success('您有新的好友申请');
        } else if (data.type === "ROOM_APPLY") {
          this.noticeIsNew = true;
          ElMessage.success('您有新的群聊申请');
        } else {
          let index = this.friendIndex.get(Number(data.friendId));
          this.friends[index] = {
            ...this.friends[index],
            lastMsg: data.msg,
            lastMsgSender: data.username,
            isNew: true
          };
        }
      };
      this.websocket.onclose = () => {
        console.log('WebSocket is closed now.');
      };
    },
    // 其他方法...
    formatDateOrTime(timestamp) {
      const messageDate = new Date(timestamp);
      const currentDate = new Date();
      if (messageDate.toDateString() === currentDate.toDateString()) {
        return messageDate.toLocaleTimeString();
      } else {
        return messageDate.toLocaleDateString();
      }
    },
    turnNotifications() {
      this.$router.push('/common/notice');
    },
    checkNull(name) {
      return name !== "";
    },
    getFriends() {
      this.realAxios.get(`http://` + Host + `:7000/chat/room/initUserFriendPage`, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        // 将获取的房间数组赋值给 rooms
        this.friends = response.data.data;
        console.log(this.friends)
        // 将获取的房间总数赋值给 totalRooms
        this.nowFriendId = this.friends[0].friendId;
        this.friends.forEach((friend, index) => {
          this.friendIndex.set(Number(friend.friendId), index);
        });
      })
    },
  }
};
</script>
<style>
body {
  overflow: hidden;
  font-family: 'Roboto', sans-serif;
  font-weight: bold;
  background-color: #f0f0f0; /* 添加背景色 */
}

.list-room {
  overflow: hidden;
  padding: 10px;
  margin-bottom: 10px;
  background-color: #f0f0f0; /* 添加背景色 */
}

.assside {
  width: 1000px
}

.containerM {
  width: 150%;
  height: 100%;
  margin: 0;
}

.avatar {
  width: 75px;
  height: 75px;
  border-radius: 50%;
}

.theAside {
  width: 25%;
  height: 210%;
  margin: 0;
  padding: 0;
  top: 0;
  background-color: #f0f0f0; /* 添加背景色 */
}

.logo {
  font-size: 28px;
  color: #409EFF;
  font-weight: bold;
  text-align: center;
  margin-bottom: 30px;
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

</style>