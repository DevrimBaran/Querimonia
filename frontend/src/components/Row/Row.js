/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';
import './Row.scss';

function Row (props) {
  let classes = props.className || '';
  classes += props.vertical ? ' row flex-column' : ' row flex-row';
  return (
    <div className={classes} style={props.style}>
      {props.children}
    </div>
  );
};

export default Row;
