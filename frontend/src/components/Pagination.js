/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';
import { fetchData } from '../redux/actions/';
// eslint-disable-next-line
import { BrowserRouter as Router, Link } from 'react-router-dom';

class Pagination extends Component {
  onClick = (name, value) => {
    this.props.dispatch((dispatch, getState) => {
      dispatch({
        type: 'PAGINATION_CHANGE',
        endpoint: this.props.endpoint,
        name: name,
        value: value
      });
      dispatch(fetchData(this.props.endpoint));
    });
  }
  render () {
    let pagelinks = [];
    let lastPage = Math.max(Math.ceil(this.props.max / this.props.count) - 1, 0);
    if (lastPage < this.props.page) {
      console.log('redirect page 0', lastPage, this.props.page);
      this.props.dispatch({
        type: 'PAGINATION_CHANGE',
        endpoint: this.props.endpoint,
        name: 'page',
        value: 0
      });
      this.props.dispatch(fetchData(this.props.endpoint));
    }
    pagelinks.push(
      <li key={'previous'} className={this.props.page > 0 ? 'previous' : 'previous disabled'} type='button' name='page' onClick={this.props.page > 0 ? () => this.onClick('page', this.props.page - 1) : null}>
        <i className='fa fa-backward' />
      </li>
    );
    if (this.props.page > 2) {
      pagelinks.push(
        <li key={0} onClick={() => this.onClick('page', 0)}>
          1
        </li>
      );
    }
    for (let i = -2; i <= 2; i++) {
      let page = this.props.page + i;
      if (page >= 0 && page <= lastPage) {
        pagelinks.push(
          <li key={page} className={page === this.props.page ? 'current' : ''} onClick={() => this.onClick('page', page)} >
            {page + 1}
          </li>
        );
      }
    }
    if (this.props.page + 2 < lastPage) {
      pagelinks.push(
        <li key={lastPage} onClick={() => this.onClick('page', lastPage)} >
          {lastPage + 1}
        </li>
      );
    }
    pagelinks.push(
      <li key={'next'} className={this.props.page < lastPage ? 'next' : 'next disabled'} onClick={this.props.page < lastPage ? () => this.onClick('page', this.props.page + 1) : null} >
        <i className='fa fa-forward' />
      </li>
    );
    return (
      <div className='pagination'>
        <ul>
          {pagelinks}
        </ul>
        <span>
          ({this.props.page * this.props.count + 1}
          -
          {Math.min((this.props.page + 1) * this.props.count, this.props.max)}
          /
          {this.props.max})
          pro Seite:&nbsp;
          <span className={this.props.count === 10 ? 'current count' : 'count'} onClick={() => this.onClick('count', 10)}>10</span>,&nbsp;
          <span className={this.props.count === 25 ? 'current count' : 'count'} onClick={() => this.onClick('count', 25)}>25</span>,&nbsp;
          <span className={this.props.count === 50 ? 'current count' : 'count'} onClick={() => this.onClick('count', 50)}>50</span>
        </span>
      </div>
    );
  }
}

const mapStateToProps = (state, props) => ({ ...state[props.endpoint].pagination });

export default connect(mapStateToProps)(Pagination);
