<template>
  <div class="common-layout">
    <el-container class="mainC">
      <el-header>Header</el-header>
      <el-container class="Tmain">
        <el-aside class="aside">
          <el-card class="box-card">
            <el-menu>
              <el-menu-item @click="goToUserPage">查看用户</el-menu-item>
              <el-menu-item class="now">查看Room</el-menu-item>
            </el-menu>
          </el-card>
        </el-aside>
        <el-main class="main">
          <el-card class="main-card">
            <el-table :data="tableData" style="width: 100%">
              <el-table-column prop="roomId" label="Room ID"></el-table-column>
              <el-table-column prop="roomName" label="Room Name"></el-table-column>
              <el-table-column prop="founderName" label="创建者"></el-table-column>
              <el-table-column prop="maxPeople" label="最大人数"></el-table-column>
              <el-table-column prop="roomDescription" label="描述"></el-table-column>
              <el-table-column
                  fixed="right"
                  label="操作"
                  width="200">
                <template v-slot="scope">
                  <el-row>
                    <el-col :span="12">
                      <el-button type="primary" round @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                    </el-col>
                    <el-col :span="12">
                      <el-button type="info" round @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                    </el-col>
                  </el-row>
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
          </el-card>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import {Host} from "@/main";

export default {
  name:'admin_roomPage',
  data() {
    return {
      room: {
        roomId: '',
        roomName: '',
        roomDescription: '',
        founderName: '',
        maxPeople: 1,
      },
      currentPage: 1, // 新增属性，用于存储当前的页数
      totalRooms: 0, // 新增属性，用于存储房间总数
      pageSize: 5,
      tableData: [] // 这里应该是你的房间数据
    }
  },
  created() {
    this.getRooms(this.currentPage);
  },
  methods: {
    goToUserPage() {
      this.$router.push('/admin/mainPage');
    },
    getRooms(page) {
      this.realAxios.get(`http://` + Host + `:7000/chat/user/queryRoomList/${page}/${8}`, {
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
    },
    handleEdit(index, row) {
      // 这里是你的编辑逻辑
    },
    handleDelete(index, row) {
      // 这里是你的删除逻辑
    }
  }
}
</script>

<style scoped>
.now{
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
.aside {
  margin: 0;
  background-color: #13ce66;
  color: white; /* 设置文字颜色为白色，以便在黑色背景上看清楚 */
}


.common-layout {
  width: 100%;
  height: 100%;
}

.el-card {
  margin: 0;
}

.box-card {
  margin: 0;
  width: 100%;
  height: 1000px;
  background-color: #2c3e50;
}
.main-card {
  margin: 0;
  width: 100%;
  height: 100%;
}
.mainC, .aside, .main {
  height: 100%;
}





</style>