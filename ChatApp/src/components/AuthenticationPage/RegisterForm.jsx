import { useState } from 'react';
import { registerUser } from '../../services/authService';
import { UserRegisterModel } from '../../models/UserRegisterModel';
import './RegisterForm.css';
import { Link } from 'react-router-dom';

const RegisterForm = () => {
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'USER',
  });

  const [message, setMessage] = useState('');
  const [statusColor, setStatusColor] = useState('');

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (formData.password !== formData.confirmPassword) {
      setMessage('Passwords do not match');
      setStatusColor('text-danger');
      return;
    }

    const user = new UserRegisterModel(
      formData.fullName,
      formData.email,
      formData.password,
      formData.role
    );

    try {
      const res = await registerUser(user);
      setMessage(res.message || 'Registered successfully');
      setStatusColor('text-success');
    } catch (err) {
      setMessage(err.message || 'Registration failed');
      setStatusColor('text-danger');
    }
  };

  return (
    <section className="register-section d-flex justify-content-center align-items-center">
      <div className="register-card container row shadow-lg rounded-4 overflow-hidden p-0">
        {/* Illustration Side */}
        <div className="col-md-6 p-4 bg-light d-none d-md-flex flex-column justify-content-center align-items-center">
          <img
            src="/undraw_chat-bot_44el.svg"
            alt="Chat Illustration"
            className="img-fluid"
            style={{ maxHeight: '350px' }}
          />
          <h4 className="text-center mt-4 fw-semibold">Welcome to ChatSphere!</h4>
          <p className="text-muted text-center px-3">
            Register now and start chatting with your friends in real-time.
          </p>
        </div>

        {/* Form Side */}
        <div className="col-md-6 p-5 bg-white">
          <h2 className="text-center fw-bold mb-4">Create Your Account</h2>

          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <input
                type="text"
                name="fullName"
                className="form-control"
                placeholder="Full Name"
                value={formData.fullName}
                onChange={handleChange}
                required
              />
            </div>

            <div className="mb-3">
              <input
                type="email"
                name="email"
                className="form-control"
                placeholder="Email Address"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>

            <div className="mb-3">
              <input
                type="password"
                name="password"
                className="form-control"
                placeholder="Password"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </div>

            <div className="mb-3">
              <input
                type="password"
                name="confirmPassword"
                className="form-control"
                placeholder="Confirm Password"
                value={formData.confirmPassword}
                onChange={handleChange}
                required
              />
            </div>

            <div className="mb-3">
              <select
                name="role"
                className="form-select"
                value={formData.role}
                onChange={handleChange}
              >
                <option value="USER">User</option>
                <option value="ADMIN">Admin</option>
              </select>
            </div>

            <button type="submit" className="btn btn-primary w-100 mb-2">
              Register
            </button>

            {message && (
              <p className={`text-center mt-2 ${statusColor}`}>{message}</p>
            )}

            <p className="text-center mt-3 small">
              Already have an account?{' '}
              <Link to="/login" className="link-primary">
                Log In
              </Link>
            </p>
          </form>
        </div>
      </div>
    </section>
  );
};

export default RegisterForm;
