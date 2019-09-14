/**
 * Renders a json object unminified in a html pre-tag.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import { pd } from 'pretty-data';

import Content from './../components/Content';

class Debug extends Component {
    static log = (...args) => {
      console.log(...args);
      return args[0];
    }
    render () {
      console.log(this.props.data);
      const { ...passThrough } = { ...this.props };
      return (<Content {...passThrough} style={{ flexBasis: '100%' }}><pre>
        {this.props.data ? pd.json(JSON.stringify(this.props.data)) : ('NO DATA')}
      </pre></Content>);
    }
}

export default Debug;
