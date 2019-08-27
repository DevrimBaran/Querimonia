/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Tag from './Tag';
import Modal from './Modal';
import Button from '../components/Button';
import { addEntity } from '../redux/actions/';
import Table from './Table';

class TaggedText extends Component {
  constructor (props) {
    super(props);
    let config = this.props.active.configuration;
    let extractorList = [];
    for (let i = 0; i < config.extractors.length; i++) {
      extractorList[config.extractors[i].label] = { name: config.extractors[i].name, type: config.extractors[i].type, color: config.extractors[i].color };
    }
    this.state = {
      editActive: false,
      newEntityQuery: null,
      extractorList: extractorList,
      selectExtractor: '',
      html: null
    };
  }

  parseText = (text, entities) => {
    if (!entities || entities.length === 0) {
      return text;
    }
    let html = [];
    let cpos = 0;
    let key = 0;
    if (entities) {
      for (const entity of entities) {
        // String before next entity
        !text.substring(cpos, entity.start) || html.push(<span key={key++} data-key={html.length}>{text.substring(cpos, entity.start)}</span>);
        // String that is entity
        html.push(<Tag key={key++} data-key={html.length} text={text.substring(entity.start, entity.end)} ids={entity.ids} />);
        cpos = entity.end;
      }
    }
    // String from last entity to end of text or complete text if there are no entities
    html.push(<span key={key++} data-key={html.length}>{text.substring(cpos, text.length)}</span>);
    return html;
  };

  addEntity = () => {
    let extractorLabel = document.getElementById('extractorChoose').value;
    let extractorInformation = this.state.extractorList[extractorLabel];
    let query = this.state.newEntityQuery;
    query['label'] = extractorInformation.name;
    query['extractor'] = extractorInformation.type;
    query['color'] = extractorInformation.color;
    if (!(query['label'] && query['extractor'] && query['color'])) {
      return;
    }
    this.props.dispatch(addEntity(this.props.active.id, query, null));
  };
  startEdit = () => {
    if (this.state.editActive) {
      window.removeEventListener('mouseup', this.handleMouseUp);
    } else {
      window.addEventListener('mouseup', this.handleMouseUp);
    }
    this.setState({
      editActive: !this.state.editActive
    });
    window.addEventListener('mousemove', this.updateModal);
  };
  updateModal = (e) => {
    this.setState({
      newEntityQuery: null,
      selectExtractor: document.getElementById('extractorChoose').value,
      html: null
    });
    document.getElementById('entityAdd_Modal').click();
    window.removeEventListener('mousemove', this.updateModal);
  }
  handleMouseUp = (e) => {
    e.stopPropagation();
    const selectedText = window.getSelection();
    const parentNode1 = window.getSelection().anchorNode.parentNode;
    const parentNode2 = window.getSelection().focusNode.parentNode;
    if (selectedText && selectedText.anchorOffset && selectedText.focusOffset && parentNode1.attributes['data-key'] && parentNode2.attributes['data-key']) {
      let text = this.props.text;
      let startIndex = selectedText.anchorOffset;
      let endIndex = selectedText.focusOffset;
      const newLabelString = text.substring(startIndex, endIndex);
      let query = {};
      query['start'] = startIndex;
      query['end'] = endIndex;
      if (startIndex > endIndex) {
        query['start'] = endIndex;
        query['end'] = startIndex;
      }
      query['setByUser'] = true;
      query['value'] = newLabelString;
      this.setState({
        newEntityQuery: query,
        editActive: false,
        selectExtractor: document.getElementById('extractorChoose').value,
        html: null
      });
      document.getElementById('entityAdd_Modal').click();
      window.removeEventListener('mouseup', this.handleMouseUp);
    }
  };

  renderModal = (id, text) => {
    let start = 0;
    let end = 0;
    let value = '';
    if (this.state.newEntityQuery) {
      start = this.state.newEntityQuery.start;
      end = this.state.newEntityQuery.end;
      value = this.state.newEntityQuery.value;
    }
    return <Modal title={'Entitäten hinzufügen'} id={'_modal'} key={id + '_modal'} htmlFor={id}>
      <div className='scrollableText'>
        {text.substring(0, start) }<mark>{text.substring(start, end)}</mark><span data-key={'text'}>{text.substring(end, text.length)}</span>
      </div>
      <Table className='addEntity-table'>
        <thead>
          <tr>
            <th><b> Label </b></th>
            <th><b> Text </b></th>
            <th><b> Aktion </b></th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td><select style={{ marginRight: '2em', fontSize: 'medium' }} id='extractorChoose'>
              {Object.keys(this.state.extractorList).map((extractor, i) => this.state.selectExtractor === extractor ? <option selected key={i}>{`${extractor}`}</option> : <option key={i}>{`${extractor}`}</option>)};
            </select></td>
            <td>
              <textarea readOnly className='entitytextbox' name='note' defaultValue={!this.state.editActive ? value : 'Bitte markieren sie den gewünschten Abschnitt!'} />
              <Button style={{ width: '50%', height: '25px', marginTop: '3px', cursor: 'pointer', fontSize: 'medium' }} icon='fas fa-marker' onClick={this.startEdit}>{!this.state.editActive ? 'Auswählen' : 'Abbrechen'}</Button>
            </td>
            <td>
              <Button disabled={!this.state.newEntityQuery} style={{ padding: '2px', cursor: 'pointer', fontSize: 'medium' }} icon='fas fa-save' onClick={this.addEntity}>Speichern</Button>
            </td>
          </tr>
        </tbody>
      </Table>
    </Modal>;
  };

  render () {
    const { text, entities } = { ...this.props };
    return (
      <div>
        <p>
          <span>{this.parseText(text, entities.calculated)}</span>
        </p>
        <div className='plus-item'>
          <i id={'entityAdd_Modal'} className={'fas fa-plus-circle fa-2x'} />
        </div>
        {this.renderModal('entityAdd_Modal', text)};
      </div>
    );
  }
}

export default TaggedText;
