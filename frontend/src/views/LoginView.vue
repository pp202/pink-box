<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const router = useRouter()

const username = ref('demo')
const password = ref('demo123')

const submit = async () => {
  try {
    await authStore.login(username.value, password.value)
    await router.push('/')
  } catch {
    // no-op (error already set on store)
  }
}
</script>

<template>
  <section>
    <h2>Login</h2>
    <p>Use <strong>demo/demo123</strong>.</p>
    <form @submit.prevent="submit">
      <label>
        Username
        <input v-model="username" required />
      </label>
      <label>
        Password
        <input v-model="password" type="password" required />
      </label>
      <button :disabled="authStore.loading" type="submit">Sign in</button>
    </form>
    <p v-if="authStore.error" class="error">{{ authStore.error }}</p>
  </section>
</template>

<style scoped>
form {
  display: grid;
  gap: 0.75rem;
  max-width: 320px;
}
label {
  display: grid;
  gap: 0.25rem;
}
.error {
  color: #b00020;
}
</style>
