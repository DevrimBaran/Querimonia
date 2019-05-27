//TODO webpack DEBUG variable
//mocks fetch to simulate api response on fetch().catch
//import fetch from '../tests/apiMock';

const fetchJson = function(action, options) {
    console.log('test');
    return fetch('https://beschwerdemanagement-dev.iao.fraunhofer.de' + action, options)
        .then(response => { return response.json(); })
}

const options = function (method, data) {
    data = data || {};

    let options = {
        method: method,
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json'
        }
    }
    if (method === 'post') {
        if (data instanceof FormData) {
            delete options.headers['Content-Type'];
            options.body = data;
        } else {
            options.body = JSON.stringify(data);
        }
    }

    return options;
}

export const api = {
    get: function(endpoint, query) {
        return fetchJson(endpoint + '?query=' + encodeURIComponent(JSON.stringify(query)), options('get'));
    },
    post: function (endpoint, data) {
        return fetchJson(endpoint, options('post', data));
    }
}

export default api;
