<template>
  <div id="cesiumContainer">
    <AppNavbar title="Simulação">
      <v-text-field
        v-model="searchQuery"
        label="Buscar localização"
        prepend-inner-icon="mdi-magnify"
        clearable
        hide-details
        density="compact"
        class="search-field"
        @keyup.enter="searchLocation"
      ></v-text-field>
    </AppNavbar>

    <!-- Botão de toggle -->
    <v-btn
      class="tools-toggle"
      color="primary"
      @click="showTools = !showTools"
      elevation="2"
    >
      <v-icon>{{ showTools ? 'mdi-close' : 'mdi-tools' }}</v-icon>
      <span class="ml-2">{{ showTools ? 'Fechar' : 'Ferramentas' }}</span>
    </v-btn>

    <!-- Controles de construção -->
    <v-slide-x-transition>
      <div class="building-controls" v-show="showTools">
        <v-card class="pa-4 controls-card">
          <v-card-title>Ferramentas de Construção</v-card-title>

          <v-card-text>
            <!-- Modo de construção -->
            <v-select
              v-model="buildingMode"
              :items="buildingModes"
              label="Modo de construção"
              class="mb-4"
            ></v-select>

            <!-- Estilo do edifício -->
            <v-select
              v-model="buildingStyle"
              :items="buildingStyles"
              label="Estilo do edifício"
              class="mb-4"
            ></v-select>

            <!-- Altura do edifício -->
            <v-text-field
              v-model="buildingHeight"
              label="Altura (metros)"
              type="number"
              class="mb-4"
            ></v-text-field>

            <!-- Botões de ação -->
            <v-btn-group class="d-flex">
              <v-btn
                color="primary"
                @click="startDrawing"
                :disabled="isDrawing"
                class="flex-grow-1"
              >
                <v-icon left>mdi-pencil</v-icon>
                Iniciar
              </v-btn>

              <v-btn
                color="error"
                @click="cancelDrawing"
                :disabled="!isDrawing"
                class="flex-grow-1"
              >
                <v-icon left>mdi-close</v-icon>
                Cancelar
              </v-btn>
            </v-btn-group>
          </v-card-text>
        </v-card>
      </div>
    </v-slide-x-transition>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import * as Cesium from '@cesium/engine';
import { Viewer } from '@cesium/widgets';
import AppNavbar from '@/components/AppNavbar.vue';

// Estados
const viewer = ref(null);
const isDrawing = ref(false);
const searchQuery = ref('');
const buildingMode = ref('polygon');
const buildingStyle = ref('modern');
const buildingHeight = ref(30);
const points = ref([]);
const activeEntity = ref(null);
const handler = ref(null);
const showTools = ref(false);

// Opções de construção
const buildingModes = [
  { title: 'Polígono', value: 'polygon' },
  { title: 'Retângulo', value: 'rectangle' },
  { title: 'Círculo', value: 'circle' }
];

const buildingStyles = [
  { title: 'Moderno', value: 'modern' },
  { title: 'Clássico', value: 'classic' },
  { title: 'Industrial', value: 'industrial' },
  { title: 'Residencial', value: 'residential' },
  { title: 'Comercial', value: 'commercial' }
];

// Configurações de estilo dos edifícios
const buildingStyleConfigs = {
  modern: {
    color: Cesium.Color.fromCssColorString('#4FC3F7').withAlpha(0.8),
    material: new Cesium.Color(0.5, 0.5, 0.5, 0.8)
  },
  classic: {
    color: Cesium.Color.fromCssColorString('#FFB74D').withAlpha(0.9),
    material: new Cesium.Color(0.7, 0.7, 0.7, 0.9)
  },
  industrial: {
    color: Cesium.Color.fromCssColorString('#90A4AE').withAlpha(0.95),
    material: new Cesium.Color(0.6, 0.6, 0.6, 0.95)
  },
  residential: {
    color: Cesium.Color.fromCssColorString('#81C784').withAlpha(0.85),
    material: new Cesium.Color(0.4, 0.4, 0.4, 0.85)
  },
  commercial: {
    color: Cesium.Color.fromCssColorString('#64B5F6').withAlpha(0.9),
    material: new Cesium.Color(0.5, 0.5, 0.5, 0.9)
  }
};

// Inicialização
onMounted(async () => {
  // Configurar o viewer
  viewer.value = new Viewer('cesiumContainer', {
    terrain: Cesium.Terrain.fromWorldTerrain(),
    baseLayerPicker: false,
    animation: false,
    fullscreenButton: false,
    geocoder: false,
    homeButton: false,
    navigationHelpButton: false,
    sceneModePicker: false,
    timeline: false
  });

  // Adicionar Cesium OSM Buildings
  const buildingsTileset = await Cesium.createOsmBuildingsAsync();
  viewer.value.scene.primitives.add(buildingsTileset);

  // Configurar a visualização inicial
  viewer.value.camera.setView({
    destination: Cesium.Cartesian3.fromDegrees(-51.9380, -23.4210, 1000), // Maringá
    orientation: {
      heading: Cesium.Math.toRadians(0),
      pitch: Cesium.Math.toRadians(-35),
      roll: 0
    }
  });
});

