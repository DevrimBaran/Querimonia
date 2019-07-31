/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

// eslint-disable-next-line
import Debug from '../../components/Debug';
import DeepObject from '../../components/DeepObject';

// eslint-disable-next-line
import { BrowserRouter as Router, Link } from 'react-router-dom';

function Header (data, dispatch) {
  return (
    <thead>
      <tr>
        <th>ID</th>
      </tr>
    </thead>
  );
}

function List (data, dispatch) {
  return (
    <tr>
      <td>{data.id}</td>
    </tr>
  );
}

function Single (active, dispatch) {
  const data = {
    foo: 'foo',
    faa: 'faa',
    bar: 0
  };
  return (
    <DeepObject data={data} save={console.log} />
  );
}

export default { List, Single, Header };
