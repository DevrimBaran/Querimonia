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
import Collapsible from 'components/Collapsible';
import Filter from 'components/Filter';
import Tabbed from 'components/Tabbed';
import TaggedText from 'components/TaggedText';
import TextBuilder from 'components/TextBuilder';
import Pagination from 'components/Pagination';
import ReactTooltip from 'react-tooltip';

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
            <Content style={{ flexBasis: '100%' }}>
              <Tabbed className='padding' style={{ height: '100%' }}>
                <div label='Überarbeitet'>
                  <TaggedText text={{ text: active.text, entities: active.entities }} />
                </div>
                <div label='Original'>
                  {active.text}
                </div>
              </Tabbed>
            </Content>
            <Collapsible label='Details' style={{ minHeight: '130px' }}>
              <b>Eingangsdatum: </b>
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
            <Collapsible label='Entitäten'>
              <ul>
                {createEntityArray(active.text, active.entities)}
              </ul>
              <ReactTooltip id='subjects' aria-haspopup='true'>
                {Object.keys(active.subject).map(s => 's: ' + active.subject[s])}
              </ReactTooltip>
              <ReactTooltip id='sentiments' aria-haspopup='true'>
                {Object.keys(active.subject).map(s => 's: ' + active.subject[s])}
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
            {this.state.loading ? (<div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>) : (this.state.issues.map(Complaint))}
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
// creates an Array of Entity-Strings
function createEntityArray (txt, ar) {
  return ar.map((entity, i) => {
    return <li key={i}> { entity['label'] } :
      <TaggedText text={{
        text: ' ' + txt.substring(entity['start'], entity['end']),
        entities: [{ label: entity['label'], start: 1, end: entity['end'] - entity['start'] + 1 }]
      }} />
    </li>;
  });
}
export default Complaints;
