import React from "react";
import styles from "./Footer.module.css";
import { Link } from "react-router-dom";

const Footer = () => {
  return (
    <footer className={styles.footer}>
      <div className={styles.footerContent}>
        <div className={styles.companyName}>
          <h2>Shopping Lane</h2>
          <p>Your one-stop shop for the latest in fashion and trends.</p>
        </div>

        <div className={styles.footerSections}>
          <div className={styles.footerSection}>
            <h3>Navigation</h3>
            <ul>
              <li>
                <Link to="/home">Home</Link>
              </li>
              <li>
                <Link to="/shop">Shop</Link>
              </li>
              <li>
                <Link to="/about">About Us</Link>
              </li>
              <li>
                <Link to="/contact">Contact</Link>
              </li>
              <li>
                <Link to="/admin">Admin</Link>
              </li>
            </ul>
          </div>

          <div className={styles.footerSection}>
            <h3>Customer Service</h3>
            <ul>
              <li>
                <Link to="/faq">FAQs</Link>
              </li>
              <li>
                <Link to="/returns">Return Policy</Link>
              </li>
              <li>
                <Link to="/shipping">Shipping Info</Link>
              </li>
              <li>
                <Link to="/support">Support</Link>
              </li>
            </ul>
          </div>

          <div className={styles.footerSection}>
            <h3>Follow Us</h3>
            <ul className={styles.socialLinks}>
              <li>
                <Link to="/facebook">Facebook</Link>
              </li>
              <li>
                <Link to="/twitter">Twitter</Link>
              </li>
              <li>
                <Link to="/instagram">Instagram</Link>
              </li>
              <li>
                <Link to="/linkedin">LinkedIn</Link>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <div className={styles.footerBottom}>
        <p>
          &copy; {new Date().getFullYear()} Shopping Lane. All rights reserved.
        </p>
      </div>
    </footer>
  );
};

export default Footer;
