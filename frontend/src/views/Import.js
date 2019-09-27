/**
 * This class creates the Import view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { Link } from 'react-router-dom';

import api from './../utility/Api';

import Block from './../components/Block';
import Content from './../components/Content';
import Row from './../components/Row';
import Input from './../components/Input';

class Import extends Component {
  constructor (props) {
    super(props);

    this.state = {
      response: [],
      loading: false,
      type: null,
      value: null
    };
  }

  parseResponse = (response) => {
    if (response) {
      this.setState({ loading: false, type: null, value: '', response: Array.isArray(response) ? response : [response] });
    } else {
      this.setState({ loading: false, type: null, value: '', response: [] });
    }
  }

  onClick = (e) => {
    let response;
    switch (this.state.type) {
      case 'file':
        // eslint-disable-next-line
        const formData = new FormData();
        formData.append('respond_to', '');
        formData.append('file', this.state.value);
        response = api.post('/api/complaints/import', formData);
        break;
      default:
        response = api.post('/api/complaints/import', { text: this.state.value, respond_to: '' });
        break;
    }
    this.setState({ loading: true });
    response.then(this.parseResponse);
  }

  onChange = (e) => {
    if (e.target.value === '') {
      this.setState({ type: null, value: '' });
    } else {
      this.setState({ type: e.target.type, value: e.target.type === 'file' ? e.target.files[0] : e.target.value });
    }
  }
  // onDrop = (e) => {
  //   e.preventDefault();
  //   if (this.state.type !== 'text') {
  //     this.setState({ type: 'file' });
  //     this.fileInput.current.files = e.dataTransfer.files;
  //   }
  // }

  // onDragOver = (e) => {
  //   if (this.dragTimer) {
  //     clearTimeout(this.dragTimer);
  //   } else {
  //     document.getElementById('Import').classList.add('drag');
  //   }
  //   this.dragTimer = setTimeout(() => {
  //     document.getElementById('Import').classList.remove('drag');
  //     this.dragTimer = false;
  //   }, 200);
  // }

  render () {
    return (
      // <Block onDragOver={this.onDragOver} onDragLeave={this.onDragLeave}>
      <Block>
        <Row vertical className='centerColumn'>
          <h1 className='center'>Import</h1>
          {/* <div className='input' id='Import' onDrop={this.onDrop} onDragOver={this.onDragOver}> */}
          <div className={'input'} id='Import'>
            {this.state.type !== 'file' && <textarea className='textarea' style={{ resize: 'none', height: '200px' }} onChange={this.onChange} ref={this.textInput} placeholder='Geben Sie eine Beschwerde ein oder wählen Sie eine Datei aus.' />}
            <div className='center'>
              {this.state.type !== 'textarea' && <Input label='File' type='file' onChange={this.onChange} name='file' ref={this.fileInput} />}
              <p className='paddingVertical' style={{ textAlign: 'justify' }} >Beschwerden können in den Formaten pdf, doc, docx und txt hochgeladen werden.</p>
              <Input type='button' disabled={!this.state.type || this.state.loading} name='uploadButton' onClick={this.onClick} value='Importieren' />
            </div>
          </div>
          <Content style={{ height: '100%' }}>
            <div id='response'>
              {this.state.loading ? (
                <div className='center'>
                  <i style={{ color: 'var(--primaryAccentColor)' }} className='fa-spinner fa-spin fa fa-5x' />
                </div>
              ) : (
                this.state.response.map(complaint => (
                  <div className='complaint' key={complaint.id}>
                    <Link to={'/complaints/' + complaint.id}>
                      <h3>Anliegen #{complaint.id}</h3>
                      <p>{complaint.preview}</p>
                    </Link>
                    <br />
                  </div>
                ))
              )}
            </div>
          </Content>
        </Row>
      </Block>
    );
  }
}

export default Import;
