import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.tsx";
import "./index.css";
// Supports weights 100-900
import "@fontsource-variable/raleway";
import { CURRENT_SHOWCASE, CURRENT_SHOWCASE_NAME } from "./showcases.ts";

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

console.log("Showcase:", CURRENT_SHOWCASE_NAME, CURRENT_SHOWCASE);
