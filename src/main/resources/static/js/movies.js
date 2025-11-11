// Movies Page JavaScript

let currentFilter = '';

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
    window.location.href = url.toString();
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
    window.location.href = url.toString();
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
    window.location.href = url.toString();
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
    window.location.href = url.toString();
}

// Open modal for new movie
function openNewMovieModal() {
    const modal = new bootstrap.Modal(document.getElementById('movieModal'));
    document.getElementById('movieModalLabel').textContent = 'New Movie';
    document.getElementById('movieForm').reset();
    document.getElementById('movieId').value = '';
    document.getElementById('movieWinner').checked = false;
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
    
    modal.show();
}

// Save movie (create or update)
function saveMovie() {
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
            window.location.reload();
        }, 1000);
    })
    .catch(error => {
        showNotification(error.message || 'Error saving movie', 'error');
    });
}

// Confirm and delete movie
function confirmDeleteMovie(button) {
    const id = button.getAttribute('data-id');
    const title = button.getAttribute('data-title');
    
    if (confirm(`Are you sure you want to delete the movie "${title}"?`)) {
        deleteMovie(id);
    }
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
        showNotification('Movie deleted successfully!', 'success');
        
        // Reload page after a short delay
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    })
    .catch(error => {
        showNotification(error.message || 'Error deleting movie', 'error');
    });
}

// Show notification
function showNotification(message, type) {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `alert alert-${type === 'success' ? 'success' : 'danger'} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    notification.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    
    document.body.appendChild(notification);
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Initialize filter on page load and maintain focus
document.addEventListener('DOMContentLoaded', function() {
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
});
