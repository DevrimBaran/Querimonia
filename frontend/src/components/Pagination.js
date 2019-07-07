/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
// eslint-disable-next-line
import { BrowserRouter as Router, Link } from 'react-router-dom';

import Query from './../components/Query';

class Pagination extends Component {
  /*
  constructor (props) {
    super(props);
  }
  get(search, page, count) {

  }
  */
  render () {
    let search = new URLSearchParams(document.location.search);
    const page = parseInt(search.get('page')) || 0;
    const QueryLink = Query(Link);
    return (
      <div className='pagination center'>
        {page > 0 &&
          <QueryLink onClick={this.props.onClick} name='page' value={page - 1}>
            &lt;
          </QueryLink>
        }
        {page >= 2 && <QueryLink onClick={this.props.onClick} name='page' value={page - 2}>
          {page - 2}
        </QueryLink>
        }
        {page >= 1 && <QueryLink onClick={this.props.onClick} name='page' value={page - 1}>
          {page - 1}
        </QueryLink>
        }
        {page >= 0 && <QueryLink onClick={this.props.onClick} name='page' value={page}>
          {page}
        </QueryLink>
        }
        {<QueryLink onClick={this.props.onClick} name='page' value={page + 1}>
          {page + 1}
        </QueryLink>
        }
        {<QueryLink onClick={this.props.onClick} name='page' value={page + 2}>
          {page + 2}
        </QueryLink>
        }
        {<QueryLink onClick={this.props.onClick} name='page' value={page + 1}>
            &gt;
        </QueryLink>
        }
        {<QueryLink onClick={this.props.onClick} name='count' value='10'>10</QueryLink>}
        {<QueryLink onClick={this.props.onClick} name='count' value='25'>25</QueryLink>}
        {<QueryLink onClick={this.props.onClick} name='count' value='50'>50</QueryLink>}
      </div>
    );
  }
}

export default Pagination;
