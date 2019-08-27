import Api from '../../utility/Api';

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
    let data = getState()[endpoint].data;
    console.log('saveActive', data);
    let active = data.active;
    dispatch({
      type: 'SAVE_START',
      endpoint: endpoint
    });
    dispatch((dispatch) => {
      delete active.requiredEntities;
      console.log(active);
      Api[active.id === 0 ? 'post' : 'put']('/api/' + endpoint + '/' + (active.id === 0 ? '' : active.id), active)
        .then(data => {
          if (data.status && data.status === 500) {
            // alert(data.message);
          }
          dispatch({
            type: 'SAVE_END',
            endpoint: endpoint
          });
        });
    });
  };
}
export function remove (endpoint, id) {
  return function (dispatch, getState) {
    dispatch((dispatch) => {
      Api.delete('/api/' + endpoint + '/' + id, {})
        .then(response => {
          if (response.status && response.status === 500) {
            // alert(data.message);
          }
          dispatch({
            type: 'FETCH_END',
            data: response,
            endpoint: endpoint
          });
        });
    });
  };
}
export function refreshComplaint (id) {
  return function (dispatch, getState) {
    dispatch((dispatch) => {
      Api.patch('/api/complaints/' + id + '/refresh', { keepUserInformation: true })
        .then(response => {
          if (response.status && response.status === 500) {
            // alert(data.message);
          }
          dispatch({
            type: 'UPDATE_SINGLE',
            data: response,
            endpoint: 'complaints'
          });
        });
    });
  };
}
export function changeEntity (complaintId, id = 0, changes) {
  return function (dispatch, getState) {
    const data = getState().complaintStuff.entities.byId[id];
    console.log('WTF', id);
    dispatch((dispatch) => {
      Api[id === 0 ? 'post' : 'put']('/api/complaints/' + complaintId + '/entities/' + (id === 0 ? '' : id), { ...data, ...changes })
        .then(data => {
          if (data.status && data.status === 500) {
            // alert(data.message);
          }
          dispatch({
            type: 'MODIFY_ENTITY',
            data: data
          });
        });
    });
  };
}
export function changeEntityPreference (complaintId, entity, entityOld) {
  return function (dispatch, getState) {
    let query = entity;
    query.preferred = !entity.preferred;
    dispatch((dispatch) => {
      Api.put('/api/complaints/' + complaintId + '/entities/' + (entity.id === 0 ? '' : entity.id), { query })
        .then(data => {
          if (data.status && data.status === 500) {
            // alert(data.message);
          }
          dispatch({
            type: 'MODIFY_ENTITY',
            data: data
          });
          if (entityOld) {
            let query2 = entityOld;
            query2.preferred = false;
            Api.put('/api/complaints/' + complaintId + '/entities/' + (entityOld.id === 0 ? '' : entityOld.id), { query2 })
              .then(data => {
                if (data.status && data.status === 500) {
                  // alert(data.message);
                }
                dispatch({
                  type: 'MODIFY_ENTITY',
                  data: data
                });
              });
          }
        });
    });
  };
}
export function deleteEntity (complaintId, id = 0) {
  return function (dispatch, getState) {
    dispatch((dispatch) => {
      Api.delete('/api/complaints/' + complaintId + '/entities/' + id, {})
        .then(data => {
          if (data.status && data.status === 500) {
            // alert(data.message);
          }
          dispatch({
            type: 'MODIFY_ENTITY',
            data: data
          });
        });
    });
  };
}

export function addEntity (complaintId, query, originalLabelID) {
  return function (dispatch, getState) {
    dispatch((dispatch) => {
      originalLabelID
        ? Api.put('/api/complaints/' + complaintId + '/entities/' + originalLabelID, query)
        : Api.post('/api/complaints/' + complaintId + '/entities', query).then(data => {
          if (data.status && data.status === 500) {
            // alert(data.message);
          }
          dispatch({
            type: 'MODIFY_ENTITY',
            data: data
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
export function fetchStuff (id) {
  return function (dispatch) {
    dispatch({
      type: 'FETCH_SINGLE_COMPLAINT_START',
      id: id
    });
    dispatch((dispatch) => {
      Promise.all([
        Api.get('/api/complaints/' + id + '/entities', {}),
        Api.get('/api/complaints/' + id + '/response', {}),
        Api.get('/api/combinations/' + id, {}),
        Api.get('/api/complaints/' + id + '/log', {}),
        Api.get('/api/complaints/' + id + '/text', {})
      ]).then(data => {
        dispatch({
          type: 'FETCH_SINGLE_COMPLAINT_END',
          entities: data[0],
          components: data[1].components,
          actions: data[1].actions,
          combinations: data[2],
          log: data[3],
          text: data[4].text
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
