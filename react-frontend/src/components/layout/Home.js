import React, { useEffect, useState } from "react";
import { Header } from "./Header";
import styles from "./Home.module.css";
import icon from "../../icons/logo-transparent-png.png";
import banner1 from "../../images/banner1.jpg";
import banner2 from "../../images/banner2.webp";
import Footer from "./Footer";

export const Home = () => {
    const [currentBanner, setCurrentBanner] = useState(0);
    const [products, setProducts] = useState([]);
    const banners = [banner1, banner2];

    useEffect(() => {
        const interval = setInterval(() => {
            setCurrentBanner((prevBanner) => (prevBanner + 1) % banners.length);
        }, 3000);
        return () => clearInterval(interval);
    }, [banners.length]);

    useEffect(() => {
        fetch("http://localhost:9898/products")
            .then((response) => response.json())
            .then((data) => setProducts(data))
            .catch((error) => console.error("Error fetching products:", error));
    }, []);

    const getProductsForSection = (startIndex, count) => {
        return products.slice(startIndex, startIndex + count);
    };

    return (
        <div className={styles.homeComp}>
            <Header textColor="greenText" icon={icon} />
            <div className={styles.bannerSlideshow}>
                <img src={banners[currentBanner]} alt="Banner" className={styles.bannerImage} />
            </div>
            <div className={styles.home}>
                <div className={styles.section}>
                    <h2 className={styles.sectionTitle}>Newly Released</h2>
                    <div className={styles.productGrid}>
                        {getProductsForSection(0, 8).map((product) => (
                            <div key={product.id} className={styles.productCard}>
                                <img src={product.imageUrl} alt={product.name} className={styles.productImage} />
                                <h3 className={styles.productName}>{product.name}</h3>
                                <p className={styles.productDescription}>{product.description}</p>
                                <p className={styles.productPrice}>${product.discountedPrice}</p>
                            </div>
                        ))}
                    </div>
                </div>

                <div className={styles.section}>
                    <h2 className={styles.sectionTitle}>Top Selling</h2>
                    <div className={styles.productGrid}>
                        {getProductsForSection(8, 8).map((product) => (
                            <div key={product.id} className={styles.productCard}>
                                <img src={product.imageUrl} alt={product.name} className={styles.productImage} />
                                <h3 className={styles.productName}>{product.name}</h3>
                                <p className={styles.productDescription}>{product.description}</p>
                                <p className={styles.productPrice}>${product.discountedPrice}</p>
                            </div>
                        ))}
                    </div>
                </div>

                <div className={styles.section}>
                    <h2 className={styles.sectionTitle}>Exclusive Deals</h2>
                    <div className={styles.productGrid}>
                        {getProductsForSection(16, 8).map((product) => (
                            <div key={product.id} className={styles.productCard}>
                                <img src={product.imageUrl} alt={product.name} className={styles.productImage} />
                                <h3 className={styles.productName}>{product.name}</h3>
                                <p className={styles.productDescription}>{product.description}</p>
                                <p className={styles.productPrice}>${product.discountedPrice}</p>
                            </div>
                        ))}
                    </div>
                </div>

                <div className={styles.section}>
                    <h2 className={styles.sectionTitle}>Top Rated</h2>
                    <div className={styles.productGrid}>
                        {getProductsForSection(24, 8).map((product) => (
                            <div key={product.id} className={styles.productCard}>
                                <img src={product.imageUrl} alt={product.name} className={styles.productImage} />
                                <h3 className={styles.productName}>{product.name}</h3>
                                <p className={styles.productDescription}>{product.description}</p>
                                <p className={styles.productPrice}>${product.discountedPrice}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
            <Footer />
        </div>
    );
};
