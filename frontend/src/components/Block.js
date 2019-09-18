/**
 * It's a big div
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

class Block extends Component {
  constructor (props) {
    super(props);

    this.state = {};
  }
  render () {
    const { className = '', ...passThrough } = { ...this.props };
    return (
      <div className={className + ' Block shadow'} {...passThrough}>
        {this.props.children}
      </div>
    );
  }
}

export default Block;
