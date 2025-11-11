// Movies Page JavaScript

let currentFilter = '';

function changePageSize(size) {
    const url = new URL(window.location.href);
    url.searchParams.set('size', size);
    url.searchParams.set('page', '0');
    window.location.href = url.toString();
}

function changeSort(sortBy) {
    const url = new URL(window.location.href);
    url.searchParams.set('sortBy', sortBy);
    url.searchParams.set('page', '0');
    window.location.href = url.toString();
}

function changeDirection(direction) {
    const url = new URL(window.location.href);
    url.searchParams.set('direction', direction);
    url.searchParams.set('page', '0');
    window.location.href = url.toString();
}

// Filter movies in the table and cards
function filterMovies() {
    const filterValue = document.getElementById('filterInput').value.toLowerCase();
    const filterType = document.getElementById('filterType').value;
    currentFilter = filterValue;
    
    // Filter table rows (desktop)
    const tableRows = document.querySelectorAll('#moviesTable tbody tr.movie-row');
    tableRows.forEach(row => {
        let shouldShow = false;
        
        if (filterType === 'all') {
            // Search in all fields
            const text = row.textContent.toLowerCase();
            shouldShow = text.includes(filterValue);
        } else {
            // Search in specific field
            const cells = row.querySelectorAll('td');
            if (cells.length >= 6) {
                let cellText = '';
                switch(filterType) {
                    case 'id':
                        cellText = cells[0].textContent.toLowerCase();
                        break;
                    case 'year':
                        cellText = cells[1].textContent.toLowerCase();
                        break;
                    case 'title':
                        cellText = cells[2].textContent.toLowerCase();
                        break;
                    case 'studios':
                        cellText = cells[3].textContent.toLowerCase();
                        break;
                    case 'producers':
                        cellText = cells[4].textContent.toLowerCase();
                        break;
                }
                shouldShow = cellText.includes(filterValue);
            }
        }
        
        row.style.display = shouldShow ? '' : 'none';
    });
    
    // Filter cards (mobile)
    const cards = document.querySelectorAll('.movie-card.movie-row');
    cards.forEach(card => {
        let shouldShow = false;
        
        if (filterType === 'all') {
            // Search in all fields
            const text = card.textContent.toLowerCase();
            shouldShow = text.includes(filterValue);
        } else {
            // Search in specific field using data attributes
            let cardText = '';
            switch(filterType) {
                case 'id':
                    cardText = card.getAttribute('data-id')?.toLowerCase() || '';
                    break;
                case 'year':
                    cardText = card.getAttribute('data-year')?.toLowerCase() || '';
                    break;
                case 'title':
                    cardText = card.getAttribute('data-title')?.toLowerCase() || '';
                    break;
                case 'studios':
                    cardText = card.getAttribute('data-studios')?.toLowerCase() || '';
                    break;
                case 'producers':
                    cardText = card.getAttribute('data-producers')?.toLowerCase() || '';
                    break;
            }
            shouldShow = cardText.includes(filterValue);
        }
        
        card.style.display = shouldShow ? '' : 'none';
    });
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

// Initialize filter on page load
document.addEventListener('DOMContentLoaded', function() {
    const filterInput = document.getElementById('filterInput');
    if (filterInput && currentFilter) {
        filterInput.value = currentFilter;
        filterMovies();
    }
});
