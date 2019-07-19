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

function List (data) {
  return (
    <React.Fragment key={data.id}>
      {
        data && (
          <Link to={'/complaints/' + data.id}>
            <div className='complaintSummary'>
              <div className='title'>
                <h3><span>Anliegen {data.id} - </span>
                  <span className='sentiment' style={{ color: 'rgb( 200, 0, 0)' }}>
                    {data.sentiment.value + ' ' + (data.sentiment.probabilities[data.sentiment.value] * 100) + '%'}
                  </span>
                  &nbsp;
                  <span className='small' style={{ fontWeight: 'normal' }}>
                    {data.subject.value + ' ' + (data.subject.probabilities[data.subject.value] * 100) + '%'}
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
function Single (active, editCategorieBool, editSentimentBool, editCategorie, editSentiment, refreshEntities) {
  return (
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
                <TaggedText taggedText={{ text: active.text, entities: active.entities }} id={active.id} active={active} refreshEntities={refreshEntities} editable />
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
                  <i id='subjects'>{active.subject.value}</i>
                  <Tooltip htmlFor='subjects'>
                    {Object.keys(active.subject.probabilities).map(subject => <div key={subject}>{`${subject}: ${active.subject.probabilities[subject]}`} <br /></div>)}
                  </Tooltip>
                  {/* eslint-disable-next-line */}
                  <i className={'far fa-edit'} onClick={editCategorie.bind(this, active, false)} style={{ cursor: 'pointer', paddingLeft: '8px' }} />
                </span>
              ) : (
                <span>
                  <select id='chooseCategorie'>
                    {Object.keys(active.subject.probabilities).map(subject => subject === active.subject.value ? <option selected='selected'>{`${subject}`}</option> : <option >{`${subject}`}</option>)};
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
                  <i id='sentiments'>{active.sentiment.value}</i>
                  <Tooltip htmlFor='sentiments'>
                    {Object.keys(active.sentiment.probabilities).map(sentiment => <div key={sentiment}>{`${sentiment}: ${active.sentiment.probabilities[sentiment]}`} <br /></div>)}
                  </Tooltip>
                  {/* eslint-disable-next-line */}
                  <i className={'far fa-edit'} onClick={editSentiment.bind(this, active, false)} style={{ cursor: 'pointer', paddingLeft: '8px' }} />
                </span>
              ) : (
                <span>
                  <select id='chooseSentiment'>
                    {Object.keys(active.sentiment.probabilities).map(sentiment => sentiment === active.sentiment.value ? <option selected='selected'>{`${sentiment}`}</option> : <option >{`${sentiment}`}</option>)};
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
      </Block>
    </React.Fragment>
  );
}

export default { List, Single };
