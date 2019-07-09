/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

class Select extends Component {
    onChange = (e) => {
      console.log('Select change');
      console.dir(e.target);
      this.props.onChange && this.props.onChange(e);
    }
    render () {
      const { className, onChange, ...passThroughProps } = this.props;

      let injectedProp = {
        className: className ? className + ' Select' : 'Select',
        onChange: this.onChange
      };
      console.log('required', this.props.required);
      return (
        <select {...injectedProp} {...passThroughProps} className='select-wrapper'>
          {this.props.required || <option key='null' value=''>-</option>}
          {
            this.props.values.map((value) => {
              return (
                <option key={value} value={value}>{value}</option>
              );
            })
          }
        </select>
      );
    }
}

export default Select;
