/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

// eslint-disable-next-line
import { BrowserRouter as Router, Link } from 'react-router-dom';
function Template (data, index) {
  return (
    <React.Fragment key={index}>
      {
        data && (
          <Link to={'/templates/' + data.componentId}>
            <div className='Template'>
              <p>Id: {data.componentId}</p>
              <p>Name: {data.componentName}</p>
              <p>Antwortvariationen: {data.templateTexts.length}</p>
              <p>Entitäten: {data.templateTexts.requiredEntites.join(', ')}</p>
            </div>
          </Link>
        )
      }
    </React.Fragment>
  );
}

export default Template;
