import React, { useState, useEffect } from "react";
import axios from "axios";
import styles from "./AdminDashboard.module.css";
import { Header } from "./Header";
import Footer from "./Footer";
import icon from "../../icons/logo-transparent-png.png";

export const AdminDashboard = () => {
    const [errors, setErrors] = useState([]);
    const [userSearchInput, setUserSearchInput] = useState("");
    const [selectedProduct, setSelectedProduct] = useState(null);
    const [users, setUsers] = useState([]);
    const [orders, setOrders] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
const [categories] = useState([
        { id: 1, name: "Electronics" },
        { id: 2, name: "Fashion" },
        { id: 3, name: "Home & Kitchen" },
        { id: 4, name: "Books" },
        { id: 5, name: "Toys & Games" },
        { id: 6, name: "Health & Beauty" },
        { id: 7, name: "Sports & Outdoors" },
        { id: 8, name: "Automotive" },
        { id: 9, name: "Groceries" },
        { id: 10, name: "Jewelry" },
    ]);

    const [updatedProduct, setUpdatedProduct] = useState(null);
const [editMode, setEditMode] = useState(false);
    const [products, setProducts] = useState([]);
    const [productPage, setProductPage] = useState(1);
    const [productTotalPages, setProductTotalPages] = useState(1);
    const [productSearchInput, setProductSearchInput] = useState("");
    
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
        fetchProducts(0); // Reset to page 1 for new search
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

    const handleCreateProduct = (e) => {
        e.preventDefault();

        const token = localStorage.getItem("adminToken");

        if (!token) {
            alert("You are not authorized to create products. Please log in as an admin.");
            return;
        }

        axios
            .post("http://localhost:9898/products", newProduct, {
                headers: {
                    Authorization: token,
                },
            })
            .then((res) => {
                alert("Product created successfully!");
                setNewProduct({
                    name: "",
                    description: "",
                    longDescription: "",
                    discountedPrice: 0,
                    price: 0,
                    imageUrl: "",
                    stock: 0,
                    categoryName: "",
                });
                setErrors([]);
                fetchProducts(productPage);
            })
            .catch((err) => {
                if (err.response && err.response.data && err.response.data.errors) {
                    setErrors(err.response.data.errors); // Set the field errors from the response
                } else {
                    console.error("Error creating product:", err);
                }
            });
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

    const fetchUserOrders = (userId) => {
        const token = localStorage.getItem("adminToken");
        axios
            .get(`http://localhost:9898/api/orders/user/${userId}`, {
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
    const handleUpdateProduct = (e) => {
        e.preventDefault();

        const token = localStorage.getItem("adminToken");

        if (!token) {
            alert("You are not authorized to update products. Please log in as an admin.");
            return;
        }

        axios
            .put(`http://localhost:9898/products/${updatedProduct.id}`, updatedProduct, {
                headers: {
                    Authorization: token,
                },
            })
            .then((res) => {
                alert("Product updated successfully!");
                setSelectedProduct(updatedProduct);
                setEditMode(false);
                fetchProducts(productPage);
            })
            .catch((err) => {
                console.error("Error updating product:", err);
                alert("Failed to update product.");
            });
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
              {!editMode ? (
                <>
                  <p>
                    <strong>Name:</strong> {selectedProduct.name}
                  </p>
                  <p>
                    <strong>Description:</strong> {selectedProduct.description}
                  </p>
                  <p>
                    <strong>Long Description:</strong>{" "}
                    {selectedProduct.longDescription}
                  </p>
                  <p>
                    <strong>Discounted Price:</strong> $
                    {selectedProduct.discountedPrice}
                  </p>
                  <p>
                    <strong>Price:</strong> ${selectedProduct.price}
                  </p>
                  <p>
                    <strong>Stock:</strong> {selectedProduct.stock}
                  </p>
                  <img
                    src={selectedProduct.imageUrl}
                    alt={selectedProduct.name}
                    className={styles.productImage}
                  />
                  <button
                    onClick={() => {
                      setEditMode(true);
                      setUpdatedProduct(selectedProduct);
                    }}
                  >
                    Edit Product
                  </button>
                </>
              ) : (
                <div>
                  <h2>Edit Product</h2>
                  <form onSubmit={handleUpdateProduct}>
                    <input
                      type="text"
                      value={updatedProduct.name}
                      onChange={(e) =>
                        setUpdatedProduct({
                          ...updatedProduct,
                          name: e.target.value,
                        })
                      }
                    />
                    <input
                      type="text"
                      value={updatedProduct.description}
                      onChange={(e) =>
                        setUpdatedProduct({
                          ...updatedProduct,
                          description: e.target.value,
                        })
                      }
                    />
                    <textarea
                      value={updatedProduct.longDescription}
                      onChange={(e) =>
                        setUpdatedProduct({
                          ...updatedProduct,
                          longDescription: e.target.value,
                        })
                      }
                    />
                    <input
                      type="number"
                      value={updatedProduct.discountedPrice}
                      onChange={(e) =>
                        setUpdatedProduct({
                          ...updatedProduct,
                          discountedPrice: parseFloat(e.target.value),
                        })
                      }
                    />
                    <input
                      type="number"
                      value={updatedProduct.price}
                      onChange={(e) =>
                        setUpdatedProduct({
                          ...updatedProduct,
                          price: parseFloat(e.target.value),
                        })
                      }
                    />
                    <input
                      type="text"
                      value={updatedProduct.imageUrl}
                      onChange={(e) =>
                        setUpdatedProduct({
                          ...updatedProduct,
                          imageUrl: e.target.value,
                        })
                      }
                    />
                    <input
                      type="number"
                      value={updatedProduct.stock}
                      onChange={(e) =>
                        setUpdatedProduct({
                          ...updatedProduct,
                          stock: parseInt(e.target.value),
                        })
                      }
                    />
                    <select
                      value={updatedProduct.categoryName}
                      onChange={(e) =>
                        setUpdatedProduct({
                          ...updatedProduct,
                          categoryName: e.target.value,
                        })
                      }
                    >
                      <option value="">Select a Category</option>
                      {categories.map((category) => (
                        <option key={category.id} value={category.name}>
                          {category.name}
                        </option>
                      ))}
                    </select>
                    <button type="submit">Save Changes</button>
                    <button type="button" onClick={() => setEditMode(false)}>
                      Cancel
                    </button>
                  </form>
                </div>
              )}
            </div>
          )}

          {/* New Product Form */}
          <div className={styles.addSection}>
            <h2>Create New Product</h2>
            <form
              onSubmit={handleCreateProduct}
              className={styles.newProductForm}
            >
              <input
                type="text"
                placeholder="Name"
                value={newProduct.name}
                onChange={(e) =>
                  setNewProduct({ ...newProduct, name: e.target.value })
                }
                required
              />
              <input
                type="text"
                placeholder="Short Description"
                value={newProduct.description}
                onChange={(e) =>
                  setNewProduct({ ...newProduct, description: e.target.value })
                }
                required
              />
              <textarea
                placeholder="Long Description"
                value={newProduct.longDescription}
                onChange={(e) =>
                  setNewProduct({
                    ...newProduct,
                    longDescription: e.target.value,
                  })
                }
                required
              />
              <input
                type="number"
                placeholder="Discounted Price"
                value={newProduct.discountedPrice}
                onChange={(e) =>
                  setNewProduct({
                    ...newProduct,
                    discountedPrice: e.target.value,
                  })
                }
                required
              />
              <input
                type="number"
                placeholder="Price"
                value={newProduct.price}
                onChange={(e) =>
                  setNewProduct({ ...newProduct, price: e.target.value })
                }
                required
              />
              <input
                type="text"
                placeholder="Image URL"
                value={newProduct.imageUrl}
                onChange={(e) =>
                  setNewProduct({ ...newProduct, imageUrl: e.target.value })
                }
                required
              />
              <input
                type="number"
                placeholder="Stock"
                value={newProduct.stock}
                onChange={(e) =>
                  setNewProduct({ ...newProduct, stock: e.target.value })
                }
                required
              />
              <select
                value={newProduct.categoryName}
                onChange={(e) =>
                  setNewProduct({ ...newProduct, categoryName: e.target.value })
                }
              >
                <option value="">Select a Category</option>
                {categories.map((category) => (
                  <option key={category.id} value={category.name}>
                    {category.name}
                  </option>
                ))}
              </select>
              {errors.length > 0 && (
                <div className={styles.errorContainer}>
                  {errors.map((error, index) => (
                    <p key={index} className={styles.errorText}>
                      {error}
                    </p>
                  ))}
                </div>
              )}
              <button type="submit">Create Product</button>
            </form>
          </div>
          {/* User Search */}
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

          {/* Users List */}
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
                      <button
                        onClick={() =>
                          alert("Order details not yet implemented.")
                        }
                      >
                        View Details
                      </button>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}
        </div>
        <Footer />
      </div>
    );
};

// export default AdminDashboard;