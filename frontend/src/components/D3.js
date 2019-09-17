/**
 * Wrapper for easy use with D3.
 * Takes two Arguments: Data & Render
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
  redraw = () => {
    this.container.current && (this.container.current.innerText = '');
    const addSpinner = this.props.render(this.container.current, JSON.parse(JSON.stringify(this.props.data)), d3, () => {
      this.container.current && this.container.current.classList.remove('loading');
    });
    if (addSpinner) {
      this.container.current && this.container.current.classList.add('loading');
    }
  }
  componentDidMount = () => {
    this.redraw();
  }
  componentDidUpdate = (prevProps) => {
    if (JSON.stringify(this.props.data) !== JSON.stringify(prevProps.data)) {
      this.redraw();
    }
  }
  render () {
    const { data, render, ...passThrough } = { ...this.props };
    return (
      <div ref={this.container} className='d3' {...passThrough} />
    );
  }
}

export default D3;
