import React, { Component } from 'react';
import TaggedText from '../TaggedText/TaggedText';
import './FormMockup.css';
import { Promise } from 'q';

export class FormMockup extends Component {
  constructor(props) {
    super(props);

    this.state = { response: null, data: {} };
  }

  handleChange = (e) => {
    var target = e.target;
    this.setState(function(state, props) {
      state.data[target.name] = target.value;
      return state;
    });
  }

  handleClick = () => {
    let formData = new FormData();
  
    for (let name in this.state.data) {
      formData.append(name, this.state.data[name]);
    }
  
    let p = fetch(this.props.action, {method: "post", body: formData})
      .then(response => {return response.json();});
    
    this.setState({ response: <TaggedText id="responseArea1" promise={p}></TaggedText>});
  };

  render() {
    return (
      <div className="form-mockup" {...this.props}>
        <textarea onChange={this.handleChange} name="text" placeholder='Bitte geben sie die Beschwerde ein oder laden eine Textdatei hoch.' style={{ resize: 'none', width: '400px', height: '100px' }} />
        <input onChange={this.handleChange} name="file" type="file"></input>
        <button className='button' onClick={this.handleClick}>Senden</button>
        {this.state.response}
      </div>
    )
  }
}

export default FormMockup