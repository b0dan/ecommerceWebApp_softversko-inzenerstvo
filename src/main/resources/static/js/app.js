function clearFileInput() {
    const fileInput = document.getElementById('uploadedImages');
    fileInput.value = '';
}

function filterByCategory() {
    const category1 = document.getElementById('vbtn-radio1');
    const category2 = document.getElementById('vbtn-radio2');
    const category3 = document.getElementById('vbtn-radio3');
    const category4 = document.getElementById('vbtn-radio4');
    const category5 = document.getElementById('vbtn-radio5');

    let targetUrl;
    if (category1.checked) {
        targetUrl = category1.getAttribute('data-url');
    } else if (category2.checked) {
        targetUrl = category2.getAttribute('data-url');
    } else if (category3.checked) {
        targetUrl = category3.getAttribute('data-url');
    } else if (category4.checked) {
        targetUrl = category4.getAttribute('data-url');
    } else if (category5.checked) {
        targetUrl = category5.getAttribute('data-url');
    }
    window.location.href = targetUrl;
}

function updateURL(queryParam) {
    const currentURL = new URL(window.location.href);
    const params = new URLSearchParams(currentURL.search);

    if (params.has('page')) {
        params.delete('page');
    }

    if (params.has('sort')) {
        params.set('sort', queryParam);
    } else {
        params.append('sort', queryParam);
    }
    currentURL.search = params.toString();

    window.location.href = currentURL.toString();
}

function validatePriceInput(input, elementId) {
    const existingAlert = document.querySelector(".alert");
    if (existingAlert) {
        existingAlert.remove();
    }

    if (!/^\d+(\.\d*)?$/.test(input.value)) {
        const alertDiv = document.createElement("div");
        alertDiv.className = "alert";
        alertDiv.textContent = "Only digits and one dot are allowed!";

        alertDiv.style.color = "#FF0000";
        alertDiv.style.marginTop = "0";
        alertDiv.style.marginBottom = "0";
        alertDiv.style.display = "block";

        const field = document.getElementById(elementId);

        if (field.parentNode.classList.contains("input-group")) {
            field.parentNode.parentNode.insertBefore(alertDiv, field.parentNode.nextSibling);
        } else {
            field.parentNode.insertBefore(alertDiv, field.nextSibling);
        }

        input.value = input.value.replace(/[^\d.]|\.(?=.*\.)/g, '');
    }
}

function validatePhoneInput(input, elementId) {
    const existingAlert = document.querySelector(".alert");
    if (existingAlert) {
        existingAlert.remove();
    }

    if (/^[a-zA-Z]+$/.test(input.value)) {
        const alertDiv = document.createElement("div");
        alertDiv.className = "alert";
        alertDiv.textContent = "Letters are not allowed!";

        alertDiv.style.color = "#FF0000";
        alertDiv.style.marginTop = "0";
        alertDiv.style.marginBottom = "0";
        alertDiv.style.paddingTop = "0";
        alertDiv.style.paddingBottom = "0";
        alertDiv.style.display = "block";

        const field = document.getElementById(elementId);

        if (field.parentNode.classList.contains("input-group")) {
            field.parentNode.parentNode.insertBefore(alertDiv, field.parentNode.nextSibling);
        } else {
            field.parentNode.insertBefore(alertDiv, field.nextSibling);
        }

        input.value = input.value.replace(/[^0-9.]/g, '');
    }
}

function toggleActive(clickedLink) {
    var isActive = clickedLink.classList.contains('active');

    document.getElementById('navbar-example3').querySelectorAll('.nav-link').forEach(function (link) {
        link.classList.remove('active');
    });

    if (!isActive) {
        clickedLink.classList.add('active');
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
});