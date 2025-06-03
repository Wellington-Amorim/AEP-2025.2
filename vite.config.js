import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import cesium from 'vite-plugin-cesium'

export default defineConfig({
  plugins: [
    vue(),
    cesium()
  ],
  define: {
    'process.env': {}
  },
  resolve: {
    alias: {
      '@': '/src'
    }
  },
  server: {
    open: true
  },
  optimizeDeps: {
    include: ['@cesium/engine', '@cesium/widgets']
  },
  build: {
    chunkSizeWarningLimit: 2000
  }
})
