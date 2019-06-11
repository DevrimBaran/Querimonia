import React from 'react';

// eslint-disable-next-line
import { BrowserRouter as Router, Link } from 'react-router-dom';

function getMaxKey (obj) {
  let max = -1;
  let value = '';
  for (let key in obj) {
    if (obj[key] > max) {
      max = obj[key];
      value = key;
    }
  }
  return max > -1 ? value + ' ' + max + '%' : '';
}

function Complaint (data, index) {
  return (
    <React.Fragment key={index}>
      {
        data && (
          <Link to={'/complaints/' + data.complaintId}>
            <div className='complaintSummary'>
              <div className='title'>
                <h3><span>Anliegen {data.complaintId} -</span>
                  <span className='sentiment' style={{ color: 'rgb( 200, 0, 0)' }}>
                    {getMaxKey(data.probableSentiment)}
                  </span>
                  <span className='small' style={{ fontWeight: 'normal' }}>
                    {getMaxKey(data.probableSubject)}
                  </span>
                </h3>
              </div>
              <div className='date'><p>{data.receiveDate} {data.receiveTime}</p></div>
              <p>{data.preview}</p>
            </div>
          </Link>
        )
      }
    </React.Fragment>
  );
}

export default Complaint;
