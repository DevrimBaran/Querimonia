import { applyMiddleware, createStore, compose } from 'redux';
import logger from 'redux-logger';
import thunk from 'redux-thunk';

import rootReducer from './reducers/index';
// import templates from './templates/index';

const middleware = applyMiddleware(thunk, logger);

const initialState = {
  complaints: {
    data: {
      byId: {},
      active: false,
      ids: [],
      fetching: false
    },
    filter: [
      {
        label: 'Stimmung',
        name: 'sentiment',
        type: 'select',
        multiple: true,
        values: [
          { label: 'Ekel', value: 'Ekel' },
          { label: 'Freude', value: 'Freude' },
          { label: 'Furcht', value: 'Furcht' },
          { label: 'Kummer', value: 'Kummer' },
          { label: 'Verachtung', value: 'Verachtung' },
          { label: 'Wut', value: 'Wut' },
          { label: 'Überraschung', value: 'Überraschung' }
        ]
      },
      {
        label: 'Kategorie',
        name: 'subject',
        type: 'select',
        multiple: true,
        values: [
          { label: 'Fahrt nicht erfolgt', value: 'Fahrt nicht erfolgt' },
          { label: 'Fahrer unfreundlich', value: 'Fahrer unfreundlich' },
          { label: 'Sonstiges', value: 'Sonstiges' }
        ]
      },
      {
        label: 'Status',
        name: 'status',
        type: 'select',
        multiple: true,
        values: [
          { label: 'Neu', value: 'NEW' },
          { label: 'In Bearbeitung', value: 'IN_PROGRESS' },
          { label: 'Geschlossen', value: 'CLOSED' }
        ]
      },
      {
        label: 'Stichwort',
        name: 'keywords',
        multiple: true,
        type: 'text'
      },
      {
        label: 'Eingangsdatum (von)',
        name: 'date_min',
        type: 'date'
      },
      {
        label: 'Eingangsdatum (bis)',
        name: 'date_max',
        type: 'date'
      },
      {
        label: 'Sortieren nach',
        name: 'sort_by',
        type: 'select',
        values: [
          { label: 'Eingangsdatum (absteigend)', value: 'upload_date_asc' },
          { label: 'Eingangsdatum (aufsteigend)', value: 'upload_date_desc' },
          { label: 'Kategorie (absteigend)', value: 'subject_asc' },
          { label: 'Kategorie (aufsteigend)', value: 'subject_desc' },
          { label: 'Stimmung (absteigend)', value: 'sentiment_asc' },
          { label: 'Stimmung (aufsteigend)', value: 'sentiment_desc' }
        ]
      }
    ],
    pagination: {
      count: 10,
      page: 0,
      max: 0
    }
  },
  components: {
    data: {
      byId: {},
      ids: [],
      active: false,
      fetching: false
    },
    filter: [
      {
        label: 'Stichwort',
        name: 'keywords',
        multiple: true,
        type: 'text'
      },
      {
        label: 'Aktion',
        name: 'action_code',
        multiple: true,
        type: 'select',
        values: [
          { label: 'E-Mail', value: 'SEND_MAIL' },
          { label: 'Gutschein', value: 'ATTACH_VOUCHER' }
        ]
      },
      {
        label: 'Sortieren nach',
        name: 'sort_by',
        type: 'select',
        values: [
          { label: 'ID (absteigend)', value: 'id_desc' },
          { label: 'ID (aufsteigend)', value: 'id_asc' },
          { label: 'Priorität (absteigend)', value: 'priority_desc' },
          { label: 'Priorität (aufsteigend)', value: 'priority_asc' },
          { label: 'Name (absteigend)', value: 'name_desc' },
          { label: 'Name (aufsteigend)', value: 'name_asc' }
        ]
      }
    ],
    pagination: {
      count: 10,
      page: 0,
      max: 0
    }
  },
  config: {
    data: {
      byId: {},
      ids: [],
      active: false,
      fetching: false
    },
    filter: [
      {
        label: 'Sortieren nach',
        name: 'sort_by',
        type: 'select',
        values: [
          { label: 'ID (absteigend)', value: 'id_desc' },
          { label: 'ID (aufsteigend)', value: 'id_asc' },
          { label: 'Name (absteigend)', value: 'name_desc' },
          { label: 'Name (aufsteigend)', value: 'name_asc' }
        ]
      }
    ],
    pagination: {
      count: 10,
      page: 0,
      max: 0
    }
  },
  currentConfig: {
    extractors: []
  },
  allExtractors: {},
  complaintStuff: {
    entities: {
      byId: { },
      ids: [],
      calculated: []
    },
    components: [],
    actions: [],
    log: [],
    combinations: [],
    text: null,
    counter: 0,
    id: 0,
    done: false
  }
};

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const store = createStore(rootReducer, initialState, composeEnhancers(middleware));

export default store;
