// Save Product
//async function saveProduct() {
//    const title = document.getElementById("productTitle").value;
//    const quantity = document.getElementById("productQty").value;
//    const price = document.getElementById("productPrice").value;
//    const description = document.getElementById("productDesc").value;
//    const weight = document.getElementById("productWeight").value;
//    const dimension = document.getElementById("productDimension").value;
//    const material = document.getElementById("productMaterial").value;
//    const color = document.getElementById("productColor").value;
//    const category = document.getElementById("productCategory").value;
//    const type = document.getElementById("productType").value;
//    const size = document.getElementById("productSize").value;
//
//
//    const image1 = document.getElementById("productImage1").files[0];
//    const image2 = document.getElementById("productImage2").files[0];
//    const image3 = document.getElementById("productImage3").files[0];
//
//    const form = new FormData();
//    form.append("title", title);
//    form.append("quantity", quantity);
//    form.append("price", price);
//    form.append("description", description);
//    form.append("weight", weight);
//    form.append("dimension", dimension);
//    form.append("material", material);
//    form.append("color", color);
//    form.append("category", category);
//    form.append("type", type);
//    form.append("size", size);
//
//    if (image1)
//        form.append("image1", image1);
//    if (image2)
//        form.append("image2", image2);
//    if (image3)
//        form.append("image3", image3);
//
//    try {
//        const response = await fetch("AddProduct", {
//            method: "POST",
//            body: form
//        });
//
//        const json = await response.json(); // Always parse JSON regardless of status
//
//        if (response.ok) {
//            // Check for validation failure (status = false)
//            if (json.status === false) {
//                if (json.message === "Please login") {
//                    // Redirect if not logged in
//                    Swal.fire({
//                        title: "Authentication Error!",
//                        text: "LogIn session expired ! Please logIn to continue...",
//                        icon: "warning",
//                        confirmButtonColor: "#f0ad4e"
//                    });
//                    window.location = "admin-login.html";
//
//                } else {
//                    // Show validation error from backend
//                    Swal.fire({
//                        title: "Validation Error!",
//                        text: json.message || "Something went wrong. Please check the form.",
//                        icon: "warning",
//                        confirmButtonColor: "#f0ad4e"
//                    });
//                }
//            } else {
//                // Product saved successfully
//
//                if (json.message === "Product saved successfullyyyyy") {
//                    Swal.fire({
//                        title: "Success!",
//                        text: "Product saved successfully.",
//                        icon: "success",
//                        confirmButtonColor: "#3085d6"
//                    });
//                }
//
//
//            }
//        } else {
//
//            Swal.fire({
//                title: "Server Error!",
//                text: "Failed to submit product. Please try again.",
//                icon: "error",
//                confirmButtonColor: "#d33"
//            });
//        }
//
//    } catch (error) {
//        console.error("Error submitting product:", error);
//        Swal.fire({
//            title: "Error!",
//            text: "An unexpected error occurred. Please try again.",
//            icon: "error",
//            confirmButtonColor: "#d33"
//        });
//    }
//}

