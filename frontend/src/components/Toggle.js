import React from 'react';

export default function Toggle (props) {
  const { onChange, on, off, state = false, ...passThrough } = { ...props };
  return (
    <label className='toggle' {...passThrough}>
      <input type='checkbox' defaultChecked={state} onChange={onChange} />
      <div className='toggle-input'>
        <div className={'toggle-off ' + off} />
        <div className='toggle-toggle'>
          <div className='toggle-pre' />
          <div className='toggle-post' />
          <div className='toggle-dot' />
        </div>
        <div className={'toggle-on ' + on} />
      </div>
    </label>
  );
};
