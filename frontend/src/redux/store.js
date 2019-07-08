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
      fetching: false
    },
    filter: [
      {
        label: 'Stimmung',
        name: 'sentiment',
        type: 'select',
        values: [
          { label: 'Ekel', value: '' },
          { label: 'Freude', value: '' },
          { label: 'Furcht', value: '' },
          { label: 'Kummer', value: '' },
          { label: 'Verachtung', value: '' },
          { label: 'Wut', value: '' },
          { label: 'Überraschung', value: '' }
        ]
      },
      {
        label: 'Kategorie',
        name: 'subject',
        type: 'select',
        values: [
          { label: 'Foo', value: '' },
          { label: 'Faa', value: '' }
        ]
      },
      {
        label: 'Stichwort',
        name: 'text_contains',
        type: 'text'
      },
      {
        label: 'Sortieren nach',
        name: 'order_by',
        type: 'select',
        values: [
          { label: 'Eingangsdatum', value: 'upload_date' },
          { label: 'Kategorie', value: 'subject' },
          { label: 'Stimmung', value: 'sentiment' }
        ]
      },
      {
        label: 'Absteigend',
        name: 'desc',
        type: 'checkbox'
      }
    ],
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
