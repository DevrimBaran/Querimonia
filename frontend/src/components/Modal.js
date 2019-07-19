/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import ReactDOM from 'react-dom';

class Modal extends Component {
  constructor (props) {
    super(props);
    this.modal = React.createRef();
    this.state = {
      htmlFor: props.htmlFor
    };
  }
  onClick = (e) => {
    this.hideModals();
    this.modal.current.classList.add('show');
  }
  hideModals = () => {
    for (const modal of document.querySelectorAll('.modal.show')) {
      modal.classList.remove('show');
    }
  }
  componentDidMount = () => {
    const element = document.getElementById(this.state.htmlFor);
    if (element) {
      element.classList.add('hasModal');
      element.addEventListener('click', this.onClick);
    }
  }
  render () {
    const { htmlFor } = { ...this.props };
    return (
      ReactDOM.createPortal(
        (<div ref={this.modal} className='modal' htmlFor={htmlFor} >
          <div className='background' onClick={this.hideModals} />
          <div className='content'>
            {this.props.children}
          </div>
        </div>),
        document.body
      )
    );
  }
}

export default Modal;
