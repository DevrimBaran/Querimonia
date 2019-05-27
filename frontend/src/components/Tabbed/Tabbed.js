import React, { Component } from 'react';
import './Tabbed.scss';

class Tabbed extends Component {
  constructor (props) {
    super(props);
    this.state = {
      index: 0
    };
  }
    handleClick = (i) => {
      this.setState({ index: i });
    }
    render () {
      return (
        <div className='tabbed'>
          {this.props.children.map((tab, i) => {
            return (
              [
                <span key={i} className={i === this.state.index ? 'tab active' : 'tab'} onClick={() => this.handleClick(i)}>{tab.props.label}</span>,
                tab
              ]
            );
          })}
        </div>
      );
    }
}

export default Tabbed;