async function saveProduct() {
    const title = document.getElementById("productTitle").value;
    const quantity = document.getElementById("productQty").value;
    const price = document.getElementById("productPrice").value;
    const description = document.getElementById("productDesc").value;
    const weight = document.getElementById("productWeight").value;
    const dimension = document.getElementById("productDimension").value;
    const material = document.getElementById("productMaterial").value;
    const color = document.getElementById("productColor").value;
    const category = document.getElementById("productCategory").value;
    const type = document.getElementById("productType").value;
    const size = document.getElementById("productSize").value;

    const image1 = document.getElementById("productImage1").files[0];
    const image2 = document.getElementById("productImage2").files[0];
    const image3 = document.getElementById("productImage3").files[0];

    const form = new FormData();

    if (editingIndex !== -1) {
        const productToEdit = window.products[editingIndex];
        form.append("id", productToEdit.id);
    }

    // Use parameter names exactly as your servlet expects:
    form.append("title", title);
    form.append("quantity", quantity);
    form.append("price", price);
    form.append("description", description);
    form.append("weight", weight);
    form.append("dimension", dimension);
    form.append("material", material);
    form.append("color", color);
    form.append("category", category);
    form.append("type", type);
    form.append("size", size);

    if (image1)
        form.append("image1", image1);
    if (image2)
        form.append("image2", image2);
    if (image3)
        form.append("image3", image3);

    const url = editingIndex === -1 ? "AddProduct" : "UpdateProduct";

    try {
        const response = await fetch(url, {
            method: "POST",
            body: form
        });

        const json = await response.json();

        if (response.ok) {
            if (json.status === false) {
                if (json.message === "Please login") {
                    Swal.fire({
                        title: "Authentication Error!",
                        text: "Login session expired! Please log in again.",
                        icon: "warning",
                        confirmButtonColor: "#f0ad4e"
                    });
                    window.location = "admin-login.html";
                } else {
                    Swal.fire({
                        title: "Validation Error!",
                        text: json.message || "Please check the form.",
                        icon: "warning",
                        confirmButtonColor: "#f0ad4e"
                    });
                }
            } else {
                Swal.fire({
                    title: "Success!",
                    text: editingIndex === -1 ? "Product added successfully." : "Product updated successfully.",
                    icon: "success",
                    confirmButtonColor: "#3085d6"
                });
                // If you want to clear file inputs, define this function or clear manually:
                // clearFileInputs();

                editingIndex = -1;
                document.getElementById("product-form").reset();

                setTimeout(() => {
                    window.location.reload();
                }, 3000);



                // Reload products list
                fetch("LoadProductDataTable")
                        .then(res => res.json())
                        .then(data => {
                            window.products = data;
                            renderProducts();
                        });
            }
        } else {
            Swal.fire({
                title: "Server Error!",
                text: "Failed to submit product. Please try again.",
                icon: "error",
                confirmButtonColor: "#d33"
            });
        }
    } catch (error) {
        console.error("Error submitting product:", error);
        Swal.fire({
            title: "Error!",
            text: "An unexpected error occurred. Please try again.",
            icon: "error",
            confirmButtonColor: "#d33"
        });
    }
}

let editingIndex = -1;

function resetProductForm() {
    Swal.fire({
        title: 'Are you sure?',
        text: 'This will clear all form fields!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, reset it!',
        cancelButtonText: 'Cancel'
    }).then((result) => {
        if (result.isConfirmed) {
            const form = document.getElementById("product-form");
            form.reset();

            form.querySelectorAll('input[type="checkbox"]').forEach(cb => {
                cb.checked = false;
                cb.disabled = false;
            });

            editingIndex = -1; // clear editing state

            Swal.fire('Reset!', 'The form has been cleared.', 'success');
        }
    });
}


document.addEventListener("DOMContentLoaded", () => {
    fetch("LoadProductDataTable")
            .then(res => res.json())
            .then(data => {
                if (data.status && Array.isArray(data.products)) {
                    window.products = data.products;
                } else {
                    window.products = [];
                    console.error("No products or status false");
                }
                renderProducts();
            })
            .catch(err => {
                console.error("Error loading products:", err);
                document.getElementById("productTable").innerHTML =
                        `<tr><td colspan="12" class="text-center text-danger">Error loading products.</td></tr>`;
            });
});

function renderProducts() {
    const table = document.getElementById("productTable");
    table.innerHTML = "";

    const products = window.products || [];

    if (products.length === 0) {
        table.innerHTML = `<tr><td colspan="12" class="text-center">No products found.</td></tr>`;
        return;
    }

    products.forEach((p, i) => {
        table.innerHTML += `
        <tr>
            <td>${p.name || ""}</td>
            <td>${p.qty || 0}</td>
            <td>${p.price || 0}</td>
            <td>${p.description || ""}</td>
            <td>${p.weight || ""}</td>
            <td>${p.dimension || ""}</td>
            <td>${p.material || ""}</td>
            <td>${p.color || ""}</td>
            <td>${p.category || ""}</td>
            <td>${p.type || ""}</td>
            <td>${p.size || ""}</td>
            <td>
                <button class="btn btn-sm btn-primary" onclick="editProduct(${i})">Edit</button>
                <button class="btn btn-sm btn-danger" onclick="deleteProduct(${i})">Delete</button>
            </td>
        </tr>`;
    });
}

