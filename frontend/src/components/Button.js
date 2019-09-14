/**
 * Self-explanatory
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
// eslint-disable-next-line
import { withRouter } from 'react-router-dom';

class Button extends Component {
  onClick = (e) => {
    e.stopPropagation();
    if (!this.props.disabled) {
      if (!this.props.confirm || window.confirm(this.props.confirm)) {
        this.props.onClick && this.props.onClick(e);
        this.props.href && this.props.history.push(this.props.href);
      } else {
        e.preventDefault();
      }
    }
    return false;
  }
  render () {
    const { icon, onClick, disabled, children, confirm, href, className = '', staticContext, ...passThrough } = { ...this.props };
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

export default withRouter(Button);
