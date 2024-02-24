<template>
  <el-container class="JcContainer">
    <JcAside></JcAside>
    <el-aside class="theAside w-64 border-r border-gray-200 overflow-y-auto ">
          <img :src="ipfsHost()+userInfo.profile" alt="User Avatar" class="avatar">
      <el-header class="p-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold">我的好友</h2>
      </el-header>
      <el-menu class="p-4 space-y-2 menu">
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
    <el-main>
    <chat-Friend v-if="nowFriendId >=1" :key="nowFriendId"
                 :friend="JSON.stringify(this.friends[this.friendIndex.get(nowFriendId)])"
                 :user="this.userInfo.username"></chat-Friend>
    </el-main>
  </el-container>
</template>

<script>
import {Host, ipfsHost} from "@/main";
import {reactive, ref} from "vue";
import ChatFriend from "@/components/common/chatFriend.vue";
import {ChatLineRound, ChatRound} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import JcAside from "@/components/common/aside.vue";

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
  components: {JcAside, ChatFriend, ChatLineRound, ChatRound},
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
    let item = localStorage.getItem("userData");
    if (item) {
      this.userInfo = JSON.parse(item);
    }
    this.initWebSocket();
  },
  activated() {
    this.getFriends();
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
      this.$router.push('/login');
      ElMessage.success('登出成功');
    },
    turnSearch() {
      this.$router.push('/roomPage');
    },
    turnRooms() {
      this.$router.push('/mainPage');
    },
    checkOut(friendId) {
      this.nowFriendId = friendId;
      this.friends[this.friendIndex.get(friendId)].isNew = false;
    },
    turnMoment() {
      this.$router.push('/moment');
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
      this.$router.push('/notice');
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
}

.menu{
  background-color: transparent;
}

.list-room {
  overflow: hidden;
  padding: 10px;
  margin-bottom: 10px;
}


.avatar {
  width: 75px;
  height: 75px;
  border-radius: 50%;
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