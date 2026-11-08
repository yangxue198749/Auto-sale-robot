// Browser acceptance test: login then walk every Vue page on the deployed /dist build.
// Requires: login.captcha.date.bypass.enabled=true (local only), Tomcat on 8080.
// Usage: node tests/ui.acceptance.mjs
import { chromium } from 'playwright'
import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const ENTRY = 'http://127.0.0.1:8080/dist/index.html'
const shotDir = path.join(path.dirname(fileURLToPath(import.meta.url)), '..', 'test-results', 'screens')
fs.mkdirSync(shotDir, { recursive: true })

// [name, route, expected h1 keyword or null]
const routes = [
  ['home', '/', '工作台'],
  ['delivery-task', '/delivery-task', '配送工单'],
  ['repair-task', '/repair-task', '维护工单'],
  ['deploy-task', '/deploy-task', '投放工单'],
  ['withdraw-task', '/withdraw-task', '撤机工单'],
  ['task-search', '/task-search', '工单查询'],
  ['dictionary', '/dictionary', '字典'],
  ['sku-type', '/sku-type', '商品类型'],
  ['mt-node', '/mt-node', '点位'],
  ['sku', '/sku', '商品管理'],
  ['vm-model', '/vm-model', '型号'],
  ['vm', '/vm', '售货机管理'],
  ['vm-channel', '/vm-channel', '货道配置'],
  ['vm-price', '/vm-price', '价格配置'],
  ['vm-energy', '/vm-energy', '节能配置'],
  ['vm-shortcuts', '/vm-shortcuts', '闪购键配置'],
  ['vm-text', '/vm-text', '走马灯配置'],
  ['vm-theme', '/vm-theme', '皮肤配置'],
  ['channel-strategy', '/channel-strategy', '货道策略'],
  ['shortcut-strategy', '/shortcut-strategy', '闪购键策略'],
  ['text-strategy', '/text-strategy', '走马灯策略'],
  ['energy-strategy', '/energy-strategy', '节能策略'],
  ['theme-strategy', '/theme-strategy', '皮肤策略'],
  ['price-strategy', '/price-strategy', '价格策略'],
  ['sku-event-strategy', '/sku-event-strategy', '商品促销策略'],
  ['vm-event-strategy', '/vm-event-strategy', '售货机促销策略'],
  ['applied-event', '/applied-event', '应用促销'],
  ['role', '/role', '角色管理'],
  ['user', '/user', '用户管理'],
  ['sys-log', '/sys-log', '系统日志'],
  ['settings', '/settings', '系统设置'],
]

const results = []
let pass = 0
let fail = 0

const browser = await chromium.launch({ channel: 'chrome' })
const page = await browser.newPage({ viewport: { width: 1440, height: 900 } })

const apiErrors = []
let consoleErrors = []
let pageErrors = []

page.on('console', (msg) => {
  if (msg.type() === 'error') consoleErrors.push(msg.text())
})
page.on('pageerror', (err) => {
  pageErrors.push(String(err))
})
page.on('response', (res) => {
  if (res.url().includes('/vms/api/') && res.status() >= 400) {
    apiErrors.push(`${res.status()} ${res.url()}`)
  }
})

// ---------- login ----------
await page.goto(`${ENTRY}#/login`, { waitUntil: 'load' })
await page.waitForSelector('form', { timeout: 10000 })
await page.fill('input[type="text"]', 'admin')
await page.fill('input[type="password"]', '1')
const today = new Date().toISOString().slice(0, 10)
await page.fill('.captcha-row input', today)
await page.click('button[type="submit"]')
try {
  await page.waitForSelector('.sidebar', { timeout: 15000 })
  console.log('[PASS] login -> sidebar visible')
  pass++
} catch {
  console.log('[FAIL] login did not reach workbench:', consoleErrors.join(' | '))
  await page.screenshot({ path: path.join(shotDir, 'login-fail.png') })
  await browser.close()
  process.exit(1)
}
await page.waitForTimeout(800)
await page.screenshot({ path: path.join(shotDir, '00-login-ok.png') })

// ---------- walk pages ----------
for (const [name, route, keyword] of routes) {
  consoleErrors = []
  pageErrors = []
  const pageApiErrors = []
  const handler = (res) => {
    if (res.url().includes('/vms/api/') && res.status() >= 400) {
      pageApiErrors.push(`${res.status()} ${res.url().replace('http://127.0.0.1:8080', '')}`)
    }
  }
  page.on('response', handler)

  let ok = true
  const notes = []
  try {
    await page.goto(`${ENTRY}#${route}`, { waitUntil: 'load' })
    await page.waitForSelector('h1', { timeout: 8000 })
    await page.waitForLoadState('networkidle').catch(() => {})
    await page.waitForTimeout(700)
  } catch (e) {
    ok = false
    notes.push('load/h1 timeout: ' + String(e).slice(0, 120))
  }

  const h1 = ok ? ((await page.locator('h1').first().textContent()) || '').trim() : ''
  if (ok && keyword && !h1.includes(keyword)) {
    ok = false
    notes.push(`h1 "${h1}" lacks "${keyword}"`)
  }
  if (consoleErrors.length) {
    ok = false
    notes.push('console: ' + consoleErrors.slice(0, 3).join(' | '))
  }
  if (pageErrors.length) {
    ok = false
    notes.push('pageerror: ' + pageErrors.slice(0, 2).join(' | '))
  }
  if (pageApiErrors.length) {
    ok = false
    notes.push('api: ' + pageApiErrors.slice(0, 3).join(', '))
  }

  await page.screenshot({ path: path.join(shotDir, `${name}.png`) }).catch(() => {})
  page.off('response', handler)

  if (ok) {
    console.log(`[PASS] ${name} (h1: ${h1})`)
    pass++
  } else {
    console.log(`[FAIL] ${name} :: ${notes.join(' ; ')}`)
    fail++
  }
  results.push({ name, route, h1, ok, notes })
}

// ---------- logout state ----------
await page.goto(`${ENTRY}#/about`, { waitUntil: 'load' })
console.log('')
console.log(`=== ${pass} passed, ${fail} failed ===`)
fs.writeFileSync(
  path.join(shotDir, '..', 'acceptance.json'),
  JSON.stringify(results, null, 2),
)
await browser.close()
process.exit(fail ? 1 : 0)
