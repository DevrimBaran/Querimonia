/**
 * This class creates the Templates view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

// eslint-disable-next-line
import { BrowserRouter as Router, Link } from 'react-router-dom';

import { fetchData } from '../redux/actions';

import Template from './partials/Template';

import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';
import Filter from './../components/Filter';
import Pagination from './../components/Pagination';
import Input from './../components/Input';

class Templates extends Component {
  componentDidMount = () => {
    this.props.dispatch(fetchData('templates'));
  }
  renderList = () => {
    return (<Block>
      <Row vertical>
        <Filter endpoint='templates' />
        <div className='row flex-row height' >
          <Link to='/templates/0'><Input type='button' value='Neues Template' /></Link>
        </div>
        <Content className='padding'>
          {this.props.data.fetching
            ? (<div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>)
            : (this.props.data && this.props.data.ids.map(id => Template.List(this.props.data.byId[id])))
          }
        </Content>
        <Pagination endpoint='templates' />
      </Row>
    </Block>);
  };

  render () {
    const id = parseInt(this.props.match.params.id);
    if (this.props.match.params.id) {
      if (!this.props.data.active || id !== this.props.data.active.id) {
        if (!this.props.data.fetching) {
          this.props.dispatch({
            type: 'SET_ACTIVE',
            endpoint: 'templates',
            id: id
          });
        }
      }
    }
    let single = this.props.match.params.id && this.props.data.active;
    return (
      <React.Fragment>
        {single ? (
          Template.Single(this.props.data.active, this.props.dispatch)
        ) : (
          this.renderList()
        )}

      </React.Fragment>
    );
  }
}

const mapStateToProps = (state, props) => ({ data: state['templates'].data });

export default connect(mapStateToProps)(Templates);
