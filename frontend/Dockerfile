FROM oven/bun:latest AS builder

WORKDIR /app

COPY package.json ./
COPY bun.lock ./
RUN bun install --frozen-lockfile

COPY svelte.config.js ./
COPY tsconfig.json ./
COPY vite.config.ts ./

COPY static ./static
COPY src ./src

ENV PUBLIC_BACKEND_URL=http://localhost:8089
RUN bun run build

FROM node:20-alpine AS server

WORKDIR /app

COPY --from=builder /app/package.json ./
COPY --from=builder /app/build ./build

EXPOSE 3000

ENTRYPOINT ["node", "build"]
