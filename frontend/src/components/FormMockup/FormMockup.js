import React, { Component } from 'react';
import TaggedText from '../TaggedText/TaggedText';
import './FormMockup.css';
//import fetch from './apiMock';

export class FormMockup extends Component {
  constructor(props) {
    super(props);

    this.state = { response: null, data: {} };
  }

  handleChange = (e) => {
    const target = e.target;
    this.setState(function(state, props) {
      state.data[target.name] = target.value;
      return state;
    });
  }

  handleClick = () => {
    let options = {
      method: this.props.method,
      credentials: 'include',
      mode: 'cors',
      headers: {
        'Content-Type': this.props.enctype
      },
      body: null
    };
    switch (this.props.enctype) {
      case 'application/json':
        options.body = JSON.stringify(this.state.data);
      break;
      case 'multipart/form-data':
        options.headers['Content-Type'] = undefined;
        let formData = new FormData();

        for (const name in this.state.data) {
          formData.append(name, this.state.data[name]);
        }
        
        options.body = formData;
        break;
    }

    let p = fetch(this.props.action, options)
      .then(response => {return response.json();});
    
    this.setState({ response: <TaggedText id="responseArea1" promise={p}></TaggedText>});
  };
  
  render() {
    const children = React.Children.map(this.props.children, child => {
      return React.cloneElement(child, { onChange: this.handleChange });
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