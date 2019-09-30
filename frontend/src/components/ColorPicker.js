import React, { Component } from 'react';
import ReactDOM from 'react-dom';
// import Color from '../utility/colors.js';
import Button from './Button.js';

import { Color } from '../utility/colors';

class ColorPicker extends Component {
  constructor (props) {
    super(props);
    this.input = React.createRef();
    this.colorpicker = React.createRef();
    this.svg = React.createRef();
    const hsl = new Color(this.props.value).hsl();
    this.state = {
      show: false,
      value: this.props.value,
      h: hsl.h,
      s: hsl.s,
      l: hsl.l
    };
  }

  show = () => {
    this.setState({ show: true });
  }

  hide = () => {
    this.setState({ show: false });
  }

  onValue = (value) => {
    this.onChange({
      target: this.input.current,
      value: value,
      name: this.props.name
    });
  }

  onSelect = (e) => {
    e.stopPropagation();
    this.hide();
    this.onValue(e.target.value);
  }

  onChange = (e) => {
    if (this.props.value === undefined) {
      this.setState({ value: e.value });
    }
    this.props.onChange && this.props.onChange(e);
  }

  xylToHsl = (x, y) => {
    const r = this.canvas.current.width / 2;
    const dx = x - r;
    const dy = y - r;
    const h = 0.5 + (Math.atan2(dy, dx) * 180 / Math.PI + 720) % 360;
    const s = 0.5 + Math.min(Math.sqrt(dx * dx + dy * dy), r) / r * 100;
    return new Color(`hsl(${~~h},${~~s}%,${~~this.state.l}%)`);
  }

  totalOffset = (target) => {
    let totalOffsetX = 0;
    let totalOffsetY = 0;
    let currentElement = target;
    while (currentElement) {
      totalOffsetX += currentElement.offsetLeft - currentElement.scrollLeft;
      totalOffsetY += currentElement.offsetTop - currentElement.scrollTop;
      currentElement = currentElement.offsetParent;
    }
    return { x: totalOffsetX, y: totalOffsetY };
  }

  canvasClick = (e) => {
    const offset = this.totalOffset(this.canvas.current);
    const color = this.xylToHsl(
      e.pageX - offset.x,
      e.pageY - offset.y
    );
    this.setState({ select: color });
  }

  clickPercentage = (e, scale) => {
    const offset = this.totalOffset(e.currentTarget);
    return (e.pageY - offset.y + this.colorpicker.current.clientHeight * 0.5) / e.currentTarget.clientHeight;
  }

  hueClick = (e) => {
    this.setState({ h: this.clickPercentage(e) * 360 });
  }
  saturationClick = (e) => {
    this.setState({ s: this.clickPercentage(e) * 100 });
  }
  lightnessClick = (e) => {
    this.setState({ l: this.clickPercentage(e) * 100 });
  }

  drawCanvas = () => {
    const canvas = this.canvas.current;
    const diameter = canvas.width;

    const ctx = canvas.getContext('2d');

    const data = ctx.getImageData(0, 0, diameter, diameter);

    let index = 0;
    for (let y = 0; y < diameter; y++) {
      for (let x = 0; x < diameter; x++) {
        const rgb = this.xylToHsl(x, y);
        data.data[index++] = rgb.r;
        data.data[index++] = rgb.g;
        data.data[index++] = rgb.b;
        data.data[index++] = 255;
      }
    }
    ctx.putImageData(data, 0, 0);
  }

  componentDidMount = () => {
  }

  componentDidUpdate = (oldProps) => {
    if (oldProps.value !== this.props.value) {
      const hsl = new Color(this.props.value).hsl();
      // eslint-disable-next-line react/no-did-update-set-state
      this.setState({ h: hsl.h, s: hsl.s, l: hsl.l });
    }
    // this.componentDidMount();
  }

