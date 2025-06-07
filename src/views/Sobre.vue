<template>
  <div class="sobre">
    <AppNavbar title="Sobre" />
    <div class="background-overlay"></div>
    <div class="content">
      <v-container>
        <v-card class="content-card">
          <v-card-title class="text-h4 mb-4">Sobre o Projeto</v-card-title>

          <v-card-text>
            <h2 class="text-h5 mb-3">O Problema</h2>
            <p class="mb-4">
              O crescimento urbano desordenado e a falta de planejamento adequado têm gerado diversos desafios
              para as cidades brasileiras. A ausência de ferramentas eficientes para simulação e visualização
              do desenvolvimento urbano dificulta a tomada de decisões por parte dos gestores e planejadores urbanos.
            </p>

            <h2 class="text-h5 mb-3">Nossa Solução</h2>
            <p class="mb-4">
              Desenvolvemos uma plataforma inovadora de simulação urbana que permite visualizar e planejar o
              crescimento das cidades em um ambiente 3D interativo. Utilizando tecnologias modernas como
              CesiumJS e Vue.js, nossa solução oferece:
            </p>
            <ul class="mb-4">
              <li>Visualização 3D detalhada do ambiente urbano</li>
              <li>Ferramentas de simulação para construção e planejamento</li>
              <li>Análise de impacto visual e ambiental</li>
              <li>Interface intuitiva e moderna para usuários técnicos e não-técnicos</li>
            </ul>

            <h2 class="text-h5 mb-3">Expectativas</h2>
            <p class="mb-4">
              Com esta ferramenta, esperamos:
            </p>
            <ul>
              <li>Facilitar o processo de planejamento urbano</li>
              <li>Melhorar a qualidade das decisões sobre desenvolvimento urbano</li>
              <li>Promover um crescimento mais sustentável e organizado das cidades</li>
              <li>Aumentar a participação da comunidade no planejamento urbano</li>
              <li>Reduzir custos e tempo no processo de planejamento</li>
            </ul>

            <h2 class="text-h5 mb-3 mt-6">Autores</h2>
            <ul class="authors-list">
              <li>Wellington Amorim de Sousa</li>
              <li>André Filipe Caetano Mulati</li>
              <li>Pedro Massaki Carnelossi</li>
            </ul>
          </v-card-text>
        </v-card>
      </v-container>
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
  position: relative;
  overflow-y: auto;
}

.background-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url('@/assets/background.jpg');
  background-size: cover;
  background-position: center;
  filter: brightness(0.3);
  z-index: 0;
}

/* Garantir que nenhum elemento tenha padding ou margin indesejado */
:deep(.v-application__wrap),
:deep(.v-main),
:deep(.v-main__wrap) {
  padding: 0 !important;
  margin: 0 !important;
}

.content {
  position: relative;
  z-index: 1;
  padding: 80px 2rem 2rem 2rem;
  min-height: 100vh;
}

.content-card {
  background: rgba(33, 33, 64, 0.8) !important;
  backdrop-filter: blur(10px);
  border-radius: 12px;
  color: white !important;
  max-width: 800px;
  margin: 0 auto;
}

.content-card :deep(.v-card-title) {
  color: white !important;
}

.content-card :deep(.v-card-text) {
  color: white !important;
}

.content-card :deep(h2) {
  color: white !important;
  margin-top: 1.5rem;
}

.content-card :deep(ul) {
  list-style-type: disc;
  padding-left: 1.5rem;
  margin-bottom: 1rem;
}

.content-card :deep(li) {
  margin-bottom: 0.5rem;
  color: white !important;
}

.content-card :deep(p) {
  line-height: 1.6;
  color: white !important;
}

.authors-list :deep(li) {
  margin-bottom: 0.75rem;
  font-size: 1.1rem;
  color: white !important;
}
</style>
