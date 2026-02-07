/* Main JavaScript - Common Functions */

// Background images for hero section
const heroImages = [
  "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=1920&h=1080&fit=crop",
  "https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=1920&h=1080&fit=crop",
  "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=1920&h=1080&fit=crop",
  "https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?w=1920&h=1080&fit=crop",
];

let heroImageIndex = 0;

// Set up hero section background image slider
function setupHeroImageSlider() {
  const heroSection = document.getElementById("heroSection");
  if (heroSection) {
    heroSection.style.backgroundImage = `url(${heroImages[0]})`;

    setInterval(() => {
      heroImageIndex = (heroImageIndex + 1) % heroImages.length;
      heroSection.style.backgroundImage = `url(${heroImages[heroImageIndex]})`;
    }, 4000);
  }
}

// Build marquee items from heroImages and duplicate for seamless scroll
function setupMarqueeFromHeroImages() {
  const track = document.querySelector(".marquee-track");
  const dotsContainer = document.querySelector(".marquee-dots");
  if (!track) return;

  // Helper to resize Unsplash URLs to small tiles
  const toTileSize = (url) =>
    url.replace(/w=\d+/i, "w=1000").replace(/h=\d+/i, "h=700");

  // Clear and build first sequence
  track.innerHTML = "";
  if (dotsContainer) dotsContainer.innerHTML = "";

  heroImages.forEach((url, i) => {
    const item = document.createElement("div");
    item.className = "marquee-item";
    item.dataset.index = i;

    const img = document.createElement("img");
    img.src = toTileSize(url);
    img.alt = `Background ${i + 1}`;
    item.appendChild(img);
    track.appendChild(item);

    // Create dot for each image
    if (dotsContainer) {
      const dot = document.createElement("span");
      dot.className = "marquee-dot";
      if (i === 0) dot.classList.add("active");
      dot.dataset.index = i;
      dotsContainer.appendChild(dot);
    }
  });

  // Duplicate sequence for seamless loop
  const cloneHTML = track.innerHTML;
  track.insertAdjacentHTML("beforeend", cloneHTML);

  // Pause animation on hover
  const items = track.querySelectorAll(".marquee-item");
  items.forEach((item) => {
    item.addEventListener("mouseenter", () => {
      track.style.animationPlayState = "paused";
    });
    item.addEventListener("mouseleave", () => {
      track.style.animationPlayState = "running";
    });
  });

  // Update active dot based on scroll position
  if (dotsContainer) {
    const updateActiveDot = () => {
      const containerWidth = track.parentElement.offsetWidth;
      const scrollLeft = Math.abs(
        parseFloat(getComputedStyle(track).transform.split(",")[4] || 0),
      );
      const itemWidth = 320 + 16; // width + gap
      const currentIndex = Math.floor(
        (scrollLeft % (itemWidth * heroImages.length)) / itemWidth,
      );

      document.querySelectorAll(".marquee-dot").forEach((dot, i) => {
        dot.classList.toggle("active", i === currentIndex);
      });
    };

    // Update dots periodically
    setInterval(updateActiveDot, 1000);
  }
}

// Check if user is logged in
window.addEventListener("load", function () {
  const user = localStorage.getItem("user");
  const currentPage = window.location.pathname.split("/").pop();

  // Protected pages - redirect to index if not logged in
  const protectedPages = [
    "home.html",
    "events.html",
    "bookings.html",
    "create-event.html",
    "profile.html",
  ];

  if (protectedPages.includes(currentPage) && !user) {
    window.location.href = "/DBMS-mini-main/index.html";
  }

  // If logged in and on index, redirect to home
  if (currentPage === "index.html" && user) {
    window.location.href = "/DBMS-mini-main/home.html";
  }

  // Display welcome message
  if (user && document.getElementById("welcomeMsg")) {
    const userData = JSON.parse(user);
    const userName = userData.fullname || userData.email.split("@")[0];
    document.getElementById("welcomeMsg").textContent =
      `Welcome back, ${userName}!`;
  }

  // Setup page transitions
  initPageTransitions();
  setupLogoNavigation();

  // Setup hero image slider
  setupHeroImageSlider();

  // Setup marquee from heroImages
  setupMarqueeFromHeroImages();
});

// Page transition functionality
function initPageTransitions() {
  ensureTransitionLogo();

  // Add fade-out animation to all navigation links
  document.querySelectorAll("a").forEach((link) => {
    link.addEventListener("click", function (e) {
      // Don't prevent default for external links or non-page navigation
      const href = this.getAttribute("href");

      // Check if it's an internal page link (not anchor, not external)
      if (
        href &&
        !href.startsWith("#") &&
        !href.startsWith("http") &&
        !href.startsWith("mailto")
      ) {
        e.preventDefault();

        // Add fade-out effect
        document.body.classList.add("fade-out");

        // Navigate after animation completes
        setTimeout(() => {
          // If href is a root-relative page, convert to DBMS-mini-main absolute path
          const finalHref = /^\/?[\w-]+\.html$/.test(href)
            ? "/DBMS-mini-main/" + href.replace(/^\//, "")
            : href;
          window.location.href = finalHref;
        }, 600);
      }
    });
  });
}

function ensureTransitionLogo() {
  if (document.querySelector(".page-transition-logo")) return;

  const logoText =
    document.querySelector(".logo")?.textContent?.trim() || "EventHub";
  const logo = document.createElement("div");
  logo.className = "page-transition-logo";
  logo.textContent = logoText;
  document.body.appendChild(logo);
}

// Logo click navigation
function setupLogoNavigation() {
  const logo = document.querySelector(".logo");
  if (logo) {
    logo.style.cursor = "pointer";
    logo.addEventListener("click", function () {
      document.body.classList.add("fade-out");
      setTimeout(() => {
        window.location.href = "/DBMS-mini-main/home.html";
      }, 600);
    });
  }
}

// Logout button handler
const logoutBtn = document.getElementById("logout");
if (logoutBtn) {
  logoutBtn.addEventListener("click", function (e) {
    e.preventDefault();
    if (confirm("Are you sure you want to log out?")) {
      localStorage.removeItem("user");
      window.location.href = "/DBMS-mini-main/index.html";
    }
  });
}

// Hamburger menu toggle
const hamburger = document.getElementById("hamburger");
const navMenu = document.querySelector(".nav-menu");

if (hamburger && navMenu) {
  hamburger.addEventListener("click", function () {
    navMenu.classList.toggle("active");
  });

  // Close menu when clicking on a link
  document.querySelectorAll(".nav-menu a").forEach((link) => {
    link.addEventListener("click", function () {
      navMenu.classList.remove("active");
    });
  });
}

// Hide navbar on scroll down, show on scroll up
let lastScrollTop = 0;
const navbar = document.querySelector(".navbar");

if (navbar) {
  window.addEventListener("scroll", function () {
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop;

    if (scrollTop > lastScrollTop && scrollTop > 100) {
      // Scroll DOWN - hide navbar
      navbar.classList.add("hidden");
    } else {
      // Scroll UP - show navbar
      navbar.classList.remove("hidden");
    }

    lastScrollTop = scrollTop <= 0 ? 0 : scrollTop;
  });
}
