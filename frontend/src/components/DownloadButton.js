/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Button from './Button';

class DownloadButton extends Component {
  createDownload = (data) => {
    if (data) {
      let link = document.createElement('a');
      if (link.download !== undefined) {
        let url = URL.createObjectURL(new Blob([data], { type: this.props.type }));
        link.setAttribute('href', url);
        link.setAttribute('download', this.props.name);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }
    }
  };
  onClick = (e) => {
    if (this.props.onClick) {
      const promise = this.props.onClick(e);
      console.log('promise', promise, promise.then);
      if (promise.then) {
        promise.then(this.createDownload);
      } else {
        this.createDownload(promise);
      }
    }
  }
  render () {
    const { onClick, type, name, title, ...passThrough } = { ...this.props };
    return <Button title={title || 'Download ' + name} onClick={this.onClick} {...passThrough} />;
  }
}

export default DownloadButton;
