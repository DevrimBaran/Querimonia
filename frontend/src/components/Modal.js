/**
 * ToDO:
 * Please describe this class.
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
  // TODO
  onClick = (e) => {
    const modal = this.modal.current;
    this.hideModals();
    if (modal) {
      modal.classList.add('show');
      this.props.onOpen && this.props.onOpen(e);
    }
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
    const { title, htmlFor } = { ...this.props };
    return (
      ReactDOM.createPortal(
        (<div ref={this.modal} className='modal' htmlFor={htmlFor} >
          <div className='background' onClick={this.hideModals} />
          <div className='content'>
            <b style={{ align: 'center' }}>{title}</b>
            <br />
            <br />
            {this.props.children}
            <Button style={{ position: 'absolute', right: 25, bottom: 15, cursor: 'pointer', fontSize: 'medium' }} icon='fas fa-door-open' onClick={this.hideModals}>Beenden</Button>
          </div>
        </div>),
        document.body
      )
    );
  }
}

export default Modal;
