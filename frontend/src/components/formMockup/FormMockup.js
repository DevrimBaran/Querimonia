import React, { Component } from 'react';
import TaggedText from '../TaggedText/TaggedText';
import './FormMockup.css';

export class FormMockup extends Component {
  constructor(props) {
    super(props);
    React.Children.map(this.props.children, child => {
      if (child.type === 'input' || child.type === 'textarea') {
        return React.cloneElement(child, { onChange: this.handleChange})
      }
      return child;
    });
    for (let child of this.props.children) {
    }
    this.state = { response: null, data: {} };
  }

  handleChange = (e) => {
    console.log(e.target);
    this.setState((state, props) => {
      state.data[e.target.name] = e.target.value;
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
      <div className="form-mockup">
        {this.props.children}
        <button className='button' onClick={this.handleClick}>Senden</button>
        {this.state.response}
      </div>
    )
  }
}

export default FormMockup