/**
 * This class creates the Statistics view.
 *
 * @version <0.1>
 */

import Block from '../components/Block';
import Content from '../components/Content';
import Row from '../components/Row';
import React, { Component } from 'react';
import Api from '../utility/Api';
import Tabbed from '../components/Tabbed';
import Input from '../components/Input';
import StatsTable from '../components/StatsTable';
import StatsDiagram from '../components/StatsDiagram';
import Spinner from '../components/Spinner';
import Grid from '../components/Grid';

const EMOTION_COLORS = {
  Ekel: 'brown',
  Freude: 'green',
  Furcht: 'black',
  Trauer: 'pink',
  Ueberraschung: 'steelblue',
  Verachtung: 'orange',
  Wut: 'red'
};
const STATE_COLORS = {
  Unbekannt: 'gray',
  ERROR: 'red',
  ANALYSING: 'orange',
  NEW: 'green',
  IN_PROGRESS: 'steelblue',
  CLOSED: 'grey'
};

class Statistics extends Component {
  constructor (props) {
    super(props);
    this.container = React.createRef();
    this.catArray = [];
    this.categoriesData = [];
    this.entArray = [];
    this.entitiesData = [];
    this.rulArray = [];
    this.rulesData = [];
    this.monArray = [];
    this.monthsData = [];
    this.state = {
      categories: 0,
      entities: 0,
      rules: 0,
      months: 0,
      data: null
    };
  }

  componentDidMount = () => {
    Promise.all([
      Api.get('/api/stats/categoriesStats', {}),
      Api.get('/api/stats/entitiesStats', {}),
      Api.get('/api/stats/rulesStats', {}),
      Api.get('/api/stats/monthsStats', {}),
      Api.get('/api/components', {})
    ]).then((data) => {
      this.catArray = ['Anzahl', 'Sentiment', 'Emotion', 'Status'].map((k, i) => (
        { value: i, label: k }
      ));
      this.entArray = Object.keys(data[1]).map((k, i) => (
        { value: i, label: k }
      ));
      this.rulArray = ['Anzahl'].map((k, i) => (
        { value: i, label: k }
      ));
      this.monArray = ['Anzahl', 'Status', 'Durchschnittliche Bearbeitungszeit'].map((k, i) => (
        { value: i, label: k }
      ));
      this.categoriesData = this.getComplaintStatsData(data[0]);
      this.entitiesData = this.getEntitiesStatsData(data[1]);
      this.rulesData = this.getRulesStatsData(data[2], data[4]);
      this.monthsData = this.getMonthsStatsData(data[3]);
      this.setState({
        data: data
      });
    });
  }

  getComplaintStatsData = (data2) => {
    let datas = [];

    let statVar = 'count';
    let data = Object.values(data2).map((d, i) => { return d[statVar]; });
    let dataKeys = Object.keys(data2);
    datas.push({ data: this.sortData(data, dataKeys, true), header: ['Kategorie', 'Anzahl'] });

    statVar = 'tendency';
    data = Object.values(data2).map((d, i) => { return d[statVar]; });
    dataKeys = Object.keys(data2);
    datas.push({ data: this.sortData(data, dataKeys, true), header: ['Kategorie', 'Sentiment'] });

    statVar = 'emotion';
    let sum = 0;
    data = [];
    dataKeys = [];
    Object.values(data2).forEach((d, i) => { sum = 0; Object.values(d[statVar]).forEach((e, j) => { e = e * 100; let key = Object.keys(data2)[i]; let key2 = Object.keys(d[statVar])[j]; dataKeys.push(key); sum += e; data.push([e, sum, EMOTION_COLORS[key2], key2, key]); }); });
    datas.push({ data: this.sortData(data, dataKeys, false), keys: [...new Set(dataKeys)], colors: EMOTION_COLORS });

    statVar = 'status';
    data = [];
    dataKeys = [];
    Object.values(data2).forEach((d, i) => { sum = 0; Object.values(d[statVar]).forEach((e, j) => { e = e * 100; let key = Object.keys(data2)[i]; let key2 = Object.keys(d[statVar])[j]; dataKeys.push(key); sum += e; data.push([e, sum, STATE_COLORS[key2], key2, key]); }); });
    datas.push({ data: this.sortData(data, dataKeys, false), keys: [...new Set(dataKeys)], colors: STATE_COLORS });

    return datas;
  };

