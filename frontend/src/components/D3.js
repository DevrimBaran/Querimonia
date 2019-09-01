/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import * as d3 from 'd3';

class D3 extends Component {
  constructor (props) {
    super(props);
    this.container = React.createRef();
  }
  componentDidMount = () => {
    this.props.render && this.props.render(this.container.current, this.props.data, d3);
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
