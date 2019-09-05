/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

import { setCurrentConfig } from '../../redux/actions/';
import template from '../../redux/templates/config';

import Block from '../../components/Block';
import Row from '../../components/Row';
import Content from '../../components/Content';
import DeepObject from '../../components/DeepObject';

function Header () {
  return (
    <thead>
      <tr>
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

function List (data, dispatch, helpers) {
  return (
    <tr key={data.id}>
      <th>
        <Row>
          {data.id}
          <div>
            {helpers.edit(data.id)}
            {helpers.copy(data.id)}
            {helpers.remove(data.id)}
          </div>
        </Row>
      </th>
      <td>
        {
          data.active
            ? (<input defaultChecked type='radio' name='active' />)
            : (<input onClick={(e) => { dispatch(setCurrentConfig(data.id)); }} type='radio' name='active' />)
        }
      </td>
      <td style={{ textAlign: 'left' }}>{data.name}</td>
      <td>{data.extractors.length}</td>
      <td>{data.classifiers.map((c) => c.name).join(', ')}</td>
      <td>{data.sentimentAnalyzer.name}</td>
    </tr>
  );
}

function Single (active, dispatch, helpers) {
  const modifyActive = (data) => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'config',
      data: data
    });
  };
  const fix = () => {
    modifyActive({
      extractors: active.extractors.map(e => ({
        ...e,
        type: e.type === 'KIKUKO_TOOL' ? 'KIKUKO_PIPELINE' : e.type
      }))
    });
  };
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 className='center'>Konfiguration</h6>
          <Content className='margin'>
            <DeepObject data={active} template={template(helpers.props.allExtractors['KIKUKO_PIPELINE'])} save={modifyActive} />
          </Content>
          <div className='center margin'>
            {helpers.save()}
            <button onClick={fix}>Fix KIKUKO_TOOL</button>
          </div>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default { Header, List, Single };
