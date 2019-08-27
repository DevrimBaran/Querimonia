import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';

import { fetchData, remove } from '../redux/actions/';

import Block from './Block';
import Row from './Row';
import Content from './Content';
import Input from './Input';
import Table from './Table';
import Filter from './Filter';
import Pagination from './Pagination';
import Button from './Button';

export default (endpoint, partial, stateToProps) => {
  return connect((state, props) => ({ ...state[endpoint].data, ...(stateToProps ? stateToProps(state) : {}) }))(class extends Component {
    // return connect((state, props) => ({ ...state[endpoint].data }))(class extends Component {
    constructor (props) {
      super(props);
      console.log('CONSTRUCTOR');
    };
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
    componentDidMount = () => {
      this.props.dispatch(fetchData(endpoint));
    }
    render () {
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
  });
};
