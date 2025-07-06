import axios from 'axios';

// const API_URL = 'http://localhost:8025/auth/register';
const BASE_URL = "http://localhost:8025/auth";
const API_BASE_URL = "http://localhost:8000/auth";

export const registerUser = async (userData) => {
  try {
    const response = await axios.post(`${BASE_URL}/register`, userData);
    return response.data;
  } catch (error) {
    throw error.response?.data || { message: 'Registration failed' };
  }
};

export const loginUser = async ({ email, password }) => {
  const response = await axios.post(`${BASE_URL}/login`, {
    email,
    password
  });
  return response.data;
};

export const otpVerification = async ({ otp, token }) => {
  const response = await axios.post(
    `${BASE_URL}/otp-verification`,
    { otp },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

export const forgetPassword = async ({ email }) => {
  const response = await axios.post(`${BASE_URL}/forget-password`, { email });
  return response.data;
}

export const newPasswordVerification = async ({ password, confirmPassword, token }) => {
  const response = await axios.post(
    `${BASE_URL}/new-password`,
    { password, confirmPassword },
    {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      validateStatus: () => true
    }
  );
  return response;
};

export const resendOtp = async ({ token }) => {
  const response = await axios.get(`${BASE_URL}/resend-otp`, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  });
  return response.data;
};