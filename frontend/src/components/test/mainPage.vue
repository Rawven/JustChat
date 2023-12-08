<template>
  <el-container style="height: 100vh;">
    <el-aside width="300px" class="border-r bg-gray-100 dark:bg-gray-900">
      <el-header class="flex items-center h-20 px-4 border-b bg-white dark:bg-gray-800">
        <h2 class="text-xl font-semibold">Group Chats</h2>
        <el-button class="ml-auto text-sm bg-blue-500 text-white dark:bg-blue-300">New Group</el-button>
      </el-header>
      <el-main class="overflow-y-auto">
        <div class="flex items-center gap-4 p-4 cursor-pointer hover:bg-gray-200 dark:hover:bg-gray-700">
          <el-avatar>
            <el-image alt="Group avatar" src="/placeholder.svg?width=32&height=32" />
            <div>GC</div>
          </el-avatar>
          <div>
            <h3 class="text-sm font-semibold">Group Chat 1</h3>
            <p class="text-sm text-gray-500 dark:text-gray-400">Last message...</p>
          </div>
          <el-badge class="ml-auto">3</el-badge>
        </div>
      </el-main>
    </el-aside>
    <el-container>
      <el-header class="flex items-center h-20 px-4 border-b bg-white dark:bg-gray-800">
        <h2 class="text-xl font-semibold">Group Chat 1</h2>
        <div class="ml-auto flex items-center gap-4">
          <router-link class="text-blue-500 dark:text-blue-300" to="#">
            Group Info
          </router-link>
          <el-button size= default>
            <SettingsIcon class="w-4 h-4" />
            <span class="sr-only">Group settings</span>
          </el-button>
        </div>
      </el-header>
      <el-main class="flex items-start gap-4 p-4">
        <el-avatar>
          <el-image alt="User avatar" src="/placeholder.svg?width=32&height=32" />
          <div>U</div>
        </el-avatar>
        <div>
          <h3 class="text-sm font-semibold">User</h3>
          <p class="text-sm">Message...</p>
        </div>
      </el-main>
      <el-footer class="fixed bottom-0 w-full border-t bg-white dark:bg-gray-800 p-4 flex items-center gap-4">
        <el-input
            class="flex-grow rounded-lg border-gray-300 dark:border-gray-700 p-2"
            placeholder="Type a message..."
        />
        <el-button class="bg-blue-500 text-white dark:bg-blue-300 rounded-full">
          <SendIcon class="w-4 h-4" />
          <span class="sr-only">Send message</span>
        </el-button>
      </el-footer>
    </el-container>
  </el-container>
</template>

<script>
import { ElAvatar, ElImage, ElBadge, ElButton, ElInput } from 'element-plus';
import { defineComponent } from 'vue';
// 使用 PascalCase 风格来定义组件名
export const SendIcon = defineComponent({
  name: 'SendIcon',
  props: ['class'],
  template: `
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="m22 2-7 20-4-9-9-4Z" />
      <path d="M22 2 11 13" />
    </svg>
  `,
});
export const SettingsIcon = defineComponent({
  name: 'SettingsIcon',
  props: ['class'],
  template: `
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  `,
});
export default defineComponent({
  components: {
    SettingsIcon,
    SendIcon,
    ElAvatar,
    ElImage,
    ElBadge,
    ElButton,
    ElInput,
  },
});




</script>
