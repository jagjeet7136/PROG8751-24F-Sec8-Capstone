import { useSearchParams } from 'react-router-dom';
import { useState } from 'react';
import axios from 'axios';
import styles from "./NewPassword.module.css";

const NewPassword = () => {
    const [searchParams] = useSearchParams();
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");

    const token = searchParams.get("token");

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!token) {
            setMessage("No token provided.");
            return;
        }

        axios.post(`http://localhost:9898/user/reset-password?token=${token}`, password, {
            headers: { 'Content-Type': 'text/plain' }
        })
            .then(res => setMessage(res.data))
            .catch(err => setMessage(err.response?.data || "Something went wrong."));
    };

    return (
        <div>
            <h2>Reset Your Password</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="password"
                    placeholder="Enter new password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button type="submit">Reset Password</button>
            </form>
            <p>{message}</p>
        </div>
    );
};

export default NewPassword;
