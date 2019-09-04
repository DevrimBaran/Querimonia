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
    this.container.current.innerText = '';
    const addSpinner = this.props.render(this.container.current, JSON.parse(JSON.stringify(this.props.data)), d3, () => {
      this.container.current.classList.remove('loading');
    });
    if (addSpinner) {
      this.container.current.classList.add('loading');
    }
  }
  componentDidUpdate = () => {
    this.componentDidMount();
  }
  shouldComponentUpdate = (nextProps) => {
    const b = JSON.stringify(this.props.data) !== JSON.stringify(nextProps.data);
    console.log(this.props.data.redraw, nextProps.data.redraw);
    return b;
  }
  render () {
    const { data, render, ...passThrough } = { ...this.props };
    return (
      <div ref={this.container} className='d3' {...passThrough} />
    );
  }
}

export default D3;
