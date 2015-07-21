# Falcon-app

This application demonstrates a scalable application using the following technologies:
- Play Framework (https://www.playframework.com/)
- Redis (http://redis.io/)


The REST endpoint where you can put dummy JSON input points to:
```
<DEPLOYED_SERVER>:9000/update
```

Just a sample for JSON:
```
{
  "message": {
    "currentDate": "1436440431",
    "clientBrowser": "Mozilla",
    "data": "This is the interesting parts"
  }
}
```


The other browser clients can be connected through WebSocket protocol:
```
<DEPLOYED_SERVER>:9000
```

