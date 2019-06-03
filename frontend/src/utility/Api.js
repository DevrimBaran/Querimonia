import fakeFetch from '../tests/apiMock';

const fetchJson = function (action, options) {
  console.log(process.env.NODE_ENV);
  if (process.env.NODE_ENV === 'development' || process.env.REACT_APP_BACKEND_PATH === 'mock') {
    console.log('Application is using mock backend!');
    return fakeFetch('https://beschwerdemanagement-dev.iao.fraunhofer.de' + action, options)
      .then(response => { return response.json(); });
  } else {
    return fetch(process.env.REACT_APP_BACKEND_PATH + action, options)
      .then(response => { return response.json(); });
  }
};

const options = function (method, data) {
  data = data || {};

  let options = {
    method: method,
    mode: 'cors',
    headers: {
      'Content-Type': 'application/json'
    }
  };
  if (method === 'post') {
    if (data instanceof FormData) {
      delete options.headers['Content-Type'];
      options.body = data;
    } else {
      options.body = JSON.stringify(data);
    }
  }

  return options;
};

export const api = {
  get: function (endpoint, query) {
    return fetchJson(endpoint + '?query=' + encodeURIComponent(JSON.stringify(query)), options('get'));
  },
  post: function (endpoint, data) {
    return fetchJson(endpoint, options('post', data));
  }
};

export default api;
