import React, { Component } from 'react';
import TaggedText from '../TaggedText/TaggedText';
import './FormMockup.css';
import { runInThisContext } from 'vm';
//import fetch from '../../tests/apiMock';

export class FormMockup extends Component {
  constructor(props) {
    super(props);

    this.state = { response: null, data: {} };
    
    this.handleClick = this.handleClick.bind(this);
  }

  handleClick() {
    let options = {
      method: this.props.method,
      mode: 'cors',
      headers: {
        'Host': 'http://localhost:8080',
        'Content-Type': this.props.enctype
      },
      body: null
    };

    switch (this.props.enctype) {
      case 'application/json':
        var data = {};

        for (const index in this.refs) {
          const input = this.refs[index];
          data[input.attributes.name.value] = input.value;
        }

        options.body = JSON.stringify(data);
      break;
      case 'multipart/form-data':
        delete options.headers['Content-Type'];
        let formData = new FormData();

        for (const index in this.refs) {
          const input = this.refs[index];
          formData.append(input.attributes.name.value, input.type === "file" ? input.files[0] : input.value);
        }
        
        options.body = formData;
        break;
    }

    let p = fetch(this.props.action, options)
      .then(response => {return response.json();});
    
    this.setState({ response: <TaggedText id="responseArea1" promise={p}></TaggedText>});
  };
  
  render() {
    const children = React.Children.map(this.props.children, (child, idx) => {
      return React.cloneElement(child, { ref: idx });
    });
    return (
      <div className="form-mockup">
        {children}
        <button className='button' onClick={this.handleClick}>Senden</button>
        {this.state.response}
      </div>
    )
  }
}

export default FormMockup