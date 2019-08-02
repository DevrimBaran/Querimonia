/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import TaggedText from './TaggedText';
import Modal from './Modal';
import Input from './Input';

class ChangeableEntityText extends Component {
  constructor (props) {
    super(props);
    this.state = {};
  }

  renderModal = (tag, id) => {
    const correctEntities = this.props.possibleEntities.flat().filter((entity) => {
      return entity.label === tag.label;
    }).map((entity) => {
      return {
        label: entity.label,
        value: entity.label
      };
    });
    // For Testing
    correctEntities.push({
      label: 'Test',
      value: 'Test'
    });
    correctEntities.push({
      label: 'Test2',
      value: 'Test2'
    });
    return <Modal htmlFor={id} key={id}>
      Wählen sie eine der folgenden Entitäten:
      <div style={{ padding: '5px' }}>
        <Input type='select' required values={correctEntities} id={id + '_select'} value={this.props.activeEntity || this.props.possibleEntities[0]} onChange={() => this.props.setActiveEntity(this.props.complaintId, document.getElementById(id + '_select').value)} />
      </div>
      Oder geben sie ganze einfach eine neue Entität ein:
      <div style={{ padding: '5px' }}>
        <Input type='text' id={id + '_input'} placeholder={'Entität'} />
        <button onClick={() => this.props.setActiveEntity(this.props.complaintId, document.getElementById(id + '_input').value)}>Entität ändern</button>
      </div>
    </Modal>;
  };

  render () {
    return <TaggedText taggedText={this.props.taggedText} appendHtml={this.renderModal} />;
  }
}

export default ChangeableEntityText;
