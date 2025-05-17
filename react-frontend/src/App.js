import CheckoutForm from "./components/layout/CheckoutForm";
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
import jwt_decode from "jwt-decode";
import Cart from "./components/layout/Cart";
import { AdminLogin } from "./components/userManagement/AdminLogin";
import { AdminDashboard } from "./components/layout/AdminDashboard";
import SearchResults from "./components/layout/SearchResults";
import { UserDashboard } from "./components/layout/UserDashboard";
import ContactPage from "./components/layout/ContactPage";
import Profile from "./components/layout/Profile";
import ResetPassword from "./components/layout/ResetPassword";
import { EmailVerification } from "./components/layout/EmailVerification";

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
            <Route path="/checkout" element={<CheckoutForm />} />
            <Route path="/admin" element={<AdminLogin />} />
            <Route path="/search" element={<SearchResults />} />
            <Route path="/adminDashboard" element={<AdminDashboard />} />
            <Route path="/adminUserDashboard" element={<UserDashboard />} />
            <Route path="/contact" element={<ContactPage />} />
            <Route path="/profile" element={<Profile />} />
            <Route path="/reset" element={<ResetPassword />} />
            <Route path="/verify-email" element={<EmailVerification />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
