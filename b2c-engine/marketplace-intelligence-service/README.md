# Marketplace Intelligence Service

## Overview
This service provides competitive intelligence by scraping external e-commerce sites for product pricing and availability data. It is used by the B2C Engine to adjust dynamic pricing and offers.

## Features
- **Web Scraper**: Jsoup-based scraper for extracting product title and price.
- **API**: Exposes endpoints to trigger scraping on-demand.

## API Endpoints
### Scrape Product
- **GET** `/api/v1/marketplace/scrape?url={target_url}`
- **Response**: JSON containing `title`, `price_raw`, `price_parsed`.

## Build & Run
### Prerequisites
- JDK 21
- Maven

### Build
```bash
mvn clean package
```

### Run
```bash
java -jar target/marketplace-intelligence-service-1.0.0-SNAPSHOT.jar
```

### Docker
```bash
docker build -t marketplace-intelligence-service .
docker run -p 8086:8080 marketplace-intelligence-service
```
