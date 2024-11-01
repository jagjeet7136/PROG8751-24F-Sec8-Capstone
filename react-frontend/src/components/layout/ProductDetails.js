import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import styles from "./ProductDetails.module.css";
import { Header } from "./Header";
import Footer from "./Footer";
import icon from "../../icons/logo-transparent-png.png";
import axios from "axios";

export const ProductDetails = () => {
    const { productId } = useParams();
    const [product, setProduct] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [message, setMessage] = useState("");
    const token = localStorage.getItem("token");

    useEffect(() => {
        fetch(`http://localhost:9898/products/${productId}`)
            .then((response) => response.json())
            .then((data) => setProduct(data))
            .catch((error) => console.error("Error fetching product details:", error));
    }, [productId]);

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString("en-US", {
            month: "long",
            year: "2-digit"
        });
    };

    const handleIncrement = () => {
        setQuantity(prevQuantity => (prevQuantity < 10 ? prevQuantity + 1 : prevQuantity));
    };

    const handleDecrement = () => {
        setQuantity(prevQuantity => (prevQuantity > 1 ? prevQuantity - 1 : prevQuantity));
    };

    const handleAddToCart = () => {
        axios.post("http://localhost:9898/cart", null, {
            headers: {
                Authorization: token
            },
            params: {
                productId: productId,
                quantity: quantity
            }
        })
            .then((res) => {
                setMessage("Product added to cart successfully!");
            })
            .catch((error) => {
                setMessage("Failed to add product to cart.");
                console.error("Error adding product to cart:", error);
            });
    };

    if (!product) {
        return <p>Loading...</p>;
    }

    return (
        <div>
            <Header textColor="greenText" icon={icon} />
            <div className={styles.productDetailsContainer}>
                <div className={styles.productImageSection}>
                    <img src={product.imageUrl} alt={product.name} className={styles.productImage} />
                </div>
                <div className={styles.productInfoSection}>
                    <h2 className={styles.productTitle}>{product.name}</h2>
                    <p className={styles.productPrice}>Discounted Price: ${product.discountedPrice}</p>
                    <p className={styles.originalPrice}>Original Price: ${product.price}</p>
                    <p className={styles.productStock}>In Stock: {product.stock}</p>
                    <p className={styles.productDescription}>{product.description}</p>
                    <p className={styles.productLongDescription}>{product.longDescription}</p>
                    <p className={styles.productManufacturer}>Manufacturer: {product.manufacturer}</p>
                    <p className={styles.productRating}>Rating: {product.rating}</p>
                    <p className={styles.productDate}>Added on: {formatDate(product.createdAt)}</p>
                    <div className={styles.quantitySelector}>
                        <button onClick={handleDecrement} className={styles.quantityButton}>-</button>
                        <input
                            type="text"
                            value={quantity}
                            readOnly
                            className={styles.quantityInput}
                        />
                        <button onClick={handleIncrement} className={styles.quantityButton}>+</button>
                    </div>
                    <button onClick={handleAddToCart} className={styles.addToCartButton}>Add to Cart</button>
                    {message && <p className={styles.message}>{message}</p>}
                </div>
            </div>
            <Footer />
        </div>
    );
};
