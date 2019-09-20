/**
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Input from './Input';

class Form extends Component {
  constructor (props) {
    super(props);
    this.state = {};
  }
  render () {
    let { children, className = '', ...passThrough } = { ...this.props };
    return (
      <form className={className + ' Form'} {...passThrough}>
        <label> <i className='fas fa-filter' />  Erweiterte Suche</label>
        {children}
        <Input type='submit' value='Anwenden' />
        {/* <Input type='reset' value='Zurücksetzen' /> */}
      </form>
    );
  }
}

export default Form;
