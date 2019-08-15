/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

class Button extends Component {
  stopPropagation = (e) => {
    e.stopPropagation();
    return false;
  }
  render () {
    const { icon, onClick, disabled, children, ...passThrough } = { ...this.props };
    return (
      <button disabled={disabled} onClick={disabled ? this.stopPropagation : onClick} {...passThrough}>
        <i className={icon} />&nbsp;{children}
      </button>
    );
  }
}

export default Button;
