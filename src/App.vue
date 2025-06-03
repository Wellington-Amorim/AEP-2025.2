<template>
  <v-app>
    <v-main>
      <router-view></router-view>
    </v-main>
  </v-app>
</template>

<script setup>
// App root component
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden;
}

.v-application {
  height: 100vh !important;
}

.v-main {
  height: 100vh !important;
  padding: 0 !important;
}

.v-main__wrap {
  height: 100vh !important;
}
</style>
