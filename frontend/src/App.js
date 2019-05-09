import React from 'react';
import logo from './logo.svg';
import FormMockup from './components/formMockup/FormMockup'
import './App.css';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <FormMockup action="http://localhost:3001/api" />
      </header>
    </div>
  );
}

export default App;
