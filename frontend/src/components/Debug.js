/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import { pd } from 'pretty-data';

import Content from 'components/Content';

class Debug extends Component {
    static log = (...args) => {
      console.log(...args);
      return args[0];
    }
    render () {
      console.log(this.props);
      return (<Content style={{ flexBasis: '100%' }}><pre>
        {pd.json(JSON.stringify(this.props.data))}
      </pre></Content>);
    }
}

export default Debug;
