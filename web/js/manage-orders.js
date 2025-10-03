function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('mainContent');
    sidebar.classList.toggle('expanded');
    mainContent.classList.toggle('shifted');
}

function logout() {
    Swal.fire({
        title: 'Are you sure you want to logout?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, logout',
        cancelButtonText: 'Cancel',
        background: '#2b2b2b',
        color: '#fff'
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = 'admin-login.html';
        }
    });
}

function getStatusBadge(status) {
    const badges = {
        pending: '<span class="badge badge-pending"><i class="fas fa-clock me-1"></i>Pending</span>',
        processing: '<span class="badge badge-processing"><i class="fas fa-cog me-1"></i>Processing</span>',
        shipped: '<span class="badge badge-shipped"><i class="fas fa-truck me-1"></i>Shipped</span>',
        delivered: '<span class="badge badge-delivered"><i class="fas fa-check-circle me-1"></i>Delivered</span>',
        cancelled: '<span class="badge badge-cancelled"><i class="fas fa-times-circle me-1"></i>Cancelled</span>'
    };
    return badges[status] || status;
}

async function loadOrders() {
    const tbody = document.getElementById('ordersTable');

    try {
        // ✅ Fetch data from your servlet
        const res = await fetch("LoadAllOrderData");
        const data = await res.json();

        if (!data.status || !data.orders || data.orders.length === 0) {
            tbody.innerHTML = `
                <tr>
                  <td colspan="8" class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <p>No orders found</p>
                  </td>
                </tr>
            `;
            return;
        }

        // ✅ Render rows dynamically
        tbody.innerHTML = data.orders.map(order => {
            const itemCount = order.items ? order.items.length : 0;

            return `
      <tr>
        <td><strong>#000${order.orderId}</strong></td>
        <td>${order.customerName || '-'}</td>
        <td>${order.customerEmail || '-'}</td>
        <td>${order.createdAt || '-'}</td>
        <td>${itemCount} items</td>
        <td><strong>${order.grandTotal.toLocaleString()}</strong></td>
        <td>${getStatusBadge(order.paymentStatus)}</td>
        <td>
          <div class="action-buttons">
            <button class="btn btn-sm btn-info" onclick="viewOrder('${order.orderId}')">
              <i class="fas fa-eye"></i>
            </button>
            <button class="btn btn-sm btn-success" onclick="updateStatus('${order.orderId}')">
              <i class="fas fa-edit"></i>
            </button>
            <button class="btn btn-sm btn-danger" onclick="deleteOrder('${order.orderId}')">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </td>
      </tr>
    `;
        }).join('');



    } catch (err) {
        console.error("Failed to load orders", err);
        tbody.innerHTML = `
            <tr>
              <td colspan="8" class="empty-state">
                <i class="fas fa-exclamation-triangle"></i>
                <p>Error loading orders</p>
              </td>
            </tr>
        `;
    }
}

function viewOrder(orderId) {
    window.location.href = `view-orders-details.html?orderId=${orderId}`;
}


//function viewOrder(orderId) {
//
//    window.location.href = `view-orders-details.html?orderId=${orderId}`;
////    Swal.fire({
////        title: 'Order Details',
////        html: `<p>Viewing details for order: <strong>${orderId}</strong></p>`,
////        icon: 'info',
////        background: '#2b2b2b',
////        color: '#fff'
////    });
//}

function updateStatus(orderId) {
    Swal.fire({
        title: 'Update Order Status',
        input: 'select',
        inputOptions: {
            pending: 'Pending',
            processing: 'Processing',
            shipped: 'Shipped',
            delivered: 'Delivered',
            cancelled: 'Cancelled'
        },
        inputPlaceholder: 'Select status',
        showCancelButton: true,
        background: '#000000',
        color: '#ffffff',
        confirmButtonColor: '#4ade80'
    }).then((result) => {
        if (result.isConfirmed) {
            Swal.fire({
                title: 'Success!',
                text: 'Order status updated',
                icon: 'success',
                background: '#000000',
                color: '#ffffff',
                confirmButtonColor: '#4ade80'
            });
        }
    });
}

function deleteOrder(orderId) {
    Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        background: '#2b2b2b',
        color: '#fff',
        confirmButtonColor: '#ef4444',
        cancelButtonColor: '#6b7280',
        confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
        if (result.isConfirmed) {
            Swal.fire({
                title: 'Deleted!',
                text: 'Order has been deleted.',
                icon: 'success',
                background: '#2b2b2b',
                color: '#fff',
                confirmButtonColor: '#4ade80'
            });
        }
    });
}

function applyFilters() {
    const search = document.getElementById('searchOrder').value;
    const status = document.getElementById('statusFilter').value;
    const date = document.getElementById('dateFilter').value;

    console.log('Applying filters:', {search, status, date});
    // Add your filter logic here

    Swal.fire({
        title: 'Filters Applied',
        text: 'Orders have been filtered',
        icon: 'success',
        timer: 1500,
        showConfirmButton: false,
        background: '#2b2b2b',
        color: '#fff'
    });
}

// Load orders on page load
window.addEventListener('load', loadOrders);