/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import TaggedText from '../../components/TaggedText';
import Api from '../../utility/Api';
import Modal from '../../components/Modal';

class EditableEntityText extends Component {
  constructor (props) {
    super(props);
    this.state = {
      complaintId: null,
      taggedText: this.props.taggedText,
      editFormActive: false,
      editActive: false,
      originalLabels: null,
      originalLabelID: null
    };
  }

  deleteEntity = (id) => {
    const originalLabel = this.state.originalLabels.get(id);
    Api.delete('/api/complaints/' + this.state.complaintId + '/entities/' + originalLabel.id, {})
      .then((data) => {
        if (Array.isArray(data)) {
        // deep copy of data
          let entities = JSON.parse(JSON.stringify(data));
          // Updates the entity list and responses with the new values
          this.props.refreshEntities(this.props.active, entities);
          this.setState({
            taggedText: ({ text: this.props.taggedText.text, entities: data })
          });
        // Api.patch('/api/responses/' + this.state.id + '/refresh');
        }
      });
  };

  addEntity = () => {
    let query = this.state.newEntityQuery;
    if (!this.state.editEntity) {
      let extractorQuery = document.getElementById('chooseExtractor').value.split(' (').map(extractor => extractor.replace(')', ''));
      let config = this.props.active.configuration.extractors;
      let extractor = config.find((extractor) => extractor.name === extractorQuery[1]);
      let color = extractor.colors.find((color) => color.label === extractorQuery[0]);
      query['label'] = extractorQuery[0];
      query['extractor'] = extractorQuery[1];
      query['color'] = color.color;
    }
    if (!(query['label'] && query['extractor'])) {
      return;
    }
    (this.state.originalLabelID
      ? Api.put('/api/complaints/' + this.state.complaintId + '/entities/' + this.state.originalLabelID, query)
      : Api.post('/api/complaints/' + this.state.complaintId + '/entities', query)).then((data) => {
      if (Array.isArray(data)) {
      // deep copy of data
        let entities = JSON.parse(JSON.stringify(data));
        // Updates the entity list and responses with the new values
        this.props.refreshEntities(this.props.active, entities);
        this.setState({
          taggedText: ({ text: this.props.taggedText.text, entities: data }),
          editFormActive: false,
          editEntity: false
        });
      // Api.patch('/api/responses/' + this.state.complaintId + '/refresh');
      }
    });
  };

  // edit or copy the Entity
  editEntity = (id, deleteEnabled) => {
    const originalLabel = this.state.originalLabels.get(id);
    let originalLabelID = null;
    if (deleteEnabled) {
      originalLabelID = originalLabel.id;
    }
    this.setState({
      newEntityQuery: { ...this.state.newEntityQuery, label: originalLabel.label, extractor: originalLabel.extractor, color: originalLabel.color },
      editEntity: true,
      originalLabelID: originalLabelID });
    this.startEdit();
    // Modal.hideModals();
    for (const modal of document.querySelectorAll('.modal.show')) {
      modal.classList.remove('show');
    }
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

  abortEdit = () => {
    this.setState({
      editFormActive: false
    });
  };

  handleMouseUp = (e) => {
    e.stopPropagation();
    const selectedText = window.getSelection();
    if (selectedText && selectedText.anchorNode.parentNode.parentNode.className === 'tagged-text' && selectedText.focusNode.parentNode.parentNode.className === 'tagged-text' && selectedText.anchorNode.parentNode.attributes['data-key'] && selectedText.focusNode.parentNode.attributes['data-key'] && selectedText.toString()) {
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
      if (this.state.editEntity) {
        this.setState({
          newEntityQuery: { ...this.state.newEntityQuery, start: query['start'], end: query['end'], value: query['value'], setByUser: query['setByUser'] }
        });
        this.addEntity();
      } else {
        let config = this.props.active.configuration;
        let extractorList = [];
        for (let i = 0; i < config.extractors.length; i++) {
          for (let j = 0; j < config.extractors[i].colors.length; j++) {
            extractorList.push(config.extractors[i].colors[j].label + ' (' + config.extractors[i].name + ')');
          }
        }
        this.setState({
          editFormActive: true,
          newEntityQuery: query,
          newEntityString: newLabelString,
          extractorList: extractorList,
          originalLabelID: null
        });
      }
    }
  };

  setOriginalLabels = (originalLabels) => {
    this.setState({
      originalLabels: originalLabels
    });
  };

  componentDidMount () {
    this.setState({
      complaintId: this.props.complaintId
    });
  }

  renderModal = (tag, id) => {
    let labels = tag.label;
    let labelArray = labels.map((label, i) => {
      return <div style={
        (i !== 0 ? { marginTop: '0.4em',
          border: '2px solid ' + label.color,
          textAlign: 'center',
          padding: '4px' }
          : { border: '2px solid ' + label.color,
            textAlign: 'center',
            padding: '4px' })
      } key={i}>
        <i>{label.label}</i>
        <br />
        <b>{label.value}</b>
        <br />
        {/* eslint-disable-next-line */}
      <i className={'far fa-clone'} onClick={this.editEntity.bind(this, label.id, false)} style={{ cursor: 'pointer', margin: 'auto', padding: '5px' }} />
        {/* eslint-disable-next-line */}
      <i className={'far fa-edit'} onClick={this.editEntity.bind(this, label.id, true)} style={{ cursor: 'pointer', margin: 'auto', padding: '5px' }} />
        {/* eslint-disable-next-line */}
      <i className={'far fa-trash-alt'} onClick={() => { if (window.confirm('Wollen sie die Entität wirklich löschen?')) this.deleteEntity(label.id)} } style={{ cursor: 'pointer', margin: 'auto', padding: '5px' }} />
      </div>;
    });
    return <Modal key={id + '_modal'} htmlFor={id}>
      {
        labelArray
      }
    </Modal>;
  };

  renderAddButton = () => {
    return <div style={{ display: 'block', paddingTop: '10px', margin: 'auto', textAlign: 'center', borderTop: '1px solid lightGrey', width: '90%', marginTop: '10px' }}>
      <i style={this.state.editActive ? { color: 'rgb(31, 130, 191)', cursor: 'pointer' } : { color: 'rgb(9, 101, 158)', cursor: 'pointer' }}
        className='fas fa-plus-circle fa-2x'
        onClick={this.startEdit} />
      {this.state.editActive ? <i style={{ display: 'block', fontSize: '0.8em', marginTop: '3px' }}>Bitte gewünschten Abschnitt markieren</i> : null}
      {this.state.editFormActive ? <div style={{ marginTop: '5px' }}> <div>{this.state.newEntityString}</div>
        <b> Entität: </b>
        <select id='chooseExtractor'>
          {this.state.extractorList.map((extractor, i) => <option key={i}>{`${extractor}`}</option>)};
        </select> <br />
        <i style={{ color: 'green', cursor: 'pointer', padding: '5px' }} onClick={this.addEntity} className='far fa-check-circle fa-2x' />
        <i style={{ color: 'red', cursor: 'pointer', padding: '5px' }} onClick={this.abortEdit} className='far fa-times-circle fa-2x' /> </div> : null}
    </div>;
  };

  render () {
    return <div>
      <TaggedText taggedText={this.props.taggedText} appendHtml={this.renderModal} setOriginalLabels={this.setOriginalLabels} />
      { this.renderAddButton() }
    </div>;
  }
}

export default EditableEntityText;
