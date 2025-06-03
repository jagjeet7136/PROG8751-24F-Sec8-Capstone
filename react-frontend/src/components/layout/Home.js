import React, { useEffect, useState } from "react";
import { Header } from "./Header";
import styles from "./Home.module.css";
import icon from "../../assets/icons/logo-transparent-png.png";
import bannerSmall from "../../assets/images/banner1-400w.avif";
import bannerMedium from "../../assets/images/banner-medium.avif";
import bannerLarge from "../../assets/images/banner-large.avif";
import Footer from "./Footer";
import { Link } from "react-router-dom";
import axios from "axios";

export const Home = () => {
  const [products, setProducts] = useState([]);
  const [message, setMessage] = useState("");
  const [addedProducts, setAddedProducts] = useState([]);
  const token = localStorage.getItem("token");

  useEffect(() => {
    fetch("http://localhost:9898/products")
      .then((response) => response.json())
      .then((data) => setProducts(data))
      .catch((error) => console.error("Error fetching products:", error));
  }, []);

  const getProductsForSection = (startIndex, count) => {
    return products.slice(startIndex, startIndex + count);
  };

  const handleAddToCart = (productId) => {
    axios
      .post("http://localhost:9898/cart", null, {
        headers: {
          Authorization: token,
        },
        params: {
          productId: productId,
          quantity: 1,
        },
      })
      .then((res) => {
        setAddedProducts((prevState) => [...prevState, productId]);
        setMessage("Product added to cart successfully!");
      })
      .catch((error) => {
        setMessage("Failed to add product to cart.");
        console.error("Error adding product to cart:", error);
      });
  };

  const isProductInCart = (productId) => {
    return addedProducts.includes(productId);
  };

  return (
    <div className={styles.homeComp}>
      <Header textColor="greenText" icon={icon} />
      <div className={styles.bannerSlideshow}>
        <picture>
          <source media="(max-width: 400px)" srcSet={bannerMedium} />
          <source media="(max-width: 1024px)" srcSet={bannerMedium} />
          <img src={bannerLarge} alt="Promotional Banner" className={styles.bannerImage} />
        </picture>
      </div>
      <div className={styles.home}>
        {/* Newly Released */}
        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Newly Released</h2>
          <div className={styles.productGrid}>
            {getProductsForSection(0, 8).map((product) => (
              <div key={product.id} className={styles.productCard}>
                <Link
                  to={`/products/${product.id}`}
                  className={styles.productLink}
                >
                  <img
                    src={product.imageUrl}
                    alt={product.name}
                    className={styles.productImage}
                  />
                  <h3 className={styles.productName}>{product.name}</h3>
                  <p className={styles.productDescription}>
                    {product.description}
                  </p>
                  <p className={styles.productPrice}>
                    ${product.discountedPrice}
                  </p>
                </Link>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleAddToCart(product.id);
                  }}
                  className={`${styles.addToCartButton} ${isProductInCart(product.id) ? styles.added : ""
                    }`}
                >
                  {isProductInCart(product.id) ? "✔️" : "Add to Cart"}
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Top Selling */}
        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Top Selling</h2>
          <div className={styles.productGrid}>
            {getProductsForSection(8, 8).map((product) => (
              <div key={product.id} className={styles.productCard}>
                <Link
                  to={`/products/${product.id}`}
                  className={styles.productLink}
                >
                  <img
                    src={product.imageUrl}
                    alt={product.name}
                    className={styles.productImage}
                  />
                  <h3 className={styles.productName}>{product.name}</h3>
                  <p className={styles.productDescription}>
                    {product.description}
                  </p>
                  <p className={styles.productPrice}>
                    ${product.discountedPrice}
                  </p>
                </Link>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleAddToCart(product.id);
                  }}
                  className={`${styles.addToCartButton} ${isProductInCart(product.id) ? styles.added : ""
                    }`}
                >
                  {isProductInCart(product.id) ? "✔️" : "Add to Cart"}
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Exclusive Deals */}
        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Exclusive Deals</h2>
          <div className={styles.productGrid}>
            {getProductsForSection(16, 8).map((product) => (
              <div key={product.id} className={styles.productCard}>
                <Link
                  to={`/products/${product.id}`}
                  className={styles.productLink}
                >
                  <img
                    src={product.imageUrl}
                    alt={product.name}
                    className={styles.productImage}
                  />
                  <h3 className={styles.productName}>{product.name}</h3>
                  <p className={styles.productDescription}>
                    {product.description}
                  </p>
                  <p className={styles.productPrice}>
                    ${product.discountedPrice}
                  </p>
                </Link>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleAddToCart(product.id);
                  }}
                  className={`${styles.addToCartButton} ${isProductInCart(product.id) ? styles.added : ""
                    }`}
                >
                  {isProductInCart(product.id) ? "✔️" : "Add to Cart"}
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Top Rated */}
        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Top Rated</h2>
          <div className={styles.productGrid}>
            {getProductsForSection(24, 8).map((product) => (
              <div key={product.id} className={styles.productCard}>
                <Link
                  to={`/products/${product.id}`}
                  className={styles.productLink}
                >
                  <img
                    src={product.imageUrl}
                    alt={product.name}
                    className={styles.productImage}
                  />
                  <h3 className={styles.productName}>{product.name}</h3>
                  <p className={styles.productDescription}>
                    {product.description}
                  </p>
                  <p className={styles.productPrice}>
                    ${product.discountedPrice}
                  </p>
                </Link>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleAddToCart(product.id);
                  }}
                  className={`${styles.addToCartButton} ${isProductInCart(product.id) ? styles.added : ""
                    }`}
                >
                  {isProductInCart(product.id) ? "✔️" : "Add to Cart"}
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
};
