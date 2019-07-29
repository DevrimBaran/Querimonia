/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React from 'react';

import { saveActive, setCurrentConfig } from '../../redux/actions';

import merge from 'deepmerge';

import Block from '../../components/Block';
import Row from '../../components/Row';
import Content from '../../components/Content';
import Input from '../../components/Input';
// eslint-disable-next-line
import { BrowserRouter as Router, Link } from 'react-router-dom';

function Header () {
  return (
    <thead>
      <th>ID</th>
      <th>Aktiv</th>
      <th>Name</th>
      <th>Extraktoren</th>
      <th>Klassififkatoren</th>
      <th>Stimmungsanalysator</th>
    </thead>
  );
}

function List (dispatch, data, currentConfig) {
  return (
    <tr key={data.id}>
      <td style={{ background: 'rgb(240, 240, 240)' }}><Link to={'/config/' + data.id}><h3>{data.id}</h3></Link></td>
      <td><Link to={'/config/' + data.id}>{ data.id === currentConfig.id ? <input defaultChecked type='radio' name='active' /> : <input onClick={(e) => { dispatch(setCurrentConfig(data.id)); }} type='radio' name='active' /> }</Link></td>
      <td style={{ background: 'rgb(240, 240, 240)' }}><Link to={'/config/' + data.id}>{data.name}</Link></td>
      <td><Link to={'/config/' + data.id}>{data.extractors.length}</Link></td>
      <td style={{ background: 'rgb(240, 240, 240)' }}><Link to={'/config/' + data.id}>{data.classifiers[0].name}</Link></td>
      <td><Link to={'/config/' + data.id}>{data.sentimentAnalyzer.name}</Link></td>
    </tr>
  );
}

function Single (active, dispatch, currentConfig) {
  const modifyLabel = (index, label, change) => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'config',
      data: ({
        ...active,
        extractors: active.extractors.map((extractor, i) => {
          if (i === index) {
            let c = { ...extractor.colors };
            delete c[label];
            return { ...extractor, colors: merge(c, change) };
          }
          return extractor;
        })
      })
    });
  };
  const modifyExtractor = (index, change) => {
    modify({
      extractors: active.extractors.map((extractor, i) => {
        if (i === index) {
          return merge(extractor, change);
        }
        return extractor;
      })
    });
  };
  const modify = (change) => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'config',
      data: merge(active, change)
    });
  };
  const addExtractor = () => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'config',
      data: {
        ...active,
        extractors: merge(active.extractors, [{ name: '', type: '', colors: {} }])
      }
    });
  };
  const removeExtractor = (index) => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'config',
      data: {
        ...active,
        extractors: active.extractors.filter((e, i) => i !== index)
      }
    });
  };
  const addLabel = (index) => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'config',
      data: {
        ...active,
        extractors: active.extractors.map((extractor, i) => {
          if (i === index) {
            return {
              ...extractor,
              colors: {
                ...extractor.colors,
                '': ''
              }
            };
          }
          return extractor;
        })
      }
    });
  };
  const removeLabel = (index, label) => {
    dispatch({
      type: 'MODIFY_ACTIVE',
      endpoint: 'config',
      data: {
        ...active,
        extractors: active.extractors.map((extractor, i) => {
          if (i === index) {
            let c = { ...extractor.colors };
            delete c[label];
            return {
              ...extractor,
              colors: c
            };
          }
          return extractor;
        })
      }
    });
  };
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 className='center'>Konfiguration</h6>
          <Content className='margin'>
            <Input type='text' value={active.name} name='name' label='Name' onChange={(e) => modify({ name: e.value })} />
            <div>
              <h6>Extraktoren</h6>
              {
                active.extractors.map((extractor, index) => {
                  return (
                    <div key={extractor.name}>
                      <Input type='text' value={extractor.name} label='Name'
                        onChange={(e) => modifyExtractor(index, { name: e.value })} />
                      <Input type='text' value={extractor.type} label='Typ'
                        onChange={(e) => modifyExtractor(index, { type: e.value })} />
                      <div>
                        <h6>Entitäten</h6>
                        {Object.keys(extractor.colors).map((label, i) => {
                          return (
                            <Row key={i} >
                              <Input type='text' value={label} label='Name'
                                onChange={(e) => modifyLabel(index, label, { [e.value]: extractor.colors[label] })} />
                              <Input type='color' value={extractor.colors[label]} label='Farbe'
                                onChange={(e) => modifyLabel(index, label, { [label]: e.value })} />
                              <i className='fa fa-trash' onClick={() => removeLabel(index, label)} />
                            </Row>
                          );
                        })}
                        <i className='fa fa-plus' onClick={() => addLabel(index)} />
                      </div>
                      <i className='fa fa-trash' onClick={() => removeExtractor(index)} />
                    </div>
                  );
                })
              }
              <i className='fa fa-plus' onClick={addExtractor} />
            </div>
            <div>
              <h6>Klassifikator</h6>
              <Input type='text' value={active.classifiers ? active.classifiers[0].name : ''} label='Name' onChange={(e) => modify({ classifier: { name: e.value } })} />
              <Input type='text' value={active.classifiers ? active.classifiers[0].type : ''} label='Typ' onChange={(e) => modify({ classifier: { type: e.value } })} />
            </div>
            <div>
              <h6>Stimmungsanalysator</h6>
              <Input type='text' value={active.sentimentAnalyzer.name} label='Name' onChange={(e) => modify({ sentimentAnalyzer: { name: e.value } })} />
              <Input type='text' value={active.sentimentAnalyzer.type} label='Typ' onChange={(e) => modify({ sentimentAnalyzer: { type: e.value } })} />
            </div>
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
          <div className='center margin'>
            <button
              type='button'
              className='important'
              disabled={active.id === currentConfig.id}
              onClick={(e) => {
                dispatch(setCurrentConfig(active.id));
              }}
            >Aktivieren</button>
          </div>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default { Header, List, Single };
