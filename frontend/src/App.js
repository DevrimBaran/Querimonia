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
import Statistics from './views/Statistics';
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
            <a href={basepath}>
              <img src={logo} id='logo' alt='logo' width='100%' />
            </a>
            <ul id='mainMenu' />
            <Login />
          </nav>
          <ErrorPopupComponent />
          <View accessRole={['guest', 'user', 'admin']} exact path='/' component={Home} />
          <View label='Beschwerden' accessRole={['user', 'admin']} endpoint='complaints' path='/complaints/:id?' stateToProps={(state) => ({ complaintStuff: state.complaintStuff })} component={Complaints} />
          <View label='Regeln' accessRole={['admin']} endpoint='components' path='/components/:id?' component={Components} />
          <View label='Konfigurationen' accessRole={['admin']} endpoint='config' path='/config/:id?' stateToProps={(state) => ({ allExtractors: state.allExtractors })} component={Config} />
          <View label='Import' accessRole={['admin']} path='/import' component={Import} />
          <View label='Wortvektoren' accessRole={['user', 'admin']} path='/wordvectors' component={WordVectors} />
          <View label='Wordhäufigkeit' accessRole={['user', 'admin']} path='/tagcloud' component={TagCloud} />
          <View label='Statistiken' accessRole={['user', 'admin']} path='/stats' component={Statistics} />
          <View label='Impressum' accessRole={['guest', 'user', 'admin']} path='/impressum' component={Impressum} />
        </Router>
      </React.Fragment>
    );
  }
}

export default connect()(App);
