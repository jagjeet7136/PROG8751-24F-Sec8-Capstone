import { useRef, useState } from "react";
import styles from "./Register.module.css";
import todoSmallIcon from "../../icons/logo-transparent-png.png";
import { Link } from "react-router-dom";

export const Register = () => {
    const userFullName = useRef();
    const email = useRef();
    const password = useRef();
    const confirmPassword = useRef();
    const [successMsg, setSuccessMsg] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const [isFormValid, setIsFormValid] = useState(true);
    const [userCreated, setUserCreated] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

    const onSubmit = async (event) => {
        event.preventDefault(); // Prevent the default form submission
        if (!isFormValid) {
            setIsFormValid(true);
        }
        if (userCreated) {
            setUserCreated(false);
        }

        const newUser = {
            userFullName: userFullName.current.value,
            email: email.current.value,
            password: password.current.value,
            confirmPassword: confirmPassword.current.value,
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
                let caughtErrorMessage = "Some error occurred!";
                if (errorData.errors && errorData.errors.length > 0) {
                    caughtErrorMessage = errorData.errors[0];
                } else if (errorData.message && errorData.message.trim().length > 0) {
                    caughtErrorMessage = errorData.message;
                }
                throw new Error(caughtErrorMessage);
            }

            // Clear the form fields
            userFullName.current.value = "";
            email.current.value = "";
            password.current.value = "";
            confirmPassword.current.value = "";

            setUserCreated(true);
            setSuccessMsg("User created Successfully");
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
                    <input type={showPassword ? "text" : "password"} placeholder="Password" ref={password}
                        className={styles.password} />
                    <span className={styles.passwordToggle} onClick={() => setShowPassword(!showPassword)}>
                        {showPassword ? "Hide" : "Show"}
                    </span>
                </div>
                <div className={`${styles.confirmPasswordContainer}`}>
                    <input type={showConfirmPassword ? "text" : "password"} placeholder="Confirm Password" ref={confirmPassword}
                        className={`${styles.confirmPassword} ${isFormValid ? styles.confirmPasswordMargin : styles.confirmPasswordNoMargin}
                        ${userCreated ? styles.confirmPasswordNoMargin : styles.confirmPasswordMargin}`} />
                    <span className={styles.confirmPasswordToggle} onClick={() => setShowConfirmPassword(!showConfirmPassword)}>
                        {showConfirmPassword ? "Hide" : "Show"}
                    </span>
                </div>
                <div className={`${styles.successMsgContainer}
                    ${userCreated ? styles.successMsgContainer + " " + styles.active : ""}`}>
                    <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQLXFtnDBVOuZqvGW2E-Px5DdU8XU9nSoE9dg&usqp=CAU" alt=""></img>
                    <h6 className={styles.successMsg}>{successMsg}</h6>
                </div>
                <div className={`${styles.errorMessageContainer}
                 ${!isFormValid ? styles.errorMessageContainer + " " + styles.active : ""}`}>
                    <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR9YISfL4Lm8FJPRneGwEq8_-
                    9Nim7YeuMJMw&usqp=CAU"
                        alt=""></img>
                    <h6 className={styles.errorMessage}>{errorMsg}</h6>
                </div>

                <button type="submit" className={styles.registerButton}>Sign Up</button>
                <h6 className={styles.loginContainer}>Already have an account?&nbsp;&nbsp;<Link to="/login">Log in</Link></h6>
            </form>
            <h6 className={styles.tPContainer}><Link to="/about">About and Information</Link></h6>
        </div>
    );
}
