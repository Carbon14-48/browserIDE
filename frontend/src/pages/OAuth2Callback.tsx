import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { setAccessToken } from "../services/api";
import api from "../services/api";

const OAuth2Callback = () => {
  const navigate = useNavigate();
  useEffect(() => {
    const completeOAuthLogin = async () => {
      try {
        const response = await api.post("/auth/refresh");
        setAccessToken(response.data.accessToken);
        navigate("/editor");
      } catch (error) {
        console.log("OAuth callback error:", error);
        navigate("/login");
      }
    };

    completeOAuthLogin();
  }, [navigate]);

  return (
    <div className="min-h-screen bg-neutral-950 flex items-center justify-center">
      <div className="text-white text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-cyan-500 mx-auto mb-4"></div>
        <p>Completing login...</p>
      </div>
    </div>
  );
};

export default OAuth2Callback;
