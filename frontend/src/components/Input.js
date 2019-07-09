/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

class Input extends Component {
  onChange = (e) => {
    console.log('Input change');
    console.dir(e.target);
    this.props.onChange && this.props.onChange(e);
  }
  render () {
    let classes = this.props.className + ' Input' || 'Input';
    return (
      <input type={this.props.type} className={classes} name={this.props.name} onChange={this.onChange} defaultValue={this.props.defaultValue} />
    );
  }
}

export default Input;
