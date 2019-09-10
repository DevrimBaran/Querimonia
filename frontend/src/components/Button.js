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
    const { icon, onClick, disabled, children, className = '', ...passThrough } = { ...this.props };
    if (children) {
      return (
        <button disabled={disabled} onClick={disabled ? this.stopPropagation : onClick} className={className + ' action-button'} {...passThrough}>
          {icon && <i className={icon} />} {children}
        </button>
      );
    } else {
      return <i className={icon + ' action-button ' + className} disabled={disabled} onClick={disabled ? this.stopPropagation : onClick} {...passThrough} />;
    }
  }
}

export default Button;
