/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

import Block from './../../components/Block';
import Row from './../../components/Row';
import Content from './../../components/Content';
import TaggedText from './../../components/TaggedText';
import Collapsible from './../../components/Collapsible';
import Tabbed from './../../components/Tabbed';
import TextBuilder from './../../components/TextBuilder';
import Tooltip from './../../components/Tooltip';
import Input from '../../components/Input';
import Api from '../../utility/Api';

// eslint-disable-next-line
import { BrowserRouter as Router, Link } from 'react-router-dom';
import EditableEntityText from './EditableEntityText';
import Log from '../../components/Log';
import Combinations from '../../components/Combinations';

function Header () {
  return (
    <thead>
      <tr>
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

function List (data) {
  let sent = data.sentiment.tendency;
  return (
    <tr key={data.id}>
      <td><Link to={'/complaints/' + data.id}><h3>{data.id}</h3></Link></td>
      <td style={{ textAlign: 'left' }}><Link to={'/complaints/' + data.id}><p>{data.preview}</p></Link></td>
      <td><Link to={'/complaints/' + data.id}>
        <span className='sentiment'>
          {getMaxKey(data.sentiment.emotion.probabilities)}
        </span></Link></td>
      <td><Link to={'/complaints/' + data.id}>
        <span style={{ fontSize: '140%' }} role='img' className='emotion'>
          {getSmiley(sent, false)}
        </span></Link></td>
      <td><Link to={'/complaints/' + data.id}>
        <span className='small' style={{ fontWeight: 'normal' }}>{data.properties.map((properties) => (properties.probabilities[properties.value] ? properties.value : '---')).join(', ')}</span></Link></td>
      <td><Link to={'/complaints/' + data.id}><div className='date'><p>{data.receiveDate} {data.receiveTime}</p></div></Link></td>
    </tr>
  );
}

function Single (active, booleans, methods) {
  let loadingEntitiesFinished = booleans[0];
  let editCategorieBool = booleans[1];
  let editTendencyBool = booleans[2];
  let editEmotionBool = booleans[3];
  let refreshResponse = booleans[4];
  let editCategorie = methods[0];
  let editTendency = methods[1];
  let editEmotion = methods[2];
  let refreshEntities = methods[3];
  let sent = active && active.sentiment ? active.sentiment.tendency : null;
  return loadingEntitiesFinished ? (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 className='center'>Antwort</h6>
          <TextBuilder complaintId={active.id} refreshResponse={refreshResponse} />
        </Row>
      </Block>
      <Block>
        <Row vertical>
          <h6 className='center'>Meldetext</h6>
          <Content>
            <Tabbed style={{ height: '100%' }}>
              <div label='Überarbeitet'>
                <EditableEntityText taggedText={{ text: active.text, entities: active.entities }} complaintId={active.id} active={active} refreshEntities={refreshEntities} />
              </div>
              <div label='Original'>
                {active.text}
              </div>
              <div label='Logs'>
                <Log complaintId={active.id} />
              </div>
            </Tabbed>
          </Content>
          <Collapsible label='Details' />
          <div>
            <b>Eingangsdatum: </b>
            <TaggedText taggedText={{
              text: active.receiveDate,
              entities: [{ label: 'Eingangsdatum', start: 0, end: active.receiveDate.length }]
            }} />
            <br />
            <b> ID: </b>
            {active.id}
            <br />
            <b> Konfiguration: </b>
            <Link to={'/config/' + active.configuration.id}>{active.configuration.name + ' (' + active.configuration.id + ')'}</Link>
            <br />
            <b> Kategorie: </b>
            {
              active.properties.map((properties, index) =>
                !editCategorieBool ? (
                  <span>
                    <span id='subjects'>{(properties.probabilities[properties.value] ? properties.value : '---')}</span>
                    <Tooltip htmlFor='subjects'>
                      {Object.keys(properties.probabilities).map(subject => <div key={subject}>{`${subject}: ${properties.probabilities[subject]}`} <br /></div>)}
                    </Tooltip>
                    {/* eslint-disable-next-line */}
                  <i className={'far fa-edit'} onClick={editCategorie.bind(this, active, index, false)} style={{ cursor: 'pointer', paddingLeft: '8px' }} />
                  </span>
                ) : (
                  <span>
                    <select id='chooseCategorie'>
                      {Object.keys(properties.probabilities).map(subject => subject === properties.value ? <option selected='selected'>{`${subject}`}</option> : <option >{`${subject}`}</option>)};
                    </select>
                    {/* eslint-disable-next-line */}
                  <i className={'far fa-check-circle fa-lg'} onClick={editCategorie.bind(this, active, index, true)} style={{ color: 'green', cursor: 'pointer', paddingLeft: '8px' }} />
                    {/* eslint-disable-next-line */}
                  <i className={'far fa-times-circle fa-lg'} onClick={editCategorie.bind(this, active, index, false)} style={{ color: 'red', cursor: 'pointer', paddingLeft: '8px' }} />
                  </span>
                ))
            }
            <br />
            <b> Sentiment: </b>
            {
              !editTendencyBool ? (
                <span>
                  <span style={{ fontSize: '120%' }} id='sentiments' role='img'>{getSmiley(sent, false)}</span>
                  <Tooltip htmlFor='sentiments'>
                    {getSmiley(sent, true) + ' (' + sent.toFixed(2) + ')'}
                  </Tooltip>
                  <i className={'far fa-edit'} onClick={() => editTendency(active, false)} style={{ cursor: 'pointer', paddingLeft: '8px' }} />
                </span>
              ) : (
                <span>
                  <Input id='chooseTendency' type='number' min='-1' max='1' step='0.01' defaultValue={sent.toFixed(2)} />
                  {/* eslint-disable-next-line */}
                  <i className={'far fa-check-circle fa-lg'} onClick={editTendency.bind(this, active, true)} style={{ color: 'green', cursor: 'pointer', paddingLeft: '8px' }} />
                  {/* eslint-disable-next-line */}
                  <i className={'far fa-times-circle fa-lg'} onClick={editTendency.bind(this, active, false)} style={{ color: 'red', cursor: 'pointer', paddingLeft: '8px' }} />
                </span>
              )
            }
            <br />
            <b> Emotion: </b>
            {
              !editEmotionBool ? (
                <span>
                  <span id='emotions'>{getMaxKey(active.sentiment.emotion.probabilities)}</span>
                  <Tooltip htmlFor='emotions'>
                    {Object.keys(active.sentiment.emotion.probabilities).map(sentiment => <div key={sentiment}>{`${sentiment}: ${active.sentiment.emotion.probabilities[sentiment]}`} <br /></div>)}
                  </Tooltip>
                  {/* eslint-disable-next-line */}
                  <i className={'far fa-edit'} onClick={editEmotion.bind(this, active, false)} style={{ cursor: 'pointer', paddingLeft: '8px' }} />
                </span>
              ) : (
                <span>
                  <select id='chooseEmotion'>
                    {Object.keys(active.sentiment.emotion.probabilities).map(sentiment => sentiment === active.sentiment.emotion.value ? <option selected='selected'>{`${sentiment}`}</option> : <option >{`${sentiment}`}</option>)};
                  </select>
                  {/* eslint-disable-next-line */}
                  <i className={'far fa-check-circle fa-lg'} onClick={editEmotion.bind(this, active, true)} style={{ color: 'green', cursor: 'pointer', paddingLeft: '8px' }} />
                  {/* eslint-disable-next-line */}
                  <i className={'far fa-times-circle fa-lg'} onClick={editEmotion.bind(this, active, false)} style={{ color: 'red', cursor: 'pointer', paddingLeft: '8px' }} />
                </span>
              )
            }
          </div>
          <Collapsible label='Entitäten' />
          <Content>
            {active.entities.length > 0 ? (<ul>
              {
                active.entities.flat().sort((entity1, entity2) => {
                  const labelCompare = entity1.label && entity2.label ? (entity1.label).localeCompare(entity2.label) : entity1 ? -1 : entity2 ? 1 : 0;
                  return labelCompare === 0 ? entity1.start - entity2.start : labelCompare;
                }).map((entity, i) => {
                  console.log(entity);
                  return <li key={i}>
                    <i key={i} onClick={() => changePreference(active, refreshEntities, entity)} style={{ cursor: 'pointer', padding: '4px', color: (entity.preferred ? 'orange' : 'lightgray') }} className={'fas fa-crown'} />
                    {entity['label']} {': '}
                    <TaggedText taggedText={{
                      text: entity.value,
                      entities: [{ label: entity['label'], extractor: entity['extractor'], start: 0, end: entity.value.length, color: entity['color'] }]
                    }} />
                  </li>;
                })
              }
            </ul>) : ''}
          </Content>
          <Collapsible label='Kombinationen' />
          <Content>
            <Combinations complaintId={active.id} />
          </Content>
        </Row>
      </Block>
    </React.Fragment>
  ) : (<div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>);
}
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

/**
 * returns a representation of the tendency
 * @param {*} value tendency-value
 * @param {*} text boolean whether text or emoji should be displayed
 */
function getSmiley (value, text) {
  let emotions = [];
  if (text) {
    emotions = ['Glücklich', 'Fröhlich', 'Zufrieden', 'Erfreut', 'Neutral', 'Betrübt', 'Unzufrieden', 'Verärgert', 'Sauer'];
  } else {
    emotions = ['😍', '😁', '😀', '🙂', '😐', '😞', '☹️', '😠', '🤬'];
  }
  return emotions[(value > 0.8 ? 0 : value > 0.6 ? 1 : value > 0.4 ? 2 : value > 0.1 ? 3 : value > -0.2 ? 4 : value > -0.4 ? 5 : value > -0.6 ? 6 : value > -0.8 ? 7 : 8)];
}

/**
 * Changes the preference of an entity.
 * Other entities with the same label are set to false
 * @param {*} entity Entity where the preference is to be changed
 */
function changePreference (active, refreshEntities, entity) {
  let entityOld = active.entities.flat().find((e) => {
    return e.label === entity.label && e.preferred === true;
  });
  let query = entity;
  query.preferred = !entity.preferred;
  Api.put('/api/complaints/' + active.id + '/entities/' + entity.id, query).then((data) => {
    if (entityOld) {
      let query2 = entityOld;
      query2.preferred = false;
      Api.put('/api/complaints/' + active.id + '/entities/' + entityOld.id, query2).then((data) => {
        if (Array.isArray(data)) {
        // deep copy of data
          let entities = JSON.parse(JSON.stringify(data));
          // Updates the entity list and responses with the new values
          refreshEntities(active, entities);
        }
      });
    } else if (Array.isArray(data)) {
      // deep copy of data
      let entities = JSON.parse(JSON.stringify(data));
      // Updates the entity list and responses with the new values
      refreshEntities(active, entities);
    }
  });
}

export default { Header, List, Single };
