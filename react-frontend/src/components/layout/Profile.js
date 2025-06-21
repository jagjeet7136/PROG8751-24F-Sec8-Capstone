import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./Profile.module.css";  // Import the CSS module
import axios from "axios";
import { Header } from "./Header";
import Footer from "./Footer";
import icon from "../../assets/icons/logo-transparent-png.png";

const Profile = () => {
    const [user, setUser] = useState(null);
    const [newPassword, setNewPassword] = useState("");
    const [oldPassword, setOldPassword] = useState("");
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const username = localStorage.getItem("username");
    const token = localStorage.getItem("token");
    const [orders, setOrders] = useState([]);
    const [expandedOrderId, setExpandedOrderId] = useState(null);

    // Fetch user details when the component mounts
    useEffect(() => {
        if (!token) {
            navigate("/login"); // Redirect to login if token is not found
        }

        const fetchUserDetails = async () => {
            try {
                const response = await fetch("http://localhost:9898/user/getUser?username=" + username, {
                    method: "GET",
                    headers: {
                        Authorization: token,
                    },
                });

                if (response.ok) {
                    const data = await response.json();
                    setUser(data);
                } else {
                    const errorData = await response.json();
                    setError(errorData.message);
                }
            } catch (err) {
                setError("Failed to fetch user details");
            }
        };

        const fetchOrders = async () => {
            try {
                const response = await axios.get("http://localhost:9898/orders", {
                    headers: { Authorization: token },
                });
                setOrders(response.data); // Orders data
            } catch (error) {
                console.error("Failed to fetch orders", error);
            }
        };

        fetchOrders();
        fetchUserDetails();
    }, [token]);

    const handlePasswordChange = async () => {
        if (!newPassword || !oldPassword) {
            setError("Both fields are required.");
            return;
        }

        const token = localStorage.getItem("token");
        const response = await fetch("http://localhost:9898/user/changePassword", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: token,
            },
            body: JSON.stringify({ oldPassword, newPassword }),
        });

        if (response.ok) {
            alert("Password changed successfully!");
            setOldPassword("");
            setNewPassword("");
        } else {
            const errorData = await response.json();
            setError(errorData.message || "Failed to change password.");
        }
    };

    const handleGenerateInvoice = async (orderId) => {
        try {
            const response = await fetch(`http://localhost:9898/orders/invoice/generate/${orderId}`, {
                method: "GET",
                headers: {
                    Authorization: token,
                },
            });

            if (!response.ok) {
                throw new Error("Failed to generate the invoice.");
            }

            const blob = await response.blob(); // Convert the response to a blob
            const url = window.URL.createObjectURL(blob); // Create a URL for the blob
            const link = document.createElement("a"); // Create a temporary link element
            link.href = url;
            link.download = `Invoice_${orderId}.pdf`; // Set the filename
            document.body.appendChild(link);
            link.click(); // Trigger the download
            document.body.removeChild(link); // Clean up the link element
            window.URL.revokeObjectURL(url); // Revoke the URL
        } catch (error) {
            console.error("Error generating the invoice:", error);
            alert("Failed to download the invoice. Please try again.");
        }
    };

    const getFormattedOrderDate = (orderDateArray) => {
        if (!Array.isArray(orderDateArray) || orderDateArray.length < 3) return "Invalid Date";
        const [year, month, day, hour = 0, minute = 0, second = 0] = orderDateArray;
        return new Date(year, month - 1, day, hour, minute, second).toLocaleDateString();
    };

    const handleToggleOrder = (orderId) => {
        setExpandedOrderId(expandedOrderId === orderId ? null : orderId);
    };

    if (error) {
        return <div className={styles.error}>{error}</div>;
    }

    if (!user) {
        return <div>Loading...</div>;
    }

    return (
        <div><Header textColor="greenText" icon={icon} />
            <div className={styles.profileContainer}>
                <h2>Profile</h2>
                <div className={styles.userInfo}>
                    <p>Name: {user.userFullName}</p>
                    <p>Email: {user.username}</p>
                </div>

                <div className={styles.passwordChange}>
                    <h3>Change Password</h3>
                    <label htmlFor="oldPassword">Old Password:</label>
                    <input
                        type="password"
                        id="oldPassword"
                        value={oldPassword}
                        onChange={(e) => setOldPassword(e.target.value)}
                        className={styles.inputField}
                    />
                    <label htmlFor="newPassword">New Password:</label>
                    <input
                        type="password"
                        id="newPassword"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        className={styles.inputField}
                    />
                    <button onClick={handlePasswordChange} className={styles.changePasswordButton}>
                        Change Password
                    </button>
                </div>

                <div className={styles.ordersList}>
                    <h3 className={styles.ordersHeader}>Order History</h3>
                    {orders.length === 0 ? (
                        <p className={styles.noOrders}>No orders found</p>
                    ) : (
                        orders.map((order) => (

                            <div key={order.id} className={styles.orderItem}>
                                <div className={styles.orderSummary} onClick={() => handleToggleOrder(order.id)}>
                                    <div className={styles.orderSummaryText}>
                                        <p className={styles.orderId}>Order ID: {order.id}</p>
                                        <p className={styles.orderTotal}>Total: ${order.total}</p>
                                        <p className={styles.orderDate}>Date: {getFormattedOrderDate(order.orderDate)}</p>
                                    </div>
                                    <span className={styles.toggleDetails}>{expandedOrderId === order.id ? 'Hide' : 'Show'} Details</span>

                                </div>

                                {expandedOrderId === order.id && (
                                    <div className={styles.orderDetails}>
                                        <h4 className={styles.detailsHeader}>Shipping Details</h4>
                                        <p className={styles.shippingInfo}>{order.firstName} {order.lastName}</p>
                                        <p className={styles.shippingInfo}>{order.address}, {order.city}, {order.state}, {order.postalCode}</p>
                                        <p className={styles.shippingInfo}>Email: {order.email}</p>
                                        <p className={styles.shippingInfo}>Phone: {order.phone}</p>
                                        <p className={styles.shippingInfo}>Payment Method: {order.paymentMethod}</p>

                                        <h4 className={styles.detailsHeader}>Ordered Products</h4>
                                        <ul className={styles.orderItemsList}>
                                            {order.orderItems.map((item) => (
                                                <li key={item.id} className={styles.orderItemDetail}>
                                                    <div className={styles.orderItemInfo}>
                                                        <img
                                                            src={item.product.imageUrl}
                                                            alt={item.product.name}
                                                            className={styles.productImage}
                                                        />
                                                        <div className={styles.productInfo}>
                                                            <p className={styles.productName}>Product: {item.product.name}</p>
                                                            <p className={styles.productQuantity}>Quantity: {item.quantity}</p>
                                                            <p className={styles.productPrice}>Price: ${item.price}</p>
                                                        </div>
                                                    </div>
                                                </li>
                                            ))}
                                        </ul>
                                        <button
                                            className={styles.generateInvoiceBtn}
                                            onClick={() => handleGenerateInvoice(order.id)}
                                        >
                                            Generate Invoice
                                        </button>
                                    </div>
                                )}
                            </div>
                        ))
                    )}
                </div>

            </div>
            <Footer />
        </div>
    );
};

export default Profile;
