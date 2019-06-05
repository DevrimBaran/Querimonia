import React, { Component } from 'react';

import './Block.scss';

class Block extends Component {
  constructor (props) {
    super(props);

    this.state = {};
  }
  render () {
    return (
      <div className={this.props.className ? this.props.className + ' Block shadow' : 'Block shadow'}>
        {this.props.children}
      </div>
    );
  }
}

export default Block;
