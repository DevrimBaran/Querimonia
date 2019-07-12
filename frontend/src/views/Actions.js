/**
 * This class creates the Actions view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

import { fetchData } from '../redux/actions';
// eslint-disable-next-line
import { BrowserRouter as Router, Link } from 'react-router-dom';
import Input from './../components/Input';
import Action from './partials/Action';

import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';
import Filter from './../components/Filter';
import Pagination from './../components/Pagination';

class Actions extends Component {
  componentDidMount = () => {
    this.props.dispatch(fetchData('actions'));
  }

  renderList = () => {
    return (<Block>
      <Row vertical>
        <Filter endpoint='actions' />
        <div className='row flex-row height' >
          <Link to='/actions/0'><Input type='button' value='Neue Aktion' /></Link>
        </div>
        <Content className='padding'>
          {this.props.fetching
            ? (<div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>)
            : (this.props.data && this.props.data.ids.map(id => Action.List(this.props.data.byId[id])))
          }
        </Content>
        <Pagination endpoint='actions' />
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
            endpoint: 'actions',
            id: id
          });
        }
      }
    }
    let single = this.props.match.params.id && this.props.data.active;
    return (
      <React.Fragment>
        {single ? (
          Action.Single(this.props.data.active, this.props.dispatch)
        ) : (
          this.renderList()
        )}

      </React.Fragment>
    );
  }
}

const mapStateToProps = (state, props) => ({ ...state['actions'] });

export default connect(mapStateToProps)(Actions);
