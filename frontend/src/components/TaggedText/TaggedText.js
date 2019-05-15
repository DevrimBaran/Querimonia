import React, { Component } from 'react'
import Fa from '../Fa/Fa';
import './TaggedText.scss';

class TaggedText extends Component {
    constructor(props) {
        super(props);

        this.state = {
            data: null,
            answer: null
        }

        props.promise.then((data) => {
            this.setState({
                data: this.parseData(data),
                answer: this.parseAnswer(data),
            });
        });
    }
    parseAnswer(response) {
        let newData = [];
        if (!Array.isArray(response)) return null;
        for (let data of response) {
            newData.push(<p key={newData.length}>{data.answer}</p>);
        }
        return newData;
    }
    parseData(response) {
        let newData = [];
        if (!Array.isArray(response)) return null;
        for (let data of response) {
            let p = [];
            let cpos = 0;
            if (!Array.isArray(data.entities)) continue;
            for (const tag of data.entities) {
                p.push(<span key={p.length}>{data.text.substring(cpos, tag.start)}</span>);
                p.push(<span key={p.length} className="tag" label={tag.label}>{data.text.substring(tag.start, tag.end)}</span>);
                cpos = tag.end;
            }
            p.push(<span key={p.length}>{data.text.substring(cpos, data.text.length)}</span>);
            newData.push(<p key={newData.length}>{p}</p>);
        }
        return newData;
    }
    render() { 
        return (
            <div className="tagged-text">
                {this.state.data}
                <br />
                {this.state.answer}
            </div>
        );
    }
}
 
export default TaggedText;