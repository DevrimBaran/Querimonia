/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';
import ReactDOM from 'react-dom';
import { changeEntity, deleteEntity } from '../redux/actions';

class Tag extends Component {
  constructor (props) {
    super(props);
    this.state = {
      style: ' '
    };
    this.tooltip = React.createRef();
    this.entity = React.createRef();
  }
  getLuminance = (color) => {
    const rgb = color && color.match(/#(..)(..)(..)/);
    if (rgb) {
      return (0.299 * parseInt(rgb[1], 16) + 0.587 * parseInt(rgb[2], 16) + 0.114 * parseInt(rgb[3], 16)) / 255;
    } else {
      return 0;
    }
  };
  averageLuminance = (avg, entity, array) => {
    return avg + (this.getLuminance(entity.color) / array.length);
  }
  minLuminance = (min, entity, array) => {
    return Math.min(min, this.getLuminance(entity.color));
  }
  getGradient = (entity, i, entities) => {
    const pers = 100 / entities.length;
    const color = entity.color || '#cccccc';
    return `${color} ${pers * i}%, ${color} ${pers * (i + 1)}%`;
  }
  getColorStyles = (entities) => {
    let gradient = entities.map(this.getGradient, '').join(', ');
    let luminance = entities.reduce(this.minLuminance, 256);
    let textColor = Math.abs(this.getLuminance('#202124') - luminance) > 0.2
      ? '#202124'
      : '#ffffff';
    return {
      color: textColor,
      backgroundImage: `linear-gradient(${gradient})`
    };
  };
  modifyEntity = (data) => {
    console.log('MODIFY', data);
    this.props.dispatch(changeEntity(this.props.complaintId, data.id || 0, data));
  }
  edit = (e) => {

  }
  copy = (e) => {

  }
  remove = (id) => (e) => {
    this.props.dispatch(deleteEntity(this.props.complaintId, id));
  }
  preferr = (id) => (e) => {
    const entity = this.props.entities.byId[id] || {};
    this.modifyEntity({ id: id, preferred: !entity.preferred });
  }
  createTooltip = (entities) => {
    return (
      <div ref={this.tooltip} className='tooltip'>
        <table>
          <thead>
            <tr>
              <th> </th>
              <th>Name</th>
              <th>Text</th>
              <th> </th>
            </tr>
          </thead>
          <tbody>
            {entities.map((entity, i) => (
              <tr key={i}>
                <td>
                  <span className='dot' style={{ backgroundColor: entity.color }} />
                </td>
                <td>{entity.label}</td>
                <td>{entity.value}</td>
                <td className='nowrap'>
                  <i onClick={this.edit} className='far fa-edit' />
                  <i onClick={this.copy} className='far fa-copy' />
                  <i onClick={this.remove(entity.id)} className='far fa-trash-alt' />
                  <i onClick={this.preferr(entity.id)} className={(entity.preferred ? 'fas' : 'far') + ' fa-star'} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  }
  onMouseEnter = (e) => {
    const element = e.target;
    const rect = element.getBoundingClientRect();
    const tooltip = this.tooltip.current;
    if (tooltip) {
      tooltip.classList.add('show');
      tooltip.style.left = (rect.x + rect.width * 0.5) + 'px';
      if (rect.y >= tooltip.offsetHeight) {
        tooltip.classList.remove('bottom');
        tooltip.classList.add('top');
        tooltip.style.top = (rect.y) + 'px';
      } else {
        tooltip.classList.remove('top');
        tooltip.classList.add('bottom');
        tooltip.style.top = (rect.y + rect.height) + 'px';
      }
      /*
      if (rect.x >= tooltip.offsetWidth) {
        tooltip.classList.remove('bottom');
        tooltip.classList.add('top');
        tooltip.style.top = (rect.y) + 'px';
      } else {
        tooltip.classList.remove('top');
        tooltip.classList.add('bottom');
        tooltip.style.top = (rect.y + rect.height) + 'px';
      }
      */
    }
  }
  onMouseLeave = (e) => {
    this.setState({
      style: ''
    });
    const tooltip = this.tooltip.current;
    if (tooltip) {
      tooltip.classList.remove('show');
    }
  }
  render () {
    const { text, ids, entities, dispatch, complaintId, ...passThrough } = { ...this.props };
    const relevantEntities = ids.map(id => entities.byId[id] || {
      color: '#cccccc'
    });
    const styles = this.getColorStyles(relevantEntities);
    const tooltip = this.createTooltip(relevantEntities);
    const inject = {
      className: 'entity',
      ref: this.entity,
      style: styles,
      onMouseEnter: this.onMouseEnter,
      onMouseLeave: this.onMouseLeave
    };
    return (
      <span data-tag-id={ids.join(',')} {...inject} {...passThrough}>
        <style>{this.state.style}</style>
        {text}
        {ReactDOM.createPortal(tooltip, document.body)}
      </span>
    );
  }
}

const mapStateToProps = (state, props) => {
  if (props.entities) {
    return {};
  }
  return {
    entities: state.complaintStuff.entities,
    complaintId: state.complaintStuff.id
  };
};

export default connect(mapStateToProps)(Tag);
