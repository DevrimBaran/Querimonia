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
  Datum: 'green',
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
      taggedText: '',
      originalLabels: new Map(),
      id: null
    };
  }

  parseText (taggedText) {
    if (!taggedText) return '';
    if (!taggedText.text) return taggedText;
    let html = [];
    let cpos = 0;
    if (Array.isArray(taggedText.entities)) {
      this.prepareEntities(taggedText.entities);
      for (const tag of taggedText.entities) {
        // TODO: uuid
        const id = String(Math.floor(Math.random() * (50)));
        // String before next entity
        !taggedText.text.substring(cpos, tag.start) || html.push(<span key={html.length} data-key={html.length}>{taggedText.text.substring(cpos, tag.start)}</span>);
        // String that is entity
        html.push(<span data-tip data-for={id} key={html.length} data-key={html.length} className='tag' style={this.createBackground(tag.label)}>{taggedText.text.substring(tag.start, tag.end)}</span>);
        // Tooltip for that entity
        html.push(this.createTooltip(tag.label, id));
        cpos = tag.end;
      }
    }
    // String from last entity to end of text or complete text if there are no entities
    html.push(<span key={html.length} data-key={html.length}>{taggedText.text.substring(cpos, taggedText.text.length)}</span>);
    return html;
  };

  // handles overlapping labels
  prepareEntities = (entities) => {
    if (entities.length === 0) return;
    entities.forEach((entity) => {
      // TODO: uuid
      const id = String(Math.floor(Math.random() * (50)));
      this.state.originalLabels.set(id, JSON.parse(JSON.stringify(entity)));
      entity.label = [{
        label: entity.label,
        id: id
      }];
    });

    let i = 0;
    const compare = (a, b) => { return a.start - b.start || a.end - b.end; };
    while (i < entities.length - 1) {
      entities.sort(compare);
      const entityA = entities[i];
      const entityB = entities[i + 1];

      if (entityA.start === entityB.start) {
        if (entityA.end === entityB.end) {
          entityA.label = [...entityA.label, ...entityB.label];
          entities.splice(i + 1, 1);
        } else {
          entityA.label = [...entityA.label, ...entityB.label];
          entityB.start = entityA.end;
        }
      } else if (entityA.end === entityB.end) {
        entityB.label = [...entityA.label, ...entityB.label];
        entityA.end = entityB.start;
      } else if (entityA.end > entityB.end) {
        entities.push({
          label: entityA.label,
          start: entityB.end,
          end: entityA.end
        });
        entityB.label = [...entityA.label, ...entityB.label];
        entityA.end = entityB.start;
      } else if (entityA.end < entityB.end && entityA.end > entityB.start) {
        entities.push({
          label: [...entityA.label, ...entityB.label],
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
    return { backgroundImage: 'linear-gradient(' + linearGradient + ')' };
  };

  createTooltip = (labels, id) => {
    let labelArray = labels.map((label, i) => {
      return <div key={i}><span className='dot' style={{ backgroundColor: LABEL_COLORS[label.label] || LABEL_COLORS['default'],
        height: '10px',
        width: '10px',
        borderRadius: '50%',
        display: 'inline-block',
        marginLeft: '5px' }}> </span> {label.label} {this.props.editable
        ? <i className={'far fa-trash-alt'} onClick={this.deleteEntity.bind(this, label.id)} style={{ cursor: 'pointer', paddingLeft: '5px' }} />
        : null} <br /> </div>;
    });
    return <ReactTooltip effect='solid' delayHide={500} clickable key={labels} id={id} aria-haspopup='true'>
      {
        labelArray
      }
    </ReactTooltip>;
  };

  deleteEntity = (id) => {
    console.log('id', id);
    const originalLabel = this.state.originalLabels.get(id);
    let query = {};
    query['label'] = originalLabel.label;
    query['start'] = originalLabel.start;
    query['end'] = originalLabel.end;
    query['extractor'] = originalLabel.extractor;
    console.log(query);
    Api.delete('/api/complaints/' + this.state.id + '/entities', query)
      .then((data) => {
        this.setState({
          taggedText: this.parseText({ text: this.props.taggedText.text, entities: data })
        });
      });
  };

  startEdit = () => {
    if (this.state.editFormActive) return;
    if (this.state.editActive) {
      window.removeEventListener('mouseup', this.handleMouseUp);
    } else {
      window.addEventListener('mouseup', this.handleMouseUp);
    }

    this.setState({
      editActive: !this.state.editActive
    });
  };

  handleMouseUp = (e) => {
    e.stopPropagation();
    const selectedText = window.getSelection();
    if (selectedText && selectedText.baseNode.parentNode.parentNode.className === 'tagged-text' && selectedText.toString()) {
      const baseOffset = selectedText.anchorOffset;
      const extentOffset = selectedText.focusOffset;
      const baseKey = selectedText.anchorNode.parentNode.attributes['data-key'].value;
      const extentKey = selectedText.focusNode.parentNode.attributes['data-key'].value;
      let globalOffsetStart = 0;
      let globalOffsetEnd = 0;
      Array.from(selectedText.focusNode.parentNode.parentNode.childNodes)
        .filter((htmlElement) => {
          return htmlElement.tagName === 'SPAN';
        }).map((spanElement) => {
          return {
            key: spanElement.attributes['data-key'].value,
            text: spanElement.innerHTML
          };
        }).forEach((element, i, thisArray) => {
          if (element.key === baseKey) {
            for (let j = 0; j < i; j++) {
              globalOffsetStart += thisArray[j].text.length;
            }
            globalOffsetStart += baseOffset;
          }
          if (element.key === extentKey) {
            for (let j = 0; j < i; j++) {
              globalOffsetEnd += thisArray[j].text.length;
            }
            globalOffsetEnd += extentOffset;
          }
        });
      let query = {};
      query['start'] = globalOffsetStart;
      query['end'] = globalOffsetEnd;
      this.startEdit();
      this.setState({
        editFormActive: true,
        newEntityQuery: query
      });
    }
  };

  abortEdit = () => {
    this.setState({
      editFormActive: false
    });
  };

  addEntity = () => {
    Api.post('/api/complaints/' + this.state.id + '/entities', this.state.newEntityQuery)
      .then((data) => {
        this.setState({
          taggedText: this.parseText({ text: this.props.taggedText.text, entities: data }),
          editFormActive: false
        });
      });
  };

  componentWillUpdate = (props) => {
    if (props.taggedText !== this.props.taggedText) {
      this.setState({
        taggedText: this.parseText(JSON.parse(JSON.stringify(props.taggedText)))
      });
    }
  };

  componentDidMount () {
    this.setState({
      taggedText: this.parseText(JSON.parse(JSON.stringify(this.props.taggedText))),
      id: this.props.id || null
    });
  }

  renderAddButton = () => {
    return <div style={{ display: 'block', paddingTop: '10px', margin: 'auto', textAlign: 'center', borderTop: '1px solid lightGrey', width: '90%', marginTop: '10px' }}>
      <i style={this.state.editActive ? { color: 'rgb(31, 130, 191)', cursor: 'pointer' } : { color: 'rgb(9, 101, 158)', cursor: 'pointer' }}
        className='fas fa-plus-circle fa-2x'
        onClick={this.startEdit} />
      {this.state.editActive ? <i style={{ display: 'block', fontSize: '0.8em', marginTop: '3px' }}>Bitte gewünschten Abschnitt markieren</i> : null}
      {this.state.editFormActive ? <div> <input type={'text'} placeholder={'Label'} onChange={(event) => { this.state.newEntityQuery['label'] = event.target.value; }} /> <input type={'text'} placeholder={'Extractor'} onChange={(event) => { this.state.newEntityQuery['extractor'] = event.target.value; }} /> <br />
        <i style={{ color: 'green', cursor: 'pointer', padding: '5px' }} onClick={this.addEntity} className='far fa-check-circle fa-2x' />
        <i style={{ color: 'red', cursor: 'pointer', padding: '5px' }} onClick={this.abortEdit} className='far fa-times-circle fa-2x' /> </div> : null}
    </div>;
  };

  render () {
    return (
      <span className='tagged-text'>
        {this.state.taggedText}
        {this.props.editable ? this.renderAddButton() : null}
      </span>
    );
  };
}

export default TaggedText;
