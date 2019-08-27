/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { connect } from 'react-redux';
import { Route, Switch, NavLink } from 'react-router-dom';
import Debug from './Debug';
import Tabbed from './Tabbed';
import SingleView from './SingleView';
import ListView from './ListView';

const mainMenu = document.getElementById('mainMenu');
class View extends Component {
  constructor (props) {
    super(props);
    this.li = document.createElement('li');
    this.state = {
      errorString: null,
      errorInfo: null
    };
  }
  componentDidCatch = (errorString, errorInfo) => {
    this.setState({ errorString, errorInfo });
  }
  componentDidMount = () => {
    document.getElementById('mainMenu').appendChild(this.li);
  }
  render () {
    const { path, component, endpoint, stateToProps, label, ...injected } = { ...this.props };
    if (this.state.errorString) {
      return (
        <Tabbed>
          <Debug label='Nachricht' data={this.state.errorString} />
          <Debug label='Info' data={this.state.errorInfo} />
        </Tabbed>
      );
    }
    if (this.props.role && !this.props.role.includes(this.props.login.role)) {
      return <React.Fragment />;
    }
    console.log(mainMenu);
    if (endpoint) {
      return (
        <React.Fragment>
          {label && ReactDOM.createPortal((<NavLink activeClassName='active' to={'/' + endpoint}>{label}</NavLink>), this.li)}
          <Switch>
            <Route {...injected} path={'/' + endpoint + '/:id'} endpoint={endpoint} component={SingleView(endpoint, component, stateToProps)} />
            <Route {...injected} path={'/' + endpoint} endpoint={endpoint} component={ListView(endpoint, component, stateToProps)} />
          </Switch>
        </React.Fragment>
      );
    } else {
      return (
        <React.Fragment>
          {label && ReactDOM.createPortal((<NavLink activeClassName='active' to={path}>{label}</NavLink>), this.li)}
          <Route {...injected} path={path} component={component} />
        </React.Fragment>
      );
    }
  }
}

const mapSateToProps = (state, props) => {
  return { login: state.login };
};

export default connect(mapSateToProps)(View);
