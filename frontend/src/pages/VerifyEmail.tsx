import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

const VerifyEmail = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { isAuthenticated, refreshUser } = useAuth();
  const [status, setStatus] = useState<"loading" | "success" | "error">(
    "loading",
  );
  const [message, setMessage] = useState("");

  useEffect(() => {
    const verifyEmail = async () => {
      const token = searchParams.get("token");

      console.log("Token from URL:", token);

      if (!token) {
        console.error("No token found!");
        setStatus("error");
        setMessage("Invalid verification link - no token found");
        return;
      }

      try {
        console.log("Calling backend to verify email...");
        const response = await api.get(`/email/verify?token=${token}`);
        console.log("Backend response:", response.data);

        setStatus("success");
        setMessage(
          typeof response.data === "string"
            ? response.data
            : "Email verified successfully!",
        );

        if (isAuthenticated) {
          console.log("Refreshing user data...");
          await refreshUser();
        }

        setTimeout(() => {
          if (isAuthenticated) {
            console.log("User is logged in, redirecting to editor...");
            navigate("/editor");
          } else {
            console.log("User is logged out, redirecting to login...");
            navigate("/login");
          }
        }, 2000);
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
      } catch (error: any) {
        console.error("Verification error:", error);
        console.error("Error response:", error.response);

        setStatus("error");

        const errorMessage =
          typeof error.response?.data === "string"
            ? error.response.data
            : error.response?.data?.message ||
              error.message ||
              "Verification failed";

        setMessage(errorMessage);
      }
    };

    verifyEmail();
  }, [searchParams, navigate, isAuthenticated, refreshUser]);

  return (
    <div className="min-h-screen bg-neutral-950 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-neutral-900/80 backdrop-blur-xl border border-white/10 rounded-2xl p-8 text-center">
        {status === "loading" && (
          <>
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-cyan-500 mx-auto mb-4"></div>
            <p className="text-white text-lg">Verifying your email...</p>
          </>
        )}

        {status === "success" && (
          <>
            <div className="text-6xl mb-4">✅</div>
            <h2 className="text-white text-2xl font-bold mb-2">
              Email Verified!
            </h2>
            <p className="text-neutral-400 mb-4">{message}</p>
            <p className="text-neutral-500 text-sm">
              {isAuthenticated
                ? "Taking you to the editor..."
                : "Redirecting to login..."}
            </p>
          </>
        )}

        {status === "error" && (
          <>
            <div className="text-6xl mb-4">❌</div>
            <h2 className="text-white text-2xl font-bold mb-2">
              Verification Failed
            </h2>
            <p className="text-red-400 mb-6">{message}</p>
            <button
              onClick={() => navigate("/login")}
              className="bg-cyan-500 hover:bg-cyan-400 text-black font-semibold py-2 px-6 rounded-full"
            >
              Back to Login
            </button>
          </>
        )}
      </div>
    </div>
  );
};

export default VerifyEmail;
