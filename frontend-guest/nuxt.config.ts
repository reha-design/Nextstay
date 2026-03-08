// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },
  modules: [
    '@pinia/nuxt'
  ],
  runtimeConfig: {
    public: {
      analyticsUrl: process.env.NUXT_PUBLIC_ANALYTICS_URL || 'http://localhost:4000'
    }
  },
  devServer: {
    port: 3000
  }
})
