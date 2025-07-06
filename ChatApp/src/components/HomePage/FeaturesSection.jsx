import React from "react";
import { FaUsers, FaVideo, FaLock } from "react-icons/fa";
import "./HomePage.css";

const FeaturesSection = () => {
  return (
    <section id="features" className="features-section py-5 bg-light">
      <div className="container text-center">
        <h2 className="fw-bold mb-5">Features</h2>
        <div className="row g-5">
          <div className="col-md-4">
            <img
              src="/undraw_group-chat_4xw0.svg"
              alt="Group Chat"
              className="feature-img mb-3"
              style={{ maxHeight: "150px" }}
            />
            <h5>Group Messaging</h5>
            <p className="text-muted">Chat in organized groups with your friends or team members.</p>
          </div>
          <div className="col-md-4">
            <FaVideo className="feature-icon text-primary mb-3" size={48} />
            <h5>Video Calling</h5>
            <p className="text-muted">Crystal-clear video calls with anyone in your contacts.</p>
          </div>
          <div className="col-md-4">
            <FaLock className="feature-icon text-primary mb-3" size={48} />
            <h5>End-to-End Encryption</h5>
            <p className="text-muted">All messages are secured and private between you and others.</p>
          </div>
        </div>
      </div>
    </section>
  );
};

export default FeaturesSection;
