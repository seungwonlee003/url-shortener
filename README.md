# URL Shortener Service Documentation

## Purpose
This documentation outlines the technical requirements for the URL Shortener Service.

Technology used: Spring Framework, JPA, MySQL Database, JUnit, Mockito, JMeter

## Non-functional Requirements
- Very low latency (approximately 500ms or less)
- Very high availability (no downtime)
- Readability of URL (less than 7 characters)

## Functional Requirements
- Given a long URL, create a short URL
- Short urls are customizable
- Clicking on the short URL redirects to the long URL
- Service should collect metrics like view count

# API Documentation

## URL Controller

### POST /shorten
#### Parameters
No parameters

#### Request Body
```json
{
  "originalUrl": "string"
}
```
#### Response Body
```json
{
  "shortUrl": "string"
}
```
### POST /custom
#### Parameters
No parameters

#### Request Body
```json
{
  "originalUrl": "string",
  "customShortUrl": "string"
}
```
#### Response Body
```json
{
  "shortUrl": "string"
}
```

### GET /{shortUrl}
#### Parameters
- **shortUrl***: string (path)

#### Responses
- **200 OK**

### GET /{shortUrl}/stats
#### Parameters
- **shortUrl***: string (path)

#### Response Body
  ```json
  {
    "totalClicks": 0
  }
```






