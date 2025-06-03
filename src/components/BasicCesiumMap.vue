<template>
  <div id="cesiumContainer">
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
    console.log('Iniciando o Cesium...')

    // Verificar se o Cesium foi importado corretamente
    if (!Cesium) {
      console.error('Cesium não foi importado corretamente')
      return
    }
    console.log('Cesium importado com sucesso')

    // Configurar o token do Cesium
    Cesium.Ion.defaultAccessToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI0MjNkYzYyYy01ZGFmLTRjODgtOGVhMy0xZjE1NmYwZjM3YjMiLCJpZCI6MTg2MjE5LCJpYXQiOjE3MDI5NDk2Mzd9.jF4GhfqLJXvHGDlCMAKYIxqB8pP6LwJWz_YzU_6mhsY'
    console.log('Token do Cesium configurado')

    // Aguardar um momento para garantir que o DOM está pronto
    setTimeout(() => {
      const container = document.getElementById('cesiumContainer')
      if (!container) {
        console.error('Container do Cesium não encontrado!')
        return
      }
      console.log('Container encontrado, dimensões:', {
        width: container.offsetWidth,
        height: container.offsetHeight,
        clientWidth: container.clientWidth,
        clientHeight: container.clientHeight
      })

      try {
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

        console.log('Viewer criado com sucesso:', viewer)

        // Mover a barra de pesquisa para o container personalizado
        const geocoderContainer = document.querySelector('.cesium-viewer-geocoderContainer')
        const customContainer = document.querySelector('.custom-geocoder-container')
        if (geocoderContainer && customContainer) {
          customContainer.appendChild(geocoderContainer)
          console.log('Barra de pesquisa movida com sucesso')
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
        console.log('Visualização inicial configurada')

        // Configurar limites de zoom
        viewer.scene.screenSpaceCameraController.minimumZoomDistance = 100
        viewer.scene.screenSpaceCameraController.maximumZoomDistance = 25000000

        // Habilitar sombras
        viewer.scene.globe.enableLighting = true
        viewer.shadows = true

        console.log('Configuração do Cesium concluída com sucesso')
      } catch (viewerError) {
        console.error('Erro ao criar o viewer:', viewerError)
      }
    }, 100)

  } catch (error) {
    console.error('Erro ao inicializar o Cesium:', error)
  }
})

onBeforeUnmount(() => {
  if (viewer) {
    try {
      viewer.destroy()
      console.log('Viewer destruído com sucesso')
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

<style>
#cesiumContainer {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  margin: 0;
  padding: 0;
  overflow: hidden;
  background-color: #000;
}

.cesium-viewer {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.cesium-widget,
.cesium-widget canvas {
  width: 100%;
  height: 100%;
  touch-action: none;
}

/* Container personalizado para a barra de pesquisa */
.custom-geocoder-container {
  position: absolute;
  left: 20px;
  top: 10px;
  z-index: 1000;
}

/* Manter a toolbar à direita */
.cesium-viewer-toolbar {
  position: absolute;
  right: 10px;
  top: 10px;
  background: rgba(255, 255, 255, 0.85);
  padding: 2px;
  border-radius: 4px;
}

/* Estilo para a barra de pesquisa */
.cesium-geocoder-input {
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 6px 12px;
  width: 250px;
  background-color: rgba(240, 240, 240, 0.9);
  color: #333;
  font-family: inherit;
}

.cesium-geocoder-searchButton {
  background-color: #a2a2a2;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-left: 5px;
}

/* Estilo para os botões */
.cesium-button {
  background-color: #fff;
  border: 1px solid #ccc;
  color: #333;
  fill: #333;
  border-radius: 4px;
  padding: 3px;
  margin: 0 2px;
}

.cesium-button:hover {
  background-color: #f8f8f8;
  border-color: #aaa;
}

/* Estilo para o seletor de camadas */
.cesium-baseLayerPicker-dropDown {
  margin-top: 5px;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 4px;
}

/* Melhorar visibilidade do painel de ajuda */
.cesium-navigation-help {
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 4px;
  border: 1px solid #ccc;
}
</style>
