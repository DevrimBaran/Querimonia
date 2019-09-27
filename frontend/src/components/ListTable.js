/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

class ListTable extends Component {
  render () {
    const { className = '', header, data = [], styles = [], headerStyles = [], ...passThroughProps } = this.props;
    if (data.length === 0) {
      return <table className={className + ' ListTable'} {...passThroughProps} />;
    }
    return (
      <table className={className + ' ListTable'} {...passThroughProps}>
        {header && (
          <thead>
            <tr>
              {header.map && header.map((col, j) => (
                <th key={j} style={headerStyles[j]}>{col}</th>
              ))}
            </tr>
          </thead>
        )}
        <tbody>
          {data.map && data.map((row, i) => (
            <tr key={i}>
              {row.map && row.map((col, j) => (
                <td style={styles[j]} key={j}>{col}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    );
  }
}

export default ListTable;
