<template>
    <!-- só o container do CesiumMap -->
    <CesiumMap />
  </template>
  
  <script setup>
  import CesiumMap from '../components/CesiumMap.vue'
  </script>
  
  <style scoped>
  /* Garante que o mapa ocupe 100% da área abaixo da navbar */
  #cesiumContainer {
    width: 100%;
    height: calc(100vh - 40px); /* ajuste se sua nav for maior/menor */
    margin: 0;
    padding: 0;
  }
  </style>
