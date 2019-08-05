import React from 'react';

// the first smiley with a tendency greater than the value will be selected
const smilies = [
  {
    smiley: '🤬',
    tendency: -0.6
  },
  {
    smiley: '😞',
    tendency: -0.2
  },
  {
    smiley: '😐',
    tendency: 0.2
  },
  {
    smiley: '😊',
    tendency: 0.6
  },
  {
    smiley: '😁',
    tendency: 1
  }
];

export default function Sentiment (props) {
  const smiley = smilies.find(smiley => props.tendency < smiley.tendency).smiley;
  return (
    <span role='img' className='emotion'>
      {smiley} ({props.tendency.toFixed(props.fixed)})
    </span>
  );
}
