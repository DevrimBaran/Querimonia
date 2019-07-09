
const fetchJson = function (action, options) {
  console.log(process.env.NODE_ENV);
  if ((process.env.NODE_ENV === 'development')) {
    const useMockInDev = (document.getElementById('useMock') && document.getElementById('useMock').checked);
    if (useMockInDev) {
      console.log('Application is using mock backend!');
      return fetch('https://querimonia.iao.fraunhofer.de/mock' + action, options)
        .then(response => { return response.ok ? response.json() : []; });
    } else {
      return fetch('https://querimonia.iao.fraunhofer.de/dev' + action, options)
        .then(response => { return response.ok ? response.json() : []; });
    }
  } else {
    return fetch(process.env.REACT_APP_BACKEND_PATH + action, options)
      .then(response => { return response.ok ? response.json() : []; });
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
  if (method === 'post' || method === 'put') {
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
    query = Object.keys(query).filter((name) => query[name]).map((name) => {
      if (Array.isArray(query[name])) {
        return query[name].map(element => {
          return encodeURIComponent(name) + '=' + encodeURIComponent(element);
        }).join('&');
      }
      return encodeURIComponent(name) + '=' + encodeURIComponent(query[name]);
    }).join('&');
    //! ((document.location.search !== query) || (document.location.search !== '?' + query)) && (document.location.href = '?' + query);
    return fetchJson(endpoint + (query ? '?' + query : ''), options('get'));
  },
  delete: function (endpoint) {
    return fetchJson(endpoint, options('delete'));
  },
  post: function (endpoint, data) {
    return fetchJson(endpoint, options('post', data));
  },
  put: function (endpoint, data) {
    return fetchJson(endpoint, options('put', data));
  }
};

export default api;
