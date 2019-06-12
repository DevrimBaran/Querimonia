import React, { Component } from 'react';

import Api from 'utility/Api';

import Block from 'components/Block/Block';
import Row from 'components/Row/Row';
import Content from 'components/Content/Content';
import Complaint from 'components/Complaint/Complaint';
import Filter from 'components/Filter/Filter';
// import Complaints from 'components/Complaints/Complaints';
import Log from 'components/Log/Log.js';
// import Modal from 'components/Modal/Modal';
import Stats from 'components/Stats/Stats';
import Tabbed from 'components/Tabbed/Tabbed';
import TaggedText from 'components/TaggedText/TaggedText';
import TextBuilder from 'components/TextBuilder/TextBuilder';
import Pagination from 'components/Pagination/Pagination';

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
            <Content>
              <Tabbed style={{ height: '100%' }}>
                <div label='Erstellen'>
                  <TextBuilder complaintId={active.complaintId} />
                </div>
                <div label='Details'>
                  <Stats label='Kategorisierung' data={active.subject} />
                  <Stats label='Sentiments' data={active.sentiment} />
                  <Log />
                </div>
                <div label='Datenbank'>BUH!</div>
              </Tabbed>
            </Content>
          </Row>
        </Block>
        <Block>
          <Row vertical>
            <h6 className='center'>Meldetext</h6>
            <Content>
              <Tabbed style={{ height: '100%' }}>
                <div label='Überarbeitet'>
                  <TaggedText label='Überarbeitet' text={{ text: active.text, entities: active.entities }} />
                  <div>
                    <br />
                    <b>Artikulationsdatum: </b>
                    <TaggedText text={{
                      text: active.receiveDate,
                      entities: [{ label: 'Upload_Datum', start: 0, end: active.receiveDate.length }]
                    }} />
                  </div>
                </div>
                <div label='Original'>
                  {active.text}
                </div>
              </Tabbed>
            </Content>
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

export default Complaints;
