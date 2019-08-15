/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Route, Link } from 'react-router-dom';

import { saveActive, fetchData, remove } from '../redux/actions';

import Block from './Block';
import Row from './Row';
import Content from './Content';
import Input from './Input';
import Table from './Table';
import Filter from './Filter';
import Pagination from './Pagination';
import Button from './Button';
import Debug from './Debug';
import Tabbed from './Tabbed';

class Fetching extends Component {
  componentDidMount = () => {
    console.log('FETCHING_DID_MOUNT');
    this.props.dispatch(fetchData(this.props.endpoint));
  }
  render () {
    return '';
  }
}

const getEndpointView = (endpoint, partial, stateToProps) => {
  return connect((state, props) => ({ ...state[endpoint].data, ...(stateToProps ? stateToProps(state) : {}) }))(class extends Component {
  // return connect((state, props) => ({ ...state[endpoint].data }))(class extends Component {
    constructor (props) {
      super(props);
      console.log('CONSTRUCTOR');
    }
    edit = (id) => {
      return (
        <Link to={'/' + endpoint + '/' + id}>
          <Button icon='far fa-edit'>Bearbeiten</Button>
        </Link>
      );
    }
    copy = (id) => {
      const dispatchCopy = () => {
        this.props.dispatch({
          type: 'SET_ACTIVE',
          endpoint: endpoint,
          id: id
        });
        this.props.dispatch({
          type: 'MODIFY_ACTIVE',
          endpoint: endpoint,
          data: { id: 0, name: '' }
        });
      };
      return (
        <Link to={'/' + endpoint + '/0'}>
          <Button icon='far fa-copy' onClick={dispatchCopy}>Kopieren</Button>
        </Link>
      );
    }
    remove = (id) => {
      return (
        <Link to={'/' + endpoint}>
          <Button icon='far fa-trash-alt' onClick={() => this.props.dispatch(remove(endpoint, id))}>Löschen</Button>
        </Link>
      );
    }
    save = () => {
      return (
        <button
          type='button'
          className='important'
          disabled={this.props.active.saving}
          onClick={(e) => {
            console.log('save', this.props.active);
            this.props.dispatch(saveActive(endpoint));
          }}
        >Speichern</button>
      );
    }
    renderList = () => {
      return (
        <Block>
          <Row vertical>
            <Filter endpoint={endpoint} />
            <div className='row flex-row height' >
              <Link to={endpoint + '/0'}><Input type='button' value='Neue Konfiguration' /></Link>
            </div>
            <Content className='padding'>
              {!this.props.fetching
                ? (
                  <Table>
                    {partial.Header()}
                    <tbody>
                      {this.props.ids.map(id => partial.List(this.props.byId[id], this.props.dispatch,
                        {
                          edit: this.edit,
                          copy: this.copy,
                          remove: this.remove,
                          transitionTo: (to) =>
                            (e) => {
                              this.props.history.push(to);
                            }
                        }
                      ))}
                    </tbody>
                  </Table>
                )
                : (<div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>)
              }
            </Content>
            <Pagination endpoint={endpoint} />
          </Row>
        </Block>
      );
    }
    render () {
      console.log('RENDER');
      const id = parseInt(this.props.match.params.id);
      if (this.props.match.params.id) {
        if (!this.props.active || id !== this.props.active.id) {
          if (!this.props.fetching) {
            console.log('set_active', this.props.active.id, id, this.props.fetching);
            this.props.dispatch({
              type: 'SET_ACTIVE',
              endpoint: endpoint,
              id: id
            });
          }
        }
      }
      let single = this.props.match.params.id && this.props.active;
      return (
        <React.Fragment>
          <Fetching endpoint={endpoint} dispatch={this.props.dispatch} />
          {single ? (
            <React.Fragment>
              {partial.Single(this.props.active, this.props.dispatch, { save: this.save, props: this.props })}
            </React.Fragment>
          ) : (
            <React.Fragment>
              <Fetching endpoint={endpoint} dispatch={this.props.dispatch} />
              {this.renderList()}
            </React.Fragment>
          )}
        </React.Fragment>
      );
    }
  });
};

class View extends Component {
  constructor (props) {
    super(props);
    this.state = {
      errorString: null,
      errorInfo: null
    };
  }
  componentDidCatch = (errorString, errorInfo) => {
    this.setState({ errorString, errorInfo });
  }
  render () {
    const { path, component, endpoint, stateToProps, ...injected } = { ...this.props };
    if (this.state.errorString) {
      return (
        <Tabbed>
          <Debug label='Nachricht' data={this.state.errorString} />
          <Debug label='Info' data={this.state.errorInfo} />
        </Tabbed>
      );
    }
    if (endpoint) {
      return (
        <Route {...injected} path={path} endpoint={endpoint} component={getEndpointView(endpoint, component, stateToProps)} />
      );
    } else {
      return (
        <Route {...injected} path={path} component={component} />
      );
    }
  }
}

export default View;
