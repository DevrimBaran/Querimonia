import React, { Component } from 'react';

import Block from 'components/Block/Block';

import api from 'utility/Api';
import Complaint from 'components/Complaint/Complaint';
import Content from 'components/Content/Content';

class ImportBlock extends Component {
  constructor(props) {
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
        {Array.isArray(response) ? this.state.issues.map(Complaint) : Complaint(response, 0) }
      </Content>
    );
    this.setState({ loading: false, type: null, response: complaints});
    this.refs.textInput.value = "";
    this.refs.fileInput.files = null;
  }

  onClick = (e) => {
    let response;
    switch (this.state.type) {
      case 'textarea':
        response = api.post('/api/complaints/import', { text: this.refs.textInput.value });
        break;
      case 'file':
        const formData = new FormData();
        formData.append('file', this.refs.fileInput.files[0]);
        response = api.post('/api/complaints/import', formData);
        break;
      default:
        //nothing to import
        return;
    }
    this.setState({loading: true});
    response.then(this.parseResponse);
  }

  onChange = (e) => {
    this.setState({ type: e.target.type});
  }
  onDrop = (e) => {
    e.preventDefault();
    if (this.state.type === 'text') {
      return;
    } else {
      this.setState({type:'file'});
      this.refs.fileInput.files = e.dataTransfer.files;
    }
  }

  onDragOver = (e) => {
    e.stopPropagation();
    e.preventDefault();
  }

  render() {
    return (
      <Block>
        <h6 className='center'>Import</h6>
        <div id="Import" onDrop={this.onDrop} onDragOver={this.onDragOver}>
          {this.state.type !== 'file' && <textarea onChange={this.onChange} ref="textInput" placeholder="Geben Sie eine Beschwerde ein oder wählen Sie eine Datei aus." />}
          {this.state.type !== 'textarea' && <input type='file' onChange={this.onChange} name='file' ref='fileInput' />}
          <input type='button' disabled={!this.state.type || this.state.loading} name='uploadButton' onClick={this.onClick} value='Importieren' />
        </div>
        <div id="response">
          {this.state.loading ? (<i className="spinner" />) : (this.state.response)}
        </div>
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
