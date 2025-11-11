/**
 * Movies list page functionality.
 * Handles pagination, sorting, filtering, and CRUD operations for movies.
 */

/**
 * Current filter value stored in memory.
 * @type {string}
 */
let currentFilter = '';

/**
 * Filter timeout for debouncing.
 * @type {number|null}
 */
let filterTimeout;

/**
 * Checks if the current page is loaded inside an iframe.
 * @returns {boolean} True if page is in an iframe, false otherwise
 */
function isInIframe() {
    try {
        return window.self !== window.top;
    } catch (e) {
        return true;
    }
}

/**
 * Sets up links to navigate within the same iframe when inside a modal.
 * Prevents modal from closing and navigates within the iframe instead.
 */
function setupIframeLinks() {
    if (isInIframe()) {
        const links = document.querySelectorAll('a[href]');
        links.forEach((link) => {
            const href = link.getAttribute('href');

            if (
                href &&
                (href.startsWith('http://') ||
                    href.startsWith('https://') ||
                    href.startsWith('mailto:') ||
                    href.startsWith('tel:') ||
                    href.startsWith('#'))
            ) {
                return;
            }

            link.setAttribute('data-iframe-navigate', 'true');

            link.addEventListener('click', function (e) {
                if (window.parent && window.parent !== window.self) {
                    e.preventDefault();
                    e.stopPropagation();

                    try {
                        const parentDoc = window.parent.document;
                        const iframe = parentDoc.getElementById('moviesIframe');
                        if (iframe) {
                            iframe.src = href;
                        } else {
                            window.parent.location.href = href;
                        }
                    } catch (err) {
                        console.warn('Could not navigate iframe:', err);
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

/**
 * Changes the page size and reloads the movies list.
 * Preserves filter type and filter value.
 * @param {number|string} size - Number of items per page
 */
function changePageSize(size) {
    const url = new URL(window.location.href);
    url.searchParams.set('size', size);
    url.searchParams.set('page', '0');
    const filterType = document.getElementById('filterType').value;
    const filterValue = document.getElementById('filterInput').value.trim();
    url.searchParams.set('filterType', filterType);
    if (filterValue) {
        url.searchParams.set('filterValue', filterValue);
    } else {
        url.searchParams.delete('filterValue');
    }

    if (isInIframe()) {
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

/**
 * Handles filter input keyup events with debounce.
 * Filters immediately on Enter key, otherwise waits 500ms after user stops typing.
 * @param {KeyboardEvent} event - Keyboard event from input field
 */
function handleFilterKeyup(event) {
    if (event.key === 'Enter') {
        clearTimeout(filterTimeout);
        filterMovies();
        return;
    }

    clearTimeout(filterTimeout);
    filterTimeout = setTimeout(() => {
        filterMovies();
    }, 500);
}

/**
 * Filters movies using backend search across all database records.
 * Validates minimum length based on filter type before searching.
 * Resets to first page when filtering.
 */
function filterMovies() {
    const filterInput = document.getElementById('filterInput');
    let filterValue = filterInput.value.trim();
    const filterType = document.getElementById('filterType').value;

    let minLength = 1;
    switch (filterType) {
        case 'id':
            minLength = 1;
            break;
        case 'year':
            minLength = 2;
            break;
        case 'title':
        case 'studios':
        case 'producers':
        case 'all':
            minLength = 3;
            break;
    }

    if (filterValue && filterValue.length < minLength) {
        return;
    }

    const url = new URL(window.location.href);
    url.searchParams.set('page', '0');

    url.searchParams.set('focusFilter', 'true');

    url.searchParams.set('filterType', filterType);

    if (filterValue && filterValue.length >= minLength) {
        url.searchParams.set('filterValue', filterValue);
    } else {
        url.searchParams.delete('filterValue');
    }

    if (isInIframe()) {
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

/**
 * Validates the year field in the movie form.
 * Checks for required value, numeric format, minimum year (1900), and maximum year (current year).
 * @returns {boolean} True if year is valid, false otherwise
 */
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
    const currentYear =
        parseInt(yearField.getAttribute('data-current-year')) || new Date().getFullYear();

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

/**
 * Saves a movie by sending POST (create) or PUT (update) request to the API.
 * Validates year field and form before saving.
 * Shows success notification and reloads page after save.
 */
function saveMovie() {
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
        winner: document.getElementById('movieWinner').checked,
    };

    const url = id ? `/api/movies/${id}` : '/api/movies';
    const method = id ? 'PUT' : 'POST';

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(movieData),
    })
        .then((response) => {
            if (!response.ok) {
                return response
                    .json()
                    .then((err) => {
                        const errorMessage =
                            err.message ||
                            err.errors?.map((e) => e.message).join(', ') ||
                            'Error saving movie';
                        throw new Error(errorMessage);
                    })
                    .catch(() => {
                        throw new Error('Error saving movie');
                    });
            }
            return response.json();
        })
        .then(() => {
            const modalElement = document.getElementById('movieModal');
            const modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) {
                modal.hide();
            }

            showNotification('Movie saved successfully!', 'success');

            setTimeout(() => {
                if (isInIframe()) {
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
        .catch((error) => {
            showNotification(error.message || 'Error saving movie', 'error');
        });
}

/**
 * Confirms movie deletion by showing a confirmation modal.
 * @param {HTMLElement} button - Button element containing movie data attributes
 */
function confirmDeleteMovie(button) {
    const id = button.getAttribute('data-id');
    const title = button.getAttribute('data-title');

    showDeleteConfirmationModal(id, title);
}

/**
 * Deletes a movie by sending DELETE request to the API.
 * Shows success notification and reloads page after deletion.
 * @param {string|number} id - Movie ID to delete
 */
function deleteMovie(id) {
    fetch(`/api/movies/${id}`, {
        method: 'DELETE',
    })
        .then((response) => {
            if (!response.ok) {
                return response
                    .json()
                    .then((err) => {
                        const errorMessage = err.message || 'Error deleting movie';
                        throw new Error(errorMessage);
                    })
                    .catch(() => {
                        throw new Error('Error deleting movie');
                    });
            }
            return response.status === 204 ? null : response.json();
        })
        .then(() => {
            showNotification('Movie deleted successfully!', 'success');

            setTimeout(() => {
                if (isInIframe()) {
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
        .catch((error) => {
            showNotification(error.message || 'Error deleting movie', 'error');
        });
}

/**
 * Shows a notification message with modern toast design.
 * @param {string} message - Message to display
 * @param {string} type - Type of notification: 'success' or 'error'
 */
function showNotification(message, type) {
    const existingNotifications = document.querySelectorAll('.custom-toast');
    existingNotifications.forEach((notif) => notif.remove());

    const notification = document.createElement('div');
    notification.className = `custom-toast custom-toast-${type}`;

    const isSuccess = type === 'success';
    const icon = isSuccess ? 'fa-check-circle' : 'fa-exclamation-circle';
    const iconBg = isSuccess
        ? 'linear-gradient(135deg, #198754 0%, #157347 100%)'
        : 'linear-gradient(135deg, #dc3545 0%, #bb2d3b 100%)';

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

    setTimeout(() => {
        notification.classList.add('show');
    }, 10);

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
    const existingModal = document.getElementById('deleteConfirmationModal');
    if (existingModal) {
        existingModal.remove();
    }

    const overlay = document.createElement('div');
    overlay.className = 'delete-modal-overlay';
    overlay.id = 'deleteConfirmationModal';

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

    setTimeout(() => {
        overlay.classList.add('show');
        const cancelButton = modal.querySelector('.delete-modal-btn-cancel');
        if (cancelButton) {
            setTimeout(() => {
                cancelButton.focus();
            }, 100);
        }
    }, 10);

    overlay.addEventListener('click', function (e) {
        if (e.target === overlay) {
            closeDeleteModal();
        }
    });

    const escapeHandler = function (e) {
        if (e.key === 'Escape') {
            closeDeleteModal();
            document.removeEventListener('keydown', escapeHandler);
        } else if (e.key === 'Enter') {
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
 * @param {string|number} id - Movie ID to delete
 */
function confirmDelete(id) {
    closeDeleteModal();
    deleteMovie(id);
}

/**
 * Sets up event listeners for year field validation in the movie modal.
 * Validates on blur and with debounce on input.
 */
function setupYearFieldValidation() {
    const yearField = document.getElementById('movieYear');
    if (!yearField) return;

    yearField.addEventListener('blur', validateYearField);
    yearField.addEventListener('input', function () {
        clearTimeout(yearField.validationTimeout);
        yearField.validationTimeout = setTimeout(() => {
            validateYearField();
        }, 500);
    });
}

/**
 * Initializes the page when DOM is ready.
 * Sets up year field validation, restores filter value, maintains focus on filter input,
 * and configures iframe links for modal navigation.
 */
document.addEventListener('DOMContentLoaded', function () {
    setupYearFieldValidation();
    const filterInput = document.getElementById('filterInput');
    if (filterInput) {
        if (currentFilter) {
            filterInput.value = currentFilter;
        }

        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('focusFilter') === 'true') {
            setTimeout(() => {
                filterInput.focus();
                const length = filterInput.value.length;
                filterInput.setSelectionRange(length, length);

                urlParams.delete('focusFilter');
                const newUrl = window.location.pathname + '?' + urlParams.toString();
                window.history.replaceState({}, '', newUrl);
            }, 100);
        }
    }

    setupIframeLinks();
});
