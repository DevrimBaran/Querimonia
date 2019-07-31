/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Route, Link } from 'react-router-dom';

import { fetchData } from '../redux/actions';

import Block from './Block';
import Row from './Row';
import Content from './Content';
import Input from './Input';
import Table from './Table';
import Filter from './Filter';
import Pagination from './Pagination';

const getEndpointView = (endpoint, partial) => {
  return connect((state, props) => ({ ...state[endpoint].data }))(class extends Component {
    componentDidMount = () => {
      this.props.dispatch(fetchData(endpoint));
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
                      {this.props.ids.map(id => partial.List(this.props.byId[id], this.props.dispatch))}
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
      console.log(this.props);
      const id = parseInt(this.props.match.params.id);
      if (this.props.match.params.id) {
        if (!this.props.active || id !== this.props.active.id) {
          if (!this.props.fetching) {
            console.log(this.props.active.id, id, this.props.fetching);
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
          {single ? (
            partial.Single(this.props.active, this.props.dispatch)
          ) : (
            this.renderList()
          )}
        </React.Fragment>
      );
    }
  });
};

class View extends Component {
  constructor (props) {
    super(props);
    this.state = {};
  }

  render () {
    const { path, component, endpoint, ...injected } = { ...this.props };
    if (endpoint) {
      return (
        <Route {...injected} path={path} endpoint={endpoint} component={getEndpointView(endpoint, component)} />
      );
    } else {
      return (
        <Route {...injected} path={path} component={component} />
      );
    }
  }
}

export default View;
