## Redis-backed Distributed Job Queue w/ Rate Limiter

A proof-of-concept to show how redis is a viable option for the following use case:

- distributed queue / job scheduler
- rate limited outgoing calls

[WORK IN PROGRESS]

- Pending addition of [karate](https://intuit.github.io/karate/karate-gatling/) for load tests
- Pending addition of [pumba](https://github.com/alexei-led/pumba) to simulate failures

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
