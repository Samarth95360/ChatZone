import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { loginUser } from '../../services/authService';
import './Login.css';

const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const response = await loginUser({ email, password });
            if (response?.jwtTokenAllocated) {
                localStorage.setItem('temp-jwt-token', response.jwt);
                navigate('/login-otp');
            } else {
                alert('Login failed!');
            }
        } catch {
            alert('Login failed!');
        } finally {
            setLoading(false);
        }
    };

    return (
        <section className="login-section d-flex justify-content-center align-items-center">
            <div className="login-card container row shadow-lg rounded-4 overflow-hidden p-0">
                {/* Illustration */}
                <div className="col-md-6 d-none d-md-flex flex-column justify-content-center align-items-center bg-light p-4">
                    <img
                        src="/undraw_everywhere-together_c4di.svg"
                        alt="Login Illustration"
                        className="img-fluid"
                        style={{ maxHeight: '360px' }}
                    />
                    <h4 className="text-center mt-4 fw-semibold">Welcome Back!</h4>
                    <p className="text-muted text-center px-3">
                        Connect and chat with your friends anywhere, anytime.
                    </p>
                </div>

                {/* Login Form */}
                <div className="col-md-6 p-5 bg-white">
                    <h2 className="text-center fw-bold mb-4">Sign In</h2>

                    <form onSubmit={handleSubmit}>
                        {/* Email Field */}
                        <div className="form-group">
                            <i className="fas fa-envelope icon-left"></i>
                            <input
                                type="email"
                                className="form-control"
                                placeholder="Email Address"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>

                        {/* Password Field */}
                        <div className="form-group">
                            <i className="fas fa-lock icon-left"></i>
                            <input
                                type={showPassword ? 'text' : 'password'}
                                className="form-control"
                                placeholder="Password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                            <i
                                className={`fas ${showPassword ? 'fa-eye-slash' : 'fa-eye'} icon-toggle`}
                                onClick={() => setShowPassword(!showPassword)}
                                title={showPassword ? 'Hide Password' : 'Show Password'}
                            ></i>
                        </div>

                        {/* Remember + Forgot */}
                        <div className="d-flex justify-content-between align-items-center mb-3">
                            <div className="form-check">
                                <input className="form-check-input" type="checkbox" id="rememberMe" defaultChecked />
                                <label className="form-check-label small" htmlFor="rememberMe">
                                    Remember me
                                </label>
                            </div>
                            <Link to="/forgot-password" className="small text-primary">
                                Forgot Password?
                            </Link>
                        </div>

                        {/* Submit Button */}
                        <button type="submit" className="btn btn-primary w-100" disabled={loading}>
                            {loading ? 'Logging in...' : 'Login'}
                        </button>

                        {/* Register Redirect */}
                        <p className="text-center mt-3 small">
                            Don’t have an account?{' '}
                            <Link to="/register" className="link-primary">
                                Register
                            </Link>
                        </p>
                    </form>

                </div>
            </div>
        </section>
    );
};

export default LoginPage;
