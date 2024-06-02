/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        ok: {
          cyan: "#009AD8",
          "dark-blue": "#006491",
          "blue-grey": "#629AB4",
          magenta: "#E8308A",
          berry: "#88146E",
          "stone-grey": "#878575",
          "grey-light": "#F2F2F0",
          "grey-medium": "#E6E6E3",
          "grey-dark": "#D9D9D7",
          "grey-transparent": "#99999999",
        },
      },
    },
  },
  plugins: [],
};
