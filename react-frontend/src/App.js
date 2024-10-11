import styles from "./App.css";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { Home } from "./components/layout/Home";

function App() {
  return (
    <Router>
      <div className={styles.app}>
        <Home />
      </div>
    </Router>
  );
}

export default App;
