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
    this.state = {
      children: this.props.children
    };
  }
  componentDidMount = () => {
    this.props.render && this.props.render(this.container.current, this.props.data, d3);
  }
  componentDidUpdate = () => {
    this.container.current.innerText = '';
    this.componentDidMount();
  }
  render () {
    const { type, id, data, render, ...passThrough } = { ...this.props };
    return (
      <div className='d3' id={id} ref={this.container} {...passThrough} />
    );
  }
}

export default D3;
