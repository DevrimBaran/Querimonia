/**
 * Pop-up window to change category, sentiment and emotion
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Input from './Input';
import Modal from './Modal';
import Button from './Button';
import { editComplaint } from '../redux/actions';

class EditDetailsModal extends Component {
  constructor (props) {
    super(props);
    EditDetailsModal.ref = this;
  }

  editCategory = () => {
    const { category, sentiment, emotion, mode } = { ...this.state };
    let query = {};
    if (mode === 'category') {
      query['subject'] = category;
    } else if (mode === 'sentiment') {
      query['tendency'] = sentiment;
    } else if (mode === 'emotion') {
      query['sentiment'] = emotion;
    }

    this.props.dispatch(editComplaint(this.props.active.id, this.props.complaintStuff, query));
    for (const modal of document.querySelectorAll('.modal.show')) {
      modal.classList.remove('show');
    }
  };
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
    EditDetailsModal.open = (e) => {
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
    const { title, category, sentiment, emotion, propertyindex, mode } = { ...this.state };
    let emotionList = ['Ekel', 'Freude', 'Furcht', 'Trauer', 'Ueberraschung', 'Verachtung', 'Wut'];
    let categoryList = (propertyindex ? Object.keys(this.props.active.properties[propertyindex].probabilities) : null);
    return (
      <Modal title={title} register={this.register} contentName={'editModal'} onClose={this.onClose} onOpen={this.onOpen}>
        { mode === 'category'
          ? <Input label='Kategorie' type='select' required value={category} values={categoryList}name={'category'} onChange={this.onChange} />
          : (mode === 'sentiment'
            ? <Input label='Sentiment' style={{ width: '9.375rem' }} type='number' value={sentiment} step='0.01' min={-1} max={1} name={'sentiment'} onChange={this.onChange} />
            : <Input label='Emotion' type='select' required value={emotion} values={emotionList} name={'emotion'} onChange={this.onChange} />) }
        <Button style={{ marginLeft: '0.625rem', padding: '0.125rem', cursor: 'pointer', fontSize: 'medium' }} icon='fas fa-save'
          onClick={this.editCategory}>Speichern</Button>
      </Modal>
    );
  }
}

export default EditDetailsModal;
