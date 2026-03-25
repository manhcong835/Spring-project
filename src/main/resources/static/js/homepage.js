/* ============================================
   HOMEPAGE INTERACTIONS
   Premium Travel Booking — travel.com.vn style
   ============================================ */

document.addEventListener('DOMContentLoaded', () => {
  initStickyHeader();
  initMobileMenu();
  initScrollReveal();
  initSmoothScroll();
  initSearchTabs();
  initWishlistButtons();
  initCountUp();
});

/* ----- Sticky Header Shadow ----- */
function initStickyHeader() {
  const header = document.querySelector('.header');
  if (!header) return;
  window.addEventListener('scroll', () => {
    header.classList.toggle('scrolled', window.pageYOffset > 20);
  }, { passive: true });
}

/* ----- Mobile Menu ----- */
function initMobileMenu() {
  const btn = document.querySelector('.hamburger');
  const nav = document.querySelector('.mobile-nav');
  const overlay = document.querySelector('.mobile-overlay');
  const close = document.querySelector('.mobile-nav-close');
  if (!btn || !nav) return;

  const open = () => {
    btn.classList.add('active');
    nav.classList.add('open');
    overlay && overlay.classList.add('open');
    document.body.style.overflow = 'hidden';
  };
  const shut = () => {
    btn.classList.remove('active');
    nav.classList.remove('open');
    overlay && overlay.classList.remove('open');
    document.body.style.overflow = '';
  };

  btn.addEventListener('click', () => nav.classList.contains('open') ? shut() : open());
  close && close.addEventListener('click', shut);
  overlay && overlay.addEventListener('click', shut);
  nav.querySelectorAll('a').forEach(a => a.addEventListener('click', shut));
}

/* ----- Scroll Reveal ----- */
function initScrollReveal() {
  const els = document.querySelectorAll('.reveal');
  if (!els.length) return;
  const io = new IntersectionObserver((entries) => {
    entries.forEach(e => {
      if (e.isIntersecting) {
        e.target.classList.add('visible');
        io.unobserve(e.target);
      }
    });
  }, { threshold: 0.12, rootMargin: '0px 0px -30px 0px' });
  els.forEach(el => io.observe(el));
}

/* ----- Smooth Scroll ----- */
function initSmoothScroll() {
  document.querySelectorAll('a[href^="#"]').forEach(a => {
    a.addEventListener('click', function (e) {
      const tgt = document.querySelector(this.getAttribute('href'));
      if (tgt) {
        e.preventDefault();
        window.scrollTo({
          top: tgt.getBoundingClientRect().top + window.pageYOffset - 80,
          behavior: 'smooth'
        });
      }
    });
  });
}

/* ----- Search Tabs ----- */
function initSearchTabs() {
  const tabs = document.querySelectorAll('.search-tab');
  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      tabs.forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
    });
  });
}

/* ----- Wishlist Toggle ----- */
function initWishlistButtons() {
  document.querySelectorAll('.tour-wishlist').forEach(btn => {
    btn.addEventListener('click', function (e) {
      e.preventDefault();
      e.stopPropagation();
      this.classList.toggle('liked');
      this.textContent = this.classList.contains('liked') ? '❤️' : '🤍';
    });
  });
}

/* ----- Count Up Stats ----- */
function initCountUp() {
  const stats = document.querySelectorAll('[data-count]');
  if (!stats.length) return;
  const io = new IntersectionObserver((entries) => {
    entries.forEach(e => {
      if (e.isIntersecting) {
        const el = e.target;
        const target = parseInt(el.dataset.count);
        const suffix = el.dataset.suffix || '';
        const dur = 2000;
        const start = performance.now();
        (function tick(now) {
          const progress = Math.min((now - start) / dur, 1);
          const eased = 1 - Math.pow(1 - progress, 3);
          el.textContent = Math.floor(eased * target) + suffix;
          if (progress < 1) requestAnimationFrame(tick);
          else el.textContent = target + suffix;
        })(start);
        io.unobserve(el);
      }
    });
  }, { threshold: 0.5 });
  stats.forEach(s => io.observe(s));
}
