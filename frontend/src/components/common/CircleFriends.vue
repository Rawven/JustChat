<template>
  <el-container class="CC">
    <div class="CC bg-gray-100">
      <div class="flex items-center p-4 border-b border-gray-200 bg-white shadow-sm">
        <svg @click="turnBack"
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="text-gray-600"
        >
          <path d="m12 19-7-7 7-7"></path>
          <path d="M19 12H5"></path>
        </svg>
        <div class="flex-grow text-center font-semibold text-gray-700">朋友圈</div>
        <svg
            @click="create = true"
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="text-gray-600"
        >
          <path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"></path>
          <circle cx="12" cy="13" r="3"></circle>
        </svg>
        <el-drawer
            v-model="create"
            title="I have a nested table inside!"
            direction="rtl"
            size="50%"
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
                  v-model="data.document"
                  :action="`http://` + Host + `:7000/user/upload`"
                  :auto-upload="false"
                  :on-success="handleAvatarSuccess"
                  ref="upload"
              >
                <template #trigger>
                  <el-button >select file</el-button>
                </template>
              </el-upload>
              <el-form-item>
                <el-button  @click="release">发布</el-button>
              </el-form-item>
            </el-form>

          </el-card>
        </el-drawer>

      </div>

      <div class="p-4 bg-white mt-4 rounded-lg shadow-sm">
        <!--      -->
        <div v-for="feedItem in feedData" :key="feedItem.username">
          <div class="flex items-center space-x-3">
      <span class="relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full">
        <img class="aspect-square h-full w-full" alt="user profile" :src="'http://10.24.3.176:8083/ipfs/'+feedItem.avatar"/>
      </span>
            <div>
              <div class="font-semibold text-gray-700">{{feedItem.username}}</div>
              <div class="text-sm text-gray-500">{{feedItem.timestamp}}</div>
            </div>
          </div>
          <div class="mt-3">
            <p class="text-gray-700">{{ feedItem.content }}</p>
            <div v-for="comment in feedItem.comments" :key="comment.commentUsername">
            <div class="mt-2 rounded-md border p-2 bg-gray-100">
              <div class="flex items-center justify-between">
                <div class="flex items-center space-x-2">
            <span class="relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full">
              <img
                  class="aspect-square h-full w-full"
                  alt="user profile"
                  :src="'http://10.24.3.176:8083/ipfs/'+feedItem.avatar"
              />
            </span>
                  <div class="text-xs text-gray-500">{{comment.commentUsername}} - 评论</div>
                </div>
                <div class="text-xs text-gray-500">{{comment.commentTimestamp}}</div>
              </div>
              <p class="mt-2 text-sm text-gray-700">{{comment.commentContent}}</p>
            </div>
            </div>
          </div>
          <div class="mt-3 flex justify-between text-gray-500">
            <svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                class="text-gray-500"
            >
              <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"></path>
            </svg>
            <svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                class="text-gray-500"
            >
              <polyline points="9 17 4 12 9 7"></polyline>
              <path d="M20 18v-2a4 4 0 0 0-4-4H4"></path>
            </svg>
            <svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                class="text-gray-500"
            >
              <path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8"></path>
              <polyline points="16 6 12 2 8 6"></polyline>
              <line x1="12" x2="12" y1="2" y2="15"></line>
            </svg>
          </div>
        </div>
        <div class="px-4 py-2 border-t border-gray-200 bg-white mt-4 rounded-lg shadow-sm">
          <div class="flex items-center justify-between">
            <div class="text-sm text-gray-500">查看全部12条评论</div>
            <svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                class="text-gray-500"
            >
              <circle cx="12" cy="12" r="10"></circle>
              <path d="M17 12h.01"></path>
              <path d="M12 12h.01"></path>
              <path d="M7 12h.01"></path>
            </svg>
          </div>
        </div>
      </div>
    </div>
    <!---->
  </el-container>
</template>

