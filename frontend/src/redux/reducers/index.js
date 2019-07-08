function filter (state = [], action, endpoint) {
  console.log(endpoint, action.endpoint);
  if (endpoint !== action.endpoint) return state;
  switch (action.type) {
    case 'FILTER_CHANGE': {
      return state[action.endpoint].filter.map((input) => {
        if (input.name === action.name) {
          return {
            label: input.label,
            name: input.name,
            type: input.type,
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
  console.log(endpoint, action.endpoint);
  if (endpoint !== action.endpoint) return state;
  switch (action.type) {
    case 'PAGINATION_CHANGE': {
      return {
        ...state,
        [action.name]: action.value
      };
    }
    default:
      return state;
  }
};

function data (state = { data: {}, ids: [], idKey: 'id', fetching: false }, action, endpoint) {
  console.log(endpoint, action.endpoint);
  if (endpoint !== action.endpoint) return state;
  switch (action.type) {
    case 'FETCH_START': {
      return {
        ...state,
        fetching: true,
        byId: {},
        ids: []
      };
    }
    case 'FETCH_END': {
      console.log(action.idKey);
      return {
        ...state,
        fetching: false,
        byId: action.data.reduce((obj, item) => { obj[item[state.idKey]] = item; return obj; }, {}),
        ids: action.data.map(item => item[state.idKey])
      };
    }
    default:
      return state;
  }
};

function fetchable (state = { data: {}, filter: [], pagination: {} }, action, endpoint) {
  return { data: data(state.data, action, endpoint), filter: filter(state.filter, action, endpoint), pagination: pagination(state.pagination, action, endpoint) };
}

const rootReducer = function (state, action) {
  return { complaints: fetchable(state.complaints, action, 'complaints'), templates: fetchable(state.templates, action, 'templates') };
};

export default rootReducer;
