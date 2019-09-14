/**
 * Popup
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import Button from '../components/Button';

class Modal extends Component {
  constructor (props) {
    super(props);
    this.modal = React.createRef();
    this.state = {
      htmlFor: props.htmlFor
    };
  }
  onClick = (e) => {
    this.show(e);
  }
  show = (e) => {
    const modal = this.modal.current;
    this.hideModals();
    if (modal) {
      modal.classList.add('show');
      this.props.onOpen && this.props.onOpen(e);
    }
  }
  hide = (e) => {
    this.hideModals();
    this.props.onClose && this.props.onClose(e);
  }
  hideModals = () => {
    for (const modal of document.querySelectorAll('.modal.show')) {
      modal.classList.remove('show');
    }
  }
  handleKeyDown = (e) => {
    // Esc-Button-Event
    if (e.keyCode === 27) {
      this.hideModals();
      // +-Button-Event
    } else if (e.ctrlKey && e.keyCode === 107) {
      this.onClick();
      e.preventDefault();
    }
  }
  componentDidMount = () => {
    this.props.register && this.props.register(this.show);

    const elements = document.querySelectorAll(this.state.htmlFor);
    elements.forEach((element) => {
      element.classList.add('hasModal');
      element.addEventListener('click', this.onClick);
      document.addEventListener('keydown', this.handleKeyDown);
    });
  }
  componentWillUnmount = () => {
    const elements = document.querySelectorAll(this.state.htmlFor);
    elements.forEach((element) => {
      element.classList.remove('hasModal');
      element.removeEventListener('click', this.onClick);
      document.removeEventListener('keydown', this.handleKeyDown);
    });
  }
  render () {
    const { title, register, htmlFor, onOpen, onClose, className = '', contentName = 'content', ...passThrough } = { ...this.props };
    return (
      ReactDOM.createPortal(
        (<div ref={this.modal} className={className + ' modal'} htmlFor={htmlFor} {...passThrough} >
          <div className='background' onClick={this.hide} />
          <div className={contentName}>
            <b style={{ align: 'center' }}>{title}</b>
            <br />
            <br />
            {this.props.children}
            <Button className={'modal-button'} icon='fas fa-times-circle fa-x' onClick={this.hide} />
          </div>
        </div>),
        document.body
      )
    );
  }
}

export default Modal;
