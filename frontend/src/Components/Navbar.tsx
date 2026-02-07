import { useNavigate, type NavigateFunction } from "react-router-dom";
import FuzzyText from "./FuzzyText";
export default function Navbar() {
  const navigate: NavigateFunction = useNavigate();

  return (
    <nav className="absolute top-6 left-1/2 transform -translate-x-1/2 w-[90%] max-w-4xl z-30 ">
      <div className="bg-black/50 backdrop-blur-md rounded-full px-6 py-3 flex items-center justify-between text-white border border-white/5 glass">
        <div className="flex items-center gap-3">
          <FuzzyText baseIntensity={0.1} hoverIntensity={0.9} fontSize="1.3rem">
            GLITCH
          </FuzzyText>
        </div>
        <div className="flex items-center gap-6 text-sm">
          <a
            className="opacity-80 hover:opacity-100 cursor-pointer"
            onClick={() => navigate("/")}
          >
            Home
          </a>
          <a
            className="opacity-80 hover:opacity-100 cursor-pointer"
            onClick={() => navigate("/docs")}
          >
            Docs
          </a>
        </div>
      </div>
    </nav>
  );
}
