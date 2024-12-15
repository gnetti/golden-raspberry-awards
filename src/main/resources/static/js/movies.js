// Movies Page JavaScript

let currentFilter = '';

// Detect if page is loaded inside an iframe (modal)
function isInIframe() {
    try {
        return window.self !== window.top;
    } catch (e) {
        return true;
    }
}

// Close parent modal helper
function closeParentModal() {
    try {
        const parentWin = window.parent;
        const parentDoc = parentWin.document;
        const modalElement = parentDoc.getElementById('moviesModal');
        if (modalElement) {
            // Try to get Bootstrap from parent window
            let Bootstrap = null;
            if (parentWin.bootstrap) {
                Bootstrap = parentWin.bootstrap;
            } else if (parentWin.window && parentWin.window.bootstrap) {
                Bootstrap = parentWin.window.bootstrap;
            }
            
            if (Bootstrap && Bootstrap.Modal) {
                let modal = Bootstrap.Modal.getInstance(modalElement);
                if (modal) {
                    modal.hide();
                } else {
                    modal = new Bootstrap.Modal(modalElement);
                    modal.hide();
                }
            } else {
                // Fallback: hide modal manually
                modalElement.classList.remove('show');
                modalElement.setAttribute('aria-hidden', 'true');
                modalElement.style.display = 'none';
                const backdrop = parentDoc.querySelector('.modal-backdrop');
                if (backdrop) backdrop.remove();
                parentDoc.body.classList.remove('modal-open');
                parentDoc.body.style.overflow = '';
                parentDoc.body.style.paddingRight = '';
            }
        }
    } catch (err) {
        console.warn('Could not close parent modal:', err);
    }
}

// Make links navigate within the same iframe when inside modal
function setupIframeLinks() {
    if (isInIframe()) {
        // Find ALL links that should navigate within the same iframe
        const links = document.querySelectorAll('a[href]');
        links.forEach(link => {
            const href = link.getAttribute('href');
            // Skip external links, mailto, tel, etc.
            if (href && (href.startsWith('http://') || href.startsWith('https://') || 
                href.startsWith('mailto:') || href.startsWith('tel:') || href.startsWith('#'))) {
                return;
            }
            
            // Mark link to prevent modal interception
            link.setAttribute('data-iframe-navigate', 'true');
            
            link.addEventListener('click', function(e) {
                if (window.parent && window.parent !== window.self) {
                    e.preventDefault();
                    e.stopPropagation();
                    
                    // Navigate within the same iframe (don't close modal, just change iframe src)
                    try {
                        // Get the iframe element from parent
                        const parentDoc = window.parent.document;
                        const iframe = parentDoc.getElementById('moviesIframe');
                        if (iframe) {
                            iframe.src = href;
                        } else {
                            // Fallback: navigate in parent window
                            window.parent.location.href = href;
                        }
                    } catch (err) {
                        console.warn('Could not navigate iframe:', err);
                        // Fallback: navigate in parent window
                        try {
                            window.parent.location.href = href;
                        } catch (e) {
                            console.warn('Could not navigate parent:', e);
                        }
                    }
                }
            });
        });
    }
}

function changePageSize(size) {
    const url = new URL(window.location.href);
    url.searchParams.set('size', size);
    url.searchParams.set('page', '0');
    // Always preserve filterType and filterValue
    const filterType = document.getElementById('filterType').value;
    const filterValue = document.getElementById('filterInput').value.trim();
    url.searchParams.set('filterType', filterType);
    if (filterValue) {
        url.searchParams.set('filterValue', filterValue);
    } else {
        url.searchParams.delete('filterValue');
    }
    
    if (isInIframe()) {
        // Navigate within the same iframe
        try {
            const parentDoc = window.parent.document;
            const iframe = parentDoc.getElementById('moviesIframe');
            if (iframe) {
                iframe.src = url.toString();
            } else {
                window.location.href = url.toString();
            }
        } catch (err) {
            window.location.href = url.toString();
        }
    } else {
        window.location.href = url.toString();
    }
}

