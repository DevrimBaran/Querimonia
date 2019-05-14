import React from 'react';
import FormMockup from '../components/FormMockup/FormMockup';

export const Test2 = function() {
    return (
        <div>
            <h2>Test 2</h2>
            <FormMockup action="/api/test/textominado" enctype="application/json" method="post">
                <textarea name="text" placeholder='Bitte geben sie die Beschwerde ein.' style={{ resize: 'none', width: '400px', height: '100px' }} />
            </FormMockup>
        </div>
    );
}

export default Test2;