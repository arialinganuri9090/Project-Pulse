<template>
  <div>
    <div class="d-flex align-center mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/admin/instructors" class="mr-2" />
      <h1 class="text-h4 font-weight-bold">{{ instructor?.firstName }} {{ instructor?.lastName }}</h1>
      <v-spacer />
      <v-btn v-if="instructor?.status === 'ACTIVE'" color="warning" variant="outlined" @click="deactivate">Deactivate</v-btn>
      <v-btn v-else color="success" variant="outlined" @click="reactivate">Reactivate</v-btn>
    </div>
    <v-card v-if="instructor" rounded="lg" max-width="600">
      <v-card-text>
        <v-list density="compact">
          <v-list-item title="Email" :subtitle="instructor.email" />
          <v-list-item title="Status">
            <template #subtitle><v-chip :color="instructor.status === 'ACTIVE' ? 'success' : 'grey'" size="small">{{ instructor.status }}</v-chip></template>
          </v-list-item>
        </v-list>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { instructorApi } from '@/api/index.js'

const route = useRoute()
const instructor = ref(null)

onMounted(async () => { instructor.value = (await instructorApi.get(route.params.id)).data })
async function deactivate() { await instructorApi.deactivate(route.params.id); instructor.value = (await instructorApi.get(route.params.id)).data }
async function reactivate() { await instructorApi.reactivate(route.params.id); instructor.value = (await instructorApi.get(route.params.id)).data }
</script>
