function loadProductDetails(productId) {
    fetch("LoadProductDetails?id=" + productId)
            .then(response => response.json())
            .then(data => {
                if (!data.status) {
                    console.error("Failed to load product:", data.message || "Unknown error");
                    return;
                }

                const product = data.product;
                const similarProducts = data.productList;

                // 🟢 Set Main Product Info
                document.getElementById("product-name").textContent = product.name;
                document.getElementById("product-price").textContent = product.price;
                document.getElementById("product-size").textContent = product.size ? product.size.value : "-";
                document.getElementById("product-qty").textContent = product.qty;
                document.getElementById("product-color").textContent = product.color ? product.color.value : "-";
                document.getElementById("product-description").textContent = product.description;

                // 🟢 Additional Info
                document.getElementById("product-weight").textContent = product.weight || "-";
                document.getElementById("product-dimension").textContent = product.dimension || "-";
                document.getElementById("product-material").textContent = product.material || "-";
                document.getElementById("product-color-ad").textContent = product.color ? product.color.value : "-";
                document.getElementById("product-size-ad").textContent = product.size ? product.size.value : "-";
                document.getElementById("product-published-on").textContent = product.created_at || "-";

                // ✅ Update preview and expand links
                for (let i = 1; i <= 3; i++) {
                    const preview = document.getElementById(`product-image-0${i}-preview`);
                    const expand = document.getElementById(`product-image-0${i}-expand`);
                    const thumbnail = document.getElementById(`product-image-0${i}-thumbnail`);

                    const imagePath = `product-images/${product.id}/image${i}.png`;

                    if (preview)
                        preview.src = imagePath;
                    if (expand)
                        expand.href = imagePath;
                    if (thumbnail)
                        thumbnail.setAttribute("data-thumb", imagePath);
                }

                //add-to-cart-main-button
                const addToCartMain = document.getElementById("add-to-cart-btn");
                addToCartMain.addEventListener(
                        "click", (e) => {
                    addToCart(product.id, document.getElementById("add-to-cart-qty").value);
                    e.preventDefault();
                });

                //add-to-cart-main-button-end



                // 🟢 Similar Products
                const container = document.querySelector('.slick2');
                container.innerHTML = "";

                similarProducts.forEach(sp => {
                    const card = `
                    <div class="item-slick2 p-l-15 p-r-15 p-t-15 p-b-15">
                        <div class="block2">
                            <div class="block2-pic hov-img0">
                                <img src="product-images/${sp.id}/image1.png" alt="${sp.name}">
                                <a href="product-detail.html?id=${sp.id}" class="block2-btn flex-c-m stext-103 cl2 size-102 bg0 bor2 hov-btn1 p-lr-15 trans-04 js-show-modal1">
                                    Quick View
                                </a>
                            </div>
                            <div class="block2-txt flex-w flex-t p-t-14">
                                <div class="block2-txt-child1 flex-col-l">
                                    <a href="product-detail.html?id=${sp.id}" class="stext-104 cl4 hov-cl1 trans-04 js-name-b2 p-b-6">${sp.name}</a>
                                    <span class="stext-105 cl3">${sp.price}</span>
                                </div>
                                <div class="block2-txt-child2 flex-r p-t-3">
                                    <a href="#" class="btn-addwish-b2 dis-block pos-relative js-addwish-b2">
                                        <img class="icon-heart1 dis-block trans-04" src="images/icons/icon-heart-01.png" alt="ICON">
                                        <img class="icon-heart2 dis-block trans-04 ab-t-l" src="images/icons/icon-heart-02.png" alt="ICON">
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                    container.innerHTML += card;
                });

                // 🟢 Re-initialize Slick Carousel
                if ($('.slick2').hasClass('slick-initialized')) {
                    $('.slick2').slick('unslick');
                }
                $('.slick2').slick({
                    slidesToShow: 3,
                    slidesToScroll: 1,
                    infinite: true,
                    arrows: true,
                    dots: false,
                    autoplay: true,
                    autoplaySpeed: 3000
                });
            })
            .catch(error => {
                console.error("Error fetching product details:", error);
            });
}

// 🟢 Load product if ID is present in URL
const params = new URLSearchParams(window.location.search);
const pid = params.get("id");
if (pid) {
    loadProductDetails(pid);
}

async function addToCart(productId, qty) {

    const response = await fetch("AddToCart?prId=" + productId + "&qty=" + qty);
    if (response.ok) {
        const json = await response.json(); // await response.text();
        if (json.status) {
            Swal.fire({
                title: "Success!",
                text: "Product add to cart successful.",
                icon: "success",
                confirmButtonColor: "#3085d6"
            });
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
            title: " Error!",
            text: json.message || "Something went wrong. Please try again.",
            icon: "warning",
            confirmButtonColor: "#f0ad4e"
        });
    }
}
