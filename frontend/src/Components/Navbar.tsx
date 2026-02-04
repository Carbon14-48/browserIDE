import { useTheme } from "../customHooks/ThemeProvider";
function Navbar() {
  const { toggleTheme } = useTheme();
  return (
    <div>
      <button
        className="dark:text-amber-50 cursor-pointer border-2 p-4 bg-red-500 text-cyan-300"
        onClick={toggleTheme}
      >
        switch theme
      </button>
    </div>
  );
}

export default Navbar;
