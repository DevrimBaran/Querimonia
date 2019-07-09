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
                <p className='h3'>{data.componentName}</p>
                <p>ID: {data.id}</p>
              </div>
              <div className='floatRight'>
                <p>Antwortvariationen: {data.templateTexts.length}</p>
                <p>Entitäten: {data.templateTexts.requiredEntites ? data.templateTexts.requiredEntites.join(', ') : ''}</p>
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
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 ref='editor' className='center'>Regeln</h6>
          <Input type='text' value={active.componentName} onChange={(e) => { modify('componentName', e.value); }} />
          <Input type='number' min='0' max='100' value={active.priority} onChange={(e) => { modify('priority', e.value); }} />
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
              return <textarea className='p visible' key={index} value={text} onChange={(e) => modify(index, e.target.value)} />;
            })}
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

export default { Single, List };
