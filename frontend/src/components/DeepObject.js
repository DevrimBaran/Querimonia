/**
 * This Component renders a nested json object based on a template object.
 * changes are propagated to the root.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Input from './Input';

class DeepObject extends Component {
  isArray = () => {
    return Array.isArray(this.props.data);
  }
  templateToDefault = (t) => {
    if (t.children) {
      const template = t.children;
      const newData = Object.keys(template).reduce((obj, key) => {
        if (template[key].default) {
          obj[key] = template[key].default;
        } else if (template[key].type === 'array') {
          obj[key] = [];
        } else if (template[key].type === 'object') {
          obj[key] = this.templateToDefault(template[key]);
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
  }
  add = () => {
    const newData = this.props.data.slice();
    newData.push(this.templateToDefault(this.props.template.children));
    if (this.props.deepChange) {
      this.props.deepChange(this.props.name, newData);
    } else {
      this.props.save && this.props.save(newData);
    }
  }
  remove = (index) => () => {
    const newData = this.props.data.filter((e, i) => i !== index);
    if (this.props.deepChange) {
      this.props.deepChange(this.props.name, newData);
    } else {
      this.props.save && this.props.save(newData);
    }
  }
  onChange = (e) => {
    this.deepChange(e.name, e.value);
  }
  deepChange = (key, value) => {
    const newData = this.isArray()
      ? (
        this.props.data.map((obj, i) => {
          return Number(i) === Number(key) ? value : obj;
        })
      ) : (
        {
          ...this.props.data,
          [key]: value
        }
      );
    if (this.props.deepChange) {
      this.props.deepChange(this.props.name, newData);
    } else {
      this.props.save && this.props.save(newData);
    }
  }
  renderKey = (key, index) => {
    const value = this.props.data[key];
    const template = this.props.template.children[key];
    if (template) {
      if (template.children) {
        return (<DeepObject data={value} template={template} name={key} key={key} deepChange={this.deepChange} />);
      } else {
        return (<Input type={template.type} key={key} label={template.label} {...template.attributes} value2={template.attributes && template.attributes.name2 && this.props.data[template.attributes.name2]} value={value} name={key} onChange={this.onChange} />);
      }
    }
  }
  renderIndex = (value, key) => {
    const template = this.props.template.children;
    if (template) {
      if (template.children) {
        return (
          <span className='deep-array' key={key}>
            <DeepObject data={value} template={template} name={key} key={key} deepChange={this.deepChange} />
            <i className='fa fa-trash' onClick={this.remove(key)} />
          </span>
        );
      } else {
        return (
          <span className='deep-array' key={key}>
            <Input type={template.type} key={key} label={template.label.replace(/\$i/g, (Number(key) + 1))} {...template.attributes} value={value} name={key} onChange={this.onChange} />
            <i className='fa fa-trash' onClick={this.remove(key)} />
          </span>
        );
      }
    }
  }
  renderData = (data) => {
    const template = this.props.template;
    if (template) {
      if (template.type === 'array') {
        return (
          <React.Fragment>
            {data.map(this.renderIndex)}
            <i className='fa fa-plus' onClick={this.add} />
          </React.Fragment>
        );
      } else if (template.type === 'object') {
        let keys = Object.keys(data);
        if (this.props.filter) {
          keys = keys.filter(this.props.filter);
        }
        return (keys.map(this.renderKey));
      }
    }
  }
  render () {
    if (this.props.template) {
      if (this.isArray()) {
        return (
          <div className='deep-object'>
            {this.renderData(this.props.data)}
          </div>
        );
      } else {
        return (
          <div className='object'>
            {this.renderData(this.props.data)}
          </div>
        );
      }
    } else {
      return (this.renderData(this.props.data));
    }
  }
}

export default DeepObject;
