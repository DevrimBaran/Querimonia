/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';
import { changeEntity, deleteEntity } from '../redux/actions/';

import { getColor, getGradient } from '../utility/colors';
import Tooltip from './Tooltip';

class Tag extends Component {
  constructor (props) {
    super(props);
    this.state = {
      style: ' ',
      editActive: false
    };
    this.tooltip = React.createRef();
    this.entity = React.createRef();
  }
  modifyEntity = (data) => {
    console.log('MODIFY', data);
    this.props.dispatch(changeEntity(this.props.complaintId, data.id || 0, data));
  }
  edit = (e) => {

  }
  remove = (id) => (e) => {
    if (window.confirm('Wollen sie die Entität wirklich löschen?')) {
      this.props.dispatch(deleteEntity(this.props.complaintId, id));
    }
  }
  preferr = (id) => (e) => {
    const entity = this.props.entities.byId[id] || {};
    this.modifyEntity({ id: id, preferred: !entity.preferred });
  }
  render () {
    const { text, ids, entities, dispatch, complaintId, ...passThrough } = { ...this.props };
    const relevantEntities = ids.map(id => entities.byId[id]);
    const gradient = getGradient(relevantEntities, this.props.config);
    const inject = {
      className: 'entity',
      style: {
        color: gradient.color,
        backgroundImage: gradient.background
      }
    };
    const tooltip = Tooltip.create();
    return (
      <span {...tooltip.events} id={'tag_' + ids.join('_')} {...inject} {...passThrough}>
        {text}
        <Tooltip {...tooltip.register} className='tag-tooltip'>
          {relevantEntities.map((entity, i) => (
            <div
              key={i}
              style={{
                borderColor: getColor(entity, this.props.config).background
              }}
            >
              <i>{entity.label}</i>
              <b>{entity.value}</b>
              <span className='action-button'>
                <i title='Kopieren' id='editEntity' className={'far fa-clone'} />
              </span>
              <span className='action-button'>
                <i title='Bearbeiten' id='editEntity' className={'far fa-edit'} />
              </span>
              <span className='action-button'>
                <i title='Löschen' className={'far fa-trash-alt'} onClick={this.remove(entity.id)} />
              </span>
            </div>
          ))}
        </Tooltip>
      </span>
    );
  }
}

const mapStateToProps = (state, props) => {
  return {
    entities: state.complaintStuff.entities,
    complaintId: state.complaintStuff.id,
    config: state.complaintStuff.config
  };
};

export default connect(mapStateToProps)(Tag);
