import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { otpVerification, resendOtp } from "../../services/authService";
import "./Login.css";

const OtpVerificationPage = () => {
    const [otp, setOtp] = useState("");
    const [resendCooldown, setResendCooldown] = useState(0);
    const [resendMessage, setResendMessage] = useState("");
    const [responseMessage, setResponseMessage] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const navigate = useNavigate();
    const location = useLocation();

    // Determine flow based on URL path or query param
    const isLoginFlow = location.pathname.includes("/login-otp");

    // Dynamic token key
    const tokenKey = isLoginFlow ? "temp-jwt-token" : "email-verification-jwt-token";

    const handleOtpSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage("");
        setResponseMessage("");

        try {
            const token = localStorage.getItem(tokenKey);
            const response = await otpVerification({ otp, token });

            if (response.jwtTokenAllocated) {
                localStorage.removeItem(tokenKey);

                if (isLoginFlow) {
                    localStorage.setItem("userId", response.message);
                    localStorage.setItem("token", response.jwt);
                    setResponseMessage("OTP Verified! Redirecting...");
                    navigate("/chat");
                } else {
                    localStorage.setItem("newpassword-jwt-token", response.jwt);
                    setResponseMessage("OTP Verified! Redirecting to password reset...");
                    navigate("/new-password");
                }
            } else {
                throw new Error(response.message || "Invalid OTP");
            }
        } catch (err) {
            setErrorMessage(err.message || "Invalid OTP!");
        }
    };

    const handleResendOtp = async () => {
        try {
            const token = localStorage.getItem(tokenKey);
            const response = await resendOtp({ token });
            setResendMessage(response);
            setResendCooldown(30);

            const interval = setInterval(() => {
                setResendCooldown((prev) => {
                    if (prev <= 1) {
                        clearInterval(interval);
                        return 0;
                    }
                    return prev - 1;
                });
            }, 1000);
        } catch {
            setResendMessage("");
            setErrorMessage("Failed to resend OTP. Please try again.");
        }
    };

    const handleBack = () => {
        navigate(isLoginFlow ? "/login" : "/forget-password");
    };

    return (
        <section className="otp-section d-flex justify-content-center align-items-center">
            <div className="otp-card container row shadow-lg rounded-4 overflow-hidden p-0 fade-in">
                {/* Illustration Side */}
                <div className="col-md-6 d-none d-md-flex flex-column justify-content-center align-items-center bg-light p-4">
                    <img
                        src="/undraw_video-call_013n.svg"
                        alt="OTP Illustration"
                        className="img-fluid"
                        style={{ maxHeight: "340px" }}
                    />
                    <h4 className="text-center mt-4 fw-semibold">Verify your Identity</h4>
                    <p className="text-muted text-center px-3">
                        Please enter the OTP sent to your registered email.
                    </p>
                </div>

                {/* OTP Form Side */}
                <div className="col-md-6 p-5 bg-white">
                    <h2 className="text-center fw-bold mb-4">OTP Verification</h2>

                    <form onSubmit={handleOtpSubmit}>
                        <div className="form-group">
                            <i className="fas fa-key icon-left"></i>
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Enter OTP"
                                value={otp}
                                onChange={(e) => setOtp(e.target.value)}
                                required
                            />
                        </div>

                        <button type="submit" className="btn btn-primary w-100 mb-2">
                            Verify OTP
                        </button>

                        <button
                            type="button"
                            className="btn btn-outline-secondary w-100 mb-2"
                            onClick={handleResendOtp}
                            disabled={resendCooldown > 0}
                        >
                            {resendCooldown > 0
                                ? `Resend OTP in ${resendCooldown}s`
                                : "Resend OTP"}
                        </button>

                        <button
                            type="button"
                            className="btn btn-link w-100 text-center"
                            onClick={handleBack}
                        >
                            Back
                        </button>

                        {responseMessage && (
                            <div className="alert alert-success mt-3 text-center">
                                {responseMessage}
                            </div>
                        )}

                        {resendMessage && (
                            <div className="alert alert-info mt-3 text-center">
                                {resendMessage}
                            </div>
                        )}

                        {errorMessage && (
                            <div className="alert alert-danger mt-3 text-center">
                                {errorMessage}
                            </div>
                        )}
                    </form>
                </div>
            </div>
        </section>
    );
};

export default OtpVerificationPage;
