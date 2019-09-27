/**
 * Renders a json object unminified in a html pre-tag.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

class Grid extends Component {
  render () {
    const { children, columns, rows, style = {}, className = '', ...passThrough } = { ...this.props };
    style.gridTemplateColumns = columns;
    style.gridTemplateRows = rows;
    return (
      <div className={className + ' grid'} style={style} {...passThrough}>
        {children}
      </div>
    );
  }
}

export default Grid;
