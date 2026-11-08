type RequestOptions = {
  form?: boolean
}

/**
 * API 错误类型
 */
export type ApiErrorType = 'unauthorized' | 'forbidden' | 'server' | 'network'

/**
 * API 错误类
 */
export class ApiError extends Error {
  constructor(
    public type: ApiErrorType,
    message: string,
    public status?: number,
  ) {
    super(message)
    this.name = 'ApiError'
  }
}

/**
 * 通用 API 客户端。所有请求走 /api 前缀，由 Vite 代理转发到 127.0.0.1:8080
 * 并自动带上 cookie（credentials: 'include'）。
 */
export const api = {
  async get<T = any>(path: string): Promise<T> {
    return request<T>(path, { method: 'GET' })
  },
  async post<T = any>(path: string, body: any, opts: RequestOptions = {}): Promise<T> {
    return request<T>(path, {
      method: 'POST',
      body: opts.form ? toForm(body) : JSON.stringify(body),
      contentType: opts.form ? 'application/x-www-form-urlencoded' : 'application/json',
    })
  },
  async upload<T = any>(path: string, formData: FormData): Promise<T> {
    return request<T>(path, {
      method: 'POST',
      body: formData,
      // 上传让浏览器自动设置 multipart boundary
      contentType: undefined,
    })
  },
}

async function request<T>(
  path: string,
  init: { method: string; body?: BodyInit; contentType?: string },
): Promise<T> {
  const url = `/api${path.startsWith('/') ? path : '/' + path}`
  const headers = new Headers()
  if (init.contentType) headers.set('Content-Type', init.contentType)

  let resp: Response
  try {
    resp = await fetch(url, {
      method: init.method,
      headers,
      body: init.body,
      credentials: 'include', // 关键：带 cookie 让 Shiro session 生效
    })
  } catch (error) {
    // 网络错误（断网、DNS 失败等）
    throw new ApiError('network', '网络错误，请检查网络连接')
  }

  // 401: 未登录，跳登录页并保留原始路径供重定向
  if (resp.status === 401) {
    // 避免循环：如果已经在登录页就不跳
    if (window.location.pathname !== '/login') {
      window.location.href = '/login?redirect=' + encodeURIComponent(window.location.pathname)
    }
    throw new ApiError('unauthorized', '未登录，请重新登录', 401)
  }

  // 403: 无权限，不跳转，抛出错误
  if (resp.status === 403) {
    throw new ApiError('forbidden', '没有权限执行此操作', 403)
  }

  // 500+: 服务器错误，不跳转，抛出错误
  if (resp.status >= 500) {
    throw new ApiError('server', '服务器错误，请稍后重试', resp.status)
  }

  // 后端 302 跳登录 = 未登录，浏览器 fetch 不会跟随跨域 302，需要识别
  if (resp.redirected) {
    throw new ApiError('unauthorized', '未登录，请重新登录', 302)
  }

  const contentType = resp.headers.get('content-type') || ''
  if (contentType.includes('application/json')) {
    return (await resp.json()) as T
  }
  return (await resp.text()) as unknown as T
}

function toForm(obj: Record<string, any>): URLSearchParams {
  const form = new URLSearchParams()
  for (const [k, v] of Object.entries(obj)) {
    if (v !== undefined && v !== null) {
      form.append(k, Array.isArray(v) ? v.join(',') : String(v))
    }
  }
  return form
}
