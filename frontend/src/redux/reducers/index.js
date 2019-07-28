const defaults = {
  templates: {
    id: 0,
    priority: 0,
    componentName: '',
    templateTexts: [],
    rulesXml: ''
  },
  config: {
    id: 0,
    name: '',
    extractors: [],
    classifier: {
      name: 'DEFAULT',
      type: 'NONE'
    },
    sentimentAnalyzer: {
      name: 'DEFAULT',
      type: 'NONE'
    }
  },
  actions: {
    id: 0,
    name: '',
    actionCode: '',
    rulesXml: '',
    parameters: {}
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
};

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
          ...state.active,
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
    default:
      return state;
  }
};
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

function fetchable (state = { data: {}, filter: [], pagination: {} }, action, endpoint) {
  return {
    data: data(state.data, action, endpoint),
    filter: filter(state.filter, action, endpoint),
    pagination: pagination(state.pagination, action, endpoint)
  };
}

const rootReducer = function (state, action) {
  return {
    complaints: fetchable(state.complaints, action, 'complaints'),
    actions: fetchable(state.actions, action, 'actions'),
    config: fetchable(state.config, action, 'config'),
    templates: fetchable(state.templates, action, 'templates'),
    currentConfig: currentConfig(state.currentConfig, action)
  };
};

export default rootReducer;
