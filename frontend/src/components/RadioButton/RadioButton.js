import React, { Component } from 'react';

import './RadioButton.scss';
class RadioButton extends Component {
  constructor (props) {
    super(props);
    this.state = {
      value: this.props.value
    };
  }
    onChange = (e) => {
      this.setState({ value: e.target.value });
      this.props.onChange && this.props.onChange(e);
    }
    render () {
      return (
        <div className='RadioButton'>
          {
            this.props.values.map((value) => {
              return (
                <React.Fragment key={value}>
                  <input onChange={this.onChange} checked={this.state.value === value} type='radio' id={this.props.name + '-' + value} name={this.props.name} value={value} />
                  <label htmlFor={this.props.name + '-' + value}>{value}</label>
                </React.Fragment>
              );
            })
          }
        </div>
      );
    }
}

export default RadioButton;
