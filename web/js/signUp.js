async function signUp() {
//    const firstName = $("#firstName").val();
    const firstName = document.getElementById("firstName").value.trim();
    const lastName = document.getElementById("lastName").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    const user = {firstName, lastName, email, password};
    const userJson = JSON.stringify(user);

    try {
        const response = await fetch(
                "SignUp",
                {
                    method: "POST",
                    header: {"Content-Type": "application/json"},
                    body: userJson
                });

        if (response.ok) {
            const json = await response.json();
            if (json.status) {
                window.location = "verify-account.html";
            } else {
                document.getElementById("message").innerHTML = json.message;
            }
        } else {
            document.getElementById("message").innerHTML = "Registration failed. Please try again.";
        }
    } catch (error) {
        console.error("Sign up error:", error);
        document.getElementById("message").innerHTML = "An unexpected error occurred.";
    }
}