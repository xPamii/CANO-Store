async function signIn() {
    const email = document.getElementById("emailSI").value;
    const password = document.getElementById("passwordSI").value;


    const signIn = {
        email: email,
        password: password
    };

    const signInJson = JSON.stringify(signIn);
//    console.log("Sign In Data:", signInJson);

    const response = await fetch(
            "SignIn",
            {
                method: "POST",
                body: signInJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const json = await response.json();
        if (json.status) { //if true

            if (json.message2 === "1") { // 1
//                Swal.fire({
//                    title: "Success!",
//                    text: "Sign In successful, redirecting to verify account page.",
//                    icon: "success",
//                    confirmButtonColor: "#3085d6"
//                });
//                console.log("Sign In successful, redirecting to Verify account . . .");
                window.location = "verify-account.html";

            } else { // 2
//                Swal.fire({
//                    title: "Success!",
//                    text: "Sign In successful, redirecting to Home . . .",
//                    icon: "success",
//                    confirmButtonColor: "#3085d6"
//                });
                window.location = "home.html";
            }
        } else { //if false
//            Swal.fire({
//                title: "Error!",
//                text: json.message2 || "Something went wrong. Please try again.",
//                icon: "warning",
//                confirmButtonColor: "#f0ad4e"
//            });
            document.getElementById("message2").innerHTML = json.message2;
        }
    } else {
        Swal.fire({
            title: "Error!",
            text: json.message2 || "Sign In failed. Please try again . . .",
            icon: "warning",
            confirmButtonColor: "#f0ad4e"
        });
        document.getElementById("message2").innerHTML = "Sign In failed. Please try again.";
    }
}