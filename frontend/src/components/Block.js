/**
 * ToDO:
 * Please describe this class.
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
    return (
      <div className={this.props.className ? this.props.className + ' Block shadow' : 'Block shadow'} style={this.props.style || {}}>
        {this.props.children}
      </div>
    );
  }
}

export default Block;
