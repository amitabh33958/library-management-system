import { faCheck, faCircleCheck } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React from 'react';
import { Link } from 'react-router-dom';

export const PaymentSuccess = () => {
  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card mt-3 shadow p-1 mb-3 bg-body rounded">
            <div className="card-body text-center">
              <h2>Payment Successful</h2>
              <p>Your payment was successfully processed.</p>
              {/* Placeholder animation (you can replace this with your desired animation/library) */}
              <div className="success-animation">
              <FontAwesomeIcon icon={faCircleCheck} beat style={{"color": "#60b515", "fontSize": "70px"}}/><p></p>
            </div>
              <Link to="/payment" className="btn btn-primary mt-3">
                Go to Payment
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

