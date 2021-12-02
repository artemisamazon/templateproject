// Check to see if the user is logged in or not, and if not, relocate them back to
// login screen
window.addEventListener('load', async () => {

    let res = await fetch('http://localhost:8080/checkloginstatus', {
        credentials: 'include',
        method: 'GET'
    });

    if (res.status === 200) {
        let userObj = await res.json();

        if (userObj.userRole === 'associate') {
            window.location.href = 'associate-homepage.html';
        }
    } else if (res.status === 401) {
        window.location.href = 'index.html';
    }

    // If we make it past the authorization checks, call another function that will 
    // retrieve all assignments
    populateTableWithAssignments();

});

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

        // If the assignment has already been graded, display the grade and graderId
        let td6 = document.createElement('td');
        let td7 = document.createElement('td');
        if (assignment.graderId != 0) {
            td3.innerHTML = assignment.grade;
            td4.innerHTML = assignment.graderId;
        } else { // otherwise, display the below content
            td3.innerHTML = 'Not graded';
            td4.innerHTML = '-';

            // Main challenge here is linking each button with a particular parameter
            // (assignment id that we want to change the grade of)
            let gradeInput = document.createElement('input');
            gradeInput.setAttribute('type', 'number');

            let gradeButton = document.createElement('button');
            gradeButton.innerText = 'Grade';
            gradeButton.addEventListener('click', async () => {
               
                let res = await fetch(`http://localhost:8080/assignments/${assignment.id}/grade`, 
                    {
                        credentials: 'include',
                        method: 'PATCH',
                        body: JSON.stringify({
                            grade: gradeInput.value
                        })
                    });

                if (res.status === 200) {
                    populateTableWithAssignments(); // Calling the function to repopulate the entire
                    // table again
                }
            });

            td6.appendChild(gradeButton);
            td7.appendChild(gradeInput);
        }
 

        let td5 = document.createElement('td');
        td5.innerHTML = assignment.authorId;

        tr.appendChild(td1);
        tr.appendChild(td2);
        tr.appendChild(td3);
        tr.appendChild(td4);
        tr.appendChild(td5);
        tr.appendChild(td6);
        tr.appendChild(td7);

        tbodyElement.appendChild(tr);
    }
}


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