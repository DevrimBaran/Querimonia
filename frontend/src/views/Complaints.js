/**
 * This class creates the Complaints view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

import { fetchData, fetchCurrentConfig } from '../redux/actions';

import Complaint from './partials/Complaint';

import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';
import Filter from './../components/Filter';
import Pagination from './../components/Pagination';

class Complaints extends Component {
  constructor (props) {
    super(props);
    this.state = {
      active: null
    };
  }
  componentDidMount = () => {
    this.props.dispatch(fetchData('complaints'));
    this.props.dispatch(fetchCurrentConfig());
  }

  renderSingle = (active) => {
    return (Complaint.Single(active));
  }

  update = () => {
    this.setState({ loading: true });
    setTimeout(() => {
      this.componentDidMount();
    }, 10);
  }

  renderList = () => {
    return (<Block>
      <Row vertical>
        <Filter endpoint='complaints' />
        <Content>
          {this.props.fetching
            ? (<div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>)
            : (this.props.data && this.props.data.ids.map(id => Complaint.List(this.props.data.byId[id])))
          }
        </Content>
        <Pagination endpoint='complaints' />
      </Row>
    </Block>);
  }

  render () {
    let active = this.props.match.params.id ? this.props.data.byId[this.props.match.params.id] : null;
    return (
      <React.Fragment>
        { active ? (
          this.renderSingle(active)
        ) : (
          this.renderList()
        ) }

      </React.Fragment>
    );
  }
}

const mapStateToProps = (state, props) => ({
  ...state.complaints
});

export default connect(mapStateToProps)(Complaints);
