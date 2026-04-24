<template>
  <div>
    <h1 class="text-h4 font-weight-bold mb-6">My Account</h1>
    <v-card rounded="lg" max-width="500">
      <v-card-text>
        <v-alert v-if="success" type="success" class="mb-4" density="compact">Account updated successfully.</v-alert>
        <v-alert v-if="error" type="error" class="mb-4" density="compact">{{ error }}</v-alert>
        <v-form ref="form">
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
            v-model="email"
            label="Email"
            variant="outlined"
            type="email"
            :rules="[v => !!v || 'Required']"
            class="mb-3"
          />
        </v-form>
      </v-card-text>
      <v-card-actions class="pa-4 pt-0">
        <v-spacer />
        <v-btn color="primary" :loading="saving" @click="save">Save Changes</v-btn>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { studentApi } from '@/api/index.js'
import { useAuthStore } from '@/stores/auth.js'

const auth = useAuthStore()
const form = ref(null)
const firstName = ref('')
const lastName = ref('')
const email = ref('')
const saving = ref(false)
const success = ref(false)
const error = ref('')

onMounted(() => {
  firstName.value = auth.user?.firstName || ''
  lastName.value = auth.user?.lastName || ''
  email.value = auth.user?.email || ''
})

async function save() {
  const { valid } = await form.value.validate()
  if (!valid) return
  saving.value = true
  success.value = false
  error.value = ''
  try {
    await studentApi.updateMe({ firstName: firstName.value, lastName: lastName.value, email: email.value })
    success.value = true
  } catch (e) {
    error.value = e.response?.data?.error || 'Failed to update account'
  } finally {
    saving.value = false
  }
}
</script>
