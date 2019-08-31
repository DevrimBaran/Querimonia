/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

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
  getLuminance = (color) => {
    const rgb = color && color.match(/#(..)(..)(..)/);
    if (rgb) {
      return (0.299 * parseInt(rgb[1], 16) + 0.587 * parseInt(rgb[2], 16) + 0.114 * parseInt(rgb[3], 16)) / 255;
    } else {
      return 0;
    }
  };
  minLuminance = (min, entity) => {
    return Math.min(min, this.getLuminance(entity.color));
  }
  getGradient = (entity, i, entities) => {
    const pers = 100 / entities.length;
    const color = entity.color || '#cccccc';
    return `${color} ${pers * i}%, ${color} ${pers * (i + 1)}%`;
  }
  getColorStyles = (entities) => {
    let gradient = entities.map(this.getGradient, '').join(', ');
    let luminance = entities.reduce(this.minLuminance, 256);
    let textColor = Math.abs(this.getLuminance('#202124') - luminance) > 0.2
      ? '#202124'
      : '#ffffff';
    return {
      color: textColor,
      backgroundImage: `linear-gradient(${gradient})`
    };
  };
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
    return this.props.entities.ids.map(id => this.props.entities.byId[id]).filter(entity => entity.label === label);
  }
  getInput = (label) => {
    const entities = this.entitiesWithLabel(label.split(':')[0]);
    if (!this.state[label]) {
      switch (entities.length) {
        case 0: {
          // TODO add to values / modify Input to include value in values?
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
    return <Input
      style={this.getColorStyles(entities)}
      label={label}
      inline
      onChange={this.onChange}
      name={label}
      key={label}
      type='select'
      required
      value={this.values[label]}
      values={entities.map(e => ({
        label: e.value,
        value: e.value,
        style: {
          backgroundColor: e.color || '#cccccc',
          color: Math.abs(this.getLuminance('#202124') - this.getLuminance(e.color || '#cccccc')) > 0.2
            ? '#202124'
            : '#ffffff'
        }
      }))
      }
    />;
  }
  splitTexts = () => {
    this.texts = this.props.texts.map(t => t.split(/\$\{(\w+)\}/));
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
    entities: state.complaintStuff.entities
  };
};

export default connect(mapStateToProps)(FillerText);
