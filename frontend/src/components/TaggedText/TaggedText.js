import React, { Component } from 'react';

import './TaggedText.scss';

class TaggedText extends Component {
  constructor (props) {
    super(props);

    this.state = {
      text: this.parseText(props.text)
    };
  }
  parseText (text) {
    if (!text) return '';
    if (!text.text) return text;
    let p = [];
    let cpos = 0;
    if (Array.isArray(text.entities)) {
      text.entities.sort((a, b) => a.start - b.start);
      for (const tag of text.entities) {
        p.push(<span key={p.length}>{text.text.substring(cpos, tag.start)}</span>);
        p.push(<span key={p.length} className='tag' label={tag.label}>{text.text.substring(tag.start, tag.end)}</span>);
        cpos = tag.end;
      }
    }
    p.push(<span key={p.length}>{text.text.substring(cpos, text.text.length)}</span>);
    return p;
  }
    componentWillUpdate = (props) => {
      if (props.text !== this.props.text) {
        this.setState({
          text: this.parseText(props.text)
        });
      }
    }
    render () {
      return (
        <span className='tagged-text'>
          {this.state.text}
        </span>
      );
    }
}

export default TaggedText;