function changeSort(sortBy) {
    const url = new URL(window.location.href);
    url.searchParams.set('sortBy', sortBy);
    url.searchParams.set('page', '0');
    // Always preserve filterType and filterValue
    const filterType = document.getElementById('filterType').value;
    const filterValue = document.getElementById('filterInput').value.trim();
    url.searchParams.set('filterType', filterType);
    if (filterValue) {
        url.searchParams.set('filterValue', filterValue);
    } else {
        url.searchParams.delete('filterValue');
    }
    
    if (isInIframe()) {
        // Navigate within the same iframe
        try {
            const parentDoc = window.parent.document;
            const iframe = parentDoc.getElementById('moviesIframe');
            if (iframe) {
                iframe.src = url.toString();
            } else {
                window.location.href = url.toString();
            }
        } catch (err) {
            window.location.href = url.toString();
        }
    } else {
        window.location.href = url.toString();
    }
}

function changeDirection(direction) {
    const url = new URL(window.location.href);
    url.searchParams.set('direction', direction);
    url.searchParams.set('page', '0');
    // Always preserve filterType and filterValue
    const filterType = document.getElementById('filterType').value;
    const filterValue = document.getElementById('filterInput').value.trim();
    url.searchParams.set('filterType', filterType);
    if (filterValue) {
        url.searchParams.set('filterValue', filterValue);
    } else {
        url.searchParams.delete('filterValue');
    }
    
    if (isInIframe()) {
        // Navigate within the same iframe
        try {
            const parentDoc = window.parent.document;
            const iframe = parentDoc.getElementById('moviesIframe');
            if (iframe) {
                iframe.src = url.toString();
            } else {
                window.location.href = url.toString();
            }
        } catch (err) {
            window.location.href = url.toString();
        }
    } else {
        window.location.href = url.toString();
    }
}

// Handle filter input keyup with debounce
let filterTimeout;
function handleFilterKeyup(event) {
    // If Enter key is pressed, filter immediately
    if (event.key === 'Enter') {
        clearTimeout(filterTimeout);
        filterMovies();
        return;
    }
    
    // Otherwise, debounce the filter
    clearTimeout(filterTimeout);
    filterTimeout = setTimeout(() => {
        filterMovies();
    }, 500); // Wait 500ms after user stops typing
}

// Filter movies using backend - searches in ALL database records
function filterMovies() {
    const filterInput = document.getElementById('filterInput');
    let filterValue = filterInput.value.trim();
    const filterType = document.getElementById('filterType').value;
    
    // Validate minimum length based on filter type
    let minLength = 1;
    switch(filterType) {
        case 'id':
            minLength = 1; // ID: from 1st character
            break;
        case 'year':
            minLength = 2; // Year: from 2nd character
            break;
        case 'title':
        case 'studios':
        case 'producers':
        case 'all':
            minLength = 3; // String fields: from 3rd character
            break;
    }
    
    // If filter value is too short, don't search (but keep the value in input)
    if (filterValue && filterValue.length < minLength) {
        return; // Don't search yet, wait for more characters
    }
    
    // Build URL with current pagination and sort parameters
    const url = new URL(window.location.href);
    url.searchParams.set('page', '0'); // Reset to first page when filtering
    
    // Always set focusFilter flag when filtering (even when clearing)
    url.searchParams.set('focusFilter', 'true');
    
    // Always preserve filterType to maintain select value
    url.searchParams.set('filterType', filterType);
    
    if (filterValue && filterValue.length >= minLength) {
        url.searchParams.set('filterValue', filterValue);
    } else {
        url.searchParams.delete('filterValue');
    }
    
    // Reload page with filter parameters - this will search in ALL database records
    if (isInIframe()) {
        // Navigate within the same iframe
        try {
            const parentDoc = window.parent.document;
            const iframe = parentDoc.getElementById('moviesIframe');
            if (iframe) {
                iframe.src = url.toString();
            } else {
                window.location.href = url.toString();
            }
        } catch (err) {
            window.location.href = url.toString();
        }
    } else {
        window.location.href = url.toString();
    }
}

