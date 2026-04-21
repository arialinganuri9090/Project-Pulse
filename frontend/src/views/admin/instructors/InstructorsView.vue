<template>
  <div>
    <div class="d-flex align-center justify-space-between mb-6">
      <h1 class="text-h4 font-weight-bold">Instructors</h1>
      <v-btn color="primary" prepend-icon="mdi-email" @click="inviteDialog = true">Invite Instructors</v-btn>
    </div>
    <v-card rounded="lg" class="mb-4">
      <v-card-text>
        <v-row dense>
          <v-col cols="4"><v-text-field v-model="firstName" label="First Name" variant="outlined" density="compact" clearable /></v-col>
          <v-col cols="4"><v-text-field v-model="lastName" label="Last Name" variant="outlined" density="compact" clearable /></v-col>
          <v-col cols="4">
            <v-select v-model="status" :items="['ACTIVE', 'INACTIVE']" label="Status" variant="outlined" density="compact" clearable />
          </v-col>
        </v-row>
        <v-btn color="primary" variant="tonal" @click="load" class="mt-2">Search</v-btn>
      </v-card-text>
    </v-card>
    <v-card rounded="lg">
      <v-data-table :headers="headers" :items="instructors" :loading="loading" item-value="id">
        <template #item.status="{ item }">
          <v-chip :color="item.status === 'ACTIVE' ? 'success' : 'grey'" size="small">{{ item.status }}</v-chip>
        </template>
        <template #item.actions="{ item }">
          <v-btn icon="mdi-eye" variant="text" size="small" :to="`/admin/instructors/${item.id}`" />
          <v-btn v-if="item.status === 'ACTIVE'" icon="mdi-account-off" variant="text" size="small" color="warning" @click="deactivate(item)" />
          <v-btn v-else icon="mdi-account-check" variant="text" size="small" color="success" @click="reactivate(item)" />
        </template>
      </v-data-table>
    </v-card>

    <v-dialog v-model="inviteDialog" max-width="600">
      <v-card rounded="lg">
        <v-card-title class="pa-4">Invite Instructors</v-card-title>
        <v-card-text>
          <v-alert type="info" density="compact" class="mb-3">Separate emails with semicolons (;)</v-alert>
          <v-textarea v-model="inviteEmails" label="Email addresses" variant="outlined" rows="4" placeholder="instructor@tcu.edu; client@company.com" />
        </v-card-text>
        <v-card-actions>
          <v-spacer /><v-btn @click="inviteDialog = false">Cancel</v-btn>
          <v-btn color="primary" :loading="inviting" @click="sendInvitations">Send Invitations</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { instructorApi } from '@/api/index.js'

const instructors = ref([]); const loading = ref(false)
const firstName = ref(''); const lastName = ref(''); const status = ref(null)
const inviteDialog = ref(false); const inviteEmails = ref(''); const inviting = ref(false)

const headers = [
  { title: 'First Name', key: 'firstName' },
  { title: 'Last Name', key: 'lastName' },
  { title: 'Email', key: 'email' },
  { title: 'Status', key: 'status' },
  { title: 'Actions', key: 'actions', sortable: false }
]

async function load() {
  loading.value = true
  try { instructors.value = (await instructorApi.list({ firstName: firstName.value || undefined, lastName: lastName.value || undefined, status: status.value || undefined })).data }
  finally { loading.value = false }
}
onMounted(load)

async function sendInvitations() {
  inviting.value = true
  try { await instructorApi.invite({ emails: inviteEmails.value }); inviteDialog.value = false; inviteEmails.value = '' }
  finally { inviting.value = false }
}
async function deactivate(i) { await instructorApi.deactivate(i.id); load() }
async function reactivate(i) { await instructorApi.reactivate(i.id); load() }
</script>
