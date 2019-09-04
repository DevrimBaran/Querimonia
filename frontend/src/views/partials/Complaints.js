/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

import { changeEntity, refreshComplaint } from '../../redux/actions/';

import localize from '../../utility/date';

import Block from '../../components/Block';
import Row from '../../components/Row';
import Content from '../../components/Content';
import Collapsible from '../../components/Collapsible';
import Sentiment from '../../components/Sentiment';
import Tag from '../../components/Tag';
import EditEntityModal from '../../components/EditEntityModal';
// eslint-disable-next-line
import { BrowserRouter as Router, Link, withRouter } from 'react-router-dom';
import TaggedText from '../../components/TaggedText';
import Tabbed from '../../components/Tabbed';
import TextBuilder from '../../components/TextBuilder';
// import Table from '../../components/Table';
import Liste from '../../components/List';
// import Input from '../../components/Input';
// import Debug from '../../components/Debug';
import Button from '../../components/Button';

const sortedEntities = (data, complaintId, dispatch, disabled) => {
  const entities = data.ids.map(id => data.byId[id]).sort((a, b) => {
    if (a.label === b.label) {
      if (a.start === b.start) {
        return (a.end < b.end ? -1 : 1);
      }
      return a.start < b.start ? -1 : 1;
    }
    return a.label < b.label ? -1 : 1;
  });

  return entities.map(entity => (
    [
      entity.label,
      <Tag text={entity.value} ids={[entity.id]} disabled={disabled} />,
      <Button
        disabled={disabled}
        onClick={() => dispatch(changeEntity(complaintId, entity.id, { preferred: !entity.preferred }))}
        style={{ color: (entity.preferred ? 'orange' : 'lightgray') }}
        icon={'fas fa-crown'}
      />
    ]
  ));
};

function Header () {
  return (
    <thead>
      <tr>
        <th>Anliegen</th>
        <th>Status</th>
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
  const refresh = (e) => {
    e.stopPropagation();
    if (data.state !== 'CLOSED' && data.state !== 'ANALYSING') {
      dispatch(refreshComplaint(data.id));
    }
    return false;
  };
  return (
    <tr className='pointer' key={data.id} onClick={helpers ? helpers.transitionTo('/complaints/' + data.id) : undefined}>
      <th>
        <Row>
          {data.id}
          <div>
            <Button title='Erneut auswerten' disabled={data.state === 'ANALYSING'} icon={data.state === 'ANALYSING' ? 'fas fa-sync fa-spin' : 'fas fa-sync'} onClick={refresh} />
            {helpers && helpers.remove(data.id)}
          </div>
        </Row>
      </th>
      <td>{data.state}</td>
      <td>{data.preview}</td>
      <td>{data.sentiment.emotion.value}</td>
      <td><Sentiment fixed={null} tendency={data.sentiment.tendency} /></td>
      <td>{data.properties.map((properties) => properties.value + ' (' + (properties.probabilities[properties.value] * 100) + '%)').join(', ')}</td>
      <td>{localize(data.receiveDate)} {data.receiveTime}</td>
    </tr>
  );
}
function Single (active, dispatch, helpers) {
  if (!helpers.props.complaintStuff.done || !active) {
    return (
      <div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>
    );
  }
  const disabled =
    active.state === 'CLOSED' ||
    active.state === 'ANALYSING' ||
    active.state === 'ERROR';
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 className='center'>Anwort</h6>
          <TextBuilder disabled={disabled} />
        </Row>
      </Block>
      <Block>
        <Row vertical>
          <h6 className='center'>Meldetext</h6>
          <Content>
            <Tabbed vertical>
              <div label='Überarbeitet'>
                <TaggedText disabled={disabled} active={active} dispatch={dispatch} text={helpers.props.complaintStuff.text} entities={helpers.props.complaintStuff.entities} />
              </div>
              <div label='Original'>
                {helpers.props.complaintStuff.text}
              </div>
              <div label='Log'>
                {helpers.props.complaintStuff.log.map((entry, i) => (
                  <div key={i} className='log'>
                    <span className='category'>[{entry.category}]</span> <span className='datetime'>{entry.date} {entry.time}</span>
                    <br />
                    <div className='message'>
                      {entry.message}
                    </div>
                  </div>
                ))}
              </div>
              {/* <div label='active'>
                <Debug data={active} />
              </div>
              <div label='stuff'>
                <Debug data={helpers.props.complaintStuff} />
              </div> */}
            </Tabbed>
          </Content>
          <EditEntityModal active={active} dispatch={dispatch} text={helpers.props.complaintStuff.text} entities={Object.values(helpers.props.complaintStuff.entities.byId)} />
          <Collapsible label='Details' />
          <Liste styles={[{ paddingRight: '1em', fontWeight: 'bold' }]} data={[
            ['Konfiguration', (<Link to={'/configuration/' + active.configuration.id}>{active.configuration.name + ' (' + active.configuration.id + ')'}</Link>)],
            ['Eingangsdatum', (localize(active.receiveDate))],
            ['Eingangszeit', (active.receiveTime)],
            ['Status', (active.state)],
            ['ID', (active.id)],
            { map: (cb) => (active.properties.map((property, i) => (
              [property.name, property.value].map(cb)
            ))) },
            ['Sentiment', (<Sentiment fixed={2} tendency={active.sentiment ? active.sentiment.tendency : null} />)],
            ['Emotion', (active.sentiment ? active.sentiment.emotion.value : 0)]
          ]} />
          <Collapsible label='Entitäten'
            disabled={helpers.props.complaintStuff.entities && helpers.props.complaintStuff.entities.ids && helpers.props.complaintStuff.entities.ids.length === 0}
            collapse={helpers.props.complaintStuff.entities && helpers.props.complaintStuff.entities.ids && helpers.props.complaintStuff.entities.ids.length === 0}
          />
          <Content>
            <Liste data={sortedEntities(helpers.props.complaintStuff.entities, active.id, dispatch, disabled)}
              styles={[{
                fontWeight: 'bold'
              }, {
                padding: '0 1em'
              }]}
            />
          </Content>
          <Collapsible label='Kombinationen'
            disabled={
              !(helpers.props.complaintStuff.combinations &&
              helpers.props.complaintStuff.combinations.map &&
              helpers.props.complaintStuff.combinations.length !== 0)
            }
            collapse={
              !(helpers.props.complaintStuff.combinations &&
              helpers.props.complaintStuff.combinations.map &&
              helpers.props.complaintStuff.combinations.length !== 0)
            }
          />
          <Content>
            <Liste data={
              helpers.props.complaintStuff.combinations &&
              helpers.props.complaintStuff.combinations.map &&
              helpers.props.complaintStuff.combinations.map((combination) => (
                [
                  combination.line,
                  combination.stop,
                  combination.place
                ]
              ))
            } />
          </Content>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default { Header, List, Single };
