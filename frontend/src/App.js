import React from 'react';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';

import Block from 'components/Block/Block';

import Home from 'views/Home';
import Complaints from 'views/Complaints';
import Statistics from 'views/Statistics';
import WordVectors from 'views/WordVectors';

function App () {
  return (
    <Router>
      <Block className='menu'>
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
          {(process.env.NODE_ENV === 'development') && <li><label htmlFor='mockApi'>no mock: </label> <input type='checkbox' defaultChecked id='mockApi' /></li>}
        </ul>
      </Block>

      <Route exact path='/' component={Home} />
      <Route path='/complaints/:id?' component={Complaints} />
      <Route path='/statistics' component={Statistics} />
      <Route path='/wordvectors' component={WordVectors} />
    </Router>
  );
}
export default App;
