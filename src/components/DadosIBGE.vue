<template>
  <div class="dados-ibge-panel" :class="{ 'is-expanded': isExpanded }">
    <div class="panel-header" @click="togglePanel">
      <h3>Dados IBGE</h3>
      <span class="toggle-icon">{{ isExpanded ? '▼' : '▶' }}</span>
    </div>

    <div v-if="isExpanded" class="panel-content">
      <div class="search-section">
        <input
          v-model="searchTerm"
          @input="handleSearch"
          placeholder="Buscar município..."
          class="search-input"
        />
        <div v-if="searchResults.length > 0" class="search-results">
          <div
            v-for="result in searchResults"
            :key="result.id"
            @click="selectMunicipio(result)"
            class="search-result-item"
          >
            {{ result.nome }} - {{ result.microrregiao.mesorregiao.UF.sigla }}
          </div>
        </div>
      </div>

      <div v-if="dadosMunicipio" class="municipality-data">
        <h4>{{ dadosMunicipio.nome }}</h4>

        <div class="data-section">
          <h5>Dados Demográficos</h5>
          <div v-if="dadosDemograficos" class="data-grid">
            <div class="data-item">
              <span class="label">População:</span>
              <span class="value">{{ formatNumber(dadosDemograficos.populacao) }}</span>
            </div>
            <div class="data-item">
              <span class="label">Densidade:</span>
              <span class="value">{{ formatNumber(dadosDemograficos.densidade) }} hab/km²</span>
            </div>
          </div>
        </div>

        <div class="data-section">
          <h5>Indicadores Urbanísticos</h5>
          <div v-if="indicadoresUrbanisticos" class="data-grid">
            <div class="data-item">
              <span class="label">Domicílios:</span>
              <span class="value">{{ formatNumber(indicadoresUrbanisticos.domicilios) }}</span>
            </div>
            <div class="data-item">
              <span class="label">Cobertura de Água:</span>
              <span class="value">{{ indicadoresUrbanisticos.coberturaAgua }}%</span>
            </div>
            <div class="data-item">
              <span class="label">Cobertura de Esgoto:</span>
              <span class="value">{{ indicadoresUrbanisticos.coberturaEsgoto }}%</span>
            </div>
          </div>
        </div>

        <button
          @click="focusOnMunicipio"
          class="focus-button"
        >
          Focar no Município
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import * as Cesium from 'cesium'
import { getMunicipioInfo, getDadosDemograficos, pesquisarMunicipio, getIndicadoresUrbanisticos } from '../services/ibgeService'

const props = defineProps({
  viewer: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['municipioSelected'])

const isExpanded = ref(false)
const searchTerm = ref('')
const searchResults = ref([])
const dadosMunicipio = ref(null)
const dadosDemograficos = ref(null)
const indicadoresUrbanisticos = ref(null)

let searchTimeout = null

function togglePanel() {
  isExpanded.value = !isExpanded.value
}

async function handleSearch() {
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }

  if (searchTerm.value.length < 3) {
    searchResults.value = []
    return
  }

  searchTimeout = setTimeout(async () => {
    try {
      searchResults.value = await pesquisarMunicipio(searchTerm.value)
    } catch (error) {
      console.error('Erro na pesquisa:', error)
      searchResults.value = []
    }
  }, 300)
}

async function selectMunicipio(municipio) {
  try {
    dadosMunicipio.value = municipio
    searchResults.value = []
    searchTerm.value = municipio.nome

    // Buscar dados complementares
    const [demograficos, urbanisticos] = await Promise.all([
      getDadosDemograficos(municipio.id),
      getIndicadoresUrbanisticos(municipio.id)
    ])

    dadosDemograficos.value = demograficos
    indicadoresUrbanisticos.value = urbanisticos

    emit('municipioSelected', {
      municipio,
      demograficos,
      urbanisticos
    })
  } catch (error) {
    console.error('Erro ao carregar dados do município:', error)
  }
}

function focusOnMunicipio() {
  if (!dadosMunicipio.value || !props.viewer) return

  const { latitude, longitude } = dadosMunicipio.value
  props.viewer.camera.flyTo({
    destination: Cesium.Cartesian3.fromDegrees(longitude, latitude, 25000.0),
    orientation: {
      heading: Cesium.Math.toRadians(0.0),
      pitch: Cesium.Math.toRadians(-45.0),
      roll: 0.0
    }
  })
}

function formatNumber(num) {
  return new Intl.NumberFormat('pt-BR').format(num)
}
</script>

<style scoped>
.dados-ibge-panel {
  position: absolute;
  right: 20px;
  top: 60px;
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

.search-section {
  position: relative;
  margin-bottom: 15px;
}

.search-input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-results {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #ddd;
  border-radius: 0 0 4px 4px;
  max-height: 200px;
  overflow-y: auto;
  z-index: 1;
}

.search-result-item {
  padding: 8px;
  cursor: pointer;
  border-bottom: 1px solid #eee;
}

.search-result-item:hover {
  background: #f5f5f5;
}

.data-section {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.data-section h5 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.data-grid {
  display: grid;
  gap: 8px;
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

.focus-button {
  margin-top: 15px;
  width: 100%;
  padding: 8px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.focus-button:hover {
  background: #2980b9;
}
</style>
