import ErrorModal from './../components/ErrorModal';

const fetchJson = function (action, options) {
  const processResponse = (response) => {
    const json = response.json();
    if (!response.ok) {
      return json.then(json => {
        throw new ErrorModal.QuerimoniaError(json);
      });
    }
    return json;
  };
  if ((process.env.NODE_ENV === 'development')) {
    const useMockInDev = (document.getElementById('useMock') && document.getElementById('useMock').checked);
    if (useMockInDev) {
      console.log('Application is using mock backend!');
      return fetch('https://querimonia.iao.fraunhofer.de/mock' + action, options)
        .then((response) => processResponse(response))
        .catch(ErrorModal.catch);
    } else {
      return fetch('https://querimonia.iao.fraunhofer.de/dev' + action, options)
        .then((response) => processResponse(response))
        .catch(ErrorModal.catch);
    }
  } else {
    return fetch(process.env.REACT_APP_BACKEND_PATH + action, options)
      .then((response) => processResponse(response))
      .catch(ErrorModal.catch);
  }
};
const options = function (method, data, additional = {}) {
  data = data || {};

  let options = {
    method: method,
    mode: 'cors',
    // credentials: 'include',
    headers: {
      'Authorization': 'Basic ' + btoa('admin:QuerimoniaPass2019'),
      // 'Authorization': 'Basic YWRtaW46UXVlcmltb25pYVBhc3MyMDE5',
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
