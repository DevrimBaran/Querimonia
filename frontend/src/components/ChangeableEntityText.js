/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import TaggedText from './TaggedText';
import Modal from './Modal';

class ChangeableEntityText extends Component {
  constructor (props) {
    super(props);
    this.state = {};
  }

  renderModal = (tag, id) => {
    return <Modal htmlFor={id}>
      <select>
        <option>Test1</option>
        <option>Test2</option>
        <option>Test3</option>
      </select>
    </Modal>;
  };

  render () {
    return <TaggedText taggedText={this.props.taggedText} onClickHtml={this.renderModal} />;
  }
}

export default ChangeableEntityText;
