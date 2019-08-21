import config from './config';
import components from './components';
import complaints from './complaints';

const templateToDefault = (t) => {
  if (t.children) {
    const template = t.children;
    const newData = Object.keys(template).reduce((obj, key) => {
      if (template[key].default) {
        obj[key] = template[key].default;
      } else if (template[key].type === 'array') {
        obj[key] = [];
      } else if (template[key].type === 'object') {
        obj[key] = this.templateToDefault(template[key].children);
      } else if (template[key].type === 'number') {
        obj[key] = 0;
      } else {
        obj[key] = '';
      }
      return obj;
    }, {});
    return newData;
  } else {
    return t.type === 'number' ? 0 : '';
  }
};

export const endpoints = {
  config: {
    template: config,
    default: templateToDefault(config)
  },
  components: {
    template: components,
    default: templateToDefault(components)
  },
  complaints: {
    template: complaints,
    default: templateToDefault(complaints)
  }
};