// Limpeza
onUnmounted(() => {
  if (viewer.value) {
    viewer.value.destroy();
  }
});

// Funções de construção
const startDrawing = () => {
  isDrawing.value = true;
  points.value = [];

  // Inicializar o handler de eventos se ainda não existir
  if (!handler.value) {
    handler.value = new Cesium.ScreenSpaceEventHandler(viewer.value.scene.canvas);
  }

  // Configurar o modo de desenho baseado no buildingMode
  switch (buildingMode.value) {
    case 'polygon':
      startPolygonDrawing();
      break;
    case 'rectangle':
      startRectangleDrawing();
      break;
    case 'circle':
      startCircleDrawing();
      break;
  }
};

const cancelDrawing = () => {
  isDrawing.value = false;
  points.value = [];
  if (activeEntity.value) {
    viewer.value.entities.remove(activeEntity.value);
    activeEntity.value = null;
  }
  if (handler.value) {
    handler.value.destroy();
    handler.value = null;
  }
};

const startPolygonDrawing = () => {
  const style = buildingStyleConfigs[buildingStyle.value];

  // Configurar o handler para capturar cliques
  handler.value.setInputAction((click) => {
    const cartesian = viewer.value.scene.pickPosition(click.position);
    if (cartesian) {
      points.value.push(cartesian);

      // Se é o primeiro ponto, criar a entidade
      if (points.value.length === 1) {
        activeEntity.value = viewer.value.entities.add({
          polygon: {
            hierarchy: new Cesium.CallbackProperty(() => {
              return new Cesium.PolygonHierarchy(points.value);
            }, false),
            material: style.material,
            extrudedHeight: new Cesium.CallbackProperty(() => {
              return Number(buildingHeight.value);
            }, false)
          }
        });
      }

      // Se temos pontos suficientes, finalizar o polígono
      if (points.value.length >= 3) {
        finishDrawing();
      }
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK);

  // Preview em tempo real
  handler.value.setInputAction((movement) => {
    if (points.value.length > 0) {
      const cartesian = viewer.value.scene.pickPosition(movement.endPosition);
      if (cartesian) {
        const tempPoints = [...points.value];
        tempPoints.push(cartesian);
        activeEntity.value.polygon.hierarchy = new Cesium.PolygonHierarchy(tempPoints);
      }
    }
  }, Cesium.ScreenSpaceEventType.MOUSE_MOVE);
};

const startRectangleDrawing = () => {
  const style = buildingStyleConfigs[buildingStyle.value];
  let firstPoint = null;

  handler.value.setInputAction((click) => {
    const cartesian = viewer.value.scene.pickPosition(click.position);
    if (cartesian) {
      if (!firstPoint) {
        firstPoint = cartesian;
        points.value = [firstPoint];

        activeEntity.value = viewer.value.entities.add({
          rectangle: {
            coordinates: new Cesium.CallbackProperty(() => {
              if (points.value.length < 2) return null;
              return Cesium.Rectangle.fromCartesianArray(points.value);
            }, false),
            material: style.material,
            extrudedHeight: new Cesium.CallbackProperty(() => {
              return Number(buildingHeight.value);
            }, false)
          }
        });
      } else {
        points.value.push(cartesian);
        finishDrawing();
      }
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK);

  handler.value.setInputAction((movement) => {
    if (firstPoint) {
      const cartesian = viewer.value.scene.pickPosition(movement.endPosition);
      if (cartesian) {
        const tempPoints = [firstPoint, cartesian];
        activeEntity.value.rectangle.coordinates = Cesium.Rectangle.fromCartesianArray(tempPoints);
      }
    }
  }, Cesium.ScreenSpaceEventType.MOUSE_MOVE);
};

const startCircleDrawing = () => {
  const style = buildingStyleConfigs[buildingStyle.value];
  let center = null;

  handler.value.setInputAction((click) => {
    const cartesian = viewer.value.scene.pickPosition(click.position);
    if (cartesian) {
      if (!center) {
        center = cartesian;
        points.value = [center];

        activeEntity.value = viewer.value.entities.add({
          position: center,
          ellipse: {
            material: style.material,
            extrudedHeight: new Cesium.CallbackProperty(() => {
              return Number(buildingHeight.value);
            }, false)
          }
        });
      } else {
        points.value.push(cartesian);
        finishDrawing();
      }
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK);

  handler.value.setInputAction((movement) => {
    if (center) {
      const cartesian = viewer.value.scene.pickPosition(movement.endPosition);
      if (cartesian) {
        const radius = Cesium.Cartesian3.distance(center, cartesian);
        activeEntity.value.ellipse.semiMajorAxis = radius;
        activeEntity.value.ellipse.semiMinorAxis = radius;
      }
    }
  }, Cesium.ScreenSpaceEventType.MOUSE_MOVE);
};

const finishDrawing = () => {
  isDrawing.value = false;
  if (handler.value) {
    handler.value.destroy();
    handler.value = null;
  }
  // Manter a entidade ativa no mapa
  activeEntity.value = null;
  points.value = [];
};

// Função de busca de localização
const searchLocation = async () => {
  if (!searchQuery.value) return;

  try {
    // Usar o Geocoder do Cesium para buscar a localização
    const results = await Cesium.IonGeocoderService.geocode(searchQuery.value);
    if (results && results.length > 0) {
      const result = results[0];
      viewer.value.camera.flyTo({
        destination: Cesium.Cartesian3.fromDegrees(
          result.longitude,
          result.latitude,
          1000
        ),
        orientation: {
          heading: Cesium.Math.toRadians(0),
          pitch: Cesium.Math.toRadians(-35),
          roll: 0
        }
      });
    }
  } catch (error) {
    console.error('Erro ao buscar localização:', error);
  }
};
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

.navbar {
  background: transparent !important;
  height: 40px !important;
  margin: 0 !important;
  padding: 0 !important;
  box-shadow: none !important;
  position: absolute;
  top: 8px;
  left: 8px;
  right: 8px;
  z-index: 100;
}

:deep(.v-toolbar__content),
:deep(.v-app-bar),
:deep(.v-toolbar),
:deep(.v-toolbar__wrapper) {
  background: transparent !important;
  padding: 0 !important;
  margin: 0 !important;
  height: 40px !important;
  min-height: 40px !important;
  box-shadow: none !important;
  background-color: transparent !important;
  background-image: none !important;
}

.navbar-content {
  background: rgba(33, 33, 64, 0.8);
  backdrop-filter: blur(10px);
  height: 40px;
  padding: 0 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  width: 100%;
  gap: 16px;
}

.text-h4 {
  font-size: 1.25rem !important;
  font-weight: 500;
  margin: 0;
  line-height: 40px;
  color: white;
  flex-shrink: 0;
}

.home-btn {
  height: 32px;
  border-radius: 16px;
  padding: 0 16px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-weight: 500;
  text-transform: none;
  letter-spacing: normal;
  backdrop-filter: blur(5px);
  flex-shrink: 0;
}

.home-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.search-field {
  max-width: 300px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  color: white;
  flex-shrink: 1;
}

:deep(.v-field__input) {
  color: white !important;
}

:deep(.v-field__outline) {
  --v-field-border-opacity: 0.2;
}

:deep(.v-field__clearable) {
  color: rgba(255, 255, 255, 0.7);
}

:deep(.v-field__input::placeholder) {
  color: rgba(255, 255, 255, 0.7);
}

.tools-toggle {
  position: fixed !important;
  top: 80px;
  right: 8px !important;
  left: auto !important;
  height: 32px !important;
  background: rgba(33, 33, 64, 0.7) !important;
  backdrop-filter: blur(10px);
  border-radius: 6px !important;
  color: white !important;
  padding: 0 12px !important;
  z-index: 1000 !important;
  font-size: 0.875rem !important;
}

.tools-toggle :deep(.v-btn__content) {
  color: white !important;
  gap: 4px;
}

.tools-toggle :deep(.v-icon) {
  font-size: 18px !important;
}

.building-controls {
  position: fixed;
  top: 122px;
  right: 8px !important;
  left: auto !important;
  z-index: 1000;
  width: 300px;
}

.controls-card {
  background: rgba(33, 33, 64, 0.8) !important;
  backdrop-filter: blur(10px);
  border-radius: 8px !important;
  color: white !important;
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

/* Ajustes para botões */
.v-btn {
  opacity: 0.9;
}

.v-btn:hover {
  opacity: 1;
}

/* Garantir que o app não tenha background */
:deep(.v-application),
:deep(.v-layout) {
  background: transparent !important;
}

/* Garantir que nenhum elemento tenha padding ou margin indesejado */
:deep(.v-application__wrap),
:deep(.v-main),
:deep(.v-main__wrap) {
  padding: 0 !important;
  margin: 0 !important;
}

/* Remover qualquer padding do container principal */
.home {
  padding: 0 !important;
  margin: 0 !important;
}

:deep(.v-card-title) {
  color: white !important;
  font-size: 1.1rem !important;
}

:deep(.v-label),
:deep(.v-select__selection),
:deep(.v-field__input),
:deep(.v-select__selection-text),
:deep(.v-text-field input) {
  color: white !important;
}

:deep(.v-field) {
  color: white !important;
  --v-field-border-opacity: 0.3;
}

:deep(.v-field__outline) {
  --v-field-border-opacity: 0.3;
}

:deep(.v-select__menu) {
  background: rgba(33, 33, 64, 0.95) !important;
  backdrop-filter: blur(10px);
}

:deep(.v-list) {
  background: transparent !important;
  color: white !important;
}

:deep(.v-list-item) {
  color: white !important;
}

:deep(.v-list-item:hover) {
  background: rgba(255, 255, 255, 0.1) !important;
}

:deep(.v-btn-group .v-btn) {
  color: white !important;
}

/* Garantir que o botão e controles fiquem sobre o mapa */
:deep(.cesium-viewer),
:deep(.cesium-widget) {
  z-index: 0;
}
</style>
