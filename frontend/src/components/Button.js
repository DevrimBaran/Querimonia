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
    e.preventDefault();
    if (!this.props.disabled) {
      if (!this.props.confirm || window.confirm(this.props.confirm)) {
        this.props.onClick && this.props.onClick(e);
        this.props.href && this.props.history.push(this.props.href);
      }
    }
    return false;
  }
  render () {
    const { icon, onClick, disabled, children, confirm, href, className = '', staticContext, history, location, match, ...passThrough } = { ...this.props };
    const stack = Array.isArray(icon) && icon.map((c, i) => <i key={i} className={c} />);
    const catchClick = this.onClick || this.href;
    let fa = stack ? 'fa-stack' : icon;
    if (children) {
      return (
        <button disabled={disabled} onClick={catchClick ? this.onClick : undefined} className={className + ' action-button'} {...passThrough}>
          {icon && <i className={fa}>{stack}</i>} {children}
        </button>
      );
    } else {
      return <i className={fa + ' action-button ' + className} disabled={disabled} onClick={catchClick ? this.onClick : undefined} {...passThrough}>
        {stack}
      </i>;
    }
  }
}

export default withRouter(Button);
