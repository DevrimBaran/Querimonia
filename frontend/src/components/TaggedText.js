/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import ReactTooltip from 'react-tooltip';

const LABEL_COLORS = {
  Datum: 'blue',
  Eingangsdatum: 'red',
  Name: 'violet',
  Geldbetrag: 'turquoise',
  Bushaltestelle: 'orange',
  Vorgangsnummer: 'yellow',
  Ortsname: 'brown',
  Linie: 'pink',
  default: 'green'
};

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
        !text.text.substring(cpos, tag.startIndex) || p.push(<span key={p.length}>{text.text.substring(cpos, tag.startIndex)}</span>);
        p.push(<span data-tip data-for={tag.label} key={p.length} className='tag' label={tag.label} style={this.createBackground(tag.label)}>{text.text.substring(tag.startIndex, tag.endIndex)}</span>);
        p.push(this.createTooltip(tag.label));
        cpos = tag.endIndex;
      }
    }
    p.push(<span key={p.length}>{text.text.substring(cpos, text.text.length)}</span>);
    return p;
  };

  // handles overlapping labels
  prepareEntities = (entities) => {
    if (entities.length === 0) return;

    let i = 0;
    const compare = (a, b) => { return a.startIndex - b.startIndex || a.endIndex - b.endIndex; };
    while (i < entities.length - 1) {
      entities.sort(compare);
      const entityA = entities[i];
      const entityB = entities[i + 1];

      if (entityA.startIndex === entityB.startIndex) {
        if (entityA.endIndex === entityB.endIndex) {
          if (!entityA.label.includes(entityB.label)) {
            entityA.label += ' ' + entityB.label;
          }
          entities.splice(i + 1, 1);
        } else {
          if (!entityA.label.includes(entityB.label)) {
            entityA.label += ' ' + entityB.label;
          }
          entityB.startIndex = entityA.endIndex;
        }
      } else if (entityA.endIndex === entityB.endIndex) {
        if (!entityB.label.includes(entityA.label)) {
          entityB.label += ' ' + entityA.label;
        }
        entityA.endIndex = entityB.startIndex;
      } else if (entityA.endIndex > entityB.endIndex) {
        entities.push({
          label: entityA.label,
          startIndex: entityB.endIndex,
          endIndex: entityA.endIndex
        });
        if (!entityB.label.includes(entityA.label)) {
          entityB.label += ' ' + entityA.label;
        }
        entityA.endIndex = entityB.startIndex;
      } else if (entityA.endIndex < entityB.endIndex && entityA.endIndex > entityB.startIndex) {
        entities.push({
          label: !entityA.label.includes(entityB.label) ? entityA.label + ' ' + entityB.label : entityA.label,
          startIndex: entityB.startIndex,
          endIndex: entityA.endIndex
        });
        entityA.endIndex = entityB.startIndex;
        entityB.startIndex = entityA.endIndex;
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
      return <div key={i}><span className='dot' style={{ backgroundColor: LABEL_COLORS[label] || LABEL_COLORS['default'],
        height: '10px',
        width: '10px',
        borderRadius: '50%',
        display: 'inline-block',
        marginLeft: '5px' }}> </span> {label} {this.props.editable ? <button>Löschen</button> : ''} <br /> </div>;
    });
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

  render () {
    return (
      <span className='tagged-text'>
        {this.state.text}
      </span>
    );
  };
}

export default TaggedText;
