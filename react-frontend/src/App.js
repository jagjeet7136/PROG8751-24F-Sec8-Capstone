import styles from "./App.css";
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { Home } from "./components/layout/Home";
import { Register } from "./components/userManagement/Register";
import { Login } from "./components/userManagement/Login";

function App() {
  return (
    <Router>
      <div className={styles.app}>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;