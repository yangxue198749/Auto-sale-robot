import { api } from './client'

/** 四类工单今日计数（口径与旧工作台一致：未完成=未开始+进行中） */
export interface TaskStatusCounts {
  ongoing: number
  finished: number
  closed: number
}

/** 工作台今日指标汇总 */
export interface WorkbenchSummary {
  date: string
  delivery: TaskStatusCounts
  repair: TaskStatusCounts
  deploy: TaskStatusCounts
  withdraw: TaskStatusCounts
  newNodeCount: number
  newVmCount: number
  eventVmCount: number
  oosVmCount: number
  salesVolume: number
  salesAmount: string
}

export function getWorkbenchSummary(): Promise<WorkbenchSummary> {
  return api.get<WorkbenchSummary>('/vms/api/workbench/summary')
}
