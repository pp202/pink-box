const CSRF_HEADER = 'X-CSRF-TOKEN'

export type ApiError = { error?: string }

let csrfToken: string | null = null

async function ensureCsrfToken(): Promise<string> {
  if (csrfToken) return csrfToken

  const response = await fetch('/api/v1/csrf', {
    method: 'GET',
    credentials: 'include',
  })

  if (!response.ok) {
    throw new Error('Unable to obtain CSRF token')
  }

  const data = (await response.json()) as { token: string }
  csrfToken = data.token
  return csrfToken
}

export async function apiRequest<T>(url: string, init: RequestInit = {}, needsCsrf = false): Promise<T> {
  const headers = new Headers(init.headers ?? {})
  headers.set('Content-Type', 'application/json')

  if (needsCsrf) {
    headers.set(CSRF_HEADER, await ensureCsrfToken())
  }

  const response = await fetch(url, {
    ...init,
    headers,
    credentials: 'include',
  })

  if (!response.ok) {
    const message = (await response.json().catch(() => ({}))) as ApiError
    throw new Error(message.error ?? `Request failed (${response.status})`)
  }

  return (await response.json()) as T
}
