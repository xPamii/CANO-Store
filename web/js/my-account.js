function loadData() {
    getUserData();
    getCityData();
}

async function getCityData() {
    const response = await fetch("CityData");

    if (response.ok) {

        const json = await response.json();
        const citySelect = document.getElementById("citySelect");

        json.forEach(city => {
            let option = document.createElement("option");
            option.innerHTML = city.name;
            option.value = city.id;
            citySelect.appendChild(option);
        });
    } else {
        console.log("City Data Fetching Failed");
    }
}


async function getUserData() {

    const response = await fetch("UserAccountDetails");

    if (response.ok) {

        const json = await response.json();
        console.log(json);
        document.getElementById("firstName").value = json.firstName;
        document.getElementById("lastName").value = json.lastName;
        document.getElementById("email").value = json.email;
        document.getElementById("currentPassword").value = json.password;

        const addressContainer = document.getElementById("addressContainer");
        addressContainer.innerHTML = ''; // Clear any existing content

        json.addressList.forEach(address => {
            const card = document.createElement("div");
            card.className = "card shadow-sm border-0 mb-3";

            card.innerHTML = `
                <div class="card-body">
                    <h5 class="card-title mb-3">Shipping Address</h5>
                    <p class="mb-1">${address.lineOne},</p>
                    <p class="mb-1">${address.lineTwo}.</p>
                    <p class="mb-1">${address.city.name}</p>
                    <p class="mb-0">Postal Code: ${address.postalCode}</p>
                </div>
            `;

            addressContainer.appendChild(card);
        });

    } else {
        console.log("User data fetching failed");
    }
}

async function saveChanges() {
    const lineOne = document.getElementById("lineOne").value;
    const lineTwo = document.getElementById("lineTwo").value;
    const postalCode = document.getElementById("postalCode").value;
    const cityId = document.getElementById("citySelect").value;
    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    alert(cityId);
    const userDataObject = {
        lineOne: lineOne,
        lineTwo: lineTwo,
        postalCode: postalCode,
        cityId: cityId,
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword
    };

    const userDataJSON = JSON.stringify(userDataObject);

    const response = await fetch("UserAccountDetails", {
        method: "PUT",
        headers: {
            "Conetent-Type": "application/json"
        },
        body: userDataJSON
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            getUserData();
        } else {
            document.getElementById("message").innerHTML = json.message;
        }
    } else {

    }
}

async function updatePassword() {
    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    const passwordData = {
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword
    };

    const response = await fetch("UpdatePassword", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(passwordData)
    });

    const json = await response.json();
    Toastify({
        text: json.message,
        duration: 3000,
        close: true,
        gravity: "top",
        position: "right",
        backgroundColor: "#dc3545",
    }).showToast();
}

async function addAddress() {
    const lineOne = document.getElementById("lineOne").value;
    const lineTwo = document.getElementById("lineTwo").value;
    const postalCode = document.getElementById("postalCode").value;
    const cityId = document.getElementById("citySelect").value;

    const addressData = {
        lineOne: lineOne,
        lineTwo: lineTwo,
        postalCode: postalCode,
        cityId: cityId
    };

    const response = await fetch("AddAddress", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(addressData)
    });

    const json = await response.json();
        Swal.fire({
            title: "Error!",
            text: json.message,
            icon: "warning",
            confirmButtonColor: "#f0ad4e"
        });
}