import React, { useContext, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { AuthContext } from "../../context/AuthContext";
import styles from "./Login.module.css";
import todoSmallIcon from "../../icons/logo-transparent-png.png";

export const Login = () => {
  const authContext = useContext(AuthContext);
  const username = useRef("");
  const password = useRef("");
  const navigate = useNavigate();
  const [errorMessage, setErrorMsg] = useState("");
  const [isFormValid, setIsFormValid] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  let user = null;

  const handleLogin = (e) => {
    if (!isFormValid) {
      setIsFormValid(true);
    }
    e.preventDefault();
    const loginObject = {
      username: username.current.value,
      password: password.current.value,
    };
    axios
      .post("http://localhost:9898/user/login", loginObject)
      .then((res) => {
        axios
          .get(
            `http://localhost:9898/user/getUser/?username=${username.current.value}`,
            {
              headers: {
                Authorization: res.data.token,
              },
            }
          )
          .then((innerRes) => {
            //Following three lines of localStorage can be deleted if API hitting components use authContext istead of localstorage for token
            user = innerRes.data;
            localStorage.setItem("token", res.data.token);
            localStorage.setItem("loggedIn", "true");
            localStorage.setItem("username", username.current.value);
            localStorage.setItem("user", JSON.stringify(user));
            authContext.login(res.data.token, username.current.value, user);
            navigate("/");
          })
          .catch((innerError) => {
            console.log(innerError);
          });
      })
      .catch((error) => {
        console.log(error);
        setErrorMsg("Some error occured");
        if (error.response) {
          if (error.response.status === 401) {
            setErrorMsg("Wrong email or password");
          } else if (
            error.response.data.errors != null &&
            error.response.data.errors.length > 0
          ) {
            setErrorMsg(error.response.data.errors[0]);
          } else if (
            error.response.data.message != null &&
            error.response.data.message.trim().length > 0
          ) {
            setErrorMsg(error.response.data.message);
          }
        }
        setIsFormValid(false);
      });
  };

  return (
    <div className={styles.login}>
      <Link to="/">
        <img src={todoSmallIcon} alt="" className={styles.loginIcon}></img>
      </Link>
      <form onSubmit={handleLogin} className={styles.loginForm}>
        <h1 className={styles.loginHeading}>Login</h1>
        <input
          type="email"
          placeholder="Email"
          ref={username}
          className={styles.username}
        />
        <div
          className={`${styles.passwordContainer} ${
            isFormValid ? styles.passwordContainerMargin : ""
          }`}
        >
          <input
            type={showPassword ? "text" : "password"}
            placeholder="Password"
            ref={password}
            className={styles.password}
          />
          <span
            className={styles.passwordToggle}
            onClick={() => setShowPassword(!showPassword)}
          >
            {showPassword ? "Hide" : "Show"}
          </span>
        </div>
        <div
          className={`${styles.errorMessageContainer} 
                    ${
                      !isFormValid
                        ? styles.errorMessageContainer + " " + styles.active
                        : ""
                    }`}
        >
          <img
            src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR9YISfL4Lm8FJPRneGwEq8_-9Nim7YeuMJMw&usqp=CAU"
            alt=""
          ></img>
          <h6 className={styles.errorMessage}>{errorMessage}</h6>
        </div>
        <button type="submit" className={styles.loginButton}>
          Login
        </button>
        <h6 className={styles.loginContainer}>
          Don't have an account?&nbsp;&nbsp;<Link to="/register">Sign Up</Link>
        </h6>
      </form>
      <h6 className={styles.tPContainer}>
        <Link to="/about">About and Information</Link>
      </h6>
    </div>
  );
};
