import { applyMiddleware, createStore } from 'redux';
import logger from 'redux-logger';
import thunk from 'redux-thunk';

import rootReducer from './reducers/index';

const middleware = applyMiddleware(thunk, logger);

const initialState = {
  complaints: {
    data: {
      byId: {},
      ids: [],
      idKey: 'complaintId',
      fetching: false
    },
    filter: [],
    pagination: {
      count: 10,
      page: 0,
      max: 0
    }
  },
  templates: {
    data: {
      byId: {},
      ids: [],
      idKey: 'componentId',
      fetching: false
    },
    filter: [],
    pagination: {
      count: 10,
      page: 0,
      max: 0
    }
  }
};

const store = createStore(rootReducer, initialState, middleware);

export default store;
