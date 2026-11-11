import { chromium } from 'playwright'
const browser = await chromium.launch({ channel: 'chrome' })
const page = await browser.newPage({ viewport: { width: 1280, height: 900 } })
await page.goto('http://127.0.0.1:8080/ui', { waitUntil: 'load' })
await page.waitForSelector('form', { timeout: 15000 })
// login
await page.fill('input[type="text"]', 'admin')
await page.fill('input[type="password"]', process.argv[2] || '')
await page.fill('.captcha-row input', process.argv[3] || '')
await page.click('button[type="submit"]')
try {
  await page.waitForSelector('.side-menu', { timeout: 15000 })
} catch {
  console.log('LOGIN_FAILED')
  await browser.close()
  process.exit(1)
}
await page.waitForTimeout(500)
// open all groups
const titles = await page.locator('.el-sub-menu__title').all()
for (const t of titles) await t.click()
await page.waitForTimeout(500)
await page.screenshot({ path: 'test-results/menu-grouped.png' })
console.log('OK sidebar groups rendered')
await browser.close()
