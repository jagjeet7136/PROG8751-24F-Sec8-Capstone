import { useRef, useState } from "react";
import styles from "./Register.module.css";
import todoSmallIcon from "../../icons/logo-transparent-png.png";
import { Link } from "react-router-dom";

export const Register = () => {
    const userFullName = useRef();
    const email = useRef();
    const password = useRef();
    const confirmPassword = useRef();
    const securityAnswer = useRef();
    const [securityQuestion, setSecurityQuestion] = useState("");
    const [successMsg, setSuccessMsg] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const [isFormValid, setIsFormValid] = useState(true);
    const [userCreated, setUserCreated] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

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
        if (password.current.value !== confirmPassword.current.value) {
            setErrorMsg("Passwords do not match");
            return false;
        }
        if (!securityQuestion) {
            setErrorMsg("Please select a security question");
            return false;
        }
        if (!securityAnswer.current.value.trim()) {
            setErrorMsg("Security answer is required");
            return false;
        }
        setErrorMsg(""); // Clear errors
        return true;
    };

    const onSubmit = async (event) => {
        event.preventDefault(); // Prevent default form submission
        if (!validateForm()) {
            setIsFormValid(false);
            return;
        }

        const newUser = {
            userFullName: userFullName.current.value,
            email: email.current.value,
            password: password.current.value,
            confirmPassword: confirmPassword.current.value,
            securityQuestion,
            securityAnswer: securityAnswer.current.value,
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
            setSecurityQuestion("");
            securityAnswer.current.value = "";

            setUserCreated(true);
            setSuccessMsg("User created successfully!");
        } catch (error) {
            setErrorMsg(error.message);
            setIsFormValid(false);
        }
    };

    const securityQuestions = [
        "What was the name of your first pet?",
        "What is your mother's maiden name?",
        "What was the name of your elementary school?",
        "What is your favorite food?",
        "What city were you born in?",
    ];

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
                <div className={styles.confirmPasswordContainer}>
                    <input type={showConfirmPassword ? "text" : "password"} placeholder="Confirm Password" ref={confirmPassword} />
                    <span className={styles.confirmPasswordToggle} onClick={() => setShowConfirmPassword(!showConfirmPassword)}>
                        {showConfirmPassword ? "Hide" : "Show"}
                    </span>
                </div>
                <select
                    value={securityQuestion}
                    onChange={(e) => setSecurityQuestion(e.target.value)}
                    className={styles.securityQuestion}
                >
                    <option value="">Select a Security Question</option>
                    {securityQuestions.map((question, index) => (
                        <option key={index} value={question}>{question}</option>
                    ))}
                </select>
                <input
                    type="text"
                    placeholder="Answer"
                    ref={securityAnswer}
                    className={styles.securityAnswer}
                />
                <div className={`${styles.successMsgContainer} ${userCreated ? styles.active : ""}`}>
                    <h6 className={styles.successMsg}>{successMsg}</h6>
                </div>
                <div className={`${styles.errorMessageContainer} ${!isFormValid ? styles.active : ""}`}>
                    <h6 className={styles.errorMessage}>{errorMsg}</h6>
                </div>
                <button type="submit" className={styles.registerButton}>Sign Up</button>
                <h6 className={styles.loginContainer}>Already have an account?&nbsp;&nbsp;<Link to="/login">Log in</Link></h6>
            </form>
        </div>
    );
};
