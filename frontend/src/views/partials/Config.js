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
          <Link to={'/config/' + data.id}>
            <div className='Template'>
              <div className='floatLeft'>
                <p className='h3'>{data.name}</p>
                <p>ID: {data.id}</p>
              </div>
              <div className='floatRight'>
                <p>Extraktoren: {data.extractors.length}</p>
                <p>Klassifikator: {data.classifier.name}</p>
                <p>Stimmungsanalysator: {data.sentimentAnalyzer.name}</p>
              </div>
            </div>
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
