import React, { useEffect, useState } from "react";
import { useLocation, Link } from "react-router-dom";
import axios from "axios";
import styles from "./SearchResults.module.css";
import { Header } from "./Header";
import Footer from "./Footer";
import icon from "../../icons/logo-transparent-png.png";

const SearchResults = () => {
  const { search } = useLocation();
  const queryParams = new URLSearchParams(search);
  const searchQuery = queryParams.get("query") || "";
  const [products, setProducts] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    if (searchQuery) {
      fetchProducts(currentPage, searchQuery);
    }
  }, [searchQuery, currentPage]);

  const fetchProducts = (page = 1, query) => {
    const token = localStorage.getItem("adminToken");
    axios
      .get(
        `http://localhost:9898/products/getProducts?page=${page}&size=20&search=${query}`,
        {
          headers: {
            Authorization: token,
          },
        }
      )
      .then((res) => {
        setProducts(res.data.content);
        setTotalPages(res.data.totalPages);
      })
      .catch((err) => console.error("Error fetching products:", err));
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  return (
    <div>
      <Header textColor="greenText" icon={icon} />
      <div className={styles.searchResults}>
        <h1>Products Searched For "{searchQuery}"</h1>
        {products.length > 0 ? (
          <div>
            <div className={styles.productGrid}>
              {products.map((product) => (
                <Link
                  to={`/products/${product.id}`}
                  className={styles.productLink}
                  key={product.id}
                >
                  <div className={styles.productCard}>
                    <img
                      src={product.imageUrl || "/placeholder.jpg"}
                      alt={product.name}
                      className={styles.productImage}
                    />
                    <div className={styles.productName}>{product.name}</div>
                    <div className={styles.productDescription}>
                      {product.description || "No description available"}
                    </div>
                    <div className={styles.productPrice}>
                      ${product.price.toFixed(2)}
                    </div>
                  </div>
                </Link>
              ))}
            </div>
            <div className={styles.pagination}>
              {Array.from({ length: totalPages }, (_, index) => (
                <button
                  key={index}
                  onClick={() => handlePageChange(index)}
                  className={index === currentPage ? styles.activePage : ""}
                >
                  {index + 1}
                </button>
              ))}
            </div>
          </div>
        ) : (
          <p>No products found for "{searchQuery}".</p>
        )}
      </div>
      <Footer />
    </div>
  );
};
export default SearchResults;
