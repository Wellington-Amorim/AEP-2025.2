<template>
    <div class="home">
      <h1>Planejamento Urbano Inteligente</h1>
      <p>Bem-vindo ao sistema de mapeamento e organização de cidades com IA</p>
      
      <div class="buttons">
        <router-link to="/mapa">
          <button>Ver Mapa Interativo</button>
        </router-link>
        <router-link to="/simulador">
          <button>Simular Construção</button>
        </router-link>
      </div>
    </div>
  </template>
  
  <style scoped>
  .home {
    text-align: center;
    margin-top: 50px;
  }
  .buttons {
    margin-top: 20px;
  }
  button {
    margin: 10px;
    padding: 12px 24px;
    font-size: 16px;
  }
  </style>
  