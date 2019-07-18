/**
 *
 * @version <0.1>
 */

import React from 'react';
import { BrowserRouter as Router, Route, NavLink } from 'react-router-dom';

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
            <NavLink activeClassName='active' to='/complaints'>Beschwerden</NavLink>
          </li>
          <li>
            <NavLink activeClassName='active' to='/import'>Import</NavLink>
          </li>
          <li>
            <NavLink activeClassName='active' to='/templates'>Textbausteine</NavLink>
          </li>
          <li>
            <NavLink activeClassName='active' to='/actions'>Aktionen</NavLink>
          </li>
          <li>
            <NavLink activeClassName='active' to='/config'>Konfiguration</NavLink>
          </li>
          <li>
            <NavLink activeClassName='active' to='/wordvectors'>Wordvektoren</NavLink>
          </li>
          <li>
            <NavLink activeClassName='active' to='/tagcloud'>Worthäufigkeiten</NavLink>
          </li>
          <li>
            { /* wird nur in development und mock gerendered */ }
            <OpenApi />
          </li>
        </ul>
        <ul style={{ position: 'absolute', bottom: '10px', width: '100%' }}>
          <li>
            <NavLink activeClassName='active' to='/impressum'>Impressum</NavLink>
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
