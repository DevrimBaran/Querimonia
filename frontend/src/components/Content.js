/**
 * If used in a Row, it takes up as much space as needed, or scrolls.
 *
 * @version <0.1>
 */

import React from 'react';

const Content = (props) => {
  const isEmpty = (props.children && props.children.length === 0) || false;
  return (
    <div {...props} className={(isEmpty ? ' ' : 'content ') + props.className}>
      {props.children}
    </div>
  );
};

export default Content;
