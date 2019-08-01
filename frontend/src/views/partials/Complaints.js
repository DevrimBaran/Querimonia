/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

import template from '../../redux/templates/config';

import Block from '../../components/Block';
import Row from '../../components/Row';
import Content from '../../components/Content';
import DeepObject from '../../components/DeepObject';

// eslint-disable-next-line
import { BrowserRouter as Router, Link, withRouter } from 'react-router-dom';

/**
 * extracts the key with the maximal value
 * @param {*} data Map (Key-Value)
 */
function getMaxKey (data) {
  let keys = Object.keys(data);
  if (keys.length === 0) {
    return ('---');
  }
  let maxVal = data[keys[0]];
  let maxIndex = 0;
  for (let i = 1; i < keys.length; i++) {
    if (data[keys[i]] > maxVal) {
      maxVal = data[keys[i]];
      maxIndex = i;
    }
  }
  return (keys[maxIndex]);
}

function getSentiment (sentiment) {
  const tendency = sentiment && sentiment.tendency ? sentiment.tendency : 0;
  if (tendency < -0.6) {
    return '🤬 (' + tendency.toFixed(2) + ')';
  } else if (tendency < -0.2) {
    return '😞 (' + tendency.toFixed(2) + ')';
  } else if (tendency < 0.2) {
    return '😐 (' + tendency.toFixed(2) + ')';
  } else if (tendency < 0.6) {
    return '😊 (' + tendency.toFixed(2) + ')';
  } else {
    return '😁 (' + tendency.toFixed(2) + ')';
  }
}

function Header () {
  return (
    <thead>
      <tr style={{ filter: 'brightness(100%)' }}>
        <th> </th>
        <th>Anliegen</th>
        <th>Vorschau</th>
        <th>Emotion</th>
        <th>Sentiment</th>
        <th>Kategorie</th>
        <th>Datum</th>
      </tr>
    </thead>
  );
}

function List (data, dispatch, helpers) {
  return (
    <tr key={data.id}>
      <td>
        {helpers.edit(data.id)}
        {helpers.copy(data.id)}
        {helpers.remove(data.id)}
      </td>
      <td><h3>{data.id}</h3></td>
      <td>{data.preview}</td>
      <td>{getMaxKey(data.sentiment.emotion.probabilities)}</td>
      <td>
        <span role='img' className='emotion'>
          {getSentiment(data.sentiment)}
        </span>
      </td>
      <td>{data.properties.map((properties) => properties.value + ' (' + (properties.probabilities[properties.value] * 100) + '%)').join(', ')}</td>
      <td>{data.receiveDate} {data.receiveTime}</td>
    </tr>
  );
}

function Single (active, dispatch, helpers) {
  const modifyActive = (data) => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'config',
      data: data
    });
  };
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 className='center'>Konfiguration</h6>
          <Content className='margin'>
            <DeepObject data={active} template={template(helpers.props.allExtractors)} save={modifyActive} />
          </Content>
          <div className='center margin'>
            {helpers.save()}
          </div>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default { Header, List, Single };
