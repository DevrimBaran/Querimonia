import { showErrorPopup } from './../components/ErrorPopup';

const fetchJson = function (action, options) {
  const processResponse = (response) => {
    const responseJsonPromise = response.json();
    if (!response.ok) showErrorPopup(responseJsonPromise);
    return responseJsonPromise;
  };
  if ((process.env.NODE_ENV === 'development')) {
    const useMockInDev = (document.getElementById('useMock') && document.getElementById('useMock').checked);
    if (useMockInDev) {
      console.log('Application is using mock backend!');
      return fetch('https://querimonia.iao.fraunhofer.de/mock' + action, options)
        .then((response) => processResponse(response));
    } else {
      return fetch('https://querimonia.iao.fraunhofer.de/dev' + action, options)
        .then((response) => processResponse(response));
    }
  } else {
    return fetch(process.env.REACT_APP_BACKEND_PATH + action, options)
      .then((response) => processResponse(response));
  }
};
const options = function (method, data, additional = {}) {
  data = data || {};

  let options = {
    method: method,
    mode: 'cors',
    headers: {
      // 'Authorization': 'Basic ' + btoa('admin:QuerimoniaPass2019'),
      'Authorization': 'Basic YWRtaW46UXVlcmltb25pYVBhc3MyMDE5',
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
  delete: function (endpoint, query) {
    query = Object.keys(query).filter((name) => query[name] || query[name] === 0).map((name) => {
      return encodeURIComponent(name) + '=' + encodeURIComponent(query[name]);
    }).join('&');
    return fetchJson(endpoint + (query ? '?' + query : ''), options('delete'));
  },
  patch: function (endpoint, data) {
    return fetchJson(endpoint, options('PATCH', data));
  },
  post: function (endpoint, data) {
    return fetchJson(endpoint, options('post', data));
  },
  put: function (endpoint, data) {
    return fetchJson(endpoint, options('put', data));
  },
  queryput: function (endpoint, query) {
    query = Object.keys(query).filter((name) => query[name]).map((name) => {
      if (Array.isArray(query[name])) {
        return query[name].map(element => {
          return encodeURIComponent(name) + '=' + encodeURIComponent(element);
        }).join('&');
      }
      return encodeURIComponent(name) + '=' + encodeURIComponent(query[name]);
    }).join('&');
    //! ((document.location.search !== query) || (document.location.search !== '?' + query)) && (document.location.href = '?' + query);
    return fetchJson(endpoint + (query ? '?' + query : ''), options('put'));
  }
};

export default api;
