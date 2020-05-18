import http from "k6/http";
import { check } from "k6";

export default function () {
  queueJob("airasia");
  queueJob("lionair");
}

function queueJob(queueId) {
  let url = "http://localhost:8081/queueJob";
  let payload = JSON.stringify({
    queueId: queueId,
    name: "spec" + Date.now() + Math.random() * 1000, // random enough
    score: 1.0,
  });

  let params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  let res = http.post(url, payload, params);
  check(res, {
    "queue is status 200": (r) => r.status === 200,
  });
}
