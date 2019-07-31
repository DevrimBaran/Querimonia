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
          obj[key] = this.templateToDefault(template[key].children);
        } else if (template[key].type === 'number') {
          obj[key] = 0;
        } else {
          obj[key] = '';
        }
        return obj;
      }, {});
      console.log(newData);
      return newData;
    } else {
      return t.type === 'number' ? 0 : '';
    }
  }
  add = () => {
    const newData = this.props.data.slice();
    newData.push(this.templateToDefault(this.props.template.children));
    console.log('add', newData);
    if (this.props.deepChange) {
      this.props.deepChange(this.props.name, newData);
    } else {
      this.props.save && this.props.save(newData);
    }
  }
  remove = (index) => () => {
    const newData = this.props.data.filter((e, i) => i !== index);
    console.log('remove', newData);
    if (this.props.deepChange) {
      this.props.deepChange(this.props.name, newData);
    } else {
      this.props.save && this.props.save(newData);
    }
  }
  onChange = (e) => {
    console.log('onChange', e.name, e.value);
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
    console.log('deepChange', key, value, newData);
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
        return (<Input type={template.type} key={key} label={template.label} {...template.attributes} value={value} name={key} onChange={this.onChange} />);
      }
    }
  }
  renderIndex = (value, key) => {
    const template = this.props.template.children;
    if (template) {
      if (template.children) {
        return (
          <span key={key}>
            <DeepObject data={value} template={template} name={key} key={key} deepChange={this.deepChange} />
            <i className='fa fa-trash' onClick={this.remove(key)} />
          </span>
        );
      } else {
        console.log(key, value, template);
        return (
          <span key={key}>
            <Input type={template.type} key={key} label={template.label} {...template.attributes} value={value} name={key} onChange={this.onChange} />
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
          <div>
            {data.map(this.renderIndex)}
            <i className='fa fa-plus' onClick={this.add} />
          </div>
        );
      } else if (template.type === 'object') {
        return Object.keys(data).map(this.renderKey);
      }
    }
  }
  render () {
    return (
      <div>
        {this.props.template && this.props.template.label && (<React.Fragment><strong>{this.props.template.label.replace(/\$i/g, (Number(this.props.name) + 1))}</strong><br /></React.Fragment>)}
        {this.renderData(this.props.data)}
      </div>
    );
  }
}

export default DeepObject;
