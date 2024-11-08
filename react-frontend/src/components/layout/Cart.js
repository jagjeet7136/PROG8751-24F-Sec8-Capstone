import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import styles from "./Cart.module.css";
import { Header } from "./Header";
import Footer from "./Footer";
import icon from "../../icons/logo-transparent-png.png";

const Cart = () => {
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const SHIPPING_CHARGE = 8.0;
    const TAX_RATE = 0.13;
    const token = localStorage.getItem("token");
    const [deleted, setDeleted] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchCartItems = async () => {
            try {
                const response = await axios.get("http://localhost:9898/cart/getCartItems", {
                    headers: {
                        Authorization: token,
                    },
                });
                setCartItems(response.data);
            } catch (error) {
                console.error("Failed to fetch cart items", error);
            } finally {
                setLoading(false);
            }
        };
        fetchCartItems();
    }, [deleted]);

    const handleRemoveFromCart = async (itemId) => {
        try {
            await axios.delete(`http://localhost:9898/cart/${itemId}`, 
                {
                headers: {
                    Authorization: token,
                },
            });
            setDeleted(deleted ? false : true);
            setCartItems((prevItems) => prevItems.filter((item) => item.id !== itemId));
        } catch (error) {
            console.error("Failed to remove item from cart", error);
        }
    };

    const subtotal = cartItems.reduce(
        (total, item) => total + (item.product.price * item.quantity),
        0
    );
    const tax = subtotal * TAX_RATE;
    const total = subtotal + tax + SHIPPING_CHARGE;

    const handleProceedToCheckout = () => {
        navigate("/checkout", { state: { cartItems, subtotal, tax, total, SHIPPING_CHARGE } });
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <div className={styles.cart}>
            <Header textColor="greenText" icon={icon} />
            <div className={styles.cartContainer}>
                <h2>Your Cart</h2>
                {cartItems.length === 0 ? (
                    <p>Your cart is empty</p>
                ) : (
                    <div>
                        <ul className={styles.cartItemsList}>
                            {cartItems.map((item) => (
                                <li key={item.id} className={styles.cartItem}>
                                    <div className={styles.itemDetails}>
                                        <img src={item.product.imageUrl} alt={item.name} className={styles.cartItemImage} />
                                        <span className={styles.itemName}>{item.product.name}</span>
                                        <span className={styles.itemPrice}>${item.product.price.toFixed(2)}</span>
                                        <span className={styles.itemQuantity}>Qty: {item.quantity}</span>
                                    </div>
                                    <button
                                        className={styles.removeButton}
                                        onClick={() => handleRemoveFromCart(item.product.id)}
                                    >
                                        Remove
                                    </button>
                                </li>
                            ))}
                        </ul>

                        <div className={styles.summary}>
                            <div className={styles.summaryItem}>
                                <span>Subtotal:</span>
                                <span>${subtotal.toFixed(2)}</span>
                            </div>
                            <div className={styles.summaryItem}>
                                <span>Shipping:</span>
                                <span>${SHIPPING_CHARGE.toFixed(2)}</span>
                            </div>
                            <div className={styles.summaryItem}>
                                <span>Tax (13%):</span>
                                <span>${tax.toFixed(2)}</span>
                            </div>
                            <div className={styles.summaryTotal}>
                                <span>Total:</span>
                                <span>${total.toFixed(2)}</span>
                            </div>
                        </div>

                        <button className={styles.checkoutButton} onClick={handleProceedToCheckout}>
                            Proceed to Checkout
                        </button>
                    </div>
                )}
            </div>
            <Footer />
        </div>
    );
};

export default Cart;