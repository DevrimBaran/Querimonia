/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

class Input extends Component {
    onChange = (e) => {
      this.props.onChange && this.props.onChange(e);
    }
    render () {
      const classes = '';
      const { className, onChange, type, label, values, ...passThroughProps } = this.props;

      let injectedProp = {
        className: className ? className + ' ' + classes : classes,
        onChange: this.onChange
      };

      let input;
      switch (this.props.type) {
        case 'select': {
          input = (<select {...injectedProp} {...passThroughProps}>
            {this.props.required || <option key='null' value=''>-</option>}
            {
              values && values.map((value) => {
                return (
                  <option key={value.value} value={value.value}>{value.label}</option>
                );
              })
            }
          </select>);
          break;
        }
        case 'textarea': {
          input = <textarea {...injectedProp} {...passThroughProps} />;
          break;
        }
        default: {
          input = <input {...injectedProp} type={type} {...passThroughProps} />;
          break;
        }
      }
      return (
        <React.Fragment>
          { label && (<label htmlFor={this.props.name}>{label}</label>) }
          { input }
        </React.Fragment>
      );
    }
}

export default Input;
