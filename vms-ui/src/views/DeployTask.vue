<template>
  <TaskListPage :config="config" />
</template>

<script setup lang="ts">
import TaskListPage from './tasks/TaskListPage.vue'

const config = {
  title: '投放工单',
  hint: '投放工单管理：创建、修改、发布、取消投放任务。',
  apiPath: '/vms/api/deployTask',
  showRemarks: true,
  showPublish: true,
}
</script>
