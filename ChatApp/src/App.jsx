import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

import LoginPage from './components/AuthenticationPage/LoginPage';
import ForgetPassword from './components/AuthenticationPage/ForgetPassword';
import OtpVerificationPage from './components/AuthenticationPage/OtpVerificationPage';
import RegisterForm from './components/AuthenticationPage/RegisterForm';
import HomePage from './components/HomePage/HomePage';
import NewPassword from './components/AuthenticationPage/NewPassword';
import ChatMain from './components/Chat/ChatMain';

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/forgot-password" element={<ForgetPassword />} />
        <Route path="/register" element={<RegisterForm />} />
        <Route path="/login-otp" element={<OtpVerificationPage />} />
        <Route path="/forget-otp" element={<OtpVerificationPage />} />
        <Route path="/new-password" element={<NewPassword />} />
        <Route path="/chat" element={<ChatMain />} />
        {/* Add other routes here */}
      </Routes>
    </Router>
  );
};

export default App;
