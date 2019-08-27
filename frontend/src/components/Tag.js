/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';
import ReactDOM from 'react-dom';
import { changeEntity, deleteEntity, addEntity } from '../redux/actions/actions';

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
    const color = (entity.extractor && this.props.colors[entity.extractor] && this.props.colors[entity.extractor][entity.label]
      ? this.props.colors[entity.extractor][entity.label] : entity.color) || '#cccccc';
    return avg + (this.getLuminance(color) / array.length);
  }
  minLuminance = (min, entity, array) => {
    const color = (entity.extractor && this.props.colors[entity.extractor] && this.props.colors[entity.extractor][entity.label]
      ? this.props.colors[entity.extractor][entity.label] : entity.color) || '#cccccc';
    return Math.min(min, this.getLuminance(color));
  }
  getGradient = (entity, i, entities) => {
    const pers = 100 / entities.length;
    const color = (entity.extractor && this.props.colors[entity.extractor] && this.props.colors[entity.extractor][entity.label]
      ? this.props.colors[entity.extractor][entity.label] : entity.color) || '#cccccc';
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
  editEntity = (label, deleteEnabled) => {
    const originalLabel = label;
    let originalLabelID = null;
    if (deleteEnabled) {
      originalLabelID = originalLabel.id;
    }

    this.setState({
      newEntityQuery: { ...this.state.newEntityQuery, label: originalLabel.label, extractor: originalLabel.extractor, color: originalLabel.color },
      originalLabelID: originalLabelID });
    this.startEdit();
  };
  startEdit = () => {
    if (this.state.editFormActive) return;
    if (this.state.editActive) {
      window.removeEventListener('mouseup', this.handleMouseUp);
    } else {
      window.addEventListener('mouseup', this.handleMouseUp);
    }

    this.setState({
      editActive: !this.state.editActive
    });
  };
  handleMouseUp = (e) => {
    e.stopPropagation();
    const selectedText = document.getSelection();
    if (selectedText && selectedText.anchorNode.parentNode.attributes['data-key'] && selectedText.focusNode.parentNode.attributes['data-key'] && selectedText.toString()) {
      const newLabelString = selectedText.toString();
      const baseOffset = selectedText.anchorOffset;
      const extentOffset = selectedText.focusOffset;
      const baseKey = selectedText.anchorNode.parentNode.attributes['data-key'].value;
      const extentKey = selectedText.focusNode.parentNode.attributes['data-key'].value;
      let globalOffsetStart = 0;
      let globalOffsetEnd = 0;
      Array.from(selectedText.focusNode.parentNode.parentNode.childNodes)
        .filter((htmlElement) => {
          return htmlElement.tagName === 'SPAN';
        }).map((spanElement) => {
          return {
            key: spanElement.attributes['data-key'].value,
            text: spanElement.innerHTML
          };
        }).forEach((element, i, thisArray) => {
          if (element.key === baseKey) {
            for (let j = 0; j < i; j++) {
              globalOffsetStart += thisArray[j].text.length;
            }
            globalOffsetStart += baseOffset;
          }
          if (element.key === extentKey) {
            for (let j = 0; j < i; j++) {
              globalOffsetEnd += thisArray[j].text.length;
            }
            globalOffsetEnd += extentOffset;
          }
        });
      if (globalOffsetEnd < globalOffsetStart) {
        let temp = globalOffsetStart;
        globalOffsetStart = globalOffsetEnd;
        globalOffsetEnd = temp;
      }
      let query = {};
      query['start'] = globalOffsetStart;
      query['end'] = globalOffsetEnd;
      query['setByUser'] = true;
      query['value'] = newLabelString;
      this.startEdit();
      this.setState({
        newEntityQuery: { ...this.state.newEntityQuery, start: query['start'], end: query['end'], value: query['value'], setByUser: query['setByUser'] }
      });
      this.props.dispatch(addEntity(this.props.complaintId, this.state.newEntityQuery, this.state.originalLabelID));
    }
  };
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
              <br />
              <b>{label.value}</b>
              <br />
              {/* eslint-disable-next-line */}
      <i className={'far fa-clone'} onClick={this.editEntity.bind(this, label, false)} style={{ cursor: 'pointer', margin: 'auto', padding: '5px' }} />
              {/* eslint-disable-next-line */}
      <i className={'far fa-edit'} onClick={this.editEntity.bind(this, label, true)} style={{ cursor: 'pointer', margin: 'auto', padding: '5px' }} />
              {/* eslint-disable-next-line */}
      <i className={'far fa-trash-alt'} onClick={this.remove(label.id)} style={{ cursor: 'pointer', margin: 'auto', padding: '5px' }} />
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
  }, {});
  if (props.entities) {
    return {
      colors: colors
    };
  }
  return {
    entities: state.complaintStuff.entities,
    complaintId: state.complaintStuff.id,
    colors: colors
  };
};

export default connect(mapStateToProps)(Tag);
