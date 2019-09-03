/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { file } from '@babel/types';

class FileInput extends Component {
  constructor (props) {
    super(props);
    this.state = {
      value: this.props.value || ''
    };
  }
  onChange = (e) => {
    const file = e.target.files[0];
    this.setState({ value: file ? file.name : '' });
    this.props.onChange && this.props.onChange(e);
  }
  render () {
    const { name, className = '', onChange, ...passThrough } = { ...this.props };
    return (
      <React.Fragment>
        <label className='FileInput' htmlFor={name}>
          <i className='fas fa-file-upload' />
          <span>{this.state.value}</span>
        </label>
        <input className={className + ' FileInput'} onChange={this.onChange} name={name} {...passThrough} />
      </React.Fragment>
    );
  }
}

export default FileInput;