  getEntitiesStatsData = (data2) => {
    let datas = [];

    Object.keys(data2).forEach((k, i) => {
      let data = Object.values(data2[k]);
      let dataKeys = Object.keys(data2[k]);
      datas.push({ data: this.sortData(data, dataKeys, true), header: ['Wert', 'Anzahl'] });
    });
    return datas;
  };

  getRulesStatsData = (data2, rules) => {
    let datas = [];

    let data = Object.values(data2);
    let counter = 1;
    let dataKeys = Object.keys(data2).map(name => { let rule = rules.find(r => r.name === name); let id = (rule ? rule.id : 'gelöscht_' + counter++); return { name: name, id: id }; });
    datas.push({ data: this.sortData(data, dataKeys, true), header: ['Regel', 'Anzahl'] });
    return datas;
  };

  getMonthsStatsData = (data2) => {
    let datas = [];

    let statVar = 'count';
    let data = Object.values(data2).map((d, i) => { return d[statVar]; });
    let dataKeys = Object.keys(data2);
    datas.push({ data: this.sortData(data, dataKeys, true), header: ['Monat', 'Anzahl'] });

    statVar = 'status';
    let sum = 0;
    data = [];
    dataKeys = [];
    Object.values(data2).forEach((d, i) => { sum = 0; Object.values(d[statVar]).forEach((e, j) => { e = e * 100; let key = Object.keys(data2)[i]; let key2 = Object.keys(d[statVar])[j]; dataKeys.push(key); sum += e; data.push([e, sum, STATE_COLORS[key2], key2, key]); }); });
    datas.push({ data: this.sortData(data, dataKeys, false), keys: [...new Set(dataKeys)], colors: STATE_COLORS });

    statVar = 'processingDuration';
    data = Object.values(data2).map((d, i) => { return d[statVar]; });
    dataKeys = Object.keys(data2);
    datas.push({ data: this.sortData(data, dataKeys, true), header: ['Monat', 'Bearbeitungszeit'] });

    return datas;
  };

  editCountCategories = () => {
    let query = {};
    let count = document.getElementById('countCategories').value;
    query['count'] = (count < 0 ? 0 : count);
    Api.get('/api/stats/categoriesStats', query)
      .then(data => {
        let oldData = this.state.data;
        oldData[0] = data;
        this.categoriesData = this.getComplaintStatsData(data);
        this.setState({
          data: oldData
        });
      });
  }
  editCountEntities = () => {
    let query = {};
    let count = document.getElementById('countEntities').value;
    query['count'] = (count < 0 ? 0 : count);
    Api.get('/api/stats/entitiesStats', query)
      .then(data => {
        let oldData = this.state.data;
        oldData[1] = data;
        this.entitiesData = this.getEntitiesStatsData(data);
        this.setState({
          data: oldData
        });
      });
  }
  editCountRules = () => {
    let query = {};
    let count = document.getElementById('countRules').value;
    query['count'] = (count < 0 ? 0 : count);
    Api.get('/api/stats/rulesStats', query)
      .then(data => {
        let oldData = this.state.data;
        oldData[2] = data;
        this.rulesData = this.getRulesStatsData(data, this.state.data[4]);
        this.setState({
          data: oldData
        });
      });
  }
  editDateMonths = () => {
    let query = {};
    query['date_min'] = document.getElementById('startDateMonths').value;
    query['date_max'] = document.getElementById('endDateMonths').value;

    Api.get('/api/stats/monthsStats', query)
      .then(data => {
        let oldData = this.state.data;
        oldData[3] = data;
        this.monthsData = this.getMonthsStatsData(data);
        this.setState({
          data: oldData
        });
      });
  }

