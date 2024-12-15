/**
 * Validation and form handling for Edit Movie page.
 * Validates fields one by one and enables Save button only when all fields are valid.
 */

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

// Setup links to navigate within the same iframe when inside modal
function setupIframeLinks() {
    if (isInIframe()) {
        const links = document.querySelectorAll('a[href]');
        links.forEach(link => {
            const href = link.getAttribute('href');
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

// Validation rules matching backend CreateMovieDTO and MovieValidation
const VALIDATION_RULES = {
    year: {
        required: true,
        min: 1900,
        getMax: () => {
            const yearField = document.getElementById('year');
            if (yearField && yearField.getAttribute('data-current-year')) {
                return parseInt(yearField.getAttribute('data-current-year'));
            }
            return new Date().getFullYear();
        },
        validate: (value) => {
            if (!value || value === '') return "";
            const num = parseInt(value);
            if (isNaN(num)) return "Year must be a number";
            const yearField = document.getElementById('year');
            const currentYear = yearField && yearField.getAttribute('data-current-year') 
                ? parseInt(yearField.getAttribute('data-current-year'))
                : new Date().getFullYear();
            const minYear = yearField && yearField.getAttribute('data-min-year')
                ? parseInt(yearField.getAttribute('data-min-year'))
                : 1900;
            if (num < minYear) return `Year must be at least ${minYear}`;
            if (num > currentYear) return `Year cannot be in the future. Maximum allowed year is ${currentYear} (current year)`;
            return null;
        }
    },
    title: {
        required: true,
        minLength: 2,
        maxLength: 255,
        validate: (value) => {
            if (!value) return "";
            const trimmed = value.trim();
            if (trimmed.length === 0) return "Title cannot be empty or contain only whitespace";
            if (trimmed.length < 2) return "Title must be at least 2 characters";
            if (trimmed.length > 255) return "Title must be at most 255 characters";
            return null;
        }
    },
    studios: {
        required: true,
        minLength: 2,
        maxLength: 255,
        validate: (value) => {
            if (!value) return "";
            const trimmed = value.trim();
            if (trimmed.length === 0) return "Studios cannot be empty or contain only whitespace";
            if (trimmed.length < 2) return "Studios must be at least 2 characters";
            if (trimmed.length > 255) return "Studios must be at most 255 characters";
            return null;
        }
    },
    producers: {
        required: true,
        minLength: 2,
        maxLength: 255,
        validate: (value) => {
            if (!value) return "";
            const trimmed = value.trim();
            if (trimmed.length === 0) return "Producers cannot be empty or contain only whitespace";
            if (trimmed.length < 2) return "Producers must be at least 2 characters";
            if (trimmed.length > 255) return "Producers must be at most 255 characters";
            return null;
        }
    },
    winner: {
        required: false,
        validate: (value) => {
            return null;
        }
    }
};

// Track validation state for each field - initialize as valid since fields are pre-filled
const fieldValidationState = {
    year: true,
    title: true,
    studios: true,
    producers: true,
    winner: true
};

/**
 * Validates a single field and updates its UI state.
 * @param {string} fieldName - Name of the field to validate
 * @param {string} value - Current value of the field
 * @param {boolean} showEmptyError - Whether to show error message when field is empty (default: false)
 * @returns {boolean} - True if field is valid, false otherwise
 */
function validateField(fieldName, value, showEmptyError = false) {
    const field = document.getElementById(fieldName);
    const errorDiv = document.getElementById(`${fieldName}-error`);
    const rule = VALIDATION_RULES[fieldName];
    
    if (!rule) {
        return true;
    }
    
    const errorMessage = rule.validate(value);
    
    const isEmpty = !value || value === '' || (typeof value === 'string' && value.trim() === '');
    const isRequired = rule.required === true;
    
    if (errorMessage !== null && errorMessage !== '') {
        field.classList.remove('is-valid');
        field.classList.add('is-invalid');
        if (errorDiv) {
            errorDiv.textContent = errorMessage;
        }
        fieldValidationState[fieldName] = false;
        return false;
    } else if (isEmpty && isRequired) {
        field.classList.remove('is-valid');
        field.classList.add('is-invalid');
        if (errorDiv) {
            if (showEmptyError) {
                errorDiv.textContent = `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} is required`;
            } else {
                errorDiv.textContent = '';
            }
        }
        fieldValidationState[fieldName] = false;
        return false;
    } else {
        field.classList.remove('is-invalid');
        field.classList.add('is-valid');
        if (errorDiv) {
            errorDiv.textContent = '';
        }
        fieldValidationState[fieldName] = true;
        return true;
    }
}

/**
 * Checks if all fields are valid and enables/disables the Save button.
 */
function updateSaveButton() {
    const allValid = Object.values(fieldValidationState).every(valid => valid === true);
    const saveButton = document.getElementById('saveButton');
    
    if (saveButton) {
        saveButton.disabled = !allValid;
    }
}

/**
 * Validates all fields and updates Save button state.
 */
function validateAllFields() {
    const fields = ['year', 'title', 'studios', 'producers', 'winner'];
    fields.forEach(fieldName => {
        const field = document.getElementById(fieldName);
        if (field) {
            const value = field.type === 'checkbox' ? field.checked : field.value;
            validateField(fieldName, value, false);
        }
    });
    updateSaveButton();
}

/**
 * Updates the movie by sending PUT request to the API.
 */
async function saveMovie() {
    const saveButton = document.getElementById('saveButton');
    if (saveButton.disabled) {
        return;
    }
    
    const movieId = document.getElementById('movieId').value;
    if (!movieId) {
        showNotification('Movie ID is missing', 'error');
        return;
    }
    
    // Validate all fields one more time before saving
    const fields = ['year', 'title', 'studios', 'producers', 'winner'];
    fields.forEach(fieldName => {
        const field = document.getElementById(fieldName);
        if (field) {
            const value = field.type === 'checkbox' ? field.checked : field.value;
            validateField(fieldName, value, true);
        }
    });
    updateSaveButton();
    if (!Object.values(fieldValidationState).every(valid => valid === true)) {
        return;
    }
    
    // Disable button during save
    saveButton.disabled = true;
    saveButton.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Updating...';
    
    // Prepare movie data
    const movieData = {
        year: parseInt(document.getElementById('year').value),
        title: document.getElementById('title').value.trim(),
        studios: document.getElementById('studios').value.trim(),
        producers: document.getElementById('producers').value.trim(),
        winner: document.getElementById('winner').checked
    };
    
    try {
        const response = await fetch(`/api/movies/${movieId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(movieData)
        });
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            const errorMessage = errorData.message || 
                               (errorData.errors && errorData.errors.map(e => e.message).join(', ')) || 
                               'Error updating movie';
            throw new Error(errorMessage);
        }
        
        const data = await response.json();
        
        // Show success message
        showNotification('Movie updated successfully!', 'success');
        
        // Redirect to movies list after a short delay
        setTimeout(() => {
            if (isInIframe()) {
                // If inside iframe, navigate within the same iframe
                try {
                    const parentDoc = window.parent.document;
                    const iframe = parentDoc.getElementById('moviesIframe');
                    if (iframe) {
                        iframe.src = '/movies';
                    } else {
                        window.parent.location.href = '/movies';
                    }
                } catch (err) {
                    console.warn('Could not navigate iframe:', err);
                    try {
                        window.parent.location.href = '/movies';
                    } catch (e) {
                        console.warn('Could not navigate parent:', e);
                    }
                }
            } else {
                window.location.href = '/movies';
            }
        }, 1500);
        
    } catch (error) {
        // Re-enable button
        saveButton.disabled = false;
        saveButton.innerHTML = '<i class="fas fa-save me-2"></i>Update Movie';
        
        // Show error message
        showNotification(error.message || 'Error updating movie', 'error');
    }
}

/**
 * Shows a notification message with modern toast design.
 * @param {string} message - Message to display
 * @param {string} type - Type of notification: 'success' or 'error'
 */
function showNotification(message, type) {
    const existingNotifications = document.querySelectorAll('.custom-toast');
    existingNotifications.forEach(notif => notif.remove());
    
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

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    // Ensure ID field is always disabled and cannot be enabled
    const displayIdField = document.getElementById('displayId');
    if (displayIdField) {
        // Force disable and readonly
        displayIdField.disabled = true;
        displayIdField.readOnly = true;
        displayIdField.setAttribute('readonly', 'readonly');
        displayIdField.setAttribute('disabled', 'disabled');
        displayIdField.setAttribute('tabindex', '-1');
        
        // Prevent any modifications via JavaScript
        Object.defineProperty(displayIdField, 'disabled', {
            get: () => true,
            set: () => {},
            configurable: false
        });
        
        Object.defineProperty(displayIdField, 'readOnly', {
            get: () => true,
            set: () => {},
            configurable: false
        });
        
        // Remove any event listeners that might enable it
        displayIdField.addEventListener('focus', function(e) {
            e.preventDefault();
            this.blur();
        });
        
        displayIdField.addEventListener('click', function(e) {
            e.preventDefault();
            this.blur();
        });
        
        displayIdField.addEventListener('keydown', function(e) {
            e.preventDefault();
            return false;
        });
        
        displayIdField.addEventListener('keyup', function(e) {
            e.preventDefault();
            return false;
        });
        
        displayIdField.addEventListener('input', function(e) {
            e.preventDefault();
            return false;
        });
    }
    
    const yearField = document.getElementById('year');
    const titleField = document.getElementById('title');
    const studiosField = document.getElementById('studios');
    const producersField = document.getElementById('producers');
    const winnerField = document.getElementById('winner');
    const saveButton = document.getElementById('saveButton');
    
    // Add event listeners for real-time validation
    if (yearField) {
        yearField.addEventListener('click', () => {
            openYearPicker();
        });
    }
    
    if (titleField) {
        titleField.addEventListener('blur', () => {
            validateField('title', titleField.value, true);
            updateSaveButton();
        });
        titleField.addEventListener('input', () => {
            clearTimeout(titleField.validationTimeout);
            titleField.validationTimeout = setTimeout(() => {
                validateField('title', titleField.value, false);
                updateSaveButton();
            }, 500);
        });
    }
    
    if (studiosField) {
        studiosField.addEventListener('blur', () => {
            validateField('studios', studiosField.value, true);
            updateSaveButton();
        });
        studiosField.addEventListener('input', () => {
            clearTimeout(studiosField.validationTimeout);
            studiosField.validationTimeout = setTimeout(() => {
                validateField('studios', studiosField.value, false);
                updateSaveButton();
            }, 500);
        });
    }
    
    if (producersField) {
        producersField.addEventListener('blur', () => {
            validateField('producers', producersField.value, true);
            updateSaveButton();
        });
        producersField.addEventListener('input', () => {
            clearTimeout(producersField.validationTimeout);
            producersField.validationTimeout = setTimeout(() => {
                validateField('producers', producersField.value, false);
                updateSaveButton();
            }, 500);
        });
    }
    
    if (winnerField) {
        winnerField.addEventListener('change', () => {
            validateField('winner', winnerField.checked);
            updateSaveButton();
        });
    }
    
    // Attach save function to button
    if (saveButton) {
        saveButton.addEventListener('click', saveMovie);
    }
    
    // Initial validation (fields are pre-filled, so they should be valid)
    validateAllFields();
    
    // Initialize year picker
    initializeYearPicker();
    
    // Setup iframe links to close modal and navigate in parent
    setupIframeLinks();
});

// Year Picker functionality
let currentYearRange = { start: 2020, end: 2029 };
let selectedYear = null;

function initializeYearPicker() {
    const yearField = document.getElementById('year');
    if (!yearField) return;
    
    const currentYear = parseInt(yearField.getAttribute('data-current-year')) || new Date().getFullYear();
    const minYear = parseInt(yearField.getAttribute('data-min-year')) || 1900;
    const currentValue = yearField.value;
    
    // Set initial range to include current year or selected year
    if (currentValue) {
        selectedYear = parseInt(currentValue);
        const decade = Math.floor(selectedYear / 10) * 10;
        currentYearRange = { start: decade, end: decade + 9 };
    } else {
        const decade = Math.floor(currentYear / 10) * 10;
        currentYearRange = { start: decade, end: decade + 9 };
    }
    
    renderYearPicker();
}

function openYearPicker() {
    const modal = document.getElementById('yearPickerModal');
    const yearField = document.getElementById('year');
    if (!modal || !yearField) return;
    
    const currentValue = yearField.value;
    if (currentValue) {
        selectedYear = parseInt(currentValue);
        const decade = Math.floor(selectedYear / 10) * 10;
        currentYearRange = { start: decade, end: decade + 9 };
        renderYearPicker();
    }
    
    modal.style.display = 'block';
    
    setTimeout(() => {
        document.addEventListener('click', closeYearPickerOnOutsideClick);
    }, 100);
}

function closeYearPicker() {
    const modal = document.getElementById('yearPickerModal');
    if (modal) {
        modal.style.display = 'none';
    }
    document.removeEventListener('click', closeYearPickerOnOutsideClick);
}

function closeYearPickerOnOutsideClick(event) {
    const modal = document.getElementById('yearPickerModal');
    const icon = document.getElementById('yearCalendarIcon');
    if (modal && icon && !modal.contains(event.target) && !icon.contains(event.target)) {
        closeYearPicker();
    }
}

function changeYearPicker(direction) {
    const yearField = document.getElementById('year');
    if (!yearField) return;
    
    const minYear = parseInt(yearField.getAttribute('data-min-year')) || 1900;
    const maxYear = parseInt(yearField.getAttribute('data-current-year')) || new Date().getFullYear();
    
    currentYearRange.start += direction;
    currentYearRange.end += direction;
    
    if (currentYearRange.start < minYear) {
        currentYearRange.start = minYear;
        currentYearRange.end = Math.min(minYear + 9, maxYear);
    }
    if (currentYearRange.end > maxYear) {
        currentYearRange.end = maxYear;
        currentYearRange.start = Math.max(maxYear - 9, minYear);
    }
    
    renderYearPicker();
}

function renderYearPicker() {
    const grid = document.getElementById('yearPickerGrid');
    const rangeText = document.getElementById('yearPickerRange');
    const yearField = document.getElementById('year');
    
    if (!grid || !rangeText || !yearField) return;
    
    const minYear = parseInt(yearField.getAttribute('data-min-year')) || 1900;
    const maxYear = parseInt(yearField.getAttribute('data-current-year')) || new Date().getFullYear();
    
    rangeText.textContent = `${currentYearRange.start} - ${currentYearRange.end}`;
    
    grid.innerHTML = '';
    
    for (let year = currentYearRange.start; year <= currentYearRange.end; year++) {
        const item = document.createElement('div');
        item.className = 'year-picker-item';
        item.textContent = year;
        
        if (year < minYear || year > maxYear) {
            item.classList.add('disabled');
        } else {
            if (selectedYear === year) {
                item.classList.add('selected');
            }
            item.addEventListener('click', () => selectYear(year));
        }
        
        grid.appendChild(item);
    }
}

function selectYear(year) {
    const yearField = document.getElementById('year');
    if (!yearField) return;
    
    yearField.value = year;
    selectedYear = year;
    
    validateField('year', year.toString(), false);
    updateSaveButton();
    
    closeYearPicker();
}

