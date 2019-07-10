/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

const Content = (props) => (
  <div {...props} className={'content ' + props.className}>
    {props.children}
  </div>
);

export default Content;
