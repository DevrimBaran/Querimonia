/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import ReactTooltip from 'react-tooltip';
import Api from '../utility/Api';

const LABEL_COLORS = {
  Datum: 'lightBlue',
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
      text: '',
      originalLabels: new Map()
    };
  }

  parseText (text) {
    if (!text) return '';
    if (!text.text) return text;
    let html = [];
    let cpos = 0;
    if (Array.isArray(text.entities)) {
      this.prepareEntities(text.entities);
      for (const tag of text.entities) {
        const id = String(Math.floor(Math.random() * (50)));
        // String before next entity
        !text.text.substring(cpos, tag.startIndex) || html.push(<span key={html.length}>{text.text.substring(cpos, tag.startIndex)}</span>);
        // String that is entity
        html.push(<span data-tip data-for={id} key={html.length} className='tag' label={tag.label} style={this.createBackground(tag.label)}>{text.text.substring(tag.startIndex, tag.endIndex)}</span>);
        // Tooltip for that entity
        html.push(this.createTooltip(tag.label, id));
        cpos = tag.endIndex;
      }
    }
    // String from last entity to end of text or complete text if there are no entities
    html.push(<span key={html.length}>{text.text.substring(cpos, text.text.length)}</span>);
    return html;
  };

  // handles overlapping labels
  prepareEntities = (entities) => {
    if (entities.length === 0) return;
    entities.forEach((entity) => {
      const id = String(Math.floor(Math.random() * (50)));
      entity.label = [{
        label: entity.label,
        id: id
      }];
      this.state.originalLabels.set(id, entity);
    });

    let i = 0;
    const compare = (a, b) => { return a.startIndex - b.startIndex || a.endIndex - b.endIndex; };
    while (i < entities.length - 1) {
      entities.sort(compare);
      const entityA = entities[i];
      const entityB = entities[i + 1];

      if (entityA.startIndex === entityB.startIndex) {
        if (entityA.endIndex === entityB.endIndex) {
          entityA.label = [...entityA.label, ...entityB.label];
          entities.splice(i + 1, 1);
        } else {
          entityA.label = [...entityA.label, ...entityB.label];
          entityB.startIndex = entityA.endIndex;
        }
      } else if (entityA.endIndex === entityB.endIndex) {
        entityB.label = [...entityA.label, ...entityB.label];
        entityA.endIndex = entityB.startIndex;
      } else if (entityA.endIndex > entityB.endIndex) {
        entities.push({
          label: entityA.label,
          startIndex: entityB.endIndex,
          endIndex: entityA.endIndex
        });
        entityB.label = [...entityA.label, ...entityB.label];
        entityA.endIndex = entityB.startIndex;
      } else if (entityA.endIndex < entityB.endIndex && entityA.endIndex > entityB.startIndex) {
        entities.push({
          label: [...entityA.label, ...entityB.label],
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
  createBackground = (labels) => {
    const individualHeightPercentage = 100 / labels.length;
    let linearGradient = '';
    labels.forEach((label, i) => {
      const color = LABEL_COLORS[label.label] || LABEL_COLORS['default'];
      linearGradient += color +
        (i !== 0 ? ' ' + String(individualHeightPercentage * (i)) + '%,' : ',') +
        color +
        ' ' +
        String(individualHeightPercentage * (i + 1)) +
        '%' +
        (i !== labels.length - 1 ? ',' : '');
    });
    return { cursor: 'pointer', backgroundImage: 'linear-gradient(' + linearGradient + ')' };
  };

  createTooltip = (labels, id) => {
    let labelArray = labels.map((label, i) => {
      return <div key={i}><span className='dot' style={{ backgroundColor: LABEL_COLORS[label.label] || LABEL_COLORS['default'],
        height: '10px',
        width: '10px',
        borderRadius: '50%',
        display: 'inline-block',
        marginLeft: '5px' }}> </span> {label.label} {this.props.editable ? <button onClick={this.deleteEntity(label.id)}>Löschen</button> : null} <br /> </div>;
    });
    return <ReactTooltip effect='solid' event='' globalEventOff='click' key={labels} id={id} aria-haspopup='true'>
      {
        labelArray
      }
    </ReactTooltip>;
  };

  deleteEntity = (id) => {
    const originalLabel = this.state.originalLabels.get(id);
    let query = {};
    query['label'] = originalLabel.label;
    query['start'] = originalLabel.startIndex;
    query['end'] = originalLabel.endIndex;
    query['extractor'] = originalLabel.name;
    Api.delete('api/complaints/' + this.state.text.complaintId + '/entities', query);
  };

  componentWillUpdate = (props) => {
    if (props.text !== this.props.text) {
      this.setState({
        text: this.parseText(JSON.parse(JSON.stringify(props.text)))
      });
    }
  };

  componentDidMount () {
    this.setState({
      text: this.parseText(JSON.parse(JSON.stringify(this.props.text)))
    });
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
