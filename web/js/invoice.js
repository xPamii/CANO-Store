const params = new URLSearchParams(window.location.search);
const orderId = params.get('orderId');
const formatter = new Intl.NumberFormat('en-LK', {style: 'currency', currency: 'LKR'});


fetch(`/CanoStore/InvoiceData?orderId=${orderId}`)
        .then(res => {
            if (!res.ok)
                throw new Error('Failed to fetch invoice data');
            return res.json();
        })
        .then(order => {
            // Customer info
            const user = order.user;
            const addr = order.address;
            document.getElementById('customerName').innerText = addr.firstName + " " + addr.lastName;
            document.getElementById('customerAddress').innerText = `${addr.lineOne}, ${addr.lineTwo}, ${addr.city.name}`;
            document.getElementById('orderId').innerText = order.orderId;
            document.getElementById('invoiceDate').innerText = new Date(order.createdAt).toLocaleDateString('en-LK', {day: 'numeric', month: 'short', year: 'numeric'});
            document.getElementById('orderStatus').innerText = order.orderStatus;


            const statusSpan = document.getElementById('orderStatus');
            let status = order.orderStatus || "Unknown";
            let badgeClass = "bg-secondary"; // default

            switch (status.toLowerCase()) {
                case "pending":
                    badgeClass = "bg-warning text-dark";
                    break;
                case "processing":
                    badgeClass = "bg-info text-dark";
                    break;
                case "shipped":
                    badgeClass = "bg-primary";
                    break;
                case "delivered":
                    badgeClass = "bg-success";
                    break;
                case "cancelled":
                case "rejected":
                    badgeClass = "bg-danger";
                    break;
            }

            statusSpan.innerHTML = `<span class="badge ${badgeClass}">${status}</span>`;


            const orderUrl = `http://${window.location.hostname}:8080/CanoStore/InvoiceData?orderId=${orderId}`;
            console.log("Order URL for QR:", orderUrl);

            const qrCodeImg = document.getElementById('qrCode');
            qrCodeImg.src = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(orderUrl)}`;
            qrCodeImg.alt = "QR Code for Order " + orderId;

            qrCodeImg.style.width = "100px";
            qrCodeImg.style.height = "100px";

//            // QR code
//            const orderUrl = `${window.location.origin}/CanoStore/InvoiceData?orderId=${order.orderId}`;
//            console.log("QR URL:", orderUrl); // check what URL is generated
//
//            document.getElementById('qrCode').src =
//                    `https://chart.googleapis.com/chart?cht=qr&chs=200x200&chl=${encodeURIComponent(orderUrl)}`;


            // Items
            let tbody = "";
            let delivery = 0;
            let discount = 0;
            let grandTotal = 0;

            order.items.forEach(item => {
                delivery = item.delivery;
                discount = item.discount;
                grandTotal += item.grandTotal;


                tbody += `<tr>
                <td>${item.productName}</td>
                <td><img src="${item.imageUrl}" alt="${item.productName}" class="product-img"></td>
                <td>${item.color}</td>
                <td>${item.size}</td>
                <td>${item.qty}</td>
                <td>${formatter.format(item.unitPrice)}</td>
                <td>${formatter.format(item.itemTotal)}</td>
            </tr>`;
            });
            document.getElementById('itemsBody').innerHTML = tbody;
            document.getElementById('delivery').innerText = formatter.format(delivery);
            document.getElementById('discount').innerText = formatter.format(discount);
            document.getElementById('grandTotal').innerText = formatter.format(grandTotal);
        })
        .catch(err => {
            console.error(err);
            document.getElementById('itemsBody').innerHTML = `<tr><td colspan="7" class="text-center text-danger">Failed to load invoice.</td></tr>`;
        });

// PDF download
document.getElementById('downloadBtn').addEventListener('click', () => {
    const element = document.getElementById('invoice');
    html2pdf().set({
        margin: [0.5, 0.5, 0.5, 0.5],
        filename: `invoice-${orderId}.pdf`,
        image: {type: 'jpeg', quality: 1},
        html2canvas: {scale: 3, dpi: 192, letterRendering: true},
        jsPDF: {unit: 'in', format: 'letter', orientation: 'portrait'}
    }).from(element).save();
});
