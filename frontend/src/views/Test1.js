import React from 'react';
import FormMockup from '../components/FormMockup/FormMockup';

export const Test1 = function() {
    return (
        <div>
            <h2>Test 1</h2>
            <FormMockup action="/api/test/recognizer" type="text" />
        </div>
    );
}

export default Test1;