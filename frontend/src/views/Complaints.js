/**
 * This class creates the Complaints view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Api from 'utility/Api';

import Block from 'components/Block';
import Row from 'components/Row';
import Content from 'components/Content';
import Complaint from 'components/Complaint';
import Filter from 'components/Filter';
import Tabbed from 'components/Tabbed';
import TaggedText from 'components/TaggedText';
import TextBuilder from 'components/TextBuilder';
import Pagination from 'components/Pagination';
import Collapsible from 'components/Collapsible';
import ReactTooltip from 'react-tooltip';
import 'assets/scss/Complaints.scss';

class Complaints extends Component {
  constructor (props) {
    super(props);

    this.state = {
      loading: true,
      active: null,
      issues: []
    };
  }
    fetchData = (query) => {
      this.setState({ active: null, loading: true });
      Api.get('/api/complaints', query)
        .then(this.setData);
    }
    setData = (data) => {
      this.setState({ loading: false, issues: data });
    }
    activate = (issue) => {
      // console.log(issue);
      this.setState({ active: this.state.issues.filter((a) => a.id === issue.id)[0] });
    }
    componentDidMount = () => {
      let searchParams = new URLSearchParams(document.location.search);
      let query = {};
      for (var key of searchParams.keys()) {
        query[key] = searchParams.get(key);
      }
      this.fetchData(query);
    }
    // Creates an enumeration of words in an array
    renderEnumeration = (word, index) => {
      return (<li key={index}>{word}</li>);
    }
    renderSingle = (active) => {
      return (<React.Fragment>
        <Block>
          <Row vertical>
            <h6 className='center'>Antwort</h6>
            <TextBuilder complaintId={active.complaintId} />
          </Row>
        </Block>
        <Block>
          <Row vertical>
            <h6 className='center'>Meldetext</h6>
            <Content>
              <Tabbed className="padding" style={{ height: '100%' }}>
                <div label='Überarbeitet'>
                  <TaggedText text={{ text: active.text, entities: active.entities }} />
                </div>
                <div label='Original'>
                  {active.text}
                </div>
              </Tabbed>
            </Content>
            <Collapsible className="Content" label="Details" collapse={false}>
              <b> Artikulationsdatum: </b>
              <TaggedText text={{
                text: active.receiveDate,
                entities: [{ label: 'Upload_Datum', start: 0, end: active.receiveDate.length }]
              }} />
              <br />
              <b> ID: </b>
              {active.complaintId}
              <br />
              <b> Kategorie: </b>
              <i data-tip data-for='subjects'>{active.probableSubject}</i>
              <br />
              <b> Sentiment: </b>
              <i data-tip data-for='sentiments'>{active.probableSentiment}</i>
              <br />
              </Collapsible>
              <Collapsible className="Content" label="Entitäten" collapse={true}>  
              <ul>
                {createEntityArray(active.text, active.entities).map(this.renderEnumeration, this)}
              </ul>
              <ReactTooltip id='subjects' aria-haspopup='true'>
                {createCategoriesArray(active.subject).map(this.renderEnumeration, this)}
              </ReactTooltip>
              <ReactTooltip id='sentiments' aria-haspopup='true'>
                {createCategoriesArray(active.sentiment).map(this.renderEnumeration, this)}
              </ReactTooltip>
            </Collapsible>
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
          <Content>
            {this.state.loading ? (<div className='center'><i style={{ color: 'var(--primaryAccentColor)' }} className='fa-spinner fa-spin fa fa-5x' /></div>) : (this.state.issues.map(Complaint))}
          </Content>
          <Pagination onClick={this.update} />
        </Row>
      </Block>);
    }
    render () {
      let active = false;
      if (this.props.match.params.id) {
        active = this.state.issues.filter((a) => '' + a.complaintId === this.props.match.params.id)[0];
      }
      return (
        <React.Fragment>
          { active ? (
            this.renderSingle(active)
          ) : (
            this.renderList()
          ) }

        </React.Fragment>
      );
    }
}
// creates an Array of Entitity-Strings
function createEntityArray (txt, ar) {
  var a = [];
  a = ar;
  var text = '';
  text = txt;

  var st = [];
  for (var i = 0; i < a.length; i++) {
    var m = new Map();
    m = a[i];
    st.push(m['label'] + ': ' + text.substring(m['start'], m['end']));
  }

  return st;
}
// creates an Array of Categories-Strings
function createCategoriesArray (map) {
  var m = new Map();
  m = map;
  var ar = [];
  for (var i = 0; i < Object.keys(m).length; i++) {
    ar.push(Object.keys(m)[i] + ' : ' + Object.values(m)[i]);
  }
  return ar;
}
export default Complaints;
