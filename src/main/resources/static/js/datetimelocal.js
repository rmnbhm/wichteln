function needsFallback() {
    // Test whether a new datetime-local input falls back to a text input or not
    const test = document.createElement('input');

    try {
        test.type = 'datetime-local';
    } catch (e) {
        console.log(e.description);
    }
    return test.type === 'text';
}

if (needsFallback()) {
    const dateTimeInput = document.querySelectorAll('[type="datetime-local"]')[0];
    dateTimeInput.type = 'date';
    const previewButton = document.getElementById('preview-button');
    if (!!previewButton) {
        previewButton.onclick = function () {
            // Temporarily fall even further back to 'text' type so we may manipulate the value since an input element
            // of type 'date' would not allow a value like "2020-11-01T00:00".
            dateTimeInput.type = 'text';
            dateTimeInput.value = dateTimeInput.value + "T00:00";
        };
    }
    const submitButton = document.getElementById('submit-button');
    if (!!submitButton) {
        submitButton.onclick = function () {
            // Temporarily fall even further back to 'text' type so we may manipulate the value since an input element
            // of type 'date' would not allow a value like "2020-11-01T00:00".
            dateTimeInput.type = 'text';
        };
    }
}
