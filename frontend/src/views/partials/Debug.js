/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

import Debug from '../../components/Debug';
// eslint-disable-next-line
import { BrowserRouter as Router, Link } from 'react-router-dom';

function List (data) {
  return (
    <React.Fragment key={data.id}>
      {
        data && (
          <Link to={'./' + data.id}>
            <Debug data={data} />
          </Link>
        )
      }
    </React.Fragment>
  );
}

function Single (data) {
  return (
    <Debug data={data} />
  );
}

export default { List, Single };
