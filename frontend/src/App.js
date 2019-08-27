/**
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { connect } from 'react-redux';

import View from './components/View';
import Login from './components/Login';

import Home from './views/Home';
import Import from './views/Import';
import WordVectors from './views/WordVectors';
import TagCloud from './views/TagCloud';
import Impressum from './views/Impressum';
import Stats from './views/Stats';
import Complaints from './views/partials/Complaints';
import Components from './views/partials/Component';
import Config from './views/partials/Config';

// import OpenApi from './components/OpenApi';
import Api from './utility/Api';
import { ErrorPopupComponent } from './components/ErrorPopup';

import logo from './assets/img/StuproLogo2.svg';

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
      <React.Fragment>
        <Router basename={basepath}>
          <nav id='menu'>
            <a href='/'>
              <img src={logo} id='logo' alt='logo' width='100%' />
            </a>
            <ul id='mainMenu' />
            <div>
              <Login />
            </div>
          </nav>
          <ErrorPopupComponent />
          <View role={['guest', 'user', 'admin']} exact path='/' component={Home} />
          <View label='Beschwerden' role={['user', 'admin']} endpoint='complaints' path='/complaints/:id?' stateToProps={(state) => ({ complaintStuff: state.complaintStuff })} component={Complaints} />
          <View label='Regeln' role={['admin']} endpoint='components' path='/components/:id?' component={Components} />
          <View label='Konfigurationen' role={['admin']} endpoint='config' path='/config/:id?' stateToProps={(state) => ({ allExtractors: state.allExtractors })} component={Config} />
          <View label='Import' role={['admin']} path='/import' component={Import} />
          <View label='Wortvektoren' role={['user', 'admin']} path='/wordvectors' component={WordVectors} />
          <View label='Wordhäufigkeit' role={['user', 'admin']} path='/tagcloud' component={TagCloud} />
          <View label='Statistiken' role={['user', 'admin']} path='/stats' component={Stats} />
          <View label='Impressum' role={['guest', 'user', 'admin']} path='/impressum' component={Impressum} />
        </Router>
      </React.Fragment>
    );
  }
}

export default connect()(App);
