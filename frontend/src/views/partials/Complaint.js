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
        <span role='img' className='emotion'>
          {sent < -0.6 ? '🤬' : sent < -0.2 ? '😞' : sent < 0.2 ? '😐' : sent < 0.6 ? '😊' : '😁'} ({sent.toFixed(2)})
        </span></Link></td>
      <td><Link to={'/complaints/' + data.id}>
        <span className='small' style={{ fontWeight: 'normal' }}>{data.properties.map((properties) => properties.value + ' (' + (properties.probabilities[properties.value] * 100) + '%)').join(', ')}</span></Link></td>
      <td><Link to={'/complaints/' + data.id}><div className='date'><p>{data.receiveDate} {data.receiveTime}</p></div></Link></td>
    </tr>
  );
}

function Single (active, loadingEntitiesFinished, editCategorieBool, editTendencyBool, editEmotionBool, editCategorie, editTendency, editEmotion, refreshEntities) {
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
            <b> Kategorie: </b>
            {
              active.properties.map((properties, index) =>
                !editCategorieBool ? (
                  <span>
                    <span id='subjects'>{properties.value}</span>
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
                  <span id='sentiments'>{sent < -0.6 ? '🤬' : sent < -0.2 ? '😞' : sent < 0.2 ? '😐' : sent < 0.6 ? '😊' : '😁'}</span>
                  <Tooltip htmlFor='sentiments'>
                    {sent.toFixed(2)}
                  </Tooltip>
                  <i className={'far fa-edit'} onClick={editTendency.bind(this, active, false)} style={{ cursor: 'pointer', paddingLeft: '8px' }} />
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
                  return <li key={i}> {entity['label']} {': '}
                    <TaggedText taggedText={{
                      text: '' + active.text.substring(entity['start'], entity['end']),
                      entities: [{ label: entity['label'], start: 0, end: entity['end'] - entity['start'], color: entity['color'] }]
                    }} />
                    <Input key={i} type={'checkbox'} class={'preferEntityCheckbox'} />
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
