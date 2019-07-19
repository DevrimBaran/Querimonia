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
  onMouseEnter = (e) => {
    const element = e.target;
    const rect = element.getBoundingClientRect();
    const tooltip = this.tooltip.current;
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
  onMouseLeave = (e) => {
    this.tooltip.current.classList.remove('show');
  }
  componentDidMount = () => {
    const element = document.getElementById(this.state.htmlFor);
    if (element) {
      element.classList.add('hasTooltip');
      element.addEventListener('mouseenter', this.onMouseEnter);
      element.addEventListener('mouseleave', this.onMouseLeave);
    }
  }
  render () {
    const { htmlFor } = { ...this.props };
    return (
      ReactDOM.createPortal(
        (<div ref={this.tooltip} className='tooltip' htmlFor={htmlFor} >
          {this.props.children}
        </div>),
        document.body
      )
    );
  }
}

export default Tooltip;
