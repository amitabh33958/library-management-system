import { useOktaAuth } from '@okta/okta-react';
import { CardElement, useElements, useStripe } from '@stripe/react-stripe-js';
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import "./cartstyle.css"
import PaymentInfoRequest from '../../models/PaymentInfoRequest';
import { SpinnerLoading } from '../Utils/SpinnerLoading';
import { loadStripe } from '@stripe/stripe-js';
import CheckoutModel from '../../models/CheckoutModel';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faStripe, faPaypal } from '@fortawesome/free-brands-svg-icons';
import { faBook } from '@fortawesome/free-solid-svg-icons';

export const PaymentPage = () => {

    const { authState } = useOktaAuth();
    const [httpError, setHttpError] = useState(false);
    const [submitDisabled, setSubmitDisabled] = useState(false);
    const [checkoutWithDues, setCheckoutWithDues] = useState<CheckoutModel[]>([]);
    const [totalAmountDue, setTotalAmountDue] = useState(0);
    const [loading, setLoading] = useState(true);

    const [selectedGateway, setSelectedGateway] = useState('');
    const [paymentGatewayView, setPaymentGatewayView] = useState(false);

    useEffect(() => {
        const fetchCheckoutWithDues = async () => {
            if (authState && authState.isAuthenticated) {
                const url = `${process.env.REACT_APP_API}/checkout/secure/payment-due`;
                const requestOptions = {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${authState.accessToken?.accessToken}`,
                        'Content-Type': 'application/json'
                    }
                };
                const response = await fetch(url, requestOptions);
                if (!response.ok) {
                    throw new Error('Something went wrong!')
                }
                const checkoutResponseJson = await response.json();
                setCheckoutWithDues(checkoutResponseJson.checkoutDueList);
                setTotalAmountDue(checkoutResponseJson.totalDueAmount);
                setLoading(false);
            }
        }
        fetchCheckoutWithDues().catch((error: any) => {
            setLoading(false);
            setHttpError(error.message);
        })
    }, [authState]);

    // const elements = useElements();
    // const stripe = useStripe();

    const stripeCheckout = async () => {
        // if (!stripe || !elements || !elements.getElement(CardElement)) {
        //     return;
        // }

        const stripe = await loadStripe('pk_test_51NqWt8SDjGNjILye14A3Y9syMCwtvHBzdBr6nrleZt6pRrMcafwWNR43MuZI9mPtQKrvDWoflMS5Th3b9keUAa3F00XmXR9Qjr');

        setSubmitDisabled(true);

        // let paymentInfo = new PaymentInfoRequest(fees , 'INR', authState?.accessToken?.claims.sub);

        /*
        ################### Stripe Payment using custom Payment page ################
        */

        // const url = `https://localhost:8443/api/payment/secure/payment-intent`;
        // const requestOptions = {
        //     method: 'POST',
        //     headers: {
        //         Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
        //         'Content-Type': 'application/json'
        //     },
        //     body: JSON.stringify(paymentInfo)
        // };
        // const stripeResponse = await fetch(url, requestOptions);
        // if (!stripeResponse.ok) {
        //     setHttpError(true);
        //     setSubmitDisabled(false);
        //     throw new Error('Something went wrong!');
        // }
        // const stripeResponseJson = await stripeResponse.json();

        // Stripe Payment using custom Payment page
        // stripe.confirmCardPayment(
        //     stripeResponseJson.client_secret, {
        //         payment_method: {
        //             card: elements.getElement(CardElement)!,
        //             billing_details: {
        //                 email: authState?.accessToken?.claims.sub
        //             }
        //         }
        //     }, {handleActions: false}
        // ).then(async function (result: any) {
        //     if (result.error) {
        //         setSubmitDisabled(false)
        //         alert('There was an error in processing the payment. Please try again.')
        //     } else {
        //         const url = `https://localhost:8443/api/payment/secure/payment-complete`;
        //         const requestOptions = {
        //             method: 'PUT',
        //             headers: {
        //                 Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
        //                 'Content-Type': 'application/json'
        //             }
        //         };
        //         const stripeResponse = await fetch(url, requestOptions);
        //         if (!stripeResponse.ok) {
        //             setHttpError(true)
        //             setSubmitDisabled(false)
        //             throw new Error('Something went wrong!')
        //         }
        //         setFees(0);
        //         setSubmitDisabled(false);
        //     }
        // });


        /*
        ################### Stripe Payment using Stripe checkout page ################
        */

        const url = `https://localhost:8443/api/payment/secure/create-checkout-session`;
        const requestOptions = {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify('')
        };
        const stripeResponse = await fetch(url, requestOptions);
        if (!stripeResponse.ok) {
            setHttpError(true);
            setSubmitDisabled(false);
            throw new Error('Something went wrong!');
        }
        const session = await stripeResponse.json();
        console.log(session)
        if (!stripe) {
            return;
        }
        const result = stripe.redirectToCheckout({
            sessionId: session.id
        }).then(async function (result: any) {
            if (result.error) {
                setSubmitDisabled(false)
                alert('There was an error in processing the payment. Please try again.')
            } else {
                const url = `https://localhost:8443/api/payment/secure/payment-complete`;
                const requestOptions = {
                    method: 'PUT',
                    headers: {
                        Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
                        'Content-Type': 'application/json'
                    }
                };
                const stripeResponse = await fetch(url, requestOptions);
                if (!stripeResponse.ok) {
                    setHttpError(true)
                    setSubmitDisabled(false)
                    throw new Error('Something went wrong!')
                }
                setSubmitDisabled(false);
            }
        });
        setHttpError(false);
    }


    if (loading) {
        return (
            <SpinnerLoading />
        )
    }

    if (httpError) {
        return (
            <div className='container m-5'>
                <p>{httpError}</p>
            </div>
        )
    }

    const handlePayNowClick = () => {
        // Handle the Pay Now button click with the selected payment gateway
        if (selectedGateway === 'stripe') {
            // Handle Stripe payment
            stripeCheckout();
        } else if (selectedGateway === 'paypal') {
            // Handle Paypal payment
            alert('Processing payment with Paypal');
        } else {
            alert('Please select a payment gateway');
        }
    };

    const toggleCardVisibility = () => {
        setPaymentGatewayView(!paymentGatewayView);
    };


    // return(
    //     <div className='container'>
    //         {fees > 0 && <div className='card mt-3'>
    //             <h5 className='card-header'>Fees pending: <span className='text-danger'>₹{fees}</span></h5>
    //             <div className='card-body'>
    //                 <h5 className='card-title mb-3'>Credit Card</h5>
    //                 {/* <CardElement id='card-element' /> */}
    //                 <button disabled={submitDisabled} type='button' className='btn btn-md main-color text-white mt-3' 
    //                     onClick={checkout}>
    //                     Pay fees
    //                 </button>
    //             </div>
    //         </div>}

    //         {fees === 0 && 
    //             <div className='mt-3'>
    //                 <h5>You have no fees!</h5>
    //                 <Link type='button' className='btn main-color text-white' to='search'>
    //                     Explore top books
    //                 </Link>
    //             </div>
    //         }
    //         {submitDisabled && <SpinnerLoading/>}
    //     </div>
    // );

    return (
        <>
            <div className='container'>
                <div className="card mt-3 shadow p-3 mb-3 bg-body rounded">
                    <div className="card-header bg-dark ">
                        <div className='card-header-flex'>
                            <h5 className='text-white m-0'>Book Due Payment{checkoutWithDues.length > 0 ? `(${checkoutWithDues.length})` : ""}</h5>
                        </div>

                    </div>
                    <div className="card-body p-0">
                        {
                            checkoutWithDues.length === 0 ? <table className='table cart-table mb-0'>
                                <tbody>
                                    <tr>
                                        <td colSpan={6}>
                                            <div className='cart-empty'>
                                            <FontAwesomeIcon icon={faBook} size="2xl" style={{opacity: 0.4}} />
                                                <p className='mt-3'>No Payment Dues. Enjoy</p>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table> :
                                <table className='table cart-table mb-0 table-responsive-sm'>
                                    <thead>
                                        <tr>
                                            <th colSpan={2}>Book Details</th>
                                            <th>Book Checkout Date</th>
                                            <th>Book Return Date</th>
                                            <th>Days Past Due</th>
                                            <th className='text-right'> <span id="amount" className='amount'>Payment Amount</span></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {
                                            checkoutWithDues.map((data, index) => {
                                                return (
                                                    <>
                                                        <tr>
                                                            <td><div className='product-img'><img src={data.book.img} alt="" /></div></td>
                                                            <td><div className='product-name'><p>{data.book.title}</p></div></td>
                                                            <td>{data.checkoutDate}</td>
                                                            <td>{data.returnDate}</td>
                                                            <td>{data.daysPastDue}</td>
                                                            <td className='text-right'>₹ {data.dueAmount}</td>
                                                        </tr>
                                                    </>
                                                )
                                            })
                                        }
                                    </tbody>
                                    <tfoot>
                                        <tr>
                                            <th>&nbsp;</th>
                                            <th colSpan={2}>&nbsp;</th>
                                            <th>Total Books Due <span className='ml-2 mr-2'>:</span><span className='text-danger'>{checkoutWithDues.length}</span></th>
                                            <th className='text-right'>Total Amount Due<span className='ml-2 mr-2'>:</span><span className='text-danger'>₹ {totalAmountDue}</span></th>
                                            <th className='text-right'><button className='btn btn-success' type='button' onClick={toggleCardVisibility}>Pay Dues</button></th>
                                        </tr>
                                    </tfoot>
                                </table>
                        }
                    </div>
                </div>

                {paymentGatewayView &&
                    <div className="row justify-content-center">
                        <div className="col-md-6">
                            <div className="card mt-3 shadow p-1 mb-3 bg-body rounded">
                                <div className="card-header bg-dark ">
                                    <div className='card-header-flex'>
                                        <h5 className='text-white m-0'>Select Gateway for Payment</h5>
                                    </div>

                                </div>
                                <div className="card-body">
                                    <form>
                                        <div className="form-check form-check-inline">
                                            <input
                                                className="form-check-input p-2"
                                                type="radio"
                                                name="paymentGateway"
                                                id="stripe"
                                                value="stripe"
                                                checked={selectedGateway === 'stripe'}
                                                onChange={e => setSelectedGateway(e.target.value)}
                                            />
                                            <label className="form-check-label" htmlFor="stripe">
                                                Stripe
                                            </label>&nbsp;
                                            <span><FontAwesomeIcon icon={faStripe} className="mr-2 text-info" /></span>
                                        </div>
                                        <div className="form-check ">
                                            <input
                                                className="form-check-input p-2"
                                                type="radio"
                                                name="paymentGateway"
                                                id="paypal"
                                                value="paypal"
                                                checked={selectedGateway === 'paypal'}
                                                onChange={e => setSelectedGateway(e.target.value)}
                                            />
                                            <label className="form-check-label" htmlFor="paypal">
                                                Paypal
                                            </label>&nbsp;
                                            <span><FontAwesomeIcon icon={faPaypal} className="mr-2 text-info" /></span>
                                        </div><hr></hr>
                                        <div className="text-center mt-3">
                                            <button
                                                type="button"
                                                className="btn btn-primary"
                                                onClick={handlePayNowClick}
                                            >
                                                Pay Now
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                }




            </div>
        </>
    );

}