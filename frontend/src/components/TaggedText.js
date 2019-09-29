/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Tag from './Tag';
class TaggedText extends Component {
  constructor (props) {
    super(props);
    this.state = {
      editActive: false,
      newEntityQuery: null,
      selectExtractor: '',
      html: null
    };
  }

  parseText = (text, entities) => {
    if (!entities || entities.length === 0) {
      return text;
    }
    let html = [];
    let cpos = 0;
    let key = 0;
    if (entities) {
      for (const entity of entities) {
        if (entity.start === entity.end) continue;
        // String before next entity
        !text.substring(cpos, entity.start) || html.push(<span key={key++} data-key={html.length}>{text.substring(cpos, entity.start)}</span>);
        // String that is entity
        html.push(<Tag key={key++} disabled={this.props.disabled} data-key={html.length} text={text.substring(entity.start, entity.end)} ids={entity.ids} config={this.props.active.configuration} />);
        cpos = entity.end;
      }
    }
    // String from last entity to end of text or complete text if there are no entities
    html.push(<span key={key++} data-key={html.length}>{text.substring(cpos, text.length)}</span>);
    return html;
  };

  render () {
    const { text, entities, active, disabled, dispatch, ...passThrough } = { ...this.props };
    return (
      <div {...passThrough}>
        <p>
          <span>{this.parseText(text, entities.calculated)}</span>
        </p>
      </div>
    );
  }
}

export default TaggedText;
