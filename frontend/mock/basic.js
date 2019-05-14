var express = require('express');
var app = express();

app.post('/api/test', function (req, res) {
  const response = [{
    "text": "Die Buslinie 637 ist am 01.03.2019 an der Gartenheimstraße in Langenberg(Schule!) um 13:46 nicht gekommen.",
    "answer": null,
    "entities": [
      {
        "label": "line",
        "start": 13,
        "end": 16
      },
      {
        "label": "date",
        "start": 24,
        "end": 34
      }
    ]
   }
  ];
  res.setHeader('Access-Control-Allow-Origin', 'http://localhost:3000')
  res.json(response);
});

app.listen(3001, function () {
  console.log('Mock listening on port 3001!');
});