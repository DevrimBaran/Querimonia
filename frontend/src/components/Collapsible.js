/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

class Collapsible extends Component {
    state = {
      collapse: this.props.collapse
    }
    handleClick = () => {
      this.setState({ collapse: !this.state.collapse });
    }
    render () {
      return (
        <div id={this.props.id} className={'collapsible ' + this.props.className} data-collapse={this.state.collapse} style={this.props.style || {}}>
          <span onClick={this.handleClick} className='handle'>
            <i className={this.state.collapse ? 'fa fa-caret-right' : 'fa fa-caret-down'} />
            <span className="h6">{this.props.label}</span>
          </span>
          <div className='content'>{this.props.children}</div>
        </div>
      );
    }
}

export default Collapsible;
