/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

class List extends Component {
  render () {
    const { className = '', header, data = [], styles = [], ...passThroughProps } = this.props;
    return (
      <table className={className + ' List'} {...passThroughProps}>
        {header && (
          <thead>
            {header.map && header.map((col, j) => (
              <th key={j}>{col}</th>
            ))}
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

export default List;
