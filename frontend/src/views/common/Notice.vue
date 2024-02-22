<template>
    <Main class="main"/>
</template>

<script>
import Main from '@/components/common/main/notification.vue';

export default {
  components: {
    // eslint-disable-next-line vue/no-reserved-component-names
    Main
  }
};
</script>
<style>
.main {
  background-color: #f5f5f5;
  width: 100%;
  height: 100%;
}
</style>
