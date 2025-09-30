# ---- Build stage ----
FROM node:18-bullseye AS build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# ---- Nginx stage ----
FROM nginx:1.27-alpine
COPY --from=build /app/build /usr/share/nginx/html
# Optional: custom nginx.conf for SPA routing
# COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx","-g","daemon off;"]
