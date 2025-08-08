async function loadCartItems() {
    const response = await fetch("LoadCartItems");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const cart_item_container = document.getElementById("cart-item-container");
            cart_item_container.innerHTML = "";

            let total = 0;
            let subtotal = 0;
            let totalQty = 0;
            let discountPercentage = 10;
            let totalDiscountAmount = 0;

            json.cartItems.forEach(cart => {
                let productSubTotal = cart.product.price * cart.qty;
                let discountAmount = productSubTotal * discountPercentage / 100;
                let discountedTotal = productSubTotal - discountAmount;

                subtotal += productSubTotal;
                totalDiscountAmount += discountAmount;
                total += discountedTotal;
                totalQty += cart.qty;

                let tableData = `<tr class="table_row" id="cart-item-row">
        <td class="column-0">${cart.product.id}</td>
        <td class="column-1">
            <div class="how-itemcart1">
                <img src="product-images\\${cart.product.id}\\image1.png" alt="IMG">
            </div>
        </td>
        <td class="column-2">${cart.product.name} - ${cart.product.color.value} </td>
        <td class="column-3">LKR.
            <span>${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(cart.product.price)}</span>
        </td>
        <td class="column-4">${cart.qty}</td>
        <td class="column-5" id="item-total-1">LKR.
            <span>${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(productSubTotal)}</span>
        </td>
    </tr>`;
                cart_item_container.innerHTML += tableData;
            });

            document.getElementById("order-total-quantity").innerHTML = totalQty;
            document.getElementById("subtotal-amount").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(subtotal);
            document.getElementById("discount").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(totalDiscountAmount);
            document.getElementById("total-discounted-amount").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(total);


        } else {
            Swal.fire({
                title: "Error!",
                text: json.message || "Something went wrong. Please try again.",
                icon: "warning",
                confirmButtonColor: "#f0ad4e"
            });
        }
    } else {
        Swal.fire({
            title: "Error!",
            text: "Cart Items loading failed...",
            icon: "warning",
            confirmButtonColor: "#f0ad4e"
        });
    }
}