  render () {
    let { value, style, onClick, onSelect, onChange, ...passThrough } = { ...this.props };
    const color = new Color(value || this.state.value);
    const selected = new Color(`hsl(${this.state.h},${this.state.s}%,${this.state.l}%)`);
    return (
      <React.Fragment>
        <input ref={this.input} style={{ color: color.font(), backgroundColor: color.background(), ...style }} readOnly type='colorpicker' value={value || this.state.value} onClick={this.show} onChange={this.onChange} {...passThrough} />
        {ReactDOM.createPortal((
          <div className='colorpicker' ref={this.colorpicker} style={{ display: this.state.show ? 'flex' : 'none' }}>
            <div className='input' style={{ width: Math.ceil(Math.sqrt(Color.predefinedColors.length)) * 42 + 'px', height: 'auto' }}>
              {Color.predefinedColors.map(c => {
                const color = new Color(c);
                return <input key={c} type='checkbox' checked={false} onChange={this.onSelect} name={c} value={color.background()} style={{ borderWidth: '0', backgroundColor: color.background(), color: color.font() }} />;
              })}
            </div>
            <div className='hslpicker'>
              <div className='fas' onClick={this.hueClick} style={{ '--perc': this.state.h / 360 }}>
                <svg width='100%' height='100%'>
                  <defs>
                    <linearGradient id='huegradient' x1='0.5' y1='0' x2='0.5' y2='1'>
                      {Array.from(Array(36), (v, i) => {
                        return <stop key={i} offset={`${i / 0.36}%`} stopColor={`hsl(${10 * i},${100}%,${50}%)`} />;
                      })}
                    </linearGradient>
                  </defs>
                  <rect x='0' y='0' width='100%' height='100%' fill='url(#huegradient)' />
                </svg>
              </div>
              <div className='fas' onClick={this.saturationClick} style={{ '--perc': this.state.s / 100 }}>
                <svg width='100%' height='100%'>
                  <defs>
                    <linearGradient id='saturationgradient' x1='0.5' y1='0' x2='0.5' y2='1'>
                      <stop offset='0%' stopColor={`hsl(${this.state.h},${0}%,${this.state.l}%)`} />
                      <stop offset='50%' stopColor={`hsl(${this.state.h},${50}%,${this.state.l}%)`} />
                      <stop offset='100%' stopColor={`hsl(${this.state.h},${100}%,${this.state.l}%)`} />
                    </linearGradient>
                  </defs>
                  <rect x='0' y='0' width='100%' height='100%' fill='url(#saturationgradient)' />
                </svg>
              </div>
              <div className='fas' onClick={this.lightnessClick} style={{ '--perc': this.state.l / 100 }}>
                <svg width='100%' height='100%'>
                  <defs>
                    <linearGradient id='lightnessgradient' x1='0.5' y1='0' x2='0.5' y2='1'>
                      <stop offset='0%' stopColor={`hsl(${this.state.h},${this.state.s}%,${0}%)`} />
                      <stop offset='50%' stopColor={`hsl(${this.state.h},${this.state.s}%,${50}%)`} />
                      <stop offset='100%' stopColor={`hsl(${this.state.h},${this.state.s}%,${100}%)`} />
                    </linearGradient>
                  </defs>
                  <rect x='0' y='0' width='100%' height='100%' fill='url(#lightnessgradient)' />
                </svg>
              </div>
              <br />
              <div className='input'>
                <input type='checkbox' checked={false} onChange={this.onSelect} value={selected.background()} style={{ borderWidth: '0', backgroundColor: selected.background(), color: selected.font() }} />
                <input type='checkbox' checked={false} onChange={this.onSelect} value={'#ffffff'} style={{ borderWidth: '0', backgroundColor: '#ffffff' }} />
                <input type='checkbox' checked={false} onChange={this.onSelect} value={'#000000'} style={{ borderWidth: '0', backgroundColor: '#000000', color: '#ffffff' }} />
              </div>
            </div>
            <Button icon='fas fa-times-circle fa-x' onClick={this.hide} />
          </div>
        ), document.body)}
      </React.Fragment>
    );
  }
}

export default ColorPicker;
