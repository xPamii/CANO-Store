async function loadProductData() {

    const response = await fetch("LoadProductData");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            loadSelect("productColor", json.colorList, "value");
            loadSelect("productCategory", json.categoryList, "category");
            loadSelect("productType", json.typeList, "value");

            console.log(json.colorList);
            console.log(json.categoryList);
            console.log(json.typeList);
        } else {
            document.getElementById("message").innerHTML = "Something went wrong. Please try again later";
        }

    } else {
        document.getElementById("message").innerHTML = "Product loading failed. Please try again";

    }

}

function loadSelect(selectId, items, property) {
    const select = document.getElementById(selectId);

    items.forEach(item => {
        const option = document.createElement("option");
        option.value = item.id;
        option.innerHTML = item[property];
        select.appendChild(option);
    });
}