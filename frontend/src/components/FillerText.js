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
    this.text = [];
    this.reference = React.createRef();
    this.state = {
      text: ''
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
    const name = e.target.getAttribute('index');
    this.text[parseInt(name)] = e.value;
    this.props.onChange && this.props.onChange({
      target: this.reference.current,
      name: name,
      value: this.text.join('')
    });
  }
  parseText = () => {
    const text = this.props.texts[this.props.selected % this.props.texts.length];
    const entities = this.props.entities;
    const parse = text.split(/\$\{(\w+)\}/);
    const labels = parse.filter((str, i) => i % 2 === 1).reduce((obj, label) => { obj[label] = []; return obj; }, {});
    entities.ids.map(id => (entities.byId[id])).forEach(entity => labels[entity.label] && labels[entity.label].push(entity));
    this.text = parse;
    return parse.map((str, i) => {
      if (i % 2 === 0) {
        return <span key={i}>{str}</span>;
      }
      return labels[str] && (
        <Input
          style={this.getColorStyles(labels[str])}
          onChange={this.onChange}
          index={i}
          key={i}
          type='select'
          required
          defaultValue={(labels[str].length === 1)
            ? labels[str][0].value
            : (
              (labels[str].find(e => e.preferred) ||
                { value: str }).value
            )}
          values={[{ label: str, value: str, style: { color: '#202124' } }].concat(labels[str].map(e => ({
            label: e.value,
            value: e.value,
            style: {
              backgroundColor: e.color || '#cccccc',
              color: Math.abs(this.getLuminance('#202124') - this.getLuminance(e.color || '#cccccc')) > 0.2
                ? '#202124'
                : '#ffffff'
            }
          })))
          }
        />
      );
    });
  };
  componentDidUpdate (prevProps) {
    // eslint-disable-next-line
    if (prevProps.selected !== this.props.selected) this.setState({ text: this.parseText() });
  }
  componentDidMount = () => {
    this.setState({ text: this.parseText() });
    this.props.onChange && this.props.onChange({
      target: this.reference.current,
      name: '',
      value: this.text.join('')
    });
  }
  render () {
    const { texts, selected, entities, dispatch, onChange, ...passThrough } = { ...this.props };
    return (
      <span ref={this.reference} className='fillerText' {...passThrough}>{this.state.text}</span>
    );
  }
}

const mapStateToProps = (state, props) => {
  return {
    entities: state.complaintStuff.entities
  };
};

export default connect(mapStateToProps)(FillerText);
