//====================Search Products by Keyword ======================


document.addEventListener('DOMContentLoaded', () => {
    const searchBtn = document.getElementById('searchBtn');
    const searchInput = document.getElementById('searchProductKeyword');
    const resultsContainer = document.getElementById('st-product-container');

    async function searchProducts() {
        const keyword = searchInput.value.trim();
        if (!keyword) {
            alert('Please enter a keyword');
            return;
        }

        try {
            const contextPath = window.location.pathname.split('/')[1]; // "CanoStore"
            const response = await fetch(`/${contextPath}/api/products/search?keyword=${encodeURIComponent(keyword)}`);

            if (!response.ok)
                throw new Error('Network response was not ok');

            const products = await response.json();
            renderResults(products);
        } catch (error) {
            resultsContainer.innerHTML = `<p style="color:red; padding: 20px;">Error: ${error.message}</p>`;
        }
    }

    function renderResults(products) {
        resultsContainer.innerHTML = '';  // Clear previous results

        if (!products.length) {
            resultsContainer.innerHTML = '<p style="padding: 20px;">No products found.</p>';
            return;
        }

        products.forEach(p => {
            const colDiv = document.createElement('div');
            colDiv.className = 'col-sm-6 col-md-4 col-lg-3 p-b-35 isotope-item';

            colDiv.innerHTML = `
        <div class="block2" id="st-product" data-id="${p.id}">
          <div class="block2-pic hov-img0">
            <a href="#" id="st-product-a-${p.id}">
              <img src="product-images/${p.id}/image1.png" alt="IMG-PRODUCT" id="st-product-img-${p.id}">
            </a>
            <a href="product-detail.html" 
               class="block2-btn flex-c-m stext-103 cl2 size-102 bg0 bor2 hov-btn1 p-lr-15 trans-04 js-show-modal1"
               data-product-id="${p.id}" 
               id="st-product-a-2">
              Quick View
            </a>
          </div>

          <div class="block2-txt flex-w flex-t p-t-14">
            <div class="block2-txt-child1 flex-col-l ">
              <a href="product-detail.html" 
                 class="stext-104 cl4 hov-cl1 trans-04 js-name-b2 p-b-6" 
                 id="st-product-title-${p.id}">
                ${p.name}
              </a>
              <span class="stext-105 cl3">
                LKR . <span id="st-product-price-${p.id}">${p.price.toFixed(2)}</span>
              </span>
            </div>
            <div class="block2-txt-child2 flex-r p-t-3">
              <a href="#" class="btn-addwish-b2 dis-block pos-relative js-addwish-b2">
                <img class="icon-heart1 dis-block trans-04" src="images/icons/icon-heart-01.png" alt="ICON">
                <img class="icon-heart2 dis-block trans-04 ab-t-l" src="images/icons/icon-heart-02.png" alt="ICON">
              </a>
            </div>
          </div>
        </div>
      `;

            resultsContainer.appendChild(colDiv);
        });
    }

    // Bind events
    searchBtn.addEventListener('click', searchProducts);
    searchInput.addEventListener('keyup', (e) => {
        if (e.key === 'Enter') {
            searchProducts();
        }
    });
});
//========================Search Products by Keyword End======================



//========================Filter Products by Type============================

document.addEventListener("DOMContentLoaded", () => {
    const productContainer = document.getElementById("st-product-container");

    // Map your simplified keys to exact DB values
    const typeMap = {
        all: 'all',
        women: "Women's wear",
        men: "Men's wear",
        bag: "Bags",
        shoes: "shoes",
        watches: "Watches"
    };

    const contextPath = '/CanoStore';
    async function loadProductsByType(typeKey) {
        const type = typeMap[typeKey] || "all";

        try {

            const res = await fetch(`${contextPath}/LoadProductsByType?type=${encodeURIComponent(type)}`);

            const data = await res.json();

            if (data.status) {
                displayProducts(data.productList);
            } else {
                productContainer.innerHTML = `<p>No products found</p>`;
            }
        } catch (err) {
            console.error("Error loading products:", err);
            productContainer.innerHTML = `<p>Error loading products</p>`;
        }
    }

    function displayProducts(products) {
        productContainer.innerHTML = "";

        if (!products || products.length === 0) {
            productContainer.innerHTML = `<p>No products available</p>`;
            return;
        }

        products.forEach(p => {
            const imagePath = `product-images/${p.id}/image1.png`;

            productContainer.innerHTML += `
                <div class="col-sm-6 col-md-4 col-lg-3 p-b-35 isotope-item">
                    <div class="block2" data-id="${p.id}">
                        <div class="block2-pic hov-img0">
                            <a href="#" id="st-product-a-${p.id}">
                                <img src="${imagePath}" alt="${p.name}" id="st-product-img-${p.id}">
                            </a>
                            <a href="product-detail.html" 
                               class="block2-btn flex-c-m stext-103 cl2 size-102 bg0 bor2 hov-btn1 p-lr-15 trans-04 js-show-modal1" 
                               data-product-id="${p.id}">
                                Quick View
                            </a>
                        </div>
                        <div class="block2-txt flex-w flex-t p-t-14">
                            <div class="block2-txt-child1 flex-col-l ">
                                <a href="product-detail.html" 
                                   class="stext-104 cl4 hov-cl1 trans-04 js-name-b2 p-b-6" 
                                   id="st-product-title-${p.id}">
                                    ${p.name}
                                </a>
                                <span class="stext-105 cl3">
                                    LKR . <span id="st-product-price-${p.id}">${p.price.toFixed(2)}</span>
                                </span>
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
        });
    }

    // Bind buttons with keys that map to DB values
    document.getElementById("allProductsBTN").addEventListener("click", () => loadProductsByType("all"));
    document.getElementById("womensWearBTN").addEventListener("click", () => loadProductsByType("women"));
    document.getElementById("mensWearBTN").addEventListener("click", () => loadProductsByType("men"));
    document.getElementById("bagBTN").addEventListener("click", () => loadProductsByType("bag"));
    document.getElementById("shoesBTN").addEventListener("click", () => loadProductsByType("shoes"));
    document.getElementById("watchesBTN").addEventListener("click", () => loadProductsByType("watches"));

    // Load all by default
    loadProductsByType("all");
});


//========================Filter Products by Type End=========================
