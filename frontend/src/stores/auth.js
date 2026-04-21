import { defineStore } from 'pinia'
import { authApi } from '@/api/index.js'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    user: JSON.parse(localStorage.getItem('user') || 'null'),
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === 'ADMIN',
    isStudent: (state) => state.user?.role === 'STUDENT',
    isInstructor: (state) => state.user?.role === 'INSTRUCTOR',
  },
  actions: {
    async login(email, password) {
      const { data } = await authApi.login({ email, password })
      this.token = data.token
      this.user = { id: data.userId, firstName: data.firstName, lastName: data.lastName, email: data.email, role: data.role }
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(this.user))
      return data.role
    },
    async register(payload) {
      const { data } = await authApi.register(payload)
      this.token = data.token
      this.user = { id: data.userId, firstName: data.firstName, lastName: data.lastName, email: data.email, role: data.role }
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(this.user))
      return data.role
    },
    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
