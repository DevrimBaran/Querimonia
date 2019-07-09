/**
 * This class creates the Config view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

import { fetchData } from '../redux/actions';

import ConfigPartial from './partials/Config';

import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';
import Filter from './../components/Filter';
import Pagination from './../components/Pagination';
import Input from './../components/Input';

class Config extends Component {
  componentDidMount = () => {
    this.props.dispatch(fetchData('config'));
  }

  renderList = () => {
    return (<Block>
      <Row vertical>
        <Filter endpoint='config' />
        <div className='row flex-row height' >
          <Input type='button' />
        </div>
        <Content className='padding'>
          {this.props.fetching
            ? (<div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>)
            : (this.props.data && this.props.data.ids.map(id => ConfigPartial.List(this.props.data.byId[id])))
          }
        </Content>
        <Pagination endpoint='config' />
      </Row>
    </Block>);
  };

  render () {
    const id = parseInt(this.props.match.params.id);
    if (id) {
      if (!this.props.data.active || id !== this.props.data.active.id) {
        if (!this.props.data.fetching) {
          console.log(this.props.data.active.id, id, this.props.data.fetching);
          this.props.dispatch({
            type: 'SET_ACTIVE',
            endpoint: 'config',
            id: id
          });
        }
      }
    }
    let single = id && this.props.data.active;
    return (
      <React.Fragment>
        {single ? (
          ConfigPartial.Single(this.props.data.active, this.props.dispatch)
        ) : (
          this.renderList()
        )}

      </React.Fragment>
    );
  }
}

const mapStateToProps = (state, props) => ({ ...state['config'] });

export default connect(mapStateToProps)(Config);
