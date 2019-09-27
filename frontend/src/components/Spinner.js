import React from 'react';

const Spinner = (props) => {
  let { show = true, className = '', ...passThrough } = { ...props };
  return show && (
    <div className='center'>
      <i className={className + ' fa-spinner fa-pulse fa fa-5x primary'} {...passThrough} />
    </div>
  );
};

export default Spinner;
