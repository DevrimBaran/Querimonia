/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

import { getColor, getGradient } from '../utility/colors';

import Input from './Input';

class FillerText extends Component {
  constructor (props) {
    super(props);
    this.splitTexts();
    this.values = {};
    this.reference = React.createRef();
    this.state = {
      values: {}
    };
  }
  onChange = (e) => {
    if (e) {
      this.setState({ [e.name]: e.value });
    }
    this.props.onChange && this.props.onChange({
      target: this.reference.current,
      name: '',
      value: this.texts[this.props.selected % this.texts.length].map((t, i) => i % 2 === 0 ? t : this.values[t]).join('')
    });
  }
  entitiesWithLabel = (label) => {
    console.log(label);
    return this.props.entities.ids.map(id => this.props.entities.byId[id]).filter(entity => entity.label === label);
  }
  getInput = (label) => {
    let entities = this.entitiesWithLabel(label.split('#')[0]);
    let values = null;
    if (!this.state[label]) {
      switch (entities.length) {
        case 0: {
          this.values[label] = 'Erstellen Sie eine Entität';
          break;
        }
        case 1: {
          this.values[label] = entities[0].value;
          break;
        }
        default: {
          const preferred = entities.find(e => e.preferred);
          this.values[label] = preferred ? preferred.value : entities[0].value;
        }
      }
    } else {
      this.values[label] = this.state[label];
    }
    switch (entities.length) {
      case 0: {
        this.values[label] = 'Erstellen Sie eine Entität';
        values = ['Erstellen Sie eine Entität'];
        break;
      }
      case 1: {
        const color = getColor(entities[0], this.props.config);
        this.values[label] = entities[0].value;
        values = [{
          label: entities[0].value,
          value: entities[0].value,
          style: {
            backgroundColor: color.background,
            color: color.color
          }
        }];
        break;
      }
      default: {
        const preferred = entities.find(e => e.preferred);
        const labels = {};
        this.values[label] = preferred ? preferred.value : entities[0].value;
        values = entities.map(e => {
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
    const gradient = getGradient(entities, this.props.config); ;
    return <Input
      style={{
        color: gradient.color,
        backgroundImage: gradient.background
      }}
      label={label}
      inline
      onChange={this.onChange}
      name={label}
      key={label}
      type='select'
      required
      value={this.values[label]}
      values={values}
    />;
  }
  splitTexts = () => {
    this.texts = this.props.texts.map(t => t.split(/\$\{(.+?)\}/));
  }
  componentDidMount = () => {
    this.onChange();
  }
  render () {
    const { texts, selected, entities, dispatch, onChange, ...passThrough } = { ...this.props };
    const render = this.texts[selected % this.texts.length].map((t, i) => {
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
