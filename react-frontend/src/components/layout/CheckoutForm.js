import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import styles from "./CheckoutForm.module.css";
import icon from "../../assets/icons/logo-transparent-png.png";
import { Header } from "./Header";
import Footer from "./Footer";

const CheckoutForm = () => {
  const navigate = useNavigate();
  const { state } = useLocation();
  const { cartItems, subtotal, tax, SHIPPING_CHARGE, total } = state || {};
  const token = localStorage.getItem("token");

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    address: "",
    city: "",
    postalCode: "",
    state: "",
    paymentMethod: "card",
  });

  const [errors, setErrors] = useState({});

  const provinces = [
    "Alberta",
    "British Columbia",
    "Manitoba",
    "New Brunswick",
    "Newfoundland and Labrador",
    "Nova Scotia",
    "Ontario",
    "Prince Edward Island",
    "Quebec",
    "Saskatchewan",
  ];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({ ...prevData, [name]: value }));
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.firstName.trim()) newErrors.firstName = "First name is required";
    if (!formData.email.trim() || !/^[\w.%+-]+@[\w.-]+\.[A-Za-z]{2,}$/.test(formData.email))
      newErrors.email = "Valid email is required";
    if (!/^\d{10}$/.test(formData.phone)) newErrors.phone = "Phone must be 10 digits";
    if (!formData.address.trim()) newErrors.address = "Street address is required";
    if (!formData.city.trim()) newErrors.city = "City is required";
    if (!/^[A-Za-z]\d[A-Za-z]\d[A-Za-z]\d$/.test(formData.postalCode))
      newErrors.postalCode = "Invalid Canadian postal code";
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  useEffect(() => {
    console.log("Received cartItems in checkout:", cartItems);
  }, [cartItems]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
      const user = JSON.parse(localStorage.getItem("user"));
      const userId = user?.id;

      const response = await axios.post(
        "http://localhost:9898/orders/create-checkout-session",
        {
          userId,
          ...formData,
          cartItems,
          subtotal,
          tax,
          shippingCharge: SHIPPING_CHARGE,
          total,
        },
        {
          headers: { Authorization: token },
        }
      );

      window.location.href = response.data.url;
    } catch (error) {
      console.error("Error creating checkout session:", error);
      alert("Could not proceed to payment.");
    }
  };

  return (
    <div>
      <Header textColor="greenText" icon={icon} />
      <form className={styles.checkoutForm} onSubmit={handleSubmit}>
        <h2>Delivery Information</h2>

        <div className={styles.section}>
          <label>
            First Name:
            <input
              type="text"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
            />
            {errors.firstName && <span className={styles.error}>{errors.firstName}</span>}
          </label>

          <label>
            Last Name:
            <input
              type="text"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
            />
          </label>

          <label>
            Email:
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
            />
            {errors.email && <span className={styles.error}>{errors.email}</span>}
          </label>

          <label>
            Phone:
            <input
              type="tel"
              name="phone"
              value={formData.phone}
              onChange={handleChange}
            />
            {errors.phone && <span className={styles.error}>{errors.phone}</span>}
          </label>
        </div>

        <div className={styles.section}>
          <h3>Address</h3>
          <label>
            Street Address:
            <input
              type="text"
              name="address"
              value={formData.address}
              onChange={handleChange}
            />
            {errors.address && <span className={styles.error}>{errors.address}</span>}
          </label>

          <label>
            City:
            <input
              type="text"
              name="city"
              value={formData.city}
              onChange={handleChange}
            />
            {errors.city && <span className={styles.error}>{errors.city}</span>}
          </label>

          <label>
            Postal Code:
            <input
              type="text"
              name="postalCode"
              value={formData.postalCode}
              onChange={handleChange}
            />
            {errors.postalCode && <span className={styles.error}>{errors.postalCode}</span>}
          </label>

          <label>
            Province:
            <select name="state" value={formData.state} onChange={handleChange}>
              <option value="">Select Province</option>
              {provinces.map((province) => (
                <option key={province} value={province}>
                  {province}
                </option>
              ))}
            </select>
          </label>
        </div>

        <button type="submit" className={styles.submitButton}>
          Proceed to Payment
        </button>
      </form>
      <Footer />
    </div>
  );
};

export default CheckoutForm;
