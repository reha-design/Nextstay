FROM oven/bun:1.1-alpine
WORKDIR /app
COPY package.json bun.lock* ./
RUN bun install
COPY . .
EXPOSE 4000
ENTRYPOINT ["bun", "run", "src/index.ts"]
