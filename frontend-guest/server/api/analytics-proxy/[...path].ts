export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig(event)
  const path = getRouterParam(event, 'path')
  
  // Elysia 서버 주소 (HTTP)
  const targetUrl = `${config.public.analyticsUrl}/${path}`

  console.log(`[Analytics Proxy] Forwarding to ${targetUrl}`)

  return proxyRequest(event, targetUrl)
})
