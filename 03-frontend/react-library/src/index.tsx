import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import { App } from './App';
import { BrowserRouter } from 'react-router-dom';
import { loadStripe } from '@stripe/stripe-js';
import { Elements } from '@stripe/react-stripe-js';

// const stripePromise = loadStripe('pk_test_51NqWt8SDjGNjILye14A3Y9syMCwtvHBzdBr6nrleZt6pRrMcafwWNR43MuZI9mPtQKrvDWoflMS5Th3b9keUAa3F00XmXR9Qjr');

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
  <BrowserRouter>
    {/* <Elements stripe={stripePromise}>
      <App />
    </Elements> */}
    <App />
  </BrowserRouter>
);
