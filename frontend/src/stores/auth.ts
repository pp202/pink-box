import { defineStore } from 'pinia'
import { apiRequest } from '../api'

export type AuthUser = { username: string }

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as AuthUser | null,
    loading: false,
    error: '' as string,
  }),
  actions: {
    async fetchMe() {
      this.loading = true
      try {
        this.user = await apiRequest<AuthUser>('/api/v1/auth/me')
      } catch {
        this.user = null
      } finally {
        this.loading = false
      }
    },
    async login(username: string, password: string) {
      this.loading = true
      this.error = ''
      try {
        this.user = await apiRequest<AuthUser>(
          '/api/v1/auth/login',
          {
            method: 'POST',
            body: JSON.stringify({ username, password }),
          },
          true,
        )
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Login failed'
        throw error
      } finally {
        this.loading = false
      }
    },
    async logout() {
      await apiRequest<{ status: string }>(
        '/api/v1/auth/logout',
        {
          method: 'POST',
        },
        true,
      )
      this.user = null
    },
  },
})
