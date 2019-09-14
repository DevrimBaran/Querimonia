/**
 *
 * @version <0.1>
 */

import React from 'react';

const Form = (props) => {
  let { className = '', ...passThrough } = { ...props };
  return (
    <form className={className + ' Form'} {...passThrough}>
      {props.children}
    </form>
  );
};

export default Form;
