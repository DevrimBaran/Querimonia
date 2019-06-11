import React, { Component } from 'react';
import randomColor from 'randomcolor';
import TagCloud from 'react-tag-cloud';
import Block from 'components/Block/Block';
import Row from 'components/Row/Row';
import Content from 'components/Content/Content';

import Api from 'utility/Api';

class TagCloudTest extends Component {
  constructor(props) {
    super(props);
    this.state = {
      words: {},
      color: '#179c7d',
      factor: 10,
      size: 5,
    };
  }
  fetchData = () => {
    let query = {};
    this.refs.minDate.value && (query.date_min = this.refs.minDate.value);
    this.refs.maxDate.value && (query.date_max = this.refs.maxDate.value);
    this.refs.onlyWords.checked && (query.words_only = this.refs.onlyWords.checked);
    this.refs.count.value && this.refs.count.value > 0 && (query.count = this.refs.count.value);
    Api.get('/api/stats/tagcloud', query)
      .then(a => { console.log(a); return a; })
      .then(this.setData);
  }
  setData = (data) => {
    this.setState({ words: data });
  }
  onChange = (e) => {
    let s = {};
    s[e.target.id] = e.target.value;
    this.setState(s);
  }
  componentDidMount() {
    this.fetchData();
  }
  renderWord = (word, index) => {
    return (<abbr key={index} title={this.state.words[word]} style={{ fontSize: this.state.size - 2 + this.state.words[word] * this.state.factor }}>{word}</abbr>);
  }
  render() {
    return (
      <React.Fragment>
        <Block>
          <Row vertical={true}>
            <h6 className='center'>Wortliste</h6>
            <br />
            <div>
              <Row vertical={false} style={{ justifyContent: 'space-around' }}>
                <div>
                  <label htmlFor='minDate'>Von:</label><br />
                  <input type='date' id='minDate' ref='minDate' />
                </div>
                <div>
                  <label htmlFor='maxDate'>Bis:</label><br />
                  <input type='date' id='maxDate' ref='maxDate' />
                </div>
                <div>
                  <label htmlFor="onlyWords">Nur Wörter anzeigen:</label><br />
                  <input type="checkbox" id="onlyWords" ref="onlyWords" />
                </div>
                <div>
                  <label htmlFor="count">Wortanzahl:</label><br />
                  <input type="number" id="count" ref="count" defaultValue="0" min="0" />
                </div>
              </Row>
            </div>
            <div className="center">
              <input type='button' onClick={this.fetchData} value='aktualisieren' />
            </div>
            <Content>
              <TagCloud
                className='tag-cloud'
                style={{
                  fontFamily: 'sans-serif',
                  color: () => randomColor({
                    hue: this.state.color
                  }),
                  height: '100%'
                }}>
                {Object.keys(this.state.words).map(this.renderWord, this)}
              </TagCloud>
            </Content>
            <div>
              <Row vertical={false} style={{ justifyContent: 'center' }}>
                <div>
                  <label htmlFor='color'>Farbe:</label><br />
                  <input type='color' id="color" onChange={this.onChange} value={this.state.color} />
                </div>
                <div style={{ width: '2em' }}></div>
                <div>
                  <label htmlFor='factor'>Schriftgröße:</label><br />
                  <input type='number' id="size" onChange={this.onChange} value={this.state.size} />
                </div>
                <div style={{ width: '2em' }}></div>
                <div>
                  <label htmlFor='factor'>Faktor:</label><br />
                  <input type='number' id="factor" onChange={this.onChange} value={this.state.factor} />
                </div>
              </Row>
            </div>
            <br />
          </Row>
        </Block>
      </React.Fragment>
    );
  }
}
// ReactDOM.render(<TagCloudTest />, document.getElementById('root'));
export default TagCloudTest;
