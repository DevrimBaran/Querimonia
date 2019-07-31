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
    return <Modal htmlFor={id} key={id}>
      Wählen sie eine der folgenden Entitäten:
      <div style={{ padding: '5px' }}>
        <select value={this.props.activeEntity || this.props.possibleEntities[0]} onChange={() => this.props.setActiveEntity(this.props.complaintId, 'Test')}>
          {
            this.props.possibleEntities.map((entity, i) => {
              return <option key={i}>
                {entity.label}
              </option>;
            })
          }
          <option>Test</option>
        </select>
      </div>
      Oder geben sie ganze einfach eine neue Entität ein:
      <div style={{ padding: '5px' }}>
        <input id={id + '_input'} placeholder={'Entität'} />
        <button onClick={() => this.props.setActiveEntity(this.props.complaintId, document.getElementById(id + '_input').value)}>Entität ändern</button>
      </div>
    </Modal>;
  };

  render () {
    return <TaggedText taggedText={this.props.taggedText} onClickHtml={this.renderModal} />;
  }
}

export default ChangeableEntityText;
