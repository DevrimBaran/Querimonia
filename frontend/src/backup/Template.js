/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

import { saveActive } from '../../redux/actions';

import merge from 'deepmerge';

import CodeMirror from '../../components/CodeMirror';
import Block from '../../components/Block';
import Row from '../../components/Row';
import Content from '../../components/Content';
import Input from '../../components/Input';
import Textarea from '../../components/Textarea';
import Api from '../../utility/Api';

// eslint-disable-next-line
import { BrowserRouter as Router, Link, withRouter } from 'react-router-dom';

function Header () {
  return (
    <thead>
      <tr style={{ filter: 'brightness(100%)' }}>
        <th>ID</th>
        <th>Name</th>
        <th>Priorität</th>
        <th>Entitäten</th>
        <th>Varianten</th>
        <th>Aktionen</th>
        <th />
      </tr>
    </thead>
  );
}

function List (data, index) {
  const removeComponent = (index) => {
    Api.delete('/api/components/' + data.id, []);
  };
  return (
    <tr key={data.id}>
      <td style={{ background: 'rgb(240, 240, 240)' }}><Link to={'/templates/' + data.id}><h3>{data.id}</h3></Link></td>
      <td style={{ textAlign: 'left' }}><Link to={'/templates/' + data.id}><p>{data.name}</p></Link></td>
      <td style={{ background: 'rgb(240, 240, 240)' }}><Link to={'/templates/' + data.id}><p>{data.priority}</p></Link></td>
      <td><Link to={'/templates/' + data.id}>  <p>{data.requiredEntities ? data.requiredEntities.join(', ') : ''}</p></Link></td>
      <td style={{ background: 'rgb(240, 240, 240)' }}><Link to={'/templates/' + data.id}><p>{data.texts.length}</p></Link></td>
      <td><Link to={'/templates/' + data.id}>  <p>{data.actions.map((action) => action.name).join(', ')}</p></Link></td>
      <td style={{ background: 'rgb(240, 240, 240)' }}> <i className='fa fa-trash' style={{ cursor: 'pointer' }} onClick={() => removeComponent(index)} /></td>
    </tr>
  );
}

