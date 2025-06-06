<template>
  <div class="dados">
    <v-container fluid class="pa-0">
      <AppNavbar title="Dados do Local" />
      <!-- Container do Mapa -->
      <v-row>
        <v-col cols="12" class="pa-0">
          <div id="cesiumContainer"></div>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import * as Cesium from '@cesium/engine';
import { Viewer } from '@cesium/widgets';
import AppNavbar from '@/components/AppNavbar.vue';

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

  // Configurar a visualização inicial (Maringá)
  viewer.camera.setView({
    destination: Cesium.Rectangle.fromDegrees(
      -52.0380, // oeste
      -23.5210, // sul
      -51.8380, // leste
      -23.3210  // norte
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
</style>
