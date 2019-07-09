/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

import CodeMirror from '../../components/CodeMirror';
import Block from '../../components/Block';
import Row from '../../components/Row';
import Content from '../../components/Content';

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

function Single (active, onSave) {
  const save = withRouter(({ history }) => (
    <button
      type='button'
      className='important'
      onClick={() => {
        history.push('../');
      }}
    >Speichern</button>
  ));
  const modify = (key, value) => {
    if (key === 'rulesXml') {
      active = {
        ...active,
        rulesXml: value
      };
    } else {
      active = {
        ...active,
        templateTexts: active.templateTexts.map((text, index) => {
          if (index === key) {
            return value;
          }
          return text;
        })
      };
    }
  };
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 ref='editor' className='center'>Regeln</h6>
          <CodeMirror onChange={(value) => modify('rulesXml', value)} value={active.rulesXml} />
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
            <save />
          </div>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default { Single, List };
