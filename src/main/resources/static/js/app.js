document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips and other components
    initializeComponents();
});

function initializeComponents() {
    // Initialize database type change handler
    const dbTypeSelect = document.getElementById('databaseType');
    if (dbTypeSelect) {
        dbTypeSelect.addEventListener('change', function() {
            updateDefaultPort(this.value);
        });
    }

    // Initialize form validation
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!validateForm(this)) {
                e.preventDefault();
            }
        });
    });

    // Initialize select all checkbox
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
        .then(response => response.json())
        .then(port => {
            const portInput = document.getElementById('port');
            if (portInput) {
                portInput.value = port;
            }
        })
        .catch(error => {
            console.error('Error fetching default port:', error);
        });
}

function testConnection() {
    const form = document.getElementById('connectionForm');
    const formData = new FormData(form);

    const connection = {
        databaseType: formData.get('databaseType'),
        host: formData.get('host'),
        port: parseInt(formData.get('port')),
        databaseName: formData.get('databaseName'),
        schema: formData.get('schema'),
        username: formData.get('username'),
        password: formData.get('password')
    };

    // Show loading spinner
    showLoadingSpinner('Testing connection...');

    fetch('/connections/test', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(connection)
    })
    .then(response => response.json())
    .then(data => {
        hideLoadingSpinner();
        if (data.success) {
            showAlert('Connection successful!', 'success');
        } else {
            showAlert('Connection failed: ' + data.message, 'danger');
        }
    })
    .catch(error => {
        hideLoadingSpinner();
        showAlert('Error testing connection: ' + error.message, 'danger');
    });
}

function validateForm(form) {
    const requiredFields = form.querySelectorAll('[required]');
    let isValid = true;

    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            field.style.borderColor = '#dc3545';
            isValid = false;
        } else {
            field.style.borderColor = '#e0e0e0';
        }
    });

    return isValid;
}

function toggleAllCheckboxes(checked) {
    const checkboxes = document.querySelectorAll('input[name="selectedObjects"]');
    checkboxes.forEach(checkbox => {
        checkbox.checked = checked;
    });
}

function showAlert(message, type) {
    // Remove existing alerts
    const existingAlerts = document.querySelectorAll('.alert');
    existingAlerts.forEach(alert => alert.remove());

    // Create new alert
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;

    // Insert at the top of the main content
    const container = document.querySelector('.container');
    if (container) {
        container.insertBefore(alert, container.firstChild);
    }

    // Auto remove after 5 seconds
    setTimeout(() => {
        alert.remove();
    }, 5000);
}

function showLoadingSpinner(message) {
    const spinner = document.createElement('div');
    spinner.id = 'loadingSpinner';
    spinner.innerHTML = `
        <div style="position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 9999; display: flex; align-items: center; justify-content: center;">
            <div style="background: white; padding: 2rem; border-radius: 8px; text-align: center;">
                <div class="spinner"></div>
                <p>${message}</p>
            </div>
        </div>
    `;
    document.body.appendChild(spinner);
}

function hideLoadingSpinner() {
    const spinner = document.getElementById('loadingSpinner');
    if (spinner) {
        spinner.remove();
    }
}

function deleteConnection(id) {
    if (confirm('Are you sure you want to delete this connection?')) {
        fetch(`/connections/${id}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                showAlert('Error deleting connection', 'danger');
            }
        })
        .catch(error => {
            showAlert('Error deleting connection: ' + error.message, 'danger');
        });
    }
}