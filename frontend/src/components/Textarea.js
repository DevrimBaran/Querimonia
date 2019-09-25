/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

class Textarea extends Component {
  constructor (props) {
    super(props);
    this.border = '0';
    this.input = React.createRef();
    this.dummy = React.createRef();
    this.min = React.createRef();
    this.max = React.createRef();
  }
  resize = () => {
    const input = this.input.current;
    const dummy = this.dummy.current;
    dummy.value = input.value;
    input.style.height = 'calc(' + dummy.scrollHeight + 'px + ' + this.border + ')';
  }
  update = () => {
    const input = this.input.current;
    const dummy = this.dummy.current;
    const min = this.min.current;
    const max = this.max.current;
    const dummyStyle = window.getComputedStyle(dummy, null);
    const scrollbar = input.clientWidth - dummy.clientWidth;
    dummy.style.width = 'calc(100% + ' + scrollbar + 'px)';
    this.border = dummyStyle.getPropertyValue('border-top-width') + ' + ' + dummyStyle.getPropertyValue('border-bottom-width');
    this.props.min && (input.style.minHeight = window.getComputedStyle(min, null).height);
    this.props.max && (input.style.maxHeight = window.getComputedStyle(max, null).height);
    this.resize();
  }
  componentDidUpdate = () => {
    this.update();
  }
  componentDidMount = () => {
    this.update();
  }
  render () {
    const { onInput, ...props } = { ...this.props };
    return (
      <div className='dynamic-textarea'>
        <textarea ref={this.input} onInput={this.resize} {...props} />
        <div style={{ overflow: 'hidden' }}>
          <textarea className='dummy' ref={this.dummy} />
          {this.props.min && (<textarea className='dummy' rows={this.props.min} ref={this.min} />)}
          {this.props.max && (<textarea className='dummy' rows={this.props.max} ref={this.max} />)}
        </div>
      </div>
    );
  }
}

export default Textarea;
