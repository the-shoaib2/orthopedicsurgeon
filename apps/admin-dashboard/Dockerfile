# Build stage
FROM node:20-alpine AS build
WORKDIR /app

# Install pnpm
RUN npm install -g pnpm

# Copy root configurations for monorepo
COPY package.json pnpm-lock.yaml pnpm-workspace.yaml turbo.json ./
COPY packages/ ./packages/
COPY apps/admin-dashboard/package.json ./apps/admin-dashboard/

# Install dependencies for the whole workspace (or just the app)
RUN pnpm install --filter admin-dashboard...

# Copy the app source
COPY apps/admin-dashboard/ ./apps/admin-dashboard/

# Build the app
RUN pnpm --filter admin-dashboard build --configuration production

# Serving stage
FROM nginx:alpine
COPY --from=build /app/apps/admin-dashboard/dist/admin-dashboard/browser /usr/share/nginx/html
COPY apps/admin-dashboard/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
