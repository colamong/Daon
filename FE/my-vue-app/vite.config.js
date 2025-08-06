import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 5173,
    cors: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        ws: true,
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('프록시 에러:', err);
          });
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('프록시 요청:', req.method, req.url, '→', options.target + req.url);
            proxyReq.setHeader('Origin', 'http://localhost:8080');
          });
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('프록시 응답:', proxyRes.statusCode, req.url);
          });
        }
      }
    }
  }
})
