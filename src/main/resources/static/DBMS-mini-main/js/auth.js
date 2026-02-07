// Modal Functions
function openSignUp() {
  document.getElementById("signupModal").style.display = "flex";
}

function closeSignUp() {
  document.getElementById("signupModal").style.display = "none";
}

function openSignIn() {
  document.getElementById("signinModal").style.display = "flex";
}

function closeSignIn() {
  document.getElementById("signinModal").style.display = "none";
}

function switchToSignIn() {
  closeSignUp();
  openSignIn();
}

function switchToSignUp() {
  closeSignIn();
  openSignUp();
}

// Quick toast helper
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
  }, 1000);
}

window.onclick = function (event) {
  const signupModal = document.getElementById("signupModal");
  const signinModal = document.getElementById("signinModal");

  if (event.target == signupModal) {
    closeSignUp();
  }
  if (event.target == signinModal) {
    closeSignIn();
  }
};

const signupForm = document.getElementById("signupForm");
const API_BASE = (window.__ENV && window.__ENV.API_BASE_URL) || "/api";
if (signupForm) {
  signupForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    const fullname = document.getElementById("fullname").value.trim();
    const email = document.getElementById("signup-email").value.trim();
    const password = document.getElementById("signup-password").value;
    const confirmPassword = document.getElementById("confirm-password").value;

    // Clear previous errors
    document.querySelectorAll(".error-message").forEach((el) => el.remove());

    // Validation
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

    // Submit to backend (CreateUserRequest: { email, password, role(optional) })
    try {
      const resp = await fetch(`${API_BASE}/users`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      if (!resp.ok) {
        let msg = await resp.text().catch(() => resp.statusText);
        showToast(`Signup failed: ${msg}`);
        return;
      }

      const data = await resp.json().catch(() => null);
      showToast("Signed up successfully!");

      // Keep some minimal user info locally for UI
      localStorage.setItem("user", JSON.stringify({ email, fullname }));

      setTimeout(() => {
        window.location.href = "/DBMS-mini-main/home.html";
      }, 700);
    } catch (err) {
      showToast("Network error during signup");
      console.error("Signup error", err);
    }
  });
}

// Helper to show field-specific error
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

// Handle Sign In
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
      // Store auth info and full user
      if (user && user.id) {
        const auth = { id: user.id, role: user.role };
        localStorage.setItem("auth", JSON.stringify(auth));
        document.cookie = `userId=${user.id};path=/;max-age=${60 * 60 * 24 * 30}`;
        document.cookie = `role=${user.role};path=/;max-age=${60 * 60 * 24 * 30}`;
      }
      localStorage.setItem("user", JSON.stringify(user));
      showToast("Signed in successfully");

      setTimeout(() => {
        window.location.href = "/DBMS-mini-main/home.html";
      }, 500);
    } catch (err) {
      console.error("Login error", err);
      showToast("Network error during login");
    }
  });
}

// Background slider
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
