<template>
  <TaskListPage :config="config" />
</template>

<script setup lang="ts">
import TaskListPage from './tasks/TaskListPage.vue'

const config = {
  title: '维护工单',
  hint: '维护工单管理：创建、修改、取消维护任务。',
  apiPath: '/vms/api/repairTask',
  showRemarks: true,
  showPublish: false,
}
</script>
