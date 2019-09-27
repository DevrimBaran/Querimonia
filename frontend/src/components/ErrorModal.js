/**
 * Popup with error-Message
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Modal from './Modal';
import ListTable from './ListTable';

class ErrorModal extends Component {
  static QuerimoniaError = class QuerimoniaError extends Error {
    constructor (json, ...rest) {
      const { statusCode, status, title, error, message, exception } = { ...json };
      super(message || exception, ...rest);
      this.name = 'QuerimoniaError';
      this.status = statusCode || status;
      this.title = title || error;
    }
  }

  constructor (props) {
    super(props);

    ErrorModal.ref = this;

    this.state = {
      error: null
    };
  }

  static catch = (error) => {
    if (error.name === 'QuerimoniaError') {
      ErrorModal.ref.setState({ error: error });
    } else {
      console.error(error);
    }
    return null;
  }

  removeError = (e) => {
    this.setState({ error: null });
  }

  register = (show) => {
    this.showModal = show;
  }

  render () {
    const { ...passThrough } = { ...this.props };
    const error = this.state.error;
    return (
      <Modal title={error ? error.title : ''} id='errorPopup' className={error ? 'show' : ''} onClose={this.removeError} register={this.register} {...passThrough}>
        <ListTable
          data={[
            ['Status Code:', error && error.status],
            ['Nachricht:', error && error.message]
          ]}
          styles={[
            { fontWeight: 'bold', padding: '0 1rem', whiteSpace: 'nowrap', verticalAlign: 'top' },
            { verticalAlign: 'top' }
          ]}
        />
      </Modal>
    );
  }
}

export default ErrorModal;
