import React, { useContext, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { AuthContext } from "../../context/AuthContext";
import styles from "./Login.module.css";
import adminIcon from "../../icons/logo-transparent-png.png";

export const AdminLogin = () => {
    const authContext = useContext(AuthContext);
    const username = useRef("");
    const password = useRef("");
    const navigate = useNavigate();
    const [errorMessage, setErrorMsg] = useState("");
    const [isFormValid, setIsFormValid] = useState(true);
    const [showPassword, setShowPassword] = useState(false);

    const handleAdminLogin = (e) => {
        if (!isFormValid) {
            setIsFormValid(true);
        }
        e.preventDefault();

        // Step 1: Logout current user if logged in
        if (authContext.isAuthenticated) {
            authContext.logout();
            localStorage.removeItem("token");
            localStorage.removeItem("loggedIn");
            localStorage.removeItem("username");
            localStorage.removeItem("user");
        }

        // Step 2: Proceed with Admin Login
        const loginObject = {
            username: username.current.value,
            password: password.current.value,
        };
        axios
            .post("http://localhost:9898/user/login", loginObject)
            .then((res) => {
                axios
                    .get(
                        `http://localhost:9898/user/getUser/?username=${username.current.value}`,
                        {
                            headers: {
                                Authorization: res.data.token,
                            },
                        }
                    )
                    .then((innerRes) => {
                        const admin = innerRes.data;
                        console.log(admin);
                        // Step 3: Check role
                        if (admin.roles && admin.roles.some((role) => role.name === "ROLE_ADMIN")) {
                            localStorage.setItem("adminToken", res.data.token);
                            localStorage.setItem("adminLoggedIn", "true");
                            localStorage.setItem("adminUsername", username.current.value);
                            localStorage.setItem("admin", JSON.stringify(admin));
                            authContext.login(res.data.token, username.current.value, admin);
                            navigate("/adminDashboard"); // Redirect to admin dashboard
                        } else {
                            // Not an admin, show forbidden error
                            setErrorMsg("Forbidden: You do not have admin access.");
                            setIsFormValid(false);
                        }
                    })
                    .catch((innerError) => {
                        console.error(innerError);
                    });
            })
            .catch((error) => {
                console.error(error);
                setErrorMsg("Some error occurred");
                if (error.response) {
                    if (error.response.status === 401) {
                        setErrorMsg("Wrong email or password");
                    } else if (
                        error.response.data.message &&
                        error.response.data.message.trim().length > 0
                    ) {
                        setErrorMsg(error.response.data.message);
                    }
                }
                setIsFormValid(false);
            });
    };


    return (
        <div className={styles.login}>
            <Link to="/">
                <img src={adminIcon} alt="" className={styles.loginIcon}></img>
            </Link>
            <form onSubmit={handleAdminLogin} className={styles.loginForm}>
                <h1 className={styles.loginHeading}>Admin Login</h1>
                <input
                    type="email"
                    placeholder="Admin Email"
                    ref={username}
                    className={styles.username}
                />
                <div
                    className={`${styles.passwordContainer} ${isFormValid ? styles.passwordContainerMargin : ""
                        }`}
                >
                    <input
                        type={showPassword ? "text" : "password"}
                        placeholder="Password"
                        ref={password}
                        className={styles.password}
                    />
                    <span
                        className={styles.passwordToggle}
                        onClick={() => setShowPassword(!showPassword)}
                    >
                        {showPassword ? "Hide" : "Show"}
                    </span>
                </div>
                <div
                    className={`${styles.errorMessageContainer} 
                    ${!isFormValid
                            ? styles.errorMessageContainer + " " + styles.active
                            : ""
                        }`}
                >
                    <img
                        src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR9YISfL4Lm8FJPRneGwEq8_-9Nim7YeuMJMw&usqp=CAU"
                        alt=""
                    ></img>
                    <h6 className={styles.errorMessage}>{errorMessage}</h6>
                </div>
                <button type="submit" className={styles.loginButton}>
                    Login as Admin
                </button>
                <h6 className={styles.loginContainer}>
                    Want to log in as a user?&nbsp;&nbsp;
                    <Link to="/login">User Login</Link>
                </h6>
            </form>
            <h6 className={styles.tPContainer}>
                <Link to="/about">About and Information</Link>
            </h6>
        </div>
    );
};