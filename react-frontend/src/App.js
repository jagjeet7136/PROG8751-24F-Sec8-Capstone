import styles from "./App.css";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  Outlet,
  Navigate,
} from "react-router-dom";
import { Home } from "./components/layout/Home";
import { Register } from "./components/userManagement/Register";
import { Login } from "./components/userManagement/Login";
import { ProductDetails } from "./components/layout/ProductDetails";
import { AuthContext, AuthProvider } from "./context/AuthContext";
import { useContext, useLayoutEffect } from "react";
import jwt_decode from "jwt-decode"; // Fixing the import
import { Cart } from "./components/layout/Cart";

const PrivateRoute = () => {
  const authContext = useContext(AuthContext);
  const loggedIn = authContext.loggedIn;
  const token = localStorage.getItem("token");
  const isTokenExpiredVal = isTokenExpired(token);
  useLayoutEffect(() => {
    if (isTokenExpiredVal) {
      authContext.logout();
    }
  }, [isTokenExpiredVal, authContext]);

  if (!loggedIn) {
    return <Navigate to="/login" />;
  }
  return <Outlet />;
};

const PublicRoute = () => {
  const authContext = useContext(AuthContext);
  const loggedIn = authContext.loggedIn;
  console.log(loggedIn);
  if (loggedIn) {
    return <Navigate to="/" />;
  }
  return <Outlet />;
};

const isTokenExpired = (token) => {
  if (!token) {
    return true;
  }

  try {
    const tokenData = jwt_decode(token);
    if (!tokenData || !tokenData.exp) {
      return true;
    }
    const expirationTime = tokenData.exp;
    const currentTime = Math.floor(Date.now() / 1000);
    return expirationTime < currentTime;
  } catch (error) {
    return true;
  }
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className={styles.app}>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
            <Route path="/products/:productId" element={<ProductDetails />} />
            <Route path="/cart" element={<Cart />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
