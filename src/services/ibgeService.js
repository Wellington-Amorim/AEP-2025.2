const BASE_URL = 'https://servicodados.ibge.gov.br/api/v1'

export async function getMunicipioInfo(municipioId) {
  try {
    const response = await fetch(`${BASE_URL}/localidades/municipios/${municipioId}`)
    return await response.json()
  } catch (error) {
    console.error('Erro ao buscar dados do município:', error)
    throw error
  }
}

export async function getDadosDemograficos(municipioId) {
  try {
    // Busca dados do último censo
    const response = await fetch(`${BASE_URL}/agregados/6579/periodos/2010/variaveis/9324?localidades=N6[${municipioId}]`)
    return await response.json()
  } catch (error) {
    console.error('Erro ao buscar dados demográficos:', error)
    throw error
  }
}

export async function pesquisarMunicipio(termo) {
  try {
    const response = await fetch(`${BASE_URL}/localidades/municipios?nome=${termo}`)
    return await response.json()
  } catch (error) {
    console.error('Erro ao pesquisar município:', error)
    throw error
  }
}

export async function getIndicadoresUrbanisticos(municipioId) {
  try {
    // Dados de domicílios, saneamento básico, etc.
    const response = await fetch(`${BASE_URL}/agregados/1394/periodos/2010/variaveis?localidades=N6[${municipioId}]`)
    return await response.json()
  } catch (error) {
    console.error('Erro ao buscar indicadores urbanísticos:', error)
    throw error
  }
}
