import React, { useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import styles from "./Login.module.css";
import todoSmallIcon from "../../icons/logo-transparent-png.png";

export const Login = () => {
    const username = useRef("");
    const password = useRef("");
    const [errorMessage, setErrorMsg] = useState("");
    const [isFormValid, setIsFormValid] = useState(true);
    const [showPassword, setShowPassword] = useState(false);

    const handleLogin = (e) => {
        e.preventDefault();
    };

    return (
        <div className={styles.login}>
            <Link to="/"><img src={todoSmallIcon} alt="" className={styles.loginIcon}></img></Link>
            <form onSubmit={handleLogin} className={styles.loginForm}>
                <h1 className={styles.loginHeading}>Login</h1>
                <input type="email" placeholder="Email" ref={username} className={styles.username} />
                <div className={`${styles.passwordContainer} ${isFormValid ? styles.passwordContainerMargin : ""}`} >
                    <input type={showPassword ? "text" : "password"} placeholder="Password" ref={password}
                        className={styles.password} />
                    <span className={styles.passwordToggle} onClick={() => setShowPassword(!showPassword)}>
                        {showPassword ? "Hide" : "Show"}
                    </span>
                </div>
                <div className={`${styles.errorMessageContainer} 
                    ${!isFormValid ? styles.active : ""}`}>
                    <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR9YISfL4Lm8FJPRneGwEq8_-9Nim7YeuMJMw&usqp=CAU" alt=""></img>
                    <h6 className={styles.errorMessage}>{errorMessage}</h6>
                </div>
                <button type="submit" className={styles.loginButton}>Login</button>
                <h6 className={styles.loginContainer}>Don't have an account?&nbsp;&nbsp;<Link to="/register">Sign Up</Link></h6>
            </form>
            <h6 className={styles.tPContainer}><Link to="/about">About and Information</Link></h6>
        </div>
    );
}
