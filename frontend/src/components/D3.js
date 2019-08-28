/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

class D3 extends Component {
  constructor (props) {
    super(props);
    this.container = React.createRef();
  }
  componentDidMount = () => {
    this.props.render && this.props.render(this.container.current, this.props.data, window.d3);
  }
  componentDidUpdate = () => {
    this.componentDidMount();
  }
  render () {
    const { type, data, render, ...passThrough } = { ...this.props };
    return (
      <div className='d3' ref={this.container} {...passThrough}>
        {this.props.children}
      </div>
    );
  }
}

export default D3;
