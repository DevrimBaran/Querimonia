/**
 * This class creates the Complaints view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

import { fetchData, fetchCurrentConfig } from '../redux/actions';

import Complaint from './partials/Complaint';
import Api from '../utility/Api';

import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';
import Filter from './../components/Filter';
import Pagination from './../components/Pagination';

class Complaints extends Component {
  constructor (props) {
    super(props);
    this.state = {
      active: null,
      editCategorie: false,
      editSentiment: false
    };
  }
  componentDidMount = () => {
    this.props.dispatch(fetchData('complaints'));
    this.props.dispatch(fetchCurrentConfig());
  }

  /**
   *  switchs between edit and normal state of the Categorie label /
   *  changes the Categorie of the complaint if "editCategorie" is true.
   **/
  editCategorie = (active, editCategorie) => {
    if (editCategorie) {
      let data = {};
      data['subject'] = document.getElementById('chooseCategorie').value;
      Api.patch('/api/complaints/' + active.id, data);
      active.subject.value = data['subject'];
    }
    this.setState({ editCategorie: !this.state.editCategorie });
  }

  /**
   *  switchs between edit and normal state of the Sentiment label /
   *  changes the Sentiment of the complaint if "editCSentiment" is true.
   **/
  editSentiment = (active, editSentiment) => {
    if (editSentiment) {
      let data = {};
      data['sentiment'] = document.getElementById('chooseSentiment').value;
      Api.patch('/api/complaints/' + active.id, data);
      active.sentiment.value = data['sentiment'];
    }
    this.setState({ editSentiment: !this.state.editSentiment });
  }

  /**
   *  updates the entity-list
   **/
  refreshEntities = (active, data) => {
    active.entities = data;
    this.setState({ active: active });
  }

  renderSingle = (active) => {
    return (Complaint.Single(active, this.state.editCategorie, this.state.editSentiment, this.editCategorie, this.editSentiment, this.refreshEntities));
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
          { this.props.data.fetching
            ? (<div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>)
            : (this.props.data && this.props.data.ids.map(id => Complaint.List(this.props.data.byId[id])))
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

const mapStateToProps = (state, props) => ({
  ...state.complaints
});

export default connect(mapStateToProps)(Complaints);
