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
    self.classList.remove('show');
  };

  render () {
    return (
      ReactDOM.createPortal(
        (<div id={'errorPopup'} >
          <div className='background' onClick={this.hidePopup} />
          <div className='content'>
            Ein Fehler ist aufgetreten :(
            <i style={{ color: 'red', cursor: 'pointer', display: 'block', float: 'right' }} onClick={this.hidePopup} className='far fa-times-circle fa-x' />
            <br />
            <br />
            <i id={'errorMessageTitle'}> </i>
            <i id={'errorMessage'} style={{ color: 'red' }}> </i>
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
    const errorMessageTitleElement = document.getElementById('errorMessageTitle');
    errorPopupElement.classList.add('show');
    errorMessageTitleElement.innerText = `Status Code: ${error.statusCode} ${error.title}:`;
    errorMessageElement.innerText = ` ${error.message}`;
  });
}
