# Frontend Dockerfile
FROM node:lts-alpine

# Expose the port React development server runs on
EXPOSE 5173

# Install mc tool for navigation (optional)
RUN apk add --no-cache mc

# Внутри контейнера выберем рабочую папку - где будет распологаться мой код
WORKDIR /app

# Copy the project files into the container
COPY ./movie-app /app

# Install project dependencies
RUN npm install

# Start the development server
CMD ["npm", "run", "dev"]