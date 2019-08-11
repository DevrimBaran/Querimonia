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
    this.state = {
      taggedText: this.props.taggedText
    };
  }

  createEntity = (tag, value) => {
    return {
      ...tag,
      value: value,
      id: tag.variable + '_dummyEntity_' + this.props.responseId,
      label: tag.label[0].label
    };
  };

  renderModal = (tag, id) => {
    const fittingEntities = this.props.entities.flat().filter((entity) => {
      return entity.label === tag.label[0].label;
    }).map((entity) => {
      return {
        label: entity.value,
        value: entity.value
      };
    });
    return <Modal htmlFor={id} key={id}>
      Um die Entität auzutauschen wählen sie eine der folgenden Entitäten:
      <div style={{ padding: '5px' }}>
        <Input type='select'
          values={fittingEntities}
          id={id + '_select'}
          onChange={() => this.props.setActiveEntity(this.props.responseId, tag.variable, this.createEntity(tag, document.getElementById(id + '_select').value || ''))} />
      </div>
      ... oder geben sie ganze einfach eine neue Entität ein:
      <div style={{ padding: '5px' }}>
        <Input type='text' id={id + '_input'} placeholder={'Entität'} />
        <button
          onClick={() => this.props.setActiveEntity(this.props.responseId, tag.variable, this.createEntity(tag, document.getElementById(id + '_input' || '').value))}>Entität ändern</button>
      </div>
    </Modal>;
  };

  componentWillUpdate (props) {
    if (props.taggedText.text !== this.state.taggedText.text) {
      this.setState({
        taggedText: props.taggedText
      });
    }
  }

  render () {
    return <TaggedText taggedText={this.state.taggedText} appendHtml={this.renderModal} />;
  }
}

export default ChangeableEntityText;