  sortData = (value, key, opt) => {
    var list = [];
    for (var j = 0; j < value.length; j++) { list.push({ 'value': value[j], 'key': key[j] }); }
    opt ? list.sort((a, b) => { return b.value - a.value; }) : list.sort((a, b) => { return a.key === b.key ? a.value[0] - b.value[0] : 0; });
    return list;
  }

  onChange = (e) => {
    this.setState({ [e.name]: e.value });
  }

  render () {
    if (this.state.data) {
      return (
        <React.Fragment>
          <Block>
            <Row vertical>
              <h1 className='center'>Statistiken</h1>
              <Content>
                <Tabbed>
                  <div className='row flex-column' titleHeader='Merkmale nach Kategorien aufgesplittet' label='Kategorien'>
                    <Grid columns='auto auto' style={{ borderBottom: '0.3125rem solid grey' }}>
                      <div style={{ textAlign: 'center' }}>
                        <Input type='select' required label='Option' values={this.catArray} name='categories' value={this.state.categories} onChange={this.onChange} />
                      </div>
                      <div style={{ textAlign: 'center' }}>
                        <Input type='number' min={0} id='countCategories' label='Anzahl' onChange={(e) => this.editCountCategories()} />
                      </div>
                    </Grid>
                    <Grid columns='45% 55%' rows='100%' style={{ overflow: 'hidden' }}>
                      <StatsTable data={this.categoriesData[this.state.categories]} />
                      <StatsDiagram style={{ overflow: 'auto' }} type={this.catArray[this.state.categories].label} data={this.categoriesData[this.state.categories]} />
                    </Grid>
                  </div>
                  <div className='row flex-column' titleHeader='Häufigsten erkannte Werte zu jedem Entitäts-Label' label='Entitäten'>
                    <Grid columns='auto auto' style={{ borderBottom: '0.3125rem solid grey' }}>
                      <div style={{ textAlign: 'center' }}>
                        <Input type='select' required label='Label' values={this.entArray} name='entities' value={this.state.entities} onChange={this.onChange} />
                      </div>
                      <div style={{ textAlign: 'center' }}>
                        <Input type='number' min={0} id='countEntities' label='Anzahl' onChange={(e) => this.editCountEntities()} />
                      </div>
                    </Grid>
                    <Grid columns='45% 55%' rows='100%' style={{ overflow: 'hidden' }}>
                      <StatsTable data={this.entitiesData[this.state.entities]} />
                      <StatsDiagram style={{ overflow: 'auto' }} data={this.entitiesData[this.state.entities]} />
                    </Grid>
                  </div>
                  <div className='row flex-column' titleHeader='Meist verwendeten Regeln mit ihrer Anzahl ' label='Regeln' style={{ height: '100%' }}>
                    <Grid columns='auto auto' style={{ borderBottom: '0.3125rem solid grey' }}>
                      <div style={{ textAlign: 'center' }}>
                        <Input type='select' required label='Option' values={this.rulArray} name='rules' value={this.state.rules} onChange={this.onChange} />
                      </div>
                      <div style={{ textAlign: 'center' }}>
                        <Input type='number' min={0} id='countRules' label='Anzahl' onChange={(e) => this.editCountRules()} />
                      </div>
                    </Grid>
                    <Grid columns='45% 55%' rows='100%' style={{ overflow: 'hidden' }}>
                      <StatsTable data={this.rulesData[this.state.rules]} />
                      <StatsDiagram style={{ overflow: 'auto' }} data={this.rulesData[this.state.rules]} />
                    </Grid>
                  </div>
                  <div className='row flex-column' titleHeader='Merkmale nach Monate aufgesplittet' label='Monate'>
                    <Grid columns='auto auto' style={{ borderBottom: '0.3125rem solid grey' }}>
                      <div style={{ textAlign: 'center' }}>
                        <Input type='select' required label='Option' values={this.monArray} name='months' value={this.state.months} onChange={this.onChange} />
                      </div>
                      <div style={{ textAlign: 'center' }}>
                        <Input type='date' id='startDateMonths' label='Start-Datum' onChange={(e) => this.editDateMonths()} />
                        <Input type='date' id='endDateMonths' label='End-Datum' onChange={(e) => this.editDateMonths()} />
                      </div>
                    </Grid>
                    <Grid columns='45% 55%' rows='100%' style={{ overflow: 'hidden' }}>
                      <StatsTable data={this.monthsData[this.state.months]} />
                      <StatsDiagram style={{ overflow: 'auto' }} data={this.monthsData[this.state.months]} />
                    </Grid>
                  </div>
                </Tabbed>
              </Content>
            </Row>
          </Block>
        </React.Fragment>
      );
    } else {
      return (
        <Spinner />
      );
    }
  }
}

