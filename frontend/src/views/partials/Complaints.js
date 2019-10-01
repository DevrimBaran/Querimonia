/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

import { changeEntity, refreshComplaint, remove } from '../../redux/actions/';

import localize from '../../utility/date';
import Row from '../../components/Row';
import Grid from '../../components/Grid';
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
        className='crone'
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
        <th>Nr.</th>
        <th>Status</th>
        <th>Vorschau</th>
        <th>Emotion / Sentiment</th>
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
    return api.get(`/api/complaints/xml`, query, null, 'blob');
  };
  return <DownloadButton name={`Beschwerden${Date.now()}.xml`} type='text/xml; charset=utf-8' icon='fas fa-download'
    onClick={xml}>Beschwerden herunterladen</DownloadButton>;
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
    return api.get(`/api/complaints/${data.id}/xml`, {}, null, 'blob');
  };
  return (
    <tr className='pointer' key={data.id} onClick={helpers.edit}>
      <td>{data.id}</td>
      <td>{states[data.state]}</td>
      <td>{data.preview}&hellip;</td>
      <td>
        <span>
          {data.sentiment.emotion.value !== 'Unbekannt' ? data.sentiment.emotion.value : '---'}<br />
          {data.sentiment.tendency !== -2 ? <Sentiment tendency={data.sentiment.tendency} /> : '---'}
        </span>
      </td>
      <td>{data.properties.map((properties) => properties.value !== 'Unbekannt' ? (properties.value + ' (' + Math.round((properties.probabilities[properties.value] * 100)) + '%)') : '---').join(', ')}</td>
      <td>{localize(data.receiveDate)} {data.receiveTime}</td>
      <th>
        <Button title='Erneut auswerten' disabled={data.state === 'ANALYSING'}
          icon={data.state === 'ANALYSING' ? 'fas fa-sync fa-spin' : 'fas fa-sync'} onClick={refresh} />
        <DownloadButton name={`Anliegen ${data.id}.xml`} type='text/xml; charset=utf-8'
          disabled={data.state === 'ANALYSING' || data.state === 'ERROR'} icon='fas fa-file-download'
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
  const error =
    active.state === 'ANALYSING' ||
    active.state === 'ERROR';
  const disabled =
    active.state === 'CLOSED' || error;
  return (
    <div className='builderBlock'>
      <Row vertical>
        {error ? (
          <Grid rows='50% 50%' columns='100%' style={{ margin: 'auto' }}>
            <Button
              title='Erneut auswerten'
              icon='fas fa-sync'
              disabled={active.state === 'ANALYSING'}
              onClick={(e) => dispatch(refreshComplaint(active.id))}
            >Erneut auswerten</Button>
            <Button
              title='Beschwerde löschen'
              icon='fa fa-trash-alt'
              confirm='Wollen sie das Element wirklich löschen?'
              onClick={(e) => dispatch(remove('complaints', active.id))}
            >Beschwerde löschen</Button>
          </Grid>
        ) : (
          <React.Fragment>
            <h6 className='center'>Antwort</h6>
            <TextBuilder disabled={disabled} />
          </React.Fragment>
        )}
      </Row>
      <Row vertical style={{ maxWidth: '40rem' }}>
        <h6 className='center'>Meldetext</h6>
        <Content>
          <Tabbed active={error ? 1 : 0} >
            {!error && (
              <div label='Überarbeitet'>
                <TaggedText disabled={disabled} active={active} dispatch={dispatch}
                  className='justify'
                  text={helpers.props.complaintStuff.text}
                  entities={helpers.props.complaintStuff.entities} />
                <Button
                  className='plus-item'
                  title='Neue Entität hinzufügen'
                  onClick={e => EditEntityModal.open(e)}
                  icon={'fas fa-plus-circle fa-2x'}
                  data-start='0'
                  data-end='0'
                  data-label=''
                  data-id=''
                  data-mode='add'
                />
              </div>
            )}
            <div label='Original'>
              <p className='justify'>{helpers.props.complaintStuff.text}</p>
              <Button
                className='plus-item fa-2x'
                title='Text kopieren'
                onClick={e => navigator.clipboard.writeText(helpers.props.complaintStuff.text)}
                icon={['fas fa-circle', 'fas fa-copy fa-1x white']}
              />
            </div>
            <div label='Log'>
              {helpers.props.complaintStuff.log.map((entry, i) => (
                <div key={i} className='log'>
                  <span className='category'>[{entry.category}]</span>
                  <span className='datetime'> {entry.date} {entry.time}</span>
                  <br />
                  <p>{entry.message}</p>
                </div>
              ))}
              <Button
                className='plus-item'
                title='Ans Ende springen'
                // onClick={e => e.target.parentNode.scroll}
                onClick={e => e.target.parentElement.scrollTop = e.target.parentElement.scrollHeight}
                icon={'fas fa-chevron-circle-down fa-2x'}
              />
            </div>
          </Tabbed>
        </Content>
        <EditEntityModal active={active} dispatch={dispatch} text={helpers.props.complaintStuff.text}
          entities={Object.values(helpers.props.complaintStuff.entities.byId)} />
        <EditDetailsModal active={active} dispatch={dispatch} complaintStuff={helpers.props.complaintStuff} />
        <Collapsible label='Details' />
        <ListTable style={{ paddingRight: '3.25rem' }} styles={[{ paddingRight: '1rem', fontWeight: 'bold' }, { width: '100%' }]} data={[
          ['Konfiguration', (
            active.configuration ? (
              <Link to={'/configurations/' + active.configuration.id}>
                {active.configuration.name + ' (' + active.configuration.id + ')'}
              </Link>
            ) : (
              <span>gelöscht</span>
            )
          ), ''],
          ['Eingangsdatum', (localize(active.receiveDate)), ''],
          ['Eingangszeit', (active.receiveTime), ''],
          ['Status', (states[active.state]), ''],
          ['ID', (active.id), ''],
          {
            map: (cb) => (active.properties.map((property) => {
              return (
                [property.name,
                  property.value !== 'Unbekannt' ? (
                    <InformationTag text={property.value} probabilities={property.probabilities} />) : '---',
                    property.value !== 'Unbekannt' ? (<Button title='Kategorie bearbeiten' data-title='Kategorie bearbeiten' icon={'fas fa-edit'}
                      data-propertyindex={0} data-category={property.value} data-mode={'category'}
                      onClick={(e) => EditDetailsModal.open(e)} />) : null]
                  .map(cb)
              )
              ;
            }))
          },
          ['Sentiment',
            active.sentiment.tendency !== -2 ? (
              <InformationTag text={<Sentiment small tendency={active.sentiment.tendency} />}
                probabilities={active.sentiment.tendency} />) : '---', active.sentiment.tendency !== -2 ? (<Button title='Sentiment bearbeiten' data-title='Sentiment bearbeiten'icon={'fas fa-edit'} data-sentiment={active.sentiment ? active.sentiment.tendency : null} data-mode={'sentiment'} onClick={(e) => EditDetailsModal.open(e)} />) : null],
          ['Emotion',
          active.sentiment.emotion.value !== 'Unbekannt' ? (
              <InformationTag text={(active.sentiment ? active.sentiment.emotion.value : 0)}
                probabilities={active.sentiment.emotion.probabilities} />) : '---', active.sentiment.emotion.value !== 'Unbekannt' ? (<Button title='Emotion bearbeiten' data-title='Emotion bearbeiten' icon={'fas fa-edit'} data-emotion={
                active.sentiment ? active.sentiment.emotion.value : 0} data-mode={'emotion'} onClick={(e) => EditDetailsModal.open(e)} />) : null]
        ]} />
        <Collapsible label='Entitäten'
          disabled={helpers.props.complaintStuff.entities && helpers.props.complaintStuff.entities.ids && helpers.props.complaintStuff.entities.ids.length === 0}
          collapse={helpers.props.complaintStuff.entities && helpers.props.complaintStuff.entities.ids && helpers.props.complaintStuff.entities.ids.length === 0}
        />
        <Content style={{ marginRight: '1.25rem', overflow: 'auto scroll' }}>
          <ListTable data={sortedEntities(helpers.props.complaintStuff.entities, active.id, dispatch, disabled)}
            styles={[{
              fontWeight: 'bold'
            }, {
              padding: '0 1rem',
              width: '100%'
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
        <Content style={{ marginRight: '1.25rem', overflow: 'auto scroll' }}>
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
            style={{ width: '100%' }}
            styles={[{}, {}, {}]}
          />
        </Content>
      </Row>
    </div>
  );
}

export default { Header, List, Single, Overlay };
