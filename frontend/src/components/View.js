/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { connect } from 'react-redux';
// import { Route, NavLink, Redirect } from 'react-router-dom';
import { Route, NavLink } from 'react-router-dom';
import SingleView from './SingleView';
import ListView from './ListView';

class View extends Component {
  constructor (props) {
    super(props);
    this.li = document.createElement('li');
  }
  componentDidMount = () => {
    document.getElementById(this.props.menu || 'mainMenu').appendChild(this.li);
  }
  render () {
    const { path, component, endpoint, stateToProps, label, menu, ...injected } = { ...this.props };
    if (endpoint) {
      return (
        <React.Fragment>
          {label && ReactDOM.createPortal((<NavLink activeClassName='active' to={'/' + endpoint}>{label}</NavLink>), this.li)}
          <Route {...injected} exact path={'/' + endpoint + '/:id'} endpoint={endpoint} component={SingleView(endpoint, component, stateToProps)} />
          <Route {...injected} exact path={'/' + endpoint} endpoint={endpoint} component={ListView(endpoint, component, stateToProps)} />
        </React.Fragment>
      );
    } else {
      return (
        <React.Fragment>
          {label && ReactDOM.createPortal((<NavLink activeClassName='active' to={path}>{label}</NavLink>), this.li)}
          <Route {...injected} exact path={path} component={component} />
        </React.Fragment>
      );
    }
  }
}
export default connect()(View);
