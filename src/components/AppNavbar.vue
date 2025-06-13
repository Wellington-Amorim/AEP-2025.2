<template>
  <v-app-bar class="navbar" elevation="0">
    <div class="navbar-content">
      <v-btn
        color="primary"
        class="home-btn"
        @click="$router.push('/')"
      >
        <v-icon>mdi-home</v-icon>
        <span class="ml-2">Home</span>
      </v-btn>
      <h1 class="text-h4">{{ title }}</h1>
      <v-spacer></v-spacer>
      <slot></slot>
    </div>
  </v-app-bar>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  }
})
</script>

<style scoped>
.navbar {
  background: transparent !important;
  height: 40px !important;
  margin: 0 !important;
  padding: 0 !important;
  box-shadow: none !important;
  position: absolute;
  top: 16px;
  left: 8px;
  right: 8px;
  z-index: 100;
}

:deep(.v-toolbar__content),
:deep(.v-app-bar),
:deep(.v-toolbar),
:deep(.v-toolbar__wrapper) {
  background: transparent !important;
  padding: 0 !important;
  margin: 0 !important;
  height: 40px !important;
  min-height: 40px !important;
  box-shadow: none !important;
  background-color: transparent !important;
  background-image: none !important;
}

.navbar-content {
  background: rgba(33, 33, 64, 0.8);
  backdrop-filter: blur(10px);
  height: 40px;
  padding: 0 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  width: 100%;
  gap: 16px;
}

.text-h4 {
  font-size: 1.25rem !important;
  font-weight: 500;
  margin: 0;
  line-height: 40px;
  color: white;
}

.home-btn,
.data-toggle-btn {
  height: 32px;
  border-radius: 16px;
  padding: 0 16px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-weight: 500;
  text-transform: none;
  letter-spacing: normal;
  backdrop-filter: blur(5px);
}

.home-btn:hover,
.data-toggle-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}
</style>
