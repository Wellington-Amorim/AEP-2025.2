<template>
  <div id="cesiumContainer">
    <!-- Navbar -->
    <v-row class="header-row">
      <v-col cols="12" class="d-flex align-center">
        <v-btn
          color="primary"
          icon="mdi-arrow-left"
          size="large"
          @click="$router.push('/')"
          class="mr-4"
        >
        </v-btn>
        <h1 class="text-h4">Simulação</h1>
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import * as Cesium from '@cesium/engine';
import { Viewer } from '@cesium/widgets';

onMounted(() => {
  const viewer = new Viewer('cesiumContainer', {
    animation: false,
    baseLayerPicker: false,
    fullscreenButton: false,
    geocoder: false,
    homeButton: false,
    infoBox: false,
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

  // Configurar a visualização inicial (São Paulo capital)
  viewer.camera.setView({
    destination: Cesium.Rectangle.fromDegrees(
      -46.8754, // oeste
      -24.0082, // sul
      -46.3654, // leste
      -23.3566  // norte
    )
  });

  // Configurações adicionais
  viewer.scene.globe.baseColor = Cesium.Color.WHITE;
  viewer.scene.backgroundColor = new Cesium.Color(1, 1, 1, 1);
  viewer.scene.skyBox.show = false;
  viewer.scene.skyAtmosphere.show = false;
  viewer.scene.sun.show = false;
  viewer.scene.moon.show = false;

  // Forçar modo 2D
  viewer.scene.morphTo2D(0);

  // Remover créditos
  if (viewer._cesiumWidget._creditContainer) {
    viewer._cesiumWidget._creditContainer.style.display = "none";
  }
});
</script>

<style>
html, body, #app {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
  background: white;
}

#cesiumContainer {
  width: 100%;
  height: 100vh;
  position: absolute;
  top: 0;
  left: 0;
  background: white;
}

.header-row {
  position: fixed !important;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.8) !important;
  backdrop-filter: blur(10px);
  padding: 0.5rem 1rem !important;
  margin: 0 !important;
  height: 64px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.text-h4 {
  margin: 0;
  font-weight: 500;
  font-size: 1.5rem !important;
  color: rgba(0, 0, 0, 0.87);
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

/* Ajustes para botão de voltar */
.v-btn {
  opacity: 0.9;
}

.v-btn:hover {
  opacity: 1;
}
</style>
