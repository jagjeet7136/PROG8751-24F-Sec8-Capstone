import styles from "./Footer.module.css";
import { Link } from "react-router-dom";
import FacebookIcon from '@mui/icons-material/Facebook';
import TwitterIcon from '@mui/icons-material/Twitter';
import InstagramIcon from '@mui/icons-material/Instagram';
import LinkedInIcon from '@mui/icons-material/LinkedIn';

const Footer = () => {
  return (
    <footer className={styles.footer}>
      <div className={styles.companyInfo}>
        <h2>Shopee</h2>
        <p>Your one-stop shop for the latest in fashion and trends.</p>
      </div>

      <div className={styles.footerSections}>
        <div className={styles.footerSection}>
          <h3>Navigation</h3>
          <ul>
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
          <div className={styles.socialLinks}>
            <a href="https://facebook.com" target="_blank" rel="noopener noreferrer">
              <FacebookIcon fontSize="medium" color="success" />
            </a>
            <a href="https://twitter.com" target="_blank" rel="noopener noreferrer">
              <TwitterIcon fontSize="medium" />
            </a>
            <a href="https://instagram.com" target="_blank" rel="noopener noreferrer">
              <InstagramIcon fontSize="medium" />
            </a>
            <a href="https://linkedin.com" target="_blank" rel="noopener noreferrer">
              <LinkedInIcon fontSize="medium" />
            </a>
          </div>
        </div>
      </div>

      <div className={styles.subscribeEmailContainer}>
        <span className={styles.subscribeEmailContainerHeading}>Subscribe to latest deals</span>
        <div className={styles.emailContainer}>
          <input
            type="text"
            placeholder="Email Address"
          />
          <span>Subscribe</span>
        </div>
      </div>


      <div className={styles.footerBottom}>
        <p>
          &copy; {new Date().getFullYear()} Shopee. All rights reserved.
        </p>
      </div>
    </footer>
  );
};

export default Footer;
