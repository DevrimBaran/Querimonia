import React, { Component } from 'react';

import Api from 'utility/Api';

import Block from 'components/Block/Block';
import Complaint from 'components/Complaint/Complaint';
import Filter from 'components/Filter/Filter';
// import Complaints from 'components/Complaints/Complaints';
import Log from 'components/Log/Log.js';
// import Modal from 'components/Modal/Modal';
import Stats from 'components/Stats/Stats';
import Tabbed from 'components/Tabbed/Tabbed';
import TaggedText from 'components/TaggedText/TaggedText';
import TextBuilder from 'components/TextBuilder/TextBuilder';

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
      // console.log(query);
      this.setState({ active: null, loading: true });
      Api.get('/api/complaints', query)
        .then((data) => { console.log(data); return data; })
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
      this.fetchData('count=20');
    }
    render () {
      let active = false;
      if (this.props.match.params.id) {
        active = this.state.issues.filter((a) => '' + a.complaintId === this.props.match.params.id)[0];
      }
      return (
        <React.Fragment>
          { active ? (
            <React.Fragment>
              <Block style={{
                display: 'flex',
                flexDirection: 'column'
              }}>
                <h6 className='center'>Antwort</h6>
                <Tabbed style={{ flexGrow: 1 }}>
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
              </Block>
              <Block style={{ display: 'flex', flexDirection: 'column' }}>
                <h6 className='center'>Meldetext</h6>
                <Tabbed style={{ flexGrow: 1 }}>
                  <div label='Überarbeitet'>
                    <TaggedText label='Überarbeitet' text={{ text: active.text, entities: active.entities }} />
                  </div>
                  <div label='Original'>
                    {active.text}
                  </div>
                </Tabbed>
              </Block>
            </React.Fragment>
          ) : (
            <Block style={{
              display: 'flex',
              flexDirection: 'column'
            }}>
              <Filter />
              <div style={{ overflow: 'auto' }}>
                {this.state.issues.map(Complaint)}
              </div>
            </Block>
          ) }

        </React.Fragment>
      );
    }
}

export default Complaints;
