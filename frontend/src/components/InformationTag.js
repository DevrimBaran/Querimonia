/**
 * Tooltip with detailed information e.g. probabilities
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Tooltip from './Tooltip';

class InformationTag extends Component {
  constructor (props) {
    super(props);
    this.tooltip = React.createRef();
  }
  render () {
    const { text, probabilities, ...passThrough } = { ...this.props };
    const tooltip = Tooltip.create();
    return (
      <span {...tooltip.events}{...passThrough}>
        {text}
        {!(Object.entries(probabilities).length === 0 && isNaN(probabilities))
          ? <Tooltip {...tooltip.register}>
            {<div>
              {isNaN(probabilities) ? Object.keys(probabilities)
                .map(subject => <li key={subject}>{`${subject}: ${probabilities[subject]}`}</li>) : probabilities}
            </div>}
          </Tooltip> : null}
      </span>
    );
  }
}

export default InformationTag;
