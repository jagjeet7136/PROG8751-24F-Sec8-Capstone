import { useRef, useState } from "react";
import styles from "./Register.module.css";
import { Link } from "react-router-dom";
import WarningIcon from '@mui/icons-material/Warning';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

export const Register = () => {
    const userFullName = useRef();
    const email = useRef();
    const password = useRef();
    const [successMsg, setSuccessMsg] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const [userCreated, setUserCreated] = useState(false);
    const [showPassword, setShowPassword] = useState(false);

    const validateForm = () => {
        if (!userFullName.current.value.trim()) {
            setErrorMsg("Full Name is required");
            return false;
        }
        if (!/\S+@\S+\.\S+/.test(email.current.value)) {
            setErrorMsg("Invalid email format");
            return false;
        }
        if (password.current.value.length < 6) {
            setErrorMsg("Password must be at least 6 characters");
            return false;
        }
        setErrorMsg("");
        return true;
    };

    const onSubmit = async (event) => {
        event.preventDefault();
        setErrorMsg("");
        setSuccessMsg("");
        if (!validateForm()) return;

        const newUser = {
            userFullName: userFullName.current.value,
            email: email.current.value,
            password: password.current.value
        };

        try {
            const response = await fetch("http://localhost:9898/user/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(newUser),
            });

            if (!response.ok) {
                const errorData = await response.json();
                let caughtErrorMessage = errorData?.errors?.[0] || errorData?.message || "Some error occurred!";
                throw new Error(caughtErrorMessage);
            }

            userFullName.current.value = "";
            email.current.value = "";
            password.current.value = "";

            const responseMessage = await response.text();
            setUserCreated(true);
            setSuccessMsg(responseMessage);
        } catch (error) {
            setErrorMsg(error.message);
        }
    };

    return (
        <div className={styles.register}>
            <Link to="/" className={styles.logo}>
                SHOPEE
            </Link>
            <form onSubmit={onSubmit} className={styles.registerForm}>
                <h1 className={styles.registerHeading}>Create Account</h1>
                <input type="text" placeholder="Full Name" ref={userFullName} />
                <input type="email" placeholder="Email" ref={email} />
                <div className={styles.passwordContainer}>
                    <input type={showPassword ? "text" : "password"} placeholder="Password" ref={password} />
                    <span className={styles.passwordToggle} onClick={() => setShowPassword(!showPassword)}>
                        {showPassword ? "Hide" : "Show"}
                    </span>
                </div>
                {successMsg && (
                    <div className={`${styles.successMsgContainer} ${userCreated ? styles.active : ""}`}>
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
                <button type="submit" className={styles.registerButton}>Sign Up</button>
                <span className={styles.loginContainer}>
                    Already have an account?&nbsp;&nbsp;<Link to="/login">Log in</Link>
                </span>
            </form>
        </div>
    );
};
