/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';
import './Fa.scss';

function Fa (props) {
  return (
    <span className='toggle-fa' active={props.active ? 'true' : 'false'} onClick={props.onClick}>
      <i className={'on fa ' + props.on} />
      <i className={'off fa ' + props.off} />
    </span>
  );
}

export default Fa;
