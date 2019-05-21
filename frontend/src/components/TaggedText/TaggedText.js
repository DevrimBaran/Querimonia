import React, { Component } from 'react'

import './TaggedText.scss';

class TaggedText extends Component {
    constructor(props) {
        super(props);


        console.log(props);
        this.state = {
            text: this.parseText(props.text)
        }
    }
    parseText(text) {
        if (!text) return '';
        let p = [];
        let cpos = 0;
        if (Array.isArray(text.entities)) {
            for (const tag of text.entities) {
                p.push(<span key={p.length}>{text.text.substring(cpos, tag.start)}</span>);
                p.push(<span key={p.length} className="tag" label={tag.label}>{text.text.substring(tag.start, tag.end)}</span>);
                cpos = tag.end;
            }
        }
        p.push(<span key={p.length}>{text.text.substring(cpos, text.text.length)}</span>);
        return p;
    }
    componentDidUpdate = (props) => {
        console.log(props);
        /*this.setState({
            text: this.parseText(props.text)
        });*/
    }
    render() { 
        return (
            <div className="tagged-text">
                {this.state.text}
            </div>
        );
    }
}
 
export default TaggedText;