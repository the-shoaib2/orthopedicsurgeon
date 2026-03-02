import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '1m', target: 20 }, // ramp up
        { duration: '3m', target: 20 }, // stay at 20 users
        { duration: '1m', target: 0 },  // ramp down
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% of requests must complete below 500ms
    },
};

export default function () {
    const res = http.get('http://api:8080/api/v1/hospitals'); // internal docker network or localhost
    check(res, {
        'status is 200': (r) => r.status === 200,
    });
    sleep(1);
}