function editProduct(i) {
    editingIndex = i;

    const p = window.products[i];
    if (!p)
        return;

    const productId = p.id || "";

    document.getElementById("productId").value = productId;
    document.getElementById("productTitle").value = p.name || "";
    document.getElementById("productQty").value = p.qty || 0;
    document.getElementById("productPrice").value = p.price || 0;
    document.getElementById("productDesc").value = p.description || "";
    document.getElementById("productWeight").value = p.weight || "";
    document.getElementById("productDimension").value = p.dimension || "";
    document.getElementById("productMaterial").value = p.material || "";

    document.getElementById("productColor").value = p.colorId || "0";
    document.getElementById("productCategory").value = p.categoryId || "0";
    document.getElementById("productType").value = p.typeId || "0";
    document.getElementById("productSize").value = p.sizeId || "0";

    if (productId) {
        document.getElementById("productImagePreview1").src = `/product-images/${productId}/image1.png`;
        document.getElementById("productImagePreview2").src = `/product-images/${productId}/image2.png`;
        document.getElementById("productImagePreview3").src = `/product-images/${productId}/image3.png`;

    } else {
        document.getElementById("productImagePreview1").src = "";
        document.getElementById("productImagePreview2").src = "";
        document.getElementById("productImagePreview3").src = "";
    }
}

async function deleteProduct(i) {
    const product = window.products[i];
    if (!product)
        return;

    Swal.fire({
        title: `Delete "${product.name}"?`,
        text: "This action cannot be undone.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#d33",
        cancelButtonColor: "#3085d6",
        confirmButtonText: "Yes, delete it!",
        cancelButtonText: "Cancel"
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(`DeleteProduct?id=${product.id}`, {
                    method: "DELETE"
                });
                const json = await response.json();

                if (json.status) {
                    Swal.fire({
                        title: "Deleted!",
                        text: json.message || "Product deleted successfully.",
                        icon: "success",
                        confirmButtonColor: "#3085d6"
                    });

                    // Reload product list
                    fetch("LoadProductDataTable")
                            .then(res => res.json())
                            .then(data => {
                                window.products = data;
                                renderProducts();
                            });
                } else {
                    Swal.fire({
                        title: "Failed!",
                        text: json.message || "Failed to delete product.",
                        icon: "error",
                        confirmButtonColor: "#d33"
                    });
                }
            } catch (error) {
                Swal.fire({
                    title: "Error!",
                    text: "An unexpected error occurred.",
                    icon: "error",
                    confirmButtonColor: "#d33"
                });
            }
        }
    });
}

function printProductTable() {
    // Find the table inside the card body
    const table = document.querySelector('.card-body table');

    if (!table) {
        alert('Table not found!');
        return;
    }

    // Clone the table so we don't modify the original on the page
    const tableClone = table.cloneNode(true);

    // Remove the last <th> in the header
    tableClone.querySelectorAll('thead tr').forEach(tr => {
        tr.removeChild(tr.lastElementChild);
    });

    // Remove the last <td> in each row
    tableClone.querySelectorAll('tbody tr').forEach(tr => {
        tr.removeChild(tr.lastElementChild);
    });

    // Open print window
    const printWindow = window.open('', '', 'width=900,height=600');
    printWindow.document.write(`
        <html>
            <head>
                <title>Product List Report</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        margin: 40px;
                        background: #f9f9f9;
                        color: #333;
                    }
                    h2 {
                        text-align: center;
                        font-weight: 700;
                        margin-bottom: 30px;
                        color: #222;
                    }
                    table {
                        width: 100%;
                        border-collapse: separate;
                        border-spacing: 0 12px;
                        box-shadow: 0 0 15px rgba(0,0,0,0.1);
                        background: #fff;
                        border-radius: 8px;
                        overflow: hidden;
                    }
                    th, td {
                        padding: 14px 20px;
                        text-align: left;
                    }
                    thead tr th {
                        background: #007bff;
                        color: white;
                        font-weight: 600;
                        text-transform: uppercase;
                        letter-spacing: 0.05em;
                    }
                    tbody tr {
                        background: #fff;
                        box-shadow: 0 2px 6px rgba(0,0,0,0.05);
                        border-radius: 6px;
                    }
                    tbody tr:hover {
                        background: #f1faff;
                    }
                    tbody tr td {
                        border-bottom: none;
                        border-top: 1px solid #eee;
                    }
                    tbody tr:last-child td {
                        border-bottom: none;
                    }
                </style>
            </head>
            <body>
                <h2>Product List Report</h2>
                ${tableClone.outerHTML}
            </body>
        </html>
    `);

    printWindow.document.close();
    printWindow.focus();
    printWindow.print();
}



function clearFileInputs() {
    document.getElementById("productImage1").value = "";
    document.getElementById("productImage2").value = "";
    document.getElementById("productImage3").value = "";
}
