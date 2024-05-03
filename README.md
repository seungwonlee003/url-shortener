# URL Shortener Service Documentation

## Purpose
This documentation outlines the technical requirements and troubleshooting details for the URL Shortener Service.

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

## Details (User Activity Analysis)
There are two main API endpoints: `createShortUrl` and `redirectToOriginalUrl`. Short URLs are generated daily, typically spread out over time, resulting in minimal concurrent requests to the `createShortUrl` endpoints. The read-to-write ratio is 1:100, meaning that reads occur more frequently than writes. This phenomenon is primarily attributed to exposed URLs embedded in viral social media posts or websites, attracting constant or sporadic external traffic, whereas newly created URLs initially experience few reads, resulting in an instantaneous read-to-write ratio of approximately 1. Based on this, it's anticipated that only a small fraction of short URLs, typically registered commercially or by specific users, will contribute to the majority of the 1:100 read-to-write ratio. 

# Troubleshooting

## Technical Issues
**Scenario**: A company shares a short URL on its social media platform. Upon posting, there is a sudden surge in concurrent requests to the redirectToOriginalUrl endpoint.
**Problem**: Data integrity issues observed with view counts when the throughput exceeds 50 requests per second.  
  
## Solutions

Several approaches proposed, each with its own trade-offs:
1. **SERIALIZABLE isolation level**: this resulted in deadlocks due to shareable read locks.
2. **Pessimistic Write Locks**: pessimistic write locks at the database level for ongoing transactions blocks both read and write operations after a read in a transaction, thus resolving deadlocks and data integrity concerns. However, this approach resulted in decreased throughput as due to lock contentions on a transaction level. Furthermore, potential issues may arise from lock propagations within the database.
3. **Optimistic Locks with Versioning**: Suitable when conflicts are rare and concurrency is not a significant concern. However, implementing retry logic in case of failure can be cumbersome.
4. **Single Update Statement**: this method involves using JPQL's update query. While this avoids maintaining a lock for the entire transaction, it ensures view count integrity with an exclusive lock only on the update statement. However, it sacrifices JPA's dirty checking mechanism and therefore violates the object-oriented programming principle. Also, domain logic shifts to the service and repository layers.

Other considerations:
- Considering the sporadic nature of highly concurrent calls to the `redirectToOriginalUrl` endpoint, where instances of data inaccuracy are infrequent, eventual consistency model was considered. This model prioritizes minimizing lock contention over real-time view count integrity by aggregating logs or data in the database and performing batch updates at regular intervals. However, this approach proved to be inefficient. During JPA's dirty checking, an update statement is executed, and exclusive locks are applied, similar to the single update statement approach. Additionally, it introduces concerns regarding data integrity. To improve efficiency, the approach should ideally avoid sending any update queries or making roundtrips to the database altogether. This could potentially be achieved by implementing a cache layer with an appropriate write-back policy. However, such optimization was considered premature given the current scale of the system.

Solution:
- Single update statement is used considering the requirement of the application.

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