function Single (active, dispatch) {
  delete active[ 'requiredEntities' ];
  const modifyText = (key, value) => {
    let modified;
    if (key === 'rulesXml') {
      modified = {
        ...active,
        rulesXml: value
      };
    } else if (key === 'name') {
      modified = {
        ...active,
        name: value
      };
    } else if (key === 'priority') {
      modified = {
        ...active,
        priority: value
      };
    } else {
      modified = {
        ...active,
        texts: active.texts.map((text, index) => {
          if (index === key) {
            return value;
          }
          return text;
        })
      };
    }
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'components',
      data: modified
    });
  };
  const addText = () => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'components',
      data: {
        ...active,
        texts: merge(active.texts, [''])
      }
    });
  };
  const removeText = (index) => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'components',
      data: {
        ...active,
        texts: active.texts.filter((e, i) => i !== index)
      }
    });
  };
  const modifyAction = (key, value, index) => {
    let modified;
    if (key === 'name' || key === 'actionCode') {
      modified = {
        ...active,
        actions: active.actions.map((action, i) => {
          if (index === i) {
            return {
              ...active.actions[i],
              [key]: value
            };
          }
          return active.actions[i];
        })
      };
    } else if (key === 'E-Mail' || key === 'Gutscheinwert' || key === 'Text') {
      modified = {
        ...active,
        actions: active.actions.map((action, i) => {
          if (index === i) {
            return {
              ...active.actions[i],
              parameters: {
                ...active.actions[i].parameters,
                [key]: value
              }
            };
          }
          return active.actions[i];
        })
      };
    }
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'components',
      data: modified
    });
  };
  const addAction = () => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'components',
      data: {
        ...active,
        actions: merge(active.actions, [{
          name: '',
          actionCode: 'ATTACH_VOUCHER',
          parameters: {
            'E-Mail': '',
            Text: '',
            Gutscheinwert: ''
          }
        }])
      }
    });
  };
  const removeAction = (index) => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'components',
      data: {
        ...active,
        actions: active.actions.filter((e, i) => i !== index)
      }
    });
  };
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 ref='editor' className='center'>Regeln</h6>
          <div className='margin'>
            <Input type='text' label='Name ' value={active.name} style={{ marginTop: '0.5em', marginRight: '1em' }} onChange={(e) => { modifyText('name', e.value); }} />
            <Input type='number' label='Priorität ' min='0' max='100' value={active.priority} onChange={(e) => { modifyText('priority', Number(e.value)); }} />
          </div>
          <Content style={{ flexBasis: '100%' }}>
            <CodeMirror onChange={(value) => modifyText('rulesXml', value)} value={active.rulesXml} />
          </Content>
        </Row>
      </Block>
      <Block>
        <Row vertical>
          <h6 style={{ marginBottom: '0.5em' }} className='center'>Antwortvariationen</h6>
          <Content className='margin'>
            {active.texts.map((text, index) => {
              return (
                <div key={index}>
                  <Textarea max='5' placeholder='Antwortvariante' value={text} onChange={(e) => modifyText(index, e.target.value)} />
                  <i style={{ marginTop: '0.5em', cursor: 'pointer' }} className='fa fa-trash' onClick={() => removeText(index)} />
                  <hr style={{ marginTop: '0.5em', marginBottom: '0.5em' }} />
                </div>
              );
            })}
            <i className='fa fa-plus' style={{ cursor: 'pointer' }} onClick={addText} />
          </Content>
          <hr style={{ marginTop: '0.5em', marginBottom: '0.5em' }} />
          <h6 style={{ marginBottom: '0.5em' }} className='center'>Aktionen</h6>
          <Content className='margin'>
            {active.actions.map((action, index) => {
              return (
                <div key={index}>
                  <i style={{ cursor: 'pointer', marginRight: '1em' }} className='fa fa-trash' onClick={() => removeAction(index)} />
                  <Input type='text' label='Name ' value={action.name} style={{ marginRight: '1em' }} onChange={(e) => { modifyAction('name', e.value, index); }} />
                  <Input type='select' label='Art ' required values={[{ label: 'Gutschein', value: 'ATTACH_VOUCHER' }, { label: 'E-Mail', value: 'SEND_MAIL' }, { label: 'Vergütung', value: 'COMPENSATION' }]} value={action.actionCode} style={{ marginRight: '1em' }} onChange={(e) => { modifyAction('actionCode', e.value, index); }} />
                  <br />
                  <Content style={{ marginLeft: '2em' }}>
                    <Input type='text' label='E-Mail ' value={action.parameters['E-Mail']} style={{ marginRight: '1em' }} onChange={(e) => { modifyAction('E-Mail', e.value, index); }} />
                    {action.actionCode !== 'SEND_MAIL'
                      ? <span className='input-symbol-euro'>
                        <Input type='number' step='0.01' label='Wert ' value={action.parameters.Gutscheinwert} onChange={(e) => { modifyAction('Gutscheinwert', Number(e.value), index); }} />
                      </span> : null
                    }
                    <Textarea max='5' placeholder='Text' value={action.parameters.Text} style={{ marginRight: '1em' }} onChange={(e) => { modifyAction('Text', e.target.value, index); }} />
                  </Content>
                  <hr style={{ marginTop: '0.5em', marginBottom: '0.5em' }} />
                </div>
              );
            })}
            <i className='fa fa-plus' style={{ cursor: 'pointer' }} onClick={addAction} />
          </Content>
          <div className='center margin'>
            <button
              type='button'
              className='important'
              disabled={active.saving}
              onClick={(e) => {
                dispatch(saveActive('components'));
              }}
            >Speichern</button>
          </div>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default { Header, Single, List };
