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
                    {data.sentiment.emotion.value + ' ' + (data.sentiment.emotion.probabilities[data.sentiment.emotion.value] * 100) + '%'}
                  </span>
                  &nbsp;
                  <span className='small' style={{ fontWeight: 'normal' }}>
                    {data.properties[0].value + ' ' + (data.properties[0].probabilities[data.properties[0].value] * 100) + '%'}
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
function Single (active, loadingEntitiesFinished, editCategorieBool, editSentimentBool, editCategorie, editSentiment, refreshEntities) {
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
                  <i id='subjects'>{active.properties[0].value}</i>
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
                  <i id='sentiments'>{active.sentiment.emotion.probabilities.value}</i>
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

export default { List, Single };
