<template>
    <div id="cesiumContainer"></div>
  </template>
  
  <script setup>
  import { onMounted } from 'vue'
  import * as Cesium from 'cesium'
  
  onMounted(() => {
    window.CESIUM_BASE_URL = '/cesium'
  
    const viewer = new Cesium.Viewer('cesiumContainer', {
      terrainProvider: Cesium.createWorldTerrain(),
      timeline: false,
      animation: false,
      homeButton: false,
      baseLayerPicker: false,
      navigationHelpButton: false,
    })
  
    // Centralizar em São Paulo (como exemplo)
    viewer.camera.setView({
      destination: Cesium.Cartesian3.fromDegrees(-46.625290, -23.533773, 15000.0)
    })
  
    // Adicionar marcador
    viewer.entities.add({
      name: 'Marco Exemplo',
      position: Cesium.Cartesian3.fromDegrees(-46.625290, -23.533773),
      point: {
        pixelSize: 10,
        color: Cesium.Color.RED,
      },
      description: 'Aqui começa a organização urbana 🚧'
    })
  })
  </script>
  
  <style scoped>
  #cesiumContainer {
    width: 100%;
    height: 100vh;
    display: block;
  }
  </style>
  