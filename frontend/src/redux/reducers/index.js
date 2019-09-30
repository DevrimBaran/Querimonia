import calculateEntities from '../../utility/calculateEntities';

const defaults = {
  components: {
    id: 0,
    priority: 0,
    name: '',
    texts: [],
    actions: [],
    rulesXml: ''
  },
  config: {
    id: 0,
    name: '',
    extractors: [],
    classifiers: [],
    sentimentAnalyzer: {
      name: 'DEFAULT',
      type: 'NONE'
    },
    emotionAnalyzer: {
      name: 'DEFAULT',
      type: 'NONE'
    }
  }
};

function filter (state = [], action, endpoint) {
  if (endpoint !== action.endpoint) return state;
  switch (action.type) {
    case 'FILTER_CHANGE': {
      return state.map((input) => {
        if (input.name === action.name) {
          return {
            ...input,
            value: action.value
          };
        }
        return input;
      });
    }
    default:
      return state;
  }
}

function pagination (state = { count: 0, limit: 10, max: 0 }, action, endpoint) {
  if (endpoint !== action.endpoint) return state;
  switch (action.type) {
    case 'PAGINATION_CHANGE': {
      if (action.name === 'count') {
        return {
          ...state,
          count: action.value,
          page: 0
        };
      } else {
        return {
          ...state,
          [action.name]: action.value
        };
      }
    }
    default:
      return state;
  }
}

function data (state = { byId: {}, active: false, ids: [], fetching: false }, action, endpoint) {
  if (endpoint !== action.endpoint) return state;
  switch (action.type) {
    case 'FETCH_START': {
      return {
        ...state,
        fetching: true,
        active: false,
        byId: {},
        ids: []
      };
    }
    case 'SET_ACTIVE': {
      if (action.id === 0) {
        return {
          ...state,
          active: {
            ...defaults[action.endpoint]
          }
        };
      }
      return {
        ...state,
        active: {
          ...state.byId[action.id]
        }
      };
    }
    case 'MODIFY_ACTIVE': {
      return {
        ...state,
        active: {
          ...state.active,
          ...action.data
        }
      };
    }
    case 'SAVE_START': {
      return {
        ...state,
        active: {
          ...state.active,
          saving: true
        }
      };
    }
    case 'SAVE_END': {
      return {
        ...state,
        active: {
          ...action.data,
          saving: false
        }
      };
    }
    case 'FETCH_END': {
      return {
        ...state,
        fetching: false,
        active: false,
        byId: action.data.reduce((obj, item) => { obj[item.id] = item; return obj; }, {}),
        ids: action.data.map(item => item.id)
      };
    }
    case 'REMOVE_START': {
      return {
        ...state,
        fetching: true,
        active: false
      };
    }
    case 'REMOVE_END': {
      return {
        ...state,
        fetching: false,
        active: false,
        ids: state.ids.filter(id => id !== action.id)
      };
    }
    case 'UPDATE_SINGLE': {
      if (!action.data) return state;
      return {
        ...state,
        fetching: false,
        active: state.active && {
          ...state.active,
          state: action.data.state
        },
        byId: {
          ...state.byId,
          [action.data.id]: action.data
        }
      };
    }
    case 'UPDATE_STATE': {
      if (!state.byId[action.id]) return state;
      return {
        ...state,
        active: state.active && {
          ...state.active,
          state: state.active.id === action.id ? action.state : state.active.id
        },
        byId: {
          ...state.byId,
          [action.id]: { ...state.byId[action.id], state: action.state }
        }
      };
    }
    case 'ACTIVATE_CONFIG': {
      return {
        ...state,
        byId: {
          ...state.byId,
          [action.oldId]: { ...state.byId[action.oldId], active: false },
          [action.id]: { ...state.byId[action.id], active: true }
        }
      };
    }
    default:
      return state;
  }
}
function currentConfig (state = {}, action) {
  switch (action.type) {
    case 'CURRENT_CONFIG': {
      return {
        ...action.data
      };
    }
    default: {
      return state;
    }
  }
}
function allExtractors (state = {}, action) {
  switch (action.type) {
    case 'INIT_EXTRACTORS': {
      return {
        NONE: [],
        KIKUKO_TOOL: action.data.tool,
        KIKUKO_PIPELINE: action.data.pipeline,
        KIKUKO_DOMAIN: action.data.domain
      };
    }
    default: {
      return state;
    }
  }
}
function complaintStuff (state = {}, action) {
  switch (action.type) {
    case 'FETCH_SINGLE_COMPLAINT_START': {
      return {
        entities: {
          byId: {},
          ids: [],
          calculated: []
        },
        components: [],
        combinations: [],
        log: [],
        response: null,
        text: null,
        config: null,
        id: action.id,
        done: false
      };
    }
    case 'FETCH_SINGLE_COMPLAINT_END': {
      return {
        ...state,
        entities: {
          byId: action.entities.reduce ? action.entities.reduce((obj, item) => { obj[item.id] = item; return obj; }, {}) : {},
          ids: action.entities.map ? action.entities.map(item => item.id) : [],
          calculated: action.entities.map ? calculateEntities(action.entities) : []
        },
        components: action.components,
        response: action.response,
        combinations: action.combinations,
        log: action.log,
        text: action.text,
        done: true
      };
    }
    case 'REFRESH_RESPONSES_START': {
      return {
        ...state,
        done: false
      };
    }
    case 'REFRESH_RESPONSES_END': {
      return {
        ...state,
        components: action.components,
        done: true
      };
    }
    case 'USE_COMPONENT': {
      return {
        ...state,
        components: state.components.map(c => c.id === action.id ? { ...c, used: true } : c)
      };
    }
    case 'MODIFY_ENTITY': {
      return {
        ...state,
        entities: {
          byId: action.data.reduce((obj, item) => { obj[item.id] = item; return obj; }, {}),
          ids: action.data.map(item => item.id),
          calculated: calculateEntities(action.data)
        }
      };
    }
    default: {
      return state;
    }
  }
}

function fetchable (state = { data: {}, filter: [], pagination: {} }, action, endpoint) {
  return {
    data: data(state.data, action, endpoint),
    filter: filter(state.filter, action, endpoint),
    pagination: pagination(state.pagination, action, endpoint)
  };
}

function login (state, action) {
  switch (action.type) {
    case 'LOGIN': {
      return {
        access: parseInt(action.access)
      };
    }
    case 'LOGOUT': {
      return {
        access: -1
      };
    }
    default: {
      return state;
    }
  }
}

const rootReducer = function (state, action) {
  return {
    login: login(state.login, action),
    complaints: fetchable(state.complaints, action, 'complaints'),
    config: fetchable(state.config, action, 'config'),
    components: fetchable(state.components, action, 'components'),
    currentConfig: currentConfig(state.currentConfig, action),
    allExtractors: allExtractors(state.allExtractors, action),
    complaintStuff: complaintStuff(state.complaintStuff, action)
  };
};

export default rootReducer;
