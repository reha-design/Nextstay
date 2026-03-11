export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig(event)
  const path = getRouterParam(event, 'path')
  
  // Spring Boot 백엔드 주소 (HTTP)
  const targetUrl = `${config.public.apiUrl}/${path}`

  console.log(`[Backend Proxy] Forwarding to ${targetUrl}`)

  return proxyRequest(event, targetUrl)
})
