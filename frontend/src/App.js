/**
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { connect } from 'react-redux';

import View from './components/View';

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
import iaoPartner from './assets/img/iao.png';
import uniLogo from './assets/img/Uni_stuttgart_logo.svg';

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

function login () {
  return (dispatch) => {
    Api.fetch(process.env.REACT_APP_BACKEND_PATH + '/access', {}, 'post').then((response) => {
      dispatch({
        type: 'LOGIN',
        access: response.headers.get('Access')
      });
    });
  };
}

class App extends Component {
  componentDidMount = () => {
    this.props.dispatch(login());
    this.props.dispatch(init());
    document.addEventListener('keydown', this.handleKeyDown);
  }
  handleKeyDown = (e) => {
    if (e.target === document.body && e.ctrlKey) {
      let prefix = '';
      if (window.location.pathname.startsWith('/dev')) {
        prefix = '/dev';
      } else if (window.location.pathname.startsWith('/mock')) {
        prefix = '/mock';
      }
      let preventDefault = true;
      switch (e.code) {
        // B-Button-Event
        case 'KeyB' :
          window.location.replace(prefix + '/complaints');
          break;
        // R-Button-Event
        case 'KeyR' :
          window.location.replace(prefix + '/rules');
          break;
        // K-Button-Event
        case 'KeyK' :
          window.location.replace(prefix + '/configuration');
          break;
        // I-Button-Event
        case 'KeyI' :
          window.location.replace(prefix + '/import');
          break;
        // V-Button-Event
        case 'KeyV' :
          window.location.replace(prefix + '/wordvectors');
          break;
        // H-Button-Event
        case 'KeyH' :
          window.location.replace(prefix + '/word_frequency');
          break;
        // S-Button-Event
        case 'KeyS' :
          window.location.replace(prefix + '/statistics');
          break;
        // ?-Button-Event
        case 'Minus' :
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
  static getDerivedStateFromError = (error) => {
    console.log(error);
    return { title: '', message: '', status: '0' };
  }
  render () {
    let basepath;
    try {
      basepath = new URL(process.env.REACT_APP_BACKEND_PATH).pathname;
    } catch (e) {
      basepath = '/';
    }
    const { login } = { ...this.props };
    return (
      <React.Fragment>
        <Router basename={basepath}>
          <nav id='menu'>
            <a href={basepath}>
              <img src={logo} id='logo' alt='logo' width='100%' />
            </a>
            <div className='scrollable'>
              <ul id='mainMenu' />
              <ul id='bottomMenu'>
                <li className='image'>
                  <a href='https://www.iao.fraunhofer.de/' rel='noopener noreferrer' target='_blank'>
                    <img src={iaoPartner} alt='logo' />
                  </a>
                </li>
                <li className='image'>
                  <a href='https://www.iat.uni-stuttgart.de/' rel='noopener noreferrer' target='_blank'>
                    <img src={uniLogo} alt='logo' />
                  </a>
                </li>
              </ul>
            </div>
          </nav>
          <ErrorModal error={this.error} />
          {login.access >= -1 && (
            <React.Fragment>
              <View exact path='/' component={Home} />
              <View menu='bottomMenu' label='Impressum' path='/impressum' component={Impressum} />
            </React.Fragment>
          )}
          {login.access === 0 && (
            <React.Fragment>
              <View label='Import' path='/import' component={Import} />
              <View label='Beschwerden' endpoint='complaints' path='/complaints/:id?' stateToProps={(state) => ({ complaintStuff: state.complaintStuff })} component={Complaints} />
              <View label='Statistiken' path='/statistics' component={Statistics} />
            </React.Fragment>
          )}
          {login.access === 1 && (
            <React.Fragment>
              <View label='Import' path='/import' component={Import} />
              <View label='Beschwerden' endpoint='complaints' path='/complaints/:id?' stateToProps={(state) => ({ complaintStuff: state.complaintStuff })} component={Complaints} />
              <View label='Worthäufigkeit' path='/word_frequency' component={TagCloud} />
              <View label='Wortvektoren' path='/wordvectors' component={WordVectors} />
              <View label='Statistiken' path='/statistics' component={Statistics} />
              <View label='Regeln' endpoint='components' path='/rules/:id?' component={Components} />
              <View label='Konfigurationen' endpoint='config' path='/configuration/:id?' stateToProps={(state) => ({ allExtractors: state.allExtractors })} component={Config} />
            </React.Fragment>
          )}
        </Router>
      </React.Fragment>
    );
  }
}

const mapSateToProps = (state, props) => {
  return { login: state.login };
};

export default connect(mapSateToProps)(App);
