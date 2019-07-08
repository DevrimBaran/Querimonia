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

class Config extends Component {
  componentDidMount = () => {
    this.props.dispatch(fetchData('config'));
  }
  renderSingle = (active) => {
    return (<React.Fragment>
      {ConfigPartial.List(active)}
    </React.Fragment>);
  };

  renderList = () => {
    return (<Block>
      <Row vertical>
        <Filter endpoint='config' />
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
    let active = this.props.match.params.id ? this.props.data.byId[this.props.match.params.id] : null;
    return (
      <React.Fragment>
        {active ? (
          this.renderSingle(active)
        ) : (
          this.renderList()
        )}

      </React.Fragment>
    );
  }
}

const mapStateToProps = (state, props) => ({ ...state['config'] });

export default connect(mapStateToProps)(Config);
