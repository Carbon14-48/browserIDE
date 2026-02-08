import { useState, useEffect } from "react";
import FaultyTerminal from "../Components/FaultyTerminal";
import FuzzyText from "../Components/FuzzyText";
import Navbar from "../Components/Navbar";
import { useNavigate, useLocation, Outlet } from "react-router-dom";

export default function Index() {
  const [fontSize, setFontSize] = useState(144);
  const navigate = useNavigate();
  const location = useLocation();
  const showOverlay =
    location.pathname === "/login" || location.pathname === "/register";

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth < 640) {
        setFontSize(20);
      } else if (window.innerWidth < 1024) {
        setFontSize(48);
      } else {
        setFontSize(60);
      }
    };

    handleResize();
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  return (
    <>
      <div
        className={`relative w-screen h-screen overflow-hidden bg-black ${showOverlay ? "blur-sm" : ""}`}
      >
        <div className="absolute inset-0 z-10">
          <FaultyTerminal
            className="w-full h-full"
            scale={2.9}
            gridMul={[2, 1]}
            digitSize={2.7}
            timeScale={1.9}
            pause={false}
            scanlineIntensity={0.5}
            glitchAmount={1}
            flickerAmount={1}
            noiseAmp={1}
            chromaticAberration={0}
            dither={0}
            curvature={0.1}
            tint="#baf6ff"
            mouseReact
            mouseStrength={0.5}
            pageLoadAnimation
            brightness={0.9}
          />
        </div>
        <Navbar />
        <main className="absolute inset-0 z-20 flex flex-col items-center justify-center px-6 text-center pointer-events-none">
          <div className="pointer-events-auto">
            <div className="inline-block bg-black/40 backdrop-blur-md px-4 py-2 rounded-full text-white mb-8 border border-white/5">
              <FuzzyText
                fontSize={fontSize}
                baseIntensity={0.1}
                hoverIntensity={0.9}
              >
                The Best Browser IDE in the Market
              </FuzzyText>
            </div>
            <div className="flex gap-4 justify-center mt-4">
              <button
                onClick={() => navigate("/login")}
                className="px-8 py-3 rounded-full bg-white text-black font-medium shadow-md pointer-events-auto hover:scale-110 cursor-pointer"
              >
                Get Started
              </button>
              <button
                onClick={() => navigate("/docs")}
                className="px-8 py-3 rounded-full bg-black/60 text-white border border-white/10 pointer-events-auto glass hover:scale-110 cursor-pointer"
              >
                Learn More
              </button>
            </div>
          </div>
        </main>
      </div>

      {showOverlay && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="relative">
            <button
              onClick={() => navigate("/")}
              className="absolute -top-0.5 -right-0.5 w-12 h-12 cursor-pointer  flex items-center justify-center text-cyan-500  z-10"
            >
              ×
            </button>
            <Outlet />
          </div>
        </div>
      )}
    </>
  );
}
