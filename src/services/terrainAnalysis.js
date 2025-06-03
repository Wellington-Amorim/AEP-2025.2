import * as Cesium from 'cesium'

export class TerrainAnalysis {
  constructor(viewer) {
    this.viewer = viewer
  }

  async getTerrainHeight(position) {
    const cartographic = Cesium.Cartographic.fromCartesian(position)
    const heights = await Cesium.sampleTerrainMostDetailed(this.viewer.terrainProvider, [cartographic])
    return heights[0].height
  }

  async calculateSlope(positions) {
    const cartographics = positions.map(pos => Cesium.Cartographic.fromCartesian(pos))
    const heights = await Cesium.sampleTerrainMostDetailed(this.viewer.terrainProvider, cartographics)

    let maxSlope = 0
    for (let i = 0; i < heights.length - 1; i++) {
      const height1 = heights[i].height
      const height2 = heights[i + 1].height
      const distance = Cesium.Cartesian3.distance(
        Cesium.Cartesian3.fromRadians(heights[i].longitude, heights[i].latitude, height1),
        Cesium.Cartesian3.fromRadians(heights[i + 1].longitude, heights[i + 1].latitude, height2)
      )
      const slope = Math.abs(height2 - height1) / distance
      maxSlope = Math.max(maxSlope, slope)
    }

    return maxSlope * 100 // Retorna em porcentagem
  }

  async analyzeArea(positions) {
    const slope = await this.calculateSlope(positions)
    const center = Cesium.BoundingSphere.fromPoints(positions).center
    const height = await this.getTerrainHeight(center)

    return {
      slope,
      height,
      riskLevel: this.calculateRiskLevel(slope, height),
      buildingRecommendations: this.getBuildingRecommendations(slope)
    }
  }

  calculateRiskLevel(slope, height) {
    if (slope > 30) return 'Alto'
    if (slope > 20) return 'Médio'
    if (height < 5) return 'Médio' // Risco de inundação
    return 'Baixo'
  }

  getBuildingRecommendations(slope) {
    if (slope > 30) {
      return {
        suitable: false,
        message: 'Área imprópria para construção devido à inclinação excessiva.'
      }
    }

    if (slope > 20) {
      return {
        suitable: true,
        message: 'Construção possível com medidas de contenção e fundações especiais.',
        recommendations: [
          'Realizar estudo geotécnico detalhado',
          'Implementar sistema de drenagem robusto',
          'Considerar muros de contenção',
          'Limitar altura das edificações'
        ]
      }
    }

    if (slope > 10) {
      return {
        suitable: true,
        message: 'Área adequada para construção com cuidados básicos.',
        recommendations: [
          'Planejar sistema de drenagem adequado',
          'Considerar terraplanagem mínima',
          'Seguir normas locais de ocupação'
        ]
      }
    }

    return {
      suitable: true,
      message: 'Área ideal para construção.',
      recommendations: [
        'Seguir normas locais de ocupação',
        'Implementar sistema básico de drenagem'
      ]
    }
  }
}
