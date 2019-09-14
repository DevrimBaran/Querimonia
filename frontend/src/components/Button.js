/**
 * Self-explanatory
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

class Button extends Component {
  onClick = (e) => {
    e.stopPropagation();
    if (!this.props.disabled) {
      if (!this.props.confirm || window.confirm(this.props.confirm)) {
        this.props.onClick(e);
      } else {
        e.preventDefault();
      }
    }
    return false;
  }
  render () {
    const { icon, onClick, disabled, children, confirm, className = '', ...passThrough } = { ...this.props };
    if (children) {
      return (
        <button disabled={disabled} onClick={this.onClick} className={className + ' action-button'} {...passThrough}>
          {icon && <i className={icon} />} {children}
        </button>
      );
    } else {
      return <i className={icon + ' action-button ' + className} disabled={disabled} onClick={this.onClick} {...passThrough} />;
    }
  }
}

export default Button;
