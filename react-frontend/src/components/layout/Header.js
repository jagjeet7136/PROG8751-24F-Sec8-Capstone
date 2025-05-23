import React, {
  useCallback,
  useContext,
  useState,
} from "react";
import { Link, useNavigate } from "react-router-dom";
import styles from "./Header.module.css";
import { AuthContext } from "../../context/AuthContext";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faSearch,
  faCartShopping,
  faUser,
} from "@fortawesome/free-solid-svg-icons";
import { MultiMenus } from "./MultiMenu";

export const Header = (props) => {
  const authContext = useContext(AuthContext);
  const navigate = useNavigate();
  const [productSearchInput, setProductSearchInput] = useState("");

  const menus = [
    {
      label: "Shop",
      submenu: [
        {
          label: "Clothing",
          submenu: [
            { label: "Men" },
            { label: "Women" },
            { label: "Kids" },
          ],
        },
        {
          label: "Electronics",
          submenu: [
            { label: "Mobiles" },
            { label: "Laptops" },
            { label: "Headphones" },
          ],
        },
        {
          label: "Home & Kitchen",
          submenu: [
            { label: "Furniture" },
            { label: "Appliances" },
            { label: "Decor" },
          ],
        },
        {
          label: "Beauty & Health",
          submenu: [
            { label: "Skincare" },
            { label: "Makeup" },
            { label: "Hair Care" },
          ],
        },
        {
          label: "Sports & Outdoors",
          submenu: [
            { label: "Fitness Equipment" },
            { label: "Outdoor Gear" },
            { label: "Bicycles" },
          ],
        },
      ],
    },
  ];


  const stopBodyScrolling = (isScrollable) => {
    if (!isScrollable) {
      document.body.classList.add('menuOpen');
    } else {
      document.body.classList.remove('menuOpen');
    }
  }

  const handleSearchInputChange = (event) => {
    setProductSearchInput(event.target.value);
  };

  const handleSearchSubmit = useCallback(() => {
    if (productSearchInput.trim()) {
      navigate(`/search?query=${encodeURIComponent(productSearchInput)}`);
    }
  }, [productSearchInput, navigate]);

  const logoutHandler = useCallback(() => {
    authContext.logout();
  }, [authContext]);

  const navbarList = authContext.loggedIn ? (
    <div className={styles.navbar}>
      <div className={styles.navLink}>
        <FontAwesomeIcon
          icon={faUser}
          className={styles.accountIcon}
          onClick={handleSearchSubmit}
        />
        <Link to="/profile">
          {`Hi, ${authContext.user ? authContext.user.name : ""}`}
        </Link>
      </div>
      <Link to="/login" onClick={logoutHandler}> {/*Change it to navLink */}
        Logout
      </Link>
      <div className={styles.navLink}>
        <FontAwesomeIcon
          icon={faCartShopping}
          className={styles.cartIcon}
          onClick={handleSearchSubmit}
        />
        <span className={styles.cartQuantity}></span>
        <Link to="/cart">Cart</Link>
      </div>
    </div>
  ) : (
    <div className={styles.navbar}>
      <div className={styles.navLink}>
        <FontAwesomeIcon
          icon={faUser}
          className={styles.accountIcon}
          onClick={handleSearchSubmit}
        />
        <Link to="/login">Account</Link>
      </div>
      <div className={styles.navLink}>
        <FontAwesomeIcon
          icon={faCartShopping}
          className={styles.cartIcon}
          onClick={handleSearchSubmit}
        />
        <span className={styles.cartQuantity}></span>
        <Link to="/cart">Cart</Link>
      </div>
    </div>
  );

  return (
    <div className={`${styles.header} ${styles[props.textColor] || ""}`}>
      <div className={styles.logoNavbarContainer}>
        <Link to="/" className={styles.logo}>
          SHOPEE
        </Link>
        {navbarList}
      </div>

      <div className={styles.menuSearchContainer}>
        <MultiMenus menus={menus} scrollable={stopBodyScrolling} />
        <div className={styles.search}>
          <input
            type="text"
            placeholder="Search products..."
            value={productSearchInput}
            onChange={handleSearchInputChange}
          />
          <FontAwesomeIcon
            icon={faSearch}
            className={styles.searchButton}
            onClick={handleSearchSubmit}
          />
        </div>
      </div>

    </div>
  );
};
