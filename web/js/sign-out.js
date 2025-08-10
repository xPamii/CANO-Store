async function signOut() {
    try {
        const response = await fetch("SignOut");
        if (!response.ok) throw new Error('Network response not OK');
        const json = await response.json();

        if (json.status) {
            Swal.fire({
                title: "Logged Out!",
                text: "You have successfully logged out.",
                icon: "success",
                timer: 5000,             // 2 seconds before auto-close
                showConfirmButton: false // hide the OK button
            });
            setTimeout(() => {
                window.location = "index.html";
            }, 5000);
        } else {
            Swal.fire({
                title: "Error!",
                text: json.message || "Logout failed. Please try again.",
                icon: "warning",
                timer: 5000,
                showConfirmButton: false
            });
            setTimeout(() => {
                window.location.reload();
            }, 5000);
        }
    } catch (error) {
        Swal.fire({
            title: "Error!",
            text: "Logout failed! Please check your connection.",
            icon: "error",
            timer: 5000,
            showConfirmButton: false
        });
        setTimeout(() => {
            window.location.reload();
        }, 5000);
        console.error("Logout failed!", error);
    }
}
