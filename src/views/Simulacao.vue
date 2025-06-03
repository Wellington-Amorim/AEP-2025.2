<template>
  <div id="cesiumContainer">
    <v-btn
      class="back-button"
      color="primary"
      icon="mdi-arrow-left"
      size="large"
      @click="$router.push('/')"
    >
    </v-btn>
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

.back-button {
  position: fixed !important;
  top: 20px;
  left: 20px;
  z-index: 1000;
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
</style>
