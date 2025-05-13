<template>
  <div id="app">
    <!-- opcional: nav global -->
    <nav class="app-nav">
      <router-link to="/">Home</router-link> |
      <router-link to="/mapa">Mapa</router-link> |
      <router-link to="/simulador">Simulador</router-link>
    </nav>

    <!-- aqui é onde a view atual será renderizada -->
    <router-view />
  </div>
</template>

<script setup>
// não precisa mais importar manualmente as views ou o CesiumMap aqui
</script>

<style>
/* só um estilo simples para a nav */
.app-nav {
  padding: 8px 16px;
  background: #333;
}
.app-nav a {
  color: white;
  margin-right: 12px;
  text-decoration: none;
}
.app-nav a.router-link-active {
  font-weight: bold;
}
</style>
