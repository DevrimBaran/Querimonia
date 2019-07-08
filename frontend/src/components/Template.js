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
    <React.Fragment key={data.componentId}>
      {
        data && (
          <Link to={'/templates/' + data.componentId}>
            <div className='Template'>
              <div className='floatLeft'>
                <p className='h3'>{data.componentName}</p>
                <p>ID: {data.componentId}</p>
              </div>
              <div className='floatRight'>
                <p>Antwortvariationen: {data.templateTexts.length}</p>
                <p>Entitäten: {data.templateTexts.requiredEntites ? data.templateTexts.requiredEntites.join(', ') : ''}</p>
              </div>
            </div>
          </Link>
        )
      }
    </React.Fragment>
  );
}

export default Template;
