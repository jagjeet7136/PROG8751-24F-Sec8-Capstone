import React, {
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";
import { Link, useNavigate } from "react-router-dom";
import styles from "./Header.module.css";
import { AuthContext } from "../../context/AuthContext";

export const Header = (props) => {
  const authContext = useContext(AuthContext);
  const navigate = useNavigate();
  const [isActive, setIsActive] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState("");
  const [productSearchInput, setProductSearchInput] = useState("");

  const handleSearchInputChange = (event) => {
    setProductSearchInput(event.target.value);
  };

  const handleSearchSubmit = useCallback(() => {
    if (productSearchInput.trim()) {
      navigate(`/search?query=${encodeURIComponent(productSearchInput)}`);
    }
  }, [productSearchInput, navigate]);

  const mainPageHandler = (event) => {
    if (authContext.loggedIn) {
      event.preventDefault();
      navigate("/");
    }
  };

  const toggleHandler = () => {
    setIsActive((prevState) => !prevState);
  };

  const toggleHandlerTwo = useCallback(() => {
    if (isActive) {
      setIsActive(false);
    }
  }, [isActive]);

  const logoutHandler = useCallback(() => {
    authContext.logout();
    toggleHandlerTwo();
  }, [toggleHandlerTwo, authContext]);

  useEffect(() => {
    const handleResize = () => {
      const windowWidth = window.innerWidth;
      const thresholdWidth = 450;

      if (isActive && windowWidth > thresholdWidth) {
        setIsActive(false);
      }
    };
    window.addEventListener("resize", handleResize);
    return () => {
      window.removeEventListener("resize", handleResize);
    };
  }, [isActive]);

  const handleCategoryChange = (event) => {
    const category = event.target.value;

    if (category.trim()) {
      navigate(`/search?query=${encodeURIComponent(category)}`);
    }
  };

  const headerList = useMemo(() => {
    return authContext.loggedIn ? (
      <div>
        <span className={styles.greeting}>
          Hi, {authContext.user?.userFullName}
        </span>
        <Link to="/login" onClick={logoutHandler}>
          Logout
        </Link>
        <Link to="/cart">Cart</Link>
      </div>
    ) : (
      <div>
        <Link to="/register" onClick={toggleHandlerTwo}>
          Sign Up
        </Link>
        <Link to="/login" onClick={toggleHandlerTwo}>
          Login
        </Link>
      </div>
    );
  }, [
    authContext.loggedIn,
    toggleHandlerTwo,
    logoutHandler,
    authContext.user?.userFullName,
  ]);

  return (
    <div className={styles.header}>
      <Link to="/">
        <img src={props.icon} alt="" className={styles.icon} />
      </Link>

      <div className={styles.categoriesDropdown}>
        <select
          value={selectedCategory}
          onChange={handleCategoryChange}
          className={styles.categoriesSelect}
        >
          <option value="">Select Category</option>
          <option value="electronics">Electronics</option>
          <option value="fashion">Fashion</option>
          <option value="home & kitchen">Home Appliances</option>
          <option value="books">Books</option>
          <option value="toys">Toys</option>
          <option value="health">Health</option>
          <option value="sports">Sports</option>
          <option value="automotive">Automotive</option>
          <option value="groceries">Groceries</option>
          <option value="jewelry">Jewelry</option>
        </select>
      </div>

      <div className={styles.searchBar}>
        <input
          type="text"
          placeholder="Search for products..."
          value={productSearchInput}
          onChange={handleSearchInputChange}
          className={styles.searchInput}
        />
        <button onClick={handleSearchSubmit} className={styles.searchButton}>
          Search
        </button>
      </div>

      <div
        className={`${styles.headerList} ${styles.displayNone} ${styles[props.textColor]
          }`}
      >
        {headerList}
      </div>
      <div
        className={`${styles.toggleMenu} ${styles[props.textColor]}`}
        onClick={toggleHandler}
      >
        <span className={styles.toggleMenuOne}></span>
        <span className={styles.toggleMenuTwo}></span>
      </div>
      <div className={`${styles.navbarMenu} ${isActive ? styles.active : ""}`}>
        <div className={` ${styles.navbar}`}>{headerList}</div>
      </div>
    </div>
  );
};
