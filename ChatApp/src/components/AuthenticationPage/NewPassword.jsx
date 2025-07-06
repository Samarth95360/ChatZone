import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { newPasswordVerification } from '../../services/authService';

const NewPassword = () => {
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [response, setResponse] = useState(null);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (password !== confirmPassword) {
            setError("Passwords do not match");
            setResponse(null);
            return;
        }

        try {
            const token = localStorage.getItem("newpassword-jwt-token");
            const res = await newPasswordVerification({ password, confirmPassword, token });

            if (res.status === 200) {
                setResponse(res.data);
                setError(null);
                setTimeout(() => {
                    localStorage.removeItem("newpassword-jwt-token");
                    localStorage.removeItem("email-verification-jwt-token");
                    navigate("/login");
                }, 2000);
            } else {
                setError(res.data || 'Something went wrong');
                setResponse(null);
            }
        } catch {
            setError('Something went wrong. Please try again.');
            setResponse(null);
        }
    };

    return (
        <section className="otp-section d-flex justify-content-center align-items-center">
            <div className="otp-card container row shadow-lg rounded-4 overflow-hidden p-0 fade-in">
                {/* Illustration Side */}
                <div className="col-md-6 d-none d-md-flex flex-column justify-content-center align-items-center bg-light p-4">
                    <img
                        src="/undraw_business-chat_xea1.svg"
                        alt="Password Reset"
                        className="img-fluid"
                        style={{ maxHeight: '340px' }}
                    />
                    <h4 className="text-center mt-4 fw-semibold">Reset Your Password</h4>
                    <p className="text-muted text-center px-3">
                        Create a secure password to access your account again.
                    </p>
                </div>

                {/* Form Side */}
                <div className="col-md-6 p-5 bg-white">
                    <h2 className="text-center fw-bold mb-4">Set New Password</h2>
                    <form onSubmit={handleSubmit}>
                        <div className="form-group mb-3">
                            <i className="fas fa-lock icon-left"></i>
                            <input
                                type="password"
                                className="form-control"
                                placeholder="New Password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>

                        <div className="form-group mb-3">
                            <i className="fas fa-lock icon-left"></i>
                            <input
                                type="password"
                                className="form-control"
                                placeholder="Confirm Password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                required
                            />
                        </div>

                        <button type="submit" className="btn btn-primary w-100">
                            Change Password
                        </button>
                    </form>

                    {response && <div className="alert alert-success mt-3 text-center">{response}</div>}
                    {error && <div className="alert alert-danger mt-3 text-center">{error}</div>}
                </div>
            </div>
        </section>
    );
};

export default NewPassword;
