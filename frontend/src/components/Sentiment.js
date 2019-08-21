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
  if (!props.tendency) {
    return (
      <span role='img' className='emotion'>
        ---
      </span>
    );
  }
  const smiley = smilies.find(smiley => props.tendency < smiley.tendency).smiley;
  if (!props.fixed) {
    return (
      <span style={{ fontSize: '150%' }} role='img' className='emotion'>
        {smiley}
      </span>
    );
  }
  return (
    <span role='img' className='emotion'>
      <span style={{ fontSize: '120%' }} role='img' className='emotion'>
        {smiley}
      </span>
      <span role='img' className='emotion'>
        ({props.tendency.toFixed(props.fixed)})
      </span>
    </span>
  );
}
