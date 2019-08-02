/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import Tooltip from './Tooltip';

import merge from 'deepmerge';

import { connect } from 'react-redux';

const LABEL_COLORS = {
  default: '#cccccc'
};

class TaggedText extends Component {
  constructor (props) {
    super(props);

    this.state = {
      taggedText: '',
      originalLabels: new Map()
    };
  }

  parseText (taggedText) {
    if (!taggedText) return '';
    if (!taggedText.text) return taggedText;
    let html = [];
    let cpos = 0;
    let key = 0;
    if (Array.isArray(taggedText.entities)) {
      this.prepareEntities(taggedText.entities);
      for (const tag of taggedText.entities) {
        // TODO: uuid
        const id = String(Math.floor(Math.random() * (10000000)));
        // String before next entity
        !taggedText.text.substring(cpos, tag.start) || html.push(<span key={key++} data-key={html.length}>{taggedText.text.substring(cpos, tag.start)}</span>);
        // String that is entity
        html.push(<span id={id} key={key++} data-key={html.length} className='tag' style={this.createBackground(tag)}>{taggedText.text.substring(tag.start, tag.end)}</span>);
        // Tooltip for that entity
        html.push(this.createTooltip(tag, id));
        if (this.props.appendHtml) {
          html.push(this.props.appendHtml(tag, id));
        }
        cpos = tag.end;
      }
    }
    // String from last entity to end of text or complete text if there are no entities
    html.push(<span key={key} data-key={html.length}>{taggedText.text.substring(cpos, taggedText.text.length)}</span>);
    return html;
  };

  // handles overlapping labels
  prepareEntities = (entities) => {
    if (entities.length === 0) return;
    entities.forEach((entity) => {
      // TODO: uuid
      const id = String(Math.floor(Math.random() * (10000000)));
      this.state.originalLabels.set(id, JSON.parse(JSON.stringify(entity)));
      if (this.props.setOriginalLabels) {
        this.props.setOriginalLabels(this.state.originalLabels);
      }
      entity.label = [{
        label: entity.label,
        id: id
      }];
    });

    let i = 0;
    const compare = (a, b) => { return a.start - b.start || a.end - b.end; };
    while (i < entities.length - 1) {
      entities.sort(compare);
      const entityA = entities[i];
      const entityB = entities[i + 1];

      if (entityA.start === entityB.start) {
        if (entityA.end === entityB.end) {
          entityA.label = [...entityA.label, ...entityB.label];
          entities.splice(i + 1, 1);
        } else {
          entityA.label = [...entityA.label, ...entityB.label];
          entityB.start = entityA.end;
        }
      } else if (entityA.end === entityB.end) {
        entityB.label = [...entityA.label, ...entityB.label];
        entityA.end = entityB.start;
      } else if (entityA.end > entityB.end) {
        entities.push({
          label: entityA.label,
          start: entityB.end,
          end: entityA.end
        });
        entityB.label = [...entityA.label, ...entityB.label];
        entityA.end = entityB.start;
      } else if (entityA.end < entityB.end && entityA.end > entityB.start) {
        entities.push({
          label: [...entityA.label, ...entityB.label],
          start: entityB.start,
          end: entityA.end
        });
        entityA.end = entityB.start;
        entityB.start = entityA.end;
      } else {
        i++;
      }
    }
    return entities;
  };

  getTextColor = (color) => {
    console.log(color);
    const rgb = color.match(/#(..)(..)(..)/);
    const luminance = 0.299 * parseInt(rgb[1], 16) + 0.587 * parseInt(rgb[2], 16) + 0.114 * parseInt(rgb[3], 16);
    const blackLum = 33.043;
    const whiteLum = 255;
    if (Math.abs(blackLum - luminance) < Math.abs(whiteLum - luminance)) {
      return '#ffffff';
    } else {
      return '#202124';
    }
  };

  // calculates the proper background colors for the given labels
  createBackground = (tag) => {
    let labels = tag.label;
    const individualHeightPercentage = 100 / labels.length;
    let linearGradient = '';
    let textColor = '#202124';
    labels.forEach((label, i) => {
      const color = (tag.extractor && this.props.colors[tag.extractor] ? this.props.colors[tag.extractor][label.label] : this.props.defaultColors[label.label]) || LABEL_COLORS['default'];
      textColor = this.getTextColor(color);
      linearGradient += color +
        (i !== 0 ? ' ' + String(individualHeightPercentage * (i)) + '%,' : ',') +
        color +
        ' ' +
        String(individualHeightPercentage * (i + 1)) +
        '%' +
        (i !== labels.length - 1 ? ',' : '');
    });
    return { color: textColor, backgroundImage: 'linear-gradient(' + linearGradient + ')' };
  };

  /**
   * Creates the tooltips for the entity-labels
   * If showOptions is true, the tooltips appear in the tooltip
   */
  createTooltip = (tag, id) => {
    let labels = tag.label;
    let labelArray = labels.map((label, i) => {
      const color = (tag.extractor && this.props.colors[tag.extractor] ? this.props.colors[tag.extractor][label.label] : this.props.defaultColors[label.label]) || LABEL_COLORS['default'];
      return <div key={i}>
        <span className='dot' style={
          {
            backgroundColor: color,
            height: '10px',
            width: '10px',
            borderRadius: '50%',
            display: 'inline-block',
            marginLeft: '5px' }
        } /> {label.label} <br />
      </div>;
    });
    return <Tooltip key={id + '_tooltip'} htmlFor={id}>
      {
        labelArray
      }
    </Tooltip>;
  };

  componentWillUpdate = (props) => {
    if (props.taggedText !== this.props.taggedText) {
      this.setState({
        taggedText: this.parseText(JSON.parse(JSON.stringify(props.taggedText)))
      });
    }
  };

  componentDidMount () {
    this.setState({
      taggedText: this.parseText(JSON.parse(JSON.stringify(this.props.taggedText)))
    });
  }

  render () {
    return (
      <span className='tagged-text'>
        {this.state.taggedText}
      </span>
    );
  };
}

const mapStateToProps = (state) => ({
  colors: state.currentConfig.extractors.reduce((obj, extractor) => {
    obj[extractor.name] = extractor.colors;
    return obj;
  }, {}),
  defaultColors: state.currentConfig.extractors.reduce((obj, extractor) => {
    return merge(obj, extractor.colors);
  }, {})
});

export default connect(mapStateToProps)(TaggedText);
