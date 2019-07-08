import Api from '../utility/Api';

export function fetchData (endpoint) {
  return function (dispatch) {
    dispatch({
      type: 'FETCH_START',
      endpoint: endpoint
    });
    dispatch((dispatch, getState) => {
      Api.get('/api/' + endpoint + '/count', {})
        .then(data => {
          dispatch({
            type: 'PAGINATION_CHANGE',
            endpoint: endpoint,
            name: 'max',
            value: data
          });
        });
    }
    );
    dispatch((dispatch, getState) => {
      const { filter, pagination } = getState()[endpoint];
      let query = { count: pagination.count, page: pagination.page };
      query = filter.reduce((obj, input) => {
        if (input.value) {
          obj[input.name] = input.value;
        }
        return obj;
      }, query);
      Api.get('/api/' + endpoint, query)
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
