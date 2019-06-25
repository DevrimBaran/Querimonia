import React, { Component } from 'react';

import './Body.scss';
class Body extends Component {
  constructor (props) {
    super(props);

    this.state = {};
  }
  render () {
    return (
      <div className='Body'>
        {this.props.children}
      </div>
    );
  }
}

export default Body;
