/**
 * This class creates the TagCloud view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
// import randomColor from 'randomcolor';
// import TagCloud from 'react-tag-cloud';
import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';
import Api from './../utility/Api';
import D3 from './../components/D3';
import Input from './../components/Input';
import Form from './../components/Form';
import DownloadButton from './../components/DownloadButton';
import Toggle from './../components/Toggle';

import { Color } from '../utility/colors';
import ListTable from '../components/ListTable';

var cloud = require('../assets/js/d3.layout.cloud');
class TagCloud extends Component {
  constructor (props) {
    super(props);
    this.state = {
      redraw: 0,
      words: {},
      minOccurrence: 0,
      maxOccurrence: 0,
      lemmatizedWords: {},
      lemmatizedMinOccurrence: 0,
      lemmatizedMaxOccurrence: 0,
      lemmaOrigin: {},
      color: '#000000',
      listView: false,
      lemmatize: false,
      query: {
        date_min: undefined,
        date_max: undefined,
        count: 70,
        emotion: undefined,
        subject: undefined,
        words_only: true
      }
    };
  }
  renderCloud = (target, data, d3, loaded) => {
    const padding = [0, 0];
    const hsl = new Color(data.color).hsl();
    if (hsl.l === 100) { // white
      hsl.l = 50;
      hsl.s = 70;
      hsl.random = (perc) => {
        hsl.h = (1 - perc) * 270;
        return hsl.css();
      };
    } else if (hsl.s === 0) { // grey
      hsl.random = (perc) => {
        hsl.l = (1 - perc) * 40 + 40;
        return hsl.css();
      };
    } else { // color
      hsl.l = 50;
      hsl.random = (perc) => {
        hsl.s = (1 - perc) * 40 + 40;
        return hsl.css();
      };
    }
    let draw = (words) => {
      loaded();
      target.firstChild && target.firstChild.remove();
      let text = d3.select(target).append('svg')
        .attr('width', layout.size()[0])
        .attr('height', layout.size()[1])
        .style('margin', 'auto')
        .style('display', 'block')
        .append('g')
        .attr('transform', 'translate(' + layout.size()[0] / 2 + ',' + layout.size()[1] / 2 + ')')
        .selectAll('text')
        .data(words)
        .enter().append('text');

      text
        .style('font-family', 'Impact')
        .style('font-weight', 'normal')
        .style('font-size', function (d) {
          return d.size + 'px';
        })
        .attr('fill', d => {
          return hsl.random((d.size - 20) / 110);
        })
        .attr('text-anchor', 'middle')
        .attr('transform', function (d) {
          return 'translate(' + [d.x, d.y] + ')rotate(' + d.rotate + ')';
        })
        .text(function (d) {
          return d.text;
        });

      text
        .append('title')
        .text(d => d.text + ' - ' + d.value + 'mal');
    };

    let layout = cloud()
      .size([target.clientWidth - padding[0] * 2, (target.clientHeight || 500) - (padding[1]) * 2])
      .words(data.words)
      .spiral('rectangular')
      .padding(1)
      .rotate(function () {
        return 0;
      })
      .font('Impact')
      .fontWeight('normal')
      .fontSize(function (d) {
        return d.size;
      })
      .on('end', draw);

    layout.timeInterval(10);
    layout.start();

    return true;
  };
  renderList = (target, data, d3) => {
    const table = d3.select(target).append('table');
    const thead = table.append('thead').append('tr');
    thead.append('th').text('Wort').style('font-weight', 'bold');
    thead.append('th').text('Häufigkeit').style('font-weight', 'bold');
    const row = table.append('tbody')
      .selectAll('tr')
      .data(data.words)
      .enter()
      .append('tr');

    if (this.state.lemmatize) {
      row.attr('title', d => this.state.lemmaOrigin[d.text].sort((a, b) => b.count - a.count).map(w => `${w.word}: ${w.count}`).join('\n'));
    };

    row.append('td')
      .text(d => d.text);
    row.append('td')
      .text(d => d.value)
      .style('text-align', 'right')
      .style('padding-left', '1.25rem');
  };

    onChange = (e) => {
      this.setState({ [e.name]: e.value });
    }

    changeQuery = (e) => {
      this.setState(state => ({ query: { ...state.query, [e.name]: e.value } }));
    }

    downloadSvg = () => {
      let svgElement = document.getElementById('d3Container').firstChild;
      svgElement.setAttribute('xmlns', 'http://www.w3.org/2000/svg');
      return svgElement.outerHTML;
    };

    downloadCsv = () => {
      let csv = Object.keys(this.state.lemmatize ? this.state.lemmatizedWords : this.state.words)
        .map(w => ({ word: w, count: this.state.lemmatize ? this.state.lemmatizedWords[w] : this.state.words[w] }))
        .sort((a, b) => {
          return b.count - a.count;
        })
        .map(w => `${w.word};${w.count}`)
        .join('\r\n');
      return 'Token;Anzahl\r\n' + csv;
    };

    createWordArray = () => {
      const wordsObject = this.state.lemmatize ? this.state.lemmatizedWords : this.state.words;
      let wordArray = [];
      Object.keys(wordsObject).forEach((element) => {
        wordArray.push({
          text: element,
          value: wordsObject[element],
          size: this.calculateSize(wordsObject[element])
        });
      });
      return wordArray.sort((a, b) => a.value === b.value ? (a.text < b.text ? -1 : 1) : b.value - a.value);
    };

    calculateSize = (size) => {
      const tmax = this.state.lemmatize ? this.state.lemmatizedMaxOccurrence : this.state.maxOccurrence; // Höchste Anzahl
      const tmin = this.state.lemmatize ? this.state.lemmatizedMinOccurrence : this.state.minOccurrence; // mindest Anzahl an Wörter
      const fmax = 130; // maximale Schriftgröße
      const fmin = 20; // minimale Schriftgröße
      return Math.max(fmax * ((size - tmin) / (tmax - tmin)), fmin);
    };

    fetchData = (e) => {
      e && e.preventDefault();
      this.setState(state => ({ redraw: state.redraw + 1 }));
      Api.get('/api/stats/tagcloud', this.state.query)
        .then(this.lemmatizeData);
    };

    lemmatizeData = (data) => {
      if (data) {
        Api.post('/python/lemmatize', { text: Object.keys(data).join(' ') })
          .then((lemma) => this.setData(data, lemma));
      }
    }

    setData = (data, lemma) => {
      if (data) {
        const max = Math.max(...Object.values(data));
        const min = Math.min(...Object.values(data));
        // TODO: Calculate other constants
        this.setState({ words: data, maxOccurrence: max, minOccurence: min });
      };
      if (lemma) {
        const lemmatizedWords = {};
        const lemmaOrigin = {};
        for (let word in lemma) {
          if (!lemmatizedWords[lemma[word]]) {
            lemmaOrigin[lemma[word]] = [];
            lemmatizedWords[lemma[word]] = 0;
          };
          lemmaOrigin[lemma[word]].push({
            word: word,
            count: data[word]
          });
          lemmatizedWords[lemma[word]] += data[word] || 0;
        }
        const max = Math.max(...Object.values(lemmatizedWords));
        const min = Math.min(...Object.values(lemmatizedWords));
        this.setState({ lemmaOrigin: lemmaOrigin, lemmatizedWords: lemmatizedWords, lemmatizedMaxOccurrence: max, lemmatizedMinOccurence: min });
      };
    };

    componentDidMount () {
      this.fetchData();
    }

    render () {
      return (
        <React.Fragment>
          <Block>
            <Row vertical>
              <Form onSubmit={this.fetchData}>
                <Input type='date' label='Eingangsdatum (von)' name='date_min' onChange={this.changeQuery} value={this.state.query.date_min} />
                <Input type='date' label='Eingangsdatum (bis)' name='date_max' onChange={this.changeQuery} value={this.state.query.date_max} />
                <Input type='number' label='Wortanzahl' name='count' onChange={this.changeQuery} value={this.state.query.count} />
                <Input type='select' label='Emotion' multiple name='emotion' onChange={this.changeQuery} value={this.state.query.emotion} values={[
                  { label: 'Ekel', value: 'Ekel' },
                  { label: 'Freude', value: 'Freude' },
                  { label: 'Furcht', value: 'Furcht' },
                  { label: 'Kummer', value: 'Kummer' },
                  { label: 'Verachtung', value: 'Verachtung' },
                  { label: 'Wut', value: 'Wut' },
                  { label: 'Überraschung', value: 'Überraschung' }
                ]} />
                <Input type='select' label='Kategorie' multiple name='subject' onChange={this.changeQuery} value={this.state.query.subject} values={[
                  { label: 'Fahrt nicht erfolgt', value: 'Fahrt nicht erfolgt' },
                  { label: 'Fahrer unfreundlich', value: 'Fahrer unfreundlich' },
                  { label: 'Sonstiges', value: 'Sonstiges' }
                ]} />
                <Input type='checkbox' label='Nur Wörter' name='words_only' onChange={this.changeQuery} value={this.state.query.words_only} />
              </Form>
              <div style={{ display: 'flex', 'justifyContent': 'space-evenly', 'alignItems': 'center' }}>
                <span>
                  <DownloadButton icon='fas fa-3x fa-file-csv' type='text/csv; charset=utf-8' name='Wortanzahl.csv' onClick={this.downloadCsv} />
                  <DownloadButton disabled={this.state.listView} icon='fas fa-3x fa-file-image' type='image/svg+xml; charset=utf-8' name='Tagcloud.svg' onClick={this.downloadSvg} />
                </span>
                <span>
                  <Toggle on='fas fa-cloud' off='fas fa-list' state={!this.state.listView} style={{ '--width': '5.625rem', '--height': '2.1875rem' }} onChange={(e) => this.setState({ listView: !e.target.checked })} />
                </span>
                <span>
                  <Input type='checkbox' label='Stammformen' name='lemmatize' onChange={this.onChange} value={this.state.lemmatize} />
                </span>
                <span>
                  <Input type='colorpicker' label='Farbton' name='color' onChange={this.onChange} value={this.state.color} />
                </span>
              </div>
              <Content style={{ paddingTop: '0rem' }}>
                <D3
                  id='d3Container'
                  render={this.renderCloud}
                  data={{ color: this.state.color, words: this.createWordArray() }}
                  style={{
                    height: '100%',
                    display: 'flex',
                    justifyContent: 'center'
                  }}
                />
                <div style={{
                  position: 'absolute',
                  backgroundColor: 'white',
                  top: '-1.25rem',
                  left: '0rem',
                  right: '0rem',
                  height: '100%',
                  display: this.state.listView ? 'block' : 'none'
                }}>
                  <ListTable
                    style={{ margin: 'auto', paddingTop: '1.125rem' }}
                    header={['Wort', 'Vorkommen']}
                    data={this.createWordArray().map(w => (
                      [
                        this.state.lemmatize ? (
                          <span title={this.state.lemmaOrigin[w.text].sort((a, b) => b.count - a.count).map(w => `${w.word}: ${w.count}`).join('\n')}>{w.text}</span>
                        ) : (
                          w.text
                        ),
                        w.value
                      ]
                    ))}
                  />
                </div>
              </Content>
            </Row>
          </Block>
        </React.Fragment>
      );
    }
}

export default TagCloud;
