/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

const Content = (props) => {
  const isEmpty = (props.children && props.children.length === 0) || false;
  console.log('empty', props.children.length, isEmpty);
  return (
    <div {...props} className={(isEmpty ? ' ' : 'content ') + props.className}>
      {props.children}
    </div>
  );
};

export default Content;