// Open modal for new movie
function openNewMovieModal() {
    const modal = new bootstrap.Modal(document.getElementById('movieModal'));
    document.getElementById('movieModalLabel').textContent = 'New Movie';
    document.getElementById('movieForm').reset();
    document.getElementById('movieId').value = '';
    document.getElementById('movieWinner').checked = false;
    // Clear validation states
    const yearField = document.getElementById('movieYear');
    if (yearField) {
        yearField.classList.remove('is-valid', 'is-invalid');
        const errorDiv = document.getElementById('movieYear-error');
        if (errorDiv) errorDiv.textContent = '';
    }
    modal.show();
}

// Open modal for editing movie
function openEditMovieModal(button) {
    const modal = new bootstrap.Modal(document.getElementById('movieModal'));
    document.getElementById('movieModalLabel').textContent = 'Edit Movie';
    
    const id = button.getAttribute('data-id');
    const year = button.getAttribute('data-year');
    const title = button.getAttribute('data-title');
    const studios = button.getAttribute('data-studios');
    const producers = button.getAttribute('data-producers');
    const winner = button.getAttribute('data-winner') === 'true';
    
    document.getElementById('movieId').value = id;
    document.getElementById('movieYear').value = year;
    document.getElementById('movieTitle').value = title;
    document.getElementById('movieStudios').value = studios;
    document.getElementById('movieProducers').value = producers;
    document.getElementById('movieWinner').checked = winner;
    
    // Clear validation states
    const yearField = document.getElementById('movieYear');
    if (yearField) {
        yearField.classList.remove('is-valid', 'is-invalid');
        const errorDiv = document.getElementById('movieYear-error');
        if (errorDiv) errorDiv.textContent = '';
    }
    
    modal.show();
}

// Validate year field
function validateYearField() {
    const yearField = document.getElementById('movieYear');
    const errorDiv = document.getElementById('movieYear-error');
    if (!yearField || !errorDiv) return true;
    
    const yearValue = yearField.value;
    if (!yearValue || yearValue === '') {
        yearField.classList.remove('is-valid');
        yearField.classList.add('is-invalid');
        errorDiv.textContent = 'Year is required';
        return false;
    }
    
    const year = parseInt(yearValue);
    const currentYear = parseInt(yearField.getAttribute('data-current-year')) || new Date().getFullYear();
    
    if (isNaN(year)) {
        yearField.classList.remove('is-valid');
        yearField.classList.add('is-invalid');
        errorDiv.textContent = 'Year must be a number';
        return false;
    }
    
    if (year < 1900) {
        yearField.classList.remove('is-valid');
        yearField.classList.add('is-invalid');
        errorDiv.textContent = 'Year must be at least 1900';
        return false;
    }
    
    if (year > currentYear) {
        yearField.classList.remove('is-valid');
        yearField.classList.add('is-invalid');
        errorDiv.textContent = `Year cannot be in the future. Maximum allowed year is ${currentYear} (current year)`;
        return false;
    }
    
    yearField.classList.remove('is-invalid');
    yearField.classList.add('is-valid');
    errorDiv.textContent = '';
    return true;
}

// Save movie (create or update)
function saveMovie() {
    // Validate year field first
    if (!validateYearField()) {
        return;
    }
    
    const form = document.getElementById('movieForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const id = document.getElementById('movieId').value;
    const movieData = {
        year: parseInt(document.getElementById('movieYear').value),
        title: document.getElementById('movieTitle').value.trim(),
        studios: document.getElementById('movieStudios').value.trim(),
        producers: document.getElementById('movieProducers').value.trim(),
        winner: document.getElementById('movieWinner').checked
    };
    
    const url = id ? `/api/movies/${id}` : '/api/movies';
    const method = id ? 'PUT' : 'POST';
    
    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(movieData)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => {
                const errorMessage = err.message || err.errors?.map(e => e.message).join(', ') || 'Error saving movie';
                throw new Error(errorMessage);
            }).catch(() => {
                throw new Error('Error saving movie');
            });
        }
        return response.json();
    })
    .then(data => {
        const modalElement = document.getElementById('movieModal');
        const modal = bootstrap.Modal.getInstance(modalElement);
        if (modal) {
            modal.hide();
        }
        
        // Show success message
        showNotification('Movie saved successfully!', 'success');
        
        // Reload page after a short delay
        setTimeout(() => {
            if (isInIframe()) {
                // Reload within the same iframe
                try {
                    const parentDoc = window.parent.document;
                    const iframe = parentDoc.getElementById('moviesIframe');
                    if (iframe) {
                        iframe.src = window.location.href;
                    } else {
                        window.location.reload();
                    }
                } catch (err) {
                    window.location.reload();
                }
            } else {
                window.location.reload();
            }
        }, 1000);
    })
    .catch(error => {
        showNotification(error.message || 'Error saving movie', 'error');
    });
}

