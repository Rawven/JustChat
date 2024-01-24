<template>
  <el-container class="CC">
    <div class="CC bg-gray-100">
      <el-header class="flex items-center p-4 border-b border-gray-200 bg-white shadow-sm">
        <svg class="text-gray-600"
             fill="none"
             height="24"
             stroke="currentColor"
             stroke-linecap="round"
             stroke-linejoin="round"
             stroke-width="2"
             viewBox="0 0 24 24"
             width="24"
             xmlns="http://www.w3.org/2000/svg"
             @click="turnBack"
        >
          <path d="m12 19-7-7 7-7"></path>
          <path d="M19 12H5"></path>
        </svg>
        <div class="flex-grow text-center font-semibold text-gray-700">朋友圈</div>
        <svg
            class="text-gray-600"
            fill="none"
            height="24"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            viewBox="0 0 24 24"
            width="24"
            xmlns="http://www.w3.org/2000/svg"
            @click="create = true"
        >
          <path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"></path>
          <circle cx="12" cy="13" r="3"></circle>
        </svg>
        <el-drawer
            v-model="create"
            direction="rtl"
            size="50%"
            title="I have a nested table inside!"
        >
          <el-card class="box-card">
            <el-header>
              <el-text class="title">来发布你的朋友圈吧👆👨</el-text>
            </el-header>
            <el-form ref="releaseForm" :model="data" :rules="rules" label-width="80px">
              <el-form-item label="Text" prop="text">
                <el-input v-model="data.text" required></el-input>
              </el-form-item>
              <el-text>上传图片</el-text>
              <el-upload
                  :http-request="uploadFile"
                  action=""
                  class="upload-demo">
                <el-button size="small" type="primary">点击上传</el-button>
                <template #tip>
                  <div class="el-upload__tip">
                    jpg/png files with a size less than 500KB.
                  </div>
                </template>
              </el-upload>
              <el-form-item>
                <el-button @click="release">发布</el-button>
              </el-form-item>
            </el-form>

          </el-card>
        </el-drawer>
      </el-header>
      <el-main class="p-4 bg-white mt-4 rounded-lg shadow-sm ccc">
        <!--      -->
        <div v-for="moment in feedData" :key="moment.momentId">
          <div class="flex items-center space-x-3">
      <span class="relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full">
        <img :src="ipfsHost()+moment.userInfo.profile" alt="user profile" class="aspect-square h-full w-full"/>
      </span>
            <div>
              <div class="font-semibold text-gray-700">{{ moment.userInfo.username }}</div>
              <div class="text-sm text-gray-500">{{ timestampToTime(moment.timestamp) }}</div>
            </div>
          </div>
          <div class="mt-3">
            <p class="text-gray-700">{{ moment.content }}</p>
            <img :src="ipfsHost()+moment.img"
                 alt="user profile" class="img-size">

            <!-- Likes -->
            <div v-for="like in moment.likes" :key="like.userInfo.username">
              <el-text type="primary">{{ like.userInfo.username }} - 点赞过</el-text>
            </div>
            <div v-for="comment in moment.comments" :key="comment.userInfo.username">
              <div class="mt-2 rounded-md border p-2 bg-gray-100">
                <div class="flex items-center justify-between">
                  <div class="flex items-center space-x-2">
            <span class="relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full">
              <img
                  :src="ipfsHost()+comment.userInfo.profile"
                  alt="user profile"
                  class="aspect-square h-full w-full"
              />
            </span>
                    <div class="text-xs text-gray-500">{{ comment.userInfo.username }} - 评论</div>
                  </div>
                  <div class="text-xs text-gray-500">{{ comment.userInfo.timestamp }}</div>
                </div>
                <p class="mt-2 text-sm text-gray-700">{{ comment.content }}</p>
              </div>
            </div>
          </div>
          <div class="mt-3 flex justify-between text-gray-500">
            <svg
                class="text-gray-500"
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
            <svg
                class="text-gray-500"
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
              <polyline points="9 17 4 12 9 7"></polyline>
              <path d="M20 18v-2a4 4 0 0 0-4-4H4"></path>
            </svg>
            <svg
                class="text-gray-500"
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
              <path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8"></path>
              <polyline points="16 6 12 2 8 6"></polyline>
              <line x1="12" x2="12" y1="2" y2="15"></line>
            </svg>
          </div>
          <div class="mt-3 flex justify-between text-gray-500">
            <el-button @click="tolike(moment.momentId)">
              <el-icon>
                <Star/>
              </el-icon>
            </el-button>
            <el-input v-model="moment.input" placeholder="要评论吗"
                      @keyup.enter="submitComment(moment.momentId,moment.input)"/>
          </div>
        </div>
      </el-main>
    </div>
    <!---->
  </el-container>
