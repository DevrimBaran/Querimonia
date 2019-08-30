/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';
import ReactDOM from 'react-dom';
import { changeEntity, deleteEntity } from '../redux/actions/';

class Tag extends Component {
  constructor (props) {
    super(props);
    this.state = {
      style: ' ',
      editActive: false
    };
    this.tooltip = React.createRef();
    this.tooltip2 = React.createRef();
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
    const color = '#cccccc';
    // (entity.extractor && this.props.colors[entity.extractor] && this.props.colors[entity.extractor][entity.label]
    //   ? this.props.colors[entity.extractor][entity.label] : entity.color) || '#cccccc';
    return avg + (this.getLuminance(color) / array.length);
  }
  minLuminance = (min, entity, array) => {
    const color = '#cccccc';
    // (entity.extractor && this.props.colors[entity.extractor] && this.props.colors[entity.extractor][entity.label]
    //   ? this.props.colors[entity.extractor][entity.label] : entity.color) || '#cccccc';
    return Math.min(min, this.getLuminance(color));
  }
  getGradient = (entity, i, entities) => {
    const pers = 100 / entities.length;
    const color = '#cccccc';
    // (entity.extractor && this.props.colors[entity.extractor] && this.props.colors[entity.extractor][entity.label]
    //   ? this.props.colors[entity.extractor][entity.label] : entity.color) || '#cccccc';
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
  remove = (id) => (e) => {
    if (window.confirm('Wollen sie die Entität wirklich löschen?')) {
      this.props.dispatch(deleteEntity(this.props.complaintId, id));
    }
  }
  preferr = (id) => (e) => {
    const entity = this.props.entities.byId[id] || {};
    this.modifyEntity({ id: id, preferred: !entity.preferred });
  }
  createTooltip = (entities, b) => {
    return (
      b ? (<div ref={this.tooltip} className='tooltip'>
        {entities.map((label, i) => (
          <div key={i}>
            <span className='dot' style={{ marginRight: '0.4em', backgroundColor: label.color }} />
            {label.label}
          </div>
        ))}
      </div>)
        : (<div ref={this.tooltip2} className='tooltip'>
          {entities.map((label, i) => (
            <div style={
              (i !== 0 ? { marginLeft: '0.4em',
                border: '2px solid ' + label.color,
                textAlign: 'center',
                float: 'left',
                padding: '4px' }
                : { border: '2px solid ' + label.color,
                  textAlign: 'center',
                  float: 'left',
                  padding: '4px' })
            } key={i}>
              <i>{label.label}</i>
              <br style={{ marginBottom: '2px' }} />
              <b>{label.value}</b>
              <br style={{ marginBottom: '2px' }} />
              <span className='action-button'>
                <i title='Kopieren' id='editEntity' className={'far fa-clone'} />
              </span>
              <span className='action-button'>
                <i title='Bearbeiten' id='editEntity' className={'far fa-edit'} />
              </span>
              <span className='action-button'>
                <i title='Löschen' className={'far fa-trash-alt'} onClick={this.remove(label.id)} />
              </span>
            </div>
          ))}
        </div>)
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

    const tooltip2 = this.tooltip2.current;
    if (tooltip2) {
      tooltip2.classList.remove('show');
    }
  }
  onMouseClick = (e) => {
    this.onMouseLeave(e);

    const element = e.target;
    const rect = element.getBoundingClientRect();
    const tooltip = this.tooltip2.current;
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
    }
  }
  render () {
    const { text, ids, entities, dispatch, complaintId, ...passThrough } = { ...this.props };
    const relevantEntities = ids.map(id => entities.byId[id] || {
      color: '#cccccc'
    });
    const styles = this.getColorStyles(relevantEntities);
    const tooltip = this.createTooltip(relevantEntities, true);
    const tooltip2 = this.createTooltip(relevantEntities, false);
    const inject = {
      className: 'entity',
      ref: this.entity,
      style: styles,
      onMouseEnter: this.onMouseEnter,
      onMouseLeave: this.onMouseLeave,
      onClick: this.onMouseClick
    };
    return (
      <span data-tag-id={ids.join(',')} {...inject} {...passThrough}>
        <style>{this.state.style}</style>
        {text}
        {ReactDOM.createPortal(tooltip, document.body)}
        {ReactDOM.createPortal(tooltip2, document.body)}
      </span>
    );
  }
}

const mapStateToProps = (state, props) => {
  let colors = null;
  /* eslint-disable-next-line */
  state.currentConfig.extractors.reduce((obj, extractor) => {
    let labels = {};
    if (extractor.colors) {
      /* eslint-disable-next-line */
      extractor.colors.map((color) => {
        labels[color.label] = color.color;
      });
    };
    obj[extractor.name] = labels;
    colors = obj;
    return obj;
  }, {});
  if (props.entities) {
    return {
      colors: colors
    };
  }
  return {
    entities: state.complaintStuff.entities,
    complaintId: state.complaintStuff.id,
    config: state.complaints.data.active.configuration
  };
};

export default connect(mapStateToProps)(Tag);
