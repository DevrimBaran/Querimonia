/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

import { fetchStuff, refreshComplaint } from '../../redux/actions';

import Block from '../../components/Block';
import Row from '../../components/Row';
import Content from '../../components/Content';
import Collapsible from '../../components/Collapsible';
import Sentiment from '../../components/Sentiment';
import Tag from '../../components/Tag';
import Email from '../../components/Email';

// eslint-disable-next-line
import { BrowserRouter as Router, Link, withRouter } from 'react-router-dom';
import TaggedText from '../../components/TaggedText';
import Modal from '../../components/Modal';
import Tabbed from '../../components/Tabbed';
import TextBuilder from '../../components/TextBuilder';
import Table from '../../components/Table';
import Input from '../../components/Input';
import Button from '../../components/Button';

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

function Header () {
  return (
    <thead>
      <tr>
        <th> </th>
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
    <tr className={data.state !== 'ERROR' ? 'pointer' : ''} key={data.id} onClick={helpers && data.state !== 'ERROR' ? helpers.transitionTo('/complaints/' + data.id) : undefined}>
      <th>
        <Button disabled={data.state === 'CLOSED' || data.state === 'ANALYSING'} icon='fas fa-sync' onClick={refresh}>Erneut auswerten</Button>
        {helpers && helpers.remove(data.id)}
      </th>
      <td><h3>{data.id}</h3></td>
      <td>{data.state}</td>
      <td>{data.preview}</td>
      <td>{data.sentiment.emotion.value}</td>
      <td><Sentiment tendency={data.sentiment.tendency} /></td>
      <td>{data.properties.map((properties) => properties.value + ' (' + (properties.probabilities[properties.value] * 100) + '%)').join(', ')}</td>
      <td>{data.receiveDate} {data.receiveTime}</td>
    </tr>
  );
}
function Single (active, dispatch, helpers) {
  if ((helpers.props.complaintStuff.done && helpers.props.complaintStuff.id !== active.id) ||
    (!helpers.props.complaintStuff.done && helpers.props.complaintStuff.id === 0)) {
    dispatch(fetchStuff(active.id));
  };
  if (!helpers.props.complaintStuff.done) {
    return (
      <div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>
    );
  }
  const editActive = true;
  const editFormActive = true;
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 className='center'>Anwort</h6>
          <TextBuilder />
          <Modal htmlFor={'[complaint="' + active.id + '"]'}>
            <a href={'mailto:subject=Antwort%20auf%20Anliegen%20#' + active.id + '&body=' + encodeURIComponent('Lorem Ipsum')}>Mailto</a>
            <Email subject={'Querimonia - Abschluss #' + active.id} to='' name={'Abschluss_' + active.id} label='Download .eml Datei'>
              Test
            </Email>
          </Modal>
        </Row>
      </Block>
      <Block>
        <Row vertical>
          <h6 className='center'>Meldetext</h6>
          <Content>
            <Tabbed vertical>
              <div label='Überarbeitet'>
                <TaggedText text={helpers.props.complaintStuff.text} entities={helpers.props.complaintStuff.entities} />
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
            <Table>
              <tbody>
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
                  <td><Sentiment tendency={active.sentiment ? active.sentiment.tendency : 0} /></td>
                </tr>
                <tr>
                  <td>Emotion</td>
                  <td>{active.sentiment ? active.sentiment.emotion.value : 0}</td>
                </tr>
              </tbody>
            </Table>
          </div>
          <Collapsible label='Entitäten' />
          <Content>
            <Table>
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
                      </tr>
                    )));
                })()}
              </tbody>
            </Table>
          </Content>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default { Header, List, Single };
