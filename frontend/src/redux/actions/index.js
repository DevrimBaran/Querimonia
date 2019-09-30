import Api from '../../utility/Api';

export function setActive (endpoint, id) {
  return function (dispatch, getState) {
    const { byId, fetching } = getState()[endpoint].data;
    if (fetching) console.log('setActive while fetching...');
    if (id === 0) {
      dispatch({
        type: 'SET_ACTIVE',
        endpoint: endpoint,
        id: id
      });
    } else if (byId[id]) {
      dispatch({
        type: 'SET_ACTIVE',
        endpoint: endpoint,
        id: id
      });
      if (endpoint === 'complaints') {
        dispatch(fetchStuff(id));
      }
    } else {
      dispatch((dispatch) => {
        Api.get('/api/' + endpoint + '/' + id, {})
          .then(data => {
            dispatch({
              type: 'UPDATE_SINGLE',
              endpoint: endpoint,
              data: data
            });
            dispatch({
              type: 'SET_ACTIVE',
              endpoint: endpoint,
              id: id
            });
            if (endpoint === 'complaints') {
              dispatch(fetchStuff(id));
            }
          });
      });
    }
  };
}
export function saveActive (endpoint) {
  return function (dispatch, getState) {
    let data = getState()[endpoint].data;
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
          dispatch({
            type: 'SAVE_END',
            data: data,
            endpoint: endpoint
          });
        });
    });
  };
}
export function remove (endpoint, id) {
  return function (dispatch, getState) {
    dispatch((dispatch) => {
      dispatch({
        type: 'REMOVE_START',
        endpoint: endpoint
      });
      Api.delete('/api/' + endpoint + '/' + id, {})
        .then(response => {
          dispatch(fetchData(endpoint));
          // dispatch({
          //   type: 'REMOVE_END',
          //   id: id,
          //   endpoint: endpoint
          // });
        });
    });
  };
}
export function finishComplaint (id) {
  return function (dispatch, getState) {
    dispatch((dispatch) => {
      Api.patch('/api/complaints/' + id + '/close', { })
        .then(response => {
          dispatch({
            type: 'UPDATE_SINGLE',
            data: response,
            endpoint: 'complaints'
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
          dispatch({
            type: 'UPDATE_SINGLE',
            data: response,
            endpoint: 'complaints'
          });
        });
    });
  };
}
export function editComplaint (complaintId, complaintStuff, query) {
  return function (dispatch, getState) {
    dispatch((dispatch) => {
      Api.patch('/api/complaints/' + complaintId, query)
        .then(response => {
          if (!response.state || response.state !== 'ANALYSING') {
            dispatch({
              type: 'UPDATE_SINGLE',
              data: response,
              endpoint: 'complaints'
            });
            dispatch({
              type: 'SET_ACTIVE',
              endpoint: 'complaints',
              id: complaintId
            });
          }
        });
    });
  };
}
export function changeEntity (complaintId, id = 0, changes) {
  return function (dispatch, getState) {
    const data = getState().complaintStuff.entities.byId[id];
    dispatch((dispatch) => {
      Api[id === 0 ? 'post' : 'put']('/api/complaints/' + complaintId + '/entities/' + (id === 0 ? '' : id), { ...data, ...changes })
        .then(data => {
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
      Api.put('/api/complaints/' + complaintId + '/entities/' + (entity.id === 0 ? '' : entity.id), query)
        .then(data => {
          dispatch({
            type: 'MODIFY_ENTITY',
            data: data
          });
          if (entityOld) {
            let query2 = entityOld;
            query2.preferred = false;
            Api.put('/api/complaints/' + complaintId + '/entities/' + (entityOld.id === 0 ? '' : entityOld.id), query2)
              .then(data => {
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
      (originalLabelID
        ? Api.put('/api/complaints/' + complaintId + '/entities/' + originalLabelID, query)
        : Api.post('/api/complaints/' + complaintId + '/entities', query)).then(data => {
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
export function refreshResponses (id) {
  return function (dispatch, getState) {
    dispatch({
      type: 'REFRESH_RESPONSES_START'
    });
    Api.patch('/api/complaints/' + id + '/response/refresh', {})
      .then(data => {
        dispatch({
          type: 'REFRESH_RESPONSES_END',
          components: data.components,
          actions: data.actions
        });
      });
  };
}
export function useComponent (complaintId, id) {
  return function (dispatch, getState) {
    Api.patch('/api/complaints/' + complaintId + '/response/' + id + '?used=true', {});
    dispatch({
      type: 'USE_COMPONENT',
      id: id
    });
  };
}
export function updateResponseText (id, text) {
  return function (dispatch, getState) {
    Api.patch('/api/complaints/' + id + '/response', { text: text });
    // .then(data => {
    //   dispatch({
    //     type: 'REFRESH_RESPONSES_END',
    //     components: data.components,
    //     actions: data.actions
    //   });
    // });
  };
}
export function fetchStuff (id) {
  return function (dispatch, getState) {
    if (!getState().complaints.data.byId[id]) return;
    const { state } = getState().complaints.data.byId[id];
    dispatch({
      type: 'FETCH_SINGLE_COMPLAINT_START',
      id: id
    });
    if (state === 'NEW') {
      Api.patch('/api/complaints/' + id, { state: 'IN_PROGRESS' })
        .then(data => {
          console.log(data);
          dispatch({
            type: 'UPDATE_SINGLE',
            endpoint: 'complaints',
            data: data
          });
        });
    };
    dispatch((dispatch) => {
      Promise.all([
        Api.get('/api/complaints/' + id + '/entities', {}),
        Api.get('/api/complaints/' + id + '/response', {}),
        Api.get('/api/combinations/' + id, {}),
        Api.get('/api/complaints/' + id + '/log', {}),
        Api.get('/api/complaints/' + id + '/text', {})
      ]).then(data => {
        console.log(data);
        dispatch({
          type: 'FETCH_SINGLE_COMPLAINT_END',
          entities: data[0],
          components: data[1].components,
          response: data[1].response,
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
    dispatch((dispatch, getState) => {
      Api.put('/api/config/current?configId=' + id, {})
        .then(data => {
          const active = Object.values(getState().config.data.byId).find(c => c.active);
          dispatch({
            type: 'ACTIVATE_CONFIG',
            id: id,
            oldId: active.id,
            endpoint: 'config'
          });
        });
    });
  };
}
