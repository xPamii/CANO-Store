// PayHere callbacks
payhere.onCompleted = function (orderId) {
    Swal.fire({
        title: "Success",
        text: "Payment completed. Order ID: " + orderId,
        icon: "success",
        confirmButtonColor: "#f0ad4e"
    });
};

payhere.onDismissed = function () {
    console.log("Payment dismissed by user.");
};

payhere.onError = function (error) {
    console.log("Payment error: " + error);
};

// Load checkout data
async function loadCheckoutData() {
    try {
        const response = await fetch("LoadCheckOutData");
        if (!response.ok) {
            if (response.status === 401)
                window.location = "index.html";
            return;
        }
        const json = await response.json();
        if (!json.status) {
            if (json.message === "empty-cart") {
                Swal.fire({
                    title: "Error!",
                    text: "Empty cart. Please add some products.",
                    icon: "warning",
                    confirmButtonColor: "#f0ad4e"
                });
                window.location = "product.html";
            }
            return;
        }

        const {userAddress, cityList, cartList, deliveryTypes} = json;

        // Load cities
        const citySelect = document.getElementById("city-select");
        citySelect.innerHTML = "";
        cityList.forEach(city => {
            const option = document.createElement("option");
            option.value = city.id;
            option.textContent = city.name;
            citySelect.appendChild(option);
        });

        // Handle current address checkbox
        document.getElementById("checkbox1").addEventListener("change", () => {
            const firstName = document.getElementById("first-name");
            const lastName = document.getElementById("last-name");
            const lineOne = document.getElementById("line-one");
            const lineTwo = document.getElementById("line-two");
            const postalCode = document.getElementById("postal-code");
            const mobile = document.getElementById("mobile");

            if (document.getElementById("checkbox1").checked) {
                firstName.value = userAddress.user.first_name;
                lastName.value = userAddress.user.last_name;
                citySelect.value = userAddress.city.id;
                citySelect.disabled = true;
                lineOne.value = userAddress.lineOne;
                lineTwo.value = userAddress.lineTwo;
                postalCode.value = userAddress.postalCode;
                mobile.value = userAddress.mobile;
            } else {
                firstName.value = userAddress.user.first_name;
                lastName.value = userAddress.user.last_name;
                citySelect.value = 0;
                citySelect.disabled = false;
                lineOne.value = "";
                lineTwo.value = "";
                postalCode.value = "";
                mobile.value = "";
            }
        });

        // Load cart items
        const productListContainer = document.getElementById("cart-product-list");
        productListContainer.innerHTML = "";
        let total = 0, itemCount = 0;

        cartList.forEach(cart => {
            itemCount += Number(cart.qty);
            total += Number(cart.qty) * Number(cart.product.price);

            const productDiv = document.createElement("div");
            productDiv.className = "flex-w flex-t p-b-13";

            const nameDiv = document.createElement("div");
            nameDiv.className = "size-209";
            const nameSpan = document.createElement("span");
            nameSpan.className = "mtext-110 cl2";
            nameSpan.textContent = cart.product.name || "Product";
            nameDiv.appendChild(nameSpan);

            const qtyDiv = document.createElement("div");
            qtyDiv.className = "size-208";
            qtyDiv.innerHTML = `x &nbsp;<span class="mtext-110 cl2">${cart.qty}</span>`;

            productDiv.appendChild(nameDiv);
            productDiv.appendChild(qtyDiv);

            productListContainer.appendChild(productDiv);
        });

        // Calculate totals
        const discountPercentage = 10;
        const discountAmount = (total * discountPercentage) / 100;
        let subtotalAfterDiscount = total - discountAmount;
        let shippingCharges = 500;

        const stProductTotal = document.getElementById("st-product-total-amount");
        const stDiscount = document.getElementById("st-discount-amount");
        const stShipping = document.getElementById("st-product-shipping-charges");
        const stOrderTotal = document.getElementById("st-order-total-amount");

        stProductTotal.textContent = total.toFixed(2);
        stDiscount.textContent = discountAmount.toFixed(2);
        stShipping.textContent = shippingCharges.toFixed(2);
        stOrderTotal.textContent = (subtotalAfterDiscount + shippingCharges).toFixed(2);

        // Update shipping on city change
        citySelect.addEventListener("change", () => {
            const cityName = citySelect.options[citySelect.selectedIndex].textContent;
            shippingCharges = (cityName === "Colombo") ? itemCount * deliveryTypes[0].price : itemCount * deliveryTypes[1].price;
            stShipping.textContent = shippingCharges.toFixed(2);
            stOrderTotal.textContent = (subtotalAfterDiscount + shippingCharges).toFixed(2);
        });

    } catch (err) {
        console.error("Checkout load error:", err);
        Swal.fire({
            title: "Error!",
            text: "Failed to load checkout data.",
            icon: "error",
            confirmButtonColor: "#f0ad4e"
        });
    }
}

// Checkout function
async function checkout() {
    const data = {
        isCurrentAddress: document.getElementById("checkbox1").checked,
        firstName: document.getElementById("first-name").value,
        lastName: document.getElementById("last-name").value,
        citySelect: document.getElementById("city-select").value,
        lineOne: document.getElementById("line-one").value,
        lineTwo: document.getElementById("line-two").value,
        postalCode: document.getElementById("postal-code").value,
        mobile: document.getElementById("mobile").value
    };

    try {
        const response = await fetch("CheckOut", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(data)
        });

        const json = await response.json();
        if (!json.status) {
            Swal.fire({
                title: "Error!",
                text: json.message || "Something went wrong.",
                icon: "warning",
                confirmButtonColor: "#f0ad4e"
            });
            return;
        }

        // Start PayHere payment
        payhere.startPayment(json.payhereJson);

    } catch (err) {
        console.error("Checkout error:", err);
        Swal.fire({
            title: "Error!",
            text: "Failed to process checkout.",
            icon: "error",
            confirmButtonColor: "#f0ad4e"
        });
    }
}

// Initialize
document.addEventListener("DOMContentLoaded", loadCheckoutData);
