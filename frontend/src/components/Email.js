/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import ReactDOMServer from 'react-dom/server';

class Email extends Component {
  getText = (content) => (`
    To: <${this.props.to}>
    Subject: ${this.props.subject}
    X-Unsent: 1
    Content-Type: text/html
    
    <html>
      <body>
        ${content}
      </body>
    </html >  
  `);
  generateFile = () => {
    const text = this.getText(ReactDOMServer.renderToStaticMarkup(this.props.children));
    var data = new Blob([text], { type: 'text/plain' });

    if (this.textFile) {
      window.URL.revokeObjectURL(this.textFile);
    }

    this.textFile = window.URL.createObjectURL(data);
    console.log(this.textFile);
    return this.textFile;
  }
  render () {
    const { subject, to, onClick, name, label, ...passThrough } = { ...this.props };
    return (
      <a download={name + '.eml'} onClick={this.onClick} href={this.generateFile()} {...passThrough}>{label}</a>
    );
  }
}

export default Email;
