/**
 * If used in a Row, it takes up as much space as needed, or scrolls.
 *
 * @version <0.1>
 */

import React from 'react';

const Content = (props) => {
  const { className = '', children, ...passThrough } = { ...props };
  const isEmpty = (children && children.length === 0) || false;
  return (
    <div {...passThrough} className={className + (isEmpty || ' content')}>
      {children}
    </div>
  );
};

export default Content;
