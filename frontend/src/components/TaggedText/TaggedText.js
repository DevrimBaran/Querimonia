import React, { Component } from 'react'
import Fa from '../Fa/Fa';
import './TaggedText.scss';

class TaggedText extends Component {
    constructor(props) {
        super(props);
        this.handlePromise(props.promise);
        
        this.state = {
            data: null
        }
    }
    handlePromise = (promise) => {
        promise.then((res) => {
            let cpos = 0;
            var newData = [];
            for (let data of res) {
                for (let tag of data.entities) {
                    newData.push(<span>{data.text.substring(cpos, tag.start)}</span>);
                    newData.push(<span className="tag" label={tag.label}>{data.text.substring(tag.start, tag.end)}</span>);
                    cpos = tag.end;
                }
                newData.push(<span>{data.text.substring(cpos, data.text.length)}</span>);
            }
            this.setState({ data: newData});
        });
    }
    render() { 
        return (
            <div className="tagged-text">
                {this.state.data}
            </div>
        );
    }
}
 
export default TaggedText;