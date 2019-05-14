import React from 'react';
import FormMockup from '../components/FormMockup/FormMockup';

export const Test1 = function() {
    return (
        <div>
            <h2>Test 1</h2>
            <FormMockup action="/api/test/recognizer" enctype="application/json" method="post">
                <textarea name="text" placeholder='Bitte geben sie die Beschwerde ein.' style={{ resize: 'none', width: '400px', height: '100px' }} />
            </FormMockup>
        </div>
    );
}

export default Test1;