/**
 * Popup to edit entities.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Input from './Input';
import Modal from './Modal';
import Button from './Button';
import { addEntity } from '../redux/actions/';
import { Color } from '../utility/colors';

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
    EditEntityModal.ref = this;
  }

  addEntity = () => {
    const { start, end, label, extractorList } = { ...this.state };
    this.props.dispatch(addEntity(this.props.active.id, {
      start: start,
      end: end,
      value: this.props.text.substring(start, end),
      setByUser: true,
      preferred: false,
      label: label,
      extractor: extractorList[label].name,
      color: extractorList[label].color
    }, this.state.id ? this.state.id : null));
  };
  mouseUp = (e) => {
    let selection = window.getSelection();
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
    this.setState(e.target.dataset);
  }
  register = (show) => {
    EditEntityModal.open = (e) => {
      show(e);
    };
  }
  render () {
    const { text, entities } = { ...this.props };
    const { start, end, label, extractorList } = { ...this.state };
    const color = new Color(extractorList[label] ? extractorList[label].color : '#cccccc');
    let cpos = 0;
    let key = 0;
    const spitText = entities.filter(e => e.label === label).sort((a, b) => (a.start - b.start)).reduce((array, entity, x) => {
      !text.substring(cpos, entity.start) || array.push(<span key={key++}>{text.substring(cpos, entity.start)}</span>);
      // String that is entity
      array.push(<span>{text.substring(entity.start, entity.end)}</span>);
      cpos = entity.end;
      return array;
      // String from last entity to end of text or complete text if there are no entities
    }, []);
    return (
      <Modal title={'Entitäten hinzufügen'} register={this.register} onOpen={this.onOpen}>
        <div className='scrollableText'>
          <div style={{ color: 'transparent', '--color': color.background() }}>
            {spitText.map((text, i) => <span key={i}>{text}</span>)}
          </div>
          <div style={{ color: 'transparent', '--color': color.background() }}>
            <span>{text.substring(0, start)}</span>
            <span>{text.substring(start, end)}</span>
            <span>{text.substring(end)}</span>
          </div>
          <div onMouseUp={this.mouseUp}>
            <span>{text}</span>
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
