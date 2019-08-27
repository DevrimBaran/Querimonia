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
      let { style, disabled, collapse, label, ...passThrough } = { ...this.props };
      return (
        <div className={disabled ? 'collapsible disabled' : 'collapsible'} data-collapse={this.state.collapse} style={style || {}} {...passThrough} >
          <span onClick={disabled ? undefined : this.handleClick} className='handle'>
            <i className={this.state.collapse ? 'fa fa-caret-right' : 'fa fa-caret-down'} />
            <span className='h6'>{label}</span>
          </span>
        </div>
      );
    }
}

export default Collapsible;
