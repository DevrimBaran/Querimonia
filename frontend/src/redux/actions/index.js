import Api from '../../utility/Api';
import localize from '../../utility/date';

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
          dispatch({
            type: 'FETCH_END',
            data: response,
            endpoint: endpoint
          });
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
          (function (id) {
            var check = () => {
              Api.get('/api/complaints/' + id, {})
                .then((response) => {
                  if (!response.state || response.state !== 'ANALYSING') {
                    dispatch({
                      type: 'UPDATE_SINGLE',
                      data: response,
                      endpoint: 'complaints'
                    });
                  } else {
                    setTimeout(check, 10000);
                  }
                });
            };
            setTimeout(check, 10000);
          })(id);
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
export function fetchStuff (id) {
  return function (dispatch, getState) {
    const { configuration, receiveDate, receiveTime } = getState().complaints.data.byId[id];
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
        Api.get('/api/complaints/' + id + '/text', {}),
        Api.get('/api/config/' + configuration.id, {})
      ]).then(data => {
        const dateExtractor = data[5].extractors.find(extractor => extractor.label === 'Eingangsdatum');
        const timeExtractor = data[5].extractors.find(extractor => extractor.label === 'Eingangszeit');
        if (dateExtractor) {
          data[0].push({
            id: 'Eingangsdatum',
            label: 'Eingangsdatum',
            start: 0,
            end: 0,
            value: localize(receiveDate),
            setByUser: false,
            preferred: false,
            extractor: dateExtractor.name,
            color: dateExtractor.color
          });
        }
        if (timeExtractor) {
          data[0].push({
            id: 'Eingangszeit',
            label: 'Eingangszeit',
            start: 0,
            end: 0,
            value: receiveTime,
            setByUser: false,
            preferred: false,
            extractor: timeExtractor.name,
            color: timeExtractor.color
          });
        }
        dispatch({
          type: 'FETCH_SINGLE_COMPLAINT_END',
          entities: data[0],
          components: data[1].components,
          actions: data[1].actions,
          combinations: data[2],
          log: data[3],
          text: data[4].text,
          config: data[5]
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
