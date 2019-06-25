/**
 * This class creates the Templates view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Api from 'utility/Api';

import Block from 'components/Block';
import Row from 'components/Row';
import Content from 'components/Content';
import Template from 'components/Template';
import Filter from 'components/Filter';
import Pagination from 'components/Pagination';

import { withRouter } from 'react-router-dom'
// this also works with react-router-native

class Templates extends Component {
  constructor(props) {
    super(props);

    this.state = {
      loading: false,
      templates: [{ id: 0, rulesXml: '<rules></rules>', templateTexts: ["Lorem Ipsum", "Hello World!"] }, { id: 1, rulesXml: '', templateTexts: [] }]
    };
  }
  fetchData = (query) => {
    this.setState({ active: null, loading: true });
    Api.get('/api/templates', query)
      .then(this.setData);
  }
  setData = (data) => {
    console.log(data);
    this.setState({ loading: false, templates: data });
  }
  activate = (issue) => {
    // console.log(issue);
    this.setState({ active: this.state.templates.filter((a) => a.id === issue.id)[0] });
  }
  componentDidMount = () => {
    let searchParams = new URLSearchParams(document.location.search);
    let query = {};
    for (var key of searchParams.keys()) {
      query[key] = searchParams.get(key);
    }
    console.log('did mount');
    if (document.getElementById('editor')) {
      const s = document.createElement('script');
      s.type = 'text/javascript';
      s.async = true;
      s.innerHTML = 'console.log("Render Editor");var editor = ace.edit("editor");editor.setTheme("ace/theme/monokai");editor.session.setMode("ace/mode/xml");';
      document.body.appendChild(s);
    }
    this.fetchData(query);
  }
  newTemplate = withRouter(({ history }) => (
    <button
      type='button'
      onClick={() => {
        history.push(window.location.pathname
          + (window.location.pathname.substr(-1) === '/' ? '0/' : '/0')
          + window.location.search);
        this.setState((state) => {
          state.templates.push({ id: 0, rulesXml: '<rules></rules>', templateTexts: ["Lorem Ipsum", "Hello World!"] });
        });
      }}
    >Neues Template</button>
  ));
  // Creates an enumeration of words in an array
  renderEnumeration = (word, index) => {
    return (<li key={index}>{word}</li>);
  }
  renderSingle = (active) => {
    return (<React.Fragment>
      <Block>
        <Row vertical>
          <h6 className='center'>Regeln</h6>
          <div id="editor">{active.rulesXml}</div>
        </Row>
      </Block>
      <Block>
        <Row vertical>
          <h6 className='center'>Antworvariationen</h6>
          {active.templateTexts.map((text, index) => {
            return <textarea key={index} value={text} onChange={() => {}} />
          })}
        </Row>
      </Block>
    </React.Fragment>);
  }
  update = () => {
    this.setState({ loading: true });
    setTimeout(() => {
      this.componentDidMount();
    }, 10);
  }
  renderList = () => {
    return (<Block>
      <Row vertical>
        <Filter onSubmit={this.fetchData} />
        <div>
          <this.newTemplate />
        </div>
        <Content>
          {this.state.loading ? (<div className='center'><i style={{ color: 'var(--primaryAccentColor)' }} className='fa-spinner fa-spin fa fa-5x' /></div>) : (this.state.templates.map(Template))}
        </Content>
        <Pagination onClick={this.update} />
      </Row>
    </Block>);
  }
  render() {
    let active = false;
    if (this.props.match.params.id) {
      active = this.state.templates.filter((a) => '' + a.id === this.props.match.params.id)[0];
    }
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
export default Templates;
