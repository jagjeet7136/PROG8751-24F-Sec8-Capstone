import React, { useState, useEffect } from "react";
import axios from "axios";
import styles from "./AdminDashboard.module.css";
import { Header } from "./Header";
import Footer from "./Footer";
import icon from "../../icons/logo-transparent-png.png";

export const AdminDashboard = () => {
    const [products, setProducts] = useState([]);
    const [productPage, setProductPage] = useState(1);
    const [productTotalPages, setProductTotalPages] = useState(1);
    const [productSearchInput, setProductSearchInput] = useState("");
    const [selectedProduct, setSelectedProduct] = useState(null);
    const [newProduct, setNewProduct] = useState({
        name: "",
        description: "",
        longDescription: "",
        discountedPrice: "",
        price: "",
        imageUrl: "",
        stock: "",
        categoryId: "",
    });

    // Fetch Products
    const fetchProducts = (page = 1) => {
        axios
            .get(`http://localhost:9898/products/getProducts?page=${page}&size=20&search=${productSearchInput}`)
            .then((res) => {
                setProducts(res.data.content);
                setProductTotalPages(res.data.totalPages);
            })
            .catch((err) => console.error("Error fetching products:", err));
    };

    // Handle Search
    const handleSearch = () => {
        fetchProducts(1); // Reset to page 1 for new search
    };

    // Handle Product Details Expansion
    const handleExpandProduct = (productId) => {
        axios
            .get(`http://localhost:9898/products/${productId}`)
            .then((res) => {
                setSelectedProduct(res.data);
            })
            .catch((err) => console.error("Error fetching product details:", err));
    };

    // Handle New Product Creation
    const handleCreateProduct = (e) => {
        e.preventDefault();

        // Get the admin token from localStorage (ensure it's available)
        const token = localStorage.getItem("adminToken");

        if (!token) {
            alert("You are not authorized to create products. Please log in as an admin.");
            return;
        }

        // Create product request
        axios
            .post("http://localhost:9898/products", newProduct, {
                headers: {
                    Authorization: token
                }
            })
            .then((res) => {
                alert("Product created successfully!");
                setNewProduct({
                    name: "",
                    description: "",
                    longDescription: "",
                    discountedPrice: "",
                    price: "",
                    imageUrl: "",
                    stock: "",
                    categoryId: "",
                });
                fetchProducts(productPage);
            })
            .catch((err) => console.error("Error creating product:", err));
    };

    useEffect(() => {
        fetchProducts();
    }, []);

    return (
        <div>
            <Header textColor="greenText" icon={icon} />
            <div className={styles.adminDashboard}>
                <h1>Admin Dashboard</h1>

                {/* Product Search */}
                <div className={styles.section}>
                    <h2>Search Products</h2>
                    <div className={styles.searchBar}>
                        <input
                            type="text"
                            placeholder="Search by name or category..."
                            value={productSearchInput}
                            onChange={(e) => setProductSearchInput(e.target.value)}
                        />
                        <button onClick={handleSearch}>Search</button>
                    </div>
                    <div className={styles.pagination}>
                        <button
                            disabled={productPage <= 1}
                            onClick={() => {
                                setProductPage((prev) => prev - 1);
                                fetchProducts(productPage - 1);
                            }}
                        >
                            Previous
                        </button>
                        <span>
                            Page {productPage} of {productTotalPages}
                        </span>
                        <button
                            disabled={productPage >= productTotalPages}
                            onClick={() => {
                                setProductPage((prev) => prev + 1);
                                fetchProducts(productPage + 1);
                            }}
                        >
                            Next
                        </button>
                    </div>
                    <div className={styles.list}>
                        {products.map((product) => (
                            <div key={product.id} className={styles.listItem}>
                                <div>
                                    <strong>Name:</strong> {product.name} |{" "}
                                    <strong>Price:</strong> ${product.price} |{" "}
                                    <strong>Stock:</strong> {product.stock}
                                </div>
                                <button onClick={() => handleExpandProduct(product.id)}>
                                    View Details
                                </button>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Product Details Expansion */}
                {selectedProduct && (
                    <div className={styles.productDetails}>
                        <h2>Product Details</h2>
                        <p><strong>Name:</strong> {selectedProduct.name}</p>
                        <p><strong>Description:</strong> {selectedProduct.description}</p>
                        <p><strong>Long Description:</strong> {selectedProduct.longDescription}</p>
                        <p><strong>Discounted Price:</strong> ${selectedProduct.discountedPrice}</p>
                        <p><strong>Price:</strong> ${selectedProduct.price}</p>
                        <p><strong>Stock:</strong> {selectedProduct.stock}</p>
                        <img
                            src={selectedProduct.imageUrl}
                            alt={selectedProduct.name}
                            className={styles.productImage}
                        />
                    </div>
                )}

                {/* New Product Form */}
                <div className={styles.addSection}>
                    <h2>Create New Product</h2>
                    <form onSubmit={handleCreateProduct} className={styles.newProductForm}>
                        <input
                            type="text"
                            placeholder="Name"
                            value={newProduct.name}
                            onChange={(e) => setNewProduct({ ...newProduct, name: e.target.value })}
                            required
                        />
                        <input
                            type="text"
                            placeholder="Short Description"
                            value={newProduct.description}
                            onChange={(e) => setNewProduct({ ...newProduct, description: e.target.value })}
                            required
                        />
                        <textarea
                            placeholder="Long Description"
                            value={newProduct.longDescription}
                            onChange={(e) =>
                                setNewProduct({ ...newProduct, longDescription: e.target.value })
                            }
                            required
                        />
                        <input
                            type="number"
                            placeholder="Discounted Price"
                            value={newProduct.discountedPrice}
                            onChange={(e) =>
                                setNewProduct({ ...newProduct, discountedPrice: e.target.value })
                            }
                            required
                        />
                        <input
                            type="number"
                            placeholder="Price"
                            value={newProduct.price}
                            onChange={(e) => setNewProduct({ ...newProduct, price: e.target.value })}
                            required
                        />
                        <input
                            type="text"
                            placeholder="Image URL"
                            value={newProduct.imageUrl}
                            onChange={(e) => setNewProduct({ ...newProduct, imageUrl: e.target.value })}
                            required
                        />
                        <input
                            type="number"
                            placeholder="Stock"
                            value={newProduct.stock}
                            onChange={(e) => setNewProduct({ ...newProduct, stock: e.target.value })}
                            required
                        />
                        <input
                            type="number"
                            placeholder="Category ID"
                            value={newProduct.categoryId}
                            onChange={(e) =>
                                setNewProduct({ ...newProduct, categoryId: e.target.value })
                            }
                            required
                        />
                        <button type="submit">Create Product</button>
                    </form>
                </div>
            </div>
            <Footer />
        </div>
    );
};

// export default AdminDashboard;