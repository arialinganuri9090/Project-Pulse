<template>
  <div>
    <div class="d-flex align-center justify-space-between mb-6">
      <h1 class="text-h4 font-weight-bold">Students</h1>
      <v-btn color="primary" prepend-icon="mdi-email" @click="inviteDialog = true">Invite Students</v-btn>
    </div>
    <v-card rounded="lg" class="mb-4">
      <v-card-text>
        <v-row dense>
          <v-col cols="4"><v-text-field v-model="firstName" label="First Name" variant="outlined" density="compact" clearable @keyup.enter="load" /></v-col>
          <v-col cols="4"><v-text-field v-model="lastName" label="Last Name" variant="outlined" density="compact" clearable @keyup.enter="load" /></v-col>
          <v-col cols="4"><v-text-field v-model="email" label="Email" variant="outlined" density="compact" clearable @keyup.enter="load" /></v-col>
        </v-row>
        <v-btn color="primary" variant="tonal" @click="load" class="mt-2">Search</v-btn>
      </v-card-text>
    </v-card>
    <v-card rounded="lg">
      <v-data-table :headers="headers" :items="students" :loading="loading" item-value="id">
        <template #item.status="{ item }">
          <v-chip :color="item.status === 'ACTIVE' ? 'success' : 'grey'" size="small">{{ item.status }}</v-chip>
        </template>
        <template #item.actions="{ item }">
          <v-btn icon="mdi-eye" variant="text" size="small" :to="`/admin/students/${item.id}`" />
          <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click="confirmDeleteStudent(item)" />
        </template>
      </v-data-table>
    </v-card>

    <!-- Invite Dialog -->
    <v-dialog v-model="inviteDialog" max-width="600">
      <v-card rounded="lg">
        <v-card-title class="pa-4">Invite Students</v-card-title>
        <v-card-text>
          <v-alert type="info" density="compact" class="mb-3">Separate emails with semicolons (;)</v-alert>
          <v-textarea v-model="inviteEmails" label="Email addresses" variant="outlined" rows="4" placeholder="student1@tcu.edu; student2@tcu.edu" />
          <v-select v-model="inviteSectionId" :items="sections" item-title="name" item-value="id" label="Section (optional)" variant="outlined" class="mt-3" clearable />
        </v-card-text>
        <v-card-actions>
          <v-spacer /><v-btn @click="inviteDialog = false">Cancel</v-btn>
          <v-btn color="primary" :loading="inviting" @click="sendInvitations">Send Invitations</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card rounded="lg">
        <v-card-title class="pa-4 text-error">Delete Student?</v-card-title>
        <v-card-text>This permanently deletes the student and all their WARs and peer evaluations.</v-card-text>
        <v-card-actions><v-spacer /><v-btn @click="deleteDialog = false">Cancel</v-btn><v-btn color="error" @click="deleteStudent">Delete</v-btn></v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { studentApi, sectionApi } from '@/api/index.js'

const students = ref([]); const loading = ref(false)
const firstName = ref(''); const lastName = ref(''); const email = ref('')
const inviteDialog = ref(false); const inviteEmails = ref(''); const inviteSectionId = ref(null)
const inviting = ref(false); const sections = ref([])
const deleteDialog = ref(false); const deleteTarget = ref(null)

const headers = [
  { title: 'First Name', key: 'firstName' },
  { title: 'Last Name', key: 'lastName' },
  { title: 'Email', key: 'email' },
  { title: 'Status', key: 'status' },
  { title: 'Actions', key: 'actions', sortable: false }
]

async function load() {
  loading.value = true
  try { students.value = (await studentApi.list({ firstName: firstName.value || undefined, lastName: lastName.value || undefined, email: email.value || undefined })).data }
  finally { loading.value = false }
}

onMounted(async () => {
  sections.value = (await sectionApi.list()).data
  load()
})

async function sendInvitations() {
  inviting.value = true
  try { await studentApi.invite({ emails: inviteEmails.value, sectionId: inviteSectionId.value }); inviteDialog.value = false; inviteEmails.value = '' }
  finally { inviting.value = false }
}

function confirmDeleteStudent(s) { deleteTarget.value = s; deleteDialog.value = true }
async function deleteStudent() { await studentApi.delete(deleteTarget.value.id); deleteDialog.value = false; load() }
</script>
