FROM oven/bun:1.1-alpine
WORKDIR /app

# Dependency installation
COPY package.json bun.lock* ./
RUN bun install

# Build
COPY . .
RUN bun run build

# Runtime configuration
ENV NUXT_HOST=0.0.0.0
ENV NUXT_PORT=3000
ENV NODE_ENV=production

EXPOSE 3000

# Nuxt output is in .output/server/index.mjs
ENTRYPOINT ["bun", "run", ".output/server/index.mjs"]
