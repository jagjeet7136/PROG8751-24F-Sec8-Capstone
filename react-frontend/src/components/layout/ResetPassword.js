import React, { useState, useRef } from "react";
import { Link, } from "react-router-dom";
import axios from "axios";
import styles from "./ResetPassword.module.css";
import WarningIcon from '@mui/icons-material/Warning';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

const ResetPassword = () => {
    const emailRef = useRef("");
    const [successMsg, setSuccessMsg] = useState("");
    const [errorMsg, setErrorMsg] = useState("");

    const handleSubmitEmail = (e) => {
        e.preventDefault();
        const email = emailRef.current.value;
        console.log(email);
        axios
            .post(
                `http://localhost:9898/user/passwordResetEmailVerification?email=` + email
            )
            .then((res) => {
                setErrorMsg("");
                emailRef.current.value = "";
                setSuccessMsg("Password reset email sent, Please check your email")
            })
            .catch((error) => {
                console.log(error);
                setErrorMsg("Some error occurred");
                if (error.response) {
                    if (error.response.data && error.response.data.errors.length > 0) {
                        setErrorMsg(error.response.data.errors[0]);
                    }
                    else if (
                        error.response.data.message &&
                        error.response.data.message.trim().length > 0
                    ) {
                        setErrorMsg(error.response.data.message);
                    }
                }
            });
    };

    return (
        <div className={styles.reset}>
            <Link to="/" className={styles.logo}>
                SHOPEE
            </Link>

            <form onSubmit={handleSubmitEmail} className={styles.resetForm}>
                <h1 className={styles.resetHeading}>Reset Password</h1>
                <input
                    placeholder="Enter your email"
                    ref={emailRef}
                    className={styles.input}
                />
                {successMsg && (
                    <div className={styles.successMsgContainer}>
                        <CheckCircleIcon fontSize="large" color="success" />
                        <span className={styles.successMsg}>{successMsg}</span>
                    </div>
                )}
                {errorMsg && (
                    <div className={styles.errorMessageContainer}>
                        <WarningIcon fontSize="large" color="error" />
                        <span className={styles.errorMessage}>{errorMsg}</span>
                    </div>
                )}
                <button type="submit" className={styles.resetButton}>Submit</button>
            </form>

            <div className={styles.loginContainer}>
                <span>Don't have an account?&nbsp;&nbsp;<Link to="/register">Sign Up</Link></span>
                <span>Already have an account?&nbsp;&nbsp;<Link to="/login">Login</Link></span>
            </div>
        </div>
    );
};

export default ResetPassword;