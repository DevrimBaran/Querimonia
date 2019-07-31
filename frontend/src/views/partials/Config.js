/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

import { saveActive, setCurrentConfig } from '../../redux/actions';
import template from '../../redux/templates/config';

import Block from '../../components/Block';
import Row from '../../components/Row';
import Content from '../../components/Content';
import DeepObject from '../../components/DeepObject';
import Tabbed from './../../components/Tabbed';
import Debug from './../../components/Debug';

// eslint-disable-next-line
import { BrowserRouter as Router, Link, withRouter } from 'react-router-dom';

function Header () {
  return (
    <thead>
      <tr>
        <th />
        <th>ID</th>
        <th>Aktiv</th>
        <th>Name</th>
        <th>Extraktoren</th>
        <th>Klassififkatoren</th>
        <th>Stimmungsanalysator</th>
      </tr>
    </thead>
  );
}
const Copy = withRouter(class extends React.Component {
  onClick = () => {
    this.props.dispatch({
      type: 'SET_ACTIVE',
      endpoint: 'config',
      id: this.props.id
    });
    this.props.dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'config',
      data: { id: 0, name: '' }
    });
    this.props.history.push('/config/0');
  }
  render () {
    return (
      <i className='far fa-copy' onClick={this.onClick} />
    );
  }
});
function List (data, dispatch) {
  return (
    <tr key={data.id}>
      <td>
        <Link to={'/config/' + data.id}>
          <i className='far fa-edit' />
        </Link>
        <Copy id={data.id} dispatch={dispatch} />
        <i className='far fa-trash-alt' />
      </td>
      <td><h3>{data.id}</h3></td>
      <td>
        {
          data.active
            ? (<input defaultChecked type='radio' name='active' />)
            : (<input onClick={(e) => { dispatch(setCurrentConfig(data.id)); }} type='radio' name='active' />)
        }
      </td>
      <td>{data.name}</td>
      <td>{data.extractors.length}</td>
      <td>{data.classifiers[0].name}</td>
      <td>{data.sentimentAnalyzer.name}</td>
    </tr>
  );
}

function Single (active, dispatch) {
  const modifyActive = (data) => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'config',
      data: data
    });
  };
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 className='center'>Konfiguration</h6>
          <Content className='margin'>
            <Tabbed>
              <div label='rendered'>
                <DeepObject data={active} template={template} save={modifyActive} />
              </div>
              <div label='plain'>
                <Debug data={active} />
              </div>
              <div label='template'>
                <Debug data={template} />
              </div>
            </Tabbed>
          </Content>
          <div className='center margin'>
            <button
              type='button'
              className='important'
              disabled={active.saving}
              onClick={(e) => {
                dispatch(saveActive('config'));
              }}
            >Speichern</button>
          </div>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default { Header, List, Single };
