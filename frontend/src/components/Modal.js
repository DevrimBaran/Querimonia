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
    const modal = this.modal.current;
    this.hideModals();
    if (modal) {
      modal.classList.add('show');
    }
  }
  hideModals = () => {
    for (const modal of document.querySelectorAll('.modal.show')) {
      modal.classList.remove('show');
    }
  }
  componentDidMount = () => {
    const elements = document.querySelectorAll(this.state.htmlFor);
    elements.forEach((element) => {
      element.classList.add('hasModal');
      element.addEventListener('click', this.onClick);
    });
  }
  componentWillUnmount = () => {
    const elements = document.querySelectorAll(this.state.htmlFor);
    elements.forEach((element) => {
      element.classList.remove('hasModal');
      element.removeEventListener('click', this.onClick);
    });
  }
  render () {
    const { htmlFor } = { ...this.props };
    return (
      ReactDOM.createPortal(
        (<div ref={this.modal} className='modal' htmlFor={htmlFor} >
          <div className='background' onClick={this.hideModals} />
          <div className='content'>
            <i style={{ color: 'red', cursor: 'pointer', display: 'block', float: 'right' }} onClick={this.hideModals} className='far fa-times-circle fa-x' />
            <br />
            {this.props.children}
          </div>
        </div>),
        document.body
      )
    );
  }
}

export default Modal;
