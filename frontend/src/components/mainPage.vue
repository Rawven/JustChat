<template>
  <el-container class="container">
    <el-row class="row-bg" justify="center" type="flex">
      <el-col v-if="userInfo" :span="8">
        <el-card class="box-card">
          <img :src="'http://10.44.59.225:8083/ipfs/'+userInfo.profile" alt="User Avatar" class="avatar">
          <h2 class="username">你好!😘 {{ userInfo.username }}</h2>
          <h2 class="signature">个性签名: {{
              userInfo.signature ? userInfo.signature : '这个用户很懒 什么也没留下'
            }}</h2>
          <router-link to="/updateInfo" class="linkText">更改个人信息</router-link>
        </el-card>
      </el-col>
      <el-col :span="16">
        <router-link to="/openRoom">
          <template v-slot:default="{ navigate }">
            <button class="button" @click="navigate">建立个新的聊天室</button>
          </template>
        </router-link>
        <el-input class="inputHolder" placeholder="请输入内容来查找房间" v-model="searchInput" @keyup.enter="submitSearch(1)"></el-input>
          <el-radio-group class="radio" v-model="radio" >
            <el-radio :label="0" border>根据用户名</el-radio>
            <el-radio :label="1" border>根据房间名</el-radio>
          </el-radio-group>
        <el-main class="cardContainer">
          <el-table :data="rooms" style="width: 100%">
            <el-table-column label="房间名" width="180">
              <template #default="scope">
                <h2 class="room-name">{{ scope.row.roomName }}</h2>
              </template>
            </el-table-column>
            <el-table-column label="简述" width="180">
              <template #default="scope">
                <p class="room-description">{{ scope.row.roomDescription }}</p>
              </template>
            </el-table-column>
            <el-table-column label="最大人数" width="180">
              <template #default="scope">
                <el-tag size="default" class="room-max-people">{{ scope.row.maxPeople }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="创建者" width="180">
              <template #default="scope">
                <el-tag size="default" class="room-founder">{{ scope.row.founderName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button type="primary" @click="enterRoom(scope.row.roomId)">进入</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination class="pagination-container"
                         background
                         layout="prev, pager, next"
                         :total="totalRooms"
                         v-model:current-page="currentPage"
                         :page-size="pageSize"
                         @current-change="handlePageChange"
          />
        </el-main>
      </el-col>
    </el-row>
  </el-container>
</template>

<script>
import {Host} from "@/main";
import {ref} from "vue";
export default {

  name: 'MainPage',
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data() {
    return {
      radio: ref(0),
      searchInput: '',
      room: {
        roomId: '',
        roomName: '',
        roomDescription: '',
        founderName: '',
        founderAvatar: '',
        maxPeople: 1,
      },
      userInfo: {
        username: '',
        profile: '',
        signature: '',
      },
      rooms: [],
      currentPage: 1, // 新增属性，用于存储当前的页数
      totalRooms: 0, // 新增属性，用于存储房间总数
      pageSize: 5,
    };
  },
  created() {
    this.getRooms(this.currentPage);

    let item = localStorage.getItem("userData");
    if (item) {
      this.userInfo = JSON.parse(item);
    }
  },
  methods: {
    Host() {
      return Host
    },
    submitSearch(value) {
      // 在这里发送请求到后端
      this.realAxios.get(`http://` + Host + `:7000/chat/queryRelatedRoomList/${this.searchInput}/${this.radio}/${value}`, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        // 处理响应
        this.rooms = response.data.data.rooms;
      })
    },
    enterRoom(roomId) {
      this.$router.push({path: `/chatRoom/` + Number(roomId)});
    },
    getRooms(page) {
      this.realAxios.get(`http://` + Host + `:7000/chat/queryRoomList/${page}`, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        // 将获取的房间数组赋值给 rooms
        this.rooms = response.data.data.rooms;
        // 将获取的房间总数赋值给 totalRooms
        this.totalRooms = parseInt(response.data.data.total);
      })
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.getRooms(page);
    }
  }
};
</script>

<style scoped>
.radio{
  margin: 20px;
}

.inputHolder{
  width: 400px;
}

.linkText {
  font-size: 24px;
  color: #409EFF;
  font-weight: bold;
  text-align: center;
  margin-bottom: 20px;
  animation: fadeIn 2s;
}

.button {
  display: inline-block;
  padding: 10px 20px;
  font-size: 16px;
  color: #fff;
  background-color: #409EFF;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.button:hover {
  background-color: #66B3FF;
}

.signature {
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

/* ...existing styles... */

.avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
}

.username {
  font-size: 20px;
  margin-top: 10px;
}


.container {
  width: 100%;
  height: auto;
  padding: 30px;
}


.main {
  background-color: #F5F5F5;
  color: #333;
  text-align: center;
  margin-left: 10px;
  width: 100%;
  padding: 0;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.cardContainer {
  width: 950px; /* 设置容器的宽度 */
  height: 500px; /* 设置容器的高度 */
  margin: 20px; /* 添加外边距 */
}


.row-bg {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.button {
  width: 200px;
  margin: 20px;
  background-color: #FF4081;
  color: #FFF;
}

.pagination-container {
  width: auto;
  left: auto;
  padding: 20px 0;
  text-align: start;
  margin-left: 338px;
}

.room-name, .room-description {
  margin: 10px;
  font-size: 16px;
  font-weight: bold;

}

.room-founder,
.room-max-people {
  margin: 10px;
  font-size: 12px;
}

.box-card {
  height: 600px;
  padding: 20px;
  margin: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-bottom: 20px;
}

.username {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
}

.signature {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.linkText {
  font-size: 16px;
  color: #409EFF;
  text-decoration: underline;
}


</style>