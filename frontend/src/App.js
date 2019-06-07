import React from 'react';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';

import Block from 'components/Block/Block';

import Home from 'views/Home';
import Complaints from 'views/Complaints';
import Import from 'views/Import';
import Export from 'views/Export';
import Statistics from 'views/Statistics';
import WordVectors from 'views/WordVectors';
import TagCloudTest from 'views/TagCloudTest';

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
            <Link to='/import'>Import</Link>
          </li>
          <li>
            <Link to='/export'>Export</Link>
          </li>
          <li>
            <Link to='/statistics'>Statistik</Link>
          </li>
          <li>
            <Link to='/wordvectors'>Wordvektoren</Link>
          </li>
          <li>
            <Link to='/tagcloud'>Tag-Cloud-Demo</Link>
          </li>
          {(process.env.NODE_ENV === 'development') && <li><label htmlFor='mockApi'>no mock: </label> <input type='checkbox' defaultChecked id='mockApi' /></li>}
        </ul>
      </Block>

      <Route exact path='/' component={Home} />
      <Route path='/complaints/:id?' component={Complaints} />
      <Route path='/statistics' component={Statistics} />
      <Route path='/import' component={Import} />
      <Route path='/export' component={Export} />
      <Route path='/wordvectors' component={WordVectors} />
      <Route path='/tagcloud' component={TagCloudTest} />
    </Router>
  );
}
export default App;
