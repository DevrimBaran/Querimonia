/**
 * This class creates the Templates view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

import { fetchData } from '../redux/actions';

import Template from './partials/Template';

import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';
import Filter from './../components/Filter';
import Pagination from './../components/Pagination';

import { pd } from 'pretty-data';
import { withRouter } from 'react-router-dom';

class Templates extends Component {
  constructor (props) {
    super(props);

    this.state = {
      loading: []
    };
  }
  componentDidMount = () => {
    this.props.dispatch(fetchData('templates'));
  }
  loadEditor = () => {
    console.log('load Editor');
    if (document.getElementById('editor')) {
      const s = document.createElement('script');
      s.type = 'text/javascript';
      s.async = true;
      s.innerHTML = 'console.log("Render Editor");var editor = ace.edit("editor");editor.setTheme("ace/theme/monokai");editor.session.setMode("ace/mode/xml");';
      this.refs.editor.appendChild(s);
    } else {
      console.log('no editor');
    }
  };

  newTemplate = withRouter(({ history }) => (
    <button
      type='button'
      onClick={() => {
        history.push(window.location.pathname +
          (window.location.pathname.substr(-1) === '/' ? '0/' : '/0') +
          window.location.search);
        this.setState((state) => ({
          templates: [...state.templates, {
            componentId: 0,
            priority: 100,
            componentName: 'Begrüßung',
            templateTexts: [
              'Sehr geehrter Herr,'
            ],
            rulesXml: '<Rules><And><Sentiment value="Wut" /><Subject value="Fahrt nicht erfolgt" /></And></Rules>',
            requiredEntites: [
              'Name'
            ]
          }]
        }));
      }
      }>Neues Template</button>
  ));

  saveTemplate = withRouter(({ history }) => (
    <button
      type='button'
      className='important'
      onClick={() => {
        history.push('/templates/' +
          window.location.search);
        let template = this.state.templates.filter((a) => '' + a.componentId === this.props.match.params.id)[0];
        template.rulesXml = pd.xmlmin(template.rulesXml);
        delete template.componentId;
        delete template.requiredEntites;
        console.log(template);
        /* Api.post('/api/templates', template)
          .then(this.componentDidMount);
        */
      }}
    >Speichern</button>
  ));

  // Creates an enumeration of words in an array
  renderEnumeration = (word, index) => {
    return (<li key={index}>{word}</li>);
  };

  renderSingle = (active) => {
    return (<React.Fragment>
      <Block>
        <Row vertical>
          <h6 ref='editor' className='center'>Regeln</h6>
          <div id='editor'>{pd.xml(active.rulesXml)}</div>
        </Row>
      </Block>
      <Block>
        <Row vertical>
          <h6 className='center'>Antworvariationen</h6>
          <Content className='margin'>
            {active.templateTexts.map((text, index) => {
              return <textarea className='p visible' key={index} value={text} onChange={() => {}} />;
            })}
          </Content>
          <div className='center margin'>
            <this.saveTemplate />
          </div>
        </Row>
      </Block>
    </React.Fragment>);
  };

  update = () => {
    this.setState({ loading: true });
    setTimeout(() => {
      this.componentDidMount();
    }, 10);
  };

  renderList = () => {
    return (<Block>
      <Row vertical>
        <Filter endpoint='templates' />
        <div className='row flex-row height' >
          <this.newTemplate />
        </div>
        <Content className='padding'>
          {this.props.fetching
            ? (<div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>)
            : (this.props.data && this.props.data.ids.map(id => Template(this.props.data.byId[id])))
          }
        </Content>
        <Pagination endpoint='templates' />
      </Row>
    </Block>);
  };

  render () {
    let active = this.props.match.params.id ? this.props.data.byId[this.props.match.params.id] : null;
    console.log(active, this.props.match.params.id);
    return (
      <React.Fragment>
        {active ? (
          this.renderSingle(active)
        ) : (
          this.renderList()
        )}

      </React.Fragment>
    );
  }
}

const mapStateToProps = (state, props) => ({ ...state['templates'] });

export default connect(mapStateToProps)(Templates);
