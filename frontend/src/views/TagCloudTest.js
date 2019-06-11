import React, { Component } from 'react';
import randomColor from 'randomcolor';
import TagCloud from 'react-tag-cloud';
// import CloudItem from './CloudItem';

import Api from 'utility/Api';

import './style.css';


class TagCloudTest extends Component {
  constructor(props) {
    super(props);
    this.state = {
      words: {}
    };
  }
  fetchData = () => {
    let query = {};
    this.refs.minDate.value && (query.date_min = this.refs.minDate.value);
    this.refs.maxDate.value && (query.date_max = this.refs.maxDate.value);
    Api.get('/api/stats/tagcloud', query)
      .then(this.setData);
  }
  setData = (data) => {
    this.setState({words: data});
  }
  componentDidMount () {
    this.fetchData();
  }
  renderWord = (word, index) => {
    return (<div key={index} style={{ fontSize: this.state.words[word] * 10 }}>{word}</div>);
  }
  render () {
    return (
      <div className='app-outer'>
        <div className='app-inner'>
          <h1>Wortliste</h1>
          <label htmlFor="minDate">Von:</label>
          <input type="date" id="minDate" ref="minDate" />
          <label htmlFor="maxDate">Bis:</label>
          <input type="date" id="maxDate" ref="maxDate" />
          <input type="button" onClick={this.fetchData} value="aktualisieren" />
          <TagCloud
            className='tag-cloud'
            style={{
              fontFamily: 'sans-serif',
              // fontSize: () => Math.round(Math.random() * 50) + 16,
              fontSize: 30,
              color: () => randomColor({
                hue: 'blue'
              }),
              padding: 5
            }}>
            {Object.keys(this.state.words).map(this.renderWord)}
          </TagCloud>
        </div>
      </div>
    );
  }
}
// ReactDOM.render(<TagCloudTest />, document.getElementById('root'));
export default TagCloudTest;
