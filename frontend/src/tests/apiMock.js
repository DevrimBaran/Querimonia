let db = [];
let chars = 'abcdefghijklmnopqrstuvwxyz'.split('');

function random (min, max) {
  return Math.floor((Math.random() * (max - min + 1)) + min);
}

function createDate () {
  return random(1, 28) + '.' + random(1, 12) + '.' + random(1970, 2018);
}

function createTime () {
  return '' + random(0, 23) + ':' + random(0, 59) + ':' + random(0, 59);
}

function createWord () {
  let word = '';
  for (let i = random(3, 7); i > 0; i--) {
    word += chars[random(0, 25)];
  }
  return word;
}

function createText (min, max, withEntities) {
  let text = createWord();
  let entities = [];
  for (let i = random(min, max); i > 0; i--) {
    text += ' ';
    switch (random(0, 10)) {
      case 0:
        let d = createDate();
        entities.push({
          label: 'date',
          start: text.length,
          end: text.length + d.length
        });
        text += d;
        break;
      case 1:
        let b = '' + random(30, 50);
        entities.push({
          label: 'bus',
          start: text.length,
          end: text.length + b.length
        });
        text += b;
        break;
      default:
        text += createWord();
        break;
    }
  }
  if (withEntities) {
    return {
      text: text,
      entities: entities
    };
  } else {
    return text;
  }
}
function createEntry (n) {
  for (let i = 0; i < n; i++) {
    let text = createText(10, 50, true);
    db.push({
      complaintId: db.length,
      receiveDate: createDate(),
      receiveTime: createTime(),
      probableSubject: {
        'Fahrt nicht erfolgt': random(0, 100),
        'Sonstiges': random(0, 100),
        'Fahrer unfreundlich': random(0, 100)
      },
      probableSentiment: {
        'Wütend': random(0, 100),
        'Fröhlich': random(0, 100),
        'Depressiv': random(0, 100)
      },
      text: text.text,
      entities: text.entities,
      preview: text.text.split(' ').slice(0, 10).join(' ')
    });
  }
}

createEntry(200);

function sort (orderby) {
  console.log(orderby);
  return (a, b) => {
    for (let o of orderby) {
      if (a[o.key] === b[o.key]) continue;
      if (a[o.key] < b[o.key]) return o.order === 'ASC' ? -1 : 1;
      return o.order === 'ASC' ? 1 : -1;
    }
  };
}

function filter (clause, e) {
  if (clause.relation) {
    let b = true;
    for (let c of clause.clauses) {
      b = filter(c, e);
      if (b) {
        if (clause.relation === 'OR') {
          return true;
        }
      } else {
        if (clause.relation === 'AND') {
          return false;
        }
      }
    }
    return clause.relation === 'AND';
  } else {
    switch (clause.compare) {
      case '=':
        return e[clause.key] === clause.value;
      case '!=':
        return e[clause.key] !== clause.value;
      case '<':
        return e[clause.key] < clause.value;
      case '<=':
        return e[clause.key] <= clause.value;
      case '>':
        return e[clause.key] > clause.value;
      case '>=':
        return e[clause.key] >= clause.value;
      case 'LIKE':
        return e[clause.key] === clause.value;
      case 'IN':
        return clause.value.contains(e[clause.key]);
      case 'BETWEEN':
        return e[clause.key] >= clause.value[1] && e[clause.key] <= clause.value[1];
      case 'REGEXP':
        return e[clause.key] === clause.value;
      default:
        return false;
    }
  }
}

function fakeResponse (uri, options) {
  let response = db;
  if (options.body) {
    let body = JSON.parse(options.body);
    body.offset || (body.offset = 0);
    body.limit || (body.limit = 20);
    if (body.query) {
      body.query.where && (response = db.filter(filter.bind(this, body.query.where)));
      body.query.where && console.log('1', response);
      body.query.orderby && (response = response.sort(sort(body.query.orderby)));
      body.query.orderby && console.log('2', response);
      return response.slice(body.query.offset, body.query.offset + body.query.limit);
    }
  }
  return db;
}

function fakeFetch (uri, options) {
  return new Promise(
    function (resolve, reject) {
      window.setTimeout(
        () => {
          resolve({
            json: () => fakeResponse(uri, options)
          });
        }, 0
      );
    });
};

export default fakeFetch;
