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

// eslint-disable-next-line
import { BrowserRouter as Router, Link } from 'react-router-dom';
import EditableEntityText from './EditableEntityText';

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
      <td style={{ background: 'rgb(240, 240, 240)' }}><Link to={'/complaints/' + data.id}><h3>{data.id}</h3></Link></td>
      <td style={{ textAlign: 'left' }}><Link to={'/complaints/' + data.id}><p>{data.preview}</p></Link></td>
      <td style={{ background: 'rgb(240, 240, 240)' }}><Link to={'/complaints/' + data.id}>
        <span className='sentiment'>
          {getMaxKey(data.sentiment.emotion.probabilities)}
        </span></Link></td>
      <td><Link to={'/complaints/' + data.id}>
        <span role='img' className='emotion'>
          {sent < -0.6 ? '🤬' : sent < -0.2 ? '😞' : sent < 0.2 ? '😐' : sent < 0.6 ? '😊' : '😁'} ({sent.toFixed(2)})
        </span></Link></td>
      <td style={{ background: 'rgb(240, 240, 240)' }}><Link to={'/complaints/' + data.id}>
        <span className='small' style={{ fontWeight: 'normal' }}>
          {data.properties[0].value + ' (' + (data.properties[0].probabilities[data.properties[0].value] * 100) + '%)'}
        </span></Link></td>
      <td><Link to={'/complaints/' + data.id}><div className='date'><p>{data.receiveDate} {data.receiveTime}</p></div></Link></td>
    </tr>
  );
}

function Single (active, loadingEntitiesFinished, editCategorieBool, editSentimentBool, editCategorie, editSentiment, refreshEntities) {
  let sent = active && active.sentiment ? active.sentiment.tendency : null;
  return loadingEntitiesFinished ? (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 className='center'>Antwort</h6>
          <TextBuilder complaintId={active.id} />
        </Row>
      </Block>
      <Block>
        <Row vertical>
          <h6 className='center'>Meldetext</h6>
          <Content>
            <Tabbed style={{ height: '100%' }}>
              <div label='Überarbeitet'>
                <EditableEntityText taggedText={{ text: active.text, entities: active.entities }} id={active.id} active={active} refreshEntities={refreshEntities} />
              </div>
              <div label='Original'>
                {active.text}
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
            <b> Kategorie: </b>
            {
              !editCategorieBool ? (
                <span>
                  <span id='subjects'>{active.properties[0].value}</span>
                  <Tooltip htmlFor='subjects'>
                    {Object.keys(active.properties[0].probabilities).map(subject => <div key={subject}>{`${subject}: ${active.properties[0].probabilities[subject]}`} <br /></div>)}
                  </Tooltip>
                  {/* eslint-disable-next-line */}
                  <i className={'far fa-edit'} onClick={editCategorie.bind(this, active, false)} style={{ cursor: 'pointer', paddingLeft: '8px' }} />
                </span>
              ) : (
                <span>
                  <select id='chooseCategorie'>
                    {Object.keys(active.properties[0].probabilities).map(subject => subject === active.properties[0].value ? <option selected='selected'>{`${subject}`}</option> : <option >{`${subject}`}</option>)};
                  </select>
                  {/* eslint-disable-next-line */}
                  <i className={'far fa-check-circle fa-lg'} onClick={editCategorie.bind(this, active, true)} style={{ color: 'green', cursor: 'pointer', paddingLeft: '8px' }} />
                  {/* eslint-disable-next-line */}
                  <i className={'far fa-times-circle fa-lg'} onClick={editCategorie.bind(this, active, false)} style={{ color: 'red', cursor: 'pointer', paddingLeft: '8px' }} />
                </span>
              )
            }
            <br />
            <b> Sentiment: </b>
            {
              !editSentimentBool ? (
                <span>
                  <span id='sentiments'>{sent < -0.6 ? '🤬' : sent < -0.2 ? '😞' : sent < 0.2 ? '😐' : sent < 0.6 ? '😊' : '😁'} ({sent.toFixed(2)})</span>
                  <Tooltip htmlFor='sentiments'>
                    {Object.keys(active.sentiment.emotion.probabilities).map(sentiment => <div key={sentiment}>{`${sentiment}: ${active.sentiment.emotion.probabilities[sentiment]}`} <br /></div>)}
                  </Tooltip>
                  {/* eslint-disable-next-line */}
                  <i className={'far fa-edit'} onClick={editSentiment.bind(this, active, false)} style={{ cursor: 'pointer', paddingLeft: '8px' }} />
                </span>
              ) : (
                <span>
                  <select id='chooseSentiment'>
                    {Object.keys(active.sentiment.emotion.probabilities).map(sentiment => sentiment === active.sentiment.emotion.value ? <option selected='selected'>{`${sentiment}`}</option> : <option >{`${sentiment}`}</option>)};
                  </select>
                  {/* eslint-disable-next-line */}
                  <i className={'far fa-check-circle fa-lg'} onClick={editSentiment.bind(this, active, true)} style={{ color: 'green', cursor: 'pointer', paddingLeft: '8px' }} />
                  {/* eslint-disable-next-line */}
                  <i className={'far fa-times-circle fa-lg'} onClick={editSentiment.bind(this, active, false)} style={{ color: 'red', cursor: 'pointer', paddingLeft: '8px' }} />
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
                  return <li key={i}> {entity['label']} {': '}
                    <TaggedText taggedText={{
                      text: '' + active.text.substring(entity['start'], entity['end']),
                      entities: [{ label: entity['label'], start: 0, end: entity['end'] - entity['start'] }]
                    }} />
                  </li>;
                })
              }
            </ul>) : ''}
          </Content>
        </Row>
      </Block>)
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

export default { Header, List, Single };
