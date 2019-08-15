/**
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { BrowserRouter as Router, Route, NavLink } from 'react-router-dom';
import { connect } from 'react-redux';

import View from './components/View';

import Home from './views/Home';
import Import from './views/Import';
import WordVectors from './views/WordVectors';
import TagCloud from './views/TagCloud';
import Impressum from './views/Impressum';
import Complaints from './views/partials/Complaints';
import Components from './views/partials/Component';
import Config from './views/partials/Config';

import logo from './assets/img/StuproLogo2.svg';
import OpenApi from './components/OpenApi';
import Api from './utility/Api';
import { ErrorPopupComponent } from './components/ErrorPopup';

function init () {
  return (dispatch, getState) => {
    Api.get('/api/config/allExtractors', {})
      .then((data) => {
        dispatch({
          type: 'INIT_EXTRACTORS',
          data: data
        });
      });
    Api.get('/api/config/current', {})
      .then((data) => {
        dispatch({
          type: 'CURRENT_CONFIG',
          data: data
        });
      });
  };
}

class App extends Component {
  componentDidMount = () => {
    this.props.dispatch(init());
  }
  render () {
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
              <NavLink activeClassName='active' to='/components'>Regeln</NavLink>
            </li>
            <li>
              <NavLink activeClassName='active' to='/config'>Konfigurationen</NavLink>
            </li>
            <li>
              <NavLink activeClassName='active' to='/wordvectors'>Wortvektoren</NavLink>
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
        <ErrorPopupComponent />

        <View exact path='/' component={Home} />
        <View endpoint='complaints' path='/complaints/:id?' stateToProps={(state) => ({ complaintStuff: state.complaintStuff })} component={Complaints} />
        <View endpoint='components' path='/components/:id?' component={Components} />
        <View endpoint='config' path='/config/:id?' stateToProps={(state) => ({ allExtractors: state.allExtractors })} component={Config} />
        <View path='/import' component={Import} />
        {/*
        <View endpoint='complaints' path='/complaints/:id?' component={Complaints} />
        <Route exact path='/' component={Home} />
        <Route path='/templates/:id?' component={TemplatesComponent >
        <Route path='/config/:id?' component={Config} />
        <Route path='/import' component={Import} />
        <Route path='/export' component={Export} />
        <Route path='/statistics' component={Statistics} />
        */}
        <Route path='/wordvectors' component={WordVectors} />
        <Route path='/tagcloud' component={TagCloud} />
        <Route path='/impressum' foo='bar' component={Impressum} />
      </Router>
    );
  }
}

export default connect()(App);
