import React, { Component } from 'react';

import './Select.scss';
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
      return (
        <select {...injectedProp} {...passThroughProps}>
          <option key='null' value=''>-</option>
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
