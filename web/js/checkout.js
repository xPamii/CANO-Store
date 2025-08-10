payhere.onCompleted = function onCompleted(orderId) {
    Swal.fire({
        title: "Success",
        text: "Payment completed. OrderID:" + orderId,
        icon: "success",
        confirmButtonColor: "#f0ad4e"
    });
};

// Payment window closed
payhere.onDismissed = function onDismissed() {
    // Note: Prompt user to pay again or show an error page
    console.log("Payment dismissed");
};

// Error occurred
payhere.onError = function onError(error) {
    // Note: show an error page
    console.log("Error:" + error);
};

async function loadCheckoutData() {
    const response = await fetch("LoadCheckOutData");
    if (response.ok) { //200
        const json = await response.json();
        if (json.status) {
            console.log(json);
            const userAddress = json.userAddress;
            const cityList = json.cityList;
            const cartItems = json.cartList;
            const deliveryTypes = json.deliveryTypes;

            // load citites
            let city_select = document.getElementById("city-select");

            cityList.forEach(city => {
                let option = document.createElement("option");
                option.value = city.id;
                option.innerHTML = city.name;
                city_select.appendChild(option);
            });

            // load current address
            const current_address_checkbox = document.getElementById("checkbox1");
            current_address_checkbox.addEventListener("change", function () {

                let firstName = document.getElementById("first-name");
                let lastName = document.getElementById("last-name");
                let line_one = document.getElementById("line-one");
                let line_two = document.getElementById("line-two");
                let postal_code = document.getElementById("postal-code");
                let mobile = document.getElementById("mobile");
                if (current_address_checkbox.checked) {
                    firstName.value = userAddress.user.first_name;
                    lastName.value = userAddress.user.last_name;
                    city_select.value = userAddress.city.id;
                    city_select.disabled = true;
                    city_select.dispatchEvent(new Event("change"));
                    line_one.value = userAddress.lineOne;
                    line_two.value = userAddress.lineTwo;
                    postal_code.value = userAddress.postalCode;
                    mobile.value = userAddress.mobile;
                } else {

                    firstName.value = userAddress.user.first_name;
                    lastName.value = userAddress.user.last_name;
                    city_select.value = 0;
                    city_select.disabled = false;
                    city_select.dispatchEvent(new Event("change"));
                    line_one.value = "";
                    line_two.value = "";
                    postal_code.value = "";
                    mobile.value = "";
                }
            });
// Get elements
            let productListContainer = document.getElementById("cart-product-list");
            productListContainer.innerHTML = ""; // Clear only the product list

            cartItems.forEach(cart => {
                let productDiv = document.createElement("div");
                productDiv.className = "flex-w flex-t p-b-13";

                let nameDiv = document.createElement("div");
                nameDiv.className = "size-209";
                let nameSpan = document.createElement("span");
                nameSpan.className = "mtext-110 cl2";
                nameSpan.textContent = cart.product.name || "Product";
                nameDiv.appendChild(nameSpan);

                let qtyDiv = document.createElement("div");
                qtyDiv.className = "size-208";
                qtyDiv.innerHTML = 'x &nbsp;<span class="mtext-110 cl2">' + cart.qty + "</span>";

                let priceDiv = document.createElement("div");
                priceDiv.className = "size-208";
//                let formattedPrice = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(cart.product.price * cart.qty);
//                priceDiv.innerHTML = 'LKR. <span class="mtext-110 cl2">' + formattedPrice + "</span>";

                productDiv.appendChild(nameDiv);
                productDiv.appendChild(qtyDiv);
//                productDiv.appendChild(priceDiv);

                productListContainer.appendChild(productDiv);
            });

// Summary elements
//            let st_product_price = document.getElementById("st-product-price");
            let st_product_total_amount = document.getElementById("st-product-total-amount");
            let st_product_shipping_charges = document.getElementById("st-product-shipping-charges");
            let st_discount_amount = document.getElementById("st-discount-amount");
            let st_order_total_amount = document.getElementById("st-order-total-amount");

// Calculate totals
            let total = 0;
            let item_count = 0;
            cartItems.forEach(cart => {
                item_count += Number(cart.qty);
                total += Number(cart.qty) * Number(cart.product.price);
            });

            let discountPercentage = 10; // 10%
            let totalDiscountAmount = (total * discountPercentage) / 100;
            let subtotalAfterDiscount = total - totalDiscountAmount;

// Initial shipping charges
            let shipping_charges = 500;

// Display initial summary values
//            st_product_price.innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(total); // showing total price before discount
            st_product_total_amount.innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(total);
            st_discount_amount.innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(totalDiscountAmount);
            st_product_shipping_charges.innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(shipping_charges);
            st_order_total_amount.innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(subtotalAfterDiscount + shipping_charges);

// Update shipping & total on city change
            city_select.addEventListener("change", () => {
                let cityName = city_select.options[city_select.selectedIndex].innerHTML;

                if (cityName === "Colombo") {
                    shipping_charges = item_count * deliveryTypes[0].price;
                } else {
                    shipping_charges = item_count * deliveryTypes[1].price;
                }

                st_product_shipping_charges.innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(shipping_charges);
                st_order_total_amount.innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(subtotalAfterDiscount + shipping_charges);
            });




        } else {
            if (json.message === "empty-cart") {
                Swal.fire({
                    title: "Error!",
                    text: "Empty cart. Please add some product.",
                    icon: "warning",
                    confirmButtonColor: "#f0ad4e"
                });
                window.location = "product.html";
            } else {
                console.log(log);
                console.log("okk");
                Swal.fire({
                    title: "Error!",
                    text: json.message || "Something went wrong. Please try again.",
                    icon: "warning",
                    confirmButtonColor: "#f0ad4e"
                });
            }
        }
    } else {
        if (response.status === 401) {
            window.location = "index.html";
        }
    }
}


async function checkout() {
    let checkbox1 = document.getElementById("checkbox1").checked;
    let first_name = document.getElementById("first-name");
    let last_name = document.getElementById("last-name");
    let city_select = document.getElementById("city-select");
    let line_one = document.getElementById("line-one");
    let line_two = document.getElementById("line-two");
    let postal_code = document.getElementById("postal-code");
    let mobile = document.getElementById("mobile");

    let data = {
        isCurrentAddress: checkbox1,
        firstName: first_name.value,
        lastName: last_name.value,
        citySelect: city_select.value,
        lineOne: line_one.value,
        lineTwo: line_two.value,
        postalCode: postal_code.value,
        mobile: mobile.value
    };
    let dataJSON = JSON.stringify(data);

    const response = await fetch("CheckOut", {
        method: "POST",
        header: {
            "Content-Type": "application/json"
        },
        body: dataJSON
    });
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);
            // PayHere Process
            payhere.startPayment(json.payhereJson);
        } else {
            Swal.fire({
                title: "Error!",
                text: json.message,
                icon: "warning",
                confirmButtonColor: "#f0ad4e"
            });
        }
    } else {
        Swal.fire({
            title: "Error!",
            text: json.message || "Something went wrong. Please try again.",
            icon: "warning",
            confirmButtonColor: "#f0ad4e"
        });
    }
}