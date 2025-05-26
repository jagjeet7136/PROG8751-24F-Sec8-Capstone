import { Link, useSearchParams } from 'react-router-dom';
import { useEffect, useRef, useState } from 'react';
import axios from 'axios';
import styles from "./NewPassword.module.css";
import WarningIcon from '@mui/icons-material/Warning';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

const NewPassword = () => {
    const [searchParams] = useSearchParams();
    const password = useRef();
    const [tokenValid, setTokenValid] = useState(false);
    const [loading, setLoading] = useState(true);
    const [successMsg, setSuccessMsg] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const [showPassword, setShowPassword] = useState(false);

    const token = searchParams.get("token");

    useEffect(() => {
        setErrorMsg("");
        setSuccessMsg("");
        if (!token) {
            setErrorMsg("No token provided.");
            setLoading(false);
            return;
        }

        axios.get(`http://localhost:9898/user/validate-reset-token?token=${token}`)
            .then(res => {
                if (res.status === 200) {
                    setTokenValid(true);
                } else {
                    setErrorMsg("Invalid or expired token.");
                }
            })
            .catch(error => {
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
            })
            .finally(() => setLoading(false));
    }, [token]);

    const handleSubmit = (e) => {
        e.preventDefault();
        setErrorMsg("");
        setSuccessMsg("");
        if (!token) return;

        axios.post(`http://localhost:9898/user/reset-password?token=${token}&password=${password.current.value}`
        )
            .then(res => {
                setSuccessMsg(res.data);
                password.current.value = ""
            })
            .catch(error => {
                console.log(error);
                setErrorMsg("Some error occurred");
                if (error.response) {
                    if (error.response.data?.errors?.length > 0) {
                        setErrorMsg(error.response.data.errors[0]);
                    } else if (error.response.data.message?.trim().length > 0) {
                        setErrorMsg(error.response.data.message);
                    }
                }
            });
    };

    return (
        <div className={styles.newPassword}>
            <Link to="/" className={styles.logo}>
                SHOPEE
            </Link>
            <h1 className={styles.heading}>Reset Your Password</h1>
            {loading ?
                <p className={styles.loadingMessage}>Loading, Please Wait!...</p>
                :
                <div>
                    {tokenValid ? (
                        <form onSubmit={handleSubmit} className={styles.passwordForm}>
                            <div className={styles.passwordContainer}>
                                <input type={showPassword ? "text" : "password"} placeholder="Password" ref={password} />
                                <span className={styles.passwordToggle} onClick={() => setShowPassword(!showPassword)}>
                                    {showPassword ? "Hide" : "Show"}
                                </span>
                            </div>
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
                            <button type="submit" className={styles.submitButton}>Reset Password</button>
                        </form>
                    ) : (
                        <div>
                            {errorMsg && (
                                <div className={styles.errorMessageContainer}>
                                    <WarningIcon fontSize="large" color="error" />
                                    <span className={styles.errorMessage}>{errorMsg}</span>
                                </div>
                            )}
                        </div>)}
                </div>
            }

        </div>
    );
};

export default NewPassword;
