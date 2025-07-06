import React, { useState } from 'react';
import { forgetPassword } from '../../services/authService';
import { useNavigate } from 'react-router-dom';
import "./Login.css"; // Reuse login styling

const ForgetPassword = () => {
    const [email, setEmail] = useState('');
    const [response, setResponse] = useState(null);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleForgetPassword = async (e) => {
        e.preventDefault();
        try {
            const res = await forgetPassword({ email });
            if (res?.jwtTokenAllocated) {
                localStorage.setItem("email-verification-jwt-token", res.jwt);
                navigate('/forget-otp');
            } else {
                setResponse(res.message);
                setError(null);
            }
        } catch (err) {
            const message = err.response?.data?.message || 'Something went wrong';
            setError(message);
            setResponse(null);
        }
    };

    return (
        <section className="otp-section d-flex justify-content-center align-items-center">
            <div className="otp-card container row shadow-lg rounded-4 overflow-hidden p-0 fade-in">
                {/* Left Illustration */}
                <div className="col-md-6 d-none d-md-flex flex-column justify-content-center align-items-center bg-light p-4">
                    <img
                        src="/undraw_group_chat.svg"
                        alt="Group Chat Illustration"
                        className="img-fluid"
                        style={{ maxHeight: '340px' }}
                    />
                    <h4 className="text-center mt-4 fw-semibold">Reset Your Password</h4>
                    <p className="text-muted text-center px-3">
                        We'll send you an OTP to help you recover access to your account.
                    </p>
                </div>

                {/* Right Form */}
                <div className="col-md-6 p-5 bg-white">
                    <h2 className="text-center fw-bold mb-4">Forgot Password</h2>
                    <p className="text-muted text-center mb-4">Enter your registered email address.</p>

                    <form onSubmit={handleForgetPassword}>
                        <div className="form-group mb-4">
                            <i className="fas fa-envelope icon-left"></i>
                            <input
                                type="email"
                                id="email"
                                className="form-control"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="yourname@example.com"
                                required
                            />
                        </div>
                        <button type="submit" className="btn btn-primary w-100 mb-2">
                            Send OTP
                        </button>
                    </form>

                    {response && <div className="alert alert-success mt-3">{response}</div>}
                    {error && <div className="alert alert-danger mt-3">{error}</div>}
                </div>
            </div>
        </section>
    );
};

export default ForgetPassword;
