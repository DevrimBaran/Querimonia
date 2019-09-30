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
    if (!config) {
      config = {
        extractors: []
      };
    }
    let extractorList = config.extractors.reduce((obj, extractor) => {
      obj[extractor.label] = { name: extractor.name, type: extractor.type, color: extractor.color };
      return obj;
    }, {});
    this.state = {
      extractorList,
      id: '',
      label: '',
      value: '',
      start: 0,
      end: 0
    };
    EditEntityModal.ref = this;
  }

  addEntity = () => {
    const { start, end, label, value, extractorList } = { ...this.state };
    this.props.dispatch(addEntity(this.props.active.id, {
      start: start,
      end: end,
      value: value,
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
    const start = Math.min(selection.anchorOffset, selection.focusOffset);
    const end = Math.max(selection.anchorOffset, selection.focusOffset);
    this.setState({
      start: start,
      end: end,
      value: this.props.text.substring(start, end)
    });
  }
  onChange = (e) => {
    this.setState({
      [e.name]: e.value
    });
  }
  onOpen = (e) => {
    this.setState(e.target.dataset);
    document.addEventListener('keydown', this.handleKeyDown);
  }
  onClose = (e) => {
    document.removeEventListener('keydown', this.handleKeyDown);
  }
  register = (show) => {
    EditEntityModal.open = (e) => {
      show(e);
    };
  }
  hideModals = () => {
    for (const modal of document.querySelectorAll('.modal.show')) {
      modal.classList.remove('show');
    }
    document.removeEventListener('keydown', this.handleKeyDown);
  }
  handleKeyDown = (e) => {
    // Esc-Button-Event
    if (e.code === 'Escape') {
      this.hideModals();
    }
  }
  render () {
    const { text, entities } = { ...this.props };
    const { start, end, label, value, extractorList } = { ...this.state };
    const color = new Color(extractorList[label] ? extractorList[label].color : '#cccccc');
    let cpos = 0;
    let key = 0;
    const spitText = entities.filter(e => e.label === label).sort((a, b) => (a.start - b.start)).reduce((array, entity, x) => {
      if (entity.start < entity.end) {
        !text.substring(cpos, entity.start) || array.push(<span key={key++}>{text.substring(cpos, entity.start)}</span>);
        // String that is entity
        array.push(<span>{text.substring(entity.start, entity.end)}</span>);
        cpos = entity.end;
      };
      return array;
      // String from last entity to end of text or complete text if there are no entities
    }, []);
    return (
      <Modal title={'Entitäten hinzufügen'} register={this.register} onClose={this.onClose} onOpen={this.onOpen}>
        <div className='scrollableText'>
          <div style={{ color: 'transparent', '--color': color.background() }}>
            {spitText.map((text, i) => <span key={i}>{text}</span>)}
          </div>
          <div style={{ color: 'transparent', '--color': color.background() }}>
            {(start < end) && (
              <React.Fragment>
                <span>{text.substring(0, start)}</span>
                <span>{text.substring(start, end)}</span>
                <span>{text.substring(end)}</span>
              </React.Fragment>
            )}
          </div>
          <div onMouseUp={this.mouseUp}>
            <span>{text}</span>
          </div>
        </div>
        <div style={{ padding: '2rem 0 0 0' }}>
          <Input label='Label' type='select' value={label} values={Object.keys(extractorList)} name='label' onChange={this.onChange} />
          <Input label='Ausgewählt' type='text' readOnly className='entitytextbox' name='note' value={start !== end ? text.substring(start, end) : 'Bitte markieren sie den gewünschten Abschnitt!'} />
          <Input label='Wert' type='text' className='entitytextbox' name='value' value={value} onChange={this.onChange} />
          <Input label='Start' type='number' name='start' value={start} min={0} max={end} onChange={this.onChange} />
          <Input label='Ende' type='number' name='end' value={end} min={start} onChange={this.onChange} />
          <Button disabled={!((start < end || (start === end && value)) && label)} style={{ padding: '0.125rem', cursor: 'pointer', fontSize: 'medium' }} icon='fas fa-save' onClick={this.addEntity}>Speichern</Button>
        </div>
      </Modal>
    );
  }
}

export default EditEntityModal;
