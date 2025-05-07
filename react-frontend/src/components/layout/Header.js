import React, {
  useCallback,
  useContext,
  useEffect,
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
import styled from "styled-components";
import { MultiMenus } from "./MultiMenu";

const Wrapper = styled.aside`
  background: #ccc;
  width: 300px;
`;

export const Header = (props) => {
  const authContext = useContext(AuthContext);
  const navigate = useNavigate();
  const [productSearchInput, setProductSearchInput] = useState("");

  const menus = [
    {
      label: "Menu",
      submenu: [
        {
          label: "Menu 1",
        },
        {
          label: "Menu 2",
          submenu: [
            {
              label: "Sub Menu 1",
            },
            {
              label: "Sub Menu 2",
            },
          ],
        },
        {
          label: "Menu 3",
          submenu: [
            {
              label: "Sub Menu 1",
              submenu: [
                {
                  label: "Boom 1",
                },
                {
                  label: "Boom 2",
                },
              ],
            },
            {
              label: "Sub Menu 2",
              submenu: [
                {
                  label: "Deep 1",
                },
                {
                  label: "Deep 2",
                  submenu: [
                    {
                      label: "Lorem 1",
                    },
                    {
                      label: "Lorem 2",
                      submenu: [
                        {
                          label: "Super Deep",
                        },
                      ],
                    },
                  ],
                },
              ],
            },
            {
              label: "Sub Menu 3",
            },
            {
              label: "Sub Menu 4",
              submenu: [
                {
                  label: "Last 1",
                },
                {
                  label: "Last 2",
                },
                {
                  label: "Last 3",
                },
              ],
            },
          ],
        },
        {
          label: "Menu 4",
        },]
    }
  ];

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

  const headerList = authContext.loggedIn ? (
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
      <Link to="/login" onClick={logoutHandler}>
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
      <div className={styles.logoNavlinksContainer}>
        <Link to="/" className={styles.logo}>
          SHOPEE
        </Link>
        {headerList}
      </div>

      <div className={styles.categorySearchContainer}>
        <Wrapper>
          <MultiMenus menus={menus} />
        </Wrapper>
        <div className={styles.search}>
          <input
            type="text"
            placeholder="Search for products..."
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
