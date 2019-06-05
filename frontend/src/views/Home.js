import React, { Component } from 'react';

import Block from 'components/Block/Block';

function Home () {
  return (
    <React.Fragment>
      <Block>
        <h6 className='center'>Antwort</h6>
      </Block>
      <Block>
        <h6 className='center'>Meldetext</h6>
      </Block>
    </React.Fragment>
  );
}

export default Home;
