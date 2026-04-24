import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: () => import('@/views/auth/LoginView.vue'), meta: { public: true } },
  { path: '/register', component: () => import('@/views/auth/RegisterView.vue'), meta: { public: true } },

  // Admin routes
  { path: '/admin', component: () => import('@/layouts/AdminLayout.vue'),
    meta: { role: 'ADMIN' },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: () => import('@/views/admin/DashboardView.vue') },
      { path: 'rubrics', component: () => import('@/views/admin/rubrics/RubricsView.vue') },
      { path: 'rubrics/create', component: () => import('@/views/admin/rubrics/CreateRubricView.vue') },
      { path: 'sections', component: () => import('@/views/admin/sections/SectionsView.vue') },
      { path: 'sections/create', component: () => import('@/views/admin/sections/CreateSectionView.vue') },
      { path: 'sections/:id', component: () => import('@/views/admin/sections/SectionDetailView.vue') },
      { path: 'sections/:id/edit', component: () => import('@/views/admin/sections/EditSectionView.vue') },
      { path: 'teams', component: () => import('@/views/admin/teams/TeamsView.vue') },
      { path: 'teams/create', component: () => import('@/views/admin/teams/CreateTeamView.vue') },
      { path: 'teams/:id', component: () => import('@/views/admin/teams/TeamDetailView.vue') },
      { path: 'students', component: () => import('@/views/admin/students/StudentsView.vue') },
      { path: 'students/:id', component: () => import('@/views/admin/students/StudentDetailView.vue') },
      { path: 'instructors', component: () => import('@/views/admin/instructors/InstructorsView.vue') },
      { path: 'instructors/:id', component: () => import('@/views/admin/instructors/InstructorDetailView.vue') },
    ]
  },

  // Student routes
  { path: '/student', component: () => import('@/layouts/StudentLayout.vue'),
    meta: { role: 'STUDENT' },
    children: [
      { path: '', redirect: '/student/dashboard' },
      { path: 'dashboard', component: () => import('@/views/student/DashboardView.vue') },
      { path: 'war', component: () => import('@/views/student/WarView.vue') },
      { path: 'peer-eval', component: () => import('@/views/student/PeerEvalView.vue') },
      { path: 'my-report', component: () => import('@/views/student/MyReportView.vue') },
      { path: 'account', component: () => import('@/views/student/AccountView.vue') },
    ]
  },

  // Instructor routes
  { path: '/instructor', component: () => import('@/layouts/InstructorLayout.vue'),
    meta: { role: 'INSTRUCTOR' },
    children: [
      { path: '', redirect: '/instructor/dashboard' },
      { path: 'dashboard', component: () => import('@/views/instructor/DashboardView.vue') },
      { path: 'section-report', component: () => import('@/views/instructor/SectionReportView.vue') },
      { path: 'team-war', component: () => import('@/views/instructor/TeamWarReportView.vue') },
      { path: 'student-reports/:id', component: () => import('@/views/instructor/StudentReportsView.vue') },
      { path: 'students', component: () => import('@/views/admin/students/StudentsView.vue') },
      { path: 'students/:id', component: () => import('@/views/admin/students/StudentDetailView.vue') },
      { path: 'teams', component: () => import('@/views/admin/teams/TeamsView.vue') },
      { path: 'teams/:id', component: () => import('@/views/admin/teams/TeamDetailView.vue') },
    ]
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  if (to.meta.public) return next()
  if (!auth.isAuthenticated) return next('/login')
  if (to.meta.role && auth.user?.role !== to.meta.role) {
    const role = auth.user?.role
    if (role === 'ADMIN') return next('/admin')
    if (role === 'STUDENT') return next('/student')
    if (role === 'INSTRUCTOR') return next('/instructor')
    return next('/login')
  }
  next()
})

export default router
