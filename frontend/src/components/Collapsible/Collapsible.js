import React, { Component } from 'react'
import Fa from '../Fa/Fa';
import './Collapsible.scss';

class Collapsible extends Component {
    state = { 
        collapse: this.props.collapse
    }
    handleClick = () => {
        this.setState({ collapse: !this.state.collapse });
    }
    render() { 
        return (
            <div id={this.props.id} className="collapsible" data-side={this.props.side} data-collapse={this.state.collapse}>
                <div className="content">{this.props.children}</div>
                <span className="handle"><Fa active={!this.state.collapse} on="fa-caret-right" off="fa-caret-left" onClick={this.handleClick} /></span>
            </div> 
        );
    }
}
 
export default Collapsible;