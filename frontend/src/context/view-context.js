import React from 'react';

export const ViewContext = React.createContext(
    {
        changeView: (name) => {}
    }
);