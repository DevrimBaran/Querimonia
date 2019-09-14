/**
 * TODO:
 * Row stacks the child elements flexibly. Default horizontal, optionally vertical
 *
 * @version <0.1>
 */

import React from 'react';

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
