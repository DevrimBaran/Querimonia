/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

class Table extends Component {
  render () {
    const classes = 'responsive-table';
    const { className, ...passThroughProps } = this.props;

    let injectedProp = {
      className: className ? className + ' ' + classes : classes
    };

    return (
      <div {...injectedProp} {...passThroughProps} >
        <table>
          {this.props.children}
        </table>
      </div>
    );
  }
}

export default Table;
