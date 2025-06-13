<template>
  <div class="dados">
    <v-container fluid class="pa-0">
      <AppNavbar title="Dados do Local">
        <!-- Botão para alternar painéis, agora dentro da navbar -->
        <v-btn
          class="data-toggle-btn"
          color="primary"
          @click="togglePanelsVisibility"
        >
          <v-icon>{{ showPanels ? 'mdi-eye-off' : 'mdi-eye' }}</v-icon>
          <span class="ml-2">Dados</span>
        </v-btn>
      </AppNavbar>

      <!-- Container do Mapa -->
      <v-row>
        <v-col cols="12" class="pa-0">
          <div id="cesiumContainer"></div>

          <div v-if="showPanels" class="panels-wrapper">
            <DadosIBGE
              :viewer="viewer"
              v-model="isIbgePanelExpanded"
              @municipioSelected="handleMunicipioSelected"
            />
            <TerrainAnalysisPanel
              :viewer="viewer"
              v-model="isTerrainPanelExpanded"
              @startAnalysis="handleStartAnalysis"
              @analysisComplete="handleAnalysisComplete"
            />
          </div>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue';
import * as Cesium from '@cesium/engine';
import { Viewer } from '@cesium/widgets';
import AppNavbar from '@/components/AppNavbar.vue';
import DadosIBGE from '@/components/DadosIBGE.vue';
import TerrainAnalysisPanel from '@/components/TerrainAnalysisPanel.vue';

const viewer = ref(null);
const showPanels = ref(true); // Estado para controlar a visibilidade geral dos painéis
const activePanel = ref(null); // 'ibge', 'terrain', ou null

// Computed properties to bind to v-model of child components
const isIbgePanelExpanded = computed({
  get: () => activePanel.value === 'ibge',
  set: (value) => {
    if (value) {
      activePanel.value = 'ibge'; // Open IBGE panel, close others
    } else {
      // If setting to false, only close if IBGE is the active one
      if (activePanel.value === 'ibge') {
        activePanel.value = null;
      }
    }
  }
});

const isTerrainPanelExpanded = computed({
  get: () => activePanel.value === 'terrain',
  set: (value) => {
    if (value) {
      activePanel.value = 'terrain'; // Open Terrain panel, close others
    } else {
      // If setting to false, only close if Terrain is the active one
      if (activePanel.value === 'terrain') {
        activePanel.value = null;
      }
    }
  }
});

const togglePanelsVisibility = () => {
  showPanels.value = !showPanels.value;
  if (!showPanels.value) {
    activePanel.value = null; // Close all panels if main toggle is off
  }
};

onMounted(() => {
  viewer.value = new Viewer('cesiumContainer', {
    animation: false,
    baseLayerPicker: false,
    fullscreenButton: true,
    geocoder: false,
    homeButton: false,
    infoBox: true,
    sceneModePicker: false,
    selectionIndicator: false,
    timeline: false,
    navigationHelpButton: false,
    sceneMode: Cesium.SceneMode.SCENE2D,
    contextOptions: {
      webgl: {
        alpha: true
      }
    }
  });

  // Configurar a visualização inicial (Maringá)
  viewer.value.camera.setView({
    destination: Cesium.Rectangle.fromDegrees(
      -52.0380, // oeste
      -23.5210, // sul
      -51.8380, // leste
      -23.3210  // norte
    )
  });

  // Configurações adicionais
  viewer.value.scene.globe.baseColor = Cesium.Color.WHITE;
  viewer.value.scene.backgroundColor = new Cesium.Color(1, 1, 1, 1);
  viewer.value.scene.skyBox.show = false;
  viewer.value.scene.skyAtmosphere.show = false;
  viewer.value.scene.sun.show = false;
  viewer.value.scene.moon.show = false;

  // Forçar modo 2D
  viewer.value.scene.morphTo2D(0);

  // Remover créditos
  if (viewer.value._cesiumWidget._creditContainer) {
    viewer.value._cesiumWidget._creditContainer.style.display = "none";
  }
});

const handleMunicipioSelected = (data) => {
  console.log('Município selecionado:', data);
};

const handleStartAnalysis = () => {
  console.log('Iniciando análise de terreno');
};

const handleAnalysisComplete = (result) => {
  console.log('Análise de terreno concluída:', result);
};
</script>

<style>
.dados {
  width: 100%;
  height: 100vh;
  background: transparent !important;
  padding: 0 !important;
  margin: 0 !important;
}

#cesiumContainer {
  position: absolute !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  padding: 0 !important;
  margin: 0 !important;
  height: 100vh !important;
  width: 100% !important;
  background: transparent !important;
}

.cesium-viewer-toolbar,
.cesium-viewer-bottom {
  display: none !important;
}

.cesium-widget-scene {
  background: white !important;
}

.cesium-widget canvas {
  background: white !important;
}

.cesium-viewer {
  background: white !important;
}

/* Ajustes para modo 2D */
.cesium-viewer-transitioner {
  display: none !important;
}

/* Ajustes para o layout */
.v-container {
  height: 100%;
  max-width: none !important;
}

/* Garantir que nenhum elemento tenha padding ou margin indesejado */
:deep(.v-application__wrap),
:deep(.v-main),
:deep(.v-main__wrap) {
  padding: 0 !important;
  margin: 0 !important;
}

.toggle-panels-btn {
  position: absolute;
  top: 60px; /* Ajuste conforme necessário para não sobrepor a navbar */
  right: 20px;
  z-index: 1001; /* Garante que o botão fique acima dos painéis */
  background-color: #2c3e50; /* Cor do cabeçalho dos painéis */
  color: white;
}

.panels-wrapper {
  position: absolute;
  top: 100px; /* Ajuste para posicionar abaixo do botão de toggle */
  right: 20px;
  display: flex;
  flex-direction: column;
  align-items: flex-end; /* Alinha os painéis à direita */
  z-index: 999; /* Abaixo do botão de toggle */
}
</style>
