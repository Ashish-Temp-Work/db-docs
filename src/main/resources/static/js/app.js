document.addEventListener('DOMContentLoaded', function() {
    initializeComponents();
});

function initializeComponents() {
    // Handle database type change to update the default port
    const dbTypeSelect = document.getElementById('databaseType');
    if (dbTypeSelect) {
        dbTypeSelect.addEventListener('change', function() {
            updateDefaultPort(this.value);
        });
        // Set initial port on page load if a type is already selected
        if (dbTypeSelect.value) {
            updateDefaultPort(dbTypeSelect.value);
        }
    }

    // Handle "Select All" checkbox functionality
    const selectAllCheckbox = document.getElementById('selectAll');
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', function() {
            toggleAllCheckboxes(this.checked);
        });
    }
}

function updateDefaultPort(databaseType) {
    if (!databaseType) return;

    fetch(`/connections/default-port/${databaseType}`)
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch port');
            return response.json();
        })
        .then(port => {
            const portInput = document.getElementById('port');
            if (portInput) {
                portInput.value = port;
            }
        })
        .catch(error => {
            console.error('Error fetching default port:', error);
            showAlert('Could not fetch default port.', 'danger');
        });
}

function testConnection() {
    const form = document.getElementById('connectionForm');
    const formData = new FormData(form);
    const connection = Object.fromEntries(formData.entries());

    showAlert('Testing connection...', 'info');

    fetch('/connections/test', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(connection)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showAlert('Connection successful!', 'success');
        } else {
            showAlert('Connection failed: ' + (data.message || 'Unknown error'), 'danger');
        }
    })
    .catch(error => {
        showAlert('Error testing connection: ' + error.message, 'danger');
    });
}

function toggleAllCheckboxes(checked) {
    document.querySelectorAll('input[name="selectedObjects"]').forEach(checkbox => {
        checkbox.checked = checked;
    });
}

function showAlert(message, type = 'info') {
    const container = document.getElementById('alert-container');
    if (!container) return;

    // Clear existing alerts
    container.innerHTML = '';

    const colors = {
        success: 'bg-green-100 border-green-400 text-green-700',
        danger: 'bg-red-100 border-red-400 text-red-700',
        info: 'bg-blue-100 border-blue-400 text-blue-700',
    };

    const alert = document.createElement('div');
    alert.className = `border px-4 py-3 rounded-md shadow-lg relative mb-4 ${colors[type]}`;
    alert.innerHTML = `<span>${message}</span>`;

    container.appendChild(alert);

    // Automatically remove the alert after 5 seconds
    setTimeout(() => alert.remove(), 5000);
}

function deleteConnection(id) {
    if (confirm('Are you sure you want to delete this connection? This action cannot be undone.')) {
        fetch(`/connections/${id}`, { method: 'DELETE' })
            .then(response => {
                if (response.ok) {
                    location.reload();
                } else {
                    showAlert('Error deleting connection.', 'danger');
                }
            })
            .catch(error => {
                showAlert('Error: ' + error.message, 'danger');
            });
    }
}