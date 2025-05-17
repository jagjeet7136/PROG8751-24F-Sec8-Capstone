import React, { useState, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import styles from "./ResetPassword.module.css";
import todoSmallIcon from "../../assets/icons/logo-transparent-png.png";

const ResetPassword = () => {
    const emailRef = useRef("");
    const securityAnswerRef = useRef("");
    const newPasswordRef = useRef("");
    const [email, setEmail] = useState("");
    const [securityQuestion, setSecurityQuestion] = useState("");
    const [errorMessage, setErrorMsg] = useState("");
    const [isFormValid, setIsFormValid] = useState(true);
    const [isSecurityQuestionVisible, setIsSecurityQuestionVisible] = useState(false);
    const navigate = useNavigate();
    const token = localStorage.getItem("token");

    const handleSubmitEmail = (e) => {
        e.preventDefault();
        const email = emailRef.current.value;
        console.log(email);
        axios
            .post(
                `http://localhost:9898/user/verify-email/${email}`
            )
            .then((res) => {
                setEmail(email);
                setSecurityQuestion(res.data.securityQuestion);
                setIsSecurityQuestionVisible(true);
                setErrorMsg("");
            })
            .catch((error) => {
                setErrorMsg("Email not found.");
                setIsFormValid(false);
            });
    };

    const handleResetPassword = (e) => {
        e.preventDefault();
        const resetData = {
            email: email,
            securityAnswer: securityAnswerRef.current.value,
            newPassword: newPasswordRef.current.value,
        };

        axios
            .post(
                "http://localhost:9898/user/reset-password",
                resetData,

            )
            .then((res) => {
                navigate("/login");
            })
            .catch((error) => {
                setErrorMsg("Incorrect answer or password reset failed.");
                setIsFormValid(false);
            });
    };

    return (
        <div className={styles.reset}>
            <Link to="/">
                <img src={todoSmallIcon} alt="" className={styles.resetIcon} />
            </Link>
            {!isSecurityQuestionVisible ? (
                <form onSubmit={handleSubmitEmail} className={styles.resetForm}>
                    <h1 className={styles.resetHeading}>Reset Password</h1>
                    <input
                        type="email"
                        placeholder="Enter your email"
                        ref={emailRef}
                        className={styles.input}
                    />
                    <div className={`${styles.errorMessageContainer} ${!isFormValid ? styles.active : ""}`}>
                        <h6 className={styles.errorMessage}>{errorMessage}</h6>
                    </div>
                    <button type="submit" className={styles.resetButton}>Submit</button>
                </form>
            ) : (
                <form onSubmit={handleResetPassword} className={styles.resetForm}>
                    <h1 className={styles.resetHeading}>Security Question</h1>
                    <p>{securityQuestion}</p>
                    <input
                        type="text"
                        placeholder="Answer"
                        ref={securityAnswerRef}
                        className={styles.input}
                    />
                    <input
                        type="password"
                        placeholder="New Password"
                        ref={newPasswordRef}
                        className={styles.input}
                    />
                    <div className={`${styles.errorMessageContainer} ${!isFormValid ? styles.active : ""}`}>
                        <h6 className={styles.errorMessage}>{errorMessage}</h6>
                    </div>
                    <button type="submit" className={styles.resetButton}>Reset Password</button>
                </form>
            )}
            <h6 className={styles.loginContainer}>
                Don't have an account?&nbsp;&nbsp;<Link to="/register">Sign Up</Link>
                Forgot Password?&nbsp;&nbsp;<Link to="/reset">Reset</Link>
            </h6>
        </div>
    );
};

export default ResetPassword;