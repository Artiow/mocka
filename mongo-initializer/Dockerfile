FROM mongo:6.0.5-jammy
COPY mongo-initialization.js /etc/mongo-initialization.js
ENTRYPOINT ["mongosh", "mongodb://localhost:27017/admin", "/etc/mongo-initialization.js"]
