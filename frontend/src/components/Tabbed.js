/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Content from './Content';

class Tabbed extends Component {
  constructor (props) {
    super(props);
    this.style = this.props.style || {};
    this.style['--tabCount'] = this.props.children ? this.props.children.length + 1 : 1;
    this.state = {
      index: 0
    };
  }
    handleClick = (i) => {
      this.setState({ index: i });
    }
    render () {
      return (
        <div className={'tabbed ' + this.props.className} style={this.style}>
          {this.props.children && this.props.children.map((tab, i) => {
            const { titleHeader } = { ...tab.props };
            let className = 'tab';
            if (i === this.state.index) {
              (className += ' active');
            }
            if (tab.disabled) {
              (className += ' disabled');
            }
            return (
              [
                <h5 key={'label' + i} title={titleHeader} className={className} onClick={tab.props.disabled ? undefined : () => this.handleClick(i)}>{tab.props.label}</h5>,
                <Content key={'content' + i}>{tab}</Content>
              ]
            );
          })}
        </div>
      );
    }
}

export default Tabbed;
