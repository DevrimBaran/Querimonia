var express = require('express');
var app = express();

app.post('/api', function (req, res) {
  const response = {"succesful": true};
  res.json(response);
  console.log('Response: ' + JSON.stringify(response));
});

app.get('/', function(req, res) {
    res.send('<form method="post" action="/api/"><input type="submit" value="Send"></form>');
});

app.listen(3001, function () {
  console.log('Example app listening on port 3001!');
});