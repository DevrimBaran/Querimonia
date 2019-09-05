const black = '#202124';
const grey = '#cccccc';
const white = '#ffffff';

export class Color {
  constructor (color) {
    this.set(color);
    this.normalize();
    this.obj = {};
  }
  set = (color) => {
    this.r = 127;
    this.g = 127;
    this.b = 127;
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
    } else if (color.substr(0, 3) === 'rgb') {
      const rgb = color && color.match(/rgb\(\s*(\d*)\s*,\s*(\d*)\s*,\s*(\d*)\s*\)/);
      if (!rgb || rgb.lenght < 4) return;
      this.r = parseInt(rgb[1]);
      this.g = parseInt(rgb[2]);
      this.b = parseInt(rgb[3]);
    } else if (color.substr(0, 3) === 'hsl') {
      const hsl = color && color.match(/hsl\(\s*(\d*\.?\d*)\s*,\s*(\d*\.?\d*)%\s*,\s*(\d*\.?\d*)%\s*\)/);
      if (!hsl || hsl.lenght < 4) return;

      const h = hsl[1] / 360;
      const s = hsl[2] / 100;
      const l = hsl[3] / 100;
      if (s === 0) {
        this.r = this.g = this.b = l; // achromatic
      } else {
        const hue2rgb = (p, q, t) => {
          if (t < 0) t += 1;
          if (t > 1) t -= 1;
          if (t < 1 / 6) return p + (q - p) * 6 * t;
          if (t < 1 / 2) return q;
          if (t < 2 / 3) return p + (q - p) * (2 / 3 - t) * 6;
          return p;
        };

        var q = l < 0.5 ? l * (1 + s) : l + s - l * s;
        var p = 2 * l - q;

        this.r = 255 * hue2rgb(p, q, h + 1 / 3);
        this.g = 255 * hue2rgb(p, q, h);
        this.b = 255 * hue2rgb(p, q, h - 1 / 3);
      }
    }
  }
  normalize = () => {
    this.r = ~~this.r;
    this.g = ~~this.g;
    this.b = ~~this.b;
  }
  static predefinedColors = [
    'rgb(212, 230, 244)',
    'rgb(136, 188, 226)',
    'rgb(31, 130, 192)',
    'rgb(0, 90, 148)',
    'rgb(0, 52, 107)',
    'rgb(199, 193, 222)',
    'rgb(144, 133, 186)',
    'rgb(57, 55, 139)',
    'rgb(41, 40, 106)',
    'rgb(226, 0, 26)',
    'rgb(158, 28, 34)',
    'rgb(119, 28, 44)',
    'rgb(254, 234, 201)',
    'rgb(251, 203, 140)',
    'rgb(242, 148, 0)',
    'rgb(235, 106, 10)',
    'rgb(255, 250, 209)',
    'rgb(255, 243, 129)',
    'rgb(255, 220, 0)',
    'rgb(216, 166, 1)',
    'rgb(238, 239, 177)',
    'rgb(209, 221, 130)',
    'rgb(177, 200, 0)',
    'rgb(143, 164, 2)',
    'rgb(106, 115, 65)',
    'rgb(180, 220, 211)',
    'rgb(109, 191, 169)',
    'rgb(0, 148, 117)',
    'rgb(215, 225, 201)',
    'rgb(203, 175, 115)',
    'rgb(70, 41, 21)',
    'rgb(76, 99, 111)',
    'rgb(51, 184, 202)',
    'rgb(37, 186, 226)',
    'rgb(0, 110, 146)',
    'rgb(168, 175, 175)'
  ];

  background = () => {
    return '#' +
      (this.r < 16 ? '0' : '') + this.r.toString(16) +
      (this.g < 16 ? '0' : '') + this.g.toString(16) +
      (this.b < 16 ? '0' : '') + this.b.toString(16);
  }
  font = () => {
    const luminance = this.luminance();
    return Math.abs(blackLuminance - luminance) >= Math.abs(whiteLuminance - luminance)
      ? black : white;
  }
  luminance = () => {
    return (0.299 * this.r + 0.587 * this.g + 0.114 * this.b) / 255.0;
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
    this.obj = { h: h * 360, s: s * 100, l: l * 100 };
    this.obj.array = () => [this.obj.h, this.obj.s, this.obj.l];
    this.obj.css = (oh = null, os = null, ol = null) => {
      return 'hsl(' + ~~(oh !== null ? oh : this.obj.h) + ', ' + ~~(os !== null ? os : this.obj.s) + '%, ' + ~~(ol !== null ? ol : this.obj.l) + '%)';
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
