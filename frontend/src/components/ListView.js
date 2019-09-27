import React, { Component } from 'react';
import { connect } from 'react-redux';

import { fetchData, remove } from '../redux/actions/';

import Block from './Block';
import Row from './Row';
import Content from './Content';
import Table from './Table';
import Filter from './Filter';
import Pagination from './Pagination';
import Button from './Button';

export default (endpoint, partial, stateToProps, path) => {
  return connect((state, props) => ({ filter: state[endpoint].filter, ...state[endpoint].data, ...(stateToProps ? stateToProps(state) : {}) }))(class extends Component {
    edit = (id) => {
      return (e) => {
        this.props.history.push('/' + path + '/' + id);
      };
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
        <Button icon='far fa-copy' onClick={dispatchCopy} href={'/' + path + '/0'} />
      );
    }
    componentDidMount = () => {
      this.props.dispatch(fetchData(endpoint));
    }
    render () {
      return (
        <Block>
          <Row vertical>
            <Filter endpoint={endpoint} />
            {partial.Overlay && (
              <div className='overlay'>
                {partial.Overlay(this.props.dispatch, this.props.filter)}
              </div>
            )}
            <Content className='padding'>
              {!this.props.fetching
                ? (
                  <Table>
                    {partial.Header()}
                    <tbody>
                      {this.props.ids.map(id => partial.List(this.props.byId[id], this.props.dispatch,
                        {
                          edit: this.edit(id),
                          copy: this.copy,
                          remove: <Button title='Löschen' icon='fa fa-trash-alt' confirm='Wollen sie das Element wirklich löschen?' onClick={(e) => this.props.dispatch(remove(endpoint, id))} />
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
  });
};
