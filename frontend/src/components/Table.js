/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

class Table extends Component {
  render () {
    const { className = '', fix = 'right', ...passThroughProps } = this.props;
    let classes = ' responsive-table';
    fix && (classes += ' fix-' + fix);
    return (
      <div className={className + classes} {...passThroughProps} >
        <table>
          {this.props.children}
        </table>
      </div>
    );
  }
}

export default Table;
