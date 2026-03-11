// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },
  modules: [
    '@pinia/nuxt'
  ],
  nitro: {
    preset: 'aws-lambda',
    externals: {
      inline: [
        'unhead', 'ufo', 'hookable', 'vue', 'vue-router', 'pinia', 
        '@vue/server-renderer', '@vue/runtime-core', '@vue/runtime-dom',
        '@vue/shared', '@vue/reactivity', 'devalue', 'destr', 'klona',
        'ohash', 'unctx', 'unenv', 'scule', 'defu'
      ]
    }
  },
  runtimeConfig: {
    public: {
      apiUrl: process.env.NUXT_PUBLIC_API_URL || 'http://localhost:8080',
      analyticsUrl: process.env.NUXT_PUBLIC_ANALYTICS_URL || 'http://localhost:4000'
    }
  },
  devServer: {
    port: 3000
  }
})
