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
import Table from './../components/Table';

class Complaints extends Component {
  constructor (props) {
    super(props);
    this.state = {
      active: null,
      editCategorie: false,
      editTendency: false,
      editEmotion: false,
      loadingEntitiesFinished: false
    };
  }
  componentDidMount = () => {
    this.props.dispatch(fetchData('complaints'));
    this.props.dispatch(fetchCurrentConfig());
  }

  activateComplaint = (active) => {
    let requests = [];
    requests.push(Api.get('/api/complaints/' + active.id + '/entities', '')
      .catch(() => {
        return { status: 404 };
      }));
    requests.push(Api.get('/api/complaints/' + active.id + '/text', '')
      .catch(() => {
        return { status: 404 };
      }));
    Promise.all(requests).then((values) => {
      this.setState({
        active: {
          ...active,
          entities: values.find(value => Array.isArray(value)),
          text: values.find(value => !Array.isArray(value)).text
        },
        loadingEntitiesFinished: true
      });
    });
  };

  /**
   *  switchs between edit and normal state of the Categorie label /
   *  changes the Categorie of the complaint if "editCategorie" is true.
   **/
  editCategorie = (active, index, editCategorie) => {
    if (editCategorie) {
      let data = {};
      data['subject'] = document.getElementById('chooseCategorie').value;
      Api.patch('/api/complaints/' + active.id, data);
      active.properties[index].value = data['subject'];
    }
    this.setState({ editCategorie: !this.state.editCategorie });
  }

  /**
   *  switchs between edit and normal state of the Sentiment label /
   *  changes the Sentiment of the complaint if "editCSentiment" is true.
   **/
  editTendency = (active, editTendency) => {
    if (editTendency) {
      let data = {};
      data['tendency'] = Number(document.getElementById('chooseTendency').value);
      Api.patch('/api/complaints/' + active.id, data);
      active.sentiment.tendency = data['tendency'];
    }
    this.setState({ editTendency: !this.state.editTendency });
  }

  /**
   *  switchs between edit and normal state of the Sentiment label /
   *  changes the Sentiment of the complaint if "editCSentiment" is true.
   **/
  editEmotion = (active, editEmotion) => {
    if (editEmotion) {
      let data = {};
      data['sentiment'] = document.getElementById('chooseEmotion').value;
      Api.patch('/api/complaints/' + active.id, data);
      active.sentiment.value = data['sentiment'];
    }
    this.setState({ editEmotion: !this.state.editEmotion });
  }

  /**
   *  updates the entity-list
   **/
  refreshEntities = (active, data) => {
    active.entities = data;
    this.setState({ active: active });
  }

  renderSingle = (active) => {
    if (this.state.active === null || this.state.active.id !== active.id) this.activateComplaint(active);
    return (Complaint.Single(this.state.active, this.state.loadingEntitiesFinished, this.state.editCategorie, this.state.editTendency, this.state.editEmotion, this.editCategorie, this.editTendency, this.editEmotion, this.refreshEntities));
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
        <Content className='padding'>
          { this.props.data.fetching
            ? (<div className='center'><i className='fa-spinner fa-spin fa fa-5x primary' /></div>)
            : (
              <Table>
                {Complaint.Header()}
                <tbody>
                  {this.props.data && this.props.data.ids.map(id => Complaint.List(this.props.data.byId[id]))}
                </tbody>
              </Table>
            )
          }
        </Content>
        <Pagination endpoint='complaints' />
      </Row>
    </Block>);
  }

  render () {
    let active = this.props.match.params.id ? this.props.data.byId[this.props.match.params.id] : null;
    if (active) {
      active.entities = [];
    } return (
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