<script>
import {ref} from "vue";
import {Host} from "@/main";

export default {
  // 在你的Vue组件中使用的数据结构
  data() {

    return {
      upload:ref(),
      create:ref(false),
      feedData: [
        {
          username: "用户A",
          timestamp: "5分钟前",
          content: "新年，终于有空🎉",
          avatar: "bafkreihbkvemmrzyecfoyktfqlaiqp3bzz6xsohhqkr2ryl7er56xjksia",
          comments: [
            {
              commentUsername: "用户名",
              commentTimestamp: "00:25",
              commentContent: "@[用户名] 恭喜发财 工作也不要太累喔",
              commentAvatar: "bafkreihbkvemmrzyecfoyktfqlaiqp3bzz6xsohhqkr2ryl7er56xjksia"
            },
            {
              commentUsername: "另一个用户",
              commentTimestamp: "01:30",
              commentContent: "祝好运，一切顺利！",
              commentAvatar:"bafkreihbkvemmrzyecfoyktfqlaiqp3bzz6xsohhqkr2ryl7er56xjksia"
            }
            // 可能还有其他评论
          ],
          likes: 12 // 点赞数
        },
        {
          username: "学生桑",
          timestamp: "13分钟前",
          content: "假期结束了",
          location: "广州市 · 广东工业大学 · 本科生区",
          avatar: "bafkreihbkvemmrzyecfoyktfqlaiqp3bzz6xsohhqkr2ryl7er56xjksia",
          likes: 8 // 点赞数
        },
        {
          username: "A.",
          timestamp: "21分钟前",
          content: "两全一年一得！文文照顾人健康吧",
          avatar: "bafkreihbkvemmrzyecfoyktfqlaiqp3bzz6xsohhqkr2ryl7er56xjksia",
          likes: 5 // 点赞数
        },
        // 可以添加更多测试数据
        {
          username: "测试用户1",
          timestamp: "30分钟前",
          content: "这是一个测试动态",
          avatar: "bafkreihbkvemmrzyecfoyktfqlaiqp3bzz6xsohhqkr2ryl7er56xjksia",
          likes: 3,
          comments: [
            {
              commentUsername: "测试用户2",
              commentTimestamp: "31分钟前",
              commentContent: "这是一个测试评论",
              commentAvatar: "bafkreihbkvemmrzyecfoyktfqlaiqp3bzz6xsohhqkr2ryl7er56xjksia"
            }
          ]
        }
      ],
      data: {
        text: '',      // 朋友圈文本内容
        document: '',  // 朋友圈附加文档内容（例如图片、链接等）
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          // 可以根据需要添加其他验证规则
        ],
        text: [
          { required: true, message: '请输入朋友圈文本内容', trigger: 'blur' },
          // 可以根据需要添加其他验证规则
        ],
        document: [
          { required: true, message: '请输入朋友圈附加文档内容', trigger: 'blur' },
          // 可以根据需要添加其他验证规则
        ],
      },
    };
  },
  created() {
    // this.realAxios.post(`http://` + Host + `:7000/feed/common/getFeed`, {}, {
    //   headers: {
    //     'token': localStorage.getItem("token")
    //   }
    // })
    //     .then(response => {
    //       this.feedData = response.data.data;
    //     })
  },
  methods: {
    Host() {
      return Host
    },
    turnBack() {
      this.$router.push('/common/mainPage');
    },
    release() {
      this.upload.value.submit();
      this.realAxios.post(`http://` + Host + `:7000/feed/common/releaseFeed`, this.data, {
        headers: {
          'token': localStorage.getItem("token")
        }
      })
          .then(() => {
            this.$message.success('发布成功');
            this.create = false;
          })
    },
    handleAvatarSuccess (res) {
      this.data.document = res.data.data;
    },
  }


}
</script>

<style scoped>
.CC {
  height: 100%;
  width: 50cm;
  margin: 0;
  padding: 0;
}
</style>