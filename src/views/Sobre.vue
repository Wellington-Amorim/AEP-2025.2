<template>
  <div class="sobre">
    <AppNavbar title="Sobre" />
    <div class="content">
      <!-- Conteúdo da página sobre será adicionado aqui -->
    </div>
  </div>
</template>

<script setup>
import AppNavbar from '@/components/AppNavbar.vue';
</script>

<style>
.sobre {
  width: 100%;
  height: 100vh;
  background: transparent !important;
  padding: 0 !important;
  margin: 0 !important;
}

/* Garantir que nenhum elemento tenha padding ou margin indesejado */
:deep(.v-application__wrap),
:deep(.v-main),
:deep(.v-main__wrap) {
  padding: 0 !important;
  margin: 0 !important;
}

.content {
  padding-top: 56px;
  padding: 2rem;
}
</style>
