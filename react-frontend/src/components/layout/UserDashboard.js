import React, { useState } from "react";
import axios from "axios";
import styles from "./AdminDashboard.module.css";
import { Header } from "./Header";
import { useNavigate } from "react-router-dom";
import icon from "../../icons/logo-transparent-png.png";

export const UserDashboard = () => {
    const [userSearchInput, setUserSearchInput] = useState("");
    const [users, setUsers] = useState([]);
    const [orders, setOrders] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    const navigate = useNavigate();
    const goToAdminUserDashboard = () => {
        navigate("/adminDashboard");
    };

    const [orderDetails, setOrderDetails] = useState(null);

    const fetchOrderDetails = (orderId) => {
        const token = localStorage.getItem("adminToken");
        axios
            .get(`http://localhost:9898/orders/${orderId}`, {
                headers: {
                    Authorization: token,
                },
            })
            .then((res) => {
                setOrderDetails(res.data);
            })
            .catch((err) => console.error("Error fetching order details:", err));
    };

    const handleViewOrderDetails = (orderId) => {
        fetchOrderDetails(orderId);
    };

    const fetchUserOrders = (userId) => {
        const token = localStorage.getItem("adminToken");
        axios
            .get(`http://localhost:9898/orders/userOrders/${userId}`, {
                headers: {
                    Authorization: token,
                },
            })
            .then((res) => {
                setOrders(res.data);
            })
            .catch((err) => console.error("Error fetching user orders:", err));
    };

    const handleShowOrders = (userId) => {
        fetchUserOrders(userId);
        setSelectedUser(userId);
    };

    const fetchUsers = (search = "") => {
        const token = localStorage.getItem("adminToken");
        axios
            .get(`http://localhost:9898/user/getUsers?search=${search}`, {
                headers: {
                    Authorization: token,
                },
            })
            .then((res) => {
                setUsers(res.data);
            })
            .catch((err) => console.error("Error fetching users:", err));
    };

    return (
        <div>
            <Header textColor="greenText" icon={icon} />
            <div className={styles.adminDashboard}>
                <h1>Admin Dashboard</h1>
                <button onClick={goToAdminUserDashboard} className={styles.mainButton}>
                    Products Admin User Dashboard
                </button>
                <div className={styles.section}>
                    <h2>Search Users</h2>
                    <div className={styles.searchBar}>
                        <input
                            type="text"
                            placeholder="Search by name or email..."
                            value={userSearchInput}
                            onChange={(e) => setUserSearchInput(e.target.value)}
                        />
                        <button onClick={() => fetchUsers(userSearchInput)}>
                            Search
                        </button>
                    </div>
                </div>

                <div className={styles.list}>
                    {users.map((user) => (
                        <div key={user.id} className={styles.listItem}>
                            <div>
                                <strong>Name:</strong> {user.userFullName} {user.lastName} |{" "}
                                <strong>Email:</strong> {user.username}
                            </div>
                            <button onClick={() => handleShowOrders(user.id)}>
                                Show Orders
                            </button>
                        </div>
                    ))}
                </div>

                {selectedUser && (
                    <div className={styles.ordersSection}>
                        <h2>Orders for User {selectedUser}</h2>
                        <div className={styles.list}>
                            {orders.length === 0 ? (
                                <p>No orders found for this user.</p>
                            ) : (
                                orders.map((order) => (
                                    <div key={order.id} className={styles.listItem}>
                                        <div>
                                            <strong>Order ID:</strong> {order.id} |{" "}
                                            <strong>Total:</strong> ${order.total}
                                        </div>
                                        <div>
                                            <strong>Payment Method:</strong> {order.paymentMethod}
                                        </div>
                                        <button onClick={() => handleViewOrderDetails(order.id)}>
                                            View Details
                                        </button>
                                    </div>
                                ))
                            )}
                        </div>
                    </div>
                )}

                {orderDetails && (
                    <div className={styles.orderDetails}>
                        <h3>Order Details</h3>
                        <p><strong>Order ID:</strong> {orderDetails.id}</p>
                        <p><strong>Customer Name:</strong> {orderDetails.firstName} {orderDetails.lastName}</p>
                        <p><strong>Email:</strong> {orderDetails.email}</p>
                        <p><strong>Phone:</strong> {orderDetails.phone}</p>
                        <p><strong>Address:</strong> {`${orderDetails.address}, ${orderDetails.city}, ${orderDetails.state}, ${orderDetails.postalCode}`}</p>
                        <p><strong>Payment Method:</strong> {orderDetails.paymentMethod}</p>
                        <p><strong>Subtotal:</strong> ${orderDetails.subtotal.toFixed(2)}</p>
                        <p><strong>Tax:</strong> ${orderDetails.tax.toFixed(2)}</p>
                        <p><strong>Shipping Charge:</strong> ${orderDetails.shippingCharge.toFixed(2)}</p>
                        <p><strong>Total:</strong> ${orderDetails.total.toFixed(2)}</p>
                        <p><strong>Order Items:</strong></p>
                        <ul>
                            {orderDetails.orderItems.map((item) => (
                                <li key={item.id} className={styles.orderItem}>
                                    <p><strong>Product Name:</strong> {item.product.name}</p>
                                    <div className={styles.productInfo}>
                                        <img
                                            src={item.product.imageUrl}
                                            alt={item.product.name}
                                            className={styles.productImage}
                                        />
                                        <div>

                                            <p><strong>Quantity:</strong> {item.quantity}</p>
                                            <p><strong>Price:</strong> ${item.price.toFixed(2)}</p>
                                        </div>
                                    </div>
                                </li>
                            ))}
                        </ul>
                        <button onClick={() => setOrderDetails(null)}>Close</button>
                    </div>
                )}

            </div>
        </div>
    );
};
