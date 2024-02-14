<template>
  <el-container class="Cont">
    <el-header class="flex items-center justify-between p-6 bg-white dark:bg-gray-800 shadow-md">
      <a class="flex items-center gap-2 font-semibold text-gray-900 dark:text-gray-100" href="#">
        <svg
            class="h-6 w-6"
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
          <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
          <polyline points="9 22 9 12 15 12 15 22"></polyline>
        </svg>
        <el-link href="/mainPage">My Website</el-link>
      </a>
      <el-menu active-text-color="hover:underline" background-color="transparent" class="flex gap-4" mode="horizontal"
               text-color="text-gray-900">
        <el-menu-item class="text-gray-900 dark:text-gray-100" index="1">Friends</el-menu-item>
        <el-menu-item class="text-gray-900 dark:text-gray-100" index="2">Groups</el-menu-item>
      </el-menu>
    </el-header>
    <el-main class="elN">
      <el-row :gutter="16">
        <el-col :span="24">
          <el-card class="h-full" shadow="hover">
            <el-row class="text-2xl font-semibold text-gray-900 dark:text-gray-100">Friend Requests</el-row>
            <el-table :data="tableDatas.filter (item => item.type === 'add_friend_apply')" style="width: 100%">
              <el-table-column label="Date" width="180">
                <template #default="scope">
                  <div style="display: flex; align-items: center">
                    <el-icon>
                      <timer/>
                    </el-icon>
                    <span style="margin-left: 10px">{{ timestampToTime(scope.row.timestamp) }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="Message" width="300">
                <template #default="scope">
                  <el-popover placement="top" trigger="hover" width="auto">
                    <template #reference>
                      <el-tag>{{ scope.row.sender.username }} 申请成为你的好友</el-tag>
                    </template>
                  </el-popover>
                </template>
              </el-table-column>
              <el-table-column label="Operations">
                <template #default="scope">
                  <el-button size="small" @click="handleFriendAgree(scope.row)"
                  >Agree
                  </el-button
                  >
                  <el-button
                      size="small"
                      type="danger"
                      @click="handleFriendReject(scope.row)"
                  >reject
                  </el-button
                  >
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="24">
          <el-card class="h-full" shadow="hover">
            <el-row class="text-2xl font-semibold text-gray-900 dark:text-gray-100">Room Requests</el-row>
            <el-table :data="tableDatas.filter (item => item.type === 'join_room_apply')" style="width: 100%">
              <el-table-column label="Date" style="margin-right: 10px;" width="180">
                <template #default="scope">
                  <div style="display: flex; align-items: center">
                    <el-icon>
                      <timer/>
                    </el-icon>
                    <span style="margin-left: 10px">{{ timestampToTime(scope.row.timestamp) }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="Message" style="margin-right: 10px;" width="300">
                <template #default="scope">
                  <el-popover placement="top" trigger="hover" width="auto">
                    <template #reference>
                      <el-tag>{{ scope.row.sender.username }} 申请成为加入你的群聊 {{ scope.row.data }}</el-tag>
                    </template>
                  </el-popover>
                </template>
              </el-table-column>
              <el-table-column label="Operations" style="margin-right: 10px;">
                <template #default="scope">
                  <el-button size="small" style="margin-right: 10px;" @click="handleRoomAgree(scope.row)"
                  >Agree
                  </el-button
                  >
                  <el-button
                      size="small"
                      style="margin-right: 10px;"
                      type="danger"
                      @click="handleRoomReject( scope.row)"
                  >reject
                  </el-button
                  >
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
    <el-footer class="flex items-center justify-between p-6 bg-white dark:bg-gray-800 shadow-md">
      <p class="text-sm text-gray-500 dark:text-gray-400">© 2024 My Website</p>
      <div class="flex gap-4">
        <a href="#">
          <svg
              class="h-5 w-5 text-gray-500 dark:text-gray-400"
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
            <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"></path>
          </svg>
        </a>
        <a href="#">
          <svg
              class="h-5 w-5 text-gray-500 dark:text-gray-400"
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
            <path
                d="M22 4s-.7 2.1-2 3.4c1.6 10-9.4 17.3-18 11.6 2.2.1 4.4-.6 6-2C3 15.5.5 9.6 3 5c2.2 2.6 5.6 4.1 9 4-.9-4.2 4-6.6 7-3.8 1.1 0 3-1.2 3-1.2z"></path>
          </svg>
        </a>
        <a href="#">
          <svg
              class="h-5 w-5 text-gray-500 dark:text-gray-400"
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
            <rect height="20" rx="5" ry="5" width="20" x="2" y="2"></rect>
            <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path>
            <line x1="17.5" x2="17.51" y1="6.5" y2="6.5"></line>
          </svg>
        </a>
      </div>
    </el-footer>
  </el-container>
</template>
<script>
import {Timer} from "@element-plus/icons-vue";
import {Host} from "@/main";

export default {
  name: 'MainPage',
  components: {Timer},
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data() {
    return {
      search: '',
      tableDatas: [],
      tableData: {
        "noticeId": 0,
        "type": "string",
        "data": "string",
        "timestamp": 0,
        "sender": {
          "userId": 0,
          "username": "test",
          "profile": "ddd"
        }
      },
    };
  },
  created() {
    this.realAxios.get(`http://` + Host + `:7000/user/notice/getNotice`, {
      headers: {
        'token': localStorage.getItem("token")
      }
    }).then(response => {
      this.tableDatas = response.data.data;
    })
  },
  methods: {
    Host() {
      return Host
    },
    handleFriendAgree(data) {
      this.realAxios({
        method: 'get',
        url: `http://` + Host + `:7000/user/friend/agreeToBeFriend/${data.sender.userId}/${data.noticeId}`,
        data: {},
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(() => {
        this.$message({
          message: 'Agree Success',
          type: 'success'
        });
        this.realAxios.get(`http://` + Host + `:7000/user/notice/getNotice`, {
          headers: {
            'token': localStorage.getItem("token")
          }
        }).then(response => {
          this.tableDatas = response.data.data;
        })
      })
    },
    handleFriendReject(data) {
      this.realAxios({
        method: 'get',
        url: `http://` + Host + `:7000/user/friend/refuseToBeFriend/${data.sender.userId}/${data.noticeId}`,
        data: {},
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(() => {
        this.$message({
          message: 'Reject Success',
          type: 'success'
        });
        this.realAxios.get(`http://` + Host + `:7000/user/notice/getNotice`, {
          headers: {
            'token': localStorage.getItem("token")
          }
        }).then(response => {
          this.tableDatas = response.data.data;
        })
      })
    },
    handleRoomAgree(data) {
      this.realAxios({
        method: 'get',
        url: `http://` + Host + `:7000/chat/common/agreeToJoinRoom/${data.data}/${data.sender.userId}/${data.noticeId}`,
        data: {},
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(() => {
        this.$message({
          message: 'Agree Success',
          type: 'success'
        });
        this.realAxios.get(`http://` + Host + `:7000/user/notice/getNotice`, {
          headers: {
            'token': localStorage.getItem("token")
          }
        }).then(response => {
          this.tableDatas = response.data.data;
        })
      })
    },
    handleRoomReject(data) {
      this.realAxios({
        method: 'get',
        url: `http://` + Host + `:7000/chat/common/refuseToJoinRoom/${data.data}/${data.sender.userId}/${data.noticeId}`,
        data: {},
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(() => {
        this.$message({
          message: 'Reject Success',
          type: 'success'
        });
        this.realAxios.get(`http://` + Host + `:7000/user/notice/getNotice`, {
          headers: {
            'token': localStorage.getItem("token")
          }
        }).then(response => {
          this.tableDatas = response.data.data;
        })
      })
    },

    timestampToTime(timestamp) {
      // 将时间戳转换为毫秒
      const date = new Date(timestamp);
      // 获取年份
      const year = date.getFullYear();
      // 获取月份
      const month = ("0" + (date.getMonth() + 1)).slice(-2);
      // 获取日期
      const day = ("0" + date.getDate()).slice(-2);
      // 获取小时
      const hours = ("0" + date.getHours()).slice(-2);
      // 获取分钟
      const minutes = ("0" + date.getMinutes()).slice(-2);
      // 获取秒
      const seconds = ("0" + date.getSeconds()).slice(-2);
      // 返回格式化的日期
      return year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
    }
  }
};
</script>
<style>
.Cont {
  height: 100%;
  width: 50cm;
  margin: 0;
}

.elN {
  height: 100%;
  width: 100%;
  margin: 0;
}
</style>