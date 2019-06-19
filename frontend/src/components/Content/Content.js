/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';
import './Content.scss';

const Content = (props) => (
  <div {...props} className='Content'>
    {props.children}
  </div>
);

export default Content;
