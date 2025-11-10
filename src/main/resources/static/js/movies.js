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

