import React from 'react';

import Block from 'components/Block/Block';

function WordVectors () {
  return (
    <React.Fragment>
      <Block>
        <h6 className='center'>Wortvektoren</h6>
        <br />
        <h6>Nearest Neighbor</h6>
        <form action="wordVectors">
          <label>Befehl:
            <select name="commands">
              <option>nn</option>
              <option>analogy</option>
            </select>
          </label>
          <label>Textkorpora:
            <select name="textkorpora">
              <option>beschwerden3kPolished.bin</option>
              <option>cc.de.300.bin</option>
              <option>ngram_ger.bin</option>
              <option>BeschwerdenCATLeipzig.bin</option>
              <option>leipzigCorporaCollection1M.bin</option>
            </select>
          </label>
          <p>Worteingabe:</p>
          <input type='text' id='text' />
          <input type='button' name='berechneButton' value='Berechnen' />
          <br />
          <br />
          <h6>Analogie</h6>
          <input type='text' id='text' />
          <label>
            <select name="berechnung">
              <option>+</option>
              <option>-</option>
            </select>
          </label>
          <input type='text' id='text' />
          <label>
            <select name="berechnung">
              <option>+</option>
              <option>-</option>
            </select>
          </label>
          <input type='text' id='text' />
          <input type='button' name='berechneButton' value='Berechnen' />
        </form>
      </Block>
    </React.Fragment>
  );
}

export default WordVectors;
