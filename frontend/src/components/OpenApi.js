/* eslint handle-callback-err:0 */
import React, { Component } from 'react';

class OpenApi extends Component {
  constructor (props) {
    super(props);
    this.state = {
      checked: this.checkParams()
    };
  }

  toggle = () => {
    const currentLocation = new URL(window.location.href);
    window.location.replace(this.state.checked
      ? currentLocation.pathname
      : currentLocation.href + '?useMock=true');
  };

  checkParams = () => {
    if (process.env.NODE_ENV === 'development') {
      const useMock = new URL(window.location.href).searchParams.get('useMock');
      if (useMock === 'true') {
        return true;
      }
    }
  };

  render () {
    if (process.env.NODE_ENV === 'development') {
      return (
        <div>
          <label htmlFor='useMock'>OpenApi Mock:</label>
          <input id='useMock' type='checkbox' onChange={this.toggle} defaultChecked={this.state.checked} />
        </div>
      );
    } else {
      return (<li />);
    }
  }
}

export default OpenApi;
