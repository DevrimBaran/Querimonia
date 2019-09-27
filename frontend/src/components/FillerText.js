/**
 * Text with selects for responses
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

import { getColor } from '../utility/colors';

import Input from './Input';

class FillerText extends Component {
  constructor (props) {
    super(props);
    this.splitTexts();
    this.values = {};
    this.reference = React.createRef();
    this.state = {};
  }
  onChange = (e) => {
    if (e && !e.fake) {
      this.setState({ [e.name]: e.value });
      this.values[e.name] = e.value;
    }
    this.props.onChange && this.props.onChange({
      target: this.reference.current,
      name: '',
      value: this.texts[this.props.selected % this.texts.length].map((t, i) => i % 2 === 0 ? t : this.values[t]).join('')
    });
  }
  entitiesWithLabel = (label) => {
    return this.props.entities.ids.map(id => this.props.entities.byId[id]).filter(entity => entity.label === label);
  }
  getOptions = (entities) => {
    if (entities.length === 0) {
      return [{
        label: 'Erstellen Sie eine Entität',
        value: 'Erstellen Sie eine Entität',
        style: {
          backgroundColor: '#cccccc',
          color: '#202124'
        }
      }];
    } else {
      const labels = {};
      return entities.map(e => {
        const color = getColor(e, this.props.config);
        return {
          label: e.value,
          value: e.value,
          style: {
            backgroundColor: color.background,
            color: color.color
          }
        };
      }).filter(v => {
        if (labels[v.value]) return false;
        labels[v.value] = true;
        return true;
      }).sort((a, b) => a.value <= b.value ? -1 : 1);
    }
  }
  getInput = (label) => {
    const entities = this.entitiesWithLabel(label.split('#')[0]);
    const options = this.getOptions(entities);
    let value = this.state[label];
    if (!value) {
      const preferred = entities.find(e => e.preferred);
      value = preferred ? preferred.value : options[0].value;
    }
    this.values[label] = value;
    return <Input
      style={options[0].style}
      label={label.replace(/#(.*)$/, ' ($1)')}
      inline
      onChange={this.onChange}
      name={label}
      key={label}
      type='select'
      required
      value={value}
      values={options}
    />;
  }
  splitTexts = () => {
    this.texts = this.props.texts.map(t => t.split(/\$\{(.+?)\}/));
  }
  componentDidMount = () => {
    if (this.props.texts.length !== 0) {
      this.onChange();
    }
  }
  render () {
    const { texts, selected, entities, dispatch, onChange, ...passThrough } = { ...this.props };
    if (texts.length === 0) {
      return <React.Fragment />;
    }
    const text = this.texts[selected % this.texts.length] || [];
    const render = text.map((t, i) => {
      return (i % 2 === 0) ? (
        <span key={i}>{t}</span>
      ) : (
        this.getInput(t)
      );
    });
    return (
      <span ref={this.reference} className='fillerText' {...passThrough}>{render}</span>
    );
  }
}

const mapStateToProps = (state, props) => {
  return {
    entities: state.complaintStuff.entities,
    config: state.complaintStuff.config
  };
};

export default connect(mapStateToProps)(FillerText);
