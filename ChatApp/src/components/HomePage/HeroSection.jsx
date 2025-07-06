import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import HeroSVG from "../../assets/undraw_chatting.svg";
import "./HomePage.css";
import { createUserProfiles } from "../../services/chatServices";
import { useState } from "react";

const HeroSection = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const handleCreateProfiles = async () => {
    try {
      setLoading(true);
      await createUserProfiles();
      alert("User profiles created successfully!");
    } catch (error) {
      console.error("Failed to create user profiles:", error);
      alert("Something went wrong while creating user profiles.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="hero-section py-5">
      <div className="container">
        <div className="row align-items-center g-4">
          <div className="col-md-6 text-center text-md-start">
            <motion.h1
              className="display-4 fw-bold text-gradient"
              initial={{ y: -50, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              transition={{ duration: 0.8 }}
            >
              Welcome to ChatZone
            </motion.h1>
            <p className="lead text-muted mb-4">
              Connect, Chat, and Video Call securely with your friends and team.
            </p>
            <div className="d-flex flex-wrap gap-3">
              <button className="btn btn-primary px-4" onClick={() => navigate("/register")}>
                Register
              </button>
              <button className="btn btn-outline-primary px-4" onClick={() => navigate("/login")}>
                Login
              </button>
              <button
                className="btn btn-outline-secondary px-4"
                onClick={handleCreateProfiles}
                disabled={loading}
              >
                {loading ? "Creating..." : "Create User Profiles"}
              </button>
            </div>
            <a href="#features" className="btn btn-link mt-4 text-decoration-none">
              Explore Features ↓
            </a>
          </div>

          <div className="col-md-6 text-center">
            <motion.img
              src={HeroSVG}
              alt="Chat Illustration"
              className="img-fluid hero-image"
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              transition={{ duration: 1 }}
            />
          </div>
        </div>
      </div>
    </section>
  );
};

export default HeroSection;
