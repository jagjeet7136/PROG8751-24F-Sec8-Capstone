import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import axios from 'axios';
import styles from './EmailVerification.module.css';
import WarningIcon from '@mui/icons-material/Warning';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

export const EmailVerification = () => {
    const [searchParams] = useSearchParams();
    const [message, setMessage] = useState("");
    const [validEmailVerification, setValidEmailVerification] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        console.log("Called once");
        const token = searchParams.get('token');
        if (token) {
            axios
                .get("http://localhost:9898/user/verify?token=" + token)
                .then((response) => {
                    setMessage(response.data);
                    setValidEmailVerification(true);
                })
                .catch((error) => {
                    console.log(error);

                    if (error.response) {
                        if (error.response.data?.errors?.length > 0) {
                            setMessage(error.response.data.errors[0]);
                        } else if (
                            error.response.data?.message &&
                            error.response.data.message.trim().length > 0
                        ) {
                            setMessage(error.response.data.message);
                        } else {
                            setMessage("An unknown error occurred.");
                        }
                    } else if (error.message === "Network Error") {
                        setMessage("Server is unreachable. Please try again later.");
                    } else {
                        setMessage("Something went wrong.");
                    }

                    setValidEmailVerification(false);
                }).finally(() => {
                    setLoading(false);
                });
        } else {
            setMessage('Invalid Token.');
            setValidEmailVerification(false);
        }
    }, [searchParams]);

    return (
        <div className={styles.emailVerification}>
            <Link to="/" className={styles.logo}>
                SHOPEE
            </Link>

            <span className={styles.heading}>SHOPEE Account Verification Status</span>
            {loading ? <p className={styles.loadingMessage}>Verifying, Please Wait!...</p>
                : !validEmailVerification ?
                    <div className={styles.messageAndButtonContainer}>
                        <div className={styles.errorMessageContainer}>
                            <WarningIcon fontSize="large" color="error" />
                            <span className={styles.errorMessage}>{message}</span>
                        </div>
                        <Link to="/register" className={styles.tryButton}>Try Sign Up Again</Link>
                    </div>
                    :
                    <div className={styles.messageAndButtonContainer}>
                        <div className={styles.successMsgContainer}>
                            <CheckCircleIcon fontSize="large" color="success" />
                            <span className={styles.successMsg}>{message}</span>
                        </div>
                        <Link to="/login" className={styles.tryButton}>Try Logging In</Link>
                    </div>
            }
        </div>
    );
};
