/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

import { changeEntity, refreshComplaint } from '../../redux/actions/';

import localize from '../../utility/date';
import Row from '../../components/Row';
import Content from '../../components/Content';
import Collapsible from '../../components/Collapsible';
import Sentiment from '../../components/Sentiment';
import Tag from '../../components/Tag';
import InformationTag from '../../components/InformationTag';
import EditEntityModal from '../../components/EditEntityModal';
import EditDetailsModal from '../../components/EditDetailsModal';
// eslint-disable-next-line
import { BrowserRouter as Router, Link, withRouter } from 'react-router-dom';
import TaggedText from '../../components/TaggedText';
import Tabbed from '../../components/Tabbed';
import TextBuilder from '../../components/TextBuilder';
// import Table from '../../components/Table';
import ListTable from '../../components/ListTable';
// import Input from '../../components/Input';
// import Debug from '../../components/Debug';
import Button from '../../components/Button';
import DownloadButton from '../../components/DownloadButton';
import api from '../../utility/Api';

const states = {
  NEW: 'Neu',
  IN_PROGRESS: 'In Bearbeitung',
  CLOSED: 'Geschlossen',
  ANALYSING: 'In Analyse',
  ERROR: 'Fehler'
};

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
        <th>Aktionen</th>
      </tr>
    </thead>
  );
}

function Overlay (dispatch, filter) {
  const xml = (e) => {
    console.log(filter);
    const query = filter.reduce((obj, input) => {
      if (input.value) {
        obj[input.name] = input.value;
      }
      return obj;
    }, {});
    console.log(query);
    e.stopPropagation();
    return api.get(`/api/complaints/xml`, query, 'blob');
  };
  return <DownloadButton name={`Beschwerden${Date.now()}.xml`} type='text/xml; charset=utf-8' icon='fas fa-file-code'
    onClick={xml}>Gefilterte Beschwerden herunterladen</DownloadButton>;
}

function List (data, dispatch, helpers) {
  const refresh = (e) => {
    e.stopPropagation();
    if (data.state !== 'CLOSED' && data.state !== 'ANALYSING') {
      dispatch(refreshComplaint(data.id));
    }
    return false;
  };
  const xml = (e) => {
    e.stopPropagation();
    return api.get(`/api/complaints/${data.id}/xml`, {}, 'blob');
  };
  return (
    <tr className='pointer' key={data.id} onClick={helpers.edit}>
      <td>{data.id}</td>
      <td>{states[data.state]}</td>
      <td>{data.preview}</td>
      <td>{data.sentiment.emotion.value}</td>
      <td><Sentiment tendency={data.sentiment.tendency} /></td>
      <td>{data.properties.map((properties) => properties.value + ' (' + (properties.probabilities[properties.value] * 100) + '%)').join(', ')}</td>
      <td>{localize(data.receiveDate)} {data.receiveTime}</td>
      <th>
        <Button title='Erneut auswerten' disabled={data.state === 'ANALYSING'}
          icon={data.state === 'ANALYSING' ? 'fas fa-sync fa-spin' : 'fas fa-sync'} onClick={refresh} />
        <DownloadButton name={`Anliegen ${data.id}.xml`} type='text/xml; charset=utf-8'
          disabled={data.state === 'ANALYSING' || data.state === 'ERROR'} icon='fas fa-file-code'
          onClick={xml} />
        {helpers.remove}
      </th>
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
    <div className='builderBlock'>
      <Row vertical>
        <h6 className='center'>Antwort</h6>
        <TextBuilder disabled={disabled} />
      </Row>
      <Row vertical>
        <h6 className='center'>Meldetext</h6>
        <Content>
          <Tabbed>
            <div label='Überarbeitet'>
              <TaggedText disabled={disabled} active={active} dispatch={dispatch}
                text={helpers.props.complaintStuff.text}
                entities={helpers.props.complaintStuff.entities} />
            </div>
            <div label='Original'>
              <p>{helpers.props.complaintStuff.text}</p>
            </div>
            <div label='Log'>
              {helpers.props.complaintStuff.log.map((entry, i) => (
                <div key={i} className='log'>
                  <span className='category'>[{entry.category}]</span> <span
                    className='datetime'>{entry.date} {entry.time} UTC</span>
                  <br />
                  <div className='message'>
                    {entry.message}
                  </div>
                </div>
              ))}
            </div>
          </Tabbed>
        </Content>
        <EditEntityModal active={active} dispatch={dispatch} text={helpers.props.complaintStuff.text}
          entities={Object.values(helpers.props.complaintStuff.entities.byId)} />
        <EditDetailsModal active={active} dispatch={dispatch} complaintStuff={helpers.props.complaintStuff} />
        <Collapsible label='Details' />
        <ListTable styles={[{ paddingRight: '1em', fontWeight: 'bold' }]} data={[
          ['Konfiguration', (<Link to={'/configurations/' + active.configuration.id}>
            {active.configuration.name + ' (' + active.configuration.id + ')'}</Link>)],
          ['Eingangsdatum', (localize(active.receiveDate))],
          ['Eingangszeit', (active.receiveTime)],
          ['Status', (states[active.state])],
          ['ID', (active.id)],
          {
            map: (cb) => (active.properties.map((property) => {
              return (
                [property.name, <InformationTag text={property.value} probabilities={property.probabilities} />,
                  <Button title='Kategorie bearbeiten' data-title='Kategorie bearbeiten' icon={'fas fa-edit'}
                    data-propertyindex={0} data-category={property.value} data-mode={'category'}
                    onClick={(e) => EditDetailsModal.open(e)} />]
                  .map(cb)
              )
              ;
            }))
          },
          ['Sentiment',
            <InformationTag text={<Sentiment small tendency={active.sentiment ? active.sentiment.tendency : null} />}
              probabilities={active.sentiment.tendency} />,
            <Button title='Sentiment bearbeiten' data-title='Sentiment bearbeiten' icon={'fas fa-edit'}
              data-sentiment={
                active.sentiment ? active.sentiment.tendency : null} data-mode={'sentiment'}
              onClick={(e) => EditDetailsModal.open(e)} />],
          ['Emotion', <InformationTag text={(active.sentiment ? active.sentiment.emotion.value : 0)}
            probabilities={active.sentiment.emotion.probabilities} />, <Button title='Emotion bearbeiten' data-title='Emotion bearbeiten' icon={'fas fa-edit'} data-emotion={
            active.sentiment ? active.sentiment.emotion.value : 0} data-mode={'emotion'} onClick={(e) => EditDetailsModal.open(e)} />]
        ]} />
        <Collapsible label='Entitäten'
          disabled={helpers.props.complaintStuff.entities && helpers.props.complaintStuff.entities.ids && helpers.props.complaintStuff.entities.ids.length === 0}
          collapse={helpers.props.complaintStuff.entities && helpers.props.complaintStuff.entities.ids && helpers.props.complaintStuff.entities.ids.length === 0}
        />
        <Content>
          <ListTable data={sortedEntities(helpers.props.complaintStuff.entities, active.id, dispatch, disabled)}
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
          <ListTable
            header={['Linie', 'Haltestelle', 'Ort']}
            data={
              helpers.props.complaintStuff.combinations &&
              helpers.props.complaintStuff.combinations.map &&
              helpers.props.complaintStuff.combinations.map((combination) => (
                [
                  combination.Linie || '-',
                  combination.Haltestelle || '-',
                  combination.Ort || '-'
                ]
              ))
            }
            styles={[{}, {
              padding: '0 1em'
            }, {}]}
          />
        </Content>
      </Row>
    </div>
  );
}

export default { Header, List, Single, Overlay };
