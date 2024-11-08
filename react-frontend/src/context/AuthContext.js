import React, { createContext, useState, useEffect } from "react";

const AuthContext = createContext();

const AuthProvider = ({ children }) => {
  //This is because any state variable do not reset when route is used
  const storedToken = localStorage.getItem("token");
  const storedLoggedIn = localStorage.getItem("loggedIn") === "true";
  const storedUsername = localStorage.getItem("username");
  const storedUser =
    localStorage.getItem("user") !== null
      ? JSON.parse(localStorage.getItem("user"))
      : null;
  // const storedUser = localStorage.getItem("user");
  const [loggedIn, setLoggedIn] = useState(storedLoggedIn);
  const [token, setToken] = useState(storedToken);
  const [username, setUsername] = useState(storedUsername);
  const [user, setUser] = useState(storedUser);

  //This is because so that if any state variable change should change localStorage values to keep former comment valid
  useEffect(() => {
    localStorage.setItem("token", token);
    localStorage.setItem("loggedIn", loggedIn.toString());
    localStorage.setItem("username", username ? username.toString() : "");
    localStorage.setItem("user", JSON.stringify(user));
  }, [token, loggedIn, username, user]);

  const login = (newToken, newUsername, newUser) => {
    setUser(newUser);
    setToken(newToken);
    setLoggedIn(true);
    setUsername(newUsername);
  };
  const setUserFunction = (newUser) => {
    setUser(newUser);
  };

  const logout = () => {
    setToken("");
    setLoggedIn(false);
    setUsername("");
    setUser(null);
    localStorage.removeItem("token");
    localStorage.removeItem("loggedIn");
    localStorage.removeItem("user");
  };

  return (
    <AuthContext.Provider
      value={{
        loggedIn,
        token,
        login,
        logout,
        username,
        user,
        setUserFunction,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export { AuthContext, AuthProvider };
