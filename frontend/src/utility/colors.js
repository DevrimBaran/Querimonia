const black = '#202124';
const grey = '#cccccc';
const white = '#ffffff';

export class Color {
  constructor (color) {
    if (color.substr(0, 1) === '#') {
      if (color.length === 4) {
        this.r = parseInt(color.substr(1, 1), 16) * 17;
        this.g = parseInt(color.substr(2, 1), 16) * 17;
        this.b = parseInt(color.substr(3, 1), 16) * 17;
      } else {
        this.r = parseInt(color.substr(1, 2), 16);
        this.g = parseInt(color.substr(3, 2), 16);
        this.b = parseInt(color.substr(5, 2), 16);
      }
    }
    this.obj = {};
  }
  css = () => {
    return '#' + this.r.toString(16) + this.g.toString(16) + this.b.toString(16);
  }
  luminance = () => {
    return (0.299 * this.r + 0.587 * this.g + 0.114 * this.b) / 255.0;
  }
  rbg = () => {
    this.obj = { r: this.r, g: this.g, b: this.b };
    this.obj.array = () => [this.r, this.g, this.b];
    this.obj.css = () => {
      return '#' + this.obj.r.toString(16) + this.obj.g.toString(16) + this.obj.b.toString(16);
    };
    return this.obj;
  }
  hsl = () => {
    let r = this.r / 255;
    let g = this.g / 255;
    let b = this.b / 255;

    let max = Math.max(r, g, b);
    let min = Math.min(r, g, b);

    let h;
    let s;
    let l = (max + min) / 2;

    if (max === min) {
      h = s = 0; // achromatic
    } else {
      var d = max - min;
      s = l > 0.5 ? d / (2 - max - min) : d / (max + min);

      switch (max) {
        case r: h = (g - b) / d + (g < b ? 6 : 0); break;
        case g: h = (b - r) / d + 2; break;
        case b: h = (r - g) / d + 4; break;
        default:
      }

      h /= 6;
    }
    this.obj = { h: h, s: s, l: l };
    this.obj.array = () => [h, s, l];
    this.obj.css = () => {
      return 'hsl(' + ~~(this.obj.h * 360) + ', ' + ~~(this.obj.s * 100) + '%, ' + ~~(this.obj.l * 100) + '%' + ')';
    };
    return this.obj;
  }
}

const extractColor = (entity, config) => {
  const extractor = config && config.extractors.find(extractor => extractor.name === entity.extractor);
  let color = grey;
  if (extractor) {
    color = extractor.color;
  } else if (entity.color) {
    color = entity.color;
  }
  return color;
};

const luminance = (color) => {
  return new Color(color).luminance();
};

const darkest = (colors) => {
  return colors.reduce((min, color) => Math.min(min, luminance(color)), 256);
};

const gradient = (color, i, colors) => {
  const pers = 100 / colors.length;
  return `${color} ${pers * i}%, ${color} ${pers * (i + 1)}%`;
};

const blackLuminance = luminance(black);
const whiteLuminance = luminance(white);

export const getGradient = (entities, config) => {
  const backgrounds = entities.map(entity => extractColor(entity, config));
  const luminance = darkest(backgrounds);
  return {
    color: Math.abs(blackLuminance - luminance) >= Math.abs(whiteLuminance - luminance)
      ? black : white,
    background: `linear-gradient(${backgrounds.map(gradient)})`,
    luminance: luminance
  };
};

export const getColor = (entity, config) => {
  const background = extractColor(entity, config);
  const lum = luminance(background);
  return {
    color: Math.abs(blackLuminance - lum) >= Math.abs(whiteLuminance - lum)
      ? black : white,
    background: background,
    luminance: lum
  };
};
