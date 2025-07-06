import React from "react";

const TestimonialsSection = () => {
  return (
    <section id="testimonials" className="py-5 bg-white">
      <div className="container text-center">
        <h2 className="fw-bold mb-4">What Users Say</h2>
        <div className="row g-4">
          <div className="col-md-4">
            <blockquote className="blockquote">
              “Super fast, secure, and reliable. I use it daily!”
              <footer className="blockquote-footer mt-2">Jane Doe</footer>
            </blockquote>
          </div>
          <div className="col-md-4">
            <blockquote className="blockquote">
              “Best chat UI I've ever used. Highly recommend!”
              <footer className="blockquote-footer mt-2">John Smith</footer>
            </blockquote>
          </div>
          <div className="col-md-4">
            <blockquote className="blockquote">
              “Our team productivity skyrocketed with this app.”
              <footer className="blockquote-footer mt-2">Team Alpha</footer>
            </blockquote>
          </div>
        </div>
      </div>
    </section>
  );
};

export default TestimonialsSection;
