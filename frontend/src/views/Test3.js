import React from 'react';
import FormMockup from '../components/FormMockup/FormMockup';

export const Test3 = function() {
    return (
        <div>
            <h2>Test 3</h2>
            <FormMockup action="/api/test/textominado-batch" type="file" />
        </div>
    );
}

export default Test3;