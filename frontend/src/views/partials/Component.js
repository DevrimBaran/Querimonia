/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

import template from '../../redux/templates/components';

import Block from '../../components/Block';
import Row from '../../components/Row';
import Content from '../../components/Content';
import DeepObject from '../../components/DeepObject';
// eslint-disable-next-line
import { BrowserRouter as Router, Link, withRouter } from 'react-router-dom';

function Header () {
  return (
    <thead>
      <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Priorität</th>
        <th>Varianten</th>
        <th>Aktionen</th>
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
      <td style={{ textAlign: 'left' }}>{data.name}</td>
      <td>{data.priority}</td>
      <td>{data.texts.length}</td>
      <td>{data.actions.map((action) => action.name).join(', ')}</td>
    </tr>
  );
}

function Single (active, dispatch, helpers) {
  const save = (data) => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'components',
      data: data
    });
  };
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 className='center'>Regeln</h6>
          <div>
            <DeepObject save={save} filter={(key) => (key === 'id' || key === 'name' || key === 'priority')} data={active} template={template} />
          </div>
          <Content>
            <DeepObject save={save} filter={(key) => (key === 'rulesXml')} data={active} template={template} />
          </Content>
        </Row>
      </Block>
      <Block>
        <Row vertical>
          <h6 className='center'>Antwortvariationen</h6>
          <Content className='margin'>
            <DeepObject save={save} filter={(key) => (key === 'texts')} data={active} template={template} />
          </Content>
          <h6 className='center'>Aktionen</h6>
          <Content className='margin'>
            <DeepObject save={save} filter={(key) => (key === 'actions')} data={active} template={template} />
          </Content>
          <div className='center margin'>
            {helpers.save()}
          </div>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default { Header, List, Single };