/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

// eslint-disable-next-line
import { BrowserRouter as Router, Link } from 'react-router-dom';

function List (data) {
  return (
    <React.Fragment key={data.id}>
      {
        data && (
          <Link to={'/actions/' + data.id}>
            <div className='Action'>
              <div className='floatLeft'>
                <p className='h3'>{data.name}</p>
                <p>ID: {data.id}</p>
              </div>
              <div className='floatRight'>
                {data.parameter['E-Mail'] && (<p>E-Mail: {data.parameter['E-Mail']}</p>)}
                {data.parameter['Wert'] && (<p>Gutscheinwert: {data.parameter['Wert']}</p>)}
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
    <React.Fragment key={data.id}>
      {
        data && (
          <div className='Action'>
            <div className='floatLeft'>
              <p className='h3'>{data.name}</p>
              <p>ID: {data.id}</p>
            </div>
            <div className='floatRight'>
              {data.parameter['E-Mail'] && (<p>E-Mail: {data.parameter['E-Mail']}</p>)}
              {data.parameter['Wert'] && (<p>Gutscheinwert: {data.parameter['Wert']}</p>)}
            </div>
          </div>
        )
      }
    </React.Fragment>
  );
}

export default { List, Single };
