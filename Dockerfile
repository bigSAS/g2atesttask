FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean

FROM mcr.microsoft.com/playwright/java:v1.44.0-jammy
RUN apt-get update && apt-get install -y --no-install-recommends \
    libnss3 \
    libatk-bridge2.0-0 \
    libxkbcommon-x11-0 \
    libxcomposite1 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /root/.m2 /root/.m2
COPY --from=build /app/ /app
ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright
ENTRYPOINT ["mvn", "test"]
