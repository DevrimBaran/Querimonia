/**
 * This class creates the Stats view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';
import D3 from './../components/D3';

class Stats extends Component {
  constructor (props) {
    super(props);
    this.state = {
      type: 'list'
    };
    this.data = { 'dass': 12, 'bus': 9, 'uhr': 7, 'busfahrer': 7, 'dame': 5, 'junge': 4, 'richtung': 4, 'ignoriert': 4, 'fahren': 4, 'leider': 3, 'hattingen': 3, 'heute': 3, 'wirklich': 3, 'kommen': 3, 'auskunft': 3, 'verärgert': 3, 'geehrt': 3, 'tag': 3, 'müssen': 3, 'herr': 3, 'verspätung': 3, 'öffnen': 2, 'wünschen': 2, 'ankommen': 2, 'direkt': 2, 'tür': 2, 'stunde': 2, 'warten': 2, 'erhalten': 2, 'schön': 2, 'weiß': 2, 'vorne': 2, 'kälte': 2, 'bogestra': 2, 'hoffen': 2, 'geben': 2, 'warum': 2, 'linie': 2, 'mal': 2, 'stand': 2, 'klein': 2, 'straße': 2, 'verstehen': 2, 'planmäßig': 2, 'sowas': 2, 'all': 2, 'fahrgast': 2, 'nutzen': 2, 'zumindest': 2, 'gesellschaft': 2, 'immer': 2, 'karlsplatz': 1, 'sollen': 1, 'gelaufen': 1, 'weilen': 1, 'anfahren': 1, 'elberfeld': 1, 'erneut': 1, 'unterwegs': 1, 'morgen': 1, 'gesagt': 1, 'meier': 1, 'inzwischen': 1, 'umsonst': 1, 'gelassen': 1, 'ab': 1, 'taxi': 1, 'statt': 1, 'einstellen': 1, 'nadja': 1 };
  }
  change = () => {
    this.setState((state) => ({
      type: state.type === 'list' ? 'cloud' : 'list'
    }));
  }
  render () {
    return (
      <React.Fragment>
        <Block>
          <Row vertical>
            <Content>
              <D3 type={this.state.type} data={this.data} />
              <button onClick={this.change}>Cloud</button>
            </Content>
          </Row>
        </Block>
      </React.Fragment>
    );
  }
}
export default Stats;
