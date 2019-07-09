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
      Api.put('/api/' + endpoint + '/' + active.id, active)
        .then(data => {
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
    dispatch((dispatch, getState) => {
      Api.get('/api/' + endpoint, { count: pagination.count, page: pagination.page, ...query })
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
