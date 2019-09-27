/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import Button from './Button';

class FileInput extends Component {
  constructor (props) {
    super(props);
    this.input = React.createRef();
    this.state = {
      value: this.props.value || ''
    };
  }
  onChange = (e) => {
    const file = e.target.files[0];
    this.setState({ value: file ? file.name : '' });
    this.props.onChange && this.props.onChange(e);
  }
  clear = (e) => {
    this.input.current.value = null;
    this.onChange({ target: this.input.current, name: this.props.name, value: '' });
  }
  render () {
    const { name, className = '', onChange, ...passThrough } = { ...this.props };
    return (
      <React.Fragment>
        <label className='FileInput' htmlFor={name}>
          <i className='fas fa-file-upload' />
          {this.state.value && <span>
            {this.state.value}
            <Button icon='far fa-times-circle' onClick={this.clear} />
          </span>}
        </label>
        <input className={className + ' FileInput'} ref={this.input} onChange={this.onChange} name={name} {...passThrough} />
      </React.Fragment>
    );
  }
}

export default FileInput;
