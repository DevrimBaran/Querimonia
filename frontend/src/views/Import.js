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
import Sentiment from './../components/Sentiment';
import Input from './../components/Input';

class ImportBlock extends Component {
  constructor (props) {
    super(props);
    this.textInput = React.createRef();
    this.fileInput = React.createRef();
    this.dragTimer = false;
    this.state = {
      response: [],
      loading: false,
      type: null
    };
  }

  renderResponse = (data) => {
    return (<tr key={data.id}>
      <td><h3>{data.id}</h3></td>
      <td>{data.state}</td>
      <td>{data.preview}</td>
      <td>{data.sentiment.emotion.value}</td>
      <td><Sentiment tendency={data.sentiment.tendency} /></td>
      <td>{data.properties.map((properties) => properties.value + ' (' + (properties.probabilities[properties.value] * 100) + '%)').join(', ')}</td>
      <td>{data.receiveDate} {data.receiveTime}</td>
      <td />
    </tr>);
  }

  parseResponse = (response) => {
    this.setState({ loading: false, type: null, response: Array.isArray(response) ? response : [response] });
    this.textInput.current.value = '';
    this.fileInput.current.files = null;
  }

  onClick = (e) => {
    let response;
    switch (this.state.type) {
      case 'textarea':
        response = api.post('/api/complaints/import', { text: this.textInput.current.value });
        break;
      case 'file':
        // eslint-disable-next-line
        const formData = new FormData();
        formData.append('file', this.fileInput.current.files[0]);
        response = api.post('/api/complaints/import', formData);
        break;
      default:
        // nothing to import
        return;
    }
    this.setState({ loading: true });
    response.then(this.parseResponse);
  }

  onChange = (e) => {
    if (e.target.value === '') {
      this.setState({ type: null });
    } else {
      this.setState({ type: e.target.type });
    }
  }
  onDrop = (e) => {
    e.preventDefault();
    if (this.state.type !== 'text') {
      this.setState({ type: 'file' });
      this.fileInput.current.files = e.dataTransfer.files;
    }
  }

  onDragOver = (e) => {
    if (this.dragTimer) {
      clearTimeout(this.dragTimer);
    } else {
      document.getElementById('Import').classList.add('drag');
    }
    this.dragTimer = setTimeout(() => {
      document.getElementById('Import').classList.remove('drag');
      this.dragTimer = false;
    }, 200);
  }

  render () {
    return (
      <Block onDragOver={this.onDragOver} onDragLeave={this.onDragLeave}>
        <h1 className='center'>Import</h1>
        <Row vertical className='centerColumn'>
          {/* <div className='input' id='Import' onDrop={this.onDrop} onDragOver={this.onDragOver}> */}
          <div className={'input' + (this.dragCounter > 0 && 'drag')} id='Import'>
            {this.state.type !== 'file' && <textarea className='textarea' style={{ resize: 'none', height: '200px' }} onChange={this.onChange} ref={this.textInput} placeholder='Geben Sie eine Beschwerde ein oder wählen Sie eine Datei aus.' />}
            <div className='center'>
              {this.state.type !== 'textarea' && <Input label='File' type='file' onChange={this.onChange} name='file' ref={this.fileInput} />}
              <p className='paddingVertical' style={{ textAlign: 'justify' }} >Beschwerden können in den Formaten pdf, doc, docx und txt hochgeladen werden.</p>
              <Input type='button' disabled={!this.state.type || this.state.loading} name='uploadButton' onClick={this.onClick} value='Importieren' />
            </div>
          </div>
          <Content>
            <div style={{ height: '100%' }} id='response'>
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
function Import () {
  return (
    <React.Fragment>
      <ImportBlock />
    </React.Fragment>
  );
}

export default Import;
