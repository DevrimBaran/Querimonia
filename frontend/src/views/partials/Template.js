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

// eslint-disable-next-line
import { BrowserRouter as Router, Link, withRouter } from 'react-router-dom';

function Header () {
  return (
    <thead>
      <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Antwortvariationen</th>
        <th>Entitäten</th>
      </tr>
    </thead>
  );
}

function List (data, index) {
  return (
    <tr key={data.id}>
      <td><Link to={'/templates/' + data.id}><h3>{data.id}</h3></Link></td>
      <td><Link to={'/templates/' + data.id}><p>{data.componentName}</p></Link></td>
      <td><Link to={'/templates/' + data.id}><p>{data.templateTexts.length}</p></Link></td>
      <td><Link to={'/templates/' + data.id}>  <p>{data.templateTexts.requiredEntites ? data.templateTexts.requiredEntites.join(', ') : ''}</p></Link></td>
    </tr>
  );
}

function Single (active, dispatch) {
  const modify = (key, value) => {
    let modified;
    if (key === 'rulesXml') {
      modified = {
        ...active,
        rulesXml: value
      };
    } else if (key === 'componentName') {
      modified = {
        ...active,
        componentName: value
      };
    } else if (key === 'priority') {
      modified = {
        ...active,
        priority: value
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
      endpoint: 'templates',
      data: modified
    });
  };
  const addText = () => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'templates',
      data: {
        ...active,
        templateTexts: merge(active.templateTexts, [''])
      }
    });
  };
  const removeText = (index) => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'templates',
      data: {
        ...active,
        templateTexts: active.templateTexts.filter((e, i) => i !== index)
      }
    });
  };
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 ref='editor' className='center'>Regeln</h6>
          <Input type='text' label='Name' value={active.componentName} onChange={(e) => { modify('componentName', e.value); }} />
          <Input type='number' label='Priotität' min='0' max='100' value={active.priority} onChange={(e) => { modify('priority', e.value); }} />
          <Content style={{ flexBasis: '100%' }}>
            <CodeMirror onChange={(value) => modify('rulesXml', value)} value={active.rulesXml} />
          </Content>
        </Row>
      </Block>
      <Block>
        <Row vertical>
          <h6 className='center'>Antworvariationen</h6>
          <Content className='margin'>
            {active.templateTexts.map((text, index) => {
              return (
                <div key={index}>
                  <Textarea max='5' className='p visible' value={text} onChange={(e) => modify(index, e.target.value)} />
                  <i className='fa fa-trash' onClick={() => removeText(index)} />
                </div>
              );
            })}
            <i className='fa fa-plus' onClick={addText} />
          </Content>
          <div className='center margin'>
            <button
              type='button'
              className='important'
              disabled={active.saving}
              onClick={(e) => {
                dispatch(saveActive('templates'));
              }}
            >Speichern</button>
          </div>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default { Header, Single, List };
