<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="6" lg="5">
        <v-card elevation="8" rounded="lg">
          <v-card-title class="pa-6 pb-2">
            <v-icon color="primary" size="40" class="mr-2">mdi-pulse</v-icon>
            <span class="text-h5 font-weight-bold">Create Account</span>
          </v-card-title>
          <v-card-subtitle class="px-6 pb-4">Complete your registration</v-card-subtitle>

          <v-card-text class="px-6">
            <v-alert v-if="error" type="error" class="mb-4" density="compact">{{ error }}</v-alert>
            <v-alert v-if="!tokenValid && tokenChecked" type="error" class="mb-4">
              Invalid or expired invitation link.
            </v-alert>

            <v-form v-if="tokenValid" @submit.prevent="register" ref="form">
              <v-text-field
                v-model="firstName"
                label="First Name"
                variant="outlined"
                :rules="[v => !!v || 'Required']"
                class="mb-3"
              />
              <v-text-field
                v-model="lastName"
                label="Last Name"
                variant="outlined"
                :rules="[v => !!v || 'Required']"
                class="mb-3"
              />
              <v-text-field
                v-if="role === 'INSTRUCTOR'"
                v-model="middleInitial"
                label="Middle Initial (optional)"
                variant="outlined"
                class="mb-3"
                maxlength="1"
              />
              <v-text-field
                :model-value="inviteEmail"
                label="Email"
                variant="outlined"
                readonly
                class="mb-3"
              />
              <v-text-field
                v-model="password"
                label="Password"
                :type="showPw ? 'text' : 'password'"
                variant="outlined"
                :append-inner-icon="showPw ? 'mdi-eye-off' : 'mdi-eye'"
                @click:append-inner="showPw = !showPw"
                :rules="[v => !!v || 'Required', v => v.length >= 6 || 'Min 6 characters']"
                class="mb-3"
              />
              <v-text-field
                v-model="confirmPassword"
                label="Confirm Password"
                type="password"
                variant="outlined"
                :rules="[v => !!v || 'Required', v => v === password || 'Passwords do not match']"
                class="mb-4"
              />
              <v-btn type="submit" color="primary" size="large" block :loading="loading">
                Create Account
              </v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'
import { authApi } from '@/api/index.js'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const firstName = ref('')
const lastName = ref('')
const middleInitial = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPw = ref(false)
const loading = ref(false)
const error = ref('')
const form = ref(null)
const inviteEmail = ref('')
const role = ref('')
const tokenValid = ref(false)
const tokenChecked = ref(false)

onMounted(async () => {
  const token = route.query.token
  if (!token) { tokenChecked.value = true; return }
  try {
    const { data } = await authApi.validateInvitation(token)
    inviteEmail.value = data.email
    role.value = data.role
    tokenValid.value = data.valid
  } catch {
    tokenValid.value = false
  } finally {
    tokenChecked.value = true
  }
})

async function register() {
  const { valid } = await form.value.validate()
  if (!valid) return
  loading.value = true
  error.value = ''
  try {
    const roleResult = await auth.register({
      firstName: firstName.value,
      lastName: lastName.value,
      middleInitial: middleInitial.value || null,
      password: password.value,
      token: route.query.token
    })
    if (roleResult === 'ADMIN') router.push('/admin')
    else if (roleResult === 'STUDENT') router.push('/student')
    else router.push('/instructor')
  } catch (e) {
    error.value = e.response?.data?.error || 'Registration failed.'
  } finally {
    loading.value = false
  }
}
</script>
