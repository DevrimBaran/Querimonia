/**
 *
 * @version <0.1>
 */

import React from 'react';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';

import Block from 'components/Block/Block';

import Home from 'views/Home';
import Complaints from 'views/Complaints';
import Import from 'views/Import';
// import Export from 'views/Export';
// import Statistics from 'views/Statistics';
import WordVectors from 'views/WordVectors';
import TagCloud from 'views/TagCloud';
import logo from './assets/img/StuproLogo2.svg';

function App () {
  let basepath;
  try {
    basepath = new URL(process.env.REACT_APP_BACKEND_PATH).pathname;
    console.log('basepath', new URL(process.env.REACT_APP_BACKEND_PATH));
  } catch (e) {
    basepath = '/';
  }
  console.log('basepath', process.env.REACT_APP_BACKEND_PATH, basepath);
  return (
    <Router basename={basepath}>
      <Block className='menu'>
        <a href='https://querimonia-dev.iao.fraunhofer.de/dev/'>
          <img src={logo} className='Topbar-logo' alt='logo' width='100%' />
        </a>
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
          {/*
          <li>
            <Link to='/export'>Export</Link>
          </li>
          <li>
            <Link to='/statistics'>Statistik</Link>
          </li>
          */}
          <li>
            <Link to='/wordvectors'>Wortvektoren</Link>
          </li>
          <li>
            <Link to='/tagcloud'>Tag-Cloud</Link>
          </li>
          {(process.env.NODE_ENV === 'development') && <li><label htmlFor='mockApi'>no mock: </label> <input type='checkbox' defaultChecked id='mockApi' /></li>}
        </ul>
      </Block>

      <Route exact path='/' component={Home} />
      <Route path='/complaints/:id?' component={Complaints} />
      <Route path='/import' component={Import} />
      {/*
      <Route path='/export' component={Export} />
      <Route path='/statistics' component={Statistics} />
      */}
      <Route path='/wordvectors' component={WordVectors} />
      <Route path='/tagcloud' component={TagCloud} />
    </Router>
  );
}
export default App;
