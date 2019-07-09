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

  // Creates an enumeration of words in an array
  renderEnumeration = (word, index) => {
    return (<li key={index}>{word}</li>);
  };

  renderSingle = (active) => {
    return (Template.Single(active));
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
            : (this.props.data && this.props.data.ids.map(id => Template.List(this.props.data.byId[id])))
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