// export default Statistics;
// {
//   render () {
//     if (!(this.state && this.state.data)) this.activateComplaint();
//     if (this.state && this.state.data) {
//       let data = this.state.data;

//       let catArray = [];
//       let monArray = [];
//       let entArray = [];
//       let rulArray = [];
//       let categoriesData = [];
//       let entitiesData = [];
//       let rulesData = [];
//       let monthsData = [];
//       if (data[0] && !data[0].exception) {
//         catArray = ['Anzahl', 'Sentiment', 'Emotion', 'Status'];
//         categoriesData = this.getComplaintStatsData(data[0]);
//       }
//       if (data[1] && !data[1].exception) {
//         entArray = Object.keys(data[1]);
//         entitiesData = this.getEntitiesStatsData(data[1]);
//       }
//       if (data[2] && !data[2].exception) {
//         rulArray = ['Anzahl'];
//         rulesData = this.getRulesStatsData(data[2]);
//       }
//       if (data[3] && !data[3].exception) {
//         monArray = ['Anzahl', 'Status', 'Durchschnittliche Bearbeitungszeit'];
//         monthsData = this.getMonthsStatsData(data[3]);
//       }

//       return (
//         <React.Fragment>
//           <Block>
//             <Row vertical>
//               <h1 className='center'>Statistiken</h1>
//               <Content>
//                 <Tabbed>
//                   <div titleHeader='Merkmale nach Kategorien aufgesplittet' label='Kategorien'>
//                     <Table className='stats-table'>
//                       <tbody>
//                         <tr>
//                           <td style={{ width: '50%', borderBottom: '0.3125em solid grey' }}><Input type='select' required label='Option' values={catArray} onChange={(e) => showMe(e, catArray, 1)} /></td>
//                           <td style={{ width: '50%', borderBottom: '0.3125em solid grey' }}><Input type='number' min={0} id='countCategories' label='Anzahl' onChange={(e) => this.editCountCategories()} /></td>
//                         </tr>
//                         <tr>
//                           <td>
//                             {categoriesData.map((k, i) => <StatsTable key={i} id={catArray[i] + '1_t'} style={{ display: 'none' }} data={k} />)}
//                           </td>
//                           <td>
//                             {categoriesData.map((k, i) => <StatsDiagram key={i} id={catArray[i] + '1_d'} style={{ display: 'none' }} data={k} />)}
//                           </td>
//                         </tr>
//                       </tbody>
//                     </Table>
//                   </div>
//                   <div titleHeader='Häufigsten erkannte Werte zu jedem Entitäts-Label' label='Entitäten'>
//                     <Table className='stats-table'>
//                       <tbody>
//                         <tr>
//                           <td style={{ width: '50%', borderBottom: '0.3125em solid grey' }}><Input type='select' required label='Label' values={entArray} onChange={(e) => showMe(e, entArray, 2)} /></td>
//                           <td style={{ width: '50%', borderBottom: '0.3125em solid grey' }}><Input type='number' min={0} id='countEntities' label='Anzahl' onChange={(e) => this.editCountEntities()} /></td>
//                         </tr>
//                         <tr>
//                           <td>
//                             {Object.keys(this.state.data[1]).map((k, i) => <StatsTable key={i} id={entArray[i] + '2_t'} style={{ display: 'none' }} data={entitiesData[i]} />)}
//                           </td>
//                           <td>
//                             {Object.keys(this.state.data[1]).map((k, i) => <StatsDiagram key={i} id={entArray[i] + '2_d'} style={{ display: 'none' }} data={entitiesData[i]} />)}
//                           </td>
//                         </tr>
//                       </tbody>
//                     </Table>
//                   </div>
//                   <div titleHeader='Meist verwendeten Regeln mit ihrer Anzahl ' label='Regeln'>
//                     <Table className='stats-table'>
//                       <tbody>
//                         <tr>
//                           <td style={{ width: '50%', borderBottom: '0.3125em solid grey' }}><Input type='select' required label='Option' values={rulArray} onChange={(e) => showMe(e, rulArray, 3)} /></td>
//                           <td style={{ width: '50%', borderBottom: '0.3125em solid grey' }}><Input type='number' min={0} id='countRules' label='Anzahl' onChange={(e) => this.editCountRules()} /></td>
//                         </tr>
//                         <tr>
//                           <td>
//                             {rulesData.map((k, i) => <StatsTable key={i} id={rulArray[i] + '3_t'} style={{ display: 'none' }} data={k} />)}
//                           </td>
//                           <td>
//                             {rulesData.map((k, i) => <StatsDiagram key={i} id={rulArray[i] + '3_d'} style={{ display: 'none' }} data={k} />)}
//                           </td>
//                         </tr>
//                       </tbody>
//                     </Table>
//                   </div>
//                   <div titleHeader='Merkmale nach Monate aufgesplittet' label='Monate'>
//                     <Table className='stats-table'>
//                       <tbody>
//                         <tr>
//                           <td style={{ width: '50%', borderBottom: '0.3125em solid grey' }}><Input type='select' required label='Option' values={monArray} onChange={(e) => showMe(e, monArray, 4)} /></td>
//                           <td style={{ width: '50%', borderBottom: '0.3125em solid grey' }}><Input type='date' id='startDateMonths' label='Start-Datum' onChange={(e) => this.editDateMonths()} /><Input type='date' id='endDateMonths' label='End-Datum' onChange={(e) => this.editDateMonths()} /></td>
//                         </tr>
//                         <tr>
//                           <td>
//                             {monthsData.map((k, i) => <StatsTable key={i} id={monArray[i] + '4_t'} style={{ display: 'none' }} data={k} />)}
//                           </td>
//                           <td>
//                             {monthsData.map((k, i) => <StatsDiagram key={i} id={monArray[i] + '4_d'} style={{ display: 'none' }} data={k} />)}
//                           </td>
//                         </tr>
//                       </tbody>
//                     </Table>
//                   </div>
//                 </Tabbed>
//               </Content>
//             </Row>
//           </Block>
//         </React.Fragment>
//       );
//     } else {
//       return (
//         <React.Fragment>
//           <Block>
//             <Row vertical>
//               <h1 className='center'>Statistiken</h1>
//               <Content>
//                 <Tabbed>
//                   <div label='Kategorien' />
//                   <div label='Entitäten' />
//                   <div label='Regeln' />
//                   <div label='Monate' />
//                 </Tabbed>
//               </Content>
//             </Row>
//           </Block>
//         </React.Fragment>
//       );
//     }
//   }
// }

// function showMe (e, idAr, id) {
//   let val = e.value;
//   var vis = 'none';
//   for (var i = 0; i < idAr.length; i++) {
//     vis = 'none';
//     if (idAr[i] === val) {
//       vis = 'block';
//     }
//     document.getElementById(idAr[i] + id + '_t').style.display = vis;
//     document.getElementById(idAr[i] + id + '_d').style.display = vis;
//   }
// }

export default Statistics;
