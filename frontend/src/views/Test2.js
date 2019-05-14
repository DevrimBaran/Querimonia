import React from 'react';
import FormMockup from '../components/FormMockup/FormMockup';

export const Test2 = function() {
    return (
        <div>
            <h2>Test 2</h2>
            <FormMockup action="/api/test/textominado" type="text" />
        </div>
    );
}

export default Test2;