</template>

<script>
import {ref} from "vue";
import {Host, ipfsHost} from "@/main";
import {Star} from "@element-plus/icons-vue";

export default {
  name: 'Moment-Fuck',
  components: {Star},
  // 在你的Vue组件中使用的数据结构
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data() {

    return {
      host: Host,
      input: "",
      file: "",
      token: "",
      upload: ref(),
      create: ref(false),
      comment: {
        userInfo: "",
        timestamp: "",
        content: "",
      },
      like: {
        userInfo: "",
        timestamp: "",
      },
      moment: {
        momentId: "",
        userInfo: "",
        timestamp: "",
        content: "",
        img: "",
        likes: [],
        comments: []
      },
      feedData: [],
      data: {
        text: '',      // 朋友圈文本内容
        img: '',  // 朋友圈附加文档内容（例如图片、链接等）
      },
      rules: {
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          // 可以根据需要添加其他验证规则
        ],
        text: [
          {required: true, message: '请输入朋友圈文本内容', trigger: 'blur'},
          // 可以根据需要添加其他验证规则
        ],
        document: [
          {required: true, message: '请输入朋友圈附加文档内容', trigger: 'blur'},
          // 可以根据需要添加其他验证规则
        ],
      },
    };
  },
  created() {
    this.realAxios.get(`http://` + Host + `:7000/social/queryMoment`, {
      headers: {
        'token': localStorage.getItem("token")
      }
    }).then(response => {
      this.feedData = response.data.data;
    })
    this.token = localStorage.getItem("token");
  },
  methods: {
    ipfsHost() {
      return ipfsHost
    },
    Host() {
      return Host
    },
    turnBack() {
      this.$router.push('/common/mainPage');
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
    },
    uploadFile(param) {
      const formData = new FormData()
      formData.append('file', param.file)
      const url = 'http://' + Host + ':7000/user/upload'
      this.realAxios.post(url, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        this.data.img = response.data.data;
        this.$message.success('文件上传成功');
      })
    },
    tolike(momentId) {
      this.realAxios.get(`http://` + Host + `:7000/social/likeMoment/${momentId}`,
          {
            headers: {
              'token': localStorage.getItem("token")
            }
          })
          .then(() => {
            this.$message.success('点赞成功');
          })
      this.realAxios.get(`http://` + Host + `:7000/social/queryMoment`, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        this.feedData = response.data.data;
      })
    },
    submitComment(momentId, text) {
      this.realAxios.post(`http://` + Host + `:7000/social/commentMoment`, {
            momentId: momentId,
            text: text
          },
          {
            headers: {
              'token': localStorage.getItem("token")
            }
          })
          .then(() => {
            this.$message.success('评论成功');
            this.input = "";
          })
      this.realAxios.get(`http://` + Host + `:7000/social/queryMoment`, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        this.feedData = response.data.data;
      })
    },
    release() {
      this.realAxios.post(`http://` + Host + `:7000/social/releaseMoment`, this.data, {
        headers: {
          'token': localStorage.getItem("token")
        }
      })
          .then(() => {
            this.$message.success('发布成功');
            this.create = false;
          })
      this.realAxios.get(`http://` + Host + `:7000/social/queryMoment`, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        this.feedData = response.data.data;
      })
    },
  }


}
</script>

<style scoped>
.CC {
  height: 100%;
  width: 56cm;
  margin: 0;
  padding: 0;
}

.img-size {
  width: 200px;
  height: 200px;
}

.ccc {
  height: 50%; /* 设置高度为视口的100% */
  overflow-y: auto; /* 当内容溢出时显示滚动条 */
  padding-top: 30px;
}
</style>