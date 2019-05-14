import React from 'react';
import FormMockup from '../components/FormMockup/FormMockup';

export const Test1 = function() {
    return (
        <div>
            <FormMockup action="http://localhost:3001/api">
                <textarea id="textArea1" placeholder='Bitte geben sie die Beschwerde ein oder laden eine Textdatei hoch.' style={{ resize: 'none', width: '400px', height: '100px' }} />
                <input id="fileArea1" type="file"></input>
            </FormMockup>
        </div>
    );
}

export default Test1;