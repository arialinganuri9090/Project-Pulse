<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="5" lg="4">
        <v-card elevation="8" rounded="lg">
          <v-card-title class="pa-6 pb-2">
            <v-icon color="primary" size="40" class="mr-2">mdi-pulse</v-icon>
            <span class="text-h5 font-weight-bold">Project Pulse</span>
          </v-card-title>
          <v-card-subtitle class="px-6 pb-4">Sign in to your account</v-card-subtitle>

          <v-card-text class="px-6">
            <v-alert v-if="error" type="error" class="mb-4" density="compact">{{ error }}</v-alert>

            <v-form @submit.prevent="login" ref="form">
              <v-text-field
                v-model="email"
                label="Email"
                type="email"
                prepend-inner-icon="mdi-email"
                variant="outlined"
                :rules="[v => !!v || 'Email is required', v => /.+@.+/.test(v) || 'Invalid email']"
                class="mb-3"
              />
              <v-text-field
                v-model="password"
                label="Password"
                :type="showPassword ? 'text' : 'password'"
                prepend-inner-icon="mdi-lock"
                :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                @click:append-inner="showPassword = !showPassword"
                variant="outlined"
                :rules="[v => !!v || 'Password is required']"
                class="mb-4"
              />
              <v-btn
                type="submit"
                color="primary"
                size="large"
                block
                :loading="loading"
              >
                Sign In
              </v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'

const router = useRouter()
const auth = useAuthStore()

const email = ref('')
const password = ref('')
const showPassword = ref(false)
const loading = ref(false)
const error = ref('')
const form = ref(null)

async function login() {
  const { valid } = await form.value.validate()
  if (!valid) return
  loading.value = true
  error.value = ''
  try {
    const role = await auth.login(email.value, password.value)
    if (role === 'ADMIN') router.push('/admin')
    else if (role === 'STUDENT') router.push('/student')
    else if (role === 'INSTRUCTOR') router.push('/instructor')
  } catch (e) {
    error.value = e.response?.data?.error || 'Login failed. Please check your credentials.'
  } finally {
    loading.value = false
  }
}
</script>
