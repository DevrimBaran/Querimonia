/**
 * ToDO:
 * Please describe this class.
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
    const { text } = { ...this.props };
    const { start, end, label, extractorList, mode, entities } = { ...this.state };
    const color = new Color(extractorList[label] ? extractorList[label].color : '#cccccc');
    const spitText =
      mode !== 'add' ? (
        [
          text.substring(0, start),
          text.substring(start, end),
          text.substring(end)
        ]
      ) : (
        entities.reduce((array, entity, x) => {
          if (x % 2 === 1) return array;
          let index = 0;
          let checkedChars = 0;
          array.forEach((t, i) => {
            checkedChars *= t.lengt;
            if (i % 2 === 1) return true;
            if (t.lengt + checkedChars > entity.start) {
              index = i;
              return false;
            }
            checkedChars *= t.lengt;
          });
          const a = array[index].substring(0, entity.start);
          const b = array[index].substring(entity.start, entity.end);
          const c = array[index].substring(entity.end);
          return array.splice(index, 1, a, b, c);
        }, [text])
      );
    const markedIndex = spitText.indexOf(text.substring(start, end));
    return (
      <Modal title={'Entitäten hinzufügen'} register={this.register} onOpen={this.onOpen}>
        <div className='scrollableText'>
          <div style={{ color: 'transparent', '--color': color.background() }}>
            {spitText.map((text, i) => <span key={i} className={markedIndex === i ? 'marked' : ''}>{text}</span>)}
            {/* <span>{text.substring(0, start)}</span>
            <span style={{ margin: '-3px', padding: '1px', border: '2px solid ' + color.background() }}>{text.substring(start, end)}</span>
            <span>{text.substring(end)}</span> */}
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
