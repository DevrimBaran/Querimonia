/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import ReactDOM from 'react-dom';

export class ErrorPopupComponent extends Component {
  hidePopup = () => {
    const self = document.getElementById('errorPopup');
    const selfErrorMessageElement = document.getElementById('errorMessage');
    selfErrorMessageElement.innerText = '';
    self.classList.remove('show');
  };

  render () {
    return (
      ReactDOM.createPortal(
        (<div id='errorPopup' >
          <div className='background' onClick={this.hidePopup} />
          <div className='content'>
            Ein Fehler ist aufgetreten :(
            <i style={{ color: 'red', cursor: 'pointer', display: 'block', float: 'right', marginLeft: '15px' }} onClick={this.hidePopup} className='far fa-times-circle fa-x' />
            <br />
            <br />
            <i id='errorMessage' />
          </div>
        </div>),
        document.body
      )
    );
  }
}

export function showErrorPopup (errorResponse) {
  errorResponse.then((error) => {
    const errorPopupElement = document.getElementById('errorPopup');
    const errorMessageElement = document.getElementById('errorMessage');
    errorPopupElement.classList.add('show');
    errorMessageElement.innerText += `Status Code: ${error.statusCode || status} ${error.title || error.error}: ${error.message} \r\n`;
  });
}
