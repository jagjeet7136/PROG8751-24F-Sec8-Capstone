import { useRef, useState } from "react";
import styles from "./Register.module.css";
import todoSmallIcon from "../../assets/icons/logo-transparent-png.png";
import { Link } from "react-router-dom";
import HomeIcon from '@mui/icons-material/Warning';

export const Register = () => {
    const userFullName = useRef();
    const email = useRef();
    const password = useRef();
    const [successMsg, setSuccessMsg] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const [isFormValid, setIsFormValid] = useState(true);
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
        setErrorMsg(""); // Clear errors
        return true;
    };

    const onSubmit = async (event) => {
        event.preventDefault();
        setErrorMsg("");
        setSuccessMsg("");
        if (!validateForm()) {
            setIsFormValid(false);
            return;
        }

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
                console.log(errorData);
                let caughtErrorMessage = "Some error occurred!";
                if (errorData.errors && errorData.errors.length > 0) {
                    caughtErrorMessage = errorData.errors[0];
                } else if (errorData.message && errorData.message.trim().length > 0) {
                    caughtErrorMessage = errorData.message;
                }
                throw new Error(caughtErrorMessage);
            }

            userFullName.current.value = "";
            email.current.value = "";
            password.current.value = "";
            const responseMessage = await response.text() + ", Please verify your email.";
            setUserCreated(true);
            setSuccessMsg(responseMessage);
        } catch (error) {
            setErrorMsg(error.message);
            setIsFormValid(false);
        }
    };

    return (
        <div className={styles.register}>
            <Link to="/"><img src={todoSmallIcon} alt="" className={styles.registerIcon}></img></Link>
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
                {successMsg && <div className={`${styles.successMsgContainer} ${userCreated ? styles.active : ""}`}>
                    <span className={styles.successMsg}>{successMsg}</span>
                </div>}
                {errorMsg && <div className={styles.errorMessageContainer}>
                    <HomeIcon fontSize="large" color="error" />
                    <span className={styles.errorMessage}>{errorMsg}</span>
                </div>}
                <button type="submit" className={styles.registerButton}>Sign Up</button>
                <h6 className={styles.loginContainer}>Already have an account?&nbsp;&nbsp;<Link to="/login">Log in</Link></h6>
            </form>
        </div>
    );
};
