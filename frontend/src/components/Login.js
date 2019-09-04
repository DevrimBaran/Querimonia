/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

import Input from './Input';

class Login extends Component {
  onChange = (e) => {
    this.props.dispatch({
      type: 'LOGIN',
      name: e.value,
      password: '',
      access: ['user', 'admin'].indexOf(e.value)
    });
  }
  render () {
    return (
      <Input
        type='select'
        name='name'
        defaultValue='admin'
        onChange={this.onChange}
        required
        values={[
          {
            label: 'Gast',
            value: 'guest'
          },
          {
            label: 'Benutzer',
            value: 'user'
          },
          {
            label: 'Admin',
            value: 'admin'
          }
        ]}
      />
    );
  }
}

const mapSateToProps = (state, props) => {
  return { login: state.login };
};

export default connect(mapSateToProps)(Login);
