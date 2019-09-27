import React from 'react';

// the first smiley with a tendency greater than the value will be selected
const smilies = [
  {
    smiley: '🤬',
    tendency: -0.8
  },
  {
    smiley: '😠',
    tendency: -0.6
  },
  {
    smiley: '☹️',
    tendency: -0.4
  },
  {
    smiley: '😞',
    tendency: -0.2
  },
  {
    smiley: '😐',
    tendency: 0.1
  },
  {
    smiley: '🙂',
    tendency: 0.4
  },
  {
    smiley: '😀',
    tendency: 0.6
  },
  {
    smiley: '😁',
    tendency: 0.8
  },
  {
    smiley: '😍',
    tendency: 1
  }
];

export default function Sentiment (props) {
  let { small, tendency, className = '', ...passThrough } = { ...props };
  className += ' emotion';
  if (tendency === null) {
    return (
      <span role='img' className={className} {...passThrough}>
        ---
      </span>
    );
  }
  const smiley = smilies.find(smiley => tendency <= smiley.tendency);
  if (!small) {
    return (
      <span style={{ fontSize: '150%' }} role='img' title={tendency} className={className} {...passThrough}>
        {smiley && smiley.smiley}
      </span>
    );
  }
  return (
    <span role='img' className={className}>
      <span style={{ fontSize: '120%' }} role='img' className={className}>
        {smiley && smiley.smiley}
      </span>
    </span>
  );
}
