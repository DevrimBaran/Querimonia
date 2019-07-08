/**
 *
 * @version <0.1>
 */

import React from 'react';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';

import Home from './views/Home';
import Complaints from './views/Complaints';
import Import from './views/Import';
import WordVectors from './views/WordVectors';
import TagCloud from './views/TagCloud';
import Templates from './views/Templates';
import Actions from './views/Actions';
import Config from './views/Config';
import Impressum from './views/Impressum';

import logo from './assets/img/StuproLogo2.svg';
import OpenApi from './components/OpenApi';

function App () {
  let basepath;
  try {
    basepath = new URL(process.env.REACT_APP_BACKEND_PATH).pathname;
    console.log('basepath', new URL(process.env.REACT_APP_BACKEND_PATH));
  } catch (e) {
    basepath = '/';
  }

  return (
    <Router basename={basepath}>
      <nav id='menu'>
        <a href={basepath}>
          <img src={logo} id='logo' alt='logo' width='100%' />
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
          <li>
            <Link to='/templates'>Textbausteine</Link>
          </li>
          <li>
            <Link to='/actions'>Aktionen</Link>
          </li>
          <li>
            <Link to='/config'>Konfiguration</Link>
          </li>
          <li>
            <Link to='/wordvectors'>Wortvektoren</Link>
          </li>
          <li>
            <Link to='/tagcloud'>Tag-Cloud</Link>
          </li>
          <li>
            { /* wird nur in development und mock gerendered */ }
            <OpenApi />
          </li>
        </ul>
        <ul style={{ position: 'absolute', bottom: '10px', width: '100%' }}>
          <li>
            <Link to='/impressum'>Impressum</Link>
          </li>
        </ul>
      </nav>

      <Route exact path='/' component={Home} />
      <Route path='/complaints/:id?' component={Complaints} />
      <Route path='/templates/:id?' component={Templates} />
      <Route path='/actions/:id?' component={Actions} />
      <Route path='/config/:id?' component={Config} />
      <Route path='/import' component={Import} />
      {/*
      <Route path='/export' component={Export} />
      <Route path='/statistics' component={Statistics} />
      */}
      <Route path='/wordvectors' component={WordVectors} />
      <Route path='/tagcloud' component={TagCloud} />
      <Route path='/impressum' component={Impressum} />
    </Router>
  );
}
export default App;
