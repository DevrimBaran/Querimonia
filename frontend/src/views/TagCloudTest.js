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
      words: {},
      max:0
    };
  }
  fetchData = () => {
    let query = {};
    this.refs.minDate.value && (query.date_min = this.refs.minDate.value);
    this.refs.maxDate.value && (query.date_max = this.refs.maxDate.value);
    this.refs.onlyWords.checked && (query.words_only = this.refs.onlyWords.checked);
    this.refs.count.value && (query.count = this.refs.count.value);
    Api.get('/api/stats/tagcloud', query)
      .then(this.setData);
  }
  setData = (data) => {
    this.setState({words: data});
    var maxVal=0;
    for(var i=0;i<Object.keys(Object.keys(data)).length;i++){
      if(maxVal<this.state.words[Object.keys(data)[i]])
        maxVal=this.state.words[Object.keys(data)[i]];
    }
    this.setState({max: maxVal});
  }
  componentDidMount () {
    this.fetchData();
  }
  renderWord = (word, index) => {
    //declaration of the tags with relative size
    return (<div key={index} style={{ fontSize: (this.state.words[word]/this.state.max) * 150 }}>{word}</div>);
  }
  render () {
    return (
      <div className='app-outer'>
        <div className='app-inner'>
          <h1>Wortliste</h1>
          <div className="date">
            <label htmlFor="minDate">Von: </label>
            <input type="date" id="minDate" ref="minDate" />
            <label htmlFor="maxDate">  Bis: </label>
            <input type="date" id="maxDate" ref="maxDate" />
          </div>
          <div className="onlyWords">
            <label htmlFor="onlyWords">Nur Wörter anzeigen:</label>
            <input type="checkbox" id="onlyWords" ref="onlyWords" />
          </div>
          <div className="count">
            <label htmlFor="count">Anzahl der Tags: </label>
            <input type="number" id="count" ref="count" />
          </div>
          <div className="button">
            <input type="button" onClick={this.fetchData} value="aktualisieren"/>
          </div>
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
