import http from "k6/http";
import { check } from "k6";

export default function () {
  let url1 = "http://localhost:8081/queueJob";
  let payload1 = JSON.stringify({
    queueId: "airasia",
    name: "spec" + Date.now() + Math.random() * 1000, // random enough
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
}
