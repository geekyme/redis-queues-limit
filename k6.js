import http from "k6/http";
import { check } from "k6";

export default function () {
  let url1 = "http://localhost:8081/queueJob";
  let payload1 = JSON.stringify({
    queueId: "airasia",
    name: "spec1",
    score: 1.0,
  });

  let params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  let res1 = http.post(url1, payload1, params);
  check(res1, {
    "queue is status 200": (r) => r.status === 200,
  });

  let url2 = "http://localhost:8080/pollJob/airasia";

  let res2 = http.post(url2, "", {});

  check(res2, {
    "poll is status 200": (r) => r.status === 200,
  });
}
