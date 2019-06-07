import React, { Component } from 'react';
import randomColor from 'randomcolor';
import TagCloud from 'react-tag-cloud';
//import CloudItem from './CloudItem';
import './style.css';

const styles = {
  large: {
    fontSize: 60,
    fontWeight: 'bold'
  },
  small: {
    opacity: 0.7,
    fontSize: 16
  }
};

class TagCloudTest extends Component {
  componentDidMount () {
    setInterval(() => {
      // this.forceUpdate();
    }, 3000);
  }

  render () {
    return (
      <div className='app-outer'>
        <div className='app-inner'>
          <h1>react-tag-cloud demo</h1>
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

            <div style={{ fontSize: 60 }}>Transformers</div>
            <div style={{ fontSize: 120 }}>Simpsons</div>
            <div style={{ fontSize: 120 }}>Dragon Ball</div>
            <div style={{ fontSize: 120 }}>Rick & Morty</div>
            <div style={{ fontFamily: 'courier' }}>He man</div>
            <div style={{ fontSize: 30 }}>World trigger</div>
            <div style={{ fontStyle: 'italic' }}>Avengers</div>
            <div style={{ fontWeight: 200 }}>Family Guy</div>
            <div style={{ color: 'green' }}>American Dad</div>
            <div className='tag-item-wrapper'>
              <div>
                                Hover Me Please!
              </div>
              <div className='tag-item-tooltip'>
                                HOVERED!
              </div>
            </div>
            <div>Gobots</div>
            <div>Thundercats</div>
            <div>M.A.S.K.</div>
            <div>GI Joe</div>
            <div>Inspector Gadget</div>
            <div>Bugs Bunny</div>
            <div>Tom & Jerry</div>
            <div>Cowboy Bebop</div>
            <div>Evangelion</div>
            <div style={{ fontSize: 120 }}>Bleach</div>
            <div>GITS</div>
            <div>Pokemon</div>
            <div>She Ra</div>
            <div>Fullmetal Alchemist</div>
            <div>Gundam</div>
            <div>Uni Taisen</div>
            <div>Pinky and the Brain</div>
            <div>Bobs Burgers</div>
            <div style={styles.small}>Dino Riders</div>
            <div style={styles.small}>Silverhawks</div>
            <div style={styles.small}>Bravestar</div>
            <div style={styles.small}>Starcom</div>
            <div style={styles.small}>Cops</div>
            <div style={styles.small}>Alfred J. Kwak</div>
            <div style={styles.small}>Dr Snuggles</div>
          </TagCloud>
        </div>
      </div>
    );
  }
}
// ReactDOM.render(<TagCloudTest />, document.getElementById('root'));
export default TagCloudTest;
