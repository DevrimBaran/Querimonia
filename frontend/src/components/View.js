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
    const { path, component, endpoint, stateToProps, label, hotkey, menu, ...injected } = { ...this.props };
    const hotkeyIndex = label ? label.indexOf(hotkey) : -1;
    const hotkeyLabel = hotkeyIndex >= 0 ? (
      [
        <span key='0'>{label.substring(0, hotkeyIndex)}</span>,
        <span key='1' className='underline'>{label.substr(hotkeyIndex, 1)}</span>,
        <span key='2'>{label.substring(hotkeyIndex + 1)}</span>
      ]
    ) : label;
    if (endpoint) {
      return (
        <React.Fragment>
          {label && ReactDOM.createPortal((<NavLink activeClassName='active' to={'/' + (path || endpoint)}>{hotkeyLabel}</NavLink>), this.li)}
          <Route {...injected} exact path={'/' + (path || endpoint) + '/:id'} endpoint={endpoint} component={SingleView(endpoint, component, stateToProps)} />
          <Route {...injected} exact path={'/' + (path || endpoint)} endpoint={endpoint} component={ListView(endpoint, component, stateToProps, path || endpoint)} />
        </React.Fragment>
      );
    } else {
      return (
        <React.Fragment>
          {label && ReactDOM.createPortal((<NavLink activeClassName='active' to={path}>{hotkeyLabel}</NavLink>), this.li)}
          <Route {...injected} exact path={path} component={component} />
        </React.Fragment>
      );
    }
  }
}
export default connect()(View);
