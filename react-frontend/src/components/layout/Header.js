import React, { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import styles from "./Header.module.css";

export const Header = (props) => {
    const [isActive, setIsActive] = useState(false);
    const [selectedCategory, setSelectedCategory] = useState("");

    const toggleHandler = () => {
        setIsActive((prevState) => !prevState);
    };

    const toggleHandlerTwo = useCallback(() => {
        if (isActive) {
            setIsActive(false);
        }
    }, [isActive]);

    useEffect(() => {
        const handleResize = () => {
            const windowWidth = window.innerWidth;
            const thresholdWidth = 450;

            if (isActive && windowWidth > thresholdWidth) {
                setIsActive(false);
            }
        };
        window.addEventListener('resize', handleResize);
        return () => {
            window.removeEventListener('resize', handleResize);
        };
    }, [isActive]);

    const headerList = () => {
        return (
            <div>
                <Link to="/register" onClick={toggleHandlerTwo}>
                    Sign Up
                </Link>
                <Link to="/login" onClick={toggleHandlerTwo}>
                    Login
                </Link>
            </div>
        );
    };

    const handleCategoryChange = (event) => {
        setSelectedCategory(event.target.value);
    };

    return (
        <div className={styles.header}>
            <Link to="/"><img src={props.icon} alt="" className={styles.icon} /></Link>

            <div className={styles.categoriesDropdown}>
                <select
                    value={selectedCategory}
                    onChange={handleCategoryChange}
                    className={styles.categoriesSelect}
                >
                    <option value="">Select Category</option>
                    <option value="electronics">Electronics</option>
                    <option value="fashion">Fashion</option>
                    <option value="home-appliances">Home Appliances</option>
                    <option value="books">Books</option>
                    <option value="toys">Toys</option>
                </select>
            </div>

            <div className={styles.searchBar}>
                <input
                    type="text"
                    placeholder="Search for products..."
                    className={styles.searchInput}
                />
                <button className={styles.searchButton}>Search</button>
            </div>

            <div className={`${styles.headerList} ${styles.displayNone} ${styles[props.textColor]}`}>
                {headerList()}
            </div>
            <div className={`${styles.toggleMenu} ${styles[props.textColor]}`} onClick={toggleHandler}>
                <span className={styles.toggleMenuOne}></span>
                <span className={styles.toggleMenuTwo}></span>
            </div>
            <div className={`${styles.navbarMenu} ${isActive ? styles.active : ''}`}>
                <div className={` ${styles.navbar}`}>{headerList()}</div>
            </div>
        </div>
    );
};
