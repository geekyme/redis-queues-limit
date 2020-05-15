## Redis-backed Distributed Job Queue w/ Rate Limiter

[WORK IN PROGRESS]

- Pending addition of [Rate Limiter](https://www.javadoc.io/doc/org.redisson/redisson/3.10.6/org/redisson/api/RRateLimiter.html)
- Pending addition of [Grafana](https://grafana.com/docs/grafana/latest/installation/docker/) to check the outgoing rates
- Pending addition of [pumba](https://github.com/alexei-led/pumba) to simulate failures
- Pending addition of [karate](https://intuit.github.io/karate/karate-gatling/) for load tests

### Setup

`docker-compose up`

### Test

Queue a job:

```
curl --request POST \
  --url http://localhost:8082/queueJob \
  --header 'content-type: application/json' \
  --data '{
	"queueId": "airasia",
	"name": "spec2",
	"score": 2.1
}'
```

Poll for a job:

```
curl --request POST \
  --url http://localhost:8081/pollJob/airasia
```
