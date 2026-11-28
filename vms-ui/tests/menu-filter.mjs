// Menu permission-filter check: login as a given user, count visible sidebar entries.
// Usage: node tests/menu-filter.mjs admin 1   |   node tests/menu-filter.mjs lowpriv 1
import { chromium } from 'playwright'

const user = process.argv[2] || 'admin'
const pwd = process.argv[3] || '1'
const BASE = process.env.BASE || 'http://127.0.0.1:8081'
const d = new Date()
const today = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`

const browser = await chromium.launch({ channel: 'chrome' })
const page = await browser.newPage({ viewport: { width: 1440, height: 900 } })
await page.goto(`${BASE}/dist/index.html#/login`, { waitUntil: 'load' })
await page.fill('input[type="text"]', user)
await page.fill('input[type="password"]', pwd)
await page.fill('.captcha-row input', today)
await page.click('button[type="submit"]')
await page.waitForSelector('.sidebar', { timeout: 15000 })
await page.waitForTimeout(600)
const items = await page.locator('.side-menu .el-menu-item:visible').count()
const groups = await page.locator('.side-menu .el-sub-menu:visible').count()
console.log(`user=${user} visible top-level+child items=${items} groups=${groups}`)
await page.screenshot({ path: 'test-results/menu-filter-' + user + '.png', fullPage: false })
await browser.close()
