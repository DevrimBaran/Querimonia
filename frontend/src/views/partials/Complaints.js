/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

import { changeEntityPreference, refreshComplaint } from '../../redux/actions/';
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
import Table from '../../components/Table';
import Input from '../../components/Input';

/**
 * extracts the key with the maximal value
 * @param {*} data Map (Key-Value)
 */
// eslint-disable-next-line
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
 * Changes the preference of an entity.
 * Other entities with the same label are set to false
 * @param {*} entity Entity where the preference is to be changed
 */
function changePreference (active, entities, dispatch, entity) {
  let entityOld = entities.flat().find((e) => {
    return e.label === entity.label && e.preferred === true;
  });
  dispatch(changeEntityPreference(active.id, entity, entityOld));
}

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
            <span className={(data.state === 'ERROR' || data.state === 'ANALYSING') ? 'action-button-disabled' : 'action-button'}>
              <i title='Erneut auswerten' className={(data.state === 'ERROR') ? 'fas fa-sync  fa-spin' : 'fas fa-sync'} onClick={refresh} style={(data.state === 'CLOSED' || data.state === 'ANALYSING') ? { cursor: 'default' } : null} />
            </span>
            {helpers && helpers.remove(data.id)}
          </div>
        </Row>
      </th>
      <td>{data.state}</td>
      <td>{data.preview}</td>
      <td>{data.sentiment.emotion.value}</td>
      <td><Sentiment fixed={null} tendency={data.sentiment.tendency} /></td>
      <td>{data.properties.map((properties) => properties.value + ' (' + (properties.probabilities[properties.value] * 100) + '%)').join(', ')}</td>
      <td>{data.receiveDate} {data.receiveTime}</td>
    </tr>
  );
}
function Single (active, dispatch, helpers) {
  if (!helpers.props.complaintStuff.done || !active) {
    return (
      <div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>
    );
  } else {
    console.log(active);
  }
  const editActive = true;
  const editFormActive = true;
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 className='center'>Anwort</h6>
          <TextBuilder />
        </Row>
      </Block>
      <Block>
        <Row vertical>
          <h6 className='center'>Meldetext</h6>
          <Content>
            <Tabbed vertical>
              <div label='Überarbeitet'>
                <TaggedText active={active} dispatch={dispatch} text={helpers.props.complaintStuff.text} entities={helpers.props.complaintStuff.entities} />
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
            </Tabbed>
          </Content>
          <EditEntityModal active={active} dispatch={dispatch} text={helpers.props.complaintStuff.text} entities={helpers.props.complaintStuff.entities} />
          <div style={{ display: 'none' }}>
            <div style={{ display: 'block', paddingTop: '10px', margin: 'auto', textAlign: 'center', borderTop: '1px solid lightGrey', width: '90%', marginTop: '10px' }}>
              <i style={editActive ? { color: 'rgb(36,191,64)', cursor: 'pointer' } : { color: 'rgb(158,72,59)', cursor: 'pointer' }}
                className='fas fa-plus-circle fa-2x'
                onClick={this.startEdit} />
              {editActive ? <i style={{ display: 'block', fontSize: '0.8em', marginTop: '3px' }}>Bitte gewünschten Abschnitt markieren</i> : null}
              {editFormActive ? <div style={{ marginTop: '5px' }}> <div>newEntityString</div>
                <b> Entität: </b>
                <Input type='select' id='chooseExtractor' />
                <br />
                <i style={{ color: 'green', cursor: 'pointer', padding: '5px' }} onClick={this.addEntity} className='far fa-check-circle fa-2x' />
                <i style={{ color: 'red', cursor: 'pointer', padding: '5px' }} onClick={this.abortEdit} className='far fa-times-circle fa-2x' /> </div> : null}
            </div>;
          </div>
          <Collapsible label='Details' />
          <div>
            <Table className='details-table'>
              <tbody>
                <tr>
                  <td>Konfiguration</td>
                  <td><Link to={'/config/' + active.configuration.id}>{active.configuration.name + ' (' + active.configuration.id + ')'}</Link></td>
                </tr>
                <tr>
                  <td>Eingangsdatum</td>
                  <td>{active.receiveDate}</td>
                </tr>
                <tr>
                  <td>Status</td>
                  <td>{active.state}</td>
                </tr>
                <tr>
                  <td>ID</td>
                  <td>{active.id}</td>
                </tr>
                {
                  // TODO tooltip with propabilities
                  active.properties &&
                  active.properties.map((property, i) => (
                    <tr key={'properties_' + i}>
                      <td>{property.name}</td>
                      <td>{property.value}</td>
                    </tr>
                  ))
                }
                <tr>
                  <td>Sentiment</td>
                  <td><Sentiment fixed={2} tendency={active.sentiment ? active.sentiment.tendency : null} /></td>
                </tr>
                <tr>
                  <td>Emotion</td>
                  <td>{active.sentiment ? active.sentiment.emotion.value : 0}</td>
                </tr>
              </tbody>
            </Table>
          </div>
          <Collapsible label='Entitäten'
            disabled={helpers.props.complaintStuff.entities && helpers.props.complaintStuff.entities.ids && helpers.props.complaintStuff.entities.ids.length === 0}
            collapse={helpers.props.complaintStuff.entities && helpers.props.complaintStuff.entities.ids && helpers.props.complaintStuff.entities.ids.length === 0}
          />
          <Content>
            <Table className='details-table'>
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Wert</th>
                  <th>Bevorzugt</th>
                </tr>
              </thead>
              <tbody>
                {(() => {
                  if (!(helpers.props.complaintStuff.entities && helpers.props.complaintStuff.entities.ids)) {
                    return <tr><td><i className='fa-spinner fa-spin fa fa-5x primary' /></td></tr>;
                  }
                  const entities = helpers.props.complaintStuff.entities.ids.reduce((entities, id) => {
                    const entity = helpers.props.complaintStuff.entities.byId[id];
                    entities[entity.label] || (entities[entity.label] = []);
                    entities[entity.label].push(entity);
                    return entities;
                  }, {});
                  const labels = Object.keys(entities).sort();
                  return labels.map(label => entities[label])
                    .map(entities => entities.sort((a, b) => a.start < b.start).map((entity, i) => (
                      <tr key={'' + entity.label + i}>
                        <td>{entity.label}</td>
                        <td><Tag text={entity.value} ids={[entity.id]} /></td>
                        <td><i key={i} onClick={() => changePreference(active, entities, dispatch, entity)} style={{ cursor: 'pointer', padding: '3px', color: (entity.preferred ? 'orange' : 'lightgray') }} className={'fas fa-crown'} /></td>
                      </tr>
                    )));
                })()}
              </tbody>
            </Table>
          </Content>
          <Collapsible label='Kombinationen'
            disabled={helpers.props.complaintStuff.combinations && helpers.props.complaintStuff.combinations.length === 0}
            collapse={helpers.props.complaintStuff.combinations && helpers.props.complaintStuff.combinations.length === 0}
          />
          <Content>
            <Table>
              <thead>
                <tr>
                  <th>Linie</th>
                  <th>Haltestelle</th>
                  <th>Ort</th>
                </tr>
              </thead>
              <tbody>
                {helpers.props.complaintStuff.combinations && helpers.props.complaintStuff.combinations.map && helpers.props.complaintStuff.combinations.map((combination, index) => (
                  <tr key={index}>
                    <td>{combination.line}</td>
                    <td>{combination.stop}</td>
                    <td>{combination.place}</td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </Content>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default { Header, List, Single };
