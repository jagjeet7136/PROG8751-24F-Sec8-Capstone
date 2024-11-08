import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import styles from "./CheckoutForm.module.css";
import icon from "../../icons/logo-transparent-png.png";
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
    paymentMethod: "credit",
    cardNumber: "",
    expiryDate: "",
    cvv: "",
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
    setFormData({ ...formData, [name]: value });
  };

  const validateForm = () => {
    let newErrors = {};

    if (!formData.firstName.trim())
      newErrors.firstName = "First name is required";
    if (
      !/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(formData.email)
    )
      newErrors.email = "Invalid email";
    if (!/^\d{10}$/.test(formData.phone))
      newErrors.phone = "Phone must be 10 digits";
    if (!formData.address.trim())
      newErrors.address = "Street address is required";
    if (!formData.city.trim()) newErrors.city = "City is required";
    if (!/^[A-Za-z]\d[A-Za-z]\d[A-Za-z]\d$/.test(formData.postalCode))
      newErrors.postalCode = "Invalid postal code format";
    if (!/^\d{16}$/.test(formData.cardNumber))
      newErrors.cardNumber = "Card number must be 16 digits";
    if (!/^\d{3}$/.test(formData.cvv)) newErrors.cvv = "CVV must be 3 digits";

    const expiryDateMatch = formData.expiryDate.match(
      /^(0[1-9]|1[0-2])\/?([0-9]{2})$/
    );
    if (!expiryDateMatch) {
      newErrors.expiryDate = "Invalid expiry date format";
    } else {
      const [_, month, year] = expiryDateMatch;
      const expiryYear = parseInt(`20${year}`, 10);
      const expiryMonth = parseInt(month, 10) - 1;
      const today = new Date();
      const expiry = new Date(expiryYear, expiryMonth + 1, 0);
      if (expiry < today)
        newErrors.expiryDate = "Expiry date must be in the future";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (validateForm()) {
      try {
        await axios.post(
          "http://localhost:9898/orders",
          {
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
        alert("Order placed successfully!");
        navigate("/");
      } catch (error) {
        console.error("Error placing order:", error);
        alert("Order placement failed.");
      }
    }
  };

  return (
    <div>
      <Header textColor="greenText" icon={icon} />
      <form className={styles.checkoutForm} onSubmit={handleSubmit}>
        <h2>Checkout</h2>

        <div className={styles.section}>
          <h3>Personal Information</h3>
          <label>
            First Name:
            <input
              type="text"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
            />
            {errors.firstName && (
              <span className={styles.error}>{errors.firstName}</span>
            )}
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
            {errors.email && (
              <span className={styles.error}>{errors.email}</span>
            )}
          </label>
          <label>
            Phone:
            <input
              type="tel"
              name="phone"
              value={formData.phone}
              onChange={handleChange}
            />
            {errors.phone && (
              <span className={styles.error}>{errors.phone}</span>
            )}
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
            {errors.address && (
              <span className={styles.error}>{errors.address}</span>
            )}
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
            {errors.postalCode && (
              <span className={styles.error}>{errors.postalCode}</span>
            )}
          </label>
          <label>
            State:
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

        <div className={styles.section}>
          <h3>Payment</h3>
          <div className={styles.radioContainer}>
            <label>
              <input
                type="radio"
                name="paymentMethod"
                value="credit"
                checked={formData.paymentMethod === "credit"}
                onChange={handleChange}
              />
              Credit Card
            </label>
            <label>
              <input
                type="radio"
                name="paymentMethod"
                value="debit"
                checked={formData.paymentMethod === "debit"}
                onChange={handleChange}
              />
              Debit Card
            </label>
          </div>
          <label>
            Card Number:
            <input
              type="text"
              name="cardNumber"
              value={formData.cardNumber}
              onChange={handleChange}
            />
            {errors.cardNumber && (
              <span className={styles.error}>{errors.cardNumber}</span>
            )}
          </label>
          <label>
            Expiry Date:
            <input
              type="text"
              name="expiryDate"
              value={formData.expiryDate}
              onChange={handleChange}
              placeholder="MM/YY"
            />
            {errors.expiryDate && (
              <span className={styles.error}>{errors.expiryDate}</span>
            )}
          </label>
          <label>
            CVV:
            <input
              type="text"
              name="cvv"
              value={formData.cvv}
              onChange={handleChange}
            />
            {errors.cvv && <span className={styles.error}>{errors.cvv}</span>}
          </label>
          <button type="submit" className={styles.submitButton}>
            Submit
          </button>
        </div>
      </form>
      <Footer />
    </div>
  );
};

export default CheckoutForm;
