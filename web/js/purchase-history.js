document.addEventListener("DOMContentLoaded", function () {
    fetch("PurchaseHistory")
        .then(res => {
            if (res.status === 401) {
                // Session expired
                return res.json().then(data => {
                    Swal.fire({
                        title: "Authentication Error!",
                        text: data.message || "Login session expired! Please log in again.",
                        icon: "warning",
                        confirmButtonColor: "#f0ad4e"
                    }).then(() => {
                        window.location = "index.html";
                    });
                });
            }

            if (!res.ok) {
                throw new Error("Failed to load purchase history");
            }

            return res.json();
        })
        .then(data => {
            if (!data || !data.status) {
                console.error("Error fetching orders:", data?.error);
                return;
            }

            const tbody = document.getElementById("purchase-history-body");
            tbody.innerHTML = "";

            data.orders.forEach(order => {
                let badgeClass = "bg-secondary";
                const status = order.paymentStatus;

                switch (status) {
                    case "PAID": badgeClass = "bg-success"; break;
                    case "UNPAID": badgeClass = "bg-warning"; break;
                    case "CANCELLED": badgeClass = "bg-danger"; break;
                }

                const itemCount = order.items ? order.items.length : 0;

                const row = document.createElement("tr");
                row.classList.add("table_row");
                row.innerHTML = `
                    <td class="column-1">#000${order.orderId}</td>
                    <td class="column-2">${new Date(order.createdAt).toLocaleDateString()}</td>
                    <td class="column-3"><span class="badge ${badgeClass}">${status}</span></td>
                    <td class="column-4">${itemCount}</td>
                    <td class="column-5">LKR. ${order.grandTotal}</td>
                    <td class="column-6">
                        <button class="btn btn-primary btn-sm view-order-btn" data-order-id="${order.orderId}">
                            View Items
                        </button>
                    </td>
                `;
                tbody.appendChild(row);
            });

            // Add click listeners for "View Items" buttons
            document.querySelectorAll(".view-order-btn").forEach(btn => {
                btn.addEventListener("click", function () {
                    const orderId = this.getAttribute("data-order-id");
                    window.location.href = `order-details.html?orderId=${orderId}`;
                });
            });
        })
        .catch(err => {
            console.error("Fetch error:", err);
        });
});
