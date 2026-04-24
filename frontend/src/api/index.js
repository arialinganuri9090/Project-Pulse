import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export default api

export const authApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  me: () => api.get('/auth/me'),
  validateInvitation: (token) => api.get(`/auth/invitation/${token}`)
}

export const rubricApi = {
  create: (data) => api.post('/rubrics', data),
  list: () => api.get('/rubrics'),
  get: (id) => api.get(`/rubrics/${id}`)
}

export const sectionApi = {
  create: (data) => api.post('/sections', data),
  list: (params) => api.get('/sections', { params }),
  get: (id) => api.get(`/sections/${id}`),
  update: (id, data) => api.put(`/sections/${id}`, data),
  setupWeeks: (id, data) => api.post(`/sections/${id}/active-weeks`, data),
  getWeeks: (id) => api.get(`/sections/${id}/active-weeks`)
}

export const teamApi = {
  create: (data) => api.post('/teams', data),
  list: (params) => api.get('/teams', { params }),
  get: (id) => api.get(`/teams/${id}`),
  myTeam: () => api.get('/teams/my-team'),
  update: (id, data) => api.put(`/teams/${id}`, data),
  delete: (id) => api.delete(`/teams/${id}`),
  assignStudents: (data) => api.post('/teams/assign-students', data),
  removeStudent: (teamId, studentId) => api.delete(`/teams/${teamId}/students/${studentId}`),
  assignInstructors: (data) => api.post('/teams/assign-instructors', data),
  removeInstructor: (teamId, instructorId) => api.delete(`/teams/${teamId}/instructors/${instructorId}`)
}

export const studentApi = {
  list: (params) => api.get('/students', { params }),
  get: (id) => api.get(`/students/${id}`),
  delete: (id) => api.delete(`/students/${id}`),
  invite: (data) => api.post('/students/invite', data),
  updateMe: (data) => api.put('/students/me', data)
}

export const instructorApi = {
  list: (params) => api.get('/instructors', { params }),
  get: (id) => api.get(`/instructors/${id}`),
  invite: (data) => api.post('/instructors/invite', data),
  deactivate: (id) => api.put(`/instructors/${id}/deactivate`),
  reactivate: (id) => api.put(`/instructors/${id}/reactivate`)
}

export const warApi = {
  getWeeks: () => api.get('/wars/weeks'),
  getWar: (weekId) => api.get(`/wars/${weekId}`),
  addActivity: (weekId, data) => api.post(`/wars/${weekId}/activities`, data),
  updateActivity: (weekId, activityId, data) => api.put(`/wars/${weekId}/activities/${activityId}`, data),
  deleteActivity: (weekId, activityId) => api.delete(`/wars/${weekId}/activities/${activityId}`)
}

export const peerEvalApi = {
  getWeeks: () => api.get('/peer-evaluations/weeks'),
  getMyEvals: (weekId) => api.get(`/peer-evaluations/${weekId}`),
  submit: (weekId, data) => api.post(`/peer-evaluations/${weekId}`, data),
  getMyReport: (weekId) => api.get(`/peer-evaluations/my-report/${weekId}`)
}

export const reportApi = {
  sectionPeerEval: (sectionId, weekId) => api.get(`/reports/peer-eval/section/${sectionId}`, { params: { weekId } }),
  studentPeerEval: (studentId, startWeekId, endWeekId) =>
    api.get(`/reports/peer-eval/student/${studentId}`, { params: { startWeekId, endWeekId } }),
  teamWar: (teamId, weekId) => api.get(`/reports/war/team/${teamId}`, { params: { weekId } }),
  studentWar: (studentId, startWeekId, endWeekId) =>
    api.get(`/reports/war/student/${studentId}`, { params: { startWeekId, endWeekId } })
}
