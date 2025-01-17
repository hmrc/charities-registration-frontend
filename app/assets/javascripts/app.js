document.addEventListener("DOMContentLoaded", function () {

    // =====================================================
    // Back link mimics browser back functionality
    // =====================================================

    // Prevent resubmit warning
    if (window.history && window.history.replaceState && typeof window.history.replaceState === 'function') {
        window.history.replaceState(null, null, window.location.href);
    }

    // Back click handle, dependent upon presence of referrer & no host change
    const backLink = document.querySelector('#back-link[href="#"]');
    if (backLink) {
        backLink.addEventListener('click', function (e) {
            e.preventDefault();
            window.history.back();
        });
    }

});
