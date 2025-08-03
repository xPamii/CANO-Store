// Save Product
async function saveProduct() {
    const title = document.getElementById("productTitle").value;
    const quantity = document.getElementById("productQty").value;
    const price = document.getElementById("productPrice").value;
    const description = document.getElementById("productDesc").value;
    const color = document.getElementById("productColor").value;
    const category = document.getElementById("productCategory").value;
    const type = document.getElementById("productType").value;
    const size = document.getElementById("productSize").value;


    const image1 = document.getElementById("productImage1").files[0];
    const image2 = document.getElementById("productImage2").files[0];
    const image3 = document.getElementById("productImage3").files[0];

    const form = new FormData();
    form.append("title", title);
    form.append("quantity", quantity);
    form.append("price", price);
    form.append("description", description);
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

    try {
        const response = await fetch("AddProduct", {
            method: "POST",
            body: form
        });

        if (response.ok) {
            const json = await response.json();

            Swal.fire({
                title: "Success!",
                text: json.message || "Product saved successfully.",
                icon: "success",
                confirmButtonColor: "#3085d6"
            });

//            resetProductForm();
        } else {
            if (json.message === "Please login") {
                window.location = "admin-login.html";
            } else {
                Swal.fire({
                    title: "Error!",
                    text: "Failed to submit product. Please try again.",
                    icon: "error",
                    confirmButtonColor: "#d33"
                });
            }

        }

    } catch (error) {
        console.error("Error submitting product:", error);
        Swal.fire({
            title: "Error!",
            text: "Failed to submit product. Please try again.",
            icon: "error",
            confirmButtonColor: "#d33"
        });
    }
}

// Reset Form
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

            Swal.fire('Reset!', 'The form has been cleared.', 'success');
        }
    });
}

// Render Product Table
//function renderProducts() {
//    const table = document.getElementById("productTable");
//    table.innerHTML = "";
//    let products = [];
//
//
//    if (!window.products || products.length === 0) {
//        table.innerHTML = `<tr><td colspan="8">No products found.</td></tr>`;
//        return;
//    }
//
//    products.forEach((p, i) => {
//        table.innerHTML += `
//        <tr>
//          <td>${p.title}</td>
//          <td>${p.quantity}</td>
//          <td>$${p.price}</td>
//          <td>${p.color}</td>
//          <td>${p.category}</td>
//          <td>${p.type}</td>
//          <td>${p.sizes.join(", ")}</td>
//          <td>
//            <button class="btn btn-sm btn-primary" onclick="editProduct(${i})">Edit</button>
//            <button class="btn btn-sm btn-danger" onclick="deleteProduct(${i})">Delete</button>
//          </td>
//        </tr>`;
//    });
//}



//document.addEventListener("DOMContentLoaded", function () {
//    const form = document.getElementById("product-form");
//    const table = document.getElementById("productTable");
//
//    let products = JSON.parse(localStorage.getItem("products")) || [];
//    let editingIndex = -1;
//
//    function getSelectedSizes() {
//        return ["S", "M", "L", "XL", "XXL"].filter(size => document.getElementById("size" + size).checked);
//    }
//
//    function setSelectedSizes(sizes) {
//        ["S", "M", "L", "XL", "XXL"].forEach(size => {
//            document.getElementById("size" + size).checked = sizes.includes(size);
//        });
//    }
//
//    function renderProducts() {
//        table.innerHTML = "";
//        products.forEach((p, i) => {
//            table.innerHTML += `
//        <tr>
//          <td>${p.title}</td>
//          <td>${p.quantity}</td>
//          <td>$${p.price}</td>
//          <td>${p.color}</td>
//          <td>${p.category}</td>
//          <td>${p.type}</td>
//          <td>${p.sizes.join(", ")}</td>
//          <td>
//            <button class="btn btn-sm btn-primary" onclick="editProduct(${i})">Edit</button>
//            <button class="btn btn-sm btn-danger" onclick="deleteProduct(${i})">Delete</button>
//          </td>
//        </tr>`;
//        });
//    }
//
//    window.editProduct = function (index) {
//        const p = products[index];
//        document.getElementById("productTitle").value = p.title;
//        document.getElementById("productQty").value = p.quantity;
//        document.getElementById("productPrice").value = p.price;
//        document.getElementById("productDesc").value = p.description;
//        document.getElementById("productColor").value = p.color;
//        document.getElementById("productCategory").value = p.category;
//        document.getElementById("productType").value = p.type;
//        setSelectedSizes(p.sizes);
//        editingIndex = index;
//    };
//
//    window.deleteProduct = function (index) {
//        if (confirm("Are you sure you want to delete this product?")) {
//            products.splice(index, 1);
//            localStorage.setItem("products", JSON.stringify(products));
//            renderProducts();
//        }
//    };
//
//    form.onsubmit = function (e) {
//        e.preventDefault();
//        const newProduct = {
//            title: document.getElementById("productTitle").value,
//            quantity: document.getElementById("productQty").value,
//            price: document.getElementById("productPrice").value,
//            description: document.getElementById("productDesc").value,
//            color: document.getElementById("productColor").value,
//            category: document.getElementById("productCategory").value,
//            type: document.getElementById("productType").value,
//            sizes: getSelectedSizes()
//        };
//
//        if (editingIndex === -1) {
//            products.push(newProduct);
//        } else {
//            products[editingIndex] = newProduct;
//            editingIndex = -1;
//        }
//
//        localStorage.setItem("products", JSON.stringify(products));
//        form.reset();
//        renderProducts();
//    };
//
//    renderProducts();
//});
