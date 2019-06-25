/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import './TaggedText.scss';
import ReactTooltip from 'react-tooltip';

const LABEL_COLORS = {
  Datum: 'blue',
  Upload_Datum: 'red',
  Name: 'violet',
  Geldbetrag: 'turquoise',
  Bushaltestelle: 'orange',
  Vorgangsnummer: 'yellow',
  Ortsname: 'brown',
  Linie: 'pink',
  default: 'green'
};

let createdTooltips = [];

class TaggedText extends Component {
  constructor (props) {
    super(props);

    this.state = {
      text: this.parseText(JSON.parse(JSON.stringify(props.text)))
    };
  }

  parseText (text) {
    if (!text) return '';
    if (!text.text) return text;
    let p = [];
    let cpos = 0;
    if (Array.isArray(text.entities)) {
      this.prepareEntities(text.entities);
      for (const tag of text.entities) {
        !text.text.substring(cpos, tag.start) || p.push(<span key={p.length}>{text.text.substring(cpos, tag.start)}</span>);
        p.push(<span data-tip data-for={tag.label} key={p.length} className='tag' label={tag.label} style={this.createBackground(tag.label)}>{text.text.substring(tag.start, tag.end)}</span>);
        p.push(this.createTooltip(tag.label));
        cpos = tag.end;
      }
    }
    p.push(<span key={p.length}>{text.text.substring(cpos, text.text.length)}</span>);
    return p;
  };

  // handles overlapping labels
  prepareEntities = (entities) => {
    if (entities.length === 0) return;

    let i = 0;
    const compare = (a, b) => { return a.start - b.start || a.end - b.end; };
    while (i < entities.length - 1) {
      entities.sort(compare);
      const entityA = entities[i];
      const entityB = entities[i + 1];

      if (entityA.start === entityB.start) {
        if (entityA.end === entityB.end) {
          if (!entityA.label.includes(entityB.label)) {
            entityA.label += ' ' + entityB.label;
          }
          entities.splice(i + 1, 1);
        } else {
          if (!entityA.label.includes(entityB.label)) {
            entityA.label += ' ' + entityB.label;
          }
          entityB.start = entityA.end;
        }
      } else if (entityA.end === entityB.end) {
        if (!entityB.label.includes(entityA.label)) {
          entityB.label += ' ' + entityA.label;
        }
        entityA.end = entityB.start;
      } else if (entityA.end > entityB.end) {
        entities.push({
          label: entityA.label,
          start: entityB.end,
          end: entityA.end
        });
        if (!entityB.label.includes(entityA.label)) {
          entityB.label += ' ' + entityA.label;
        }
        entityA.end = entityB.start;
      } else if (entityA.end < entityB.end && entityA.end > entityB.start) {
        entities.push({
          label: !entityA.label.includes(entityB.label) ? entityA.label + ' ' + entityB.label : entityA.label,
          start: entityB.start,
          end: entityA.end
        });
        entityA.end = entityB.start;
        entityB.start = entityA.end;
      } else {
        i++;
      }
    }
    return entities;
  };

  // calculates the proper background colors for the given labels
  createBackground = (label) => {
    const labels = label.split(' ');
    const individualHeightPercentage = 100 / labels.length;
    let linearGradient = '';
    labels.forEach((label, i) => {
      const color = LABEL_COLORS[label] || LABEL_COLORS['default'];
      linearGradient += color +
        (i !== 0 ? ' ' + String(individualHeightPercentage * (i)) + '%,' : ',') +
        color +
        ' ' +
        String(individualHeightPercentage * (i + 1)) +
        '%' +
        (i !== labels.length - 1 ? ',' : '');
    });
    return { backgroundImage: 'linear-gradient(' + linearGradient + ')' };
  };

  createTooltip = (labels) => {
    let labelArray = labels.split(' ').map((label, i) => {
      return <div key={i}>{label}<span className='dot' style={{ backgroundColor: LABEL_COLORS[label] || LABEL_COLORS['default'] }}> </span> <br /> </div>;
    });
    if (createdTooltips.includes(labels)) return;
    createdTooltips.push(labels);
    return <ReactTooltip key={labels} id={labels} aria-haspopup='true'>
      {
        labelArray
      }
    </ReactTooltip>;
  };

  componentWillUpdate = (props) => {
    if (props.text !== this.props.text) {
      this.setState({
        text: this.parseText(props.text)
      });
    }
  };

  componentWillUnmount = () => {
    createdTooltips = [];
  }

  render () {
    return (
      <span className='tagged-text'>
        {this.state.text}
      </span>
    );
  };
}

export default TaggedText;
