import Api from '../utility/Api';

export function activate (endpoint, id) {
  return function (dispatch, getState) {
    const { active } = getState()[endpoint].data;
    dispatch((dispatch) => {
      Api.put('/api/' + endpoint + '/' + active.id, active);
    });
    dispatch({
      type: 'SET_ACTIVE',
      endpoint: endpoint,
      id: id
    });
    dispatch((dispatch) => {
      Api.put('/api/' + endpoint + '/' + active.id, active);
    });
  };
}
export function saveActive (endpoint) {
  return function (dispatch, getState) {
    const { active } = getState()[endpoint].data;
    dispatch({
      type: 'SAVE_START',
      endpoint: endpoint
    });
    dispatch((dispatch) => {
      Api[active.id === 0 ? 'post' : 'put']('/api/' + endpoint + '/' + active.id, active)
        .then(data => {
          if (data.status && data.status === 500) {
            alert(data.message);
          }
          dispatch({
            type: 'SAVE_END',
            endpoint: endpoint
          });
        });
    });
  };
}
export function fetchData (endpoint) {
  return function (dispatch, getState) {
    const { filter, pagination } = getState()[endpoint];
    let query = filter.reduce((obj, input) => {
      if (input.value) {
        obj[input.name] = input.value;
      }
      return obj;
    }, {});
    dispatch({
      type: 'FETCH_START',
      endpoint: endpoint
    });
    dispatch((dispatch) => {
      Api.get('/api/' + endpoint + '/count', query)
        .then(data => {
          dispatch({
            type: 'PAGINATION_CHANGE',
            endpoint: endpoint,
            name: 'max',
            value: data
          });
        });
    });
    dispatch({
      type: 'PAGINATION_CHANGE',
      endpoint: endpoint,
      name: 'page',
      value: 0
    });
    dispatch((dispatch, getState) => {
      Api.get('/api/' + endpoint, { count: pagination.count, page: 0, ...query })
        .then(data => {
          dispatch({
            type: 'FETCH_END',
            data: data,
            endpoint: endpoint
          });
        });
    }
    );
  };
}
export function fetchCurrentConfig () {
  return function (dispatch) {
    dispatch((dispatch) => {
      Api.get('/api/config/current', {})
        .then(data => {
          dispatch({
            type: 'CURRENT_CONFIG',
            data: data
          });
        });
    });
  };
}
export function setCurrentConfig (id) {
  return function (dispatch) {
    dispatch((dispatch) => {
      Api.put('/api/config/current?configId=' + id, {})
        .then(data => {
          dispatch({
            type: 'CURRENT_CONFIG',
            id: id
          });
        });
    });
  };
}
