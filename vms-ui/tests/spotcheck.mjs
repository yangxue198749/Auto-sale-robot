import { chromium } from 'playwright'
const ENTRY = 'http://127.0.0.1:8080/dist/index.html'
const browser = await chromium.launch({ channel: 'chrome' })
const page = await browser.newPage({ viewport: { width: 1440, height: 900 } })
await page.goto(`${ENTRY}#/login`, { waitUntil: 'load' })
await page.waitForSelector('form')
await page.fill('input[type="text"]', 'admin')
await page.fill('input[type="password"]', '1')
await page.fill('.captcha-row input', new Date().toISOString().slice(0, 10))
await page.click('button[type="submit"]')
await page.waitForSelector('.sidebar', { timeout: 15000 })

for (const [name, route] of [['vm','/vm'],['sku','/sku'],['mt-node','/mt-node'],['task-search','/task-search'],['delivery-task','/delivery-task']]) {
  await page.goto(`${ENTRY}#${route}`, { waitUntil: 'load' })
  await page.waitForTimeout(1500)
  const rows = await page.locator('.el-table__row').count()
  const total = await page.locator('.el-pagination__total').textContent().catch(() => '')
  console.log(`${name}: tableRows=${rows} pager="${(total||'').trim()}"`)
}
await browser.close()
