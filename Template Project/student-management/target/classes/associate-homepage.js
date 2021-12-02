// Initial page load
window.addEventListener('load', async () => {

    let res = await fetch('http://localhost:8080/checkloginstatus', {
        credentials: 'include',
        method: 'GET'
    });

    if (res.status === 200) {
        let userObj = await res.json();

        if (userObj.userRole === 'trainer') {
            window.location.href = 'trainer-homepage.html';
        }
    } else if (res.status === 401) {
        window.location.href = 'index.html';
    }

    populateTableWithAssignments();
});

// Logout btn
let logoutBtn = document.querySelector('#logout');

logoutBtn.addEventListener('click', async () => {

    let res = await fetch('http://localhost:8080/logout', {
        'method': 'POST',
        'credentials': 'include'
    });

    if (res.status === 200) {
        window.location.href = 'index.html';
    }

})


async function populateTableWithAssignments() {
    let res = await fetch('http://localhost:8080/assignments', {
        credentials: 'include',
        method: 'GET'
    });

    let tbodyElement = document.querySelector("#assignment-table tbody");
    tbodyElement.innerHTML = '';
    let assignmentArray =  await res.json();

    for (let i = 0; i < assignmentArray.length; i++) {
        let assignment = assignmentArray[i];

        let tr = document.createElement('tr');

        let td1 = document.createElement('td');
        td1.innerHTML = assignment.id;

        let td2 = document.createElement('td');
        td2.innerHTML = assignment.assignmentName;


        let td3 = document.createElement('td'); // grade
        let td4 = document.createElement('td'); // grader id

        if (assignment.graderId != 0) {
            td3.innerHTML = assignment.grade;
            td4.innerHTML = assignment.graderId;
        } else {
            td3.innerHTML = 'Not graded';
            td4.innerHTML = '-';
        }
 

        let td5 = document.createElement('td');
        td5.innerHTML = assignment.authorId;

        let td6 = document.createElement('td');
        let viewImageButton = document.createElement('button');
        viewImageButton.innerHTML = 'View Image';
        td6.appendChild(viewImageButton);

        viewImageButton.addEventListener('click', async () => {
            // Add the is-active class to the modal (so that the modal appears)
            // inside of the modal on div.modal-content (div w/ class modal-content)
            //  -> img tag with src="http://localhost:8080/assignments/{id}/image"
            let assignmentImageModal = document.querySelector('#assignment-image-modal');

            // Close button
            let modalCloseElement = assignmentImageModal.querySelector('button');
            modalCloseElement.addEventListener('click', () => {
                assignmentImageModal.classList.remove('is-active');
            });

            // you can take an element and use querySelector on it to find the child elements
            // that are nested within it
            let modalContentElement = assignmentImageModal.querySelector('.modal-content');
            modalContentElement.innerHTML = '';

            let imageElement = document.createElement('img');
            imageElement.setAttribute('src', `http://localhost:8080/assignments/${assignment.id}/image`);
            modalContentElement.appendChild(imageElement);

            assignmentImageModal.classList.add('is-active'); // add a class to the modal element to have it display
        });

        tr.appendChild(td1);
        tr.appendChild(td2);
        tr.appendChild(td3);
        tr.appendChild(td4);
        tr.appendChild(td5);
        tr.appendChild(td6);

        tbodyElement.appendChild(tr);
    }
}

// Submitting assignment
let assignmentSubmitButton = document.querySelector('#submit-assignment-btn');

assignmentSubmitButton.addEventListener('click', submitAssignment);

async function submitAssignment() {

    let assignmentNameInput = document.querySelector('#assignment-name');
    let assignmentImageInput = document.querySelector('#assignment-file');

    const file = assignmentImageInput.files[0];

    let formData = new FormData();
    formData.append('assignment_name', assignmentNameInput.value);
    formData.append('assignment_image', file);

    let res = await fetch('http://localhost:8080/assignments', {
        method: 'POST',
        credentials: 'include',
        body: formData
    });

    if (res.status === 201) { // If we successfully added an assignment
        populateTableWithAssignments(); // Refresh the table of assignments
    } else if (res.status === 400) {
        let assignmentForm = document.querySelector('#assignment-submit-form');

        let data = await res.json();

        let pTag = document.createElement('p');
        pTag.innerHTML = data.message;
        pTag.style.color = 'red';

        assignmentForm.appendChild(pTag);
    }
}