import React, { useEffect, useState, useCallback } from "react";
import axios from "axios";
import styles from "./Cart.module.css";

export const Cart = () => {
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const SHIPPING_CHARGE = 8.0;
    const TAX_RATE = 0.13;

    useEffect(() => {
        // Fetch cart items when the component loads
        const fetchCartItems = async () => {
            try {
                const response = await axios.get("http://localhost:9898/cart/getCartItems");
                setCartItems(response.data);
            } catch (error) {
                console.error("Failed to fetch cart items", error);
            } finally {
                setLoading(false);
            }
        };
        fetchCartItems();
    }, []);

    const handleRemoveFromCart = useCallback(async (itemId) => {
        try {
            await axios.delete(`/removeCartItem/${itemId}`);
            // Update cart items immediately after removing an item
            setCartItems((prevItems) => prevItems.filter((item) => item.id !== itemId));
        } catch (error) {
            console.error("Failed to remove item from cart", error);
        }
    }, []);

    const subtotal = cartItems.reduce((total, item) => total + item.price * item.quantity, 0);
    const tax = subtotal * TAX_RATE;
    const total = subtotal + tax + SHIPPING_CHARGE;

    const handleCancel = () => {
        setCartItems([]);
    };

    const handleCheckout = () => {
        // Proceed to checkout logic here (e.g., redirect or call checkout API)
        console.log("Proceeding to checkout...");
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
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
                                    <span className={styles.itemName}>{item.name}</span>
                                    <span className={styles.itemPrice}>${item.price.toFixed(2)}</span>
                                    <span className={styles.itemQuantity}>Qty: {item.quantity}</span>
                                </div>
                                <button
                                    className={styles.removeButton}
                                    onClick={() => handleRemoveFromCart(item.id)}
                                >
                                    Remove from Cart
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

                    <div className={styles.cartActions}>
                        <button className={styles.cancelButton} onClick={handleCancel}>
                            Cancel
                        </button>
                        <button className={styles.checkoutButton} onClick={handleCheckout}>
                            Checkout
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};
