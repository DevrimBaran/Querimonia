/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

import { saveActive } from '../../redux/actions';

import CodeMirror from '../../components/CodeMirror';
import Block from '../../components/Block';
import Row from '../../components/Row';
import Content from '../../components/Content';
import Input from '../../components/Input';

// eslint-disable-next-line
import { BrowserRouter as Router, Link, withRouter } from 'react-router-dom';

function List (data, index) {
  return (
    <React.Fragment key={data.id}>
      {
        data && (
          <Link to={'/templates/' + data.id}>
            <div className='Template'>
              <div className='floatLeft'>
                <p className='h3'>{data.name}</p>
                <p>ID: {data.actionId}</p>
              </div>
              <div className='floatRight'>
                <p>Aktionscode: {data.actionCode}</p>
                <p>E-Mail: {data.parameters['E-Mail']}</p>
                <p>Wert: {data.parameters['Wert']}</p>
              </div>
            </div>
          </Link>
        )
      }
    </React.Fragment>
  );
}

function Single (active, dispatch) {
  const modify = (key, value) => {
    let modified;
    if (key === 'rulesXml' || key === 'name' || key === 'actionCode') {
      modified = {
        ...active,
        [key]: value
      };
    } else if (key === 'E-Mail' || key === 'Wert') {
      modified = {
        ...active,
        parameters: {
          ...active.parameters,
          [key]: value
        }
      };
    } else {
      modified = {
        ...active,
        templateTexts: active.templateTexts.map((text, index) => {
          if (index === key) {
            return value;
          }
          return text;
        })
      };
    }
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'actions',
      data: modified
    });
  };
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 ref='editor' className='center'>Regeln</h6>
          <Input label='Name' type='text' value={active.name} onChange={(e) => { modify('name', e.value); }} />
          <Content style={{ flexBasis: '100%' }}>
            <CodeMirror onChange={(value) => modify('rulesXml', value)} value={active.rulesXml} />
          </Content>
        </Row>
      </Block>
      <Block>
        <Row vertical>
          <Input type='select' label='Aktionscode' required values={[{ label: 'ATTACH_VOUCHER', value: 'ATTACH_VOUCHER' }, { label: 'SEND_MAIL', value: 'SEND_MAIL' }]} value={active.actionCode} onChange={(e) => { modify('actionCode', e.value); }} />
          <Input type='text' value={active.parameters['E-Mail']} label='E-Mail' onChange={(e) => { modify('E-Mail', e.value); }} />
          <Input type='text' value={active.parameters['Wert']} label='Wert' onChange={(e) => { modify('Wert', e.value); }} />
          <div className='center margin'>
            <button
              type='button'
              className='important'
              disabled={active.saving}
              onClick={(e) => {
                dispatch(saveActive('actions'));
              }}
            >Speichern</button>
          </div>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default { Single, List };
