/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import ReactDOM from 'react-dom';

class Tooltip extends Component {
  constructor (props) {
    super(props);
    this.tooltip = React.createRef();
    this.state = {
      htmlFor: props.htmlFor
    };
  }

  static show = (e) => {
    e.persist();
    console.log('show', e);
  };

  static hide = (e) => {
    e.persist();
    console.log('hide', e);
  };

  onMouseEnter = (e) => {
    const element = e.target;
    const rect = element.getBoundingClientRect();
    const tooltip = this.tooltip.current;
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
  onMouseLeave = (e) => {
    const tooltip = this.tooltip.current;
    if (tooltip) {
      tooltip.classList.remove('show');
    }
  }
  componentDidMount = () => {
    const elements = document.querySelectorAll(this.state.htmlFor);
    elements.forEach((element) => {
      element.classList.add('hasTooltip');
      element.addEventListener('mouseEnter', this.onMouseEnter);
      element.addEventListener('mouseLeave', this.onMouseLeave);
    });
  }
  componentWillUnmount = () => {
    const elements = document.querySelectorAll(this.state.htmlFor);
    elements.forEach((element) => {
      element.classList.remove('hasTooltip');
      element.removeEventListener('mouseEnter', this.onMouseEnter);
      element.removeEventListener('mouseLeave', this.onMouseLeave);
    });
  }
  render () {
    const { htmlFor, className = '' } = { ...this.props };
    return (
      ReactDOM.createPortal(
        (<div ref={this.tooltip} className={className + ' tooltip'} htmlFor={htmlFor} >
          {this.props.children}
        </div>),
        document.body
      )
    );
  }
}

export default Tooltip;
