# Build stage
FROM node:14.17.6 as build
LABEL stage=build
ENV NODE_ENV=production
WORKDIR /app
COPY package.json /app
COPY package-lock.json /app
RUN npm install --production
COPY public /app/public
COPY src /app/src
RUN npm run build

# Package stage
FROM nginx:1.21.1-alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
