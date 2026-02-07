// Shared navbar functionality for FestiGo
(function () {
  // Get auth from localStorage
  function getAuth() {
    try {
      return JSON.parse(localStorage.getItem("auth"));
    } catch {
      return null;
    }
  }

  // Add search bar to navbar
  function addNavSearch() {
    const navContainer = document.querySelector(".nav-container");
    const navMenu = document.querySelector(".nav-menu");
    if (!navContainer || !navMenu) return;

    // Check if search already exists
    if (document.querySelector(".nav-search")) return;

    const searchDiv = document.createElement("div");
    searchDiv.className = "nav-search";
    searchDiv.innerHTML = `
      <input type="text" class="nav-search-input" id="navSearchInput" placeholder="Search events..." />
      <button class="nav-search-btn" id="navSearchBtn">🔍</button>
    `;

    // Insert into nav or navMenu depending on viewport to ensure mobile centering
    function placeSearch() {
      const mobile = window.innerWidth <= 768;
      if (searchDiv.parentElement)
        searchDiv.parentElement.removeChild(searchDiv);
      if (mobile) {
        // place inside the sliding mobile menu so it's centered and doesn't float to the right
        navMenu.insertBefore(searchDiv, navMenu.firstChild);
      } else {
        // desktop: keep search next to menu
        navContainer.insertBefore(searchDiv, navMenu);
      }
    }

    placeSearch();
    window.addEventListener("resize", placeSearch);

    // Add search functionality
    const searchInput = document.getElementById("navSearchInput");
    const searchBtn = document.getElementById("navSearchBtn");

    function performSearch() {
      const query = searchInput.value.trim();
      if (query) {
        // Redirect to events page with search query
        window.location.href = `/DBMS-mini-main/events.html?search=${encodeURIComponent(query)}`;
      }
    }

    searchBtn.addEventListener("click", performSearch);
    searchInput.addEventListener("keypress", function (e) {
      if (e.key === "Enter") performSearch();
    });
  }

  // Show/hide Create Event nav item based on role
  function setupNavbar() {
    const auth = getAuth();
    const createEventNav = document.getElementById("navCreateEvent");

    if (createEventNav) {
      // Show Create Event only for ORGANIZER and ADMIN
      if (auth && (auth.role === "ORGANIZER" || auth.role === "ADMIN")) {
        createEventNav.style.display = "block";
      } else {
        createEventNav.style.display = "none";
      }
    }

    // Setup logout handler
    const logoutLink = document.getElementById("logoutLink");
    if (logoutLink) {
      logoutLink.addEventListener("click", function (e) {
        e.preventDefault();
        localStorage.removeItem("auth");
        localStorage.removeItem("user");
        document.cookie =
          "userId=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        document.cookie =
          "role=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        document.cookie =
          "auth=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        window.location.href = "/DBMS-mini-main/login.html";
      });
    }

    // Setup hamburger menu for mobile
    const hamburger = document.getElementById("hamburger");
    const navMenu = document.querySelector(".nav-menu");
    if (hamburger && navMenu) {
      // Accessibility attributes
      hamburger.setAttribute("role", "button");
      hamburger.setAttribute("aria-label", "Toggle navigation");
      hamburger.setAttribute("aria-expanded", "false");

      const toggleNav = () => {
        navMenu.classList.toggle("active");
        const expanded = navMenu.classList.contains("active");
        hamburger.classList.toggle("active");
        hamburger.setAttribute("aria-expanded", expanded ? "true" : "false");
      };

      hamburger.addEventListener("click", toggleNav);
      // Add touch listener for mobile devices
      hamburger.addEventListener(
        "touchstart",
        function (e) {
          e.preventDefault();
          toggleNav();
        },
        { passive: false },
      );
    }

    // If user clicks Bookings while not logged in, redirect to login with next param
    const bookingsLink = document.querySelector('a[href$="bookings.html"]');
    if (bookingsLink) {
      bookingsLink.addEventListener("click", function (e) {
        const auth = getAuth();
        if (!auth || !auth.id) {
          e.preventDefault();
          const next = encodeURIComponent("/DBMS-mini-main/bookings.html");
          window.location.href = `/DBMS-mini-main/login.html?next=${next}`;
        }
      });
    }

    // Add search bar to navbar
    addNavSearch();
  }

  // Run when DOM is ready
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", setupNavbar);
  } else {
    setupNavbar();
  }
})();
