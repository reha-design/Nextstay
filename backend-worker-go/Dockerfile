# Build Stage
FROM golang:1.24-alpine AS build
# Git is required for some go modules
RUN apk add --no-cache git
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN go build -o worker main.go

# Run Stage
FROM alpine:latest
RUN apk add --no-cache ca-certificates
WORKDIR /app
COPY --from=build /app/worker .
EXPOSE 9000
ENTRYPOINT ["./worker"]
