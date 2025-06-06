<template>
  <div class="home">
    <h1 class="title">HOME</h1>
    <div class="menu-container">
      <router-link to="/simulacao" class="menu-item">
        <img src="@/assets/icon_simulacao.png" alt="Ícone Simulação" class="menu-icon">
        <span>Simulação</span>
      </router-link>
      <router-link to="/dados" class="menu-item">
        <img src="@/assets/icon_dados.png" alt="Ícone Dados" class="menu-icon">
        <span>Dados do local</span>
      </router-link>
      <router-link to="/sobre" class="menu-item">
        <img src="@/assets/icon_info.png" alt="Ícone Sobre" class="menu-icon">
        <span>Sobre</span>
      </router-link>
    </div>
    <div class="social-links">
      <a href="#" target="_blank" class="social-icon">
        <v-icon icon="mdi-instagram"></v-icon>
      </a>
      <a href="#" target="_blank" class="social-icon">
        <v-icon icon="mdi-facebook"></v-icon>
      </a>
      <a href="#" target="_blank" class="social-icon">
        <v-icon icon="mdi-twitter"></v-icon>
      </a>
    </div>
  </div>
</template>

<script setup>
import DadosIBGE from '../components/DadosIBGE.vue'

defineOptions({
  name: 'HomeView'
})
</script>

<style scoped>
* {
  overflow: hidden;
}
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-image: url('@/assets/background.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  color: white;
  position: relative;
}

.title {
  font-size: 3rem;
  margin-bottom: 4rem;
  font-weight: 500;
}

.menu-container {
  display: flex;
  gap: 2rem;
  padding: 2rem;
  background: rgba(33, 33, 64, 0.8);
  border-radius: 1rem;
  backdrop-filter: blur(10px);
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: white;
  padding: 2rem;
  transition: transform 0.3s ease;
  text-align: center;
  min-width: 150px;
}

.menu-item:hover {
  transform: translateY(-10px);
}

.menu-item span {
  margin-top: 1rem;
  font-size: 1.2rem;
}

.menu-icon {
  width: 64px;
  height: 64px;
  object-fit: contain;
}

.social-links {
  position: absolute;
  bottom: 2rem;
  display: flex;
  gap: 1rem;
}

.social-icon {
  color: white;
  text-decoration: none;
  font-size: 1.5rem;
  opacity: 0.8;
  transition: opacity 0.3s ease;
}

.social-icon:hover {
  opacity: 1;
}

.ibge-container {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
}

/* Sobrescrever estilos do DadosIBGE para a página inicial */
:deep(.dados-ibge-panel) {
  position: relative;
  right: auto;
  top: auto;
  width: 100%;
  margin-top: 20px;
}
</style>
