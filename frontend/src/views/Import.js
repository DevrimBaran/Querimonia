/**
 * This class creates the Import view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import api from './../utility/Api';

import Complaint from './partials/Complaints';

import Table from './../components/Table';
import Block from './../components/Block';
import Content from './../components/Content';
import Row from './../components/Row';

class ImportBlock extends Component {
  constructor (props) {
    super(props);
    this.state = {
      response: null,
      loading: false,
      type: null
    };
  }

  parseResponse = (response) => {
    let complaints = (
      <Content>
        <Table>
          {Complaint.Header()}
          <tbody>
            {Array.isArray(response) ? this.state.issues.map(Complaint.List) : Complaint.List(response, 0) }
          </tbody>
        </Table>
      </Content>
    );
    this.setState({ loading: false, type: null, response: complaints });
    this.refs.textInput.value = '';
    this.refs.fileInput.files = null;
  }

  onClick = (e) => {
    let response;
    switch (this.state.type) {
      case 'textarea':
        response = api.post('/api/complaints/import', { text: this.refs.textInput.value });
        break;
      case 'file':
        // eslint-disable-next-line
        const formData = new FormData();
        formData.append('file', this.refs.fileInput.files[0]);
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
      this.refs.fileInput.files = e.dataTransfer.files;
    }
  }

  onDragOver = (e) => {
    e.stopPropagation();
    e.preventDefault();
  }

  render () {
    return (
      <Block>
        <Row vertical>
          <h6 className='center'>Import</h6>
          <div id='Import' style={{ margin: '1em' }} onDrop={this.onDrop} onDragOver={this.onDragOver}>
            {this.state.type !== 'file' && <textarea style={{ resize: 'none', height: '200px' }} onChange={this.onChange} ref='textInput' placeholder='Geben Sie eine Beschwerde ein oder wählen Sie eine Datei aus.' />}
            <div className='center'>
              {this.state.type !== 'textarea' && <input type='file' onChange={this.onChange} name='file' ref='fileInput' />}
              <input type='button' disabled={!this.state.type || this.state.loading} name='uploadButton' onClick={this.onClick} value='Importieren' />
            </div>
          </div>
          <Content>
            <div style={{ height: '100%' }} id='response'>
              {this.state.loading ? (<div className='center'><i style={{ color: 'var(--primaryAccentColor)' }} className='fa-spinner fa-spin fa fa-5x' /></div>) : (this.state.response)}
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
