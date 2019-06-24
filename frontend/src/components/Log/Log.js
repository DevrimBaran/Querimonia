/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import './Log.scss';

class Log extends Component {
  constructor (props) {
    super(props);

    this.state = {
      lines: []
    };
  }
  render () {
    return (
      <div className='Log'>
        <pre>
          {this.state.lines.map(line => {
            return line + '\r\n';
          })}
        </pre>
      </div>
    );
  }
}

export default Log;
