import React from 'react';
import ReactDOM from 'react-dom';
import './assets/scss/style.scss';
import App from './App.js';
import { Provider } from 'react-redux';
import * as serviceWorker from './serviceWorker';
import store from './redux/store';

console.log('index.js', new URL(process.env.REACT_APP_BACKEND_PATH));

ReactDOM.render((
  <Provider store={store}>
    <App />
  </Provider>
), document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
