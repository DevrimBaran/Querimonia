/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Input from './Input';
import Modal from './Modal';
import Button from '../components/Button';
import { addEntity } from '../redux/actions/';

class EditEntityModal extends Component {
  constructor (props) {
    super(props);
    let config = props.active.configuration;
    let extractorList = config.extractors.reduce((obj, extractor) => {
      obj[extractor.label] = { name: extractor.name, type: extractor.type, color: extractor.color };
      return obj;
    }, {});
    this.state = {
      extractorList,
      id: '',
      label: '',
      start: 0,
      end: 0
    };
  }

  addEntity = () => {
    const { start, end, label, extractorList } = { ...this.state };
    this.props.dispatch(addEntity(this.props.active.id, {
      start: start,
      end: end,
      value: this.props.text.substring(start, end),
      setByUser: true,
      preferred: false,
      label: extractorList[label].name,
      extractor: extractorList[label].type,
      color: extractorList[label].color
    }, this.state.id ? this.state.id : null));
  };
  mouseUp = (e) => {
    let selection = window.getSelection();
    console.log(selection);
    if (selection.anchorOffset === selection.focusOffset) {
      return;
    }
    this.setState({
      start: Math.min(selection.anchorOffset, selection.focusOffset),
      end: Math.max(selection.anchorOffset, selection.focusOffset)
    });
  }
  onChange = (e) => {
    this.setState({
      [e.name]: e.value
    });
  }
  onOpen = (e) => {
    console.log(e.target.dataset);
    this.setState(e.target.dataset);
  }
  render () {
    const { text } = { ...this.props };
    const { start, end, label, extractorList } = { ...this.state };
    return (
      <Modal title={'Entitäten hinzufügen'} htmlFor='[modal=editEntityModal]' onOpen={this.onOpen}>
        <div className='scrollableText'>
          <div>
            <span>{text.substring(0, start)}</span>
            <mark>{text.substring(start, end)}</mark>
            <span>{text.substring(end)}</span>
          </div>
          <div onMouseUp={this.mouseUp}>
            {text}
          </div>
        </div>
        <Input label='Label' type='select' value={label} values={Object.keys(extractorList)} name='label' onChange={this.onChange} />
        <Input label='Text' type='text' readOnly className='entitytextbox' name='note' value={start !== end ? text.substring(start, end) : 'Bitte markieren sie den gewünschten Abschnitt!'} />
        <Input label='Start' type='number' name='start' value={start} min={0} max={end} onChange={this.onChange} />
        <Input label='Ende' type='number' name='end' value={end} min={start} onChange={this.onChange} />
        <Button disabled={start === end} style={{ padding: '2px', cursor: 'pointer', fontSize: 'medium' }} icon='fas fa-save' onClick={this.addEntity}>Speichern</Button>
      </Modal>
    );
  }
}

export default EditEntityModal;
