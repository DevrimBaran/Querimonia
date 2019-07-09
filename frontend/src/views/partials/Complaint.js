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
import ReactTooltip from 'react-tooltip';

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
function Single (active) {
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
          <Content style={{ flexBasis: '100%' }}>
            <Tabbed className='padding' style={{ height: '100%' }}>
              <div label='Überarbeitet'>
                <TaggedText taggedText={{ text: active.text, entities: active.entities }} id={active.id} editable />
              </div>
              <div label='Original'>
                {active.text}
              </div>
            </Tabbed>
          </Content>
          <Collapsible label='Details' style={{ minHeight: '130px' }}>
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
            <i data-tip data-for='subjects'>{active.subject.value}</i>
            <br />
            <b> Sentiment: </b>
            <i data-tip data-for='sentiments'>{active.sentiment.value}</i>
            <br />
            <ReactTooltip id='subjects' aria-haspopup='true'>
              {Object.keys(active.subject.probabilities).map(subject => <div>{`${subject}: ${active.subject.probabilities[subject]}`} <br /></div>)}
            </ReactTooltip>
            <ReactTooltip id='sentiments' aria-haspopup='true'>
              {Object.keys(active.sentiment.probabilities).map(sentiment => <div>{`${sentiment}: ${active.sentiment.probabilities[sentiment]}`} <br /></div>)}
            </ReactTooltip>
          </Collapsible>
          <Collapsible label='Entitäten'>
            <ul>
              {
                active.entities.map((entity, i) => {
                  return <li key={i}> {entity['label']} {': '}
                    <TaggedText taggedText={{
                      text: '' + active.text.substring(entity['start'], entity['end']),
                      entities: [{ label: entity['label'], start: 0, end: entity['end'] - entity['start'] }]
                    }} />
                  </li>;
                })
              }
            </ul>
          </Collapsible>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default { List, Single };
