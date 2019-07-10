/**
 * This class creates the Config view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

import { fetchData, fetchCurrentConfig } from '../redux/actions';
// eslint-disable-next-line
import { BrowserRouter as Router, Link } from 'react-router-dom';
import Input from './../components/Input';
import ConfigPartial from './partials/Config';

import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';
import Filter from './../components/Filter';
import Pagination from './../components/Pagination';

class Config extends Component {
  componentDidMount = () => {
    this.props.dispatch(fetchData('config'));
    this.props.dispatch(fetchCurrentConfig());
  }

  renderList = () => {
    return (<Block>
      <Row vertical>
        <Filter endpoint='config' />
        <div className='row flex-row height' >
          <Link to='/config/0'><Input type='button' value='Neue Konfiguration' /></Link>
        </div>
        <Content className='padding'>
          {this.props.fetching
            ? (<div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>)
            : (this.props.data && this.props.data.ids.map(id => ConfigPartial.List(this.props.data.byId[id], this.props.currentConfig)))
          }
        </Content>
        <Pagination endpoint='config' />
      </Row>
    </Block>);
  };

  render () {
    const id = parseInt(this.props.match.params.id);
    if (this.props.match.params.id) {
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
    let single = this.props.match.params.id && this.props.data.active;
    return (
      <React.Fragment>
        {single ? (
          ConfigPartial.Single(this.props.data.active, this.props.dispatch, this.props.currentConfig)
        ) : (
          this.renderList()
        )}

      </React.Fragment>
    );
  }
}

const mapStateToProps = (state, props) => ({ ...state['config'], currentConfig: state.currentConfig });

export default connect(mapStateToProps)(Config);
