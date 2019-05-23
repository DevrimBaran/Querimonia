//TODO webpack DEBUG variable
//mocks fetch to simulate api response on fetch().catch
//import fetch from '../tests/apiMock';

const fetchJson = function(action, options) {
    return fetch(action, options)
        .then(response => { return response.json(); })
        .catch((e) => {
            return []
        });
}

const options = function (method, data, refs) {
    let formData = new FormData();
    let isFileUpload = false;
    data = data || {};

    if (refs) {
        for (const index in refs) {
            const input = refs[index];
            data[input.attributes.name.value] = input.value;
            formData.append(input.attributes.name.value, input.type === "file" ? input.files[0] : input.value);
            if (input.type === "file" && input.files[0]) {
                isFileUpload = true;
            }
        }
    }

    let options = {
        method: method,
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json'
        }
    }
    if (isFileUpload) {
        delete options.headers['Content-Type'];
        options.body = formData;
    } else {
        options.body = JSON.stringify(data);
    }

    return options;
}

export const api = {
    get: function(endpoint, data, refs) {
        return fetchJson(endpoint, options('get', data, refs));
    },
    post: function (endpoint, data, refs) {
        return fetchJson(endpoint, options('post', data, refs));
    }
}

export default api;