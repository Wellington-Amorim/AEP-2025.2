<template>
  <div class="sobre">
    <v-row class="header-row">
      <v-col cols="12" class="d-flex align-center">
        <v-btn
          color="primary"
          icon="mdi-arrow-left"
          size="large"
          @click="$router.push('/')"
          class="mr-4"
        >
          <v-icon>mdi-arrow-left</v-icon>
          <span class="ml-2">Home</span>
        </v-btn>
        <h1 class="text-h4">Sobre</h1>
      </v-col>
    </v-row>
    <div class="content">
      <!-- Conteúdo da página sobre será adicionado aqui -->
    </div>
  </div>
</template>

<script setup>
// Lógica do componente
</script>

<style>
.sobre {
  width: 100%;
  height: 100vh;
  background: white;
}

.header-row {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: white;
  padding: 1rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  margin: 0;
}

.content {
  margin-top: 80px;
  padding: 2rem;
}

.text-h4 {
  margin: 0;
  font-weight: 500;
}
</style>
