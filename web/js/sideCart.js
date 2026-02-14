async function loadCartSidebar() {
  
  const response = await fetch("LoadSideCartItems");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            
            // Container for items
            const cartContainer = document.getElementById("side-cart-item-container");
            cartContainer.innerHTML = "";

            // Totals
            let total = 0;
            let totalQty = 0;


            json.cartItems.forEach(cart => {
                let productSubTotal = cart.product.price * cart.qty;
                total += productSubTotal;
                totalQty += cart.qty;
                const li = `
                <li class="header-cart-item flex-w flex-t m-b-12">
                    <div class="header-cart-item-img">
                        <img src="product-images/${cart.product.id}/image1.png" alt="IMG">
                    </div>
                    <div class="header-cart-item-txt p-t-8">
                        <a href="#" class="header-cart-item-name m-b-18 hov-cl1 trans-04">
                            ${cart.product.name} - ${cart.product.color.value}
                        </a>
                        <span class="header-cart-item-info">
                            ${cart.qty} x LKR ${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(cart.product.price)}
                        </span>
                    </div>
                </li>
            `;
                cartContainer.innerHTML += li;
            });
            // Update total in sidebar
            document.getElementById("order-total-amount").innerHTML =
                    new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(total);


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

//function openCart() {
//    document.getElementById("cart-panel").classList.add("show-cart");
//    loadCartSidebar(); // fetch and refresh data
//}
//
//// Close cart function stays the same
//function closeCart() {
//    document.getElementById("cart-panel").classList.remove("show-cart");
//}
//
//// Attach event listener so clicking icon opens and loads
//document.getElementById("cart-icon-btn").addEventListener("click", openCart);
