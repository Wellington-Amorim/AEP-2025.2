<template>
    <div class="simulador-container">
      <!-- Sidebar com controles -->
      <aside class="sidebar">
        <h2>Simulador Urbano</h2>
        <p>Escolha o tipo de zona para adicionar:</p>
        <ul class="zone-list">
          <li><button @click="selecionarZona('residencial')">Residencial</button></li>
          <li><button @click="selecionarZona('industrial')">Industrial</button></li>
          <li><button @click="selecionarZona('comercial')">Comercial</button></li>
          <li><button @click="selecionarZona('area-verde')">Área Verde</button></li>
        </ul>
        <div class="info">
          <p><strong>Zona selecionada:</strong></p>
          <p v-if="zona">{{ zona }}</p>
          <p v-else>Nenhuma</p>
        </div>
      </aside>
  
      <!-- Espaço principal (mapa, gráficos, etc) -->
      <main class="main-view">
        <h3>Visão de Simulação</h3>
        <p>Aqui aparecerá o seu mapa 3D e as simulações.</p>
        <!-- futuramente: <CesiumMap /> -->
      </main>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  
  const zona = ref(null)
  
  function selecionarZona(tipo) {
    zona.value = tipo
    // aqui podemos disparar lógica para destacar área no mapa,...
  }
  </script>
  
  <style scoped>
  .simulador-container {
    display: flex;
    height: 100vh;
    font-family: sans-serif;
  }
  
  /* sidebar */
  .sidebar {
    width: 240px;
    padding: 16px;
    background: #f5f5f5;
    border-right: 1px solid #ddd;
  }
  .sidebar h2 {
    margin-top: 0;
  }
  .zone-list {
    list-style: none;
    padding: 0;
  }
  .zone-list li + li {
    margin-top: 8px;
  }
  .zone-list button {
    width: 100%;
    padding: 8px;
    cursor: pointer;
  }
  .info {
    margin-top: 24px;
    font-size: 14px;
  }
  
  /* área principal */
  .main-view {
    flex: 1;
    padding: 24px;
  }
  .main-view h3 {
    margin-top: 0;
  }
  </style>
  