/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';
import { fetchData } from '../redux/actions';
// eslint-disable-next-line
import { BrowserRouter as Router, Link } from 'react-router-dom';

class Pagination extends Component {
  onClick = (e) => {
    this.props.dispatch((dispatch, getState) => {
      dispatch({
        type: 'PAGINATION_CHANGE',
        endpoint: this.props.endpoint,
        name: e.target.name,
        value: ~~e.target.value
      });
      dispatch(fetchData(this.props.endpoint));
    });
  }
  render () {
    let pagelinks = [];
    for (let i = -2; i <= 2; i++) {
      let page = this.props.page + i;
      if (page > 0 && page * this.props.count - this.props.max > 0) {
        pagelinks.push(
          <input key={page} type='button' name='page' onClick={this.onClick} value={page} />
        );
      }
    }
    return (
      <div className='pagination center'>
        {pagelinks}
        <input type='button' name='count' onClick={this.onClick} value={10} />
        <input type='button' name='count' onClick={this.onClick} value={25} />
        <input type='button' name='count' onClick={this.onClick} value={50} />
      </div>
    );
  }
}

const mapStateToProps = (state, props) => ({ ...state[props.endpoint].pagination });

export default connect(mapStateToProps)(Pagination);
