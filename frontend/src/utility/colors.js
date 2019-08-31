const black = '#202124';
const grey = '#cccccc';
const white = '#ffffff';

const color = (entity, config) => {
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
  const rgb = color && color.match(/#(..)(..)(..)/);
  if (rgb) {
    return (0.299 * parseInt(rgb[1], 16) + 0.587 * parseInt(rgb[2], 16) + 0.114 * parseInt(rgb[3], 16)) / 255;
  } else {
    return 0;
  }
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
  const backgrounds = entities.map(entity => color(entity, config));
  const luminance = darkest(backgrounds);
  return {
    color: Math.abs(blackLuminance - luminance) >= Math.abs(whiteLuminance - luminance)
      ? black : white,
    background: `linear-gradient(${backgrounds.map(gradient)})`,
    luminance: luminance
  };
};

export const getColor = (entity, config) => {
  const background = color(entity, config);
  const lum = luminance(background);
  return {
    color: Math.abs(blackLuminance - lum) >= Math.abs(whiteLuminance - lum)
      ? black : white,
    background: background,
    luminance: lum
  };
};
