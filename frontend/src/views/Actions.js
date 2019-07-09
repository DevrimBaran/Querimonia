/**
 * This class creates the Actions view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

import { fetchData } from '../redux/actions';

// import Action from './partials/Action';
import DebugPartial from './partials/Debug';

import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';
import Filter from './../components/Filter';
import Pagination from './../components/Pagination';

class Actions extends Component {
  componentDidMount = () => {
    this.props.dispatch(fetchData('actions'));
  }
  renderSingle = (active) => {
    return (<React.Fragment>
      {DebugPartial.List(active)}
    </React.Fragment>);
  };

  renderList = () => {
    return (<Block>
      <Row vertical>
        <Filter endpoint='actions' />
        <Content className='padding'>
          {this.props.fetching
            ? (<div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>)
            : (this.props.data && this.props.data.ids.map(id => DebugPartial.List(this.props.data.byId[id])))
          }
        </Content>
        <Pagination endpoint='actions' />
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

const mapStateToProps = (state, props) => ({ ...state['actions'] });

export default connect(mapStateToProps)(Actions);
