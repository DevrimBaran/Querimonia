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
    const { icon, onClick, disabled, children, value, ...passThrough } = { ...this.props };
    if (value) {
      return (
        <button disabled={disabled} onClick={disabled ? this.stopPropagation : onClick} {...passThrough}>
          <i className={icon} />&nbsp;{children}
        </button>
      );
    } else {
      return <i className={icon} disabled={disabled} onClick={disabled ? this.stopPropagation : onClick} {...passThrough} />;
    }
  }
}

export default Button;
