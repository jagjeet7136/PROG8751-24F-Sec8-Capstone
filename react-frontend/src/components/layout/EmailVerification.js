import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import axios from 'axios';
import styles from './EmailVerification.module.css';

export const EmailVerification = () => {
    const [searchParams] = useSearchParams();
    const [message, setMessage] = useState('Verifying your email...');

    useEffect(() => {
        console.log("Called once");
        const token = searchParams.get('token');
        if (token) {
            axios
                .get("http://localhost:9898/user/verify?token=" + token)
                .then((response) => {
                    setMessage(response.data);
                })
                .catch((error) => {
                    if (error.response && error.response.data) {
                        setMessage(error.response.data);
                    } else {
                        setMessage('Verification failed due to network error or unexpected response.');
                    }
                });
        } else {
            setMessage('No token found in URL.');
        }
    }, []);

    return (
        <div className="email-verification">
            <h2>Email Verification</h2>
            <p>{message}</p>
        </div>
    );
};
