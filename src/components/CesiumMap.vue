<template>
  <div id="cesiumContainer">
    <!-- Container personalizado para a barra de pesquisa -->
    <div class="custom-geocoder-container">
      <div class="cesium-viewer-geocoderContainer"></div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount } from 'vue'
import * as Cesium from 'cesium'
import "cesium/Build/Cesium/Widgets/widgets.css"

let viewer = null

onMounted(() => {
  try {
    Cesium.Ion.defaultAccessToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI0MjNkYzYyYy01ZGFmLTRjODgtOGVhMy0xZjE1NmYwZjM3YjMiLCJpZCI6MTg2MjE5LCJpYXQiOjE3MDI5NDk2Mzd9.jF4GhfqLJXvHGDlCMAKYIxqB8pP6LwJWz_YzU_6mhsY'

    viewer = new Cesium.Viewer('cesiumContainer', {
      terrainProvider: Cesium.createWorldTerrain(),
      baseLayerPicker: true,
      sceneModePicker: true,
      navigationHelpButton: true,
      animation: false,
      timeline: false,
      fullscreenButton: true,
      navigationInstructionsInitiallyVisible: false,
      homeButton: true,
      geocoder: true,
      vrButton: false,
      sceneMode: Cesium.SceneMode.SCENE3D
    })

    // Mover a barra de pesquisa para o container personalizado
    const geocoderContainer = document.querySelector('.cesium-viewer-geocoderContainer')
    const customContainer = document.querySelector('.custom-geocoder-container')
    if (geocoderContainer && customContainer) {
      customContainer.appendChild(geocoderContainer)
    }

    // Configurar a visualização inicial
    viewer.camera.setView({
      destination: Cesium.Cartesian3.fromDegrees(-46.625290, -23.533773, 5000.0),
      orientation: {
        heading: Cesium.Math.toRadians(0.0),
        pitch: Cesium.Math.toRadians(-45.0),
        roll: 0.0
      }
    })

    // Configurar limites de zoom
    viewer.scene.screenSpaceCameraController.minimumZoomDistance = 100
    viewer.scene.screenSpaceCameraController.maximumZoomDistance = 25000000

    // Habilitar sombras
    viewer.scene.globe.enableLighting = true
    viewer.shadows = true

  } catch (error) {
    console.error('Erro ao inicializar o Cesium:', error)
  }
})

onBeforeUnmount(() => {
  if (viewer) {
    try {
      viewer.destroy()
    } catch (error) {
      console.error('Erro ao destruir o viewer:', error)
    }
    viewer = null
  }
})

defineExpose({
  viewer: () => viewer
})
</script>

<style scoped>
#cesiumContainer {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
}

/* Container personalizado para a barra de pesquisa */
.custom-geocoder-container {
  position: absolute;
  left: 20px;
  top: 10px;
  z-index: 1000;
}

/* Estilos para o container do Cesium */
:deep(.cesium-viewer) {
  width: 100%;
  height: 100%;
}

/* Manter a toolbar à direita */
:deep(.cesium-viewer-toolbar) {
  position: absolute;
  right: 10px;
  top: 10px;
  background: rgba(255, 255, 255, 0.85);
  padding: 2px;
  border-radius: 4px;
}

/* Estilo para a barra de pesquisa */
:deep(.cesium-geocoder-input) {
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 6px 12px;
  width: 250px;
  background-color: rgba(240, 240, 240, 0.9);
  color: #333;
  font-family: inherit;
}

:deep(.cesium-geocoder-searchButton) {
  background-color: #a2a2a2;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-left: 5px;
}

/* Estilo para os botões */
:deep(.cesium-button) {
  background-color: #fff;
  border: 1px solid #ccc;
  color: #333;
  fill: #333;
  border-radius: 4px;
  padding: 3px;
  margin: 0 2px;
}

:deep(.cesium-button:hover) {
  background-color: #f8f8f8;
  border-color: #aaa;
}

/* Estilo para o seletor de camadas */
:deep(.cesium-baseLayerPicker-dropDown) {
  margin-top: 5px;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 4px;
}

/* Melhorar visibilidade do painel de ajuda */
:deep(.cesium-navigation-help) {
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 4px;
  border: 1px solid #ccc;
}
</style>
