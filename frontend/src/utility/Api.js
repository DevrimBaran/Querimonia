import ErrorModal from './../components/ErrorModal';

const fetchJson = function (action, options, responseFormat = 'json') {
  const processResponse = (response) => {
    if (responseFormat) {
      if (!response.ok) {
        return response.json().then(data => {
          throw new ErrorModal.QuerimoniaError(data);
        });
      }
      return response[responseFormat]();
    } else {
      return response;
    }
  };
  // if (action.substr(0, 7) === '/python') {
  //   return fetch('https://querimonia.iao.fraunhofer.de' + action, options)
  //     .then((response) => processResponse(response))
  //     .catch(ErrorModal.catch);
  // };
  return fetch(process.env.REACT_APP_BACKEND_PATH + action, options)
    .then((response) => processResponse(response))
    .catch(ErrorModal.catch);
};
const getOptions = function (method, data, additional = {}) {
  data = data || {};

  let options = {
    method: method,
    mode: 'cors',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
    ...additional
  };
  if (method === 'post' || method === 'put' || method === 'PATCH') {
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
  get: function (endpoint, query, options, responseFormat) {
    query = Object.keys(query).filter((name) => query[name]).map((name) => {
      if (Array.isArray(query[name])) {
        return query[name].map(element => {
          return encodeURIComponent(name) + '=' + encodeURIComponent(element);
        }).join('&');
      }
      return encodeURIComponent(name) + '=' + encodeURIComponent(query[name]);
    }).join('&');
    return fetchJson(endpoint + (query ? '?' + query : ''), getOptions('get', {}, options), responseFormat);
  },
  delete: function (endpoint, query, options, responseFormat) {
    query = Object.keys(query).filter((name) => query[name] || query[name] === 0).map((name) => {
      return encodeURIComponent(name) + '=' + encodeURIComponent(query[name]);
    }).join('&');
    return fetchJson(endpoint + (query ? '?' + query : ''), getOptions('delete', {}, options), responseFormat);
  },
  patch: function (endpoint, query, options, responseFormat) {
    return fetchJson(endpoint, getOptions('PATCH', query, options), responseFormat);
  },
  post: function (endpoint, query, options, responseFormat) {
    return fetchJson(endpoint, getOptions('post', query, options), responseFormat);
  },
  put: function (endpoint, query, options, responseFormat) {
    return fetchJson(endpoint, getOptions('put', query, options), responseFormat);
  },
  queryput: function (endpoint, query, options, responseFormat) {
    query = Object.keys(query).filter((name) => query[name]).map((name) => {
      if (Array.isArray(query[name])) {
        return query[name].map(element => {
          return encodeURIComponent(name) + '=' + encodeURIComponent(element);
        }).join('&');
      }
      return encodeURIComponent(name) + '=' + encodeURIComponent(query[name]);
    }).join('&');
    return fetchJson(endpoint + (query ? '?' + query : ''), getOptions('put', {}, options), responseFormat);
  },
  querypatch: function (endpoint, query, options, responseFormat) {
    query = Object.keys(query).filter((name) => query[name]).map((name) => {
      if (Array.isArray(query[name])) {
        return query[name].map(element => {
          return encodeURIComponent(name) + '=' + encodeURIComponent(element);
        }).join('&');
      }
      return encodeURIComponent(name) + '=' + encodeURIComponent(query[name]);
    }).join('&');
    return fetchJson(endpoint + (query ? '?' + query : ''), getOptions('patch', {}, options), responseFormat);
  }
};

export default api;
