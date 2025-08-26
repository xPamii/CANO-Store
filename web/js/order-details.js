document.addEventListener("DOMContentLoaded", function () {
    const params = new URLSearchParams(window.location.search);
    const orderId = params.get("orderId");
    if (!orderId)
        return;

    fetch(`PurchasedOrderDetails?orderId=${orderId}`)
            .then(res => {
                if (!res.ok)
                    throw new Error("Failed to fetch order details");
                return res.json();
            })
            .then(data => {
                if (!data.status) {
                    console.error("Error fetching order:", data.error);
                    return;
                }

                const order = data.order;

                const bor10 = document.querySelector(".bor10");
                if (bor10) {
                    // Left Column
                    const leftCol = bor10.querySelector(".col-md-6:first-child");
                    if (leftCol) {
                        const orderIdElem = leftCol.querySelector("p:nth-child(1) strong");
                        if (orderIdElem)
                            orderIdElem.textContent = `#000${order.id}`;

                        const dateElem = leftCol.querySelector("p:nth-child(2)");
                        if (dateElem)
                            dateElem.innerHTML = `<strong>Date:</strong> ${formatDate(order.createdAt)}`;

                        const statusElem = leftCol.querySelector("p:nth-child(3)");
                        if (statusElem)
                            statusElem.innerHTML = `<strong>Status:</strong> <span class="badge ${getBadgeClass(order.orderStatus)}">${order.orderStatus || "Unknown"}</span>`;

                        const paymentStatusElem = leftCol.querySelector("p:nth-child(4)");
                        if (paymentStatusElem)
                            paymentStatusElem.innerHTML = `<strong>Payment Status:</strong> <span class="badge ${getBadgeClass(order.paymentStatus)}">${order.paymentStatus || "Unknown"}</span>`;

                        const paymentMethodElem = leftCol.querySelector("p:nth-child(5)");
                        if (paymentMethodElem)
                            paymentMethodElem.innerHTML = `<strong>Payment Method:</strong> ${order.paymentMethod || "Card"}`;
                    }

                    // Right Column
                    const rightCol = bor10.querySelector(".col-md-6:last-child");
                    if (rightCol) {
                        const shipToElem = rightCol.querySelector("p:nth-child(1)");
                        if (shipToElem)
                            shipToElem.innerHTML = `<strong>Ship To:</strong> ${order.customerName}`;

                        const addressElem = rightCol.querySelector("p:nth-child(2)");
                        if (addressElem)
                            addressElem.innerHTML = `<strong>Shipping Address:</strong> ${formatAddress(order.address)}`;

                        const mobileElem = rightCol.querySelector("p:nth-child(3)");
                        if (mobileElem)
                            mobileElem.innerHTML = `<strong>Mobile:</strong> ${order.mobile || "N/A"}`;
                    }
                }


                // ✅ Order Items Table
                const tbody = document.querySelector(".table-shopping-cart tbody");
                if (tbody) {
                    tbody.innerHTML = "";
                    order.items?.forEach(item => {
                        const tr = document.createElement("tr");
                        tr.classList.add("table_row");
                        tr.innerHTML = `
                        <td class="column-1">
                            <div class="how-itemcart1">
                                <img src="product-images/${item.product?.id || 0}/image1.png" alt="IMG">
                            </div>
                        </td>
                        <td class="column-2">${item.product?.name || "N/A"}</td>
                        <td class="column-3">LKR. ${(item.itemSubtotal / item.qty).toFixed(2)}</td>
                        <td class="column-4">${item.qty}</td>
                        <td class="column-5">LKR. ${item.itemSubtotal.toFixed(2)}</td>
                    `;
                        tbody.appendChild(tr);
                    });
                }

                // ✅ Order Summary
                const subTotalElem = document.querySelector(".size-209:nth-child(2) span");
                if (subTotalElem)
                    subTotalElem.textContent = `LKR. ${order.subTotal?.toFixed(2) || "0.00"}`;

                const discountElem = document.querySelector(".size-209:nth-child(4) span");
                if (discountElem)
                    discountElem.textContent = `LKR. ${order.discount?.toFixed(2) || "0.00"}`;

                const grandTotalElem = document.querySelector(".size-209.p-t-1 span");
                if (grandTotalElem)
                    grandTotalElem.textContent = `LKR. ${order.grandTotal?.toFixed(2) || "0.00"}`;
            })
            .catch(err => console.error(err));

    // Helpers
    function getBadgeClass(status) {
        switch ((status || "").toUpperCase()) {
            case "PAID":
                return "bg-success";
            case "UNPAID":
                return "bg-warning";
            case "CANCELLED":
                return "bg-danger";
            default:
                return "bg-primary";
        }
    }

    function formatAddress(address) {
        if (!address)
            return "N/A";
        const parts = [
            address.lineOne,
            address.lineTwo,
            address.city?.name,
            address.postalCode
        ].filter(Boolean);
        return parts.join(", ");
    }

    function formatDate(dateStr) {
        if (!dateStr)
            return "N/A";
        const date = new Date(dateStr);
        return isNaN(date.getTime()) ? dateStr : date.toLocaleDateString();
    }
});
