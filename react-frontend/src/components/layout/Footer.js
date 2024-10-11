import React from "react";
import styles from "./Footer.module.css";

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
                            <li><a href="#home">Home</a></li>
                            <li><a href="#shop">Shop</a></li>
                            <li><a href="#about">About Us</a></li>
                            <li><a href="#contact">Contact</a></li>
                        </ul>
                    </div>

                    <div className={styles.footerSection}>
                        <h3>Customer Service</h3>
                        <ul>
                            <li><a href="#faq">FAQs</a></li>
                            <li><a href="#returns">Return Policy</a></li>
                            <li><a href="#shipping">Shipping Info</a></li>
                            <li><a href="#support">Support</a></li>
                        </ul>
                    </div>

                    <div className={styles.footerSection}>
                        <h3>Follow Us</h3>
                        <ul className={styles.socialLinks}>
                            <li><a href="#facebook">Facebook</a></li>
                            <li><a href="#twitter">Twitter</a></li>
                            <li><a href="#instagram">Instagram</a></li>
                            <li><a href="#linkedin">LinkedIn</a></li>
                        </ul>
                    </div>
                </div>
            </div>
            <div className={styles.footerBottom}>
                <p>&copy; {new Date().getFullYear()} Shopping Lane. All rights reserved.</p>
            </div>
        </footer>
    );
};

export default Footer;
