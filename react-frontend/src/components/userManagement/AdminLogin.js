import React, { useContext, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { AuthContext } from "../../context/AuthContext";
import styles from "./Login.module.css";
import WarningIcon from '@mui/icons-material/Warning';

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

        if (authContext.isAuthenticated) {
            authContext.logout();
            localStorage.removeItem("token");
            localStorage.removeItem("loggedIn");
            localStorage.removeItem("username");
            localStorage.removeItem("user");
        }

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
                        if (admin.roles && admin.roles.some((role) => role.name === "ROLE_ADMIN")) {
                            localStorage.setItem("adminToken", res.data.token);
                            localStorage.setItem("adminLoggedIn", "true");
                            localStorage.setItem("adminUsername", username.current.value);
                            localStorage.setItem("admin", JSON.stringify(admin));
                            authContext.login(res.data.token, username.current.value, admin);
                            navigate("/adminDashboard");
                        } else {
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
            <Link to="/" className={styles.logo}>
                SHOPEE
            </Link>
            <form onSubmit={handleAdminLogin} className={styles.loginForm}>
                <h1 className={styles.loginHeading}>Login</h1>
                <input
                    placeholder="Email"
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
                {errorMessage && (
                    <div className={styles.errorMessageContainer}>
                        <WarningIcon fontSize="large" color="error" />
                        <span className={styles.errorMessage}>{errorMessage}</span>
                    </div>
                )}
                <button type="submit" className={styles.loginButton}>
                    Login
                </button>
                <div className={styles.loginContainer}>
                    <span>Don't have an account?&nbsp;&nbsp;<Link to="/register">Sign Up</Link></span>
                    <span>Forgot Password?&nbsp;&nbsp;<Link to="/reset">Reset</Link></span>
                </div>
            </form>
        </div>
    );
};