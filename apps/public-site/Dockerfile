# Build stage
FROM node:20-alpine AS build
WORKDIR /app

# Install pnpm
RUN npm install -g pnpm

# Copy root configurations for monorepo
COPY package.json pnpm-lock.yaml pnpm-workspace.yaml turbo.json ./
COPY packages/ ./packages/
COPY apps/public-site/package.json ./apps/public-site/

# Install dependencies
RUN pnpm install --filter public-site...

# Copy the app source
COPY apps/public-site/ ./apps/public-site/

# Build the app
RUN pnpm --filter public-site build --configuration production

# Serving stage
FROM nginx:alpine
COPY --from=build /app/apps/public-site/dist/public-site/browser /usr/share/nginx/html
COPY apps/public-site/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
