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
import Button from './Button';
import EditEntityModal from './EditEntityModal';

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
  modifyEntity = (id) => (data) => {
    this.props.dispatch(changeEntity(this.props.complaintId, data.id || 0, data));
  }
  openModal = (e) => {
    EditEntityModal.open(e);
  }
  remove = (id) => (e) => {
    if (window.confirm('Wollen sie die Entität wirklich löschen?')) {
      this.props.dispatch(deleteEntity(this.props.complaintId, id));
    }
  }
  render () {
    const { text, ids, entities, dispatch, complaintId, disabled, ...passThrough } = { ...this.props };
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
      <span {...tooltip.events} {...inject} {...passThrough}>
        {text}
        <Tooltip {...tooltip.register} className='tag-tooltip'>
          {relevantEntities.map((entity, i) => {
            let emptyData = {
              'data-start': '',
              'data-end': '',
              'data-label': '',
              'data-id': '',
              'data-value': '',
              'data-mode': ''
            };
            let entitiyData = {
              'data-id': entity.id,
              'data-start': entity.start,
              'data-end': entity.end,
              'data-label': entity.label,
              'data-value': entity.value
            };
            return <div
              key={i}
              style={{
                borderColor: getColor(entity, this.props.config).background
              }}
            >
              <i>{entity.label}</i>
              <b>{entity.value}</b>
              {disabled || <React.Fragment>
                <Button title='Kopieren' icon='far fa-clone' onClick={this.openModal} {...{ ...emptyData, 'data-label': entity.label }} />
                <Button title='Bearbeiten' icon='far fa-edit' onClick={this.openModal} {...{ ...emptyData, ...entitiyData }} />
                <Button title='Löschen' icon='far fa-trash-alt' onClick={this.remove(entity.id)} />
              </React.Fragment>}
            </div>;
          })}
        </Tooltip>
      </span>
    );
  }
}

const mapStateToProps = (state, props) => {
  return {
    entities: state.complaintStuff.entities,
    complaintId: state.complaintStuff.id
  };
};

export default connect(mapStateToProps)(Tag);
