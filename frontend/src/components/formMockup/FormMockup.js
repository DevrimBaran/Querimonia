import React, { Component } from 'react'

export class FormMockup extends Component {
  constructor() {
    super()
    this.state = {};
  } 

  handleClick = () => {
    fetch(this.props.action, {method: "post", body: document.getElementById("textarea1").value, mode: "no-cors"})
    .then(data => JSON.parse(data.body))  
    .then(console.log());
  };

  render() {
    return (
      <div className="form-mockup">
        <textarea id="textarea1" name="text"/>
        <input type="submit" onClick={this.handleClick} />
      </div>
    )
  }
}

export default FormMockup