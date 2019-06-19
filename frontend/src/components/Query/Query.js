/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
// eslint-disable-next-line
import { BrowserRouter as Router, Link } from 'react-router-dom';

function Query (WrappedComponent) {
  return class extends Component {
    constructor (props) {
      super(props);
      this.state = {
        value: new URLSearchParams(document.location.search).get(props.name) || '',
        type: props.type
      };
      this.state.name = this.state.value && this.props.name;
    }
    onChange = (e) => {
      const value = this.state.type === 'checkbox' ? e.target.checked : e.target.value;
      this.setState({
        value: value,
        name: value && this.props.name
      });
      this.props.onChange && this.props.onChange(e);
    }
    render () {
      // Filter out extra props that are specific to this HOC and shouldn't be
      // passed through
      const { name, value, onChange, type, ...passThroughProps } = this.props;

      const search = new URLSearchParams(document.location.search);
      const pathname = document.location.pathname;

      // Inject props into the wrapped component. These are usually state values or
      // instance methods.
      let injectedProp = {};
      if (WrappedComponent.name === 'Link') {
        search.set(name, value);
        injectedProp.to = pathname + '?' + search;
      } else if (type === 'hidden') {
        injectedProp.defaultValue = this.state.value;
        injectedProp.value = this.state.value;
        injectedProp.name = this.state.name;
        injectedProp.type = type;
      } else {
        injectedProp.defaultValue = this.state.value;
        injectedProp.onChange = this.onChange;
        injectedProp.name = this.state.name;
        injectedProp.type = type;
      }

      // Pass props to wrapped component
      return (
        <WrappedComponent
          {...injectedProp}
          {...passThroughProps}
        />
      );
    }
  };
}

export default Query;
