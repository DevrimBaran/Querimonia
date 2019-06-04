import React, { Component } from 'react';

import './Select.scss';
class Select extends Component {
  constructor (props) {
    super(props);
    this.state = {
      value: this.props.value
    };
  }
    onChange = (e) => {
      this.props.onChange && this.props.onChange(e);
    }
    render () {
      let classes = this.props.className + ' Select' || 'Select';
      return (
        <select className={classes} name={this.props.name} onChange={this.onChange}>
          <option key="null" value={null}>-</option>
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
