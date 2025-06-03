<template>
  <div class="terrain-analysis-panel" :class="{ 'is-expanded': isExpanded }">
    <div class="panel-header" @click="togglePanel">
      <h3>Análise de Terreno</h3>
      <span class="toggle-icon">{{ isExpanded ? '▼' : '▶' }}</span>
    </div>

    <div v-if="isExpanded" class="panel-content">
      <div v-if="!analyzing && !analysisResult" class="instructions">
        <p>Desenhe uma área no mapa para analisar o terreno.</p>
        <button @click="startAnalysis" class="start-btn">
          Iniciar Análise
        </button>
      </div>

      <div v-if="analyzing" class="loading">
        <p>Analisando terreno...</p>
      </div>

      <div v-if="analysisResult" class="analysis-results">
        <div class="result-section">
          <h4>Características do Terreno</h4>
          <div class="data-grid">
            <div class="data-item">
              <span class="label">Inclinação:</span>
              <span class="value">{{ formatNumber(analysisResult.slope) }}%</span>
            </div>
            <div class="data-item">
              <span class="label">Altitude:</span>
              <span class="value">{{ formatNumber(analysisResult.height) }}m</span>
            </div>
            <div class="data-item">
              <span class="label">Nível de Risco:</span>
              <span class="value" :class="'risk-' + analysisResult.riskLevel.toLowerCase()">
                {{ analysisResult.riskLevel }}
              </span>
            </div>
          </div>
        </div>

        <div class="result-section">
          <h4>Recomendações</h4>
          <div class="recommendations">
            <p :class="{ 'text-danger': !analysisResult.buildingRecommendations.suitable }">
              {{ analysisResult.buildingRecommendations.message }}
            </p>
            <ul v-if="analysisResult.buildingRecommendations.recommendations">
              <li v-for="(rec, index) in analysisResult.buildingRecommendations.recommendations"
                  :key="index">
                {{ rec }}
              </li>
            </ul>
          </div>
        </div>

        <button @click="startAnalysis" class="analyze-new-btn">
          Analisar Nova Área
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import * as Cesium from 'cesium'
import { TerrainAnalysis } from '../services/terrainAnalysis'

const props = defineProps({
  viewer: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['startAnalysis', 'analysisComplete'])

const isExpanded = ref(true)
const analyzing = ref(false)
const analysisResult = ref(null)
let terrainAnalyzer = null
let drawHandler = null

function togglePanel() {
  isExpanded.value = !isExpanded.value
}

function formatNumber(num) {
  return new Intl.NumberFormat('pt-BR', {
    minimumFractionDigits: 1,
    maximumFractionDigits: 1
  }).format(num)
}

function startAnalysis() {
  analyzing.value = false
  analysisResult.value = null
  emit('startAnalysis')

  if (!terrainAnalyzer) {
    terrainAnalyzer = new TerrainAnalysis(props.viewer)
  }

  // Configurar handler para desenho de polígono
  const positions = []
  drawHandler = new Cesium.ScreenSpaceEventHandler(props.viewer.canvas)

  drawHandler.setInputAction((event) => {
    const earthPosition = props.viewer.scene.pickPosition(event.position)
    if (Cesium.defined(earthPosition)) {
      positions.push(earthPosition)

      // Adicionar ponto visual
      props.viewer.entities.add({
        position: earthPosition,
        point: {
          pixelSize: 5,
          color: Cesium.Color.WHITE,
          outlineColor: Cesium.Color.BLACK,
          outlineWidth: 1
        }
      })

      // Se tiver 3 ou mais pontos, mostrar polígono
      if (positions.length >= 3) {
        props.viewer.entities.add({
          polygon: {
            hierarchy: positions,
            material: Cesium.Color.WHITE.withAlpha(0.3),
            outline: true,
            outlineColor: Cesium.Color.BLACK
          }
        })
      }
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK)

  // Finalizar desenho com clique direito
  drawHandler.setInputAction(async () => {
    if (positions.length >= 3) {
      drawHandler.destroy()
      analyzing.value = true

      try {
        const result = await terrainAnalyzer.analyzeArea(positions)
        analysisResult.value = result
        emit('analysisComplete', result)
      } catch (error) {
        console.error('Erro na análise:', error)
      } finally {
        analyzing.value = false
      }
    }
  }, Cesium.ScreenSpaceEventType.RIGHT_CLICK)
}
</script>

<style scoped>
.terrain-analysis-panel {
  position: absolute;
  right: 20px;
  top: 340px;
  width: 300px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
  z-index: 1000;
}

.panel-header {
  padding: 12px 15px;
  background: #2c3e50;
  color: white;
  border-radius: 8px 8px 0 0;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
}

.panel-content {
  padding: 15px;
}

.instructions {
  text-align: center;
  padding: 20px 0;
}

.loading {
  text-align: center;
  padding: 20px 0;
  color: #666;
}

.data-grid {
  display: grid;
  gap: 8px;
  margin: 10px 0;
}

.data-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.label {
  color: #666;
}

.value {
  font-weight: 500;
}

.risk-alto {
  color: #e74c3c;
}

.risk-medio {
  color: #f39c12;
}

.risk-baixo {
  color: #27ae60;
}

.result-section {
  margin-bottom: 20px;
}

.result-section h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.recommendations {
  background: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
}

.recommendations ul {
  margin: 10px 0;
  padding-left: 20px;
}

.recommendations li {
  margin-bottom: 5px;
  color: #666;
}

.text-danger {
  color: #e74c3c;
}

.start-btn, .analyze-new-btn {
  width: 100%;
  padding: 8px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
}

.start-btn:hover, .analyze-new-btn:hover {
  background: #2980b9;
}
</style>
