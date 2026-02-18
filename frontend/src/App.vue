<template>
  <div class="app-shell">
    <header>
      <h1>Corck Board</h1>
      <nav>
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/about">About</RouterLink>
        <RouterLink to="/login">Login</RouterLink>
      </nav>
    </header>
    <main>
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app-shell {
  max-width: 900px;
  margin: 0 auto;
  padding: 1rem;
  font-family: Arial, sans-serif;
}
header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ddd;
  margin-bottom: 1rem;
}
nav {
  display: flex;
  gap: 1rem;
}
main {
  padding: 1rem 0;
}
</style>
