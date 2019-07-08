/**
 * This class creates the Complaints view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

import { fetchData } from '../redux/actions';

import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';
import Complaint from './../components/Complaint';
import Collapsible from './../components/Collapsible';
import Filter from './../components/Filter';
import Tabbed from './../components/Tabbed';
import TaggedText from './../components/TaggedText';
import TextBuilder from './../components/TextBuilder';
import Pagination from './../components/Pagination';
import ReactTooltip from 'react-tooltip';

class Complaints extends Component {
  constructor (props) {
    super(props);
    this.state = {
      active: null
    };
  }
  componentDidMount = () => {
    this.props.dispatch(fetchData('complaints'));
  }

    renderSingle = (active) => {
      return (<React.Fragment>
        <Block>
          <Row vertical>
            <h6 className='center'>Antwort</h6>
            <TextBuilder complaintId={active.id} />
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
              {active.id}
              <br />
              <b> Kategorie: </b>
              <i data-tip data-for='subjects'>{active.subject.value}</i>
              <br />
              <b> Sentiment: </b>
              <i data-tip data-for='sentiments'>{active.sentiment.value}</i>
              <br />
            </Collapsible>
            <Collapsible label='Entitäten'>
              <ul>
                {createEntityArray(active.text, active.entities)}
              </ul>
              <ReactTooltip id='subjects' aria-haspopup='true'>
                {Object.keys(active.subject.probabilities).map(subject => `${subject}: ${active.subject.probabilities[subject]}`)}
              </ReactTooltip>
              <ReactTooltip id='sentiments' aria-haspopup='true'>
                {Object.keys(active.sentiment.probabilities).map(sentiment => `${sentiment}: ${active.sentiment.probabilities[sentiment]}`)}
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
          <Filter endpoint='complaints' />
          <Content>
            {this.props.fetching
              ? (<div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>)
              : (this.props.data && this.props.data.ids.map(id => Complaint(this.props.data.byId[id])))
            }
          </Content>
          <Pagination endpoint='complaints' />
        </Row>
      </Block>);
    }

    render () {
      let active = this.props.match.params.id ? this.props.data.byId[this.props.match.params.id] : null;
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

const mapStateToProps = (state, props) => ({ ...state['complaints'] });

export default connect(mapStateToProps)(Complaints);
