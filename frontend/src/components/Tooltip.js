/**
 * Tooltips Component
 *
 * usage:
 * let tooltip = Tooltip.create();
 *
 * <any {...tooltip.events} />          // adds onMouseEnter & onMouseLeave events
 * <Tooltip {...tooltip.register} />    // registers this tooltip as target
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import ReactDOM from 'react-dom';

class Tooltip extends Component {
  constructor (props) {
    super(props);
    this.state = {
      htmlFor: props.htmlFor
    };
  }

  static create = () => {
    var tooltip = {
      ref: null
    };
    tooltip.register = {
      tooltipRef: ref => {
        tooltip.ref = ref; return ref;
      }
    };
    tooltip.events = {
      onMouseEnter: Tooltip.onMouseEnter.bind(tooltip),
      onMouseLeave: Tooltip.onMouseLeave.bind(tooltip)
    };
    return tooltip;
  }

  static onMouseEnter = function (e) {
    const element = e.target;
    const rect = element.getBoundingClientRect();
    const tooltip = this.ref;
    if (tooltip) {
      tooltip.classList.add('show');
      tooltip.style.left = (rect.x + rect.width * 0.5) + 'px';
      if (rect.y >= tooltip.offsetHeight) {
        tooltip.classList.remove('bottom');
        tooltip.classList.add('top');
        tooltip.style.top = (rect.y) + 'px';
      } else {
        tooltip.classList.remove('top');
        tooltip.classList.add('bottom');
        tooltip.style.top = (rect.y + rect.height) + 'px';
      }
    }
  }
  static onMouseLeave = function (e) {
    const tooltip = this.ref;
    if (tooltip) {
      tooltip.classList.remove('show');
    }
  }
  render () {
    const { className = '', tooltipRef } = { ...this.props };
    return (
      ReactDOM.createPortal(
        (<div ref={tooltipRef} className={className + ' tooltip'} >
          {this.props.children}
        </div>),
        document.body
      )
    );
  }
}

export default Tooltip;
