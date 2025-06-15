import React, { useEffect, useState } from "react";
import { Header } from "./Header";
import styles from "./Home.module.css";
import icon from "../../assets/icons/logo-transparent-png.png";
import bannerMedium from "../../assets/images/banner-medium.avif";
import bannerLarge from "../../assets/images/banner-large.avif";
import Footer from "./Footer";
import { Link } from "react-router-dom";

export const Home = () => {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetch("http://localhost:9898/products")
      .then((response) => response.json())
      .then((data) => setProducts(data))
      .catch((error) => console.error("Error fetching products:", error));
  }, []);

  const getProductsForSection = (startIndex, count) => {
    return products.slice(startIndex, startIndex + count);
  };

  const renderStars = (rating) => {
    const fullStars = Math.floor(rating);
    const halfStar = rating % 1 >= 0.5;
    const emptyStars = 5 - fullStars - (halfStar ? 1 : 0);

    const stars = [];
    for (let i = 0; i < fullStars; i++) {
      stars.push(<span key={`full-${i}`} style={{ color: "#FFD700" }}>★</span>);
    }
    if (halfStar) {
      stars.push(<span key="half" style={{ color: "#FFD700" }}>☆</span>);
    }
    for (let i = 0; i < emptyStars; i++) {
      stars.push(<span key={`empty-${i}`} style={{ color: "#ccc" }}>★</span>);
    }
    return stars;
  };

  const renderProductCard = (product) => (
    <div key={product.id} className={styles.productCard}>
      <Link to={`/products/${product.id}`} className={styles.productLink}>
        <img
          src={product.imageUrl}
          alt={product.name}
          className={styles.productImage}
        />
        <h3 className={styles.productName}>{product.name}</h3>
        <p className={styles.productDescription}>{product.description}</p>
        <p className={styles.productPrice}>${product.discountedPrice}</p>
      </Link>
      <div className={styles.ratingLine}>
        {renderStars(product.averageRating || 0)}
        <span style={{ marginLeft: "8px", fontSize: "0.9rem", color: "#666" }}>
          ({product.totalRatings || 0})
        </span>
      </div>
    </div>
  );

  return (
    <div className={styles.homeComp}>
      <Header textColor="greenText" icon={icon} />
      <div className={styles.bannerSlideshow}>
        <picture>
          <source media="(max-width: 400px)" srcSet={bannerMedium} />
          <source media="(max-width: 1024px)" srcSet={bannerLarge} />
          <img src={bannerLarge} alt="Promotional Banner" className={styles.bannerImage} />
        </picture>
      </div>
      <div className={styles.home}>
        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Newly Released</h2>
          <div className={styles.productGrid}>
            {getProductsForSection(0, 8).map(renderProductCard)}
          </div>
        </div>

        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Top Selling</h2>
          <div className={styles.productGrid}>
            {getProductsForSection(8, 8).map(renderProductCard)}
          </div>
        </div>

        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Exclusive Deals</h2>
          <div className={styles.productGrid}>
            {getProductsForSection(16, 8).map(renderProductCard)}
          </div>
        </div>

        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Top Rated</h2>
          <div className={styles.productGrid}>
            {getProductsForSection(24, 8).map(renderProductCard)}
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
};
