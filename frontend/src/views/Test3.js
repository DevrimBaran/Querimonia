import React from 'react';
import FormMockup from '../components/FormMockup/FormMockup';

export const Test3 = function() {
    return (
        <div>
            <h2>Test 3</h2>
            <FormMockup action="/api/test/textominado-batch" enctype="multipart/form-data" method="post">
                <input type="file" name="file"></input>
            </FormMockup>
        </div>
    );
}

export default Test3;