// Confirm and delete movie with elegant modal
function confirmDeleteMovie(button) {
    const id = button.getAttribute('data-id');
    const title = button.getAttribute('data-title');
    
    showDeleteConfirmationModal(id, title);
}

// Delete movie
function deleteMovie(id) {
    fetch(`/api/movies/${id}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => {
                const errorMessage = err.message || 'Error deleting movie';
                throw new Error(errorMessage);
            }).catch(() => {
                throw new Error('Error deleting movie');
            });
        }
        return response.status === 204 ? null : response.json();
    })
    .then(() => {
        // Show success toast notification
        showNotification('Movie deleted successfully!', 'success');
        
        // Reload page after a short delay to show the toast
        setTimeout(() => {
            if (isInIframe()) {
                // Reload within the same iframe
                try {
                    const parentDoc = window.parent.document;
                    const iframe = parentDoc.getElementById('moviesIframe');
                    if (iframe) {
                        iframe.src = window.location.href;
                    } else {
                        window.location.reload();
                    }
                } catch (err) {
                    window.location.reload();
                }
            } else {
                window.location.reload();
            }
        }, 2000);
    })
    .catch(error => {
        showNotification(error.message || 'Error deleting movie', 'error');
    });
}

// Show notification with modern toast design
function showNotification(message, type) {
    // Remove any existing notifications
    const existingNotifications = document.querySelectorAll('.custom-toast');
    existingNotifications.forEach(notif => notif.remove());
    
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `custom-toast custom-toast-${type}`;
    
    const isSuccess = type === 'success';
    const icon = isSuccess ? 'fa-check-circle' : 'fa-exclamation-circle';
    const iconBg = isSuccess ? 'linear-gradient(135deg, #198754 0%, #157347 100%)' : 'linear-gradient(135deg, #dc3545 0%, #bb2d3b 100%)';
    
    notification.innerHTML = `
        <div class="toast-content">
            <div class="toast-icon" style="background: ${iconBg};">
                <i class="fas ${icon}"></i>
            </div>
            <div class="toast-message">
                <div class="toast-title">${isSuccess ? 'Success!' : 'Error!'}</div>
                <div class="toast-text">${message}</div>
            </div>
            <button type="button" class="toast-close" onclick="closeToast(this.closest('.custom-toast'))">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <div class="toast-progress"></div>
    `;
    
    document.body.appendChild(notification);
    
    // Trigger animation
    setTimeout(() => {
        notification.classList.add('show');
    }, 10);
    
    // Auto-remove after 5 seconds with fade out
    setTimeout(() => {
        closeToast(notification);
    }, 5000);
}

/**
 * Closes a toast notification with smooth animation.
 * @param {HTMLElement} toast - The toast element to close
 */
function closeToast(toast) {
    if (!toast) return;
    
    toast.classList.remove('show');
    setTimeout(() => {
        toast.remove();
    }, 300);
}

/**
 * Shows an elegant delete confirmation modal.
 * @param {string} id - Movie ID to delete
 * @param {string} title - Movie title
 */
function showDeleteConfirmationModal(id, title) {
    // Remove any existing modals
    const existingModal = document.getElementById('deleteConfirmationModal');
    if (existingModal) {
        existingModal.remove();
    }
    
    // Create modal overlay
    const overlay = document.createElement('div');
    overlay.className = 'delete-modal-overlay';
    overlay.id = 'deleteConfirmationModal';
    
    // Create modal content
    const modal = document.createElement('div');
    modal.className = 'delete-modal-content';
    
    modal.innerHTML = `
        <div class="delete-modal-icon">
            <i class="fas fa-exclamation-triangle"></i>
        </div>
        <div class="delete-modal-title">Confirm Deletion</div>
        <div class="delete-modal-message">
            Are you sure you want to delete the movie<br>
            <strong>"${title}"</strong>?
        </div>
        <div class="delete-modal-warning">
            <i class="fas fa-info-circle"></i>
            This action cannot be undone.
        </div>
        <div class="delete-modal-actions">
            <button type="button" class="delete-modal-btn delete-modal-btn-cancel" onclick="closeDeleteModal()">
                <i class="fas fa-times me-2"></i>No
            </button>
            <button type="button" class="delete-modal-btn delete-modal-btn-confirm" onclick="confirmDelete(${id})">
                <i class="fas fa-check me-2"></i>Yes
            </button>
        </div>
    `;
    
    overlay.appendChild(modal);
    document.body.appendChild(overlay);
    
    // Trigger animation
    setTimeout(() => {
        overlay.classList.add('show');
        // Focus on "No" button for safety (easier to cancel)
        const cancelButton = modal.querySelector('.delete-modal-btn-cancel');
        if (cancelButton) {
            setTimeout(() => {
                cancelButton.focus();
            }, 100);
        }
    }, 10);
    
    // Close on overlay click
    overlay.addEventListener('click', function(e) {
        if (e.target === overlay) {
            closeDeleteModal();
        }
    });
    
    // Close on Escape key or Enter key (defaults to "No" for safety)
    const escapeHandler = function(e) {
        if (e.key === 'Escape') {
            closeDeleteModal();
            document.removeEventListener('keydown', escapeHandler);
        } else if (e.key === 'Enter') {
            // Enter key defaults to "No" (cancel) for safety
            e.preventDefault();
            closeDeleteModal();
            document.removeEventListener('keydown', escapeHandler);
        }
    };
    document.addEventListener('keydown', escapeHandler);
}

/**
 * Closes the delete confirmation modal.
 */
function closeDeleteModal() {
    const modal = document.getElementById('deleteConfirmationModal');
    if (modal) {
        modal.classList.remove('show');
        setTimeout(() => {
            modal.remove();
        }, 300);
    }
}

/**
 * Confirms and executes the deletion.
 * @param {string} id - Movie ID to delete
 */
function confirmDelete(id) {
    closeDeleteModal();
    deleteMovie(id);
}

// Initialize filter on page load and maintain focus
// Add event listeners for year field validation in modal
function setupYearFieldValidation() {
    const yearField = document.getElementById('movieYear');
    if (!yearField) return;
    
    yearField.addEventListener('blur', validateYearField);
    yearField.addEventListener('input', function() {
        clearTimeout(yearField.validationTimeout);
        yearField.validationTimeout = setTimeout(() => {
            validateYearField();
        }, 500);
    });
}

document.addEventListener('DOMContentLoaded', function() {
    setupYearFieldValidation();
    const filterInput = document.getElementById('filterInput');
    if (filterInput) {
        // Restore filter value if exists
        if (currentFilter) {
            filterInput.value = currentFilter;
        }
        
        // Maintain focus on filter input after page reload if focusFilter flag is set
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('focusFilter') === 'true') {
            // Small delay to ensure page is fully loaded
            setTimeout(() => {
                filterInput.focus();
                // Move cursor to end of input
                const length = filterInput.value.length;
                filterInput.setSelectionRange(length, length);
                
                // Remove focusFilter parameter from URL without reload
                urlParams.delete('focusFilter');
                const newUrl = window.location.pathname + '?' + urlParams.toString();
                window.history.replaceState({}, '', newUrl);
            }, 100);
        }
    }
    
    // Setup iframe links to close modal and navigate in parent
    setupIframeLinks();
});
