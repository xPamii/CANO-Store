const form = document.getElementById("login-form");
const message = document.getElementById("message");
const loginBtn = document.getElementById("login-btn");
const btnText = loginBtn.querySelector('.btn-text') || loginBtn;

// Add input animations
document.querySelectorAll('.form-control').forEach(input => {
    input.addEventListener('focus', function () {
        this.parentElement.style.transform = 'translateY(-2px)';
    });

    input.addEventListener('blur', function () {
        this.parentElement.style.transform = 'translateY(0)';
    });
});

//---------------------------------------------------------------------------

async function adminLogIn() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;


    const adLogin = {
        username: username,
        password: password
    };

    const adLogJson = JSON.stringify(adLogin);
//    console.log("Sign In Data:", signInJson);

    const response = await fetch(
            "AdminLogin",
            {
                method: "POST",
                body: adLogJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const json = await response.json();
        if (json.status) { //if true

            window.location = "admin-dashboard.html";

        } else { //if false
            document.getElementById("message").innerHTML = json.message;
        }
    } else {
        document.getElementById("message").innerHTML = "Sign In failed. Please try again.";
    }
}

//----------------------------------------------------------------------------------------------------

//form.onsubmit = function (e) {
//    e.preventDefault();
//
//    const username = document.getElementById("username").value.trim();
//    const password = document.getElementById("password").value.trim();
//
//    // Add loading state
//    loginBtn.classList.add('loading');
//    btnText.textContent = '';
//
//    // Hide previous error message
//    message.classList.remove('show');
//
//    // Simulate authentication delay
//    setTimeout(() => {
//
//        if (username === "admin" && password === "admin123") {
//            btnText.textContent = 'Success!';
//            loginBtn.style.background = 'linear-gradient(135deg, #48bb78, #38a169)';
//
//            // Store login state in memory instead of localStorage
//            window.adminLoggedIn = true;
//
//            setTimeout(() => {
//                window.location.href = "admin-dashboard.html";
//            }, 800);
//        } else {
//            loginBtn.classList.remove('loading');
//            btnText.textContent = 'Sign In';
//            message.classList.add('show');
//
//            // Shake animation for error
//            form.style.animation = 'shake 0.5s ease-in-out';
//            setTimeout(() => {
//                form.style.animation = '';
//            }, 500);
//        }
//    }, 1200);
//};

// Add shake animation
const shakeKeyframes = `
      @keyframes shake {
        0%, 100% { transform: translateX(0); }
        10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
        20%, 40%, 60%, 80% { transform: translateX(5px); }
      }
    `;

const styleSheet = document.createElement("style");
styleSheet.textContent = shakeKeyframes;
document.head.appendChild(styleSheet);

function showHelp() {
    alert('For technical support, please contact your system administrator.\n\nDefault credentials:\nUsername: admin\nPassword: admin123');
}

function goBack() {
    // Check if there's history to go back to
    if (window.history.length > 1) {
        window.history.back();
    } else {
        // Fallback to a default page or home
        window.location.href = 'home.html';
    }
}

// Add keyboard shortcuts
document.addEventListener('keydown', function (e) {
    if (e.ctrlKey && e.key === 'l') {
        e.preventDefault();
        document.getElementById('username').focus();
    }
    // ESC key to go back
    if (e.key === 'Escape') {
        goBack();
    }
});