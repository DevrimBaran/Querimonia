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
import ErrorModal from './components/ErrorModal';

import logo from './assets/img/StuproLogo.svg';

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
    document.addEventListener('keydown', this.handleKeyDown);
  }
  handleKeyDown = (e) => {
    if (e.ctrlKey) {
      let prefix = '';
      if (window.location.pathname.startsWith('/dev')) {
        prefix = '/dev';
      } else if (window.location.pathname.startsWith('/mock')) {
        prefix = '/mock';
      }
      let preventDefault = true;
      switch (e.keyCode) {
        // B-Button-Event
        case 66 :
          window.location.replace(prefix + '/complaints');
          break;
        // R-Button-Event
        case 82 :
          window.location.replace(prefix + '/components');
          break;
        // K-Button-Event
        case 75 :
          window.location.replace(prefix + '/config');
          break;
        // I-Button-Event
        case 73 :
          window.location.replace(prefix + '/import');
          break;
        // V-Button-Event
        case 86 :
          window.location.replace(prefix + '/wordvectors');
          break;
        // H-Button-Event
        case 72 :
          window.location.replace(prefix + '/tagcloud');
          break;
        // S-Button-Event
        case 83 :
          window.location.replace(prefix + '/stats');
          break;
        // ?-Button-Event
        case 63 :
          window.location.replace(prefix + '/impressum');
          break;
        default:
          preventDefault = false;
      }
      if (preventDefault) {
        e.preventDefault();
      }
    }
  }
  componentWillUnmount = () => {
    document.removeEventListener('keydown', this.handleKeyDown);
  }
  componentDidCatch = (errorString, errorInfo) => {
    this.error = { title: errorString, message: errorInfo, status: '0' };
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
          <ErrorModal error={this.error} />
          <View accessRole={['guest', 'user', 'admin']} exact path='/' component={Home} />
          <View label='Beschwerden' accessRole={['user', 'admin']} endpoint='complaints' path='/complaints/:id?' stateToProps={(state) => ({ complaintStuff: state.complaintStuff })} component={Complaints} />
          <View label='Regeln' accessRole={['admin']} endpoint='components' path='/components/:id?' component={Components} />
          <View label='Konfigurationen' accessRole={['admin']} endpoint='config' path='/config/:id?' stateToProps={(state) => ({ allExtractors: state.allExtractors })} component={Config} />
          <View label='Import' accessRole={['admin']} path='/import' component={Import} />
          <View label='Wortvektoren' accessRole={['user', 'admin']} path='/wordvectors' component={WordVectors} />
          <View label='Worthäufigkeit' accessRole={['user', 'admin']} path='/tagcloud' component={TagCloud} />
          <View label='Statistiken' accessRole={['user', 'admin']} path='/stats' component={Statistics} />
          <View label='Impressum' accessRole={['guest', 'user', 'admin']} path='/impressum' component={Impressum} />
        </Router>
      </React.Fragment>
    );
  }
}

export default connect()(App);
