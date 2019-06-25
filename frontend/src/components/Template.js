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
          <Link to={'/templates/' + data.id}>
            <div className='Template'>
              Lorem Ipsum
            </div>
          </Link>
        )
      }
    </React.Fragment>
  );
}

export default Template;
