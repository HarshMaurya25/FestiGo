// Bundle: inject CSS and provide all frontend behavior in one file
(function () {
  // CSS (moved from index.html inline <style>)
  const css = `
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body, html {
  height: 100%;
  font-family: Arial, sans-serif;
}

body {
  display: flex;
  flex-direction: column;
}

.slider {
  height: 100vh;
  width: 100%;
  background-size: cover;
  background-position: center;
  transition: background-image 0.6s ease-in-out;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  position: relative;
  padding-left: 60px;
  flex: 1;
}

.overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
}

.content {
  position: relative;
  text-align: left;
  color: white;
  z-index: 1;
}

.content h1 {
  font-size: 60px;
  margin-bottom: 10px;
  background: linear-gradient(to right, #71b3f0, #f07eb9, #8346f3);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.content h2 {
  font-size: 35px;
  margin-bottom: 10px;
  color: #f0f0f0;
}

.content h3 {
  font-size: 18px;
  margin-bottom: 30px;
  color: #ddd;
}

.content button {
  width: 200px;
  padding: 12px;
  margin: 10px;
  font-size: 16px;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  transition: all 0.3s;
}

.signup {
  background: #4f46e5;
  color: white;
}

.signin {
  background: white;
  color: #111;
}

.content button:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

/* Modal Styles */
.modal {
  display: none;
  position: fixed;
  z-index: 1000;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  align-items: center;
  justify-content: center;
}

.modal-content {
  background-color: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  margin: auto;
  padding: 40px;
  border-radius: 15px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 5px 30px rgba(0, 0, 0, 0.3);
}

.close {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
  cursor: pointer;
}

.close:hover {
  color: #000;
}

.modal-content h2 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
}

.modal-content form {
  display: flex;
  flex-direction: column;
}

.modal-content input {
  padding: 12px;
  margin-bottom: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
}

.modal-content input:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 5px rgba(79, 70, 229, 0.3);
}

.modal-content button {
  padding: 12px;
  background: #4f46e5;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 10px;
}

.modal-content button:hover {
  background: #3f37d1;
}

.modal-content input.error-input {
  border-color: #dc2626 !important;
  background-color: #fef2f2;
}

.modal-content p {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.modal-content a {
  color: #4f46e5;
  text-decoration: none;
  cursor: pointer;
}

.modal-content a:hover {
  text-decoration: underline;
}

.site-footer {
  background: #0b0b0b;
  color: #f8fafc;
  padding: 1rem 0;
  margin-top: auto;
}

.site-footer .footer-content {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1.25rem;
  flex-wrap: wrap;
  font-weight: 600;
  font-size: 0.95rem;
}

.site-footer a {
  color: #f8fafc;
  text-decoration: none;
}

.site-footer a:hover {
  color: #e5e7eb;
  text-decoration: underline;
}

@media (max-width: 768px) {
  .slider {
    padding-left: 20px;
  }

  .content h1 {
    font-size: 40px;
  }

  .content h2 {
    font-size: 24px;
  }

  .content h3 {
    font-size: 16px;
  }
}
`;

  // Inject CSS
  const styleEl = document.createElement("style");
  styleEl.setAttribute("data-generated", "bundle-styles");
  styleEl.appendChild(document.createTextNode(css));
  document.head.appendChild(styleEl);

  // API base (set by /env/config.js loaded before this script)
  const API_BASE = (window.__ENV && window.__ENV.API_BASE_URL) || "";

  // Modal Functions (global)
  window.openSignUp = function () {
    const m = document.getElementById("signupModal");
    if (m) m.style.display = "flex";
  };
  window.closeSignUp = function () {
    const m = document.getElementById("signupModal");
    if (m) m.style.display = "none";
  };
  window.openSignIn = function () {
    const m = document.getElementById("signinModal");
    if (m) m.style.display = "flex";
  };
  window.closeSignIn = function () {
    const m = document.getElementById("signinModal");
    if (m) m.style.display = "none";
  };
  window.switchToSignIn = function () {
    closeSignUp();
    openSignIn();
  };
  window.switchToSignUp = function () {
    closeSignIn();
    openSignUp();
  };

  // Toast helper
  function showToast(message) {
    const toast = document.createElement("div");
    toast.textContent = message;
    toast.style.position = "fixed";
    toast.style.bottom = "24px";
    toast.style.right = "24px";
    toast.style.background = "#1f2937";
    toast.style.color = "#f9fafb";
    toast.style.padding = "10px 14px";
    toast.style.borderRadius = "8px";
    toast.style.boxShadow = "0 4px 12px rgba(0,0,0,0.3)";
    toast.style.zIndex = "9999";
    toast.style.fontSize = "14px";
    toast.style.opacity = "0";
    toast.style.transition = "opacity 0.2s ease";
    document.body.appendChild(toast);
    requestAnimationFrame(() => (toast.style.opacity = "1"));
    setTimeout(() => {
      toast.style.opacity = "0";
      setTimeout(() => toast.remove(), 200);
    }, 1500);
  }

  // Close modals when clicking outside
  window.addEventListener("click", function (event) {
    const signupModal = document.getElementById("signupModal");
    const signinModal = document.getElementById("signinModal");
    if (signupModal && event.target === signupModal) closeSignUp();
    if (signinModal && event.target === signinModal) closeSignIn();
  });

  // Field error helper
  function showFieldError(fieldId, message) {
    const field = document.getElementById(fieldId);
    if (field) {
      field.style.borderColor = "#dc2626";
      const error = document.createElement("div");
      error.className = "error-message";
      error.textContent = message;
      error.style.color = "#dc2626";
      error.style.fontSize = "12px";
      error.style.marginTop = "4px";
      error.style.marginBottom = "10px";
      field.parentNode.insertBefore(error, field.nextSibling);
    }
  }

  // Attach form handlers and slider after DOM is ready
  document.addEventListener("DOMContentLoaded", function () {
    // Signup
    const signupForm = document.getElementById("signupForm");
    if (signupForm) {
      signupForm.addEventListener("submit", async function (e) {
        e.preventDefault();
        document
          .querySelectorAll(".error-message")
          .forEach((el) => el.remove());

        const email = document.getElementById("signup-email").value.trim();
        const role =
          (document.getElementById("signup-role") &&
            document.getElementById("signup-role").value) ||
          "USER";
        const password = document.getElementById("signup-password").value;
        const confirmPassword =
          document.getElementById("confirm-password").value;

        let isValid = true;
        if (!email) {
          showFieldError("signup-email", "Email is required");
          isValid = false;
        }
        if (!password) {
          showFieldError("signup-password", "Password is required");
          isValid = false;
        }
        if (password.length > 0 && password.length < 6) {
          showFieldError(
            "signup-password",
            "Password must be at least 6 characters",
          );
          isValid = false;
        }
        if (password !== confirmPassword) {
          showFieldError("confirm-password", "Passwords do not match");
          isValid = false;
        }
        if (!isValid) return;

        try {
          const resp = await fetch(`${API_BASE}/users`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password, role }),
          });

          if (!resp.ok) {
            let msg = await resp.text().catch(() => resp.statusText);
            showToast(`Signup failed: ${msg}`);
            return;
          }

          const user = await resp.json().catch(() => null);
          showToast("Signed up successfully!");

          // store id and role for future
          if (user && user.id) {
            const auth = { id: user.id, role: user.role };
            localStorage.setItem("auth", JSON.stringify(auth));
            document.cookie = `userId=${user.id};path=/;max-age=${60 * 60 * 24 * 30}`;
            document.cookie = `role=${user.role};path=/;max-age=${60 * 60 * 24 * 30}`;
          }

          setTimeout(
            () => (window.location.href = "/DBMS-mini-main/profile.html"),
            700,
          );
        } catch (err) {
          showToast("Network error during signup");
          console.error("Signup error", err);
        }
      });
    }

    // Signin
    const signinForm = document.getElementById("signinForm");
    if (signinForm) {
      signinForm.addEventListener("submit", async function (e) {
        e.preventDefault();
        const email = document.getElementById("signin-email").value;
        const password = document.getElementById("signin-password").value;
        if (!email || !password) {
          showToast("Email and password required");
          return;
        }

        try {
          const resp = await fetch(`${API_BASE}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password }),
          });

          if (!resp.ok) {
            let body = await resp.text().catch(() => resp.statusText);
            showToast(`Login failed: ${body}`);
            return;
          }

          const user = await resp.json();
          localStorage.setItem("user", JSON.stringify(user));
          showToast("Signed in successfully");
          setTimeout(
            () => (window.location.href = "/DBMS-mini-main/home.html"),
            500,
          );
        } catch (err) {
          console.error("Login error", err);
          showToast("Network error during login");
        }
      });
    }

    // Slider background
    const images = [
      "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=1920&h=1080&fit=crop",
      "https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=1920&h=1080&fit=crop",
      "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=1920&h=1080&fit=crop",
      "https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?w=1920&h=1080&fit=crop",
    ];
    let currentIndex = 0;
    const slider = document.getElementById("slider");
    if (slider) {
      slider.style.backgroundImage = `url(${images[0]})`;
      function changeBackground() {
        currentIndex = (currentIndex + 1) % images.length;
        slider.style.backgroundImage = `url(${images[currentIndex]})`;
      }
      setInterval(changeBackground, 2000);
    }
  });
})();
