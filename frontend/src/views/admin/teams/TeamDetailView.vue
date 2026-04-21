<template>
  <div>
    <div class="d-flex align-center mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/admin/teams" class="mr-2" />
      <h1 class="text-h4 font-weight-bold">{{ team?.name || 'Team' }}</h1>
      <v-spacer />
      <v-btn color="error" variant="outlined" @click="confirmDelete = true" class="mr-2">Delete</v-btn>
    </div>

    <v-row v-if="team">
      <v-col cols="12" md="5">
        <v-card rounded="lg" class="mb-4">
          <v-card-title class="d-flex align-center justify-space-between">
            Details
            <v-btn size="small" color="primary" variant="tonal" @click="editDialog = true">Edit</v-btn>
          </v-card-title>
          <v-card-text>
            <v-list density="compact">
              <v-list-item title="Description" :subtitle="team.description || 'N/A'" />
              <v-list-item title="Website" :subtitle="team.websiteUrl || 'N/A'" />
              <v-list-item title="Section" :subtitle="team.section?.name || 'N/A'" />
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="7">
        <!-- Students -->
        <v-card rounded="lg" class="mb-4">
          <v-card-title class="d-flex align-center justify-space-between">
            Students ({{ team.students?.length || 0 }})
            <v-btn size="small" color="primary" variant="tonal" @click="assignStudentDialog = true">Assign</v-btn>
          </v-card-title>
          <v-card-text>
            <v-chip
              v-for="s in team.students" :key="s.id"
              closable
              class="ma-1"
              @click:close="removeStudent(s)"
            >{{ s.firstName }} {{ s.lastName }}</v-chip>
            <p v-if="!team.students?.length" class="text-medium-emphasis">No students assigned.</p>
          </v-card-text>
        </v-card>

        <!-- Instructors -->
        <v-card rounded="lg">
          <v-card-title class="d-flex align-center justify-space-between">
            Instructors ({{ team.instructors?.length || 0 }})
            <v-btn size="small" color="primary" variant="tonal" @click="assignInstructorDialog = true">Assign</v-btn>
          </v-card-title>
          <v-card-text>
            <v-chip v-for="i in team.instructors" :key="i.id" closable class="ma-1" @click:close="removeInstructor(i)">
              {{ i.firstName }} {{ i.lastName }}
            </v-chip>
            <p v-if="!team.instructors?.length" class="text-medium-emphasis">No instructors assigned.</p>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Edit Dialog -->
    <v-dialog v-model="editDialog" max-width="500">
      <v-card rounded="lg">
        <v-card-title class="pa-4">Edit Team</v-card-title>
        <v-card-text>
          <v-text-field v-model="editName" label="Team Name" variant="outlined" class="mb-3" />
          <v-textarea v-model="editDescription" label="Description" variant="outlined" rows="3" class="mb-3" />
          <v-text-field v-model="editUrl" label="Website URL" variant="outlined" />
        </v-card-text>
        <v-card-actions>
          <v-spacer /><v-btn @click="editDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="saveEdit">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Assign Students Dialog -->
    <v-dialog v-model="assignStudentDialog" max-width="500">
      <v-card rounded="lg">
        <v-card-title class="pa-4">Assign Students</v-card-title>
        <v-card-text>
          <v-autocomplete
            v-model="selectedStudents"
            :items="availableStudents"
            :item-title="s => s.firstName + ' ' + s.lastName + ' (' + s.email + ')'"
            item-value="id"
            label="Select Students"
            variant="outlined"
            multiple
            chips
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer /><v-btn @click="assignStudentDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="assignStudents">Assign</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Assign Instructors Dialog -->
    <v-dialog v-model="assignInstructorDialog" max-width="500">
      <v-card rounded="lg">
        <v-card-title class="pa-4">Assign Instructors</v-card-title>
        <v-card-text>
          <v-autocomplete
            v-model="selectedInstructors"
            :items="availableInstructors"
            :item-title="i => i.firstName + ' ' + i.lastName + ' (' + i.email + ')'"
            item-value="id"
            label="Select Instructors"
            variant="outlined"
            multiple
            chips
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer /><v-btn @click="assignInstructorDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="assignInstructors">Assign</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete Confirm -->
    <v-dialog v-model="confirmDelete" max-width="400">
      <v-card rounded="lg">
        <v-card-title class="pa-4 text-error">Delete Team?</v-card-title>
        <v-card-text>This will permanently delete the team and all associated WARs and peer evaluations.</v-card-text>
        <v-card-actions><v-spacer /><v-btn @click="confirmDelete = false">Cancel</v-btn><v-btn color="error" @click="deleteTeam">Delete</v-btn></v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { teamApi, studentApi, instructorApi } from '@/api/index.js'

const route = useRoute(); const router = useRouter()
const team = ref(null)
const editDialog = ref(false); const assignStudentDialog = ref(false); const assignInstructorDialog = ref(false); const confirmDelete = ref(false)
const editName = ref(''); const editDescription = ref(''); const editUrl = ref('')
const availableStudents = ref([]); const availableInstructors = ref([])
const selectedStudents = ref([]); const selectedInstructors = ref([])

async function load() {
  team.value = (await teamApi.get(route.params.id)).data
  editName.value = team.value.name; editDescription.value = team.value.description; editUrl.value = team.value.websiteUrl
}
onMounted(async () => {
  await load()
  availableStudents.value = (await studentApi.list()).data
  availableInstructors.value = (await instructorApi.list()).data
})

async function saveEdit() {
  await teamApi.update(route.params.id, { name: editName.value, description: editDescription.value, websiteUrl: editUrl.value, sectionId: team.value.section?.id })
  await load(); editDialog.value = false
}
async function removeStudent(s) { await teamApi.removeStudent(route.params.id, s.id); await load() }
async function removeInstructor(i) { await teamApi.removeInstructor(route.params.id, i.id); await load() }
async function assignStudents() {
  await teamApi.assignStudents({ teamId: Number(route.params.id), userIds: selectedStudents.value })
  await load(); assignStudentDialog.value = false; selectedStudents.value = []
}
async function assignInstructors() {
  await teamApi.assignInstructors({ teamId: Number(route.params.id), userIds: selectedInstructors.value })
  await load(); assignInstructorDialog.value = false; selectedInstructors.value = []
}
async function deleteTeam() { await teamApi.delete(route.params.id); router.push('/admin/teams') }
</script>
