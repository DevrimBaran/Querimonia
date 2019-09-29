import { applyMiddleware, createStore, compose } from 'redux';
// import logger from 'redux-logger';
import thunk from 'redux-thunk';

import rootReducer from './reducers/index';
// import templates from './templates/index';

// const middleware = applyMiddleware(thunk, logger);
const middleware = applyMiddleware(thunk);

const initialState = {
  login: {
    access: -1
  },
  complaints: {
    data: {
      byId: {},
      active: false,
      ids: [],
      fetching: false
    },
    filter: [
      {
        label: 'Stichwort',
        icon: 'fas fa-search',
        multiple: 'multiple',
        name: 'keywords',
        type: 'text'
      },
      {
        label: 'Status',
        name: 'state',
        multiple: 'multiple',
        type: 'select',
        icon: 'fas fa-clipboard',
        values: [
          { label: 'Neu', value: 'NEW' },
          { label: 'In Bearbeitung', value: 'IN_PROGRESS' },
          { label: 'Geschlossen', value: 'CLOSED' },
          { label: 'In Analyse', value: 'ANALYSING' },
          { label: 'Fehler', value: 'ERROR' }
        ]
      },
      {
        label: 'Emotion',
        name: 'emotion',
        type: 'select',
        multiple: 'multiple',
        icon: 'fas fa-theater-masks',
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
        label: 'Kategorie ',
        name: 'subject',
        type: 'select',
        multiple: 'multiple',
        icon: 'fas fa-bars',
        values: [
          { label: 'Fahrt nicht erfolgt', value: 'Fahrt nicht erfolgt' },
          { label: 'Fahrer unfreundlich', value: 'Fahrer unfreundlich' },
          { label: 'Sonstiges', value: 'Sonstiges' }
        ]
      },
      {
        label: 'Sortieren nach',
        name: 'sort_by',
        type: 'select',
        multiple: 'multiple',
        icon: 'fas fa-sort',
        values: [
          { label: 'Eingangsdatum (absteigend)', value: 'upload_date_desc' },
          { label: 'Eingangsdatum (aufsteigend)', value: 'upload_date_asc' },
          { label: 'Kategorie (absteigend)', value: 'subject_desc' },
          { label: 'Kategorie (aufsteigend)', value: 'subject_asc' },
          { label: 'Stimmung (absteigend)', value: 'sentiment_desc' },
          { label: 'Stimmung (aufsteigend)', value: 'sentiment_asc' }
        ]
      },
      {
        label: 'Eingangsdatum (von)',
        name: 'date_min',
        type: 'date',
        icon: 'fas fa-calendar-day'
      },
      {
        label: 'Eingangsdatum (bis)',
        name: 'date_max',
        type: 'date',
        icon: 'fas fa-calendar-day'
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
        multiple: 'multiple',
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
        multiple: 'multiple',
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
    response: null,
    text: null,
    config: null,
    counter: 0,
    id: 0,
    done: false
  }
};

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const store = createStore(rootReducer, initialState, composeEnhancers(middleware));

export default store;
