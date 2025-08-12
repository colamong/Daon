import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  define: {
    global: 'globalThis',
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 5173,
    strictPort: true,
    cors: true,
    proxy: {
      // ✅ REST API 프록시
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        ws: false, // API는 웹소켓 아님
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('프록시 에러:', err)
          })
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('프록시 요청(API):', req.method, req.url, '→', options.target + req.url)
            // 굳이 Origin 강제 세팅 필요 없음. (CORS 설정 제대로면 불필요)
            // proxyReq.setHeader('Origin', 'http://localhost:8080')
          })
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('프록시 응답(API):', proxyRes.statusCode, req.url)
          })
        }
      },

      // ✅ 웹소켓(SockJS/STOMP) 프록시 — 이게 핵심!
      // 백엔드가 /ws/** 를 쓰므로 rewrite 없이 그대로 터널링
      '/ws': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        ws: true, // ← 웹소켓 업그레이드 허용
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('WS 프록시 에러:', err)
          })
          // 웹소켓 업그레이드 이벤트용 훅 (디버깅 편의)
          proxy.on('open', (proxySocket) => {
            console.log('WS 프록시 open')
          })
          proxy.on('close', (res, socket, head) => {
            console.log('WS 프록시 close')
          })
          // 필요시 헤더 조정이 있으면 아래 사용 (일반적으로 불필요)
          // proxy.on('proxyReqWs', (proxyReq, req, socket, options, head) => {
          //   proxyReq.setHeader('Origin', 'http://localhost:8080')
          // })
        }
      }
    }
  }
})
