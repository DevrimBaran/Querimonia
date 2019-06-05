import React, { Component } from 'react';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';

import Block from 'components/Block/Block';

export function Menu () {
  return (
    <Block>
      <h6 className='center'>Menü</h6>
      <ul>
        <li>
          <Link to='/'>Start</Link>
        </li>
        <li>
          <Link to='/complaints'>Beschwerden</Link>
        </li>
        <li>
          <Link to='/statistics'>Statistiken</Link>
        </li>
        <li>
          <Link to='/wordvectors'>Wordvektoren</Link>
        </li>
      </ul>
    </Block>
  );
}

export default Menu;
