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
              <el-table-column label="Room ID" prop="roomId"></el-table-column>
              <el-table-column label="Room Name" prop="roomName"></el-table-column>
              <el-table-column label="创建者" prop="founderName"></el-table-column>
              <el-table-column label="最大人数" prop="maxPeople"></el-table-column>
              <el-table-column label="描述" prop="roomDescription"></el-table-column>
              <el-table-column
                  fixed="right"
                  label="操作"
                  width="200">
                <template v-slot="scope">
                  <el-row>
                    <el-col :span="12">
                      <el-button round type="primary" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                    </el-col>
                    <el-col :span="12">
                      <el-button round type="info" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                    </el-col>
                  </el-row>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination v-model:current-page="currentPage"
                           :page-size="pageSize"
                           :total="totalRooms"
                           background
                           class="pagination-container"
                           layout="prev, pager, next"
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
  name: 'admin_roomPage',
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
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
      this.realAxios.get(`http://` + Host + `:7000/chat/common/queryRoomList/${page}/${8}`, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        // 将获取的房间数组赋值给 rooms
        this.tableData = response.data.data.rooms;
        // 将获取的房间总数赋值给 totalRooms
        this.totalRooms = parseInt(response.data.data.total);
      })
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.getRooms(page);
    },
  }
}
</script>

<style scoped>
.now {
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